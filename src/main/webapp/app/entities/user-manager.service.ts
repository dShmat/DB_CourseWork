import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { IUserManager, UserManager } from 'app/shared/model/user-manager.model';
import { Observable } from 'rxjs';
import { createRequestOption } from 'app/shared';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { IUser } from 'app/core';
import { IPeriod } from 'app/shared/model/period.model';

type EntityResponseType = HttpResponse<IUserManager>;
type EntityArrayResponseType = HttpResponse<IUserManager[]>;
type EntityUserArrayResponseType = HttpResponse<IUser[]>;

@Injectable({
    providedIn: 'root'
})
export class UserManagerService {
    public resourceUrl = SERVER_API_URL + 'api/user-managers';

    constructor(protected http: HttpClient) {}

    create(userManager: IUserManager): Observable<EntityResponseType> {
        return this.http.post<IUserManager>(this.resourceUrl, userManager, { observe: 'response' });
    }

    update(userManager: IUserManager): Observable<EntityResponseType> {
        return this.http.put<IUserManager>(this.resourceUrl, userManager, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IUserManager>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IUserManager[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
    getManagerUsersByPeriod(periodId: number): Observable<EntityUserArrayResponseType> {
        let options = new HttpParams();
        options = options.append('periodId', periodId.toString());
        return this.http.get<IUser[]>(`${this.resourceUrl}/getManagerUsersByPeriod`, { params: options, observe: 'response' });
    }
    createUserManagerFromUser(userManager: IUserManager): Observable<EntityResponseType> {
        return this.http.post<IUserManager>(`${this.resourceUrl}/createUserManagerFromUser`, userManager, { observe: 'response' });
    }

    deleteByUserAndPeriod(userId: number, periodId: number) {
        let options = new HttpParams();
        options = options.append('periodId', periodId.toString());
        options = options.append('userId', userId.toString());
        return this.http.delete<any>(`${this.resourceUrl}/deleteByUserAndPeriod`, { params: options, observe: 'response' });
    }
}
