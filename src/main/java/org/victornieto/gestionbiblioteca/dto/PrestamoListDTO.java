package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record PrestamoListDTO(
        Long idPrestamo,
        LocalDate fechaInicio,
        LocalDate fechaEntrega,
        Long idEjemplar,
        String titulo,
        String autor,
        String cliente,
        String usuario


) {
    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public Long getIdEjemplar() {
        return idEjemplar;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCliente() {
        return cliente;
    }

    public String getUsuario() {
        return usuario;
    }
}
