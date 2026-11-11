package co.edu.uco.kudos.academico.persistencia;

import co.edu.uco.kudos.academico.dominio.VinculacionUsuarioPrograma;
import co.edu.uco.kudos.academico.dominio.VinculacionUsuarioProgramaId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VinculacionUsuarioProgramaRepositorio extends JpaRepository<VinculacionUsuarioPrograma, VinculacionUsuarioProgramaId> {
    List<VinculacionUsuarioPrograma> findByUsuarioId(UUID usuarioId);
    List<VinculacionUsuarioPrograma> findByProgramaId(UUID programaId);
}
