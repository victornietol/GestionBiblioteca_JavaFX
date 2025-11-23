package org.victornieto.gestionbiblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.service.UsuarioService;

import java.io.IOException;

public class LoginController {

    @FXML
    private Label textErrorLogin;

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword;

    @FXML
    protected void onLoginClick(ActionEvent event) throws IOException {
       textErrorLogin.setText("");

       final UsuarioService userService = new UsuarioService();

       String username = textUsername.getText();
       String password = textPassword.getText();

       Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();

       if(userService.login(username, password)) {

           // Cerrar ventana login
           stageCurrent.close();

           // Abrir ventana principal
           FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/home.fxml"));
           Parent root = fxmlLoader.load();

           Stage stage = new Stage();
           stage.setTitle("Gestión de biblioteca");
           stage.setScene(new Scene(root));
           stage.setMinHeight(550);
           stage.setMinWidth(800);
           stage.setMaximized(true);
           stage.show();

       } else {
           Alert alert = generateAlert("error", "Credenciales incorrectas.");
           alert.initOwner(stageCurrent);
           alert.showAndWait();
       }

    }

    @FXML
    protected void onSignUpClick(ActionEvent event) throws IOException {
        textErrorLogin.setText("");
        textUsername.setText("");
        textPassword.setText("");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/signUp.fxml"));
        Parent signUpParent = fxmlLoader.load();

        Stage signUpStage = new Stage();
        signUpStage.setTitle("Registro");
        signUpStage.setScene(new Scene(signUpParent));

        signUpStage.show();
    }

    private Alert generateAlert(String type, String message) {
        /**
         * Tipos de alerta:
         *  info
         *  error
         */

        if (type.equals("error")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            return alert;

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(null);
            alert.setContentText(message);
            return alert;
        }

    }
}