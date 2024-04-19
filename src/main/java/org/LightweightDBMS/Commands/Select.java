package org.LightweightDBMS.Commands;

import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Select {
    User user;
    String query;
    String tableName;
    String column;
    String condition;
    boolean whereInQuery = false;

    public Select(User user, String query) {
        this.user = user;
        this.query = query;
    }

    public Select()
    {

    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void selectContinue()
    {
        Pattern pattern = Pattern.compile("(.+)\\s+(?i:from)\\s+(\\w+)\\s*(\\s((?i:where))\\s+(\\w+)\\s*=\\s*(\\w+)\\s*)?\\s*;");
        Matcher matcher = pattern.matcher(this.query);
        this.whereInQuery = false;
        if(matcher.find())
        {
            this.tableName = matcher.group(2);
            this.query = matcher.group(1);
            if(matcher.group(4)!=null)
            {
                this.whereInQuery = true;
                this.column = matcher.group(5);
                this.condition = matcher.group(6);
            }

            selectFromTable();
        }
        else
        {
            Logs.addLogs("->Syntax Error!","",true);
        }
    }

    public void selectFromTable()
    {
        int locationIndex = -1;
        ArrayList<String> selectValues = new ArrayList<String>();
        ArrayList<Integer> selectColumnIndex = new ArrayList<Integer>();
        ArrayList<String> selectColumnName = new ArrayList<String>();
        String fileContent = "";
        String line;
        String newLine = "";
        String[] columns = {};
        boolean allColumn = false;

        if(this.query.equals("*"))
        {
            allColumn = true;
        }

        Pattern pattern;
        Matcher matcher;
        try {
            File location = new File("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt");
            if(!location.exists())
            {
                Logs.addLogs("->Table "+this.tableName+" does not exists.","",true);
            }
            else {

                pattern = Pattern.compile("(\\w+)");
                matcher = pattern.matcher(this.query);

                BufferedReader tableReader = new BufferedReader(new FileReader("src/main/java/org/LightweightDBMS/files/tables/"+this.tableName+".txt"));
                if (tableReader.ready()) {
                    line = tableReader.readLine();
                    fileContent = fileContent+line+'\n';
                    columns = line.split("-");
                    if(this.whereInQuery)
                    {
                        for(int i=0;i<columns.length;i++)
                        {
                            if(columns[i].equals(this.column))
                            {
                                locationIndex = i;
                            }
                        }
                    }

                }


                if(matcher.find() || allColumn)
                {
                    if(allColumn)
                    {
                        for(int i=0;i<columns.length;i++)
                        {
                            selectColumnName.add(columns[i]);
                        }

                    }
                    else
                    {
                        while(matcher.find())
                        {
                            selectColumnName.add(matcher.group(1));
                        }
                    }

                    printOutput(String.join("-", selectColumnName));

                    for(int i= 0;i<selectColumnName.size();i++)
                    {
                        for(int j=0;j<columns.length;j++)
                        {
                            if(columns[j].equals(selectColumnName.get(i)))
                            {
                                selectColumnIndex.add(j);
                            }
                        }
                    }

                    if(locationIndex!=-1 || !this.whereInQuery)
                    {
                        if (tableReader.ready())
                        {
                            line = tableReader.readLine();
                            fileContent = fileContent+line+'\n';
                        }

                        while (tableReader.ready()) {
                            line = tableReader.readLine();
                            columns = line.split("-");

                            newLine = "";
                            if(!this.whereInQuery)
                            {

                                for(int i= 0;i<selectColumnIndex.size();i++)
                                {
                                    newLine = newLine + columns[selectColumnIndex.get(i)]+"|";

                                }
                                printOutput(newLine);

                            }
                            else if(columns[locationIndex].equals(this.condition))
                            {
                                for(int i= 0;i<selectColumnIndex.size();i++)
                                {
                                    newLine = newLine + columns[selectColumnIndex.get(i)]+"-";
                                }
                                printOutput(newLine);

                            }

                        }

                        tableReader.close();

                        Logs.addLogs("->Selected rows in "+this.tableName,"",true);
                    }
                    else
                    {
                        Logs.addLogs("->Column: "+this.column+" not in table","",true);
                    }



                }
                else
                {
                    Logs.addLogs("->Syntax Error!","",true);
                }

            }

        } catch (IOException e) {
            Logs.addLogs("->Not able to add values to table "+this.tableName,"",true);
        }
    }

    void printOutput(String row) {
        String[] columns = row.split("-");
        System.out.print("|");
        for (int i = 0; i < columns.length; i++) {
            System.out.print(columns[i]);
            if (i < columns.length-1) {
                System.out.print("|");
            }
        }
        System.out.println("|"); // End the line with a final bar
    }
}
