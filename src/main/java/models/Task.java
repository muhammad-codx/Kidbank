package models;

public class Task {

    private String id;
    private String title;
    private String description;
    private double reward;
    private String status;   // "pending", "completed", "approved", "rejected"
    private String childId;
    private String parentId;

    public Task(String id, String title, String description,
                double reward, String childId, String parentId) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.reward      = reward;
        this.status      = "pending";
        this.childId     = childId;
        this.parentId    = parentId;
    }

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public double getReward()      { return reward; }
    public String getStatus()      { return status; }
    public String getChildId()     { return childId; }
    public String getParentId()    { return parentId; }

    public void setStatus(String status) { this.status = status; }

    public boolean isApproved() { return status.equals("approved"); }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title=" + title
                + ", reward=" + reward + ", status=" + status + "}";
    }
}