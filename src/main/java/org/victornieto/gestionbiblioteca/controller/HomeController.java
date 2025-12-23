package org.victornieto.gestionbiblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.Main;
import org.victornieto.gestionbiblioteca.controller.tabs.*;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;

import java.io.IOException;
import java.util.Arrays;

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
