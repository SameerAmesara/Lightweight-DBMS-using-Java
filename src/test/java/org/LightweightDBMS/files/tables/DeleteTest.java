package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Commands.Delete;
import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteTest {
    private Delete delete;
    private User user;
    private Path tableFilePath;

    private final ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() throws IOException {
        user = new User("1", "testUser", "testPassword");
        Logs.startLogs(false);
        // Initialize the path for the test table
        tableFilePath = Path.of("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        // Create a test table file with sample data
        Files.write(tableFilePath, "column1-int-column2-varchar\n1-A\n2-B\n3-C\n".getBytes());
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
    public void testDeleteFromTableWhereConditionIsMet() throws IOException {
        // Initialize Delete with the command to delete where column1 is 2
        delete = new Delete(user, "from TestTable where column1 = 2;");
        delete.deleteContinue();

        List<String> lines = Files.readAllLines(tableFilePath);
        assertFalse(lines.contains("2-B"), "Row with column1 = 2 should have been deleted.");
    }

    @Test
    public void testDeleteFromTableWhereConditionIsNotMet() throws IOException {
        // Initialize Delete with the command that should not lead to deletion
        delete = new Delete(user, "from TestTable where column1 = 99;");
        delete.deleteContinue();

        List<String> lines = Files.readAllLines(tableFilePath);
        assertTrue(lines.stream().anyMatch(s -> s.contains("2-B")), "Row with column1 = 2 should not have been deleted.");
    }

    @Test
    public void testDeleteAllFromTable() throws IOException {
        delete = new Delete(user, "from TestTable;");
        delete.deleteContinue();

        List<String> lines = Files.readAllLines(tableFilePath);
        assertEquals(2, lines.size(), "Only header rows should remain.");
    }

    @Test
    public void testSyntaxError() {
        delete = new Delete(user, "incorrect syntax;");
        delete.deleteContinue();
    }
}
