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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonServiceTest {
  @Mock
  private SeasonRepository seasonRepository;
  @Mock
  private LeagueRepository leagueRepository;
  @Mock
  private LeagueMemberRepository leagueMemberRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageSource messageSource;

  @InjectMocks
  private SeasonService seasonService;

  @Test
  void create_shouldFailForDriverRole() {
    User user = User.builder().id(1L).email("driver@example.com").build();
    when(userRepository.findByEmail("driver@example.com")).thenReturn(Optional.of(user));
    when(leagueMemberRepository.findByLeagueIdAndUserId(10L, 1L))
      .thenReturn(Optional.of(LeagueMember.builder().role(LeagueRole.DRIVER).build()));
    when(messageSource.getMessage("season.forbidden.manage", null, Locale.FRENCH)).thenReturn("forbidden");

    assertThatThrownBy(() -> seasonService.create(
      10L,
      new SeasonDtos.SeasonRequest("S1", LocalDate.parse("2026-01-01"), LocalDate.parse("2026-12-31")),
      "driver@example.com",
      Locale.FRENCH
    )).isInstanceOf(UnauthorizedException.class).hasMessage("forbidden");
  }

  @Test
  void activate_shouldFailWhenAnotherActiveSeasonExists() {
    User user = User.builder().id(2L).email("owner@example.com").build();
    League league = League.builder().id(11L).build();
    Season season = Season.builder().id(20L).league(league).status(SeasonStatus.DRAFT).build();

    when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(user));
    when(seasonRepository.findById(20L)).thenReturn(Optional.of(season));
    when(leagueMemberRepository.findByLeagueIdAndUserId(11L, 2L))
      .thenReturn(Optional.of(LeagueMember.builder().role(LeagueRole.OWNER).build()));
    when(seasonRepository.existsByLeagueIdAndStatus(11L, SeasonStatus.ACTIVE)).thenReturn(true);
    when(messageSource.getMessage("season.active.exists", null, Locale.FRENCH)).thenReturn("active exists");

    assertThatThrownBy(() -> seasonService.activate(20L, "owner@example.com", Locale.FRENCH))
      .isInstanceOf(FunctionalException.class)
      .hasMessage("active exists");
  }
}
