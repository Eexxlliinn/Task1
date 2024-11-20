package me.eexxlliinn.executor.impl;

import lombok.extern.slf4j.Slf4j;
import me.eexxlliinn.entities.Migration;
import me.eexxlliinn.executor.SqlExecutor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqlExecutorImpl implements SqlExecutor {

    @Override
    public void runMigration(Connection connection, String path, String version) throws IOException {
        log.info("Running migration: {} with version: {}", path, version);
        String fullPath = "src/main/resources/db/changelog/" + path;
        String sql = Files.readString(Paths.get(fullPath));
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Migration> parseChangelog(final String changelogPath) throws Exception {
        List<Migration> migrations = new ArrayList<>();
        File file = new File(changelogPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        NodeList changeSets = document.getElementsByTagName("changeSet");
        for (int i = 0; i < changeSets.getLength(); i++) {
            Element changeSet = (Element) changeSets.item(i);
            String version = changeSet.getAttribute("version");
            String fileName = changeSet.getAttribute("file");
            String rollbackFileName = changeSet.getAttribute("rollback");
            migrations.add(new Migration(version, fileName, rollbackFileName));
        }
        return migrations;
    }
}
