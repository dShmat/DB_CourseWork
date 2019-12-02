/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CollectmeTestModule } from '../../../test.module';
import { UserReportDetailComponent } from 'app/entities/user-report/user-report-detail.component';
import { UserReport } from 'app/shared/model/user-report.model';

describe('Component Tests', () => {
    describe('UserReport Management Detail Component', () => {
        let comp: UserReportDetailComponent;
        let fixture: ComponentFixture<UserReportDetailComponent>;
        const route = ({ data: of({ userReport: new UserReport(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CollectmeTestModule],
                declarations: [UserReportDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(UserReportDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(UserReportDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.userReport).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
