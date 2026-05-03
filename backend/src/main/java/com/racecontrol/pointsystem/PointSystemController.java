package com.racecontrol.pointsystem;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointSystemController {
  private final PointSystemService pointSystemService;

  @PostMapping("/seasons/{seasonId}/point-system")
  PointSystemDtos.PointSystemResponse create(
    @PathVariable Long seasonId,
    @Valid @RequestBody PointSystemDtos.PointSystemRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return pointSystemService.create(seasonId, request, authentication.getName(), locale);
  }

  @GetMapping("/seasons/{seasonId}/point-system")
  PointSystemDtos.PointSystemResponse getBySeason(@PathVariable Long seasonId, Authentication authentication, Locale locale) {
    return pointSystemService.getBySeason(seasonId, authentication.getName(), locale);
  }

  @PutMapping("/point-systems/{pointSystemId}")
  PointSystemDtos.PointSystemResponse update(
    @PathVariable Long pointSystemId,
    @Valid @RequestBody PointSystemDtos.PointSystemRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return pointSystemService.update(pointSystemId, request, authentication.getName(), locale);
  }
}
