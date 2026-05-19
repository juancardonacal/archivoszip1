package uni;

import uni.exc.*;
import java.util.*;
import java.io.*;

public class Sistema {

    // ── Estructuras principales ──────────────────────────────────────────────
    HashMap<String,Estudiante> ests = new HashMap<>();   // índice rápido por ID
    TreeMap<String,Materia>    mats = new TreeMap<>();   // ordenadas por código
    TreeMap<String,Aula>       aulas= new TreeMap<>();   // ordenadas por nombre
    Facultad[] facs = new Facultad[5];                   // arreglo nativo Facultad[5]
    Grafo grafo = new Grafo();

    // ── Pilas deshacer/rehacer + navegación reportes ─────────────────────────
    Pila<Op>     undo = new Pila<>("pilaDeshacer");
    Pila<Op>     redo = new Pila<>("pilaRehacer");
    Pila<String> navB = new Pila<>("navAtras");
    Pila<String> navF = new Pila<>("navAdelante");

    // ── Cola batch ───────────────────────────────────────────────────────────
    Cola<String[]> batch = new Cola<>();

    Scanner sc = new Scanner(System.in);

    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) { new Sistema().run(); }

    Sistema() {
        facs[0]=new Facultad("IS","Ingeniería Sistemas"); facs[1]=new Facultad("IC","Ingeniería Civil");
        facs[2]=new Facultad("MED","Medicina");           facs[3]=new Facultad("DER","Derecho");
        facs[4]=new Facultad("ADM","Administración");

        aulas.put("101",new Aula("101",30)); aulas.put("102",new Aula("102",40));
        aulas.put("LAB1",new Aula("LAB1",20));

        int a=grafo.add("Ingenieria"),b=grafo.add("Biblioteca"),c=grafo.add("Cafeteria"),
            r=grafo.add("Rectoria"),l=grafo.add("Laboratorios");
        grafo.link(a,b,200);grafo.link(a,c,150);grafo.link(b,r,300);
        grafo.link(c,r,180);grafo.link(r,l,250);grafo.link(b,l,220);

        // datos demo
        put("2024001","Ana Maria Gomez","ana@uni.edu",3);
        put("2024002","Juan Perez","juan@uni.edu",2);
        put("2024003","Maria Lopez","maria@uni.edu",1);
        mats.put("CALC101",new Materia("CALC101","Calculo I",3,4));
        mats.put("FIS101", new Materia("FIS101","Fisica I",25,3));
        mats.put("PROG101",new Materia("PROG101","Programacion I",20,3));
        mats.put("CALC102",new Materia("CALC102","Calculo II",20,4));
        mats.get("CALC102").addPrereq("CALC101");
        Estudiante ana=ests.get("2024001");
        ana.addNota(0,"CALC101",4.5);ana.addNota(0,"FIS101",2.8);ana.addNota(0,"PROG101",5.0);
        ana.addNota(1,"CALC102",3.5);ana.addNota(1,"MATDIS",2.5);
    }
    private void put(String id,String n,String e,int s){ests.put(id,new Estudiante(n,id,e,s));}

    // ════════════════════════════════════════════════════════════════════════
    //  MENÚ
    // ════════════════════════════════════════════════════════════════════════
    void run() {
        while(true){
            System.out.println("\n============================================================");
            System.out.println("      PLANIFICACIÓN ACADÉMICA - SISTEMA UNIVERSITARIO");
            System.out.println("============================================================");
            System.out.println("=== GESTION DE ESTUDIANTES ===");
            System.out.println(" 1.Registrar  2.Buscar  3.Listar  4.Eliminar");
            System.out.println("=== GESTION DE MATERIAS ===");
            System.out.println(" 5.Crear materia  6.Agregar pre-requisito  7.Ver pre-requisitos");
            System.out.println(" 8.Inscribir  9.Cancelar inscripcion  10.Cola de espera");
            System.out.println("=== GESTION DE HORARIOS ===");
            System.out.println("11.Reservar  12.Liberar  13.Disponibilidad  14.Ver horario");
            System.out.println("=== RUTAS ENTRE EDIFICIOS ===");
            System.out.println("15.Ver edificios  16.Agregar conexion  17.Ruta mas corta");
            System.out.println("=== REPORTES ACADEMICOS ===");
            System.out.println("18.Registrar nota  19.Ver reporte  20.Navegar reportes");
            System.out.println("=== DESHACER/REHACER ===");
            System.out.println("21.Deshacer  22.Rehacer");
            System.out.println("=== BATCH ===");
            System.out.println("23.Procesar CSV  24.Generar CSV ejemplo  25.Salir");
            System.out.println("============================================================");
            int op=num("Seleccione: ");
            System.out.println();
            try{ if(!exec(op))break; }
            catch(Exception e){System.out.println("Error: "+e.getClass().getSimpleName()+" - "+e.getMessage());}
        }
        System.out.println("Hasta luego.");
    }

    boolean exec(int op) throws Exception {
        switch(op){
        // ── ESTUDIANTES ─────────────────────────────────────────────────────
        case 1->{
            System.out.println("--- REGISTRO DE ESTUDIANTE ---");
            String id=str("ID: "),n=str("Nombre: "),e=str("Email: ");int s=num("Semestre: ");
            if(ests.containsKey(id))throw new IllegalArgumentException("ID ya existe");
            Estudiante est=new Estudiante(n,id,e,s); ests.put(id,est);
            reg(new Op(Op.T.REG,"Registro "+n,null,est,id,null));
            System.out.println("Estudiante registrado exitosamente.");
        }
        case 2->{
            Estudiante e=get(str("ID: ")); e.info();
            System.out.printf("Promedio acumulado: %.2f%n",e.promAcum());
        }
        case 3->{ if(ests.isEmpty())System.out.println("Sin estudiantes."); else ests.values().forEach(Estudiante::info); }
        case 4->{
            String id=str("ID: "); Estudiante e=get(id); ests.remove(id);
            reg(new Op(Op.T.DEL,"Eliminación "+e.getNombre(),e,null,id,null));
            System.out.println("Estudiante eliminado.");
        }
        // ── MATERIAS ────────────────────────────────────────────────────────
        case 5->{
            String c=str("Código: "),n=str("Nombre: ");int cu=num("Cupos: "),cr=num("Créditos: ");
            if(mats.containsKey(c))throw new IllegalArgumentException("Código ya existe");
            mats.put(c,new Materia(c,n,cu,cr)); System.out.println("Materia creada.");
        }
        case 6->{
            String m=str("Materia: "),p=str("Pre-requisito: ");
            mat(m);mat(p); mats.get(m).addPrereq(p); System.out.println("Pre-requisito agregado.");
        }
        case 7->{ Materia m=mat(str("Materia: ")); System.out.println("Pre-requisitos: "+m.prereqs); }
        case 8->{ inscribir(str("ID estudiante: "),str("Código materia: ")); }
        case 9->{ cancelar(str("ID estudiante: "),str("Código materia: ")); }
        case 10->{ Materia m=mat(str("Materia: ")); System.out.println("Cola de espera - "+m.nombre+":"); m.espera.print(); }
        // ── HORARIOS ────────────────────────────────────────────────────────
        case 11->{
            System.out.println("Aulas: "+aulas.keySet());
            String a=str("Aula: ");int d=num("Dia(0=Dom..6=Sáb): "),h=num("Hora(0-23): "),du=num("Duración: ");
            reservar(a,d,h,du);
        }
        case 12->{
            String a=str("Aula: ");int d=num("Dia: "),h=num("Hora: "),du=num("Duración: ");
            Aula au=aula(a); boolean[][]ant=au.copia(); au.liberar(d,h,du);
            reg(new Op(Op.T.HOR,"Liberar "+a,ant,au.copia(),a,null));
            System.out.println("Horario liberado.");
        }
        case 13->{
            String a=str("Aula: ");int d=num("Dia: "),h=num("Hora: ");
            System.out.println(Aula.D[d]+" "+h+":00 → "+(aula(a).libre(d,h)?"LIBRE":"OCUPADO"));
        }
        case 14->{ System.out.println("Aulas: "+aulas.keySet()); aula(str("Aula: ")).mostrar(); }
        // ── EDIFICIOS ───────────────────────────────────────────────────────
        case 15->{ System.out.println("--- EDIFICIOS ---"); grafo.list(); }
        case 16->{ grafo.list();int a=num("Desde: "),b=num("Hasta: "),m=num("Metros: ");grafo.link(a,b,m);System.out.println("Conexión agregada."); }
        case 17->{ grafo.list();grafo.dijkstra(num("Origen: "),num("Destino: ")); }
        // ── REPORTES ────────────────────────────────────────────────────────
        case 18->{
            String id=str("ID: ");int s=num("Semestre(1-10): ")-1;String c=str("Código: ");double n=dbl("Nota(0-5): ");
            Estudiante e=get(id); if(n<0||n>5)throw new IllegalArgumentException("Nota fuera de rango");
            e.addNota(s,c,n); System.out.printf("Nota %.1f registrada.%n",n);
            reg(new Op(Op.T.NOTA,"Nota "+n+" en "+c,null,null,id,c));
        }
        case 19->{ String id=str("ID: "); navB.push(id); navF.clear(); get(id).reporte(); }
        case 20->{
            System.out.println("1.← Atrás   2.→ Adelante"); int n=num("Opción: ");
            if(n==1){String id=navB.pop();navF.push(id);if(!navB.empty())get(navB.peek()).reporte();else System.out.println("Sin reportes anteriores.");}
            else{String id=navF.pop();navB.push(id);get(id).reporte();}
        }
        // ── DESHACER / REHACER ──────────────────────────────────────────────
        case 21->{ Op o=undo.pop();System.out.println("Deshaciendo: "+o.desc);aplicar(o,true);redo.push(o);System.out.println("Operacion deshecha."); }
        case 22->{ Op o=redo.pop();System.out.println("Rehaciendo: "+o.desc);aplicar(o,false);undo.push(o);System.out.println("Operacion rehecha."); }
        // ── BATCH ───────────────────────────────────────────────────────────
        case 23->{ cargarCSV(str("Ruta CSV: ")); procesarBatch(); }
        case 24->{ generarCSV(); }
        case 25->{ return false; }
        default->System.out.println("Opción inválida.");
        }
        return true;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  LÓGICA INSCRIPCIÓN / CANCELACIÓN
    // ════════════════════════════════════════════════════════════════════════
    void inscribir(String idE,String codM) throws Exception {
        Estudiante e=get(idE); Materia m=mat(codM);
        for(int i=0;i<m.prereqs.sz;i++){String p=m.prereqs.get(i);if(!e.curso(p))throw new PreRequisitoNoAprobadoException("Falta pre-requisito: "+p);}
        if(m.inscrito(idE)){System.out.println("Ya está inscrito.");return;}
        if(m.inscribir(idE)){reg(new Op(Op.T.INS,"Inscripción "+e.getNombre()+" en "+m.nombre,false,true,idE,codM));System.out.println("Inscripcion exitosa. Cupos restantes: "+m.getCuposRest());}
        else System.out.println("Materia llena. "+e.getNombre()+" en COLA DE ESPERA.");
    }
    void cancelar(String idE,String codM) throws Exception {
        Estudiante e=get(idE); Materia m=mat(codM);
        if(!m.inscrito(idE)){System.out.println("No está inscrito.");return;}
        reg(new Op(Op.T.CAN,"Cancelación "+e.getNombre()+" en "+m.nombre,true,false,idE,codM));
        String sig=m.cancelar(idE); System.out.println("Cancelacion exitosa. Cupo liberado.");
        if(sig!=null){String sn=ests.containsKey(sig)?ests.get(sig).getNombre():sig;System.out.println("Asignando cupo a "+sn+" (primero en cola)");}
    }

    void reservar(String n,int d,int h,int du) throws Exception {
        Aula a=aula(n); System.out.println("Verificando disponibilidad...");
        for(int i=h;i<h+du&&i<24;i++)System.out.println(Aula.D[d]+" "+i+":00 → "+(a.libre(d,i)?"LIBRE":"OCUPADO"));
        boolean[][]ant=a.copia(); a.reservar(d,h,du);
        reg(new Op(Op.T.HOR,"Reserva "+n+" "+Aula.D[d]+" "+h+":00 dur:"+du,ant,a.copia(),n,null));
        System.out.println("Reserva exitosa.");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DESHACER / REHACER
    // ════════════════════════════════════════════════════════════════════════
    void reg(Op o){undo.push(o);redo.clear();}

    @SuppressWarnings("unchecked")
    void aplicar(Op o,boolean deshacer){
        try{switch(o.tipo){
            case REG ->{ if(deshacer)ests.remove(o.id1); else ests.put(o.id1,(Estudiante)o.post); }
            case DEL ->{ if(deshacer)ests.put(o.id1,(Estudiante)o.ant); else ests.remove(o.id1); }
            case INS ->{ Materia m=mats.get(o.id2);if(m!=null){if(deshacer)m.cancelar(o.id1);else m.inscribir(o.id1);} }
            case CAN ->{ Materia m=mats.get(o.id2);if(m!=null){if(deshacer)m.inscribir(o.id1);else m.cancelar(o.id1);} }
            case HOR ->{ Aula a=aulas.get(o.id1);if(a!=null)a.restaurar(deshacer?(boolean[][])o.ant:(boolean[][])o.post); }
            case NOTA->System.out.println("(Reversión de nota manual)");
        }}catch(Exception e){System.out.println("Error: "+e.getMessage());}
    }

    // ════════════════════════════════════════════════════════════════════════
    //  BATCH
    // ════════════════════════════════════════════════════════════════════════
    void cargarCSV(String ruta) throws ArchivoInvalidoException {
        File f=new File(ruta);
        if(!f.exists())throw new ArchivoInvalidoException("Archivo no encontrado: "+ruta);
        int n=0;
        try(BufferedReader br=new BufferedReader(new FileReader(f))){
            String l; while((l=br.readLine())!=null){l=l.trim();if(l.isEmpty()||l.startsWith("#"))continue;String[]p=l.split(",");if(p.length>=2){batch.add(new String[]{p[0].trim(),p[1].trim()});n++;}}
        }catch(IOException e){throw new ArchivoInvalidoException("Error: "+e.getMessage());}
        System.out.println("Se encolaron "+n+" solicitudes.");
    }

    void procesarBatch(){
        int total=batch.sz,ok=0,fail=0,i=1;
        System.out.println("Procesando cola...");
        while(!batch.empty()){
            String[]s;try{s=batch.poll();}catch(Exception e){break;}
            System.out.printf("[%d/%d] %s → %s → ",i++,total,s[0],s[1]);
            try{inscribir(s[0],s[1]);ok++;System.out.println("Exitosa");}
            catch(Exception e){fail++;System.out.println("Fallida: "+e.getMessage());}
        }
        System.out.println("=== RESUMEN ===\nExitosas: "+ok+"\nFallidas: "+fail);
    }

    void generarCSV() throws IOException {
        try(PrintWriter pw=new PrintWriter(new FileWriter("inscripciones.csv"))){
            pw.println("# idEstudiante,codigoMateria");
            pw.println("2024001,CALC101");pw.println("2024002,FIS101");
            pw.println("2024003,PROG101");pw.println("2024002,CALC102");
        }
        System.out.println("Archivo generado: inscripciones.csv");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════════════
    Estudiante get(String id) throws EstudianteNoEncontradoException {
        Estudiante e=ests.get(id);
        if(e==null)throw new EstudianteNoEncontradoException("No existe estudiante con ID: "+id);
        return e;
    }
    Materia mat(String c) throws Exception {
        Materia m=mats.get(c);if(m==null)throw new Exception("Materia no encontrada: "+c);return m;
    }
    Aula aula(String n) throws Exception {
        Aula a=aulas.get(n);if(a==null)throw new Exception("Aula no encontrada: "+n+". Disponibles: "+aulas.keySet());return a;
    }
    String str(String p){System.out.print(p);return sc.nextLine().trim();}
    int num(String p){while(true){System.out.print(p);try{return Integer.parseInt(sc.nextLine().trim());}catch(Exception e){System.out.println("Ingrese un entero.");}} }
    double dbl(String p){while(true){System.out.print(p);try{return Double.parseDouble(sc.nextLine().trim());}catch(Exception e){System.out.println("Ingrese un decimal.");}} }
}
