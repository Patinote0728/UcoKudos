package co.edu.uco.kudos.notificacion.dominio;

import co.edu.uco.kudos.usuario.dominio.Usuario;
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
@Table(name = "dispositivo_push")
public class DispositivoPush {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "firebase_installation_id", nullable = false, unique = true, length = 255, updatable = false)
    private String identificadorInstalacionFirebase;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected DispositivoPush() {
    }

    public DispositivoPush(UUID id, Usuario usuario, String identificadorInstalacionFirebase, Instant ahora) {
        this.id = id;
        this.usuario = usuario;
        this.identificadorInstalacionFirebase = identificadorInstalacionFirebase;
        this.activo = true;
        this.creadoEn = ahora;
        this.actualizadoEn = ahora;
    }

    public void renovar(Instant ahora) {
        this.activo = true;
        this.actualizadoEn = ahora;
    }

    public void desactivar(Instant ahora) {
        this.activo = false;
        this.actualizadoEn = ahora;
    }

    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getIdentificadorInstalacionFirebase() { return identificadorInstalacionFirebase; }
    public boolean isActivo() { return activo; }
}
