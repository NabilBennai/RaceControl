package com.racecontrol.pointsystem;

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
public class PointSystemService {
  private final PointSystemRepository pointSystemRepository;
  private final SeasonRepository seasonRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @Transactional
  public PointSystemDtos.PointSystemResponse create(Long seasonId, PointSystemDtos.PointSystemRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureCanManage(season.getLeague().getId(), user.getId(), locale);
    validateRules(request.rules(), locale);

    if (pointSystemRepository.findBySeasonId(seasonId).isPresent()) {
      throw new FunctionalException(messageSource.getMessage("pointsystem.already.exists", null, locale));
    }

    PointSystem pointSystem = PointSystem.builder()
      .season(season)
      .name(request.name().trim())
      .build();

    request.rules().forEach(rule -> pointSystem.getRules().add(PointRule.builder()
      .pointSystem(pointSystem)
      .type(rule.type())
      .positionRank(rule.positionRank())
      .points(rule.points())
      .build()));

    return toResponse(pointSystemRepository.save(pointSystem));
  }

  @Transactional(readOnly = true)
  public PointSystemDtos.PointSystemResponse getBySeason(Long seasonId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    Season season = findSeason(seasonId, locale);
    ensureMember(season.getLeague().getId(), user.getId(), locale);

    PointSystem pointSystem = pointSystemRepository.findBySeasonId(seasonId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("pointsystem.notfound", null, locale)));
    return toResponse(pointSystem);
  }

  @Transactional
  public PointSystemDtos.PointSystemResponse update(Long pointSystemId, PointSystemDtos.PointSystemRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    PointSystem pointSystem = pointSystemRepository.findById(pointSystemId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("pointsystem.notfound", null, locale)));
    ensureCanManage(pointSystem.getSeason().getLeague().getId(), user.getId(), locale);
    validateRules(request.rules(), locale);

    pointSystem.setName(request.name().trim());
    pointSystem.getRules().clear();
    request.rules().forEach(rule -> pointSystem.getRules().add(PointRule.builder()
      .pointSystem(pointSystem)
      .type(rule.type())
      .positionRank(rule.positionRank())
      .points(rule.points())
      .build()));

    return toResponse(pointSystemRepository.save(pointSystem));
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
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

  private void validateRules(List<PointSystemDtos.PointRuleRequest> rules, Locale locale) {
    for (PointSystemDtos.PointRuleRequest rule : rules) {
      if (rule.type() == PointRuleType.FINISH_POSITION && (rule.positionRank() == null || rule.positionRank() <= 0)) {
        throw new FunctionalException(messageSource.getMessage("pointsystem.rank.required", null, locale));
      }
      if (rule.type() != PointRuleType.FINISH_POSITION && rule.positionRank() != null) {
        throw new FunctionalException(messageSource.getMessage("pointsystem.rank.not.allowed", null, locale));
      }
    }
  }

  private PointSystemDtos.PointSystemResponse toResponse(PointSystem pointSystem) {
    List<PointSystemDtos.PointRuleResponse> rules = pointSystem.getRules().stream()
      .map(rule -> new PointSystemDtos.PointRuleResponse(rule.getId(), rule.getType(), rule.getPositionRank(), rule.getPoints()))
      .toList();
    return new PointSystemDtos.PointSystemResponse(pointSystem.getId(), pointSystem.getSeason().getId(), pointSystem.getName(), rules);
  }
}
