package org.LightweightDBMS.Commands;

import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Update {
    User user;
    String query;
    String tableName;
    String column;
    String condition;

    public Update(User user, String query) {
        this.user = user;
        this.query = query;
    }

    public Update()
    {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void updateContinue()
    {
        Pattern pattern = Pattern.compile("(\\w+)\\s+(?i:set)\\s*(.+)\\s*(?i:where)\\s+(\\w+)\\s*=\\s*(.+)\\s*;");
        Matcher matcher = pattern.matcher(this.query);
        if(matcher.find())
        {
            this.tableName = matcher.group(1);
            this.query = matcher.group(2);
            this.column = matcher.group(3);
            this.condition = matcher.group(4);
            updateFromTable();
        }
        else
        {
            Logs.addLogs("->Syntax Error.","",true);
        }
    }

    public void updateFromTable() {
        int conditionColumnIndex = -1;
        int updateColumnIndex = -1;
        String newValue = null;
        String[] columns;
        StringBuilder fileContent = new StringBuilder();

        Pattern pattern = Pattern.compile("(\\w+)\\s*=\\s*'?(\\w+)'?");
        Matcher matcher = pattern.matcher(this.query);

        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt");
            if (!location.exists()) {
                Logs.addLogs("->Table " + this.tableName + " does not exist.", "", true);
                return;
            }

            BufferedReader tableReader = new BufferedReader(new FileReader(location));
            String line = tableReader.readLine();
            if (line == null) {
                Logs.addLogs("->Table " + this.tableName + " is empty.", "", true);
                tableReader.close();
                return;
            }

            fileContent.append(line).append(System.lineSeparator());
            columns = line.split("-");

            // Find the index of the column that needs to be updated based on the condition
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equals(this.column)) {
                    conditionColumnIndex = i;
                    break;
                }
            }

            // If condition column does not exist, log error and exit
            if (conditionColumnIndex == -1) {
                Logs.addLogs("->Column: " + this.column + " not in table.", "", true);
                tableReader.close();
                return;
            }

            boolean foundSetClause = false;
            while (matcher.find()) {
                foundSetClause = true;
                String updateColumn = matcher.group(1);
                newValue = matcher.group(2);

                for (int i = 0; i < columns.length; i++) {
                    if (columns[i].equals(updateColumn)) {
                        updateColumnIndex = i;
                        break;
                    }
                }

                // If update column does not exist, log error and exit
                if (updateColumnIndex == -1) {
                    Logs.addLogs("->Column to update: " + matcher.group(1) + " not in table.", "", true);
                    tableReader.close();
                    return;
                }
            }

            if (!foundSetClause) {
                Logs.addLogs("->Syntax Error in SET clause.", "", true);
                tableReader.close();
                return;
            }

            // Read the rest of the table and update rows as needed
            while ((line = tableReader.readLine()) != null) {
                columns = line.split("-");
                if (columns[conditionColumnIndex].equals(this.condition)) {
                    columns[updateColumnIndex] = newValue;
                    line = String.join("-", columns);
                }
                fileContent.append(line).append(System.lineSeparator());
            }
            tableReader.close();

            // Write the updated content back to the file
            BufferedWriter tableWriter = new BufferedWriter(new FileWriter(location));
            tableWriter.write(fileContent.toString());
            tableWriter.close();

            Logs.addLogs("->Updated rows in " + this.tableName, "", true);

        } catch (IOException e) {
            Logs.addLogs("->Not able to update table " + this.tableName, "", true);
        }
    }

}