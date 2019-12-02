import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { IPeriod } from 'app/shared/model/period.model';
import { Observable } from 'rxjs';
import { createRequestOption } from 'app/shared';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { JhiDateUtils } from 'ng-jhipster';
import { Moment } from 'moment';

type EntityResponseType = HttpResponse<IPeriod>;
type EntityArrayResponseType = HttpResponse<IPeriod[]>;

@Injectable({
    providedIn: 'root'
})
export class PeriodService {
    public resourceUrl = SERVER_API_URL + 'api/periods';

    constructor(protected http: HttpClient, protected dateUtils: JhiDateUtils) {}

    create(period: IPeriod): Observable<EntityResponseType> {
        return this.http.post<IPeriod>(this.resourceUrl, period, { observe: 'response' });
    }

    update(period: IPeriod): Observable<EntityResponseType> {
        return this.http.put<IPeriod>(this.resourceUrl, period, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IPeriod>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IPeriod[]>(this.resourceUrl, { params: options, observe: 'response' });
    }
    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
    findCurrentPeriod(): Observable<EntityResponseType> {
        return this.http.get<IPeriod>(`${this.resourceUrl}/findCurrentPeriod`, { observe: 'response' });
    }

    find3LastPeriods(): Observable<EntityArrayResponseType> {
        return this.http.get<IPeriod[]>(`${this.resourceUrl}/find3LastPeriods`, { observe: 'response' });
    }

    findByMonth(month: Moment): Observable<EntityResponseType> {
        let options = new HttpParams();
        options = options.append('month', month.startOf('month').format('YYYY-MM-DD'));
        return this.http.get<IPeriod>(`${this.resourceUrl}/findByMonth`, { params: options, observe: 'response' });
    }
}
