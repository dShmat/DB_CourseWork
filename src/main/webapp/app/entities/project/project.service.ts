import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IProject } from 'app/shared/model/project.model';

type EntityResponseType = HttpResponse<IProject>;
type EntityArrayResponseType = HttpResponse<IProject[]>;

@Injectable({ providedIn: 'root' })
export class ProjectService {
    public resourceUrl = SERVER_API_URL + 'api/projects';

    constructor(protected http: HttpClient) {}

    create(project: IProject): Observable<EntityResponseType> {
        return this.http.post<IProject>(this.resourceUrl, project, { observe: 'response' });
    }

    update(project: IProject): Observable<EntityResponseType> {
        return this.http.put<IProject>(this.resourceUrl, project, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IProject>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IProject[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    getManagerProjectsByPeriod(periodId: number): Observable<EntityArrayResponseType> {
        let options = new HttpParams();
        options = options.append('periodId', periodId.toString());
        return this.http.get<IProject[]>(`${this.resourceUrl}/getManagerProjectsByPeriod`, {
            params: options,
            observe: 'response'
        });
    }

    createProjectAndManagerProject(project: IProject): Observable<EntityResponseType> {
        return this.http.post<IProject>(`${this.resourceUrl}/createProjectAndManagerProject`, project, { observe: 'response' });
    }

    getProjectsByIds(projectIds: number[]): Observable<EntityArrayResponseType> {
        let options = new HttpParams();
        options = options.append('projectIds', projectIds.toString());
        return this.http.get<IProject[]>(`${this.resourceUrl}/getProjectsByIds`, { params: options, observe: 'response' });
    }
}
