import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];
        int squareSize = Integer.parseInt(args[1]);
        String processingMode = args[2];

        ImageProcessor imageProcessor = new ImageProcessor();
        imageProcessor.initialize(fileName, squareSize, processingMode);
    }
}
