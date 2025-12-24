package org.victornieto.gestionbiblioteca.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.Main;
import org.victornieto.gestionbiblioteca.controller.tabs.*;
import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;
import org.victornieto.gestionbiblioteca.service.LibroService;
import org.victornieto.gestionbiblioteca.service.PdfService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;
import org.victornieto.gestionbiblioteca.utility.FileGenerator;
import org.victornieto.gestionbiblioteca.utility.LoadingDialog;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class HomeController {

    @FXML private VBox root;

    @FXML private AnchorPane tabInventario;
    @FXML private InventarioController tabInventarioController;

    @FXML private AnchorPane tabClientes;
    @FXML private ClientesControllers tabClientesController;

    @FXML private AnchorPane tabPrestamos;
    @FXML private PrestamosController tabPrestamosController;

    @FXML private TabPane tabPane;
    @FXML private Tab tabReportesTab;
    @FXML private AnchorPane tabReportes;
    @FXML private ReportesController tabReportesController;

    @FXML private AnchorPane tabSanciones;
    @FXML private SancionesController tabSancionesController;

    private UsuarioModel userLogged;

    @FXML
    public void initialize() {
        tabPane.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldTab, newTab) -> {
            if (newTab==tabReportesTab && tabReportesController!=null) {
                tabReportesController.refreshData();
            }
        });
    }

    @FXML
    public void openInfo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/info.fxml"));
            Parent parent = fxmlLoader.load();

            Stage newStage = new Stage();
            newStage.setTitle("Información");
            newStage.setScene(new Scene(parent));
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.setResizable(false);
            newStage.setHeight(300);
            newStage.setWidth(400);

            Stage currentStage = (Stage) root.getScene().getWindow();
            newStage.initOwner(currentStage);
            newStage.showAndWait();

        } catch (IOException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
        }
    }

    @FXML
    public void reportTitles() {
        File file = new FileGenerator().createFile("reporte_titulos_libro", root);

        if (file != null) {
            AlertWindow alertWindow = new AlertWindow();
            LoadingDialog dialog = new LoadingDialog("Generando reporte.");
            dialog.show();

            Task<List<LibroInventarioDTO>> taskReport = new Task<>() {
                @Override
                protected List<LibroInventarioDTO> call() {
                    return new LibroService().getLibrosForInventario(
                            true,
                            "Todos",
                            "",
                            "ID",
                            false);
                }
            };

            taskReport.setOnSucceeded(e -> {
                try {
                    // generar reporte
                    List<LibroInventarioDTO> list = taskReport.getValue();
                    String[] headers = {"ID", "Título", "Autor", "Categoría", "Editorial", "Edición", "Año", "Páginas", "Unidades"};
                    float[] colWidths = {40, 200, 200, 60, 60, 60, 40, 40, 40};
                    new PdfService().generateTitlesUnitsBooks(list, headers, colWidths, userLogged, file);
                    dialog.close();
                    alertWindow.generateInformation("Información", "Operación exitosa.", "Reporte generado correctamente en: \n"+file);

                } catch (Exception ex) {
                    System.out.println("Error al crear pdf: " + Arrays.toString(ex.getStackTrace()));
                    dialog.close();
                    alertWindow.generateError("Error", "Ocurrió un error generar el reporte de títulos de libros", null);
                }
            });

            taskReport.setOnFailed(e -> {
                dialog.close();
                alertWindow.generateError("Error", "Ocurrió un error generar el reporte de títulos de libros", null);
            });

            Thread thread = new Thread(taskReport);
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    public void logout() {
        try {
            clearSession();
            Stage currentStage = (Stage) root.getScene().getWindow();
            openLoginWindow();
            currentStage.close();
        } catch (IOException e) {
            System.out.println("Error: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void clearSession() {
        userLogged = null;
    }

    private void openLoginWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        Stage stage = new Stage();

        stage.setTitle("Gestión de biblioteca");
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(400);

        stage.show();
    }

    public void setUserLogged(UsuarioModel userLogged) {
        this.userLogged = userLogged;
        initChildControllers();
    }

    private void initChildControllers() {
        if (tabPrestamosController != null) {
            tabPrestamosController.setUserLogged(userLogged);
        }
    }
}
