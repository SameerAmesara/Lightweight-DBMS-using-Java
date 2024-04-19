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
import java.util.Scanner;

public class UserAuthentication {
    private final Map<String, User> users;
    private final Scanner scanner;
    private final String jsonFileName = "src/main/java/org/LightweightDBMS/files/users.json";

    public UserAuthentication() {
        users = loadUserDataFromJSON();
        scanner = new Scanner(System.in);
    }

    public void registerUser(String userID, String username, String password) {
        if (!users.containsKey(userID)) {
            MD5Hash mdh = new MD5Hash();
            String hashPassword = mdh.hashPassword(password);
            User user = new User(userID, username, hashPassword);
            users.put(userID, user);
            saveUserDataToJSON();
        } else {
            System.out.println("User with the same ID already exists.");
        }
    }

    public User authenticateUser() {
        System.out.print("Enter User ID: ");
        String userID = scanner.nextLine();

        if (users.containsKey(userID)) {
            User user = users.get(userID);

            System.out.print("Enter Password: ");
            String enteredPassword = scanner.nextLine();

            if (authenticate(enteredPassword,user)) {
                // Password is correct, generate and validate captcha
                Captcha captcha = new Captcha();
                System.out.println("Captcha: " + captcha.getCaptchaText());

                System.out.print("Enter Captcha: ");
                String enteredCaptcha = scanner.nextLine();

                if (captcha.validateCaptcha(enteredCaptcha)) {
                    System.out.println("Authentication successful.");
                    return user;
                } else {
                    System.out.println("Captcha validation failed.");
                }
            } else {
                System.out.println("Password authentication failed.");
            }
        } else {
            System.out.println("User not found.");
        }

        return null; // Authentication failed
    }

    private boolean authenticate(String enteredPassword,User user) {
        MD5Hash mdh = new MD5Hash();
        String enteredPasswordHash = mdh.hashPassword(enteredPassword);
        return user.getPassword().equals(enteredPasswordHash);
    }

    public void close() {
        scanner.close();
    }

    public Map<String, User> loadUserDataFromJSON() {
        try {
            JSONParser parser = new JSONParser();
            FileReader fileReader = new FileReader(jsonFileName);
            JSONObject jsonData = (JSONObject) parser.parse(fileReader);

            Map<String, User> users = new HashMap<>();
            JSONArray userArray = (JSONArray) jsonData.get("Users");
            for (Object obj : userArray) {
                JSONObject userObject = (JSONObject) obj;
                String userID = (String) userObject.get("UserID");
                String username = (String) userObject.get("Username");
                String passwordHash = (String) userObject.get("Password");
                users.put(userID, new User(userID, username, passwordHash));
            }

            return users;
        } catch (IOException | ParseException e) {
            // Handle file read error or JSON parse error
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void saveUserDataToJSON() {
        try (FileWriter file = new FileWriter(jsonFileName)) {
            JSONArray userArray = new JSONArray();
            for (User user : users.values()) {
                JSONObject userObject = new JSONObject();
                userObject.put("UserID", user.getUserID());
                userObject.put("Username", user.getUsername());
                userObject.put("Password", user.getPassword());
                userArray.add(userObject);
            }

            JSONObject jsonData = new JSONObject();
            jsonData.put("Users", userArray);

            file.write(jsonData.toJSONString());
            file.flush();
        } catch (IOException e) {
            // Handle file write error
            e.printStackTrace();
        }
    }
}
