package co.edu.uco.kudos.seguridad;

import co.edu.uco.kudos.usuario.dominio.EstadoAprobacion;
import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import co.edu.uco.kudos.academico.dominio.Facultad;
import co.edu.uco.kudos.academico.dominio.ProgramaAcademico;
import co.edu.uco.kudos.academico.persistencia.FacultadRepositorio;
import co.edu.uco.kudos.academico.persistencia.ProgramaAcademicoRepositorio;
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
            FacultadRepositorio facultades,
            ProgramaAcademicoRepositorio programas,
            PasswordEncoder codificador,
            @Value("${ucokudos.demostracion.contrasena}") String contrasena) {
        return args -> {
            crearSiNoExiste(usuarios, codificador, "demo.autor@uco.edu.co", "Demo Autor", "+573000000001", contrasena);
            crearSiNoExiste(usuarios, codificador, "demo.destinatario@uco.edu.co", "Demo Destinatario", "+573000000002", contrasena);
            Instant ahora=Instant.now();
            Facultad facultad=facultades.findAll().stream().filter(f->f.getNombre().equals("Facultad de Demostración")).findFirst().orElseGet(()->facultades.save(new Facultad(UUID.fromString("10000000-0000-0000-0000-000000000001"),"Facultad de Demostración",true,ahora)));
            ProgramaAcademico programa=programas.findAll().stream().filter(p->p.getNombre().equals("Programa de Demostración")).findFirst().orElseGet(()->programas.save(new ProgramaAcademico(UUID.fromString("20000000-0000-0000-0000-000000000001"),facultad,"Programa de Demostración",true,ahora)));
            crearRol(usuarios,codificador,"demo.programa@uco.edu.co","Demo Coordinador de Programa","+573000000003",contrasena,RolUsuario.COORDINADOR_PROGRAMA,null,programa);
            crearRol(usuarios,codificador,"demo.facultad@uco.edu.co","Demo Coordinador de Facultad","+573000000004",contrasena,RolUsuario.COORDINADOR_FACULTAD,facultad,null);
            crearRol(usuarios,codificador,"demo.academico@uco.edu.co","Demo Coordinador Académico","+573000000005",contrasena,RolUsuario.COORDINADOR_ACADEMICO,null,null);
            crearRol(usuarios,codificador,"demo.rector@uco.edu.co","Demo Rector","+573000000006",contrasena,RolUsuario.RECTOR,null,null);
            crearRol(usuarios,codificador,"demo.admin@uco.edu.co","Demo Administrador","+573000000007",contrasena,RolUsuario.ADMINISTRADOR_SISTEMA,null,null);
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
                true, true, false, true, null, null, ahora, ahora);
        usuarios.save(usuario);
    }

    private void crearRol(UsuarioRepositorio usuarios, PasswordEncoder codificador, String correo, String nombre, String celular,
                         String contrasena, RolUsuario rol, co.edu.uco.kudos.academico.dominio.Facultad facultad,
                         co.edu.uco.kudos.academico.dominio.ProgramaAcademico programa) {
        if(usuarios.findByCorreoInstitucional(correo).isPresent()) return;
        Instant ahora=Instant.now();
        usuarios.save(new Usuario(UUID.randomUUID(),correo,celular,nombre,codificador.encode(contrasena),rol,
                EstadoAprobacion.APROBADA,true,true,false,true,facultad,programa,ahora,ahora));
    }
}
