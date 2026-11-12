package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import java.util.UUID;

public record MiCuentaRespuesta(UUID id, String nombreCompleto, String correoInstitucional, RolUsuario rol) {
    public static MiCuentaRespuesta desde(Usuario usuario) {
        return new MiCuentaRespuesta(usuario.getId(), usuario.getNombreCompleto(),
                usuario.getCorreoInstitucional(), usuario.getRol());
    }
}
