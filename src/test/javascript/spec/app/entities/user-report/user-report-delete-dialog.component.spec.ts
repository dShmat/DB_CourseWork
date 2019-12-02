/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { CollectmeTestModule } from '../../../test.module';
import { UserReportDeleteDialogComponent } from 'app/entities/user-report/user-report-delete-dialog.component';
import { UserReportService } from 'app/entities/user-report/user-report.service';

describe('Component Tests', () => {
    describe('UserReport Management Delete Component', () => {
        let comp: UserReportDeleteDialogComponent;
        let fixture: ComponentFixture<UserReportDeleteDialogComponent>;
        let service: UserReportService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CollectmeTestModule],
                declarations: [UserReportDeleteDialogComponent]
            })
                .overrideTemplate(UserReportDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(UserReportDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(UserReportService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
