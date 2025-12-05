package org.victornieto.gestionbiblioteca.service;

import org.victornieto.gestionbiblioteca.dto.PrestamoDTO;
import org.victornieto.gestionbiblioteca.dto.PrestamoListDTO;
import org.victornieto.gestionbiblioteca.model.PrestamoModel;
import org.victornieto.gestionbiblioteca.repository.PrestamoRepository;
import org.victornieto.gestionbiblioteca.repository.PrestamoRepositoryImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroService libroService;

    public PrestamoService() {
        this.prestamoRepository = new PrestamoRepositoryImpl();
        this.libroService = new LibroService();
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

    public Optional<PrestamoModel> createPrestamo(PrestamoDTO prestamoDTO) {
        /**
         * Crea un nuevo préstamo y desactiva el libro correspondiente de la tabla ejemplares
         */
        try {
            boolean removed = libroService.removeEjemplarLibro(prestamoDTO.idEjemplar());
            if(removed) {
                // Se removio de ejemplares disponibles
                Optional<PrestamoModel> optPrestamo = prestamoRepository.newPrestamo(prestamoDTO);
                if (optPrestamo.isEmpty()) {
                    throw new SQLException("Error al generar el préstamo");
                }
                return optPrestamo;
            }

        } catch (SQLException e) {
            libroService.reactiveEjemplar(prestamoDTO.idEjemplar()); // reactivar el libro que se desactivó
            throw new RuntimeException(e.getMessage());
        }
        return Optional.empty();
    }

    public Boolean returnPrestamo(Long idPrestamo, Long idEjemplar) {
        /**
         * Desactiva el préstamo indicado y reactiva el ejemplar indicado.
         */

        try {
            boolean returned = prestamoRepository.returnPrestamo(idPrestamo);

            if(returned) {
                // Reactivar el ejemplar
                return libroService.reactiveEjemplar(idEjemplar);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return false;
    }
}
