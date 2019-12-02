import { Component, Input, OnInit } from '@angular/core';
import { IPeriod } from 'app/shared/model/period.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiAlertService } from 'ng-jhipster';
import { UserManagerService } from 'app/entities/user-manager.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { IProject } from 'app/shared/model/project.model';
import { IUser } from 'app/core';

@Component({
    selector: 'jhi-delete-user-manager',
    templateUrl: './delete-user-manager.component.html',
    styles: []
})
export class DeleteUserManagerComponent implements OnInit {
    @Input() period: IPeriod;
    @Input() user: IUser;
    isSaving: boolean;

    constructor(
        protected activeModal: NgbActiveModal,
        protected userManagerService: UserManagerService,
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
        this.userManagerService
            .deleteByUserAndPeriod(this.user.id, this.period.id)
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
