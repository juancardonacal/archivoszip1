package src.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MiColaDatos<T> implements Iterable<T> {
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio; // <-- 1. Añadimos esta variable para contar

    public MiColaDatos() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0; // Al principio no hay nadie
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
        tamanio++; // <-- 2. Si entra alguien, sumamos 1
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
        tamanio--; // <-- 3. Si se sale alguien, restamos 1
        return valor;
    }

    // <-- 4. ESTE ES EL MÉTODO QUE TE FALTA Y RESUELVE EL ERROR
    public int size() {
        return this.tamanio;
    }

    public boolean estaVacia() {
        return primero == null;
    }

    public void clear() {
        this.primero = null;
        this.ultimo = null;
        this.tamanio = 0; // <-- 5. Si se limpia, el contador vuelve a cero
    }

    // Implementación de Iterable para permitir el uso en bucles for-each
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
