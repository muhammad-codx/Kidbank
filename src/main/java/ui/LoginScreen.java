package ui;
import app.Main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import models.User;
import services.AuthService;

public class LoginScreen {

    private AuthService authService = new AuthService();

    public VBox getView() {

        // ── Root ──────────────────────────────────
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(0);
        root.setStyle("-fx-background-color: #FFFFFF;");

        // ── Header ────────────────────────────────
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(60, 0, 40, 0));

        Label logo = new Label("🏦");
        logo.setStyle("-fx-font-size: 52px;");

        Label title = new Label("KidBank");
        title.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0D2BA8;"
        );

        Label subtitle = new Label("Smart money for smart kids");
        subtitle.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #666666;"
        );

        header.getChildren().addAll(logo, title, subtitle);

        // ── Card ──────────────────────────────────
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(36, 40, 36, 40));
        card.setMaxWidth(420);
        card.setStyle(
                "-fx-background-color: #F8FAFF;" +
                        "-fx-background-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0, 0, 4);"
        );

        // Email
        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        emailField.setPrefHeight(44);
        emailField.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #E0E8FF;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-padding: 0 12;" +
                        "-fx-font-size: 14px;"
        );

        // Password
        Label passLabel = new Label("Parol");
        passLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");
        PasswordField passField = new PasswordField();
        passField.setPromptText("••••••••");
        passField.setPrefHeight(44);
        passField.setStyle(
                "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #E0E8FF;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-padding: 0 12;" +
                        "-fx-font-size: 14px;"
        );

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setStyle(
                "-fx-text-fill: #E74C3C;" +
                        "-fx-font-size: 13px;"
        );

        // Login button
        Button loginBtn = new Button("Kirish");
        loginBtn.setPrefWidth(340);
        loginBtn.setPrefHeight(48);
        loginBtn.setStyle(
                "-fx-background-color: #1A3FD4;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        // Register link
        Label registerLink = new Label("Hisobingiz yo'qmi? Ro'yxatdan o'ting");
        registerLink.setStyle(
                "-fx-text-fill: #1A3FD4;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;"
        );

        // ── Login logikasi ────────────────────────
        loginBtn.setOnAction(e -> {
            String email    = emailField.getText().trim();
            String password = passField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Email va parol to'ldirilishi shart!");
                return;
            }

            User user = authService.login(email, password);

            if (user == null) {
                errorLabel.setText("Email yoki parol noto'g'ri!");
                return;
            }

            // Role bo'yicha yo'naltirish
            if (user.getRole().equals("parent")) {
                Main.primaryStage.getScene().setRoot(
                        new ParentDashboard(user).getView()
                );
            } else {
                Main.primaryStage.getScene().setRoot(
                        new ChildDashboard(user).getView()
                );
            }
        });

        // Register link bosilganda
        registerLink.setOnMouseClicked(e -> {
            Main.primaryStage.getScene().setRoot(
                    new RegisterScreen().getView()
            );
        });

        card.getChildren().addAll(
                emailLabel, emailField,
                passLabel, passField,
                errorLabel,
                loginBtn,
                registerLink
        );

        // ── Assemble ──────────────────────────────
        root.getChildren().addAll(header, card);
        VBox.setMargin(card, new Insets(0, 0, 60, 0));

        return root;
    }
}