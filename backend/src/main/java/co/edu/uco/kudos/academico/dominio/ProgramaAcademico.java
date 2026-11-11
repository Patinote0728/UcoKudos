package co.edu.uco.kudos.academico.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "programa_academico")
public class ProgramaAcademico {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facultad_id", nullable = false)
    private Facultad facultad;

    @Column(name = "nombre", nullable = false, length = 160)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected ProgramaAcademico() {
    }

    public ProgramaAcademico(UUID id, Facultad facultad, String nombre, boolean activo, Instant creadoEn) {
        this.id = id;
        this.facultad = facultad;
        this.nombre = nombre;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public Facultad getFacultad() { return facultad; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
}
