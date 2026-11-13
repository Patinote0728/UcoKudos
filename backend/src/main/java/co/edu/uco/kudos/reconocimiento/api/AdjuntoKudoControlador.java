package co.edu.uco.kudos.reconocimiento.api;

import co.edu.uco.kudos.reconocimiento.aplicacion.ServicioAdjuntosKudo;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/kudos/{kudoId}/adjuntos")
public class AdjuntoKudoControlador {
    private final ServicioAdjuntosKudo servicio;
    public AdjuntoKudoControlador(ServicioAdjuntosKudo servicio) { this.servicio=servicio; }

    @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdjuntoKudoRespuesta> subir(@PathVariable UUID kudoId,
            @RequestParam("archivo") MultipartFile archivo, @AuthenticationPrincipal UserDetails usuario) {
        return ResponseEntity.status(201).body(servicio.guardar(kudoId,usuario.getUsername(),archivo));
    }

    @GetMapping
    public List<AdjuntoKudoRespuesta> listar(@PathVariable UUID kudoId,@AuthenticationPrincipal UserDetails usuario) {
        return servicio.listar(kudoId,usuario.getUsername());
    }

    @GetMapping("/{adjuntoId}/contenido")
    public ResponseEntity<Resource> descargar(@PathVariable UUID kudoId,@PathVariable UUID adjuntoId,
            @AuthenticationPrincipal UserDetails usuario) {
        var archivo=servicio.descargar(kudoId,adjuntoId,usuario.getUsername());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(archivo.tipo()))
                .header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.attachment()
                        .filename(archivo.nombre(),StandardCharsets.UTF_8).build().toString())
                .body(archivo.recurso());
    }
}
