import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IInscricao } from '../inscricao.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../inscricao.test-samples';

import { InscricaoService, RestInscricao } from './inscricao.service';

const requireRestSample: RestInscricao = {
  ...sampleWithRequiredData,
  data: sampleWithRequiredData.data?.toJSON(),
};

describe('Inscricao Service', () => {
  let service: InscricaoService;
  let httpMock: HttpTestingController;
  let expectedResult: IInscricao | IInscricao[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(InscricaoService);
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

    it('should create a Inscricao', () => {
      const inscricao = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(inscricao).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Inscricao', () => {
      const inscricao = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(inscricao).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Inscricao', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Inscricao', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Inscricao', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addInscricaoToCollectionIfMissing', () => {
      it('should add a Inscricao to an empty array', () => {
        const inscricao: IInscricao = sampleWithRequiredData;
        expectedResult = service.addInscricaoToCollectionIfMissing([], inscricao);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(inscricao);
      });

      it('should not add a Inscricao to an array that contains it', () => {
        const inscricao: IInscricao = sampleWithRequiredData;
        const inscricaoCollection: IInscricao[] = [
          {
            ...inscricao,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addInscricaoToCollectionIfMissing(inscricaoCollection, inscricao);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Inscricao to an array that doesn't contain it", () => {
        const inscricao: IInscricao = sampleWithRequiredData;
        const inscricaoCollection: IInscricao[] = [sampleWithPartialData];
        expectedResult = service.addInscricaoToCollectionIfMissing(inscricaoCollection, inscricao);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(inscricao);
      });

      it('should add only unique Inscricao to an array', () => {
        const inscricaoArray: IInscricao[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const inscricaoCollection: IInscricao[] = [sampleWithRequiredData];
        expectedResult = service.addInscricaoToCollectionIfMissing(inscricaoCollection, ...inscricaoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const inscricao: IInscricao = sampleWithRequiredData;
        const inscricao2: IInscricao = sampleWithPartialData;
        expectedResult = service.addInscricaoToCollectionIfMissing([], inscricao, inscricao2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(inscricao);
        expect(expectedResult).toContain(inscricao2);
      });

      it('should accept null and undefined values', () => {
        const inscricao: IInscricao = sampleWithRequiredData;
        expectedResult = service.addInscricaoToCollectionIfMissing([], null, inscricao, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(inscricao);
      });

      it('should return initial array if no Inscricao is added', () => {
        const inscricaoCollection: IInscricao[] = [sampleWithRequiredData];
        expectedResult = service.addInscricaoToCollectionIfMissing(inscricaoCollection, undefined, null);
        expect(expectedResult).toEqual(inscricaoCollection);
      });
    });

    describe('compareInscricao', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareInscricao(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareInscricao(entity1, entity2);
        const compareResult2 = service.compareInscricao(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareInscricao(entity1, entity2);
        const compareResult2 = service.compareInscricao(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareInscricao(entity1, entity2);
        const compareResult2 = service.compareInscricao(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
