package com.racecontrol.auth;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.security.JwtService;
import com.racecontrol.user.Role;
import com.racecontrol.user.User;
import com.racecontrol.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {
  private static final String BLACKLIST_PREFIX = "bl:";

  private final UserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final StringRedisTemplate redis;
  private final MessageSource ms;

  public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request, Locale locale) {
    if (repo.existsByEmail(request.email())) {
      throw new FunctionalException(ms.getMessage("auth.email.exists", null, locale));
    }

    User user = repo.save(User.builder()
      .email(request.email())
      .password(encoder.encode(request.password()))
      .role(Role.USER)
      .build());

    return tokens(user.getEmail());
  }

  public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request, Locale locale) {
    User user = repo.findByEmail(request.email())
      .orElseThrow(() -> new UnauthorizedException(ms.getMessage("auth.invalid", null, locale)));

    if (!encoder.matches(request.password(), user.getPassword())) {
      throw new UnauthorizedException(ms.getMessage("auth.invalid", null, locale));
    }

    return tokens(user.getEmail());
  }

  public AuthDtos.AuthResponse refresh(String refreshToken, Locale locale) {
    if (Boolean.TRUE.equals(redis.hasKey(BLACKLIST_PREFIX + refreshToken))) {
      throw new UnauthorizedException(ms.getMessage("auth.refresh.invalid", null, locale));
    }

    String subject;
    try {
      subject = jwt.subject(refreshToken);
    } catch (Exception ex) {
      throw new UnauthorizedException(ms.getMessage("auth.refresh.invalid", null, locale));
    }

    return tokens(subject);
  }

  public void logout(String refreshToken) {
    redis.opsForValue().set(BLACKLIST_PREFIX + refreshToken, "1", Duration.ofDays(7));
  }

  private AuthDtos.AuthResponse tokens(String subject) {
    return new AuthDtos.AuthResponse(jwt.accessToken(subject), jwt.refreshToken(subject), "Bearer");
  }
}
