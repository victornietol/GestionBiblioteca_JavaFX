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
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;
import org.victornieto.gestionbiblioteca.service.*;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;
import org.victornieto.gestionbiblioteca.utility.FileGenerator;
import org.victornieto.gestionbiblioteca.utility.LoadingDialog;
import org.victornieto.gestionbiblioteca.utility.PrestamosViewTitlesMenuBtn;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
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
    private UsuarioModel userLogged;

    private String columnToSearch;
    private String coincidenceToSearch;
    private String orderByColumn;
    private boolean orderDesc;
    private List<PrestamoListDTO> prestamosList;
    private String columnToSearch1;
    private String coincidenceToSearch1;
    private String orderByColumn1;
    private List<PrestamoListDTO> prestamosReturnedList;

    private Integer activePrestamos;
    private Integer newClientesToday;
    private Integer activeClientes;
    private Integer activeSanciones;


    @FXML
    public void initialize() {
        this.prestamoService = new PrestamoService();
        this.clienteService = new ClienteService();
        this.sancionService = new SancionService();

        setColumns();
        showPrestamos();
    }

    @FXML
    public void reportDay() {
        File file = new FileGenerator().createFile("reporte_resumen", rootReportes);

        if (file != null) {
            AlertWindow alertWindow = new AlertWindow();
            LoadingDialog dialog = new LoadingDialog("Generando reporte.");
            dialog.show();

            Task<Void> taskReport = new Task<>() {
                @Override
                protected Void call() throws IOException {
                    String[] headers = {
                            PrestamosViewTitlesMenuBtn.ID,
                            "Inicio",
                            "Entrega",
                            PrestamosViewTitlesMenuBtn.ID_EJEMPLAR,
                            PrestamosViewTitlesMenuBtn.TITULO,
                            PrestamosViewTitlesMenuBtn.AUTOR,
                            PrestamosViewTitlesMenuBtn.CLIENTE,
                            PrestamosViewTitlesMenuBtn.ATENDIO
                    };
                    float[] colWidths = {40, 60, 60, 70, 180, 100, 180, 80};
                    new PdfService().generateResumeToday(
                            file,
                            prestamosList,
                            prestamosReturnedList,
                            activePrestamos,
                            newClientesToday,
                            activeClientes,
                            activeSanciones,
                            headers,
                            colWidths,
                            userLogged
                    );
                    return null;
                }
            };

            taskReport.setOnSucceeded(e -> {
                dialog.close();
                alertWindow.generateInformation("Información", "Operación exitosa.", "Reporte generado correctamente en: \n"+file);
            });

            taskReport.setOnFailed(e -> {
                System.out.println("Error: "+ Arrays.toString(taskReport.getException().getStackTrace()));
                dialog.close();
                alertWindow.generateError("Error", "Ocurrió un error generar el reporte con el resumen de hoy.", null);
            });

            Thread thread = new Thread(taskReport);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void showPrestamos() {
        labelBuscando.setVisible(true);
        tablePrestamos.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        setValues();
        progressBarLoad.setProgress(0.20);

        // Task para operaciones con BD
        Task<Void> getPrestamosTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                prestamosList = generatePrestamosList();
                prestamosReturnedList = generatePrestamosReturnedList();
                activePrestamos = prestamoService.getNumberActivePrestamos();
                newClientesToday = clienteService.getNewClientesToday();
                activeClientes = clienteService.getActiveClientes();
                activeSanciones = sancionService.getActiveAmount();
                progressBarLoad.setProgress(0.50);
                return null;
            }
        };

        // Tarea ejecutada correctamente
        getPrestamosTask.setOnSucceeded(e -> {
            if (prestamosList.isEmpty()) { // Prestamos iniciados hoy
                tablePrestamos.getItems().clear();
            } else {
                ObservableList<PrestamoListDTO> data = FXCollections.observableArrayList(prestamosList);
                tablePrestamos.setItems(data);
            }

            if (prestamosReturnedList.isEmpty()) { // Prestamos devueltos hoy
                tablePrestamos1.getItems().clear();
            } else {
                ObservableList<PrestamoListDTO> data1 = FXCollections.observableArrayList(prestamosReturnedList);
                tablePrestamos1.setItems(data1);
            }

            labelPrestamosActuales.setText(activePrestamos.toString());
            labelNuevosUsuarios.setText(newClientesToday.toString());
            labelUsuariosActuales.setText(activeClientes.toString());
            labelSanciones.setText(activeSanciones.toString());
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

    public void setUserLogged(UsuarioModel userLogged) {
        this.userLogged = userLogged;
    }
}
