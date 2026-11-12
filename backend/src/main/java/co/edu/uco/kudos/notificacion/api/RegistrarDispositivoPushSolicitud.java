package co.edu.uco.kudos.notificacion.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarDispositivoPushSolicitud(
        @NotBlank @Size(max = 255) String identificadorInstalacionFirebase
) {
}
