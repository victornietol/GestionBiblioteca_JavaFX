package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record PrestamoWithoutSancionDTO(
        Long idPrestamo,
        Long idCliente,
        LocalDate fechaEntrega
) {
}
