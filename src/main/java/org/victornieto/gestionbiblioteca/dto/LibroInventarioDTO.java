package org.victornieto.gestionbiblioteca.dto;

import java.time.Year;

public class LibroInventarioDTO {

    private Integer id_libro;
    private String titulo;
    private String autor;
    private String categoria;
    private String editorial;
    private Year anio_publicacion;
    private Integer paginas;
    private String edicion;
    private Integer unidades;

    private LibroInventarioDTO(Builder builder) {
        this.id_libro = builder.id_libro;
        this.titulo = builder.titulo;
        this.autor = builder.autor;
        this.categoria = builder.categoria;
        this.editorial = builder.editorial;
        this.anio_publicacion = builder.anio_publicacion;
        this.paginas = builder.paginas;
        this.edicion = builder.edicion;
        this.unidades = builder.unidades;
    }

    public static class Builder {
        private Integer id_libro;
        private String titulo;
        private String autor;
        private String categoria;
        private String editorial;
        private Year anio_publicacion;
        private Integer paginas;
        private String edicion;
        private Integer unidades;

        public Builder setId_libro(Integer id_libro) {
            this.id_libro = id_libro;
            return this;
        }

        public Builder setTitulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder setAutor(String autor) {
            this.autor = autor;
            return this;
        }

        public Builder setCategoria(String categoria) {
            this.categoria = categoria;
            return this;
        }

        public Builder setEditorial(String editorial) {
            this.editorial = editorial;
            return this;
        }

        public Builder setAnio_publicacion(Year anio_publicacion) {
            this.anio_publicacion = anio_publicacion;
            return this;
        }

        public Builder setPaginas(Integer paginas) {
            this.paginas = paginas;
            return this;
        }

        public Builder setEdicion(String edicion) {
            this.edicion = edicion;
            return this;
        }

        public Builder setUnidades(Integer unidades) {
            this.unidades = unidades;
            return this;
        }

        public LibroInventarioDTO build() {
            return new LibroInventarioDTO(this);
        }
    }

    public Integer getId_libro() {
        return id_libro;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEditorial() {
        return editorial;
    }

    public Year getAnio_publicacion() {
        return anio_publicacion;
    }

    public Integer getPaginas() {
        return paginas;
    }

    public String getEdicion() {
        return edicion;
    }

    public Integer getUnidades() {
        return unidades;
    }

    @Override
    public String toString() {
        return "LibroInventarioDTO{" +
                "id_libro=" + id_libro +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", editorial='" + editorial + '\'' +
                ", anio_publicacion=" + anio_publicacion +
                ", paginas=" + paginas +
                ", edicion='" + edicion + '\'' +
                ", unidades=" + unidades +
                '}';
    }
}
