/**
 * 
 */
package com.fusioncharts.exporter.encoders;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.stream.FileImageOutputStream;

/**
 * Interface to be implemented by all Image Encoders.
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public interface Encoder {

	/**
	 * 
	 * @param bufferedImage
	 * @param fileImageOutputStream
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream) throws Throwable;

	/**
	 * 
	 * @param bufferedImage
	 * @param fileImageOutputStream
	 * @param quality
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream, float quality)
			throws Throwable;

	/**
	 * 
	 * @param bufferedImage
	 * @param fileImageOutputStream
	 * @param quality
	 * @param format
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage,
			FileImageOutputStream fileImageOutputStream, float quality,
			String format) throws Throwable;

	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream)
			throws Throwable;

	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 * @param quality
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream,
			float quality) throws Throwable;

	/**
	 * 
	 * @param bufferedImage
	 * @param outputStream
	 * @param quality
	 * @param format
	 * @throws Throwable
	 */
	public void encode(BufferedImage bufferedImage, OutputStream outputStream,
			float quality, String format) throws Throwable;

}
