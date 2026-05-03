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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeagueService {
  private final LeagueRepository leagueRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final LeagueJoinRequestRepository leagueJoinRequestRepository;
  private final LeagueInvitationCodeRepository leagueInvitationCodeRepository;
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

    String invitationCode = createInvitationCode(league);
    return toResponse(league, ownerMember.getRole(), invitationCode);
  }

  @Transactional(readOnly = true)
  public List<LeagueDtos.LeagueResponse> listMine(String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    List<League> mine = leagueRepository.findDistinctByMembersUserIdOrderByCreatedAtDesc(user.getId());
    List<League> publicLeagues = leagueRepository.findByVisibilityOrderByCreatedAtDesc(LeagueVisibility.PUBLIC);

    return java.util.stream.Stream.concat(mine.stream(), publicLeagues.stream())
      .collect(java.util.stream.Collectors.toMap(League::getId, league -> league, (a, b) -> a, java.util.LinkedHashMap::new))
      .values()
      .stream()
      .map(league -> {
        LeagueRole role = roleIfMember(league.getId(), user.getId());
        String invitationCode = canManageLeague(role) ? activeCodeForLeague(league.getId()) : null;
        return toResponse(league, role, invitationCode);
      })
      .toList();
  }

  @Transactional(readOnly = true)
  public LeagueDtos.LeagueResponse getById(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    League league = findLeague(leagueId, locale);
    LeagueRole role = roleIfMember(leagueId, user.getId());
    String invitationCode = canManageLeague(role) ? activeCodeForLeague(leagueId) : null;
    return toResponse(league, role, invitationCode);
  }

  @Transactional
  public LeagueDtos.LeagueResponse update(Long leagueId, LeagueDtos.LeagueRequest request, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = requiredRole(leagueId, user.getId(), locale);
    if (!canManageLeague(role)) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.update", null, locale));
    }

    League league = findLeague(leagueId, locale);
    league.setName(request.name().trim());
    league.setDescription(request.description().trim());
    league.setGamePlatform(request.gamePlatform());
    league.setVisibility(request.visibility());

    String invitationCode = activeCodeForLeague(leagueId);
    return toResponse(leagueRepository.save(league), role, invitationCode);
  }

  @Transactional
  public void delete(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = requiredRole(leagueId, user.getId(), locale);
    if (role != LeagueRole.OWNER) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.delete", null, locale));
    }

    League league = findLeague(leagueId, locale);
    leagueRepository.delete(league);
  }

  @Transactional
  public LeagueDtos.LeagueJoinResponse join(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    League league = findLeague(leagueId, locale);

    if (leagueMemberRepository.findByLeagueIdAndUserId(leagueId, user.getId()).isPresent()) {
      return new LeagueDtos.LeagueJoinResponse(
        leagueId,
        MembershipStatus.APPROVED,
        messageSource.getMessage("league.join.already.member", null, locale)
      );
    }

    if (league.getVisibility() == LeagueVisibility.PRIVATE) {
      throw new FunctionalException(messageSource.getMessage("league.join.private.use.code", null, locale));
    }

    LeagueJoinRequest joinRequest = leagueJoinRequestRepository.findByLeagueIdAndUserId(leagueId, user.getId())
      .orElse(LeagueJoinRequest.builder().league(league).user(user).build());
    joinRequest.setStatus(MembershipStatus.PENDING);
    leagueJoinRequestRepository.save(joinRequest);

    return new LeagueDtos.LeagueJoinResponse(
      leagueId,
      MembershipStatus.PENDING,
      messageSource.getMessage("league.join.request.created", null, locale)
    );
  }

  @Transactional
  public LeagueDtos.LeagueJoinResponse joinByCode(LeagueDtos.JoinByCodeRequest request, String email, Locale locale) {
    LeagueInvitationCode invitationCode = leagueInvitationCodeRepository.findByCodeAndActiveTrue(request.invitationCode().trim())
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("league.join.code.invalid", null, locale)));

    League league = invitationCode.getLeague();
    User user = findUserByEmail(email, locale);

    if (leagueMemberRepository.findByLeagueIdAndUserId(league.getId(), user.getId()).isPresent()) {
      return new LeagueDtos.LeagueJoinResponse(
        league.getId(),
        MembershipStatus.APPROVED,
        messageSource.getMessage("league.join.already.member", null, locale)
      );
    }

    LeagueJoinRequest joinRequest = leagueJoinRequestRepository.findByLeagueIdAndUserId(league.getId(), user.getId())
      .orElse(LeagueJoinRequest.builder().league(league).user(user).build());
    joinRequest.setStatus(MembershipStatus.APPROVED);
    leagueJoinRequestRepository.save(joinRequest);

    leagueMemberRepository.save(LeagueMember.builder()
      .league(league)
      .user(user)
      .role(LeagueRole.DRIVER)
      .build());

    return new LeagueDtos.LeagueJoinResponse(
      league.getId(),
      MembershipStatus.APPROVED,
      messageSource.getMessage("league.join.code.accepted", null, locale)
    );
  }

  @Transactional(readOnly = true)
  public List<LeagueDtos.LeagueMemberResponse> members(Long leagueId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = requiredRole(leagueId, user.getId(), locale);
    if (!canManageLeague(role)) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.members", null, locale));
    }

    List<LeagueDtos.LeagueMemberResponse> approvedMembers = leagueMemberRepository.findByLeagueId(leagueId).stream()
      .map(member -> new LeagueDtos.LeagueMemberResponse(
        member.getId(),
        member.getUser().getId(),
        member.getUser().getUsername(),
        member.getUser().getEmail(),
        member.getRole(),
        MembershipStatus.APPROVED,
        null
      ))
      .toList();

    List<LeagueDtos.LeagueMemberResponse> requests = leagueJoinRequestRepository.findByLeagueIdOrderByCreatedAtDesc(leagueId).stream()
      .filter(request -> request.getStatus() != MembershipStatus.APPROVED)
      .map(request -> new LeagueDtos.LeagueMemberResponse(
        request.getId(),
        request.getUser().getId(),
        request.getUser().getUsername(),
        request.getUser().getEmail(),
        LeagueRole.DRIVER,
        request.getStatus(),
        request.getCreatedAt()
      ))
      .toList();

    return java.util.stream.Stream.concat(approvedMembers.stream(), requests.stream()).toList();
  }

  @Transactional
  public LeagueDtos.LeagueMemberResponse approveMember(Long leagueId, Long memberId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = requiredRole(leagueId, user.getId(), locale);
    if (!canManageLeague(role)) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.members", null, locale));
    }

    LeagueJoinRequest request = leagueJoinRequestRepository.findByIdAndLeagueId(memberId, leagueId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("league.member.notfound", null, locale)));

    request.setStatus(MembershipStatus.APPROVED);
    leagueJoinRequestRepository.save(request);

    if (leagueMemberRepository.findByLeagueIdAndUserId(leagueId, request.getUser().getId()).isEmpty()) {
      leagueMemberRepository.save(LeagueMember.builder()
        .league(request.getLeague())
        .user(request.getUser())
        .role(LeagueRole.DRIVER)
        .build());
    }

    return new LeagueDtos.LeagueMemberResponse(
      request.getId(),
      request.getUser().getId(),
      request.getUser().getUsername(),
      request.getUser().getEmail(),
      LeagueRole.DRIVER,
      MembershipStatus.APPROVED,
      request.getCreatedAt()
    );
  }

  @Transactional
  public LeagueDtos.LeagueMemberResponse rejectMember(Long leagueId, Long memberId, String email, Locale locale) {
    User user = findUserByEmail(email, locale);
    LeagueRole role = requiredRole(leagueId, user.getId(), locale);
    if (!canManageLeague(role)) {
      throw new UnauthorizedException(messageSource.getMessage("league.forbidden.members", null, locale));
    }

    LeagueJoinRequest request = leagueJoinRequestRepository.findByIdAndLeagueId(memberId, leagueId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("league.member.notfound", null, locale)));

    request.setStatus(MembershipStatus.REJECTED);
    leagueJoinRequestRepository.save(request);

    return new LeagueDtos.LeagueMemberResponse(
      request.getId(),
      request.getUser().getId(),
      request.getUser().getUsername(),
      request.getUser().getEmail(),
      LeagueRole.DRIVER,
      MembershipStatus.REJECTED,
      request.getCreatedAt()
    );
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private League findLeague(Long leagueId, Locale locale) {
    return leagueRepository.findById(leagueId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("league.notfound", null, locale)));
  }

  private LeagueRole requiredRole(Long leagueId, Long userId, Locale locale) {
    return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .map(LeagueMember::getRole)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
  }

  private LeagueRole roleIfMember(Long leagueId, Long userId) {
    return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .map(LeagueMember::getRole)
      .orElse(null);
  }

  private String createInvitationCode(League league) {
    String code = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase(Locale.ROOT);
    leagueInvitationCodeRepository.save(LeagueInvitationCode.builder()
      .league(league)
      .code(code)
      .active(true)
      .build());
    return code;
  }

  private String activeCodeForLeague(Long leagueId) {
    return leagueInvitationCodeRepository.findTopByLeagueIdAndActiveTrueOrderByCreatedAtDesc(leagueId)
      .map(LeagueInvitationCode::getCode)
      .orElse(null);
  }

  private boolean canManageLeague(LeagueRole role) {
    return role == LeagueRole.OWNER || role == LeagueRole.ADMIN;
  }

  private LeagueDtos.LeagueResponse toResponse(League league, LeagueRole role, String invitationCode) {
    return new LeagueDtos.LeagueResponse(
      league.getId(),
      league.getName(),
      league.getDescription(),
      league.getGamePlatform(),
      league.getVisibility(),
      role,
      invitationCode
    );
  }
}
