package co.edu.uco.kudos.notificacion.infraestructura;

import co.edu.uco.kudos.notificacion.aplicacion.AdaptadorNotificaciones;
import co.edu.uco.kudos.notificacion.aplicacion.NotificacionPendiente;
import co.edu.uco.kudos.notificacion.aplicacion.ResultadoNotificacion;
import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class AdaptadorWhatsappDeshabilitado implements AdaptadorNotificaciones {
    @Override
    public boolean soporta(CanalNotificacion canal) {
        return canal == CanalNotificacion.WHATSAPP;
    }

    @Override
    public ResultadoNotificacion enviar(NotificacionPendiente notificacion) {
        return ResultadoNotificacion.OMITIDA;
    }
}
