package com.racecontrol.season;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeasonRepository extends JpaRepository<Season, Long> {
  List<Season> findByLeagueIdOrderByStartDateDesc(Long leagueId);

  Optional<Season> findByIdAndLeagueId(Long seasonId, Long leagueId);

  boolean existsByLeagueIdAndStatus(Long leagueId, SeasonStatus status);
}
