package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"circuit"})
    List<Reservation> findAll();
}
