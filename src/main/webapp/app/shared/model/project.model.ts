export interface IProject {
    id?: number;
    name?: string;
    description?: string;
    type?: string;
}

export class Project implements IProject {
    constructor(public id?: number, public name?: string, public description?: string, public type?: string) {}
}
