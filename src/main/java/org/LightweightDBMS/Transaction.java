package org.LightweightDBMS;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private final List<String> queriesInTransaction;

    Query query;

    public Transaction(User user) {
        queriesInTransaction = new ArrayList<>();
        query = new Query(user);
    }

    public void addQueryToTransaction(String query) {
        queriesInTransaction.add(query);
    }

    public void commitTransaction() {
        for (int i = 1; i < queriesInTransaction.size() - 1; i++){
            query.setCurrentQuery(queriesInTransaction.get(i));
            query.queryOperation(); // Execute the queries in the transaction
        }
        queriesInTransaction.clear(); // Clear the transaction after committing
    }

    public void rollbackTransaction() {
        queriesInTransaction.clear(); // Discard the queries in the transaction
    }
}
