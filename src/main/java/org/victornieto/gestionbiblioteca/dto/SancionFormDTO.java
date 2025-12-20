package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record SancionFormDTO(
        Long idTipo,
        Long idCliente,
        LocalDate fecha,
        Long fkPrestamo
) {
}
