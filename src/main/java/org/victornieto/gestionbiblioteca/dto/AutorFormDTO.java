package org.victornieto.gestionbiblioteca.dto;

import org.victornieto.gestionbiblioteca.model.AutorModel;

public record AutorFormDTO(
        String nombre,
        String apellidoP,
        String apellidoM
) {

    @Override
    public String toString() {
        return nombre + " " + apellidoP + " " + apellidoM;
    }
}
