package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.*;
import org.victornieto.gestionbiblioteca.repository.SancionRepository;
import org.victornieto.gestionbiblioteca.repository.SancionRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class SancionService {

    private final SancionRepository sancionRepository;

    public SancionService() {
        this.sancionRepository = new SancionRepositoryImpl();
    }

    public List<SancionClienteListDTO> getById(Long id) {
        try {
            return sancionRepository.getByIdCliente(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<SancionesListDTO> getAllList(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) {
        if (coincidenceToSearch==null || coincidenceToSearch.isEmpty()) {
            columnToSearch = "Todos";
        }

        try {
            return sancionRepository.getAllList(columnToSearch, coincidenceToSearch, orderByColumn, orderDes);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<SancionesListUpdateDTO> getListToUpdate() {
        try {
            return sancionRepository.getSancionesToUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean create(SancionFormDTO sancion) {
        try {
            return sancionRepository.create(sancion);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean updateSancion(SancionToUpdateDTO sancion) {
        try {
            return sancionRepository.updateSancion(sancion);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Integer getActiveAmount() {
        try {
            return sancionRepository.getActiveAmount();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Boolean delete(Long id) {
        try {
            return sancionRepository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
