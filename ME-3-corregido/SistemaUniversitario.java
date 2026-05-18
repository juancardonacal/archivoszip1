import src.modelo.*;
import src.excepsiones.*;
import src.utilidades.CampusRouter;
import src.estructuras.*;

import java.util.*;

public class SistemaUniversitario {
    // Almacenamiento rápido exigido por la guía
    private HashMap<String, Estudiante> mapaEstudiantes = new HashMap<>();
    private TreeMap<String, Aula> mapaAulas = new TreeMap<>();
    private ListaEnlazada<Materia> listaMaterias = new ListaEnlazada<>();
    
    // Matriz estática fija de facultades
    private String[] arregloFacultades = new String[]{"Ingeniería", "Medicina", "Derecho", "Ciencias", "Artes"};
    
    // Grafo de conectividad del campus (5 edificios mínimos requeridos)
    private String[] nombresEdificios = {"Ingeniería", "Biblioteca", "Cafetería", "Rectoría", "Laboratorios"};
    private int[][] matrizDistanciasCampus = new int[5][5];

    // Pilas del sistema de control transaccional e informes
    private MiPilaDatos<Operacion> pilaDeshacer = new MiPilaDatos<>();
    private MiPilaDatos<Operacion> pilaRehacer = new MiPilaDatos<>();
    private MiPilaDatos<String> pilaHistorialReportes = new MiPilaDatos<>();

    public static void main(String[] args) {
        SistemaUniversitario sistema = new SistemaUniversitario();
        sistema.inicializarGrafoBase();
        sistema.ejecutarMenu();
    }

    private void inicializarGrafoBase() {
        // Conexiones de prueba predeterminadas para simulación de caminos
        matrizDistanciasCampus[0][1] = 100; // Ingeniería <-> Biblioteca
        matrizDistanciasCampus[1][0] = 100;
        matrizDistanciasCampus[0][2] = 150; // Ingeniería <-> Cafetería
        matrizDistanciasCampus[2][0] = 150;
        matrizDistanciasCampus[2][3] = 180; // Cafetería <-> Rectoría
        matrizDistanciasCampus[3][2] = 180;
        matrizDistanciasCampus[1][4] = 200; // Biblioteca <-> Laboratorios
        matrizDistanciasCampus[4][1] = 200;
        matrizDistanciasCampus[4][3] = 90;  // Laboratorios <-> Rectoría
        matrizDistanciasCampus[3][4] = 90;
    }

    public void ejecutarMenu() {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n============================================================");
            System.out.println(" PLANIFICACIÓN ACADÉMICA - SISTEMA UNIVERSITARIO");
            System.out.println("============================================================");
            System.out.println("=== GENERADOR DE PRUEBAS ===");
            System.out.println("0. Cargar simulación masiva aleatoria (Datos de Prueba)");
            System.out.println("=== GESTIÓN DE ESTUDIANTES ===");
            System.out.println("1. Registrar estudiante");
            System.out.println("2. Buscar estudiante por ID");
            System.out.println("3. Listar todos los estudiantes");
            System.out.println("4. Eliminar estudiante");
            System.out.println("=== GESTIÓN DE MATERIAS ===");
            System.out.println("5. Crear materia");
            System.out.println("6. Agregar pre-requisito");
            System.out.println("7. Mostrar pre-requisitos");
            System.out.println("8. Inscribir estudiante");
            System.out.println("9. Cancelar inscripción");
            System.out.println("10. Mostrar cola de espera");
            System.out.println("=== GESTIÓN DE HORARIOS ===");
            System.out.println("11. Reservar horario en aula");
            System.out.println("12. Liberar horario");
            System.out.println("13. Consultar disponibilidad");
            System.out.println("=== RUTAS ENTRE EDIFICIOS ===");
            System.out.println("14. Agregar conexión entre edificios");
            System.out.println("15. Calcular ruta más corta");
            System.out.println("=== REPORTES ACADÉMICOS ===");
            System.out.println("16. Registrar nota");
            System.out.println("17. Ver reporte académico");
            System.out.println("18. Navegador de reportes (Atrás)");
            System.out.println("=== SISTEMA DESHACER/REHACER ===");
            System.out.println("19. Deshacer última operación");
            System.out.println("20. Rehacer última operación");
            System.out.println("=== PROCESAMIENTO POR LOTES ===");
            System.out.println("21. Procesar bloque masivo (Batch simulado)");
            System.out.println("22. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = sc.nextInt();
                sc.nextLine(); // Limpiar buffer
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Ingrese un número del menú.");
                sc.nextLine();
                continue;
            }

            switch (opcion) {
                case 0:
                    generarDatosAleatoriosDePrueba();
                    break;

                // ─── CASO 1: Registrar estudiante ───────────────────────────
                case 1:
                    try {
                        System.out.print("ID: ");
                        String id = sc.nextLine();
                        System.out.print("Nombre: ");
                        String nom = sc.nextLine();
                        System.out.print("Email: ");
                        String em = sc.nextLine();
                        System.out.print("Semestre (1-10): ");
                        int sem = sc.nextInt();
                        sc.nextLine();
                        if (sem < 1 || sem > 10) {
                            System.out.println("Error: el semestre debe estar entre 1 y 10. Regresando al menú.");
                            break;
                        }
                        mapaEstudiantes.put(id, new Estudiante(nom, id, em, sem));
                        System.out.println("Estudiante registrado exitosamente.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese un número entero para el semestre. Regresando al menú.");
                        sc.nextLine();
                    }
                    break;

                // ─── CASO 2: Buscar estudiante ───────────────────────────────
                case 2:
                    System.out.print("ID del estudiante a buscar: ");
                    String bId = sc.nextLine();
                    try {
                        Estudiante est2 = buscarEstudiante(bId);
                        est2.mostrarInformacion();
                    } catch (EstudianteNoEncontradoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                // ─── CASO 3: Listar estudiantes ──────────────────────────────
                case 3:
                    System.out.println("\n--- LISTA DE ESTUDIANTES ---");
                    for (Estudiante e : mapaEstudiantes.values()) {
                        e.mostrarInformacion();
                        System.out.println("--------------------");
                    }
                    break;

                // ─── CASO 4: Eliminar estudiante ─────────────────────────────
                case 4:
                    System.out.print("ID del estudiante a dar de baja: ");
                    String delId = sc.nextLine();
                    try {
                        Estudiante eDel = buscarEstudiante(delId);
                        mapaEstudiantes.remove(delId);
                        pilaDeshacer.push(new Operacion("ELIMINAR", eDel, null));
                        pilaRehacer.clear();
                        System.out.println("Estudiante eliminado. Operación registrada en pila para deshacer.");
                    } catch (EstudianteNoEncontradoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                // ─── CASO 5: Crear materia ────────────────────────────────────
                case 5:
                    try {
                        System.out.print("Código Materia: ");
                        String cMat = sc.nextLine();
                        System.out.print("Nombre: ");
                        String nMat = sc.nextLine();
                        System.out.print("Cupos Máximos: ");
                        int cupMax = sc.nextInt();
                        System.out.print("Créditos: ");
                        int cred = sc.nextInt();
                        sc.nextLine();
                        if (cupMax < 0 || cred < 0) {
                            System.out.println("Error: cupos y créditos deben ser valores positivos. Regresando al menú.");
                            break;
                        }
                        listaMaterias.agregar(new Materia(cMat, nMat, cupMax, cred));
                        System.out.println("Materia creada de forma exitosa.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese valores numéricos para cupos y créditos. Regresando al menú.");
                        sc.nextLine();
                    }
                    break;

                // ─── CASO 6: Agregar prerrequisito ───────────────────────────
                case 6:
                    System.out.print("Código Materia Objetivo: ");
                    String cObj = sc.nextLine();
                    System.out.print("Código del Prerrequisito a añadir: ");
                    String cPre = sc.nextLine();
                    Materia obj = encontrarMateria(cObj);
                    Materia prereq = encontrarMateria(cPre);
                    if (obj != null && prereq != null) {
                        obj.agregarPreRequisito(prereq);
                        System.out.println("Prerrequisito asignado.");
                    } else {
                        System.out.println("Una o ambas materias no existen.");
                    }
                    break;

                // ─── CASO 7: Mostrar prerrequisitos ──────────────────────────
                case 7:
                    System.out.print("Código Materia: ");
                    String cVer = sc.nextLine();
                    Materia mVer = encontrarMateria(cVer);
                    if (mVer != null) {
                        System.out.println("Prerrequisitos de " + mVer.getNombre() + ":");
                        for (Materia pr : mVer.getPreRequisitos()) {
                            System.out.println("- " + pr.getCodigo() + ": " + pr.getNombre());
                        }
                    } else {
                        System.out.println("Materia no encontrada.");
                    }
                    break;

                // ─── CASO 8: Inscribir estudiante ────────────────────────────
                case 8:
                    System.out.print("ID Estudiante: ");
                    String insEst = sc.nextLine();
                    System.out.print("Código Materia: ");
                    String insMat = sc.nextLine();
                    try {
                        inscribirEstudianteEnMateria(insEst, insMat, true);
                    } catch (Exception e) {
                        System.out.println("Aviso del sistema: " + e.getMessage());
                    }
                    break;

                // ─── CASO 9: Cancelar inscripción ────────────────────────────
                case 9:
                    System.out.print("ID Estudiante: ");
                    String cancEst = sc.nextLine();
                    System.out.print("Código Materia: ");
                    String cancMat = sc.nextLine();
                    cancelarInscripcion(cancEst, cancMat, true);
                    break;

                // ─── CASO 10: Mostrar cola de espera ─────────────────────────
                case 10:
                    System.out.print("Código Materia: ");
                    String colMat = sc.nextLine();
                    Materia mCol = encontrarMateria(colMat);
                    if (mCol != null) {
                        System.out.println("Cola de espera para: " + mCol.getNombre());
                        int pos = 1;
                        for (Estudiante estCola : mCol.getColaEspera()) {
                            System.out.println("Posición " + pos + ": " + estCola.getNombre());
                            pos++;
                        }
                        System.out.println("Total en espera: " + mCol.getColaEspera().size());
                    } else {
                        System.out.println("Materia no encontrada.");
                    }
                    break;

                // ─── CASOS 11 y 12: Reservar / Liberar horario ───────────────
                case 11:
                    gestionarReservaAula(sc, true);
                    break;
                case 12:
                    gestionarReservaAula(sc, false);
                    break;

                // ─── CASO 13: Consultar disponibilidad ───────────────────────
                case 13:
                    try {
                        System.out.print("Nombre Aula: ");
                        String aulaNom = sc.nextLine();
                        Aula au = mapaAulas.get(aulaNom);
                        if (au != null) {
                            System.out.print("Día (0=Dom, 1=Lun, 2=Mar, 3=Mie, 4=Jue, 5=Vie, 6=Sab): ");
                            int d = sc.nextInt();
                            System.out.print("Hora (0-23): ");
                            int h = sc.nextInt();
                            sc.nextLine();
                            if (d < 0 || d > 6 || h < 0 || h > 23) {
                                System.out.println("Error: día debe ser 0-6 y hora debe ser 0-23. Regresando al menú.");
                                break;
                            }
                            System.out.println("Estado: " + (au.getDisponibilidad()[d][h] ? "OCUPADO" : "LIBRE"));
                        } else {
                            System.out.println("Aula no encontrada.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese valores numéricos para día y hora. Regresando al menú.");
                        sc.nextLine();
                    }
                    break;

                // ─── CASO 14: Agregar conexión entre edificios ───────────────
                case 14:
                    try {
                        System.out.print("Edificio Origen (0-4): ");
                        int o = sc.nextInt();
                        System.out.print("Edificio Destino (0-4): ");
                        int destNode = sc.nextInt();
                        System.out.print("Distancia en metros: ");
                        int dist = sc.nextInt();
                        sc.nextLine();
                        if (o < 0 || o > 4 || destNode < 0 || destNode > 4 || dist < 0) {
                            System.out.println("Error: edificios deben ser 0-4 y la distancia debe ser positiva. Regresando al menú.");
                            break;
                        }
                        matrizDistanciasCampus[o][destNode] = dist;
                        matrizDistanciasCampus[destNode][o] = dist;
                        System.out.println("Conexión del campus establecida.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese únicamente valores numéricos. Regresando al menú.");
                        sc.nextLine();
                    }
                    break;

                // ─── CASO 15: Calcular ruta más corta ────────────────────────
                case 15:
                    try {
                        System.out.print("Edificio Origen (0 a 4):\n[0:Ingeniería, 1:Biblioteca, 2:Cafetería, 3:Rectoría, 4:Laboratorios]\nSelección: ");
                        int orig = sc.nextInt();
                        System.out.print("Edificio Destino (0 a 4): ");
                        int dNode = sc.nextInt();
                        sc.nextLine();
                        if (orig < 0 || orig > 4 || dNode < 0 || dNode > 4) {
                            System.out.println("Error: seleccione edificios entre 0 y 4. Regresando al menú.");
                            break;
                        }
                        CampusRouter.calcularRutaMasCorta(matrizDistanciasCampus, orig, dNode, nombresEdificios);
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese un número entero para el edificio. Regresando al menú.");
                        sc.nextLine();
                    }
                    break;

                // ─── CASO 16: Registrar nota ──────────────────────────────────
                case 16:
                    try {
                        System.out.print("ID Estudiante: ");
                        String nEst = sc.nextLine();
                        System.out.print("Semestre (1-10): ");
                        int nSem = sc.nextInt();
                        System.out.print("Índice de Materia (1-20): ");
                        int nInd = sc.nextInt();
                        System.out.print("Nota (0.0 a 5.0): ");
                        double notaVal = sc.nextDouble();
                        sc.nextLine();
                        if (nSem < 1 || nSem > 10 || nInd < 1 || nInd > 20 || notaVal < 0.0 || notaVal > 5.0) {
                            System.out.println("Error: semestre 1-10, índice 1-20, nota 0.0-5.0. Regresando al menú.");
                            break;
                        }
                        Estudiante eNota = buscarEstudiante(nEst);
                        Double notaAnterior = eNota.getNotas()[nSem - 1][nInd - 1];
                        eNota.getNotas()[nSem - 1][nInd - 1] = notaVal;
                        // Se pasa notaAnterior Y notaVal para que el rehacer pueda restaurar el valor nuevo
                        pilaDeshacer.push(new Operacion("NOTA", eNota, nSem, nInd, notaAnterior, notaVal));
                        pilaRehacer.clear();
                        System.out.println("Nota guardada correctamente.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese valores numéricos válidos. Regresando al menú.");
                        sc.nextLine();
                    } catch (EstudianteNoEncontradoException e) {
                        System.out.println("Error operativo: " + e.getMessage());
                    }
                    break;

                // ─── CASO 17: Ver reporte académico ──────────────────────────
                case 17:
                    System.out.print("ID Estudiante para el informe: ");
                    String repId = sc.nextLine();
                    generarReporteAcademico(repId);
                    break;

                // ─── CASO 18: Navegador atrás ─────────────────────────────────
                case 18:
                    navegarAtrasReporte();
                    break;

                // ─── CASO 19: Deshacer ────────────────────────────────────────
                case 19:
                    try {
                        ejecutarDeshacer();
                    } catch (PilaDeshacerVaciaException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                // ─── CASO 20: Rehacer ─────────────────────────────────────────
                case 20:
                    ejecutarRehacer();
                    break;

                // ─── CASO 21: Procesamiento por lotes ────────────────────────
                case 21:
                    procesarLoteSujetoSimulado();
                    break;

                case 22:
                    System.out.println("Finalizando la ejecución del sistema de gestión.");
                    break;

                default:
                    System.out.println("Opción incorrecta. Seleccione un número del menú.");
            }
        } while (opcion != 22);
    }

    // ═══════════════════════════════════════════════════════════════
    //  MÉTODOS LÓGICOS DEL NEGOCIO ACADÉMICO
    // ═══════════════════════════════════════════════════════════════

    private Estudiante buscarEstudiante(String id) throws EstudianteNoEncontradoException {
        Estudiante e = mapaEstudiantes.get(id);
        if (e == null) throw new EstudianteNoEncontradoException("No existe estudiante con ID: " + id);
        return e;
    }

    private Materia encontrarMateria(String codigo) {
        for (Materia m : listaMaterias) {
            if (m.getCodigo().equalsIgnoreCase(codigo)) return m;
        }
        return null;
    }

    private void inscribirEstudianteEnMateria(String idEst, String codMat, boolean registrarEnPila)
            throws EstudianteNoEncontradoException, PreRequisitoNoAprobadoException, CupoLlenoException {

        Estudiante est = buscarEstudiante(idEst);
        Materia mat = encontrarMateria(codMat);
        if (mat == null) {
            System.out.println("Materia inexistente.");
            return;
        }

        // Validación de prerrequisitos obligatorios
        for (Materia pre : mat.getPreRequisitos()) {
            boolean aprobado = false;
            for (Materia cursada : est.getHistorialMaterias()) {
                if (cursada.getCodigo().equalsIgnoreCase(pre.getCodigo())) {
                    aprobado = true;
                    break;
                }
            }
            if (!aprobado) {
                throw new PreRequisitoNoAprobadoException("PreRequisitoNoAprobadoException - Falta cursar: " + pre.getNombre());
            }
        }

        // Verificación de disponibilidad de cupos
        if (mat.getCuposDisponibles() <= 0) {
            mat.getColaEspera().encolar(est);
            throw new CupoLlenoException("CupoLlenoException - " + mat.getNombre() + " llena. Agregado a cola de espera.");
        }

        mat.setCuposDisponibles(mat.getCuposDisponibles() - 1);
        est.getHistorialMaterias().agregar(mat);

        if (registrarEnPila) {
            pilaDeshacer.push(new Operacion("INSCRIBIR", est, mat));
            pilaRehacer.clear();
        }
        System.out.println("Inscripción exitosa del estudiante " + est.getNombre() + " en " + mat.getNombre());
    }

    private void cancelarInscripcion(String idEst, String codMat, boolean registrarEnPila) {
        try {
            Estudiante est = buscarEstudiante(idEst);
            Materia mat = encontrarMateria(codMat);
            if (mat == null) {
                System.out.println("Materia inexistente.");
                return;
            }

            if (est.getHistorialMaterias().remove(mat)) {
                mat.setCuposDisponibles(mat.getCuposDisponibles() + 1);
                System.out.println("Cancelación exitosa para " + est.getNombre());

                if (registrarEnPila) {
                    pilaDeshacer.push(new Operacion("CANCELAR", est, mat));
                    pilaRehacer.clear();
                }

                // Asignación automática desde la cola de espera
                if (!mat.getColaEspera().estaVacia()) {
                    Estudiante siguiente = mat.getColaEspera().desencolar();
                    System.out.println("Cupo liberado asignado automáticamente a: " + siguiente.getNombre());
                    mat.setCuposDisponibles(mat.getCuposDisponibles() - 1);
                    siguiente.getHistorialMaterias().agregar(mat);
                }
            } else {
                System.out.println("El estudiante no estaba inscrito en esa materia.");
            }
        } catch (Exception e) {
            System.out.println("Error en proceso de cancelación: " + e.getMessage());
        }
    }

    private void gestionarReservaAula(Scanner sc, boolean reservar) {
        try {
            System.out.print("Nombre Aula: ");
            String aNom = sc.nextLine();
            Aula aula = mapaAulas.get(aNom);
            if (aula == null) {
                System.out.println("El aula indicada no se encuentra en los registros.");
                return;
            }
            System.out.print("Día (0=Dom, 1=Lun, 2=Mar, 3=Mie, 4=Jue, 5=Vie, 6=Sab): ");
            int d = sc.nextInt();
            System.out.print("Hora de inicio (0-23): ");
            int h = sc.nextInt();
            System.out.print("Duración en horas: ");
            int dur = sc.nextInt();
            sc.nextLine();

            if (d < 0 || d > 6 || h < 0 || h > 23 || dur < 1 || (h + dur - 1) > 23) {
                System.out.println("Error: día 0-6, hora 0-23, y la duración no debe superar las 24h del día. Regresando al menú.");
                return;
            }

            for (int i = 0; i < dur; i++) {
                if (reservar && aula.getDisponibilidad()[d][h + i]) {
                    throw new HorarioConflictivoException("HorarioConflictivoException - El bloque de hora " + (h + i) + " está reservado.");
                }
            }
            for (int i = 0; i < dur; i++) {
                aula.getDisponibilidad()[d][h + i] = reservar;
            }
            System.out.println("Operación en matriz horaria ejecutada con éxito.");

        } catch (InputMismatchException e) {
            System.out.println("Error: ingrese valores numéricos para día, hora y duración. Regresando al menú.");
            sc.nextLine();
        } catch (HorarioConflictivoException e) {
            System.out.println("Conflicto: " + e.getMessage());
        }
    }

    private void generarReporteAcademico(String idEst) {
        try {
            Estudiante e = buscarEstudiante(idEst);
            StringBuilder sb = new StringBuilder();
            sb.append("--- REPORTE ACADÉMICO ---\n");
            sb.append("Estudiante: ").append(e.getNombre()).append(" (ID: ").append(e.getId()).append(")\n");

            double sumaAcumulada = 0;
            int totalMateriasConNota = 0;
            int reprobadas = 0;
            int aprobadas = 0;

            for (int i = 0; i < 10; i++) {
                double sumaSemestre = 0;
                int materiasSemestre = 0;
                StringBuilder sbSem = new StringBuilder();

                for (int j = 0; j < 20; j++) {
                    Double nota = e.getNotas()[i][j];
                    if (nota != null) {
                        sbSem.append("  Materia [").append(j + 1).append("]: ").append(nota).append("\n");
                        sumaSemestre += nota;
                        materiasSemestre++;
                        sumaAcumulada += nota;
                        totalMateriasConNota++;
                        if (nota < 3.0) reprobadas++;
                        else aprobadas++;
                    }
                }
                if (materiasSemestre > 0) {
                    sb.append("Semestre ").append(i + 1).append(":\n").append(sbSem);
                    sb.append("  Promedio Semestre: ").append(String.format("%.2f", (sumaSemestre / materiasSemestre))).append("\n");
                }
            }

            double promAcumulado = totalMateriasConNota > 0 ? (sumaAcumulada / totalMateriasConNota) : 0.0;
            sb.append("=== RESUMEN ===\n");
            sb.append("Promedio acumulado: ").append(String.format("%.2f", promAcumulado)).append("\n");
            sb.append("Materias aprobadas: ").append(aprobadas).append("\n");
            sb.append("Materias reprobadas: ").append(reprobadas).append("\n");

            String reporteFinal = sb.toString();
            System.out.println(reporteFinal);

            // Guardar en la pila para simular el botón "Atrás"
            pilaHistorialReportes.push(reporteFinal);

        } catch (EstudianteNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    private void navegarAtrasReporte() {
        if (pilaHistorialReportes.size() > 1) {
            pilaHistorialReportes.pop(); // Saca el actual
            System.out.println("--- REPORTE ANTERIOR EN EL HISTORIAL ---");
            System.out.println(pilaHistorialReportes.peek());
        } else if (pilaHistorialReportes.size() == 1) {
            System.out.println("Se encuentra en el primer reporte consultado. No hay más historial.");
        } else {
            System.out.println("El historial de navegación se encuentra vacío.");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  LÓGICA TRANSACCIONAL DE PILAS (DESHACER / REHACER)
    // ═══════════════════════════════════════════════════════════════

    private void ejecutarDeshacer() throws PilaDeshacerVaciaException {
        if (pilaDeshacer.estaVacia()) {
            throw new PilaDeshacerVaciaException("PilaDeshacerVaciaException - No hay operaciones para deshacer.");
        }
        Operacion op = pilaDeshacer.pop();
        pilaRehacer.push(op);

        switch (op.getTipo()) {
            case "INSCRIBIR":
                op.getEstudiante().getHistorialMaterias().remove(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() + 1);
                System.out.println("Deshecho: Se retiró la inscripción de " + op.getEstudiante().getNombre() + " en " + op.getMateria().getNombre());
                break;
            case "CANCELAR":
                op.getEstudiante().getHistorialMaterias().agregar(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() - 1);
                System.out.println("Deshecho: Se restauró la inscripción de " + op.getEstudiante().getNombre() + " en " + op.getMateria().getNombre());
                break;
            case "NOTA":
                op.getEstudiante().getNotas()[op.getSemestre() - 1][op.getIndiceNota() - 1] = op.getNotaAnterior();
                System.out.println("Deshecho: Se restauró la nota previa del estudiante.");
                break;
            case "ELIMINAR":
                mapaEstudiantes.put(op.getEstudiante().getId(), op.getEstudiante());
                System.out.println("Deshecho: Se dio de alta nuevamente al estudiante dado de baja.");
                break;
        }
    }

    private void ejecutarRehacer() {
        // CORRECCIÓN: se usaba isEmpty() que no existe en MiPilaDatos; el método correcto es estaVacia()
        if (pilaRehacer.estaVacia()) {
            System.out.println("Error: No hay operaciones en la pila de rehacer.");
            return;
        }
        Operacion op = pilaRehacer.pop();
        pilaDeshacer.push(op);

        switch (op.getTipo()) {
            case "INSCRIBIR":
                // CORRECCIÓN: agregar(null);(op.getMateria()) → agregar(op.getMateria())
                op.getEstudiante().getHistorialMaterias().agregar(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() - 1);
                System.out.println("Rehecho: Inscripción confirmada.");
                break;
            case "CANCELAR":
                // CORRECCIÓN: .getHistorialMaterias().(op.getMateria()) → .getHistorialMaterias().remove(op.getMateria())
                op.getEstudiante().getHistorialMaterias().remove(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() + 1);
                System.out.println("Rehecho: Cancelación confirmada.");
                break;
            case "NOTA":
                // CORRECCIÓN: ahora restaura el valor nuevo usando getNotaNueva() (campo agregado a Operacion)
                op.getEstudiante().getNotas()[op.getSemestre() - 1][op.getIndiceNota() - 1] = op.getNotaNueva();
                System.out.println("Rehecho: Cambio de nota aplicado nuevamente.");
                break;
            case "ELIMINAR":
                mapaEstudiantes.remove(op.getEstudiante().getId());
                System.out.println("Rehecho: Eliminación aplicada de nuevo.");
                break;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PROCESAMIENTO POR LOTES
    // ═══════════════════════════════════════════════════════════════

    private void procesarLoteSujetoSimulado() {
        System.out.println("--- PROCESAMIENTO MASIVO (BATCH SIMULADO) ---");
        // Formato simulación: idEstudiante,codigoMateria
        String[] lineasSimuladasCSV = {
            "EST001,MATE01",
            "EST002,MATE01",
            "EST003,MATE01",
            "EST004,MATE01", // Debe ir a cola por falta de cupos en la simulación
            "EST005,MATE02"
        };

        Queue<String> colaBatch = new LinkedList<>();
        for (String linea : lineasSimuladasCSV) {
            colaBatch.add(linea);
        }

        System.out.println("Se cargaron " + colaBatch.size() + " solicitudes en la cola batch.");
        int exitosas = 0, fallidas = 0;

        while (!colaBatch.isEmpty()) {
            String registro = colaBatch.poll();
            String[] campos = registro.split(",");
            if (campos.length < 2) continue;

            String idB = campos[0];
            String codB = campos[1];

            try {
                inscribirEstudianteEnMateria(idB, codB, false);
                System.out.println(" [" + idB + " -> " + codB + "] -> Exitosa");
                exitosas++;
            } catch (Exception e) {
                System.out.println(" [" + idB + " -> " + codB + "] -> Fallida (" + e.getClass().getSimpleName() + ")");
                fallidas++;
            }
        }

        System.out.println("=== RESUMEN PROCESAMIENTO ===");
        System.out.println("Exitosas: " + exitosas);
        System.out.println("Fallidas: " + fallidas);
    }

    // ═══════════════════════════════════════════════════════════════
    //  GENERADOR AUTOMÁTICO DE DATOS ALEATORIOS PARA PRUEBAS
    // ═══════════════════════════════════════════════════════════════

    private void generarDatosAleatoriosDePrueba() {
        System.out.println("\nEjecutando generador de datos aleatorios...");

        // 1. Población de Aulas en el TreeMap
        mapaAulas.put("Aula 101", new Aula("Aula 101", 30));
        mapaAulas.put("Aula 202", new Aula("Aula 202", 40));
        mapaAulas.put("Laboratorio Alfa", new Aula("Laboratorio Alfa", 25));

        // 2. Población de Materias
        Materia m1 = new Materia("MATE01", "Cálculo I", 3, 4); // Cupo bajo a propósito para probar la cola de espera
        Materia m2 = new Materia("MATE02", "Cálculo II", 15, 4);
        Materia m3 = new Materia("PROG01", "Estructura de Datos", 20, 3);

        m2.agregarPreRequisito(m1); // Requisito lógico para pruebas de excepción

        listaMaterias.agregar(m1);
        listaMaterias.agregar(m2);
        listaMaterias.agregar(m3);

        // 3. Generación masiva de Estudiantes aleatorios
        String[] nombresEjemplo = {"Mateo", "Valentina", "Santiago", "Camila", "Nicolas", "Isabella", "Lucas", "Gabriela"};
        String[] apellidosEjemplo = {"Gómez", "Rodríguez", "López", "Martínez", "Pérez", "García", "Castro", "Zapata"};
        Random rnd = new Random();

        for (int i = 1; i <= 10; i++) {
            String idGen = "EST00" + i;
            String nombreCompleto = nombresEjemplo[rnd.nextInt(nombresEjemplo.length)] + " " + apellidosEjemplo[rnd.nextInt(apellidosEjemplo.length)];
            String emailGen = nombreCompleto.toLowerCase().replace(" ", ".") + "@universidad.edu";
            int semestreGen = rnd.nextInt(5) + 1;

            Estudiante est = new Estudiante(nombreCompleto, idGen, emailGen, semestreGen);

            // Carga aleatoria de notas en el arreglo estático Double[10][20]
            for (int s = 0; s < semestreGen; s++) {
                int materiasEnSemestre = rnd.nextInt(3) + 2; // entre 2 y 4 materias asignadas
                for (int m = 0; m < materiasEnSemestre; m++) {
                    double notaAleatoria = 1.5 + (rnd.nextDouble() * 3.5); // Notas entre 1.5 y 5.0
                    est.getNotas()[s][m] = Math.round(notaAleatoria * 10.0) / 10.0;
                }
            }

            // Para los estudiantes pares, simular que ya aprobaron Cálculo I
            if (i % 2 == 0) {
                est.getHistorialMaterias().agregar(m1);
            }

            mapaEstudiantes.put(idGen, est);
        }

        System.out.println("¡Operación exitosa! Se han cargado 3 aulas, 3 materias académicas y 10 estudiantes con notas e historial inicializados de forma aleatoria.");
    }
}
