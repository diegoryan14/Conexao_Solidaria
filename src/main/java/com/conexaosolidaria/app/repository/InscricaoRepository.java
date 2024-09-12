package com.conexaosolidaria.app.repository;

import com.conexaosolidaria.app.domain.Inscricao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Inscricao entity.
 */
@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long>, JpaSpecificationExecutor<Inscricao> {
    @Query("select inscricao from Inscricao inscricao where inscricao.user.login = ?#{authentication.name}")
    List<Inscricao> findByUserIsCurrentUser();

    default Optional<Inscricao> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Inscricao> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Inscricao> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select inscricao from Inscricao inscricao left join fetch inscricao.user left join fetch inscricao.evento",
        countQuery = "select count(inscricao) from Inscricao inscricao"
    )
    Page<Inscricao> findAllWithToOneRelationships(Pageable pageable);

    @Query("select inscricao from Inscricao inscricao left join fetch inscricao.user left join fetch inscricao.evento")
    List<Inscricao> findAllWithToOneRelationships();

    @Query(
        "select inscricao from Inscricao inscricao left join fetch inscricao.user left join fetch inscricao.evento where inscricao.id =:id"
    )
    Optional<Inscricao> findOneWithToOneRelationships(@Param("id") Long id);
}
