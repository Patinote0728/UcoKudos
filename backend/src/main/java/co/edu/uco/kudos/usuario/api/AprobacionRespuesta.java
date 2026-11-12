package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.dominio.SolicitudRegistro;
import java.time.Instant;
import java.util.UUID;

public record AprobacionRespuesta(UUID id, UUID usuarioId, String nombreCompleto, String correoInstitucional,
        String rol, UUID facultadId, String facultad, UUID programaId, String programa, Instant creadaEn) {
    public static AprobacionRespuesta desde(SolicitudRegistro s) {
        var u=s.getUsuario(); var f=s.getFacultad(); var p=s.getPrograma();
        return new AprobacionRespuesta(s.getId(),u.getId(),u.getNombreCompleto(),u.getCorreoInstitucional(),u.getRol().name(),
                f==null?null:f.getId(),f==null?null:f.getNombre(),p==null?null:p.getId(),p==null?null:p.getNombre(),s.getCreadoEn());
    }
}
