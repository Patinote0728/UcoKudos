package co.edu.uco.kudos.usuario.dominio;

import co.edu.uco.kudos.academico.dominio.ProgramaAcademico;
import co.edu.uco.kudos.academico.dominio.Facultad;
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
@Table(name = "usuario")
public class Usuario {

    @Id
    private UUID id;

    @Column(name = "correo_institucional", nullable = false, unique = true, length = 254)
    private String correoInstitucional;

    @Column(name = "numero_celular", nullable = false, unique = true, length = 32)
    private String numeroCelular;

    @Column(name = "nombre_completo", nullable = false, length = 180)
    private String nombreCompleto;

    @Column(name = "hash_contrasena", nullable = false)
    private String hashContrasena;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 32)
    private RolUsuario rol;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_aprobacion", nullable = false, length = 16)
    private EstadoAprobacion estadoAprobacion;

    @Column(name = "correo_verificado", nullable = false)
    private boolean correoVerificado;

    @Column(name = "celular_verificado", nullable = false)
    private boolean celularVerificado;

    @Column(name = "whatsapp_autorizado", nullable = false)
    private boolean whatsappAutorizado;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programa_id")
    private ProgramaAcademico programa;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    protected Usuario() {
    }

    public Usuario(UUID id, String correoInstitucional, String numeroCelular, String nombreCompleto,
                   String hashContrasena, RolUsuario rol, EstadoAprobacion estadoAprobacion,
                   boolean correoVerificado, boolean celularVerificado, boolean whatsappAutorizado, boolean activo,
                   Facultad facultad, ProgramaAcademico programa, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.correoInstitucional = correoInstitucional;
        this.numeroCelular = numeroCelular;
        this.nombreCompleto = nombreCompleto;
        this.hashContrasena = hashContrasena;
        this.rol = rol;
        this.estadoAprobacion = estadoAprobacion;
        this.correoVerificado = correoVerificado;
        this.celularVerificado = celularVerificado;
        this.whatsappAutorizado = whatsappAutorizado;
        this.activo = activo;
        this.facultad = facultad;
        this.programa = programa;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public String getCorreoInstitucional() { return correoInstitucional; }
    public String getNumeroCelular() { return numeroCelular; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getHashContrasena() { return hashContrasena; }
    public RolUsuario getRol() { return rol; }
    public EstadoAprobacion getEstadoAprobacion() { return estadoAprobacion; }
    public boolean isCorreoVerificado() { return correoVerificado; }
    public boolean isCelularVerificado() { return celularVerificado; }
    public boolean isWhatsappAutorizado() { return whatsappAutorizado; }
    public boolean isActivo() { return activo; }
    public Facultad getFacultad() { return facultad; }
    public ProgramaAcademico getPrograma() { return programa; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }

    public void cambiarAutorizacionWhatsapp(boolean autorizado, Instant actualizadoEn) {
        this.whatsappAutorizado = autorizado;
        this.actualizadoEn = actualizadoEn;
    }

    public void aprobar(Instant instante) {
        aprobar(instante, true);
    }

    public void aprobar(Instant instante, boolean requiereVerificacionCelular) {
        this.estadoAprobacion = EstadoAprobacion.APROBADA;
        actualizarActividad(instante, requiereVerificacionCelular);
    }

    public void rechazar(Instant instante) {
        this.estadoAprobacion = EstadoAprobacion.RECHAZADA;
        this.activo = false;
        this.actualizadoEn = instante;
    }

    public void verificar(CanalVerificacion canal, Instant instante) {
        verificar(canal, instante, true);
    }

    public void verificar(CanalVerificacion canal, Instant instante, boolean requiereVerificacionCelular) {
        if (canal == CanalVerificacion.CORREO) {
            this.correoVerificado = true;
        } else {
            this.celularVerificado = true;
        }
        actualizarActividad(instante, requiereVerificacionCelular);
    }

    private void actualizarActividad(Instant instante) {
        actualizarActividad(instante, true);
    }

    private void actualizarActividad(Instant instante, boolean requiereVerificacionCelular) {
        this.activo = estadoAprobacion == EstadoAprobacion.APROBADA && correoVerificado
                && (!requiereVerificacionCelular || celularVerificado);
        this.actualizadoEn = instante;
    }
}
