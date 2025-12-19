package org.victornieto.gestionbiblioteca.utility;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoadingDialog {

    private final Stage stage;
    private final ProgressIndicator indicator;
    private final Label labelMessage;
    private final Button btnAceptar;

    public LoadingDialog(String message) {
        stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);


        indicator = new ProgressIndicator();
        indicator.setPrefSize(50,50);
        indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        labelMessage = new Label(message);

        HBox loaderBox = new HBox(15, indicator, labelMessage);
        loaderBox.setAlignment(Pos.CENTER);

        btnAceptar = new Button("Aceptar");
        btnAceptar.setVisible(false);
        btnAceptar.setOnAction(e -> close());

        HBox buttonBox = new HBox(btnAceptar);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        BorderPane root = new BorderPane();
        root.setCenter(loaderBox);
        root.setBottom(buttonBox);

        BorderPane.setMargin(buttonBox, new Insets(10));
        BorderPane.setMargin(loaderBox, new Insets(20));

        root.setPrefSize(300,140);
        root.setStyle("""
                -fx-background-radius: 10;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 4);
                """);

        stage.setScene(new Scene(root));
    }

    public void completeMessage(String message) {
        indicator.setProgress(1.0);
        labelMessage.setText(message);
        btnAceptar.setVisible(true);
    }

    public void show() {
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
