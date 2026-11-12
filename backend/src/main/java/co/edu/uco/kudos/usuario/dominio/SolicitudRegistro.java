package co.edu.uco.kudos.usuario.dominio;

import co.edu.uco.kudos.academico.dominio.Facultad;
import co.edu.uco.kudos.academico.dominio.ProgramaAcademico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "solicitud_registro")
public class SolicitudRegistro {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultad_id", updatable = false)
    private Facultad facultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id", updatable = false)
    private ProgramaAcademico programa;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_aprobador", nullable = false, length = 32, updatable = false)
    private RolUsuario rolAprobador;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 16)
    private EstadoAprobacion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobador_usuario_id")
    private Usuario aprobador;

    @Column(name = "decidido_en")
    private Instant decididoEn;

    @Column(name = "comentario", length = 1000)
    private String comentario;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected SolicitudRegistro() {
    }

    public SolicitudRegistro(UUID id, Usuario usuario, Facultad facultad, ProgramaAcademico programa,
                            RolUsuario rolAprobador, Instant creadoEn) {
        this.id = id;
        this.usuario = usuario;
        this.facultad = facultad;
        this.programa = programa;
        this.rolAprobador = rolAprobador;
        this.estado = EstadoAprobacion.PENDIENTE;
        this.creadoEn = creadoEn;
    }

    public void decidir(Usuario aprobador, EstadoAprobacion decision, String comentario, Instant instante) {
        this.aprobador = aprobador;
        this.estado = decision;
        this.comentario = comentario;
        this.decididoEn = instante;
    }

    public UUID getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public Facultad getFacultad() { return facultad; }
    public ProgramaAcademico getPrograma() { return programa; }
    public RolUsuario getRolAprobador() { return rolAprobador; }
    public EstadoAprobacion getEstado() { return estado; }
    public Instant getCreadoEn() { return creadoEn; }
    public String getComentario() { return comentario; }
}
