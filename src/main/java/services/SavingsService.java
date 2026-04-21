package services;

import models.SavingsGoal;
import storage.JsonStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SavingsService {

    private JsonStorage storage;
    private BankService bankService;
    private static final String PATH = System.getProperty("user.dir") + "/data/";

    public SavingsService() {
        this.storage     = new JsonStorage(PATH + "goals.json");
        this.bankService = new BankService();
    }

    public boolean createGoal(String title, double target, String childId) {
        if (title.isEmpty() || target <= 0) return false;
        List<SavingsGoal> goals = storage.loadAll(SavingsGoal.class);
        goals.add(new SavingsGoal(UUID.randomUUID().toString(),
                title, target, childId));
        storage.saveAll(goals);
        return true;
    }

    public boolean contribute(String goalId, double amount, String childId) {
        if (amount > bankService.getBalance(childId)) return false;
        List<SavingsGoal> goals = storage.loadAll(SavingsGoal.class);
        for (SavingsGoal g : goals) {
            if (g.getId().equals(goalId)) {
                g.addAmount(amount);
                bankService.withdraw(childId, amount, "Maqsad: " + g.getTitle());
                storage.saveAll(goals);
                return true;
            }
        }
        return false;
    }

    public List<SavingsGoal> getGoals(String childId) {
        List<SavingsGoal> all    = storage.loadAll(SavingsGoal.class);
        List<SavingsGoal> result = new ArrayList<>();
        for (SavingsGoal g : all) {
            if (g.getChildId().equals(childId)) result.add(g);
        }
        return result;
    }
}