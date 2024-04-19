package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Commands.Select;
import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectTest {
    private Select select;
    private User user;
    private final ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Path tableFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        System.setOut(new PrintStream(consoleOut));
        user = new User("1","testUser","testPassword");
        // Initialize the Logs for each test
        Logs.startLogs(false);
        tableFilePath = Path.of("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        // Create a test table file with sample data
        Files.write(tableFilePath, "id-name-age\nint-string-int\n1-John-30\n2-Jane-25".getBytes());
        select = new Select(user, "column1 from TestTable;");
    }

    @AfterEach
    public void tearDown() {
        // After each test, the temp directory and its content are automatically deleted
        Logs.deleteLog();
        System.setOut(originalOut);
        File tableFile = new File("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        if (tableFile.exists()) {
            tableFile.delete();
        }
    }

    @Test
    public void testSelectSpecificColumns() throws IOException {
        String query = "SELECT id, name, age FROM TestTable;";
        Select selectCommand = new Select(user, query);
        selectCommand.selectContinue();

        String expectedOutput = String.format("|id|name|age|%n|1|John|30||%n|2|Jane|25||%n->Selected rows in TestTable%n");
        assertEquals(expectedOutput, consoleOut.toString(), "Output should match expected output for specific columns.");
    }

    @Test
    public void testSelectWithWhereClause() throws IOException {
        String query = "SELECT name FROM TestTable WHERE id = 1;";
        Select selectCommand = new Select(user, query);
        selectCommand.selectContinue();

        // Assert expected outputs
        String expectedOutput = String.format("|name|%n|John|%n->Selected rows in TestTable%n");
        assertEquals(expectedOutput, consoleOut.toString(), "Output should match expected output with WHERE clause.");
    }

    @Test
    public void testInvalidQuery() {
        String query = "SELECT FROM TestTable WHERE id = 1;"; // intentionally malformed
        Select selectCommand = new Select(user, query);
        selectCommand.selectContinue();

        // Normalize line separators to match the system's line separator
        String expectedOutput = String.format("||%n||%n->Selected rows in TestTable%n");
        assertEquals(expectedOutput, consoleOut.toString(), "No output expected for malformed query.");
    }
}
