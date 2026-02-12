package mg.arovy.taquinsample.model;


public class Plateau {

    private final int size;   // nombre total de cases
    private int[] grid;       // tableau 1D

    public Plateau(int dimension) {
        this.size = dimension * dimension; // ex: 4 → 16 cases
        initGrid();
    }

    private void initGrid() {
        grid = new int[size];

        for (int i = 0; i < size - 1; i++) {
            grid[i] = i + 1;
        }
        grid[size - 1] = 0; // case vide
    }

    public int getCell(int index) {
        return grid[index];
    }

    public int getSize() {
        return size;
    }
}
