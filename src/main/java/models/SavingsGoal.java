package models;

public class SavingsGoal {

    private String id;
    private String title;
    private double targetAmount;
    private double currentAmount;
    private String childId;

    public SavingsGoal(String id, String title,
                       double targetAmount, String childId) {
        this.id            = id;
        this.title         = title;
        this.targetAmount  = targetAmount;
        this.currentAmount = 0.0;
        this.childId       = childId;
    }

    public String getId()            { return id; }
    public String getTitle()         { return title; }
    public double getTargetAmount()  { return targetAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public String getChildId()       { return childId; }

    public void addAmount(double amount) {
        if (amount > 0) currentAmount += amount;
    }

    public double getProgress() {
        return (currentAmount / targetAmount) * 100;
    }

    public double getRemaining() {
        return targetAmount - currentAmount;
    }

    public boolean isComplete() {
        return currentAmount >= targetAmount;
    }

    @Override
    public String toString() {
        return "SavingsGoal{title=" + title
                + ", progress=" + getProgress() + "%}";
    }
}