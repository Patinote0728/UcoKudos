package co.edu.uco.kudos.usuario.persistencia;

import co.edu.uco.kudos.usuario.dominio.EstadoAprobacion;
import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import co.edu.uco.kudos.usuario.dominio.SolicitudRegistro;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRegistroRepositorio extends JpaRepository<SolicitudRegistro, UUID> {
    List<SolicitudRegistro> findByEstadoAndRolAprobadorOrderByCreadoEnAsc(
            EstadoAprobacion estado, RolUsuario rolAprobador);
}
