package src.excepsiones;

public class HorarioConflictivoException extends Exception {
    public HorarioConflictivoException(String mensaje) {
        super(mensaje);
    }
}