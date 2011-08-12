package com.hexcoder.icarrus.dao;

import com.hexcoder.icarrus.messaging.MessageHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageDAO {
    private static final String rootPath = "data/images/";

    private ImageDAO() {}

    public static BufferedImage getImage(String imageFileName) throws FileNotFoundException, IOException {
        if (!imageFileName.startsWith(rootPath)) imageFileName = rootPath + imageFileName;      // Add the full path to the resources if it is not available

        File imageFile = new File(imageFileName);

        if (!imageFile.exists()) {                                                              // Confirm the existence of the file in question
            MessageHandler.postMessage("Missing File", "The image file \"" + imageFile.getAbsolutePath() + "\" could not be found.", LoggingDAO.WARNING);
            throw new FileNotFoundException();
        }

        try {
            return ImageIO.read(imageFile);                                                     // Attempt to return a BufferedImage containing the file
        } catch (IOException e) {
            MessageHandler.postMessage("Image IO Error", "Could not read the file \"" + imageFile.getAbsolutePath() + "\".", LoggingDAO.ERROR);
            throw e;
        }
    }
}
