import src.modelo.*;
import src.excepsiones.*;
import src.utilidades.CampusRouter;
import src.estructuras.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SistemaUniversitario {
    // Almacenamiento rÃ¡pido exigido por la guÃ­a
    private HashMap<String, Estudiante> mapaEstudiantes = new HashMap<>();
    private TreeMap<String, Aula> mapaAulas = new TreeMap<>();
    private ListaEnlazada<Materia> listaMaterias = new ListaEnlazada<>();
    
    // Matriz estÃ¡tica fija de facultades
    private String[] arregloFacultades = new String[]{"IngenierÃ­a", "Medicina", "Derecho", "Ciencias", "Artes"};
    
    // Grafo de conectividad del campus (5 edificios mÃ­nimos requeridos)
    private String[] nombresEdificios = {"IngenierÃ­a", "Biblioteca", "CafeterÃ­a", "RectorÃ­a", "Laboratorios"};
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
        // Conexiones de prueba predeterminadas para simulaciÃ³n de caminos
        matrizDistanciasCampus[0][1] = 100; // IngenierÃ­a <-> Biblioteca
        matrizDistanciasCampus[1][0] = 100;
        matrizDistanciasCampus[0][2] = 150; // IngenierÃ­a <-> CafeterÃ­a
        matrizDistanciasCampus[2][0] = 150;
        matrizDistanciasCampus[2][3] = 180; // CafeterÃ­a <-> RectorÃ­a
        matrizDistanciasCampus[3][2] = 180;
        matrizDistanciasCampus[1][4] = 200; // Biblioteca <-> Laboratorios
        matrizDistanciasCampus[4][1] = 200;
        matrizDistanciasCampus[4][3] = 90;  // Laboratorios <-> RectorÃ­a
        matrizDistanciasCampus[3][4] = 90;
    }

    public void ejecutarMenu() {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n============================================================");
            System.out.println(" PLANIFICACIÃ“N ACADÃ‰MICA - SISTEMA UNIVERSITARIO");
            System.out.println("============================================================");
            System.out.println("=== GENERADOR DE PRUEBAS ===");
            System.out.println("0. Cargar simulaciÃ³n masiva aleatoria (Datos de Prueba)");
            System.out.println("=== GESTIÃ“N DE ESTUDIANTES ===");
            System.out.println("1. Registrar estudiante");
            System.out.println("2. Buscar estudiante por ID");
            System.out.println("3. Listar todos los estudiantes");
            System.out.println("4. Eliminar estudiante");
            System.out.println("=== GESTIÃ“N DE MATERIAS ===");
            System.out.println("5. Crear materia");
            System.out.println("6. Agregar pre-requisito");
            System.out.println("7. Mostrar pre-requisitos");
            System.out.println("8. Inscribir estudiante");
            System.out.println("9. Cancelar inscripciÃ³n");
            System.out.println("10. Mostrar cola de espera");
            System.out.println("=== GESTIÃ“N DE HORARIOS ===");
            System.out.println("11. Reservar horario en aula");
            System.out.println("12. Liberar horario");
            System.out.println("13. Consultar disponibilidad");
            System.out.println("=== RUTAS ENTRE EDIFICIOS ===");
            System.out.println("14. Agregar conexiÃ³n entre edificios");
            System.out.println("15. Calcular ruta mÃ¡s corta");
            System.out.println("=== REPORTES ACADÃ‰MICOS ===");
            System.out.println("16. Registrar nota");
            System.out.println("17. Ver reporte acadÃ©mico");
            System.out.println("18. Navegador de reportes (AtrÃ¡s)");
            System.out.println("=== SISTEMA DESHACER/REHACER ===");
            System.out.println("19. Deshacer Ãºltima operaciÃ³n");
            System.out.println("20. Rehacer Ãºltima operaciÃ³n");
            System.out.println("=== PROCESAMIENTO POR LOTES ===");
            System.out.println("21. Procesar archivo CSV de inscripciones (Batch)");
            System.out.println("22. Salir");
            System.out.print("Seleccione una opciÃ³n: ");

            try {
                opcion = sc.nextInt();
                sc.nextLine(); // Limpiar buffer
            } catch (InputMismatchException e) {
                System.out.println("Entrada invÃ¡lida. Ingrese un nÃºmero del menÃº.");
                sc.nextLine();
                continue;
            }

            switch (opcion) {
                case 0:
                    generarDatosAleatoriosDePrueba();
                    break;

                // â”€â”€â”€ CASO 1: Registrar estudiante â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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
                            System.out.println("Error: el semestre debe estar entre 1 y 10. Regresando al menÃº.");
                            break;
                        }
                        mapaEstudiantes.put(id, new Estudiante(nom, id, em, sem));
                        System.out.println("Estudiante registrado exitosamente.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese un nÃºmero entero para el semestre. Regresando al menÃº.");
                        sc.nextLine();
                    }
                    break;

                // â”€â”€â”€ CASO 2: Buscar estudiante â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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

                // â”€â”€â”€ CASO 3: Listar estudiantes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 3:
                    System.out.println("\n--- LISTA DE ESTUDIANTES ---");
                    for (Estudiante e : mapaEstudiantes.values()) {
                        e.mostrarInformacion();
                        System.out.println("--------------------");
                    }
                    break;

                // â”€â”€â”€ CASO 4: Eliminar estudiante â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 4:
                    System.out.print("ID del estudiante a dar de baja: ");
                    String delId = sc.nextLine();
                    try {
                        Estudiante eDel = buscarEstudiante(delId);
                        mapaEstudiantes.remove(delId);
                        pilaDeshacer.push(new Operacion("ELIMINAR", eDel, null));
                        pilaRehacer.clear();
                        System.out.println("Estudiante eliminado. OperaciÃ³n registrada en pila para deshacer.");
                    } catch (EstudianteNoEncontradoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                // â”€â”€â”€ CASO 5: Crear materia â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 5:
                    try {
                        System.out.print("CÃ³digo Materia: ");
                        String cMat = sc.nextLine();
                        System.out.print("Nombre: ");
                        String nMat = sc.nextLine();
                        System.out.print("Cupos MÃ¡ximos: ");
                        int cupMax = sc.nextInt();
                        System.out.print("CrÃ©ditos: ");
                        int cred = sc.nextInt();
                        sc.nextLine();
                        if (cupMax < 0 || cred < 0) {
                            System.out.println("Error: cupos y crÃ©ditos deben ser valores positivos. Regresando al menÃº.");
                            break;
                        }
                        listaMaterias.agregar(new Materia(cMat, nMat, cupMax, cred));
                        System.out.println("Materia creada de forma exitosa.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese valores numÃ©ricos para cupos y crÃ©ditos. Regresando al menÃº.");
                        sc.nextLine();
                    }
                    break;

                // â”€â”€â”€ CASO 6: Agregar prerrequisito â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 6:
                    System.out.print("CÃ³digo Materia Objetivo: ");
                    String cObj = sc.nextLine();
                    System.out.print("CÃ³digo del Prerrequisito a aÃ±adir: ");
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

                // â”€â”€â”€ CASO 7: Mostrar prerrequisitos â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 7:
                    System.out.print("CÃ³digo Materia: ");
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

                // â”€â”€â”€ CASO 8: Inscribir estudiante â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 8:
                    System.out.print("ID Estudiante: ");
                    String insEst = sc.nextLine();
                    System.out.print("CÃ³digo Materia: ");
                    String insMat = sc.nextLine();
                    try {
                        inscribirEstudianteEnMateria(insEst, insMat, true);
                    } catch (Exception e) {
                        System.out.println("Aviso del sistema: " + e.getMessage());
                    }
                    break;

                // â”€â”€â”€ CASO 9: Cancelar inscripciÃ³n â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 9:
                    System.out.print("ID Estudiante: ");
                    String cancEst = sc.nextLine();
                    System.out.print("CÃ³digo Materia: ");
                    String cancMat = sc.nextLine();
                    cancelarInscripcion(cancEst, cancMat, true);
                    break;

                // â”€â”€â”€ CASO 10: Mostrar cola de espera â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 10:
                    System.out.print("CÃ³digo Materia: ");
                    String colMat = sc.nextLine();
                    Materia mCol = encontrarMateria(colMat);
                    if (mCol != null) {
                        System.out.println("Cola de espera para: " + mCol.getNombre());
                        int pos = 1;
                        for (Estudiante estCola : mCol.getColaEspera()) {
                            System.out.println("PosiciÃ³n " + pos + ": " + estCola.getNombre());
                            pos++;
                        }
                        System.out.println("Total en espera: " + mCol.getColaEspera().size());
                    } else {
                        System.out.println("Materia no encontrada.");
                    }
                    break;

                // â”€â”€â”€ CASOS 11 y 12: Reservar / Liberar horario â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 11:
                    gestionarReservaAula(sc, true);
                    break;
                case 12:
                    gestionarReservaAula(sc, false);
                    break;

                // â”€â”€â”€ CASO 13: Consultar disponibilidad â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 13:
                    try {
                        System.out.print("Nombre Aula: ");
                        String aulaNom = sc.nextLine();
                        Aula au = mapaAulas.get(aulaNom);
                        if (au != null) {
                            System.out.print("DÃ­a (0=Dom, 1=Lun, 2=Mar, 3=Mie, 4=Jue, 5=Vie, 6=Sab): ");
                            int d = sc.nextInt();
                            System.out.print("Hora (0-23): ");
                            int h = sc.nextInt();
                            sc.nextLine();
                            if (d < 0 || d > 6 || h < 0 || h > 23) {
                                System.out.println("Error: dÃ­a debe ser 0-6 y hora debe ser 0-23. Regresando al menÃº.");
                                break;
                            }
                            System.out.println("Estado: " + (au.getDisponibilidad()[d][h] ? "OCUPADO" : "LIBRE"));
                        } else {
                            System.out.println("Aula no encontrada.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese valores numÃ©ricos para dÃ­a y hora. Regresando al menÃº.");
                        sc.nextLine();
                    }
                    break;

                // â”€â”€â”€ CASO 14: Agregar conexiÃ³n entre edificios â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
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
                            System.out.println("Error: edificios deben ser 0-4 y la distancia debe ser positiva. Regresando al menÃº.");
                            break;
                        }
                        matrizDistanciasCampus[o][destNode] = dist;
                        matrizDistanciasCampus[destNode][o] = dist;
                        System.out.println("ConexiÃ³n del campus establecida.");
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese Ãºnicamente valores numÃ©ricos. Regresando al menÃº.");
                        sc.nextLine();
                    }
                    break;

                // â”€â”€â”€ CASO 15: Calcular ruta mÃ¡s corta â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 15:
                    try {
                        System.out.print("Edificio Origen (0 a 4):\n[0:IngenierÃ­a, 1:Biblioteca, 2:CafeterÃ­a, 3:RectorÃ­a, 4:Laboratorios]\nSelecciÃ³n: ");
                        int orig = sc.nextInt();
                        System.out.print("Edificio Destino (0 a 4): ");
                        int dNode = sc.nextInt();
                        sc.nextLine();
                        if (orig < 0 || orig > 4 || dNode < 0 || dNode > 4) {
                            System.out.println("Error: seleccione edificios entre 0 y 4. Regresando al menÃº.");
                            break;
                        }
                        CampusRouter.calcularRutaMasCorta(matrizDistanciasCampus, orig, dNode, nombresEdificios);
                    } catch (InputMismatchException e) {
                        System.out.println("Error: ingrese un nÃºmero entero para el edificio. Regresando al menÃº.");
                        sc.nextLine();
                    }
                    break;

                // â”€â”€â”€ CASO 16: Registrar nota â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 16:
                    try {
                        System.out.print("ID Estudiante: ");
                        String nEst = sc.nextLine();
                        System.out.print("Semestre (1-10): ");
                        int nSem = sc.nextInt();
                        System.out.print("Ãndice de Materia (1-20): ");
                        int nInd = sc.nextInt();
                        System.out.print("Nota (0.0 a 5.0): ");
                        double notaVal = sc.nextDouble();
                        sc.nextLine();
                        if (nSem < 1 || nSem > 10 || nInd < 1 || nInd > 20 || notaVal < 0.0 || notaVal > 5.0) {
                            System.out.println("Error: semestre 1-10, Ã­ndice 1-20, nota 0.0-5.0. Regresando al menÃº.");
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
                        System.out.println("Error: ingrese valores numÃ©ricos vÃ¡lidos. Regresando al menÃº.");
                        sc.nextLine();
                    } catch (EstudianteNoEncontradoException e) {
                        System.out.println("Error operativo: " + e.getMessage());
                    }
                    break;

                // â”€â”€â”€ CASO 17: Ver reporte acadÃ©mico â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 17:
                    System.out.print("ID Estudiante para el informe: ");
                    String repId = sc.nextLine();
                    generarReporteAcademico(repId);
                    break;

                // â”€â”€â”€ CASO 18: Navegador atrÃ¡s â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 18:
                    navegarAtrasReporte();
                    break;

                // â”€â”€â”€ CASO 19: Deshacer â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 19:
                    try {
                        ejecutarDeshacer();
                    } catch (PilaDeshacerVaciaException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                // â”€â”€â”€ CASO 20: Rehacer â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 20:
                    ejecutarRehacer();
                    break;

                // â”€â”€â”€ CASO 21: Procesamiento por lotes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                case 21:
                    System.out.print("Ruta del archivo CSV (idEstudiante,codigoMateria): ");
                    String rutaCsv = sc.nextLine();
                    try {
                        procesarArchivoBatch(rutaCsv);
                    } catch (ArchivoInvalidoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 22:
                    System.out.println("Finalizando la ejecuciÃ³n del sistema de gestiÃ³n.");
                    break;

                default:
                    System.out.println("OpciÃ³n incorrecta. Seleccione un nÃºmero del menÃº.");
            }
        } while (opcion != 22);
    }

    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
    //  MÃ‰TODOS LÃ“GICOS DEL NEGOCIO ACADÃ‰MICO
    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

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

        // ValidaciÃ³n de prerrequisitos obligatorios
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

        // VerificaciÃ³n de disponibilidad de cupos
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
        if (registrarEnPila) {
            System.out.println("InscripciÃ³n exitosa del estudiante " + est.getNombre() + " en " + mat.getNombre());
        }
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
                System.out.println("CancelaciÃ³n exitosa para " + est.getNombre());

                if (registrarEnPila) {
                    pilaDeshacer.push(new Operacion("CANCELAR", est, mat));
                    pilaRehacer.clear();
                }

                // AsignaciÃ³n automÃ¡tica desde la cola de espera
                if (!mat.getColaEspera().estaVacia()) {
                    Estudiante siguiente = mat.getColaEspera().desencolar();
                    System.out.println("Cupo liberado asignado automÃ¡ticamente a: " + siguiente.getNombre());
                    mat.setCuposDisponibles(mat.getCuposDisponibles() - 1);
                    siguiente.getHistorialMaterias().agregar(mat);
                }
            } else {
                System.out.println("El estudiante no estaba inscrito en esa materia.");
            }
        } catch (Exception e) {
            System.out.println("Error en proceso de cancelaciÃ³n: " + e.getMessage());
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
            System.out.print("DÃ­a (0=Dom, 1=Lun, 2=Mar, 3=Mie, 4=Jue, 5=Vie, 6=Sab): ");
            int d = sc.nextInt();
            System.out.print("Hora de inicio (0-23): ");
            int h = sc.nextInt();
            System.out.print("DuraciÃ³n en horas: ");
            int dur = sc.nextInt();
            sc.nextLine();

            if (d < 0 || d > 6 || h < 0 || h > 23 || dur < 1 || (h + dur - 1) > 23) {
                System.out.println("Error: dÃ­a 0-6, hora 0-23, y la duraciÃ³n no debe superar las 24h del dÃ­a. Regresando al menÃº.");
                return;
            }

            for (int i = 0; i < dur; i++) {
                if (reservar && aula.getDisponibilidad()[d][h + i]) {
                    throw new HorarioConflictivoException("HorarioConflictivoException - El bloque de hora " + (h + i) + " estÃ¡ reservado.");
                }
            }
            for (int i = 0; i < dur; i++) {
                aula.getDisponibilidad()[d][h + i] = reservar;
            }
            System.out.println("OperaciÃ³n en matriz horaria ejecutada con Ã©xito.");

        } catch (InputMismatchException e) {
            System.out.println("Error: ingrese valores numÃ©ricos para dÃ­a, hora y duraciÃ³n. Regresando al menÃº.");
            sc.nextLine();
        } catch (HorarioConflictivoException e) {
            System.out.println("Conflicto: " + e.getMessage());
        }
    }

    private void generarReporteAcademico(String idEst) {
        try {
            Estudiante e = buscarEstudiante(idEst);
            StringBuilder sb = new StringBuilder();
            sb.append("--- REPORTE ACADÃ‰MICO ---\n");
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

            // Guardar en la pila para simular el botÃ³n "AtrÃ¡s"
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
            System.out.println("Se encuentra en el primer reporte consultado. No hay mÃ¡s historial.");
        } else {
            System.out.println("El historial de navegaciÃ³n se encuentra vacÃ­o.");
        }
    }

    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
    //  LÃ“GICA TRANSACCIONAL DE PILAS (DESHACER / REHACER)
    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

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
                System.out.println("Deshecho: Se retirÃ³ la inscripciÃ³n de " + op.getEstudiante().getNombre() + " en " + op.getMateria().getNombre());
                break;
            case "CANCELAR":
                op.getEstudiante().getHistorialMaterias().agregar(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() - 1);
                System.out.println("Deshecho: Se restaurÃ³ la inscripciÃ³n de " + op.getEstudiante().getNombre() + " en " + op.getMateria().getNombre());
                break;
            case "NOTA":
                op.getEstudiante().getNotas()[op.getSemestre() - 1][op.getIndiceNota() - 1] = op.getNotaAnterior();
                System.out.println("Deshecho: Se restaurÃ³ la nota previa del estudiante.");
                break;
            case "ELIMINAR":
                mapaEstudiantes.put(op.getEstudiante().getId(), op.getEstudiante());
                System.out.println("Deshecho: Se dio de alta nuevamente al estudiante dado de baja.");
                break;
        }
    }

    private void ejecutarRehacer() {
        // CORRECCIÃ“N: se usaba isEmpty() que no existe en MiPilaDatos; el mÃ©todo correcto es estaVacia()
        if (pilaRehacer.estaVacia()) {
            System.out.println("Error: No hay operaciones en la pila de rehacer.");
            return;
        }
        Operacion op = pilaRehacer.pop();
        pilaDeshacer.push(op);

        switch (op.getTipo()) {
            case "INSCRIBIR":
                // CORRECCIÃ“N: agregar(null);(op.getMateria()) â†’ agregar(op.getMateria())
                op.getEstudiante().getHistorialMaterias().agregar(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() - 1);
                System.out.println("Rehecho: InscripciÃ³n confirmada.");
                break;
            case "CANCELAR":
                // CORRECCIÃ“N: .getHistorialMaterias().(op.getMateria()) â†’ .getHistorialMaterias().remove(op.getMateria())
                op.getEstudiante().getHistorialMaterias().remove(op.getMateria());
                op.getMateria().setCuposDisponibles(op.getMateria().getCuposDisponibles() + 1);
                System.out.println("Rehecho: CancelaciÃ³n confirmada.");
                break;
            case "NOTA":
                // CORRECCIÃ“N: ahora restaura el valor nuevo usando getNotaNueva() (campo agregado a Operacion)
                op.getEstudiante().getNotas()[op.getSemestre() - 1][op.getIndiceNota() - 1] = op.getNotaNueva();
                System.out.println("Rehecho: Cambio de nota aplicado nuevamente.");
                break;
            case "ELIMINAR":
                mapaEstudiantes.remove(op.getEstudiante().getId());
                System.out.println("Rehecho: EliminaciÃ³n aplicada de nuevo.");
                break;
        }
    }

    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
    //  PROCESAMIENTO POR LOTES
    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

    private static class SolicitudInscripcion {
        private final int numeroLinea;
        private final String idEstudiante;
        private final String codigoMateria;

        SolicitudInscripcion(int numeroLinea, String idEstudiante, String codigoMateria) {
            this.numeroLinea = numeroLinea;
            this.idEstudiante = idEstudiante;
            this.codigoMateria = codigoMateria;
        }
    }

    /**
     * Ejecuta la carga masiva solicitada en la guia: primero encola todo el CSV y luego
     * procesa cada inscripcion en orden de llegada.
     */
    private void procesarArchivoBatch(String rutaCsv) throws ArchivoInvalidoException {
        System.out.println("--- PROCESAMIENTO MASIVO (BATCH) ---");
        System.out.println("Archivo: " + rutaCsv);

        MiColaDatos<SolicitudInscripcion> colaBatch = cargarSolicitudesDesdeCsv(rutaCsv);
        int totalSolicitudes = colaBatch.size();
        int exitosas = 0;
        int fallidas = 0;

        System.out.println("Se encolaron " + totalSolicitudes + " solicitudes.");
        System.out.println("Procesando cola...");

        for (int procesadas = 1; !colaBatch.estaVacia(); procesadas++) {
            SolicitudInscripcion solicitud = colaBatch.desencolar();

            try {
                procesarSolicitudBatch(solicitud);
                System.out.println("[" + procesadas + "/" + totalSolicitudes + "] "
                        + solicitud.idEstudiante + " -> " + solicitud.codigoMateria + " -> Exitosa");
                exitosas++;
            } catch (Exception e) {
                System.out.println("[" + procesadas + "/" + totalSolicitudes + "] "
                        + solicitud.idEstudiante + " -> " + solicitud.codigoMateria
                        + " -> Fallida (" + e.getMessage() + ")");
                fallidas++;
            }
        }

        System.out.println("=== RESUMEN ===");
        System.out.println("Exitosas: " + exitosas);
        System.out.println("Fallidas: " + fallidas);
    }

    /**
     * Convierte cada registro valido del CSV en una solicitud dentro de la cola propia.
     */
    private MiColaDatos<SolicitudInscripcion> cargarSolicitudesDesdeCsv(String rutaCsv) throws ArchivoInvalidoException {
        Path ruta = Path.of(rutaCsv.trim());
        if (!Files.isRegularFile(ruta)) {
            throw new ArchivoInvalidoException("ArchivoInvalidoException - No existe el archivo indicado.");
        }

        MiColaDatos<SolicitudInscripcion> colaBatch = new MiColaDatos<>();

        // Primero se carga todo el archivo en la cola; el procesamiento ocurre despues, en orden FIFO.
        try (BufferedReader lector = Files.newBufferedReader(ruta)) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                linea = linea.trim().replace("\uFEFF", "");

                if (linea.isEmpty()) {
                    continue;
                }
                if (numeroLinea == 1 && linea.equalsIgnoreCase("idEstudiante,codigoMateria")) {
                    continue;
                }

                String[] campos = linea.split(",", -1);
                if (campos.length != 2 || campos[0].trim().isEmpty() || campos[1].trim().isEmpty()) {
                    throw new ArchivoInvalidoException("ArchivoInvalidoException - Linea " + numeroLinea
                            + " invalida. Formato esperado: idEstudiante,codigoMateria");
                }

                colaBatch.encolar(new SolicitudInscripcion(numeroLinea, campos[0].trim(), campos[1].trim()));
            }
        } catch (IOException e) {
            throw new ArchivoInvalidoException("ArchivoInvalidoException - No se pudo leer el archivo: " + e.getMessage());
        }

        if (colaBatch.estaVacia()) {
            throw new ArchivoInvalidoException("ArchivoInvalidoException - El archivo no contiene solicitudes validas.");
        }
        return colaBatch;
    }

    /**
     * Reutiliza las reglas normales de inscripcion para que el Batch respete cupos,
     * prerrequisitos, estudiantes existentes y materias existentes.
     */
    private void procesarSolicitudBatch(SolicitudInscripcion solicitud) throws Exception {
        if (encontrarMateria(solicitud.codigoMateria) == null) {
            throw new ArchivoInvalidoException("Linea " + solicitud.numeroLinea
                    + ": no existe la materia " + solicitud.codigoMateria);
        }

        buscarEstudiante(solicitud.idEstudiante);
        inscribirEstudianteEnMateria(solicitud.idEstudiante, solicitud.codigoMateria, false);
    }

    //  GENERADOR AUTOMÃTICO DE DATOS ALEATORIOS PARA PRUEBAS
    // â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

    private void generarDatosAleatoriosDePrueba() {
        System.out.println("\nEjecutando generador de datos aleatorios...");

        // 1. PoblaciÃ³n de Aulas en el TreeMap
        mapaAulas.put("Aula 101", new Aula("Aula 101", 30));
        mapaAulas.put("Aula 202", new Aula("Aula 202", 40));
        mapaAulas.put("Laboratorio Alfa", new Aula("Laboratorio Alfa", 25));

        // 2. PoblaciÃ³n de Materias
        Materia m1 = new Materia("MATE01", "CÃ¡lculo I", 3, 4); // Cupo bajo a propÃ³sito para probar la cola de espera
        Materia m2 = new Materia("MATE02", "CÃ¡lculo II", 15, 4);
        Materia m3 = new Materia("PROG01", "Estructura de Datos", 20, 3);

        m2.agregarPreRequisito(m1); // Requisito lÃ³gico para pruebas de excepciÃ³n

        listaMaterias.agregar(m1);
        listaMaterias.agregar(m2);
        listaMaterias.agregar(m3);

        // 3. GeneraciÃ³n masiva de Estudiantes aleatorios
        String[] nombresEjemplo = {"Mateo", "Valentina", "Santiago", "Camila", "Nicolas", "Isabella", "Lucas", "Gabriela"};
        String[] apellidosEjemplo = {"GÃ³mez", "RodrÃ­guez", "LÃ³pez", "MartÃ­nez", "PÃ©rez", "GarcÃ­a", "Castro", "Zapata"};
        Random rnd = new Random();

        for (int i = 1; i <= 10; i++) {
            String idGen = "EST00" + i;
            String nombreCompleto = nombresEjemplo[rnd.nextInt(nombresEjemplo.length)] + " " + apellidosEjemplo[rnd.nextInt(apellidosEjemplo.length)];
            String emailGen = nombreCompleto.toLowerCase().replace(" ", ".") + "@universidad.edu";
            int semestreGen = rnd.nextInt(5) + 1;

            Estudiante est = new Estudiante(nombreCompleto, idGen, emailGen, semestreGen);

            // Carga aleatoria de notas en el arreglo estÃ¡tico Double[10][20]
            for (int s = 0; s < semestreGen; s++) {
                int materiasEnSemestre = rnd.nextInt(3) + 2; // entre 2 y 4 materias asignadas
                for (int m = 0; m < materiasEnSemestre; m++) {
                    double notaAleatoria = 1.5 + (rnd.nextDouble() * 3.5); // Notas entre 1.5 y 5.0
                    est.getNotas()[s][m] = Math.round(notaAleatoria * 10.0) / 10.0;
                }
            }

            // Para los estudiantes pares, simular que ya aprobaron CÃ¡lculo I
            if (i % 2 == 0) {
                est.getHistorialMaterias().agregar(m1);
            }

            mapaEstudiantes.put(idGen, est);
        }

        System.out.println("Â¡OperaciÃ³n exitosa! Se han cargado 3 aulas, 3 materias acadÃ©micas y 10 estudiantes con notas e historial inicializados de forma aleatoria.");
    }
}

