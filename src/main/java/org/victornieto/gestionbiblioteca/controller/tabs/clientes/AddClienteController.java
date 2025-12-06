package org.victornieto.gestionbiblioteca.controller.tabs.clientes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.ClienteFormControllerDTO;
import org.victornieto.gestionbiblioteca.model.ClienteModel;
import org.victornieto.gestionbiblioteca.service.ClienteService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.util.Optional;

public class AddClienteController {

    @FXML public TextField textUsername;
    @FXML public PasswordField textPassword1;
    @FXML public PasswordField textPassword2;
    @FXML public TextField textNombre;
    @FXML public TextField textApellidoP;
    @FXML public TextField textApellidoM;
    @FXML public TextField textCorreo;
    @FXML public Button btnConfirm;
    @FXML public Button btnCancel;

    private ClienteService clienteService;

    public void initialize() {
        this.clienteService = new ClienteService();
    }

    public void onAddCliente(ActionEvent event) {
        AlertWindow alertWindow = new AlertWindow();

        // Implementar logica para obtener datos del usuario y pasarlos al service
        // Service y Repository ya estan creado pero hay que testearlos
        try {
            ClienteFormControllerDTO clienteFormControllerDTO = new ClienteFormControllerDTO(
                    textUsername.getText().trim(),
                    textPassword1.getText(),
                    textPassword2.getText(),
                    textNombre.getText(),
                    textApellidoP.getText().trim(),
                    textApellidoM.getText().trim(),
                    textCorreo.getText().trim()
            );

            boolean confirm = alertWindow.generateConfirmation(
                    "Confirmación",
                    "¿Confirma la creación del siguiente cliente?",
                    "Detalles:\n\nUsername: " + clienteFormControllerDTO.username() +
                            "\nNombre: " + clienteFormControllerDTO.nombre().substring(0,1).toUpperCase() + clienteFormControllerDTO.nombre().substring(1)+
                            "\nApellido paterno: " + clienteFormControllerDTO.apellidoP().substring(0,1).toUpperCase() + clienteFormControllerDTO.apellidoP().substring(1) +
                            "\nApellido materno: " + (clienteFormControllerDTO.apellidoM().isEmpty() ? "" :
                            clienteFormControllerDTO.apellidoM().substring(0,1).toUpperCase() + clienteFormControllerDTO.apellidoM().substring(1))

                    );

            if (confirm) {
                Optional<ClienteModel> newCliente = clienteService.create(clienteFormControllerDTO);
                if (newCliente.isPresent()) {
                    ClienteModel cliente = newCliente.get();

                    alertWindow.generateInformation(
                            "Información",
                            "Cliente creado con éxito",
                            "Detalles:\n\nID cliente: " + cliente.getId() +
                                    "\nUsername: " + cliente.getUsername() +
                                    "\nNombre del cliente: " + cliente +
                                    "\nCorreo: " + cliente.getCorreo()
                    );
                    closeWindow(event);

                } else {
                    throw new RuntimeException("Ocurrió un error al generar nuevo cliente.");
                }
            }

        } catch (Exception e) {
            alertWindow.generateError("Error", e.getMessage(), null);
        }
    }

    public void onCancel(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
    }
}
