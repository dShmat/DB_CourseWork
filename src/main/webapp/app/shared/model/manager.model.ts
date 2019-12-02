export interface IManager {
    id?: number;
    userId?: number;
}
export class Manager implements IManager {
    constructor(public id?: number, public userId?: number) {}
}
