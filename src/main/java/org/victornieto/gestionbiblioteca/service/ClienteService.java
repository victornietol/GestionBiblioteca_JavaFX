package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.repository.ClienteRepository;
import org.victornieto.gestionbiblioteca.repository.ClienteRepositoryImpl;

import java.sql.SQLException;
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
}
