package services;

import models.Task;
import storage.JsonStorage;
import utils.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskService {

    private JsonStorage storage;
    private BankService bankService;
    private static final String PATH = System.getProperty("user.dir") + "/data/";

    public TaskService() {
        this.storage     = new JsonStorage(PATH + "tasks.json");
        this.bankService = new BankService();
    }

    public boolean createTask(String title, String description,
                              double reward, String childId, String parentId) {
        if (!Validator.isNotEmpty(title) ||
                !Validator.isValidAmount(reward)) return false;

        List<Task> tasks = storage.loadAll(Task.class);
        tasks.add(new Task(UUID.randomUUID().toString(),
                title, description, reward, childId, parentId));
        storage.saveAll(tasks);
        return true;
    }

    public boolean completeTask(String taskId) {
        List<Task> tasks = storage.loadAll(Task.class);
        for (Task t : tasks) {
            if (t.getId().equals(taskId) && t.getStatus().equals("pending")) {
                t.setStatus("completed");
                storage.saveAll(tasks);
                return true;
            }
        }
        return false;
    }

    public boolean approveTask(String taskId) {
        List<Task> tasks = storage.loadAll(Task.class);
        for (Task t : tasks) {
            if (t.getId().equals(taskId) && t.getStatus().equals("completed")) {
                t.setStatus("approved");
                storage.saveAll(tasks);
                bankService.deposit(t.getChildId(), t.getReward(),
                        "Vazifa mukofoti: " + t.getTitle());
                return true;
            }
        }
        return false;
    }

    public boolean rejectTask(String taskId) {
        List<Task> tasks = storage.loadAll(Task.class);
        for (Task t : tasks) {
            if (t.getId().equals(taskId) && t.getStatus().equals("completed")) {
                t.setStatus("pending");
                storage.saveAll(tasks);
                return true;
            }
        }
        return false;
    }

    public List<Task> getTasksByChild(String childId) {
        List<Task> all    = storage.loadAll(Task.class);
        List<Task> result = new ArrayList<>();
        for (Task t : all) {
            if (t.getChildId().equals(childId)) result.add(t);
        }
        return result;
    }

    public List<Task> getTasksByParent(String parentId) {
        List<Task> all    = storage.loadAll(Task.class);
        List<Task> result = new ArrayList<>();
        for (Task t : all) {
            if (t.getParentId().equals(parentId)) result.add(t);
        }
        return result;
    }

    public List<Task> getPendingTasks(String parentId) {
        List<Task> all    = storage.loadAll(Task.class);
        List<Task> result = new ArrayList<>();
        for (Task t : all) {
            if (t.getParentId().equals(parentId) &&
                    t.getStatus().equals("completed")) result.add(t);
        }
        return result;
    }
}