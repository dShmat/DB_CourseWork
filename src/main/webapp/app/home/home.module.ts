import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { CollectmeSharedModule } from 'app/shared';
import { HOME_ROUTE, HomeComponent } from './';
import { ManagerMatrixComponent } from 'app/manager-matrix/manager-matrix.component';
import { ProjectCreateComponent } from 'app/manager-matrix/project-create/project-create.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AddUserComponent } from 'app/manager-matrix/add-user/add-user.component';
import { ReportHomeComponent } from 'app/user/report/report-home/report-home.component';
import { DeleteReportComponent } from 'app/manager-matrix/delete-report/delete-report.component';
import { MatrixCellComponent } from 'app/manager-matrix/matrix-cell/matrix-cell.component';
import { DeleteManagerProjectComponent } from 'app/manager-matrix/delete-manager-project/delete-manager-project.component';
import { NavigationBarComponent } from 'app/manager-matrix/navigation_bar/navigation-bar.component';
import { DeleteUserManagerComponent } from 'app/manager-matrix/delete-user-manager/delete-user-manager.component';
import { SendReportsComponent } from 'app/manager-matrix/send-reports/send-reports.component';
import { ProjectEditComponent } from 'app/manager-matrix/project-edit/project-edit.component';

@NgModule({
    imports: [CollectmeSharedModule, RouterModule.forChild([HOME_ROUTE]), FormsModule, ReactiveFormsModule],
    declarations: [
        HomeComponent,
        ManagerMatrixComponent,
        ReportHomeComponent,
        AddUserComponent,
        DeleteReportComponent,
        MatrixCellComponent,
        DeleteManagerProjectComponent,
        NavigationBarComponent,
        DeleteUserManagerComponent,
        SendReportsComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: [
        ProjectCreateComponent,
        AddUserComponent,
        DeleteReportComponent,
        MatrixCellComponent,
        DeleteManagerProjectComponent,
        DeleteUserManagerComponent,
        SendReportsComponent,
        ProjectEditComponent
    ]
})
export class CollectmeHomeModule {}
