package com.beninexplo.backend.repository;

import com.beninexplo.backend.entity.TombolaParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TombolaParticipantRepository extends JpaRepository<TombolaParticipant, Long> {

    Optional<TombolaParticipant> findByEmail(String email);

    Optional<TombolaParticipant> findByCodeUnique(String codeUnique);
}
