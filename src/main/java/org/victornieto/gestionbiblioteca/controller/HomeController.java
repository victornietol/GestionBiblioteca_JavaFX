package org.victornieto.gestionbiblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.victornieto.gestionbiblioteca.controller.tabs.ClientesControllers;
import org.victornieto.gestionbiblioteca.controller.tabs.InventarioController;
import org.victornieto.gestionbiblioteca.controller.tabs.PrestamosController;
import org.victornieto.gestionbiblioteca.controller.tabs.ReportesController;

public class HomeController {

    @FXML
    private AnchorPane tabInventario;
    @FXML
    private InventarioController tabInventarioController;

    @FXML
    private AnchorPane tabClientes;
    @FXML
    private ClientesControllers tabClientesController;

    @FXML
    private AnchorPane tabPrestamos;
    @FXML
    private PrestamosController tabPrestamosController;

    @FXML
    private AnchorPane tabReportes;
    @FXML
    private ReportesController tabReportesController;


}
