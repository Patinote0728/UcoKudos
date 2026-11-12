package co.edu.uco.kudos.notificacion.aplicacion;

import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import java.util.UUID;

public record NotificacionPendiente(
        UUID notificacionId,
        UUID kudoId,
        CanalNotificacion canal,
        String correoInstitucional,
        String numeroCelular,
        String mensaje
) {
}
