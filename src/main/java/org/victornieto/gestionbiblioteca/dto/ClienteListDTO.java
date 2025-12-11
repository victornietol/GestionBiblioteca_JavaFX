package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public record ClienteListDTO(
        Long id,
        String username,
        String nombre,
        String correo,
        Integer prestamos,
        Integer sanciones,
        LocalDate fechaCreacion
) {

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public Integer getPrestamos() {
        return prestamos;
    }

    public Integer getSanciones() {
        return sanciones;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }
}
