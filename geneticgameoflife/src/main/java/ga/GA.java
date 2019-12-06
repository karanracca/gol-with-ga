package ga;

import life.Life;
import util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.*;

public class GA extends Canvas {

    private static Random random = new Random();
    private static BufferedImage image;
    private static int[] pixels;

    private static final int FRAME_SIZE = 500;
    private static final int GRID_SIZE = 100;
    private static final int MAX_GENERATIONS = 100;
    private static final int POPULATION_SIZE = 100;
    private static final double MUTATION_COEFFICIENT = 0.01;
    private static final double MUTATION_FACTOR = 20;
    private static final int INITIAL_FACTOR = 10;
    private static JFrame frame;

    private static Population population;
    private static ArrayList<DNA> nextGenPool;
    private static int generation = 0;
    private static boolean[] fittestPattern;
    private static double overallMaxFitness;
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

    public GA() {
        Dimension d = new Dimension(FRAME_SIZE, FRAME_SIZE);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
        frame = new JFrame();
        image = new BufferedImage(GRID_SIZE, GRID_SIZE, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    }

    public static void main(String arg[]) {
        GA ga = new GA();
        Life.MIN_ALIVE = 5;
        Life.MAX_ALIVE = 95;
        Life.SPEED = 0;
        showFittestPattern(ga);
        setInitialPopulation();
        while (generation < MAX_GENERATIONS) {
            evaluatePopulation();
//            createNextGenPool(ga);
            createNextGenerationPopulation(ga);
        }
    }

    public static void showFittestPattern(GA ga) {
        frame.setTitle("Fittest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(ga);
        frame.pack();
    }

    private static void createNextGenerationPopulation(GA ga) {
        Collections.sort(population.getPool(), dnaComparator);
        System.out.println("GENERATION: " + generation + " MAX FITNESS: " + getMaxFitness(ga));
        Population tempPop = new Population();
        for (int i = 0; i < POPULATION_SIZE / 2; i++) {
            DNA currentDNA = population.getPool().get(i);
            DNA dna1 = new DNA(GRID_SIZE);
            dna1.setGrid(currentDNA.getGrid());
            DNA dna2 = new DNA(GRID_SIZE);
            dna2.setGrid(currentDNA.getGrid());
            mutateDNA(dna1);
            mutateDNA(dna2);
            tempPop.add(dna1);
            tempPop.add(dna2);
        }
        population = tempPop;
//        System.out.println("POP SIZE: " + population.getPool().size());

//        population = new Population();
//        for (int i = 0; i < POPULATION_SIZE; i++) {
//            int randomNumber = random.nextInt(nextGenPool.size());
//            DNA dna = nextGenPool.get(randomNumber);
//            mutateDNA(dna);
//            population.add(dna);
//        }


    }

    private static void setInitialPopulation() {
        population = new Population();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            DNA dna = new DNA(GRID_SIZE);
            dna.setGrid(generateRandomInitialPattern());
            population.add(dna);
        }
    }

    private static void evaluatePopulation() {
        Life life;
        for (int i = 0; i < population.getPool().size(); i++) {
            DNA dna = population.getPool().get(i);
            life = new Life(FRAME_SIZE, GRID_SIZE);
            life.startGame(dna);
            life.getFrame().dispose();
        }
        generation++;
    }

    public static boolean[] generateRandomInitialPattern() {
        int initialPatternGridSize = GRID_SIZE / INITIAL_FACTOR;
        int max = GRID_SIZE / 2 - initialPatternGridSize / 2 + initialPatternGridSize;
        boolean[] randomPattern = new boolean[GRID_SIZE * GRID_SIZE];
        boolean[][] twoDRandomPattern = new boolean[GRID_SIZE][GRID_SIZE];
//        for (int i = initialPatternGridSize; i <= 2 * initialPatternGridSize; i++) {
//            for (int j = initialPatternGridSize; j <= 2 * initialPatternGridSize; j++) {
        for (int i = GRID_SIZE / 2 - initialPatternGridSize / 2; i <= max; i++) {
            for (int j = GRID_SIZE / 2 - initialPatternGridSize / 2; j <= max; j++) {
                twoDRandomPattern[i][j] = random.nextBoolean();
            }
        }
        Util.convert2Dto1D(twoDRandomPattern, randomPattern);
        return randomPattern;

    }

    private static void createNextGenPool(GA ga) {
        nextGenPool = new ArrayList<DNA>();
        HashMap<DNA, Integer> probs = new HashMap<DNA, Integer>();
        double maxFitness = getMaxFitness(ga);
        for (DNA dna : population.getPool()) {
            double fitness = dna.getFitness();
            double ratio = (double) fitness / maxFitness;
            int prob = (int) (ratio * 100);
            probs.put(dna, prob);
        }
        int nextGenPoolSize = calculateNextGenSize(probs);
        for (DNA dna : population.getPool()) {
            int prob = probs.get(dna);
            double ratio = (double) prob / nextGenPoolSize;
            int times = (int) (ratio * 100);
            for (int i = 0; i < times; i++) {
                DNA currentDna = new DNA(GRID_SIZE);
                currentDna.setGrid(dna.getGrid());
                nextGenPool.add(currentDna);
            }
        }
        System.out.println("GENERATION: " + generation + " MAX FITNESS: " + getMaxFitness(ga));
    }

    private static double getMaxFitness(GA ga) {
        double maxFitness = 0;
        for (DNA dna : population.getPool()) {
            if (dna.getFitness() > maxFitness) {
                maxFitness = dna.getFitness();
            }
            if (dna.getFitness() > overallMaxFitness) {
                overallMaxFitness = dna.getFitness();
                fittestPattern = Arrays.copyOf(dna.getGrid(), dna.getGrid().length);
            }
            ga.render();
        }
        return maxFitness;
    }

    public void render() {
        frame.setTitle("Fittest (Fitness: " + overallMaxFitness + ")");
        BufferStrategy bs = getBufferStrategy();
        if (null == bs) {
            createBufferStrategy(3);
            render();
            return;
        }
        Graphics g = bs.getDrawGraphics();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = fittestPattern[i] ? 0xffffff : 0;
        }
        try {
            File outputfile = new File("image.bmp");
            ImageIO.write(image, "bmp", outputfile);
        } catch (Exception e) {
        }
        g.drawImage(image, 0, 0, FRAME_SIZE, FRAME_SIZE, null);
        g.dispose();
        bs.show();
    }

    private static int calculateNextGenSize(HashMap<DNA, Integer> probs) {
        int sum = 0;
        for (int i : probs.values()) {
            sum += i;
        }
        return sum;
    }

    private static void mutateDNA(DNA dna) {
        boolean shouldMutate = false;
        int randomNumber = random.nextInt(100);
        shouldMutate = randomNumber < MUTATION_FACTOR;
        if (shouldMutate) {
            int initialPatternGridSize = GRID_SIZE / INITIAL_FACTOR;
            boolean tempOneD[] = new boolean[GRID_SIZE * GRID_SIZE];
            boolean tempTwoD[][] = new boolean[GRID_SIZE][GRID_SIZE];
            Util.convert1Dto2D(dna.getGrid(), tempTwoD);
            int min = GRID_SIZE / 2 - initialPatternGridSize / 2;
            int max = GRID_SIZE / 2 - initialPatternGridSize / 2 + initialPatternGridSize;
            int totalCells = initialPatternGridSize * initialPatternGridSize;
            int numberOfCellChange = (int) (MUTATION_COEFFICIENT * totalCells);
            for (int k = 0; k < numberOfCellChange; k++) {
                int i = random.nextInt((max - min) + 1) + min;
                int j = random.nextInt((max - min) + 1) + min;
                tempTwoD[i][j] = !tempTwoD[i][j];
            }
            Util.convert2Dto1D(tempTwoD, tempOneD);
            dna.setGrid(tempOneD);
        }
    }


}
