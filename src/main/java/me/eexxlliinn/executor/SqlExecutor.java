package me.eexxlliinn.executor;

import me.eexxlliinn.entities.Migration;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SqlExecutor {

    void runMigration(Connection connection, String path, String version) throws IOException;
    List<Migration> parseChangelog(String changelogPath) throws Exception;
}
