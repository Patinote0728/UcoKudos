package co.edu.uco.kudos.reconocimiento.api;

import co.edu.uco.kudos.reconocimiento.dominio.AdjuntoKudo;
import java.time.Instant;
import java.util.UUID;

public record AdjuntoKudoRespuesta(UUID id, String nombreArchivo, String tipoContenido, long tamanoBytes,
        Instant fechaCreacion) {
    public static AdjuntoKudoRespuesta desde(AdjuntoKudo adjunto) {
        return new AdjuntoKudoRespuesta(adjunto.getId(), adjunto.getNombreArchivoOriginal(),
                adjunto.getTipoContenido(), adjunto.getTamanoBytes(), adjunto.getCreadoEn());
    }
}
