package org.victornieto.gestionbiblioteca.dto;

public record ClienteFormDTO(
        String username,
        String password,
        String nombre,
        String apellidoP,
        String apellidoM,
        String correo
) {
}
