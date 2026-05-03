package com.racecontrol.standing;

import com.racecontrol.season.Season;
import com.racecontrol.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "driver_standings",
  uniqueConstraints = @UniqueConstraint(name = "uk_driver_standings_season_user", columnNames = {"season_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverStanding {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", nullable = false)
  private Season season;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Integer points;

  @Column(nullable = false)
  private Integer wins;

  @Column(nullable = false)
  private Integer podiums;

  @Column(nullable = false)
  private Integer poles;

  @Column(nullable = false)
  private Integer fastestLaps;
}
