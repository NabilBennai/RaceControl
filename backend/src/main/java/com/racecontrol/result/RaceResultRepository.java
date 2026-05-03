package com.racecontrol.result;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
  Optional<RaceResult> findByRaceId(Long raceId);
}
