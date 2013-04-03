package com.fusioncharts.jsp.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Extends BodyTagSupport. This tag helps in html embedding of the chart. To use
 * more advanced features supported by FusionCharts, please use the other tag
 * ChartTag.
 * 
 * @see ChartTag
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class ChartHTMLTag extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	protected String chartId = "default";
	protected String swfFilename = null;
	protected String xmlData = null;
	protected String xmlUrl = null;
	protected String width = null;
	protected String height = null;
	protected String debugMode = null;
	protected Boolean registerWithJS = null;

	protected String windowMode = null;

	protected String bgColor = null;

	protected String scaleMode = null;
	protected String lang = null;
	protected String useSingleQuotes = "false";
	protected String QUOTE = useSingleQuotes.equals("true") ? "'" : "\"";

	/**
	 * Converts a Boolean value to int value<br>
	 * 
	 * @param bool
	 *            Boolean value which needs to be converted to int value
	 * @return int value corresponding to the boolean : 1 for true and 0 for
	 *         false
	 */
	private int boolToNum(Boolean bool) {
		int num = 0;
		if (bool.booleanValue()) {
			num = 1;
		}
		return num;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspException {

		BodyContent bc = super.getBodyContent();

		// Only if xml is not set as part of the attribute, the value will be
		// taken from body of the tag
		if (xmlData == null) {
			if (bodyContent != null) {
				String body = bc.getString();
				setXmlData(body);
				// Could not find a way to put the xml as value binding
				// setValueExpression("xml", body);
			}
		}
		if (bodyContent != null)
			bodyContent.clearBody();
		return SKIP_BODY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			JspWriter writer = pageContext.getOut();
			// Default value for debugMode is set to false
			Boolean debugModeBool = new Boolean("false");
			// Default value of debugModeInt is 0
			int debugModeInt = 0;
			int regWithJSInt = 0;

			if (null != debugMode && !debugMode.equals("")) {
				debugModeBool = new Boolean(debugMode);
				debugModeInt = boolToNum(debugModeBool);
			}
			if (null != registerWithJS && !registerWithJS.equals("")) {
				regWithJSInt = boolToNum(registerWithJS);
			}
			String strFlashVars = "";
			strFlashVars = "&chartWidth=" + width + "&chartHeight=" + height
					+ "&debugMode=" + debugModeInt + "&registerWithJS="
					+ regWithJSInt + "&DOMId=" + chartId;
			if (xmlData == null || xmlData.equals(""))
				strFlashVars += "&dataURL=" + xmlUrl;
			else {
				// Strip out all carriage returns and line feeds if any.
				xmlData = xmlData.replaceAll("[\\r\\n]+", "");
				xmlData = xmlData.trim();
				strFlashVars += "&dataXML=" + xmlData;
			}

			strFlashVars += "&scaleMode=" + scaleMode + "&lang=" + lang;

			StringBuffer objectParamsBuffer = new StringBuffer();
			// FlashVars
			objectParamsBuffer.append("\t\t\t");
			objectParamsBuffer.append("<param name=" + QUOTE + "FlashVars"
					+ QUOTE + " value=" + QUOTE + "" + strFlashVars + ""
					+ QUOTE + " />");
			// value for FlashVars
			objectParamsBuffer.append("\n");

			writer.write(objectParamsBuffer.substring(0));

			StringBuffer embedTagBuffer = new StringBuffer();
			// embed

			embedTagBuffer.append("\t\t\t");
			embedTagBuffer.append("<embed");
			embedTagBuffer.append(" src=" + QUOTE + "" + swfFilename + ""
					+ QUOTE + "");
			embedTagBuffer.append(" FlashVars=" + QUOTE + "" + strFlashVars
					+ "" + QUOTE + "");
			embedTagBuffer.append(" src=" + QUOTE + "" + swfFilename + ""
					+ QUOTE + "");
			embedTagBuffer.append(" quality=" + QUOTE + "high" + QUOTE + "");
			embedTagBuffer.append(" width=" + QUOTE + "" + width + "" + QUOTE
					+ "");
			embedTagBuffer.append(" height=" + QUOTE + "" + height + "" + QUOTE
					+ "");
			embedTagBuffer.append(" name=" + QUOTE + "" + chartId + "" + QUOTE
					+ "");
			embedTagBuffer.append(" allowScriptAccess=" + QUOTE + "always"
					+ QUOTE + "");
			embedTagBuffer.append(" type=" + QUOTE
					+ "application/x-shockwave-flash" + QUOTE + "");
			embedTagBuffer.append(" pluginspage=" + QUOTE
					+ "http://www.macromedia.com/go/getflashplayer" + QUOTE
					+ "");
			if (!windowMode.equals("")) {
				embedTagBuffer.append(" wMode=" + QUOTE + "" + windowMode + ""
						+ QUOTE + "");
			}
			embedTagBuffer.append(" bgcolor=" + QUOTE + "" + bgColor + ""
					+ QUOTE + ">");

			embedTagBuffer.append("\n");
			embedTagBuffer.append("\t\t\t");
			embedTagBuffer.append("</embed>");
			writer.write(embedTagBuffer.substring(0));
			writer.write("\n");

			// End the object tag
			writer.write("\t\t");
			writer.write("</object>");
			writer.write("\n");
			writer.write("\t\t<!--END Code Block for Chart " + chartId
					+ "-->\n");
		} catch (Exception ex) {
			throw new JspTagException("ChartHTMLTag: " + ex.getMessage());
		}

		return SKIP_BODY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		QUOTE = useSingleQuotes.equals("true") ? "'" : "\"";
		try {
			JspWriter writer = pageContext.getOut();
			windowMode = (getWindowMode() == null) ? "" : windowMode;
			bgColor = (getBgColor() == null) ? "" : bgColor;
			scaleMode = (getScaleMode() == null) ? "" : scaleMode;
			lang = (getLang() == null) ? "" : lang;

			writer.write("\t\t<!-- START Code Block for Chart " + chartId
					+ "-->\n");
			writer.write("\t\t");

			StringBuffer objectStartTag = new StringBuffer();
			objectStartTag.append("<object");

			objectStartTag
					.append(" classid=" + QUOTE
							+ "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
							+ QUOTE + "");
			objectStartTag
					.append(" codebase="
							+ QUOTE
							+ "http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,0,0,0"
							+ QUOTE + "");
			objectStartTag.append(" width=" + QUOTE + "" + width + "" + QUOTE
					+ "");
			objectStartTag.append(" height=" + QUOTE + "" + height + "" + QUOTE
					+ "");
			objectStartTag.append(" name=" + QUOTE + "" + chartId + "" + QUOTE
					+ "");
			objectStartTag.append(" allowScriptAccess=" + QUOTE + "always"
					+ QUOTE + "");
			objectStartTag.append(" type=" + QUOTE
					+ "application/x-shockwave-flash" + QUOTE + "");
			objectStartTag.append(" pluginspage=" + QUOTE
					+ "http://www.macromedia.com/go/getflashplayer" + QUOTE
					+ " >");
			objectStartTag.append("\n");

			// parameters
			// allowScriptAccess
			objectStartTag.append("\t\t\t");
			objectStartTag.append("<param name=" + QUOTE + "allowScriptAccess"
					+ QUOTE + " value=" + QUOTE + "always" + QUOTE + "/>");
			objectStartTag.append("\n");

			// movie

			objectStartTag.append("\t\t\t");
			objectStartTag
					.append("<param name=" + QUOTE + "movie" + QUOTE
							+ " value=" + QUOTE + "" + swfFilename + "" + QUOTE
							+ " />");
			objectStartTag.append("\n");

			// quality

			objectStartTag.append("\t\t\t");
			objectStartTag.append("<param name=" + QUOTE + "quality" + QUOTE
					+ " value=" + QUOTE + "high" + QUOTE + "/>");
			objectStartTag.append("\n");

			if (windowMode != null) {
				windowMode = (windowMode.equalsIgnoreCase("transparent")
						|| windowMode.equalsIgnoreCase("opaque") || windowMode
						.equalsIgnoreCase("window")) ? windowMode : "";
				if (!windowMode.equals("")) {
					// wMode
					objectStartTag.append("\t\t\t");
					objectStartTag.append("<param name=" + QUOTE + "wMode"
							+ QUOTE + " value=" + QUOTE + "" + windowMode + ""
							+ QUOTE + " />");
					objectStartTag.append("\n");
				}
			}
			// bgcolor
			objectStartTag.append("\t\t\t");
			objectStartTag.append("<param name=" + QUOTE + "bgcolor" + QUOTE
					+ " value=" + QUOTE + "" + bgColor + "" + QUOTE + " />");
			objectStartTag.append("\n");

			writer.write(objectStartTag.substring(0));

			// the xml will be set in the - doAsfterBody()
		} catch (Exception ex) {
			throw new JspTagException("ChartHTMLTag: " + ex.getMessage());
		}
		return EVAL_BODY_AGAIN;
	}

	/**
	 * Returns the value in the field bgColor
	 * 
	 * @return the bgColor
	 */
	public String getBgColor() {
		return bgColor;
	}

	/**
	 * Returns the value in the field chartId
	 * 
	 * @return the chartId
	 */
	public String getChartId() {
		return chartId;
	}

	/**
	 * Returns the value in the field debugMode
	 * 
	 * @return the debugMode
	 */
	public String getDebugMode() {
		return debugMode;
	}

	/**
	 * Returns the value in the field height
	 * 
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * Returns the value in the field lang
	 * 
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Returns the value in the field registerWithJS
	 * 
	 * @return the registerWithJS
	 */
	public Boolean getRegisterWithJS() {
		return registerWithJS;
	}

	/**
	 * Returns the value in the field scaleMode
	 * 
	 * @return the scaleMode
	 */
	public String getScaleMode() {
		return scaleMode;
	}

	/**
	 * Returns the value in the field swfFilename
	 * 
	 * @return the swfFilename
	 */
	public String getSwfFilename() {
		return swfFilename;
	}

	/**
	 * Returns the value in the field useSingleQuotes
	 * 
	 * @return the useSingleQuotes
	 */
	public String getUseSingleQuotes() {
		return useSingleQuotes;
	}

	/**
	 * Returns the value in the field width
	 * 
	 * @return the width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * Returns the value in the field windowMode
	 * 
	 * @return the windowMode
	 */
	public String getWindowMode() {
		return windowMode;
	}

	/**
	 * Returns the value in the field xmlData
	 * 
	 * @return the xmlData
	 */
	public String getXmlData() {
		return xmlData;
	}

	/**
	 * Returns the value in the field xmlUrl
	 * 
	 * @return the xmlUrl
	 */
	public String getXmlUrl() {
		return xmlUrl;
	}

	/**
	 * Release all members.
	 */
	@Override
	public void release() {
		// the super class method should be called
		super.release();
		chartId = null;
		swfFilename = null;
		xmlData = null;
		xmlUrl = null;
		width = null;
		height = null;
		debugMode = null;

		windowMode = null;
		bgColor = null;
		scaleMode = null;
		lang = null;
	}

	/**
	 * Sets the value for bgColor
	 * 
	 * @param bgColor
	 *            the bgColor to set
	 */
	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	/**
	 * Sets the value for chartId
	 * 
	 * @param chartId
	 *            the chartId to set
	 */
	public void setChartId(String chartId) {
		this.chartId = chartId;
	}

	/**
	 * Sets the value for debugMode
	 * 
	 * @param debugMode
	 *            the debugMode to set
	 */
	public void setDebugMode(String debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Sets the value for height
	 * 
	 * @param height
	 *            the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * Sets the value for lang
	 * 
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * Sets the value for registerWithJS
	 * 
	 * @param registerWithJS
	 *            the registerWithJS to set
	 */
	public void setRegisterWithJS(Boolean registerWithJS) {
		this.registerWithJS = registerWithJS;
	}

	/**
	 * Sets the value for scaleMode
	 * 
	 * @param scaleMode
	 *            the scaleMode to set
	 */
	public void setScaleMode(String scaleMode) {
		this.scaleMode = scaleMode;
	}

	/**
	 * Sets the value for swfFilename
	 * 
	 * @param swfFilename
	 *            the swfFilename to set
	 */
	public void setSwfFilename(String swfFilename) {
		this.swfFilename = swfFilename;
	}

	/**
	 * Sets the value for useSingleQuotes
	 * 
	 * @param useSingleQuotes
	 *            the useSingleQuotes to set
	 */
	public void setUseSingleQuotes(String useSingleQuotes) {
		this.useSingleQuotes = useSingleQuotes;
	}

	/**
	 * Sets the value for width
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * Sets the value for windowMode
	 * 
	 * @param windowMode
	 *            the windowMode to set
	 */
	public void setWindowMode(String windowMode) {
		this.windowMode = windowMode;
	}

	/**
	 * Sets the value for xmlData
	 * 
	 * @param xmlData
	 *            the xmlData to set
	 */
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	/**
	 * Sets the value for xmlUrl
	 * 
	 * @param xmlUrl
	 *            the xmlUrl to set
	 */
	public void setXmlUrl(String xmlUrl) {
		this.xmlUrl = xmlUrl;
	}

}
