package co.edu.uco.kudos.academico.dominio;

import co.edu.uco.kudos.usuario.dominio.Usuario;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "vinculacion_usuario_programa")
public class VinculacionUsuarioPrograma {

    @EmbeddedId
    private VinculacionUsuarioProgramaId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("programaAcademicoId")
    @JoinColumn(name = "programa_academico_id", nullable = false)
    private ProgramaAcademico programa;

    @Enumerated(EnumType.STRING)
    @jakarta.persistence.Column(name = "tipo_vinculacion", insertable = false, updatable = false, nullable = false)
    private TipoVinculacion tipo;

    @jakarta.persistence.Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected VinculacionUsuarioPrograma() {
    }

    public VinculacionUsuarioPrograma(Usuario usuario, ProgramaAcademico programa, TipoVinculacion tipo, Instant creadoEn) {
        this.id = new VinculacionUsuarioProgramaId(usuario.getId(), programa.getId(), tipo);
        this.usuario = usuario;
        this.programa = programa;
        this.tipo = tipo;
        this.creadoEn = creadoEn;
    }

    public VinculacionUsuarioProgramaId getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public ProgramaAcademico getPrograma() { return programa; }
    public TipoVinculacion getTipo() { return tipo; }
}
