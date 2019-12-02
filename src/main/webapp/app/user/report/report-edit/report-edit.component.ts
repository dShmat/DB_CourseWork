import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { IReport } from 'app/shared/model/report.model';
import { IProject, Project } from 'app/shared/model/project.model';
import { JhiAlertService } from 'ng-jhipster';
import { ReportService } from 'app/entities/report';
import { ProjectService } from 'app/entities/project';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';
import { IPeriod } from 'app/shared/model/period.model';
import { PeriodService } from 'app/entities/period.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { absentDaysValidator } from 'app/validators/absent-day-validator';
import { monthlyHoursValidator } from 'app/validators/monthly-hours-validator';
import * as moment from 'moment';

@Component({
    selector: 'jhi-report-edit',
    templateUrl: './report-edit.component.html',
    styles: [
        `
            label {
                font-weight: bold;
                font-size: 17px;
            }

            text-edit {
                overflow: hidden;
            }
            .report-header {
                font-size: 20px;
            }
        `
    ]
})
export class ReportEditComponent implements OnInit, AfterViewInit {
    @ViewChild('activitiesInput') activitiesInput: ElementRef;
    report: IReport;
    project: IProject;
    period: IPeriod;
    isSaving: boolean;
    reportForm: FormGroup;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected reportService: ReportService,
        protected projectService: ProjectService,
        protected periodService: PeriodService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ report }) => {
            this.report = report;
        });
        this.loadAll();
        this.reportForm = this.createFormGroup();
    }

    ngAfterViewInit() {
        this.onInput(this.activitiesInput.nativeElement);
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

    save() {
        this.report.hours = +this.report.hours;
        this.report.daysAbsent = +this.report.daysAbsent;
        this.isSaving = true;
        if (this.report.id !== undefined) {
            this.subscribeToSaveResponse(this.reportService.update(this.report));
        } else {
            this.subscribeToSaveResponse(this.reportService.create(this.report));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IReport>>) {
        result.subscribe((res: HttpResponse<IReport>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    onInput(target) {
        target.style.height = 'auto';
        target.style.height = target.scrollHeight + 'px';
    }

    createFormGroup() {
        return new FormGroup({
            days: new FormControl('', [Validators.required, Validators.min(0), Validators.max(23), absentDaysValidator]),
            hours: new FormControl('', [Validators.required, Validators.min(0), Validators.max(744), monthlyHoursValidator]),
            activities: new FormControl('')
        });
    }

    get days() {
        return this.reportForm.get('days');
    }

    get hours() {
        return this.reportForm.get('hours');
    }
}
