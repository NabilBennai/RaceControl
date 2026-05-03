package com.racecontrol.standing;

import java.util.List;

public class StandingDtos {
  public record DriverStandingResponse(
    Long userId,
    String username,
    String email,
    Integer points,
    Integer wins,
    Integer podiums,
    Integer poles,
    Integer fastestLaps
  ) {
  }

  public record TeamStandingResponse(
    Long teamId,
    String teamName,
    String color,
    Integer points,
    Integer wins,
    Integer podiums,
    Integer poles,
    Integer fastestLaps
  ) {
  }

  public record StandingsResponse(
    Long seasonId,
    List<DriverStandingResponse> drivers,
    List<TeamStandingResponse> teams
  ) {
  }
}
