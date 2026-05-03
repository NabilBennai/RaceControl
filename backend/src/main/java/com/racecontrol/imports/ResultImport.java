package com.racecontrol.imports;

import com.racecontrol.race.Race;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "result_imports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultImport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "race_id", nullable = false)
  private Race race;

  @Column(nullable = false, length = 255)
  private String originalFileName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ImportStatus status;

  @JdbcTypeCode(SqlTypes.LONGVARCHAR)
  @Column(nullable = false, columnDefinition = "longtext")
  private String rawCsv;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "resultImport", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ResultImportError> errors = new ArrayList<>();

  @PrePersist
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
