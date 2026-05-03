package com.racecontrol.imports;

import com.racecontrol.result.ResultStatus;

import java.util.List;

public class ResultImportDtos {
  public record ImportLinePreview(
    Integer lineNumber,
    String userEmail,
    Integer position,
    String totalTime,
    String bestLap,
    boolean polePosition,
    Integer incidents,
    ResultStatus status
  ) {
  }

  public record ImportErrorResponse(
    Integer lineNumber,
    String message
  ) {
  }

  public record ImportResponse(
    Long id,
    Long raceId,
    String originalFileName,
    ImportStatus status,
    List<ImportLinePreview> preview,
    List<ImportErrorResponse> errors
  ) {
  }
}
