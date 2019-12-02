import { IReport } from 'app/shared/model/report.model';
import { IUser } from 'app/core/user/user.model';

export interface IUserReport {
    id?: number;
    report?: IReport;
    user?: IUser;
}

export class UserReport implements IUserReport {
    constructor(public id?: number, public report?: IReport, public user?: IUser) {}
}
