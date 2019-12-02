import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'project',
                loadChildren: './project/project.module#CollectmeProjectModule'
            },
            {
                path: 'report',
                loadChildren: './report/report.module#CollectmeReportModule'
            },
            {
                path: 'project',
                loadChildren: './project/project.module#CollectmeProjectModule'
            },
            {
                path: 'report',
                loadChildren: './report/report.module#CollectmeReportModule'
            },
            {
                path: 'report',
                loadChildren: './report/report.module#CollectmeReportModule'
            },
            {
                path: 'project',
                loadChildren: './project/project.module#CollectmeProjectModule'
            },
            {
                path: 'project',
                loadChildren: './project/project.module#CollectmeProjectModule'
            },
            {
                path: 'report',
                loadChildren: './report/report.module#CollectmeReportModule'
            },
            {
                path: 'report',
                loadChildren: './report/report.module#CollectmeReportModule'
            }
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
        ])
    ],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CollectmeEntityModule {}
