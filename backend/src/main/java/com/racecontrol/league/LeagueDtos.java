package com.racecontrol.league;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class LeagueDtos {
  public record LeagueRequest(
    @NotBlank @Size(min = 3, max = 120) String name,
    @NotBlank @Size(min = 10, max = 2000) String description,
    @NotNull GamePlatform gamePlatform,
    @NotNull LeagueVisibility visibility
  ) {
  }

  public record LeagueResponse(
    Long id,
    String name,
    String description,
    GamePlatform gamePlatform,
    LeagueVisibility visibility,
    LeagueRole myRole,
    String invitationCode
  ) {
  }

  public record JoinByCodeRequest(@NotBlank String invitationCode) {
  }

  public record LeagueJoinResponse(Long leagueId, MembershipStatus status, String message) {
  }

  public record LeagueMemberResponse(
    Long id,
    Long userId,
    String username,
    String email,
    LeagueRole role,
    MembershipStatus status,
    Instant requestedAt
  ) {
  }
}
