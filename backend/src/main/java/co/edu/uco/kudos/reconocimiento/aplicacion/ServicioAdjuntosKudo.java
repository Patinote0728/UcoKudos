package co.edu.uco.kudos.reconocimiento.aplicacion;

import co.edu.uco.kudos.comun.error.RecursoNoEncontradoException;
import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.reconocimiento.api.AdjuntoKudoRespuesta;
import co.edu.uco.kudos.reconocimiento.dominio.AdjuntoKudo;
import co.edu.uco.kudos.reconocimiento.dominio.Kudo;
import co.edu.uco.kudos.reconocimiento.infraestructura.AlmacenamientoAdjuntos;
import co.edu.uco.kudos.reconocimiento.persistencia.AdjuntoKudoRepositorio;
import co.edu.uco.kudos.reconocimiento.persistencia.KudoRepositorio;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicioAdjuntosKudo {
    private static final long TAMANO_MAXIMO = 5 * 1024 * 1024;
    private static final int MAXIMO_POR_KUDO = 3;
    private final AdjuntoKudoRepositorio adjuntos;
    private final KudoRepositorio kudos;
    private final UsuarioRepositorio usuarios;
    private final AlmacenamientoAdjuntos almacenamiento;

    public ServicioAdjuntosKudo(AdjuntoKudoRepositorio adjuntos, KudoRepositorio kudos,
            UsuarioRepositorio usuarios, AlmacenamientoAdjuntos almacenamiento) {
        this.adjuntos=adjuntos; this.kudos=kudos; this.usuarios=usuarios; this.almacenamiento=almacenamiento;
    }

    @Transactional
    public AdjuntoKudoRespuesta guardar(UUID kudoId, String correo, MultipartFile archivo) {
        Usuario actor = usuarioActivo(correo);
        Kudo kudo = buscarKudo(kudoId);
        if (!kudo.getAutor().getId().equals(actor.getId()))
            throw new ReglaNegocioException("Solo quien publicó el Kudo puede adjuntar archivos.");
        if (adjuntos.countByKudoId(kudoId) >= MAXIMO_POR_KUDO)
            throw new ReglaNegocioException("Cada Kudo admite hasta tres archivos.");
        if (archivo.isEmpty() || archivo.getSize() > TAMANO_MAXIMO)
            throw new ReglaNegocioException("El archivo debe tener contenido y pesar máximo 5 MB.");
        try {
            byte[] contenido = archivo.getBytes();
            String tipo = detectarTipo(contenido);
            String clave = almacenamiento.guardar(contenido, tipo);
            try {
                AdjuntoKudo adjunto = new AdjuntoKudo(UUID.randomUUID(), kudo, clave,
                        limpiarNombre(archivo.getOriginalFilename()), tipo, contenido.length,
                        sha256(contenido), Instant.now());
                return AdjuntoKudoRespuesta.desde(adjuntos.save(adjunto));
            } catch (RuntimeException fallo) {
                almacenamiento.eliminar(clave);
                throw fallo;
            }
        } catch (IOException fallo) {
            throw new ReglaNegocioException("No se pudo leer o guardar el archivo.");
        }
    }

    @Transactional(readOnly = true)
    public List<AdjuntoKudoRespuesta> listar(UUID kudoId, String correo) {
        Usuario actor=usuarioActivo(correo); Kudo kudo=buscarKudo(kudoId); validarLectura(kudo, actor);
        return adjuntos.findByKudoIdOrderByCreadoEnAsc(kudoId).stream().map(AdjuntoKudoRespuesta::desde).toList();
    }

    @Transactional(readOnly = true)
    public DescargaAdjunto descargar(UUID kudoId, UUID adjuntoId, String correo) {
        Usuario actor=usuarioActivo(correo); Kudo kudo=buscarKudo(kudoId); validarLectura(kudo, actor);
        AdjuntoKudo adjunto=adjuntos.findByIdAndKudoId(adjuntoId,kudoId)
                .orElseThrow(()->new RecursoNoEncontradoException("No se encontró el archivo adjunto."));
        try {
            Resource recurso=almacenamiento.resolver(adjunto.getClaveAlmacenamiento());
            if (!recurso.exists() || !recurso.isReadable()) throw new RecursoNoEncontradoException("El archivo adjunto ya no está disponible.");
            return new DescargaAdjunto(adjunto.getNombreArchivoOriginal(),adjunto.getTipoContenido(),recurso);
        } catch (IOException fallo) {
            throw new RecursoNoEncontradoException("El archivo adjunto ya no está disponible.");
        }
    }

    private String detectarTipo(byte[] b) {
        if (b.length >= 5 && b[0]=='%' && b[1]=='P' && b[2]=='D' && b[3]=='F' && b[4]=='-') return "application/pdf";
        if (b.length >= 8 && (b[0]&255)==0x89 && b[1]=='P' && b[2]=='N' && b[3]=='G') return "image/png";
        if (b.length >= 3 && (b[0]&255)==0xff && (b[1]&255)==0xd8 && (b[2]&255)==0xff) return "image/jpeg";
        throw new ReglaNegocioException("Solo se permiten archivos PDF, PNG o JPG.");
    }

    private String limpiarNombre(String nombre) {
        String seguro=(nombre==null?"archivo":nombre).replace('\\','/').replaceAll("[\\p{Cntrl}]", "");
        seguro=seguro.substring(seguro.lastIndexOf('/')+1).trim();
        if (seguro.isBlank()) seguro="archivo";
        return seguro.length()>255?seguro.substring(0,255):seguro;
    }
    private String sha256(byte[] bytes) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (NoSuchAlgorithmException imposible) { throw new IllegalStateException(imposible); }
    }
    private Usuario usuarioActivo(String correo) {
        return usuarios.findByCorreoInstitucional(correo).filter(Usuario::isActivo)
                .orElseThrow(()->new RecursoNoEncontradoException("No se encontró el usuario activo."));
    }
    private Kudo buscarKudo(UUID id) {
        return kudos.findById(id).orElseThrow(()->new RecursoNoEncontradoException("No se encontró el Kudo."));
    }
    private void validarLectura(Kudo kudo, Usuario actor) {
        if (!kudo.getAutor().getId().equals(actor.getId()) && !kudo.getDestinatario().getId().equals(actor.getId()))
            throw new ReglaNegocioException("No tienes autorización para consultar estos archivos.");
    }
    public record DescargaAdjunto(String nombre, String tipo, Resource recurso) { }
}
