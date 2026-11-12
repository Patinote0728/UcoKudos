package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.aplicacion.ServicioAprobaciones;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aprobaciones")
public class AprobacionControlador {
    private final ServicioAprobaciones servicio;
    public AprobacionControlador(ServicioAprobaciones servicio) { this.servicio=servicio; }
    @GetMapping("/pendientes") public List<AprobacionRespuesta> pendientes(Principal actor) { return servicio.pendientes(actor.getName()); }
    @PostMapping("/{id}") public Map<String,String> decidir(@PathVariable UUID id, @Valid @RequestBody DecidirAprobacionSolicitud s, Principal actor) {
        servicio.decidir(id,s,actor.getName()); return Map.of("mensaje","Decisión registrada.");
    }
}
