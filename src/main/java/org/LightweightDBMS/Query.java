package org.LightweightDBMS;

import org.LightweightDBMS.Commands.*;

import java.util.ArrayList;

public class Query {
    ArrayList<String> currentQuery = new ArrayList<>();
    User user;
    Create create = new Create();
    Insert insert = new Insert();
    Delete delete = new Delete();
    Update update = new Update();
    Select select = new Select();

    Query(User user)
    {
        this.user = user;
        this.create.setUser(user);
        this.insert.setUser(user);
        this.delete.setUser(user);
        this.update.setUser(user);
        this.select.setUser(user);
    }

    public void setCurrentQuery(String currentQuery) {

        String[] currentQueryList = currentQuery.split(" ", 2);
        this.currentQuery.clear();
        for(int i=0; i< currentQueryList.length; i++)
        {
            this.currentQuery.add(currentQueryList[i]);
        }

    }

    public void queryOperation()
    {
        String queryHead = this.currentQuery.get(0);
        String queryBody = this.currentQuery.get(1);

        switch (queryHead.toUpperCase())
        {
            case "CREATE":
                this.create.setQuery(queryBody);
                this.create.createContinue();
                break;
            case "INSERT":
                this.insert.setQuery(queryBody);
                this.insert.insertContinue();
                break;
            case "DELETE":
                this.delete.setQuery(queryBody);
                this.delete.deleteContinue();
                break;
            case "UPDATE":
                this.update.setQuery(queryBody);
                this.update.updateContinue();
                break;
            case "SELECT":
                this.select.setQuery(queryBody);
                this.select.selectContinue();
                break;

            default:
                Logs.addLogs("->Query not found.","",true);
                break;
        }
    }
}
