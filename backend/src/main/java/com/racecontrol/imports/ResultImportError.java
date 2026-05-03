package com.racecontrol.imports;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "result_import_errors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultImportError {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "result_import_id", nullable = false)
  private ResultImport resultImport;

  @Column(nullable = false)
  private Integer lineNumber;

  @Column(nullable = false, length = 500)
  private String message;
}
