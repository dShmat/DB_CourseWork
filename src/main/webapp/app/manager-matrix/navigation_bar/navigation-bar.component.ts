import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IPeriod } from 'app/shared/model/period.model';
import { PeriodService } from 'app/entities/period.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService } from 'ng-jhipster';

@Component({
    selector: 'jhi-navigation-bar',
    templateUrl: './navigation-bar.component.html',
    styles: [
        `
            .nav-tabs {
                border-bottom: 0;
            }
        `
    ]
})
export class NavigationBarComponent implements OnInit {
    @Input() selectedPeriod: IPeriod;
    @Output() reloadAllEvent = new EventEmitter<IPeriod>();
    periods: IPeriod[];

    constructor(protected periodService: PeriodService, protected jhiAlertService: JhiAlertService) {}

    ngOnInit() {
        this.periodService.query().subscribe(
            (res: HttpResponse<IPeriod[]>) => {
                this.periods = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    onError(message: string) {
        this.jhiAlertService.error(message, null, null);
    }

    onClick(period: IPeriod) {
        this.reloadAllEvent.emit(period);
    }

    monthAsNumber(month: Date) {
        return new Date(month).getMonth() + 1;
    }
}
