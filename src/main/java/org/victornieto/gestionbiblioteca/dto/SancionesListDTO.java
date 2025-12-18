package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public class SancionesListDTO {

    private final Long id;
    private final String sancion;
    private final String descripcion;
    private final LocalDate fecha;
    private final String cliente;
    private final Long idPrestamo;
    private final String libro;
    private final Long idEjemplar;

    private SancionesListDTO(Long id,
                             String sancion,
                             String descripcion,
                             LocalDate fecha,
                             String cliente,
                             Long idPrestamo,
                             String libro,
                             Long idEjemplar) {
        this.id = id;
        this.sancion = sancion;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.cliente = cliente;
        this.idPrestamo = idPrestamo;
        this.libro = libro;
        this.idEjemplar = idEjemplar;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getSancion() {
        return sancion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public String getLibro() {
        return libro;
    }

    public Long getIdEjemplar() {
        return idEjemplar;
    }

    // ===== Builder =====

    public static IdStep builder() {
        return new Builder();
    }

    public interface IdStep {
        SancionStep id(Long id);
    }

    public interface SancionStep {
        DescripcionStep sancion(String sancion);
    }

    public interface DescripcionStep {
        FechaStep descripcion(String descripcion);
    }

    public interface FechaStep {
        ClienteStep fecha(LocalDate fecha);
    }

    public interface ClienteStep {
        IdPrestamoStep cliente(String cliente);
    }

    public interface IdPrestamoStep {
        LibroStep idPrestamo(Long idPrestamo);
    }

    public interface LibroStep {
        IdEjemplarStep libro(String libro);
    }

    public interface IdEjemplarStep {
        BuildStep idEjemplar(Long idEjemplar);
    }

    public interface BuildStep {
        SancionesListDTO build();
    }

    private static class Builder implements
            IdStep,
            SancionStep,
            DescripcionStep,
            FechaStep,
            ClienteStep,
            IdPrestamoStep,
            LibroStep,
            IdEjemplarStep,
            BuildStep {

        private Long id;
        private String sancion;
        private String descripcion;
        private LocalDate fecha;
        private String cliente;
        private Long idPrestamo;
        private String libro;
        private Long idEjemplar;

        @Override
        public SancionStep id(Long id) {
            this.id = id;
            return this;
        }

        @Override
        public DescripcionStep sancion(String sancion) {
            this.sancion = sancion;
            return this;
        }

        @Override
        public FechaStep descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        @Override
        public ClienteStep fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        @Override
        public IdPrestamoStep cliente(String cliente) {
            this.cliente = cliente;
            return this;
        }

        @Override
        public LibroStep idPrestamo(Long idPrestamo) {
            this.idPrestamo = idPrestamo;
            return this;
        }

        @Override
        public IdEjemplarStep libro(String libro) {
            this.libro = libro;
            return this;
        }

        @Override
        public BuildStep idEjemplar(Long idEjemplar) {
            this.idEjemplar = idEjemplar;
            return this;
        }

        @Override
        public SancionesListDTO build() {
            return new SancionesListDTO(
                    id,
                    sancion,
                    descripcion,
                    fecha,
                    cliente,
                    idPrestamo,
                    libro,
                    idEjemplar
            );
        }
    }
}