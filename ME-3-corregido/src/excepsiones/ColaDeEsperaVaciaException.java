package src.excepsiones;

public class ColaDeEsperaVaciaException extends Exception {
    public ColaDeEsperaVaciaException(String mensaje) {
        super(mensaje);
    }
}