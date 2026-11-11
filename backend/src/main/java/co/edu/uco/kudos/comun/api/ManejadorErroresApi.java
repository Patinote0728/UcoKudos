package co.edu.uco.kudos.comun.api;

import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.comun.error.SolicitudInvalidaException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErroresApi {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> manejarNoEncontrado(RecursoNoEncontradoException exception) {
        return respuesta(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<RespuestaError> manejarReglaNegocio(ReglaNegocioException exception) {
        return respuesta(HttpStatus.CONFLICT, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<RespuestaError> manejarSolicitudInvalida(SolicitudInvalidaException exception) {
        return respuesta(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(MethodArgumentNotValidException exception) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return respuesta(HttpStatus.BAD_REQUEST, "La solicitud contiene datos inválidos.", errores);
    }

    private ResponseEntity<RespuestaError> respuesta(HttpStatus estado, String mensaje, Map<String, String> errores) {
        return ResponseEntity.status(estado)
                .body(new RespuestaError(Instant.now(), estado.value(), mensaje, errores));
    }
}
