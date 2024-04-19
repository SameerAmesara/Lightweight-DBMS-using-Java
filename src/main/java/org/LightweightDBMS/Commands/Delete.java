package org.LightweightDBMS.Commands;

import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delete {
    User user;
    String query;
    String tableName;

    public Delete(User user, String query) {
        this.user = user;
        this.query = query;
    }

    public Delete() {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void deleteContinue() {
        Pattern pattern = Pattern.compile("(?i:from)\\s+(\\w+)\\s+(?i:where)\\s+(.+);");
        System.out.println(this.query);
        Matcher matcher = pattern.matcher(this.query);
        Pattern pattern2 = Pattern.compile("(?i:from)\\s+(\\w+)\\s*;");
        Matcher matcher2 = pattern2.matcher(this.query);
        if (matcher.find()) {
            this.tableName = matcher.group(1);
            this.query = matcher.group(2);
            deleteFromTable();
        } else if (matcher2.find()) {
            this.tableName = matcher2.group(1);
            deleteAll();
        } else {
            Logs.addLogs("->Syntax Error", "in the query", true);
        }
    }

    public void deleteFromTable() {
        Pattern pattern = Pattern.compile("(\\w+)\\s*([=><!]+)\\s*'?([^';]+)'?");
        Matcher matcher = pattern.matcher(this.query);

        if (!matcher.find()) {
            Logs.addLogs("Syntax Error", "", true);
            return;
        }

        String column = matcher.group(1);
        String symbol = matcher.group(2);
        String condition = matcher.group(3).trim();
        String fileContent = "";
        boolean foundColumn = false;
        boolean changesMade = false;

        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt");
            if (!location.exists()) {
                Logs.addLogs("Table " + this.tableName + " does not exist", "", true);
                return;
            }

            try (BufferedReader tableReader = new BufferedReader(new FileReader(location))) {
                String header = tableReader.readLine();
                if (header == null) {
                    Logs.addLogs("Table format error: Missing header", "", true);
                    return;
                }

                fileContent += header + "\n";
                String[] columns = header.split("-");
                int locationIndex = Arrays.asList(columns).indexOf(column);

                if (locationIndex == -1) {
                    Logs.addLogs("Column: " + column + " not in table", "", true);
                    return;
                }

                foundColumn = true;
                String typesLine = tableReader.readLine();
                fileContent += typesLine + "\n"; // Assuming the second line contains types
                String[] types = typesLine.split("-");

                String line;
                while ((line = tableReader.readLine()) != null) {
                    String[] values = line.split("-");

                    if (evaluateCondition(values[locationIndex], condition, symbol, types[locationIndex])) {
                        changesMade = true;
                        continue; // Skip adding this line to fileContent
                    }

                    fileContent += line + "\n";
                }
            }

            if (foundColumn && changesMade) {
                try (BufferedWriter tableWriter = new BufferedWriter(new FileWriter(location))) {
                    tableWriter.write(fileContent);
                }
                Logs.addLogs("Deleted rows in " + this.tableName, "", true);
            }

        } catch (IOException e) {
            Logs.addLogs("Failed to delete values from table " + this.tableName, "", true);
        }
    }

    private boolean evaluateCondition(String value, String condition, String operator, String type) {
        try {
            switch (type) {
                case "int":
                    int intValue = Integer.parseInt(value);
                    int conditionValue = Integer.parseInt(condition);
                    return evaluateIntCondition(intValue, conditionValue, operator);
                default: // Assuming string type for simplicity
                    return evaluateStringCondition(value, condition, operator);
            }
        } catch (NumberFormatException e) {
            Logs.addLogs("Data type error: " + e.getMessage(), "", true);
            return false;
        }
    }

    private boolean evaluateIntCondition(int value, int condition, String operator) {
        switch (operator) {
            case "=": return value == condition;
            case "!=": return value != condition;
            case ">": return value > condition;
            case "<": return value < condition;
            case ">=": return value >= condition;
            case "<=": return value <= condition;
            default: return false;
        }
    }

    private boolean evaluateStringCondition(String value, String condition, String operator) {
        switch (operator) {
            case "=": return value.equals(condition);
            case "!=": return !value.equals(condition);
            default: return false; // For simplicity, assuming only equality checks for strings
        }
    }

//    public void deleteFromTable() {
//        String column;
//        String symbol;
//        String condition;
//        String type = "";
//        int locationIndex = -1;
//        String line;
//        String fileContent = "";
//
//        Pattern pattern;
//        Matcher matcher;
//        try {
//            File location = new File("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt");
//            if (!location.exists()) {
//                Logs.addLogs("Table " + this.tableName + " does not exists", "", true);
//            } else {
//
//                pattern = Pattern.compile("(\\w+)\\s*'*([^'\\\"\\w\\s]+)'*\\s*(.+);");
//                matcher = pattern.matcher(this.query);
//                if (matcher.find()) {
//                    column = matcher.group(1);
//                    symbol = matcher.group(2);
//                    condition = matcher.group(3);
//
//                    BufferedReader tableReader = new BufferedReader(new FileReader("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt"));
//                    if (tableReader.ready()) {
//                        line = tableReader.readLine();
//                        fileContent = fileContent + line + '\n';
//                        String[] columns = line.split("-");
//                        for (int i = 0; i < columns.length; i++) {
//                            if (columns[i].equals(column)) {
//                                locationIndex = i;
//                            }
//                        }
//                    }
//                    if (locationIndex != -1) {
//                        if (tableReader.ready()) {
//                            line = tableReader.readLine();
//                            String[] columns = line.split("-");
//                            type = columns[locationIndex];
//                            fileContent = fileContent + line + '\n';
//                        }
//
//                        while (tableReader.ready()) {
//                            line = tableReader.readLine();
//                            String[] columns = line.split("-");
//
//                            if (type.equals("int")) {
//                                try {
//
//                                    int colVal = Integer.parseInt(columns[locationIndex]);
//                                    int colCondition = Integer.parseInt(condition);
//                                    switch (symbol) {
//                                        case "=":
//                                            if (colVal == colCondition) {
//                                                line = "";
//                                            }
//                                            break;
//                                        case "<":
//                                            if (colVal < colCondition) {
//                                                line = "";
//                                            }
//                                            break;
//                                        case ">":
//                                            if (colVal > colCondition) {
//                                                line = "";
//                                            }
//                                            break;
//                                        case "<=":
//                                            if (colVal <= colCondition) {
//                                                line = "";
//                                            }
//                                            break;
//                                        case ">=":
//                                            if (colVal >= colCondition) {
//                                                line = "";
//                                            }
//                                            break;
//                                        case "!=":
//                                            if (colVal != colCondition) {
//                                                line = "";
//                                            }
//                                            break;
//                                        default:
//                                            Logs.addLogs("->Wrong Symbol", "", true);
//                                            break;
//
//                                    }
//                                } catch (NumberFormatException e) {
//                                    System.out.println("Please enter an integer.");
//                                }
//                            } else {
//                                if (columns[locationIndex].equals(condition)) {
//                                    line = "";
//                                }
//                            }
//
//                            if (!line.equals("")) {
//                                fileContent = fileContent + line + '\n';
//                            }
//                        }
//
//                        tableReader.close();
//
//                        BufferedWriter tableWriter = new BufferedWriter(new FileWriter("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt"));
//
//                        tableWriter.write(fileContent);
//                        tableWriter.close();
//
//                        Logs.addLogs("->Deleted rows in " + this.tableName, "", true);
//                    } else {
//                        Logs.addLogs("->Column: " + column + " not in table", "", true);
//                    }
//
//                } else {
//                    Logs.addLogs("Syntax Error", "", true);
//                }
//
//            }
//
//        } catch (IOException e) {
//            Logs.addLogs("->Failed to delete values from table " + this.tableName, "", true);
//        }
//    }

    public void deleteAll() {
        String line;
        String fileContent = "";
        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt");
            if (!location.exists()) {
                Logs.addLogs("Table " + this.tableName + " does not exits", "", true);
            } else {

                BufferedReader tableReader = new BufferedReader(new FileReader("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt"));
                if (tableReader.ready()) {
                    line = tableReader.readLine();
                    fileContent = fileContent + line + '\n';
                    tableReader.ready();
                    line = tableReader.readLine();
                    fileContent = fileContent + line + '\n';
                }
                tableReader.close();
                BufferedWriter tableWriter = new BufferedWriter(new FileWriter("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt"));

                tableWriter.write(fileContent);
                tableWriter.close();

                Logs.addLogs("->Deleted all rows in " + this.tableName, "", true);

            }
        } catch (IOException e) {
            Logs.addLogs("->Failed to delete all rows from table " + this.tableName, "", true);
        }
    }
}