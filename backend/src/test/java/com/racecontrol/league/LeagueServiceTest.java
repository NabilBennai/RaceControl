package com.racecontrol.league;

import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.user.User;
import com.racecontrol.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

  @Mock
  private LeagueRepository leagueRepository;
  @Mock
  private LeagueMemberRepository leagueMemberRepository;
  @Mock
  private LeagueJoinRequestRepository leagueJoinRequestRepository;
  @Mock
  private LeagueInvitationCodeRepository leagueInvitationCodeRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageSource messageSource;

  @InjectMocks
  private LeagueService leagueService;

  @Test
  void create_shouldCreateLeagueAndOwnerMembership() {
    User user = User.builder().id(11L).email("pilot@example.com").username("pilot").build();
    League savedLeague = League.builder().id(3L).name("GT France").description("Championnat GT3").gamePlatform(GamePlatform.ACC).visibility(LeagueVisibility.PUBLIC).build();
    LeagueMember owner = LeagueMember.builder().id(20L).league(savedLeague).user(user).role(LeagueRole.OWNER).build();

    when(userRepository.findByEmail("pilot@example.com")).thenReturn(Optional.of(user));
    when(leagueRepository.save(any(League.class))).thenReturn(savedLeague);
    when(leagueMemberRepository.save(any(LeagueMember.class))).thenReturn(owner);
    when(leagueInvitationCodeRepository.save(any(LeagueInvitationCode.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    LeagueDtos.LeagueResponse response = leagueService.create(
      new LeagueDtos.LeagueRequest("GT France", "Championnat GT3 pour pilotes francophones", GamePlatform.ACC, LeagueVisibility.PUBLIC),
      "pilot@example.com",
      Locale.FRENCH
    );

    assertThat(response.id()).isEqualTo(3L);
    assertThat(response.myRole()).isEqualTo(LeagueRole.OWNER);
    verify(leagueMemberRepository).save(any(LeagueMember.class));
  }

  @Test
  void update_shouldFailForDriver() {
    User user = User.builder().id(7L).email("driver@example.com").username("driver").build();

    when(userRepository.findByEmail("driver@example.com")).thenReturn(Optional.of(user));
    when(leagueMemberRepository.findByLeagueIdAndUserId(1L, 7L))
      .thenReturn(Optional.of(LeagueMember.builder().role(LeagueRole.DRIVER).build()));
    when(messageSource.getMessage("league.forbidden.update", null, Locale.FRENCH))
      .thenReturn("forbidden update");

    assertThatThrownBy(() -> leagueService.update(
      1L,
      new LeagueDtos.LeagueRequest("Nom", "Description assez longue", GamePlatform.F1, LeagueVisibility.PRIVATE),
      "driver@example.com",
      Locale.FRENCH
    )).isInstanceOf(UnauthorizedException.class).hasMessage("forbidden update");
  }

  @Test
  void delete_shouldFailForAdmin() {
    User user = User.builder().id(8L).email("admin@example.com").username("admin").build();

    when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
    when(leagueMemberRepository.findByLeagueIdAndUserId(4L, 8L))
      .thenReturn(Optional.of(LeagueMember.builder().role(LeagueRole.ADMIN).build()));
    when(messageSource.getMessage("league.forbidden.delete", null, Locale.FRENCH))
      .thenReturn("forbidden delete");

    assertThatThrownBy(() -> leagueService.delete(4L, "admin@example.com", Locale.FRENCH))
      .isInstanceOf(UnauthorizedException.class)
      .hasMessage("forbidden delete");
  }
}
