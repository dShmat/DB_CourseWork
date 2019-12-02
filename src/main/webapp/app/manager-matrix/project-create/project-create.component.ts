import { Component, Input, OnInit } from '@angular/core';
import { IProject } from 'app/shared/model/project.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { IUserProject } from 'app/shared/model/user-project.model';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { ManagerProjectService } from 'app/entities/manager-project.service';
import { ManagerProject } from 'app/shared/model/manager-project.model';
import { IPeriod } from 'app/shared/model/period.model';
import { ProjectService } from 'app/entities/project';

@Component({
    selector: 'jhi-project-create-component',
    templateUrl: './project-create.component.html',
    styles: []
})
export class ProjectCreateComponent implements OnInit {
    @Input() period: IPeriod;
    project: IProject;
    isSaving: boolean;

    constructor(
        protected projectService: ProjectService,
        public activeModal: NgbActiveModal,
        protected activatedRoute: ActivatedRoute,
        protected jhiAlertService: JhiAlertService,
        protected managerProjectService: ManagerProjectService
    ) {
        this.project = {};
    }

    ngOnInit() {
        this.isSaving = false;
    }

    dismiss() {
        this.activeModal.close();
    }

    save() {
        console.log(this.period);
        this.isSaving = true;
        if (this.project.name != null && this.project.type != null) {
            this.subscribeToSaveResponse(
                this.managerProjectService.createManagerProjectAndProject(new ManagerProject(null, null, this.project, this.period))
            );
        }
    }

    subscribeToSaveResponse(result: Observable<HttpResponse<IProject>>) {
        result.subscribe(
            (res: HttpResponse<IUserProject>) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onSaveError(res.message)
        );
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.activeModal.close();
    }

    selectChangeHandler(event: any) {
        this.project.type = event.target.value;
    }

    protected onSaveError(error: string) {
        this.jhiAlertService.error(error, null, null);
        this.isSaving = false;
        this.activeModal.close();
    }
}
