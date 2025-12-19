package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.SancionClienteListDTO;
import org.victornieto.gestionbiblioteca.dto.SancionToUpdateDTO;
import org.victornieto.gestionbiblioteca.dto.SancionesListDTO;
import org.victornieto.gestionbiblioteca.dto.SancionesListUpdateDTO;

import java.sql.SQLException;
import java.util.List;

public interface SancionRepository {
    List<SancionClienteListDTO> getByIdCliente(Long id) throws SQLException;
    List<SancionesListDTO> getAllList(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws SQLException;
    Integer getActiveAmount() throws SQLException;
    List<SancionesListUpdateDTO> getSancionesToUpdate() throws SQLException;
    Boolean updateSancion(SancionToUpdateDTO sancion) throws SQLException;
    Boolean delete(Long id) throws SQLException;
}
