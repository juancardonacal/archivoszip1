package src.modelo;
import src.estructuras.*;


public class Materia {
    private String codigo;
    private String nombre;
    private int cuposMaximos;
    private int cuposDisponibles;
    private int creditos;
    private ListaEnlazada<Materia> preRequisitos;
    private MiColaDatos<Estudiante> colaEspera;

    public Materia(String codigo, String nombre, int cuposMaximos, int creditos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cuposMaximos = cuposMaximos;
        this.cuposDisponibles = cuposMaximos;
        this.creditos = creditos;
        this.preRequisitos = new ListaEnlazada<>();
        // Cola enlazada propia para atender estudiantes en espera por orden de llegada.
        this.colaEspera = new MiColaDatos<>();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getCuposMaximos() { return cuposMaximos; }
    public int getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(int cupos) { this.cuposDisponibles = cupos; }
    public int getCreditos() { return creditos; }
    
    public ListaEnlazada<Materia> getPreRequisitos() { 
        return preRequisitos; 
    }
    public MiColaDatos<Estudiante> getColaEspera() { return colaEspera; }

    public void agregarPreRequisito(Materia m) {
        this.preRequisitos.agregar(m);
    }
}
