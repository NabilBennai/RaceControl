package com.racecontrol.league;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeagueInvitationCodeRepository extends JpaRepository<LeagueInvitationCode, Long> {
  Optional<LeagueInvitationCode> findByCodeAndActiveTrue(String code);

  Optional<LeagueInvitationCode> findTopByLeagueIdAndActiveTrueOrderByCreatedAtDesc(Long leagueId);
}
