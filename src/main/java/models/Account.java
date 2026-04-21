package models;

public class Account {

    private String id;
    private String childId;
    private String type;     // "current" yoki "saving"
    private double balance;

    public Account(String id, String childId, String type) {
        this.id      = id;
        this.childId = childId;
        this.type    = type;
        this.balance = 0.0;
    }

    public String getId()      { return id; }
    public String getChildId() { return childId; }
    public String getType()    { return type; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Account{type=" + type + ", balance=" + balance + "}";
    }
}