package com.racecontrol.imports;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ResultImportController {
  private final ResultImportService importService;

  @PostMapping("/races/{raceId}/results/import")
  ResultImportDtos.ImportResponse upload(
    @PathVariable Long raceId,
    @RequestParam("file") MultipartFile file,
    Authentication authentication,
    Locale locale
  ) {
    return importService.upload(raceId, file, authentication.getName(), locale);
  }

  @GetMapping("/imports/{importId}")
  ResultImportDtos.ImportResponse get(@PathVariable Long importId, Authentication authentication, Locale locale) {
    return importService.get(importId, authentication.getName(), locale);
  }

  @PostMapping("/imports/{importId}/confirm")
  ResultImportDtos.ImportResponse confirm(@PathVariable Long importId, Authentication authentication, Locale locale) {
    return importService.confirm(importId, authentication.getName(), locale);
  }

  @GetMapping("/results/template.csv")
  ResponseEntity<byte[]> template() {
    byte[] content = importService.templateCsv().getBytes(StandardCharsets.UTF_8);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"race-results-template.csv\"")
      .contentType(MediaType.parseMediaType("text/csv"))
      .body(content);
  }
}
