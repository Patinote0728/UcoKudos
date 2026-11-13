package co.edu.uco.kudos.usuario.infraestructura;

import co.edu.uco.kudos.usuario.aplicacion.EntregaCodigoVerificacion;
import co.edu.uco.kudos.usuario.dominio.CanalVerificacion;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("prod")
public class EntregaCodigoProduccion implements EntregaCodigoVerificacion {
    private final RestClient cliente;
    private final String brevoApiKey;
    private final String remitente;
    private final String nombreRemitente;
    public EntregaCodigoProduccion(RestClient.Builder builder,
            @Value("${ucokudos.verificacion.brevo.api-key}") String brevoApiKey,
            @Value("${ucokudos.verificacion.brevo.remitente}") String remitente,
            @Value("${ucokudos.verificacion.brevo.nombre-remitente:UCO Kudos}") String nombreRemitente) {
        this.cliente = builder.build();
        this.brevoApiKey = brevoApiKey;
        this.remitente = remitente;
        this.nombreRemitente = nombreRemitente;
    }

    @Override
    public void enviar(CanalVerificacion canal, String destino, String codigo) {
        if (canal != CanalVerificacion.CORREO) {
            throw new IllegalStateException("La verificación del celular está deshabilitada en producción.");
        }
        enviarCorreo(destino, codigo);
    }

    private void enviarCorreo(String destino, String codigo) {
        Map<String, Object> cuerpo = Map.of(
                "sender", Map.of("name", nombreRemitente, "email", remitente),
                "to", List.of(Map.of("email", destino)),
                "subject", "Código de verificación de UCO Kudos",
                "textContent", "Tu código de verificación es " + codigo + ". Vence en 10 minutos. Si no solicitaste este registro, ignora este mensaje.");
        cliente.post().uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", brevoApiKey).contentType(MediaType.APPLICATION_JSON)
                .body(cuerpo).retrieve().toBodilessEntity();
    }

}
