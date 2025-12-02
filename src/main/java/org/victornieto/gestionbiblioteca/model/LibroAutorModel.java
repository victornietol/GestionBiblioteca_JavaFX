package org.victornieto.gestionbiblioteca.model;

public class LibroAutorModel {
    private Long id_libro;
    private Long id_autor;
    private Integer activo;

    public LibroAutorModel(Long id_libro, Long id_autor, Integer activo) {
        this.id_libro = id_libro;
        this.id_autor = id_autor;
        this.activo = activo;
    }

    public Long getId_libro() {
        return id_libro;
    }

    public Long getId_autor() {
        return id_autor;
    }

    public Integer getActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return "LibroAutorModel{" +
                "id_libro=" + id_libro +
                ", id_autor=" + id_autor +
                ", activo=" + activo +
                '}';
    }
}
