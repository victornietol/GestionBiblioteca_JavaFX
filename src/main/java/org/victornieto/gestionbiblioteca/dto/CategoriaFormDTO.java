package org.victornieto.gestionbiblioteca.dto;

public record CategoriaFormDTO(String nombre) {

    @Override
    public String toString() {
        return nombre;
    }
}
