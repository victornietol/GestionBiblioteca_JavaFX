package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.service.LibroService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

public class AddUnitsController {
    @FXML private Label labelMax;
    @FXML private Label labelTitulo;
    @FXML private TextField textCantidad;

    private Long id_libro;

    private final LibroService libroService = new LibroService();

    private int MAX_UNITS = 500;
    private int MIN_UNITS = 1;

    @FXML
    public void initialize() {
        configTextInput();
        labelMax.setText(String.valueOf(MAX_UNITS));
    }

    @FXML
    public void onAgregar(ActionEvent event) {
        AlertWindow alertWindow = new AlertWindow();

        try {
            int units = Integer.parseInt(textCantidad.getText());
            boolean confirm = alertWindow.generateConfirmation("Confirmar", "¿Estás seguro de agregar " + units + " ejemplares?", null);

            if(confirm) {
                boolean inserted = libroService.addUnits(id_libro, units);

                if (inserted) {
                    alertWindow.generateInformation("Información", "Acción realizada con éxito", "Elementos agregados: " + units);
                    closeWindow(event);
                } else {
                    throw new RuntimeException("Error al agregar unidades.");
                }
            }

        } catch (Exception e) {
            alertWindow.generateError("Error", "Error al agregar unidades.", null);
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void configTextInput() {
        textCantidad.setTextFormatter(new TextFormatter<>(change -> {
            String newValue = change.getControlNewText();
            if(newValue.isEmpty()) {
                return change;
            }
            if (!newValue.matches("\\d{0,3}")) {
                return null;
            }
            try {
                int value = Integer.parseInt(newValue);
                if(value<MIN_UNITS || value>MAX_UNITS) {
                    return null;
                }
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    private void closeWindow(ActionEvent event) {
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
    }

    public void setTitle(String title) {
        labelTitulo.setText(title);
    }

    public void setId_libro(Long id_libro) {
        this.id_libro = id_libro;
    }
}
