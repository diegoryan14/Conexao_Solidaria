package com.conexaosolidaria.app.service.impl;

import com.conexaosolidaria.app.domain.Inscricao;
import com.conexaosolidaria.app.repository.InscricaoRepository;
import com.conexaosolidaria.app.service.InscricaoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.conexaosolidaria.app.domain.Inscricao}.
 */
@Service
@Transactional
public class InscricaoServiceImpl implements InscricaoService {

    private final Logger log = LoggerFactory.getLogger(InscricaoServiceImpl.class);

    private final InscricaoRepository inscricaoRepository;

    public InscricaoServiceImpl(InscricaoRepository inscricaoRepository) {
        this.inscricaoRepository = inscricaoRepository;
    }

    @Override
    public Inscricao save(Inscricao inscricao) {
        log.debug("Request to save Inscricao : {}", inscricao);
        return inscricaoRepository.save(inscricao);
    }

    @Override
    public Inscricao update(Inscricao inscricao) {
        log.debug("Request to update Inscricao : {}", inscricao);
        return inscricaoRepository.save(inscricao);
    }

    @Override
    public Optional<Inscricao> partialUpdate(Inscricao inscricao) {
        log.debug("Request to partially update Inscricao : {}", inscricao);

        return inscricaoRepository
            .findById(inscricao.getId())
            .map(existingInscricao -> {
                if (inscricao.getData() != null) {
                    existingInscricao.setData(inscricao.getData());
                }

                return existingInscricao;
            })
            .map(inscricaoRepository::save);
    }

    public Page<Inscricao> findAllWithEagerRelationships(Pageable pageable) {
        return inscricaoRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inscricao> findOne(Long id) {
        log.debug("Request to get Inscricao : {}", id);
        return inscricaoRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Inscricao : {}", id);
        inscricaoRepository.deleteById(id);
    }
}
