import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    //declaration
    static BufferedImage image;
    static Image scaledImage;
    static ImageIcon icon;
    static JFrame frame;

    public static void main(String[] args) {
        // Collecting input from user
        String fileName = args[0];
        int squareSize = Integer.parseInt(args[1]);
        String processingMode = args[2];

        try {
            image = ImageIO.read(new File(fileName));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        initialization();
        processImage(squareSize, processingMode);
    }

    private static void initialization() {
        DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        // width and height
        int screenWidth = mode.getWidth();
        int screenHeight = mode.getHeight();

        if (image.getWidth() > screenWidth || image.getHeight() > screenHeight) {
            // Calculate the scaling factors
            double widthScale = (double) screenWidth / image.getWidth();
            double heightScale = (double) screenHeight / image.getHeight();
            double scale = Math.min(widthScale, heightScale);

            // Resize the image
            int newWidth = (int) (image.getWidth() * scale);
            int newHeight = (int) (image.getHeight() * scale);

            scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        } else {
            scaledImage = image;
        }
        // Create an ImageIcon from the scaled image
        icon = new ImageIcon(scaledImage);

        // Create a JLabel to display the scaled image
        JLabel label = new JLabel(icon);

        frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Processing Image...");
        frame.setLocationRelativeTo(null); // Center the JFrame on the screen
        frame.setVisible(true);
    }
    private static void processImage(int squareSize, String processingMode) {
        ArrayList<Thread> threads = new ArrayList<>();

        if (processingMode.equals("S")) {
            blurring(0, 0, image.getWidth(), image.getHeight(),squareSize);
        }
        else if (processingMode.equals("M")) {

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
            System.out.println("Please, write S or M");
        }

        try {
            for (Thread th : threads)
                th.join();

            File resultFile = new File("result.jpg");
            ImageIO.write(image, "jpg", resultFile);
            frame.dispose();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void blurring(int x, int y,int lastX, int lastY, int n) {
        for (int j = y; j < lastY; j = j + n) {
            for (int i = x; i < lastX; i = i + n) {
                findColorAvg(i, j, lastX, lastY, n);
                icon.setImage(image);
                frame.repaint();
                frame.revalidate();
            }
        }
    }

    private static void findColorAvg(int x, int y, int lastX, int lastY,int n) {

        Color avg;

        ArrayList<Integer> onlyRed = new ArrayList<>();
        ArrayList<Integer> onlyGreen = new ArrayList<>();
        ArrayList<Integer> onlyBlue = new ArrayList<>();

        for (int currentY = y; currentY < y + n && currentY < lastY; currentY++) {
            for (int currentX = x; currentX < x + n && currentX < lastX; currentX++) {
                int pixel = image.getRGB(currentX, currentY);
                Color c = new Color(pixel, true);
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();
                onlyRed.add(red);
                onlyGreen.add(green);
                onlyBlue.add(blue);
            }
        }

        int avgRed = average(onlyRed);
        int avgBlue = average(onlyBlue);
        int avgGreen = average(onlyGreen);

        avg = new Color(avgRed, avgGreen, avgBlue);

        for (int i = x; i < x + n && i < lastX; i++) {
            for (int j = y; j < y + n && j < lastY; j++) {
                image.setRGB(i, j, avg.getRGB());
            }
        }
    }

    static int average(ArrayList<Integer> arr1) {
        int sum = 0;
        for (int val : arr1)
            sum += val;
        sum = sum/arr1.size();
        return sum;
    }

}