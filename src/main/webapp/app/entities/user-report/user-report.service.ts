import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IUserReport } from 'app/shared/model/user-report.model';

type EntityResponseType = HttpResponse<IUserReport>;
type EntityArrayResponseType = HttpResponse<IUserReport[]>;

@Injectable({ providedIn: 'root' })
export class UserReportService {
    public resourceUrl = SERVER_API_URL + 'api/user-reports';

    constructor(protected http: HttpClient) {}

    create(userReport: IUserReport): Observable<EntityResponseType> {
        return this.http.post<IUserReport>(this.resourceUrl, userReport, { observe: 'response' });
    }

    update(userReport: IUserReport): Observable<EntityResponseType> {
        return this.http.put<IUserReport>(this.resourceUrl, userReport, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IUserReport>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IUserReport[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
