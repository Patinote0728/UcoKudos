package co.edu.uco.kudos.reconocimiento.api;

import co.edu.uco.kudos.reconocimiento.aplicacion.ServicioKudos;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/kudos")
public class KudoControlador {

    private final ServicioKudos servicio;

    public KudoControlador(ServicioKudos servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<KudoRespuesta> publicar(@Valid @RequestBody CrearKudoSolicitud solicitud) {
        KudoRespuesta respuesta = servicio.publicar(solicitud);
        return ResponseEntity.created(URI.create("/api/kudos/" + respuesta.id())).body(respuesta);
    }

    @GetMapping
    public PaginaRespuesta<KudoRespuesta> listarPorDestinatario(
            @RequestParam @NotNull UUID destinatarioId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamano) {
        return servicio.listarPorDestinatario(destinatarioId, pagina, tamano);
    }
}
