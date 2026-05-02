package com.racecontrol.league;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
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
public class LeagueService {
  private final LeagueRepository leagueRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional
  public LeagueDtos.LeagueResponse create(LeagueDtos.LeagueRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);

    League league = leagueRepository.save(League.builder()
      .name(request.name().trim())
      .description(request.description().trim())
      .gamePlatform(request.gamePlatform())
      .visibility(request.visibility())
      .build());

    LeagueMember ownerMember = leagueMemberRepository.save(LeagueMember.builder()
      .league(league)
      .user(user)
      .role(LeagueRole.OWNER)
      .build());

    return toResponse(league, ownerMember.getRole());
  }

  @Transactional(readOnly = true)
  public List<LeagueDtos.LeagueResponse> listMine(String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    return leagueRepository.findDistinctByMembersUserIdOrderByCreatedAtDesc(user.getId()).stream()
      .map(league -> toResponse(league, roleFor(league.getId(), user.getId(), locale)))
      .toList();
  }

  @Transactional(readOnly = true)
  public LeagueDtos.LeagueResponse getById(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    League league = findLeague(leagueId, locale);
    LeagueRole role = roleFor(leagueId, user.getId(), locale);
    return toResponse(league, role);
  }

  @Transactional
  public LeagueDtos.LeagueResponse update(Long leagueId, LeagueDtos.LeagueRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = roleFor(leagueId, user.getId(), locale);
    if (!(role == LeagueRole.OWNER || role == LeagueRole.ADMIN)) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.update", null, locale));
    }

    League league = findLeague(leagueId, locale);
    league.setName(request.name().trim());
    league.setDescription(request.description().trim());
    league.setGamePlatform(request.gamePlatform());
    league.setVisibility(request.visibility());

    return toResponse(leagueRepository.save(league), role);
  }

  @Transactional
  public void delete(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = roleFor(leagueId, user.getId(), locale);
    if (role != LeagueRole.OWNER) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.delete", null, locale));
    }

    League league = findLeague(leagueId, locale);
    leagueRepository.delete(league);
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private League findLeague(Long leagueId, Locale locale) {
    return leagueRepository.findById(leagueId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("league.notfound", null, locale)));
  }

  private LeagueRole roleFor(Long leagueId, Long userId, Locale locale) {
    return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .map(LeagueMember::getRole)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
  }

  private LeagueDtos.LeagueResponse toResponse(League league, LeagueRole role) {
    return new LeagueDtos.LeagueResponse(
      league.getId(),
      league.getName(),
      league.getDescription(),
      league.getGamePlatform(),
      league.getVisibility(),
      role
    );
  }
}
