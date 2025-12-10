package org.victornieto.gestionbiblioteca.controller.tabs.clientes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.victornieto.gestionbiblioteca.dto.ClienteListDTO;
import org.victornieto.gestionbiblioteca.dto.SancionListDTO;
import org.victornieto.gestionbiblioteca.service.SancionService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.util.List;

public class SancionesClienteController {
    @FXML public Button btnDelete;

    @FXML public TableView<SancionListDTO> tableSanciones;
    @FXML public TableColumn<SancionListDTO, Long> columnId;
    @FXML public TableColumn<SancionListDTO, String> columnSancion;
    @FXML public TableColumn<SancionListDTO, String> columnDescripcion;
    @FXML public TableColumn<SancionListDTO, String> columnFecha;
    @FXML public TableColumn<SancionListDTO, Long> columnIdPrestamo;

    @FXML public Label labelBuscando;
    @FXML public ProgressBar progressBarLoad;
    @FXML public Label numberRows;
    @FXML public Label labelNombreCliente;

    private final SancionService sancionService = new SancionService();
    private List<SancionListDTO> sanciones;

    private ClienteListDTO cliente;

    @FXML
    public void initialize() {
        setColumns();
    }

    @FXML
    public void deleteCliente() {

    }

    private void showSanciones() {
        labelBuscando.setVisible(true);
        tableSanciones.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        progressBarLoad.setProgress(0.20);
        labelNombreCliente.setText(cliente.getNombre());

        // Task para operaciones con BD
        Task<Void> getSancionesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                sanciones = sancionService.getById(cliente.getId());
                progressBarLoad.setProgress(0.50);
                return null;
            }
        };

        // Tarea ejecutada correctamente
        getSancionesTask.setOnSucceeded(e -> {
            if (sanciones.isEmpty()) {
                tableSanciones.getItems().clear();

            } else {
                progressBarLoad.setProgress(0.70);
                ObservableList<SancionListDTO> data = FXCollections.observableArrayList(sanciones);
                tableSanciones.setItems(data);
            }

            numberRows.setText(String.valueOf(sanciones.size()));
            progressBarLoad.setProgress(1.0);
            progressBarLoad.setVisible(false);
            labelBuscando.setVisible(false);
        });

        // Tarea no se ejecuta correctamente
        getSancionesTask.setOnFailed(e -> {
            numberRows.setText("0");
            progressBarLoad.setProgress(1.0);

            // Mostrar ventana de error
            System.out.println("Error: " + getSancionesTask.getException().getMessage());
            AlertWindow alertWindow = new AlertWindow();
            alertWindow.generateError(
                    "Error",
                    "Ocurrio un error al obtener las sanciones.",
                    null
            );
        });

        Thread thread = new Thread(getSancionesTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void setColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnSancion.setCellValueFactory(new PropertyValueFactory<>("sancion"));
        columnDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnIdPrestamo.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
    }

    public void setCliente(ClienteListDTO cliente) {
        this.cliente = cliente;
        initData();
    }

    private void initData() {
        if (cliente==null) return;

        labelNombreCliente.setText(cliente.getNombre());

        showSanciones();
    }
}
