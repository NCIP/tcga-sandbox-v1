/**
 * 
 */
package com.fusioncharts.sampledata;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class DOMHelper {

	public static void main(String[] args) throws Exception {
		DOMHelper domHelper = new DOMHelper();
		Map<String, String> chartAttributes = new HashMap<String, String>();
		;
		chartAttributes.put("caption", "Factory Output report");
		chartAttributes.put("subCaption", "By Quantity");
		chartAttributes.put("pieSliceDepth", "30");
		chartAttributes.put("showBorder", "1");
		chartAttributes.put("formatNumberScale", "0");
		chartAttributes.put("numberSuffix", " Units");
		Document chartDoc = domHelper.getDocument();
		Element rootElement = chartDoc.createElement("chart");
		domHelper.addAttributesToElement(rootElement, chartAttributes);
		Element setElem = chartDoc.createElement("set");
		setElem.setAttribute("label", "label1");
		setElem.setAttribute("value", "value1");
		setElem.setAttribute("link", "link1");

		rootElement.appendChild(setElem);
		chartDoc.appendChild(rootElement);
		domHelper.outputDocument(chartDoc);
	}

	public Element addAttributesToElement(Element element,
			Map<String, String> attributes) {
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			// do stuff here
			element.setAttribute(key, value);
		}
		return element;
	}

	public Document getDocument() {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder;
		Document document = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			document = documentBuilder.newDocument();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return document;
	}

	public String getXMLString(Document document) {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer;
		String xmlString = "";
		try {
			transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);

			transformer.transform(source, result);

			xmlString = sw.toString();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xmlString;

	}

	public void outputDocument(Document document) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		// StreamResult result = new StreamResult(System.out);
		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);

		transformer.transform(source, result);
		String xmlString = sw.toString();

		// print xml
		System.out.println(xmlString);

	}
}