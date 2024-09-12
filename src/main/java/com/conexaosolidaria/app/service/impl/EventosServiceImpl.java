package com.conexaosolidaria.app.service.impl;

import com.conexaosolidaria.app.domain.Eventos;
import com.conexaosolidaria.app.repository.EventosRepository;
import com.conexaosolidaria.app.service.EventosService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.conexaosolidaria.app.domain.Eventos}.
 */
@Service
@Transactional
public class EventosServiceImpl implements EventosService {

    private final Logger log = LoggerFactory.getLogger(EventosServiceImpl.class);

    private final EventosRepository eventosRepository;

    public EventosServiceImpl(EventosRepository eventosRepository) {
        this.eventosRepository = eventosRepository;
    }

    @Override
    public Eventos save(Eventos eventos) {
        log.debug("Request to save Eventos : {}", eventos);
        return eventosRepository.save(eventos);
    }

    @Override
    public Eventos update(Eventos eventos) {
        log.debug("Request to update Eventos : {}", eventos);
        return eventosRepository.save(eventos);
    }

    @Override
    public Optional<Eventos> partialUpdate(Eventos eventos) {
        log.debug("Request to partially update Eventos : {}", eventos);

        return eventosRepository
            .findById(eventos.getId())
            .map(existingEventos -> {
                if (eventos.getNome() != null) {
                    existingEventos.setNome(eventos.getNome());
                }
                if (eventos.getDataCadastro() != null) {
                    existingEventos.setDataCadastro(eventos.getDataCadastro());
                }
                if (eventos.getDataEvento() != null) {
                    existingEventos.setDataEvento(eventos.getDataEvento());
                }
                if (eventos.getHoraInicio() != null) {
                    existingEventos.setHoraInicio(eventos.getHoraInicio());
                }
                if (eventos.getHoraTermino() != null) {
                    existingEventos.setHoraTermino(eventos.getHoraTermino());
                }
                if (eventos.getObservacao() != null) {
                    existingEventos.setObservacao(eventos.getObservacao());
                }

                return existingEventos;
            })
            .map(eventosRepository::save);
    }

    public Page<Eventos> findAllWithEagerRelationships(Pageable pageable) {
        return eventosRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Eventos> findOne(Long id) {
        log.debug("Request to get Eventos : {}", id);
        return eventosRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Eventos : {}", id);
        eventosRepository.deleteById(id);
    }
}
