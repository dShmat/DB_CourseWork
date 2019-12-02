import { Component, Input, OnInit } from '@angular/core';
import { AccountService, IUser, UserService } from 'app/core';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { JhiAlertService } from 'ng-jhipster';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IReport } from 'app/shared/model/report.model';
import { ReportService } from 'app/entities/report';
import { IPeriod } from 'app/shared/model/period.model';
import { FormControl } from '@angular/forms';

@Component({
    selector: 'jhi-send-reports',
    templateUrl: './send-reports.component.html',
    styles: [
        `
            li .btn-delete {
                padding: 0.75em;
                color: red;
                visibility: hidden;
            }

            li:hover .btn-delete {
                visibility: visible;
            }
            li {
                padding-top: 0;
                padding-bottom: 0;
                border-bottom: solid 1px #dddddd;
            }
            .input-group {
                margin-bottom: 3em;
            }
            .default {
                padding-left: 4em;
            }
            b {
                width: 4em;
            }
            .subject {
                margin-bottom: 0;
            }
        `
    ]
})
export class SendReportsComponent implements OnInit {
    users: IUser[];
    selectedUser: IUser;
    subject: string;
    @Input() period: IPeriod;
    currentAccount: any;

    constructor(
        private userService: UserService,
        private jhiAlertService: JhiAlertService,
        private activeModal: NgbActiveModal,
        private reportService: ReportService,
        private accountService: AccountService
    ) {
        this.users = [];
    }

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            this.users.push(account);
        });
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

    sendReports() {
        this.reportService
            .sendReports(this.period, this.users.map(u => u.email), this.subject)
            .subscribe((res: HttpResponse<IReport>) => this.onSendSuccess(), (res: HttpErrorResponse) => this.onSendError(res.message));
        console.log(this.subject);
        this.activeModal.close();
    }

    onSendSuccess() {
        this.jhiAlertService.addAlert({ type: 'success', msg: 'msg.messages.success', timeout: 5000 }, []);
    }

    onSendError(message: string) {
        this.jhiAlertService.addAlert(
            {
                type: 'warning',
                msg: 'msg.messages.error',
                params: { message },
                timeout: 5000
            },
            []
        );
    }

    addUser() {
        this.users.push(this.selectedUser);
    }

    deleteUser(i: number) {
        this.users.splice(i, 1);
    }
}
