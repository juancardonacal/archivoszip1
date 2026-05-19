package uni;

import uni.exc.*;

// ─── LISTA ENLAZADA propia ───────────────────────────────────────────────────
class Lista<T> {
    private static class N<T> { T d; N<T> s; N(T d){this.d=d;} }
    private N<T> h; int sz;
    public void add(T d){N<T> n=new N<>(d);if(h==null)h=n;else{N<T>a=h;while(a.s!=null)a=a.s;a.s=n;}sz++;}
    public boolean del(T d){if(h==null)return false;if(h.d.equals(d)){h=h.s;sz--;return true;}N<T>a=h;while(a.s!=null){if(a.s.d.equals(d)){a.s=a.s.s;sz--;return true;}a=a.s;}return false;}
    public boolean has(T d){N<T>a=h;while(a!=null){if(a.d.equals(d))return true;a=a.s;}return false;}
    public T get(int i){N<T>a=h;for(int k=0;k<i;k++)a=a.s;return a.d;}
    public boolean empty(){return sz==0;}
    public String toString(){if(empty())return"[]";StringBuilder b=new StringBuilder("[");N<T>a=h;while(a!=null){b.append(a.d);if(a.s!=null)b.append(",");a=a.s;}return b.append("]").toString();}
}

// ─── PILA propia (LIFO) ──────────────────────────────────────────────────────
class Pila<T> {
    private static class N<T>{T d;N<T>s;N(T d){this.d=d;}}
    private N<T> top; int sz; private String name;
    Pila(String n){name=n;}
    public void push(T d){N<T>n=new N<>(d);n.s=top;top=n;sz++;}
    public T pop() throws PilaDeshacerVaciaException{if(top==null)throw new PilaDeshacerVaciaException("No hay operaciones en "+name);T d=top.d;top=top.s;sz--;return d;}
    public T peek() throws PilaDeshacerVaciaException{if(top==null)throw new PilaDeshacerVaciaException("Pila vacía: "+name);return top.d;}
    public boolean empty(){return top==null;}
    public void clear(){top=null;sz=0;}
}

// ─── COLA propia (FIFO) ──────────────────────────────────────────────────────
class Cola<T> {
    private static class N<T>{T d;N<T>s;N(T d){this.d=d;}}
    private N<T> front,back; int sz;
    public void add(T d){N<T>n=new N<>(d);if(back==null){front=back=n;}else{back.s=n;back=n;}sz++;}
    public T poll() throws ColaDeEsperaVaciaException{if(front==null)throw new ColaDeEsperaVaciaException("Cola vacía");T d=front.d;front=front.s;if(front==null)back=null;sz--;return d;}
    public boolean empty(){return front==null;}
    public void print(){N<T>a=front;int p=1;while(a!=null){System.out.println("  Posicion "+p+++": "+a.d);a=a.s;}System.out.println("  Total: "+sz);}
}

// ─── GRAFO con matriz int[N][N] + Dijkstra ───────────────────────────────────
class Grafo {
    static final int MAX=10, INF=Integer.MAX_VALUE/2;
    int[][] d=new int[MAX][MAX]; String[] names=new String[MAX]; int n;
    Grafo(){for(int i=0;i<MAX;i++)for(int j=0;j<MAX;j++)d[i][j]=(i==j)?0:INF;}
    int add(String s){names[n]=s;return n++;}
    void link(int a,int b,int w){d[a][b]=w;d[b][a]=w;}
    void list(){for(int i=0;i<n;i++)System.out.println("  "+i+": "+names[i]);}
    void dijkstra(int src,int dst){
        int[]dist=new int[n],prev=new int[n];boolean[]vis=new boolean[n];
        for(int i=0;i<n;i++){dist[i]=INF;prev[i]=-1;}dist[src]=0;
        for(int c=0;c<n;c++){int u=-1;for(int i=0;i<n;i++)if(!vis[i]&&(u==-1||dist[i]<dist[u]))u=i;if(u==-1||dist[u]==INF)break;vis[u]=true;for(int v=0;v<n;v++)if(d[u][v]!=INF&&!vis[v]&&dist[u]+d[u][v]<dist[v]){dist[v]=dist[u]+d[u][v];prev[v]=u;}}
        if(dist[dst]==INF){System.out.println("  Sin ruta.");return;}
        int[]path=new int[n];int len=0,cur=dst;while(cur!=-1){path[len++]=cur;cur=prev[cur];}
        System.out.print("  Ruta: ");for(int i=len-1;i>=0;i--){System.out.print(names[path[i]]);if(i>0)System.out.print(" -> ("+d[path[i]][path[i-1]]+"m) -> ");}
        System.out.println("\n  Total: "+dist[dst]+" metros");
    }
}
