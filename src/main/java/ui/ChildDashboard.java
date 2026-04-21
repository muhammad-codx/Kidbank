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
import models.SavingsGoal;
import models.Task;
import models.Transaction;
import models.User;
import services.BankService;
import services.ReportService;
import services.SavingsService;
import services.TaskService;

import java.util.List;

public class ChildDashboard {

    private final User user;
    private final BankService    bankService    = new BankService();
    private final TaskService    taskService    = new TaskService();
    private final ReportService  reportService  = new ReportService();
    private final SavingsService savingsService = new SavingsService();

    public ChildDashboard(User user) { this.user = user; }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4FF;");
        root.setTop(buildNavbar());
        root.setLeft(buildSidebar());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox content = new VBox(24);
        content.setPadding(new Insets(28));

        // Welcome
        Label welcome = new Label("Salom, " + user.getName() + "! 👋");
        welcome.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0D2BA8;");
        Label sub = new Label("Bugun qancha pul ishlaysan?");
        sub.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        VBox welcomeBox = new VBox(4, welcome, sub);

        // ── STATS ROW ─────────────────────────────────
        double balance = bankService.getBalance(user.getId());
        double earned  = reportService.getTotalEarned(user.getId());
        double spent   = reportService.getTotalSpent(user.getId());
        List<Task> tasks = taskService.getTasksByChild(user.getId());
        long doneTasks = tasks.stream().filter(t -> t.getStatus().equals("approved")).count();
        long pendingTasks = tasks.stream().filter(t -> t.getStatus().equals("pending")).count();

        HBox statsRow = new HBox(16);
        statsRow.getChildren().addAll(
            statCard("💰", "Balans", String.format("%.0f so'm", balance), "#1A3FD4", "#D6E4FF"),
            statCard("📈", "Ishlagan", String.format("%.0f so'm", earned), "#27AE60", "#D5F5E3"),
            statCard("📉", "Sarflagan", String.format("%.0f so'm", spent), "#E74C3C", "#FADBD8"),
            statCard("✅", "Bajarilgan", doneTasks + " ta", "#7B2FBE", "#EDD9FF"),
            statCard("📋", "Yangi Vazifa", pendingTasks + " ta", "#E67E22", "#FFE9D0")
        );

        // ── BALANCE CARD ─────────────────────────────
        VBox balCard = buildBalanceCard(balance, earned, spent);

        // ── QUICK ACTIONS ─────────────────────────────
        Label actionsLbl = sectionLabel("⚡  Tezkor Harakatlar");
        HBox actions = new HBox(14);
        actions.getChildren().addAll(
            actionBtn("💰 Hamyon", "#1A3FD4", () -> Main.primaryStage.getScene().setRoot(new WalletScreen(user).getView())),
            actionBtn("🎯 Maqsadlar", "#27AE60", () -> Main.primaryStage.getScene().setRoot(new SavingsScreen(user).getView())),
            actionBtn("📊 Statistika", "#7B2FBE", () -> {})
        );

        // ── EARNINGS CHART ────────────────────────────
        Label chartLbl = sectionLabel("📊  Daromad Grafigi");
        VBox chart = buildEarningsChart();

        // ── ACTIVE TASKS ──────────────────────────────
        Label taskLbl = sectionLabel("📋  Mening Vazifalarim");
        VBox taskBox = new VBox(10);
        if (tasks.isEmpty()) {
            taskBox.getChildren().add(emptyLabel("Hozircha vazifa yo'q. Ota-onangiz vazifa berishi kutilmoqda!"));
        } else {
            for (Task t : tasks) taskBox.getChildren().add(taskCard(t));
        }

        // ── SAVINGS GOALS ─────────────────────────────
        Label goalsLbl = sectionLabel("🎯  Jamg'arma Maqsadlarim");
        VBox goalsBox = new VBox(10);
        buildSavingsGoals(goalsBox);

        // ── RECENT TRANSACTIONS ───────────────────────
        Label histLbl = sectionLabel("📊  So'ngi Tranzaksiyalar");
        VBox histBox = new VBox(8);
        buildRecentHistory(histBox);

        content.getChildren().addAll(
            welcomeBox, statsRow, balCard,
            actionsLbl, actions,
            chartLbl, chart,
            taskLbl, taskBox,
            goalsLbl, goalsBox,
            histLbl, histBox
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
        Label role = new Label("👶  Bola");
        role.setStyle("-fx-font-size: 12px; -fx-text-fill: #A8C4FF; -fx-background-color: rgba(255,255,255,0.1);" +
                "-fx-padding: 4 10; -fx-background-radius: 20;");
        Label name = new Label("  " + user.getName());
        name.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;");
        Button logout = new Button("Chiqish");
        logout.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-text-fill: white;" +
                "-fx-border-color: rgba(255,255,255,0.4); -fx-border-radius: 8;" +
                "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 16;");
        logout.setOnAction(e -> Main.primaryStage.getScene().setRoot(new LoginScreen().getView()));
        nav.getChildren().addAll(logo, sp, role, name, new Label("   "), logout);
        return nav;
    }

    // ── SIDEBAR ──────────────────────────────────────
    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.setPrefWidth(200);
        sb.setPadding(new Insets(24, 12, 24, 12));
        sb.setStyle("-fx-background-color: #0D2BA8;");

        String[] items = {"🏠  Dashboard", "📋  Vazifalar", "💰  Hamyon", "🎯  Maqsadlar", "📊  Statistika"};
        for (int i = 0; i < items.length; i++) {
            Button btn = new Button(items[i]);
            btn.setPrefWidth(176);
            boolean active = i == 0;
            btn.setStyle((active
                ? "-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-weight: bold;"
                : "-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.7);") +
                "-fx-background-radius: 8; -fx-alignment: CENTER_LEFT; -fx-padding: 12 16; -fx-font-size: 13px; -fx-cursor: hand;");
            int fi = i;
            btn.setOnAction(e -> {
                if (fi == 2) Main.primaryStage.getScene().setRoot(new WalletScreen(user).getView());
                if (fi == 3) Main.primaryStage.getScene().setRoot(new SavingsScreen(user).getView());
            });
            sb.getChildren().add(btn);
        }

        Region sp = new Region(); VBox.setVgrow(sp, Priority.ALWAYS);
        sb.getChildren().add(sp);

        VBox userInfo = new VBox(4);
        userInfo.setPadding(new Insets(12));
        userInfo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10;");
        Label avatar = new Label(String.valueOf(user.getName().charAt(0)).toUpperCase());
        avatar.setPrefSize(36, 36);
        avatar.setAlignment(Pos.CENTER);
        avatar.setStyle("-fx-background-color: #39FF14; -fx-background-radius: 18;" +
                "-fx-font-weight: bold; -fx-text-fill: #0D2BA8; -fx-font-size: 16px;");
        Label uname = new Label(user.getName());
        uname.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-font-weight: bold;");
        Label uemail = new Label(user.getEmail());
        uemail.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.5);");
        userInfo.getChildren().addAll(avatar, uname, uemail);
        sb.getChildren().add(userInfo);
        return sb;
    }

    // ── STAT CARD ─────────────────────────────────────
    private VBox statCard(String icon, String title, String val, String color, String bg) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 12, 0, 0, 3);");
        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size: 20px; -fx-background-color: " + bg + ";" +
                "-fx-padding: 8; -fx-background-radius: 10;");
        Label tl = new Label(title); tl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        Label vl = new Label(val);   vl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        card.getChildren().addAll(ico, tl, vl);
        return card;
    }

    // ── BALANCE CARD ─────────────────────────────────
    private VBox buildBalanceCard(double balance, double earned, double spent) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: #0D2BA8; -fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(13,43,168,0.4), 20, 0, 0, 6);");

        VBox main = new VBox(8);
        main.setPadding(new Insets(28, 28, 20, 28));
        Label lbl = new Label("💰  Joriy Balansingiz");
        lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.75);");
        Label amt = new Label(String.format("%.0f so'm", balance));
        amt.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: white;");

        HBox subRow = new HBox(24);
        subRow.setPadding(new Insets(16, 28, 20, 28));
        subRow.setStyle("-fx-background-color: rgba(0,0,0,0.15); -fx-background-radius: 0 0 20 20;");
        subRow.getChildren().addAll(
            balSubStat("📈 Ishlagan", String.format("%.0f so'm", earned), "#39FF14"),
            balSubStat("📉 Sarflagan", String.format("%.0f so'm", spent), "#FF6B6B")
        );

        main.getChildren().addAll(lbl, amt);
        card.getChildren().addAll(main, subRow);
        return card;
    }

    private VBox balSubStat(String label, String val, String color) {
        VBox v = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.6);");
        Label vl = new Label(val);  vl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        v.getChildren().addAll(l, vl);
        return v;
    }

    // ── EARNINGS CHART ────────────────────────────────
    private VBox buildEarningsChart() {
        VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 12, 0, 0, 3);");

        List<Transaction> history = bankService.getHistory(user.getId());

        if (history.isEmpty()) {
            container.getChildren().add(emptyLabel("Hali tranzaksiyalar yo'q — vazifa bajarib pul ishlashni boshlang!"));
            return container;
        }

        Label subtitle = new Label("Barcha tranzaksiyalar (kirim yashil, chiqim qizil)");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");

        Canvas canvas = new Canvas(700, 220);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE); gc.fillRect(0, 0, 700, 220);

        int limit = Math.min(history.size(), 20);
        List<Transaction> recent = history.subList(history.size() - limit, history.size());

        double maxAmt = recent.stream().mapToDouble(Transaction::getAmount).max().orElse(1000);
        double barW = (660.0) / limit - 4;
        double maxH = 160;

        // Grid
        gc.setStroke(Color.web("#F5F5F5")); gc.setLineWidth(1);
        for (int i = 1; i <= 4; i++) {
            double y = 20 + maxH / 4 * i;
            gc.strokeLine(30, y, 690, y);
        }

        for (int i = 0; i < recent.size(); i++) {
            Transaction t = recent.get(i);
            boolean cr = t.getType().equals("credit");
            double h = (t.getAmount() / maxAmt) * maxH;
            if (h < 4) h = 4;
            double x = 34 + i * (barW + 4);
            double y = 20 + maxH - h;

            gc.setFill(cr ? Color.web("#27AE60") : Color.web("#E74C3C"));
            gc.fillRoundRect(x, y, barW, h, 6, 6);

            // Amount label
            if (h > 20) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 9));
                gc.fillText(formatNum(t.getAmount()), x + 2, y + h - 4);
            }
        }

        // Y labels
        gc.setFill(Color.web("#AAAAAA")); gc.setFont(Font.font("Arial", 9));
        for (int i = 0; i <= 4; i++) {
            gc.fillText(formatNum(maxAmt * (4 - i) / 4), 0, 20 + maxH / 4 * i + 4);
        }

        HBox legend = new HBox(20); legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(legendItem("Kirim (Credit)", "#27AE60"), legendItem("Chiqim (Debit)", "#E74C3C"));

        ScrollPane sp = new ScrollPane(canvas); sp.setFitToHeight(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setPrefHeight(240);

        container.getChildren().addAll(subtitle, legend, sp);
        return container;
    }

    // ── TASK CARD ─────────────────────────────────────
    private HBox taskCard(Task task) {
        HBox card = new HBox(14);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.CENTER_LEFT);
        String bg = switch (task.getStatus()) { case "approved" -> "#F0FFF4"; case "completed" -> "#FFFBF0"; default -> "#FFFFFF"; };
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 12;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        Label badge = new Label(statusIcon(task.getStatus()));
        badge.setStyle("-fx-font-size: 22px; -fx-background-color: " + statusBg(task.getStatus()) + ";" +
                "-fx-padding: 8; -fx-background-radius: 10;");

        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Label tl = new Label(task.getTitle()); tl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            Label dl = new Label(task.getDescription()); dl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
            info.getChildren().add(dl);
        }
        Label rl = new Label("💰 " + String.format("%.0f", task.getReward()) + " so'm mukofot");
        rl.setStyle("-fx-font-size: 13px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
        info.getChildren().addAll(tl, rl);

        Label sl = new Label(statusText(task.getStatus()));
        sl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + statusColor(task.getStatus()) + ";" +
                "-fx-background-color: " + statusBg(task.getStatus()) + "; -fx-background-radius: 20; -fx-padding: 4 10;");

        card.getChildren().addAll(badge, info, sl);

        if (task.getStatus().equals("pending")) {
            Button doneBtn = new Button("✅  Bajarildi!");
            doneBtn.setStyle("-fx-background-color: #39FF14; -fx-text-fill: #0D2BA8;" +
                    "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 10; -fx-cursor: hand; -fx-padding: 10 18;");
            doneBtn.setOnAction(e -> {
                taskService.completeTask(task.getId());
                Main.primaryStage.getScene().setRoot(new ChildDashboard(user).getView());
            });
            card.getChildren().add(doneBtn);
        }
        return card;
    }

    // ── SAVINGS GOALS ─────────────────────────────────
    private void buildSavingsGoals(VBox box) {
        List<SavingsGoal> goals = savingsService.getGoals(user.getId());
        if (goals.isEmpty()) {
            box.getChildren().add(emptyLabel("Hali maqsad yo'q. Maqsad qo'shish uchun '🎯 Maqsadlar' ga o'ting!"));
            return;
        }
        for (SavingsGoal g : goals) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(16));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");
            HBox top = new HBox(10); top.setAlignment(Pos.CENTER_LEFT);
            Label nl = new Label("🎯 " + g.getTitle()); nl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
            HBox.setHgrow(nl, Priority.ALWAYS); top.getChildren().add(nl);
            if (g.isComplete()) {
                Label done = new Label("✅ Maqsad bajarildi!");
                done.setStyle("-fx-font-size: 12px; -fx-text-fill: #27AE60; -fx-font-weight: bold;" +
                        "-fx-background-color: #D5F5E3; -fx-background-radius: 20; -fx-padding: 4 10;");
                top.getChildren().add(done);
            }
            double pct = Math.min(g.getProgress(), 100);
            ProgressBar bar = new ProgressBar(pct / 100);
            bar.setPrefWidth(Double.MAX_VALUE);
            bar.setStyle("-fx-accent: " + (g.isComplete() ? "#27AE60" : "#39FF14") + ";");
            Label pl = new Label(String.format("%.0f so'm / %.0f so'm  —  %.0f%%", g.getCurrentAmount(), g.getTargetAmount(), pct));
            pl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            card.getChildren().addAll(top, bar, pl);
            box.getChildren().add(card);
        }
    }

    // ── RECENT HISTORY ────────────────────────────────
    private void buildRecentHistory(VBox box) {
        List<Transaction> history = bankService.getHistory(user.getId());
        if (history.isEmpty()) {
            box.getChildren().add(emptyLabel("Hali tranzaksiya yo'q")); return;
        }
        int show = Math.min(history.size(), 7);
        for (int i = history.size() - 1; i >= history.size() - show; i--) {
            Transaction t = history.get(i);
            HBox row = new HBox(14); row.setPadding(new Insets(14)); row.setAlignment(Pos.CENTER_LEFT);
            boolean cr = t.getType().equals("credit");
            row.setStyle("-fx-background-color: " + (cr ? "#F0FFF4" : "#FFF5F5") + "; -fx-background-radius: 10;");
            Label icon = new Label(cr ? "⬆️" : "⬇️"); icon.setStyle("-fx-font-size: 18px;");
            VBox info = new VBox(2); HBox.setHgrow(info, Priority.ALWAYS);
            Label dl = new Label(t.getDescription()); dl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #333;");
            Label dt = new Label(t.getDate()); dt.setStyle("-fx-font-size: 11px; -fx-text-fill: #AAA;");
            info.getChildren().addAll(dl, dt);
            Label al = new Label((cr ? "+" : "−") + String.format("%.0f", t.getAmount()) + " so'm");
            al.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + (cr ? "#27AE60" : "#E74C3C") + ";");
            row.getChildren().addAll(icon, info, al);
            box.getChildren().add(row);
        }
    }

    // ── HELPERS ──────────────────────────────────────
    private Button actionBtn(String text, String color, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;" +
                "-fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 10;" +
                "-fx-padding: 14 24; -fx-cursor: hand;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A1A1A;");
        return l;
    }

    private Label emptyLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 13px; -fx-padding: 16;" +
                "-fx-background-color: #F9FAFB; -fx-background-radius: 10;");
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private HBox legendItem(String label, String color) {
        HBox row = new HBox(6); row.setAlignment(Pos.CENTER_LEFT);
        Label sq = new Label("  ");
        sq.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3; -fx-min-width: 14; -fx-min-height: 14;");
        Label lbl = new Label(label); lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        row.getChildren().addAll(sq, lbl);
        return row;
    }

    private String formatNum(double val) {
        if (val >= 1000000) return String.format("%.1fM", val / 1000000);
        if (val >= 1000)    return String.format("%.0fK", val / 1000);
        return String.format("%.0f", val);
    }

    private String statusIcon(String s)  { return switch (s) { case "pending" -> "📋"; case "completed" -> "⏳"; case "approved" -> "✅"; case "rejected" -> "❌"; default -> "📌"; }; }
    private String statusText(String s)  { return switch (s) { case "pending" -> "🕐 Yangi"; case "completed" -> "⏳ Tasdiq kutmoqda"; case "approved" -> "✅ Tasdiqlandi"; case "rejected" -> "❌ Rad etildi"; default -> s; }; }
    private String statusColor(String s) { return switch (s) { case "approved" -> "#27AE60"; case "completed" -> "#E67E22"; case "rejected" -> "#E74C3C"; default -> "#888888"; }; }
    private String statusBg(String s)    { return switch (s) { case "approved" -> "#D5F5E3"; case "completed" -> "#FEF9E7"; case "rejected" -> "#FADBD8"; default -> "#EBF5FB"; }; }
}
