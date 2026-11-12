package co.edu.uco.kudos.notificacion.dominio;

import co.edu.uco.kudos.reconocimiento.dominio.Kudo;
import co.edu.uco.kudos.usuario.dominio.Usuario;
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
@Table(name = "cola_notificacion")
public class ColaNotificacion {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reconocimiento_kudo_id", nullable = false, updatable = false)
    private Kudo kudo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false, updatable = false)
    private Usuario destinatario;

    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 20, updatable = false)
    private CanalNotificacion canal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoNotificacion estado;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "proximo_intento_en", nullable = false)
    private Instant proximoIntentoEn;

    @Column(name = "bloqueado_hasta")
    private Instant bloqueadoHasta;

    @Column(name = "ultimo_error", length = 1000)
    private String ultimoError;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    @Column(name = "completado_en")
    private Instant completadoEn;

    protected ColaNotificacion() {
    }

    public ColaNotificacion(UUID id, Kudo kudo, Usuario destinatario, CanalNotificacion canal, Instant ahora) {
        this.id = id;
        this.kudo = kudo;
        this.destinatario = destinatario;
        this.canal = canal;
        this.estado = EstadoNotificacion.PENDIENTE;
        this.intentos = 0;
        this.proximoIntentoEn = ahora;
        this.creadoEn = ahora;
        this.actualizadoEn = ahora;
    }

    public void marcarProcesando(Instant ahora, Instant vencimientoBloqueo) {
        this.estado = EstadoNotificacion.PROCESANDO;
        this.intentos++;
        this.bloqueadoHasta = vencimientoBloqueo;
        this.actualizadoEn = ahora;
    }

    public void marcarCompletada(EstadoNotificacion estadoFinal, Instant ahora) {
        this.estado = estadoFinal;
        this.bloqueadoHasta = null;
        this.ultimoError = null;
        this.completadoEn = ahora;
        this.actualizadoEn = ahora;
    }

    public void programarReintento(Instant siguienteIntento, String error, Instant ahora) {
        this.estado = EstadoNotificacion.REINTENTAR;
        this.proximoIntentoEn = siguienteIntento;
        this.bloqueadoHasta = null;
        this.ultimoError = error;
        this.actualizadoEn = ahora;
    }

    public void marcarFallida(String error, Instant ahora) {
        this.estado = EstadoNotificacion.FALLIDA;
        this.bloqueadoHasta = null;
        this.ultimoError = error;
        this.completadoEn = ahora;
        this.actualizadoEn = ahora;
    }

    public UUID getId() { return id; }
    public Kudo getKudo() { return kudo; }
    public Usuario getDestinatario() { return destinatario; }
    public CanalNotificacion getCanal() { return canal; }
    public int getIntentos() { return intentos; }
}
