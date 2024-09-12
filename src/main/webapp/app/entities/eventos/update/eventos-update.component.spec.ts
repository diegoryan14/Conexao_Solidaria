import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { EventosService } from '../service/eventos.service';
import { IEventos } from '../eventos.model';
import { EventosFormService } from './eventos-form.service';

import { EventosUpdateComponent } from './eventos-update.component';

describe('Eventos Management Update Component', () => {
  let comp: EventosUpdateComponent;
  let fixture: ComponentFixture<EventosUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventosFormService: EventosFormService;
  let eventosService: EventosService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, EventosUpdateComponent],
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
      .overrideTemplate(EventosUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventosUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventosFormService = TestBed.inject(EventosFormService);
    eventosService = TestBed.inject(EventosService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const eventos: IEventos = { id: 456 };
      const user: IUser = { id: 7563 };
      eventos.user = user;

      const userCollection: IUser[] = [{ id: 20689 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const eventos: IEventos = { id: 456 };
      const user: IUser = { id: 25034 };
      eventos.user = user;

      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.eventos).toEqual(eventos);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEventos>>();
      const eventos = { id: 123 };
      jest.spyOn(eventosFormService, 'getEventos').mockReturnValue(eventos);
      jest.spyOn(eventosService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eventos }));
      saveSubject.complete();

      // THEN
      expect(eventosFormService.getEventos).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventosService.update).toHaveBeenCalledWith(expect.objectContaining(eventos));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEventos>>();
      const eventos = { id: 123 };
      jest.spyOn(eventosFormService, 'getEventos').mockReturnValue({ id: null });
      jest.spyOn(eventosService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventos: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: eventos }));
      saveSubject.complete();

      // THEN
      expect(eventosFormService.getEventos).toHaveBeenCalled();
      expect(eventosService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEventos>>();
      const eventos = { id: 123 };
      jest.spyOn(eventosService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ eventos });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventosService.update).toHaveBeenCalled();
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
  });
});
