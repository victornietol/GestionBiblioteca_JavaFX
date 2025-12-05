package org.victornieto.gestionbiblioteca.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;
import org.victornieto.gestionbiblioteca.service.UsuarioService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField textUsername;

    @FXML
    private TextField textPassword;

    @FXML
    private ProgressIndicator progressIndicatorLogin;

    @FXML
    private Button buttonLogin;

    @FXML
    private Button buttonSignup;

    private UsuarioModel usuarioLogged;

    @FXML
    protected void onLoginClick(ActionEvent event) {

        progressIndicatorLogin.setVisible(true);
        progressIndicatorLogin.setProgress(0);
        buttonLogin.setDisable(true);
        buttonSignup.setDisable(true);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();

        final UsuarioService userService = new UsuarioService();

        String username = textUsername.getText();
        String password = textPassword.getText();

        // Task para operaciones con mas carga (logica, no UI)
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {

                // actualizar progreso inicial
                updateProgress(0.1, 1);

                return userService.login(
                        username,
                        password,
                        progress -> updateProgress(progress, 1)
                );
            }
        };

        // Login exitoso (tarea completada correctamente)
        loginTask.setOnSucceeded(e -> {
            boolean success = loginTask.getValue();

            // desvincular ProgressIndicator
            if (progressIndicatorLogin.progressProperty().isBound()) {
                progressIndicatorLogin.progressProperty().unbind();
            }

            if (success) {
                try {
                    Optional<UsuarioModel> optional = userService.getUsuarioByUsername(username);
                    optional.ifPresent(usuarioModel -> usuarioLogged = usuarioModel);
                    openHomeWindow(stageCurrent);
                } catch (Exception ex) {
                    System.out.println("Error en controller: "+ Arrays.toString(ex.getStackTrace()));
                    progressIndicatorLogin.setVisible(false);
                    progressIndicatorLogin.setProgress(0);
                    Alert alert = generateAlert("error", "Error inesperado al iniciar sesión.");
                    alert.initOwner(stageCurrent);
                    alert.showAndWait();
                    buttonLogin.setDisable(false);
                    buttonSignup.setDisable(false);
                }

            } else { // Las credenciales no son correctas
                progressIndicatorLogin.setVisible(false);
                progressIndicatorLogin.setProgress(0);
                Alert alert = generateAlert("error", "Credenciales incorrectas.");
                alert.initOwner(stageCurrent);
                alert.showAndWait();
                buttonLogin.setDisable(false);
                buttonSignup.setDisable(false);
            }
        });

        // Login fallido por un error (falla al ejecutar la tarea)
        loginTask.setOnFailed(e -> {
            // desvincular ProgressIndicator
            if (progressIndicatorLogin.progressProperty().isBound()) {
                progressIndicatorLogin.progressProperty().unbind();
            }

            progressIndicatorLogin.setVisible(false);
            Alert alert = generateAlert("error", "Error al verificar credenciales");
            alert.initOwner(stageCurrent);
            alert.showAndWait();
        });

        // Vincular ProgressIndicator al task
        progressIndicatorLogin.progressProperty().bind(loginTask.progressProperty());

        // Iniciar en segundo plano en otro hilo
        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    protected void onSignUpClick(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/signUp.fxml"));
        Parent signUpParent = fxmlLoader.load();

        Stage signUpStage = new Stage();
        signUpStage.setTitle("Registro");
        signUpStage.setScene(new Scene(signUpParent));

        signUpStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        signUpStage.initOwner(stageCurrent);

        signUpStage.showAndWait();
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

    private void openHomeWindow(Stage stageCurrent) {
        /**
         * Abre la ventana principal
         * @param stageCurrent un Stage correspondiente al stage actual
         */
        try {
            // Cerrar ventana login
            stageCurrent.close();

            // Abrir ventana principal
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/home.fxml"));
            Parent root = fxmlLoader.load();

            // Cargar en Home el usuario logueado
            HomeController controller = fxmlLoader.getController();
            controller.setUserLogged(usuarioLogged);

            Stage stage = new Stage();
            stage.setTitle("Gestión de biblioteca");
            stage.setScene(new Scene(root));
            stage.setMinHeight(550);
            stage.setMinWidth(800);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException error) {
            throw new RuntimeException(error);
        }
    }
}