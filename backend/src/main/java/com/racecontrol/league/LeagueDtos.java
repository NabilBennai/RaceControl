package com.racecontrol.league;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
    LeagueRole myRole
  ) {
  }
}
