import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IReport } from 'app/shared/model/report.model';
import { IPeriod } from 'app/shared/model/period.model';

@Component({
    selector: 'jhi-matrix-cell',
    templateUrl: './matrix-cell.component.html',
    styleUrls: ['./matrix-cell.css']
})
export class MatrixCellComponent implements OnInit {
    @Input() report: IReport;
    @Input() currentPeriod: IPeriod;
    @Input() selectedPeriod: IPeriod;
    @Output() addUserToProjectEvent = new EventEmitter();
    @Output() deleteUserProjectEvent = new EventEmitter();
    constructor() {}

    ngOnInit() {}
}
