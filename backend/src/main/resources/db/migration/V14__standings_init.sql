CREATE TABLE IF NOT EXISTS driver_standings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  season_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  points INT NOT NULL DEFAULT 0,
  wins INT NOT NULL DEFAULT 0,
  podiums INT NOT NULL DEFAULT 0,
  poles INT NOT NULL DEFAULT 0,
  fastest_laps INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_driver_standings_season FOREIGN KEY (season_id) REFERENCES seasons(id) ON DELETE CASCADE,
  CONSTRAINT fk_driver_standings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT uk_driver_standings_season_user UNIQUE (season_id, user_id)
);

CREATE TABLE IF NOT EXISTS team_standings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  season_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  points INT NOT NULL DEFAULT 0,
  wins INT NOT NULL DEFAULT 0,
  podiums INT NOT NULL DEFAULT 0,
  poles INT NOT NULL DEFAULT 0,
  fastest_laps INT NOT NULL DEFAULT 0,
  CONSTRAINT fk_team_standings_season FOREIGN KEY (season_id) REFERENCES seasons(id) ON DELETE CASCADE,
  CONSTRAINT fk_team_standings_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
  CONSTRAINT uk_team_standings_season_team UNIQUE (season_id, team_id)
);

CREATE INDEX idx_driver_standings_season ON driver_standings(season_id);
CREATE INDEX idx_team_standings_season ON team_standings(season_id);
