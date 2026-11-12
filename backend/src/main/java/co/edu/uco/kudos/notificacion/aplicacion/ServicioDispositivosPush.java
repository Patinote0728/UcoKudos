package co.edu.uco.kudos.notificacion.aplicacion;

import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.notificacion.dominio.DispositivoPush;
import co.edu.uco.kudos.notificacion.persistencia.DispositivoPushRepositorio;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioDispositivosPush {

    private final DispositivoPushRepositorio dispositivos;
    private final UsuarioRepositorio usuarios;

    public ServicioDispositivosPush(DispositivoPushRepositorio dispositivos, UsuarioRepositorio usuarios) {
        this.dispositivos = dispositivos;
        this.usuarios = usuarios;
    }

    @Transactional
    public UUID registrar(String correoUsuario, String identificadorFirebase) {
        Usuario usuario = buscarUsuario(correoUsuario);
        DispositivoPush dispositivo = dispositivos.findByIdentificadorInstalacionFirebase(identificadorFirebase)
                .map(existente -> {
                    if (!existente.getUsuario().getId().equals(usuario.getId())) {
                        throw new ReglaNegocioException("La instalación ya está asociada a otra cuenta.");
                    }
                    existente.renovar(Instant.now());
                    return existente;
                })
                .orElseGet(() -> dispositivos.save(new DispositivoPush(
                        UUID.randomUUID(), usuario, identificadorFirebase, Instant.now())));
        return dispositivo.getId();
    }

    @Transactional
    public void desactivar(String correoUsuario, UUID dispositivoId) {
        Usuario usuario = buscarUsuario(correoUsuario);
        DispositivoPush dispositivo = dispositivos.findById(dispositivoId)
                .filter(existente -> existente.getUsuario().getId().equals(usuario.getId()))
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el dispositivo de esta cuenta."));
        dispositivo.desactivar(Instant.now());
    }

    private Usuario buscarUsuario(String correo) {
        return usuarios.findByCorreoInstitucional(correo)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta activa."));
    }
}
