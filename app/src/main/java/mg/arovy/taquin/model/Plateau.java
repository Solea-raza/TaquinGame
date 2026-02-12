package mg.arovy.taquin.model;

// TODO : mettre le plateau en une seule dimension
public class Plateau {
    private final int rows;
    private final int cols;
    private int[][] grid;
    public Plateau(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        initGrid();
    }

    private void initGrid() {
        grid = new int[rows][cols];
        int number = 1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (number < rows * cols) {
                    grid[row][col] = number++;
                } else {
                    grid[row][col] = 0; // case vide
                }
            }
        }
    }
    public int getCell(int row, int col) {
        return grid[row][col];
    }

    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }

}

