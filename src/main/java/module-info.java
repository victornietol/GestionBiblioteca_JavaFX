module org.victornieto.gestionbiblioteca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires de.mkammerer.argon2.nolibs;

    opens org.victornieto.gestionbiblioteca to javafx.fxml;
    exports org.victornieto.gestionbiblioteca;
    exports org.victornieto.gestionbiblioteca.controller;
    opens org.victornieto.gestionbiblioteca.controller to javafx.fxml;
}