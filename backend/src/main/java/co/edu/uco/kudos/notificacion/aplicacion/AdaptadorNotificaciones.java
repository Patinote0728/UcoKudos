package co.edu.uco.kudos.notificacion.aplicacion;

import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;

public interface AdaptadorNotificaciones {
    boolean soporta(CanalNotificacion canal);
    ResultadoNotificacion enviar(NotificacionPendiente notificacion);
}
