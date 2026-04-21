package models;

public class SpendingPlan {

    private String id;
    private String title;
    private String category;
    private double amount;
    private String childId;

    public SpendingPlan(String id, String title, String category,
                        double amount, String childId) {
        this.id       = id;
        this.title    = title;
        this.category = category;
        this.amount   = amount;
        this.childId  = childId;
    }

    public String getId()       { return id; }
    public String getTitle()    { return title; }
    public String getCategory() { return category; }
    public double getAmount()   { return amount; }
    public String getChildId()  { return childId; }

    public boolean isAffordable(double balance) {
        return balance >= amount;
    }

    public double getRemaining(double balance) {
        return amount - balance;
    }

    public int getNeededTasks(double avgReward) {
        if (avgReward <= 0) return 0;
        return (int) Math.ceil(getRemaining(0) / avgReward);
    }

    @Override
    public String toString() {
        return "SpendingPlan{title=" + title + ", amount=" + amount + "}";
    }
}