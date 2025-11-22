package org.victornieto.gestionbiblioteca.model;

public class UsuarioModel {

    private Long id;
    private String username;
    private String password;
    private String nombre;
    private String apellido_p;
    private String apellido_m;
    private String correo;
    private String telefono;
    private Integer activo; // 1 = activo, 0 = no
    private Integer fk_tipo_usuario;

    private UsuarioModel(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.password = builder.password;
        this.nombre = builder.nombre;
        this.apellido_p = builder.apellido_p;
        this.apellido_m = builder.apellido_m;
        this.correo = builder.correo;
        this.telefono = builder.telefono;
        this.activo = builder.activo;
        this.fk_tipo_usuario = builder.fk_tipo_usuario;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String password;
        private String nombre;
        private String apellido_p;
        private String apellido_m;
        private String correo;
        private String telefono;
        private Integer activo;
        private Integer fk_tipo_usuario;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder setApellidoP(String apellido_p) {
            this.apellido_p = apellido_p;
            return this;
        }

        public Builder setApellidoM(String apellido_m) {
            this.apellido_m = apellido_m;
            return this;
        }

        public Builder setCorreo(String correo) {
            this.correo = correo;
            return this;
        }

        public Builder setTelefono(String telefono) {
            this.telefono = telefono;
            return this;
        }

        public Builder setActivo(Integer activo) {
            this.activo = activo;
            return this;
        }

        public Builder setFK_TipoUsuario(Integer tipoUsuario) {
            this.fk_tipo_usuario = tipoUsuario;
            return this;
        }

        public UsuarioModel build() {
            return new UsuarioModel(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public Integer getActivo() {
        return activo;
    }

    public Integer getFk_tipo_usuario() {
        return fk_tipo_usuario;
    }

    @Override
    public String toString() {
        return "UsuarioModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido_p='" + apellido_p + '\'' +
                ", apellido_m='" + apellido_m + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", activo=" + activo + '\'' +
                ", activo=" + fk_tipo_usuario +
                '}';
    }
}
