package co.edu.uco.kudos.comun.error;

public class SolicitudInvalidaException extends RuntimeException {
    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
