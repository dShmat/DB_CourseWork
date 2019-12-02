import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { IManagerProject } from 'app/shared/model/manager-project.model';
import { Observable } from 'rxjs';
import { createRequestOption } from 'app/shared';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { IUser } from 'app/core';
import { IProject } from 'app/shared/model/project.model';

type EntityResponseType = HttpResponse<IManagerProject>;
type EntityArrayResponseType = HttpResponse<IManagerProject[]>;
type EntityProjectArrayResponseType = HttpResponse<IProject[]>;

@Injectable({
    providedIn: 'root'
})
export class ManagerProjectService {
    public resourceUrl = SERVER_API_URL + 'api/manager-projects';

    constructor(protected http: HttpClient) {}

    create(managerProject: IManagerProject): Observable<EntityResponseType> {
        return this.http.post<IManagerProject>(this.resourceUrl, managerProject, { observe: 'response' });
    }

    update(managerProject: IManagerProject): Observable<EntityResponseType> {
        return this.http.put<IManagerProject>(this.resourceUrl, managerProject, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IManagerProject>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IManagerProject[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
    deleteByProjectAndPeriod(projectId: number, periodId: number): Observable<HttpResponse<any>> {
        let options = new HttpParams();
        options = options.append('projectId', projectId.toString());
        options = options.append('periodId', periodId.toString());
        return this.http.delete<any>(`${this.resourceUrl}/deleteByProjectAndPeriod`, { params: options, observe: 'response' });
    }
    createManagerProjectAndProject(managerProject: IManagerProject): Observable<EntityResponseType> {
        return this.http.post<IManagerProject>(`${this.resourceUrl}/createManagerProjectAndProject`, managerProject, {
            observe: 'response'
        });
    }
}
