package co.edu.uco.kudos.reconocimiento.infraestructura;

import java.io.IOException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Profile("prod")
public class AlmacenamientoAdjuntosSupabase implements AlmacenamientoAdjuntos {
    private final RestClient cliente;
    private final String claveServicio;
    private final String bucket;

    public AlmacenamientoAdjuntosSupabase(RestClient.Builder builder,
            @Value("${ucokudos.adjuntos.supabase.url}") String url,
            @Value("${ucokudos.adjuntos.supabase.service-role-key}") String claveServicio,
            @Value("${ucokudos.adjuntos.supabase.bucket}") String bucket) {
        this.cliente = builder.baseUrl(url.replaceAll("/$", "")).build();
        this.claveServicio = claveServicio;
        this.bucket = bucket;
    }

    @Override
    public String guardar(byte[] contenido) throws IOException {
        String clave = UUID.randomUUID().toString();
        cliente.post().uri("/storage/v1/object/{bucket}/{clave}", bucket, clave)
                .header("apikey", claveServicio)
                .header("Authorization", "Bearer " + claveServicio)
                .header("x-upsert", "false")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(contenido)
                .retrieve().toBodilessEntity();
        return clave;
    }

    @Override
    public Resource resolver(String clave) throws IOException {
        byte[] contenido = cliente.get()
                .uri("/storage/v1/object/authenticated/{bucket}/{clave}", bucket, clave)
                .header("apikey", claveServicio)
                .header("Authorization", "Bearer " + claveServicio)
                .retrieve().body(byte[].class);
        if (contenido == null) throw new IOException("El almacenamiento no devolvió el archivo.");
        return new ByteArrayResource(contenido);
    }

    @Override
    public void eliminar(String clave) {
        try {
            cliente.delete().uri("/storage/v1/object/{bucket}/{clave}", bucket, clave)
                    .header("apikey", claveServicio)
                    .header("Authorization", "Bearer " + claveServicio)
                    .retrieve().toBodilessEntity();
        } catch (RuntimeException ignored) { }
    }
}
