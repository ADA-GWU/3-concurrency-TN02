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
    static ImageIcon icon;
    static JFrame frame;
    //  static final int startX = 0;
    //  static final int startY = 0;

    public static void main(String[] args) {
        // Collecting input from user
        String fileName = args[0];
        int squareSize = Integer.parseInt(args[1]);
        String processingMode = args[2];

        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        initialization();
        processImage(squareSize, processingMode);

    }

    private static void initialization() {
      /*  // screen size
        DisplayMode mode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        // width and height
        int maxWidth = mode.getWidth();
        int maxHeight = mode.getHeight();

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        double widthScale = (double) maxWidth / imageWidth;
        double heightScale = (double) maxHeight / imageHeight;

        double scale = Math.min(widthScale, heightScale);

        int scaledWidth = 1920;//(int) (imageWidth * scale);
        int scaledHeight = 1080;//(int) (imageHeight * scale);

        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();*/

        //  Image resizedImage = image.getScaledInstance(800, 600, Image.SCALE_SMOOTH);


        // Create an ImageIcon from the scaled image
        icon = new ImageIcon(image);

        // Create a JLabel to display the scaled image
        JLabel label = new JLabel(icon);

        frame = new JFrame();
        // Add the JLabel to the JFrame
        frame.getContentPane().add(label);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Image Display");
        //   frame.setSize(scaledWidth, scaledHeight);
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
                int startThreadY = image.getHeight() * i / cores;
                int endThreadY;

                if (i != cores - 1) {
                    endThreadY = image.getHeight() * (i + 1) / cores;
                }
                else {
                    endThreadY = image.getHeight();
                }
                threads.add(new Thread(() -> blurring(0, startThreadY, image.getWidth(), endThreadY,squareSize)));
                threads.get(i).start();
            }
        }
        else {
            System.out.println("Please, write S or M");
        }

        try {
            for (Thread th : threads)
                th.join();

            File resultImage = new File("result.jpg");
            ImageIO.write(image, "jpg", resultImage);
            frame.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void blurring(int x, int y,int lastX, int lastY,int n) {
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
