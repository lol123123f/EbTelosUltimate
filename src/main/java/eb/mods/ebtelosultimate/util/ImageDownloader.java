package eb.mods.ebtelosultimate.util;

import eb.mods.ebtelosultimate.client.EbTelosUltimateClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {
    public static BufferedImage downloadImage(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            EbTelosUltimateClient.LOGGER.error("Failed to download image from URL: " + url, e);
            return null;
        }
    }
}
