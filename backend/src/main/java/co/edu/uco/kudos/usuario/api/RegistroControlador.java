package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.aplicacion.ServicioRegistro;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registros")
public class RegistroControlador {
    private final ServicioRegistro servicio;
    public RegistroControlador(ServicioRegistro servicio) { this.servicio = servicio; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registrar(@Valid @RequestBody RegistrarUsuarioSolicitud solicitud) {
        servicio.registrar(solicitud); return Map.of("mensaje", "Registro creado; verifica correo y celular.");
    }
    @PostMapping("/verificar")
    public Map<String, String> verificar(@Valid @RequestBody VerificarRegistroSolicitud solicitud) {
        servicio.verificar(solicitud); return Map.of("mensaje", "Verificación completada. Tu cuenta espera aprobación.");
    }
}
