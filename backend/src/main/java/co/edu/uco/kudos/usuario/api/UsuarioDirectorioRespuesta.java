package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import java.util.UUID;

public record UsuarioDirectorioRespuesta(UUID id, String nombreCompleto, RolUsuario rol) {
    public static UsuarioDirectorioRespuesta desde(Usuario usuario) {
        return new UsuarioDirectorioRespuesta(usuario.getId(), usuario.getNombreCompleto(), usuario.getRol());
    }
}
