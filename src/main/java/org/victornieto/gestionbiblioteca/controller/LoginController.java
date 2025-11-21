package org.victornieto.gestionbiblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

       String username = textUsername.getText();
       String password = textPassword.getText();

       if(true) {

           // Cerrar ventana login
           Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
           stageCurrent.close();

           // Abrir ventana principal
           FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/home.fxml"));
           Parent root = fxmlLoader.load();

           Stage stage = new Stage();
           stage.setTitle("Gestión de biblioteca");
           stage.setScene(new Scene(root));
           stage.show();
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
}