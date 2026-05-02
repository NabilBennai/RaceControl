package com.racecontrol.league;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeagueRepository extends JpaRepository<League, Long> {
  List<League> findDistinctByMembersUserIdOrderByCreatedAtDesc(Long userId);
}
