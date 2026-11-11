package co.edu.uco.kudos.usuario.persistencia;

import co.edu.uco.kudos.usuario.dominio.Usuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);
    Optional<Usuario> findByIdAndActivoTrue(UUID id);
    boolean existsByNumeroCelular(String numeroCelular);
}
