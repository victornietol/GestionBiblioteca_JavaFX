package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.EditorialFormDTO;
import org.victornieto.gestionbiblioteca.model.EditorialModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface EditorialRepository {
    List<EditorialModel> getAll() throws SQLException;
    List<EditorialFormDTO> getNombres() throws SQLException;
    Optional<EditorialModel> getById(Integer id) throws SQLException;
    Optional<EditorialModel> getByNombre(String nombre) throws SQLException;
    Optional<EditorialModel> save(EditorialFormDTO editorial) throws SQLException;
    Boolean delete(Long id) throws SQLException;
}
