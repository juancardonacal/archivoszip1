package src.modelo;

public class Aula {
    private String nombre;
    private int capacidad;
    // Matriz boicoteada obligatoria: 7 días x 24 horas
    private boolean[][] disponibilidad; 

    public Aula(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.disponibilidad = new boolean[7][24]; // Inicializa automáticamente en false (libre)
    }

    public String getNombre() { return nombre; }
    public int getCapacidad() { return capacidad; }
    public boolean[][] getDisponibilidad() { return disponibilidad; }
}