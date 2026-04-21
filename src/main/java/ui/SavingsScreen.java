package ui;

import app.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.SavingsGoal;
import models.User;
import services.BankService;
import services.SavingsService;

import java.util.List;

public class SavingsScreen {

    private final User user;
    private final SavingsService savingsService = new SavingsService();
    private final BankService    bankService    = new BankService();

    public SavingsScreen(User user) { this.user = user; }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4FF;");
        root.setTop(buildNavbar());

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        Button back = new Button("← Orqaga");
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: #1A3FD4; -fx-cursor: hand; -fx-font-size: 13px;");
        back.setOnAction(e -> Main.primaryStage.getScene().setRoot(new ChildDashboard(user).getView()));

        Label title = new Label("🎯 Jamg'arma Maqsadlarim");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0D2BA8;");

        double balance = bankService.getBalance(user.getId());
        Label balLbl = new Label(String.format("💰 Joriy balans: %.0f so'm", balance));
        balLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        Button addBtn = new Button("+ Yangi Maqsad");
        addBtn.setStyle("-fx-background-color: #1A3FD4; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 24; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showAddGoalDialog());

        Label goalsLbl = new Label("📋 Maqsadlar");
        goalsLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox goalsBox = new VBox(14);
        buildGoals(goalsBox);

        content.getChildren().addAll(back, title, balLbl, addBtn, goalsLbl, goalsBox);
        scroll.setContent(content);
        root.setCenter(scroll);
        return root;
    }

    private void buildGoals(VBox box) {
        box.getChildren().clear();
        List<SavingsGoal> goals = savingsService.getGoals(user.getId());
        if (goals.isEmpty()) {
            Label l = new Label("Hali maqsad qo'shilmagan"); l.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            box.getChildren().add(l); return;
        }
        for (SavingsGoal g : goals) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(18));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 10, 0, 0, 3);");

            Label nl = new Label("🎯 " + g.getTitle()); nl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0D2BA8;");
            double progress = Math.min(g.getProgress(), 100);
            ProgressBar bar = new ProgressBar(progress / 100);
            bar.setPrefWidth(Double.MAX_VALUE);
            bar.setStyle("-fx-accent: #39FF14;");
            Label pl = new Label(String.format("%.0f so'm / %.0f so'm (%.0f%%)", g.getCurrentAmount(), g.getTargetAmount(), progress));
            pl.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
            Label rl;
            if (g.isComplete()) {
                rl = new Label("✅ Maqsadga yetdingiz!"); rl.setStyle("-fx-font-size: 13px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
            } else {
                rl = new Label(String.format("Qoldi: %.0f so'm", g.getRemaining())); rl.setStyle("-fx-font-size: 13px; -fx-text-fill: #E74C3C;");
            }
            Button cb = new Button("💰 Pul Qo'shish");
            cb.setStyle("-fx-background-color: #39FF14; -fx-text-fill: #0D2BA8;" +
                    "-fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 14;");
            cb.setOnAction(e -> showContributeDialog(g));
            if (g.isComplete()) cb.setDisable(true);
            card.getChildren().addAll(nl, bar, pl, rl, cb);
            box.getChildren().add(card);
        }
    }

    private void showAddGoalDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Yangi Maqsad");
        ButtonType saveBtn = new ButtonType("Saqlash", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        VBox form = new VBox(10); form.setPadding(new Insets(16));
        TextField tl = new TextField(); tl.setPromptText("Maqsad nomi (masalan: Velosiped)");
        TextField am = new TextField(); am.setPromptText("Maqsad summasi (so'm)");
        Label err = new Label(""); err.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        form.getChildren().addAll(new Label("Maqsad nomi:"), tl, new Label("Summa:"), am, err);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                try {
                    String t = tl.getText().trim(); double a = Double.parseDouble(am.getText().trim());
                    if (t.isEmpty()) { err.setText("Nom bo'sh bo'lmaydi!"); return null; }
                    savingsService.createGoal(t, a, user.getId()); return true;
                } catch (Exception e) { err.setText("Noto'g'ri summa!"); return null; }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(r -> { if (r) Main.primaryStage.getScene().setRoot(new SavingsScreen(user).getView()); });
    }

    private void showContributeDialog(SavingsGoal goal) {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Pul Qo'shish"); d.setHeaderText(goal.getTitle() + " maqsadiga pul qo'shish"); d.setContentText("Summa (so'm):");
        d.showAndWait().ifPresent(val -> {
            try {
                double amt = Double.parseDouble(val.trim());
                boolean ok = savingsService.contribute(goal.getId(), amt, user.getId());
                if (ok) Main.primaryStage.getScene().setRoot(new SavingsScreen(user).getView());
                else new Alert(Alert.AlertType.ERROR, "Balans yetarli emas!").show();
            } catch (Exception e) { new Alert(Alert.AlertType.ERROR, "Noto'g'ri summa!").show(); }
        });
    }

    private HBox buildNavbar() {
        HBox nav = new HBox(); nav.setPadding(new Insets(14, 24, 14, 24)); nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color: #0D2BA8;");
        Label logo = new Label("🏦 KidBank"); logo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        nav.getChildren().add(logo);
        return nav;
    }
}
