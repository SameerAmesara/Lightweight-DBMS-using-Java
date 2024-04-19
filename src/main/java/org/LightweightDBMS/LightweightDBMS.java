package org.LightweightDBMS;

import java.util.*;

public class LightweightDBMS {
    public static void main(String[] args) {
        UserAuthentication userAuth = new UserAuthentication();
        Scanner scanner = new Scanner(System.in);
        Logs.startLogs(true);
        Transaction currentTransaction;

        boolean isRunning = true;
        System.out.println("+---------------------------------------------+");
        System.out.println("|         Welcome to Lightweight DBMS         |");
        System.out.println("+---------------------------------------------+");
        while (isRunning) {
            System.out.println("+---------------------------------------------+");
            System.out.println("|                Login/Register               |");
            System.out.println("+---------------------------------------------+");
            System.out.println("1. Register User");
            System.out.println("2. Log In");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    // User Registration
                    System.out.print("Enter User ID: ");
                    String userID = scanner.nextLine();
                    System.out.print("Enter Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter Password: ");
                    String password = scanner.nextLine();
                    userAuth.registerUser(userID, username, password);
                    System.out.println("User Registered Successfully.");
                    Logs.addLogs("User " + username + " Created Successfully.", "", true);
                    break;

                case 2:
                    // User Authentication
                    User authenticatedUser = userAuth.authenticateUser();
                    if (authenticatedUser != null) {
                        Logs.addLogs("Logged user:" + authenticatedUser.getUsername(), "", true);
                        boolean menuRunning = true;
                        while (menuRunning) {
                            System.out.println("+---------------------------------------------+");
                            System.out.println("|                    Menu                     |");
                            System.out.println("+---------------------------------------------+");
                            System.out.println("1. Execute SQL Query");
                            System.out.println("2. Execute Transaction");
                            System.out.println("3. Log Out");
                            System.out.print("Enter your choice: ");
                            int userChoice = scanner.nextInt();
                            scanner.nextLine(); // Consume newline character

                            switch (userChoice) {
                                case 1:
                                    // Execute SQL Query
                                    System.out.println("+---------------------------------------------+");
                                    System.out.println("|                  SQL Console                |");
                                    System.out.println("+---------------------------------------------+");
                                    System.out.print("Enter SQL Query: ");
                                    String input = scanner.nextLine();
                                    Query q_obj = new Query(authenticatedUser);
                                    q_obj.setCurrentQuery(input);
                                    q_obj.queryOperation();
                                    break;

                                case 2:
                                    // Execute Transaction
                                    Logs.addLogs("--- Transaction Execution Started ---", "", true);
                                    currentTransaction = new Transaction(authenticatedUser);
                                    boolean transactionRunning = true;
                                    System.out.println("+---------------------------------------------+");
                                    System.out.println("|                  SQL Console                |");
                                    System.out.println("+---------------------------------------------+");
                                    while (transactionRunning) {
                                        String trans_input = scanner.nextLine();
                                        if (trans_input.equalsIgnoreCase("commit")) {
                                            System.out.println("\nCommitting the Changes");
                                            currentTransaction.commitTransaction();
                                            Logs.addLogs("--- Transaction Committed ---", "", true);
                                            transactionRunning = false;
                                        } else if (trans_input.equalsIgnoreCase("rollback")) {
                                            System.out.println("\nRolling back the Changes");
                                            currentTransaction.rollbackTransaction();
                                            Logs.addLogs("--- Transaction Rollbacked ---", "", true);
                                            transactionRunning = false;
                                        } else {
                                            currentTransaction.addQueryToTransaction(trans_input);
                                        }
                                    }
                                    Logs.addLogs("--- Transaction Execution Ended ---", "", true);
                                    break;

                                case 3:
                                    // Logout
                                    menuRunning = false;
                                    break;

                                default:
                                    System.out.println("Invalid choice.");
                            }
                        }
                    }

                case 3:
                    // Exit
                    isRunning = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
        System.out.println("+---------------------------------------------+");
        System.out.println("\nGoodbye!");
        Logs.stopLogs();
        scanner.close(); // Close the scanner when done
    }
}
