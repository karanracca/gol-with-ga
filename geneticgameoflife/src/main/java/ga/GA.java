package ga;

import life.Life;
import ui.UI;
import java.util.*;
import library.Library;

public class GA {

    private static final int FRAME_SIZE = Library.FRAME_SIZE;
    private static final int GRID_SIZE = Library.GRID_SIZE;
    private static final int POPULATION_SIZE = Library.POPULATION_SIZE;
    private static final int TOP_FITTEST_COEFFICIENT = Library.TOP_FITTEST_COEFFICIENT;
    private static final int LEAST_FITTEST_COEFFICIENT = Library.LEAST_FITTEST_COEFFICIENT;
    private static final int MAX_GENERATIONS = Library.MAX_GENERATIONS;
    private static final double MUTATION_COEFFICIENT = Library.MUTATION_COEFFICIENT;
    private static final double MUTATION_FACTOR = Library.MUTATION_FACTOR;
    private static final int INITIAL_FACTOR = Library.INITIAL_FACTOR;

    private UI ui;
    private Population population;
    private static int generation = 0;
    private static double overallMaxFitness;

    private static Random random = new Random();

    public GA (UI ui, Population population) {
        this.ui = ui;
        this.population = population;
        ui.showFittestPattern();
        this.run();
    }

    private void run () {
        while (generation < MAX_GENERATIONS) {
            evaluatePopulation(this.population);

            //Display Overall max fitness screen
            DNA fittest = this.population.getFittestDNA();
            if (fittest.getFitness() > overallMaxFitness) {
                overallMaxFitness = fittest.getFitness();
                ui.displayFittest(fittest);
            }

            createNextGenerationPopulation();

            mutatePopulation();
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
        System.out.println("GENERATION: " + generation + " MAX FITNESS: " + this.population.getMaxFitness());
        Population newPopulation = new Population(GRID_SIZE);
        newPopulation.add(this.population.getTop(TOP_FITTEST_COEFFICIENT));
        population = wheelOfFortune(newPopulation);
    }

    private Population wheelOfFortune(Population newPopulation) {

        HashMap<DNA, Integer> probs = new HashMap<DNA, Integer>();
        double maxFitness = this.population.getMaxFitness();
        for (int i=TOP_FITTEST_COEFFICIENT + 1; i <= this.population.getPool().size() - LEAST_FITTEST_COEFFICIENT; i++) {
            DNA dna = this.population.getPool().get(i);
            double fitness = dna.getFitness();
            double ratio = fitness / maxFitness;
            int prob = (int) (ratio * 100);
            probs.put(dna, prob);
        }
        int nextGenPoolSize = calculateNextGenSize(probs);

        for (int i=TOP_FITTEST_COEFFICIENT + 1; i <= this.population.getPool().size() - LEAST_FITTEST_COEFFICIENT; i++) {
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

        newPopulation.fill(POPULATION_SIZE);

        return newPopulation;
    }

    private static int calculateNextGenSize(HashMap<DNA, Integer> probs) {
        int sum = 0;
        for (int i : probs.values()) {
            sum += i;
        }
        return sum;
    }

    private void mutatePopulation () {
        for (int i=0; i<this.population.getPool().size(); i++) {
            int randomNumber = random.nextInt(100);
            if (randomNumber < MUTATION_FACTOR) {
                this.population.getPool().get(i).mutateDNA(INITIAL_FACTOR, MUTATION_COEFFICIENT);
            }
        }
    }

}
