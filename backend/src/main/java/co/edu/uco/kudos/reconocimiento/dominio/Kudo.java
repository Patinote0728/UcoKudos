package co.edu.uco.kudos.reconocimiento.dominio;

import co.edu.uco.kudos.usuario.dominio.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reconocimiento_kudo")
public class Kudo {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_kudo_id", nullable = false)
    private CategoriaKudo categoria;

    @Column(name = "mensaje", nullable = false, columnDefinition = "text")
    private String mensaje;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected Kudo() {
    }

    public Kudo(UUID id, Usuario autor, Usuario destinatario, CategoriaKudo categoria,
                String mensaje, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.autor = autor;
        this.destinatario = destinatario;
        this.categoria = categoria;
        this.mensaje = mensaje;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public Usuario getAutor() { return autor; }
    public Usuario getDestinatario() { return destinatario; }
    public CategoriaKudo getCategoria() { return categoria; }
    public String getMensaje() { return mensaje; }
    public Instant getCreadoEn() { return creadoEn; }
}
