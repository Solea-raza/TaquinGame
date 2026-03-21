package mg.arovy.taquin.IA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class A_star {
    private static class Node implements Comparable<Node> {
        int[] grid;
        int g;
        int h;
        int movedIndex;
        Node parent;

        Node(int[] grid, int g, int h, int movedIndex, Node parent) {
            this.grid = grid;
            this.g = g;
            this.h = h;
            this.movedIndex = movedIndex;
            this.parent = parent;
        }

        int f() {return g + h; }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.f(), other.f());
        }
    }

    private final int dimension;
    private final int size;
    private final int[] goalGrid;

    private final int[] goalPositions;

    public A_star(int dimension, int[] goalGrid){
        this.dimension = dimension;
        this.size = dimension * dimension;
        this.goalGrid = goalGrid.clone();
        this.goalPositions = new int[size];

        for (int i = 0; i < size; i++) {
            goalPositions[goalGrid[i]] = i;
        }
    }

    public List<Integer> solve(int[] startGrid) {
        if (Arrays.equals(startGrid, goalGrid)) return new ArrayList<>();

        //explore et prend le noeud avec le plus petit f
        PriorityQueue<Node> openSet = new PriorityQueue<>();

        //Ensemble des etats deja explores (evite les boucles)
        Map<String, Integer> visited = new HashMap<>();

        //Noeud de depart
        int startH = manhattan(startGrid);
        Node startNode = new Node(startGrid.clone(), 0, startH, -1, null);
        openSet.add(startNode);
        visited.put(toKey(startGrid), 0);

        // boucle principale de A*
        while (!openSet.isEmpty()) {

            //Prend le noeud avec le plus petit f(n)
            Node current = openSet.poll();

            //Verifier si on a atteint le but
            if (Arrays.equals(current.grid, goalGrid)){
                return reconstructPath(current);
            }

            int emptyIndex = findEmpty(current.grid);

            for (int neighborIndex : getNeighbors(emptyIndex)){

                // effectue le mouvement
                int[] newGrid = current.grid.clone();
                newGrid[emptyIndex] = newGrid[neighborIndex];
                newGrid[neighborIndex] = 0;

                int newG = current.g + 1;
                String key = toKey(newGrid);

                //Ne pas revisiter un etat avec un cout plus eleve
                if (visited.containsKey(key) && visited.get(key) <= newG) continue;

                visited.put(key, newG);

                int newH = manhattan(newGrid);

                // neighborIndex = tuile qui s'est déplacée vers la case vide
                Node neighbor = new Node(newGrid, newG, newH, neighborIndex, current);
                openSet.add(neighbor);
            }
        }

        // Aucune solution trouvee (grille insoble)
        return null;
    }

    // heuristique : distance de Manhattan
    private int manhattan(int[] grid){
        int total = 0;
        for (int i = 0; i < size; i++) {
            int value = grid[i];
            if (value == 0) continue; // case vide ignoree

            int currentRow = i / dimension;
            int currentCol = i % dimension;

            int targetIndex = goalPositions[value];
            int targetRow = targetIndex / dimension;
            int targetCol = targetIndex % dimension;

            total += Math.abs(currentRow - targetRow) + Math.abs(currentCol - targetCol);
        }
        return total;
    }

    //retourne les indices adjacent valide a emptyIndex
    private List<Integer> getNeighbors(int emptyIndex) {
        List<Integer>neighbors = new ArrayList<>();
        int row = emptyIndex / dimension;
        int col = emptyIndex % dimension;

        if (row > 0) neighbors.add(emptyIndex - dimension); // vers le haut
        if (row < dimension - 1) neighbors.add(emptyIndex + dimension); // vres le bas
        if (col > 0) neighbors.add(emptyIndex - 1); // vers la gauche
        if (col < dimension - 1) neighbors.add(emptyIndex + 1);

        return neighbors;
    }

    // Trouver l'index de la case vide
    private int findEmpty(int[] grid) {
        for (int i = 0; i < size; i++) if (grid[i] == 0) return i;
        return -1;
    }

    //Convertir une grille en String pour l'utiliser comme cle de HashMap
    private String toKey(int[] grid) {
        return Arrays.toString(grid);
    }

    private List<Integer> reconstructPath(Node node) {
        List<Integer> path = new ArrayList<>();
        Node current = node;

        while (current.parent != null) {
            path.add(current.movedIndex);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }
}
