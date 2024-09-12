package com.conexaosolidaria.app.service.impl;

import com.conexaosolidaria.app.domain.Avaliacao;
import com.conexaosolidaria.app.repository.AvaliacaoRepository;
import com.conexaosolidaria.app.service.AvaliacaoService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.conexaosolidaria.app.domain.Avaliacao}.
 */
@Service
@Transactional
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final Logger log = LoggerFactory.getLogger(AvaliacaoServiceImpl.class);

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoServiceImpl(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        log.debug("Request to save Avaliacao : {}", avaliacao);
        return avaliacaoRepository.save(avaliacao);
    }

    @Override
    public Avaliacao update(Avaliacao avaliacao) {
        log.debug("Request to update Avaliacao : {}", avaliacao);
        return avaliacaoRepository.save(avaliacao);
    }

    @Override
    public Optional<Avaliacao> partialUpdate(Avaliacao avaliacao) {
        log.debug("Request to partially update Avaliacao : {}", avaliacao);

        return avaliacaoRepository
            .findById(avaliacao.getId())
            .map(existingAvaliacao -> {
                if (avaliacao.getEstrelas() != null) {
                    existingAvaliacao.setEstrelas(avaliacao.getEstrelas());
                }
                if (avaliacao.getObservacao() != null) {
                    existingAvaliacao.setObservacao(avaliacao.getObservacao());
                }

                return existingAvaliacao;
            })
            .map(avaliacaoRepository::save);
    }

    public Page<Avaliacao> findAllWithEagerRelationships(Pageable pageable) {
        return avaliacaoRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> findOne(Long id) {
        log.debug("Request to get Avaliacao : {}", id);
        return avaliacaoRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Avaliacao : {}", id);
        avaliacaoRepository.deleteById(id);
    }
}
