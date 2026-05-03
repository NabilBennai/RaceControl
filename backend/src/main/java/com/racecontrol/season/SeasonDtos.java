package com.racecontrol.season;

import com.racecontrol.league.GamePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class SeasonDtos {
  public record SeasonRequest(
    @NotBlank String name,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
  }

  public record SeasonResponse(
    Long id,
    Long leagueId,
    GamePlatform gamePlatform,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    SeasonStatus status
  ) {
  }
}
