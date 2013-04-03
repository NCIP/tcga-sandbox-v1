package com.fusioncharts.exporter.generators;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.zip.Deflater;

import com.fusioncharts.exporter.beans.ChartMetadata;
import com.fusioncharts.exporter.beans.ExportBean;

/**
 * Generates the PDF document.
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class PDFGenerator {

	private final Logger logger = Logger
			.getLogger(PDFGenerator.class.getName());

	int pageIndex = 0;

	ArrayList<ExportBean> pagesData = new ArrayList<ExportBean>();

	/**
	 * Calls setBitmapData to create one page
	 * 
	 * @param data
	 * @param metadata
	 */
	public PDFGenerator(String data, ChartMetadata metadata) {
		setBitmapData(data, metadata);

	}

	/**
	 * Creates image PDF object containing the chart image
	 * 
	 * @param id
	 *            image number or page number -1
	 * @param isCompressed
	 *            whether to compress the data or not
	 * @return String containing the image binary for pdf format
	 */
	private byte[] addImageToPDF(int id, boolean isCompressed) {
		byte[] imagePDFBytes = null;
		ByteArrayOutputStream imagePDFBAOS = new ByteArrayOutputStream();
		try {
			// PDF Object number
			int imgObjNo = 6 + id * 3;

			// Get chart Image binary
			byte[] bitmapImage = getBitmapData24(id);
			// Compress image binary
			byte[] imgBinary = isCompressed ? compress(bitmapImage)
					: bitmapImage;
			// get the length of the image binary
			int length = imgBinary.length;
			ChartMetadata metadata = pagesData.get(id).getMetadata();
			// Build PDF object containing the image binary and other formats
			// required
			String imgObj = imgObjNo
					+ " 0 obj\n<<\n/Subtype /Image /ColorSpace /DeviceRGB /BitsPerComponent 8 /HDPI 72 /VDPI 72 "
					+ (isCompressed ? "/Filter /FlateDecode " : "") + "/Width "
					+ (int) metadata.getWidth() + " /Height "
					+ (int) metadata.getHeight() + " /Length " + length
					+ " >>\nstream\n";

			String imgObj2 = "endstream\nendobj\n";

			imagePDFBAOS.write(imgObj.getBytes());
			imagePDFBAOS.write(imgBinary);
			imagePDFBAOS.write(imgObj2.getBytes());
			imagePDFBytes = imagePDFBAOS.toByteArray();
			imagePDFBAOS.close();
		} catch (IOException e) {

			// e.printStackTrace();
			logger.severe("Exception while parsing image data for PDF: "
					+ e.toString());
		}

		return imagePDFBytes;

	}

	/**
	 * Helper function to convert an ArrayList to string
	 * 
	 * @param a
	 *            The ArrayList to be converted into String
	 * @param separator
	 *            appends this separator at the end of each element in the
	 *            ArrayList
	 * @return
	 */
	private String arrayToString(ArrayList<String> a, String separator) {
		StringBuffer result = new StringBuffer();
		if (a.size() > 0) {
			result.append(a.get(0));
			for (int i = 1; i < a.size(); i++) {
				result.append(separator);
				result.append(a.get(i));
			}
		}
		return result.toString();
	}

	/**
	 * calculate the current xpos value
	 * 
	 * @param posn
	 * @return
	 */
	private String calculateXPos(String posn) {
		String paddedStr = "0000000000".substring(0, 10 - posn.length()) + posn;
		// padLeft(posn,0,10-posn.length())
		return (paddedStr + " 00000 n \n");
	}

	/**
	 * Compress the data
	 * 
	 * @param data
	 * @return
	 */
	private byte[] compress(byte[] data) {
		logger.info("Compressing the image data");
		byte[] compressedData = null;

		// Create the compressor with highest level of compression
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);

		// Give the compressor the data to compress
		compressor.setInput(data);
		compressor.finish();

		// Create an expandable byte array to hold the compressed data.
		// You cannot use an array that's the same size as the orginal because
		// there is no guarantee that the compressed data will be smaller than
		// the uncompressed data.
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

		// Compress the data
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		} catch (IOException e) {
		}

		// Get the compressed data
		compressedData = bos.toByteArray();
		logger.info("Image data compressed");
		return compressedData;
	}

	/**
	 * Gets the bitmap data by parsing the image data
	 * 
	 * @param id
	 *            image number eg. 0
	 * @return String containing the bitmap bytes as string
	 */
	private byte[] getBitmapData24(int id) {
		byte[] imageData = null;
		ByteArrayOutputStream imageData24OS = new ByteArrayOutputStream();
		// Split the data into rows using ; as separator

		StringTokenizer rows = new StringTokenizer(pagesData.get(id)
				.getStream(), ";");

		logger.info("Parsing image data");
		StringTokenizer pixels = null;
		String pixelData[] = null;
		String color = null;
		int repeat = 0;
		// Detect the background color
		String bgColor = pagesData.get(id).getMetadata().getBgColor();
		if (bgColor == null || bgColor.equals("")) {
			pagesData.get(id).getMetadata().setBgColor("FFFFFF");
			bgColor = "FFFFFF";
		}

		while (rows.hasMoreElements()) {
			pixels = new StringTokenizer((String) rows.nextElement(), ",");
			while (pixels.hasMoreElements()) {

				pixelData = ((String) pixels.nextElement()).split("_");
				color = pixelData[0];
				repeat = Integer.parseInt(pixelData[1]);
				// If color is empty (i.e., background pixel)
				if (color == null || color.equals("")) {
					color = bgColor;
				}
				if (color.length() < 6) {
					color = "000000".substring(0, 6 - color.length()) + color;
				}

				byte[] rgbBytes = hexToBytes(color);
				byte[] repeatedBytes = repeatBytes(rgbBytes, repeat);

				try {
					imageData24OS.write(repeatedBytes);
					imageData24OS.flush();
				} catch (IOException e) {
					// e.printStackTrace();
					logger
							.severe("Exception while writing image data for PDF: "
									+ e.toString());
				}

			}// end of inner while pixels.hasMoreElements

		}// end of outer while rows.hasMoreElements
		imageData = imageData24OS.toByteArray();
		// NOTE: This causes problem
		// imageData24 = new String(imageData);

		try {
			imageData24OS.close();
		} catch (IOException e) {

			// e.printStackTrace();
			logger.severe("Exception while closing stream for PDF: "
					+ e.toString());
		}
		logger.info("Image data parsed successfully");
		return imageData;
	}

	/**
	 * Main PDF builder function Adds the pdf objects and xref
	 * 
	 * @param isCompressed
	 *            whether data is compressed or not
	 * @return String containing the PDF objects
	 */
	public byte[] getPDFObjects(boolean isCompressed) {
		logger.info("Creating PDF specific objects.");

		ByteArrayOutputStream PDFBytesOS = new ByteArrayOutputStream();
		byte[] pdfBytes = null;

		// Store all PDF objects in this temporary string to be written to
		// ByteArray
		String strTmpObj = "";

		// start xref array
		ArrayList<String> xRefList = new ArrayList<String>();

		xRefList.add(0, "xref\n0 ");
		xRefList.add(1, "0000000000 65535 f \n"); // Address Reference to obj 0

		// Build PDF objects sequentially
		// version and header
		strTmpObj = "%PDF-1.3\n%{FC}\n";
		try {
			PDFBytesOS.write(strTmpObj.getBytes());

			// PDFBytes+=strTmpObj;

			// OBJECT 1 : info (optional)
			strTmpObj = "1 0 obj<<\n/Author (FusionCharts)\n/Title (FusionCharts)\n/Creator (FusionCharts)\n>>\nendobj\n";
			xRefList.add(calculateXPos("" + PDFBytesOS.size())); // refenrece to
			// obj 1
			// PDFBytes+=strTmpObj;
			PDFBytesOS.write(strTmpObj.getBytes());

			// OBJECT 2 : Starts with Pages Catalogue
			strTmpObj = "2 0 obj\n<< /Type /Catalog /Pages 3 0 R >>\nendobj\n";
			xRefList.add(calculateXPos("" + PDFBytesOS.size())); // refenrece to
			// obj 2
			// PDFBytes+=strTmpObj;
			PDFBytesOS.write(strTmpObj.getBytes());

			// OBJECT 3 : Page Tree (reference to pages of the catalogue)
			strTmpObj = "3 0 obj\n<<  /Type /Pages /Kids [";
			for (int i = 0; i < pageIndex; i++) {
				strTmpObj += (((i + 1) * 3) + 1) + " 0 R\n";
			}
			strTmpObj += "] /Count " + pageIndex + " >>\nendobj\n";

			xRefList.add(calculateXPos("" + PDFBytesOS.size())); // refenrece to
			// obj 3
			// PDFBytes+=strTmpObj;
			PDFBytesOS.write(strTmpObj.getBytes());

			ChartMetadata metadata = null;
			int iWidth;
			int iHeight;
			byte imgPDFBytes[];
			// Each image page
			logger.info("Gathering data for  each page");
			for (int itr = 0; itr < pageIndex; itr++) {
				metadata = pagesData.get(itr).getMetadata();
				iWidth = (int) metadata.getWidth();
				iHeight = (int) metadata.getHeight();
				// OBJECT 4..7..10..n : Page config
				strTmpObj = (((itr + 2) * 3) - 2)
						+ " 0 obj\n<<\n/Type /Page /Parent 3 0 R \n/MediaBox [ 0 0 "
						+ iWidth + " " + iHeight
						+ " ]\n/Resources <<\n/ProcSet [ /PDF ]\n/XObject <</R"
						+ (itr + 1) + " " + ((itr + 2) * 3)
						+ " 0 R>>\n>>\n/Contents [ " + (((itr + 2) * 3) - 1)
						+ " 0 R ]\n>>\nendobj\n";
				xRefList.add(calculateXPos("" + PDFBytesOS.size())); // refenrece
				// to
				// obj
				// 4,7,10,13,16...
				PDFBytesOS.write(strTmpObj.getBytes());

				// OBJECT 5...8...11...n : Page resource object (xobject
				// resource that transforms the image)
				xRefList.add(calculateXPos("" + PDFBytesOS.size())); // refenrece
				// to
				// obj
				// 5,8,11,14,17...
				PDFBytesOS.write(getXObjResource(itr).getBytes());

				// OBJECT 6...9...12...n : Binary xobject of the page (image)
				imgPDFBytes = addImageToPDF(itr, isCompressed);

				xRefList.add(calculateXPos("" + PDFBytesOS.size())); // refenrece
				// to
				// obj
				// 6,9,12,15,18...
				PDFBytesOS.write(imgPDFBytes);

			}
			// xrefs compilation
			String xRef0 = xRefList.get(0) + (xRefList.size() - 1) + "\n";
			xRefList.set(0, xRef0);

			// get trailer
			String trailer = getTrailer(PDFBytesOS.size(), xRefList.size() - 1);

			// write xref and trailer to PDF

			// PDFBytes2+=arrayToString(xRefList,"");
			PDFBytesOS.write(arrayToString(xRefList, "").getBytes());

			// PDFBytes2+=trailer;
			PDFBytesOS.write(trailer.getBytes());
			// write EOF
			String EOF = "%%EOF\n";
			// PDFBytes2+="%%EOF\n";
			PDFBytesOS.write(EOF.getBytes());
			pdfBytes = PDFBytesOS.toByteArray();
			logger.info("PDF data created successfully");
		} catch (IOException e) {

			// e.printStackTrace();
			logger.severe("Exception while writing PDF data: " + e.toString());
		}
		return pdfBytes;

	}

	/**
	 * Construct the trailer for the pdf
	 * 
	 * @param xrefpos
	 * @param numxref
	 * @return
	 */
	private String getTrailer(int xrefpos, int numxref) {
		return "trailer\n<<\n/Size " + numxref
				+ "\n/Root 2 0 R\n/Info 1 0 R\n>>\nstartxref\n" + xrefpos
				+ "\n";
	}

	/**
	 * Build Image resource object that transforms the image from First Quadrant
	 * system to Second Quadrant system
	 * 
	 * @param itr
	 * @return
	 */
	private String getXObjResource(int itr) {
		ChartMetadata metadata = pagesData.get(itr).getMetadata();
		return (((itr + 2) * 3) - 1)
				+ " 0 obj\n<< /Length "
				+ (24 + ("" + (int) metadata.getWidth() + (int) metadata
						.getHeight()).length()) + " >>\nstream\nq\n"
				+ (int) metadata.getWidth() + " 0 0 "
				+ (int) metadata.getHeight() + " 0 0 cm\n/R" + (itr + 1)
				+ " Do\nQ\nendstream\nendobj\n";
	}

	/**
	 * Convert Hex number like FFFFFF to byte[]
	 * 
	 * @param hex
	 * @return
	 */
	private byte[] hexToBytes(char[] hex) {
		int length = 3;
		byte[] raw = new byte[length];
		for (int i = 0; i < length; i++) {
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			int value = (high << Byte.SIZE / 2) | low;
			// if (value > 127)
			// value -= 256;
			// raw[i] = (byte) value;
			raw[i] = (byte) ((value & 0x000000ff) <= 127 ? value
					: (value - 256));
		}
		return raw;
	}

	/**
	 * Convert hex string to bytes
	 * 
	 * @param hex
	 * @return
	 */
	private byte[] hexToBytes(String hex) {
		return hexToBytes(hex.toCharArray());
	}

	/**
	 * Repeat the bytes contained in an array given number of times
	 * 
	 * @param bytes
	 *            bytes to repeat
	 * @param repeat
	 *            number of times to repeat
	 * @return
	 */
	private byte[] repeatBytes(byte[] bytes, int repeat) {
		byte[] repeatedBytes = new byte[(bytes.length * repeat)];
		int counter = 0;
		for (int i = 0; i < repeat; i++) {
			for (int j = 0; j < bytes.length; j++)
				repeatedBytes[counter++] = bytes[j];
		}
		return repeatedBytes;
	}

	/**
	 * 
	 * Sets the first page with data from the metadata and image data.
	 * 
	 * @param imageData_FCFormat
	 * @param metadata
	 */
	public void setBitmapData(String imageData_FCFormat, ChartMetadata metadata) {
		ExportBean exportBean = new ExportBean();
		exportBean.setStream(imageData_FCFormat);
		exportBean.setMetadata(metadata);

		pagesData.add(pageIndex, exportBean);
		pageIndex++;

	}

}