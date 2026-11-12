package co.edu.uco.kudos.auditoria.dominio;

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
@Table(name = "evento_auditoria")
public class EventoAuditoria {

    @Id
    private UUID id;

    @Column(name = "tipo_evento", nullable = false, length = 80, updatable = false)
    private String tipoEvento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_usuario_id", nullable = false, updatable = false)
    private Usuario actor;

    @Column(name = "recurso_tipo", nullable = false, length = 60, updatable = false)
    private String recursoTipo;

    @Column(name = "recurso_id", nullable = false, updatable = false)
    private UUID recursoId;

    @Column(name = "ocurrido_en", nullable = false, updatable = false)
    private Instant ocurridoEn;

    protected EventoAuditoria() {
    }

    public EventoAuditoria(UUID id, String tipoEvento, Usuario actor, String recursoTipo,
                           UUID recursoId, Instant ocurridoEn) {
        this.id = id;
        this.tipoEvento = tipoEvento;
        this.actor = actor;
        this.recursoTipo = recursoTipo;
        this.recursoId = recursoId;
        this.ocurridoEn = ocurridoEn;
    }
}
