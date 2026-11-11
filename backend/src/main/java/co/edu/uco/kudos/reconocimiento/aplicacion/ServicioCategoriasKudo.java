package co.edu.uco.kudos.reconocimiento.aplicacion;

import co.edu.uco.kudos.reconocimiento.api.CategoriaKudoRespuesta;
import co.edu.uco.kudos.reconocimiento.persistencia.CategoriaKudoRepositorio;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioCategoriasKudo {

    private final CategoriaKudoRepositorio categorias;

    public ServicioCategoriasKudo(CategoriaKudoRepositorio categorias) {
        this.categorias = categorias;
    }

    @Transactional(readOnly = true)
    public List<CategoriaKudoRespuesta> listarActivas() {
        return categorias.findByActivoTrueOrderByNombreAsc().stream()
                .map(categoria -> new CategoriaKudoRespuesta(
                        categoria.getId(), categoria.getNombre(), categoria.getDescripcion()))
                .toList();
    }
}
