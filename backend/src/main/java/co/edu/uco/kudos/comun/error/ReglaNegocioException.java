package co.edu.uco.kudos.comun.error;

public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
