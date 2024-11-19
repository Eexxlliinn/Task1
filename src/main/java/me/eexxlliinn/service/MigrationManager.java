package me.eexxlliinn.service;

import java.sql.SQLException;
import java.util.Set;

public interface MigrationManager {

    void runMigrations(String path) throws Exception;
    Set<String> getAppliedMigrations();
    void recordMigration(String appliedMigration) throws SQLException;
}
