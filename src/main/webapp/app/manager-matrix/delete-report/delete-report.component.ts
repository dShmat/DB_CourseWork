import { Component, Input, OnInit } from '@angular/core';
import { IReport } from 'app/shared/model/report.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService } from 'ng-jhipster';
import { ReportService } from 'app/entities/report';

@Component({
    selector: 'jhi-delete-report',
    templateUrl: './delete-report.component.html',
    styles: []
})
export class DeleteReportComponent implements OnInit {
    @Input() selectedReport: IReport;
    isSaving: boolean;
    constructor(
        protected reportService: ReportService,
        protected activeModal: NgbActiveModal,
        protected jhiAlertService: JhiAlertService
    ) {}

    ngOnInit() {
        this.isSaving = false;
    }
    dismiss() {
        this.activeModal.close();
    }
    delete() {
        this.isSaving = true;
        this.reportService
            .delete(this.selectedReport.id)
            .subscribe((res: HttpResponse<IReport>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError(res.message));
    }
    protected onSaveSuccess() {
        this.isSaving = false;
        this.activeModal.close();
    }

    protected onSaveError(error: string) {
        this.jhiAlertService.error(error, null, null);
        this.isSaving = false;
        this.activeModal.close();
    }
}
