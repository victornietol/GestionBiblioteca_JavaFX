package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.AutorFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.service.AutorService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.util.Optional;

public class NewAutorFormController {
    @FXML public TextField textFieldNombre;
    @FXML public TextField textFieldApellidoP;
    @FXML public TextField textFieldApellidoM;

    private final AutorService autorService = new AutorService();
    private AutorModel autor = null;

    @FXML
    public void onSave(ActionEvent event) {
        AlertWindow alertWindow = new AlertWindow(); // generador de Alert's

        boolean confirm = alertWindow.generateConfirmation("Confirmación",
                "¿Estás seguro?",
                null
        );

        if (confirm) {
            try {
                autor  = createAutor();
                if (autor!=null) {
                    alertWindow.generateInformation("Información",
                            "Autor '" + autor.getNombreCompleto() + "' generado.",
                            "Ha sido agregado a la lista de autores disponibles."
                    );
                    closeWindow(event);
                } else {
                    alertWindow.generateError("Error",
                            "Error al generar nuevo autor",
                            null
                    );
                }
            } catch (RuntimeException e) {
                alertWindow.generateError("Error", e.getMessage(), null);
            }
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
    }

    private AutorModel createAutor() {
        AutorFormDTO autor = new AutorFormDTO(
                textFieldNombre.getText(),
                textFieldApellidoP.getText(),
                textFieldApellidoM.getText()
        );

        try {
            Optional<AutorModel> newAutor = autorService.create(autor);
            if (newAutor.isPresent()) {
                return  newAutor.get();
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }

        return null;
    }

    public AutorModel getAutor() {
        return autor;
    }

}
