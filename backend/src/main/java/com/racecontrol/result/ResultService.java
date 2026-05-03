package com.racecontrol.result;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.league.LeagueMember;
import com.racecontrol.league.LeagueMemberRepository;
import com.racecontrol.league.LeagueRole;
import com.racecontrol.race.Race;
import com.racecontrol.race.RaceRepository;
import com.racecontrol.user.User;
import com.racecontrol.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResultService {
  private final RaceResultRepository raceResultRepository;
  private final RaceRepository raceRepository;
  private final UserRepository userRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final MessageSource messageSource;

  @Transactional
  public ResultDtos.RaceResultResponse create(Long raceId, ResultDtos.RaceResultRequest request, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), actor.getId(), locale);
    validateLines(request.lines(), race.getSeason().getLeague().getId(), locale);

    if (raceResultRepository.findByRaceId(raceId).isPresent()) {
      throw new FunctionalException(messageSource.getMessage("result.already.exists", null, locale));
    }

    RaceResult raceResult = RaceResult.builder().race(race).build();
    applyLines(raceResult, request.lines());
    RaceResult saved = raceResultRepository.save(raceResult);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public ResultDtos.RaceResultResponse get(Long raceId, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureLeagueMember(race.getSeason().getLeague().getId(), actor.getId(), locale);
    RaceResult result = raceResultRepository.findByRaceId(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("result.notfound", null, locale)));
    return toResponse(result);
  }

  @Transactional
  public ResultDtos.RaceResultResponse update(Long raceId, ResultDtos.RaceResultRequest request, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), actor.getId(), locale);
    validateLines(request.lines(), race.getSeason().getLeague().getId(), locale);

    RaceResult result = raceResultRepository.findByRaceId(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("result.notfound", null, locale)));
    result.getLines().clear();
    applyLines(result, request.lines());
    RaceResult saved = raceResultRepository.save(result);
    return toResponse(saved);
  }

  @Transactional
  public void delete(Long raceId, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), actor.getId(), locale);
    RaceResult result = raceResultRepository.findByRaceId(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("result.notfound", null, locale)));
    raceResultRepository.delete(result);
  }

  private void validateLines(List<ResultDtos.RaceResultLineRequest> lines, Long leagueId, Locale locale) {
    Set<Integer> positions = new HashSet<>();
    Set<Long> drivers = new HashSet<>();
    for (ResultDtos.RaceResultLineRequest line : lines) {
      if (!positions.add(line.position())) {
        throw new FunctionalException(messageSource.getMessage("result.position.duplicate", null, locale));
      }
      if (!drivers.add(line.userId())) {
        throw new FunctionalException(messageSource.getMessage("result.driver.duplicate", null, locale));
      }
      LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, line.userId())
        .orElseThrow(() -> new FunctionalException(messageSource.getMessage("result.driver.not.in.league", null, locale)));
      if (member.getRole() == null) {
        throw new FunctionalException(messageSource.getMessage("result.driver.not.in.league", null, locale));
      }
    }
  }

  private void applyLines(RaceResult raceResult, List<ResultDtos.RaceResultLineRequest> lines) {
    lines.stream()
      .sorted(java.util.Comparator.comparing(ResultDtos.RaceResultLineRequest::position))
      .forEach(line -> {
        User user = userRepository.findById(line.userId()).orElseThrow();
        raceResult.getLines().add(RaceResultLine.builder()
          .raceResult(raceResult)
          .user(user)
          .position(line.position())
          .totalTime(trimToNull(line.totalTime()))
          .bestLap(trimToNull(line.bestLap()))
          .polePosition(line.polePosition())
          .incidents(line.incidents())
          .status(line.status())
          .build());
      });
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private Race findRace(Long raceId, Locale locale) {
    return raceRepository.findById(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("race.notfound", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    if (!(member.getRole() == LeagueRole.OWNER || member.getRole() == LeagueRole.ADMIN || member.getRole() == LeagueRole.STEWARD)) {
      throw new UnauthorizedException(messageSource.getMessage("result.forbidden.manage", null, locale));
    }
  }

  private void ensureLeagueMember(Long leagueId, Long userId, Locale locale) {
    leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
  }

  private ResultDtos.RaceResultResponse toResponse(RaceResult result) {
    List<ResultDtos.RaceResultLineResponse> lines = result.getLines().stream()
      .sorted(java.util.Comparator.comparing(RaceResultLine::getPosition))
      .map(line -> new ResultDtos.RaceResultLineResponse(
        line.getId(),
        line.getUser().getId(),
        line.getUser().getUsername(),
        line.getUser().getEmail(),
        line.getPosition(),
        line.getTotalTime(),
        line.getBestLap(),
        line.isPolePosition(),
        line.getIncidents(),
        line.getStatus()
      ))
      .toList();

    return new ResultDtos.RaceResultResponse(result.getId(), result.getRace().getId(), lines);
  }

  private String trimToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
