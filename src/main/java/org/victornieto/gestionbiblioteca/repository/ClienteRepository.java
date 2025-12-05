package org.victornieto.gestionbiblioteca.repository;

import java.sql.SQLException;
import java.util.Optional;

public interface ClienteRepository {
    Optional<String> getNombreCompletoById(Long id) throws SQLException;

}
