import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../avaliacao.test-samples';

import { AvaliacaoFormService } from './avaliacao-form.service';

describe('Avaliacao Form Service', () => {
  let service: AvaliacaoFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AvaliacaoFormService);
  });

  describe('Service methods', () => {
    describe('createAvaliacaoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAvaliacaoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            estrelas: expect.any(Object),
            observacao: expect.any(Object),
            user: expect.any(Object),
            evento: expect.any(Object),
          }),
        );
      });

      it('passing IAvaliacao should create a new form with FormGroup', () => {
        const formGroup = service.createAvaliacaoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            estrelas: expect.any(Object),
            observacao: expect.any(Object),
            user: expect.any(Object),
            evento: expect.any(Object),
          }),
        );
      });
    });

    describe('getAvaliacao', () => {
      it('should return NewAvaliacao for default Avaliacao initial value', () => {
        const formGroup = service.createAvaliacaoFormGroup(sampleWithNewData);

        const avaliacao = service.getAvaliacao(formGroup) as any;

        expect(avaliacao).toMatchObject(sampleWithNewData);
      });

      it('should return NewAvaliacao for empty Avaliacao initial value', () => {
        const formGroup = service.createAvaliacaoFormGroup();

        const avaliacao = service.getAvaliacao(formGroup) as any;

        expect(avaliacao).toMatchObject({});
      });

      it('should return IAvaliacao', () => {
        const formGroup = service.createAvaliacaoFormGroup(sampleWithRequiredData);

        const avaliacao = service.getAvaliacao(formGroup) as any;

        expect(avaliacao).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAvaliacao should not enable id FormControl', () => {
        const formGroup = service.createAvaliacaoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAvaliacao should disable id FormControl', () => {
        const formGroup = service.createAvaliacaoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
