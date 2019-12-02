import { Component, Input, OnInit } from '@angular/core';
import { IProject } from 'app/shared/model/project.model';
import { ActivatedRoute } from '@angular/router';
import { ProjectService } from 'app/entities/project';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService } from 'ng-jhipster';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';

@Component({
    selector: 'jhi-project-edit',
    templateUrl: './project-edit.component.html',
    styles: []
})
export class ProjectEditComponent implements OnInit {
    @Input() project: IProject;
    isSaving: boolean;

    constructor(
        protected jhiAlertService: JhiAlertService,
        public activeModal: NgbActiveModal,
        protected projectService: ProjectService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.loadAll();
    }

    loadAll() {
        this.projectService.find(this.project.id).subscribe(
            (res: HttpResponse<IProject>) => {
                this.project = res.body;
                console.log('Project to edit: ', this.project);
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    save() {
        console.log(this.project);
        this.isSaving = true;
        if (this.project.name != null && this.project.type != null) {
            this.subscribeToSaveResponse(this.projectService.update(this.project));
        }
    }

    dismiss() {
        this.activeModal.close();
    }

    subscribeToSaveResponse(result: Observable<HttpResponse<IProject>>) {
        result.subscribe((res: HttpResponse<IProject>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError(res.message));
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

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
