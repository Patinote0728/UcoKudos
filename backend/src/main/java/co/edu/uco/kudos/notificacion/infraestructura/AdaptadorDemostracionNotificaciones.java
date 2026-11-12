package co.edu.uco.kudos.notificacion.infraestructura;

import co.edu.uco.kudos.notificacion.aplicacion.AdaptadorNotificaciones;
import co.edu.uco.kudos.notificacion.aplicacion.NotificacionPendiente;
import co.edu.uco.kudos.notificacion.aplicacion.ResultadoNotificacion;
import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class AdaptadorDemostracionNotificaciones implements AdaptadorNotificaciones {

    private static final Logger logger = LoggerFactory.getLogger(AdaptadorDemostracionNotificaciones.class);

    @Override
    public boolean soporta(CanalNotificacion canal) {
        return EnumSet.allOf(CanalNotificacion.class).contains(canal);
    }

    @Override
    public ResultadoNotificacion enviar(NotificacionPendiente notificacion) {
        logger.info("Notificación simulada. canal={} kudoId={} notificacionId={}",
                notificacion.canal(), notificacion.kudoId(), notificacion.notificacionId());
        return ResultadoNotificacion.SIMULADA;
    }
}
