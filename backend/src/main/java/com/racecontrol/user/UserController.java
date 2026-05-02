package com.racecontrol.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

  @GetMapping("/me")
  public com.racecontrol.user.MeResponse me(Authentication auth) {
    String role = auth.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_USER");
    return new com.racecontrol.user.MeResponse(auth.getName(), role);
  }
}
