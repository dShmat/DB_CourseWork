export interface IUserProject {
    id?: number;
    userId?: number;
    projectId?: number;
}

export class UserProject implements IUserProject {
    constructor(public id?: number, public userId?: number, public projectId?: number) {}
}
