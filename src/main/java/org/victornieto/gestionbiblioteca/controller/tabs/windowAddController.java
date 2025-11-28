package org.victornieto.gestionbiblioteca.controller.tabs;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class windowAddController {
    @FXML public TextField labelTitulo;
    @FXML public TextField labelEdicion;
    @FXML public TextField LabelAnio;

    @FXML public ListView<String> listAutoresSelected;
    @FXML public ListView<String> listAutoresAvailable;
    @FXML public ListView<String> listCatSelected;
    @FXML public ListView<String> listCatAvailable;
    @FXML public ListView<String> listEditSelected;
    @FXML public ListView<String> listEditAvailable;

    @FXML
    public void initialize() {

    }

    @FXML
    public void addAutor() {

    }

    @FXML
    public void removeAutor() {

    }

    @FXML
    public void createAutor() {

    }

    @FXML
    public void addCategoria() {

    }

    @FXML
    public void removeCategoria() {

    }

    @FXML
    public void createCategoria() {

    }

    @FXML
    public void selectEditorial() {

    }

    @FXML
    public void removeEditorial() {

    }

    @FXML
    public void createEditorial() {

    }
}
