import { HttpHeaders } from '@angular/common/http';
import { Component, NgZone, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subscription, combineLatest, filter, tap } from 'rxjs';

import { FormsModule } from '@angular/forms';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { ItemCountComponent } from 'app/shared/pagination';
import SharedModule from 'app/shared/shared.module';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { FilterComponent, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { IAppelOffre } from '../appel-offre.model';
import { AppelOffreDeleteDialogComponent } from '../delete/appel-offre-delete-dialog.component';
import { AppelOffreService, EntityArrayResponseType } from '../service/appel-offre.service';

@Component({
  selector: 'jhi-appel-offre',
  templateUrl: './appel-offre.component.html',
  styleUrls: ['./appel-offre.component.scss'],
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    FormatMediumDatetimePipe,
    FilterComponent,
    ItemCountComponent,
  ],
})
export class AppelOffreComponent implements OnInit {
  subscription: Subscription | null = null;
  appelOffres = signal<IAppelOffre[]>([]);
  appelOffreDetails = signal<IAppelOffre | null>(null);
  isLoading = false;
  appelOffreId?: number;
  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();

  itemsPerPage = ITEMS_PER_PAGE;
  totalItems = 0;
  page = 1;

  public readonly router = inject(Router);
  protected readonly appelOffreService = inject(AppelOffreService);
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (item: IAppelOffre): number => this.appelOffreService.getAppelOffreIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => {
          // 1. Gestion standard JHipster (Pagination, tri)
          this.fillComponentAttributeFromRoute(params, data);

          // 2. FLUIDITÉ : Extraction de l'ID de l'Appel d'Offre depuis le filtre
          // JHipster utilise souvent le format 'nomChamp.equals' dans l'URL
          const aoId = params.get('appelOffreId.equals') ?? params.get('appelOffreId');

          if (aoId) {
            this.appelOffreId = Number(aoId);
            // 3. OPTIMISATION : On charge les détails (titre) pour l'afficher dans l'en-tête
            this.appelOffreService.find(this.appelOffreId).subscribe(res => {
              this.appelOffreDetails.set(res.body);
            });
          } else {
            this.appelOffreId = undefined;
            this.appelOffreDetails.set(null);
          }
        }),
        tap(() => this.load()), // Charge les données de la table
      )
      .subscribe();

    // Gestion des changements de filtres manuels (barre de recherche)
    this.filters.filterChanges.subscribe(filterOptions => this.handleNavigation(1, this.sortState(), filterOptions));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(appelOffre: IAppelOffre): void {
    const modalRef = this.modalService.open(AppelOffreDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.appelOffre = appelOffre;
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
    this.handleNavigation(this.page, event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page = +(page ?? 1);
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.appelOffres.set(dataFromBody);
  }

  protected fillComponentAttributesFromResponseBody(data: IAppelOffre[] | null): IAppelOffre[] {
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems = Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER));
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { page, filters } = this;

    this.isLoading = true;
    const pageToLoad: number = page;
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    filters.filterOptions.forEach(filterOption => {
      queryObject[filterOption.name] = filterOption.values;
    });
    return this.appelOffreService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage,
      sort: this.sortService.buildSortParam(sortState),
    };

    filterOptions?.forEach(filterOption => {
      queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
    });

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
