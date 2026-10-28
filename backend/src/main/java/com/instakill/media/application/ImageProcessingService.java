package com.instakill.media.application;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

@Service
public class ImageProcessingService {

    private static final int MAX_WIDTH = 300;
    private static final String WATERMARK_TEXT = "Instakill Demo";

    public void processAndSaveImage(InputStream imageStream, File outputFile, String extension) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageStream);
        if (originalImage == null) {
            throw new IOException("Unsupported or corrupt image file");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // Scale proportionally if width exceeds 300px
        if (originalWidth > MAX_WIDTH) {
            newWidth = MAX_WIDTH;
            newHeight = (int) (((double) originalHeight / originalWidth) * MAX_WIDTH);
        }

        // Create new image buffer with calculated dimensions
        BufferedImage processedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = processedImage.createGraphics();

        // 1. Draw the scaled image
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);

        // 2. Configure and draw the watermark
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(255, 255, 255, 180)); // Semi-transparent white

        FontMetrics fontMetrics = g2d.getFontMetrics();
        int x = newWidth - fontMetrics.stringWidth(WATERMARK_TEXT) - 10;
        int y = newHeight - fontMetrics.getHeight() + 10;

        g2d.drawString(WATERMARK_TEXT, x, y);
        g2d.dispose();

        // Standardize output format
        String format = extension.replace(".", "").toLowerCase();
        if (format.equals("jpg"))
            format = "jpeg";

        // Save to local directory
        ImageIO.write(processedImage, format, outputFile);
    }
}