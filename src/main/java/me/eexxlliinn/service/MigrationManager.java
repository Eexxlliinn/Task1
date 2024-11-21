package me.eexxlliinn.service;

import me.eexxlliinn.entities.Migration;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MigrationManager {

    void runMigrationsToVersion(String changelogPath, String targetVersion) throws Exception;
    List<Migration> getAppliedMigrations();
    void rollbackMigration(String changelogPath, String path) throws Exception;
    void recordMigration(String version, String appliedMigration, String rollbackFile) throws SQLException;
    void removeMigrationRecord(String appliedMigration) throws SQLException;
}
