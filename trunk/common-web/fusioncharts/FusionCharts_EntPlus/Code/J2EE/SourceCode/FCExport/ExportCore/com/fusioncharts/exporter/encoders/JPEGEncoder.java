package com.fusioncharts.exporter.encoders;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Contains functions to encode the BufferedImage of the chart to jpg format and
 * writes to the given stream
 * 
 * @author InfoSoft Global (P) Ltd.
 * @see JPEGEncoder
 */
public class JPEGEncoder implements Encoder {
	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream) throws Throwable {
		encode(bufferedImage, fileImageOutputStream, 1F);
	}

	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream, float quality)
			throws Throwable {

		java.util.Iterator<ImageWriter> writers = ImageIO
				.getImageWritersByFormatName("JPEG");
		ImageWriter writer = writers.next();

		JPEGImageWriteParam params = new JPEGImageWriteParam(null);
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality(quality);
		params.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_DISABLED);
		params.setDestinationType(new ImageTypeSpecifier(IndexColorModel
				.getRGBdefault(), IndexColorModel.getRGBdefault()
				.createCompatibleSampleModel(16, 16)));

		IIOImage image = new IIOImage(bufferedImage, null, null);

		writer.setOutput(fileImageOutputStream);
		writer.write(null, image, params);
		fileImageOutputStream.close();

	}

	/**
	 * Since this is a JPEG encoder, format has to be JPEG and hence the format
	 * parameter is just ignored.
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream, float quality,
			String format) throws Throwable {
		encode(bufferedImage, fileImageOutputStream, quality);
	}

	/*
	 * FileImageOutputStream handlers
	 */

	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream)
			throws Throwable {
		encode(bufferedImage, outputStream, 1F);
	}

	/**
	 * Encodes the BufferedImage to a JPEG format.
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 * @param quality
	 *            float value from 0.0f(worst image quality) - 1.0f(best image
	 *            quality)
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream,
			float quality) throws Throwable {

		// ImageIO.write( bufferedImage, "JPEG", outputStream );
		ImageOutputStream ios = null;
		try {
			java.util.Iterator<ImageWriter> writers = ImageIO
					.getImageWritersByFormatName("JPEG");
			ImageWriter writer = writers.next();

			JPEGImageWriteParam params = new JPEGImageWriteParam(null);
			params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			params.setCompressionQuality(quality);
			params
					.setProgressiveMode(javax.imageio.ImageWriteParam.MODE_DISABLED);
			params.setDestinationType(new ImageTypeSpecifier(IndexColorModel
					.getRGBdefault(), IndexColorModel.getRGBdefault()
					.createCompatibleSampleModel(16, 16)));

			IIOImage image = new IIOImage(bufferedImage, null, null);

			ios = ImageIO.createImageOutputStream(outputStream);
			writer.setOutput(ios);
			writer.write(null, image, params);
			ios.close();
		} catch (IllegalArgumentException e) {
			if (ios != null) {
				ios.close();
			}
			throw new Throwable();
		} catch (IOException e) {
			if (ios != null) {
				ios.close();
			}
			throw new Throwable();
		}

	}

	/**
	 * Since this is a JPEG encoder, format has to be JPEG and hence the format
	 * parameter is just ignored.
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream,
			float quality, String format) throws Throwable {
		encode(bufferedImage, outputStream, quality);
	}

}
