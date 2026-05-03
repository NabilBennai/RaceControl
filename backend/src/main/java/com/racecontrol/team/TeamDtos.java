package com.racecontrol.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class TeamDtos {
  public record TeamRequest(
    @NotBlank String name,
    @NotBlank @Pattern(regexp = "^#?[0-9A-Fa-f]{6}$", message = "Couleur invalide") String color,
    String logoUrl
  ) {
  }

  public record TeamMemberRequest(
    @NotNull Long userId
  ) {
  }

  public record TeamMemberResponse(
    Long id,
    Long userId,
    String username,
    String email
  ) {
  }

  public record TeamResponse(
    Long id,
    Long seasonId,
    String name,
    String color,
    String logoUrl,
    List<TeamMemberResponse> members
  ) {
  }
}
