package com.racecontrol.registration;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.league.LeagueMember;
import com.racecontrol.league.LeagueMemberRepository;
import com.racecontrol.league.LeagueRole;
import com.racecontrol.race.Race;
import com.racecontrol.race.RaceRepository;
import com.racecontrol.team.Team;
import com.racecontrol.team.TeamRepository;
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
public class RegistrationService {
  private final RaceRegistrationRepository registrationRepository;
  private final RaceRepository raceRepository;
  private final TeamRepository teamRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional
  public RegistrationDtos.RegistrationResponse create(Long raceId, RegistrationDtos.RegistrationRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureLeagueMember(race.getSeason().getLeague().getId(), user.getId(), locale);

    if (registrationRepository.existsByRaceIdAndUserId(raceId, user.getId())) {
      throw new FunctionalException(messageSource.getMessage("registration.already.exists", null, locale));
    }

    Team team = resolveTeam(request.teamId(), race.getSeason().getId(), locale);
    RaceRegistration registration = registrationRepository.save(RaceRegistration.builder()
      .race(race)
      .user(user)
      .car(request.car().trim())
      .number(request.number().trim())
      .team(team)
      .status(RegistrationStatus.CONFIRMED)
      .build());

    return toResponse(registration);
  }

  @Transactional(readOnly = true)
  public List<RegistrationDtos.RegistrationResponse> list(Long raceId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureLeagueMember(race.getSeason().getLeague().getId(), user.getId(), locale);
    return registrationRepository.findByRaceIdOrderByCreatedAtAsc(raceId).stream().map(this::toResponse).toList();
  }

  @Transactional
  public void deleteMine(Long raceId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureLeagueMember(race.getSeason().getLeague().getId(), user.getId(), locale);

    RaceRegistration registration = registrationRepository.findByRaceIdAndUserId(raceId, user.getId())
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("registration.notfound", null, locale)));
    registrationRepository.delete(registration);
  }

  @Transactional
  public RegistrationDtos.RegistrationResponse updateStatus(Long raceId, Long registrationId, RegistrationDtos.RegistrationStatusRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), user.getId(), locale);

    RaceRegistration registration = registrationRepository.findById(registrationId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("registration.notfound", null, locale)));
    if (!registration.getRace().getId().equals(raceId)) {
      throw new FunctionalException(messageSource.getMessage("registration.notfound", null, locale));
    }

    registration.setStatus(request.status());
    return toResponse(registrationRepository.save(registration));
  }

  private Team resolveTeam(Long teamId, Long seasonId, Locale locale) {
    if (teamId == null) {
      return null;
    }
    Team team = teamRepository.findById(teamId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("team.notfound", null, locale)));
    if (!team.getSeason().getId().equals(seasonId)) {
      throw new FunctionalException(messageSource.getMessage("registration.team.invalid.season", null, locale));
    }
    return team;
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private Race findRace(Long raceId, Locale locale) {
    return raceRepository.findById(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("race.notfound", null, locale)));
  }

  private void ensureLeagueMember(Long leagueId, Long userId, Locale locale) {
    leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    if (!(member.getRole() == LeagueRole.OWNER || member.getRole() == LeagueRole.ADMIN)) {
      throw new UnauthorizedException(messageSource.getMessage("registration.forbidden.manage", null, locale));
    }
  }

  private RegistrationDtos.RegistrationResponse toResponse(RaceRegistration registration) {
    return new RegistrationDtos.RegistrationResponse(
      registration.getId(),
      registration.getRace().getId(),
      registration.getUser().getId(),
      registration.getUser().getUsername(),
      registration.getUser().getEmail(),
      registration.getCar(),
      registration.getNumber(),
      registration.getTeam() != null ? registration.getTeam().getId() : null,
      registration.getTeam() != null ? registration.getTeam().getName() : null,
      registration.getStatus()
    );
  }
}
