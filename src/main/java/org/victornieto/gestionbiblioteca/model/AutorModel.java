package org.victornieto.gestionbiblioteca.model;

public class AutorModel {

    private Long id;
    private String nombre;
    private String apellido_p;
    private String apellido_m;
    private Integer activo;

    private AutorModel(Builder builder) {
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.apellido_p = builder.apellido_p;
        this.apellido_m = builder.apellido_m;
        this.activo = builder.activo;
    }

    public static class Builder {
        private Long id;
        private String nombre;
        private String apellido_p;
        private String apellido_m;
        private Integer activo;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder setApellido_p(String apellido_p) {
            this.apellido_p = apellido_p;
            return this;
        }

        public Builder setApellido_m(String apellido_m) {
            this.apellido_m = apellido_m;
            return this;
        }

        public Builder setActivo(Integer activo) {
            this.activo = activo;
            return this;
        }

        public AutorModel build() {
            return new AutorModel(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido_p() {
        return apellido_p;
    }

    public String getApellido_m() {
        return apellido_m;
    }

    public Integer getActivo() {
        return activo;
    }

    public String getNombreCompleto() {
        return (nombre.substring(0,1).toUpperCase() + nombre.substring(1)) + " " +
                (apellido_p.substring(0,1).toUpperCase() + apellido_p.substring(1)) + " " +
                (apellido_m.substring(0,1).toUpperCase() + apellido_m.substring(1));
    }

    @Override
    public String toString() {
        return "AutorModel{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido_p='" + apellido_p + '\'' +
                ", apellido_m='" + apellido_m + '\'' +
                ", activo=" + activo +
                '}';
    }
}
