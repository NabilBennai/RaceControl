package com.racecontrol.result;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/races/{raceId}/results")
public class ResultController {
  private final ResultService resultService;

  @PostMapping
  ResultDtos.RaceResultResponse create(
    @PathVariable Long raceId,
    @Valid @RequestBody ResultDtos.RaceResultRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return resultService.create(raceId, request, authentication.getName(), locale);
  }

  @GetMapping
  ResultDtos.RaceResultResponse get(@PathVariable Long raceId, Authentication authentication, Locale locale) {
    return resultService.get(raceId, authentication.getName(), locale);
  }

  @PutMapping
  ResultDtos.RaceResultResponse update(
    @PathVariable Long raceId,
    @Valid @RequestBody ResultDtos.RaceResultRequest request,
    Authentication authentication,
    Locale locale
  ) {
    return resultService.update(raceId, request, authentication.getName(), locale);
  }

  @DeleteMapping
  void delete(@PathVariable Long raceId, Authentication authentication, Locale locale) {
    resultService.delete(raceId, authentication.getName(), locale);
  }
}
