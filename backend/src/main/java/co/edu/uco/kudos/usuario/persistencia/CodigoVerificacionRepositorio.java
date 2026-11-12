package co.edu.uco.kudos.usuario.persistencia;

import co.edu.uco.kudos.usuario.dominio.CanalVerificacion;
import co.edu.uco.kudos.usuario.dominio.CodigoVerificacion;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodigoVerificacionRepositorio extends JpaRepository<CodigoVerificacion, UUID> {
    Optional<CodigoVerificacion> findByUsuarioIdAndCanal(UUID usuarioId, CanalVerificacion canal);
}
