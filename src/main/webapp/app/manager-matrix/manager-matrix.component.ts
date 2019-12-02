import { JhiAlertService, JhiEventManager } from 'ng-jhipster';
import { UserManagerService } from 'app/entities/user-manager.service';
import { Observable } from 'rxjs';
import { filter, map, mergeMap } from 'rxjs/operators';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { IManager } from 'app/shared/model/manager.model';
import { IReport, Report } from 'app/shared/model/report.model';
import { IProject } from 'app/shared/model/project.model';
import { ActivatedRoute } from '@angular/router';
import { ProjectCreateComponent } from 'app/manager-matrix/project-create/project-create.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AddUserComponent } from 'app/manager-matrix/add-user/add-user.component';
import { DeleteReportComponent } from 'app/manager-matrix/delete-report/delete-report.component';
import { IPeriod } from 'app/shared/model/period.model';
import { IManagerProject } from 'app/shared/model/manager-project.model';
import { ManagerProjectService } from 'app/entities/manager-project.service';
import { DeleteManagerProjectComponent } from 'app/manager-matrix/delete-manager-project/delete-manager-project.component';
import { PeriodService } from 'app/entities/period.service';
import { IUserManager } from 'app/shared/model/user-manager.model';
import { DeleteUserManagerComponent } from 'app/manager-matrix/delete-user-manager/delete-user-manager.component';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AccountService, IUser, UserService } from 'app/core';
import { ProjectService } from 'app/entities/project';
import { ReportService } from 'app/entities/report';
import { SendReportsComponent } from 'app/manager-matrix/send-reports/send-reports.component';
import { ProjectEditComponent } from 'app/manager-matrix/project-edit/project-edit.component';

@Component({
    selector: 'jhi-manager-matrix',
    templateUrl: './manager-matrix.component.html',
    styleUrls: ['./matrix.css']
})
export class ManagerMatrixComponent implements OnInit {
    manager: IManager;
    reports: IReport[];
    userIds: number[];
    users: IUser[];
    projects: IProject[];
    selectedReport: IReport;
    period: IPeriod;
    currentPeriod: IPeriod;
    @ViewChild('header')
    header: ElementRef;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected userManagerService: UserManagerService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService,
        protected reportService: ReportService,
        protected userService: UserService,
        protected projectService: ProjectService,
        protected activatedRoute: ActivatedRoute,
        protected modalService: NgbModal,
        protected managerProjectService: ManagerProjectService,
        protected periodService: PeriodService
    ) {}

    ngOnInit() {
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
        this.reports = null;
        this.userManagerService
            .getManagerUsersByPeriod(period.id)
            .pipe(
                filter((res: HttpResponse<IUser[]>) => res.ok),
                map((res: HttpResponse<IUser[]>) => {
                    console.log('Users', res.body);
                    this.users = res.body.sort((a, b) => {
                        return a.firstName.localeCompare(b.firstName);
                    });
                    return res.body.map(u => u.id);
                }),
                mergeMap(userIds => this.reportService.getReportsByUsersAndPeriod(userIds, period.id)),
                map((res: HttpResponse<IReport[]>) => res.body)
            )
            .subscribe(
                (res: IReport[]) => {
                    console.log('Projects', res);
                    this.reports = res;
                },
                (res: HttpErrorResponse) => console.log(res.message)
            );
        this.projectService.getManagerProjectsByPeriod(period.id).subscribe(
            (res: HttpResponse<IProject[]>) => {
                this.projects = res.body;
            },
            (res: HttpErrorResponse) => console.log(res.message)
        );
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    protected subscribeToAdding(result: Observable<HttpResponse<IReport>>) {
        result.subscribe((res: HttpResponse<IReport>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError(res.message));
    }

    protected onSaveSuccess() {
        this.loadAll(this.period);
    }

    protected onSaveError(error: string) {
        this.jhiAlertService.error(error, null, null);
    }

    findReport(project: IProject, user: IUser) {
        const report = this.reports.find(r => r.project.id === project.id && r.user.id === user.id && r.period.id === this.period.id);
        return report;
    }

    addUserToProject(user: IUser, project: IProject) {
        this.subscribeToAdding(
            this.reportService.createAutomaticallyForNextMonth(new Report(null, null, null, null, user, project, this.period))
        );
    }

    toggleChild() {
        const modalRef = this.modalService.open(ProjectCreateComponent as Component, {
            size: 'lg',
            backdrop: true,
            centered: true
        });
        modalRef.componentInstance.period = this.period;
        modalRef.result.then(this.loadAll.bind(this, this.period));
    }

    toggleUserChild() {
        const modalRef = this.modalService.open(AddUserComponent as Component, {
            size: 'lg',
            backdrop: true,
            centered: true
        });
        modalRef.componentInstance.period = this.period;
        modalRef.result.then(this.loadAll.bind(this, this.period));
    }

    editProject(project: IProject) {
        /**
         * EDIT DIALOG
         */
        const modalProjRef = this.modalService.open(ProjectEditComponent as Component, {
            size: 'lg',
            backdrop: true,
            centered: true
        });
        modalProjRef.componentInstance.project = project;
        modalProjRef.result.then(this.loadAll.bind(this, this.period));
    }

    deleteReport(user: IUser, project: IProject) {
        const report = this.findReport(project, user);
        if (report.hours || report.activities) {
            const modalRef = this.modalService.open(DeleteReportComponent as Component, {
                size: 'lg',
                backdrop: true,
                centered: true
            });
            modalRef.componentInstance.selectedReport = report;
            modalRef.result.then(this.loadAll.bind(this, this.period));
        } else {
            this.reportService
                .delete(report.id)
                .subscribe((res: HttpResponse<IReport>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onError(res.message));
        }
    }

    deleteManagerProject(project: IProject) {
        if (!this.isFulfilledForProject(project)) {
            this.managerProjectService
                .deleteByProjectAndPeriod(project.id, this.period.id)
                .subscribe(
                    (res: HttpResponse<IManagerProject>) => this.onSaveSuccess(),
                    (res: HttpErrorResponse) => this.onSaveError(res.message)
                );
        } else {
            const modalRef = this.modalService.open(DeleteManagerProjectComponent as Component, {
                size: 'lg',
                backdrop: true,
                centered: true
            });
            modalRef.componentInstance.selectedProject = project;
            modalRef.componentInstance.period = this.period;
            modalRef.result.then(this.loadAll.bind(this, this.period));
        }
    }

    isFulfilledForProject(project: IProject) {
        if (this.reports) {
            const reports = this.reports.filter(r => r.project.id === project.id);
            return this.isFulfilled(reports);
        }
        return false;
    }

    isFulFilledForUser(user: IUser) {
        if (this.reports) {
            const reports = this.reports.filter(r => r.user.id === user.id);
            console.log(reports, this.isFulfilled(reports));
            return this.isFulfilled(reports);
        }
        return false;
    }

    isFulfilled(reports) {
        return (
            reports.filter(r => {
                if (r.hours || r.activities) {
                    return r;
                }
            }).length > 0
        );
    }

    deleteUserManager(user: IUser) {
        if (!this.isFulFilledForUser(user)) {
            this.userManagerService
                .deleteByUserAndPeriod(user.id, this.period.id)
                .subscribe(
                    (res: HttpResponse<IUserManager>) => this.onSaveSuccess(),
                    (res: HttpErrorResponse) => this.onSaveError(res.message)
                );
        } else {
            const modalRef = this.modalService.open(DeleteUserManagerComponent as Component, {
                size: 'lg',
                backdrop: true,
                centered: true
            });
            modalRef.componentInstance.user = user;
            modalRef.componentInstance.period = this.period;
            modalRef.result.then(this.loadAll.bind(this, this.period));
        }
    }
    onScroll($event) {
        this.header.nativeElement.classList.add('shader');
    }

    openSendReportsModal() {
        const modalRef = this.modalService.open(SendReportsComponent as Component, {
            size: 'lg',
            backdrop: true
        });
        modalRef.componentInstance.period = this.period;
        modalRef.result.then(this.loadAll.bind(this, this.period));
    }
}
