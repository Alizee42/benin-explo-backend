package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {
    @Query("""
            select a
            from Actualite a
            order by a.datePublication desc nulls last, a.idActualite desc
            """)
    List<Actualite> findAllOrderedForAdmin();

    // Une actualite programmee (datePublication dans le futur) reste invisible cote public tant
    // que cette date n'est pas atteinte, meme si publiee=true. datePublication=null est traite
    // comme "publication immediate" (comportement historique conserve).
    @Query("""
            select a
            from Actualite a
            where a.publiee = true and (a.datePublication is null or a.datePublication <= :now)
            order by a.aLaUne desc, a.datePublication desc nulls last, a.idActualite desc
            """)
    List<Actualite> findAllPublishedOrdered(@Param("now") LocalDateTime now);

    @Query("""
            select a
            from Actualite a
            where a.idActualite = :id and a.publiee = true
              and (a.datePublication is null or a.datePublication <= :now)
            """)
    Optional<Actualite> findPublishedById(@Param("id") Long id, @Param("now") LocalDateTime now);
}
