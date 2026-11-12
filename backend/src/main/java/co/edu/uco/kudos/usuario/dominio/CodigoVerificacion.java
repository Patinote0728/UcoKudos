package co.edu.uco.kudos.usuario.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "codigo_verificacion")
public class CodigoVerificacion {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, updatable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 16, updatable = false)
    private CanalVerificacion canal;

    @Column(name = "hash_codigo", nullable = false, length = 100)
    private String hashCodigo;

    @Column(name = "expira_en", nullable = false)
    private Instant expiraEn;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "usado_en")
    private Instant usadoEn;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    protected CodigoVerificacion() {
    }

    public CodigoVerificacion(UUID id, Usuario usuario, CanalVerificacion canal, String hashCodigo,
                              Instant expiraEn, Instant creadoEn) {
        this.id = id;
        this.usuario = usuario;
        this.canal = canal;
        this.hashCodigo = hashCodigo;
        this.expiraEn = expiraEn;
        this.creadoEn = creadoEn;
    }

    public boolean puedeIntentar(Instant ahora) {
        return usadoEn == null && expiraEn.isAfter(ahora) && intentos < 5;
    }

    public boolean coincide(String codigo, org.springframework.security.crypto.password.PasswordEncoder codificador, Instant ahora) {
        if (!puedeIntentar(ahora)) return false;
        intentos++;
        return codificador.matches(codigo, hashCodigo);
    }

    public void marcarUsado(Instant ahora) { usadoEn = ahora; }

    public Usuario getUsuario() { return usuario; }
    public CanalVerificacion getCanal() { return canal; }
}
