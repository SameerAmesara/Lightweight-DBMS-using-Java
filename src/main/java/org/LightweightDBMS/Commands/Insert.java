package org.LightweightDBMS.Commands;

import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Insert {
    User user;
    String query;
    String tableName;
    String columns;

    public Insert(User user, String query) {
        this.user = user;
        this.query = query;
    }

    public Insert()
    {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void insertContinue()
    {
        Pattern pattern = Pattern.compile("(?i:into)\\s+(\\w+)\\s+(?i:values)\\s*\\((.+)\\)\\s*;");
        Matcher matcher = pattern.matcher(this.query);

        Pattern pattern2 = Pattern.compile("(?i:into)\\s+(\\w+)\\s*\\((.+)\\)\\s+(?i:values)\\s*\\((.+)\\)\\s*;");
        Matcher matcher2 = pattern2.matcher(this.query);

        if(matcher.find())
        {
            this.tableName = matcher.group(1);
            this.query = matcher.group(2);
            addIntoTable();
        }
        else if(matcher2.find())
        {
            this.tableName = matcher2.group(1);
            this.query = matcher2.group(3);
            this.columns = matcher2.group(2);
            addIntoTableWithOrder();
        }
        else
        {
            Logs.addLogs("->Syntax Error","",true);
        }
    }

    public void addIntoTable()
    {
        String value = "";
        Pattern pattern;
        Matcher matcher;
        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt");
            if(!location.exists())
            {
                Logs.addLogs("->Table "+this.tableName+" does not exists","",true);
            }
            else {

                pattern = Pattern.compile("(\\w+)");
                matcher = pattern.matcher(this.query);
                if(matcher.find())
                {
                    value = value+matcher.group(1);

                    while(matcher.find())
                    {
                        value = value+"-"+matcher.group(1);
                    }


                    BufferedWriter tableWriter = new BufferedWriter(new FileWriter("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt", true));

                    tableWriter.write(value);
                    tableWriter.write('\n');

                    tableWriter.close();

                    Logs.addLogs("->Added values in "+this.tableName,"",true);

                }
                else
                {
                    Logs.addLogs("->Syntax Error","",true);
                }

            }

        } catch (IOException e) {
            Logs.addLogs("->Failed to add values in table "+this.tableName,"",true);
        }
    }

    public void addIntoTableWithOrder()
    {
        String value = "";
        String line ="";
        String [] columnNames;
        ArrayList<String> selectValues = new ArrayList<String>();
        ArrayList<Integer> selectColumnIndex = new ArrayList<Integer>();
        ArrayList<String> selectColumnName = new ArrayList<String>();
        Pattern pattern;
        Matcher matcher;
        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt");
            if(!location.exists())
            {
                Logs.addLogs("->Table "+this.tableName+" does not exists","-Query canceled",true);

            }
            else {

                BufferedReader tableReader = new BufferedReader(new FileReader("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt"));
                if (tableReader.ready()) {
                    line = tableReader.readLine();

                    columnNames = line.split("-");

                    pattern = Pattern.compile("(\\w+)");
                    matcher = pattern.matcher(this.columns);

                    if(matcher.find()) {
                        selectColumnName.add(matcher.group(1));

                        while (matcher.find()) {
                            selectColumnName.add(matcher.group(1));
                        }

                        for (int i = 0; i < columnNames.length; i++) {
                            for(int j=0;j<selectColumnName.size();j++)
                            {
                                if (columnNames[i].equals(selectColumnName.get(j))) {
                                    selectColumnIndex.add(j);
                                }
                            }

                        }
                    }


                }

                pattern = Pattern.compile("(\\w+)");
                matcher = pattern.matcher(this.query);
                ArrayList<String> selectValuesOrdered = new ArrayList<>(selectColumnName.size());
                if(matcher.find())
                {
                    selectValues.add(matcher.group(1));

                    while(matcher.find())
                    {
                        selectValues.add(matcher.group(1));
                    }

                    for(int i=0;i<selectValues.size();i++)
                    {
                        selectValuesOrdered.add(String.valueOf(selectValues.get(selectColumnIndex.get(i))));
                    }


                    BufferedWriter tableWriter = new BufferedWriter(new FileWriter("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt", true));

                    value = String.join("-",selectValuesOrdered);
                    tableWriter.write(value);
                    tableWriter.write('\n');

                    tableWriter.close();

                    Logs.addLogs("->Added values in "+this.tableName,"",true);

                }
                else
                {
                    Logs.addLogs("->Syntax Error.","",true);
                }

            }

        } catch (IOException e) {
            Logs.addLogs("->Failed to add values to table "+this.tableName,"",true);
        }
    }
}
