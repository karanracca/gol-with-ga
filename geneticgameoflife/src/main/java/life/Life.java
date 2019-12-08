package life;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import ga.DNA;
import ui.UI;
import library.Library;
import util.Util;

import javax.swing.JFrame;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Life extends Canvas {

    private static Random random = new Random();
    //private static final long serialVersionUID = 1L;
    public static int MAX_ALIVE = Library.MAX_ALIVE;
    public static double MIN_ALIVE = Library.MIN_ALIVE;
    public static int SPEED = Library.SPEED;
    public static boolean CREATE_CSV = false;

    private int frameSize;
    private int gridSize;
    private boolean[] cGrid;
    private boolean[] pGrid;
    private int generation;
    private boolean continueGame;
    private ArrayList history;
    private ArrayList<Double> averageLifeList;
    private double averageLife;
    private FileWriter fileWriter;

    private UI ui;

    public Life(int frameSize, int gridSize) {
        this.ui = new UI(frameSize, gridSize);
        this.gridSize = gridSize;
        this.frameSize = frameSize;
        this.generation = 0;
        this.continueGame = true;
        history = new ArrayList<boolean[]>();
        averageLifeList = new ArrayList<Double>();

    }

    public void startGame(DNA dna) throws IOException {
        if (CREATE_CSV) {
            fileWriter = new FileWriter("graph.csv");
        }
        this.ui.showGOL();
        double fitness;
        this.start(dna);
        fitness = averageLife;
        dna.setFitness(fitness);
        if (CREATE_CSV) {
            fileWriter.flush();
            fileWriter.close();
        }
    }

    private void start(DNA dna) {
        cGrid = new boolean[this.ui.getPixelsLength()];
        pGrid = new boolean[this.ui.getPixelsLength()];
        if (null == dna) {
            for (int i = 0; i < cGrid.length; i++) {
                cGrid[i] = random.nextInt(100) / 100.0 > 0.8;
            }
        } else {
            cGrid = Arrays.copyOf(dna.getGrid(), dna.getGrid().length);
        }
        boolean finalGrid[] = this.run();
        dna.setFinalGrid(finalGrid);
    }

    public boolean[] run() {
        while (continueGame) {
            update();
            try {
                Thread.sleep(SPEED);
                render();
            } catch (Exception e) {
                System.out.println("Thread sleep exception");
            }
        }
        return cGrid;
    }

    public void update() {
        boolean tempcGrid[][] = new boolean[gridSize][gridSize];
        boolean temppGrid[][] = new boolean[gridSize][gridSize];
        Util.convert1Dto2D(cGrid, tempcGrid);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                boolean state = tempcGrid[i][j];
                int neighbors = countNeighbors(tempcGrid, i, j);
                if (state == false && neighbors == 3) {
                    temppGrid[i][j] = true;
                } else if (state == true && (neighbors < 2 || neighbors > 3)) {
                    temppGrid[i][j] = false;
                } else {
                    temppGrid[i][j] = state;
                }
            }
        }
        Util.convert2Dto1D(temppGrid, pGrid);
        checkGenerationGrowth();
        cGrid = pGrid;
    }

    private void render() throws IOException {
        this.ui.render(cGrid);
        generation++;
        double percentLive = ((double) getLiveCellCount() / this.ui.getPixelsLength()) * 100;
        //frame.setTitle("Generation: " + generation + " Live cells: " + percentLive + "%");
        if (CREATE_CSV) {
            createCsv(generation, getLiveCellCount());
        }
        history.add(Arrays.copyOf(cGrid, cGrid.length));
    }

    private int countNeighbors(boolean grid[][], int x, int y) {
        int sum = 0;
        int row = 0;
        int col = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                col = (x + i + gridSize) % gridSize;
                row = (y + j + gridSize) % gridSize;
                sum += grid[col][row] ? 1 : 0;
            }
        }
        sum -= grid[x][y] ? 1 : 0;
        return sum;
    }

    private void checkGenerationGrowth() {
        boolean match[];
        boolean contains = false;
        int totalLiveCells = getLiveCellCount();
        double percentAlive = (((double) totalLiveCells) / (gridSize * gridSize)) * 100;
        calculateAverageLife(percentAlive);
        if (generation > 100) {
            if (percentAlive > MAX_ALIVE || percentAlive < MIN_ALIVE) {
                this.continueGame = false;
                return;
            }
        }
        for (int i = 0; i < history.size(); i++) {
            match = (boolean[]) history.get(i);
            if (Arrays.equals(cGrid, match)) {
                contains = true;
                break;
            }
        }
        if (contains) {
            this.continueGame = false;
        } else {
            this.continueGame = true;
        }
    }

    private int getLiveCellCount() {
        int count = 0;
        for (boolean b : cGrid) {
            if (b) count++;
        }
        return count;
    }

    private void calculateAverageLife(double percentAlive) {
        averageLifeList.add(percentAlive);
        double sum = 0;
        for (Double d : averageLifeList) {
            sum += d;
        }
        averageLife = sum / averageLifeList.size();
    }

    public JFrame getFrame() {
        return this.ui.getFrame();
    }

    private void createCsv(int generation, int livingCells) throws IOException {
        fileWriter.append(String.valueOf(generation));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(livingCells));
        fileWriter.append("\n");
    }

}
