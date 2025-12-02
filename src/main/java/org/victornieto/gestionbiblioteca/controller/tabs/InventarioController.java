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
import org.victornieto.gestionbiblioteca.controller.tabs.inventario.AddUnitsController;
import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.service.LibroService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class InventarioController {

    @FXML public AnchorPane rootInventario;

    @FXML public MenuButton menuBtnCriterio;
    @FXML public TextField textFieldSearch;
    @FXML public Button btnSearch;
    @FXML public RadioButton radioBtnUnidades;
    @FXML public ToggleGroup showByOption;
    @FXML public RadioButton radioBtnTitulos;
    @FXML public MenuButton menuBtnOrdenCampo;
    @FXML public MenuButton menuBtnOrden;
    @FXML public ProgressBar progressBarLoad;
    @FXML public Label numberRows;
    @FXML public Label labelBuscando;

    @FXML public TableView<LibroInventarioDTO> tableInventario;
    @FXML public TableColumn<LibroInventarioDTO, Number> columnId;
    @FXML public TableColumn<LibroInventarioDTO, String> columnTitulo;
    @FXML public TableColumn<LibroInventarioDTO, String> columnAutor;
    @FXML public TableColumn<LibroInventarioDTO, String> columnCategoria;
    @FXML public TableColumn<LibroInventarioDTO, String> columnEditorial;
    @FXML public TableColumn<LibroInventarioDTO, String> columnEdicion;
    @FXML public TableColumn<LibroInventarioDTO, Year> columnYear;
    @FXML public TableColumn<LibroInventarioDTO, Number> columnPag;
    @FXML public TableColumn<LibroInventarioDTO, Number> columnUnidades;

    private ContextMenu contextMenu = new ContextMenu();

    private LibroService libroService;
    private List<LibroInventarioDTO> inventarioLibros;
    private Boolean showTitles;
    private String columnToSearch;
    private String coincidenceToSearch;
    private String orderByColumn;
    private Boolean orderDesc;

    @FXML
    public void initialize() {
        this.libroService = new LibroService();

        generateFunctionsMenuButton();
        setColumns();
        showInventario();
        createContextMenu();

    }

    @FXML
    private void showInventario() {
        labelBuscando.setVisible(true);
        tableInventario.getItems().clear();
        progressBarLoad.setVisible(true);
        progressBarLoad.setProgress(0.0);
        btnSearch.setDisable(true);
        setValues();
        progressBarLoad.setProgress(0.20);

        // Task para operaciones con BD
        Task<Void> getInventarioTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                inventarioLibros = generateInventarioLibros();
                progressBarLoad.setProgress(0.50);
                return null;
            }
        };

        // Si la tarea se ejecuta correctamente aunque no regrese resultados
        getInventarioTask.setOnSucceeded(e -> {
            if (inventarioLibros.isEmpty()) {
                // Lista vacia
                tableInventario.getItems().clear();

            } else {
                // Lista con resultados
                progressBarLoad.setProgress(0.70);
                ObservableList<LibroInventarioDTO> data = FXCollections.observableArrayList(inventarioLibros);
                tableInventario.setItems(data);
            }

            numberRows.setText(String.valueOf(inventarioLibros.size()));
            progressBarLoad.setProgress(1.0);
            progressBarLoad.setVisible(false);
            labelBuscando.setVisible(false);
            btnSearch.setDisable(false);
        });

        // La tarea no se ejecuta correctamente debido a un error durante la ejecucion
        getInventarioTask.setOnFailed(e -> {
            numberRows.setText("0");
            progressBarLoad.setProgress(1.0);
            btnSearch.setDisable(false);
        });

        Thread thread = new Thread(getInventarioTask);
        thread.setDaemon(true);
        thread.start();
    }

    public void add(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/inventario/windowAdd.fxml"));
        Parent parent = fxmlLoader.load();

        Stage newStage = new Stage();
        newStage.setTitle("Agregar");
        newStage.setScene(new Scene(parent));

        newStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(stageCurrent);

        newStage.showAndWait();
    }

    private void createContextMenu() {
        /**
         * Creación de ContextMenu para opciones de agregar y eliminar
         */
        MenuItem itemAddUnit = new MenuItem("Agregar ejemplar");
        MenuItem itemDelete = new MenuItem("Eliminar selección");

        contextMenu.getItems().addAll(itemAddUnit, itemDelete);

        itemAddUnit.setOnAction(e -> {
            try {
                openWindowAddUnits(e);
            } catch (IOException ex) {
                AlertWindow window = new AlertWindow();
                window.generateError("Error", "Se generó un problema al abrir la ventana.", null);
            }
        });

        itemDelete.setOnAction(e -> openWindowDelete(e));

        // asignar ContextMenu a cada fila
        setContextMenuPerRow();
    }

    private void openWindowAddUnits(ActionEvent event) throws IOException {
        LibroInventarioDTO selected = tableInventario.getSelectionModel().getSelectedItem();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/inventario/addUnits.fxml"));
        Parent parent = fxmlLoader.load();

        AddUnitsController addUnitsController = fxmlLoader.getController();
        addUnitsController.setTitle(selected.getTitulo());
        addUnitsController.setId_libro(Long.valueOf(selected.getId_libro()));

        Stage newStage = new Stage();
        newStage.setTitle("Agregar");
        newStage.setScene(new Scene(parent));

        newStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) tableInventario.getScene().getWindow();
        newStage.initOwner(stageCurrent);
        newStage.setWidth(400);
        newStage.setHeight(260);
        newStage.setResizable(false);

        newStage.showAndWait();
    }

    private void openWindowDelete(ActionEvent event) {
        System.out.println("Eliminar selccion presionado");
    }

    private void setContextMenuPerRow() {
        tableInventario.setRowFactory(tv -> {
            TableRow<LibroInventarioDTO> row = new TableRow<>();

            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    tableInventario.getSelectionModel().select(row.getIndex());
                    contextMenu.show(row, event.getSceneX(), event.getScreenY());
                }
            });

            // Evitar mostrar menú en filas vacias
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.SECONDARY) && row.isEmpty()) {
                    contextMenu.hide();
                }
            });

            return row;
        });
    }

    private void setColumns() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id_libro"));
        columnTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        columnCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        columnEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        columnYear.setCellValueFactory(new PropertyValueFactory<>("anio_publicacion"));
        columnPag.setCellValueFactory(new PropertyValueFactory<>("paginas"));
        columnEdicion.setCellValueFactory(new PropertyValueFactory<>("edicion"));
        columnUnidades.setCellValueFactory(new PropertyValueFactory<>("unidades"));
    }

    private void setValues() {
        /**
         * Obtiene los valores de los elementos de la interfaz y los asigna a variables
         */

        // Mostrar por unidades o titulos
        if (radioBtnTitulos.isSelected()) {
            showTitles = true;
        } else {
            showTitles = false;
        }

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

    private List<LibroInventarioDTO> generateInventarioLibros() {
        /**
         *
         * Utiliza service para obtener los datos de los libros en inventario
         *
         * @return una lista con los resultados, puede ser vacia o no dependiendo del resultado de la consulta
         */

        try {
            return libroService.getLibrosForInventario(
                    showTitles,
                    columnToSearch,
                    coincidenceToSearch,
                    orderByColumn,
                    orderDesc);

        } catch (Exception e) {
            System.out.println("Ocurrió un error la ejecutar la tarea para recuperar el inventario");
            return new ArrayList<>();
        }
    }

    private void generateFunctionsMenuButton() {
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

}
