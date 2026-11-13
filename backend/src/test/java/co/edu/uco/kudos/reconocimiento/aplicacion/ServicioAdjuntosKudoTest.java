package co.edu.uco.kudos.reconocimiento.aplicacion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.uco.kudos.comun.error.ReglaNegocioException;
import co.edu.uco.kudos.reconocimiento.dominio.AdjuntoKudo;
import co.edu.uco.kudos.reconocimiento.dominio.Kudo;
import co.edu.uco.kudos.reconocimiento.infraestructura.AlmacenamientoAdjuntos;
import co.edu.uco.kudos.reconocimiento.infraestructura.AlmacenamientoAdjuntosLocal;
import co.edu.uco.kudos.reconocimiento.persistencia.AdjuntoKudoRepositorio;
import co.edu.uco.kudos.reconocimiento.persistencia.KudoRepositorio;
import co.edu.uco.kudos.usuario.dominio.EstadoAprobacion;
import co.edu.uco.kudos.usuario.dominio.RolUsuario;
import co.edu.uco.kudos.usuario.dominio.Usuario;
import co.edu.uco.kudos.usuario.persistencia.UsuarioRepositorio;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class ServicioAdjuntosKudoTest {
    private static final String CORREO_AUTOR = "autor@uco.edu.co";
    private static final UUID ID_KUDO = UUID.randomUUID();

    @Mock private AdjuntoKudoRepositorio adjuntos;
    @Mock private KudoRepositorio kudos;
    @Mock private UsuarioRepositorio usuarios;
    @TempDir Path directorioTemporal;

    private Usuario autor;
    private Usuario destinatario;
    private Kudo kudo;
    private AlmacenamientoAdjuntos almacenamiento;
    private ServicioAdjuntosKudo servicio;

    @BeforeEach
    void preparar() {
        Instant ahora = Instant.now();
        autor = usuario(UUID.randomUUID(), CORREO_AUTOR);
        destinatario = usuario(UUID.randomUUID(), "destinatario@uco.edu.co");
        kudo = new Kudo(ID_KUDO, autor, destinatario, null, "Gracias por tu apoyo", ahora, ahora);
        almacenamiento = new AlmacenamientoAdjuntosLocal(directorioTemporal.toString());
        servicio = new ServicioAdjuntosKudo(adjuntos, kudos, usuarios, almacenamiento);

        when(kudos.findById(ID_KUDO)).thenReturn(Optional.of(kudo));
    }

    @Test
    void guardaUnPdfAutorizadoFueraDeLosRecursosPublicos() throws Exception {
        byte[] contenido = "%PDF-1.7 ejemplo".getBytes();
        MockMultipartFile archivo = new MockMultipartFile("archivo", "reconocimiento.pdf",
                "application/pdf", contenido);
        when(usuarios.findByCorreoInstitucional(CORREO_AUTOR)).thenReturn(Optional.of(autor));
        when(adjuntos.countByKudoId(ID_KUDO)).thenReturn(0L);
        when(adjuntos.save(any(AdjuntoKudo.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        var respuesta = servicio.guardar(ID_KUDO, CORREO_AUTOR, archivo);

        ArgumentCaptor<AdjuntoKudo> captor = ArgumentCaptor.forClass(AdjuntoKudo.class);
        verify(adjuntos).save(captor.capture());
        AdjuntoKudo guardado = captor.getValue();
        assertEquals("reconocimiento.pdf", respuesta.nombreArchivo());
        assertEquals("application/pdf", respuesta.tipoContenido());
        assertArrayEquals(contenido, Files.readAllBytes(
                almacenamiento.resolver(guardado.getClaveAlmacenamiento())));
    }

    @Test
    void impideQueElDestinatarioAdjunteArchivosAlKudoDeOtraPersona() {
        when(usuarios.findByCorreoInstitucional("destinatario@uco.edu.co"))
                .thenReturn(Optional.of(destinatario));
        MockMultipartFile archivo = new MockMultipartFile("archivo", "prueba.png",
                "image/png", new byte[]{(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10});

        assertThrows(ReglaNegocioException.class,
                () -> servicio.guardar(ID_KUDO, "destinatario@uco.edu.co", archivo));

        verify(adjuntos, never()).save(any(AdjuntoKudo.class));
    }

    @Test
    void rechazaArchivosQueNoSeanPdfPngOJpg() {
        when(adjuntos.countByKudoId(ID_KUDO)).thenReturn(0L);
        when(usuarios.findByCorreoInstitucional(CORREO_AUTOR)).thenReturn(Optional.of(autor));
        MockMultipartFile archivo = new MockMultipartFile("archivo", "notas.txt",
                "text/plain", "contenido no permitido".getBytes());

        assertThrows(ReglaNegocioException.class,
                () -> servicio.guardar(ID_KUDO, CORREO_AUTOR, archivo));

        verify(adjuntos, never()).save(any(AdjuntoKudo.class));
    }

    @Test
    void limitaCadaKudoATresArchivos() {
        when(adjuntos.countByKudoId(ID_KUDO)).thenReturn(3L);
        when(usuarios.findByCorreoInstitucional(CORREO_AUTOR)).thenReturn(Optional.of(autor));
        MockMultipartFile archivo = new MockMultipartFile("archivo", "prueba.png",
                "image/png", new byte[]{(byte) 0x89, 'P', 'N', 'G', 13, 10, 26, 10});

        assertThrows(ReglaNegocioException.class,
                () -> servicio.guardar(ID_KUDO, CORREO_AUTOR, archivo));

        verify(adjuntos, never()).save(any(AdjuntoKudo.class));
    }

    private Usuario usuario(UUID id, String correo) {
        Instant ahora = Instant.now();
        return new Usuario(id, correo, "+573000000001", correo, "hash", RolUsuario.ESTUDIANTE,
                EstadoAprobacion.APROBADA, true, true, false, true, null, null, ahora, ahora);
    }
}
