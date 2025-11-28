module org.victornieto.gestionbiblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires de.mkammerer.argon2.nolibs;
    requires java.logging;
    requires java.sql;
    requires mysql.connector.j;

    opens org.victornieto.gestionbiblioteca to javafx.fxml;
    exports org.victornieto.gestionbiblioteca;
    exports org.victornieto.gestionbiblioteca.controller;
    opens org.victornieto.gestionbiblioteca.controller to javafx.fxml;
    opens org.victornieto.gestionbiblioteca.controller.tabs to javafx.fxml;
    opens org.victornieto.gestionbiblioteca.dto to javafx.base;
    opens org.victornieto.gestionbiblioteca.controller.tabs.inventario to javafx.fxml;
}