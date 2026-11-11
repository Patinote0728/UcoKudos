package co.edu.uco.kudos.academico.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VinculacionUsuarioProgramaId implements Serializable {

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "programa_academico_id")
    private UUID programaAcademicoId;

    @Column(name = "tipo_vinculacion", length = 16)
    private String tipoVinculacion;

    protected VinculacionUsuarioProgramaId() {
    }

    public VinculacionUsuarioProgramaId(UUID usuarioId, UUID programaAcademicoId, TipoVinculacion tipoVinculacion) {
        this.usuarioId = usuarioId;
        this.programaAcademicoId = programaAcademicoId;
        this.tipoVinculacion = tipoVinculacion.name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof VinculacionUsuarioProgramaId that)) return false;
        return Objects.equals(usuarioId, that.usuarioId)
                && Objects.equals(programaAcademicoId, that.programaAcademicoId)
                && Objects.equals(tipoVinculacion, that.tipoVinculacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, programaAcademicoId, tipoVinculacion);
    }
}
