package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.service.EditorialService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.util.Optional;

public class NewEditorialFormController {
    @FXML public TextField textFieldEditorial;

    private final EditorialService editorialService = new EditorialService();
    private EditorialModel editorial = null;

    @FXML
    public void onSave(ActionEvent event) {
        AlertWindow alertWindow = new AlertWindow(); // generador de Alert's

        boolean confirm = alertWindow.generateConfirmation("Confirmación",
                "¿Estás seguro?",
                null
        );

        if (confirm) {
            try {
                editorial  = createEditorial();
                if (editorial!=null) {
                    alertWindow.generateInformation("Información",
                            "Editorial '" + editorial + "' generado.",
                            "Ha sido agregado a la lista de editoriales disponibles."
                    );
                    closeWindow(event);
                } else {
                    alertWindow.generateError("Error",
                            "Error al generar nueva editorial",
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

    private EditorialModel createEditorial() {
        EditorialFormDTO editorial = new EditorialFormDTO(
                textFieldEditorial.getText()
        );

        try {
            Optional<EditorialModel> newEditorial = editorialService.create(editorial);
            if (newEditorial.isPresent()) {
                return  newEditorial.get();
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }

        return null;
    }

    public EditorialModel getEditorial() {
        return editorial;
    }

}
