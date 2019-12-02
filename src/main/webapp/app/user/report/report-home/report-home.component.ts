import { Component, OnInit } from '@angular/core';
import { IReport } from 'app/shared/model/report.model';
import { IProject, Project } from 'app/shared/model/project.model';
import { ReportService } from 'app/entities/report';
import { ProjectService } from 'app/entities/project';
import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { AccountService } from 'app/core';
import { Router } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { map, mergeMap } from 'rxjs/operators';
import { PeriodService } from 'app/entities/period.service';
import { IPeriod } from 'app/shared/model/period.model';

@Component({
    selector: 'jhi-report-home',
    templateUrl: './report-home.component.html',
    styles: [
        `
            table {
                width: 100%;
            }

            th {
                text-align: center;
            }

            td.counterColumn {
                width: 3%;
                text-align: center;
            }

            td.projectColumn {
                width: 10%;
            }

            td.hoursColumn {
                width: 3%;
                text-align: center;
            }

            td.activitiesColumn {
                max-width: 0;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            td.daysColumn {
                width: 4%;
                text-align: center;
            }
        `
    ]
})
export class ReportHomeComponent implements OnInit {
    reports: IReport[];
    projects: IProject[];
    period: IPeriod;
    periodId: number;
    currentAccount: any;
    page: any;
    predicate: any;
    reverse: any;

    currentPeriod: IPeriod;

    constructor(
        protected reportService: ReportService,
        protected projectService: ProjectService,
        protected periodService: PeriodService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService,
        protected router: Router
    ) {
        this.reports = [];
        this.predicate = 'id';
        this.reverse = true;
    }

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.periodService.findCurrentPeriod().subscribe(
            (res: HttpResponse<IPeriod>) => {
                this.currentPeriod = res.body;
                this.period = res.body;
                this.loadAll(res.body);
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    loadAll(period: IPeriod) {
        this.period = period;
        this.reportService
            .getReportsByUserPeriodId(period.id)
            .pipe(
                map((res: HttpResponse<IReport[]>) => {
                    this.reports = res.body;
                    console.log('HomeTable UserReports: ', res.body);
                    this.periodId = res.body.map(r => r.period.id)[0];
                    return res.body.map(r => r.project.id);
                }),
                mergeMap(userProjectIds => this.projectService.getProjectsByIds(userProjectIds)),
                map((res: HttpResponse<Project[]>) => {
                    console.log('HomeTable Projects: ', res.body);
                    this.projects = res.body;
                    return this.projects;
                })
            )
            .subscribe(
                (res: IProject[]) => {
                    this.projects = res;
                },

                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    isUserProject(report: IReport, project: IProject) {
        return report.project.id === project.id;
    }
}
