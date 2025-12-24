package org.victornieto.gestionbiblioteca.utility;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

public class FileGenerator {

    public File createFile(String defaultName, Node root) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar reporte");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf")
        );
        fileChooser.setInitialFileName(defaultName + "_" + LocalDate.now() + ".pdf");

        return fileChooser.showSaveDialog( // si no se selecciona nada se regresa null, de lo contrario se regresa el objeto
                (Stage) root.getScene().getWindow()
        );
    }
}
