package ga;

import java.util.Arrays;
import java.util.Random;

import util.Util;

public class DNA {
    private boolean[] grid;
    private boolean[] finalGrid;
    private double fitness;
    private int gridSize;
    private static Random random = new Random();

    public DNA(int gridSize) {
        this.gridSize = gridSize;
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

    public void mutateDNA(int initialFactor, double mutationCoefficient) {
        int initialPatternGridSize = this.gridSize / initialFactor;
            boolean tempOneD[] = new boolean[this.gridSize * this.gridSize];
            boolean tempTwoD[][] = new boolean[this.gridSize][this.gridSize];
            Util.convert1Dto2D(this.grid, tempTwoD);
            int min = this.gridSize / 2 - initialPatternGridSize / 2;
            int max = this.gridSize / 2 - initialPatternGridSize / 2 + initialPatternGridSize;
            int totalCells = initialPatternGridSize * initialPatternGridSize;
            int numberOfCellChange = (int) (mutationCoefficient * totalCells);
            for (int k = 0; k < numberOfCellChange; k++) {
                int i = random.nextInt((max - min) + 1) + min;
                int j = random.nextInt((max - min) + 1) + min;
                tempTwoD[i][j] = !tempTwoD[i][j];
            }
            Util.convert2Dto1D(tempTwoD, tempOneD);
            this.setGrid(tempOneD);
        }
}
