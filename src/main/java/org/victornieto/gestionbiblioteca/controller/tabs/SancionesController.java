package org.victornieto.gestionbiblioteca.controller.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.victornieto.gestionbiblioteca.dto.*;
import org.victornieto.gestionbiblioteca.service.PrestamoService;
import org.victornieto.gestionbiblioteca.service.SancionService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;
import org.victornieto.gestionbiblioteca.utility.LoadingDialog;
import org.victornieto.gestionbiblioteca.utility.SancionesViewTitlesMenuBtn;
import org.victornieto.gestionbiblioteca.utility.TypeSancion;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class SancionesController {
    @FXML public AnchorPane rootSanciones;
    @FXML public MenuButton menuBtnCriterio;
    @FXML public TextField textFieldSearch;
    @FXML public Button btnSearch;
    @FXML public MenuButton menuBtnOrdenCampo;
    @FXML public MenuButton menuBtnOrden;
    @FXML public Button btnUpdate;

    @FXML public Label labelBuscando;
    @FXML public ProgressBar progressBarLoad;
    @FXML public Label numberRows;

    @FXML public TableView<SancionesListDTO> tableSanciones;
    @FXML public TableColumn<SancionesListDTO, Long> columnId;
    @FXML public TableColumn<SancionesListDTO, String> columnSancion;
    @FXML public TableColumn<SancionesListDTO, String> columnDescripcion;
    @FXML public TableColumn<SancionesListDTO, LocalDate> columnFecha;
    @FXML public TableColumn<SancionesListDTO, String> columnCliente;
    @FXML public TableColumn<SancionesListDTO, Long> columnIdPrestamo;
    @FXML public TableColumn<SancionesListDTO, String> columnLibro;
    @FXML public TableColumn<SancionesListDTO, Long> columnIdEjemplar;

    private SancionService sancionService;
    private PrestamoService prestamoService;
    private List<SancionesListDTO> sancionesList;
    private List<SancionesListUpdateDTO> listToUpdate;
    private String columnToSearch;
    private String coincidenceToSearch;
    private String orderByColumn;
    private Boolean orderDesc;

    @FXML
    public void initialize() {
        this.sancionService = new SancionService();
        this.prestamoService = new PrestamoService();

        generateFunctionMenuButton();
        setColumns();
        showSanciones();
    }

    @FXML
    public void updateSanciones() {
        btnUpdate.setDisable(true);
        LoadingDialog loadingDialog = new LoadingDialog("Actualizando sanciones...");
        loadingDialog.show();
        final int[] amounUpdatedAndCreated = new int[2]; // 0: numero de sanciones actualizadas, 1: numero de sanciones creadas

        Task<Void> taskUpdate = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // sanciones a actualizar
                listToUpdate = sancionService.getListToUpdate();
                List<SancionToUpdateDTO> selectedToUpdate = selectSancionesToUpdate(listToUpdate);
                amounUpdatedAndCreated[0] =  applyUpdate(selectedToUpdate);

                // sanciones a crear
                List<PrestamoWithoutSancionDTO> prestamos = prestamoService.getPrestamosWithoutSancion();
                List<SancionFormDTO> prestamosToPenalize = selectPrestamosToPenalize(prestamos);
                amounUpdatedAndCreated[1] = createSanciones(prestamosToPenalize);

                return null;
            }
        };

        taskUpdate.setOnSucceeded(e -> {
            if (amounUpdatedAndCreated[0]==0 && amounUpdatedAndCreated[1]==0) {
                loadingDialog.completeMessage("No se realizaron movimientos.\nTodas las sanciones están al corriente.");

            } else if (amounUpdatedAndCreated[0]>0 && amounUpdatedAndCreated[1]==0) {
                loadingDialog.completeMessage("Actualización completada.\nCantidad de sanciones actualizadas: " + amounUpdatedAndCreated[0] + "\nCantidad de nuevas sanciones: 0");

            } else if (amounUpdatedAndCreated[0]==0 && amounUpdatedAndCreated[1]>0){
                loadingDialog.completeMessage("Actualización completada.\nCantidad de sanciones actualizadas: 0\nCantidad de nuevas sanciones: " + amounUpdatedAndCreated[1]);

            } else if (amounUpdatedAndCreated[0]>0 && amounUpdatedAndCreated[1]>0) {
                loadingDialog.completeMessage("Actualización completada.\nCantidad de sanciones actualizadas: " + amounUpdatedAndCreated[0] + "\nCantidad de nuevas sanciones: " + amounUpdatedAndCreated[1]);
            }
            showSanciones(); // volver a cargar sanciones
            btnUpdate.setDisable(false);
        });

        taskUpdate.setOnFailed(e -> {
            loadingDialog.close();
            System.out.println(taskUpdate.getException().getMessage());
            AlertWindow alertWindow = new AlertWindow();
            alertWindow.generateError("Error", "Error al actualizar las sanciones",null);
            btnUpdate.setDisable(false);
        });

        Thread thread = new Thread(taskUpdate);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    public void showSanciones() {
        labelBuscando.setVisible(true);
        tableSanciones.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        btnSearch.setDisable(true);
        setValues();
        progressBarLoad.setProgress(0.20);

        // Task para operaciones con BD
        Task<Void> getSancionesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                sancionesList = generateSancionesList();
                progressBarLoad.setProgress(0.50);
                return null;
            }
        };

        // Tarea ejecutada correctamente
        getSancionesTask.setOnSucceeded(e -> {
            if (sancionesList.isEmpty()) {
                tableSanciones.getItems().clear();

            } else {
                progressBarLoad.setProgress(0.70);
                ObservableList<SancionesListDTO> data = FXCollections.observableArrayList(sancionesList);
                tableSanciones.setItems(data);
            }

            numberRows.setText(String.valueOf(sancionesList.size()));
            progressBarLoad.setProgress(1.0);
            progressBarLoad.setVisible(false);
            labelBuscando.setVisible(false);
            btnSearch.setDisable(false);
        });

        // Tarea no se ejecuta correctamente
        getSancionesTask.setOnFailed(e -> {
            numberRows.setText("0");
            progressBarLoad.setProgress(1.0);
            btnSearch.setDisable(false);

            // Mostrar ventana de error
            System.out.println("Error: " + getSancionesTask.getException().getMessage());
            AlertWindow alertWindow = new AlertWindow();
            alertWindow.generateError(
                    "Error",
                    "Ocurrió un error al obtener las sanciones.",
                    null
            );
        });

        Thread thread = new Thread(getSancionesTask);
        thread.setDaemon(true);
        thread.start();
    }

    private List<SancionesListDTO> generateSancionesList() {
        /**
         *
         * Utiliza service para obtener los datos de las sanciones
         *
         * @return una lista con los resultados, puede ser vacia o no dependiendo del resultado de la consulta
         */

        try {
            return sancionService.getAllList(
                    columnToSearch, coincidenceToSearch, orderByColumn, orderDesc
            );

        } catch (Exception e) {
            System.out.println("Ocurrió un error al ejecutar la tarea para recuperar las sanciones.");
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<SancionToUpdateDTO> selectSancionesToUpdate(List<SancionesListUpdateDTO> listToUpdate) {
        List<SancionToUpdateDTO> list = new ArrayList<>();
        for (SancionesListUpdateDTO sancion: listToUpdate) {
            long daysDiff = ChronoUnit.DAYS.between(sancion.fechaEntrega(), LocalDate.now());
            long newId = selectTypeSancion(daysDiff);

            if (sancion.id_tipo() != newId) {
                list.add(
                        new SancionToUpdateDTO(sancion.id(), newId, LocalDate.now())
                );
            }
        }

        return list;
    }

    private List<SancionFormDTO> selectPrestamosToPenalize(List<PrestamoWithoutSancionDTO> prestamos) {
        List<SancionFormDTO> listToPenalize = new ArrayList<>();
        for (PrestamoWithoutSancionDTO prestamo: prestamos) {
            long daysDiff = ChronoUnit.DAYS.between(prestamo.fechaEntrega(), LocalDate.now());
            long idSancion = selectTypeSancion(daysDiff);

            if (idSancion != 0) {
                // Aplicar penalización
                listToPenalize.add(
                        new SancionFormDTO(
                                idSancion,
                                prestamo.idCliente(),
                                LocalDate.now(),
                                prestamo.idPrestamo()
                        )
                );
            }
        }
        return listToPenalize;
    }

    private Long selectTypeSancion(Long daysDiff) {
        /**
         * Asigna el valor del ID del tipo de retardo que corresponde
         */
        if (daysDiff>=TypeSancion.RETARDO_1.get("min") && daysDiff<=TypeSancion.RETARDO_1.get("max"))
            return Long.valueOf(TypeSancion.RETARDO_1.get("id"));

        else if (daysDiff>=TypeSancion.RETARDO_2.get("min") && daysDiff<=TypeSancion.RETARDO_2.get("max"))
            return Long.valueOf(TypeSancion.RETARDO_2.get("id"));

        else if (daysDiff>=TypeSancion.RETARDO_3.get("min") && daysDiff<=TypeSancion.RETARDO_3.get("max"))
            return Long.valueOf(TypeSancion.RETARDO_3.get("id"));

        else if (daysDiff>=TypeSancion.RETARDO_4.get("min") && daysDiff<=TypeSancion.RETARDO_4.get("max"))
            return Long.valueOf(TypeSancion.RETARDO_4.get("id"));

        else if (daysDiff>=TypeSancion.RETARDO_5.get("min"))
            return Long.valueOf(TypeSancion.RETARDO_5.get("id"));

        else return 0L;
    }

    private Integer applyUpdate(List<SancionToUpdateDTO> sanciones) {
        int count = 0;
        boolean updated;

        try {
            for (SancionToUpdateDTO sancion: sanciones) {
                updated = sancionService.updateSancion(sancion);
                if (updated) {
                    count++;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return count;
    }

    private Integer createSanciones(List<SancionFormDTO> sanciones) {
        int count = 0;
        boolean created;
        try {
            for (SancionFormDTO sancion: sanciones) {
                created = sancionService.create(sancion);
                if (created) {
                    count++;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return count;
    }

    private void generateFunctionMenuButton() {
        // Funciones para cada menuItem para actualizar el texto de menuBtnCriterio
        for (MenuItem item: menuBtnCriterio.getItems()) {
            item.setOnAction(e -> menuBtnCriterioConfig(item));
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

    private void menuBtnCriterioConfig(MenuItem item) {
        /**
         * Configura los MenuItem para la búsqueda por campo y las cadenas permitidas para el campo de búsqueda
         */
        // Actualiza el valor de MenuButton
        menuBtnCriterio.setText(item.getText());

        // Dependiendo del valor de MenuButton se asigna un comportamiento al TextField
        if(menuBtnCriterio.getText().equals(SancionesViewTitlesMenuBtn.ID) || menuBtnCriterio.getText().equals(SancionesViewTitlesMenuBtn.ID_EJEMPLAR) || menuBtnCriterio.getText().equals(SancionesViewTitlesMenuBtn.ID_PRESTAMO)) { // Numeros
            textFieldSearch.setText(""); // limpiar campo
            textFieldSearch.setPromptText("Ingresa la búsqueda");

            textFieldSearch.setTextFormatter(new TextFormatter<>(change -> {
                String newValue = change.getControlNewText();
                if (newValue.matches("\\d+") || newValue.isEmpty()) {
                    return change;
                }
                return null;
            }));

        } else if (menuBtnCriterio.getText().equals(SancionesViewTitlesMenuBtn.FECHA)) { // Fechas
            textFieldSearch.setText(""); // limpiar campo
            textFieldSearch.setPromptText("AAAA-MM-DD");

            textFieldSearch.setTextFormatter(new TextFormatter<>(change -> {
                String newValue = change.getControlNewText();

                // Permitir vacío
                if (newValue.isEmpty()) {
                    return change;
                }

                // Permitir dígitos y guiones mientras se escribe
                if (!newValue.matches("[0-9-]*")) {
                    return null;
                }

                // Limitar longitud máxima 10
                if (newValue.length() > 10) {
                    return null;
                }

                // Si todavía no tiene el largo completo de fecha, y verificar que se introduzcan guiones para el formato
                if (newValue.length() < 10) {
                    if(newValue.length()==5 && newValue.charAt(4)!='-') {
                        return null;
                    }
                    if(newValue.length()==8 && newValue.charAt(7)!='-') {
                        return null;
                    }
                    return change;
                }

                // Si ya tiene exactamente 10 caracteres, validar formato yyyy-MM-dd
                if (!newValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    return null;
                }

                return change;
            }));

        } else {
            textFieldSearch.setPromptText("Ingresa la búsqueda");
            textFieldSearch.setTextFormatter(new TextFormatter<>(change -> change));
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

    private void setColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnSancion.setCellValueFactory(new PropertyValueFactory<>("sancion"));
        columnDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        columnIdPrestamo.setCellValueFactory(new PropertyValueFactory<>("idPrestamo"));
        columnLibro.setCellValueFactory(new PropertyValueFactory<>("libro"));
        columnIdEjemplar.setCellValueFactory(new PropertyValueFactory<>("idEjemplar"));

        // corregir filas dobles
        forceSingleLine(columnDescripcion);
        forceSingleLine(columnLibro);
        forceSingleLine(columnCliente);
    }

    private void forceSingleLine(TableColumn<SancionesListDTO, String> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.replaceAll("[\\r\\n]+", " ").trim());
                    setWrapText(false);
                }
            }
        });
    }
}
