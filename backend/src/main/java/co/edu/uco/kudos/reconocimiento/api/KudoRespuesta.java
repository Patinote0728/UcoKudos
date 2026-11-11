package co.edu.uco.kudos.reconocimiento.api;

import java.time.Instant;
import java.util.UUID;

public record KudoRespuesta(
        UUID id,
        UUID autorId,
        String nombreAutor,
        UUID destinatarioId,
        String nombreDestinatario,
        UUID categoriaId,
        String nombreCategoria,
        String mensaje,
        Instant fechaCreacion
) {
}
