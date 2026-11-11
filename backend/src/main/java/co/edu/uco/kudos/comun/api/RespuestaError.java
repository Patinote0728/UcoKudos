package co.edu.uco.kudos.comun.api;

import java.time.Instant;
import java.util.Map;

public record RespuestaError(Instant fecha, int estado, String mensaje, Map<String, String> errores) {
}
