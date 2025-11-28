package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.CategoriaFormDTO;
import org.victornieto.gestionbiblioteca.model.CategoriaModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    List<CategoriaModel> getAll() throws SQLException;
    Optional<CategoriaModel> getById(Integer id) throws SQLException;
    Optional<CategoriaModel> save(CategoriaFormDTO categoria) throws SQLException;
    Boolean delete(Long id) throws SQLException;
}
