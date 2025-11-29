package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.service.CategoriaService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.util.Optional;

public class NewCategoriaFormController {
    @FXML public TextField textFieldCategoria;

    private final CategoriaService categoriaService = new CategoriaService();
    private CategoriaModel categoria = null;

    @FXML
    public void onSave(ActionEvent event) {
        AlertWindow alertWindow = new AlertWindow(); // generador de Alert's

        boolean confirm = alertWindow.generateConfirmation("Confirmación",
                "¿Estás seguro?",
                null
        );

        if (confirm) {
            try {
                categoria  = createCategoria();
                if (categoria!=null) {
                    alertWindow.generateInformation("Información",
                            "Categoria '" + categoria + "' generado.",
                            "Ha sido agregado a la lista de categorias disponibles."
                    );
                    closeWindow(event);
                } else {
                    alertWindow.generateError("Error",
                            "Error al generar nueva categoria",
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

    private CategoriaModel createCategoria() {
        CategoriaFormDTO categoria = new CategoriaFormDTO(
                textFieldCategoria.getText()
        );

        try {
            Optional<CategoriaModel> newCategoria = categoriaService.create(categoria);
            if (newCategoria.isPresent()) {
                return  newCategoria.get();
            }
        } catch (IllegalArgumentException e) {
            throw e;
        }

        return null;
    }

    public CategoriaModel getCategoria() {
        return categoria;
    }
}
