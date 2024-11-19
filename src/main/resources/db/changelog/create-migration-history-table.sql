CREATE TABLE IF NOT EXISTS migration_history (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL
);