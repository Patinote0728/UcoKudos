package co.edu.uco.kudos.reconocimiento.infraestructura;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class AlmacenamientoAdjuntosLocal implements AlmacenamientoAdjuntos {
    private final Path directorio;

    public AlmacenamientoAdjuntosLocal(@Value("${ucokudos.adjuntos.ruta:./datos/adjuntos}") String ruta) {
        this.directorio = Path.of(ruta).toAbsolutePath().normalize();
    }

    @Override
    public String guardar(byte[] contenido, String tipoContenido) throws IOException {
        Files.createDirectories(directorio);
        String clave = UUID.randomUUID().toString();
        Files.write(directorio.resolve(clave), contenido, StandardOpenOption.CREATE_NEW);
        return clave;
    }

    @Override
    public Resource resolver(String clave) throws IOException {
        Path archivo = directorio.resolve(clave).normalize();
        if (!archivo.startsWith(directorio)) throw new IOException("Ruta de archivo inválida.");
        return new UrlResource(archivo.toUri());
    }

    @Override
    public void eliminar(String clave) {
        try {
            Path archivo = directorio.resolve(clave).normalize();
            if (archivo.startsWith(directorio)) Files.deleteIfExists(archivo);
        } catch (IOException ignored) { }
    }
}
