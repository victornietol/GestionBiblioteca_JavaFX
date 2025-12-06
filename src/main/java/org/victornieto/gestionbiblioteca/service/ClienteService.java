package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.ClienteListDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.repository.ClienteRepository;
import org.victornieto.gestionbiblioteca.repository.ClienteRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepositoryImpl();
    }

    public Optional<String> getNombreCompletoById(Long id) {
        try {
            return clienteRepository.getNombreCompletoById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<ClienteListDTO> getListDTOAll(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDesc) {
        /**
         * @return una lista con los resultados de la búsqueda en la base de datos, en caso de no encontrar resultados se regresa una lista vacía.
         */
        List<ClienteListDTO> clientes = new ArrayList<>();

        if (coincidenceToSearch==null || coincidenceToSearch.isEmpty()) {
            columnToSearch = "Todos";
        }

        try {
            clientes = clienteRepository.getListDTOAll(columnToSearch, coincidenceToSearch, orderByColumn, orderDesc);

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return clientes;
    }
}
