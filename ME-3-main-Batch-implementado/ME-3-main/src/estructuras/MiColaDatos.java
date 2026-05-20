package src.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MiColaDatos<T> implements Iterable<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public MiColaDatos() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0;
    }

    public void encolar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (estaVacia()) {
            primero = nuevoNodo;
            ultimo = nuevoNodo;
        } else {
            ultimo.setSiguiente(nuevoNodo);
            ultimo = nuevoNodo;
        }
        tamanio++;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T valor = primero.getDato();
        primero = primero.getSiguiente();
        if (primero == null) {
            ultimo = null;
        }
        tamanio--;
        return valor;
    }

    // Permite reportar cuantas solicitudes o estudiantes hay sin recorrer toda la cola.
    public int size() {
        return this.tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public void clear() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0;
    }

    // Hace posible revisar la cola con for-each sin desencolar sus elementos.
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = primero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
