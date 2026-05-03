CREATE TABLE IF NOT EXISTS race_results (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  race_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_race_results_race FOREIGN KEY (race_id) REFERENCES races(id) ON DELETE CASCADE,
  CONSTRAINT uk_race_results_race UNIQUE (race_id)
);

CREATE TABLE IF NOT EXISTS race_result_lines (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  race_result_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  position INT NOT NULL,
  total_time VARCHAR(32) NULL,
  best_lap VARCHAR(32) NULL,
  pole_position BOOLEAN NOT NULL DEFAULT FALSE,
  incidents INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_result_lines_result FOREIGN KEY (race_result_id) REFERENCES race_results(id) ON DELETE CASCADE,
  CONSTRAINT fk_result_lines_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_result_lines_result ON race_result_lines(race_result_id);
CREATE INDEX idx_result_lines_user ON race_result_lines(user_id);
