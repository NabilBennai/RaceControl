package com.racecontrol.result;

import com.racecontrol.race.Race;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "race_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResult {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "race_id", nullable = false, unique = true)
  private Race race;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "raceResult", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<RaceResultLine> lines = new ArrayList<>();

  @PrePersist
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
