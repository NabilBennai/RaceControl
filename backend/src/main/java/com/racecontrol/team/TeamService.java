package com.racecontrol.team;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.league.LeagueMember;
import com.racecontrol.league.LeagueMemberRepository;
import com.racecontrol.league.LeagueRole;
import com.racecontrol.season.Season;
import com.racecontrol.season.SeasonRepository;
import com.racecontrol.user.User;
import com.racecontrol.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TeamService {
  private final TeamRepository teamRepository;
  private final TeamMemberRepository teamMemberRepository;
  private final SeasonRepository seasonRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional
  public TeamDtos.TeamResponse create(Long seasonId, TeamDtos.TeamRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureCanManage(season.getLeague().getId(), user.getId(), locale);

    Team team = teamRepository.save(Team.builder()
      .season(season)
      .name(request.name().trim())
      .color(normalizeColor(request.color()))
      .logoUrl(normalizeLogoUrl(request.logoUrl()))
      .build());

    return toResponse(team);
  }

  @Transactional(readOnly = true)
  public List<TeamDtos.TeamResponse> listBySeason(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureMember(season.getLeague().getId(), user.getId(), locale);
    return teamRepository.findBySeasonIdOrderByNameAsc(seasonId).stream().map(this::toResponse).toList();
  }

  @Transactional
  public TeamDtos.TeamResponse update(Long teamId, TeamDtos.TeamRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Team team = findTeam(teamId, locale);
    ensureCanManage(team.getSeason().getLeague().getId(), user.getId(), locale);

    team.setName(request.name().trim());
    team.setColor(normalizeColor(request.color()));
    team.setLogoUrl(normalizeLogoUrl(request.logoUrl()));

    return toResponse(teamRepository.save(team));
  }

  @Transactional
  public void delete(Long teamId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Team team = findTeam(teamId, locale);
    ensureCanManage(team.getSeason().getLeague().getId(), user.getId(), locale);
    teamRepository.delete(team);
  }

  @Transactional
  public TeamDtos.TeamResponse addMember(Long teamId, TeamDtos.TeamMemberRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Team team = findTeam(teamId, locale);
    Long leagueId = team.getSeason().getLeague().getId();
    ensureCanManage(leagueId, user.getId(), locale);

    User targetUser = userRepository.findById(request.userId())
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("team.member.user.notfound", null, locale)));

    boolean approvedLeagueMember = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, targetUser.getId()).isPresent();
    if (!approvedLeagueMember) {
      throw new FunctionalException(messageSource.getMessage("team.member.not.in.league", null, locale));
    }
    if (teamMemberRepository.existsByTeamIdAndUserId(teamId, targetUser.getId())) {
      throw new FunctionalException(messageSource.getMessage("team.member.already.exists", null, locale));
    }
    if (teamMemberRepository.existsByUserIdAndTeam_Season_Id(targetUser.getId(), team.getSeason().getId())) {
      throw new FunctionalException(messageSource.getMessage("team.member.already.in.season.team", null, locale));
    }

    teamMemberRepository.save(TeamMember.builder()
      .team(team)
      .user(targetUser)
      .build());

    Team reloaded = findTeam(teamId, locale);
    return toResponse(reloaded);
  }

  @Transactional
  public TeamDtos.TeamResponse removeMember(Long teamId, Long memberId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Team team = findTeam(teamId, locale);
    ensureCanManage(team.getSeason().getLeague().getId(), user.getId(), locale);

    TeamMember member = teamMemberRepository.findByIdAndTeamId(memberId, teamId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("team.member.notfound", null, locale)));
    teamMemberRepository.delete(member);

    Team reloaded = findTeam(teamId, locale);
    return toResponse(reloaded);
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private Season findSeason(Long seasonId, Locale locale) {
    return seasonRepository.findById(seasonId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("season.notfound", null, locale)));
  }

  private Team findTeam(Long teamId, Locale locale) {
    return teamRepository.findById(teamId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("team.notfound", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueRole role = ensureMember(leagueId, userId, locale);
    if (!(role == LeagueRole.OWNER || role == LeagueRole.ADMIN)) {
      throw new UnauthorizedException(messageSource.getMessage("team.forbidden.manage", null, locale));
    }
  }

  private LeagueRole ensureMember(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    return member.getRole();
  }

  private TeamDtos.TeamResponse toResponse(Team team) {
    List<TeamDtos.TeamMemberResponse> members = team.getMembers().stream()
      .map(member -> new TeamDtos.TeamMemberResponse(
        member.getId(),
        member.getUser().getId(),
        member.getUser().getUsername(),
        member.getUser().getEmail()
      ))
      .toList();

    return new TeamDtos.TeamResponse(
      team.getId(),
      team.getSeason().getId(),
      team.getName(),
      team.getColor(),
      team.getLogoUrl(),
      members
    );
  }

  private String normalizeColor(String color) {
    String raw = color.trim();
    String prefixed = raw.startsWith("#") ? raw : "#" + raw;
    return prefixed.toUpperCase(Locale.ROOT);
  }

  private String normalizeLogoUrl(String logoUrl) {
    if (logoUrl == null || logoUrl.isBlank()) {
      return null;
    }
    return logoUrl.trim();
  }
}
