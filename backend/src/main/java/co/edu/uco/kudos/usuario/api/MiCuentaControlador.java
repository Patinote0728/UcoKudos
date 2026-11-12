package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.aplicacion.ServicioMiCuenta;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mi-cuenta")
public class MiCuentaControlador {
    private final ServicioMiCuenta servicio;

    public MiCuentaControlador(ServicioMiCuenta servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public MiCuentaRespuesta obtener(Principal usuario) {
        return servicio.obtener(usuario.getName());
    }
}
