package co.edu.uco.kudos.reconocimiento.aplicacion;

import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.comun.error.SolicitudInvalidaException;
import co.edu.uco.kudos.reconocimiento.api.CrearKudoSolicitud;
import co.edu.uco.kudos.reconocimiento.api.KudoRespuesta;
import co.edu.uco.kudos.reconocimiento.api.PaginaRespuesta;
import co.edu.uco.kudos.reconocimiento.dominio.CategoriaKudo;
import co.edu.uco.kudos.reconocimiento.dominio.Kudo;
import co.edu.uco.kudos.reconocimiento.persistencia.CategoriaKudoRepositorio;
import co.edu.uco.kudos.reconocimiento.persistencia.KudoRepositorio;
import co.edu.uco.kudos.auditoria.dominio.EventoAuditoria;
import co.edu.uco.kudos.auditoria.persistencia.EventoAuditoriaRepositorio;
import co.edu.uco.kudos.notificacion.aplicacion.ServicioColaNotificaciones;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioKudos {

    private static final int TAMANO_MAXIMO_PAGINA = 50;

    private final KudoRepositorio kudos;
    private final UsuarioRepositorio usuarios;
    private final CategoriaKudoRepositorio categorias;
    private final EventoAuditoriaRepositorio auditoria;
    private final ServicioColaNotificaciones colaNotificaciones;

    public ServicioKudos(KudoRepositorio kudos, UsuarioRepositorio usuarios,
                         CategoriaKudoRepositorio categorias, EventoAuditoriaRepositorio auditoria,
                         ServicioColaNotificaciones colaNotificaciones) {
        this.kudos = kudos;
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.auditoria = auditoria;
        this.colaNotificaciones = colaNotificaciones;
    }

    @Transactional
    public KudoRespuesta publicar(CrearKudoSolicitud solicitud, String correoAutor) {
        Usuario autor = usuarios.findByCorreoInstitucional(correoAutor)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró un autor activo para la sesión."));
        Usuario destinatario = usuarioActivo(solicitud.destinatarioId(), "destinatario");
        CategoriaKudo categoria = categorias.findById(solicitud.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la categoría indicada."));

        if (!categoria.isActivo()) {
            throw new ReglaNegocioException("No se puede publicar un Kudo en una categoría inactiva.");
        }

        Instant ahora = Instant.now();
        Kudo kudo = new Kudo(UUID.randomUUID(), autor, destinatario, categoria,
                solicitud.mensaje().trim(), ahora, ahora);
        Kudo guardado = kudos.save(kudo);
        auditoria.save(new EventoAuditoria(UUID.randomUUID(), "KUDO_PUBLICADO", autor,
                "KUDO", guardado.getId(), ahora));
        colaNotificaciones.programar(guardado, ahora);
        return convertir(guardado);
    }

    @Transactional(readOnly = true)
    public PaginaRespuesta<KudoRespuesta> listarPropios(String correoDestinatario, int pagina, int tamano) {
        if (pagina < 0) {
            throw new SolicitudInvalidaException("El número de página no puede ser negativo.");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO_PAGINA) {
            throw new SolicitudInvalidaException("El tamaño de página debe estar entre 1 y " + TAMANO_MAXIMO_PAGINA + ".");
        }

        Usuario destinatario = usuarios.findByCorreoInstitucional(correoDestinatario)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario activo para la sesión."));
        Page<KudoRespuesta> resultados = kudos
                .findByDestinatarioIdOrderByCreadoEnDesc(destinatario.getId(), PageRequest.of(pagina, tamano))
                .map(this::convertir);
        return new PaginaRespuesta<>(resultados.getContent(), resultados.getNumber(), resultados.getSize(),
                resultados.getTotalElements(), resultados.getTotalPages());
    }

    private Usuario usuarioActivo(UUID id, String tipo) {
        return usuarios.findById(id)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un " + tipo + " activo con el identificador indicado."));
    }

    private KudoRespuesta convertir(Kudo kudo) {
        return new KudoRespuesta(kudo.getId(), kudo.getAutor().getId(), kudo.getAutor().getNombreCompleto(),
                kudo.getDestinatario().getId(), kudo.getDestinatario().getNombreCompleto(),
                kudo.getCategoria().getId(), kudo.getCategoria().getNombre(), kudo.getMensaje(), kudo.getCreadoEn());
    }
}
