package com.racecontrol.standing;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seasons/{seasonId}/standings")
public class StandingController {
  private final StandingCalculationService standingService;

  @GetMapping("/drivers")
  List<StandingDtos.DriverStandingResponse> drivers(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return standingService.getDriverStandings(seasonId, authentication.getName(), locale);
  }

  @GetMapping("/teams")
  List<StandingDtos.TeamStandingResponse> teams(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return standingService.getTeamStandings(seasonId, authentication.getName(), locale);
  }

  @PostMapping("/recalculate")
  StandingDtos.StandingsResponse recalculate(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return standingService.recalculate(seasonId, authentication.getName(), locale);
  }
}
