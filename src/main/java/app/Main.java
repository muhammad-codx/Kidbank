package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.LoginScreen;

public class Main extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("KidBank");
        stage.setWidth(1000);
        stage.setHeight(700);
        stage.setResizable(false);

        // Login ekranini ko'rsat
        LoginScreen loginScreen = new LoginScreen();
        Scene scene = new Scene(loginScreen.getView());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}