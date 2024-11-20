package me.eexxlliinn;

import me.eexxlliinn.executor.impl.SqlExecutorImpl;
import me.eexxlliinn.filereader.impl.FileReaderImpl;
import me.eexxlliinn.service.impl.MigrationManagerImpl;

public class MigrationTool {
    public static void main(String[] args) throws Exception {
        String changelogPath = "src/main/resources/db/changelog/db.changelog-master.xml";
        FileReaderImpl fileReader = new FileReaderImpl();
        SqlExecutorImpl sqlExecutor = new SqlExecutorImpl();
        MigrationManagerImpl migrationManager = new MigrationManagerImpl(fileReader, sqlExecutor);
        migrationManager.runMigrationsToVersion(changelogPath, "2.0.0");
        migrationManager.rollbackMigration(changelogPath, "1.0.0");
    }
}