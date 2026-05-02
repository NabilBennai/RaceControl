package com.racecontrol.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService svc;

  @PostMapping("/register")
  AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request, Locale locale) {
    return svc.register(request, locale);
  }

  @PostMapping("/login")
  AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request, Locale locale) {
    return svc.login(request, locale);
  }

  @PostMapping("/refresh")
  AuthDtos.AuthResponse refresh(@Valid @RequestBody AuthDtos.RefreshRequest request, Locale locale) {
    return svc.refresh(request.refreshToken(), locale);
  }

  @PostMapping("/logout")
  void logout(@Valid @RequestBody AuthDtos.RefreshRequest request) {
    svc.logout(request.refreshToken());
  }
}
