package com.racecontrol.race;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RaceController {
  private final RaceService raceService;

  @PostMapping("/seasons/{seasonId}/races")
  RaceDtos.RaceResponse create(
    @PathVariable Long seasonId,
    @Valid @RequestBody RaceDtos.RaceRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return raceService.create(seasonId, request, authentication.getName(), locale);
  }

  @GetMapping("/seasons/{seasonId}/races")
  List<RaceDtos.RaceResponse> listBySeason(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return raceService.listBySeason(seasonId, authentication.getName(), locale);
  }

  @GetMapping("/races/{raceId}")
  RaceDtos.RaceResponse getById(@PathVariable Long raceId, Authentication authentication, Locale locale) {
    return raceService.getById(raceId, authentication.getName(), locale);
  }

  @PutMapping("/races/{raceId}")
  RaceDtos.RaceResponse update(
    @PathVariable Long raceId,
    @Valid @RequestBody RaceDtos.RaceRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return raceService.update(raceId, request, authentication.getName(), locale);
  }

  @DeleteMapping("/races/{raceId}")
  void delete(@PathVariable Long raceId, Authentication authentication, Locale locale) {
    raceService.delete(raceId, authentication.getName(), locale);
  }

  @PatchMapping("/races/{raceId}/status")
  RaceDtos.RaceResponse updateStatus(
    @PathVariable Long raceId,
    @Valid @RequestBody RaceDtos.RaceStatusRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return raceService.updateStatus(raceId, request, authentication.getName(), locale);
  }
}
