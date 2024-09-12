import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { InscricaoDetailComponent } from './inscricao-detail.component';

describe('Inscricao Management Detail Component', () => {
  let comp: InscricaoDetailComponent;
  let fixture: ComponentFixture<InscricaoDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InscricaoDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: InscricaoDetailComponent,
              resolve: { inscricao: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(InscricaoDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InscricaoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load inscricao on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', InscricaoDetailComponent);

      // THEN
      expect(instance.inscricao()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
