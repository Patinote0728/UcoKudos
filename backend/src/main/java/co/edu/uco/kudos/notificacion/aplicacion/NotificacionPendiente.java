package co.edu.uco.kudos.notificacion.aplicacion;

import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import java.util.UUID;

public record NotificacionPendiente(
        UUID notificacionId,
        UUID kudoId,
        UUID destinatarioId,
        CanalNotificacion canal,
        String correoInstitucional,
        String numeroCelular,
        String nombreDestinatario,
        String nombreAutor,
        String nombreCategoria,
        boolean whatsappAutorizado,
        String mensaje
) {
}
