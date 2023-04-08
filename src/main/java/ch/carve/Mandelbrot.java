package ch.carve;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.*;


public class Mandelbrot extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    JImageCanvas canvas;
    BufferedImage bImage;

    public Mandelbrot() {
        setTitle("Mandelbrot");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout());
        getContentPane().add(rootPanel);
        canvas = new JImageCanvas();
        rootPanel.add(canvas, BorderLayout.CENTER);
        bImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Mandelbrot mandel = new Mandelbrot();
        mandel.setSize(WIDTH + 10, HEIGHT + 10);
        mandel.setVisible(true);
        StopWatch watch = new StopWatch();
        for (int r = 0; r < 5; r++) {
            watch.start();
//            double i = 0;
            for (double i = 0; i < 0.8; i += 0.01) {
                mandel.calculateImage(-0.7 + i, 2.1 - i, -1.2 + i, 1.5 - i, 256);
            }
            watch.stop();
            System.out.println(watch.toString());
        }
    }

    public void calculateImage(double minX, double maxX, double minY, double maxY, int maxIteration) {
        byte[] pixels = new byte[WIDTH * HEIGHT];
        double dx = (maxX - minX) / WIDTH;
        double dy = (maxY - minY) / HEIGHT;
        double xk = minX;
        double yk = minY;

        ExecutorService executor = Executors.newFixedThreadPool(8);
        // create 512 (1 per line) threads
        Thread threads[] = new Thread[HEIGHT];
        for (int y = 0; y < HEIGHT; y++) {
            executor.submit(new LineCalculator(maxIteration, dx, xk, yk, y, pixels));
            yk += dy;
            xk = minX;
        }

        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bImage.getRaster().setDataElements(0, 0, WIDTH, HEIGHT, pixels);
        canvas.setImage(bImage);
    }

    private class LineCalculator implements Runnable {

        int maxIteration;
        double dx;
        double xk;
        double yk;
        int y;
        byte[] pixels;

        public LineCalculator(int maxIteration, double dx, double xk, double yk, int y, byte[] pixels) {
            this.maxIteration = maxIteration;
            this.dx = dx;
            this.xk = xk;
            this.yk = yk;
            this.y = y;
            this.pixels = pixels;
        }

        @Override
        public void run() {
            int iter;
            double xx;
            double yy;
            double valueX;
            double valueY;
            int scany = y * WIDTH;
            for (int x = 0; x < WIDTH; x++) {
                iter = 0;
                xx = 0;
                yy = 0;
                valueX = 0;
                valueY = 0;
                while ((iter < maxIteration) && ((xx + yy) < 4)) {
                    valueY = 2 * valueX * valueY - yk;
                    valueX = xx - yy - xk;
                    xx = valueX * valueX;
                    yy = valueY * valueY;
                    iter++;
                }
                if (iter < maxIteration) {
                    pixels[scany + x] = (byte) (-127 + (iter % 256));
                } else {
                    pixels[scany + x] = 0;
                }
                xk += dx;
            }
        }

    }
}
