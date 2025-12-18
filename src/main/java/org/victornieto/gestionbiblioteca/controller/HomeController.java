package org.victornieto.gestionbiblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.victornieto.gestionbiblioteca.controller.tabs.*;
import org.victornieto.gestionbiblioteca.model.UsuarioModel;

public class HomeController {

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
