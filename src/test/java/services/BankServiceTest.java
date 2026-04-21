package services;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankServiceTest {

    private static BankService bankService;
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data/";
    private static final String TEST_CHILD_ID = "test-child-001";

    @BeforeAll
    static void setUp() throws Exception {
        Files.createDirectories(Paths.get(DATA_DIR));
        Files.writeString(Paths.get(DATA_DIR + "transactions.json"), "[]");
        bankService = new BankService();
    }

    @AfterAll
    static void tearDown() throws Exception {
        Files.writeString(Paths.get(DATA_DIR + "transactions.json"), "[]");
    }

    // ── DEPOSIT ───────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Pul qo'shish muvaffaqiyatli")
    void testDepositSuccess() {
        bankService.deposit(TEST_CHILD_ID, 10000, "Test deposit");
        double balance = bankService.getBalance(TEST_CHILD_ID);
        assertEquals(10000, balance, 0.01, "Balans 10000 bo'lishi kerak");
    }

    @Test
    @Order(2)
    @DisplayName("Ikki marta pul qo'shish")
    void testDepositMultiple() {
        bankService.deposit(TEST_CHILD_ID, 5000, "Ikkinchi deposit");
        double balance = bankService.getBalance(TEST_CHILD_ID);
        assertEquals(15000, balance, 0.01, "Balans 15000 bo'lishi kerak");
    }

    @Test
    @Order(3)
    @DisplayName("Manfiy summa qo'shib bo'lmaydi")
    void testDepositNegative() {
        double before = bankService.getBalance(TEST_CHILD_ID);
        bankService.deposit(TEST_CHILD_ID, -500, "Manfiy summa");
        double after = bankService.getBalance(TEST_CHILD_ID);
        assertEquals(before, after, 0.01, "Manfiy summa qo'shilmasligi kerak");
    }

    @Test
    @Order(4)
    @DisplayName("Nol summa qo'shib bo'lmaydi")
    void testDepositZero() {
        double before = bankService.getBalance(TEST_CHILD_ID);
        bankService.deposit(TEST_CHILD_ID, 0, "Nol summa");
        double after = bankService.getBalance(TEST_CHILD_ID);
        assertEquals(before, after, 0.01, "Nol summa qo'shilmasligi kerak");
    }

    // ── WITHDRAW ──────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("Pul yechish muvaffaqiyatli")
    void testWithdrawSuccess() {
        double before = bankService.getBalance(TEST_CHILD_ID);
        boolean result = bankService.withdraw(TEST_CHILD_ID, 3000, "Test withdraw");
        assertTrue(result, "Pul yechish muvaffaqiyatli bo'lishi kerak");
        double after = bankService.getBalance(TEST_CHILD_ID);
        assertEquals(before - 3000, after, 0.01, "Balans 3000 kamayishi kerak");
    }

    @Test
    @Order(6)
    @DisplayName("Balansdan ko'p pul yechib bo'lmaydi")
    void testWithdrawInsufficientFunds() {
        double balance = bankService.getBalance(TEST_CHILD_ID);
        boolean result = bankService.withdraw(TEST_CHILD_ID, balance + 1000, "Ko'p yechish");
        assertFalse(result, "Balansdan ko'p yechib bo'lmasligi kerak");
    }

    @Test
    @Order(7)
    @DisplayName("Manfiy summa yechib bo'lmaydi")
    void testWithdrawNegative() {
        boolean result = bankService.withdraw(TEST_CHILD_ID, -1000, "Manfiy");
        assertFalse(result, "Manfiy summa yechib bo'lmasligi kerak");
    }

    @Test
    @Order(8)
    @DisplayName("Nol summa yechib bo'lmaydi")
    void testWithdrawZero() {
        boolean result = bankService.withdraw(TEST_CHILD_ID, 0, "Nol");
        assertFalse(result, "Nol summa yechib bo'lmasligi kerak");
    }

    // ── BALANCE ───────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("Yangi foydalanuvchi balansi 0")
    void testNewUserBalance() {
        double balance = bankService.getBalance("brand-new-user-999");
        assertEquals(0, balance, 0.01, "Yangi user balansi 0 bo'lishi kerak");
    }

    @Test
    @Order(10)
    @DisplayName("Balans to'g'ri hisoblanadi")
    void testBalanceCalculation() {
        String uid = "calc-test-user";
        bankService.deposit(uid, 20000, "D1");
        bankService.deposit(uid, 5000, "D2");
        bankService.withdraw(uid, 3000, "W1");
        double balance = bankService.getBalance(uid);
        assertEquals(22000, balance, 0.01, "Balans 20000+5000-3000=22000 bo'lishi kerak");
    }

    // ── HISTORY ───────────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("Tranzaksiyalar tarixi to'g'ri qaytariladi")
    void testGetHistory() {
        String uid = "history-test-user";
        bankService.deposit(uid, 1000, "H1");
        bankService.deposit(uid, 2000, "H2");
        bankService.withdraw(uid, 500, "H3");
        var history = bankService.getHistory(uid);
        assertEquals(3, history.size(), "3 ta tranzaksiya bo'lishi kerak");
    }

    @Test
    @Order(12)
    @DisplayName("Yangi foydalanuvchi tarixi bo'sh")
    void testEmptyHistory() {
        var history = bankService.getHistory("empty-history-user-xyz");
        assertNotNull(history, "Tarix null bo'lmasligi kerak");
        assertTrue(history.isEmpty(), "Yangi user tarixi bo'sh bo'lishi kerak");
    }

    @Test
    @Order(13)
    @DisplayName("Tranzaksiya turi to'g'ri saqlanadi")
    void testTransactionType() {
        String uid = "type-test-user";
        bankService.deposit(uid, 5000, "Credit test");
        bankService.withdraw(uid, 1000, "Debit test");
        var history = bankService.getHistory(uid);
        assertEquals("credit", history.get(0).getType(), "Birinchi tranzaksiya credit bo'lishi kerak");
        assertEquals("debit",  history.get(1).getType(), "Ikkinchi tranzaksiya debit bo'lishi kerak");
    }
}
