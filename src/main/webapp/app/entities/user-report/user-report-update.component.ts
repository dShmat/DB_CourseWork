import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IUserReport } from 'app/shared/model/user-report.model';
import { UserReportService } from './user-report.service';
import { IReport } from 'app/shared/model/report.model';
import { ReportService } from 'app/entities/report';
import { IUser, UserService } from 'app/core';

@Component({
    selector: 'jhi-user-report-update',
    templateUrl: './user-report-update.component.html'
})
export class UserReportUpdateComponent implements OnInit {
    userReport: IUserReport;
    isSaving: boolean;

    reports: IReport[];

    users: IUser[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected userReportService: UserReportService,
        protected reportService: ReportService,
        protected userService: UserService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ userReport }) => {
            this.userReport = userReport;
        });
        this.reportService
            .query({ filter: 'userreport-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<IReport[]>) => mayBeOk.ok),
                map((response: HttpResponse<IReport[]>) => response.body)
            )
            .subscribe(
                (res: IReport[]) => {
                    if (!this.userReport.report || !this.userReport.report.id) {
                        this.reports = res;
                    } else {
                        this.reportService
                            .find(this.userReport.report.id)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IReport>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IReport>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IReport) => (this.reports = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        this.userService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
                map((response: HttpResponse<IUser[]>) => response.body)
            )
            .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.userReport.id !== undefined) {
            this.subscribeToSaveResponse(this.userReportService.update(this.userReport));
        } else {
            this.subscribeToSaveResponse(this.userReportService.create(this.userReport));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserReport>>) {
        result.subscribe((res: HttpResponse<IUserReport>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackReportById(index: number, item: IReport) {
        return item.id;
    }

    trackUserById(index: number, item: IUser) {
        return item.id;
    }
}
