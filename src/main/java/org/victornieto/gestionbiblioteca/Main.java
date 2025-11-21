package org.victornieto.gestionbiblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        stage.setTitle("Gestión de biblioteca");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinHeight(600);
        stage.setMinWidth(900);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}