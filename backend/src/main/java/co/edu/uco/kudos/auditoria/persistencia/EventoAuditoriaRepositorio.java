package co.edu.uco.kudos.auditoria.persistencia;

import co.edu.uco.kudos.auditoria.dominio.EventoAuditoria;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoAuditoriaRepositorio extends JpaRepository<EventoAuditoria, UUID> {
}
