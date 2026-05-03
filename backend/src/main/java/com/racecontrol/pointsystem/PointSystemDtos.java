package com.racecontrol.pointsystem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PointSystemDtos {
  public record PointRuleRequest(
    @NotNull PointRuleType type,
    Integer positionRank,
    @NotNull Integer points
  ) {
  }

  public record PointSystemRequest(
    @NotBlank String name,
    @NotEmpty List<@Valid PointRuleRequest> rules
  ) {
  }

  public record PointRuleResponse(
    Long id,
    PointRuleType type,
    Integer positionRank,
    Integer points
  ) {
  }

  public record PointSystemResponse(
    Long id,
    Long seasonId,
    String name,
    List<PointRuleResponse> rules
  ) {
  }
}
