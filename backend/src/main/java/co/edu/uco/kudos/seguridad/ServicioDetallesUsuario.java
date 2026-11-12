package co.edu.uco.kudos.seguridad;

import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicioDetallesUsuario implements UserDetailsService {

    private final UsuarioRepositorio usuarios;

    public ServicioDetallesUsuario(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public UserDetails loadUserByUsername(String correoInstitucional) throws UsernameNotFoundException {
        Usuario usuario = usuarios.findByCorreoInstitucional(correoInstitucional)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas."));

        return User.withUsername(usuario.getCorreoInstitucional())
                .password(usuario.getHashContrasena())
                .authorities("ROLE_" + usuario.getRol().name())
                .build();
    }
}
