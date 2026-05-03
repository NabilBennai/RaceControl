package com.racecontrol.result;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class ResultDtos {
  public record RaceResultLineRequest(
    @NotNull Long userId,
    @NotNull @Positive Integer position,
    String totalTime,
    String bestLap,
    boolean polePosition,
    @NotNull Integer incidents,
    @NotNull ResultStatus status
  ) {
  }

  public record RaceResultRequest(
    @NotEmpty List<@Valid RaceResultLineRequest> lines
  ) {
  }

  public record RaceResultLineResponse(
    Long id,
    Long userId,
    String username,
    String email,
    Integer position,
    String totalTime,
    String bestLap,
    boolean polePosition,
    Integer incidents,
    ResultStatus status
  ) {
  }

  public record RaceResultResponse(
    Long id,
    Long raceId,
    List<RaceResultLineResponse> lines
  ) {
  }
}
