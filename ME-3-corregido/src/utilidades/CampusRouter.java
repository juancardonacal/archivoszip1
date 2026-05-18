package src.utilidades;

public class CampusRouter {
    // Busca la ruta más corta usando el clásico algoritmo de Dijkstra
    public static void calcularRutaMasCorta(int[][] matrizAdyacencia, int origen, int destino, String[] nombresEdificios) {
        int n = matrizAdyacencia.length;
        int[] distancias = new int[n];
        boolean[] visitados = new boolean[n];
        int[] padres = new int[n];

        for (int i = 0; i < n; i++) {
            distancias[i] = Integer.MAX_VALUE;
            visitados[i] = false;
            padres[i] = -1;
        }

        distancias[origen] = 0;

        for (int i = 0; i < n - 1; i++) {
            int u = trackingDistanciaMinima(distancias, visitados);
            if (u == -1) break;
            visitados[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visitados[v] && matrizAdyacencia[u][v] != 0 && distancias[u] != Integer.MAX_VALUE) {
                    int nuevaDistancia = distancias[u] + matrizAdyacencia[u][v];
                    if (nuevaDistancia < distancias[v]) {
                        distancias[v] = nuevaDistancia;
                        padres[v] = u;
                    }
                }
            }
        }

        if (distancias[destino] == Integer.MAX_VALUE) {
            System.out.println("No existe una ruta conectada entre los edificios especificados.");
            return;
        }

        System.out.println("\n--- RESULTADO ENCONTRADO ---");
        System.out.print("Ruta más corta: ");
        imprimirCaminoRecurrente(destino, padres, nombresEdificios, matrizAdyacencia);
        System.out.println("\nDistancia TOTAL: " + distancias[destino] + " metros.");
    }

    private static int trackingDistanciaMinima(int[] distancias, boolean[] visitados) {
        int min = Integer.MAX_VALUE;
        int indiceMin = -1;
        for (int v = 0; v < distancias.length; v++) {
            if (!visitados[v] && distancias[v] <= min) {
                min = distancias[v];
                indiceMin = v;
            }
        }
        return indiceMin;
    }

    private static void imprimirCaminoRecurrente(int nodoActual, int[] padres, String[] nombres, int[][] grafo) {
        if (padres[nodoActual] == -1) {
            System.out.print(nombres[nodoActual]);
            return;
        }
        imprimirCaminoRecurrente(padres[nodoActual], padres, nombres, grafo);
        System.out.print(" -> " + nombres[nodoActual] + " (" + grafo[padres[nodoActual]][nodoActual] + "m)");
    }
}