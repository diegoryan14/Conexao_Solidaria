import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAvaliacao } from '../avaliacao.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../avaliacao.test-samples';

import { AvaliacaoService } from './avaliacao.service';

const requireRestSample: IAvaliacao = {
  ...sampleWithRequiredData,
};

describe('Avaliacao Service', () => {
  let service: AvaliacaoService;
  let httpMock: HttpTestingController;
  let expectedResult: IAvaliacao | IAvaliacao[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AvaliacaoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Avaliacao', () => {
      const avaliacao = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(avaliacao).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Avaliacao', () => {
      const avaliacao = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(avaliacao).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Avaliacao', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Avaliacao', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Avaliacao', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAvaliacaoToCollectionIfMissing', () => {
      it('should add a Avaliacao to an empty array', () => {
        const avaliacao: IAvaliacao = sampleWithRequiredData;
        expectedResult = service.addAvaliacaoToCollectionIfMissing([], avaliacao);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(avaliacao);
      });

      it('should not add a Avaliacao to an array that contains it', () => {
        const avaliacao: IAvaliacao = sampleWithRequiredData;
        const avaliacaoCollection: IAvaliacao[] = [
          {
            ...avaliacao,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAvaliacaoToCollectionIfMissing(avaliacaoCollection, avaliacao);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Avaliacao to an array that doesn't contain it", () => {
        const avaliacao: IAvaliacao = sampleWithRequiredData;
        const avaliacaoCollection: IAvaliacao[] = [sampleWithPartialData];
        expectedResult = service.addAvaliacaoToCollectionIfMissing(avaliacaoCollection, avaliacao);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(avaliacao);
      });

      it('should add only unique Avaliacao to an array', () => {
        const avaliacaoArray: IAvaliacao[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const avaliacaoCollection: IAvaliacao[] = [sampleWithRequiredData];
        expectedResult = service.addAvaliacaoToCollectionIfMissing(avaliacaoCollection, ...avaliacaoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const avaliacao: IAvaliacao = sampleWithRequiredData;
        const avaliacao2: IAvaliacao = sampleWithPartialData;
        expectedResult = service.addAvaliacaoToCollectionIfMissing([], avaliacao, avaliacao2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(avaliacao);
        expect(expectedResult).toContain(avaliacao2);
      });

      it('should accept null and undefined values', () => {
        const avaliacao: IAvaliacao = sampleWithRequiredData;
        expectedResult = service.addAvaliacaoToCollectionIfMissing([], null, avaliacao, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(avaliacao);
      });

      it('should return initial array if no Avaliacao is added', () => {
        const avaliacaoCollection: IAvaliacao[] = [sampleWithRequiredData];
        expectedResult = service.addAvaliacaoToCollectionIfMissing(avaliacaoCollection, undefined, null);
        expect(expectedResult).toEqual(avaliacaoCollection);
      });
    });

    describe('compareAvaliacao', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAvaliacao(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAvaliacao(entity1, entity2);
        const compareResult2 = service.compareAvaliacao(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAvaliacao(entity1, entity2);
        const compareResult2 = service.compareAvaliacao(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAvaliacao(entity1, entity2);
        const compareResult2 = service.compareAvaliacao(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
