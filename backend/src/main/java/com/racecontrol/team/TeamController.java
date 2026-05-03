package com.racecontrol.team;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TeamController {
  private final TeamService teamService;

  @PostMapping("/seasons/{seasonId}/teams")
  TeamDtos.TeamResponse create(
    @PathVariable Long seasonId,
    @Valid @RequestBody TeamDtos.TeamRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return teamService.create(seasonId, request, authentication.getName(), locale);
  }

  @GetMapping("/seasons/{seasonId}/teams")
  List<TeamDtos.TeamResponse> listBySeason(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return teamService.listBySeason(seasonId, authentication.getName(), locale);
  }

  @PutMapping("/teams/{teamId}")
  TeamDtos.TeamResponse update(
    @PathVariable Long teamId,
    @Valid @RequestBody TeamDtos.TeamRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return teamService.update(teamId, request, authentication.getName(), locale);
  }

  @DeleteMapping("/teams/{teamId}")
  void delete(@PathVariable Long teamId, Authentication authentication, Locale locale) {
    teamService.delete(teamId, authentication.getName(), locale);
  }

  @PostMapping("/teams/{teamId}/members")
  TeamDtos.TeamResponse addMember(
    @PathVariable Long teamId,
    @Valid @RequestBody TeamDtos.TeamMemberRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return teamService.addMember(teamId, request, authentication.getName(), locale);
  }

  @DeleteMapping("/teams/{teamId}/members/{memberId}")
  TeamDtos.TeamResponse removeMember(
    @PathVariable Long teamId,
    @PathVariable Long memberId,
    Authentication authentication,
    Locale locale
  ) {
    return teamService.removeMember(teamId, memberId, authentication.getName(), locale);
  }
}
