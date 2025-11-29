package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.repository.*;
import org.victornieto.gestionbiblioteca.service.AutorService;
import org.victornieto.gestionbiblioteca.service.CategoriaService;
import org.victornieto.gestionbiblioteca.service.EditorialService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class WindowAddController {
    @FXML public TextField labelTitulo;
    @FXML public TextField labelEdicion;
    @FXML public TextField LabelAnio;

    @FXML public ListView<String> listViewAutoresSelected;
    @FXML public ListView<String> listViewAutoresAvailable;
    @FXML public ListView<String> listViewCatSelected;
    @FXML public ListView<String> listViewCatAvailable;
    @FXML public ListView<String> listViewEditSelected;
    @FXML public ListView<String> listViewEditAvailable;

    private final AutorService autorService = new AutorService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final EditorialService editorialService = new EditorialService();

    private List<CategoriaModel> listCat;
    private List<EditorialModel> listEdit;
    private List<AutorModel> listAutor;

    @FXML
    public void initialize() {
        loadCategorias();
        loadEditoriales();
        loadAutor();
    }

    @FXML
    public void addAutor() {
        String autor = listViewAutoresAvailable.getSelectionModel().getSelectedItem();
        if (autor!=null) {
            listViewAutoresAvailable.getItems().remove(autor); // remover de lista disponibles
            listViewAutoresSelected.getItems().add(autor); // agregar a lista seleccionados
            FXCollections.sort(listViewAutoresSelected.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void removeAutor() {
        String autor = listViewAutoresSelected.getSelectionModel().getSelectedItem();
        if (autor!=null) {
            listViewAutoresSelected.getItems().remove(autor);
            listViewAutoresAvailable.getItems().add(autor);
            FXCollections.sort(listViewAutoresAvailable.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void createAutor(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/inventario/newAutorForm.fxml"));
        Parent parent = fxmlLoader.load();

        Stage newStage = new Stage();
        newStage.setTitle("Agregar");
        newStage.setScene(new Scene(parent));

        newStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(stageCurrent);
        newStage.setResizable(false);

        newStage.showAndWait();

        // Si se creó nuevo autor actualizar lista de disponibles
        NewAutorFormController formController = fxmlLoader.getController();
        AutorModel newAutor = formController.getAutor();
        if (newAutor != null) {
            listViewAutoresAvailable.getItems().add(newAutor.getNombreCompleto());
            FXCollections.sort(listViewAutoresAvailable.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void addCategoria() {
        String categoria = listViewCatAvailable.getSelectionModel().getSelectedItem();
        if (categoria!=null) {
            listViewCatAvailable.getItems().remove(categoria);
            listViewCatSelected.getItems().add(categoria);
            FXCollections.sort(listViewCatSelected.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void removeCategoria() {
        String categoria = listViewCatSelected.getSelectionModel().getSelectedItem();
        if(categoria!=null) {
            listViewCatSelected.getItems().remove(categoria);
            listViewCatAvailable.getItems().add(categoria);
            FXCollections.sort(listViewCatAvailable.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void createCategoria(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/inventario/newCategoriaForm.fxml"));
        Parent parent = fxmlLoader.load();

        Stage newStage = new Stage();
        newStage.setTitle("Agregar");
        newStage.setScene(new Scene(parent));

        newStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(stageCurrent);
        newStage.setResizable(false);

        newStage.showAndWait();

        // Si se creó nueva categoria actualizar lista de disponibles
        NewCategoriaFormController formController = fxmlLoader.getController();
        CategoriaModel newCategoria = formController.getCategoria();
        if (newCategoria != null) {
            listViewCatAvailable.getItems().add(newCategoria.toString());
            FXCollections.sort(listViewCatAvailable.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void selectEditorial() {
        String editorial = listViewEditAvailable.getSelectionModel().getSelectedItem();
        if (editorial!=null && listViewEditSelected.getItems().isEmpty()) {
            listViewEditAvailable.getItems().remove(editorial);
            listViewEditSelected.getItems().add(editorial);
            FXCollections.sort(listViewEditSelected.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void removeEditorial() {
        String editorial = listViewEditSelected.getSelectionModel().getSelectedItem();
        if (editorial!=null) {
            listViewEditSelected.getItems().remove(editorial);
            listViewEditAvailable.getItems().add(editorial);
            FXCollections.sort(listViewEditAvailable.getItems(), String::compareToIgnoreCase);
        }
    }

    @FXML
    public void createEditorial(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/victornieto/gestionbiblioteca/fxml/tabs/inventario/newEditorialForm.fxml"));
        Parent parent = fxmlLoader.load();

        Stage newStage = new Stage();
        newStage.setTitle("Agregar");
        newStage.setScene(new Scene(parent));

        newStage.initModality(Modality.WINDOW_MODAL);

        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        newStage.initOwner(stageCurrent);
        newStage.setResizable(false);

        newStage.showAndWait();

        // Si se creó nueva editorial actualizar lista de disponibles
        NewEditorialFormController formController = fxmlLoader.getController();
        EditorialModel newEditorial = formController.getEditorial();
        if (newEditorial != null) {
            listViewEditAvailable.getItems().add(newEditorial.toString());
            FXCollections.sort(listViewEditAvailable.getItems(), String::compareToIgnoreCase);
        }
    }

    private void loadCategorias() {
        listCat = categoriaService.getAll();
        for (CategoriaModel c: listCat) {
            listViewCatAvailable.getItems().add(c.getNombre());
        }
    }

    private void loadEditoriales() {
        listEdit = editorialService.getAll();
        for (EditorialModel e: listEdit) {
            listViewEditAvailable.getItems().add(e.toString());
        }
    }

    private void loadAutor() {
        listAutor = autorService.getAllModels();
        for (AutorModel a: listAutor) {
            listViewAutoresAvailable.getItems().add(a.getNombreCompleto());
        }
    }

}
