import { IProject } from 'app/shared/model/project.model';
import { IManager } from 'app/shared/model/manager.model';
import { IPeriod } from 'app/shared/model/period.model';

export interface IManagerProject {
    id?: number;
    manager?: IManager;
    project?: IProject;
    period?: IPeriod;
}
export class ManagerProject implements IManagerProject {
    constructor(public id?: number, public manager?: IManager, public project?: IProject, public period?: IPeriod) {}
}
