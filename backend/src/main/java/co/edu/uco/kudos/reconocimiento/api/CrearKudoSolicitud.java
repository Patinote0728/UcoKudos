package co.edu.uco.kudos.reconocimiento.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CrearKudoSolicitud(
        @NotNull UUID autorId,
        @NotNull UUID destinatarioId,
        @NotNull UUID categoriaId,
        @NotBlank String mensaje
) {
}
