package co.edu.uco.kudos.reconocimiento.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "adjunto_kudo")
public class AdjuntoKudo {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reconocimiento_kudo_id", nullable = false)
    private Kudo kudo;
    @Column(name = "clave_almacenamiento", nullable = false, unique = true, length = 512)
    private String claveAlmacenamiento;
    @Column(name = "nombre_archivo_original", nullable = false, length = 255)
    private String nombreArchivoOriginal;
    @Column(name = "tipo_contenido", nullable = false, length = 150)
    private String tipoContenido;
    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;
    @Column(name = "suma_verificacion_sha256", nullable = false, length = 64, columnDefinition = "char(64)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String sumaVerificacionSha256;
    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected AdjuntoKudo() { }

    public AdjuntoKudo(UUID id, Kudo kudo, String claveAlmacenamiento, String nombreArchivoOriginal,
            String tipoContenido, long tamanoBytes, String sumaVerificacionSha256, Instant creadoEn) {
        this.id=id; this.kudo=kudo; this.claveAlmacenamiento=claveAlmacenamiento;
        this.nombreArchivoOriginal=nombreArchivoOriginal; this.tipoContenido=tipoContenido;
        this.tamanoBytes=tamanoBytes; this.sumaVerificacionSha256=sumaVerificacionSha256; this.creadoEn=creadoEn;
    }
    public UUID getId() { return id; }
    public Kudo getKudo() { return kudo; }
    public String getClaveAlmacenamiento() { return claveAlmacenamiento; }
    public String getNombreArchivoOriginal() { return nombreArchivoOriginal; }
    public String getTipoContenido() { return tipoContenido; }
    public long getTamanoBytes() { return tamanoBytes; }
    public String getSumaVerificacionSha256() { return sumaVerificacionSha256; }
    public Instant getCreadoEn() { return creadoEn; }
}
