package co.edu.uco.kudos.reconocimiento.persistencia;

import co.edu.uco.kudos.reconocimiento.dominio.AdjuntoKudo;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdjuntoKudoRepositorio extends JpaRepository<AdjuntoKudo, UUID> {
    List<AdjuntoKudo> findByKudoIdOrderByCreadoEnAsc(UUID kudoId);
    Optional<AdjuntoKudo> findByIdAndKudoId(UUID id, UUID kudoId);
    long countByKudoId(UUID kudoId);
}
