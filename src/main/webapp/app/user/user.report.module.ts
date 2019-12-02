import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { CollectmeSharedModule } from 'app/shared';
// import { ReportHomeComponent } from  'app/user/report/report-home/report-home.component';
import { ReportViewComponent } from 'app/user/report/report-view/report-view.component';
import { ReportEditComponent } from 'app/user/report/report-edit/report-edit.component';
import { monthlyReportRoute } from 'app/user/user.report.route';
import { ReactiveErrors } from '@angular/forms/src/directives/reactive_errors';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

const ENTITY_STATES = [...monthlyReportRoute];

@NgModule({
    imports: [CollectmeSharedModule, RouterModule.forChild(ENTITY_STATES), ReactiveFormsModule, FormsModule],
    declarations: [ReportViewComponent, ReportEditComponent],
    entryComponents: [ReportEditComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CollectmeMonthlyReportModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
