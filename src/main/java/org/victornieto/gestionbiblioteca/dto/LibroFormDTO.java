package org.victornieto.gestionbiblioteca.dto;

public record LibroFormDTO(
        String titulo,
        Integer anio_publicacion,
        Integer paginas,
        String edicion
) {
}
