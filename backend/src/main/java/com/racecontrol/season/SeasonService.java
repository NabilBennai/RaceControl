package com.racecontrol.season;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.league.League;
import com.racecontrol.league.LeagueMember;
import com.racecontrol.league.LeagueMemberRepository;
import com.racecontrol.league.LeagueRepository;
import com.racecontrol.league.LeagueRole;
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
public class SeasonService {
  private final SeasonRepository seasonRepository;
  private final LeagueRepository leagueRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional
  public SeasonDtos.SeasonResponse create(Long leagueId, SeasonDtos.SeasonRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    ensureCanManage(leagueId, user.getId(), locale);
    validateDates(request, locale);

    League league = findLeague(leagueId, locale);
    Season season = seasonRepository.save(Season.builder()
      .league(league)
      .name(request.name().trim())
      .startDate(request.startDate())
      .endDate(request.endDate())
      .status(SeasonStatus.DRAFT)
      .build());

    return toResponse(season);
  }

  @Transactional(readOnly = true)
  public List<SeasonDtos.SeasonResponse> listByLeague(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    ensureMember(leagueId, user.getId(), locale);
    return seasonRepository.findByLeagueIdOrderByStartDateDesc(leagueId).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public SeasonDtos.SeasonResponse getById(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureMember(season.getLeague().getId(), user.getId(), locale);
    return toResponse(season);
  }

  @Transactional
  public SeasonDtos.SeasonResponse update(Long seasonId, SeasonDtos.SeasonRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureCanManage(season.getLeague().getId(), user.getId(), locale);
    validateDates(request, locale);

    season.setName(request.name().trim());
    season.setStartDate(request.startDate());
    season.setEndDate(request.endDate());

    return toResponse(seasonRepository.save(season));
  }

  @Transactional
  public SeasonDtos.SeasonResponse activate(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    Long leagueId = season.getLeague().getId();
    ensureCanManage(leagueId, user.getId(), locale);

    if (seasonRepository.existsByLeagueIdAndStatus(leagueId, SeasonStatus.ACTIVE) && season.getStatus() != SeasonStatus.ACTIVE) {
      throw new FunctionalException(messageSource.getMessage("season.active.exists", null, locale));
    }

    season.setStatus(SeasonStatus.ACTIVE);
    return toResponse(seasonRepository.save(season));
  }

  @Transactional
  public SeasonDtos.SeasonResponse finish(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureCanManage(season.getLeague().getId(), user.getId(), locale);

    season.setStatus(SeasonStatus.FINISHED);
    return toResponse(seasonRepository.save(season));
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private League findLeague(Long leagueId, Locale locale) {
    return leagueRepository.findById(leagueId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("league.notfound", null, locale)));
  }

  private Season findSeason(Long seasonId, Locale locale) {
    return seasonRepository.findById(seasonId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("season.notfound", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueRole role = ensureMember(leagueId, userId, locale);
    if (!(role == LeagueRole.OWNER || role == LeagueRole.ADMIN)) {
      throw new UnauthorizedException(messageSource.getMessage("season.forbidden.manage", null, locale));
    }
  }

  private LeagueRole ensureMember(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    return member.getRole();
  }

  private void validateDates(SeasonDtos.SeasonRequest request, Locale locale) {
    if (request.endDate().isBefore(request.startDate())) {
      throw new FunctionalException(messageSource.getMessage("season.dates.invalid", null, locale));
    }
  }

  private SeasonDtos.SeasonResponse toResponse(Season season) {
    return new SeasonDtos.SeasonResponse(
      season.getId(),
      season.getLeague().getId(),
      season.getName(),
      season.getStartDate(),
      season.getEndDate(),
      season.getStatus()
    );
  }
}
