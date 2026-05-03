package com.racecontrol.league;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "leagues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class League {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(nullable = false, length = 2000)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private GamePlatform gamePlatform;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LeagueVisibility visibility;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<LeagueMember> members = new ArrayList<>();

  @PrePersist
  void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
