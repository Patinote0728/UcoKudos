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
@ConditionalOnProperty(prefix = "ucokudos.notificaciones.whatsapp", name = {"token", "phone-number-id", "graph-version", "template-name"})
public class AdaptadorWhatsAppMeta implements AdaptadorNotificaciones {

    private final RestClient cliente;
    private final String token;
    private final String idNumeroTelefono;
    private final String versionGraph;
    private final String plantilla;
    private final String idioma;

    public AdaptadorWhatsAppMeta(
            RestClient.Builder constructorCliente,
            @Value("${ucokudos.notificaciones.whatsapp.token}") String token,
            @Value("${ucokudos.notificaciones.whatsapp.phone-number-id}") String idNumeroTelefono,
            @Value("${ucokudos.notificaciones.whatsapp.graph-version}") String versionGraph,
            @Value("${ucokudos.notificaciones.whatsapp.template-name}") String plantilla,
            @Value("${ucokudos.notificaciones.whatsapp.template-language:es_CO}") String idioma) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(5));
        fabrica.setReadTimeout(Duration.ofSeconds(30));
        this.cliente = constructorCliente.requestFactory(fabrica).build();
        this.token = token;
        this.idNumeroTelefono = idNumeroTelefono;
        this.versionGraph = versionGraph;
        this.plantilla = plantilla;
        this.idioma = idioma;
    }

    @Override
    public boolean soporta(CanalNotificacion canal) {
        return canal == CanalNotificacion.WHATSAPP;
    }

    @Override
    public ResultadoNotificacion enviar(NotificacionPendiente notificacion) {
        if (!notificacion.whatsappAutorizado()) {
            return ResultadoNotificacion.OMITIDA;
        }

        List<Map<String, String>> parametros = List.of(
                parametro(notificacion.nombreAutor()),
                parametro(notificacion.nombreCategoria()),
                parametro(notificacion.mensaje()));
        Map<String, Object> cuerpo = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", notificacion.numeroCelular().replaceAll("\\D", ""),
                "type", "template",
                "template", Map.of(
                        "name", plantilla,
                        "language", Map.of("code", idioma),
                        "components", List.of(Map.of("type", "body", "parameters", parametros))));

        cliente.post()
                .uri("https://graph.facebook.com/{version}/{phoneNumberId}/messages", versionGraph, idNumeroTelefono)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(cuerpo)
                .retrieve()
                .toBodilessEntity();
        return ResultadoNotificacion.ENVIADA;
    }

    private Map<String, String> parametro(String texto) {
        return Map.of("type", "text", "text", texto);
    }
}
