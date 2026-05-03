package com.racecontrol.result;

import com.racecontrol.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "race_result_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResultLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "race_result_id", nullable = false)
  private RaceResult raceResult;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private Integer position;

  @Column(length = 32)
  private String totalTime;

  @Column(length = 32)
  private String bestLap;

  @Column(nullable = false)
  private boolean polePosition;

  @Column(nullable = false)
  private Integer incidents;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ResultStatus status;
}
