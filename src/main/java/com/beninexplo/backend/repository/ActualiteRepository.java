package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Actualite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActualiteRepository extends JpaRepository<Actualite, Long> {
    @Query("""
            select a
            from Actualite a
            order by a.datePublication desc nulls last, a.idActualite desc
            """)
    List<Actualite> findAllOrderedForAdmin();

    @Query("""
            select a
            from Actualite a
            where a.publiee = true
            order by a.aLaUne desc, a.datePublication desc nulls last, a.idActualite desc
            """)
    List<Actualite> findAllPublishedOrdered();

    @Query("""
            select a
            from Actualite a
            where a.idActualite = :id and a.publiee = true
            """)
    Optional<Actualite> findPublishedById(Long id);
}
