package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.auditoria.dominio.EventoAuditoria;
import co.edu.uco.kudos.auditoria.persistencia.EventoAuditoriaRepositorio;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.usuario.api.*;
import co.edu.uco.kudos.usuario.dominio.*;
import co.edu.uco.kudos.usuario.persistencia.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioAprobaciones {
    private final UsuarioRepositorio usuarios; private final SolicitudRegistroRepositorio solicitudes; private final EventoAuditoriaRepositorio auditoria;
    public ServicioAprobaciones(UsuarioRepositorio u,SolicitudRegistroRepositorio s,EventoAuditoriaRepositorio a){usuarios=u;solicitudes=s;auditoria=a;}
    @Transactional(readOnly=true)
    public List<AprobacionRespuesta> pendientes(String correo) {
        Usuario actor=actor(correo);
        return solicitudes.findByEstadoAndRolAprobadorOrderByCreadoEnAsc(EstadoAprobacion.PENDIENTE,actor.getRol()).stream()
                .filter(s -> actor.getRol()!=RolUsuario.COORDINADOR_PROGRAMA || (s.getPrograma()!=null && actor.getPrograma()!=null && s.getPrograma().getId().equals(actor.getPrograma().getId())))
                .filter(s -> actor.getRol()!=RolUsuario.COORDINADOR_FACULTAD || (s.getFacultad()!=null && actor.getFacultad()!=null && s.getFacultad().getId().equals(actor.getFacultad().getId())))
                .filter(s -> !s.getUsuario().getId().equals(actor.getId())).map(AprobacionRespuesta::desde).toList();
    }
    @Transactional
    public void decidir(UUID id,DecidirAprobacionSolicitud dto,String correo) {
        Usuario actor=actor(correo); SolicitudRegistro s=solicitudes.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Solicitud no encontrada."));
        if(s.getEstado()!=EstadoAprobacion.PENDIENTE) throw new ReglaNegocioException("La solicitud ya fue decidida.");
        if(s.getRolAprobador()!=actor.getRol() || (actor.getRol()==RolUsuario.COORDINADOR_PROGRAMA && (actor.getPrograma()==null || s.getPrograma()==null || !actor.getPrograma().getId().equals(s.getPrograma().getId())))
                || (actor.getRol()==RolUsuario.COORDINADOR_FACULTAD && (actor.getFacultad()==null || s.getFacultad()==null || !actor.getFacultad().getId().equals(s.getFacultad().getId())))
                || actor.getId().equals(s.getUsuario().getId())) throw new ReglaNegocioException("No tienes autorización para decidir esta solicitud.");
        if(dto.decision()==EstadoAprobacion.PENDIENTE) throw new ReglaNegocioException("La decisión debe ser APROBADA o RECHAZADA.");
        Instant ahora=Instant.now(); s.decidir(actor,dto.decision(),dto.comentario(),ahora);
        if(dto.decision()==EstadoAprobacion.APROBADA) s.getUsuario().aprobar(ahora); else s.getUsuario().rechazar(ahora);
        auditoria.save(new EventoAuditoria(UUID.randomUUID(),"SOLICITUD_REGISTRO_"+dto.decision(),actor,"SOLICITUD_REGISTRO",id,ahora));
    }
    private Usuario actor(String correo){return usuarios.findByCorreoInstitucional(correo).filter(Usuario::isActivo).orElseThrow(()->new ReglaNegocioException("Usuario aprobador inactivo."));}
}
