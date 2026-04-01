import { Component, OnInit, inject, ViewEncapsulation } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router'; // Retiré 'Event' d'ici
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';
import { DomSanitizer } from '@angular/platform-browser';
import { QuillModule } from 'ngx-quill';
import Quill from 'quill'; // Importez la bibliothèque Quill directement

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { EvaluationService } from '../service/evaluation.service';
import { IEvaluation } from '../evaluation.model';
import { EvaluationFormGroup, EvaluationFormService } from './evaluation-form.service';
import { SafeHtmlPipe } from 'app/shared/pipe/safe-html.pipe';
import { IDocumentJoint } from 'app/entities/document-joint/document-joint.model'; // Ajustez le chemin
import { FormatDocument } from 'app/entities/enumerations/format-document.model';

// Suppression des imports node:console et jhipster qui causent des erreurs

@Component({
  standalone: true,
  selector: 'jhi-evaluation-update',
  templateUrl: './evaluation-update.component.html',
  styleUrls: ['./evaluation-update.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [SharedModule, FormsModule, ReactiveFormsModule, MarkdownModule, QuillModule, SafeHtmlPipe],
})
export class EvaluationUpdateComponent implements OnInit {
  isSaving = false;
  evaluation: IEvaluation | null = null;
  usersSharedCollection: IUser[] = [];

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected evaluationService = inject(EvaluationService);
  protected evaluationFormService = inject(EvaluationFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);
  private sanitizer = inject(DomSanitizer);
  quillEditor: any;

  editForm: EvaluationFormGroup = this.evaluationFormService.createEvaluationFormGroup();

  constructor() {
    // On force le type à 'any' ou 'Record<string, any>' pour pouvoir modifier les icônes
    const icons = Quill.import('ui/icons') as any;

    if (icons) {
      icons['undo'] =
        '<svg viewbox="0 0 18 18"><polygon class="ql-fill ql-stroke" points="6 10 4 12 2 10 6 10"></polygon><path class="ql-stroke" d="M6,10a4,4,0,1,1,4,4H8"></path></svg>';
      icons['redo'] =
        '<svg viewbox="0 0 18 18"><polygon class="ql-fill ql-stroke" points="12 10 14 12 16 10 12 10"></polygon><path class="ql-stroke" d="M12,10a4,4,0,1,0-4,4h2"></path></svg>';
    }
  }

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evaluation }) => {
      this.evaluation = evaluation; // On stocke la version originale pour le panneau de gauche

      if (evaluation && !evaluation.rapportAnalyse && evaluation.id) {
        this.evaluationService.find(evaluation.id).subscribe(res => {
          if (res.body) {
            this.updateForm(res.body);
          }
        });
      } else if (evaluation) {
        this.updateForm(evaluation);
      }
      this.loadRelationshipsOptions();
    });
  }
  protected updateForm(evaluation: IEvaluation): void {
    let cleanedContent = evaluation.rapportAnalyse ?? '';

    if (cleanedContent) {
      // 1. Nettoyage (votre logique existante)
      cleanedContent = cleanedContent
        .replace(/```html/gi, '')
        .replace(/```/gi, '')
        .trim();
      if (cleanedContent.includes('<body')) {
        const bodyMatch = cleanedContent.match(/<body[^>]*>([\s\S]*?)<\/body>/i);
        if (bodyMatch && bodyMatch[1]) cleanedContent = bodyMatch[1];
      } else {
        cleanedContent = cleanedContent.replace(/<head[^>]*>[\s\S]*?<\/head>/gi, '');
        cleanedContent = cleanedContent.replace(/<style[^>]*>[\s\S]*?<\/style>/gi, '');
        cleanedContent = cleanedContent.replace(/<html[^>]*>|<\/html>|<!DOCTYPE[^>]*>/gi, '');
      }
      const firstTag = cleanedContent.indexOf('<h');
      if (firstTag !== -1) cleanedContent = cleanedContent.substring(firstTag);
      cleanedContent = cleanedContent.trim();
    }

    // 2. Mettre à jour l'objet de référence (pour le panneau de gauche)
    this.evaluation = { ...evaluation, rapportAnalyse: cleanedContent };

    // 3. Initialiser le formulaire
    this.evaluationFormService.resetForm(this.editForm, this.evaluation);

    // 4. Lancer la synchronisation forcée
    this.syncContentToQuill(cleanedContent);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, evaluation.evaluateur);
  }

  onEditorCreated(quill: any): void {
    this.quillEditor = quill;
    // Si l'éditeur est créé après le chargement des données, on synchronise
    if (this.evaluation?.rapportAnalyse) {
      this.syncContentToQuill(this.evaluation.rapportAnalyse);
    }
  }

  /**
   * Synchronise de force le contenu entre le Formulaire Angular et Quill
   * pour éviter que l'un n'écrase l'autre au démarrage.
   */
  private syncContentToQuill(content: string): void {
    setTimeout(() => {
      // Mettre à jour la valeur du formulaire sans déclencher d'événements globaux
      this.editForm.get('rapportAnalyse')?.patchValue(content, { emitEvent: false });

      // Injecter manuellement dans l'instance Quill si elle est prête
      if (this.quillEditor) {
        this.quillEditor.clipboard.dangerouslyPasteHTML(content);
        console.log('Contenu injecté de force dans Quill');
      }
    }, 200); // 200ms suffisent pour que le resetForm soit passé
  }

  copyOriginalToEditor(): void {
    if (this.evaluation?.rapportAnalyse) {
      this.syncContentToQuill(this.evaluation.rapportAnalyse);
    }
  }

  // 3. Fonction pour charger le contenu
  loadQuillContent(): void {
    if (this.quillEditor && this.evaluation?.rapportAnalyse) {
      // clipboard.dangerouslyPasteHTML est la méthode la plus fiable pour injecter du HTML
      this.quillEditor.clipboard.dangerouslyPasteHTML(this.evaluation.rapportAnalyse);
    }
  }

  syncScroll(source: HTMLElement, target: HTMLElement): void {
    target.scrollTop = source.scrollTop;
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  // Ici 'event: Event' utilisera maintenant le type global correct car l'import Router a été supprimé
  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('iaevalApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const evaluation = this.evaluationFormService.getEvaluation(this.editForm);
    if (evaluation.id !== null) {
      this.subscribeToSaveResponse(this.evaluationService.update(evaluation));
    } else {
      this.subscribeToSaveResponse(this.evaluationService.create(evaluation));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvaluation>>): void {
    result.pipe(finalize(() => (this.isSaving = false))).subscribe({
      next: () => this.previousState(),
      error: () => console.error('Erreur lors de la sauvegarde'),
    });
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.evaluation?.evaluateur)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  getDocumentIcon(format?: string | null): string {
    switch (format?.toLowerCase()) {
      case 'pdf':
        return 'file-pdf';
      case 'doc':
      case 'docx':
        return 'file-word';
      case 'xls':
      case 'xlsx':
        return 'file-excel';
      case 'jpg':
      case 'png':
        return 'file-image';
      default:
        return 'file-alt';
    }
  }
  viewDocument(doc: IDocumentJoint): void {
    if (doc.url) {
      // Si c'est un lien URL (Cloud/S3)
      window.open(doc.url, '_blank');
    } else if (doc.contenuOcr) {
      // Si vous stockez le contenu ou si vous avez un service dédié
      // Adaptez ici selon la manière dont vos fichiers sont stockés
      console.log('Contenu OCR disponible pour : ' + doc.nom);
    }
  }
}
