package org.victornieto.gestionbiblioteca.dto;

public record ClienteListDTO(
        Long id,
        String username,
        String nombre,
        String correo,
        Integer prestamos,
        Integer sanciones
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
}
