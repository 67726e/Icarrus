package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.MessageHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:32 PM
 */
public class ImageDao {
    private static final String rootPath = "resource/images/";

    private ImageDao() {}

    public static BufferedImage getImage(String imageFileName) {
        // Add the full path to the resources if it is not available
        if (!imageFileName.startsWith(rootPath)) imageFileName = rootPath + imageFileName;
        File imageFile = new File(imageFileName);

        // Confirm the existence of the file in question
        if (!imageFile.exists()) {
            MessageHandler.postMessage("Missing File",
                    "The image file \"" + imageFile.getAbsolutePath() + "\" could not be found.", LoggingDao.Status.Warning);
        }

        // Attempt to return a BufferedImage containing the file
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            MessageHandler.postMessage("Image IO Error",
                    "Could not read the file \"" + imageFile.getAbsolutePath() + "\".", LoggingDao.Status.Error);
            return null;
        }
    }
}
