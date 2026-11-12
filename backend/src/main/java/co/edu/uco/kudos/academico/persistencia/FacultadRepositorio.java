package co.edu.uco.kudos.academico.persistencia;

import co.edu.uco.kudos.academico.dominio.Facultad;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacultadRepositorio extends JpaRepository<Facultad, UUID> {
    List<Facultad> findByActivoTrueOrderByNombreAsc();
}
