package src.modelo;

public class Operacion {
    private String tipo; // "INSCRIBIR", "CANCELAR", "NOTA", "ELIMINAR"
    private Estudiante estudiante;
    private Materia materia;
    private int semestre;
    private int indiceNota;
    private Double notaAnterior;
    private Double notaNueva; // Necesario para que ejecutarRehacer() pueda restaurar la nota correcta

    public Operacion(String tipo, Estudiante estudiante, Materia materia) {
        this.tipo = tipo;
        this.estudiante = estudiante;
        this.materia = materia;
    }

    // Constructor principal para operaciones de nota (incluye notaNueva)
    public Operacion(String tipo, Estudiante estudiante, int semestre, int indiceNota, Double notaAnterior, Double notaNueva) {
        this.tipo = tipo;
        this.estudiante = estudiante;
        this.semestre = semestre;
        this.indiceNota = indiceNota;
        this.notaAnterior = notaAnterior;
        this.notaNueva = notaNueva;
    }

    // Constructor de compatibilidad (sin notaNueva) para no romper código existente
    public Operacion(String tipo, Estudiante estudiante, int semestre, int indiceNota, Double notaAnterior) {
        this(tipo, estudiante, semestre, indiceNota, notaAnterior, null);
    }

    public String getTipo() { return tipo; }
    public Estudiante getEstudiante() { return estudiante; }
    public Materia getMateria() { return materia; }
    public int getSemestre() { return semestre; }
    public int getIndiceNota() { return indiceNota; }
    public Double getNotaAnterior() { return notaAnterior; }
    public Double getNotaNueva() { return notaNueva; }
}
