package com.racecontrol.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrationDtos {
  public record RegistrationRequest(
    @NotBlank String car,
    @NotBlank String number,
    Long teamId
  ) {
  }

  public record RegistrationStatusRequest(
    @NotNull RegistrationStatus status
  ) {
  }

  public record RegistrationResponse(
    Long id,
    Long raceId,
    Long userId,
    String username,
    String email,
    String car,
    String number,
    Long teamId,
    String teamName,
    RegistrationStatus status
  ) {
  }
}
