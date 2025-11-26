package org.victornieto.gestionbiblioteca;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        stage.setTitle("Gestión de biblioteca");
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(400);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}