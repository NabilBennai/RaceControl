CREATE TABLE IF NOT EXISTS races (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  season_id BIGINT NOT NULL,
  track VARCHAR(120) NOT NULL,
  race_date DATE NOT NULL,
  race_time TIME NOT NULL,
  format VARCHAR(120) NOT NULL,
  laps INT NULL,
  duration_minutes INT NULL,
  weather VARCHAR(40) NOT NULL,
  car_category VARCHAR(120) NOT NULL,
  status VARCHAR(30) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_races_season FOREIGN KEY (season_id) REFERENCES seasons(id) ON DELETE CASCADE
);

CREATE INDEX idx_races_season ON races(season_id);
CREATE INDEX idx_races_schedule ON races(race_date, race_time);
