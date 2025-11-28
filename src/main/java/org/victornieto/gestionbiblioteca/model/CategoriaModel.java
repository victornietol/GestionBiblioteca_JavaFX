package org.victornieto.gestionbiblioteca.model;

public class CategoriaModel {

    private Long id;
    private String nombre;
    private Integer activo;

    // Constructor privado para el Builder
    private CategoriaModel(Builder builder) {
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.activo = builder.activo;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getActivo() {
        return activo;
    }

    // Builder
    public static class Builder {
        private Long id;
        private String nombre;
        private Integer activo;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder setActivo(Integer activo) {
            this.activo = activo;
            return this;
        }

        public CategoriaModel build() {
            return new CategoriaModel(this);
        }
    }

    @Override
    public String toString() {
        return "CategoriaModel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", activo=" + activo +
                '}';
    }
}
