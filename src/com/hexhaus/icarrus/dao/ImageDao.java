package com.hexhaus.icarrus.dao;

import com.hexhaus.icarrus.handler.MessageHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * User: 67726e
 * Date: 8/5/11
 * Time: 10:32 PM
 */
public class ImageDao {
	private static final String rootPath = "resource/images/";

	private ImageDao() {
	}

	public static Image getImage(String imageFileName) {
		File imageFile = new File(rootPath + imageFileName);
		Image image;

		if (imageFile.exists()) {
			image = Toolkit.getDefaultToolkit().getImage(rootPath + imageFileName);
		} else {
			try {
				image = ImageIO.read(ClassLoader.getSystemResource(rootPath + imageFileName));
			} catch (Exception e) {
				MessageHandler.postMessage("IO Error",
						"Could not load image: " + rootPath + imageFileName, LoggingDao.Status.Error);
				image = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
			}
		}

		return image;
	}
}
