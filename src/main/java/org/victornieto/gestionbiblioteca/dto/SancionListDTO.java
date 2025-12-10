package org.victornieto.gestionbiblioteca.dto;

import java.time.LocalDate;

public class SancionListDTO {

    private Long id;
    private String sancion;
    private String descripcion;
    private LocalDate fecha;
    private Long idPrestamo;

    public SancionListDTO(Long id, String sancion, String descripcion, LocalDate fecha, Long idPrestamo) {
        this.id = id;
        this.sancion = sancion;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.idPrestamo = idPrestamo;
    }

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

    public Long getIdPrestamo() {
        return idPrestamo;
    }
}
