package com.example.template.auth;

import com.example.template.exception.FunctionalException;
import com.example.template.exception.UnauthorizedException;
import com.example.template.security.JwtService;
import com.example.template.user.Role;
import com.example.template.user.User;
import com.example.template.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtService jwtService;
  @Mock
  private StringRedisTemplate redis;
  @Mock
  private MessageSource messageSource;
  @Mock
  private ValueOperations<String, String> valueOperations;

  @InjectMocks
  private AuthService authService;

  @Test
  void register_shouldFailWhenEmailExists() {
    when(messageSource.getMessage("auth.email.exists", null, Locale.FRENCH)).thenReturn("email existe");
    when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

    assertThatThrownBy(() -> authService.register(new AuthDtos.RegisterRequest("user@example.com", "password123"), Locale.FRENCH))
      .isInstanceOf(FunctionalException.class)
      .hasMessage("email existe");
  }

  @Test
  void login_shouldReturnTokensWhenCredentialsValid() {
    when(jwtService.accessToken("user@example.com")).thenReturn("access");
    when(jwtService.refreshToken("user@example.com")).thenReturn("refresh");

    User user = User.builder().email("user@example.com").password("hash").role(Role.USER).build();
    when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

    AuthDtos.AuthResponse response = authService.login(new AuthDtos.LoginRequest("user@example.com", "password123"), Locale.FRENCH);

    assertThat(response.accessToken()).isEqualTo("access");
    assertThat(response.refreshToken()).isEqualTo("refresh");
  }

  @Test
  void refresh_shouldFailWhenBlacklisted() {
    when(messageSource.getMessage("auth.refresh.invalid", null, Locale.FRENCH)).thenReturn("refresh invalide");
    when(redis.hasKey("bl:bad-refresh")).thenReturn(true);

    assertThatThrownBy(() -> authService.refresh("bad-refresh", Locale.FRENCH))
      .isInstanceOf(UnauthorizedException.class)
      .hasMessage("refresh invalide");
  }

  @Test
  void logout_shouldBlacklistRefreshToken() {
    when(redis.opsForValue()).thenReturn(valueOperations);

    authService.logout("refresh-token");

    verify(valueOperations).set(any(), any(), any());
  }
}
