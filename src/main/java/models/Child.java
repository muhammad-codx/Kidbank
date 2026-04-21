package models;

public class Child extends User {

    private double balance;
    private String parentId;

    public Child(String id, String name, String email,
                 String password, String parentId) {
        super(id, name, email, password, "child");
        this.balance = 0.0;
        this.parentId = parentId;
    }

    public double getBalance()             { return balance; }
    public String getParentId()            { return parentId; }

    public void addBalance(double amount) {
        if (amount > 0) balance += amount;
    }

    public void subtractBalance(double amount) {
        if (amount > 0 && amount <= balance) balance -= amount;
    }

    @Override
    public String toString() {
        return "Child{id=" + getId() + ", name=" + getName()
                + ", balance=" + balance + "}";
    }
}