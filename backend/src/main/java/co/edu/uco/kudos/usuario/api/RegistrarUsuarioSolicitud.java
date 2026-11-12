package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record RegistrarUsuarioSolicitud(
        @Email @NotBlank @Size(max = 254) String correoInstitucional,
        @NotBlank @Pattern(regexp = "^\\+[1-9][0-9]{7,14}$") String numeroCelular,
        @NotBlank @Size(max = 180) String nombreCompleto,
        @NotBlank @Size(min = 12, max = 72) String contrasena,
        @NotNull RolUsuario rol,
        UUID facultadId,
        UUID programaId) { }
