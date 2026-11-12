package co.edu.uco.kudos.academico.api;

import co.edu.uco.kudos.academico.persistencia.FacultadRepositorio;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facultades")
public class FacultadControlador {
    private final FacultadRepositorio facultades;

    public FacultadControlador(FacultadRepositorio facultades) {
        this.facultades = facultades;
    }

    @GetMapping
    public List<FacultadRespuesta> listar() {
        return facultades.findByActivoTrueOrderByNombreAsc().stream()
                .map(FacultadRespuesta::desde)
                .toList();
    }
}
