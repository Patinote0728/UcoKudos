package co.edu.uco.kudos.flujo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.uco.kudos.usuario.aplicacion.EntregaCodigoVerificacion;
import co.edu.uco.kudos.usuario.dominio.CanalVerificacion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class FlujoRegistroKudoIntegracionTest {

    private static final String CONTRASENA_DEMO = "ClaveDemoIntegracion2026!";
    private static final String URL_BASE_DATOS = System.getenv().getOrDefault(
            "TEST_DB_URL", "jdbc:postgresql://localhost:5433/ucokudos");
    private static final String USUARIO_BASE_DATOS = System.getenv().getOrDefault("TEST_DB_USERNAME", "ucokudos");
    private static final String CONTRASENA_BASE_DATOS = System.getenv().getOrDefault("TEST_DB_PASSWORD", "ucokudos_local");
    private static final String ESQUEMA = "prueba_integracion_" + UUID.randomUUID().toString().replace("-", "");
    private static final Path RUTA_ADJUNTOS = Path.of(System.getProperty("java.io.tmpdir"),
            "ucokudos-integracion-" + UUID.randomUUID());

    static {
        try (Connection conexion = DriverManager.getConnection(URL_BASE_DATOS, USUARIO_BASE_DATOS, CONTRASENA_BASE_DATOS);
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("CREATE SCHEMA " + ESQUEMA);
        } catch (SQLException error) {
            throw new ExceptionInInitializerError(error);
        }
    }

    @DynamicPropertySource
    static void configurarPropiedades(DynamicPropertyRegistry propiedades) {
        propiedades.add("spring.datasource.url", FlujoRegistroKudoIntegracionTest::urlConEsquema);
        propiedades.add("spring.datasource.username", () -> USUARIO_BASE_DATOS);
        propiedades.add("spring.datasource.password", () -> CONTRASENA_BASE_DATOS);
        propiedades.add("spring.flyway.schemas", () -> ESQUEMA);
        propiedades.add("spring.flyway.default-schema", () -> ESQUEMA);
        propiedades.add("ucokudos.adjuntos.ruta", RUTA_ADJUNTOS::toString);
        propiedades.add("ucokudos.demostracion.contrasena", () -> CONTRASENA_DEMO);
        propiedades.add("ucokudos.notificaciones.reales-habilitadas", () -> false);
    }

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private CodigosCapturados codigos;

    @AfterAll
    static void eliminarRecursosDePrueba() throws SQLException, IOException {
        try (Connection conexion = DriverManager.getConnection(URL_BASE_DATOS, USUARIO_BASE_DATOS, CONTRASENA_BASE_DATOS);
             Statement sentencia = conexion.createStatement()) {
            sentencia.execute("DROP SCHEMA IF EXISTS " + ESQUEMA + " CASCADE");
        }
        if (Files.exists(RUTA_ADJUNTOS)) {
            try (var rutas = Files.walk(RUTA_ADJUNTOS)) {
                for (Path ruta : rutas.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(ruta);
            }
        }
    }

    @Test
    void registraApruebaPublicaAuditaEncolaYEntregaElAdjunto() throws Exception {
        UUID identificadorPrueba = UUID.randomUUID();
        String sufijo = identificadorPrueba.toString().substring(0, 8);
        String correo = "integracion." + sufijo + "@uco.edu.co";
        String celular = "+573" + String.format("%09d",
                Math.floorMod(identificadorPrueba.getLeastSignificantBits(), 1_000_000_000L));
        String contrasena = "ClaveIntegracion2026!";

        JsonNode programas = json(mvc.perform(get("/api/programas-academicos"))
                .andExpect(status().isOk()).andReturn());
        String programaId = encontrar(programas, "nombre", "Programa de Demostración").path("id").asText();

        mvc.perform(post("/api/registros").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "correoInstitucional", correo,
                                "numeroCelular", celular,
                                "nombreCompleto", "Estudiante de Integración",
                                "contrasena", contrasena,
                                "rol", "ESTUDIANTE",
                                "programaId", programaId))))
                .andExpect(status().isCreated());

        String codigoCorreo = codigos.codigo(CanalVerificacion.CORREO);
        String codigoCelular = codigos.codigo(CanalVerificacion.CELULAR);
        mvc.perform(post("/api/registros/verificar").with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "correoInstitucional", correo,
                                "codigoCorreo", codigoCorreo,
                                "codigoCelular", codigoCelular))))
                .andExpect(status().isOk());

        JsonNode pendientes = json(mvc.perform(get("/api/aprobaciones/pendientes")
                .with(httpBasic("demo.programa@uco.edu.co", CONTRASENA_DEMO)))
                .andExpect(status().isOk()).andReturn());
        String solicitudId = encontrar(pendientes, "correoInstitucional", correo).path("id").asText();
        mvc.perform(post("/api/aprobaciones/{id}", solicitudId).with(csrf())
                        .with(httpBasic("demo.programa@uco.edu.co", CONTRASENA_DEMO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"APROBADA\",\"comentario\":\"Aprobación de integración\"}"))
                .andExpect(status().isOk());

        UUID destinatarioId = jdbc.queryForObject(
                "select id from usuario where correo_institucional = ?", UUID.class, "demo.destinatario@uco.edu.co");
        JsonNode categorias = json(mvc.perform(get("/api/categorias-kudos"))
                .andExpect(status().isOk()).andReturn());
        String categoriaId = encontrar(categorias, "nombre", "Empatía").path("id").asText();
        JsonNode kudo = json(mvc.perform(post("/api/kudos").with(csrf())
                        .with(httpBasic(correo, contrasena)).contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(Map.of(
                                "destinatarioId", destinatarioId,
                                "categoriaId", categoriaId,
                                "mensaje", "Gracias por apoyarme durante la integración."))))
                .andExpect(status().isCreated()).andReturn());
        UUID kudoId = UUID.fromString(kudo.path("id").asText());

        assertThat(jdbc.queryForObject("select count(*) from evento_auditoria "
                + "where tipo_evento = 'KUDO_PUBLICADO' and recurso_id = ?", Integer.class, kudoId)).isEqualTo(1);
        assertThat(jdbc.queryForObject("select count(*) from cola_notificacion "
                + "where reconocimiento_kudo_id = ?", Integer.class, kudoId)).isEqualTo(3);
        assertThat(jdbc.queryForObject("select count(distinct canal) from cola_notificacion "
                + "where reconocimiento_kudo_id = ?", Integer.class, kudoId)).isEqualTo(3);

        JsonNode buzón = json(mvc.perform(get("/api/kudos")
                .with(httpBasic("demo.destinatario@uco.edu.co", CONTRASENA_DEMO)))
                .andExpect(status().isOk()).andReturn());
        assertThat(buzón.toString()).contains(kudoId.toString());

        byte[] contenidoPdf = "%PDF-1.7\nPrueba de integración\n".getBytes(StandardCharsets.UTF_8);
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "evidencia.pdf", "application/pdf", contenidoPdf);
        JsonNode adjunto = json(mvc.perform(multipart("/api/kudos/{id}/adjuntos", kudoId)
                        .file(archivo).with(csrf()).with(httpBasic(correo, contrasena)))
                .andExpect(status().isCreated()).andReturn());
        UUID adjuntoId = UUID.fromString(adjunto.path("id").asText());

        MvcResult descarga = mvc.perform(get("/api/kudos/{kudoId}/adjuntos/{adjuntoId}/contenido", kudoId, adjuntoId)
                        .with(httpBasic("demo.destinatario@uco.edu.co", CONTRASENA_DEMO)))
                .andExpect(status().isOk()).andReturn();
        assertThat(descarga.getResponse().getContentAsByteArray()).containsExactly(contenidoPdf);
    }

    private JsonNode json(MvcResult resultado) throws Exception {
        return json.readTree(resultado.getResponse().getContentAsString());
    }

    private JsonNode encontrar(JsonNode arreglo, String campo, String valor) {
        for (JsonNode elemento : arreglo) {
            if (valor.equals(elemento.path(campo).asText())) return elemento;
        }
        throw new AssertionError("No se encontró " + campo + " = " + valor + " en " + arreglo);
    }

    private static String urlConEsquema() {
        return URL_BASE_DATOS + (URL_BASE_DATOS.contains("?") ? "&" : "?") + "currentSchema=" + ESQUEMA;
    }

    @TestConfiguration
    static class ConfiguracionIntegracion {
        @Bean
        @Primary
        CodigosCapturados codigosCapturados() {
            return new CodigosCapturados();
        }
    }

    static class CodigosCapturados implements EntregaCodigoVerificacion {
        private final Map<CanalVerificacion, String> valores = new ConcurrentHashMap<>();

        @Override
        public void enviar(CanalVerificacion canal, String destino, String codigo) {
            valores.put(canal, codigo);
        }

        String codigo(CanalVerificacion canal) {
            String codigo = valores.get(canal);
            if (codigo == null) throw new AssertionError("No se generó el código para " + canal);
            return codigo;
        }
    }
}
