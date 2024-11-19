package me.eexxlliinn.executor;

import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

public interface SqlExecutor {

    void runAllMigrations(String changelogPath) throws Exception;
    void runMigration(Connection connection, String path) throws IOException;
    Set<String> parseChangelog(String changelogPath) throws Exception;
}
