package ui;

import ga.DNA;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.Arrays;

public class UI extends Canvas {

    private int frameSize;
    private int gridSize;
    private BufferedImage image;
    private int[] pixels;
    private JFrame frame;
    private Dimension dimension;

    public UI (int frameSize, int gridSize) {
        this.frameSize = frameSize;
        this.gridSize = gridSize;
        this.dimension = new Dimension(this.frameSize, this.frameSize);
        setMinimumSize(this.dimension);
        setMaximumSize(this.dimension);
        setPreferredSize(this.dimension);
        frame = new JFrame();
        image = new BufferedImage(this.gridSize, this.gridSize, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public void showFittestPattern() {
        frame.setTitle("Fittest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.add(this);
        frame.pack();
    }

    public void displayFittest (DNA fittest) {
        boolean[] fittestPattern = Arrays.copyOf(fittest.getGrid(), fittest.getGrid().length);
        this.render(fittestPattern, fittest.getFitness());
    }

    private void render(boolean[] fittestPattern, double overallMaxFitness) {
        frame.setTitle("Fittest (Fitness: " + overallMaxFitness + ")");
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
            pixels[i] = fittestPattern[i] ? 0xffffff : 0;
        }
        try {
            File outputfile = new File("image.bmp");
            ImageIO.write(image, "bmp", outputfile);
        } catch (Exception e) {
            System.out.println("Unable to save Fittest pattern image file.");
        }
        g.drawImage(image, 0, 0, this.frameSize, this.frameSize, null);
        g.dispose();
        bs.show();
    }
}
