package co.edu.uco.kudos.notificacion.infraestructura;

import co.edu.uco.kudos.notificacion.aplicacion.AdaptadorNotificaciones;
import co.edu.uco.kudos.notificacion.aplicacion.NotificacionPendiente;
import co.edu.uco.kudos.notificacion.aplicacion.ResultadoNotificacion;
import co.edu.uco.kudos.notificacion.dominio.CanalNotificacion;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;

@Component
@ConditionalOnProperty(name = "ucokudos.notificaciones.reales-habilitadas", havingValue = "true")
@ConditionalOnProperty(prefix = "ucokudos.notificaciones.brevo", name = {"api-key", "remitente"})
public class AdaptadorCorreoBrevo implements AdaptadorNotificaciones {

    private final RestClient cliente;
    private final String apiKey;
    private final String remitente;
    private final String nombreRemitente;

    public AdaptadorCorreoBrevo(
            RestClient.Builder constructorCliente,
            @Value("${ucokudos.notificaciones.brevo.api-key}") String apiKey,
            @Value("${ucokudos.notificaciones.brevo.remitente}") String remitente,
            @Value("${ucokudos.notificaciones.brevo.nombre-remitente:UCO Kudos}") String nombreRemitente) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(5));
        fabrica.setReadTimeout(Duration.ofSeconds(30));
        this.cliente = constructorCliente.baseUrl("https://api.brevo.com/v3").requestFactory(fabrica).build();
        this.apiKey = apiKey;
        this.remitente = remitente;
        this.nombreRemitente = nombreRemitente;
    }

    @Override
    public boolean soporta(CanalNotificacion canal) {
        return canal == CanalNotificacion.CORREO;
    }

    @Override
    public ResultadoNotificacion enviar(NotificacionPendiente notificacion) {
        String texto = "Hola " + notificacion.nombreDestinatario() + ",\n\n"
                + notificacion.nombreAutor() + " te envió un Kudo en la categoría "
                + notificacion.nombreCategoria() + ":\n\n" + notificacion.mensaje();
        Map<String, Object> cuerpo = Map.of(
                "sender", Map.of("name", nombreRemitente, "email", remitente),
                "to", List.of(Map.of("email", notificacion.correoInstitucional(), "name", notificacion.nombreDestinatario())),
                "subject", "Recibiste un Kudo en UCO Kudos",
                "textContent", texto);

        cliente.post()
                .uri("/smtp/email")
                .header("api-key", apiKey)
                .header("accept", "application/json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(cuerpo)
                .retrieve()
                .toBodilessEntity();
        return ResultadoNotificacion.ENVIADA;
    }
}
