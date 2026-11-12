package co.edu.uco.kudos.notificacion.api;

import co.edu.uco.kudos.notificacion.aplicacion.ServicioDispositivosPush;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispositivos-push")
public class DispositivoPushControlador {

    private final ServicioDispositivosPush servicio;

    public DispositivoPushControlador(ServicioDispositivosPush servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<DispositivoPushRespuesta> registrar(
            @Valid @RequestBody RegistrarDispositivoPushSolicitud solicitud,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        UUID id = servicio.registrar(usuarioAutenticado.getUsername(), solicitud.identificadorInstalacionFirebase());
        return ResponseEntity.created(URI.create("/api/dispositivos-push/" + id))
                .body(new DispositivoPushRespuesta(id, true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id,
                                           @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        servicio.desactivar(usuarioAutenticado.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
