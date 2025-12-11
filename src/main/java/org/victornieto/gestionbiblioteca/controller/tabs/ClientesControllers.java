package org.victornieto.gestionbiblioteca.controller.tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.victornieto.gestionbiblioteca.controller.tabs.clientes.SancionesClienteController;
import org.victornieto.gestionbiblioteca.controller.tabs.prestamos.AddPrestamoController;
import org.victornieto.gestionbiblioteca.dto.ClienteListDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.service.ClienteService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;
import org.victornieto.gestionbiblioteca.utility.ClientesViewTitlesMenuBtn;
import org.victornieto.gestionbiblioteca.utility.PrestamosViewTitlesMenuBtn;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ClientesControllers {

    @FXML public AnchorPane rootClientes;

    @FXML public Button btnDelete;
    @FXML public MenuButton menuBtnCriterio;
    @FXML public TextField textFieldSearch;
    @FXML public Button btnSearch;
    @FXML public MenuButton menuBtnOrdenCampo;
    @FXML public MenuButton menuBtnOrden;

    @FXML public TableView<ClienteListDTO> tableClientes;
    @FXML public TableColumn<ClienteListDTO, String> columnUsername;
    @FXML public TableColumn<ClienteListDTO, String> columnNombre;
    @FXML public TableColumn<ClienteListDTO, String> columnCorreo;
    @FXML public TableColumn<ClienteListDTO, Integer> columnPrestamos;
    @FXML public TableColumn<ClienteListDTO, Integer> columnSanciones;
    @FXML public TableColumn<ClienteListDTO, LocalDate> columnFechaCreacion;

    @FXML public Label labelBuscando;
    @FXML public ProgressBar progressBarLoad;
    @FXML public Label numberRows;

    private ClienteService clienteService;
    private List<ClienteListDTO> clientesList;
    private String columnToSearch;
    private String coincidenceToSearch;
    private String orderByColumn;
    private Boolean orderDesc;

    private ContextMenu contextMenu = new ContextMenu();

    @FXML
    public void initialize() {
        this.clienteService = new ClienteService();

        generateFunctionMenuButton();
        setColumns();
        showClientes();
    }

    @FXML
    public void add(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/clientes/addCliente.fxml"));
        Parent parent = fxmlLoader.load();

        Stage newStage = new Stage();
        newStage.setTitle("Nuevo");
        newStage.setScene(new Scene(parent));

        newStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(stageCurrent);

        newStage.showAndWait();
        showClientes();
    }

    @FXML
    public void deleteCliente() {
        btnDelete.setDisable(true);
        AlertWindow alertWindow = new AlertWindow();

        try {
            ClienteListDTO selected = tableClientes.getSelectionModel().getSelectedItem();
            if (selected == null) {
                throw new NullPointerException("Ocurrió un error al seleccionar un elemento. Verifique que se haya\nseleccionado un cliente de la lista.");
            }

            boolean confirm = alertWindow.generateConfirmation(
                    "Confirmación",
                    "¿Estás seguro de confirmar la eliminación del siguiente cliente?",
                    "Detalles:\n\nID del cliente: " + selected.id() +
                            "\nNombre: " + selected.nombre() +
                            "\nUsername: " + selected.username() +
                            "\nCorreo: " + selected.correo()
            );

            if (confirm) {
                boolean returned = clienteService.remove(selected.id());

                if (returned) {
                    alertWindow.generateInformation(
                            "Información",
                            "Se eliminó el cliente con éxito.",
                            "Detalles:\n\nID cliente: " + selected.id() +
                                    "\nUsername: " + selected.username()
                    );
                    showClientes();
                } else {
                    throw new RuntimeException("Ocurrió un error al eliminar.");
                }
            }


        } catch (Exception e) {
            alertWindow.generateError("Error", e.getMessage(),null);
        }

        btnDelete.setDisable(false);
    }

    @FXML
    public void showClientes() {
        labelBuscando.setVisible(true);
        tableClientes.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        btnSearch.setDisable(true);
        setValues();
        progressBarLoad.setProgress(0.20);

        // Task para operaciones con BD
        Task<Void> getClientesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                clientesList = generateClientesList();
                progressBarLoad.setProgress(0.50);
                return null;
            }
        };

        // Tarea ejecutada correctamente
        getClientesTask.setOnSucceeded(e -> {
            if (clientesList.isEmpty()) {
                tableClientes.getItems().clear();

            } else {
                progressBarLoad.setProgress(0.70);
                ObservableList<ClienteListDTO> data = FXCollections.observableArrayList(clientesList);
                tableClientes.setItems(data);
                createContextMenuPerPrestamo();
            }

            numberRows.setText(String.valueOf(clientesList.size()));
            progressBarLoad.setProgress(1.0);
            progressBarLoad.setVisible(false);
            labelBuscando.setVisible(false);
            btnSearch.setDisable(false);
        });

        // Tarea no se ejecuta correctamente
        getClientesTask.setOnFailed(e -> {
            numberRows.setText("0");
            progressBarLoad.setProgress(1.0);
            btnSearch.setDisable(false);

            // Mostrar ventana de error
            System.out.println("Error: " + getClientesTask.getException().getMessage());
            AlertWindow alertWindow = new AlertWindow();
            alertWindow.generateError(
                    "Error",
                    "Ocurrio un error al obtener los clientes.",
                    null
            );
        });

        Thread thread = new Thread(getClientesTask);
        thread.setDaemon(true);
        thread.start();
    }

    private List<ClienteListDTO> generateClientesList() {
        /**
         *
         * Utiliza service para obtener los datos de los clientes
         *
         * @return una lista con los resultados, puede ser vacia o no dependiendo del resultado de la consulta
         */

        try {
            return clienteService.getListDTOAll(
                    columnToSearch, coincidenceToSearch, orderByColumn, orderDesc
            );

        } catch (Exception e) {
            System.out.println("Ocurrió un error al ejecutar la tarea para recuperar los clientes");
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createContextMenuPerPrestamo() {
        MenuItem itemDeleteCliente = new MenuItem("Eliminar cliente");
        MenuItem itemSanciones = new MenuItem("Ver sanciones");

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(itemDeleteCliente, itemSanciones);

        itemDeleteCliente.setOnAction(e -> deleteCliente());

        itemSanciones.setOnAction(e -> {
            try {
                showSanciones(e);
            } catch (IOException ex) {
                AlertWindow alertWindow = new AlertWindow();
                alertWindow.generateError("Error", "Ocurrió un problema al obtener las sanciones", null);
            }
        });

        setContextMenuPerRow();
    }

    private void setContextMenuPerRow() {
        tableClientes.setRowFactory(tv -> {
            TableRow<ClienteListDTO> row = new TableRow<>();

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tableClientes.getSelectionModel().select(row.getIndex());
                    contextMenu.show(row, event.getSceneX(), event.getScreenY());
                }
            });

            // Evitar mostrar menú en filas vacías
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && row.isEmpty()) {
                    contextMenu.hide();
                }
            });

            return row;
        });
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
        if(menuBtnCriterio.getText().equals(ClientesViewTitlesMenuBtn.PRESTAMOS) || menuBtnCriterio.getText().equals(ClientesViewTitlesMenuBtn.SANCIONES)) { // Numeros
            textFieldSearch.setText(""); // limpiar campo
            textFieldSearch.setPromptText("Ingresa la búsqueda");

            textFieldSearch.setTextFormatter(new TextFormatter<>(change -> {
                String newValue = change.getControlNewText();
                if (newValue.matches("\\d+") || newValue.isEmpty()) {
                    return change;
                }
                return null;
            }));

        } else if (menuBtnCriterio.getText().equals(ClientesViewTitlesMenuBtn.FECHA_CREACION)) { // Fechas
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

    private void showSanciones(ActionEvent event) throws IOException {
        // Validar selección del cliente
        ClienteListDTO selected = tableClientes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione un cliente primero.");
            alert.show();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/clientes/sancionesCliente.fxml"));
        Parent parent = fxmlLoader.load();

        // obtener nombre del cliente
        SancionesClienteController controller = fxmlLoader.getController();
        controller.setCliente(selected);

        Stage newStage = new Stage();
        newStage.setTitle("Sanciones del cliente");
        newStage.setScene(new Scene(parent));
        newStage.initModality(Modality.WINDOW_MODAL);

        MenuItem menuItem = (MenuItem) event.getSource();
        Window window = menuItem.getParentPopup().getOwnerWindow();

        newStage.initOwner(window);
        newStage.showAndWait();
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
        // nombre de los atributos del tipo de dato asignado a cada TableColumn
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        columnPrestamos.setCellValueFactory(new PropertyValueFactory<>("prestamos"));
        columnSanciones.setCellValueFactory(new PropertyValueFactory<>("sanciones"));
        columnFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
    }
}
