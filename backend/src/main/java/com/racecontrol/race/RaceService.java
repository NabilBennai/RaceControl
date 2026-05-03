package com.racecontrol.race;

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
public class RaceService {
  private final RaceRepository raceRepository;
  private final SeasonRepository seasonRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional
  public RaceDtos.RaceResponse create(Long seasonId, RaceDtos.RaceRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureCanManage(season.getLeague().getId(), user.getId(), locale);
    validateRequest(request, locale);

    Race race = raceRepository.save(Race.builder()
      .season(season)
      .track(request.track().trim())
      .raceDate(request.raceDate())
      .raceTime(request.raceTime())
      .format(request.format().trim())
      .laps(request.laps())
      .durationMinutes(request.durationMinutes())
      .weather(request.weather().trim())
      .carCategory(request.carCategory().trim())
      .status(RaceStatus.SCHEDULED)
      .build());

    return toResponse(race);
  }

  @Transactional(readOnly = true)
  public List<RaceDtos.RaceResponse> listBySeason(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureMember(season.getLeague().getId(), user.getId(), locale);
    return raceRepository.findBySeasonIdOrderByRaceDateAscRaceTimeAsc(seasonId).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public RaceDtos.RaceResponse getById(Long raceId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureMember(race.getSeason().getLeague().getId(), user.getId(), locale);
    return toResponse(race);
  }

  @Transactional
  public RaceDtos.RaceResponse update(Long raceId, RaceDtos.RaceRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), user.getId(), locale);
    validateRequest(request, locale);

    race.setTrack(request.track().trim());
    race.setRaceDate(request.raceDate());
    race.setRaceTime(request.raceTime());
    race.setFormat(request.format().trim());
    race.setLaps(request.laps());
    race.setDurationMinutes(request.durationMinutes());
    race.setWeather(request.weather().trim());
    race.setCarCategory(request.carCategory().trim());

    return toResponse(raceRepository.save(race));
  }

  @Transactional
  public void delete(Long raceId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), user.getId(), locale);
    raceRepository.delete(race);
  }

  @Transactional
  public RaceDtos.RaceResponse updateStatus(Long raceId, RaceDtos.RaceStatusRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), user.getId(), locale);
    race.setStatus(request.status());
    return toResponse(raceRepository.save(race));
  }

  private void validateRequest(RaceDtos.RaceRequest request, Locale locale) {
    if ((request.laps() == null || request.laps() <= 0) && (request.durationMinutes() == null || request.durationMinutes() <= 0)) {
      throw new FunctionalException(messageSource.getMessage("race.laps.or.duration.required", null, locale));
    }
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private Season findSeason(Long seasonId, Locale locale) {
    return seasonRepository.findById(seasonId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("season.notfound", null, locale)));
  }

  private Race findRace(Long raceId, Locale locale) {
    return raceRepository.findById(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("race.notfound", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueRole role = ensureMember(leagueId, userId, locale);
    if (!(role == LeagueRole.OWNER || role == LeagueRole.ADMIN)) {
      throw new UnauthorizedException(messageSource.getMessage("race.forbidden.manage", null, locale));
    }
  }

  private LeagueRole ensureMember(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    return member.getRole();
  }

  private RaceDtos.RaceResponse toResponse(Race race) {
    return new RaceDtos.RaceResponse(
      race.getId(),
      race.getSeason().getId(),
      race.getTrack(),
      race.getRaceDate(),
      race.getRaceTime(),
      race.getFormat(),
      race.getLaps(),
      race.getDurationMinutes(),
      race.getWeather(),
      race.getCarCategory(),
      race.getStatus()
    );
  }
}
