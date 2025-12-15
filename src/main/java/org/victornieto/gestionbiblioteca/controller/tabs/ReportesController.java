package org.victornieto.gestionbiblioteca.controller.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.service.ClienteService;
import org.victornieto.gestionbiblioteca.service.PrestamoService;
import org.victornieto.gestionbiblioteca.service.SancionService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;
import org.victornieto.gestionbiblioteca.utility.PrestamosViewTitlesMenuBtn;

import java.time.LocalDate;
import java.util.List;

public class ReportesController {

    @FXML
    public AnchorPane rootReportes;

    @FXML public Label labelPrestamosHoy;
    @FXML public Label labelPrestamosActuales;
    @FXML public Label labelDevoluciones;
    @FXML public Label labelNuevosUsuarios;
    @FXML public Label labelUsuariosActuales;
    @FXML public Label labelSanciones;
    @FXML public Label labelBuscando;
    @FXML public ProgressBar progressBarLoad;

    @FXML public TableView<PrestamoListDTO> tablePrestamos;
    @FXML public TableColumn<PrestamoListDTO, Long> columnId;
    @FXML public TableColumn<PrestamoListDTO, LocalDate> columnFechaInicio;
    @FXML public TableColumn<PrestamoListDTO, LocalDate> columnFechaEntrega;
    @FXML public TableColumn<PrestamoListDTO, Long> columnIdEjem;
    @FXML public TableColumn<PrestamoListDTO, String> columnTitulo;
    @FXML public TableColumn<PrestamoListDTO, String> columnAutor;
    @FXML public TableColumn<PrestamoListDTO, String> columnCliente;
    @FXML public TableColumn<PrestamoListDTO, String> columnAtendio;

    @FXML public TableView<PrestamoListDTO> tablePrestamos1;
    @FXML public TableColumn<PrestamoListDTO, Long> columnId1;
    @FXML public TableColumn<PrestamoListDTO, LocalDate> columnFechaInicio1;
    @FXML public TableColumn<PrestamoListDTO, LocalDate> columnFechaEntrega1;
    @FXML public TableColumn<PrestamoListDTO, Long> columnIdEjem1;
    @FXML public TableColumn<PrestamoListDTO, String> columnTitulo1;
    @FXML public TableColumn<PrestamoListDTO, String> columnAutor1;
    @FXML public TableColumn<PrestamoListDTO, String> columnCliente1;
    @FXML public TableColumn<PrestamoListDTO, String> columnAtendio1;

    private PrestamoService prestamoService;
    private ClienteService clienteService;
    private SancionService sancionService;

    private String columnToSearch;
    private String coincidenceToSearch;
    private String orderByColumn;
    private boolean orderDesc;
    private List<PrestamoListDTO> prestamosList;
    private String columnToSearch1;
    private String coincidenceToSearch1;
    private String orderByColumn1;
    private List<PrestamoListDTO> prestamosReturnedList;


    @FXML
    public void initialize() {
        this.prestamoService = new PrestamoService();
        this.clienteService = new ClienteService();
        this.sancionService = new SancionService();

        setColumns();
        showPrestamos();
    }

    private void showPrestamos() {
        labelBuscando.setVisible(true);
        tablePrestamos.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        setValues();
        progressBarLoad.setProgress(0.20);

        final Integer[] amountPrestamos = new Integer[4]; // 0: prestamosActuales, 1: nuevosClientesHoy, 2: clientesActivos, 3: sancionesActivas

        // Task para operaciones con BD
        Task<Void> getPrestamosTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                prestamosList = generatePrestamosList();
                prestamosReturnedList = generatePrestamosReturnedList();
                amountPrestamos[0] = prestamoService.getNumberActivePrestamos();
                amountPrestamos[1] = clienteService.getNewClientesToday();
                amountPrestamos[2] = clienteService.getActiveClientes();
                amountPrestamos[3] = sancionService.getActiveAmount();
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
                ObservableList<PrestamoListDTO> data1 = FXCollections.observableArrayList(prestamosReturnedList);
                tablePrestamos.setItems(data); // Prestamos iniciados hoy
                tablePrestamos1.setItems(data1); // Prestamos devueltos hoy
                labelPrestamosActuales.setText(amountPrestamos[0].toString());
                labelNuevosUsuarios.setText(amountPrestamos[1].toString());
                labelUsuariosActuales.setText(amountPrestamos[2].toString());
                labelSanciones.setText(amountPrestamos[3].toString());
            }

            labelPrestamosHoy.setText(String.valueOf(prestamosList.size()));
            labelDevoluciones.setText(String.valueOf(prestamosReturnedList.size()));
            progressBarLoad.setProgress(1.0);
            progressBarLoad.setVisible(false);
            labelBuscando.setVisible(false);
        });

        // Tarea no se ejecuta correctamente
        getPrestamosTask.setOnFailed(e -> {
            labelPrestamosHoy.setText("0");
            progressBarLoad.setProgress(1.0);

            // Mostrar ventana de error
            System.out.println("Error: " + getPrestamosTask.getException().getMessage());
            AlertWindow alertWindow = new AlertWindow();
            alertWindow.generateError(
                    "Error",
                    "Ocurrió un error al obtener los prestamos.",
                    null
            );
        });

        Thread thread = new Thread(getPrestamosTask);
        thread.setDaemon(true);
        thread.start();
    }

    private List<PrestamoListDTO> generatePrestamosList() {
        try {
            return prestamoService.getAllPrestamosToday(
                    columnToSearch, coincidenceToSearch, orderByColumn, orderDesc
            );

        } catch (Exception e) {
            System.out.println("Ocurrió un error al ejecutar la tarea para recuperar los prestamos.");
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<PrestamoListDTO> generatePrestamosReturnedList() {
        try {
            return prestamoService.getReturnedPrestamosToday(
                    columnToSearch1, coincidenceToSearch1, orderByColumn1, orderDesc
            );

        } catch (Exception e) {
            System.out.println("Ocurrió un error al ejecutar la tarea para recuperar los prestamos devueltos.");
            throw new RuntimeException(e.getMessage());
        }
    }

    private void setValues() {
        /**
         * Asignar valores para realizar la búsqueda de préstamos del dia actual
         */

        // Columna en la que se busca coincidencia
        columnToSearch = PrestamosViewTitlesMenuBtn.FECHA_INICIO;

        // Texto a buscar
        coincidenceToSearch = LocalDate.now().toString();

        // Ordenamiento por columna
        orderByColumn = PrestamosViewTitlesMenuBtn.FECHA_INICIO;

        // Ordenamiento ascendente o desc
        orderDesc = false;

        // Columna en la que se busca coincidencia
        columnToSearch1 = PrestamosViewTitlesMenuBtn.FECHA_ENTREGA;

        // Texto a buscar
        coincidenceToSearch1 = LocalDate.now().toString();

        // Ordenamiento por columna
        orderByColumn1 = PrestamosViewTitlesMenuBtn.FECHA_ENTREGA;
    }

    private void setColumns() {
        // nombre de los atributos del tipo de dato asignado a cada TableColumn
        columnId.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        columnFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        columnIdEjem.setCellValueFactory(new PropertyValueFactory<>("idEjemplar"));
        columnTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        columnCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        columnAtendio.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        columnId1.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        columnFechaInicio1.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        columnFechaEntrega1.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        columnIdEjem1.setCellValueFactory(new PropertyValueFactory<>("idEjemplar"));
        columnTitulo1.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnAutor1.setCellValueFactory(new PropertyValueFactory<>("autor"));
        columnCliente1.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        columnAtendio1.setCellValueFactory(new PropertyValueFactory<>("usuario"));
    }

    public void refreshData() {
        showPrestamos();
    }
}
