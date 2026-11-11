package co.edu.uco.kudos.reconocimiento.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "categoria_kudo")
public class CategoriaKudo {

    @Id
    private UUID id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected CategoriaKudo() {
    }

    public CategoriaKudo(UUID id, String nombre, String descripcion, boolean activo, Instant creadoEn) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isActivo() { return activo; }
}
