package co.edu.uco.kudos.seguridad;

import co.edu.uco.kudos.usuario.dominio.EstadoAprobacion;
import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
@ConditionalOnProperty(name = "ucokudos.demostracion.habilitada", havingValue = "true", matchIfMissing = true)
public class CargadorUsuariosDemostracion {

    @Bean
    ApplicationRunner cargarUsuariosDemostracion(
            UsuarioRepositorio usuarios,
            PasswordEncoder codificador,
            @Value("${ucokudos.demostracion.contrasena}") String contrasena) {
        return args -> {
            crearSiNoExiste(usuarios, codificador, "demo.autor@uco.edu.co", "Demo Autor", "+573000000001", contrasena);
            crearSiNoExiste(usuarios, codificador, "demo.destinatario@uco.edu.co", "Demo Destinatario", "+573000000002", contrasena);
        };
    }

    private void crearSiNoExiste(UsuarioRepositorio usuarios, PasswordEncoder codificador,
                                 String correo, String nombre, String celular, String contrasena) {
        if (usuarios.findByCorreoInstitucional(correo).isPresent()) {
            return;
        }
        Instant ahora = Instant.now();
        Usuario usuario = new Usuario(UUID.randomUUID(), correo, celular, nombre,
                codificador.encode(contrasena), RolUsuario.ESTUDIANTE, EstadoAprobacion.APROBADA,
                true, true, true, null, null, ahora, ahora);
        usuarios.save(usuario);
    }
}
