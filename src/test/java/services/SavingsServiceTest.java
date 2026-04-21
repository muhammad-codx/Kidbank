package services;

import org.junit.jupiter.api.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SavingsServiceTest {

    private static SavingsService savingsService;
    private static BankService    bankService;
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data/";
    private static final String CHILD_ID = "savings-test-child";

    @BeforeAll
    static void setUp() throws Exception {
        Files.createDirectories(Paths.get(DATA_DIR));
        Files.writeString(Paths.get(DATA_DIR + "goals.json"), "[]");
        Files.writeString(Paths.get(DATA_DIR + "transactions.json"), "[]");
        savingsService = new SavingsService();
        bankService    = new BankService();
        bankService.deposit(CHILD_ID, 50000, "Test uchun pul");
    }

    @AfterAll
    static void tearDown() throws Exception {
        Files.writeString(Paths.get(DATA_DIR + "goals.json"), "[]");
        Files.writeString(Paths.get(DATA_DIR + "transactions.json"), "[]");
    }

    @Test
    @Order(1)
    @DisplayName("Jamg'arma maqsad muvaffaqiyatli yaratiladi")
    void testCreateGoalSuccess() {
        boolean result = savingsService.createGoal("Velosiped", 150000, CHILD_ID);
        assertTrue(result, "Maqsad yaratilishi kerak");
    }

    @Test
    @Order(2)
    @DisplayName("Bo'sh nom bilan maqsad yaratib bo'lmaydi")
    void testCreateGoalEmptyTitle() {
        boolean result = savingsService.createGoal("", 100000, CHILD_ID);
        assertFalse(result, "Bo'sh nom bilan maqsad yaratib bo'lmasligi kerak");
    }

    @Test
    @Order(3)
    @DisplayName("Manfiy summa bilan maqsad yaratib bo'lmaydi")
    void testCreateGoalNegativeTarget() {
        boolean result = savingsService.createGoal("Test", -5000, CHILD_ID);
        assertFalse(result, "Manfiy summa bilan maqsad yaratib bo'lmasligi kerak");
    }

    @Test
    @Order(4)
    @DisplayName("Maqsadga pul qo'shish")
    void testContributeSuccess() {
        savingsService.createGoal("Telefon", 200000, CHILD_ID);
        var goals = savingsService.getGoals(CHILD_ID);
        String goalId = goals.get(goals.size() - 1).getId();
        double balBefore = bankService.getBalance(CHILD_ID);
        boolean result = savingsService.contribute(goalId, 10000, CHILD_ID);
        assertTrue(result, "Pul qo'shish muvaffaqiyatli bo'lishi kerak");
        double balAfter = bankService.getBalance(CHILD_ID);
        assertEquals(balBefore - 10000, balAfter, 0.01, "Balans 10000 kamayishi kerak");
    }

    @Test
    @Order(5)
    @DisplayName("Balansdan ko'p pul qo'shib bo'lmaydi")
    void testContributeInsufficientBalance() {
        savingsService.createGoal("Noutbuk", 1000000, CHILD_ID);
        var goals = savingsService.getGoals(CHILD_ID);
        String goalId = goals.get(goals.size() - 1).getId();
        double balance = bankService.getBalance(CHILD_ID);
        boolean result = savingsService.contribute(goalId, balance + 999999, CHILD_ID);
        assertFalse(result, "Balansdan ko'p qo'shib bo'lmasligi kerak");
    }

    @Test
    @Order(6)
    @DisplayName("Bolaning maqsadlari to'g'ri filtrlanadi")
    void testGetGoalsByChild() {
        String otherId = "other-child-savings";
        savingsService.createGoal("Boshqasining maqsadi", 50000, otherId);
        var myGoals = savingsService.getGoals(CHILD_ID);
        for (var g : myGoals) {
            assertEquals(CHILD_ID, g.getChildId(), "Faqat shu bolaning maqsadlari bo'lishi kerak");
        }
    }

    @Test
    @Order(7)
    @DisplayName("Progress to'g'ri hisoblanadi")
    void testProgressCalculation() {
        savingsService.createGoal("Progress test", 100000, CHILD_ID);
        var goals = savingsService.getGoals(CHILD_ID);
        var goal = goals.get(goals.size() - 1);
        savingsService.contribute(goal.getId(), 25000, CHILD_ID);
        var updated = savingsService.getGoals(CHILD_ID);
        var updGoal = updated.stream().filter(g -> g.getId().equals(goal.getId())).findFirst().get();
        assertEquals(25.0, updGoal.getProgress(), 0.01, "Progress 25% bo'lishi kerak");
    }
}
