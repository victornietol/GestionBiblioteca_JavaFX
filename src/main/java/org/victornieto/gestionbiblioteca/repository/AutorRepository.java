package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.AutorFormDTO;
import org.victornieto.gestionbiblioteca.model.AutorModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AutorRepository {
    List<AutorModel> getAll() throws SQLException;
    Optional<AutorModel> getById(Integer id) throws SQLException;
    Optional<AutorModel> save(AutorFormDTO autor) throws SQLException;
    Boolean delete(Long id) throws SQLException;
}
