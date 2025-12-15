package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.SancionListDTO;

import java.sql.SQLException;
import java.util.List;

public interface SancionRepository {
    List<SancionListDTO> getByIdCliente(Long id) throws SQLException;
    Integer getActiveAmount() throws SQLException;
    Boolean delete(Long id) throws SQLException;
}
