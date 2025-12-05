package org.victornieto.gestionbiblioteca.model;

import java.time.LocalDate;

public class PrestamoModel {

    private final Long id;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;
    private final Long fkCliente;
    private final Long fkUsuario;
    private final Long idEjemplar;
    private final Integer activo;

    private PrestamoModel(Long id, LocalDate fechaInicio, LocalDate fechaFin,
                          Long fkCliente, Long fkUsuario, Long idEjemplar, Integer activo) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fkCliente = fkCliente;
        this.fkUsuario = fkUsuario;
        this.idEjemplar = idEjemplar;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Long getFkCliente() {
        return fkCliente;
    }

    public Long getFkUsuario() {
        return fkUsuario;
    }

    public Long getIdEjemplar() {
        return idEjemplar;
    }

    public Integer getActivo() {
        return activo;
    }

    public static IdStep builder() {
        return new Builder();
    }

    public interface IdStep {
        FechaInicioStep id(Long id);
    }

    public interface FechaInicioStep {
        FechaFinStep fechaInicio(LocalDate fechaInicio);
    }

    public interface FechaFinStep {
        FkClienteStep fechaFin(LocalDate fechaFin);
    }

    public interface FkClienteStep {
        FkUsuarioStep fkCliente(Long fkCliente);
    }

    public interface FkUsuarioStep {
        IdEjemplarStep fkUsuario(Long fkUsuario);
    }

    public interface IdEjemplarStep {
        ActivoStep idEjemplar(Long idEjemplar);
    }

    public interface ActivoStep {
        BuildStep activo(Integer activo);
    }

    public interface BuildStep {
        PrestamoModel build();
    }

    private static class Builder implements
            IdStep, FechaInicioStep, FechaFinStep,
            FkClienteStep, FkUsuarioStep, IdEjemplarStep,
            ActivoStep, BuildStep {

        private Long id;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private Long fkCliente;
        private Long fkUsuario;
        private Long idEjemplar;
        private Integer activo;

        @Override
        public FechaInicioStep id(Long id) {
            this.id = id;
            return this;
        }

        @Override
        public FechaFinStep fechaInicio(LocalDate fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        @Override
        public FkClienteStep fechaFin(LocalDate fechaFin) {
            this.fechaFin = fechaFin;
            return this;
        }

        @Override
        public FkUsuarioStep fkCliente(Long fkCliente) {
            this.fkCliente = fkCliente;
            return this;
        }

        @Override
        public IdEjemplarStep fkUsuario(Long fkUsuario) {
            this.fkUsuario = fkUsuario;
            return this;
        }

        @Override
        public ActivoStep idEjemplar(Long idEjemplar) {
            this.idEjemplar = idEjemplar;
            return this;
        }

        @Override
        public BuildStep activo(Integer activo) {
            this.activo = activo;
            return this;
        }

        @Override
        public PrestamoModel build() {
            return new PrestamoModel(id, fechaInicio, fechaFin, fkCliente, fkUsuario, idEjemplar, activo);
        }
    }
}
