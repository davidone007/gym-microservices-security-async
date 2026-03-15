package co.analisys.pagos.service;

public class PagoFailedException extends RuntimeException {
    public PagoFailedException(String message) {
        super(message);
    }

    public PagoFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
