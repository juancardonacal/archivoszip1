package src.estructuras;

public class MiPilaDatos<T> {
    private Nodo<T> tope;
    private int tamanio; // <-- 1. Añadimos la variable para contar los elementos

    public MiPilaDatos() {
        this.tope = null;
        this.tamanio = 0; // Al principio la pila está vacía
    }

    // Pone un elemento arriba en la pila (Equivalente al push nativo)
    public void push(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        nuevoNodo.setSiguiente(tope);
        tope = nuevoNodo;
        tamanio++; // <-- 2. Cada vez que agregas un elemento, sumas 1
    }

    // Quita y devuelve el elemento de arriba (Equivalente al pop nativo)
    public T pop() {
        if (estaVacia()) {
            return null;
        }
        T valor = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--; // <-- 3. Cada vez que sacas un elemento, restas 1
        return valor;
    }

    // Mira el elemento de arriba sin quitarlo (Equivalente al peek nativo)
    public T peek() {
        if (estaVacia()) {
            return null;
        }
        return tope.getDato();
    }

    // <-- 4. ESTE ES EL MÉTODO QUE TE RESOLVERÁ EL ERROR DE SIZE
    public int size() {
        return this.tamanio; 
    }

    public boolean estaVacia() {
        return tope == null;
    }

    // El método clear que agregamos en el paso anterior también debe resetear el contador
    public void clear() {
        this.tope = null;
        this.tamanio = 0; // <-- 5. Si vacías la pila, el contador vuelve a cero
    }
}