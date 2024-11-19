package me.eexxlliinn.service.impl;

import me.eexxlliinn.connection.ConnectionManager;
import me.eexxlliinn.executor.impl.SqlExecutorImpl;
import me.eexxlliinn.filereader.impl.FileReaderImpl;
import me.eexxlliinn.service.MigrationManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

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
    public void runMigrations(String changelogPath) throws Exception {
        Set<String> appliedMigrations = getAppliedMigrations();
        Set<String> availableMigrations = sqlExecutor.parseChangelog(changelogPath);

        for (String migration : availableMigrations) {
            if (!appliedMigrations.contains(migration)) {
                sqlExecutor.runMigration(connection, migration);
                recordMigration(migration);
            } else {
                //log.info();
            }

        }
    }

    @Override
    public Set<String> getAppliedMigrations() {
        Set<String> migrations = new HashSet<>();
        String query = "SELECT file_name FROM migration_history";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                migrations.add(resultSet.getString("file_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return migrations;
    }

    @Override
    public void recordMigration(String appliedMigration) throws SQLException {
        String query = "INSERT INTO migration_history (file_name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, appliedMigration);
            preparedStatement.execute();
        }
    }
}
