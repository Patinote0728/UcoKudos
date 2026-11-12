package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.aplicacion.ServicioPreferenciasNotificacion;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mi-cuenta/preferencias/notificaciones")
public class PreferenciasNotificacionControlador {

    private final ServicioPreferenciasNotificacion servicio;

    public PreferenciasNotificacionControlador(ServicioPreferenciasNotificacion servicio) {
        this.servicio = servicio;
    }

    @PutMapping
    public PreferenciasNotificacionRespuesta cambiarWhatsapp(
            @Valid @RequestBody CambiarAutorizacionWhatsappSolicitud solicitud,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        return servicio.cambiarAutorizacionWhatsapp(usuarioAutenticado.getUsername(), solicitud.autorizado());
    }
}
