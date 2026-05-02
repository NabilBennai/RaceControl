package com.racecontrol.user;

import com.racecontrol.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
  private final UserRepository userRepository;
  private final MessageSource messageSource;

  @GetMapping("/me")
  public CurrentUserResponse me(Authentication auth, Locale locale) {
    User user = userRepository.findByEmail(auth.getName())
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));

    return new CurrentUserResponse(user.getUsername(), user.getEmail(), user.getRole().name());
  }
}
