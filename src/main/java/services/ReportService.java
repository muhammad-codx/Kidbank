package services;

import models.Transaction;

import java.util.List;

public class ReportService {

    private BankService bankService;

    public ReportService() {
        this.bankService = new BankService();
    }

    public double getTotalEarned(String childId) {
        List<Transaction> history = bankService.getHistory(childId);
        double total = 0;
        for (Transaction t : history) {
            if (t.getType().equals("credit")) total += t.getAmount();
        }
        return total;
    }

    public double getTotalSpent(String childId) {
        List<Transaction> history = bankService.getHistory(childId);
        double total = 0;
        for (Transaction t : history) {
            if (t.getType().equals("debit")) total += t.getAmount();
        }
        return total;
    }

    public double getCurrentBalance(String childId) {
        return bankService.getBalance(childId);
    }
}