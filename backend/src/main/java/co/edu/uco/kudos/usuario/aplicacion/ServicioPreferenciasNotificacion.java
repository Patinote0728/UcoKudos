package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.usuario.api.PreferenciasNotificacionRespuesta;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioPreferenciasNotificacion {

    private final UsuarioRepositorio usuarios;
    private final boolean whatsappHabilitado;

    public ServicioPreferenciasNotificacion(UsuarioRepositorio usuarios,
            @Value("${ucokudos.notificaciones.whatsapp-habilitadas:true}") boolean whatsappHabilitado) {
        this.usuarios = usuarios;
        this.whatsappHabilitado = whatsappHabilitado;
    }

    @Transactional
    public PreferenciasNotificacionRespuesta cambiarAutorizacionWhatsapp(String correo, boolean autorizado) {
        if (autorizado && !whatsappHabilitado) {
            throw new ReglaNegocioException("Las notificaciones de WhatsApp están deshabilitadas en este entorno.");
        }
        Usuario usuario = usuarios.findByCorreoInstitucional(correo)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la cuenta activa."));
        if (autorizado && !usuario.isCelularVerificado()) {
            throw new ReglaNegocioException("El número de celular debe verificarse antes de autorizar WhatsApp.");
        }
        usuario.cambiarAutorizacionWhatsapp(autorizado, Instant.now());
        return new PreferenciasNotificacionRespuesta(usuario.isWhatsappAutorizado());
    }
}
