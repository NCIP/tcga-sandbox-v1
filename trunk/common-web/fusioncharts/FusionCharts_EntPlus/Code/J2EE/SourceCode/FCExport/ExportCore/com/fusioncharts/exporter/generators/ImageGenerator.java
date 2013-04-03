package com.fusioncharts.exporter.generators;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import com.fusioncharts.exporter.beans.ChartMetadata;

/**
 * Generates the buffered image.
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class ImageGenerator {
	private static Logger logger = null;

	static {
		logger = Logger.getLogger(ImageGenerator.class.getName());
	}

	/**
	 * 
	 * @param data
	 * @param metadata
	 * @return
	 */
	public static BufferedImage getChartImage(String data,
			ChartMetadata metadata) {
		// Decoded data from charts.
		logger.info("Creating the Chart image");
		// Rows of color values.
		String[] rows;
		// Width and height of chart.
		int width = 0;
		int height = 0;
		// Default background color of the chart
		String bgcolor = "";
		Color bgColor;

		// Get the width and height from form
		width = (int) metadata.getWidth();
		height = (int) metadata.getHeight();

		if (width <= 0 || height <= 0) {
			// If the width and height are less than 1, we cannot create the
			// image.
			// throw new NoBasicDataException();
			logger.severe("Image width/height not provided.");
			// out.close();
		}

		// Get background color from request and set default
		bgcolor = metadata.getBgColor();
		if (bgcolor == null || bgcolor == "" || bgcolor == null) {
			bgcolor = "FFFFFF";
		}
		// Convert background color to color object
		bgColor = new Color(Integer.parseInt(bgcolor, 16));

		if (data == null) {
			// If image data not provided.
			// throw new NoImageDataException();
			logger.severe("Image Data not supplied.");
			// out.close();
		}
		BufferedImage chart = null;
		try {
			// Parse data
			rows = new String[height + 1];
			rows = data.split(";");

			// Bitmap to store the chart.
			// Reference to graphics object - gr
			chart = new BufferedImage(width, height,
					BufferedImage.TYPE_3BYTE_BGR);
			Graphics gr = chart.createGraphics();
			gr.setColor(bgColor);
			gr.fillRect(0, 0, width, height);

			String c;
			int r;
			int ri = 0;
			for (int i = 0; i < rows.length; i++) {
				// Split individual pixels.
				String[] pixels = rows[i].split(",");
				// Set horizontal row index to 0
				ri = 0;
				for (int j = 0; j < pixels.length; j++) {
					// Now, if it's not empty, we process it
					// Split the color and repeat factor

					String[] clrs = pixels[j].split("_");
					// Reference to color
					c = clrs[0];
					r = Integer.parseInt(clrs[1]);

					// If color is not empty (i.e. not background pixel)
					if (c != null && c.length() > 0 && c != "") {
						if (c.length() < 6) {
							// If the hexadecimal code is less than 6
							// characters, pad with 0
							StringBuffer str = new StringBuffer(c);
							for (int p = c.length() + 1; p <= 6; p++) {
								str.insert(0, "0");
							}
							// the new padded string
							c = str.toString();
						}
						for (int k = 1; k <= r; k++) {
							// Draw each pixel
							gr.setColor(new Color(Integer.parseInt(c, 16)));
							gr.fillRect(ri, i, 1, 1);
							// Increment horizontal row count
							ri++;
						}
					} else {
						// Just increment horizontal index
						ri = ri + r;
					}
				}
			}

			logger.info("Image created successfully");
		} catch (Exception e) {
			// IF the image data is mal-formatted.
			logger.severe("Image data is not in proper format:" + e.toString());
			// throw new InvalidImageDataFormatException();
		}
		return chart;
	}

}