package co.edu.uco.kudos.notificacion.infraestructura;

import co.edu.uco.kudos.notificacion.aplicacion.AdaptadorNotificaciones;
import co.edu.uco.kudos.notificacion.aplicacion.NotificacionPendiente;
import co.edu.uco.kudos.notificacion.aplicacion.ResultadoNotificacion;
import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import co.edu.uco.kudos.notificacion.dominio.DispositivoPush;
import co.edu.uco.kudos.notificacion.persistencia.DispositivoPushRepositorio;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;

public class AdaptadorFirebasePush implements AdaptadorNotificaciones {

    private static final int MAXIMO_DISPOSITIVOS_POR_LOTE = 500;

    private final FirebaseMessaging firebaseMessaging;
    private final DispositivoPushRepositorio dispositivos;

    public AdaptadorFirebasePush(FirebaseMessaging firebaseMessaging, DispositivoPushRepositorio dispositivos) {
        this.firebaseMessaging = firebaseMessaging;
        this.dispositivos = dispositivos;
    }

    @Override
    public boolean soporta(CanalNotificacion canal) {
        return canal == CanalNotificacion.PUSH;
    }

    @Override
    public ResultadoNotificacion enviar(NotificacionPendiente notificacion) {
        List<String> identificadores = dispositivos.findByUsuarioIdAndActivoTrue(notificacion.destinatarioId()).stream()
                .map(DispositivoPush::getIdentificadorInstalacionFirebase)
                .toList();
        if (identificadores.isEmpty()) {
            return ResultadoNotificacion.OMITIDA;
        }

        for (int inicio = 0; inicio < identificadores.size(); inicio += MAXIMO_DISPOSITIVOS_POR_LOTE) {
            List<String> lote = identificadores.subList(inicio,
                    Math.min(inicio + MAXIMO_DISPOSITIVOS_POR_LOTE, identificadores.size()));
            enviarLote(notificacion, lote);
        }
        return ResultadoNotificacion.ENVIADA;
    }

    private void enviarLote(NotificacionPendiente notificacion, List<String> identificadores) {
        MulticastMessage mensaje = MulticastMessage.builder()
                .addAllFids(identificadores)
                .setNotification(Notification.builder()
                        .setTitle("Recibiste un Kudo")
                        .setBody(notificacion.nombreAutor() + " te reconoció en " + notificacion.nombreCategoria())
                        .build())
                .putData("kudoId", notificacion.kudoId().toString())
                .build();
        try {
            BatchResponse respuesta = firebaseMessaging.sendEachForMulticast(mensaje);
            if (respuesta.getFailureCount() > 0) {
                throw new IllegalStateException("Firebase no aceptó todos los mensajes push del lote.");
            }
        } catch (FirebaseMessagingException exception) {
            throw new IllegalStateException("Firebase no aceptó el mensaje push.", exception);
        }
    }
}
