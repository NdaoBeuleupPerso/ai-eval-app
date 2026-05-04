import { HttpHeaders } from '@angular/common/http';
import { Component, NgZone, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';

import { FormsModule } from '@angular/forms';
import { ItemCountComponent } from 'app/shared/pagination';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ICritere } from '../critere.model';
import { CritereDeleteDialogComponent } from '../delete/critere-delete-dialog.component';
import { CritereService, EntityArrayResponseType } from '../service/critere.service';

import { IAppelOffre } from 'app/entities/appel-offre/appel-offre.model';
import { AppelOffreService } from 'app/entities/appel-offre/service/appel-offre.service';
@Component({
  selector: 'jhi-critere',
  templateUrl: './critere.component.html',
  imports: [RouterModule, FormsModule, SharedModule, SortDirective, SortByDirective, ItemCountComponent],
})
export class CritereComponent implements OnInit {
  subscription: Subscription | null = null;
  criteres = signal<ICritere[]>([]);
  isLoading = false;
  appelOffreId?: number;
  appelOffreDetails = signal<IAppelOffre | null>(null);
  sortState = sortStateSignal({});
  protected readonly appelOffreService = inject(AppelOffreService);

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public readonly router = inject(Router);
  protected readonly critereService = inject(CritereService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: ICritere): number => this.critereService.getCritereIdentifier(item);

  /*ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }*/
  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => {
          this.fillComponentAttributeFromRoute(params, data);

          // 2. RECUPERATION DE L'ID (Format JHipster filter : 'appelOffreId.equals')
          const aoId = params.get('appelOffreId.equals');
          if (aoId) {
            this.appelOffreId = Number(aoId);
            // RÉCUPÉRATION DU TITRE POUR L'AFFICHAGE
            this.appelOffreService.find(this.appelOffreId).subscribe(res => this.appelOffreDetails.set(res.body));
          }
        }),
        tap(() => this.load()),
      )
      .subscribe();
  }

  // 3. LA MÉTHODE DE GÉNÉRATION
  genererViaIA(): void {
    if (!this.appelOffreId) {
      alert("Veuillez d'abord filtrer par un Appel d'Offre pour utiliser l'IA.");
      return;
    }

    this.isLoading = true;
    this.critereService.suggestForAppelOffre(this.appelOffreId).subscribe({
      next: () => {
        this.isLoading = false;
        this.load(); // Recharge la liste pour voir les suggestions orange "À VALIDER"
      },
      error: () => {
        this.isLoading = false;
        alert("Erreur lors de la génération des critères par l'IA.");
      },
    });
  }

  // MÉTHODE DE VALIDATION
  valider(critere: ICritere): void {
    if (critere.id) {
      const copy = { ...critere, statut: 'VALIDE' as any };
      this.critereService.update(copy).subscribe(() => this.load());
    }
  }

  // MÉTHODE DE REJET
  rejeter(critere: ICritere): void {
    if (critere.id) {
      const copy = { ...critere, statut: 'REJETE' as any };
      this.critereService.update(copy).subscribe(() => this.load());
    }
  }

  delete(critere: ICritere): void {
    const modalRef = this.modalService.open(CritereDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.critere = critere;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page, event);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState());
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.criteres.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: ICritere[] | null): ICritere[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    return this.critereService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState): void {
    const queryParamsObj = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
