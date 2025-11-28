package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.repository.CategoriaRepositoryImpl;
import org.victornieto.gestionbiblioteca.repository.EditorialRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class WindowAddController {
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
