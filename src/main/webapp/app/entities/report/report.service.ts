import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IReport } from 'app/shared/model/report.model';
import { IPeriod } from 'app/shared/model/period.model';
import { IUser } from 'app/core';

type EntityResponseType = HttpResponse<IReport>;
type EntityArrayResponseType = HttpResponse<IReport[]>;

@Injectable({ providedIn: 'root' })
export class ReportService {
    public resourceUrl = SERVER_API_URL + 'api/reports';

    constructor(protected http: HttpClient) {}

    create(report: IReport): Observable<EntityResponseType> {
        return this.http.post<IReport>(this.resourceUrl, report, { observe: 'response' });
    }

    update(report: IReport): Observable<EntityResponseType> {
        return this.http.put<IReport>(this.resourceUrl, report, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IReport>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IReport[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    getReportsByUsersAndPeriod(userIds: number[], periodId: number) {
        let options = new HttpParams();
        options = options.append('periodId', periodId.toString());
        options = options.append('userIds', userIds.toString());
        return this.http.get<IReport[]>(`${this.resourceUrl}/getReportsByUsersAndPeriod`, { params: options, observe: 'response' });
    }

    getReportsByUserPeriodId(periodId: number) {
        let params = new HttpParams();
        params = params.append('periodId', periodId.toString());
        return this.http.get<IReport[]>(`${this.resourceUrl}/getReportsByUserPeriodId`, { params, observe: 'response' });
    }
    createAutomaticallyForNextMonth(report: IReport): Observable<EntityResponseType> {
        return this.http.post<IReport>(`${this.resourceUrl}/createAutomaticallyForNextMonth`, report, { observe: 'response' });
    }
    sendReports(period: IPeriod, emails: string[], subject: string): Observable<EntityResponseType> {
        subject = encodeURIComponent(subject);
        return this.http.post<IReport>(`${this.resourceUrl}/sendReports/${period.id}/${subject}`, emails, { observe: 'response' });
    }
}
