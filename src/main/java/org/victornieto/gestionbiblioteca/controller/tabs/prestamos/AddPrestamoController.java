package org.victornieto.gestionbiblioteca.controller.tabs.prestamos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.PrestamoDTO;
import org.victornieto.gestionbiblioteca.model.LibroModel;
import org.victornieto.gestionbiblioteca.model.PrestamoModel;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;
import org.victornieto.gestionbiblioteca.service.ClienteService;
import org.victornieto.gestionbiblioteca.service.LibroService;
import org.victornieto.gestionbiblioteca.service.PrestamoService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.time.LocalDate;
import java.util.Optional;

public class AddPrestamoController {

    @FXML public TextField textFieldCliente;
    @FXML public TextField textFieldEjemplar;

    private UsuarioModel userLogged;
    private final PrestamoService prestamoService = new PrestamoService();
    private final LibroService libroService = new LibroService();
    private final ClienteService clienteService = new ClienteService();

    @FXML
    public void initialize() {
        configTextField();
    }

    @FXML
    public void onSave(ActionEvent event) {
        AlertWindow alertWindow = new AlertWindow();
        LibroModel libroPrestamo;
        String clienteNombre;
        Long idEjemplar = Long.valueOf(textFieldEjemplar.getText());
        Long idCliente = Long.valueOf(textFieldCliente.getText());

        try {
            Optional<LibroModel> optLibro = libroService.getTituloByIdEjemplar(idEjemplar);
            Optional<String> optCliente = clienteService.getNombreCompletoById(idCliente);

            libroPrestamo = optLibro.orElseThrow(() -> new IllegalArgumentException("Id de ejemplar inválido"));
            clienteNombre = optCliente.orElseThrow(() -> new IllegalArgumentException("Id de cliente inválido"));

            boolean confirm = alertWindow.generateConfirmation(
                    "Confirmar",
                    "¿Confirmar préstamo?",
                    "Detalles:\n\nID ejemplar: " + idEjemplar +
                            "\nTítulo del ejemplar: " + libroPrestamo.getTitulo() +
                            "\nID cliente: " + idCliente +
                            "\nNombre del cliente: " + clienteNombre +
                            "\nPréstamo atendido por: " + userLogged.getUsername()
            );

            if (confirm) {

                LocalDate fechaInicio = LocalDate.now();
                LocalDate fechaFin = LocalDate.now().plusWeeks(1);

                Optional<PrestamoModel> opt = prestamoService.createPrestamo(new PrestamoDTO(
                        fechaInicio,
                        fechaFin,
                        idCliente,
                        userLogged.getId(),
                        idEjemplar
                ));
                if (opt.isPresent()) {
                    PrestamoModel prestamoModel = opt.get();
                    alertWindow.generateInformation(
                            "Información",
                            "Préstamo generado con éxito",
                            "Detalles:\nID préstamo: " + prestamoModel.getId() +
                                    "\nID cliente: " + prestamoModel.getFkCliente() +
                                    "\nNombre del cliente: " + clienteNombre +
                                    "\nID ejemplar: " + prestamoModel.getIdEjemplar() +
                                    "\nTítulo del ejemplar: " + libroPrestamo.getTitulo() +
                                    "\nAtendió: " + userLogged.getUsername()
                    );
                    closeWindow(event);
                } else {
                    throw new RuntimeException("Error al generar nuevo préstamo");
                }
            }
        } catch (IllegalArgumentException e) {
            alertWindow.generateError("Error", "Error: " + e.getMessage(), null);

        } catch (RuntimeException ex) {
            alertWindow.generateError("Error", "Error: " + ex.getMessage(), null);

        } catch (Exception e) {
            alertWindow.generateError("Error", "Ocurrió un error al generar el préstamo", null);
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        closeWindow(event);
    }

    public void setUserLogged(UsuarioModel userLogged) {
        this.userLogged = userLogged;
    }

    private void closeWindow(ActionEvent event) {
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
    }

    private void configTextField() {
        /**
         * Configurar textField para solo permitir dígitos
         */
        textFieldCliente.setTextFormatter(new TextFormatter<>(change -> {
            String newValue = change.getControlNewText();
            if (newValue.matches("\\d+") || newValue.isEmpty()) {
                return change;
            }
            return null;
        }));

        textFieldEjemplar.setTextFormatter(new TextFormatter<>(change -> {
            String newValue = change.getControlNewText();
            if (newValue.matches("\\d+") || newValue.isEmpty()) {
                return change;
            }
            return null;
        }));
    }
}
