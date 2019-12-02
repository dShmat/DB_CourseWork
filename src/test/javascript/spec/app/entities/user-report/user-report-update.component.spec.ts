/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { CollectmeTestModule } from '../../../test.module';
import { UserReportUpdateComponent } from 'app/entities/user-report/user-report-update.component';
import { UserReportService } from 'app/entities/user-report/user-report.service';
import { UserReport } from 'app/shared/model/user-report.model';

describe('Component Tests', () => {
    describe('UserReport Management Update Component', () => {
        let comp: UserReportUpdateComponent;
        let fixture: ComponentFixture<UserReportUpdateComponent>;
        let service: UserReportService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CollectmeTestModule],
                declarations: [UserReportUpdateComponent]
            })
                .overrideTemplate(UserReportUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(UserReportUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(UserReportService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new UserReport(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.userReport = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new UserReport();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.userReport = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
