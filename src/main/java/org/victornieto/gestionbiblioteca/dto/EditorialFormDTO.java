package org.victornieto.gestionbiblioteca.dto;

public record EditorialFormDTO(
        String nombre
) {
    @Override
    public String toString() {
        return nombre;
    }
}
