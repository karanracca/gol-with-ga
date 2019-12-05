package ga;

import java.util.Arrays;

public class DNA {
    private boolean[] grid;
    private boolean[] finalGrid;
    private double fitness;

    public DNA(int gridSize) {
        grid = new boolean[gridSize];
        finalGrid = new boolean[gridSize];
    }

    public boolean[] getGrid() {
        return grid;
    }

    public void setGrid(boolean[] grid) {
        this.grid = Arrays.copyOf(grid, grid.length);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public boolean[] getFinalGrid() {
        return finalGrid;
    }

    public void setFinalGrid(boolean[] finalGrid) {
        this.finalGrid = Arrays.copyOf(finalGrid, finalGrid.length);
    }
}
