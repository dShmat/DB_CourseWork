import { Component, OnInit } from '@angular/core';
import { IReport } from 'app/shared/model/report.model';
import { IProject, Project } from 'app/shared/model/project.model';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from 'app/entities/project';
import { JhiAlertService } from 'ng-jhipster';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { map, mergeMap } from 'rxjs/operators';
import { IPeriod } from 'app/shared/model/period.model';
import { PeriodService } from 'app/entities/period.service';
import { AccountService } from 'app/core';
import * as moment from 'moment';

@Component({
    selector: 'jhi-report-view',
    templateUrl: './report-view.component.html',
    styles: [
        `
            label {
                font-weight: bold;
                font-size: 17px;
            }
            .report-header {
                font-size: 20px;
            }
        `
    ]
})
export class ReportViewComponent implements OnInit {
    report: IReport;
    project: IProject;
    period: IPeriod;
    currentAccount: Account;

    constructor(
        protected accountService: AccountService,
        protected activatedRoute: ActivatedRoute,
        protected projectService: ProjectService,
        protected periodService: PeriodService,
        protected jhiAlertService: JhiAlertService
    ) {}

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            console.log(account);
        });
        this.activatedRoute.data.subscribe(({ report }) => {
            this.report = report;
        });
        this.loadAll();
    }

    loadAll() {
        this.projectService
            .find(this.report.project.id)
            .pipe(
                map((res: HttpResponse<Project>) => {
                    this.project = res.body;
                    return this.report.period.id;
                }),
                mergeMap(currPeriodId => this.periodService.find(currPeriodId)),
                map((res: HttpResponse<IPeriod>) => res.body)
            )

            .subscribe(
                (res: IPeriod) => {
                    this.period = res;
                    console.log('ReportView Period: ', this.period);
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    previousState() {
        window.history.back();
    }

    onError(msg: string) {
        this.jhiAlertService.error(msg);
    }
}
