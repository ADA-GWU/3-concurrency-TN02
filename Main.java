import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Main {
    //Declaration of variables
    static BufferedImage image;
    static JPanel imgPanel;
    static ImageIcon icon;
    static JFrame frame;
    static Dimension newDimension;
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Collecting input from user
        String fileName = args[0];
        int squareSize = Integer.parseInt(args[1]);
        String processingMode = args[2];
        //Reading the file
        try {
            image = ImageIO.read(new File(fileName));
        }
        catch (IOException e) {
            logger.info("Error: "+ e.getMessage());
        }
        initialization(); //Initialization of JFrame, JLabel
        processImage(squareSize, processingMode);
    }

    private static void initialization() {
        // Width and height of the current screen
        DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        int screenWidth = mode.getWidth();
        int screenHeight = mode.getHeight();
        //if the image resolution is bigger than screen's resolution, get a new resolution, if not, we take image's resolution
        if (image.getWidth() > screenWidth || image.getHeight() > screenHeight) {
            //New Resolution for the photo
            int newWidth = (int) (screenWidth * 0.7);
            int newHeight = (int) (screenHeight * 0.7);
            Dimension newRes = new Dimension(newWidth, newHeight);
            //Scale for the photo (how much to increase or decrease)
            double widthScale = newRes.getWidth() / image.getWidth();
            double heightScale = newRes.getHeight() / image.getHeight();
            double scale = Math.min(widthScale, heightScale);
            //New Resolution for the photo
            newDimension = new Dimension((int) (image.getWidth() * scale), (int) (image.getHeight() * scale));
        }
        else{
            newDimension = new Dimension(image.getWidth(), image.getHeight());
        }
        imgPanel = new JPanel() {
            @Override
            //The Swing framework automatically calls the paintComponent method while render or repaint
            //with the new resolution
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int panelWidth = newDimension.width;
                int panelHeight = newDimension.height;
                // Drawing the image with the new size
                g.drawImage(image, 0, 0, panelWidth, panelHeight, this);
            }
        };
        // Settings for the windows
        icon = new ImageIcon(image);
        JLabel label = new JLabel(icon);
        label.setIcon(new ImageIcon(image));
        frame = new JFrame();
        frame.add(label,BorderLayout.CENTER);
        frame.getContentPane().add(imgPanel);
        frame.setBounds(0, 0, newDimension.width+25, newDimension.height+50);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Processing Image...");
        frame.setLocationRelativeTo(null); // Center the JFrame on the screen
        frame.setVisible(true);
    }
    private static void processImage(int squareSize, String processingMode) {
        ArrayList<Thread> threads = new ArrayList<>();
        //Single thread
        if (processingMode.equals("S")) {
            blurring(0, 0, image.getWidth(), image.getHeight(),squareSize);
        }
        //Multithreading
        else if (processingMode.equals("M")) {
            //Get number of cores
            int cores = Runtime.getRuntime().availableProcessors();

            for (int i = 0; i < cores; i++) {
                int startY = image.getHeight() * i / cores;
                int endY;
                if (i != cores - 1) {
                    endY = image.getHeight() * (i + 1) / cores;
                }
                else {
                    endY = image.getHeight();
                }
                threads.add(new Thread(() -> blurring(0, startY, image.getWidth(), endY, squareSize)));
                threads.get(i).start();
            }
        }
        else {
            logger.info("Please, write S or M");
            System.exit(1);
        }
        try {
            for (int i = 0; i < Thread.activeCount(); i++) {
                threads.get(i).join();
            }
            //When all threads will be finished, save the result in result.jpg
            File resultFile = new File("result.jpg");
            ImageIO.write(image, "jpg", resultFile);
        }
        catch (IndexOutOfBoundsException e) {
            logger.info("Done");
        }
        catch (InterruptedException e) {
            logger.info("An unexpected error occurred: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.info("An unexpected IO error occurred: " + e.getMessage());
        }
    }
    //Blurring will happen for single thread with the starting point of 0.0, and until lastX, lastY
    // In multithreading, the starting Y position will be different numbers, because of multiple number of threads
    // n is square size
    private static void blurring(int x, int y,int lastX, int lastY, int n) {
        for (int j = y; j < lastY; j = j + n) {
            for (int i = x; i < lastX; i = i + n) {
                findColorAvg(i, j, lastX, lastY, n); //average color function
                icon.setImage(image);
                frame.repaint();
                frame.revalidate();
            }
        }
    }
    //Method for calculating Average
    static int calculateAvg(ArrayList<Integer> numbers) {
        int total = 0;
        for (int i = 0; i < numbers.size(); i++) {
            int value = numbers.get(i);
            total += value;
        }
        return total / numbers.size();
    }
    //Method for finding a
    private static void findColorAvg(int x, int y, int lastX, int lastY,int n) {

        Color avg;
        ArrayList<Integer> allRed = new ArrayList<>();
        ArrayList<Integer> allGreen = new ArrayList<>();
        ArrayList<Integer> allBlue = new ArrayList<>();

        for (int currentY = y; currentY < y + n && currentY < lastY; currentY++) {
            for (int currentX = x; currentX < x + n && currentX < lastX; currentX++) {
                int pixel = image.getRGB(currentX, currentY);
                Color c = new Color(pixel, true);
                allRed.add(c.getRed()); //collect all red patterns
                allGreen.add(c.getGreen()); //collect all green patterns
                allBlue.add(c.getBlue()); //collect all blue patterns
            }
        }
        int avgRed = calculateAvg(allRed);
        int avgBlue = calculateAvg(allBlue);
        int avgGreen = calculateAvg(allGreen);
        avg = new Color(avgRed, avgGreen, avgBlue); //get a color

        for (int currentX = x; currentX < x + n && currentX < lastX; currentX++) {
            for (int currentY = y; currentY < y + n && currentY < lastY; currentY++) {
                image.setRGB(currentX, currentY, avg.getRGB()); //apply a color
            }
        }
    }

}