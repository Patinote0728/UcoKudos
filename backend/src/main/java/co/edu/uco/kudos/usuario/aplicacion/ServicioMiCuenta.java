package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.usuario.api.MiCuentaRespuesta;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioMiCuenta {
    private final UsuarioRepositorio usuarios;

    public ServicioMiCuenta(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    @Transactional(readOnly = true)
    public MiCuentaRespuesta obtener(String correo) {
        return usuarios.findByCorreoInstitucional(correo)
                .filter(usuario -> usuario.isActivo())
                .map(MiCuentaRespuesta::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta activa."));
    }
}
