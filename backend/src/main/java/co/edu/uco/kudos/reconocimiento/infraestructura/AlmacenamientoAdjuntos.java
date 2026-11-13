package co.edu.uco.kudos.reconocimiento.infraestructura;

import java.io.IOException;
import org.springframework.core.io.Resource;

public interface AlmacenamientoAdjuntos {
    String guardar(byte[] contenido, String tipoContenido) throws IOException;
    Resource resolver(String clave) throws IOException;
    void eliminar(String clave);
}
