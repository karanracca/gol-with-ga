package ga;

import life.Life;
import ui.UI;
import java.awt.image.BufferedImage;
import java.util.*;

public class GA {

    private UI ui;

    //TODO Add this to generic file
    private static final int FRAME_SIZE = 500;
    private static final int GRID_SIZE = 100;
    private static final int POPULATION_SIZE = 100;
    private static final int TOP_FITTEST_COEFFICIENT = 10;
    private static final int LEAST_FITTEST_COEFFICIENT = 10;

    //private static Random random = new Random();
    private static BufferedImage image;


    private static final int MAX_GENERATIONS = 100;
    //private static final int POPULATION_SIZE = 100;
    private static final double MUTATION_COEFFICIENT = 0.01;
    private static final double MUTATION_FACTOR = 20;
    //private static final int INITIAL_FACTOR = 10;
    //private static JFrame frame;

    private Population population;
    private static ArrayList<DNA> nextGenPool;
    private static int generation = 0;
    private static boolean[] fittestPattern;
    private static double overallMaxFitness;


    public GA (UI ui, Population population) {
        this.ui = ui;
        this.population = population;
        ui.showFittestPattern(this);

        this.run();
    }

    private void run () {
        while (generation < MAX_GENERATIONS) {
            evaluatePopulation(this.population);
//            createNextGenPool(ga);

            //Display Overall max fitness screen
            DNA fittest = this.population.getFittestDNA();
            if (fittest.getFitness() > overallMaxFitness) {
                overallMaxFitness = fittest.getFitness();
                ui.displayFittest(fittest);
            }

            createNextGenerationPopulation();
        }
    }

    private static void evaluatePopulation(Population population) {
        for (int i = 0; i < population.getPool().size(); i++) {
            DNA dna = population.getPool().get(i);
            Life life = new Life(FRAME_SIZE, GRID_SIZE);
            life.startGame(dna);
            life.getFrame().dispose();
        }
        generation++;
        System.out.println(generation);
    }

    private void createNextGenerationPopulation() {
        // Collections.sort(this.population.getPool(), dnaComparator);
        System.out.println("GENERATION: " + generation + " MAX FITNESS: " + this.population.getMaxFitness());
        Population newPopulation = new Population();
        newPopulation.add(this.population.getTop(TOP_FITTEST_COEFFICIENT));

//        for (int i = 0; i < POPULATION_SIZE / 2; i++) {
//            DNA currentDNA = population.getPool().get(i);
//            DNA dna1 = new DNA(GRID_SIZE);
//            dna1.setGrid(currentDNA.getGrid());
//            DNA dna2 = new DNA(GRID_SIZE);
//            dna2.setGrid(currentDNA.getGrid());
//            mutateDNA(dna1);
//            mutateDNA(dna2);
//            tempPop.add(dna1);
//            tempPop.add(dna2);
//        }
        population = wheelOfFortune(newPopulation);
//        System.out.println("POP SIZE: " + population.getPool().size());

//        population = new Population();
//        for (int i = 0; i < POPULATION_SIZE; i++) {
//            int randomNumber = random.nextInt(nextGenPool.size());
//            DNA dna = nextGenPool.get(randomNumber);
//            mutateDNA(dna);
//            population.add(dna);
//        }


    }

    private Population wheelOfFortune(Population newPopulation) {

        HashMap<DNA, Integer> probs = new HashMap<DNA, Integer>();
        double maxFitness = this.population.getMaxFitness();
        for (int i=TOP_FITTEST_COEFFICIENT; i<this.population.getPool().size() - LEAST_FITTEST_COEFFICIENT; i++) {
            DNA dna = this.population.getPool().get(i);
            double fitness = dna.getFitness();
            double ratio = fitness / maxFitness;
            int prob = (int) (ratio * 100);
            probs.put(dna, prob);
        }
        int nextGenPoolSize = calculateNextGenSize(probs);

        for (int i=TOP_FITTEST_COEFFICIENT; i<this.population.getPool().size() - LEAST_FITTEST_COEFFICIENT; i++) {
            DNA dna = this.population.getPool().get(i);
            int prob = probs.get(dna);
            double ratio = (double) prob / nextGenPoolSize;
            int times = (int) (ratio * 100);
            for (int j = 0; j < times; j++) {
                DNA currentDna = new DNA(GRID_SIZE);
                currentDna.setGrid(dna.getGrid());
                newPopulation.add(currentDna);
            }
        }

        return newPopulation;

        //System.out.println("GENERATION: " + generation + " MAX FITNESS: " + getMaxFitness(ga));
    }


    //TODO Change this logic
    private static int calculateNextGenSize(HashMap<DNA, Integer> probs) {
        int sum = 0;
        for (int i : probs.values()) {
            sum += i;
        }
        return sum;
    }

//    private static void mutateDNA(DNA dna) {
//        boolean shouldMutate = false;
//        int randomNumber = random.nextInt(100);
//        shouldMutate = randomNumber < MUTATION_FACTOR;
//        if (shouldMutate) {
//            int initialPatternGridSize = GRID_SIZE / INITIAL_FACTOR;
//            boolean tempOneD[] = new boolean[GRID_SIZE * GRID_SIZE];
//            boolean tempTwoD[][] = new boolean[GRID_SIZE][GRID_SIZE];
//            Util.convert1Dto2D(dna.getGrid(), tempTwoD);
//            int min = GRID_SIZE / 2 - initialPatternGridSize / 2;
//            int max = GRID_SIZE / 2 - initialPatternGridSize / 2 + initialPatternGridSize;
//            int totalCells = initialPatternGridSize * initialPatternGridSize;
//            int numberOfCellChange = (int) (MUTATION_COEFFICIENT * totalCells);
//            for (int k = 0; k < numberOfCellChange; k++) {
//                int i = random.nextInt((max - min) + 1) + min;
//                int j = random.nextInt((max - min) + 1) + min;
//                tempTwoD[i][j] = !tempTwoD[i][j];
//            }
//            Util.convert2Dto1D(tempTwoD, tempOneD);
//            dna.setGrid(tempOneD);
//        }
//    }

}
