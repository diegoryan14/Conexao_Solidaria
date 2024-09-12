package com.conexaosolidaria.app.repository;

import com.conexaosolidaria.app.domain.Avaliacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Avaliacao entity.
 */
@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>, JpaSpecificationExecutor<Avaliacao> {
    @Query("select avaliacao from Avaliacao avaliacao where avaliacao.user.login = ?#{authentication.name}")
    List<Avaliacao> findByUserIsCurrentUser();

    default Optional<Avaliacao> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Avaliacao> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Avaliacao> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select avaliacao from Avaliacao avaliacao left join fetch avaliacao.user left join fetch avaliacao.evento",
        countQuery = "select count(avaliacao) from Avaliacao avaliacao"
    )
    Page<Avaliacao> findAllWithToOneRelationships(Pageable pageable);

    @Query("select avaliacao from Avaliacao avaliacao left join fetch avaliacao.user left join fetch avaliacao.evento")
    List<Avaliacao> findAllWithToOneRelationships();

    @Query(
        "select avaliacao from Avaliacao avaliacao left join fetch avaliacao.user left join fetch avaliacao.evento where avaliacao.id =:id"
    )
    Optional<Avaliacao> findOneWithToOneRelationships(@Param("id") Long id);
}
