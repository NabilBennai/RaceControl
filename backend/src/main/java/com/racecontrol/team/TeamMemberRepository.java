package com.racecontrol.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
  Optional<TeamMember> findByIdAndTeamId(Long id, Long teamId);

  boolean existsByTeamIdAndUserId(Long teamId, Long userId);

  boolean existsByUserIdAndTeam_Season_Id(Long userId, Long seasonId);
}
