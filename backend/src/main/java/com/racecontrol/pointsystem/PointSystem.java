package com.racecontrol.pointsystem;

import com.racecontrol.season.Season;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "point_systems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointSystem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", nullable = false, unique = true)
  private Season season;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "pointSystem", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<PointRule> rules = new ArrayList<>();

  @PrePersist
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
