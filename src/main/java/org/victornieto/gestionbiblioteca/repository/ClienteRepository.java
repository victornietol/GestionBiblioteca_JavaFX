package org.victornieto.gestionbiblioteca.repository;

import org.victornieto.gestionbiblioteca.dto.ClienteFormDTO;
import org.victornieto.gestionbiblioteca.dto.ClienteListDTO;
import org.victornieto.gestionbiblioteca.model.ClienteModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Optional<String> getNombreCompletoById(Long id) throws SQLException;
    List<ClienteListDTO> getListDTOAll(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) throws SQLException;
    Optional<ClienteModel> save(ClienteFormDTO clienteFormDTO) throws SQLException;
    Boolean delete(Long id) throws SQLException;
}
