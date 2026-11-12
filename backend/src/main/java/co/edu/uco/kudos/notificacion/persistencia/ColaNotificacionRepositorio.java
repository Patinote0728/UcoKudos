package co.edu.uco.kudos.notificacion.persistencia;

import co.edu.uco.kudos.notificacion.dominio.ColaNotificacion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColaNotificacionRepositorio extends JpaRepository<ColaNotificacion, UUID> {

    @Query(value = """
            SELECT *
            FROM cola_notificacion
            WHERE (
                estado IN ('PENDIENTE', 'REINTENTAR')
                OR (estado = 'PROCESANDO' AND bloqueado_hasta <= CURRENT_TIMESTAMP)
            )
              AND proximo_intento_en <= CURRENT_TIMESTAMP
            ORDER BY creado_en
            LIMIT :limite
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<ColaNotificacion> reclamarPendientes(@Param("limite") int limite);
}
