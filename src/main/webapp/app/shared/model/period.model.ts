import { Moment } from 'moment';

export interface IPeriod {
    id?: number;
    month?: Moment;
}

export class Period implements IPeriod {
    constructor(public id?: number, public month?: Moment) {}
}
