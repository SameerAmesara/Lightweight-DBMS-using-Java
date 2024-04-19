package org.LightweightDBMS.Commands;

import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create {
    User user;
    String query;
    String tableName;

    public Create(User user, String query) {
        this.user = user;
        this.query = query;
    }

    public Create()
    {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void createContinue()
    {
        Pattern pattern = Pattern.compile("(?i:table)\\s+(\\w+)\\s*\\((.+)\\);");
        Matcher matcher = pattern.matcher(this.query);
        if(matcher.find())
        {
            this.tableName = matcher.group(1);
            String columnDefinitions = matcher.group(2);
            // Check if the table file already exists
            File tableFile = new File("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt");
            if(tableFile.exists()) {
                // If the table file exists, log that the table already exists and return without creating the table
                Logs.addLogs("->Table " + this.tableName + " already exists", "", true);
                return;
            }

            // Proceed to create the table if it does not exist
            table(columnDefinitions);
        }
        else
        {
            Logs.addLogs("->Syntax Error","",true);
        }
    }

    void table(String columnDefinitions) {
        String columns = "";
        String type = "";
        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt");
            if(location.exists())
            {
                Logs.addLogs("->Table "+this.tableName+" already exists","",true);
            }
            else {

                Pattern pattern = Pattern.compile("(\\w+)\\s+(\\w+)");
                Matcher matcher = pattern.matcher(columnDefinitions);
                if(matcher.find())
                {
                    columns = columns+matcher.group(1);
                    type = type+matcher.group(2);

                    if(type.equals("int") || type.equals("varchar")) {

                        while (matcher.find()) {
                            columns = columns + "-" + matcher.group(1);
                            type = type + "-" + matcher.group(2);
                        }

                        location.createNewFile();
                        BufferedWriter databaseWriter = new BufferedWriter(new FileWriter("src/main/java/org/LightweightDBMS/files/tables/" + this.tableName + ".txt", true));

                        databaseWriter.write(columns);
                        databaseWriter.write('\n');
                        databaseWriter.write(type);
                        databaseWriter.write('\n');

                        databaseWriter.close();

                        Logs.addLogs("->Table " + this.tableName + " Created", "by" + user.getUsername(), true);
                        this.user.addTable(this.tableName, "Maintainer");
                        user.updateAccess();
                    }
                    else
                    {
                        Logs.addLogs("->Syntax Error, wrong datatype","in the query",true);
                    }
                }
                else
                {
                    Logs.addLogs("->Syntax Error","in the query",true);
                }

            }

        } catch (IOException e) {
            Logs.addLogs("->Not able to create table "+this.tableName,"",true);
        }

    }
}

