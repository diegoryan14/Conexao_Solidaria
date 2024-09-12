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
import { IAvaliacao } from '../avaliacao.model';
import { AvaliacaoService } from '../service/avaliacao.service';
import { AvaliacaoFormService } from './avaliacao-form.service';

import { AvaliacaoUpdateComponent } from './avaliacao-update.component';

describe('Avaliacao Management Update Component', () => {
  let comp: AvaliacaoUpdateComponent;
  let fixture: ComponentFixture<AvaliacaoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let avaliacaoFormService: AvaliacaoFormService;
  let avaliacaoService: AvaliacaoService;
  let userService: UserService;
  let eventosService: EventosService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AvaliacaoUpdateComponent],
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
      .overrideTemplate(AvaliacaoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AvaliacaoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    avaliacaoFormService = TestBed.inject(AvaliacaoFormService);
    avaliacaoService = TestBed.inject(AvaliacaoService);
    userService = TestBed.inject(UserService);
    eventosService = TestBed.inject(EventosService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const avaliacao: IAvaliacao = { id: 456 };
      const user: IUser = { id: 28153 };
      avaliacao.user = user;

      const userCollection: IUser[] = [{ id: 5225 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ avaliacao });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Eventos query and add missing value', () => {
      const avaliacao: IAvaliacao = { id: 456 };
      const evento: IEventos = { id: 17345 };
      avaliacao.evento = evento;

      const eventosCollection: IEventos[] = [{ id: 22024 }];
      jest.spyOn(eventosService, 'query').mockReturnValue(of(new HttpResponse({ body: eventosCollection })));
      const additionalEventos = [evento];
      const expectedCollection: IEventos[] = [...additionalEventos, ...eventosCollection];
      jest.spyOn(eventosService, 'addEventosToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ avaliacao });
      comp.ngOnInit();

      expect(eventosService.query).toHaveBeenCalled();
      expect(eventosService.addEventosToCollectionIfMissing).toHaveBeenCalledWith(
        eventosCollection,
        ...additionalEventos.map(expect.objectContaining),
      );
      expect(comp.eventosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const avaliacao: IAvaliacao = { id: 456 };
      const user: IUser = { id: 6187 };
      avaliacao.user = user;
      const evento: IEventos = { id: 13976 };
      avaliacao.evento = evento;

      activatedRoute.data = of({ avaliacao });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.eventosSharedCollection).toContain(evento);
      expect(comp.avaliacao).toEqual(avaliacao);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAvaliacao>>();
      const avaliacao = { id: 123 };
      jest.spyOn(avaliacaoFormService, 'getAvaliacao').mockReturnValue(avaliacao);
      jest.spyOn(avaliacaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ avaliacao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: avaliacao }));
      saveSubject.complete();

      // THEN
      expect(avaliacaoFormService.getAvaliacao).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(avaliacaoService.update).toHaveBeenCalledWith(expect.objectContaining(avaliacao));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAvaliacao>>();
      const avaliacao = { id: 123 };
      jest.spyOn(avaliacaoFormService, 'getAvaliacao').mockReturnValue({ id: null });
      jest.spyOn(avaliacaoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ avaliacao: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: avaliacao }));
      saveSubject.complete();

      // THEN
      expect(avaliacaoFormService.getAvaliacao).toHaveBeenCalled();
      expect(avaliacaoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAvaliacao>>();
      const avaliacao = { id: 123 };
      jest.spyOn(avaliacaoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ avaliacao });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(avaliacaoService.update).toHaveBeenCalled();
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
