package models;

public class Transaction {

    private String id;
    private String type;        // "credit" yoki "debit"
    private double amount;
    private String date;
    private String childId;
    private String description;

    public Transaction(String id, String type, double amount,
                       String date, String childId, String description) {
        this.id          = id;
        this.type        = type;
        this.amount      = amount;
        this.date        = date;
        this.childId     = childId;
        this.description = description;
    }

    public String getId()          { return id; }
    public String getType()        { return type; }
    public double getAmount()      { return amount; }
    public String getDate()        { return date; }
    public String getChildId()     { return childId; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "Transaction{type=" + type + ", amount="
                + amount + ", date=" + date + "}";
    }
}