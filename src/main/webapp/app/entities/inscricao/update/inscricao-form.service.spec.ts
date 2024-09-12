import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../inscricao.test-samples';

import { InscricaoFormService } from './inscricao-form.service';

describe('Inscricao Form Service', () => {
  let service: InscricaoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InscricaoFormService);
  });

  describe('Service methods', () => {
    describe('createInscricaoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createInscricaoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            data: expect.any(Object),
            user: expect.any(Object),
            evento: expect.any(Object),
          }),
        );
      });

      it('passing IInscricao should create a new form with FormGroup', () => {
        const formGroup = service.createInscricaoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            data: expect.any(Object),
            user: expect.any(Object),
            evento: expect.any(Object),
          }),
        );
      });
    });

    describe('getInscricao', () => {
      it('should return NewInscricao for default Inscricao initial value', () => {
        const formGroup = service.createInscricaoFormGroup(sampleWithNewData);

        const inscricao = service.getInscricao(formGroup) as any;

        expect(inscricao).toMatchObject(sampleWithNewData);
      });

      it('should return NewInscricao for empty Inscricao initial value', () => {
        const formGroup = service.createInscricaoFormGroup();

        const inscricao = service.getInscricao(formGroup) as any;

        expect(inscricao).toMatchObject({});
      });

      it('should return IInscricao', () => {
        const formGroup = service.createInscricaoFormGroup(sampleWithRequiredData);

        const inscricao = service.getInscricao(formGroup) as any;

        expect(inscricao).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IInscricao should not enable id FormControl', () => {
        const formGroup = service.createInscricaoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewInscricao should disable id FormControl', () => {
        const formGroup = service.createInscricaoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
