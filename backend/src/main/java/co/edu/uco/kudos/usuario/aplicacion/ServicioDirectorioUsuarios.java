package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.comun.error.SolicitudInvalidaException;
import co.edu.uco.kudos.usuario.api.UsuarioDirectorioRespuesta;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioDirectorioUsuarios {
    private final UsuarioRepositorio usuarios;

    public ServicioDirectorioUsuarios(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDirectorioRespuesta> buscar(String correoActual, String texto) {
        String busqueda = texto == null ? "" : texto.trim();
        if (busqueda.length() < 2 || busqueda.length() > 80) {
            throw new SolicitudInvalidaException("La búsqueda debe tener entre 2 y 80 caracteres.");
        }
        var actual = usuarios.findByCorreoInstitucional(correoActual)
                .filter(usuario -> usuario.isActivo())
                .orElseThrow(() -> new SolicitudInvalidaException("La cuenta actual no está activa."));
        return usuarios.findTop10ByActivoTrueAndIdNotAndNombreCompletoContainingIgnoreCaseOrderByNombreCompletoAsc(
                        actual.getId(), busqueda).stream()
                .map(UsuarioDirectorioRespuesta::desde)
                .toList();
    }
}
