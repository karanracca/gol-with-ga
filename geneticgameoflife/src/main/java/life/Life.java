package life;

import ga.DNA;
import util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Life extends Canvas {

    private static final long serialVersionUID = 1L;
    private static final int MAX_ALIVE = 90;
    private static final double MIN_ALIVE = 0.5;
    private static final int SPEED = 0;

    private int frameSize;
    private int gridSize;
    public static String title = "Game Of Life";

    private static Random random = new Random();
    private BufferedImage image;
    private int[] pixels;
    private boolean[] cGrid;
    private boolean[] pGrid;
    private int generation;
    private int livingCells;
    private boolean continueGame;
    private ArrayList history;
    private JFrame frame;

    public Life(int frameSize, int gridSize) {
        Dimension d = new Dimension(frameSize, frameSize);
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
        this.frameSize = frameSize;
        this.gridSize = gridSize;
        frame = new JFrame();
    }

    private void initiateGame() {
        this.generation = 0;
        this.livingCells = 0;
        this.continueGame = true;
        history = new ArrayList<boolean[]>();
        image = new BufferedImage(gridSize, gridSize, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public void start(DNA dna) {
        cGrid = new boolean[pixels.length];
        pGrid = new boolean[pixels.length];
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
                if(generation == 1) {
//                    Thread.sleep(1000);
                } else {
                    Thread.sleep(SPEED);
                }
            } catch (Exception e) {

            }
            render();
        }

        return cGrid;
//        System.out.println("Last Generation: " + generation);
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

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (null == bs) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = cGrid[i] ? 0xffffff : 0;
        }

        g.drawImage(image, 0, 0, frameSize, frameSize, null);
        g.dispose();
        bs.show();
        generation++;
        livingCells += getLiveCellCount();

        history.add(Arrays.copyOf(cGrid, cGrid.length));
    }

    public void startGame(DNA dna) {
        double fitness;
        this.initiateGame();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        frame.add(this);
        frame.pack();
        this.start(dna);
        fitness = (double)livingCells / generation;
        dna.setFitness((int)fitness);
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
        if (generation > 10) {
            int totalLiveCells = getLiveCellCount();
            double percentAlive = (((double) totalLiveCells) / (gridSize * gridSize)) * 100;
            if (percentAlive > MAX_ALIVE || percentAlive < MIN_ALIVE) {
//                System.out.println("Terminated due to population problem: " + percentAlive + "%");
                this.continueGame = false;
                return;
            }
        }
        for (int i = 0; i < history.size(); i++) {
            match = (boolean[]) history.get(i);
            if (Arrays.equals(cGrid, match)) {
//                System.out.println("Terminated due to repeated pattern");
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

    public JFrame getFrame() {
        return this.frame;
    }

}
