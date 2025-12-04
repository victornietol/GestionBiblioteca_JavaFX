package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.repository.PrestamoRepository;
import org.victornieto.gestionbiblioteca.repository.PrestamoRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrestamoService {

    private  final PrestamoRepository prestamoRepository;

    public PrestamoService() {
        this.prestamoRepository = new PrestamoRepositoryImpl();
    }

    public List<PrestamoListDTO> getPrestamosList(String columnToSearch, String coincidenceToSearch, String orderByColumn, boolean orderDes) throws RuntimeException {
        /**
         * @return una lista con los resultados de la búsqueda en la base de datos, en caso de no encontrar resultados se regresa una lista vacía.
         */
        List<PrestamoListDTO> prestamos = new ArrayList<>();

        if (coincidenceToSearch==null || coincidenceToSearch.isEmpty()) {
            columnToSearch = "Todos";
        }

        try {
            prestamos = prestamoRepository.getPrestamosList(columnToSearch, coincidenceToSearch, orderByColumn, orderDes);

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return prestamos;
    }
}
