package co.edu.uco.kudos.notificacion.persistencia;

import co.edu.uco.kudos.notificacion.dominio.DispositivoPush;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispositivoPushRepositorio extends JpaRepository<DispositivoPush, UUID> {
    Optional<DispositivoPush> findByIdentificadorInstalacionFirebase(String identificador);
    List<DispositivoPush> findByUsuarioIdAndActivoTrue(UUID usuarioId);
}
