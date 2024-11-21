package me.eexxlliinn.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.eexxlliinn.connection.ConnectionManager;
import me.eexxlliinn.entities.Migration;
import me.eexxlliinn.executor.impl.SqlExecutorImpl;
import me.eexxlliinn.filereader.impl.FileReaderImpl;
import me.eexxlliinn.service.MigrationManager;

import java.sql.*;
import java.util.*;

@Slf4j
public class MigrationManagerImpl implements MigrationManager {

    private final FileReaderImpl fileReader;
    private final SqlExecutorImpl sqlExecutor;
    private final Connection connection;

    public MigrationManagerImpl(FileReaderImpl fileReader, SqlExecutorImpl sqlExecutor) throws SQLException {
        this.fileReader = fileReader;
        this.sqlExecutor = sqlExecutor;
        this.connection = ConnectionManager.getConnection();
    }

    @Override
    public void runMigrationsToVersion(String changelogPath, String targetVersion) throws Exception {
        log.info("Running migrations to version {}", targetVersion);
        List<Migration> availableMigrations = sqlExecutor.parseChangelog(changelogPath);
        List<Migration> appliedMigrations = getAppliedMigrations();
        connection.setAutoCommit(false);
        try {
            for (Migration migration : availableMigrations) {
                String version = migration.getVersion();
                if (compareVersions(version, targetVersion) > 0) {
                    break;
                }
                boolean alreadyApplied = appliedMigrations.contains(migration);

                if (!alreadyApplied) {
                    sqlExecutor.runMigration(connection, migration.getFile(), migration.getVersion());
                    recordMigration(version, migration.getFile(), migration.getRollbackFile());
                } else {
                    log.info("Skipping {} with version {}", migration, version);
                }
            }
            connection.commit();
            log.info("Migrations applied to version {}", targetVersion);
        } catch (Exception e) {
            connection.rollback();
            log.error("Migrations applied to version {} failed", targetVersion);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }

    }

    @Override
    public List<Migration> getAppliedMigrations() {
        List<Migration> appliedMigrations = new ArrayList<>();
        String query = "SELECT version, file_name, rollback FROM migration_history ORDER BY applied_at ASC";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String version = resultSet.getString("version");
                String fileName = resultSet.getString("file_name");
                String rollback = resultSet.getString("rollback");
                appliedMigrations.add(new Migration(version, fileName, rollback));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appliedMigrations;
    }

    @Override
    public void rollbackMigration(String changelogPath, String targetVersion) throws Exception {
        log.info("Rolling back migration to version {}", targetVersion);
        List<Migration> appliedMigrations = getAppliedMigrations();
        List<Migration> appliedList = appliedMigrations.stream()
                        .sorted((m1, m2) -> compareVersions(m2.getVersion(), m1.getVersion()))
                        .toList();
        connection.setAutoCommit(false);
        try {
            for (Migration migration : appliedList) {
                String version = migration.getVersion();
                if (compareVersions(version, targetVersion) <= 0) {
                    break;
                }
                sqlExecutor.runMigration(connection, migration.getRollbackFile(), migration.getVersion());
                removeMigrationRecord(migration.getFile());
            }
            connection.commit();
            log.info("Rollback migrations applied to version {}", targetVersion);
        } catch (Exception e) {
            connection.rollback();
            log.info("Rollback migrations applied to version {} failed", targetVersion);
            throw e;
        }
    }

    @Override
    public void recordMigration(String version, String appliedMigration, String rollbackFile) throws SQLException {
        log.info("Recording migration {}", appliedMigration);
        String query = "INSERT INTO migration_history (version, file_name, rollback, applied_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, version);
            preparedStatement.setString(2, appliedMigration);
            preparedStatement.setString(3, rollbackFile);
            preparedStatement.execute();
        }
    }

    @Override
    public void removeMigrationRecord(String appliedMigration) throws SQLException {
        log.info("Removing migration {}", appliedMigration);
        String query = "DELETE FROM migration_history WHERE file_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, appliedMigration);
            preparedStatement.execute();
        }
    }

    private int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int part1 = Integer.parseInt(parts1[i]);
            int part2 = Integer.parseInt(parts2[i]);
            if (part1 > part2) {
                return 1;
            } else if (part1 < part2) {
                return -1;
            }
        }
        return 0;
    }
}
