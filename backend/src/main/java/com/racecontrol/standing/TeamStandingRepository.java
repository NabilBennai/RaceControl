package com.racecontrol.standing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamStandingRepository extends JpaRepository<TeamStanding, Long> {
  List<TeamStanding> findBySeasonIdOrderByPointsDescWinsDescPodiumsDesc(Long seasonId);

  void deleteBySeasonId(Long seasonId);
}
