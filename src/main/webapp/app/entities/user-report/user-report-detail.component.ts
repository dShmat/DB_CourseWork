import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IUserReport } from 'app/shared/model/user-report.model';

@Component({
    selector: 'jhi-user-report-detail',
    templateUrl: './user-report-detail.component.html'
})
export class UserReportDetailComponent implements OnInit {
    userReport: IUserReport;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ userReport }) => {
            this.userReport = userReport;
        });
    }

    previousState() {
        window.history.back();
    }
}
