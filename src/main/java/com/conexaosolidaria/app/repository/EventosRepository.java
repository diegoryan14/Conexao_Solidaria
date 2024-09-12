package com.conexaosolidaria.app.repository;

import com.conexaosolidaria.app.domain.Eventos;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Eventos entity.
 */
@Repository
public interface EventosRepository extends JpaRepository<Eventos, Long>, JpaSpecificationExecutor<Eventos> {
    @Query("select eventos from Eventos eventos where eventos.user.login = ?#{authentication.name}")
    List<Eventos> findByUserIsCurrentUser();

    default Optional<Eventos> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Eventos> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Eventos> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select eventos from Eventos eventos left join fetch eventos.user",
        countQuery = "select count(eventos) from Eventos eventos"
    )
    Page<Eventos> findAllWithToOneRelationships(Pageable pageable);

    @Query("select eventos from Eventos eventos left join fetch eventos.user")
    List<Eventos> findAllWithToOneRelationships();

    @Query("select eventos from Eventos eventos left join fetch eventos.user where eventos.id =:id")
    Optional<Eventos> findOneWithToOneRelationships(@Param("id") Long id);
}
