package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Logs;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.LightweightDBMS.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class UserTest {
    private Path tempDirectory;

    @Before
    public void setUp() throws IOException {
        // Create a temporary directory to work with
        tempDirectory = Files.createTempDirectory("testAccessJson");
        Logs.startLogs(false);
        // Prepare a temporary access.json file with test content
        try (FileWriter writer = new FileWriter(tempDirectory.resolve("access.json").toFile())) {
            writer.write("[{\"userID\":\"1\",\"tables\":[{\"table\":\"TestTable\",\"access\":\"ReadWrite\"}]}]");
        }
    }

    @After
    public void tearDown() throws IOException {
        // Delete the temporary directory and its content
        Logs.deleteLog();
        Files.walk(tempDirectory)
                .sorted((a, b) -> b.compareTo(a)) // Sort in reverse order, files before dirs
                .forEach(path -> path.toFile().delete());
    }

    @Test
    public void testSetAndGetProperties() {
        User user = new User("1", "JohnDoe", "password123");
        assertEquals("User ID should match", "1", user.getUserID());
        assertEquals("Username should match", "JohnDoe", user.getUsername());
        assertEquals("Password should match", "password123", user.getPassword());
    }

    @Test
    public void testAddTable() {
        User user = new User("1", "JohnDoe", "password123");
        user.addTable("TestTable", "ReadWrite");
        assertEquals("Access type should be ReadWrite", "ReadWrite", user.tables.get("TestTable"));
    }
}
