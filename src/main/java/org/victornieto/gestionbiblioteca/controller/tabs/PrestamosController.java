package org.victornieto.gestionbiblioteca.controller.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.service.PrestamoService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.time.LocalDate;
import java.util.List;

public class PrestamosController {

    @FXML public AnchorPane rootPrestamos;

    @FXML public MenuButton menuBtnCriterio;
    @FXML public TextField textFieldSearch;
    @FXML public Button btnSearch;
    @FXML public MenuButton menuBtnOrdenCampo;
    @FXML public MenuButton menuBtnOrden;

    @FXML public TableView<PrestamoListDTO> tablePrestamos;
    @FXML public TableColumn<PrestamoListDTO, Long> columnId;
    @FXML public TableColumn<PrestamoListDTO, String> columnTitulo;
    @FXML public TableColumn<PrestamoListDTO, String> columnAutor;
    @FXML public TableColumn<PrestamoListDTO, LocalDate> columnFechaInicio;
    @FXML public TableColumn<PrestamoListDTO, LocalDate> columnFechaEntrega;
    @FXML public TableColumn<PrestamoListDTO, Long> columnIdEjem;
    @FXML public TableColumn<PrestamoListDTO, String> columnCliente;
    @FXML public TableColumn<PrestamoListDTO, String> columnAtendio;

    @FXML public Label labelBuscando;
    @FXML public ProgressBar progressBarLoad;
    @FXML public Label numberRows;

    private PrestamoService prestamoService;
    private List<PrestamoListDTO> prestamosList;
    private String columnToSearch;
    private String coincidenceToSearch;
    private String orderByColumn;
    private Boolean orderDesc;

    @FXML
    public void initialize() {
        this.prestamoService = new PrestamoService();

        generateFunctionMenuButton();
        setColumns();
        showInventario();
    }

    @FXML
    public void showInventario() {
        labelBuscando.setVisible(true);
        tablePrestamos.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        btnSearch.setDisable(true);
        setValues();
        progressBarLoad.setProgress(0.20);

        // Task para operaciones con BD
        Task<Void> getPrestamosTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                prestamosList = generatePrestamosList();
                progressBarLoad.setProgress(0.50);
                return null;
            }
        };

        // Tarea ejecutada correctamente
        getPrestamosTask.setOnSucceeded(e -> {
            if (prestamosList.isEmpty()) {
                tablePrestamos.getItems().clear();

            } else {
                progressBarLoad.setProgress(0.70);
                ObservableList<PrestamoListDTO> data = FXCollections.observableArrayList(prestamosList);
                tablePrestamos.setItems(data);
            }

            numberRows.setText(String.valueOf(prestamosList.size()));
            progressBarLoad.setProgress(1.0);
            progressBarLoad.setVisible(false);
            labelBuscando.setVisible(false);
            btnSearch.setDisable(false);
        });

        // Tarea no se ejecuta correctamente
        getPrestamosTask.setOnFailed(e -> {
            numberRows.setText("0");
            progressBarLoad.setProgress(1.0);
            btnSearch.setDisable(false);

            // Mostrar ventana de error
            System.out.println("Error: " + getPrestamosTask.getException().getMessage());
            AlertWindow alertWindow = new AlertWindow();
            alertWindow.generateError(
                    "Error",
                    "Ocurrio un error al obtener los prestamos.",
                    null
            );
        });


        Thread thread = new Thread(getPrestamosTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    public void add() {

    }

    private List<PrestamoListDTO> generatePrestamosList() {
        /**
         *
         * Utiliza service para obtener los datos de los préstamos
         *
         * @return una lista con los resultados, puede ser vacia o no dependiendo del resultado de la consulta
         */

        try {
            return prestamoService.getPrestamosList(
                    columnToSearch, coincidenceToSearch, orderByColumn, orderDesc
            );

        } catch (Exception e) {
            System.out.println("Ocurrió un error la ejecutar la tarea para recuperar los prestamos");
            throw new RuntimeException(e.getMessage());
        }
    }

    private void setValues() {
        /**
         * Obtiene los valores de los elementos de la interfaz y los asigna a variables
         */

        // Columna en la que se busca coincidencia
        columnToSearch = menuBtnCriterio.getText();

        // Texto a buscar
        coincidenceToSearch = textFieldSearch.getText();

        // Ordenamiento por columna
        orderByColumn = menuBtnOrdenCampo.getText();

        // Ordenamiento ascendente o desc
        if (menuBtnOrden.getText().equals("Ascendente")) {
            orderDesc = false;
        } else {
            orderDesc = true;
        }
    }

    private void generateFunctionMenuButton() {
        // Funciones para cada menuItem para actualizar el texto de menuBtnCriterio
        for (MenuItem item: menuBtnCriterio.getItems()) {
            item.setOnAction(e -> menuBtnCriterio.setText(item.getText()));
        }

        // Funciones para cada menuItem para actualizar el ordenamiento por columna
        for (MenuItem item: menuBtnOrdenCampo.getItems()) {
            item.setOnAction(e -> menuBtnOrdenCampo.setText(item.getText()));
        }

        // Funciones para cada menuItem para actualizar el ordenamiento ascendente o descendente
        for (MenuItem item: menuBtnOrden.getItems()) {
            item.setOnAction(e -> menuBtnOrden.setText(item.getText()));
        }
    }

    private void setColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        columnIdEjem.setCellValueFactory(new PropertyValueFactory<>("idEjemplar"));
        columnTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        columnCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        columnAtendio.setCellValueFactory(new PropertyValueFactory<>("usuario"));
    }
}
