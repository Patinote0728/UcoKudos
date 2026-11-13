package co.edu.uco.kudos.reconocimiento.infraestructura;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AlmacenamientoAdjuntos {
    private final Path directorio;

    public AlmacenamientoAdjuntos(@Value("${ucokudos.adjuntos.ruta:./datos/adjuntos}") String ruta) {
        this.directorio = Path.of(ruta).toAbsolutePath().normalize();
    }

    public String guardar(byte[] contenido) throws IOException {
        Files.createDirectories(directorio);
        String clave = UUID.randomUUID().toString();
        Files.write(directorio.resolve(clave), contenido, StandardOpenOption.CREATE_NEW);
        return clave;
    }

    public Path resolver(String clave) throws IOException {
        Path archivo = directorio.resolve(clave).normalize();
        if (!archivo.startsWith(directorio)) throw new IOException("Ruta de archivo inválida.");
        return archivo;
    }

    public void eliminar(String clave) {
        try { Files.deleteIfExists(resolver(clave)); } catch (IOException ignored) { }
    }
}
