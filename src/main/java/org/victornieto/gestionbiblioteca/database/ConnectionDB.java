package org.victornieto.gestionbiblioteca.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionDB {

    Connection getConection() throws SQLException;
}
