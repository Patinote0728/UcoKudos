package co.edu.uco.kudos.academico.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "facultad")
public class Facultad {

    @Id
    private UUID id;

    @Column(name = "nombre", nullable = false, unique = true, length = 160)
    private String nombre;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected Facultad() {
    }

    public Facultad(UUID id, String nombre, boolean activo, Instant creadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public boolean isActivo() { return activo; }
}
