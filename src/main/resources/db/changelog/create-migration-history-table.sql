CREATE TABLE IF NOT EXISTS migration_history (
    id SERIAL PRIMARY KEY,
    version VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    rollback VARCHAR(255) NOT NULL,
    applied_at TIMESTAMP
);