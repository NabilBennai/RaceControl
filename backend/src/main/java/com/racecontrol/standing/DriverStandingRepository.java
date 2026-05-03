package com.racecontrol.standing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverStandingRepository extends JpaRepository<DriverStanding, Long> {
  List<DriverStanding> findBySeasonIdOrderByPointsDescWinsDescPodiumsDesc(Long seasonId);

  void deleteBySeasonId(Long seasonId);
}
