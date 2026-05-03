package com.racecontrol.registration;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/races/{raceId}/registrations")
public class RegistrationController {
  private final RegistrationService registrationService;

  @PostMapping
  RegistrationDtos.RegistrationResponse create(
    @PathVariable Long raceId,
    @Valid @RequestBody RegistrationDtos.RegistrationRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return registrationService.create(raceId, request, authentication.getName(), locale);
  }

  @GetMapping
  List<RegistrationDtos.RegistrationResponse> list(@PathVariable Long raceId, Authentication authentication, Locale locale) {
    return registrationService.list(raceId, authentication.getName(), locale);
  }

  @DeleteMapping("/me")
  void deleteMine(@PathVariable Long raceId, Authentication authentication, Locale locale) {
    registrationService.deleteMine(raceId, authentication.getName(), locale);
  }

  @PatchMapping("/{registrationId}/status")
  RegistrationDtos.RegistrationResponse updateStatus(
    @PathVariable Long raceId,
    @PathVariable Long registrationId,
    @Valid @RequestBody RegistrationDtos.RegistrationStatusRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return registrationService.updateStatus(raceId, registrationId, request, authentication.getName(), locale);
  }
}
