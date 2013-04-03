/**
 * 
 */
package com.fusioncharts.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Sample Usage: FCParameters fcParams = new FCParameters("Column2D.swf",
 * "myFirst", "100", "200", "0", "1", "window", "CCCCCC", "", "EN", null, "",
 * "", "xml", "flash", null);
 * 
 * fcParams.toJSON();
 * 
 * @author InfoSoft Global (P) Ltd.
 * 
 */
public class FCParameters {

	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, allowing JSON text
	 * to be delivered in HTML. In JSON text, a string cannot contain a control
	 * character or an unescaped quote or backslash.
	 * 
	 * @param string
	 *            A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string) {
		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);
		String t;

		sb.append('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			/*
			 * case '/': if (b == '<') { sb.append('\\'); } sb.append(c); break;
			 */
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
						|| (c >= '\u2000' && c < '\u2100')) {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
			}
		}
		sb.append('"');
		return sb.toString();
	}

	HashMap<FCParams, String> parameters = null;

	/**
	 * Default Constructor
	 */
	public FCParameters() {
		parameters = new HashMap<FCParams, String>();
	}

	/**
	 * Constructor with limited parameters. The mandatory values only.
	 * 
	 * @param swfFilename
	 * @param chartId
	 * @param width
	 * @param height
	 * @param debugMode
	 * @param registerWithJS
	 * @param dataSource
	 * @param dataFormat
	 * @param renderer
	 * @param renderAt
	 */
	public FCParameters(String swfFilename, String chartId, String width,
			String height, String debugMode, String registerWithJS,
			String dataSource, String dataFormat, String renderer,
			String renderAt) {
		super();

		parameters = new HashMap<FCParams, String>();
		addParameterValue(FCParams.ID.toString(), chartId);
		addParameterValue(FCParams.SWFURL.toString(), swfFilename);
		addParameterValue(FCParams.WIDTH.toString(), width);
		addParameterValue(FCParams.HEIGHT.toString(), height);
		addParameterValue(FCParams.DEBUGMODE.toString(), debugMode);
		addParameterValue(FCParams.REGISTERWITHJS.toString(), registerWithJS);
		addParameterValue(FCParams.RENDERER.toString(), renderer);
		addParameterValue(FCParams.RENDERAT.toString(), renderAt);
		addParameterValue(FCParams.DATAFORMAT.toString(), dataFormat);
		addParameterValue(FCParams.DATASOURCE.toString(), dataSource);
	}

	/**
	 * Constructor accepting most of the useful parameters If you do not wish to
	 * provide a particular parameter, please pass null value
	 * 
	 * @param swfFilename
	 *            - SWF File Name (and Path) of the chart which you intend to
	 *            plot
	 * @param chartId
	 *            - Id for the chart, using which it will be recognized in the
	 *            HTML page. Each chart on the page needs to have a unique Id.
	 * @param width
	 *            - Intended width for the chart (in pixels)
	 * @param height
	 *            - Intended height for the chart (in pixels)
	 * @param debugMode
	 *            - Whether to start the chart in debug mode
	 * @param registerWithJS
	 *            - Whether to ask chart to register itself with JavaScript
	 * @param windowMode
	 *            - "window" (default) - here the chart /Flash Player acts as a
	 *            separate always window lying above the HTML elements..so the
	 *            HTML elements like html menu always below the charts ;
	 *            "opaque" - mode where chart unlike "window" mode stay
	 *            integrated with HTML elements - here DHTML elements can come
	 *            over the chart ; "transparent" - almost like OPAQUE mode - but
	 *            with more feature that can turn a chart background
	 *            transparent/transucent (if bgAlpha is set accrodingly) so that
	 *            the color/HTML elements below the chart (which are part of
	 *            HTML and not chart) become visible.
	 * @param bgColor
	 *            - Background color of the Flash movie (here chart) which comes
	 *            below the chart and is visible if chart's background color is
	 *            set to transparent or translucent using bgAlpha, it also comes
	 *            as background color of the preloader state like when messages
	 *            like "Loading chart", "retrieving data" "No data to display"
	 *            etc. are shown - format - Hexcoded #RRGGBB color e.g.
	 *            "#ff0000"
	 * @param scaleMode
	 *            - "noScale" - recommended/default, "exactFit" - scales the
	 *            chart to fit the container exactly with width and height
	 *            (causes distortion in some cases), "noBorder" - constrained
	 *            scale. (not recommended at all), "showAll" - (not recommended)
	 * @param lang
	 *            - Language, as of now its only value is "EN" for english
	 * @param detectFlashVersion
	 *            - Checks the Flash Player version and if version is less than
	 *            8 and autoInstallRedirect is set on then asks the user to
	 *            install Flash Player from Adobe site
	 * 
	 * @param autoInstallRedirect
	 *            - If set on, the user would be redirected to Adobe site if
	 *            Flash player 8 is not installed.
	 * @param dataSource
	 *            - If you intend to use dataUrl method for the chart, provide
	 *            the Url to the xml/json as this parameter. For example, we can
	 *            specify Data/Data.xml or Data/Data.json as the URL. If you
	 *            intend to use dataString method then provide the xml or json
	 *            data in this parameter.
	 * @param dataFormat
	 *            - the format of the dataSource provided. Currently accepted
	 *            values are "xml","json", "xmlurl" and "jsonurl"
	 * @param renderer
	 *            - The renderer to be used. -
	 * @param renderAt
	 *            - The container in which to render the chart.
	 */
	public FCParameters(String swfFilename, String chartId, String width,
			String height, String debugMode, String registerWithJS,
			String windowMode, String bgColor, String scaleMode, String lang,
			String detectFlashVersion, String autoInstallRedirect,
			String dataSource, String dataFormat, String renderer,
			String renderAt) {
		super();

		parameters = new HashMap<FCParams, String>();
		addParameterValue(FCParams.ID.toString(), chartId);
		addParameterValue(FCParams.SWFURL.toString(), swfFilename);
		addParameterValue(FCParams.WIDTH.toString(), width);
		addParameterValue(FCParams.HEIGHT.toString(), height);
		addParameterValue(FCParams.DEBUGMODE.toString(), debugMode);
		addParameterValue(FCParams.REGISTERWITHJS.toString(), registerWithJS);
		addParameterValue(FCParams.WMODE.toString(), windowMode);
		addParameterValue(FCParams.SCALEMODE.toString(), scaleMode);
		addParameterValue(FCParams.BGCOLOR.toString(), bgColor);
		addParameterValue(FCParams.LANG.toString(), lang);
		addParameterValue(FCParams.AUTOINSTALLREDIRECT.toString(),
				autoInstallRedirect);
		addParameterValue(FCParams.DETECTFLASHVERSION.toString(),
				detectFlashVersion);
		addParameterValue(FCParams.RENDERER.toString(), renderer);
		addParameterValue(FCParams.RENDERAT.toString(), renderAt);
		addParameterValue(FCParams.DATAFORMAT.toString(), dataFormat);
		addParameterValue(FCParams.DATASOURCE.toString(), dataSource);
	}

	/**
	 * Adds the parameter key-value pair given to the already existing params
	 * HashMap.
	 * 
	 * @param key
	 * @param value
	 * @return boolean whether the key is valid or not
	 */
	public boolean addParameterValue(String key, String value) {
		// test validity of key
		boolean validKey = testValidity(key);
		if (validKey && value != null) {
			parameters.put(Enum.valueOf(FCParams.class, key), value);
		}
		return validKey;
	}

	/**
	 * Adds the parameter values given in the HashMap to the already existing
	 * params HashMap.
	 * 
	 * @param params
	 * @return boolean whether the key is valid or not
	 */
	public boolean addParameterValues(HashMap<String, String> params) {

		boolean validKey = true;

		Set<String> keySet = params.keySet();
		String key = "";
		String value = "";
		for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
			key = (String) iterator.next();
			value = params.get(key);
			addParameterValue(key, value);
		}
		return validKey;
	}

	public Object getParameterValue(String key) {
		return this.parameters.get(FCParams.valueOf(key));
	}

	/**
	 * Remove a name and its value, if present.
	 * 
	 * @param key
	 *            The name to be removed.
	 * @return The value that was associated with the name, or null if there was
	 *         no value.
	 */
	public Object remove(String key) {
		return this.parameters.remove(key);
	}

	/**
	 * Test the validity of the key. Whether the key is a valid FCParameter or
	 * not.
	 * 
	 * @param key
	 * @return boolean whether the key is valid or not
	 */
	public boolean testValidity(String key) {
		boolean validParam = true;
		try {
			Enum FCParams = Enum.valueOf(FCParams.class, key);
		} catch (IllegalArgumentException ex) {
			// this is not a valid parameter to FC
			validParam = false;

		}

		return validParam;
	}

	/**
	 * Converts the HashMap of keys to JSON format which can be provided to the
	 * FusionCharts constructor.
	 * 
	 * @return String- The JSON representation of the HashMap
	 */
	public String toJSON() {
		String json_representation = "";
		try {
			Iterator<FCParams> keys = parameters.keySet().iterator();
			StringBuffer sb = new StringBuffer("{");

			while (keys.hasNext()) {
				if (sb.length() > 1) {
					sb.append(',');
				}
				FCParams o = keys.next();
				sb.append(quote(o.getParamName()));
				sb.append(':');
				sb.append(quote(this.parameters.get(o)));
			}
			sb.append('}');
			json_representation = sb.toString();
		} catch (Exception e) {
			return null;
		}

		return json_representation;
	}
}
