package src.modelo;
import src.estructuras.*;

public class Estudiante extends Persona {
    private int semestreActual;
    // Matriz obligatoria de tamaño fijo [10][20] para almacenar las notas
    private Double[][] notas; 
    private ListaEnlazada<Materia> historialMaterias;

    public Estudiante(String nombre, String id, String email, int semestreActual) {
        super(nombre, id, email);
        this.semestreActual = semestreActual;
        // 10 semestres máximo, 20 materias por semestre
        this.notas = new Double[10][20]; 
        this.historialMaterias = new ListaEnlazada<>();
    }

    public int getSemestreActual() { return semestreActual; }
    public void setSemestreActual(int semestreActual) { this.semestreActual = semestreActual; }

    public Double[][] getNotas() { return notas; }
    public ListaEnlazada<Materia> getHistorialMaterias() { return historialMaterias; }

    // Polimorfismo: Sobrescritura del método de la clase padre
    @Override
    public void mostrarInformacion() {
        System.out.println("ID: " + getId());
        System.out.println("Nombre: " + getNombre());
        System.out.println("Email: " + getEmail());
        System.out.println("Semestre Actual: " + semestreActual);
    }
}
