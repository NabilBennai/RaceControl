CREATE TABLE IF NOT EXISTS result_imports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  race_id BIGINT NOT NULL,
  original_file_name VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL,
  raw_csv LONGTEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_result_imports_race FOREIGN KEY (race_id) REFERENCES races(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS result_import_errors (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  result_import_id BIGINT NOT NULL,
  line_number INT NOT NULL,
  message VARCHAR(500) NOT NULL,
  CONSTRAINT fk_result_import_errors_import FOREIGN KEY (result_import_id) REFERENCES result_imports(id) ON DELETE CASCADE
);

CREATE INDEX idx_result_imports_race ON result_imports(race_id);
CREATE INDEX idx_result_import_errors_import ON result_import_errors(result_import_id);
