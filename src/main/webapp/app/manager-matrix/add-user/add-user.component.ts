import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService } from 'ng-jhipster';
import { UserManagerService } from 'app/entities/user-manager.service';
import { IUserManager, UserManager } from 'app/shared/model/user-manager.model';
import { IPeriod } from 'app/shared/model/period.model';
import { IUser, UserService } from 'app/core';
@Component({
    selector: 'jhi-user-add',
    templateUrl: './add-user.component.html',
    styles: []
})
export class AddUserComponent implements OnInit {
    @Input() period: IPeriod;
    users: IUser[];
    selectedUser: IUser;
    username: string;
    isSaving: boolean;

    constructor(
        protected userService: UserService,
        protected userManagerService: UserManagerService,
        protected activeModal: NgbActiveModal,
        protected jhiAlertService: JhiAlertService
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.userManagerService.getManagerUsersByPeriod(this.period.id).subscribe(
            (res: HttpResponse<IUser[]>) => {
                this.users = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    search = (text$: Observable<string>) =>
        text$.pipe(
            debounceTime(200),
            distinctUntilChanged(),
            switchMap(term => {
                console.log(term);
                return this.userService.getTypeheadUsers(term);
            }),
            map((res: HttpResponse<IUser[]>) => res.body)
        );

    formatter = (result: IUser) => {
        this.selectedUser = result;
        return `${result.firstName} ${result.lastName}`;
    };
    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
    dismiss() {
        this.activeModal.close();
    }
    save() {
        this.isSaving = true;
        if (!this.isUserManager()) {
            console.log(this.selectedUser);
            this.subscribeToSaveResponse(
                this.userManagerService.createUserManagerFromUser(new UserManager(null, null, this.selectedUser, this.period))
            );
        } else {
            this.jhiAlertService.error('User already exists');
            this.activeModal.close();
        }
    }
    subscribeToSaveResponse(result: Observable<HttpResponse<IUserManager>>) {
        result.subscribe(
            (res: HttpResponse<IUserManager>) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onSaveError(res.message)
        );
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
    isUserManager(): boolean {
        if (this.users === undefined) {
            return false;
        }
        return this.users.find(u => u.id === this.selectedUser.id) ? true : false;
    }
}
