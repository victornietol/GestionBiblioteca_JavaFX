package org.victornieto.gestionbiblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.UsuarioFormDTO;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;
import org.victornieto.gestionbiblioteca.service.UsuarioService;

import java.util.HashMap;

public class SignUpController {

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword1;

    @FXML
    private TextField textPassword2;

    @FXML
    private TextField textNombre;

    @FXML
    private TextField textApellidoP;

    @FXML
    private TextField textApellidoM;

    @FXML
    private TextField textCorreo;

    @FXML
    private TextField textTelefono;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnCancel;


    @FXML
    protected void onSignUpClick(ActionEvent event) {
        btnConfirm.setDisable(true);
        btnCancel.setDisable(true);

        final UsuarioService userService = new UsuarioService();

        UsuarioFormDTO userFormDTO = new UsuarioFormDTO.Builder()
                .setUsername(textUsername.getText())
                .setPassword1(textPassword1.getText())
                .setPassword2(textPassword2.getText())
                .setNombre(textNombre.getText().toLowerCase())
                .setApellidoP(textApellidoP.getText().toLowerCase())
                .setApellidoM(textApellidoM.getText().toLowerCase())
                .setCorreo(textCorreo.getText())
                .setTelefono(textTelefono.getText())
                .build();

        HashMap<String, Object> result = userService.createUsuario(userFormDTO);

        String message = "";
        Alert alert;

        // Abrir ventana de creacion exitosa o error
        if ((boolean) result.get("created")) {
            message = "Usuario creado: " + ((UsuarioModel) result.get("user")).getUsername();
            alert = generateAlert("info", message);
        } else {
            message = "Error: " + result.get("error");
            alert = generateAlert("error", message);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        alert.initOwner(stage);
        alert.showAndWait();
        btnConfirm.setDisable(false);
        btnCancel.setDisable(false);

        if ((boolean) result.get("created")) {
            stage.close();
        }
    }

    @FXML
    protected void onCancelSignUpClick(ActionEvent event) {
        // Cerrar ventana login
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
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
