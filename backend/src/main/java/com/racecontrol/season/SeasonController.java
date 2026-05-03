package com.racecontrol.season;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeasonController {
  private final SeasonService seasonService;

  @PostMapping("/leagues/{leagueId}/seasons")
  SeasonDtos.SeasonResponse create(
    @PathVariable Long leagueId,
    @Valid @RequestBody SeasonDtos.SeasonRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return seasonService.create(leagueId, request, authentication.getName(), locale);
  }

  @GetMapping("/leagues/{leagueId}/seasons")
  List<SeasonDtos.SeasonResponse> listByLeague(@PathVariable Long leagueId, Authentication authentication, Locale locale) {
    return seasonService.listByLeague(leagueId, authentication.getName(), locale);
  }

  @GetMapping("/seasons/{seasonId}")
  SeasonDtos.SeasonResponse getById(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return seasonService.getById(seasonId, authentication.getName(), locale);
  }

  @PutMapping("/seasons/{seasonId}")
  SeasonDtos.SeasonResponse update(
    @PathVariable Long seasonId,
    @Valid @RequestBody SeasonDtos.SeasonRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return seasonService.update(seasonId, request, authentication.getName(), locale);
  }

  @PatchMapping("/seasons/{seasonId}/activate")
  SeasonDtos.SeasonResponse activate(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return seasonService.activate(seasonId, authentication.getName(), locale);
  }

  @PatchMapping("/seasons/{seasonId}/finish")
  SeasonDtos.SeasonResponse finish(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return seasonService.finish(seasonId, authentication.getName(), locale);
  }
}
