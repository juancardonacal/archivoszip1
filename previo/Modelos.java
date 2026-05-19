package uni;

import uni.exc.*;

// ─── JERARQUÍA: Persona (abstracta) → Estudiante, Profesor ──────────────────
abstract class Persona {
    protected String nombre, id, email;
    Persona(String nombre,String id,String email){this.nombre=nombre;this.id=id;this.email=email;}
    abstract void info(); // polimorfismo
    public String getId(){return id;} public String getNombre(){return nombre;}
}

class Estudiante extends Persona {
    int semestre;
    Double[][] notas = new Double[10][20];   // arreglo nativo Double[10][20]
    String[][] cods  = new String[10][20];
    int[] cnt = new int[10];
    Lista<String> historial = new Lista<>();

    Estudiante(String n,String id,String e,int s){super(n,id,e);semestre=s;}

    boolean addNota(int s,String cod,double nota){
        if(s<0||s>=10||cnt[s]>=20)return false;
        notas[s][cnt[s]]=nota; cods[s][cnt[s]]=cod; cnt[s]++; historial.add(cod); return true;
    }
    double promSem(int s){if(cnt[s]==0)return 0;double t=0;for(int i=0;i<cnt[s];i++)t+=notas[s][i];return t/cnt[s];}
    double promAcum(){int t=0;double s=0;for(int i=0;i<10;i++)for(int j=0;j<cnt[i];j++){s+=notas[i][j];t++;}return t==0?0:s/t;}
    boolean curso(String cod){return historial.has(cod);}

    void reporte(){
        System.out.println("--- REPORTE ACADEMICO ---\nEstudiante: "+nombre+" (ID: "+id+")");
        int ap=0,re=0;
        for(int s=0;s<10;s++){if(cnt[s]==0)continue;System.out.println("Semestre "+(s+1)+":");for(int m=0;m<cnt[s];m++){System.out.printf("  %s: %.1f%n",cods[s][m],notas[s][m]);if(notas[s][m]>=3.0)ap++;else re++;}System.out.printf("  Promedio: %.2f%n",promSem(s));}
        System.out.printf("=== RESUMEN ===%nPromedio acumulado: %.2f%nAprobadas: %d | Reprobadas: %d%n",promAcum(),ap,re);
    }
    @Override void info(){System.out.printf("ID:%-10s Nombre:%-20s Sem:%d Prom:%.2f%n",id,nombre,semestre,promAcum());}
}

class Profesor extends Persona {
    String dpto;
    Profesor(String n,String id,String e,String d){super(n,id,e);dpto=d;}
    @Override void info(){System.out.println("PROFESOR: "+nombre+" | Dpto: "+dpto);}
}

// ─── Facultad — arreglo nativo Facultad[5] requerido ────────────────────────
class Facultad {
    String cod,nombre;
    Facultad(String c,String n){cod=c;nombre=n;}
    public String toString(){return "["+cod+"] "+nombre;}
}

// ─── Materia con lista de prereqs y cola de espera ───────────────────────────
class Materia {
    String cod,nombre; int cuposMax,ocupados,cred;
    Lista<String> prereqs=new Lista<>(), inscritos=new Lista<>();
    Cola<String> espera=new Cola<>();

    Materia(String c,String n,int cm,int cr){cod=c;nombre=n;cuposMax=cm;cred=cr;}
    void addPrereq(String c){prereqs.add(c);}
    boolean inscribir(String id){if(ocupados<cuposMax){inscritos.add(id);ocupados++;return true;}espera.add(id);return false;}
    String cancelar(String id){if(!inscritos.has(id))return null;inscritos.del(id);ocupados--;if(!espera.empty()){try{String s=espera.poll();inscritos.add(s);ocupados++;return s;}catch(Exception ignored){}}return null;}
    boolean inscrito(String id){return inscritos.has(id);}
    public String toString(){return "["+cod+"] "+nombre+" | Créditos:"+cred+" | Cupos:"+ocupados+"/"+cuposMax;}
}

// ─── Aula con boolean[7][24] nativa ─────────────────────────────────────────
class Aula {
    String nombre; int cap;
    boolean[][] h=new boolean[7][24]; // arreglo nativo boolean[7][24]
    static final String[] D={"Dom","Lun","Mar","Mié","Jue","Vie","Sáb"};

    Aula(String n,int c){nombre=n;cap=c;}
    void reservar(int d,int hr,int dur) throws HorarioConflictivoException{
        for(int i=hr;i<hr+dur;i++)if(h[d][i])throw new HorarioConflictivoException(D[d]+" "+i+":00 ya está reservado");
        for(int i=hr;i<hr+dur;i++)h[d][i]=true;
    }
    void liberar(int d,int hr,int dur){for(int i=hr;i<hr+dur&&i<24;i++)h[d][i]=false;}
    boolean libre(int d,int hr){return !h[d][hr];}
    boolean[][] copia(){boolean[][]c=new boolean[7][24];for(int d=0;d<7;d++)for(int i=0;i<24;i++)c[d][i]=h[d][i];return c;}
    void restaurar(boolean[][]c){for(int d=0;d<7;d++)for(int i=0;i<24;i++)h[d][i]=c[d][i];}
    void mostrar(){System.out.println("Aula "+nombre+" (cap:"+cap+")");System.out.println("Hora| Dom Lun Mar Mié Jue Vie Sáb");for(int hr=7;hr<22;hr++){System.out.printf("%2d:0|",hr);for(int d=0;d<7;d++)System.out.print(h[d][hr]?" X ":"  .");System.out.println();}}
    public String toString(){return "Aula "+nombre+"(cap:"+cap+")";}
}

// ─── Operación para deshacer/rehacer ─────────────────────────────────────────
class Op {
    enum T{REG,DEL,INS,CAN,NOTA,HOR}
    T tipo; String desc,id1,id2; Object ant,post;
    Op(T t,String d,Object a,Object p,String i1,String i2){tipo=t;desc=d;ant=a;post=p;id1=i1;id2=i2;}
    public String toString(){return "["+tipo+"] "+desc;}
}
