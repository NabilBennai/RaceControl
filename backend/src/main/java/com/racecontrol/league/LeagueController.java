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

  @PostMapping("/{leagueId}/join")
  LeagueDtos.LeagueJoinResponse join(@PathVariable Long leagueId, Authentication authentication, Locale locale) {
    return leagueService.join(leagueId, authentication.getName(), locale);
  }

  @PostMapping("/join-by-code")
  LeagueDtos.LeagueJoinResponse joinByCode(
    @Valid @RequestBody LeagueDtos.JoinByCodeRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return leagueService.joinByCode(request, authentication.getName(), locale);
  }

  @GetMapping("/{leagueId}/members")
  List<LeagueDtos.LeagueMemberResponse> members(@PathVariable Long leagueId, Authentication authentication, Locale locale) {
    return leagueService.members(leagueId, authentication.getName(), locale);
  }

  @PatchMapping("/{leagueId}/members/{memberId}/approve")
  LeagueDtos.LeagueMemberResponse approve(
    @PathVariable Long leagueId,
    @PathVariable Long memberId,
    Authentication authentication,
    Locale locale
  ) {
    return leagueService.approveMember(leagueId, memberId, authentication.getName(), locale);
  }

  @PatchMapping("/{leagueId}/members/{memberId}/reject")
  LeagueDtos.LeagueMemberResponse reject(
    @PathVariable Long leagueId,
    @PathVariable Long memberId,
    Authentication authentication,
    Locale locale
  ) {
    return leagueService.rejectMember(leagueId, memberId, authentication.getName(), locale);
  }
}
