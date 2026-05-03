package com.racecontrol.standing;

import com.racecontrol.season.Season;
import com.racecontrol.team.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "team_standings",
  uniqueConstraints = @UniqueConstraint(name = "uk_team_standings_season_team", columnNames = {"season_id", "team_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamStanding {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "season_id", nullable = false)
  private Season season;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

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
