package org.victornieto.gestionbiblioteca.controller.tabs.inventario;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;
import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.dto.LibroDTO;
import org.victornieto.gestionbiblioteca.dto.LibroFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;
import org.victornieto.gestionbiblioteca.model.EditorialModel;
import org.victornieto.gestionbiblioteca.model.LibroModel;
import org.victornieto.gestionbiblioteca.repository.*;
import org.victornieto.gestionbiblioteca.service.AutorService;
import org.victornieto.gestionbiblioteca.service.CategoriaService;
import org.victornieto.gestionbiblioteca.service.EditorialService;
import org.victornieto.gestionbiblioteca.service.LibroService;
import org.victornieto.gestionbiblioteca.utility.AlertWindow;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class WindowAddController {
    @FXML public TextField labelTitulo;
    @FXML public TextField labelEdicion;
    @FXML public TextField labelAnio;
    @FXML public TextField labelPaginas;
    @FXML public TextField numberUnits;
    @FXML public Slider sliderUnits;

    @FXML public ListView<String> listViewAutoresSelected;
    @FXML public ListView<String> listViewAutoresAvailable;
    @FXML public ListView<String> listViewCatSelected;
    @FXML public ListView<String> listViewCatAvailable;
    @FXML public ListView<String> listViewEditSelected;
    @FXML public ListView<String> listViewEditAvailable;

    private final AutorService autorService = new AutorService();
    private final CategoriaService categoriaService = new CategoriaService();
    private final EditorialService editorialService = new EditorialService();
    private final LibroService libroService = new LibroService();

    private List<CategoriaModel> listCat;
    private List<EditorialModel> listEdit;
    private List<AutorModel> listAutor;

    private final int MAX_UNITS = 500;
    private final int MIN_UNITS = 1;

    @FXML
    public void initialize() {
        loadCategorias();
        loadEditoriales();
        loadAutor();
        configSliderAndTextUnits();
        configLabelAnioAndPag();
    }

    @FXML
    public void onSave(ActionEvent event) {
        String titulo = getValueTittulo();
        String edicion = getValueEdicion();
        int units = getUnitsValue();
        int anio = getValueAnio();
        List<String> autores = getValuesAutores();
        List<String> categorias = getValuesCategorias();
        String editorial = getValueEditorial();
        int paginas = getValuePaginas();

        AlertWindow alertWindow = new AlertWindow();
        boolean confirm = alertWindow.generateConfirmation("Confirmar", "¿Confirmar guardado?", null);

        if (confirm) {

            LibroModel newLibro;

            try {
                LibroFormDTO libroFormDTO = new LibroFormDTO(titulo, anio, paginas, edicion);
                Optional<LibroModel> newLibroOp =  libroService.createNewTitle(libroFormDTO, categorias, autores, editorial, units);
                if (newLibroOp.isEmpty()) {
                    alertWindow.generateError("Error", "Error al registrar el libro nuevo y sus ejemplares", null);
                } else {
                    newLibro = newLibroOp.get();
                    alertWindow.generateInformation(
                            "Información",
                            "Operación exitosa, se agrergaron:",
                            "Titulo del libro: " + newLibro.getTitulo().substring(0,1).toUpperCase() + newLibro.getTitulo().substring(1) + "\nUnidades: " + units);
                    closeWindow(event);
                }
            } catch (RuntimeException e) {
                alertWindow.generateError("Error", e.getMessage(), null);
            }
        }
    }

    @FXML
    public void onCancel(ActionEvent event) {
        closeWindow(event);
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

    private void closeWindow(ActionEvent event) {
        Stage stageCurrent = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stageCurrent.close();
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

    private int getUnitsValue() {
        /**
         * Obtener el valor de las unidades selecionadas
         */
        String text = numberUnits.getText();
        if (text ==null || text.isEmpty())
            return MIN_UNITS;

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return MIN_UNITS;
        }
    }

    private void configSliderAndTextUnits() {
        sliderUnits.setMax(MAX_UNITS);
        sliderUnits.setMin(MIN_UNITS);
        sliderUnits.setValue(MIN_UNITS);
        sliderUnits.setBlockIncrement(1);
        sliderUnits.setShowTickLabels(true);
        sliderUnits.setShowTickLabels(true);
        sliderUnits.setMajorTickUnit(50);
        sliderUnits.setMinorTickCount(4);
        sliderUnits.setSnapToTicks(true);

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change; // permitir vacio para permitir borrado y escritura
            }

            if (!newText.matches("\\d*")) {
                return null;
            }

            try {
                int value = Integer.parseInt(newText);
                if(value<MIN_UNITS || value>MAX_UNITS) {
                    return null;
                }
                return change;
            } catch (NumberFormatException e) {
                return null;
            }
        };

        IntegerStringConverter converter = new IntegerStringConverter();
        TextFormatter<Integer> textFormatter = new TextFormatter<>(converter, MIN_UNITS, filter);
        numberUnits.setTextFormatter(textFormatter);

        numberUnits.setText(String.valueOf((int) sliderUnits.getValue()));

        sliderUnits.valueProperty().addListener((obs, oldValue, newValue) -> {
            int intVal = newValue.intValue();

            String currentText = numberUnits.getText();

            try {
                int current = currentText.isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(currentText);
                if(current != intVal) {
                    numberUnits.setText(String.valueOf(intVal));
                }
            } catch (NumberFormatException e) {
                numberUnits.setText(String.valueOf(intVal));
            }
        });

        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue !=null) {
                int v = newValue;
                if((int) sliderUnits.getValue() != v) {
                    sliderUnits.setValue(v);
                }
            }
        });
    }

    private void configLabelAnioAndPag() {
        labelAnio.setTextFormatter(new TextFormatter<>(change -> {
            String newValue = change.getControlNewText();
            if (newValue.matches("\\d{0,4}")) {
                return change;
            }
            return null;
        }));

        labelPaginas.setTextFormatter(new TextFormatter<>(change -> {
            String newValue = change.getControlNewText();
            if (newValue.matches("\\d+")) {
                return change;
            }
            return null;
        }));
    }

    private String getValueEditorial() {
        if (listViewEditSelected.getItems().isEmpty()) {
            return "";
        }
        return listViewEditSelected.getItems().getFirst();
    }

    private List<String> getValuesCategorias() {
        List<String> list = new ArrayList<>();
        if (listViewCatSelected.getItems().isEmpty()) {
            return list;
        }
        return listViewCatSelected.getItems();
    }

    private List<String> getValuesAutores() {
        List<String> list = new ArrayList<>();
        if (listViewAutoresSelected.getItems().isEmpty()) {
            return list;
        }
        return listViewAutoresSelected.getItems();
    }

    private Integer getValueAnio() {
        String text = labelAnio.getText().replaceAll("\\s+", "");
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
    }

    private String getValueTittulo() {
        return labelTitulo.getText().trim().replaceAll("\\s+", " ");
    }

    private String getValueEdicion() {
        return labelEdicion.getText().trim().replaceAll("\\s+", " ");
    }

    private Integer getValuePaginas() {
        String text = labelPaginas.getText().replaceAll("\\s+", "");
        if (text.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(text);
    }

}
