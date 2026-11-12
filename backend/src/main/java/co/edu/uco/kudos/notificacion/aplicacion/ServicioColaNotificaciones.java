package co.edu.uco.kudos.notificacion.aplicacion;

import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import co.edu.uco.kudos.notificacion.dominio.ColaNotificacion;
import co.edu.uco.kudos.notificacion.dominio.EstadoNotificacion;
import co.edu.uco.kudos.notificacion.persistencia.ColaNotificacionRepositorio;
import co.edu.uco.kudos.reconocimiento.dominio.Kudo;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioColaNotificaciones {

    private static final int INTENTOS_MAXIMOS = 8;
    private static final int TAMANO_LOTE = 5;
    private static final long BLOQUEO_MINUTOS = 5;

    private final ColaNotificacionRepositorio notificaciones;

    public ServicioColaNotificaciones(ColaNotificacionRepositorio notificaciones) {
        this.notificaciones = notificaciones;
    }

    @Transactional
    public void programar(Kudo kudo, Instant ahora) {
        for (CanalNotificacion canal : CanalNotificacion.values()) {
            notificaciones.save(new ColaNotificacion(UUID.randomUUID(), kudo, kudo.getDestinatario(), canal, ahora));
        }
    }

    @Transactional
    public List<UUID> reclamarPendientes() {
        Instant ahora = Instant.now();
        return notificaciones.reclamarPendientes(TAMANO_LOTE).stream()
                .peek(notificacion -> notificacion.marcarProcesando(ahora, ahora.plus(BLOQUEO_MINUTOS, ChronoUnit.MINUTES)))
                .map(ColaNotificacion::getId)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificacionPendiente preparar(UUID id) {
        ColaNotificacion notificacion = notificaciones.findById(id)
                .orElseThrow(() -> new IllegalStateException("No se encontró el trabajo de notificación."));
        Kudo kudo = notificacion.getKudo();
        return new NotificacionPendiente(notificacion.getId(), kudo.getId(),
                notificacion.getDestinatario().getId(), notificacion.getCanal(),
                notificacion.getDestinatario().getCorreoInstitucional(),
                notificacion.getDestinatario().getNumeroCelular(),
                notificacion.getDestinatario().getNombreCompleto(), kudo.getAutor().getNombreCompleto(),
                kudo.getCategoria().getNombre(), notificacion.getDestinatario().isWhatsappAutorizado(),
                kudo.getMensaje());
    }

    @Transactional
    public void registrarResultado(UUID id, ResultadoNotificacion resultado) {
        ColaNotificacion notificacion = cargar(id);
        EstadoNotificacion estado = switch (resultado) {
            case ENVIADA -> EstadoNotificacion.ENVIADA;
            case SIMULADA -> EstadoNotificacion.SIMULADA;
            case OMITIDA -> EstadoNotificacion.OMITIDA;
        };
        notificacion.marcarCompletada(estado, Instant.now());
    }

    @Transactional
    public void registrarFallo(UUID id, RuntimeException error) {
        ColaNotificacion notificacion = cargar(id);
        Instant ahora = Instant.now();
        String descripcionSegura = error.getClass().getSimpleName();
        if (notificacion.getIntentos() >= INTENTOS_MAXIMOS) {
            notificacion.marcarFallida(descripcionSegura, ahora);
            return;
        }
        long esperaSegundos = Math.min(3600, 10L << Math.min(notificacion.getIntentos() - 1, 8));
        notificacion.programarReintento(ahora.plusSeconds(esperaSegundos), descripcionSegura, ahora);
    }

    private ColaNotificacion cargar(UUID id) {
        return notificaciones.findById(id)
                .orElseThrow(() -> new IllegalStateException("No se encontró el trabajo de notificación."));
    }
}
