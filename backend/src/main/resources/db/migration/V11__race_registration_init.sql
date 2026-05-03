CREATE TABLE IF NOT EXISTS race_registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  race_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  car VARCHAR(120) NOT NULL,
  number VARCHAR(16) NOT NULL,
  team_id BIGINT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_race_registrations_race FOREIGN KEY (race_id) REFERENCES races(id) ON DELETE CASCADE,
  CONSTRAINT fk_race_registrations_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_race_registrations_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
  CONSTRAINT uk_race_registrations_race_user UNIQUE (race_id, user_id)
);

CREATE INDEX idx_race_registrations_race ON race_registrations(race_id);
CREATE INDEX idx_race_registrations_user ON race_registrations(user_id);
