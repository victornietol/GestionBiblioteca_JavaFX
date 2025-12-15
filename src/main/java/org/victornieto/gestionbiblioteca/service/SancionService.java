package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.SancionListDTO;
import org.victornieto.gestionbiblioteca.repository.SancionRepository;
import org.victornieto.gestionbiblioteca.repository.SancionRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class SancionService {

    private final SancionRepository sancionRepository;

    public SancionService() {
        this.sancionRepository = new SancionRepositoryImpl();
    }

    public List<SancionListDTO> getById(Long id) {
        try {
            return sancionRepository.getByIdCliente(id);
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
