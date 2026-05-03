package com.racecontrol.standing;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.league.LeagueMember;
import com.racecontrol.league.LeagueMemberRepository;
import com.racecontrol.league.LeagueRole;
import com.racecontrol.pointsystem.PointRule;
import com.racecontrol.pointsystem.PointRuleType;
import com.racecontrol.pointsystem.PointSystem;
import com.racecontrol.pointsystem.PointSystemRepository;
import com.racecontrol.registration.RaceRegistration;
import com.racecontrol.registration.RaceRegistrationRepository;
import com.racecontrol.result.RaceResult;
import com.racecontrol.result.RaceResultLine;
import com.racecontrol.result.RaceResultRepository;
import com.racecontrol.season.Season;
import com.racecontrol.season.SeasonRepository;
import com.racecontrol.team.Team;
import com.racecontrol.user.User;
import com.racecontrol.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StandingCalculationService {
  private final DriverStandingRepository driverStandingRepository;
  private final TeamStandingRepository teamStandingRepository;
  private final SeasonRepository seasonRepository;
  private final PointSystemRepository pointSystemRepository;
  private final RaceResultRepository raceResultRepository;
  private final RaceRegistrationRepository raceRegistrationRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional(readOnly = true)
  public List<StandingDtos.DriverStandingResponse> getDriverStandings(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureLeagueMember(season.getLeague().getId(), user.getId(), locale);
    return driverStandingRepository.findBySeasonIdOrderByPointsDescWinsDescPodiumsDesc(seasonId).stream()
      .map(ds -> new StandingDtos.DriverStandingResponse(
        ds.getUser().getId(), ds.getUser().getUsername(), ds.getUser().getEmail(), ds.getPoints(), ds.getWins(), ds.getPodiums(), ds.getPoles(), ds.getFastestLaps()
      )).toList();
  }

  @Transactional(readOnly = true)
  public List<StandingDtos.TeamStandingResponse> getTeamStandings(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureLeagueMember(season.getLeague().getId(), user.getId(), locale);
    return teamStandingRepository.findBySeasonIdOrderByPointsDescWinsDescPodiumsDesc(seasonId).stream()
      .map(ts -> new StandingDtos.TeamStandingResponse(
        ts.getTeam().getId(), ts.getTeam().getName(), ts.getTeam().getColor(), ts.getPoints(), ts.getWins(), ts.getPodiums(), ts.getPoles(), ts.getFastestLaps()
      )).toList();
  }

  @Transactional
  public StandingDtos.StandingsResponse recalculate(Long seasonId, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureCanManage(season.getLeague().getId(), actor.getId(), locale);

    PointSystem pointSystem = pointSystemRepository.findBySeasonId(seasonId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("pointsystem.notfound", null, locale)));

    Map<Integer, Integer> finishPoints = new HashMap<>();
    int poleBonus = 0;
    int fastestBonus = 0;
    for (PointRule r : pointSystem.getRules()) {
      if (r.getType() == PointRuleType.FINISH_POSITION && r.getPositionRank() != null) {
        finishPoints.put(r.getPositionRank(), r.getPoints());
      } else if (r.getType() == PointRuleType.POLE_POSITION) {
        poleBonus += r.getPoints();
      } else if (r.getType() == PointRuleType.FASTEST_LAP) {
        fastestBonus += r.getPoints();
      }
    }

    Map<Long, DriverAgg> driver = new HashMap<>();
    Map<Long, TeamAgg> team = new HashMap<>();

    List<RaceResult> results = raceResultRepository.findAll().stream()
      .filter(rr -> rr.getRace().getSeason().getId().equals(seasonId))
      .toList();

    for (RaceResult rr : results) {
      Long raceId = rr.getRace().getId();
      Map<Long, RaceRegistration> regsByUser = new HashMap<>();
      raceRegistrationRepository.findByRaceIdOrderByCreatedAtAsc(raceId)
        .forEach(reg -> regsByUser.put(reg.getUser().getId(), reg));

      for (RaceResultLine line : rr.getLines()) {
        Long userId = line.getUser().getId();
        DriverAgg d = driver.computeIfAbsent(userId, k -> new DriverAgg());
        d.user = line.getUser();
        d.points += finishPoints.getOrDefault(line.getPosition(), 0);
        if (line.isPolePosition()) {
          d.points += poleBonus;
          d.poles += 1;
        }
        if (line.getBestLap() != null && !line.getBestLap().isBlank()) {
          d.points += fastestBonus;
          d.fastestLaps += 1;
        }
        if (line.getPosition() == 1) {
          d.wins += 1;
        }
        if (line.getPosition() <= 3) {
          d.podiums += 1;
        }

        RaceRegistration reg = regsByUser.get(userId);
        Team t = reg != null ? reg.getTeam() : null;
        if (t != null) {
          TeamAgg ta = team.computeIfAbsent(t.getId(), k -> new TeamAgg());
          ta.team = t;
          ta.points += finishPoints.getOrDefault(line.getPosition(), 0);
          if (line.isPolePosition()) {
            ta.points += poleBonus;
            ta.poles += 1;
          }
          if (line.getBestLap() != null && !line.getBestLap().isBlank()) {
            ta.points += fastestBonus;
            ta.fastestLaps += 1;
          }
          if (line.getPosition() == 1) {
            ta.wins += 1;
          }
          if (line.getPosition() <= 3) {
            ta.podiums += 1;
          }
        }
      }
    }

    driverStandingRepository.deleteBySeasonId(seasonId);
    teamStandingRepository.deleteBySeasonId(seasonId);

    driver.values().forEach(v -> driverStandingRepository.save(DriverStanding.builder()
      .season(season).user(v.user).points(v.points).wins(v.wins).podiums(v.podiums).poles(v.poles).fastestLaps(v.fastestLaps).build()));
    team.values().forEach(v -> teamStandingRepository.save(TeamStanding.builder()
      .season(season).team(v.team).points(v.points).wins(v.wins).podiums(v.podiums).poles(v.poles).fastestLaps(v.fastestLaps).build()));

    return new StandingDtos.StandingsResponse(
      seasonId,
      getDriverStandings(seasonId, email, locale),
      getTeamStandings(seasonId, email, locale)
    );
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private Season findSeason(Long seasonId, Locale locale) {
    return seasonRepository.findById(seasonId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("season.notfound", null, locale)));
  }

  private void ensureLeagueMember(Long leagueId, Long userId, Locale locale) {
    leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    if (!(member.getRole() == LeagueRole.OWNER || member.getRole() == LeagueRole.ADMIN || member.getRole() == LeagueRole.STEWARD)) {
      throw new UnauthorizedException(messageSource.getMessage("standing.forbidden.recalculate", null, locale));
    }
  }

  private static class DriverAgg {
    User user;
    int points;
    int wins;
    int podiums;
    int poles;
    int fastestLaps;
  }

  private static class TeamAgg {
    Team team;
    int points;
    int wins;
    int podiums;
    int poles;
    int fastestLaps;
  }
}
