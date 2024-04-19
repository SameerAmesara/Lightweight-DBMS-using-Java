package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Commands.Create;
import org.LightweightDBMS.Commands.Insert;
import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertTest {
    private Insert insert;
    private User user;
    private final ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();

    private Path tableFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        System.setOut(new PrintStream(consoleOut));
        user = new User("1","testUser","testPassword");
        // Initialize the Logs for each test
        Logs.startLogs(false);
        tableFilePath = Path.of("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        // Create a test table file with sample data
        Files.write(tableFilePath, "column1-int\n42\n".getBytes());
        insert = new Insert(user, "into TestTable values (42);");
    }

    @AfterEach
    public void tearDown() {
        // After each test, the temp directory and its content are automatically deleted
        Logs.deleteLog();
        File tableFile = new File("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        if (tableFile.exists()) {
            tableFile.delete();
        }
    }

    @Test
    public void testAddIntoTable() throws IOException {
        insert.insertContinue();

        String expectedOutput = "->Added values in TestTable" + System.lineSeparator();
        String fileContent = getFileContent(tableFilePath.toString());

        assertEquals(expectedOutput, consoleOut.toString());
        assertTrue(fileContent.contains("42"));
    }

    @Test
    public void testAddIntoTableWithOrder() throws IOException {
        insert = new Insert(user, "into TestTable (column1) values (42);");
        insert.insertContinue();

        String expectedOutput = "->Added values in TestTable"  + System.lineSeparator();
        String fileContent = getFileContent(tableFilePath.toString());

        assertEquals(expectedOutput, consoleOut.toString());
        assertTrue(fileContent.contains("42"));
    }

    private String getFileContent(String filePath) {
        File file = new File(filePath);
        StringBuilder content = new StringBuilder();
        if (file.exists()) {
            try {
                java.util.Scanner scanner = new java.util.Scanner(file);
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine()).append("\n");
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content.toString();
    }
}
