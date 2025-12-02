package org.victornieto.gestionbiblioteca.dto;

import org.victornieto.gestionbiblioteca.model.LibroModel;

import java.time.Year;

public record LibroDTO(
        String titulo,
        Integer anio_publicacion,
        Integer paginas,
        String edicion,
        Long id_editorial
) {}
