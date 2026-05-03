package com.racecontrol.race;

import com.racecontrol.season.Season;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "races")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", nullable = false)
  private Season season;

  @Column(nullable = false, length = 120)
  private String track;

  @Column(nullable = false)
  private LocalDate raceDate;

  @Column(nullable = false)
  private LocalTime raceTime;

  @Column(nullable = false, length = 120)
  private String format;

  @Column
  private Integer laps;

  @Column
  private Integer durationMinutes;

  @Column(nullable = false, length = 40)
  private String weather;

  @Column(nullable = false, length = 120)
  private String carCategory;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private RaceStatus status;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
