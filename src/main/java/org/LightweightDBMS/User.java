package org.LightweightDBMS;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final String userID;
    private final String username;
    private final String password;

    public HashMap<String, String> tables = new HashMap<String, String>();

    public User(String userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
    }

    public String getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }


    public void addTable(String table, String accessType) {
        this.tables.put(table, accessType);
    }

    public void setTables() {
        try {
            JSONParser parser = new JSONParser();
            JSONArray accessArray = new JSONArray();

            // Read the existing JSON content, if any
            try (FileReader reader = new FileReader("src/main/java/org/LightweightDBMS/files/access.json")) {
                Object obj = parser.parse(reader);
                if (obj instanceof JSONArray) {
                    accessArray = (JSONArray) obj;
                }
            }

            for (int i = 0; i < accessArray.size(); i++) {
                JSONObject accessObject = (JSONObject) accessArray.get(i);
                String id = (String) accessObject.get("userID");

                if (id.equals(this.userID)) {
                    JSONArray tablesArray = (JSONArray) accessObject.get("tables");

                    for (Object tableObj : tablesArray) {
                        JSONObject tableData = (JSONObject) tableObj;
                        String tableName = (String) tableData.get("table");
                        String access = (String) tableData.get("access");
                        this.tables.put(tableName, access);
                    }

                    break;
                }
            }
        } catch (IOException | ParseException e) {
            Logs.addLogs("->Cannot read access file", "", true);
        }
    }

    public void updateAccess() {
        try {
            JSONParser parser = new JSONParser();
            JSONArray accessArray = new JSONArray();

            // Read the existing JSON content, if any
            try (FileReader reader = new FileReader("src/main/java/org/LightweightDBMS/files/access.json")) {
                Object obj = parser.parse(reader);
                if (obj instanceof JSONArray) {
                    accessArray = (JSONArray) obj;
                }
            }

            boolean updated = false;

            for (int i = 0; i < accessArray.size(); i++) {
                JSONObject accessObject = (JSONObject) accessArray.get(i);
                String id = (String) accessObject.get("userID");

                if (id.equals(userID)) {
                    // Update the access for the user
                    JSONArray tablesArray = new JSONArray();
                    for (Map.Entry<String, String> tableAccess : this.tables.entrySet()) {
                        JSONObject tableObject = new JSONObject();
                        tableObject.put("table", tableAccess.getKey());
                        tableObject.put("access", tableAccess.getValue());
                        tablesArray.add(tableObject);
                    }

                    accessObject.put("tables", tablesArray);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                JSONObject newUserAccess = new JSONObject();
                newUserAccess.put("id", this.userID);

                JSONArray tablesArray = new JSONArray();
                for (Map.Entry<String, String> tableAccess : this.tables.entrySet()) {
                    JSONObject tableObject = new JSONObject();
                    tableObject.put("table", tableAccess.getKey());
                    tableObject.put("access", tableAccess.getValue());
                    tablesArray.add(tableObject);
                }

                newUserAccess.put("tables", tablesArray);
                accessArray.add(newUserAccess);
            }

            // Write the updated JSON content to the file
            try (FileWriter writer = new FileWriter("src/main/java/org/LightweightDBMS/files/access.json")) {
                writer.write(accessArray.toJSONString());
            }

        } catch (IOException | ParseException e) {
        }
    }
}
