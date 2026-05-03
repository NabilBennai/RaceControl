package com.racecontrol.league;

import com.racecontrol.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "league_members",
  uniqueConstraints = @UniqueConstraint(name = "uk_league_members_league_user", columnNames = {"league_id", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueMember {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "league_id", nullable = false)
  private League league;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private LeagueRole role;
}
