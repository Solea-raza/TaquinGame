package mg.arovy.taquin.model;

public class Plateau {

    private final int size;   // nombre total de cases
    private int[] grid;       // tableau 1D

    private GameState state;
    public GameState getState(){
        return state;
    }
    public Plateau(int dimension) {
        this.size = dimension * dimension; // ex: 4 → 16 cases
        initGrid();
    }
    public void setState(GameState newState) {
        this.state = newState;
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
    public void startNewGame() {
        initGrid();
        shuffle();
        state = GameState.PLAYING;
    }
    private void shuffle() {
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < size; i++) {
            int j = random.nextInt(size);

            int temp = grid[i];
            grid[i] = grid[j];
            grid[j] = temp;
        }
    }
    public void swap(int index1, int index2) {
        int temp = grid[index1];
        grid[index1] = grid[index2];
        grid[index2] = temp;
    }
    public boolean move(int index) {
        // Si on est en mode configuration, on ne bouge pas via move()
        if (state == GameState.CONFIG_START || state == GameState.CONFIG_END) {
            return false; // tout est géré via swap()
        }

        int dimension = (int) Math.sqrt(size);
        int emptyIndex = findEmpty();

        int row = index / dimension;
        int col = index % dimension;

        int emptyRow = emptyIndex / dimension;
        int emptyCol = emptyIndex % dimension;

        // Vérifie si la case est adjacente à la case vide
        if ((Math.abs(row - emptyRow) + Math.abs(col - emptyCol)) == 1) {
            grid[emptyIndex] = grid[index];
            grid[index] = 0;

            if (isSolved()) {
                state = GameState.FINISHED;
            }

            return true;
        }

        return false;
    }
    private int findEmpty() {
        for (int i = 0; i < size; i++) {
            if (grid[i] == 0) return i;
        }
        return -1; // au cas où, normalement impossible
    }
    public boolean isSolved() {
        for (int i = 0; i < size - 1; i++) {
            if (grid[i] != i + 1) {
                return false;
            }
        }
        return grid[size - 1] == 0;
    }
}

