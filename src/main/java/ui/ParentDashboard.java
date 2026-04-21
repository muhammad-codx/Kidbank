package ui;

import app.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import models.Child;
import models.Task;
import models.Transaction;
import models.User;
import services.AuthService;
import services.BankService;
import services.ReportService;
import services.TaskService;

import java.util.List;

public class ParentDashboard {

    private final User user;
    private final TaskService   taskService   = new TaskService();
    private final AuthService   authService   = new AuthService();
    private final BankService   bankService   = new BankService();
    private final ReportService reportService = new ReportService();

    public ParentDashboard(User user) { this.user = user; }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4FF;");
        root.setTop(buildNavbar());

        // ── SIDEBAR ──────────────────────────────────
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        // ── MAIN CONTENT ─────────────────────────────
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox content = new VBox(24);
        content.setPadding(new Insets(28));

        // Welcome section
        HBox welcomeRow = new HBox(16);
        welcomeRow.setAlignment(Pos.CENTER_LEFT);
        VBox welcomeText = new VBox(4);
        Label welcome = new Label("Xush kelibsiz, " + user.getName() + "! 👋");
        welcome.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0D2BA8;");
        Label subtitle = new Label("Bolalaringizning moliyaviy faoliyatini kuzating");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        welcomeText.getChildren().addAll(welcome, subtitle);
        welcomeRow.getChildren().add(welcomeText);

        // ── STATS ROW ─────────────────────────────────
        List<Child>  children     = authService.getChildrenByParent(user.getId());
        List<Task>   allTasks     = taskService.getTasksByParent(user.getId());
        List<Task>   pendingTasks = taskService.getPendingTasks(user.getId());
        long approved = allTasks.stream().filter(t -> t.getStatus().equals("approved")).count();
        double totalPaidOut = allTasks.stream()
            .filter(t -> t.getStatus().equals("approved"))
            .mapToDouble(Task::getReward).sum();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            bigStatCard("👨‍👩‍👦", "Bolalar", String.valueOf(children.size()), "#1A3FD4", "#D6E4FF"),
            bigStatCard("📋", "Jami Vazifalar", String.valueOf(allTasks.size()), "#7B2FBE", "#EDD9FF"),
            bigStatCard("⏳", "Tasdiq Kutmoqda", String.valueOf(pendingTasks.size()), "#E67E22", "#FFE9D0"),
            bigStatCard("✅", "Tasdiqlangan", String.valueOf(approved), "#27AE60", "#D5F5E3"),
            bigStatCard("💰", "To'langan Pul", String.format("%.0f", totalPaidOut), "#C0392B", "#FADBD8")
        );

        // ── CHILDREN PROFILES ─────────────────────────
        Label childLbl = sectionLabel("👨‍👩‍👦  Bolalar Profillari");
        VBox childrenSection = buildChildrenProfiles(children);

        // ── BAR CHART ─────────────────────────────────
        Label chartLbl = sectionLabel("📊  Bolalar Daromadi — Bar Chart");
        VBox chartBox = buildBarChart(children);

        // ── PENDING TASKS ─────────────────────────────
        Label pendingLbl = sectionLabel("⏳  Tasdiq Kutayotgan Vazifalar");
        VBox pendingBox = new VBox(10);
        if (pendingTasks.isEmpty()) {
            pendingBox.getChildren().add(emptyLabel("Hozircha tasdiq kutayotgan vazifa yo'q ✅"));
        } else {
            for (Task t : pendingTasks) pendingBox.getChildren().add(pendingCard(t));
        }

        // ── ADD TASK BUTTON ───────────────────────────
        Button addTaskBtn = new Button("＋  Yangi Vazifa Qo'shish");
        addTaskBtn.setStyle("-fx-background-color: #1A3FD4; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10;" +
                "-fx-padding: 14 28; -fx-cursor: hand;");
        addTaskBtn.setOnAction(e -> showAddTaskDialog());

        // ── ALL TASKS ─────────────────────────────────
        Label allLbl = sectionLabel("📋  Barcha Vazifalar");
        VBox allBox = new VBox(8);
        buildAllTasks(allBox, allTasks);

        content.getChildren().addAll(
            welcomeRow, statsRow,
            childLbl, childrenSection,
            chartLbl, chartBox,
            pendingLbl, pendingBox,
            addTaskBtn,
            allLbl, allBox
        );

        scroll.setContent(content);
        root.setCenter(scroll);
        return root;
    }

    // ── NAVBAR ───────────────────────────────────────
    private HBox buildNavbar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(16, 28, 16, 28));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color: #0D2BA8;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);");
        Label logo = new Label("🏦  KidBank");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label role = new Label("👨‍👩‍👦  Ota-ona");
        role.setStyle("-fx-font-size: 12px; -fx-text-fill: #A8C4FF; -fx-background-color: rgba(255,255,255,0.1);" +
                "-fx-padding: 4 10; -fx-background-radius: 20;");
        Label name = new Label("  " + user.getName());
        name.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
        Button logout = new Button("Chiqish");
        logout.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white;" +
                "-fx-border-color: rgba(255,255,255,0.4); -fx-border-radius: 8;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16; -fx-font-size: 13px;");
        logout.setOnAction(e -> Main.primaryStage.getScene().setRoot(new LoginScreen().getView()));
        nav.getChildren().addAll(logo, sp, role, name, new Label("   "), logout);
        return nav;
    }

    // ── SIDEBAR ──────────────────────────────────────
    private VBox buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color: #0D2BA8;");

        String[] labels = {"🏠  Dashboard", "👨‍👩‍👦  Bolalar", "📋  Vazifalar", "📊  Statistika", "⚙️  Sozlamalar"};
        for (int i = 0; i < labels.length; i++) {
            Button btn = new Button(labels[i]);
            btn.setPrefWidth(176);
            if (i == 0) {
                btn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white;" +
                        "-fx-background-radius: 8; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16;" +
                        "-fx-font-size: 13px; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.7);" +
                        "-fx-background-radius: 8; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16;" +
                        "-fx-font-size: 13px; -fx-cursor: hand;");
            }
            sidebar.getChildren().add(btn);
        }

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        sidebar.getChildren().add(sp);

        // User info at bottom
        VBox userInfo = new VBox(4);
        userInfo.setPadding(new Insets(12));
        userInfo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");
        Label uname = new Label("👤  " + user.getName());
        uname.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;");
        Label uemail = new Label(user.getEmail());
        uemail.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
        userInfo.getChildren().addAll(uname, uemail);
        sidebar.getChildren().add(userInfo);

        return sidebar;
    }

    // ── BIG STAT CARD ─────────────────────────────────
    private VBox bigStatCard(String icon, String title, String val, String color, String bg) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 3);");

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 22px; -fx-background-color: " + bg + ";" +
                "-fx-padding: 8; -fx-background-radius: 10;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        Label valLbl = new Label(val);
        valLbl.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(iconLbl, titleLbl, valLbl);
        return card;
    }

    // ── CHILDREN PROFILES ────────────────────────────
    private VBox buildChildrenProfiles(List<Child> children) {
        VBox box = new VBox(12);
        if (children.isEmpty()) {
            box.getChildren().add(emptyLabel("Hali hech bir bola bog'lanmagan. Bola ro'yxatdan o'tganda ID sini kiritsin."));
            return box;
        }

        for (Child c : children) {
            HBox card = new HBox(20);
            card.setPadding(new Insets(20));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 14;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 3);");

            // Avatar
            Label avatar = new Label(String.valueOf(c.getName().charAt(0)).toUpperCase());
            avatar.setPrefSize(52, 52);
            avatar.setAlignment(Pos.CENTER);
            avatar.setStyle("-fx-background-color: #D6E4FF; -fx-background-radius: 26;" +
                    "-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1A3FD4;");

            // Info
            VBox info = new VBox(4);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label nameLbl = new Label(c.getName());
            nameLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
            Label emailLbl = new Label("📧 " + c.getEmail());
            emailLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            Label idLbl = new Label("🪪 ID: " + c.getId().substring(0, Math.min(16, c.getId().length())) + "...");
            idLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
            info.getChildren().addAll(nameLbl, emailLbl, idLbl);

            // Stats
            double balance  = bankService.getBalance(c.getId());
            double earned   = reportService.getTotalEarned(c.getId());
            double spent    = reportService.getTotalSpent(c.getId());
            List<Task> cTasks = taskService.getTasksByChild(c.getId());
            long doneTasks = cTasks.stream().filter(t -> t.getStatus().equals("approved")).count();

            VBox stats = new VBox(6);
            stats.setAlignment(Pos.CENTER_RIGHT);
            stats.getChildren().addAll(
                miniStat("💰 Balans",  String.format("%.0f so'm", balance), "#1A3FD4"),
                miniStat("📈 Ishlagan", String.format("%.0f so'm", earned),  "#27AE60"),
                miniStat("📉 Sarflagan", String.format("%.0f so'm", spent),   "#E74C3C"),
                miniStat("✅ Vazifalar", doneTasks + " ta",                    "#7B2FBE")
            );

            card.getChildren().addAll(avatar, info, stats);
            box.getChildren().add(card);
        }
        return box;
    }

    // ── BAR CHART ─────────────────────────────────────
    private VBox buildBarChart(List<Child> children) {
        VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");

        if (children.isEmpty()) {
            container.getChildren().add(emptyLabel("Grafik uchun bolalar ma'lumoti yo'q"));
            return container;
        }

        Label title = new Label("Har bir bolaning moliyaviy ko'rsatkichlari");
        title.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // Legend
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
            legendItem("Ishlagan", "#27AE60"),
            legendItem("Sarflagan", "#E74C3C"),
            legendItem("Balans", "#1A3FD4")
        );

        int canvasWidth = Math.max(700, children.size() * 160);
        Canvas canvas = new Canvas(canvasWidth, 280);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvasWidth, 280);

        // Grid lines
        gc.setStroke(Color.web("#F0F0F0"));
        gc.setLineWidth(1);
        for (int i = 0; i <= 5; i++) {
            double y = 30 + (200.0 / 5) * i;
            gc.strokeLine(40, y, canvasWidth - 20, y);
        }

        // Find max value for scale
        double maxVal = 1000;
        for (Child c : children) {
            double earned = reportService.getTotalEarned(c.getId());
            double spent  = reportService.getTotalSpent(c.getId());
            double bal    = bankService.getBalance(c.getId());
            maxVal = Math.max(maxVal, Math.max(earned, Math.max(spent, bal)));
        }

        double barW = 28;
        double groupW = (double)(canvasWidth - 60) / children.size();

        for (int i = 0; i < children.size(); i++) {
            Child c = children.get(i);
            double earned  = reportService.getTotalEarned(c.getId());
            double spent   = reportService.getTotalSpent(c.getId());
            double balance = bankService.getBalance(c.getId());

            double x = 50 + i * groupW + groupW / 2;

            // Draw bars
            drawBar(gc, x - barW - 2, earned,  maxVal, 200, barW, "#27AE60");
            drawBar(gc, x + 2,        spent,   maxVal, 200, barW, "#E74C3C");
            drawBar(gc, x + barW + 6, balance, maxVal, 200, barW, "#1A3FD4");

            // Child name
            gc.setFill(Color.web("#333333"));
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            String name = c.getName().length() > 8 ? c.getName().substring(0, 8) + ".." : c.getName();
            gc.fillText(name, x - 20, 260);
        }

        // Y-axis labels
        gc.setFill(Color.web("#999999"));
        gc.setFont(Font.font("Arial", 10));
        for (int i = 0; i <= 5; i++) {
            double val = maxVal * (5 - i) / 5;
            double y = 30 + (200.0 / 5) * i;
            gc.fillText(formatNum(val), 0, y + 4);
        }

        ScrollPane chartScroll = new ScrollPane(canvas);
        chartScroll.setFitToHeight(true);
        chartScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        chartScroll.setPrefHeight(300);

        container.getChildren().addAll(title, legend, chartScroll);
        return container;
    }

    private void drawBar(GraphicsContext gc, double x, double val, double maxVal, double maxH, double w, String color) {
        double h = (val / maxVal) * maxH;
        if (h < 2) h = 2;
        double y = 30 + maxH - h;
        gc.setFill(Color.web(color));
        gc.fillRoundRect(x, y, w, h, 6, 6);

        // Value on top
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("Arial", 9));
        if (val > 0) gc.fillText(formatNum(val), x, y - 4);
    }

    private String formatNum(double val) {
        if (val >= 1000000) return String.format("%.1fM", val / 1000000);
        if (val >= 1000)    return String.format("%.0fK", val / 1000);
        return String.format("%.0f", val);
    }

    private HBox legendItem(String label, String color) {
        HBox row = new HBox(6); row.setAlignment(Pos.CENTER_LEFT);
        Label sq = new Label("  ");
        sq.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3; -fx-min-width: 16; -fx-min-height: 16;");
        Label lbl = new Label(label); lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        row.getChildren().addAll(sq, lbl);
        return row;
    }

    // ── PENDING TASK CARD ─────────────────────────────
    private HBox pendingCard(Task task) {
        HBox card = new HBox(14);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
                "-fx-border-color: #FFE082; -fx-border-width: 2; -fx-border-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);");

        Label badge = new Label("⏳");
        badge.setStyle("-fx-font-size: 20px; -fx-background-color: #FFF9E6;" +
                "-fx-padding: 8; -fx-background-radius: 8;");

        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Label tl = new Label(task.getTitle()); tl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        Label rl = new Label("Mukofot: " + String.format("%.0f", task.getReward()) + " so'm");
        rl.setStyle("-fx-font-size: 13px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
        info.getChildren().addAll(tl, rl);

        Button approve = new Button("✅  Tasdiqlash");
        approve.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 16;");
        approve.setOnAction(e -> { taskService.approveTask(task.getId()); refresh(); });

        Button reject = new Button("❌  Rad etish");
        reject.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 16;");
        reject.setOnAction(e -> { taskService.rejectTask(task.getId()); refresh(); });

        card.getChildren().addAll(badge, info, approve, reject);
        return card;
    }

    // ── ALL TASKS ─────────────────────────────────────
    private void buildAllTasks(VBox box, List<Task> tasks) {
        box.getChildren().clear();
        if (tasks.isEmpty()) { box.getChildren().add(emptyLabel("Hali vazifa qo'shilmagan")); return; }

        // Header row
        HBox hdr = new HBox(0); hdr.setPadding(new Insets(8, 14, 8, 14));
        hdr.setStyle("-fx-background-color: #F0F4FF; -fx-background-radius: 8;");
        addHdrCell(hdr, "Vazifa nomi", 280);
        addHdrCell(hdr, "Bola", 150);
        addHdrCell(hdr, "Mukofot", 120);
        addHdrCell(hdr, "Holat", 160);
        box.getChildren().add(hdr);

        for (Task t : tasks) {
            HBox row = new HBox(0); row.setPadding(new Insets(12, 14, 12, 14)); row.setAlignment(Pos.CENTER_LEFT);
            String bg = switch (t.getStatus()) {
                case "approved" -> "#F0FFF4"; case "completed" -> "#FFFBF0";
                case "rejected" -> "#FFF5F5"; default -> "#FFFFFF";
            };
            row.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10;" +
                    "-fx-border-color: #EEEEEE; -fx-border-width: 0 0 1 0;");

            Label tl = new Label(t.getTitle()); tl.setPrefWidth(280); tl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Find child name
            String childName = "—";
            Child c = authService.findChildById(t.getChildId());
            if (c != null) childName = c.getName();
            Label cl = new Label(childName); cl.setPrefWidth(150); cl.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

            Label rl = new Label(String.format("%.0f so'm", t.getReward())); rl.setPrefWidth(120); rl.setStyle("-fx-font-size: 13px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");

            Label badge = new Label(statusText(t.getStatus())); badge.setPrefWidth(160);
            badge.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + statusColor(t.getStatus()) + ";" +
                    "-fx-background-color: " + statusBg(t.getStatus()) + "; -fx-background-radius: 20; -fx-padding: 4 10;");

            row.getChildren().addAll(tl, cl, rl, badge);
            box.getChildren().add(row);
        }
    }

    private void addHdrCell(HBox hdr, String text, double w) {
        Label l = new Label(text); l.setPrefWidth(w);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #888; -fx-font-weight: bold;");
        hdr.getChildren().add(l);
    }

    // ── ADD TASK DIALOG ───────────────────────────────
    private void showAddTaskDialog() {
        List<Child> children = authService.getChildrenByParent(user.getId());
        if (children.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Hali bola bog'lanmagan!").show(); return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Yangi Vazifa Qo'shish");
        ButtonType saveBtn = new ButtonType("✅ Saqlash", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(420);

        VBox form = new VBox(12); form.setPadding(new Insets(24));

        TextField titleF  = styledField("Vazifa nomi (masalan: Xona yig'ishtirish)");
        TextField descF   = styledField("Tavsif (ixtiyoriy)");
        TextField rewardF = styledField("Mukofot summasi (so'm)");

        ComboBox<String> childCombo = new ComboBox<>();
        for (Child c : children) childCombo.getItems().add(c.getName() + "  ||  " + c.getId());
        childCombo.setPromptText("Bola tanlang...");
        childCombo.setPrefWidth(380);

        Label errLbl = new Label(""); errLbl.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 12px;");

        form.getChildren().addAll(
            fieldLabel("📌 Vazifa nomi:"), titleF,
            fieldLabel("📝 Tavsif:"), descF,
            fieldLabel("💰 Mukofot summasi:"), rewardF,
            fieldLabel("👶 Bola tanlang:"), childCombo,
            errLbl
        );
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    String ttl = titleF.getText().trim();
                    String dsc = descF.getText().trim();
                    double rwd = Double.parseDouble(rewardF.getText().trim());
                    String sel = childCombo.getValue();
                    if (ttl.isEmpty() || sel == null) { errLbl.setText("❗ Barcha majburiy maydonlarni to'ldiring!"); return null; }
                    if (rwd <= 0) { errLbl.setText("❗ Mukofot summasi musbat bo'lishi kerak!"); return null; }
                    String cid = sel.split("\\|\\|")[1].trim();
                    taskService.createTask(ttl, dsc, rwd, cid, user.getId());
                    return true;
                } catch (NumberFormatException ex) { errLbl.setText("❗ Mukofot summasi raqam bo'lishi kerak!"); return null; }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(r -> { if (r) refresh(); });
    }

    // ── HELPERS ──────────────────────────────────────
    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        return l;
    }

    private Label emptyLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px; -fx-padding: 16;" +
                "-fx-background-color: #F9FAFB; -fx-background-radius: 10;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private HBox miniStat(String label, String val, String color) {
        HBox row = new HBox(8); row.setAlignment(Pos.CENTER_RIGHT);
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
        Label v = new Label(val);   v.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        row.getChildren().addAll(l, v);
        return row;
    }

    private TextField styledField(String prompt) {
        TextField f = new TextField(); f.setPromptText(prompt); f.setPrefHeight(42); f.setPrefWidth(380);
        f.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #E0E8FF;" +
                "-fx-border-width: 1.5; -fx-padding: 0 12; -fx-font-size: 14px;");
        return f;
    }

    private Label fieldLabel(String text) {
        Label l = new Label(text); l.setStyle("-fx-font-size: 13px; -fx-text-fill: #444; -fx-font-weight: bold;");
        return l;
    }

    private void refresh() { Main.primaryStage.getScene().setRoot(new ParentDashboard(user).getView()); }

    private String statusText(String s) { return switch (s) { case "pending" -> "🕐 Kutilmoqda"; case "completed" -> "⏳ Tasdiq kutmoqda"; case "approved" -> "✅ Tasdiqlandi"; case "rejected" -> "❌ Rad etildi"; default -> s; }; }
    private String statusColor(String s) { return switch (s) { case "approved" -> "#27AE60"; case "completed" -> "#E67E22"; case "rejected" -> "#E74C3C"; default -> "#888888"; }; }
    private String statusBg(String s) { return switch (s) { case "approved" -> "#E8F8F0"; case "completed" -> "#FFF3CD"; case "rejected" -> "#FDECEA"; default -> "#F5F5F5"; }; }
}
