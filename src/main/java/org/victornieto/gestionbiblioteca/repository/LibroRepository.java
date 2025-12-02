package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.LibroDTO;
import org.victornieto.gestionbiblioteca.dto.LibroInventarioDTO;
import org.victornieto.gestionbiblioteca.model.LibroModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface LibroRepository {

    List<LibroInventarioDTO> getBooksInfoComplete(boolean showTitles, String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) throws SQLException;
    Optional<LibroModel> getByTituloAnioPaginasEdicion(String titulo, Integer anio, Integer paginas, String edicion) throws SQLException;
    Optional<LibroModel> save(LibroDTO book) throws SQLException;
    Boolean addUnits(Long id_libro, Integer numberUnits) throws SQLException;
    Boolean delete(Long id) throws SQLException;
    Boolean removeEjemplarLibro(Long id_ejemplar) throws SQLException;
    Boolean relateLibroAutor(Long id_libro, Long id_autor) throws SQLException;
    Boolean relateLibroCategoria(Long id_libro, Long id_categoria) throws SQLException;
    Boolean deleteRelationLibroAutor(Long id_libro, Long id_autor) throws SQLException;
    Boolean deleteRelationLibroCategoria(Long id_libro, Long id_categoria) throws SQLException;
}
