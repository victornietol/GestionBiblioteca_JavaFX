package org.victornieto.gestionbiblioteca.model;

import java.time.LocalDate;

public class ClienteModel {

    private final Long id;
    private final String username;
    private final String passw;
    private final String nombre;
    private final String apellidoP;
    private final String apellidoM;
    private final String correo;
    private final Integer activo;
    private final LocalDate fechaCreacion;

    private ClienteModel(Long id, String username, String passw,
                         String nombre, String apellidoP, String apellidoM,
                         String correo, Integer activo, LocalDate fechaCreacion) {
        this.id = id;
        this.username = username;
        this.passw = passw;
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.correo = correo;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassw() {
        return passw;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidoP() {
        return apellidoP;
    }

    public String getApellidoM() {
        return apellidoM;
    }

    public String getCorreo() {
        return correo;
    }

    public Integer getActivo() {
        return activo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    // ===== BUILDER =====

    public static IdStep builder() {
        return new Builder();
    }

    public interface IdStep {
        UsernameStep id(Long id);
    }

    public interface UsernameStep {
        PasswStep username(String username);
    }

    public interface PasswStep {
        NombreStep passw(String passw);
    }

    public interface NombreStep {
        ApellidoPStep nombre(String nombre);
    }

    public interface ApellidoPStep {
        ApellidoMStep apellidoP(String apellidoP);
    }

    public interface ApellidoMStep {
        CorreoStep apellidoM(String apellidoM);
    }

    public interface CorreoStep {
        ActivoStep correo(String correo);
    }

    public interface ActivoStep {
        FechaStep activo(Integer activo);
    }

    public interface FechaStep{
        BuildStep fechaCreacion(LocalDate fechaCreacion);
    }

    public interface BuildStep {
        ClienteModel build();
    }

    private static class Builder implements
            IdStep, UsernameStep, PasswStep, NombreStep,
            ApellidoPStep, ApellidoMStep, CorreoStep,
            ActivoStep, FechaStep, BuildStep {

        private Long id;
        private String username;
        private String passw;
        private String nombre;
        private String apellidoP;
        private String apellidoM;
        private String correo;
        private Integer activo;
        private LocalDate fechaCreacion;

        @Override
        public UsernameStep id(Long id) {
            this.id = id;
            return this;
        }

        @Override
        public PasswStep username(String username) {
            this.username = username;
            return this;
        }

        @Override
        public NombreStep passw(String passw) {
            this.passw = passw;
            return this;
        }

        @Override
        public ApellidoPStep nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        @Override
        public ApellidoMStep apellidoP(String apellidoP) {
            this.apellidoP = apellidoP;
            return this;
        }

        @Override
        public CorreoStep apellidoM(String apellidoM) {
            this.apellidoM = apellidoM;
            return this;
        }

        @Override
        public ActivoStep correo(String correo) {
            this.correo = correo;
            return this;
        }

        @Override
        public FechaStep activo(Integer activo) {
            this.activo = activo;
            return this;
        }

        @Override
        public BuildStep fechaCreacion(LocalDate fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        @Override
        public ClienteModel build() {
            return new ClienteModel(id, username, passw, nombre, apellidoP, apellidoM, correo, activo, fechaCreacion);
        }
    }

    @Override
    public String toString() {
        return nombre.substring(0,1).toUpperCase() + nombre.substring(1) + " " +
                apellidoP.substring(0,1).toUpperCase() + apellidoP.substring(1) + " " +
                (apellidoM!=null ?
                        (apellidoM.substring(0,1).toUpperCase() + apellidoM.substring(1)) :
                        ""
                );
    }
}