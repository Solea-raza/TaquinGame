package mg.arovy.taquin.model;

public class Plateau {

    private final int size;
    private int[] grid;
    private int[] startGrid;
    private int[] goalGrid;

    private GameState state;
    public GameState getState() { return state; }

    public Plateau(int dimension) {
        this.size = dimension * dimension;
        this.grid = new int[size];
        this.startGrid = new int[size];
        this.goalGrid = new int[size];
        initGrid();
    }

    public void setState(GameState newState) { this.state = newState; }

    private void initGrid() {
        for (int i = 0; i < size - 1; i++) grid[i] = i + 1;
        grid[size - 1] = 0;
    }

    private void initGoalGrid() {
        for (int i = 0; i < size - 1; i++) goalGrid[i] = i + 1;
        goalGrid[size - 1] = 0;
    }

    public int getCell(int index) { return grid[index]; }
    public int getSize()          { return size; }
    public int[] getGoalGrid()    { return goalGrid.clone(); } // MODIFIÉ : ajout

    //getter et setters
    public int[] getStartGrid() {
        return startGrid.clone();
    }
    public void setStartGrid(int[] grid){
        System.arraycopy(grid, 0, this.startGrid, 0, size);
    }
    public void setGoalGrid(int[] grid){
        System.arraycopy(grid, 0, this.goalGrid, 0, size);
    }
    public int[] getCurrentGrid() {
        return grid.clone();
    }
    public void setCurrentGrid(int[] current) {
        System.arraycopy(current, 0, this.grid, 0, size);
    }

    public void prepareStart() {
        initGrid();
        state = GameState.CONFIG_START;
    }


    public void randomizeStart() {
        initGrid();
        shuffleArray(grid);
    }

    public void confirmStart() {
        System.arraycopy(grid, 0, startGrid, 0, size);
        initGoalGrid();
        state = GameState.CONFIG_END;
    }

    public void randomizeGoal() {
        initGoalGrid();
        shuffleArray(goalGrid);
    }

    // MODIFIÉ : ajout (remplace setState PLAYING dans MainActivity)
    public void confirmGoal() {
        restoreStart();
        state = GameState.PLAYING;
    }

    // MODIFIÉ : ajout
    public void reset() {
        restoreStart();
        state = GameState.PLAYING;
    }

    private void restoreStart() { // MODIFIÉ : ajout
        System.arraycopy(startGrid, 0, grid, 0, size);
    }

    public void startNewGame() {
        initGrid();
        state = GameState.PLAYING;
    }

    // MODIFIÉ : supprimé shuffle(), remplacé par randomizeStart()
    private void shuffleArray(int[] arr) {
        java.util.Random rng = new java.util.Random();
        for (int i = 0; i < arr.length; i++) {
            int j = rng.nextInt(arr.length);
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
    }

    public void swap(int index1, int index2) {
        int tmp = grid[index1]; grid[index1] = grid[index2]; grid[index2] = tmp;
    }

    // MODIFIÉ : ajout
    public void swapGoal(int i1, int i2) {
        int tmp = goalGrid[i1]; goalGrid[i1] = goalGrid[i2]; goalGrid[i2] = tmp;
    }

    public boolean move(int index) {
        if (state == GameState.CONFIG_START || state == GameState.CONFIG_END) return false;

        int dimension = (int) Math.sqrt(size);
        int emptyIndex = findEmpty();
        int row = index / dimension, col = index % dimension;
        int emptyRow = emptyIndex / dimension, emptyCol = emptyIndex % dimension;

        if ((Math.abs(row - emptyRow) + Math.abs(col - emptyCol)) == 1) {
            grid[emptyIndex] = grid[index];
            grid[index] = 0;
            if (isSolved()) state = GameState.FINISHED;
            return true;
        }
        return false;
    }

    private int findEmpty() {
        for (int i = 0; i < size; i++) if (grid[i] == 0) return i;
        return -1;
    }

    // MODIFIÉ : compare à goalGrid au lieu de l'ordre canonique
    public boolean isSolved() {
        for (int i = 0; i < size; i++) {
            if (grid[i] != goalGrid[i]) return false;
        }
        return true;
    }

}