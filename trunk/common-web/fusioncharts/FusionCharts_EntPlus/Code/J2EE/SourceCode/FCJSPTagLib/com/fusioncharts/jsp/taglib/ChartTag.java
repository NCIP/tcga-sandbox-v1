package com.fusioncharts.jsp.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.fusioncharts.helper.ChartDataFormat;
import com.fusioncharts.helper.FCParameters;

/**
 * This tag can be used for javascript embedding of the chart. The advanced
 * features of v3.2 can be put to use effectively.
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class ChartTag extends BodyTagSupport {

	private static final long serialVersionUID = 2L;

	/**
	 * Id for the chart, using which it will be recognized in the HTML page.
	 * Each chart on the page needs to have a unique Id.
	 */
	protected String chartId = "";
	protected String swfFilename = null;

	protected String chartData = null;
	protected String chartDataUrl = null;

	protected String dataFormat = null;
	// for future use
	protected Boolean silentUpdate = null;

	protected String xmlData = null;
	protected String xmlUrl = null;
	protected String jsonData = null;
	protected String jsonUrl = null;

	protected String width = null;
	protected String height = null;
	protected Boolean debugMode = null;
	protected Boolean registerWithJS = null;

	protected String windowMode = null;
	protected String bgColor = null;
	protected String scaleMode = null;
	protected String lang = null;
	protected String detectFlashVersion = null;
	protected String autoInstallRedirect = null;

	protected String _bodyContentString = null;

	protected String renderer = null; // no default value

	protected String renderAt = null; // no default value

	protected Object options = null;

	protected String _dataSource = null;

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
	public int doAfterBody() {
		BodyContent bc = super.getBodyContent();

		// Only if xml is not set as part of the attribute, the value will be
		// taken from body of the tag
		if (xmlData == null) {
			if (bodyContent != null) {
				String body = bc.getString();
				setBodyContentString(body);

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
			// 1. body tag contents will be accessed, if dataFormat is provided

			// if body content is not null, use it
			Boolean debugModeBool = getDebugMode();
			Boolean registerWithJSBool = getRegisterWithJS();

			int debugModeInt = 0;
			int regWithJSInt = 0;

			if (null != debugModeBool && !debugModeBool.equals("")) {
				debugModeInt = boolToNum(debugModeBool);
			}
			if (null != registerWithJSBool && !registerWithJSBool.equals("")) {
				regWithJSInt = boolToNum(registerWithJSBool);
			}
			if (getBodyContentString() != null
					&& !getBodyContentString().trim().equals("")) {

				// use the body content as the dataSource
				setDataSource(_bodyContentString);

			} else if (xmlData != null && !xmlData.equals("")) {
				setDataSource(xmlData);
				setDataFormat(ChartDataFormat.XML.toString());
			} else if (xmlUrl != null && !xmlUrl.equals("")) {
				setDataSource(xmlUrl);
				setDataFormat(ChartDataFormat.XMLURL.toString());
			} else if (jsonData != null && !jsonData.equals("")) {
				setDataSource(jsonData);
				setDataFormat(ChartDataFormat.JSON.toString());
			} else if (jsonUrl != null && !jsonUrl.equals("")) {
				setDataSource(jsonUrl);
				setDataFormat(ChartDataFormat.JSONURL.toString());
			} else if (chartData != null && !chartData.equals("")) {
				setDataSource(chartData);
			} else if (chartDataUrl != null && !chartDataUrl.equals("")) {
				setDataSource(chartDataUrl);
			}

			// Use the new constructor of FusionCharts

			FCParameters fcParams = new FCParameters(swfFilename, chartId,
					width, height, "" + debugModeInt, "" + regWithJSInt,
					windowMode, bgColor, scaleMode, lang, detectFlashVersion,
					autoInstallRedirect, _dataSource, dataFormat, renderer,
					renderAt);

			String paramsInJSON = fcParams.toJSON();
			// instantiate the chart
			// var chart_chartId = new FusionCharts(objectStyleParams).render();
			writer.write("\t\t\tvar chart_" + chartId + " = new FusionCharts("
					+ paramsInJSON + ").render();");
			writer.write("\n");

			writer.write("\t\t");
			writer.write("</script>");
			writer.write("\n");
			writer.write("\t\t<!--END Script Block for Chart " + chartId
					+ "-->\n");
		} catch (Exception ex) {
			throw new JspTagException("ChartTag: " + ex.getMessage());
		}
		// Tag instance reuse of non-similar tags is done in some jsp containers
		// To avoid this, we release all the non-reusable attributes.
		releaseNonReusableAttributes();
		return SKIP_BODY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		init();

		validateTag();
		try {

			JspWriter writer = pageContext.getOut();
			writer.write("\t\t<!-- START Script Block for Chart " + chartId
					+ "-->\n");
			writer.write("\t\t");
			writer.write("<div id='" + chartId + "Div' align='center'>");
			writer.write("\n");
			writer.write("\t\t\tChart.\n");

			/*
			 * The above text "Chart." is shown to users before the chart has
			 * started loading(if there is a lag in relaying SWF from server).
			 * This text is also shown to users who do not have Flash Player
			 * installed. You can configure it as per your needs.
			 */
			writer.write("\t\t");
			writer.write("</div>");
			writer.write("\n");

			writer.write("\t\t");
			writer.write("<script type='text/javascript'>");
			writer.write("\n");

			if (renderAt == null || renderAt.equals("")) {
				renderAt = chartId + "Div";
			}

			/*
			 * windowMode = (getWindowMode() == null) ? "" : windowMode; bgColor
			 * = (getBgColor() == null) ? "" : bgColor; scaleMode =
			 * (getScaleMode() == null) ? "" : scaleMode; lang = (getLang() ==
			 * null) ? "" : lang; detectFlashVersion = (getDetectFlashVersion()
			 * == null) ? "" : detectFlashVersion; autoInstallRedirect =
			 * (getAutoInstallRedirect() == null) ? "" : autoInstallRedirect;
			 */

		} catch (Exception ex) {
			throw new JspTagException("ChartTag: " + ex.getMessage());
		}
		return EVAL_BODY_AGAIN;
	}

	/**
	 * Returns the value in the field autoInstallRedirect
	 * 
	 * @return the autoInstallRedirect
	 */
	public String getAutoInstallRedirect() {
		return autoInstallRedirect;
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
	 * @return the bodyContentString
	 */
	public String getBodyContentString() {
		return _bodyContentString;
	}

	/**
	 * Returns the value in the field chartData
	 * 
	 * @return the chartData
	 */
	public String getChartData() {
		return chartData;
	}

	/**
	 * Returns the value in the field chartDataUrl
	 * 
	 * @return the chartDataUrl
	 */
	public String getChartDataUrl() {
		return chartDataUrl;
	}

	/**
	 * Id for the chart, using which it will be recognized in the HTML page.
	 * Each chart on the page needs to have a unique Id.
	 * 
	 * @return the chartId
	 */
	public String getChartId() {
		return chartId;
	}

	/**
	 * Returns the value in the field dataFormat
	 * 
	 * @return the dataFormat
	 */
	public String getDataFormat() {
		return dataFormat;
	}

	/**
	 * Returns the value in the field debugMode
	 * 
	 * @return the debugMode
	 */
	public Boolean getDebugMode() {
		return debugMode;
	}

	/**
	 * Returns the value in the field detectFlashVersion
	 * 
	 * @return the detectFlashVersion
	 */
	public String getDetectFlashVersion() {
		return detectFlashVersion;
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
	 * Returns the value in the field jsonData
	 * 
	 * @return the jsonData
	 */
	public String getJsonData() {
		return jsonData;
	}

	/**
	 * Returns the value in the field jsonUrl
	 * 
	 * @return the jsonUrl
	 */
	public String getJsonUrl() {
		return jsonUrl;
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
	 * Returns the value in the field options
	 * 
	 * @return the options
	 */
	public Object getOptions() {
		return options;
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
	 * Returns the value in the field renderAt
	 * 
	 * @return the renderAt
	 */
	public String getRenderAt() {
		return renderAt;
	}

	/**
	 * Returns the value in the field renderer
	 * 
	 * @return the renderer
	 */
	public String getRenderer() {
		return renderer;
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
	 * Returns the value in the field silentUpdate
	 * 
	 * @return the silentUpdate
	 */
	public Boolean getSilentUpdate() {
		return silentUpdate;
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
	 * Initialize all internal members of this tag
	 */
	private void init() {
		_dataSource = null;
		_bodyContentString = null;

		// tag attributes should not be modified by TagHandler.

	}

	/**
	 * Release all members.
	 */
	@Override
	public void release() {
		// the super class method should be called
		super.release();
		releaseAttributes();

	}

	private void releaseAttributes() {
		releaseMandatoryAttributes();
		releaseOptionalAttributes();
		releaseNonReusableAttributes();
	}

	/**
	 * Release the mandatory attributes
	 */
	private void releaseMandatoryAttributes() {
		chartId = null;
		swfFilename = null;
		width = null;
		height = null;

	}

	/**
	 * renderAt attribute cannot be reused across tags in the same page. It has
	 * to be unique.
	 */
	private void releaseNonReusableAttributes() {
		renderAt = null;
	}

	/**
	 * Release the optional attributes
	 */
	private void releaseOptionalAttributes() {
		chartData = null;
		chartDataUrl = null;
		dataFormat = null;
		xmlData = null;
		xmlUrl = null;
		jsonData = null;
		jsonUrl = null;
		debugMode = null;
		registerWithJS = null;

		windowMode = null;
		bgColor = null;
		scaleMode = null;
		lang = null;
		detectFlashVersion = null;
		autoInstallRedirect = null;

		renderer = null;
		_bodyContentString = null;
		bodyContent = null;
		_dataSource = null;
		options = null;
		silentUpdate = null;

	}

	/**
	 * Sets the value for autoInstallRedirect
	 * 
	 * @param autoInstallRedirect
	 *            the autoInstallRedirect to set
	 */
	public void setAutoInstallRedirect(String autoInstallRedirect) {
		this.autoInstallRedirect = autoInstallRedirect;
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

	@Override
	public void setBodyContent(BodyContent bc) {
		super.setBodyContent(bc);
	}

	/**
	 * @param bodyContentString
	 *            the bodyContentString to set
	 */
	public void setBodyContentString(String bodyContentString) {
		this._bodyContentString = bodyContentString;
	}

	/**
	 * Sets the value for chartData
	 * 
	 * @param chartData
	 *            the chartData to set
	 */
	public void setChartData(String chartData) {
		this.chartData = chartData;
	}

	/**
	 * Sets the value for chartDataUrl
	 * 
	 * @param chartDataUrl
	 *            the chartDataUrl to set
	 */
	public void setChartDataUrl(String chartDataUrl) {
		this.chartDataUrl = chartDataUrl;
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
	 * Sets the value for dataFormat
	 * 
	 * @param dataFormat
	 *            the dataFormat to set
	 */
	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	/**
	 * Sets the value for dataSource
	 * 
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(String dataSource) {
		this._dataSource = dataSource;
	}

	/**
	 * Sets the value for debugMode
	 * 
	 * @param debugMode
	 *            the debugMode to set
	 */
	public void setDebugMode(Boolean debugMode) {
		this.debugMode = debugMode;
	}

	/**
	 * Sets the value for detectFlashVersion
	 * 
	 * @param detectFlashVersion
	 *            the detectFlashVersion to set
	 */
	public void setDetectFlashVersion(String detectFlashVersion) {
		this.detectFlashVersion = detectFlashVersion;
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
	 * Sets the value for jsonData
	 * 
	 * @param jsonData
	 *            the jsonData to set
	 */
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	/**
	 * Sets the value for jsonUrl
	 * 
	 * @param jsonUrl
	 *            the jsonUrl to set
	 */
	public void setJsonUrl(String jsonUrl) {
		this.jsonUrl = jsonUrl;
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
	 * Sets the value for options
	 * 
	 * @param options
	 *            the options to set
	 */
	public void setOptions(Object options) {
		this.options = options;
	}

	/**
	 * @param registerWithJS
	 *            the registerWithJS to set
	 */
	public void setRegisterWithJS(Boolean registerWithJS) {
		this.registerWithJS = registerWithJS;
	}

	/**
	 * Sets the value for renderAt
	 * 
	 * @param renderAt
	 *            the renderAt to set
	 */
	public void setRenderAt(String renderAt) {
		this.renderAt = renderAt;
	}

	/**
	 * Sets the value for renderer
	 * 
	 * @param renderer
	 *            the renderer to set
	 */
	public void setRenderer(String renderer) {
		this.renderer = renderer;
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
	 * Sets the value for silentUpdate
	 * 
	 * @param silentUpdate
	 *            the silentUpdate to set
	 */
	public void setSilentUpdate(Boolean silentUpdate) {
		this.silentUpdate = silentUpdate;
	}

	/**
	 * @param swfFilename
	 *            the swfilename to set
	 */
	public void setSwfFilename(String swfFilename) {
		this.swfFilename = swfFilename;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * Sets the value for wMode
	 * 
	 * @param mode
	 *            the wMode to set
	 */
	public void setWindowMode(String mode) {
		windowMode = mode;
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

	/**
	 * Validates the tag to check if all the required data is provided
	 * 
	 * @throws JspException
	 */
	private void validateTag() throws JspException {
		// if dataFormat is not set, and none of the 4 attributes jsonURL,
		// jsonData, xmlURl and xmlData is set then Error.
		if (getChartData() == null && getChartDataUrl() == null
				&& getDataFormat() == null && getJsonUrl() == null
				&& getJsonData() == null && getXmlUrl() == null
				&& getXmlData() == null) {
			throw new JspException(
					"Please provide either xmlData, xmlURL, jsonData or jsonURL as attribute. If providing the xml or json as body of the tag, then please include the value for dataFormat attribute");
		} else if ((getChartData() != null || getChartDataUrl() != null)
				&& getDataFormat() == null) {
			throw new JspException(
					"Please include the value for dataFormat attribute while using chartData or chartDataURL attributes");
		}
		if (getDataFormat() != null) {
			try {
				ChartDataFormat.exists(getDataFormat().toLowerCase());
			} catch (IllegalArgumentException iae) {
				throw new JspException(
						"Please specify a valid value for the attribute dataFormat.The currently supported values are xml,xml-url, json and json-url.");
			}
		}

	}
}
