package com.racecontrol.league;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeagueMemberRepository extends JpaRepository<LeagueMember, Long> {
  Optional<LeagueMember> findByLeagueIdAndUserId(Long leagueId, Long userId);

  List<LeagueMember> findByLeagueId(Long leagueId);
}
