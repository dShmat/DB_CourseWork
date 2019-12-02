import { IUser } from 'app/core';
import { IProject } from 'app/shared/model/project.model';
import { IPeriod } from 'app/shared/model/period.model';

export interface IReport {
    id?: number;
    hours?: number;
    activities?: string;
    daysAbsent?: number;
    user?: IUser;
    project?: IProject;
    period?: IPeriod;
}

export class Report implements IReport {
    constructor(
        public id?: number,
        public hours?: number,
        public activities?: string,
        public daysAbsent?: number,
        public user?: IUser,
        public project?: IProject,
        public period?: IPeriod
    ) {}
}
