package co.edu.uco.kudos.reconocimiento.persistencia;

import co.edu.uco.kudos.reconocimiento.dominio.Kudo;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KudoRepositorio extends JpaRepository<Kudo, UUID> {
    Page<Kudo> findByDestinatarioIdOrderByCreadoEnDesc(UUID destinatarioId, Pageable pageable);
    Page<Kudo> findByAutorIdOrderByCreadoEnDesc(UUID autorId, Pageable pageable);
}
