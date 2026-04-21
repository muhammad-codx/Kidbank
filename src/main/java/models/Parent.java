package models;

import java.util.ArrayList;
import java.util.List;

public class Parent extends User {

    private List<String> childrenIds;

    // Constructor
    public Parent(String id, String name, String email, String password) {
        super(id, name, email, password, "parent");
        this.childrenIds = new ArrayList<>();
    }

    // Getters
    public List<String> getChildren() {
        return childrenIds;
    }

    // Methods
    public void addChild(String childId) {
        if (!childrenIds.contains(childId)) {
            childrenIds.add(childId);
        }
    }

    public void removeChild(String childId) {
        childrenIds.remove(childId);
    }

    @Override
    public String toString() {
        return "Parent{id=" + getId() + ", name=" + getName() + "}";
    }
}