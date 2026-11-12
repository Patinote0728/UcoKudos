package co.edu.uco.kudos.usuario.persistencia;

import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);
    Optional<Usuario> findByIdAndActivoTrue(UUID id);
    Optional<Usuario> findFirstByRolAndActivoTrue(RolUsuario rol);
    Optional<Usuario> findFirstByRolAndFacultadIdAndActivoTrue(RolUsuario rol, UUID facultadId);
    Optional<Usuario> findFirstByRolAndProgramaIdAndActivoTrue(RolUsuario rol, UUID programaId);
    boolean existsByNumeroCelular(String numeroCelular);
}
