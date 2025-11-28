package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.repository.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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

    private CategoriaRepository categoriaRepository;
    private EditorialRepository editorialRepository;

    private List<CategoriaModel> listCat;
    private List<EditorialModel> listEdit;
    private List<AutorModel> listAutor;

    @FXML
    public void initialize() {
        categoriaRepository = new CategoriaRepositoryImpl();
        editorialRepository = new EditorialRepositoryImpl();


        loadCategorias();
        loadEditoriales();
        loadAutor();

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

    private void loadCategorias() {

    }

    private void loadEditoriales() {

    }

    private void loadAutor() {

    }

    private void setCategoriasToListView() {

    }

    private void setEditorialesToListView() {

    }

    private void setAutoresToListView() {

    }
}
