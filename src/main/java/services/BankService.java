package services;

import models.Transaction;
import storage.JsonStorage;
import utils.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankService {

    private JsonStorage transactionStorage;
    private static final String PATH = System.getProperty("user.dir") + "/data/";

    public BankService() {
        this.transactionStorage = new JsonStorage(PATH + "transactions.json");
    }

    public void deposit(String childId, double amount, String description) {
        if (!Validator.isValidAmount(amount)) return;
        saveTransaction(childId, "credit", amount, description);
    }

    public boolean withdraw(String childId, double amount, String description) {
        if (!Validator.isValidAmount(amount)) return false;
        if (amount > getBalance(childId)) return false;
        saveTransaction(childId, "debit", amount, description);
        return true;
    }

    public double getBalance(String childId) {
        List<Transaction> all = transactionStorage.loadAll(Transaction.class);
        double balance = 0;
        for (Transaction t : all) {
            if (t.getChildId().equals(childId)) {
                if (t.getType().equals("credit")) balance += t.getAmount();
                else balance -= t.getAmount();
            }
        }
        return balance;
    }

    public List<Transaction> getHistory(String childId) {
        List<Transaction> all    = transactionStorage.loadAll(Transaction.class);
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getChildId().equals(childId)) result.add(t);
        }
        return result;
    }

    private void saveTransaction(String childId, String type,
                                 double amount, String description) {
        List<Transaction> all = transactionStorage.loadAll(Transaction.class);
        all.add(new Transaction(
                UUID.randomUUID().toString(), type, amount,
                LocalDate.now().toString(), childId, description
        ));
        transactionStorage.saveAll(all);
    }
}