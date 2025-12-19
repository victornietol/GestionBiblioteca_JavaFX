package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record SancionToUpdateDTO(
        Long id,
        Long id_tipo,
        LocalDate fecha
) {
}
