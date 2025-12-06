package org.victornieto.gestionbiblioteca.dto;

public record ClienteFormControllerDTO(
        String username,
        String password1,
        String password2,
        String nombre,
        String apellidoP,
        String apellidoM,
        String correo
) {
}
