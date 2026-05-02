package com.racecontrol.league;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/leagues")
@RequiredArgsConstructor
public class LeagueController {
  private final LeagueService leagueService;

  @PostMapping
  LeagueDtos.LeagueResponse create(
    @Valid @RequestBody LeagueDtos.LeagueRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return leagueService.create(request, authentication.getName(), locale);
  }

  @GetMapping
  List<LeagueDtos.LeagueResponse> list(Authentication authentication, Locale locale) {
    return leagueService.listMine(authentication.getName(), locale);
  }

  @GetMapping("/{leagueId}")
  LeagueDtos.LeagueResponse getById(
    @PathVariable Long leagueId,
    Authentication authentication,
    Locale locale
  ) {
    return leagueService.getById(leagueId, authentication.getName(), locale);
  }

  @PutMapping("/{leagueId}")
  LeagueDtos.LeagueResponse update(
    @PathVariable Long leagueId,
    @Valid @RequestBody LeagueDtos.LeagueRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return leagueService.update(leagueId, request, authentication.getName(), locale);
  }

  @DeleteMapping("/{leagueId}")
  void delete(@PathVariable Long leagueId, Authentication authentication, Locale locale) {
    leagueService.delete(leagueId, authentication.getName(), locale);
  }
}
