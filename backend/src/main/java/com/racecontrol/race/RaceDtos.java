package com.racecontrol.race;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class RaceDtos {
  public record RaceRequest(
    @NotBlank String track,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate raceDate,
    @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime raceTime,
    @NotBlank String format,
    @Positive Integer laps,
    @Positive Integer durationMinutes,
    @NotBlank String weather,
    @NotBlank String carCategory
  ) {
  }

  public record RaceStatusRequest(
    @NotNull RaceStatus status
  ) {
  }

  public record RaceResponse(
    Long id,
    Long seasonId,
    String track,
    LocalDate raceDate,
    LocalTime raceTime,
    String format,
    Integer laps,
    Integer durationMinutes,
    String weather,
    String carCategory,
    RaceStatus status
  ) {
  }
}
