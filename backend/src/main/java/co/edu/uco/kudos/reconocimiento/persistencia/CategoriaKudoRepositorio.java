package co.edu.uco.kudos.reconocimiento.persistencia;

import co.edu.uco.kudos.reconocimiento.dominio.CategoriaKudo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaKudoRepositorio extends JpaRepository<CategoriaKudo, UUID> {
    List<CategoriaKudo> findByActivoTrueOrderByNombreAsc();
}
