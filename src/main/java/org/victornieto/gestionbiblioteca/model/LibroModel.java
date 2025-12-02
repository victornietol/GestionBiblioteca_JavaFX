package org.victornieto.gestionbiblioteca.model;

import java.time.Year;

public class LibroModel {

    private Long id;
    private String titulo;
    private Year anio_publicacion;
    private Integer no_paginas;
    private String edicion;
    private Long id_editorial;
    private Integer activo;

    private LibroModel(Builder builder) {
        this.id = builder.id;
        this.titulo = builder.titulo;
        this.anio_publicacion = builder.anio_publicacion;
        this.no_paginas = builder.no_paginas;
        this.edicion = builder.edicion;
        this.id_editorial = builder.id_editorial;
        this.activo = builder.activo;
    }

    public static class Builder {
        private Long id;
        private String titulo;
        private Year anio_publicacion;
        private Integer no_paginas;
        private String edicion;
        private Long id_editorial;
        private Integer activo;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setTitulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder setAnio_publicacion(Year anio_publicacion) {
            this.anio_publicacion = anio_publicacion;
            return this;
        }

        public Builder setNo_paginas(Integer no_paginas) {
            this.no_paginas = no_paginas;
            return this;
        }

        public Builder setEdicion(String edicion) {
            this.edicion = edicion;
            return this;
        }

        public Builder setId_editorial(Long id_editorial) {
            this.id_editorial = id_editorial;
            return this;
        }

        public Builder setActivo(Integer activo) {
            this.activo = activo;
            return this;
        }

        public LibroModel build() {
            return new LibroModel(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Year getAnio_publicacion() {
        return anio_publicacion;
    }

    public Integer getNo_paginas() {
        return no_paginas;
    }

    public String getEdicion() {
        return edicion;
    }

    public Long getId_editorial() {
        return id_editorial;
    }

    public Integer getActivo() {
        return activo;
    }

    @Override
    public String toString() {
        return "LibroModel{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", anio_publicacion=" + anio_publicacion +
                ", no_paginas=" + no_paginas +
                ", edicion='" + edicion + '\'' +
                ", id_editorial=" + id_editorial +
                ", activo=" + activo +
                '}';
    }
}
