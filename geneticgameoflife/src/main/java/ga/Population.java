package ga;

import util.Util;

import java.util.*;

public class Population {


    private static final int INITIAL_FACTOR = 10;
    private ArrayList<DNA> pool;
    private int gridSize;
    private static Random random = new Random();

    public Population(int size, int gridSize) {
        this.pool = new ArrayList<DNA>();
        this.gridSize = gridSize;
        setInitialPopulation(size, gridSize);
    }

    public Population() {
        this.pool = new ArrayList<DNA>();
    }

    private void setInitialPopulation(int size, int gridSize) {
        for (int i = 0; i < size; i++) {
            DNA dna = new DNA(gridSize);
            dna.setGrid(generateRandomInitialPattern());
            this.pool.add(dna);
        }
    }

    public boolean[] generateRandomInitialPattern() {
        int initialPatternGridSize = this.gridSize / INITIAL_FACTOR;
        int max = this.gridSize / 2 - initialPatternGridSize / 2 + initialPatternGridSize;
        boolean[] randomPattern = new boolean[this.gridSize * this.gridSize];
        boolean[][] twoDRandomPattern = new boolean[this.gridSize][this.gridSize];
        for (int i = this.gridSize / 2 - initialPatternGridSize / 2; i <= max; i++) {
            for (int j = this.gridSize / 2 - initialPatternGridSize / 2; j <= max; j++) {
                twoDRandomPattern[i][j] = random.nextBoolean();
            }
        }
        Util.convert2Dto1D(twoDRandomPattern, randomPattern);
        return randomPattern;

    }

    private static Comparator<DNA> dnaComparator = new Comparator<DNA>() {
        public int compare(DNA o1, DNA o2) {
            if(o2.getFitness() > o1.getFitness()) {
                return 1;
            } else if(o2.getFitness() < o1.getFitness()) {
                return -1;
            }
            return 0;
        }
    };

    public ArrayList<DNA> getTop (int coefficient) {
        Collections.sort(this.pool, dnaComparator);
        ArrayList list = new ArrayList<DNA>();
        for (int i=0; i < coefficient; i++ ) {
            list.add(this.pool.get(i));
        }
        return list;
    }

    public DNA getFittestDNA() {
        DNA fittest = this.pool.get(0);
        double maxFitness = 0;
        for (DNA dna : this.pool) {
            if (dna.getFitness() > maxFitness) {
                maxFitness = dna.getFitness();
                fittest = dna;
            }
        }
        return fittest;
    }

    public double getMaxFitness() {
        return getFittestDNA().getFitness();
    }

    public ArrayList<DNA> getPool() {
        return pool;
    }

    public void setPool(ArrayList<DNA> pool) {
        this.pool = pool;
    }

    public void add(DNA dna) {
        this.pool.add(dna);
    }

    public void add(ArrayList<DNA> list) {
        this.pool.addAll(list);
    }

    public ArrayList<DNA> removeLast(int coefficient) {
        return new ArrayList<DNA>(this.pool.subList(coefficient, this.pool.size()));
    }
}
