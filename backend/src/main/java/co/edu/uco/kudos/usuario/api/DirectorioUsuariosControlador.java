package co.edu.uco.kudos.usuario.api;

import co.edu.uco.kudos.usuario.aplicacion.ServicioDirectorioUsuarios;
import jakarta.validation.constraints.Size;
import java.security.Principal;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/usuarios")
public class DirectorioUsuariosControlador {
    private final ServicioDirectorioUsuarios servicio;

    public DirectorioUsuariosControlador(ServicioDirectorioUsuarios servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<UsuarioDirectorioRespuesta> buscar(@RequestParam @Size(min = 2, max = 80) String busqueda,
                                                   Principal usuario) {
        return servicio.buscar(usuario.getName(), busqueda);
    }
}
