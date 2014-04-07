package pl.plgrid.unicore.common.utils;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Rafal
 */
public class ClassLoaderHelper {
    private static final Logger logger = Logger.getLogger(ClassLoaderHelper.class);
    private static final ClassLoader classLoader = ClassLoaderHelper.class.getClassLoader();

    public static InputStream getClasspathResourceInputStream(String resourcePath) {
        return classLoader.getResourceAsStream(resourcePath);
    }

    public static BufferedImage getClasspathResourceImage(String resourcePath) {
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            logger.warn("Unable to read image from classpath resource: " + resourcePath, e);
        }
        return null;
    }
}
