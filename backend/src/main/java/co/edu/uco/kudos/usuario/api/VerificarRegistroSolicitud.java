package co.edu.uco.kudos.usuario.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificarRegistroSolicitud(@Email @NotBlank String correoInstitucional,
        @NotBlank String codigoCorreo, @NotBlank String codigoCelular) { }
