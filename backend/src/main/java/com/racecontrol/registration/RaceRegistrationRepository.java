package com.racecontrol.registration;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RaceRegistrationRepository extends JpaRepository<RaceRegistration, Long> {
  boolean existsByRaceIdAndUserId(Long raceId, Long userId);

  Optional<RaceRegistration> findByRaceIdAndUserId(Long raceId, Long userId);

  List<RaceRegistration> findByRaceIdOrderByCreatedAtAsc(Long raceId);
}
