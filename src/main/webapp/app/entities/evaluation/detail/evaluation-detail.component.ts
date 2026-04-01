import { Component, inject, input, ViewEncapsulation } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IEvaluation } from '../evaluation.model';
import { MarkdownPipe } from 'app/shared/pipe/markdown.pipe'; // Ajustez le chemin
import { SafeHtmlPipe } from 'app/shared/pipe/safe-html.pipe'; // Ajuste le chemin

@Component({
  selector: 'jhi-evaluation-detail',
  templateUrl: './evaluation-detail.component.html',
  styleUrls: ['./evaluation-detail.component.scss'],
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, MarkdownPipe, SafeHtmlPipe],
  encapsulation: ViewEncapsulation.None,
})
export class EvaluationDetailComponent {
  evaluation = input<IEvaluation | null>(null);

  protected dataUtils = inject(DataUtils);

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
