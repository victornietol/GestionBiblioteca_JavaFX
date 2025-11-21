package org.victornieto.gestionbiblioteca.dto;

public class UsuarioFormDTO {

    private String username;
    private String password1;
    private String password2;
    private String nombre;
    private String apellido_p;
    private String apellido_m;
    private String correo;
    private String telefono;

    private UsuarioFormDTO(Builder builder) {
        this.username = builder.username;
        this.password1 = builder.password1;
        this.password2 = builder.password2;
        this.nombre = builder.nombre;
        this.apellido_p = builder.apellido_p;
        this.apellido_m = builder.apellido_m;
        this.correo = builder.correo;
        this.telefono = builder.telefono;
    }

    public static class Builder {
        private String username;
        private String password1;
        private String password2;
        private String nombre;
        private String apellido_p;
        private String apellido_m;
        private String correo;
        private String telefono;


        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword1(String password) {
            this.password1 = password;
            return this;
        }

        public Builder setPassword2(String password) {
            this.password2 = password;
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

        public UsuarioFormDTO build() {
            return new UsuarioFormDTO(this);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword1() {
        return password1;
    }

    public String getPassword2() {
        return password2;
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

    @Override
    public String toString() {
        return "UsuarioDTO{" +
                ", username='" + username + '\'' +
                ", password1='" + password1 + '\'' +
                ", password2='" + password2 + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido_p='" + apellido_p + '\'' +
                ", apellido_m='" + apellido_m + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono +
                '}';
    }
}
