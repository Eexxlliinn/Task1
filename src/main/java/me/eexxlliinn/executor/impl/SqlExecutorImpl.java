package me.eexxlliinn.executor.impl;

import me.eexxlliinn.connection.ConnectionManager;
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
import java.util.HashSet;
import java.util.Set;

public class SqlExecutorImpl implements SqlExecutor {

    @Override
    public void runAllMigrations(String changelogPath) throws Exception {
        try (Connection connection = ConnectionManager.getConnection()) {
            Set<String> migrations = parseChangelog(changelogPath);
            for (String migration : migrations) {
                runMigration(connection, migration);
            }
        }
    }

    @Override
    public void runMigration(Connection connection, String path) throws IOException {
        String sql = Files.readString(Paths.get(path));
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> parseChangelog(String changelogPath) throws Exception {
        Set<String> migrations = new HashSet<>();
        File file = new File(changelogPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        NodeList changeSets = document.getElementsByTagName("changeSet");
        for (int i = 0; i < changeSets.getLength(); i++) {
            Element changeSet = (Element) changeSets.item(i);
            String fileName = changeSet.getAttribute("file");
            migrations.add("src/main/resources/db/changelog/" + fileName);
        }
        return migrations;
    }
}
