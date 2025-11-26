package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.model.LibroModel;

import java.sql.SQLException;
import java.util.List;

public interface LibroRepository {

    List<LibroInventarioDTO> getBooksInfoComplete(boolean showTitles, String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) throws SQLException;
    LibroModel save(LibroModel book);
    LibroModel delete(Integer id);
}
