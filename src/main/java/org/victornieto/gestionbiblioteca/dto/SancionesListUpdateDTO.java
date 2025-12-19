package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record SancionesListUpdateDTO(
        Long id,
        Long id_tipo,
        LocalDate fechaEntrega
) {}
