package org.victornieto.gestionbiblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private Label textErrorLogin;

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword;

    @FXML
    protected void onLoginClick() {
       textErrorLogin.setText("");

       String username = textUsername.getText();
       String password = textPassword.getText();
    }

    @FXML
    protected void onSignUpClick() {
        textErrorLogin.setText("");
    }
}