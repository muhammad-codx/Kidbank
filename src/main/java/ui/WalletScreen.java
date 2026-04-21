package ui;

import app.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Transaction;
import models.User;
import services.BankService;

import java.util.List;

public class WalletScreen {

    private final User user;
    private final BankService bankService = new BankService();

    public WalletScreen(User user) { this.user = user; }

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

        Label title = new Label("💰 Hamyon");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0D2BA8;");

        double balance = bankService.getBalance(user.getId());
        VBox balCard = new VBox(6);
        balCard.setPadding(new Insets(24));
        balCard.setAlignment(Pos.CENTER);
        balCard.setStyle("-fx-background-color: #0D2BA8; -fx-background-radius: 14;" +
                "-fx-effect: dropshadow(gaussian, rgba(13,43,168,0.35), 16, 0, 0, 4);");
        Label balLbl = new Label("Joriy Balans"); balLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: rgba(255,255,255,0.7);");
        Label balAmt = new Label(String.format("%.0f so'm", balance)); balAmt.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: white;");
        balCard.getChildren().addAll(balLbl, balAmt);

        Button withdrawBtn = new Button("💸 Pul Yechish");
        withdrawBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;" +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 12 24; -fx-cursor: hand;");
        withdrawBtn.setOnAction(e -> showWithdrawDialog());

        Label histLbl = new Label("📊 Tranzaksiyalar Tarixi");
        histLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox histBox = new VBox(8);
        buildHistory(histBox);

        content.getChildren().addAll(back, title, balCard, withdrawBtn, histLbl, histBox);
        scroll.setContent(content);
        root.setCenter(scroll);
        return root;
    }

    private void buildHistory(VBox box) {
        box.getChildren().clear();
        List<Transaction> history = bankService.getHistory(user.getId());
        if (history.isEmpty()) {
            Label l = new Label("Hali tranzaksiya yo'q"); l.setStyle("-fx-text-fill: #888; -fx-font-size: 14px;");
            box.getChildren().add(l); return;
        }
        for (int i = history.size() - 1; i >= 0; i--) {
            Transaction t = history.get(i);
            HBox row = new HBox(12); row.setPadding(new Insets(14)); row.setAlignment(Pos.CENTER_LEFT);
            boolean cr = t.getType().equals("credit");
            row.setStyle("-fx-background-color: " + (cr ? "#E8F8F0" : "#FEF0F0") + "; -fx-background-radius: 10;");
            Label icon = new Label(cr ? "⬆️" : "⬇️");
            VBox info = new VBox(2); HBox.setHgrow(info, Priority.ALWAYS);
            Label dl = new Label(t.getDescription()); dl.setStyle("-fx-font-size: 13px;");
            Label dt = new Label(t.getDate()); dt.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            info.getChildren().addAll(dl, dt);
            Label al = new Label((cr ? "+" : "-") + String.format("%.0f", t.getAmount()) + " so'm");
            al.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + (cr ? "#27AE60" : "#E74C3C") + ";");
            row.getChildren().addAll(icon, info, al);
            box.getChildren().add(row);
        }
    }

    private void showWithdrawDialog() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Pul Yechish"); d.setHeaderText("Qancha pul yechmoqchisiz?"); d.setContentText("Summa (so'm):");
        d.showAndWait().ifPresent(val -> {
            try {
                double amt = Double.parseDouble(val.trim());
                boolean ok = bankService.withdraw(user.getId(), amt, "Pul yechish");
                if (ok) Main.primaryStage.getScene().setRoot(new WalletScreen(user).getView());
                else new Alert(Alert.AlertType.ERROR, "Balans yetarli emas!").show();
            } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Noto'g'ri summa!").show(); }
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
