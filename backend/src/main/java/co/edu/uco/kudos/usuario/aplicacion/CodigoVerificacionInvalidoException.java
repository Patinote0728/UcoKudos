package co.edu.uco.kudos.usuario.aplicacion;

import co.edu.uco.kudos.comun.error.ReglaNegocioException;

public class CodigoVerificacionInvalidoException extends ReglaNegocioException {
    public CodigoVerificacionInvalidoException() { super("Código de verificación inválido, vencido o bloqueado."); }
}
