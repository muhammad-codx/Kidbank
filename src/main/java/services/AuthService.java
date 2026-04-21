package services;

import models.Child;
import models.Parent;
import models.User;
import storage.JsonStorage;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthService {

    private JsonStorage parentStorage;
    private JsonStorage childStorage;
    private static final String PATH = System.getProperty("user.dir") + "/data/";

    public AuthService() {
        this.parentStorage = new JsonStorage(PATH + "parents.json");
        this.childStorage  = new JsonStorage(PATH + "children.json");
    }

    public boolean registerParent(String name, String email, String password) {
        if (!Validator.isNotEmpty(name) || !Validator.isValidEmail(email) || !Validator.isNotEmpty(password)) return false;
        List<Parent> parents = parentStorage.loadAll(Parent.class);
        for (Parent p : parents) { if (p.getEmail().equals(email)) return false; }
        parents.add(new Parent(UUID.randomUUID().toString(), name, email, password));
        parentStorage.saveAll(parents);
        return true;
    }

    public boolean registerChild(String name, String email, String password, String parentId) {
        if (!Validator.isNotEmpty(name) || !Validator.isValidEmail(email) || !Validator.isNotEmpty(password)) return false;
        List<Child> children = childStorage.loadAll(Child.class);
        for (Child c : children) { if (c.getEmail().equals(email)) return false; }
        children.add(new Child(UUID.randomUUID().toString(), name, email, password, parentId));
        childStorage.saveAll(children);
        return true;
    }

    public User login(String email, String password) {
        List<Parent> parents = parentStorage.loadAll(Parent.class);
        for (Parent p : parents) { if (p.getEmail().equals(email) && p.getPassword().equals(password)) return p; }
        List<Child> children = childStorage.loadAll(Child.class);
        for (Child c : children) { if (c.getEmail().equals(email) && c.getPassword().equals(password)) return c; }
        return null;
    }

    public Parent findParentById(String id) {
        for (Parent p : parentStorage.loadAll(Parent.class)) { if (p.getId().equals(id)) return p; }
        return null;
    }

    public Child findChildById(String id) {
        for (Child c : childStorage.loadAll(Child.class)) { if (c.getId().equals(id)) return c; }
        return null;
    }

    public List<Child> getChildrenByParent(String parentId) {
        List<Child> all = childStorage.loadAll(Child.class);
        List<Child> result = new ArrayList<>();
        for (Child c : all) { if (c.getParentId() != null && c.getParentId().equals(parentId)) result.add(c); }
        return result;
    }

    public List<Parent> getAllParents() { return parentStorage.loadAll(Parent.class); }
    public List<Child> getAllChildren() { return childStorage.loadAll(Child.class); }
}
