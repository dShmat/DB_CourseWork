import { Component, Input, OnInit } from '@angular/core';
import { IProject } from 'app/shared/model/project.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ManagerProjectService } from 'app/entities/manager-project.service';
import { JhiAlertService } from 'ng-jhipster';
import { IPeriod } from 'app/shared/model/period.model';

@Component({
    selector: 'jhi-delete-manager-project',
    templateUrl: './delete-manager-project.component.html',
    styles: []
})
export class DeleteManagerProjectComponent implements OnInit {
    @Input() period: IPeriod;
    @Input() selectedProject: IProject;
    isSaving: boolean;

    constructor(
        protected activeModal: NgbActiveModal,
        protected managerProjectService: ManagerProjectService,
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
        this.managerProjectService
            .deleteByProjectAndPeriod(this.selectedProject.id, this.period.id)
            .subscribe((res: HttpResponse<IProject>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError(res.message));
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
