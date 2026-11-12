package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.usuario.dominio.CanalVerificacion;

public interface EntregaCodigoVerificacion {
    void enviar(CanalVerificacion canal, String destino, String codigo);
}
