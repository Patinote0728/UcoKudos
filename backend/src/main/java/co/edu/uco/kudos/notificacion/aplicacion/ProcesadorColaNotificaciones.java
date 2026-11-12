package co.edu.uco.kudos.notificacion.aplicacion;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ProcesadorColaNotificaciones {

    private static final Logger logger = LoggerFactory.getLogger(ProcesadorColaNotificaciones.class);

    private final ServicioColaNotificaciones cola;
    private final List<AdaptadorNotificaciones> adaptadores;

    public ProcesadorColaNotificaciones(ServicioColaNotificaciones cola,
                                        List<AdaptadorNotificaciones> adaptadores) {
        this.cola = cola;
        this.adaptadores = adaptadores;
    }

    @Scheduled(fixedDelayString = "${ucokudos.notificaciones.intervalo-ms:3000}")
    public void procesarLote() {
        for (UUID id : cola.reclamarPendientes()) {
            procesar(id);
        }
    }

    private void procesar(UUID id) {
        try {
            NotificacionPendiente notificacion = cola.preparar(id);
            AdaptadorNotificaciones adaptador = adaptadores.stream()
                    .filter(candidato -> candidato.soporta(notificacion.canal()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No hay un proveedor configurado para el canal."));
            ResultadoNotificacion resultado = adaptador.enviar(notificacion);
            cola.registrarResultado(id, resultado);
        } catch (RuntimeException error) {
            logger.warn("Falló el procesamiento de una notificación. id={} causa={}", id, error.getClass().getSimpleName());
            cola.registrarFallo(id, error);
        }
    }
}
