package runner;

import com.sun.org.apache.xpath.internal.operations.Bool;
import ga.DNA;
import ga.GA;
import library.Library;
import life.Life;
import ui.UI;
import ga.Population;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;


public class Runner {

    private static final int FRAME_SIZE = Library.FRAME_SIZE;
    private static final int GRID_SIZE = Library.GRID_SIZE;
    private static final int POPULATION_SIZE = Library.POPULATION_SIZE;

    public static void checkGrowth() throws IOException{
        Life.MAX_ALIVE = 90;
        Life.MIN_ALIVE = 0;
        Life.SPEED = 25;
        Life.CREATE_CSV = true;
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
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == 0xffffff) {
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


    public static void main(String[] args) throws IOException{
        boolean runBestInitial = Boolean.valueOf(args[0]);
        if (runBestInitial) {
            checkGrowth();
        } else {
            UI ui = new UI(Runner.FRAME_SIZE, Runner.GRID_SIZE);
            Population initialPopulation = new Population(POPULATION_SIZE, GRID_SIZE);

            Life.MIN_ALIVE = 1;
            Life.MAX_ALIVE = 95;
            Life.SPEED = 0;
            Life.CREATE_CSV = false;

            GA ga = new GA(ui, initialPopulation);
        }


    }
}
