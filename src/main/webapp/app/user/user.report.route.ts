import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot, Routes } from '@angular/router';
import { Injectable } from '@angular/core';
import { IReport, Report } from 'app/shared/model/report.model';
import { ReportService } from 'app/entities/report';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { UserRouteAccessService } from 'app/core';

import { ReportHomeComponent } from 'app/user/report/report-home/report-home.component';
import { ReportViewComponent } from 'app/user/report/report-view/report-view.component';
import { ReportEditComponent } from 'app/user/report/report-edit/report-edit.component';

@Injectable({ providedIn: 'root' })
export class ReportResolve implements Resolve<IReport> {
    constructor(private service: ReportService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IReport> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Report>) => response.ok),
                map((report: HttpResponse<Report>) => report.body)
            );
        }
        return of(new Report());
    }
}

export const monthlyReportRoute: Routes = [
    {
        path: 'monthly_report',
        component: ReportHomeComponent,
        data: {
            authorities: ['ROLE_USER', 'ROLE_MANAGER'],
            pageTitle: 'collectmeApp.report.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'monthly_report/:id/report_view',
        component: ReportViewComponent,
        resolve: {
            report: ReportResolve
        },
        data: {
            authorities: ['ROLE_USER', 'ROLE_MANAGER'],
            pageTitle: 'collectmeApp.report.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'monthly_report/:id/report_edit',
        component: ReportEditComponent,
        resolve: {
            report: ReportResolve
        },
        data: {
            authorities: ['ROLE_USER', 'ROLE_MANAGER'],
            pageTitle: 'collectmeApp.report.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
