package com.racecontrol.pointsystem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointSystemRepository extends JpaRepository<PointSystem, Long> {
  Optional<PointSystem> findBySeasonId(Long seasonId);
}
