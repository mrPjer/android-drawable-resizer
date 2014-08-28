package com.github.forsety;

import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import static org.imgscalr.Scalr.Method;
import static org.imgscalr.Scalr.Mode;
import static org.imgscalr.Scalr.OP_ANTIALIAS;

/**
 * Created by Forsety on 28.08.2014.
 */
public class BatchDrawableResizer extends Thread {

    private static final List<String> DEFAULT_EXTENSIONS = Arrays.asList("png", "jpg", "gif");
    private File inputDir;
    private File outputDir;
    private double ratio;
    private int resizedDrawablesCount = 0;

    public BatchDrawableResizer(File inputDir, File outputDir, double ratio) {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.ratio = ratio;
    }

    public void run() {
        Arrays.asList(inputDir.listFiles((file, s) -> {
            String extension = getExtension(s);
            return DEFAULT_EXTENSIONS.contains(extension) && !s.endsWith(".9." + extension); // Exclude 9-patches
        })).forEach(input -> {
            try {
                File output = new File(outputDir, input.getName());
                if (!output.exists() || input.lastModified() > output.lastModified()) {
                    resizeDrawable(input, output, getExtension(input.getName()), ratio);
                    resizedDrawablesCount++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public int getResizedDrawablesCount() {
        return resizedDrawablesCount;
    }

    public static void resizeDrawable(File input, File output, String outputExtension, double ratio) throws IOException {
        BufferedImage inputImg = ImageIO.read(input);
        BufferedImage outputImg = Scalr.resize(inputImg, Method.QUALITY, Mode.FIT_TO_WIDTH, (int) (inputImg.getWidth() * ratio), OP_ANTIALIAS);
        ImageIO.write(outputImg, outputExtension, output);
    }

    public static String getExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }

}
