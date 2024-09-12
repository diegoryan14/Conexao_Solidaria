import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IEventos } from 'app/entities/eventos/eventos.model';
import { EventosService } from 'app/entities/eventos/service/eventos.service';
import { IInscricao } from '../inscricao.model';
import { InscricaoService } from '../service/inscricao.service';
import { InscricaoFormService } from './inscricao-form.service';

import { InscricaoUpdateComponent } from './inscricao-update.component';

describe('Inscricao Management Update Component', () => {
  let comp: InscricaoUpdateComponent;
  let fixture: ComponentFixture<InscricaoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inscricaoFormService: InscricaoFormService;
  let inscricaoService: InscricaoService;
  let userService: UserService;
  let eventosService: EventosService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, InscricaoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(InscricaoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InscricaoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inscricaoFormService = TestBed.inject(InscricaoFormService);
    inscricaoService = TestBed.inject(InscricaoService);
    userService = TestBed.inject(UserService);
    eventosService = TestBed.inject(EventosService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const inscricao: IInscricao = { id: 456 };
      const user: IUser = { id: 31284 };
      inscricao.user = user;

      const userCollection: IUser[] = [{ id: 18398 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inscricao });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Eventos query and add missing value', () => {
      const inscricao: IInscricao = { id: 456 };
      const evento: IEventos = { id: 5898 };
      inscricao.evento = evento;

      const eventosCollection: IEventos[] = [{ id: 8883 }];
      jest.spyOn(eventosService, 'query').mockReturnValue(of(new HttpResponse({ body: eventosCollection })));
      const additionalEventos = [evento];
      const expectedCollection: IEventos[] = [...additionalEventos, ...eventosCollection];
      jest.spyOn(eventosService, 'addEventosToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inscricao });
      comp.ngOnInit();

      expect(eventosService.query).toHaveBeenCalled();
      expect(eventosService.addEventosToCollectionIfMissing).toHaveBeenCalledWith(
        eventosCollection,
        ...additionalEventos.map(expect.objectContaining),
      );
      expect(comp.eventosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const inscricao: IInscricao = { id: 456 };
      const user: IUser = { id: 22085 };
      inscricao.user = user;
      const evento: IEventos = { id: 1939 };
      inscricao.evento = evento;

      activatedRoute.data = of({ inscricao });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.eventosSharedCollection).toContain(evento);
      expect(comp.inscricao).toEqual(inscricao);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInscricao>>();
      const inscricao = { id: 123 };
      jest.spyOn(inscricaoFormService, 'getInscricao').mockReturnValue(inscricao);
      jest.spyOn(inscricaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inscricao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inscricao }));
      saveSubject.complete();

      // THEN
      expect(inscricaoFormService.getInscricao).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inscricaoService.update).toHaveBeenCalledWith(expect.objectContaining(inscricao));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInscricao>>();
      const inscricao = { id: 123 };
      jest.spyOn(inscricaoFormService, 'getInscricao').mockReturnValue({ id: null });
      jest.spyOn(inscricaoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inscricao: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inscricao }));
      saveSubject.complete();

      // THEN
      expect(inscricaoFormService.getInscricao).toHaveBeenCalled();
      expect(inscricaoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInscricao>>();
      const inscricao = { id: 123 };
      jest.spyOn(inscricaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inscricao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inscricaoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEventos', () => {
      it('Should forward to eventosService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(eventosService, 'compareEventos');
        comp.compareEventos(entity, entity2);
        expect(eventosService.compareEventos).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
