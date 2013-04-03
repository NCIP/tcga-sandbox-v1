package com.fusioncharts.exporter.encoders;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * Contains functions to encode the BufferedImage of the chart to particular
 * format (eg. jpg) and writes to the given stream
 * 
 * @author InfoSoft Global (P) Ltd.
 * @see JPEGEncoder
 */
public class BasicEncoder implements Encoder {

	String defaultFormat = "JPEG";

	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream) throws Throwable {
		encode(bufferedImage, fileImageOutputStream, 1F);
	}

	/*
	 * Encodes the BufferedImage to default format.
	 * 
	 * @param bufferedImage
	 * 
	 * @param outputStream
	 * 
	 * @param quality float value from 0.0f(worst image quality) - 1.0f(best
	 * image quality) - Here it is ignored
	 * 
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream, float quality)
			throws Throwable {

		encode(bufferedImage, fileImageOutputStream, quality, defaultFormat);

	}

	/**
	 * Here quality is ignored.
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream, float quality,
			String format) throws Throwable {

		java.util.Iterator<ImageWriter> writers = ImageIO
				.getImageWritersByFormatName(format);
		ImageWriter writer = writers.next();

		ImageWriteParam iwp = writer.getDefaultWriteParam();

		writer.setOutput(fileImageOutputStream);
		writer.write(null, new IIOImage(bufferedImage, null, null), iwp);
		fileImageOutputStream.close();
	}

	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream)
			throws Throwable {
		encode(bufferedImage, outputStream, 1F);
	}

	/*
	 * Encodes the BufferedImage to default format.
	 * 
	 * @param bufferedImage
	 * 
	 * @param outputStream
	 * 
	 * @param quality float value from 0.0f(worst image quality) - 1.0f(best
	 * image quality) - Here it is ignored
	 * 
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream,
			float quality) throws Throwable {

		encode(bufferedImage, outputStream, quality, defaultFormat);

	}

	/**
	 * Here quality is ignored.
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream,
			float quality, String format) throws Throwable {
		ImageOutputStream ios = null;
		try {
			java.util.Iterator<ImageWriter> writers = ImageIO
					.getImageWritersByFormatName(format);
			ImageWriter writer = writers.next();

			ImageWriteParam iwp = writer.getDefaultWriteParam();

			ios = ImageIO.createImageOutputStream(outputStream);

			writer.setOutput(ios);
			writer.write(null, new IIOImage(bufferedImage, null, null), iwp);
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

	public void encode(BufferedImage bufferedImage, Writer out, float quality,
			String format) throws Throwable {
		ImageOutputStream ios = null;
		try {
			java.util.Iterator<ImageWriter> writers = ImageIO
					.getImageWritersByFormatName(format);
			ImageWriter writer = writers.next();

			ImageWriteParam iwp = writer.getDefaultWriteParam();

			ios = ImageIO.createImageOutputStream(out);

			writer.setOutput(ios);
			writer.write(null, new IIOImage(bufferedImage, null, null), iwp);
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
}
