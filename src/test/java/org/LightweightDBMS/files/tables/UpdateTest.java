package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Commands.Update;
import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateTest {
    private Update updateCommand;
    private User user;
    private final ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();

    private File logFile;
    private Path tableFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        System.setOut(new PrintStream(consoleOut));
        user = new User("1","testUser","testPassword");

        Logs.startLogs(false);
        logFile = new File(Logs.fileName);
        tableFilePath = Path.of("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        // Create a test table file with sample data
        Files.write(tableFilePath, "id-name-age\nint-string-int\n1-John-30\n2-Jane-25".getBytes());

        updateCommand = new Update(new User("1", "testUser", "testPassword"), "");
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

    private String getLogFileContent() throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    @Test
    public void testSuccessfulUpdate() throws IOException {
        String query = "TestTable SET age = 35 WHERE name = John;";
        updateCommand.setQuery(query);
        updateCommand.updateContinue();

        BufferedReader tableReader = new BufferedReader(new FileReader(String.valueOf(tableFilePath)));
        String header = tableReader.readLine();
        String datatypeRow = tableReader.readLine();
        String updatedRow = tableReader.readLine();
        String secondRow = tableReader.readLine();
        tableReader.close();

        String expectedUpdatedRow = "1-John-35";
        assertEquals("id-name-age", "id-name-age", header);
        assertTrue(updatedRow.contains(expectedUpdatedRow), "Row with updated age should match");
        assertNotNull(secondRow, "There should be a second row");
    }

    @Test
    public void testSyntaxError() throws IOException {
        String query = "TestTable SET WHERE age = John;";
        updateCommand.setQuery(query);
        updateCommand.updateContinue();

        String logContent = getLogFileContent();
        assertTrue(logContent.contains("->Syntax Error in SET clause."), "Log content should contain syntax error");
    }

    @Test
    public void testTableDoesNotExist() throws IOException {
        String query = "NonExistingTable SET age = 35 WHERE name = John;";
        updateCommand.setQuery(query);
        updateCommand.updateContinue();

        String logContent = getLogFileContent();
        assertTrue(logContent.contains("->Table NonExistingTable does not exist."), "Log content should contain table does not exist message");
    }

    @Test
    public void testColumnNotFound() throws IOException {
        String query = "TestTable SET nonExistingColumn = 35 WHERE name = John;";
        updateCommand.setQuery(query);
        updateCommand.updateContinue();

        String logContent = getLogFileContent();
        assertTrue(logContent.contains("->Column to update: nonExistingColumn not in table"), "Log content should contain column not found message");
    }
}
