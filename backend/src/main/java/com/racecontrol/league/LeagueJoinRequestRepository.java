package com.racecontrol.league;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeagueJoinRequestRepository extends JpaRepository<LeagueJoinRequest, Long> {
  Optional<LeagueJoinRequest> findByLeagueIdAndUserId(Long leagueId, Long userId);
  Optional<LeagueJoinRequest> findByIdAndLeagueId(Long id, Long leagueId);

  List<LeagueJoinRequest> findByLeagueIdOrderByCreatedAtDesc(Long leagueId);
}
