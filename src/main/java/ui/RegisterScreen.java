package ui;

import app.Main;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.AuthService;

public class RegisterScreen {

    private final AuthService authService = new AuthService();

    public VBox getView() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #FFFFFF;");

        Label title = new Label("🏦 KidBank — Ro'yxat");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0D2BA8;");
        VBox.setMargin(title, new Insets(40, 0, 24, 0));

        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(32, 40, 32, 40));
        card.setMaxWidth(420);
        card.setStyle("-fx-background-color: #F8FAFF; -fx-background-radius: 16;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 16, 0, 0, 4);");

        TextField nameField  = createField("Ism");
        TextField emailField = createField("Email");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Parol");
        passField.setPrefHeight(44);
        styleField(passField);

        Label roleLabel = new Label("Rol tanlang:");
        roleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

        ToggleGroup roleGroup   = new ToggleGroup();
        RadioButton parentRadio = new RadioButton("Ota-ona");
        RadioButton childRadio  = new RadioButton("Bola");
        parentRadio.setToggleGroup(roleGroup);
        childRadio.setToggleGroup(roleGroup);
        parentRadio.setSelected(true);
        HBox roleBox = new HBox(20, parentRadio, childRadio);

        TextField parentIdField = createField("Ota-ona ID");
        parentIdField.setVisible(false);
        parentIdField.setManaged(false);

        childRadio.setOnAction(e -> { parentIdField.setVisible(true);  parentIdField.setManaged(true);  });
        parentRadio.setOnAction(e -> { parentIdField.setVisible(false); parentIdField.setManaged(false); });

        Label errorLabel   = new Label("");
        errorLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 13px;");
        Label successLabel = new Label("");
        successLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-size: 13px;");

        Button registerBtn = new Button("Ro'yxatdan o'tish");
        registerBtn.setPrefWidth(340);
        registerBtn.setPrefHeight(48);
        registerBtn.setStyle("-fx-background-color: #1A3FD4; -fx-text-fill: white;" +
                "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");

        Label backLink = new Label("← Kirish sahifasiga qaytish");
        backLink.setStyle("-fx-text-fill: #1A3FD4; -fx-font-size: 13px; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass  = passField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Barcha maydonlar to'ldirilishi shart!");
                successLabel.setText("");
                return;
            }

            boolean success;
            if (parentRadio.isSelected()) {
                success = authService.registerParent(name, email, pass);
            } else {
                String parentId = parentIdField.getText().trim();
                success = authService.registerChild(name, email, pass, parentId);
            }

            if (success) {
                successLabel.setText("✅ Muvaffaqiyatli! Login sahifasiga o'tyapsiz...");
                errorLabel.setText("");
                new Thread(() -> {
                    try { Thread.sleep(1500); } catch (Exception ex) { /* ignore */ }
                    Platform.runLater(() ->
                        Main.primaryStage.getScene().setRoot(new LoginScreen().getView())
                    );
                }).start();
            } else {
                errorLabel.setText("❌ Bu email allaqachon mavjud!");
                successLabel.setText("");
            }
        });

        backLink.setOnMouseClicked(e ->
            Main.primaryStage.getScene().setRoot(new LoginScreen().getView())
        );

        card.getChildren().addAll(
            nameField, emailField, passField,
            roleLabel, roleBox, parentIdField,
            errorLabel, successLabel,
            registerBtn, backLink
        );

        root.getChildren().addAll(title, card);
        VBox.setMargin(card, new Insets(0, 0, 40, 0));
        return root;
    }

    private TextField createField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setPrefHeight(44);
        styleField(f);
        return f;
    }

    private void styleField(Control f) {
        f.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;" +
                "-fx-border-color: #E0E8FF; -fx-border-width: 1.5;" +
                "-fx-padding: 0 12; -fx-font-size: 14px;");
    }
}
