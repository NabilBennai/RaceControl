package com.racecontrol.race;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RaceRepository extends JpaRepository<Race, Long> {
  List<Race> findBySeasonIdOrderByRaceDateAscRaceTimeAsc(Long seasonId);
}
