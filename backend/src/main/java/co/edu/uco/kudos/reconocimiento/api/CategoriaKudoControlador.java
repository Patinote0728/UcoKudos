package co.edu.uco.kudos.reconocimiento.api;

import co.edu.uco.kudos.reconocimiento.aplicacion.ServicioCategoriasKudo;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias-kudos")
public class CategoriaKudoControlador {

    private final ServicioCategoriasKudo servicio;

    public CategoriaKudoControlador(ServicioCategoriasKudo servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<CategoriaKudoRespuesta> listar() {
        return servicio.listarActivas();
    }
}
