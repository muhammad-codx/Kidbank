package services;

import models.Child;
import models.Parent;
import models.User;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthServiceTest {

    private static AuthService authService;
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data/";

    @BeforeAll
    static void setUp() throws Exception {
        // test uchun bo'sh JSON fayllar yaratish
        Files.createDirectories(Paths.get(DATA_DIR));
        Files.writeString(Paths.get(DATA_DIR + "parents.json"), "[]");
        Files.writeString(Paths.get(DATA_DIR + "children.json"), "[]");
        authService = new AuthService();
    }

    @AfterAll
    static void tearDown() throws Exception {
        // test tugagach tozalash
        Files.writeString(Paths.get(DATA_DIR + "parents.json"), "[]");
        Files.writeString(Paths.get(DATA_DIR + "children.json"), "[]");
    }

    // ── REGISTER PARENT ──────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Ota-ona muvaffaqiyatli ro'yxatdan o'tishi")
    void testRegisterParentSuccess() {
        boolean result = authService.registerParent("Ali", "ali@mail.com", "1234");
        assertTrue(result, "Yangi parent ro'yxatdan o'tishi kerak");
    }

    @Test
    @Order(2)
    @DisplayName("Duplicate email bilan ro'yxatdan o'tib bo'lmaydi")
    void testRegisterParentDuplicateEmail() {
        authService.registerParent("Vali", "vali@mail.com", "5678");
        boolean result = authService.registerParent("Vali2", "vali@mail.com", "9999");
        assertFalse(result, "Duplicate email qabul qilinmasligi kerak");
    }

    @Test
    @Order(3)
    @DisplayName("Bo'sh ism bilan ro'yxatdan o'tib bo'lmaydi")
    void testRegisterParentEmptyName() {
        boolean result = authService.registerParent("", "empty@mail.com", "1234");
        assertFalse(result, "Bo'sh ism qabul qilinmasligi kerak");
    }

    @Test
    @Order(4)
    @DisplayName("Noto'g'ri email bilan ro'yxatdan o'tib bo'lmaydi")
    void testRegisterParentInvalidEmail() {
        boolean result = authService.registerParent("Test", "notanemail", "1234");
        assertFalse(result, "Noto'g'ri email qabul qilinmasligi kerak");
    }

    @Test
    @Order(5)
    @DisplayName("Bo'sh parol bilan ro'yxatdan o'tib bo'lmaydi")
    void testRegisterParentEmptyPassword() {
        boolean result = authService.registerParent("Test", "test2@mail.com", "");
        assertFalse(result, "Bo'sh parol qabul qilinmasligi kerak");
    }

    // ── REGISTER CHILD ───────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("Bola muvaffaqiyatli ro'yxatdan o'tishi")
    void testRegisterChildSuccess() {
        boolean result = authService.registerChild("Bobur", "bobur@mail.com", "pass123", "parent-1");
        assertTrue(result, "Yangi child ro'yxatdan o'tishi kerak");
    }

    @Test
    @Order(7)
    @DisplayName("Duplicate email bilan bola ro'yxatdan o'tib bo'lmaydi")
    void testRegisterChildDuplicateEmail() {
        authService.registerChild("Kamol", "kamol@mail.com", "pass", "parent-1");
        boolean result = authService.registerChild("Kamol2", "kamol@mail.com", "pass2", "parent-2");
        assertFalse(result, "Duplicate email qabul qilinmasligi kerak");
    }

    // ── LOGIN ─────────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("Parent login muvaffaqiyatli")
    void testLoginParentSuccess() {
        authService.registerParent("Sardor", "sardor@mail.com", "mypass");
        User user = authService.login("sardor@mail.com", "mypass");
        assertNotNull(user, "Login muvaffaqiyatli bo'lishi kerak");
        assertEquals("parent", user.getRole(), "Rol 'parent' bo'lishi kerak");
        assertEquals("Sardor", user.getName(), "Ism to'g'ri bo'lishi kerak");
    }

    @Test
    @Order(9)
    @DisplayName("Child login muvaffaqiyatli")
    void testLoginChildSuccess() {
        authService.registerChild("Zafar", "zafar@mail.com", "childpass", "some-parent");
        User user = authService.login("zafar@mail.com", "childpass");
        assertNotNull(user, "Child login muvaffaqiyatli bo'lishi kerak");
        assertEquals("child", user.getRole(), "Rol 'child' bo'lishi kerak");
    }

    @Test
    @Order(10)
    @DisplayName("Noto'g'ri parol bilan login bo'lmaydi")
    void testLoginWrongPassword() {
        authService.registerParent("Nodir", "nodir@mail.com", "correctpass");
        User user = authService.login("nodir@mail.com", "wrongpass");
        assertNull(user, "Noto'g'ri parol bilan login null qaytarishi kerak");
    }

    @Test
    @Order(11)
    @DisplayName("Mavjud bo'lmagan email bilan login bo'lmaydi")
    void testLoginNotFound() {
        User user = authService.login("notexist@mail.com", "anypass");
        assertNull(user, "Mavjud bo'lmagan user null qaytarishi kerak");
    }

    @Test
    @Order(12)
    @DisplayName("Bo'sh email bilan login bo'lmaydi")
    void testLoginEmptyEmail() {
        User user = authService.login("", "1234");
        assertNull(user, "Bo'sh email bilan login null qaytarishi kerak");
    }

    // ── FIND BY ID ────────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("Parent ID bo'yicha topiladi")
    void testFindParentById() {
        authService.registerParent("Husan", "husan@mail.com", "pass");
        User loggedIn = authService.login("husan@mail.com", "pass");
        assertNotNull(loggedIn);
        Parent found = authService.findParentById(loggedIn.getId());
        assertNotNull(found, "Parent ID bo'yicha topilishi kerak");
        assertEquals("Husan", found.getName());
    }

    @Test
    @Order(14)
    @DisplayName("Mavjud bo'lmagan ID bilan parent topilmaydi")
    void testFindParentByIdNotFound() {
        Parent found = authService.findParentById("non-existent-id");
        assertNull(found, "Mavjud bo'lmagan ID null qaytarishi kerak");
    }

    // ── GET CHILDREN BY PARENT ────────────────────────────────────────

    @Test
    @Order(15)
    @DisplayName("Ota-onaning bolalari topiladi")
    void testGetChildrenByParent() {
        authService.registerParent("Muxammad", "muxammad@mail.com", "pass");
        User parent = authService.login("muxammad@mail.com", "pass");
        assertNotNull(parent);
        authService.registerChild("Child1", "child1@mail.com", "pass", parent.getId());
        authService.registerChild("Child2", "child2@mail.com", "pass", parent.getId());
        var children = authService.getChildrenByParent(parent.getId());
        assertEquals(2, children.size(), "2 ta bola topilishi kerak");
    }
}
