package src.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaEnlazada<T> implements Iterable<T> {
    // Variable personalizable (puedes cambiar 'cabeza' por 'primerNodo' o 'inicio')
    private Nodo<T> cabeza;

    public ListaEnlazada() {
        this.cabeza = null;
    }

    // Método primitivo para insertar al final de la lista
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
    }

    // Alias de agregar() para compatibilidad con llamadas a .add()
    public void add(T elemento) {
        agregar(elemento);
    }

    // Elimina la primera ocurrencia del elemento y retorna true si lo encontró
    public boolean remove(T elemento) {
        if (cabeza == null) return false;
        if (cabeza.getDato().equals(elemento)) {
            cabeza = cabeza.getSiguiente();
            return true;
        }
        Nodo<T> actual = cabeza;
        while (actual.getSiguiente() != null) {
            if (actual.getSiguiente().getDato().equals(elemento)) {
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public void clear() {
        this.cabeza = null; // Se pierde el rastro del primer eslabón
    }

    // Implementación de Iterable para permitir el uso en bucles for-each
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

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
