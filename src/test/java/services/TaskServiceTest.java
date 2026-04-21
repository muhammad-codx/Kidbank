package services;

import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TaskServiceTest {

    private static TaskService taskService;
    private static BankService bankService;
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data/";
    private static final String PARENT_ID = "test-parent-task";
    private static final String CHILD_ID  = "test-child-task";

    @BeforeAll
    static void setUp() throws Exception {
        Files.createDirectories(Paths.get(DATA_DIR));
        Files.writeString(Paths.get(DATA_DIR + "tasks.json"), "[]");
        Files.writeString(Paths.get(DATA_DIR + "transactions.json"), "[]");
        taskService  = new TaskService();
        bankService  = new BankService();
    }

    @AfterAll
    static void tearDown() throws Exception {
        Files.writeString(Paths.get(DATA_DIR + "tasks.json"), "[]");
        Files.writeString(Paths.get(DATA_DIR + "transactions.json"), "[]");
    }

    @Test
    @Order(1)
    @DisplayName("Vazifa muvaffaqiyatli yaratiladi")
    void testCreateTaskSuccess() {
        boolean result = taskService.createTask("Xona yig'ishtir", "Xonani yig'ishtirish", 5000, CHILD_ID, PARENT_ID);
        assertTrue(result, "Vazifa yaratilishi kerak");
    }

    @Test
    @Order(2)
    @DisplayName("Bo'sh nom bilan vazifa yaratib bo'lmaydi")
    void testCreateTaskEmptyTitle() {
        boolean result = taskService.createTask("", "Tavsif", 3000, CHILD_ID, PARENT_ID);
        assertFalse(result, "Bo'sh nom bilan vazifa yaratib bo'lmasligi kerak");
    }

    @Test
    @Order(3)
    @DisplayName("Manfiy mukofot bilan vazifa yaratib bo'lmaydi")
    void testCreateTaskNegativeReward() {
        boolean result = taskService.createTask("Test", "Tavsif", -500, CHILD_ID, PARENT_ID);
        assertFalse(result, "Manfiy mukofot bilan vazifa yaratib bo'lmasligi kerak");
    }

    @Test
    @Order(4)
    @DisplayName("Bola vazifasini bajarildi deydi")
    void testCompleteTask() {
        taskService.createTask("Idish yuvish", "Idishlarni yuvish", 3000, CHILD_ID, PARENT_ID);
        var tasks = taskService.getTasksByChild(CHILD_ID);
        String taskId = tasks.get(tasks.size() - 1).getId();
        boolean result = taskService.completeTask(taskId);
        assertTrue(result, "Vazifa bajarildi deb belgilanishi kerak");
        var updated = taskService.getTasksByChild(CHILD_ID);
        String status = updated.stream().filter(t -> t.getId().equals(taskId)).findFirst().get().getStatus();
        assertEquals("completed", status, "Status 'completed' bo'lishi kerak");
    }

    @Test
    @Order(5)
    @DisplayName("Ota-ona vazifani tasdiqlaydi va pul o'tkaziladi")
    void testApproveTask() {
        taskService.createTask("Ko'cha supurish", "Ko'chani supurish", 4000, CHILD_ID, PARENT_ID);
        var tasks = taskService.getTasksByChild(CHILD_ID);
        String taskId = tasks.get(tasks.size() - 1).getId();
        taskService.completeTask(taskId);
        double balanceBefore = bankService.getBalance(CHILD_ID);
        boolean result = taskService.approveTask(taskId);
        assertTrue(result, "Tasdiqlash muvaffaqiyatli bo'lishi kerak");
        double balanceAfter = bankService.getBalance(CHILD_ID);
        assertEquals(balanceBefore + 4000, balanceAfter, 0.01, "Balans 4000 oshishi kerak");
    }

    @Test
    @Order(6)
    @DisplayName("Ota-ona vazifani rad etadi")
    void testRejectTask() {
        taskService.createTask("Bog' sug'orish", "Bog'ni sug'orish", 2000, CHILD_ID, PARENT_ID);
        var tasks = taskService.getTasksByChild(CHILD_ID);
        String taskId = tasks.get(tasks.size() - 1).getId();
        taskService.completeTask(taskId);
        boolean result = taskService.rejectTask(taskId);
        assertTrue(result, "Rad etish muvaffaqiyatli bo'lishi kerak");
        var updated = taskService.getTasksByChild(CHILD_ID);
        String status = updated.stream().filter(t -> t.getId().equals(taskId)).findFirst().get().getStatus();
        assertEquals("pending", status, "Rad etilgandan keyin status 'pending' bo'lishi kerak");
    }

    @Test
    @Order(7)
    @DisplayName("Bolaning vazifalari filtrlanadi")
    void testGetTasksByChild() {
        String otherId = "other-child-999";
        taskService.createTask("Boshqa bola vazifasi", "", 1000, otherId, PARENT_ID);
        var myTasks = taskService.getTasksByChild(CHILD_ID);
        for (var t : myTasks) {
            assertEquals(CHILD_ID, t.getChildId(), "Faqat shu bolaning vazifalari qaytarilishi kerak");
        }
    }

    @Test
    @Order(8)
    @DisplayName("Tasdiq kutayotgan vazifalar filtrlanadi")
    void testGetPendingTasks() {
        taskService.createTask("Pending vazifa", "", 1500, CHILD_ID, PARENT_ID);
        var tasks = taskService.getTasksByChild(CHILD_ID);
        String taskId = tasks.get(tasks.size() - 1).getId();
        taskService.completeTask(taskId);
        var pending = taskService.getPendingTasks(PARENT_ID);
        assertTrue(pending.size() > 0, "Kamida 1 ta pending vazifa bo'lishi kerak");
        for (var t : pending) {
            assertEquals("completed", t.getStatus(), "Pending vazifalar 'completed' statusda bo'lishi kerak");
            assertEquals(PARENT_ID, t.getParentId(), "Faqat shu ota-onaning vazifalari bo'lishi kerak");
        }
    }
}
