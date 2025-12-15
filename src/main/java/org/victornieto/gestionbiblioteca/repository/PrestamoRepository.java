package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.PrestamoDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.model.PrestamoModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PrestamoRepository {

    List<PrestamoListDTO> getPrestamosList(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws SQLException;
    Optional<PrestamoModel> getById(Long id) throws SQLException;
    Optional<PrestamoModel> getPrestamosActiveByAllColumns(PrestamoDTO prestamoDTO) throws SQLException;
    Optional<PrestamoModel> newPrestamo(PrestamoDTO prestamo) throws SQLException;
    Integer getNumberActivePrestamos() throws SQLException;
    Integer getNumberReturnedPrestamosToday() throws SQLException;
    Boolean returnPrestamo(Long idPrestamo) throws SQLException;
}
