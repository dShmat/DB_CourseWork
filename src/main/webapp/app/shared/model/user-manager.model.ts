import { IManager } from 'app/shared/model/manager.model';
import { IUser } from 'app/core';
import { IPeriod } from 'app/shared/model/period.model';

export interface IUserManager {
    id?: number;
    manager?: IManager;
    user?: IUser;
    period?: IPeriod;
}
export class UserManager implements IUserManager {
    constructor(public id?: number, public manager?: IManager, public user?: IUser, public period?: IPeriod) {}
}
