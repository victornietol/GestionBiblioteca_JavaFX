package org.victornieto.gestionbiblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    protected void onSignUpClick() {

        final UsuarioService userService = new UsuarioService();

        UsuarioFormDTO userFormDTO = new UsuarioFormDTO.Builder()
                .setUsername(textUsername.getText())
                .setPassword1(textPassword1.getText())
                .setPassword2(textPassword2.getText())
                .setNombre(textNombre.getText())
                .setApellidoP(textApellidoP.getText())
                .setApellidoM(textApellidoM.getText())
                .setCorreo(textCorreo.getText())
                .setTelefono(textTelefono.getText())
                .build();

        HashMap<String, Object> result = userService.createUsuario(userFormDTO);
        String message = "";

        // Abrir ventana de creacion exitosa de ser el caso
        if ((boolean) result.get("created")) {
            message = "Usuario creado: " + ((UsuarioModel) result.get("user")).getUsername();
        } else {
            message = "Error: " + result.get("error");
        }



    }

    @FXML
    protected void onCancelSignUpClick(ActionEvent event) {
        // Cerrar ventana login
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
    }
}
