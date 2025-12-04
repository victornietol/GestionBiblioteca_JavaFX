package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record PrestamoDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Long fkCliente,
        Long fkUsuario,
        Long idEjemplar
) {
}
