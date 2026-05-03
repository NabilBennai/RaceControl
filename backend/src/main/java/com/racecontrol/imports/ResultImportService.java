package com.racecontrol.imports;

import com.racecontrol.exception.FunctionalException;
import com.racecontrol.exception.UnauthorizedException;
import com.racecontrol.league.LeagueMember;
import com.racecontrol.league.LeagueMemberRepository;
import com.racecontrol.league.LeagueRole;
import com.racecontrol.race.Race;
import com.racecontrol.race.RaceRepository;
import com.racecontrol.result.ResultDtos;
import com.racecontrol.result.ResultService;
import com.racecontrol.result.ResultStatus;
import com.racecontrol.user.User;
import com.racecontrol.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResultImportService {
  private final ResultImportRepository importRepository;
  private final RaceRepository raceRepository;
  private final UserRepository userRepository;
  private final LeagueMemberRepository leagueMemberRepository;
  private final ResultService resultService;
  private final MessageSource messageSource;

  @Transactional
  public ResultImportDtos.ImportResponse upload(Long raceId, MultipartFile file, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    Race race = findRace(raceId, locale);
    ensureCanManage(race.getSeason().getLeague().getId(), actor.getId(), locale);

    String rawCsv;
    try {
      rawCsv = new String(file.getBytes(), StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new FunctionalException(messageSource.getMessage("import.read.failed", null, locale));
    }

    ParseOutcome parsed = parse(rawCsv, locale);
    ImportStatus status = parsed.errors.isEmpty() ? ImportStatus.PENDING : ImportStatus.FAILED;

    ResultImport resultImport = ResultImport.builder()
      .race(race)
      .originalFileName(file.getOriginalFilename() == null ? "results.csv" : file.getOriginalFilename())
      .status(status)
      .rawCsv(rawCsv)
      .build();
    parsed.errors.forEach(err -> resultImport.getErrors().add(ResultImportError.builder()
      .resultImport(resultImport)
      .lineNumber(err.lineNumber())
      .message(err.message())
      .build()));

    ResultImport saved = importRepository.save(resultImport);
    return new ResultImportDtos.ImportResponse(
      saved.getId(), raceId, saved.getOriginalFileName(), saved.getStatus(), parsed.preview, parsed.errors
    );
  }

  @Transactional(readOnly = true)
  public ResultImportDtos.ImportResponse get(Long importId, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    ResultImport resultImport = findImport(importId, locale);
    ensureCanManage(resultImport.getRace().getSeason().getLeague().getId(), actor.getId(), locale);
    ParseOutcome parsed = parse(resultImport.getRawCsv(), locale);
    List<ResultImportDtos.ImportErrorResponse> errors = resultImport.getErrors().stream()
      .map(e -> new ResultImportDtos.ImportErrorResponse(e.getLineNumber(), e.getMessage())).toList();
    return new ResultImportDtos.ImportResponse(
      resultImport.getId(), resultImport.getRace().getId(), resultImport.getOriginalFileName(), resultImport.getStatus(), parsed.preview, errors
    );
  }

  @Transactional
  public ResultImportDtos.ImportResponse confirm(Long importId, String email, Locale locale) {
    User actor = findUserByEmail(email, locale);
    ResultImport resultImport = findImport(importId, locale);
    ensureCanManage(resultImport.getRace().getSeason().getLeague().getId(), actor.getId(), locale);

    ParseOutcome parsed = parse(resultImport.getRawCsv(), locale);
    if (!parsed.errors.isEmpty()) {
      resultImport.setStatus(ImportStatus.FAILED);
      resultImport.getErrors().clear();
      parsed.errors.forEach(err -> resultImport.getErrors().add(ResultImportError.builder()
        .resultImport(resultImport).lineNumber(err.lineNumber()).message(err.message()).build()));
      importRepository.save(resultImport);
      return new ResultImportDtos.ImportResponse(resultImport.getId(), resultImport.getRace().getId(), resultImport.getOriginalFileName(), resultImport.getStatus(), parsed.preview, parsed.errors);
    }

    List<ResultDtos.RaceResultLineRequest> lines = new ArrayList<>();
    for (ResultImportDtos.ImportLinePreview line : parsed.preview) {
      Long userId = userRepository.findByEmail(line.userEmail()).map(User::getId)
        .orElseThrow(() -> new FunctionalException(messageSource.getMessage("import.user.notfound", null, locale)));
      lines.add(new ResultDtos.RaceResultLineRequest(
        userId, line.position(), line.totalTime(), line.bestLap(), line.polePosition(), line.incidents(), line.status()
      ));
    }

    ResultDtos.RaceResultRequest request = new ResultDtos.RaceResultRequest(lines);
    try {
      resultService.update(resultImport.getRace().getId(), request, actor.getEmail(), locale);
    } catch (FunctionalException ex) {
      resultService.create(resultImport.getRace().getId(), request, actor.getEmail(), locale);
    }

    resultImport.setStatus(ImportStatus.CONFIRMED);
    importRepository.save(resultImport);
    return new ResultImportDtos.ImportResponse(
      resultImport.getId(), resultImport.getRace().getId(), resultImport.getOriginalFileName(), resultImport.getStatus(), parsed.preview, List.of()
    );
  }

  public String templateCsv() {
    return "user_email,position,total_time,best_lap,pole_position,incidents,status\n"
      + "driver1@mail.com,1,01:15:33.210,01:31.122,true,0,CLASSIFIED\n"
      + "driver2@mail.com,2,01:15:40.332,01:31.450,false,1,CLASSIFIED\n";
  }

  private ParseOutcome parse(String rawCsv, Locale locale) {
    List<ResultImportDtos.ImportLinePreview> preview = new ArrayList<>();
    List<ResultImportDtos.ImportErrorResponse> errors = new ArrayList<>();
    String[] lines = rawCsv.replace("\r\n", "\n").split("\n");
    if (lines.length == 0 || lines[0].isBlank()) {
      errors.add(new ResultImportDtos.ImportErrorResponse(1, messageSource.getMessage("import.header.invalid", null, locale)));
      return new ParseOutcome(preview, errors);
    }
    for (int i = 1; i < lines.length; i++) {
      String row = lines[i].trim();
      if (row.isBlank()) {
        continue;
      }
      String[] cols = row.split(",", -1);
      if (cols.length < 7) {
        errors.add(new ResultImportDtos.ImportErrorResponse(i + 1, messageSource.getMessage("import.row.invalid", null, locale)));
        continue;
      }
      try {
        String email = cols[0].trim();
        Integer position = Integer.parseInt(cols[1].trim());
        String totalTime = cols[2].trim();
        String bestLap = cols[3].trim();
        boolean pole = Boolean.parseBoolean(cols[4].trim());
        Integer incidents = Integer.parseInt(cols[5].trim());
        ResultStatus status = ResultStatus.valueOf(cols[6].trim());
        preview.add(new ResultImportDtos.ImportLinePreview(i + 1, email, position, emptyToNull(totalTime), emptyToNull(bestLap), pole, incidents, status));
      } catch (Exception ex) {
        errors.add(new ResultImportDtos.ImportErrorResponse(i + 1, messageSource.getMessage("import.row.invalid", null, locale)));
      }
    }
    return new ParseOutcome(preview, errors);
  }

  private String emptyToNull(String value) {
    return value == null || value.isBlank() ? null : value;
  }

  private User findUserByEmail(String email, Locale locale) {
    return userRepository.findByEmail(email)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("auth.invalid", null, locale)));
  }

  private Race findRace(Long raceId, Locale locale) {
    return raceRepository.findById(raceId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("race.notfound", null, locale)));
  }

  private ResultImport findImport(Long importId, Locale locale) {
    return importRepository.findById(importId)
      .orElseThrow(() -> new FunctionalException(messageSource.getMessage("import.notfound", null, locale)));
  }

  private void ensureCanManage(Long leagueId, Long userId, Locale locale) {
    LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
      .orElseThrow(() -> new UnauthorizedException(messageSource.getMessage("league.access.denied", null, locale)));
    if (!(member.getRole() == LeagueRole.OWNER || member.getRole() == LeagueRole.ADMIN || member.getRole() == LeagueRole.STEWARD)) {
      throw new UnauthorizedException(messageSource.getMessage("result.forbidden.manage", null, locale));
    }
  }

  private record ParseOutcome(
    List<ResultImportDtos.ImportLinePreview> preview,
    List<ResultImportDtos.ImportErrorResponse> errors
  ) {
  }
}
