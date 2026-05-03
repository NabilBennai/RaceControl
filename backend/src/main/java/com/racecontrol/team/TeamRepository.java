package com.racecontrol.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
  List<Team> findBySeasonIdOrderByNameAsc(Long seasonId);
}
