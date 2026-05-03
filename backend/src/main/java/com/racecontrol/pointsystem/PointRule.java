package com.racecontrol.pointsystem;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "point_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointRule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "point_system_id", nullable = false)
  private PointSystem pointSystem;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private PointRuleType type;

  @Column(name = "position_rank")
  private Integer positionRank;

  @Column(nullable = false)
  private Integer points;
}
