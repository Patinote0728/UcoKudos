package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.dominio.EstadoAprobacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DecidirAprobacionSolicitud(@NotNull EstadoAprobacion decision, @Size(max=1000) String comentario) { }
