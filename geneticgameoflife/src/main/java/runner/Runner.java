package runner;

import ga.DNA;
import ga.GA;
import life.Life;
import ui.UI;
import ga.Population;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;


public class Runner {

    private static final int FRAME_SIZE = 500;
    private static final int GRID_SIZE = 100;
    private static final int POPULATION_SIZE = 100;
    private static BufferedImage image;
    private static int[] pixels;
    private static JFrame frame;

    public static void checkGrowth() {
        Life.MAX_ALIVE = 90;
        Life.MIN_ALIVE = 0;
        Life.SPEED = 100;
        BufferedImage img = null;
        BufferedImage rgbImage = new BufferedImage(GRID_SIZE, GRID_SIZE, BufferedImage.TYPE_INT_RGB);
        try {
            img = ImageIO.read(new File("image.bmp"));
            rgbImage.setData(img.getData());
        } catch (IOException e) {
        }
        int[] pixels = ((DataBufferInt) rgbImage.getRaster().getDataBuffer()).getData();
        System.out.println(pixels.length);
        boolean[] pattern = new boolean[pixels.length];
        for(int i = 0; i < pixels.length; i++) {
            if(pixels[i] == 0xffffff) {
                pattern[i] = true;
            } else {
                pattern[i] = false;
            }
        }
        try {
            File outputfile = new File("initial.bmp");
            ImageIO.write(rgbImage, "bmp", outputfile);
        } catch (Exception e) {
        }
        DNA dna = new DNA(GRID_SIZE);
        dna.setGrid(pattern);
        Life life = new Life(FRAME_SIZE, GRID_SIZE);
        life.startGame(dna);
    }


    public static void main(String[] args) {

        UI ui = new UI(Runner.FRAME_SIZE, Runner.GRID_SIZE);
        Population initialPopulation =  new Population(POPULATION_SIZE, GRID_SIZE);

        Life.MIN_ALIVE = 5;
        Life.MAX_ALIVE = 95;
        Life.SPEED = 0;

        GA ga = new GA(ui, initialPopulation);


    }
}
