package org.victornieto.gestionbiblioteca.model;

public class CategoriaLibroModel {
    private Long id_categoria;
    private Long id_libro;
    private Integer activo;

    public CategoriaLibroModel(Long id_categoria, Long id_libro, Integer activo) {
        this.id_categoria = id_categoria;
        this.id_libro = id_libro;
        this.activo = activo;
    }

    public Long getId_categoria() {
        return id_categoria;
    }

    public Long getId_libro() {
        return id_libro;
    }

    public Integer getActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return "CategoriaLibroModel{" +
                "id_categoria=" + id_categoria +
                ", id_libro=" + id_libro +
                ", activo=" + activo +
                '}';
    }
}
