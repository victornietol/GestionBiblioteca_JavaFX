package org.victornieto.gestionbiblioteca.controller;

import javafx.concurrent.Task;
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

        // Task para proceso pesado
        Task<HashMap<String, Object>> signUpTask = new Task<>() {
            @Override
            protected HashMap<String, Object> call() throws Exception {
                return userService.createUsuario(userFormDTO);
            }
        };

        // Tarea de SignUp completada correctamente (no significa que se haya registrado usuario correctamente)
        signUpTask.setOnSucceeded(e -> {
            HashMap<String, Object> result = signUpTask.getValue();
            String message;
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
        });

        // Ocurrio algun error en la ejecucion de la tarea SignUp
        signUpTask.setOnFailed(e -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Alert alert = generateAlert("error", "Ocurrio un error inesperado en la creación del usuario.");
            alert.initOwner(stage);
            alert.showAndWait();
            btnConfirm.setDisable(false);
            btnCancel.setDisable(false);
        });

        Thread signUpThread = new Thread(signUpTask);
        signUpThread.setDaemon(true);
        signUpThread.start();
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

        Alert alert;

        if (type.equals("error")) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
        }

        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;

    }
}
