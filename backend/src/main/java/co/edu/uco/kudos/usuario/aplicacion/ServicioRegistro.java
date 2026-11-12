package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.academico.dominio.*;
import co.edu.uco.kudos.academico.persistencia.*;
import co.edu.uco.kudos.auditoria.dominio.EventoAuditoria;
import co.edu.uco.kudos.auditoria.persistencia.EventoAuditoriaRepositorio;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.usuario.api.*;
import co.edu.uco.kudos.usuario.dominio.*;
import co.edu.uco.kudos.usuario.persistencia.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioRegistro {
    private final UsuarioRepositorio usuarios;
    private final FacultadRepositorio facultades;
    private final ProgramaAcademicoRepositorio programas;
    private final VinculacionUsuarioProgramaRepositorio vinculaciones;
    private final SolicitudRegistroRepositorio solicitudes;
    private final CodigoVerificacionRepositorio codigos;
    private final EventoAuditoriaRepositorio auditoria;
    private final PasswordEncoder codificador;
    private final EntregaCodigoVerificacion entrega;
    private final SecureRandom aleatorio = new SecureRandom();

    public ServicioRegistro(UsuarioRepositorio usuarios, FacultadRepositorio facultades,
            ProgramaAcademicoRepositorio programas, VinculacionUsuarioProgramaRepositorio vinculaciones,
            SolicitudRegistroRepositorio solicitudes, CodigoVerificacionRepositorio codigos,
            EventoAuditoriaRepositorio auditoria, PasswordEncoder codificador, EntregaCodigoVerificacion entrega) {
        this.usuarios=usuarios; this.facultades=facultades; this.programas=programas;
        this.vinculaciones=vinculaciones; this.solicitudes=solicitudes; this.codigos=codigos;
        this.auditoria=auditoria; this.codificador=codificador; this.entrega=entrega;
    }

    @Transactional
    public void registrar(RegistrarUsuarioSolicitud s) {
        String correo=s.correoInstitucional().trim().toLowerCase();
        if (usuarios.findByCorreoInstitucional(correo).isPresent() || usuarios.existsByNumeroCelular(s.numeroCelular()))
            throw new ReglaNegocioException("El correo o celular ya está registrado.");
        if (s.contrasena().getBytes(StandardCharsets.UTF_8).length > 72) throw new ReglaNegocioException("La contraseña supera el límite permitido.");
        Facultad facultad=null; ProgramaAcademico programa=null; RolUsuario aprobador;
        if (s.rol()==RolUsuario.ESTUDIANTE || s.rol()==RolUsuario.DOCENTE || s.rol()==RolUsuario.COORDINADOR_PROGRAMA) {
            if (s.programaId()==null) throw new ReglaNegocioException("Debes seleccionar un programa académico.");
            programa=programas.findById(s.programaId()).filter(ProgramaAcademico::isActivo).orElseThrow(()->new ReglaNegocioException("Programa académico inválido."));
            facultad=programa.getFacultad();
        } else if (s.rol()==RolUsuario.COORDINADOR_FACULTAD) {
            if (s.facultadId()==null) throw new ReglaNegocioException("Debes seleccionar una facultad.");
            facultad=facultades.findById(s.facultadId()).filter(Facultad::isActivo).orElseThrow(()->new ReglaNegocioException("Facultad inválida."));
        } else if (s.rol()==RolUsuario.ADMINISTRADOR_SISTEMA) throw new ReglaNegocioException("No se permite el autorregistro de administradores del sistema.");
        aprobador=switch(s.rol()) { case ESTUDIANTE,DOCENTE -> RolUsuario.COORDINADOR_PROGRAMA; case COORDINADOR_PROGRAMA -> RolUsuario.COORDINADOR_FACULTAD; case COORDINADOR_FACULTAD -> RolUsuario.COORDINADOR_ACADEMICO; case COORDINADOR_ACADEMICO -> RolUsuario.RECTOR; case RECTOR -> RolUsuario.ADMINISTRADOR_SISTEMA; default -> throw new ReglaNegocioException("Rol no permitido."); };
        Instant ahora=Instant.now(); UUID id=UUID.randomUUID();
        Usuario u=new Usuario(id,correo,s.numeroCelular(),s.nombreCompleto().trim(),codificador.encode(s.contrasena()),s.rol(),EstadoAprobacion.PENDIENTE,false,false,false,false,
                s.rol()==RolUsuario.COORDINADOR_FACULTAD?facultad:null,
                s.rol()==RolUsuario.COORDINADOR_PROGRAMA?programa:null,ahora,ahora);
        usuarios.save(u);
        if (s.rol()==RolUsuario.ESTUDIANTE || s.rol()==RolUsuario.DOCENTE)
            vinculaciones.save(new VinculacionUsuarioPrograma(u,programa,s.rol()==RolUsuario.ESTUDIANTE?TipoVinculacion.ESTUDIANTE:TipoVinculacion.DOCENTE,ahora));
        solicitudes.save(new SolicitudRegistro(UUID.randomUUID(),u,facultad,programa,aprobador,ahora));
        emitir(u,CanalVerificacion.CORREO,correo,ahora); emitir(u,CanalVerificacion.CELULAR,s.numeroCelular(),ahora);
    }

    private void emitir(Usuario u, CanalVerificacion canal, String destino, Instant ahora) {
        String codigo=String.format("%06d",aleatorio.nextInt(1_000_000));
        codigos.save(new CodigoVerificacion(UUID.randomUUID(),u,canal,codificador.encode(codigo),ahora.plus(10,ChronoUnit.MINUTES),ahora));
        entrega.enviar(canal,destino,codigo);
    }

    @Transactional(noRollbackFor = CodigoVerificacionInvalidoException.class)
    public void verificar(VerificarRegistroSolicitud s) {
        Usuario u=usuarios.findByCorreoInstitucional(s.correoInstitucional().trim().toLowerCase()).orElseThrow(()->new ReglaNegocioException("Registro no encontrado."));
        Instant ahora=Instant.now();
        var correo=codigos.findByUsuarioIdAndCanal(u.getId(),CanalVerificacion.CORREO).orElseThrow(()->new ReglaNegocioException("Código de correo inválido o vencido."));
        var celular=codigos.findByUsuarioIdAndCanal(u.getId(),CanalVerificacion.CELULAR).orElseThrow(()->new ReglaNegocioException("Código de celular inválido o vencido."));
        boolean correoValido=correo.coincide(s.codigoCorreo(),codificador,ahora);
        boolean celularValido=celular.coincide(s.codigoCelular(),codificador,ahora);
        if (!correoValido || !celularValido) throw new CodigoVerificacionInvalidoException();
        correo.marcarUsado(ahora); celular.marcarUsado(ahora);
        u.verificar(CanalVerificacion.CORREO,ahora); u.verificar(CanalVerificacion.CELULAR,ahora);
        auditoria.save(new EventoAuditoria(UUID.randomUUID(),"CORREO_VERIFICADO",u,"USUARIO",u.getId(),ahora));
        auditoria.save(new EventoAuditoria(UUID.randomUUID(),"CELULAR_VERIFICADO",u,"USUARIO",u.getId(),ahora));
    }
}
