/**
* @class ZoomLineChart
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* ZoomLineChart chart render a multi-series 2D line chart with zoom, scroll and pin support.
*/
//Import Chart class
import com.fusioncharts.core.Chart;
//Error class
import com.fusioncharts.helper.FCError;
//Axis class
import com.fusioncharts.helper.Axis;
//Import Logger Class
import com.fusioncharts.helper.Logger;
//Style Object
import com.fusioncharts.core.StyleObject;
//Delegate
import mx.utils.Delegate;
//Legend Class & Associated Helpers
import com.fusioncharts.helper.AdvancedLegend;
import com.fusioncharts.helper.LegendIconGenerator;
import flash.display.BitmapData;
//Color manager for the zoom chart
import com.fusioncharts.colormanagers.ZoomChartColorManager;
//Index based scroll bar
import com.fusioncharts.components.HorIndexScrollBar;
//Drop shadow filter
import flash.filters.DropShadowFilter;
//External Interface - to expose methods via JavaScript
import flash.external.ExternalInterface;
//Extensions
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.extensions.StringExt;
import com.fusioncharts.extensions.MathExt;
import com.fusioncharts.extensions.DrawingExt;
class com.fusioncharts.core.charts.ZoomLineChart extends Chart {
	//Version number (if different from super Chart class)
	//private var _version:String = "3.0.0";
	//Instance variables
	//List of chart objects
	private var _arrObjects:Array;
	private var xmlData:XML;
	//----- Data containers ------//
	//Stores all properties for dataset
	private var dataset:Array;
	//Stores all values for the chart
	private var data:Array;
	//Stores all category labels for the chart
	private var labels:Array;
	//Formatted value for each value
	private var formattedVal:Array;
	//Stores any custom tool-tips for the chart
	private var toolText:Array;
	//Stores any display value for the chart
	private var displayValue:Array, displayValueGiven:Array;
	//Array to store global reference of index for items in subset
	private var subsetIndex:Array;
	//Arrays to store X and Y position of items in subset
	private var subsetX:Array, subsetY:Array;
	//To store indexes of view as it's zoomed in
	private var zoomHistory:Array;
	//Axis charts can have trend lines. trendLines array
	//stores the trend lines for an axis chart.
	private var trendLines:Array;
	//Axis charts can have vertical lines dividing the x-axis
	//labels. Container to store those vLines.
	private var vTrendLines:Array;
	//---- Counters & Calculation Fields ------//
	//Number of items per dataset (defined by number of category labels)
	private var numItems:Number;
	//Number of datasets
	private var numDS:Number;
	//Sub-set indexes of the currently visible (and later zoomed) display set
	private var subsetStartIndex:Number, subsetEndIndex:Number;
	//Step to be used for skipping data within the current display data subset
	private var subsetStep:Number;
	//Number of items in the current display subset
	private var subsetItems:Number;
	//Slots that we've to render points
	private var slots:Number;
	//Plot Distance between two consecutive points
	private var ipdistance:Number;
	//Index of pinned data
	private var pinStartIndex:Number, pinEndIndex:Number;
	//Minimum and maximum value in the subset. Required for axis calculation
	private var subsetMin:Number, subsetMax:Number;
	//Scroll pane size
	private var scrollPaneSize:Number;
	//numTrendLines stores the number of trend lines
	private var numTrendLines:Number = 0;
	//Number of trend lines below
	private var numTrendLinesBelow:Number = 0;
	//Number of vertical lines
	private var numVTrendLines:Number = 0, numVTrendLinesBelow:Number=0;;
	//Quick reference to canvas properties
	private var canvasX:Number, canvasY:Number, canvasWidth:Number, canvasHeight:Number;
	//Holding last index for tooltip display
	private var lastIndex:Number;
	//---- State Fields -----//
	//Flag indicating whether chart is in zoom mode (true) or
	//pin mode (false)
	private var zoomMode:Boolean;
	//Whether an instance of pin has already been drawn
	private var pinDrawn:Boolean;
	//Tool tip style
	private var toolTipStyle:Object;
	//---- Objects ----//
	//Color Manager for the chart
	public var colorM:ZoomChartColorManager;
	//Reference to legend component of chart
	private var lgnd:AdvancedLegend;
	//Reference to axis
	private var ax:Axis;
	//Scroll bar
	private var scrollBar:HorIndexScrollBar;
	//Context menu items
	private var zoomOutCMI:ContextMenuItem, resetChartCMI:ContextMenuItem, zoomModeCMI:ContextMenuItem, pinModeCMI:ContextMenuItem;
	//------ Containers -------//
	private var canvasMC:MovieClip, scrollMC:MovieClip, chartMC:MovieClip, valuesMC:MovieClip;
	private var xAxisLabelsMC:MovieClip, yAxisValuesMC:MovieClip;
	private var canvasOverlay:MovieClip, canvasZoomPane:MovieClip;
	private var trendBelowMC:MovieClip, trendAboveMC:MovieClip;
	private var vTrendBelowMC:MovieClip, vTrendAboveMC:MovieClip;
	private var toolbarMC:MovieClip, tooltipMC:MovieClip;
	private var pinContainer:MovieClip, zoomPaneContainer:MovieClip, ttMC:MovieClip;
	//Buttons for tool-bar
	private var btnZoomOut:MovieClip, btnShowAll:MovieClip, btnPinMode:MovieClip;
	//Icon containers
	private var mouseCursors:MovieClip, mouseDragCursor:MovieClip, mouseZoomCursor:MovieClip, mousePinCursor:MovieClip;
	//Reference to legend movie clip
	private var lgndMC:MovieClip;
	/**
	* Constructor function. We invoke the super class'
	* constructor and then set the objects for this chart.
	*/
	function ZoomLineChart(targetMC:MovieClip, depth:Number, width:Number, height:Number, x:Number, y:Number, debugMode:Boolean, lang:String, scaleMode:String, registerWithJS:Boolean, DOMId:String) {
		//Invoke the super class constructor
		super(targetMC, depth, width, height, x, y, debugMode, lang, scaleMode, registerWithJS, DOMId);
		//Log additional information to debugger
		//We log version from this class, so that if this class version
		//is different, we can log it
		this.log("Version", _version, Logger.LEVEL.INFO);
		this.log("Chart Type", "Zoom Line Chart", Logger.LEVEL.INFO);
		//List Chart Objects and set them
		_arrObjects = new Array("BACKGROUND", "CANVAS", "CAPTION", "SUBCAPTION", "YAXISNAME", "XAXISNAME", "DIVLINES", "YAXISVALUES", "DATALABELS", "DATAVALUES", "TRENDLINES", "TRENDVALUES", "DATAPLOT", "ANCHORS", "TOOLTIP", "LEGEND", "SCROLLBAR", "OVERLAYLABEL", "ENABLEDBUTTON", "DISABLEDBUTTON", "DATATOOLTIP", "VTRENDLINES", "VTRENDVALUES");
		super.setChartObjects(_arrObjects);
		//Initialize the containers for chart
		data = new Array();
		dataset = new Array();
		labels = new Array();
		toolText = new Array();
		formattedVal = new Array();
		displayValue = new Array();
		displayValueGiven = new Array();
		zoomHistory = new Array();
		//Initialize the number of data elements present
		numDS = 0;
		numItems = 0;
		slots = 0;
		//Set the chart in zoom mode by default
		zoomMode = true;
		pinDrawn = false;
		//Add external Interface APIs exposed by this chart
		extInfMethods += ",zoomTo,setZoomMode,zoomOut,resetChart,getViewStartIndex,getViewEndIndex";
		if (this.registerWithJS == true && ExternalInterface.available) {
			//To zoom out to previous level
			ExternalInterface.addCallback("zoomOut", this, extInfZoomOut);
			//To reset chart
			ExternalInterface.addCallback("resetChart", this, extInfResetChart);
			//To zoom into specific subset of data
			ExternalInterface.addCallback("zoomTo", this, extInfSetZoomIndex);
			//To switch modes
			ExternalInterface.addCallback("setZoomMode", this, extInfSetZoomMode);
			//To get currently viewed indexes
			ExternalInterface.addCallback("getViewStartIndex", this, extInfGetStartIndex);
			ExternalInterface.addCallback("getViewEndIndex", this, extInfGetEndIndex);
		}
	}
	/**
	* render method is the single call method that does the rendering of chart:
	* - Parsing XML
	* - Calculating values and co-ordinates
	* - Visual layout and rendering
	* - Event handling
	* @param	isRedraw		Whether the chart is a redraw (called during resize).
	*/
	public function render(isRedraw:Boolean):Void {
		//If it's the first draw, perform accordingly
		if (!isRedraw) {
			//Parse the XML Data document
			this.parseXML();
			//Now, if the number of data elements is 0, we show pertinent
			//error.
			if (this.numDS*this.numItems == 0) {
				tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
				//Add a message to log.
				log("No Data to Display", "No data was found in the XML data document provided. Possible cases can be: <LI>There isn't any data generated by your system. If your system generates data based on parameters passed to it using dataURL, please make sure dataURL is URL Encoded.</LI><LI>You might be providing the data in wrong format (e.g., in single series format).</LI>", Logger.LEVEL.ERROR);
				//Expose rendered method
				exposeChartRendered();
				//Also raise the no data event
				raiseNoDataExternalEvent();
			} else {
				//Detect number scales
				detectNumberScales();
				//Set the default values for data-sets
				setDataDefaults();
				//Set all points to be drawn in the first render, if no start/end index has been specified
				if (this.params.displayStartIndex != undefined && this.params.displayEndIndex != undefined && !isNaN(this.params.displayStartIndex) && !isNaN(this.params.displayEndIndex)) {
					//Store in zoomHistory
					zoomHistory.push({start:this.params.displayStartIndex, end:this.params.displayEndIndex});
					//Set the indexes
					setSubsetIndexes(Number(this.params.displayStartIndex), Number(this.params.displayEndIndex));					
				} else {
					setSubsetIndexes(1, numItems);
				}
				//Calculate the min/max value in the global subset
				calculateSubsetLimits();
				//Calculate the axis limits
				calculateAxis();
				//Set Style defaults
				setStyleDefaults();
				//Set tool tip parameter
				setToolTipParam();
				//Validate trend lines
				validateTrendLines();
				//Allot the depths for various charts objects now
				allotDepths();
				//Calculate the base co-ordinates
				calculateBaseCoords(isRedraw);
				//Calculate the skip index and size of scroll bar
				//based on the number of items and visible area
				calculateSubsetSteps();
				//Create the sub-set of data to be displayed
				populateSubset();
				//Calculate sub-set co-ordinates
				calculateSubsetCoords();
				//Create containers
				createContainers(this.cMC);
				//Draw base objects
				drawBaseObjects();
				//Draw the mouse icons
				drawMouseCursors();
				//Draw the display subset
				drawSubset();
				//Remove application message
				removeAppMessage(this.tfAppMsg);
				//Render context menu
				setContextMenu();
				//Set context menu state
				setContextMenuState();
				//Expose that chart has rendered
				exposeChartRendered();				
			}
		} else {
			if (this.numDS*this.numItems == 0) {
				tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
				//Add a message to log.
				log("No Data to Display", "No data was found in the XML data document provided. Possible cases can be: <LI>There isn't any data generated by your system. If your system generates data based on parameters passed to it using dataURL, please make sure dataURL is URL Encoded.</LI><LI>You might be providing the data in wrong format (e.g., in single series format).</LI>", Logger.LEVEL.ERROR);
				//Expose rendered method
				exposeChartRendered();
				//Terminate other process.
				return;
			}
			//Resize the chart
			//Calculate base co-ordinates again
			calculateBaseCoords(isRedraw);
			//If axis is dynamic, re-calculate the limits
			if (this.params.dynamicAxis) {
				//Adjust axis accordingly
				calculateAxis();
			}
			//Calculate sub-set co-ordinates          
			calculateSubsetCoords();
			//Set mouse cursor to normal
			setMouseCursor("normal");
			//Draw base objects
			drawBaseObjects();
			//Draw the display subset
			drawSubset();
			//Now, if the chart has been resized in pin mode, draw the overlay pin
			if (zoomMode == false) {
				if (pinStartIndex != 0 && pinEndIndex != 0) {
					drawOverlayPin(pinStartIndex, pinEndIndex);
				}
			}
			//Update scroll properties         
			updateScrollProps();
			//Set context menu state
			setContextMenuState();
			//Expose that chart has rendered
			exposeChartRendered();
		}
	}
	/**
	* parseXML method parses the XML data, sets defaults and validates
	* the attributes before storing them to data storage objects.
	*/
	private function parseXML():Void {
		//Get the element nodes
		var arrDocElement:Array = this.xmlData.childNodes;
		//Loop variable
		var i:Number;
		var j:Number;
		var k:Number;
		//Look for <graph> element
		for (i=0; i<arrDocElement.length; i++) {
			//If it's a <graph> element, proceed.
			//Do case in-sensitive mathcing by changing to upper case
			if (arrDocElement[i].nodeName.toUpperCase() == "GRAPH" || arrDocElement[i].nodeName.toUpperCase() == "CHART") {
				//Extract attributes of <graph> element
				this.parseAttributes(arrDocElement[i]);
				//Extract common attributes/over-ride chart specific ones
				this.parseCommonAttributes(arrDocElement[i], true);
				//Over-ride attributes
				this.overrideAttributes(arrDocElement[i]);
				//Now, get the child nodes - first level nodes
				//Level 1 nodes can be - CATEGORIES, DATASET, TRENDLINES, STYLES etc.
				var arrLevel1Nodes:Array = arrDocElement[i].childNodes;
				var setNode:XMLNode;
				//Iterate through all level 1 nodes.
				for (j=0; j<arrLevel1Nodes.length; j++) {
					if (arrLevel1Nodes[j].nodeName.toUpperCase() == "CATEGORIES") {
						//Categories Node.
						var categoriesNode:XMLNode = arrLevel1Nodes[j];
						//Convert attributes to array
						var categoriesAtt:Array = this.getAttributesArray(categoriesNode);
						//Extract attributes of this node.
						this.params.catFont = getFV(categoriesAtt["font"], this.params.outCnvBaseFont);
						this.params.catFontSize = getFN(categoriesAtt["fontsize"], this.params.outCnvBaseFontSize);
						this.params.catFontColor = formatColor(getFV(categoriesAtt["fontcolor"], this.params.outCnvBaseFontColor));
						//Based on whether the data is in compact mode or not, proceed
						if (this.params.compactMode) {
							//Categories are in compact mode. Parse accordingly.
							//Split the labels on separator
							var arrLabels:Array = categoriesNode.firstChild.nodeValue.split(this.params.dataSeparator);
							//Iterate and add to data model.
							for (k=0; k<arrLabels.length; k++) {
								//Update counter
								numItems++;
								labels[numItems] = arrLabels[k];
							}
						} else {
							//Get reference to child node.
							var arrLevel2Nodes:Array = arrLevel1Nodes[j].childNodes;
							for (k=0; k<arrLevel2Nodes.length; k++) {
								if (arrLevel2Nodes[k].nodeName.toUpperCase() == "CATEGORY") {
									//Category Node.
									//Update counter
									numItems++;
									//Extract attributes
									var categoryNode:XMLNode = arrLevel2Nodes[k];
									var categoryAtt:Array = this.getAttributesArray(categoryNode);
									//Store it
									labels[numItems] = getFV(categoryAtt["label"], categoryAtt["name"], "");
								}
							}
						}
					} else if (arrLevel1Nodes[j].nodeName.toUpperCase() == "DATASET") {
						//Increment
						this.numDS++;
						//Dataset node.
						var dataSetNode:XMLNode = arrLevel1Nodes[j];
						//Get attributes array
						var dsAtts:Array = this.getAttributesArray(dataSetNode);
						//Create storage object in dataset array
						this.dataset[this.numDS] = new Object();
						//Store attributes
						this.dataset[this.numDS].seriesName = getFV(dsAtts["seriesname"], "");
						this.dataset[this.numDS].color = formatColor(getFV(dsAtts["color"], this.params.lineColor, this.defColors.getColor ()));
						this.dataset[this.numDS].alpha = getFN(dsAtts["alpha"], this.params.lineAlpha);
						this.dataset[this.numDS].showValues = toBoolean(getFN(dsAtts["showvalues"], this.params.showValues));
						this.dataset[this.numDS].valuePosition = getFV(dsAtts["valueposition"], this.params.valuePosition);
						this.dataset[this.numDS].valuePosition = this.dataset[this.numDS].valuePosition.toUpperCase();
						this.dataset[this.numDS].includeInLegend = toBoolean(getFN(dsAtts["includeinlegend"], 1));
						this.dataset[this.numDS].lineThickness = getFN(dsAtts["linethickness"], this.params.lineThickness);
						//Data set anchors
						this.dataset[this.numDS].drawAnchors = toBoolean(getFN(dsAtts["drawanchors"], dsAtts["showanchors"], (this.params.drawAnchors) ? 1 : 0));
						this.dataset[this.numDS].anchorRadius = getFN(dsAtts["anchorradius"], this.params.anchorRadius);
						this.dataset[this.numDS].anchorBorderColor = formatColor(getFV(dsAtts["anchorbordercolor"], this.params.anchorBorderColor, this.dataset[this.numDS].color));
						this.dataset[this.numDS].anchorBorderThickness = getFN(dsAtts["anchorborderthickness"], this.params.anchorBorderThickness);
						this.dataset[this.numDS].anchorBgColor = formatColor(getFV(dsAtts["anchorbgcolor"], this.params.anchorBgColor, this.dataset[this.numDS].color));
						this.dataset[this.numDS].anchorBgAlpha = getFN(dsAtts["anchorbgalpha"], this.params.anchorBgAlpha);
						//Create sub arrays for this dataset
						data[numDS] = new Array();
						toolText[numDS] = new Array();
						displayValue[numDS] = new Array();
						displayValueGiven[numDS] = new Array();
						formattedVal[numDS] = new Array();
						//Counter
						var setCount:Number = 0;
						//Based on whether data is in compact mode or not, take action
						if (this.params.compactMode) {
							//Split the values on separator
							var arrValues:Array = dataSetNode.firstChild.nodeValue.split(this.params.dataSeparator);
							//Iterate and add to data model.
							for (k=0; k<arrValues.length; k++) {
								//Increase counter
								setCount++;
								//Now, get value.
								var setValue:Number = Number(arrValues[k]);
								//Store value
								data[numDS][setCount] = setValue;
							}
						} else {
							//Get reference to child node.
							var arrLevel2Nodes:Array = arrLevel1Nodes[j].childNodes;
							//Iterate through all child-nodes of DATASET element
							//and search for SET node
							for (k=0; k<arrLevel2Nodes.length; k++) {
								if (arrLevel2Nodes[k].nodeName.toUpperCase() == "SET") {
									//Set Node. So extract the data.
									//Update counter
									setCount++;
									//Get reference to node.
									setNode = arrLevel2Nodes[k];
									//Get attributes
									var atts:Array;
									atts = getAttributesArray(setNode);
									//Now, get value.
									var setValue:Number = Number(atts["value"]);
									//Get explicitly specified display value
									var setExDispVal:String = getFV(atts["displayvalue"], "");
									var setToolText:String = getFV(atts["tooltext"], atts["hovertext"]);
									//Store value
									data[numDS][setCount] = setValue;
									toolText[numDS][setCount] = setToolText;
									displayValue[numDS][setCount] = setExDispVal;
								}
							}
						}
						//If dataset alpha is zero or no anchor is visible, chart should not show the legend item for that dataset
						//Also needs to consider whether the dataset is included to legend or not.
						if(this.dataset[this.numDS].includeInLegend){
							this.dataset[this.numDS].includeInLegend = (this.dataset[this.numDS].alpha > 0 || this.dataset[this.numDS].drawAnchors)? true:false; 
						}
					} else if (arrLevel1Nodes[j].nodeName.toUpperCase() == "STYLES") {
						//Styles Node - extract child nodes
						var arrStyleNodes:Array = arrLevel1Nodes[j].childNodes;
						//Parse the style nodes to extract style information
						super.parseStyleXML(arrStyleNodes);
					} else if (arrLevel1Nodes[j].nodeName.toUpperCase() == "TRENDLINES") {
						//Trend lines node
						var arrTrendNodes:Array = arrLevel1Nodes[j].childNodes;
						//Parse the trend line nodes
						parseTrendLineXML(arrTrendNodes);
					} else if (arrLevel1Nodes[j].nodeName.toUpperCase() == "VTRENDLINES") {
						//Trend lines node
						var arrTrendNodes:Array = arrLevel1Nodes[j].childNodes;
						//Parse the vertical trend line nodes
						parseVTrendLineXML(arrTrendNodes);
					} 
				}
			}
		}
		//Delete all temporary objects used for parsing XML Data document
		delete setNode;
		delete arrDocElement;
		delete arrLevel1Nodes;
		delete arrLevel2Nodes;
	}
	/**
	* parseAttributes method parses the attributes and stores them in
	* chart storage objects.
	* Starting ActionScript 2, the parsing of XML attributes have also
	* become case-sensitive. However, prior versions of FusionCharts
	* supported case-insensitive attributes. So we need to parse all
	* attributes as case-insensitive to maintain backward compatibility.
	* To do so, we first extract all attributes from XML, convert it into
	* lower case and then store it in an array. Later, we extract value from
	* this array.
	* @param	graphElement	XML Node containing the <graph> element
	*							and it's attributes
	*/
	private function parseAttributes(graphElement:XMLNode):Void {
		//Array to store the attributes
		var atts:Array = this.getAttributesArray(graphElement);
		//NOW IT'S VERY NECCESARY THAT WHEN WE REFERENCE THIS ARRAY
		//TO GET AN ATTRIBUTE VALUE, WE SHOULD PROVIDE THE ATTRIBUTE
		//NAME IN LOWER CASE. ELSE, UNDEFINED VALUE WOULD SHOW UP.
		//Extract attributes pertinent to this chart
		//Which palette to use?
		this.params.palette = getFN(atts["palette"], 1);
		//Palette colors to use
		this.params.paletteColors = getFV(atts["palettecolors"], "");
		//If single color them is to be used
		this.params.paletteThemeColor = formatColor (getFV(atts["palettethemecolor"], ""));
		//Set palette colors before parsing the <set> nodes.
		this.setPaletteColors();
		//Setup the color manager
		this.setupColorManager(this.params.palette, this.params.paletteThemeColor);
		//Whether data is provided in compact mode
		this.params.compactMode = toBoolean(getFN(atts["compactdatamode"], 0));
		//If data is provided in compact mode, separator character
		this.params.dataSeparator = getFV(atts["dataseparator"], "|");
		// ---------- PADDING AND SPACING RELATED ATTRIBUTES ----------- //
		//captionPadding = Space between caption/subcaption and tool bar buttons
		this.params.captionPadding = getFN(atts["captionpadding"], 15);
		//Padding for x-axis name - to the right
		this.params.xAxisNamePadding = getFN(atts["xaxisnamepadding"], 5);
		//Padding for y-axis name - from top
		this.params.yAxisNamePadding = getFN(atts["yaxisnamepadding"], 5);
		//Y-Axis Values padding - Horizontal space between the axis edge and
		//y-axis values or trend line values (on left/right side).
		this.params.yAxisValuesPadding = getFN(atts["yaxisvaluespadding"], 2);
		//Label padding - Vertical space between the labels and canvas end position
		this.params.labelPadding = getFN(atts["labelpadding"], atts["labelspadding"], 3);
		//Value padding - vertical space between the anchors and start of value textboxes
		this.params.valuePadding = getFN(atts["valuepadding"], 2);
		//Padding of legend from right/bottom side of canvas
		this.params.legendPadding = getFN(atts["legendpadding"], 6);
		//Chart Margins - Empty space at the 4 sides
		this.params.chartLeftMargin = getFN(atts["chartleftmargin"], 15);
		this.params.chartRightMargin = getFN(atts["chartrightmargin"], 15);
		this.params.chartTopMargin = getFN(atts["charttopmargin"], 15);
		this.params.chartBottomMargin = getFN(atts["chartbottommargin"], 15);
		//Tool bar button padding and margins
		this.params.toolBarBtnVPadding = getFN(atts["toolbarbtnvpadding"], 5);
		this.params.toolBarBtnTextHMargin = getFN(atts["toolbarbtntexthmargin"], 3);
		this.params.toolBarBtnHPadding = getFN(atts["toolbarbtnhpadding"], 5);
		this.params.toolBarBtnTextVMargin = getFN(atts["toolbarbtntextvmargin"], 6);
		// ------------------ AXIS CONFIGURATION -----------------------//
		this.params.axis = getFV(atts["axis"], "LINEAR");
		//Whether to have a dynamic axis
		this.params.dynamicAxis = toBoolean(getFN(atts["dynamicaxis"], 0));
		//Convert to upper case, and restrict between LOG and LINEAR
		this.params.axis = this.params.axis.toUpperCase();
		if (this.params.axis != "LINEAR" && this.params.axis != "LOG") {
			//Default to linear
			this.params.axis = "LINEAR";
		}
		//Base of log               
		this.params.logBase = getFN(atts["logbase"], atts["base"], 10);
		//Base cannot be <0 and cannot be 1
		if (this.params.logBase<0 || this.params.logBase == 1) {
			//Set default to 10
			this.params.logBase = 10;
		}
		// -------------- Start and end indexes for display ------------// 
		this.params.displayStartIndex = atts["displaystartindex"];
		this.params.displayEndIndex = atts["displayendindex"];
		// -------------------------- HEADERS ------------------------- //              
		//Chart Caption and sub Caption
		this.params.caption = getFV(atts["caption"], "");
		this.params.subCaption = getFV(atts["subcaption"], "");
		//X and Y Axis Name
		this.params.xAxisName = getFV(atts["xaxisname"], "");
		this.params.yAxisName = getFV(atts["yaxisname"], "");
		//Adaptive yMin - if set to true, the y min will be based on the values
		//provided. It won't be set to 0 in case of all positive values
		this.params.setAdaptiveYMin = toBoolean(getFN(atts["setadaptiveymin"], 0));
		// --------------------- CONFIGURATION ------------------------- //
		//The upper and lower limits of y and x axis
		this.params.yAxisMinValue = atts["yaxisminvalue"];
		this.params.yAxisMaxValue = atts["yaxismaxvalue"];
		//Animation is not supported in this chart
		this.params.animation = false;
		//Whether to set the default chart animation
		this.params.defaultAnimation = false;
		//Whether null data points are to be connected or left broken
		this.params.connectNullData = toBoolean(getFN(atts["connectnulldata"], 0));
		//Number of pixels required to draw a point. Lesser the number, the more cluttered the chart
		//looks.
		this.params.pixelsPerPoint = getFN(atts["pixelsperpoint"], 15);
		//Whether tool bar is to be drawn
		this.params.drawToolbarButtons = toBoolean(getFN(atts["drawtoolbarbuttons"], 1));
		//Whether pin mode should be allowed
		this.params.allowPinMode = toBoolean(getFN(atts["allowpinmode"], 1));
		//Configuration to set whether to show the labels
		this.params.showLabels = toBoolean(getFN(atts["showlabels"], atts["shownames"], 1));
		//Label Display Mode - WRAP, STAGGER, ROTATE or NONE
		this.params.labelDisplay = getFV(atts["labeldisplay"], "AUTO");
		//Remove spaces and capitalize
		this.params.labelDisplay = StringExt.removeSpaces(this.params.labelDisplay);
		this.params.labelDisplay = this.params.labelDisplay.toUpperCase();
		//Store a copy
		this.params.labelDisplayOrig = this.params.labelDisplay;
		//Number of visible labels per screen of chart
		this.params.numVisibleLabels = getFN(atts["numvisiblelabels"], -1);
		//Option to show vertical x-axis labels
		this.params.rotateLabels = getFV(atts["rotatelabels"], atts["rotatenames"]);
		//Whether to slant label (if rotated)
		this.params.slantLabels = toBoolean(getFN(atts["slantlabels"], atts["slantlabel"], 0));
		//Angle of rotation based on slanting
		this.config.labelAngle = (this.params.slantLabels == true) ? 315 : 270;
		//If rotateLabels has been explicitly specified, we assign ROTATE value to this.params.labelDisplay
		this.params.labelDisplay = (this.params.rotateLabels == "1") ? "ROTATE" : this.params.labelDisplay;
		//Number of stagger lines
		this.params.staggerLines = int(getFN(atts["staggerlines"], 2));
		//Cannot be less than 2
		this.params.staggerLines = (this.params.staggerLines<2) ? 2 : this.params.staggerLines;
		//Configuration whether to show data values
		this.params.showValues = toBoolean(getFN(atts["showvalues"], 0));
		//Value position
		this.params.valuePosition = getFV(atts["valueposition"], "AUTO");
		//Whether to rotate values
		this.params.rotateValues = toBoolean(getFN(atts["rotatevalues"], 0));
		//Option to show/hide y-axis values
		this.params.showYAxisValues = getFN(atts["showyaxisvalues"], atts["showyaxisvalue"], 1);
		//Whether to rotate y-axis name
		this.params.rotateYAxisName = toBoolean(getFN(atts["rotateyaxisname"], 1));
		//Max width to be alloted to y-axis name - No defaults, as it's calculated later.
		this.params.yAxisNameWidth = (this.params.yAxisName != undefined && this.params.yAxisName != "") ? atts ["yaxisnamewidth"] : 0;
		//Click URL
		this.params.clickURL = getFV(atts["clickurl"], "");
		// ------------------------- COSMETICS -----------------------------//
		//Background properties - Gradient
		this.params.bgColor = getFV(atts["bgcolor"], this.colorM.get2DBgColor());
		this.params.bgAlpha = getFV(atts["bgalpha"], this.colorM.get2DBgAlpha());
		this.params.bgRatio = getFV(atts["bgratio"], this.colorM.get2DBgRatio());
		this.params.bgAngle = getFV(atts["bgangle"], this.colorM.get2DBgAngle());
		//Border Properties of chart
		this.params.showBorder = toBoolean(getFN(atts["showborder"], 1));
		this.params.borderColor = formatColor(getFV(atts["bordercolor"], this.colorM.get2DBorderColor()));
		this.params.borderThickness = getFN(atts["borderthickness"], 1);
		this.params.borderAlpha = getFN(atts["borderalpha"], this.colorM.get2DBorderAlpha());
		//Canvas background properties - Gradient
		this.params.canvasBgColor = formatColor (getFV(atts["canvasbgcolor"], this.colorM.get2DCanvasBgColor()));
		this.params.canvasBgAlpha = getFV(atts["canvasbgalpha"], this.colorM.get2DCanvasBgAlpha());
		this.params.canvasBgRatio = getFV(atts["canvasbgratio"], this.colorM.get2DCanvasBgRatio());
		this.params.canvasBgAngle = getFV(atts["canvasbgangle"], this.colorM.get2DCanvasBgAngle());
		//Canvas Border properties
		this.params.canvasBorderColor = formatColor(getFV(atts["canvasbordercolor"], this.colorM.get2DCanvasBorderColor()));
		this.params.canvasBorderThickness = getFN(atts["canvasborderthickness"], 1);
		this.params.canvasBorderAlpha = getFN(atts["canvasborderalpha"], this.colorM.get2DCanvasBorderAlpha());
		//Line Properties
		this.params.lineColor = atts["linecolor"];
		this.params.lineThickness = getFN(atts["linethickness"], 2);
		this.params.lineAlpha = getFN(atts["linealpha"], 100);
		//Pin mode line thickness delta
		this.params.pinLineThicknessDelta = getFN(atts["pinlinethicknessdelta"], 1);
		//Scroll Properties
		//Color for the scroll bar
		this.params.scrollColor = formatColor(getFV(atts["scrollcolor"], this.colorM.get2DScrollBarColor()));
		//Vertical padding between the canvas end Y and scroll bar
		this.params.scrollPadding = getFN(atts["scrollpadding"], -Math.floor(this.params.canvasBorderThickness/2));
		//Height of scroll bar
		this.params.scrollHeight = getFN(atts["scrollheight"], 16);
		//Width of plus and minus button
		this.params.scrollBtnWidth = getFN(atts["scrollbtnwidth"], 16);
		//Padding between the button and the face.
		this.params.scrollBtnPadding = getFN(atts["scrollbtnpadding"], 0);
		//Legend properties
		this.params.showLegend = toBoolean(getFN(atts["showlegend"], 1));
		//Alignment position
		this.params.legendPosition = getFV(atts["legendposition"], "BOTTOM");
		//Legend position can be either RIGHT or BOTTOM -Check for it
		this.params.legendPosition = (this.params.legendPosition.toUpperCase() == "RIGHT") ? "RIGHT" : "BOTTOM";
		this.params.interactiveLegend = toBoolean(getFN(atts["interactivelegend"], 1));
		this.params.legendCaption = getFV(atts["legendcaption"], "");
		this.params.legendMarkerCircle = toBoolean(getFN(atts["legendmarkercircle"], 0));
		this.params.legendBorderColor = formatColor(getFV(atts["legendbordercolor"], this.colorM.get2DLegendBorderColor()));
		this.params.legendBorderThickness = getFN(atts["legendborderthickness"], 1);
		this.params.legendBorderAlpha = getFN(atts["legendborderalpha"], 100);
		this.params.legendBgColor = formatColor (getFV(atts["legendbgcolor"], this.colorM.get2DLegendBgColor()));
		this.params.legendBgAlpha = getFN(atts["legendbgalpha"], 100);
		this.params.legendShadow = toBoolean(getFN(atts["legendshadow"], 1));
		this.params.legendAllowDrag = toBoolean(getFN(atts["legendallowdrag"], 0));
		this.params.legendScrollBgColor = formatColor(getFV(atts["legendscrollbgcolor"], "CCCCCC"));
		this.params.legendScrollBarColor = formatColor(getFV(atts["legendscrollbarcolor"], this.params.legendBorderColor));
		this.params.legendScrollBtnColor = formatColor(getFV(atts["legendscrollbtncolor"], this.params.legendBorderColor));
		this.params.reverseLegend = toBoolean(getFN(atts["reverselegend"], 0));
		this.params.legendIconScale = getFN(atts["legendiconscale"], 1);
		if (this.params.legendIconScale<=0 || this.params.legendIconScale>5) {
			this.params.legendIconScale = 1;
		}
		this.params.legendNumColumns = Math.round(getFN(atts["legendnumcolumns"], 0));
		if (this.params.legendNumColumns<0) {
			this.params.legendNumColumns = 0;
		}
		this.params.minimiseWrappingInLegend = toBoolean(getFN(atts["minimisewrappinginlegend"], 0));
		//Horizontal grid division Lines - Number, color, thickness & alpha
		//Necessarily need a default value for numDivLines.
		this.params.numDivLines = getFN(atts["numdivlines"], 50);
		this.params.numMinorLogDivLines = getFN(atts["numminorlogdivlines"], -1);
		//Hint of div lines
		this.params.divIntervalHints = getFV(atts["divintervalhints"], "");
		this.params.divLineColor = formatColor(getFV(atts["divlinecolor"], this.colorM.get2DDivLineColor()));
		this.params.divLineThickness = getFN(atts["divlinethickness"], 1);
		this.params.divLineAlpha = getFN(atts["divlinealpha"], this.colorM.get2DDivLineAlpha());
		this.params.divLineIsDashed = toBoolean(getFN(atts["divlineisdashed"], 1));
		this.params.divLineDashLen = getFN(atts["divlinedashlen"], 4);
		this.params.divLineDashGap = getFN(atts["divlinedashgap"], 2);
		//Vertical div lines
		this.params.showVDivLines = toBoolean(getFN(atts["showvdivlines"], 1));
		this.params.vDivLineColor = formatColor(getFV(atts["vdivlinecolor"], this.params.divLineColor));
		this.params.vDivLineThickness = getFN(atts["vdivlinethickness"], this.params.divLineThickness);
		this.params.vDivLineAlpha = getFN(atts["vdivlinealpha"], this.params.divLineAlpha);
		this.params.vDivLineIsDashed = toBoolean(getFN(atts["vdivlineisdashed"], this.params.divLineIsDashed));
		this.params.vDivLineDashLen = getFN(atts["vdivlinedashlen"], this.params.divLineDashLen);
		this.params.vDivLineDashGap = getFN(atts["vdivlinedashgap"], this.params.divLineDashGap);
		//Zero Plane properties
		this.params.zeroPlaneColor = formatColor(getFV(atts["zeroplanecolor"], this.params.divLineColor));
		this.params.zeroPlaneThickness = getFN(atts["zeroplanethickness"], 1);
		this.params.zeroPlaneAlpha = getFN(atts["zeroplanealpha"], this.params.divLineAlpha);
		//Alternating grid colors
		this.params.showAlternateHGridColor = toBoolean(getFN(atts["showalternatehgridcolor"], 1));
		this.params.alternateHGridColor = formatColor(getFV(atts["alternatehgridcolor"], this.colorM.get2DAltHGridColor()));
		this.params.alternateHGridAlpha = getFN(atts["alternatehgridalpha"], this.colorM.get2DAltHGridAlpha());
		//Shadow properties
		this.params.showShadow = toBoolean(getFN(atts["showshadow"], 1));
		//Anchor Properties
		this.params.drawAnchors = toBoolean(getFN(atts["drawanchors"], atts["showanchors"], 1));
		this.params.anchorRadius = getFN(atts["anchorradius"], 3);
		this.params.anchorBorderColor = atts["anchorbordercolor"];
		this.params.anchorBorderThickness = getFN(atts["anchorborderthickness"], 1);
		this.params.anchorBgColor = atts["anchorbgcolor"];
		this.params.anchorBgAlpha = getFN(atts["anchorbgalpha"], 100);
		//Minimum Distance between two anchors after which it starts rendering (to avoid clutter on chart)
		this.params.anchorMinRenderDistance = getFN(atts["anchorminrenderdistance"], 20);
		//Color for tool-bar buttons
		this.params.toolbarButtonColor = formatColor(getFV(atts["toolbarbuttoncolor"], this.colorM.get2DToolBarButtonColor()));
		this.params.toolbarButtonFontColor = formatColor(getFV(atts["toolbarbuttonfontcolor"], this.colorM.get2DToolBarButtonFontColor()));
		//Titles of toolbar buttons
		this.params.btnResetChartTitle = getFV(atts["btnresetcharttitle"], "Reset Chart");
		this.params.btnZoomOutTitle = getFV(atts["btnzoomouttitle"], "Zoom Out");
		this.params.btnSwitchToZoomModeTitle = getFV(atts["btnswitchtozoommodetitle"], "Switch to Zoom Mode");
		this.params.btnSwitchToPinModeTitle = getFV(atts["btnswitchtopinmodetitle"], "Switch to Pin Mode");
		//Tool-text for buttons
		this.params.showToolBarButtonTooltext = toBoolean(getFN(atts["showtoolbarbuttontooltext"], 1));
		this.params.btnResetChartTooltext = getFV(atts["btnresetcharttooltext"], "Show all data points");
		this.params.btnZoomOutTooltext = getFV(atts["btnzoomouttooltext"], "Zoom out to previous view");
		this.params.btnSwitchToZoomModeTooltext = getFV(atts["btnswitchtozoommodetooltext"], "Select a subset of data to zoom into it for detailed view");
		this.params.btnSwitchToPinModeTooltext = getFV(atts["btnswitchtopinmodetooltext"], "Pin a subset of data and drag the pinned region for comparing with other subsets");
		//Context menu items
		this.params.resetChartMenuItemLabel = getFV(atts["resetchartmenuitemlabel"], "Reset Chart");
		this.params.zoomOutMenuItemLabel = getFV(atts["zoomoutmenuitemlabel"], "Zoom Out Chart");
		this.params.zoomModeMenuItemLabel = getFV(atts["zoommodemenuitemlabel"], "Switch to Zoom Mode");
		this.params.pinModeMenuItemLabel = getFV(atts["pinmodemenuitemlabel"], "Switch to Pin Mode");
		//Zoom pane color
		this.params.zoomPaneBorderColor = formatColor(getFV(atts["zoompanebordercolor"], this.params.canvasBorderColor));
		this.params.zoomPaneBgColor = formatColor(getFV(atts["zoompanebgcolor"], this.params.canvasBorderColor));
		this.params.zoomPaneBgAlpha = getFN(atts["zoompanebgalpha"], 15);
		//Pin pane color
		this.params.pinPaneBorderColor = formatColor(getFV(atts["pinpanebordercolor"], this.params.canvasBorderColor));
		this.params.pinPaneBgColor = formatColor(getFV(atts["pinpanebgcolor"], this.params.canvasBorderColor));
		this.params.pinPaneBgAlpha = getFN(atts["pinpanebgalpha"], 15);
		//Tool tip bar color
		this.params.toolTipBarColor = formatColor(getFV(atts["tooltipbarcolor"], this.colorM.get2DToolTipBarColor()));
		//Whether to enable different cursors
		this.params.enableIconMouseCursors = toBoolean(getFN(atts["enableiconmousecursors"], 1));
		//Mouse cursor color
		this.params.mouseCursorColor = formatColor(getFV(atts["mousecursorcolor"], this.colorM.get2DMouseCursorColor()));
		//Over-ride from Super class to use local color manager
		//Tool Tip - Show/Hide, Background Color, Border Color, Separator Character
		this.params.toolTipBgColor = formatColor(getFV(atts["tooltipbgcolor"], atts["hovercapbgcolor"], atts["hovercapbg"], this.colorM.get2DToolTipBgColor()));
		this.params.toolTipBorderColor = formatColor(getFV(atts["tooltipbordercolor"], atts["hovercapbordercolor"], atts["hovercapborder"], this.colorM.get2DToolTipBorderColor()));
		//Font Properties
		this.params.baseFontColor = formatColor(getFV(atts["basefontcolor"], this.colorM.get2DBaseFontColor()));
		this.params.outCnvBaseFontColor = formatColor(getFV(atts["outcnvbasefontcolor"], this.params.baseFontColor));
		// ------------------------- NUMBER FORMATTING ---------------------------- //
		//Option whether the format the number (using Commas)
		this.params.formatNumber = toBoolean(getFN(atts["formatnumber"], 1));
		//Option to format number scale
		this.params.formatNumberScale = toBoolean(getFN(atts["formatnumberscale"], 1));
		//Number Scales
		this.params.defaultNumberScale = getFV(atts["defaultnumberscale"], "");
		this.params.numberScaleUnit = getFV(atts["numberscaleunit"], "K,M");
		this.params.numberScaleValue = getFV(atts["numberscalevalue"], "1000,1000");
		//Number prefix and suffix
		this.params.numberPrefix = getFV(atts["numberprefix"], "");
		this.params.numberSuffix = getFV(atts["numbersuffix"], "");
		//Decimal Separator Character
		this.params.decimalSeparator = getFV(atts["decimalseparator"], ".");
		//Thousand Separator Character
		this.params.thousandSeparator = getFV(atts["thousandseparator"], ",");
		//Decimal Precision (number of decimal places to be rounded to)
		this.params.decimals = getFV(atts["decimals"], atts["decimalprecision"]);
		//Force Decimal Padding
		this.params.forceDecimals = toBoolean(getFN(atts["forcedecimals"], 0));
		//y-Axis values decimals
		this.params.yAxisValueDecimals = getFV(atts["yaxisvaluedecimals"], atts["yaxisvaluesdecimals"], atts["divlinedecimalprecision"], atts["limitsdecimalprecision"]);
	}
	/**
	* Overrides attributes after common parsing module has parsed.
	* @param	graphElement	XML Node containing the <graph> element
	*							and it's attributes
	*/
	private function overrideAttributes(graphElement:XMLNode):Void {
		//Array to store the attributes
		var atts:Array = this.getAttributesArray(graphElement);
		//Font Properties
		this.params.baseFontColor = formatColor(getFV(atts["basefontcolor"], this.colorM.get2DBaseFontColor()));
		this.params.outCnvBaseFontColor = formatColor(getFV(atts["outcnvbasefontcolor"], this.params.baseFontColor));
	}
	/**
	* returnDataAsTrendObj method takes in functional parameters, and creates
	* an object to represent the trend line as a unified object.
	*	@param	startValue		Starting value of the trend line.
	*	@param	endValue		End value of the trend line (if different from start)
	*	@param	displayValue	Display value for the trend (if custom).
	*	@param	color			Color of the trend line
	*	@param	thickness		Thickness (in pixels) of line
	*	@param	alpha			Alpha of the line
	*	@param	isTrendZone		Flag to control whether to show trend as a line or block(zone)
	*	@param	showOnTop		Whether to show trend over data plot or under it.
	*	@param	isDashed		Whether the line would appear dashed.
	*	@param	dashLen			Length of dash (if isDashed selected)
	*	@param	dashGap			Gap of dash (if isDashed selected)
	*	@param	valueOnRight	Whether to put the trend value on right side of canvas
	*	@return				An object encapsulating these values.
	*/
	private function returnDataAsTrendObj(startValue:Number, endValue:Number, displayValue:String, color:String, thickness:Number, alpha:Number, isTrendZone:Boolean, showOnTop:Boolean, isDashed:Boolean, dashLen:Number, dashGap:Number, valueOnRight:Boolean):Object {
		//Create an object that will be returned.
		var rtnObj:Object = new Object();
		//Store parameters as object properties
		rtnObj.startValue = startValue;
		rtnObj.endValue = endValue;
		rtnObj.displayValue = displayValue;
		rtnObj.color = color;
		rtnObj.thickness = thickness;
		rtnObj.alpha = alpha;
		rtnObj.isTrendZone = isTrendZone;
		rtnObj.showOnTop = showOnTop;
		rtnObj.isDashed = isDashed;
		rtnObj.dashLen = dashLen;
		rtnObj.dashGap = dashGap;
		rtnObj.valueOnRight = valueOnRight;
		//Flag whether trend line is proper
		rtnObj.isValid = true;
		//Holders for dimenstions
		rtnObj.y = 0;
		rtnObj.toY = 0;
		//Text box y position
		rtnObj.tbY = 0;
		//Return
		return rtnObj;
	}
	
	/**
	* returnDataAsVTrendObj method takes in functional parameters, and creates
	* an object to represent the vertical trend line as a unified object.
	*	@param	startIndex			Starting index of the trend line.
	*	@param	endIndex			End index of the trend line (if different from start)
	*	@param	displayValue		Display value for the trend (if custom).
	* 	@param	displayAlways		Whether to display this trend line permanently on chart?
	*	@param	displayWhenCount	Or, if the trend line is not to be displayed permanently, at
	*								what count of subset items (endIndex-startIndex) would it show up.			
	*	@param	color				Color of the trend line
	*	@param	thickness			Thickness (in pixels) of line
	*	@param	alpha				Alpha of the line
	*	@param	showOnTop			Whether to show trend over data plot or under it.
	*	@param	isDashed			Whether the line would appear dashed.
	*	@param	dashLen				Length of dash (if isDashed selected)
	*	@param	dashGap				Gap of dash (if isDashed selected)
	*	@param	valueOnTop			Whether to put the trend value on top of canvas
	*	@return						An object encapsulating these values.
	*/
	private function returnDataAsVTrendObj(startIndex:Number, endIndex:Number, displayValue:String, displayAlways:Boolean, displayWhenCount:Number, color:String, thickness:Number, alpha:Number, showOnTop:Boolean, isDashed:Boolean, dashLen:Number, dashGap:Number, valueOnTop:Boolean):Object {
		//Create an object that will be returned.
		var rtnObj:Object = new Object();
		//Store parameters as object properties
		rtnObj.startIndex = startIndex;
		rtnObj.endIndex = endIndex;
		rtnObj.displayValue = displayValue;
		rtnObj.displayAlways = displayAlways;
		rtnObj.displayWhenCount = displayWhenCount;
		rtnObj.color = color;
		rtnObj.thickness = thickness;
		rtnObj.alpha = alpha;
		rtnObj.isTrendZone = false;
		rtnObj.showOnTop = showOnTop;
		rtnObj.isDashed = isDashed;
		rtnObj.dashLen = dashLen;
		rtnObj.dashGap = dashGap;
		rtnObj.valueOnTop = valueOnTop;
		//Flag whether trend line is proper
		rtnObj.isValid = true;
		//Holders for dimenstions
		rtnObj.y = 0;
		rtnObj.toY = 0;
		//Text box y position
		rtnObj.tbY = 0;
		//Return
		return rtnObj;
	}
	/**
	* parseVTrendLineXML method parses the XML node containing vertical trend line nodes
	* and then stores it in local objects.
	*	@param		arrTrendLineNodes		Array containing vertical Trend LINE nodes.
	*	@return								Nothing.
	*/
	private function parseVTrendLineXML(arrTrendLineNodes:Array):Void {
		//Create container
		vTrendLines = new Array();
		//Define variables for local use
		var startIndex:Number, endIndex:Number, displayValue:String;
		var color:String, thickness:Number, alpha:Number;
		var showOnTop:Boolean, isDashed:Boolean;
		var dashLen:Number, dashGap:Number, valueOnTop:Boolean;
		var displayAlways:Boolean, displayWhenCount:Number;
		//Loop variable
		var i:Number;
		//Iterate through all nodes in array
		for (i=0; i<=arrTrendLineNodes.length; i++) {
			//Check if LINE node
			if (arrTrendLineNodes[i].nodeName.toUpperCase() == "LINE") {
				//Update count
				numVTrendLines++;
				//Store the node reference
				var lineNode:XMLNode = arrTrendLineNodes[i];
				//Get attributes array
				var lineAttr:Array = this.getAttributesArray(lineNode);
				//Extract and store attributes
				startIndex = getFV(lineAttr["startindex"], lineAttr["index"], lineAttr["value"]);
				endIndex = getFV(lineAttr["endindex"], startIndex);
				displayValue = lineAttr["displayvalue"];
				color = String(formatColor(getFV(lineAttr["color"], "333333")));
				thickness = getFN(lineAttr["thickness"], 1);
				displayAlways = toBoolean(Number(getFV(lineAttr["displayalways"], 1)));
				displayWhenCount = Number(getFV(lineAttr["displaywhencount"], -1));
				alpha = getFN(lineAttr["alpha"], 99);
				showOnTop = toBoolean(getFN(lineAttr["showontop"], 0));
				isDashed = toBoolean(getFN(lineAttr["dashed"], 0));
				dashLen = getFN(lineAttr["dashlen"], 5);
				dashGap = getFN(lineAttr["dashgap"], 2);
				valueOnTop = toBoolean(getFN(lineAttr["valueontop"], 1));
				//If the values are not numbers, ignore
				if (startIndex==undefined || endIndex==undefined || isNaN(startIndex) || isNaN(endIndex)){
					numVTrendLines--;
				}else{
					//Create trend line object
					this.vTrendLines[numVTrendLines] = returnDataAsVTrendObj(Number(startIndex), Number(endIndex), displayValue, displayAlways, displayWhenCount, color, thickness, alpha, showOnTop, isDashed, dashLen, dashGap, valueOnTop);
					//Update numTrendLinesBelow
					numVTrendLinesBelow = (showOnTop == false) ? (++numVTrendLinesBelow) : numVTrendLinesBelow;
				}
			}
		}
	}
	/**
	* parseTrendLineXML method parses the XML node containing trend line nodes
	* and then stores it in local objects.
	*	@param		arrTrendLineNodes		Array containing Trend LINE nodes.
	*	@return							Nothing.
	*/
	private function parseTrendLineXML(arrTrendLineNodes:Array):Void {
		//Create container
		trendLines = new Array();
		//Define variables for local use
		var startValue:Number, endValue:Number, displayValue:String;
		var color:String, thickness:Number, alpha:Number;
		var isTrendZone:Boolean, showOnTop:Boolean, isDashed:Boolean;
		var dashLen:Number, dashGap:Number, valueOnRight:Boolean;
		//Loop variable
		var i:Number;
		//Iterate through all nodes in array
		for (i=0; i<=arrTrendLineNodes.length; i++) {
			//Check if LINE node
			if (arrTrendLineNodes[i].nodeName.toUpperCase() == "LINE") {
				//Update count
				numTrendLines++;
				//Store the node reference
				var lineNode:XMLNode = arrTrendLineNodes[i];
				//Get attributes array
				var lineAttr:Array = this.getAttributesArray(lineNode);
				//Extract and store attributes
				startValue = getFN(this.getSetValue(lineAttr["startvalue"]), this.getSetValue(lineAttr["value"]));
				endValue = getFN(this.getSetValue(lineAttr["endvalue"]), startValue);
				displayValue = lineAttr["displayvalue"];
				color = String(formatColor(getFV(lineAttr["color"], "333333")));
				thickness = getFN(lineAttr["thickness"], 1);
				isTrendZone = toBoolean(Number(getFV(lineAttr["istrendzone"], 0)));
				alpha = getFN(lineAttr["alpha"], (isTrendZone == true) ? 40 : 99);
				showOnTop = toBoolean(getFN(lineAttr["showontop"], 0));
				isDashed = toBoolean(getFN(lineAttr["dashed"], 0));
				dashLen = getFN(lineAttr["dashlen"], 5);
				dashGap = getFN(lineAttr["dashgap"], 2);
				valueOnRight = toBoolean(getFN(lineAttr["valueonright"], 0));
				//Create trend line object
				this.trendLines[numTrendLines] = returnDataAsTrendObj(startValue, endValue, displayValue, color, thickness, alpha, isTrendZone, showOnTop, isDashed, dashLen, dashGap, valueOnRight);
				//Update numTrendLinesBelow
				numTrendLinesBelow = (showOnTop == false) ? (++numTrendLinesBelow) : numTrendLinesBelow;
			}
		}
	}
	/**
	* validateTrendLines method helps us validate the different trend line
	* points entered by user in XML. Some trend points may fall out of
	* chart range (yMin,yMax) and we need to invalidate them. Also, we
	* need to check if displayValue has been specified. Else, we specify
	* formatted number as displayValue.
	*	@return		Nothing
	*/
	private function validateTrendLines() {
		//Sequentially do the following.
		//- Check if start value and end value are numbers. If not,
		//  invalidate them
		//- Check range of each trend line against chart yMin,yMax and
		//  devalidate wrong ones.
		//- Resolve displayValue conflict.
		//- Calculate and store y position of start and end position.
		//Loop variable
		var i:Number;
		for (i=1; i<=this.numTrendLines; i++) {
			//If the trend line start/end value is NaN or out of range
			if (isNaN(this.trendLines[i].startValue) || (this.trendLines[i].startValue<ax.getMin()) || (this.trendLines[i].startValue>ax.getMax()) || isNaN(this.trendLines[i].endValue) || (this.trendLines[i].endValue<ax.getMin()) || (this.trendLines[i].endValue>ax.getMax())) {
				//Invalidate it
				this.trendLines[i].isValid = false;
			} else {
				//We resolve displayValue conflict
				this.trendLines[i].displayValue = getFV(this.trendLines[i].displayValue, formatInterval(this.trendLines[i].startValue));
			}
		}
	}
	/**
	* Sets the default values for all the data items like:
	* - Formatted values
	* - Display values
	* - Tool-text values
	*/
	private function setDataDefaults():Void {
		var i:Number, j:Number;
		for (i=1; i<=numDS; i++) {
			for (j=1; j<=numItems; j++) {
				//Get Formatted value.
				var fmtVal:String;
				var tText:String;
				if (isNaN(data[i][j])) {
					fmtVal = "";
				} else {
					fmtVal = formatNumber(data[i][j], this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
				}
				//Stored Formatted value
				formattedVal[i][j] = fmtVal;
				//Tool-text value
				toolText[i][j] = getFV(toolText[i][j], fmtVal);
				//If an explicit display value has been specified
				if (displayValue[i][j]!=undefined && displayValue[i][j]!=""){
					//Mark it in dataset.
					this.dataset[i].displayValueSpecified = true;
					displayValueGiven[i][j] = true;
				}else{
					displayValueGiven[i][j] = false;
				}
				//Display value
				displayValue[i][j] = getFV(displayValue[i][j], fmtVal);
				/**
				 * If the series name + category label + formatted value is to be set as 
				 * the tool tip label, the following code should be used:
				 * //Tool text. Preferential Order - Defined Tool Text (No concatenation) > SeriesName + Cat Name + Value
				 * 	if (toolText[i][j] == undefined || toolText[i][j] == "")
				 * 	{
				 * 		tText = (this.params.seriesNameInToolTip && this.dataset [i].seriesName != "") ? (this.dataset [i].seriesName + this.params.toolTipSepChar) : "";
				 *	 	tText = tText + ((labels[j] != "") ? (labels[j] + this.params.toolTipSepChar) : "");
				 * 		tText = tText + fmtVal;
				 * 		toolText[i][j] = toolText;
				 * 	}
				*/
			}
		}
	}
	/**
	 * Sets the subset indexes. It's based on these indexes that the 
	 * chart subset is drawn.
	 * @param	startIndex		Starting index of subset
	 * @param	endIndex		Ending index of subset
	*/
	private function setSubsetIndexes(startIndex:Number, endIndex:Number):Void {
		//Make sure they're integers
		startIndex = int(startIndex);
		endIndex = int(endIndex);
		//If startIndex<1, set it to 1
		startIndex = Math.max(1, startIndex);
		//If endIndex>numItems, set to numItems
		endIndex = Math.min(endIndex, numItems);
		//Store them
		subsetStartIndex = startIndex;
		subsetEndIndex = endIndex;
	}
	/**
	 * Resets the subset indexes and redraws the same.
	*/
	function resetIndexes(startIndex:Number, endIndex:Number):Void {
		//If the new indexes are different from old, redraw
		if ((endIndex-startIndex)>0 && (subsetStartIndex != startIndex || subsetEndIndex != endIndex)) {
			//Set the subset index
			setSubsetIndexes(startIndex, endIndex);
			//Calculate the skip index and size of scroll bar
			//based on the number of items and visible area
			calculateSubsetSteps();
			//Create the sub-set of data to be displayed
			populateSubset();
			//Calculate sub-set co-ordinates
			calculateSubsetCoords();
			//Draw the display subset
			drawSubset();
			//Set new values for scroll bar		
			updateScrollProps();
		}
	}
	/**
	* Calculates the min and max value in the subset
	*/
	private function calculateSubsetLimits():Void {
		//Clear existing values, for calculation during dynamic axis
		delete subsetMin;
		delete subsetMax;
		var i:Number, j:Number;
		//Figure the start and end index.
		var startIndex:Number = (this.params.dynamicAxis) ? subsetStartIndex : 1;
		var endIndex:Number = (this.params.dynamicAxis) ? subsetEndIndex : numItems;
		//Iterate through all the points
		for (i=1; i<=numDS; i++) {
			//NOTE: We're not skipping steps within the subset, as 
			//we want the axis to reflect any spikes/drops in data in
			//this subset, which might have been missed by step.
			for (j=startIndex; j<=endIndex; j++) {
				//If data is defined and a number.
				if (!isNaN(data[i][j])) {
					//Calculate max and min
					subsetMax = (subsetMax == undefined) ? (data[i][j]) : (Math.max(subsetMax, data[i][j]));
					subsetMin = (subsetMin == undefined) ? (data[i][j]) : (Math.min(subsetMin, data[i][j]));
				}
			}
		}
	}
	/**
	 * Calculates the axis based on the calculated subset min and max values.
	*/
	function calculateAxis():Void {
		//If axis class is already instantiated, dispose it
		if (ax != undefined) {
			ax.dispose();
		}
		//Create an instance of axis class to feed in values and get                  
		ax = new Axis();
		//Set Axis Type based on user defined parameter
		ax.setType((this.params.axis == "LINEAR") ? Axis.TYPE_LINEAR : Axis.TYPE_LOG);
		//Set the log base, if log axis
		ax.setLogBase(this.params.logBase);
		//Set the data limits
		ax.setDataLimits(subsetMin, subsetMax);
		//Set linear axis parameters: Allow negative max, allow positive min
		ax.setLinearAxisParams(true, this.params.setAdaptiveYMin);
		//Set the maximum number of intervals (for linear axis)
		ax.setMaxIntervals(this.params.numDivLines);
		//Number of minor intervals for log axis
		if (this.params.numMinorLogDivLines != -1) {
			ax.setNumMinorIntervalsLog(this.params.numMinorLogDivLines);
		}
		//Set interval hints (for linear axis)               
		if (this.params.divIntervalHints != "") {
			ax.setIntervalHints(this.params.divIntervalHints);
		}
		//Set forced limits for axis               
		ax.setForcedLimits(this.params.yAxisMinValue, this.params.yAxisMaxValue);
		//Finally, calculate axis values
		ax.calculate();
	}
	/**
	 * s method sets up the color manager for the chart.
	  *	@param	paletteId	Palette Id for the chart.
	 *	@param	themeColor	Color code if the chart uses single color theme.
	*/
	private function setupColorManager(paletteId:Number, themeColor:String):Void {
		this.colorM = new ZoomChartColorManager(paletteId, themeColor);
	}
	/**
	* setStyleDefaults method sets the default values for styles or
	* extracts information from the attributes and stores them into
	* style objects.
	*/
	private function setStyleDefaults():Void {
		//Default font object for Caption
		//-----------------------------------------------------------------//
		var captionFont = new StyleObject();
		captionFont.name = "_SdCaptionFont";
		captionFont.align = "center";
		captionFont.valign = "bottom";
		captionFont.bold = "1";
		captionFont.font = this.params.outCnvBaseFont;
		captionFont.size = this.params.outCnvBaseFontSize+3;
		captionFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.CAPTION, captionFont, this.styleM.TYPE.FONT, null);
		delete captionFont;
		//-----------------------------------------------------------------//
		//Default font object for SubCaption
		//-----------------------------------------------------------------//
		var subCaptionFont = new StyleObject();
		subCaptionFont.name = "_SdSubCaptionFont";
		subCaptionFont.align = "center";
		subCaptionFont.valign = "bottom";
		subCaptionFont.bold = "1";
		subCaptionFont.font = this.params.outCnvBaseFont;
		subCaptionFont.size = this.params.outCnvBaseFontSize+1;
		subCaptionFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.SUBCAPTION, subCaptionFont, this.styleM.TYPE.FONT, null);
		delete subCaptionFont;
		//-----------------------------------------------------------------//
		//Default font object for YAxisName
		//-----------------------------------------------------------------//
		var yAxisNameFont = new StyleObject();
		yAxisNameFont.name = "_SdYAxisNameFont";
		yAxisNameFont.align = "center";
		yAxisNameFont.valign = "middle";
		yAxisNameFont.bold = "1";
		yAxisNameFont.font = this.params.outCnvBaseFont;
		yAxisNameFont.size = this.params.outCnvBaseFontSize;
		yAxisNameFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.YAXISNAME, yAxisNameFont, this.styleM.TYPE.FONT, null);
		delete yAxisNameFont;
		//-----------------------------------------------------------------//
		//Default font object for XAxisName
		//-----------------------------------------------------------------//
		var xAxisNameFont = new StyleObject();
		xAxisNameFont.name = "_SdXAxisNameFont";
		xAxisNameFont.align = "center";
		xAxisNameFont.valign = "middle";
		xAxisNameFont.bold = "1";
		xAxisNameFont.font = this.params.outCnvBaseFont;
		xAxisNameFont.size = this.params.outCnvBaseFontSize;
		xAxisNameFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.XAXISNAME, xAxisNameFont, this.styleM.TYPE.FONT, null);
		delete xAxisNameFont;
		//-----------------------------------------------------------------//
		//Default font object for trend lines
		//-----------------------------------------------------------------//
		var trendFont = new StyleObject();
		trendFont.name = "_SdTrendFont";
		trendFont.font = this.params.outCnvBaseFont;
		trendFont.size = this.params.outCnvBaseFontSize;
		trendFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.TRENDVALUES, trendFont, this.styleM.TYPE.FONT, null);
		delete trendFont;
		//-----------------------------------------------------------------//
		//Default font object for vertical trend lines
		//-----------------------------------------------------------------//
		var vTrendFont = new StyleObject();
		vTrendFont.name = "_SdVTrendFont";
		vTrendFont.font = this.params.baseFont;
		vTrendFont.size = this.params.baseFontSize;
		vTrendFont.color = this.params.baseFontColor;
		vTrendFont.align = "center";
		//Over-ride
		this.styleM.overrideStyle(this.objects.VTRENDVALUES, vTrendFont, this.styleM.TYPE.FONT, null);
		delete vTrendFont;
		//-----------------------------------------------------------------//
		//Default font object for yAxisValues
		//-----------------------------------------------------------------//
		var yAxisValuesFont = new StyleObject();
		yAxisValuesFont.name = "_SdYAxisValuesFont";
		yAxisValuesFont.align = "right";
		yAxisValuesFont.valign = "middle";
		yAxisValuesFont.font = this.params.outCnvBaseFont;
		yAxisValuesFont.size = this.params.outCnvBaseFontSize;
		yAxisValuesFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.YAXISVALUES, yAxisValuesFont, this.styleM.TYPE.FONT, null);
		delete yAxisValuesFont;
		//-----------------------------------------------------------------//
		//Default font object for DataLabels
		//-----------------------------------------------------------------//
		var dataLabelsFont = new StyleObject();
		dataLabelsFont.name = "_SdDataLabelsFont";
		dataLabelsFont.align = "center";
		dataLabelsFont.valign = "bottom";
		dataLabelsFont.font = this.params.catFont;
		dataLabelsFont.size = this.params.catFontSize;
		dataLabelsFont.color = this.params.catFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.DATALABELS, dataLabelsFont, this.styleM.TYPE.FONT, null);
		delete dataLabelsFont;
		//-----------------------------------------------------------------//
		//Default font object for Overlay DataLabels
		//-----------------------------------------------------------------//
		var overlayDataLabelsFont = new StyleObject();
		overlayDataLabelsFont.name = "_SdOverlayDataLabelsFont";
		overlayDataLabelsFont.align = "center";
		overlayDataLabelsFont.valign = "bottom";
		overlayDataLabelsFont.bold = "1";
		overlayDataLabelsFont.font = this.params.catFont;
		overlayDataLabelsFont.size = this.params.catFontSize;
		overlayDataLabelsFont.color = this.params.catFontColor;
		overlayDataLabelsFont.bgcolor = this.params.canvasBgColor;
		overlayDataLabelsFont.bordercolor = this.params.catFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.OVERLAYLABEL, overlayDataLabelsFont, this.styleM.TYPE.FONT, null);
		delete overlayDataLabelsFont;
		//-----------------------------------------------------------------//
		//Default font object for Legend
		//-----------------------------------------------------------------//
		var legendFont = new StyleObject();
		legendFont.name = "_SdLegendFont";
		legendFont.font = this.params.outCnvBaseFont;
		legendFont.size = this.params.outCnvBaseFontSize;
		legendFont.color = this.params.outCnvBaseFontColor;
		legendFont.ishtml = 1;
		legendFont.leftmargin = 3;
		//Over-ride
		this.styleM.overrideStyle(this.objects.LEGEND, legendFont, this.styleM.TYPE.FONT, null);
		delete legendFont;
		//-----------------------------------------------------------------//
		//Default font object for DataValues
		//-----------------------------------------------------------------//
		var dataValuesFont = new StyleObject();
		dataValuesFont.name = "_SdDataValuesFont";
		dataValuesFont.align = "center";
		dataValuesFont.valign = "middle";
		dataValuesFont.font = this.params.baseFont;
		dataValuesFont.size = this.params.baseFontSize;
		dataValuesFont.color = this.params.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.DATAVALUES, dataValuesFont, this.styleM.TYPE.FONT, null);
		delete dataValuesFont;
		//-----------------------------------------------------------------//
		//Default font object for ToolTip
		//-----------------------------------------------------------------//
		var toolTipFont = new StyleObject();
		toolTipFont.name = "_SdToolTipFont";
		toolTipFont.font = this.params.baseFont;
		toolTipFont.size = this.params.baseFontSize;
		toolTipFont.color = this.params.baseFontColor;
		toolTipFont.bgcolor = this.params.toolTipBgColor;
		toolTipFont.bordercolor = this.params.toolTipBorderColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.TOOLTIP, toolTipFont, this.styleM.TYPE.FONT, null);
		delete toolTipFont;
		//-----------------------------------------------------------------//
		//Default font object for Tool-bar enabled button
		//-----------------------------------------------------------------//
		var enabledButtonFont = new StyleObject();
		enabledButtonFont.name = "_SdEnabledButtonFont";
		enabledButtonFont.align = "center";
		enabledButtonFont.valign = "top";
		enabledButtonFont.font = this.params.baseFont;
		enabledButtonFont.size = this.params.baseFontSize;
		enabledButtonFont.color = this.params.toolbarButtonFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.ENABLEDBUTTON, enabledButtonFont, this.styleM.TYPE.FONT, null);
		delete enabledButtonFont;
		//-----------------------------------------------------------------//
		//Default font object for Tool-bar disabled button
		//-----------------------------------------------------------------//
		var disabledButtonFont = new StyleObject();
		disabledButtonFont.name = "_SdDisabledButtonFont";
		disabledButtonFont.align = "center";
		disabledButtonFont.valign = "top";
		disabledButtonFont.font = this.params.baseFont;
		disabledButtonFont.size = this.params.baseFontSize;
		disabledButtonFont.color = ColorExt.getLightColor(this.params.toolbarButtonFontColor, 0.4).toString(16);
		//Over-ride
		this.styleM.overrideStyle(this.objects.DISABLEDBUTTON, disabledButtonFont, this.styleM.TYPE.FONT, null);
		delete disabledButtonFont;
		//-----------------------------------------------------------------//
		//Default font object for V-line labels
		//-----------------------------------------------------------------//
		var vLineLabelsFont = new StyleObject();
		vLineLabelsFont.name = "_SdDataVLineLabelsFont";
		vLineLabelsFont.align = "center";
		vLineLabelsFont.valign = "bottom";
		vLineLabelsFont.font = this.params.baseFont;
		vLineLabelsFont.size = this.params.baseFontSize;
		vLineLabelsFont.color = this.params.baseFontColor;
		vLineLabelsFont.bgcolor = this.params.canvasBgColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.VLINELABELS, vLineLabelsFont, this.styleM.TYPE.FONT, null);
		delete vLineLabelsFont;
		//-----------------------------------------------------------------//
		//Default font object for Tool-bar disabled button
		//-----------------------------------------------------------------//
		var dataToolTipFont = new StyleObject();
		dataToolTipFont.name = "_SdDataToolTipFont";
		dataToolTipFont.align = "left";
		dataToolTipFont.valign = "middle";
		dataToolTipFont.font = this.params.baseFont;
		dataToolTipFont.size = this.params.baseFontSize;
		dataToolTipFont.bgcolor = this.params.canvasBgColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.DATATOOLTIP, dataToolTipFont, this.styleM.TYPE.FONT, null);
		delete dataToolTipFont;
		//Store globally
		toolTipStyle = this.styleM.getTextStyle(this.objects.DATATOOLTIP);
		//-----------------------------------------------------------------//
		//Default Effect (Shadow) object for Canvas
		//-----------------------------------------------------------------//
		if (this.params.showShadow) {
			var canvasShadow = new StyleObject();
			canvasShadow.name = "_SdCanvasShadow";
			canvasShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle(this.objects.CANVAS, canvasShadow, this.styleM.TYPE.SHADOW, null);
			delete canvasShadow;
		}
		//-----------------------------------------------------------------//              
		//Default Effect (Shadow) object for DataPlot
		//-----------------------------------------------------------------//
		if (this.params.showShadow) {
			var dataPlotShadow = new StyleObject();
			dataPlotShadow.name = "_SdDataPlotShadow";
			dataPlotShadow.blurx = 2;
			dataPlotShadow.blury = 2;
			dataPlotShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle(this.objects.DATAPLOT, dataPlotShadow, this.styleM.TYPE.SHADOW, null);
			delete dataPlotShadow;
		}
		//-----------------------------------------------------------------//               
		//Default Effect (Shadow) object for Scroll Bar
		//-----------------------------------------------------------------//
		if (this.params.showShadow) {
			var scrollBarShadow = new StyleObject();
			scrollBarShadow.name = "_SdScrollBarShadow";
			scrollBarShadow.distance = 2;
			scrollBarShadow.angle = 15;
			//Over-ride
			this.styleM.overrideStyle(this.objects.SCROLLBAR, scrollBarShadow, this.styleM.TYPE.SHADOW, null);
			delete scrollBarShadow;
		}
		//-----------------------------------------------------------------//               
		//Default Effect (Shadow) object for Legend
		//-----------------------------------------------------------------//
		if (this.params.legendShadow) {
			var legendShadow = new StyleObject();
			legendShadow.name = "_SdLegendShadow";
			legendShadow.distance = 2;
			legendShadow.alpha = 90;
			legendShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle(this.objects.LEGEND, legendShadow, this.styleM.TYPE.SHADOW, null);
			delete legendShadow;
		}
	}
	/**
	 * Calculates the co-ordinates required to plot base objects. Base objects
	 * include background, canvas, headers, legend, scroll bar, tool bar etc.
	 * Basically, items which do not change when subset of data is zoomed into.
	 * @param	isRedraw	Whether calculation has been called during resize phase.
	*/
	private function calculateBaseCoords(isRedraw:Boolean):Void {
		//Loop variable
		var i:Number;
		var j:Number;
		//We now need to calculate the available Width on the canvas.
		// ------------ Begin: Calculate Canvas Co-ordinates -------------//
		//Available width = total Chart width minus the list below
		// - Left and Right Margin
		// - yAxisName (if to be shown)
		// - yAxisValues
		// - Trend line display values (both left side and right side).
		// - Legend (If to be shown at right)
		var canvasWidth:Number = this.width-(this.params.chartLeftMargin+this.params.chartRightMargin);
		//Set canvas startX
		var canvasStartX:Number = this.params.chartLeftMargin;
		//Now, if y-axis name is to be shown, simulate it and get the width
		if (this.params.yAxisName != "") {
			//Get style object
			var yAxisNameStyle:Object = this.styleM.getTextStyle(this.objects.YAXISNAME);
			if (this.params.rotateYAxisName) {
				//Create text field to get width
				var yAxisNameObj:Object = createText(true, this.params.yAxisName, this.tfTestMC, 1, testTFX, testTFY, 90, yAxisNameStyle, true, this.height-(this.params.chartTopMargin+this.params.chartBottomMargin), canvasWidth/2);
				//Accomodate width and padding
				canvasStartX = canvasStartX+yAxisNameObj.width+this.params.yAxisNamePadding;
				canvasWidth = canvasWidth-yAxisNameObj.width-this.params.yAxisNamePadding;
				//Create element for yAxisName - to store width/height
				this.elements.yAxisName = returnDataAsElement(0, 0, yAxisNameObj.width, yAxisNameObj.height);
			} else {
				//If the y-axis name is not to be rotated
				//Calculate the width of the text if in full horizontal mode
				//Create text field to get width
				var yAxisNameObj:Object = createText(true, this.params.yAxisName, this.tfTestMC, 1, testTFX, testTFY, 0, yAxisNameStyle, false, 0, 0);
				//Get a value for this.params.yAxisNameWidth
				this.params.yAxisNameWidth = Number(getFV(this.params.yAxisNameWidth, yAxisNameObj.width));
				//Get the lesser of the width (to avoid un-necessary space)
				this.params.yAxisNameWidth = Math.min(this.params.yAxisNameWidth, yAxisNameObj.width);
				//Accomodate width and padding
				canvasStartX = canvasStartX+this.params.yAxisNameWidth+this.params.yAxisNamePadding;
				canvasWidth = canvasWidth-this.params.yAxisNameWidth-this.params.yAxisNamePadding;
				//Create element for yAxisName - to store width/height
				this.elements.yAxisName = returnDataAsElement(0, 0, this.params.yAxisNameWidth, yAxisNameObj.height);
			}
			delete yAxisNameStyle;
			delete yAxisNameObj;
		}
		//----- Y-axis values ------//              
		//Accomodate width for y-axis values. 
		//Get the number of intervals closed to maximum specified
		var intervals:Array = ax.getIntervals(this.params.numDivLines);
		//Container to store maximum widtg
		var yAxisValMaxWidth:Number = 0;
		//Also store the height required to render the text field
		var yAxisValMaxHeight:Number = 0;
		//If y-axis values are to be shown
		if (this.params.showYAxisValues) {
			var divLineObj:Object;
			var divStyle:Object = this.styleM.getTextStyle(this.objects.YAXISVALUES);
			//Iterate through all the div line values
			for (i=1; i<intervals.length; i++) {
				//If it's a major interval, only then the value has to be shown
				if (intervals[i].isMajor) {
					//Get the width of the text
					divLineObj = createText(true, formatInterval(intervals[i].position), this.tfTestMC, 1, testTFX, testTFY, 0, divStyle, false, 0, 0);
					//Accomodate
					yAxisValMaxWidth = (divLineObj.width>yAxisValMaxWidth) ? (divLineObj.width) : (yAxisValMaxWidth);
					yAxisValMaxHeight = (divLineObj.height>yAxisValMaxHeight) ? (divLineObj.height) : (yAxisValMaxHeight);
				}
			}
			delete divLineObj;
		}
		//Globally, store height required per div interval value line              
		this.config.yAxisValueHeight = yAxisValMaxHeight;
		//----- Trend lines: Left Side  -------//
		//Also iterate through all trend lines whose values are to be shown on
		//left side of the canvas.
		//Get style object
		var trendStyle:Object = this.styleM.getTextStyle(this.objects.TRENDVALUES);
		var trendObj:Object;
		for (i=1; i<=this.numTrendLines; i++) {
			if (this.trendLines[i].isValid == true && this.trendLines[i].valueOnRight == false) {
				//If it's a valid trend line and value is to be shown on left
				//Get the width of the text
				trendObj = createText(true, this.trendLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				yAxisValMaxWidth = (trendObj.width>yAxisValMaxWidth) ? (trendObj.width) : (yAxisValMaxWidth);
			}
		}
		//Accomodate for y-axis/left-trend line values text width
		if (yAxisValMaxWidth>0) {
			canvasStartX = canvasStartX+yAxisValMaxWidth+this.params.yAxisValuesPadding;
			canvasWidth = canvasWidth-yAxisValMaxWidth-this.params.yAxisValuesPadding;
		}
		//              
		var trendRightWidth:Number = 0;
		//------ Trend lines: Right size --------//
		//Now, also check for trend line values that fall on right
		for (i=1; i<=this.numTrendLines; i++) {
			if (this.trendLines[i].isValid == true && this.trendLines[i].valueOnRight == true) {
				//If it's a valid trend line and value is to be shown on right
				//Get the width of the text
				trendObj = createText(true, this.trendLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				trendRightWidth = (trendObj.width>trendRightWidth) ? (trendObj.width) : (trendRightWidth);
			}
		}
		delete trendObj;
		//Accomodate trend right text width
		if (trendRightWidth>0) {
			canvasWidth = canvasWidth-trendRightWidth-this.params.yAxisValuesPadding;
		}
		//Round them off finally to avoid distorted pixels              
		canvasStartX = int(canvasStartX);
		canvasWidth = int(canvasWidth);
		//We finally have canvas Width and canvas Start X
		//-----------------------------------------------------------------------------------//
		//Now, we need to calculate the available Height on the canvas.
		//Available height = total Chart height minus the list below
		// - Chart Top and Bottom Margins
		// - Space for Caption, Sub Caption and caption padding
		// - Space for tool-bar buttons
		// - Height of scroll bar
		// - Height of data labels
		// - xAxisName
		// - Legend (If to be shown at bottom position)
		//Initialize canvasHeight to total height minus margins
		var canvasHeight:Number = this.height-(this.params.chartTopMargin+this.params.chartBottomMargin);
		//Set canvasStartY
		var canvasStartY:Number = this.params.chartTopMargin;
		//------ Headers ------//
		//Now, if we've to show caption
		if (this.params.caption != "") {
			//Create text field to get height
			var captionObj:Object = createText(true, this.params.caption, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.CAPTION), true, canvasWidth, canvasHeight/4);
			//Store the height
			canvasStartY = canvasStartY+captionObj.height;
			canvasHeight = canvasHeight-captionObj.height;
			//Create element for caption - to store width & height
			this.elements.caption = returnDataAsElement(0, 0, captionObj.width, captionObj.height);
			delete captionObj;
		}
		//Now, if we've to show sub-caption              
		if (this.params.subCaption != "") {
			//Create text field to get height
			var subCaptionObj:Object = createText(true, this.params.subCaption, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.SUBCAPTION), true, canvasWidth, canvasHeight/4);
			//Store the height
			canvasStartY = canvasStartY+subCaptionObj.height;
			canvasHeight = canvasHeight-subCaptionObj.height;
			//Create element for sub caption - to store height
			this.elements.subCaption = returnDataAsElement(0, 0, subCaptionObj.width, subCaptionObj.height);
			delete subCaptionObj;
		}
		//Now, if either caption or sub-caption was shown, we also need to adjust caption padding              
		if (this.params.caption != "" || this.params.subCaption != "") {
			//Account for padding
			canvasStartY = canvasStartY+this.params.captionPadding;
			canvasHeight = canvasHeight-this.params.captionPadding;
		}
		//----- Tool bar buttons ------//             
		if (this.params.drawToolbarButtons) {
			//Get height required for 1 line of tool bar text, and add padding
			var toolbarBtnTextHeight:Number = createText(false, this.params.btnResetChartTitle+"|"+this.params.btnZoomOutTitle+"|"+this.params.btnSwitchToZoomModeTitle, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.ENABLEDBUTTON), false, 0, 0).height;
			//Store it
			this.config.toolbarBtnTextHeight = toolbarBtnTextHeight;
			this.config.toolbarBtnHeight = toolbarBtnTextHeight+this.params.toolBarBtnTextVMargin;
			this.config.toolbarAreaHeight = toolbarBtnTextHeight+this.params.toolBarBtnTextVMargin+this.params.toolBarBtnVPadding;
			canvasHeight = canvasHeight-this.config.toolbarAreaHeight;
			canvasStartY = canvasStartY+this.config.toolbarAreaHeight;
		}
		//----- Scroll bar -----//   
		canvasHeight = canvasHeight-this.params.scrollHeight-this.params.scrollPadding;
		this.config.scrollAreaHeight = this.params.scrollHeight+this.params.scrollPadding;
		//-------- Begin: Data Label calculation --------------//
		this.config.labelAreaHeight = 0;
		this.config.maxLabelHeight = 0;
		this.config.labelWrapMinWidth = 0;
		this.config.labelWrapMinWidth = 0;
		if (this.params.showLabels) {
			//Get the required style for labels
			var labelObj:Object;
			var labelStyleObj:Object = this.styleM.getTextStyle(this.objects.DATALABELS);
			//Calculate how many points are to be rendered (by way of pixelsPerPoint)
			//This is the maximum number of labels that'll appear in a single screen
			var maxVisibleLabels:Number = Math.min(numItems, Math.floor(canvasWidth/this.params.pixelsPerPoint));
			//Set a value for how many labels we can show, and have to show
			if (this.params.numVisibleLabels != -1) {
				//If user specified numVisibleLabels>maxVisibleLabels, store lower
				this.params.numVisibleLabels = Math.min(this.params.numVisibleLabels, maxVisibleLabels);
			} else {
				this.params.numVisibleLabels = maxVisibleLabels;
			}
			//If the label display is set as Auto, figure the best way to render them.
			//In that case, we choose between wrap (preferred) or rotate mode to see
			//which one acts better
			//Storage for string to be simulated.
			var str:String = "";
			//Build the string by adding upper-case letters A,B,C,D...
			for (i=1; i<=WRAP_MODE_MIN_CHARACTERS; i++) {
				//Build the string from upper case A,B,C...
				str = str+String.fromCharCode(64+i);
			}
			//Minimum width required to plot this string in both wrap
			//and rotate mode.
			var wrapMinWidth:Number = 0, rotateMinWidth:Number = 0;
			var fitLabels:Number;
			//Simulate width of this text field - without wrapping
			labelObj = createText(true, str, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
			wrapMinWidth = labelObj.width;
			this.config.labelWrapMinWidth = labelObj.width;
			//Store height required to render a single line of label text
			this.config.labelSingleLineHeight = labelObj.height;
			//Logic: Start with wrap mode, figure how many labels can be fit (using a min
			//of WRAP_MODE_MIN_CHARACTERS characters). If all can fit in well, use wrap mode.
			//If not, try rotate mode and see how many can fit. If all can fit, use rotate.
			//Else, switch back to wrap mode and only show as many labels as can fit.								
			if (this.params.labelDisplayOrig == "AUTO") {
				//If we've space to accommodate this width for all view labels, render in wrap mode
				if (this.params.numVisibleLabels*wrapMinWidth<=canvasWidth) {
					//Render all labels in wrap mode
					this.params.labelDisplay = "WRAP";
				} else {
					//Since we do not have space to accommodate the width of wrap mode:
					//Check if all labels can be placed in rotate mode
					this.params.labelDisplay = "ROTATE";
				}
			}
			//Data labels can be rendered in 4 ways:              
			//1. Normal - no staggering - no wrapping - no rotation
			//2. Wrapped - no staggering - no rotation
			//3. Staggered - no wrapping - no rotation
			//4. Rotated - no staggering - no wrapping
			//Placeholder to store max height
			this.config.maxLabelHeight = 0;
			this.config.labelAreaHeight = 0;
			if (this.params.labelDisplay == "ROTATE") {
				//Figure out the width required to render a single line of text in rotated mode
				rotateMinWidth = createText(true, str, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, false, 0, 0).width;
				//Calculate using this value as to how many can be rendered.
				//This is the maximum number of labels that can be rendered on the chart
				//without an overlap, in a single view.
				//If we've space to accommodate this width for all view labels, render without
				//skipping any label (i.e., use all slots)
				if (this.params.numVisibleLabels*rotateMinWidth<=canvasWidth) {
					this.config.labelSlots = this.params.numVisibleLabels;
				} else {
					//Set slots to maximum value that can be accommodated
					this.config.labelSlots = Math.floor(canvasWidth/rotateMinWidth);
				}
				//Based on slots, set skip index. This is calculated on the total
				//number of elements.
				this.config.labelStep = Math.ceil(numItems/this.config.labelSlots);
				//Iterate through each one, simulate and get max height
				//Note: Here, width is calculated based on canvas height, as the labels
				//are going to be rotated.
				var maxLabelWidth:Number = (canvasHeight/3);
				var maxLabelHeight:Number = (canvasWidth/this.config.labelSlots);
				//Case 4: If the labels are rotated, we iterate through all the string labels
				//provided to us and get the height and store max.
				for (i=1; i<=numItems; i=i+this.config.labelStep) {
					//Create text box and get height
					labelObj = createText(true, labels[i], this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					//Store the larger
					this.config.maxLabelHeight = (labelObj.height>this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
				}
				//Store max label height as label area height.
				this.config.labelAreaHeight = this.config.maxLabelHeight;
			} else if (this.params.labelDisplay == "WRAP") {
				//Figure out the width required to render a single line of text in Wrapped mode
				wrapMinWidth = createText(true, str, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0).width;
				//Calculate using this value as to how many can be rendered.
				//This is the maximum number of labels that can be rendered on the chart
				//without an overlap, in a single view.
				//If we've space to accommodate this width for all view labels, render without
				//skipping any label (i.e., use all slots)
				if (this.params.numVisibleLabels*wrapMinWidth<=canvasWidth) {
					this.config.labelSlots = this.params.numVisibleLabels;
				} else {
					//Set slots to maximum value that can be accommodated
					this.config.labelSlots = Math.floor(canvasWidth/wrapMinWidth);
				}
				//Based on slots, set skip index. This is calculated on the total
				//number of elements.
				this.config.labelStep = Math.ceil(numItems/this.config.labelSlots);
				//Now, to calculate height required, iterate through all labels, skipping through
				var maxLabelWidth:Number = (canvasWidth/this.config.labelSlots);
				var maxLabelHeight:Number = (canvasHeight/3);
				for (i=1; i<=numItems; i=i+this.config.labelStep) {
					//Create text box and get height
					labelObj = createText(true, labels[i], this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					//Store the larger
					this.config.maxLabelHeight = (labelObj.height>this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
				}
				//Store max label height as label area height. 
				this.config.labelAreaHeight = this.config.maxLabelHeight;
			} else {
				//Case 1,3: Normal or Staggered Label
				//Using the width required to render a single line of text in Wrapped mode,
				//calculate as to how many labels can be rendered.
				//This is the maximum number of labels that can be rendered on the chart
				//without an overlap, in a single view.
				//If we've space to accommodate this width for all view labels, render without
				//skipping any label (i.e., use all slots)
				if (this.params.numVisibleLabels*this.config.labelWrapMinWidth<=canvasWidth) {
					this.config.labelSlots = this.params.numVisibleLabels;
				} else {
					//Set slots to maximum value that can be accommodated
					this.config.labelSlots = Math.floor(canvasWidth/wrapMinWidth);
					//If in stagger mode, multiply by number of lines
					if (this.params.labelDisplay == "STAGGER") {
						this.config.labelSlots = this.params.staggerLines*this.config.labelSlots;
					}
				}
				//Based on slots, set skip index. This is calculated on the total
				//number of elements.
				this.config.labelStep = Math.ceil(numItems/this.config.labelSlots);				
				//Total label height required is this.config.labelSingleLineHeight (height of single line)
				this.config.maxLabelHeight = this.config.labelSingleLineHeight;
				if (this.params.labelDisplay == "STAGGER") {
					//Multiply max label height by stagger lines.
					this.config.labelAreaHeight = this.params.staggerLines*this.config.labelSingleLineHeight;
				} else {
					this.config.labelAreaHeight = this.config.labelWrapMinWidth;
				}
			}
			if (this.config.labelAreaHeight>0) {
				//Deduct the calculated label height from canvas height
				canvasHeight = canvasHeight-this.config.labelAreaHeight-this.params.labelPadding;
			}
			//Delete objects              
			delete labelObj;
			delete labelStyleObj;
		}
		//----- X Axis Name ------//   
		//Accomodate space for xAxisName (if to be shown);
		if (this.params.xAxisName != "") {
			//Create text field to get height
			var xAxisNameObj:Object = createText(true, this.params.xAxisName, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.XAXISNAME), true, canvasWidth, canvasHeight/2);
			//Store the height
			canvasHeight = canvasHeight-xAxisNameObj.height-this.params.xAxisNamePadding;
			//Object to store width and height of xAxisName
			this.elements.xAxisName = returnDataAsElement(0, 0, xAxisNameObj.width, xAxisNameObj.height);
			delete xAxisNameObj;
		}
		//-------- Legend ---------//		         
		//We now check whether the legend is to be drawn
		if (this.params.showLegend) {
			//Object to store dimensions
			var lgndDim:Object;
			//for the case of resizing
			if (isRedraw) {
				//Reallocate the original data for legend to the new legend, for changed status of chart is maintained in it.
				var lgndItems:Array = lgnd.items;
				//Set the legend ID Map
				var lgndIdMap:Array = lgnd.arrIdMap;
				//"true" for new legend to save bitmaps from getting removed from memory.
				lgnd.destroy(true);
				lgnd = null;
				this.lgndMC.removeMovieClip();
			}
			//Create container movie clip for legend          
			this.lgndMC = this.cMC.createEmptyMovieClip("Legend", this.dm.getDepth("LEGEND"));
			//Create instance of legend
			if (this.params.legendPosition == "BOTTOM") {
				//Maximum Height - 50% of stage
				lgnd = new AdvancedLegend(lgndMC, this.styleM.getTextStyle(this.objects.LEGEND), this.params.interactiveLegend, this.params.legendPosition, canvasStartX+canvasWidth/2, this.height/2, canvasWidth, (this.height-(this.params.chartTopMargin+this.params.chartBottomMargin))*0.5, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			} else {
				//Maximum Width - 40% of stage
				lgnd = new AdvancedLegend(lgndMC, this.styleM.getTextStyle(this.objects.LEGEND), this.params.interactiveLegend, this.params.legendPosition, this.width/2, canvasStartY+canvasHeight/2, (this.width-(this.params.chartLeftMargin+this.params.chartRightMargin))*0.4, canvasHeight, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			}
			if (this.params.minimiseWrappingInLegend) {
				lgnd.minimiseWrapping = true;
			}
			//If not resizing          
			if (!isRedraw) {
				//Get the height of single line text for legend items (to be used to specify icon width and height)
				var iconHeight:Number = lgnd.getIconHeight()*this.params.legendIconScale;
				var j:Number;
				var objIcon:Object;
				for (i=1; i<=this.numDS; i++) {
					//Adjust working index for reverseLegend option
					j = (this.params.reverseLegend) ? this.numDS-i+1 : i;
					//Validation of item eligibility					
					if (this.dataset[j].includeInLegend && this.dataset[j].seriesName != "") {
						objIcon = {lineColor:parseInt(this.dataset[j].color, 16), intraIconPaddingPercent:0.3};
						//If anchor be shown
						if (this.dataset[j].drawAnchors) {
							//Fill in respective params
							objIcon.showAnchor = true;
							//Anchor bg color
							objIcon.anchorBgColor = parseInt(this.dataset[j].anchorBgColor, 16);
							//Number of sides of anchor polygon
							objIcon.anchorSides = this.dataset[j].anchorSides;
							//Anchor border color
							objIcon.anchorBorderColor = parseInt(this.dataset[j].anchorBorderColor, 16);
						}
						//Create the 2 icons          
						var objIcons:Object = LegendIconGenerator.getIcons(LegendIconGenerator["LINE"], iconHeight, true, objIcon);
						//State specific icon BitmapData
						var bmpd1:BitmapData = objIcons.active;
						var bmpd2:BitmapData = objIcons.inactive;
						//Add the item to legend
						lgnd.addItem(this.dataset[j].seriesName, j, true, bmpd1, bmpd2);
					}
				}
				//If resizing
			} else {
				//Set the reference of 'items' array of previous legend instance to that of next one
				lgnd.items = lgndItems;
				//Set the count value too
				lgnd.count = lgndItems.length;
				//Set array of map
				lgnd.arrIdMap = lgndIdMap;
			}
			//If user has defined a caption for the legend, set it
			if (this.params.legendCaption != "") {
				lgnd.setCaption(this.params.legendCaption);
			}
			if (this.params.legendPosition == "BOTTOM") {
				lgndDim = lgnd.getDimensions();
				//Now deduct the height from the calculated canvas height
				canvasHeight = canvasHeight-lgndDim.height-this.params.legendPadding;
				//Re-set the legend position
				this.lgnd.resetXY(canvasStartX+canvasWidth/2, this.height-this.params.chartBottomMargin-lgndDim.height/2);
			} else {
				//Get dimensions
				lgndDim = lgnd.getDimensions();
				//Now deduct the width from the calculated canvas width
				canvasWidth = canvasWidth-lgndDim.width-this.params.legendPadding;
				//Right position
				this.lgnd.resetXY(this.width-this.params.chartRightMargin-lgndDim.width/2, canvasStartY+canvasHeight/2);
			}
		}
		//We now have canvas start Y and canvas height              
		//----------- HANDLING CUSTOM CANVAS MARGINS --------------//     
		//Before doing so, we take into consideration, user's forced canvas margins (if any defined)
		//If the user's forced values result in overlapping of chart items, we ignore.
		if (this.params.canvasLeftMargin != -1 && this.params.canvasLeftMargin>canvasStartX) {
			//Update width (deduct the difference)
			canvasWidth = canvasWidth-(this.params.canvasLeftMargin-canvasStartX);
			//Update left start position
			canvasStartX = this.params.canvasLeftMargin;
		}
		if (this.params.canvasRightMargin != -1 && (this.params.canvasRightMargin>(this.width-(canvasStartX+canvasWidth)))) {
			//Update width (deduct the difference)
			canvasWidth = canvasWidth-(this.params.canvasRightMargin-(this.width-(canvasStartX+canvasWidth)));
		}
		if (this.params.canvasTopMargin != -1 && this.params.canvasTopMargin>canvasStartY) {
			//Update height (deduct the difference)
			canvasHeight = canvasHeight-(this.params.canvasTopMargin-canvasStartY);
			//Update top start position
			canvasStartY = this.params.canvasTopMargin;
		}
		if (this.params.canvasBottomMargin != -1 && (this.params.canvasBottomMargin>(this.height-(canvasStartY+canvasHeight)))) {
			//Update height(deduct the difference)
			canvasHeight = canvasHeight-(this.params.canvasBottomMargin-(this.height-(canvasStartY+canvasHeight)));
		}
		//------------ END OF CUSTOM CANVAS MARGIN HANDLING --------------------//              
		//Create an element to represent the canvas now.
		this.elements.canvas = returnDataAsElement(canvasStartX, canvasStartY, canvasWidth, canvasHeight);
		//Store references for quick access
		this.canvasX = canvasStartX;
		this.canvasY = canvasStartY;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
	}
	/**
	 * Calculates the indexes for display subset.
	*/
	private function calculateSubsetSteps():Void {
		//Number of display slots that is available
		slots = Math.floor(this.elements.canvas.w/this.params.pixelsPerPoint);
		//What should be skip index (step) for the data points.
		//This is applicable in cases when there are more data items
		//than slots available. Then items have to be skipped to be
		//able to show the data efficiently.
		subsetStep = Math.ceil((subsetEndIndex-subsetStartIndex+1)/slots);
		//Size of scroll pane - how many items to show in this
		scrollPaneSize = subsetEndIndex-subsetStartIndex;
	}
	/**
	* allotDepths method allots the depths for various chart objects
	* to be rendered. We do this before hand, so that we can later just
	* go on rendering chart objects, without swapping.
	*/
	private function allotDepths():Void {
		//Background
		this.dm.reserveDepths("BACKGROUND", 1);
		//Click URL Handler
		this.dm.reserveDepths("CLICKURLHANDLER", 1);
		//Background SWF
		this.dm.reserveDepths("BGSWF", 1);
		//Canvas
		this.dm.reserveDepths("CANVAS", 1);
		//X-axis Labels
		this.dm.reserveDepths("XAXISLABELS", 1);
		//Y-Axis Values
		this.dm.reserveDepths("YAXISVALUES", 1);
		//Data values
		//Pin overlay
		this.dm.reserveDepths("PINOVERLAY", 1);
		//Canvas overlay (border)
		this.dm.reserveDepths("CANVASOVERLAY", 1);
		//Caption
		this.dm.reserveDepths("CAPTION", 1);
		//Sub-caption
		this.dm.reserveDepths("SUBCAPTION", 1);
		//X-Axis Name
		this.dm.reserveDepths("XAXISNAME", 1);
		//Y-Axis Name
		this.dm.reserveDepths("YAXISNAME", 1);
		//Vertical trend lines below plot (lines and their labels)
		this.dm.reserveDepths("VTRENDLINESBELOW", this.numVTrendLines);
		//Trend lines below plot (lines and their labels)
		this.dm.reserveDepths("TRENDLINESBELOW", this.numTrendLinesBelow);
		//Chart clip
		this.dm.reserveDepths("CHART", 1);
		//Data Values
		this.dm.reserveDepths("DATAVALUES", 1);
		//Vertical trend lines labels (above plot)
		this.dm.reserveDepths("VTRENDLINESABOVE", this.numVTrendLines - this.numVTrendLinesBelow);		
		//Trend lines above plot (lines and their labels)
		this.dm.reserveDepths("TRENDLINESABOVE", (this.numTrendLines-this.numTrendLinesBelow));
		//Scroll bar
		this.dm.reserveDepths("SCROLLBAR", 1);
		//Tool bar
		this.dm.reserveDepths("TOOLBAR", 1);
		//Zoom pane container
		this.dm.reserveDepths("ZOOMPANECONTAINER", 1);
		//Data Tool tip
		this.dm.reserveDepths("DATATOOLTIP", 1);
		//Legend
		this.dm.reserveDepths("LEGEND", 1);
		//Mouse Cursors
		this.dm.reserveDepths("MOUSECURSORS", 1);
	}
	/**
	* Based on the indexes of display subset, populate the subset
	*/
	private function populateSubset():Void {
		//Create the new array
		subsetIndex = new Array();
		//Iterate through all indexes in primary data model and 
		//add to subset.
		var i:Number, j:Number;
		for (i=1; i<=numDS; i++) {
			//Set item count to 0
			subsetItems = 0;
			//Create nested array for this dataset
			subsetIndex[i] = new Array();
			for (j=subsetStartIndex; j<=subsetEndIndex; j=j+subsetStep) {
				//Increment counter
				subsetItems++;
				//Push the index in subsetIndex[i][j]
				subsetIndex[i][subsetItems] = j;
			}
		}
		//If axis is dynamic, re-calculate the limits
		if (this.params.dynamicAxis) {
			//Calculate limits in the subset
			calculateSubsetLimits();
			//Adjust axis accordingly
			calculateAxis();
		}
	}
	/**
	* Calculates the rendering co-ordinates for this subset.
	*/
	private function calculateSubsetCoords():Void {
		//For each item in the subset, calculate the x and y position
		var count:Number;
		var i:Number, j:Number;
		//Create the arrays
		subsetX = new Array();
		subsetY = new Array();
		//Calculate the inter point distance
		ipdistance = this.elements.canvas.w/(subsetItems-1);
		//Iterate through all indexes in primary data model and 	
		for (i=1; i<=numDS; i++) {
			//Set counter to 0
			count = 0;
			//Create nested array for this dataset
			subsetX[i] = new Array();
			subsetY[i] = new Array();
			for (j=subsetStartIndex; j<=subsetEndIndex; j=j+subsetStep) {
				//Increment count for this series
				count++;
				//Store the x-position
				subsetX[i][count] = (numItems == 1) ? (this.elements.canvas.w/2) : (ipdistance*(count-1));
				//If data is non NaN, define
				if (!isNaN(data[i][j])) {
					//Push the index in subsetIndex[i][j]
					subsetY[i][count] = getYPos(data[i][j]);
				}
			}
		}
	}
	// --------- Drawing functions -------------//
	/**
	 * Create containers creates movie clips required by dynamic objects.
	 * This is to ensure optimization that such dynamic objects mostly just
	 * clear and redraw within the movie clip container.
	 * All base objects create their movie clip in their drawing routine, 
	 * as they do not need to change like dynamic objects, unless the chart
	 * is resized.
	*/
	function createContainers(parentMC:MovieClip) {
		//Create the chart movie clip
		chartMC = parentMC.createEmptyMovieClip("Chart", this.dm.getDepth("CHART"));
		//Create data values movie clip
		valuesMC = parentMC.createEmptyMovieClip("DataValues", this.dm.getDepth("DATAVALUES"));
		//Position both at start of canvas (start X, top Y).
		chartMC._x = canvasX;
		chartMC._y = canvasY;
		valuesMC._x = canvasX;
		valuesMC._y = canvasY;
		//Create dataset specific movie clips
		var dsMC:MovieClip, dvMC:MovieClip;
		var i:Number;
		for (i=1; i<=numDS; i++) {
			//Create the nested movie clip in both
			dsMC = chartMC.createEmptyMovieClip("Series_"+i, i);
			dvMC = valuesMC.createEmptyMovieClip("Series_"+i, i);
			//Apply shadow and filters to the dataset movie clip
			this.styleM.applyFilters(dsMC, this.objects.DATAPLOT);
		}
		//Apply collective shadow and filters to data values
		this.styleM.applyFilters(valuesMC, this.objects.DATAVALUES);
		//Create tool-bar for zoom-out buttons
		toolbarMC = parentMC.createEmptyMovieClip("Toolbar", this.dm.getDepth("TOOLBAR"));
		//Create container for mouse cursors
		if (this.params.enableIconMouseCursors){
			mouseCursors = parentMC.createEmptyMovieClip("MouseCursors", this.dm.getDepth("MOUSECURSORS"));
			mouseDragCursor = mouseCursors.createEmptyMovieClip("DragCursor", 1);
			mouseZoomCursor = mouseCursors.createEmptyMovieClip("ZoomCursor", 2);
			mousePinCursor = mouseCursors.createEmptyMovieClip("PinCursor", 3);
			//Set mouseCursors movie clip outside canvas to avoid flicker
			mouseCursors._x = -100;
			mouseCursors._y = -100;
			//By default, hide all mouse cursors
			mouseDragCursor._visible = false;
			mouseZoomCursor._visible = false;
			mousePinCursor._visible = false;
		}
	}
	/**
	 * Draws the base objects (which do not change upon zoom/scroll).
	 * However, during resize, base objects need to be drawn again as well.
	*/
	private function drawBaseObjects():Void {
		//Draw background
		drawBackground();
		//Draw click URL handler
		drawClickURLHandler();
		//Load background SWF
		loadBgSWF();
		//Draw canvas
		drawCanvas();
		//Draw headers
		drawHeaders();
		//Draw scroll bar
		drawScrollBar();
		//Draw tool bar
		if (this.params.drawToolbarButtons) {
			drawToolbar();
		}
		//Draw legend   
		drawLegend();
		//Draw y-axis values only if it's not dynamic
		if (this.params.dynamicAxis == false) {
			drawYAxisValues();
			if (this.numTrendLines>0) {
				drawTrendLines();
			}
		}
	}
	/**
	 * Renders the visual canvas. Also renders the zoom handlers.
	*/
	function drawCanvas():Void {
		//Create container
		canvasMC = this.cMC.createEmptyMovieClip("Canvas", this.dm.getDepth("CANVAS"));
		canvasOverlay = this.cMC.createEmptyMovieClip("CanvasOverlay", this.dm.getDepth("CANVASOVERLAY"));
		zoomPaneContainer = this.cMC.createEmptyMovieClip("ZoomPaneContainer", this.dm.getDepth("ZOOMPANECONTAINER"));
		//Clear existing drawing
		canvasMC.clear();
		//Clear existing event handlers
		delete canvasMC.onPress;
		delete canvasMC.onRelease;
		delete canvasMC.onReleaseOutside;
		//Set the position of canvas and overlay
		canvasMC._x = canvasX;
		canvasMC._y = canvasY;
		canvasOverlay._x = canvasX;
		canvasOverlay._y = canvasY;
		zoomPaneContainer._x = canvasX;
		zoomPaneContainer._y = canvasY;
		//Draw the filled rectangle as part of canvas
		canvasMC.beginFill(parseInt(this.params.canvasBgColor, 16), this.params.canvasBgAlpha);
		canvasMC.moveTo(0, 0);
		canvasMC.lineTo(canvasWidth, 0);
		canvasMC.lineTo(canvasWidth, canvasHeight);
		canvasMC.lineTo(0, canvasHeight);
		canvasMC.lineTo(0, 0);
		canvasMC.endFill();
		//Draw the border in overlay movie
		canvasOverlay.lineStyle(this.params.canvasBorderThickness, parseInt(this.params.canvasBorderColor, 16), this.params.canvasBorderAlpha);
		canvasOverlay.moveTo(0, 0);
		canvasOverlay.lineTo(canvasWidth, 0);
		canvasOverlay.lineTo(canvasWidth, canvasHeight);
		canvasOverlay.lineTo(0, canvasHeight);
		canvasOverlay.lineTo(0, 0);
		//Set filters for canvas background	
		this.styleM.applyFilters(canvasMC, this.objects.CANVAS);
		//Reference to class
		var classRef = this;
		//--- Local nested functions ----//
		function approxMousePos(pos:Number):Number {
			//Restrict mouse to drawing slots
			pos = classRef.subsetX[1][classRef.getNearestIndex(pos)];
			return pos;
		}
		//---- End Local nested functions ----//
		var startMouseX:Number, currMouseX:Number, endMouseX:Number;
		//Do not use hand mouse cursor
		canvasMC.useHandCursor = false;
		//Define the roll over and roll out events for tool-tip
		canvasMC.onRollOver = function() {
			//Add the tool tip movie clip
			classRef.addToolTip();
			//Define the onMouseMove Event
			this.onMouseMove = function() {
				//Show tool-tip only if the mouse is hovering over canvas
				if (this._xmouse>=0 && this._xmouse<=this._width) {
					classRef.drawToolTip(this._xmouse);
				}
			};
			//Set cursor, based on mode
			if (classRef.zoomMode == true) {
				//Set zoom cursor, if there are more than 2 subsetItems
				if (classRef.subsetItems>2) {
					classRef.setMouseCursor("zoom");
				} else {
					classRef.setMouseCursor("normal");
				}
			} else {
				if (classRef.pinDrawn == false) {
					classRef.setMouseCursor("pin");
				} else {
					classRef.setMouseCursor("normal");
				}
			}
		};
		canvasMC.onRollOut = function() {
			classRef.lastIndex = null;
			//Delete the mouse move event
			delete this.onMouseMove;
			//Remove tool-tip
			classRef.removeToolTip();
			//Set back to normal cursor
			classRef.setMouseCursor("normal");
		};
		//Define the zoom pane handlers
		canvasMC.onPress = function() {
			//If the chart is in zoom mode, or chart is in pin mode and no pin has been drawn yet
			if ((classRef.zoomMode == true && classRef.subsetItems>2) || (classRef.zoomMode == false && classRef.pinDrawn == false)) {
				//Create the movie clip
				classRef.canvasZoomPane = classRef.zoomPaneContainer.createEmptyMovieClip("ZoomPane", 1);
				//Remove tool-tip
				classRef.removeToolTip();
				//Store start mouse position				
				startMouseX = approxMousePos(this._xmouse);
				//Draw the starting label
				var index:Number = classRef.localToGlobalIndex(classRef.getNearestIndex(startMouseX));
				classRef.createText(false, classRef.labels[index], classRef.canvasZoomPane, 1, startMouseX, classRef.canvasHeight+classRef.params.scrollHeight+classRef.params.scrollPadding+classRef.params.labelPadding, null, classRef.styleM.getTextStyle(classRef.objects.OVERLAYLABEL), false, 0, 0);
				//Define the Enter-Frame handler
				this.onEnterFrame = function() {
					//If mouse has moved, store
					if (currMouseX != this._xmouse) {
						currMouseX = approxMousePos(this._xmouse);
						//Draw the ending label
						var index:Number = classRef.localToGlobalIndex(classRef.getNearestIndex(currMouseX));
						classRef.createText(false, classRef.labels[index], classRef.canvasZoomPane, 2, currMouseX, classRef.canvasHeight+classRef.params.scrollHeight+classRef.params.scrollPadding+classRef.params.labelPadding, null, classRef.styleM.getTextStyle(classRef.objects.OVERLAYLABEL), false, 0, 0);
						//Draw the zoom pane from start Mouse position to current
						//Clear the existing drawing
						classRef.canvasZoomPane.clear();
						classRef.canvasZoomPane.beginFill(parseInt(classRef.params.zoomPaneBgColor, 16), classRef.params.zoomPaneBgAlpha);
						classRef.canvasZoomPane.lineStyle(0, parseInt(classRef.params.zoomPaneBorderColor, 16), 100);
						classRef.canvasZoomPane.moveTo(startMouseX, 0);
						classRef.canvasZoomPane.lineStyle();
						classRef.canvasZoomPane.lineTo(currMouseX, 0);
						classRef.canvasZoomPane.lineStyle(0, parseInt(classRef.params.zoomPaneBorderColor, 16), 100);
						classRef.canvasZoomPane.lineTo(currMouseX, classRef.canvasHeight);
						classRef.canvasZoomPane.lineStyle();
						classRef.canvasZoomPane.lineTo(startMouseX, classRef.canvasHeight);
						classRef.canvasZoomPane.lineStyle(0, parseInt(classRef.params.zoomPaneBorderColor, 16), 100);
						classRef.canvasZoomPane.lineTo(startMouseX, 0);
						classRef.canvasZoomPane.endFill();
					}
				};
			}
		};
		canvasMC.onRelease = canvasMC.onReleaseOutside=function () {
			//If the chart is in zoom mode, or chart is in pin mode and no pin has been drawn yet
			if ((classRef.zoomMode == true && classRef.subsetItems>2) || (classRef.zoomMode == false && classRef.pinDrawn == false)) {
				//Delete the enterFrame hander
				delete this.onEnterFrame;
				//Remove the movie clip
				classRef.canvasZoomPane.removeMovieClip();
				//Store the final value
				endMouseX = approxMousePos(this._xmouse);
				//Add the tool tip movie clip back again
				classRef.addToolTip();
				//Invoke function to do post zoom work				
				classRef.zoomHandlerAction(startMouseX, endMouseX);
			}
			classRef.lastIndex = null;
		};
	}
	/**
	* drawHeaders method renders the following on the chart:
	* CAPTION, SUBCAPTION, XAXISNAME, YAXISNAME
	*/
	private function drawHeaders() {
		var captionEndPos:Number = this.params.chartTopMargin;
		//Render caption
		if (this.params.caption != "") {
			var captionStyleObj:Object = this.styleM.getTextStyle(this.objects.CAPTION);
			captionStyleObj.vAlign = "bottom";
			//Switch the alignment to lower case
			captionStyleObj.align = captionStyleObj.align.toLowerCase();
			//Now, based on alignment, decide the xPosition of the caption
			var xPos:Number = (captionStyleObj.align == "center") ? (this.elements.canvas.x+(this.elements.canvas.w/2)) : ((captionStyleObj.align == "left") ? (this.elements.canvas.x) : (this.elements.canvas.toX));
			var captionObj:Object = createText(false, this.params.caption, this.cMC, this.dm.getDepth("CAPTION"), xPos, this.params.chartTopMargin, 0, captionStyleObj, true, this.elements.caption.w, this.elements.caption.h);
			captionEndPos += captionObj.height;
			//Apply filters          
			this.styleM.applyFilters(captionObj.tf, this.objects.CAPTION);
			//Delete
			delete captionObj;
			delete captionStyleObj;
		}
		//Render sub caption           
		if (this.params.subCaption != "") {
			var subCaptionStyleObj:Object = this.styleM.getTextStyle(this.objects.SUBCAPTION);
			//Switch the alignment to lower case
			subCaptionStyleObj.align = subCaptionStyleObj.align.toLowerCase();
			//Now, based on alignment, decide the xPosition of the caption
			var xPos:Number = (subCaptionStyleObj.align == "center") ? (this.elements.canvas.x+(this.elements.canvas.w/2)) : ((subCaptionStyleObj.align == "left") ? (this.elements.canvas.x) : (this.elements.canvas.toX));
			var subCaptionObj:Object = createText(false, this.params.subCaption, this.cMC, this.dm.getDepth("SUBCAPTION"), xPos, captionEndPos, 0, subCaptionStyleObj, true, this.elements.subCaption.w, this.elements.subCaption.h);
			//Apply filters          
			this.styleM.applyFilters(subCaptionObj.tf, this.objects.SUBCAPTION);
			//Delete
			delete subCaptionObj;
			delete subCaptionStyleObj;
		}
		//Render x-axis name           
		if (this.params.xAxisName != "") {
			var xAxisNameStyleObj:Object = this.styleM.getTextStyle(this.objects.XAXISNAME);
			xAxisNameStyleObj.align = "center";
			xAxisNameStyleObj.vAlign = "bottom";
			var xAxisNameObj:Object = createText(false, this.params.xAxisName, this.cMC, this.dm.getDepth("XAXISNAME"), this.elements.canvas.x+(this.elements.canvas.w/2), this.elements.canvas.toY+this.config.scrollAreaHeight+this.params.labelPadding+this.config.labelAreaHeight+this.params.xAxisNamePadding, 0, xAxisNameStyleObj, true, this.elements.xAxisName.w, this.elements.xAxisName.h);
			//Apply filters          
			this.styleM.applyFilters(xAxisNameObj.tf, this.objects.XAXISNAME);
			//Delete
			delete xAxisNameObj;
			delete xAxisNameStyleObj;
		}
		//Render y-axis name           
		if (this.params.yAxisName != "") {
			var yAxisNameStyleObj:Object = this.styleM.getTextStyle(this.objects.YAXISNAME);
			//Set alignment parameters
			yAxisNameStyleObj.align = "left";
			yAxisNameStyleObj.vAlign = "middle";
			//If the name is to be rotated
			if (this.params.rotateYAxisName) {
				if(this.params.centerYAxisName){
					//Center y axis name with respect to chart.
					var yAxisNameObj : Object = createText(false, this.params.yAxisName, this.cMC, this.dm.getDepth("YAXISNAME"), this.params.chartLeftMargin, this.height/2, 270, yAxisNameStyleObj, true, this.elements.yAxisName.h, this.elements.yAxisName.w);
				} else {
					//Center y axis name with respect to canvas.
					var yAxisNameObj : Object = createText (false, this.params.yAxisName, this.cMC, this.dm.getDepth ("YAXISNAME") , this.params.chartLeftMargin, this.elements.canvas.y + (this.elements.canvas.h / 2), 270, yAxisNameStyleObj, true, this.elements.yAxisName.h, this.elements.yAxisName.w);
				}
			} else {
				//We show horizontal name
				//Adding 1 to this.params.yAxisNameWidth and then passing to avoid line breaks
				var yAxisNameObj:Object = createText(false, this.params.yAxisName, this.cMC, this.dm.getDepth("YAXISNAME"), this.params.chartLeftMargin, this.elements.canvas.y+(this.elements.canvas.h/2), 0, yAxisNameStyleObj, true, this.params.yAxisNameWidth+1, this.elements.canvas.h);
			}
			//Apply filters
			this.styleM.applyFilters(yAxisNameObj.tf, this.objects.YAXISNAME);
			//Delete
			delete yAxisNameObj;
			delete yAxisNameStyleObj;
		}
		//Clear Interval           
		clearInterval(this.config.intervals.headers);
	}
	/**
	 * Draws the y-axis handler and guides
	*/
	private function drawYAxisValues():Void {
		//Remove movie clip, if already present
		if (yAxisValuesMC != undefined) {
			yAxisValuesMC.removeMovieClip();
		}
		//Re-create the movie clips         
		yAxisValuesMC = this.cMC.createEmptyMovieClip("YAXisValues", this.dm.getDepth("YAXISVALUES"));
		//Create sub movie clips to render text fields, line and grid separately
		var yAxisValuesTFMC:MovieClip = yAxisValuesMC.createEmptyMovieClip("Labels", 1);
		var yAxisValuesGridMC:MovieClip = yAxisValuesMC.createEmptyMovieClip("Grids", 2);
		var yAxisValuesLineMC:MovieClip = yAxisValuesMC.createEmptyMovieClip("Lines", 3);
		//Simulate how many y-axis values can be fitted in		
		var numLabelFit:Number = Math.floor(canvasHeight/this.config.yAxisValueHeight);
		//Y Position
		var yPos:Number;
		//Get a similar range from Axis
		var intervals = ax.getIntervals(numLabelFit);
		//Count and previous y position
		var count:Number = 0;
		var prevY:Number = 0;
		//Iterate through each and render
		for (var i:Number = 0; i<intervals.length; i++) {
			//Counter increment
			count++;
			//Calculate the yposition
			yPos = canvasY+getYPos(intervals[i].position);
			//Display the label, if it's a major interval
			if (this.params.showYAxisValues && intervals[i].isMajor) {
				//Create text field
				createText(false, formatInterval(intervals[i].position), yAxisValuesTFMC, i+1, canvasX-this.params.yAxisValuesPadding, yPos, null, this.styleM.getTextStyle(this.objects.YAXISVALUES), false, 0, 0);
			}
			//Set line           
			//If it's a 0 line, draw with double thickness
			if (intervals[i].position == 0) {
				yAxisValuesLineMC.lineStyle(this.params.zeroPlaneThickness, parseInt(this.params.zeroPlaneColor, 16), this.params.zeroPlaneAlpha);
			} else {
				yAxisValuesLineMC.lineStyle(this.params.divLineThickness, parseInt(this.params.divLineColor, 16), this.params.divLineAlpha);
			}
			//Draw the line
			if (this.params.divLineIsDashed) {
				//If it's a dashed lined
				DrawingExt.dashTo(yAxisValuesLineMC, canvasX, yPos, canvasX+canvasWidth, yPos, this.params.divLineDashLen, this.params.divLineDashGap);
			} else {
				//Else, plot normal line
				yAxisValuesLineMC.moveTo(canvasX, yPos);
				yAxisValuesLineMC.lineTo(canvasX+canvasWidth, yPos);
			}
			//Draw the grid for even instances (of linear axis)
			if (this.params.axis == "LINEAR" && this.params.showAlternateHGridColor && count%2 == 0) {
				yAxisValuesGridMC.beginFill(parseInt(this.params.alternateHGridColor, 16), this.params.alternateHGridAlpha);
				drawRectangle(yAxisValuesGridMC, canvasX, prevY, canvasWidth, (yPos-prevY));
				yAxisValuesGridMC.endFill();
			}
			//Store previous y         
			prevY = yPos;
		}
	}
	/**
	 * Draws the trend lines for the charts.
	*/
	private function drawTrendLines():Void {
		//Remove movie clip, if already present
		if (trendBelowMC != undefined) {
			trendBelowMC.removeMovieClip();
		}
		if (trendAboveMC != undefined) {
			trendAboveMC.removeMovieClip();
		}
		//Re-create the movie clips         
		trendBelowMC = this.cMC.createEmptyMovieClip("TrendBelow", this.dm.getDepth("TRENDLINESBELOW"));
		trendAboveMC = this.cMC.createEmptyMovieClip("TrendAbove", this.dm.getDepth("TRENDLINESABOVE"));
		//Set the x and y positions
		trendBelowMC._x = canvasX;
		trendBelowMC._y = canvasY;
		trendAboveMC._x = canvasX;
		trendAboveMC._y = canvasY;
		//Create sub movie clips to render text fields and line separately
		var trendBelowLineMC:MovieClip = trendBelowMC.createEmptyMovieClip("Lines", 1);
		var trendBelowTextMC:MovieClip = trendBelowMC.createEmptyMovieClip("Labels", 2);
		var trendAboveLineMC:MovieClip = trendAboveMC.createEmptyMovieClip("Lines", 1);
		var trendAboveTextMC:MovieClip = trendAboveMC.createEmptyMovieClip("Labels", 2);
		//Get font
		var trendFontObj:Object = this.styleM.getTextStyle(this.objects.TRENDVALUES);
		//Set vertical alignment
		trendFontObj.vAlign = "middle";
		//Delegate function for tool-text for trendlines
		var fnRollOver:Function;
		//Reference pointers
		var trendLineMC:MovieClip, trendTextMC:MovieClip;
		//Loop variable
		var i:Number;
		//Boolean value to determine trend zone status changed due to negotiable height
		var trendZoneStatChange:Boolean;
		for (i=1; i<=this.numTrendLines; i++) {
			//Set validity of trend line
			//If the trend line start/end value is NaN or out of range
			if (isNaN(this.trendLines[i].startValue) || (this.trendLines[i].startValue<ax.getMin()) || (this.trendLines[i].startValue>ax.getMax()) || isNaN(this.trendLines[i].endValue) || (this.trendLines[i].endValue<ax.getMin()) || (this.trendLines[i].endValue>ax.getMax())) {
				//Invalidate it
				this.trendLines[i].isValid = false;
			} else {
				//Set to valid
				this.trendLines[i].isValid = true;
			}
			//We proceed only if the trend line is valid  
			if (this.trendLines[i].isValid == true) {
				//Calculate and store y-positions
				this.trendLines[i].y = getYPos(this.trendLines[i].startValue);
				//If end value is different from start value
				if (this.trendLines[i].startValue != this.trendLines[i].endValue) {
					//Calculate y for end value
					this.trendLines[i].toY = getYPos(this.trendLines[i].endValue);
					//Now, if it's a trend zone, we need mid value
					if (this.trendLines[i].isTrendZone) {
						//For textbox y position, we need mid value.
						this.trendLines[i].tbY = Math.min(this.trendLines[i].y, this.trendLines[i].toY)+(Math.abs(this.trendLines[i].y-this.trendLines[i].toY)/2);
					} else {
						//If the value is to be shown on left, then at left
						if (this.trendLines[i].valueOnRight) {
							this.trendLines[i].tbY = this.trendLines[i].toY;
						} else {
							this.trendLines[i].tbY = this.trendLines[i].y;
						}
					}
					//Height
					this.trendLines[i].h = (this.trendLines[i].toY-this.trendLines[i].y);
				} else {
					//Just copy
					this.trendLines[i].toY = this.trendLines[i].y;
					//Set same position for value tb
					this.trendLines[i].tbY = this.trendLines[i].y;
					//Height
					this.trendLines[i].h = 0;
				}
				//Set trend zone change status to false
				trendZoneStatChange = false;
				//When trendzone height is less than 0.3 the zone is not visible. So need to display the single trend line.
				//At that situation isTrendZone needs to make false.
				if(this.trendLines [i].isTrendZone && Math.abs(this.trendLines [i].h) < 0.3){
					this.trendLines [i].isTrendZone = false;
					trendZoneStatChange = true;
				}
				//Draw it.
				//If it's a valid trend line
				//Get the depth and update counters
				if (this.trendLines[i].showOnTop) {
					trendLineMC = trendAboveLineMC;
					trendTextMC = trendAboveTextMC;
				} else {
					trendLineMC = trendBelowLineMC;
					trendTextMC = trendBelowTextMC;
				}
				//Now, draw the line or trend zone
				if (this.trendLines[i].isTrendZone) {
					//Create rectangle
					trendLineMC.lineStyle();
					//Absolute height value
					this.trendLines[i].h = Math.abs(this.trendLines[i].h);
					//We need to align rectangle at L,M
					trendLineMC.moveTo(0, 0);
					//Begin fill
					trendLineMC.beginFill(parseInt(this.trendLines[i].color, 16), this.trendLines[i].alpha);
					//Draw rectangle
					trendLineMC.lineTo(0, this.trendLines[i].tbY-(this.trendLines[i].h/2));
					trendLineMC.lineTo(this.elements.canvas.w, this.trendLines[i].tbY-(this.trendLines[i].h/2));
					trendLineMC.lineTo(this.elements.canvas.w, this.trendLines[i].tbY+(this.trendLines[i].h/2));
					trendLineMC.lineTo(0, this.trendLines[i].tbY+(this.trendLines[i].h/2));
					trendLineMC.lineTo(0, this.trendLines[i].tbY);
				} else {
					//When trend zone status is changed draw a hair line instead of using trendline thickness.
					this.trendLines[i].thickness = (trendZoneStatChange)? 0 : this.trendLines[i].thickness;
					//Just draw line  
					trendLineMC.lineStyle(this.trendLines[i].thickness, parseInt(this.trendLines[i].color, 16), this.trendLines[i].alpha);
					//Now, if dashed line is to be drawn
					if (!this.trendLines[i].isDashed) {
						//Draw normal line line keeping 0,0 as registration point
						trendLineMC.moveTo(0, this.trendLines[i].y);
						trendLineMC.lineTo(this.elements.canvas.w, this.trendLines[i].y+this.trendLines[i].h);
					} else {
						//Dashed Line line
						DrawingExt.dashTo(trendLineMC, 0, this.trendLines[i].y, this.elements.canvas.w, this.trendLines[i].y+this.trendLines[i].h, this.trendLines[i].dashLen, this.trendLines[i].dashGap);
					}
				}
				//Set color  
				trendFontObj.color = this.trendLines[i].color;
				//Now, render the trend line value, based on its position
				if (this.trendLines[i].valueOnRight == false) {
					//Value to be placed on right
					trendFontObj.align = "right";
					//Create text
					var trendValueObj:Object = createText(false, this.trendLines[i].displayValue, trendTextMC, i, 0-this.params.yAxisValuesPadding, this.trendLines[i].tbY, 0, trendFontObj, false, 0, 0);
				} else {
					//Left side
					trendFontObj.align = "left";
					//Create text
					var trendValueObj:Object = createText(false, this.trendLines[i].displayValue, trendTextMC, i, this.elements.canvas.w+this.params.yAxisValuesPadding, this.trendLines[i].tbY, 0, trendFontObj, false, 0, 0);
				}
			}
		}
		//Apply filters
		this.styleM.applyFilters(trendAboveMC, this.objects.TRENDLINES);
		this.styleM.applyFilters(trendBelowMC, this.objects.TRENDLINES);
		this.styleM.applyFilters(trendBelowTextMC, this.objects.TRENDVALUES);
		this.styleM.applyFilters(trendAboveTextMC, this.objects.TRENDVALUES);
	}
	/**
	 * Draws the vertical trend lines for the charts.
	*/
	private function drawVTrendLines():Void {
		//Remove movie clip, if already present
		if (vTrendBelowMC != undefined) {
			vTrendBelowMC.removeMovieClip();
		}
		if (vTrendAboveMC != undefined) {
			vTrendAboveMC.removeMovieClip();
		}
		//Re-create the movie clips         
		vTrendBelowMC = this.cMC.createEmptyMovieClip("VTrendBelow", this.dm.getDepth("VTRENDLINESBELOW"));
		vTrendAboveMC = this.cMC.createEmptyMovieClip("VTrendAbove", this.dm.getDepth("VTRENDLINESABOVE"));
		//Set the x and y positions
		vTrendBelowMC._x = canvasX;
		vTrendBelowMC._y = canvasY;
		vTrendAboveMC._x = canvasX;
		vTrendAboveMC._y = canvasY;
		//Create sub movie clips to render text fields and line separately
		var vTrendBelowLineMC:MovieClip = vTrendBelowMC.createEmptyMovieClip("Lines", 1);
		var vTrendBelowTextMC:MovieClip = vTrendBelowMC.createEmptyMovieClip("Labels", 2);
		var vTrendAboveLineMC:MovieClip = vTrendAboveMC.createEmptyMovieClip("Lines", 1);
		var vTrendAboveTextMC:MovieClip = vTrendAboveMC.createEmptyMovieClip("Labels", 2);
		//Get font
		var trendFontObj:Object = this.styleM.getTextStyle(this.objects.VTRENDVALUES);
		//Delegate function for tool-text for trendlines
		var fnRollOver:Function;
		//Reference pointers
		var trendLineMC:MovieClip, trendTextMC:MovieClip;
		var dcount:Number, startIndex:Number, endIndex:Number, isTrendZone:Boolean;
		var trendValueObj:Object;
		//Loop variable
		var i:Number;
		//Boolean value to determine trend zone status changed due to negotiable width.
		var trendZoneStatChange:Boolean;
		for (i=1; i<=this.numVTrendLines; i++) {
			//Get indexes
			startIndex = this.vTrendLines[i].startIndex;
			endIndex = this.vTrendLines[i].endIndex;
			//Make sure that startIndex is lower one
			startIndex = Math.min(startIndex, endIndex);
			endIndex = Math.max(startIndex, endIndex);
			//Check if it's a trend zone
			if (startIndex!=endIndex){
				isTrendZone = true;
			}else{
				isTrendZone = false;
			}
			//If it's a trendzone extending beyond the current subset, set current subset index
			if (subsetStartIndex>startIndex && subsetEndIndex<endIndex){
				startIndex = subsetStartIndex;
				endIndex = subsetEndIndex;
			}
			//If it's a trendzone having a part of it visible, adjust indexes accordingly			
			if (startIndex<subsetStartIndex && endIndex>=subsetStartIndex && endIndex<=subsetEndIndex){
				startIndex = subsetStartIndex;
			}
			if (startIndex>=subsetStartIndex && startIndex<=subsetEndIndex && endIndex>subsetEndIndex){
				endIndex = subsetEndIndex;
			}
			//Find concrete for value "Display when subset index count"
			var dcount:Number = this.vTrendLines[i].displayWhenCount;			
			//If the trend index is out of range
			if (isNaN(startIndex) || (startIndex<subsetStartIndex) || isNaN(endIndex) || (endIndex>subsetEndIndex)) {
				//Set invalid
				this.vTrendLines[i].isValid = false;
			} else {
				//If it's not a trend zone and falls on first/last item, set valid as false
				if (!isTrendZone && (startIndex==subsetStartIndex || endIndex==subsetEndIndex)){
					this.vTrendLines[i].isValid = false;
				}else{
					//If it's not to be shown always, check if proximity range is applicable
					//If displayWhenCount is set to -1, set to current subset items
					if (dcount==-1){
						dcount = subsetEndIndex-subsetStartIndex+1;
					}
					if (this.vTrendLines[i].displayAlways==true || (dcount>=(subsetEndIndex-subsetStartIndex+1))){
						this.vTrendLines[i].isValid = true;
					}else{
						//Set invalid for this draw
						this.vTrendLines[i].isValid = false;
					}
				}
			}
			//We proceed only if the trend line is valid  
			if (this.vTrendLines[i].isValid == true) {
				//Calculate and store x-positions
				this.vTrendLines[i].x = getXPos(startIndex);
				//If trend zone
				if (isTrendZone) {
					//Reset alpha
					if (this.vTrendLines[i].alpha==99){
						this.vTrendLines[i].alpha = 40;
					}
					//Calculate x for end index
					this.vTrendLines[i].toX = getXPos(endIndex);
					//For textbox x position, we need mid value.
					this.vTrendLines[i].tbX = Math.min(this.vTrendLines[i].x, this.vTrendLines[i].toX)+(Math.abs(this.vTrendLines[i].x-this.vTrendLines[i].toX)/2);
					//Width
					this.vTrendLines[i].w = (this.vTrendLines[i].toX-this.vTrendLines[i].x);
				} else {
					//Just copy
					this.vTrendLines[i].toX = this.vTrendLines[i].x;
					//Set same position for value tb
					this.vTrendLines[i].tbX = this.vTrendLines[i].x;
					//Width
					this.vTrendLines[i].w = 0;
				}
				//Set trend zone change status to false
				trendZoneStatChange = false;
				//When trendzone width is less than 0.3 the zone is not visible. So need to display the single trend line.
				//At that situation isTrendZone needs to make false.
				if(isTrendZone && Math.abs(this.vTrendLines [i].w) < 0.3){
					isTrendZone = false;
					trendZoneStatChange = true;
				}
				//Draw it.
				//If it's a valid trend line
				//Get the depth and update counters
				if (this.vTrendLines[i].showOnTop) {
					trendLineMC = vTrendAboveLineMC;
					trendTextMC = vTrendAboveTextMC;
				} else {
					trendLineMC = vTrendBelowLineMC;
					trendTextMC = vTrendBelowTextMC;
				}
				//Now, draw the line or trend zone
				if (isTrendZone) {
					//Create rectangle
					trendLineMC.lineStyle();
					//Absolute width value
					this.vTrendLines[i].w = Math.abs(this.vTrendLines[i].w);
					trendLineMC.moveTo(this.vTrendLines[i].x, 0);
					trendLineMC.beginFill(parseInt(this.vTrendLines[i].color, 16), this.vTrendLines[i].alpha);
					trendLineMC.lineTo(this.vTrendLines[i].toX, 0);
					trendLineMC.lineTo(this.vTrendLines[i].toX, canvasHeight);
					trendLineMC.lineTo(this.vTrendLines[i].x, canvasHeight);
					trendLineMC.lineTo(this.vTrendLines[i].x, 0);
					trendLineMC.endFill();
				} else {
					//When trend zone status is changed draw a hair line instead of using trendline thickness.
					this.vTrendLines[i].thickness = (trendZoneStatChange)? 0 : this.vTrendLines[i].thickness;
					//Just draw line  
					trendLineMC.lineStyle(this.vTrendLines[i].thickness, parseInt(this.vTrendLines[i].color, 16), this.vTrendLines[i].alpha);
					//Now, if dashed line is to be drawn
					if (!this.vTrendLines[i].isDashed) {
						//Draw normal line line keeping 0,0 as registration point
						trendLineMC.moveTo(this.vTrendLines[i].x, 0);
						trendLineMC.lineTo(this.vTrendLines[i].toX, canvasHeight);
					} else {
						//Dashed Line line
						DrawingExt.dashTo(trendLineMC, this.vTrendLines[i].x, 0, this.vTrendLines[i].toX, canvasHeight, this.vTrendLines[i].dashLen, this.vTrendLines[i].dashGap);
					}
				}
				//Set color  
				trendFontObj.color = this.vTrendLines[i].color;
				//If it's a trend line add border and background color
				if (!isTrendZone){
					trendFontObj.borderColor = this.vTrendLines[i].color;
					trendFontObj.bgColor = this.params.canvasBgColor;
				}else{
					trendFontObj.borderColor = "";
					trendFontObj.bgColor = "";
				}
				//Now, render the trend line value, based on its position
				if (this.vTrendLines[i].valueOnTop == false) {
					//Value to be placed at bottom
					trendFontObj.vAlign = "top";
					//Create text
					trendValueObj = createText(false, this.vTrendLines[i].displayValue, trendTextMC, i, this.vTrendLines[i].tbX, canvasHeight, 0, trendFontObj, false, 0, 0);
				} else {
					//Left side
					trendFontObj.vAlign = "bottom";
					//Create text
					trendValueObj = createText(false, this.vTrendLines[i].displayValue, trendTextMC, i, this.vTrendLines[i].tbX, 0, 0, trendFontObj, false, 0, 0);
				}
				//If it's a trendzone and value is not being able to fit in, hide
				if (isTrendZone && trendValueObj.width>this.vTrendLines[i].w){
					trendValueObj.tf._visible = false;
				}
			}			
		}
		
		//Apply filters
		this.styleM.applyFilters(vTrendAboveMC, this.objects.VTRENDLINES);
		this.styleM.applyFilters(vTrendBelowMC, this.objects.VTRENDLINES);
		this.styleM.applyFilters(vTrendBelowTextMC, this.objects.VTRENDVALUES);
		this.styleM.applyFilters(vTrendAboveTextMC, this.objects.VTRENDVALUES);
	}
	/**
	 * Draws all the required mouse icons
	*/
	private function drawMouseCursors() {
		if (this.params.enableIconMouseCursors){
			//Set the colors		
			var iconDarkColor:Number = parseInt(this.params.mouseCursorColor, 16);
			var iconLightColor:Number = ColorExt.getLightColor(this.params.mouseCursorColor, 0.15);
			//Draw drag icon
			mouseDragCursor.lineStyle(2, iconDarkColor, 100);
			mouseDragCursor.moveTo(5, 0);
			mouseDragCursor.lineTo(5, 12);
			mouseDragCursor.moveTo(5, 3);
			mouseDragCursor.lineTo(0, 6);
			mouseDragCursor.lineTo(5, 9);
			mouseDragCursor.moveTo(9, 0);
			mouseDragCursor.lineTo(9, 12);
			mouseDragCursor.moveTo(9, 3);
			mouseDragCursor.lineTo(14, 6);
			mouseDragCursor.lineTo(9, 9);
			//Set X and Y Position at center
			mouseDragCursor._x = -(mouseDragCursor._width)/2;
			mouseDragCursor._y = -(mouseDragCursor._height)/2;
			// ------ Zoom icon ----------//
			mouseZoomCursor.lineStyle(2, iconDarkColor, 100);
			mouseZoomCursor.beginFill(iconLightColor, 100);
			DrawingExt.drawCircle(mouseZoomCursor, 6, 6, 6, 6, 0, 360);
			mouseZoomCursor.endFill();
			mouseZoomCursor.moveTo(11, 11);
			mouseZoomCursor.lineStyle(3, iconDarkColor, 100);
			mouseZoomCursor.lineTo(16, 16);
			mouseZoomCursor.lineStyle(2, iconDarkColor, 100);
			mouseZoomCursor.moveTo(16, 6);
			mouseZoomCursor.lineTo(22, 6);
			mouseZoomCursor.moveTo(19, 3);
			mouseZoomCursor.lineTo(19, 9);
			//Set X and Y Position at center
			mouseZoomCursor._x = -(mouseZoomCursor._width)/2;
			mouseZoomCursor._y = -(mouseZoomCursor._height)/2;
			//------- Draw pin icon ---------//	
			mousePinCursor.lineStyle(2, iconDarkColor, 100);
			mousePinCursor.beginFill(iconLightColor, 100);
			DrawingExt.drawCircle(mousePinCursor, 10, 10, 6, 6, 0, 360);
			mousePinCursor.endFill();
			mousePinCursor.lineStyle(2, iconDarkColor, 100);
			mousePinCursor.moveTo(10, 0);
			mousePinCursor.lineTo(10, 20);
			mousePinCursor.moveTo(0, 10);
			mousePinCursor.lineTo(20, 10);
			//Set X and Y Position at center
			mousePinCursor._x = -(mousePinCursor._width)/2;
			mousePinCursor._y = -(mousePinCursor._height)/2;
		}
	}
	/**
	* Sets the mouse cursor to specified type (normal, drag, zoom, pin)
	*/
	private function setMouseCursor(type:String):Void {
		if (this.params.enableIconMouseCursors){
			//Convert to lower case for case insensitive operation
			type = type.toLowerCase();
			//Delete any existing this.cMC.onMouseMove
			delete this.cMC.onMouseMove;
			//If type is normal, remove any existing cursors
			if (type == "normal") {
				//Hide any other cursor
				mouseDragCursor._visible = false;
				mouseZoomCursor._visible = false;
				mousePinCursor._visible = false;
				//Show normal cursor
				Mouse.show();
				//Return
				return;
			} else {
				//Select the movie clip to be shown
				if (type == "pin") {
					mouseDragCursor._visible = false;
					mouseZoomCursor._visible = false;
					mousePinCursor._visible = true;
				} else if (type == "drag") {
					mouseDragCursor._visible = true;
					mouseZoomCursor._visible = false;
					mousePinCursor._visible = false;
				} else if (type == "zoom") {
					mouseDragCursor._visible = false;
					mouseZoomCursor._visible = true;
					mousePinCursor._visible = false;
				}
				//Hide normal mouse          
				Mouse.hide();
				//Store local references
				var mouseCursors:MovieClip = this.mouseCursors;
				var cMC:MovieClip = this.cMC;
				//Move the cursor along with mouse
				this.cMC.onMouseMove = function() {
					mouseCursors._x = cMC._xmouse;
					mouseCursors._y = cMC._ymouse;
					updateAfterEvent();
				};
			}
		}
	}
	/**
	 * Draws the scroll bar for the chart
	*/
	private function drawScrollBar():Void {
		//Delete existing object
		if (scrollBar != undefined) {
			scrollBar.destroy();
		}
		//Create container movie clip            
		scrollMC = this.cMC.createEmptyMovieClip("Scroll", this.dm.getDepth("SCROLLBAR"));
		//Set position of container movie clip
		scrollMC._x = canvasX;
		//If the canvas border is thick, adjust the scroll position
		if (this.params.canvasBorderThickness>1) {
			scrollMC._x -= (this.params.canvasBorderThickness/2);
		}
		scrollMC._y = canvasY+canvasHeight+this.params.scrollPadding;
		//Set filters
		this.styleM.applyFilters(scrollMC, this.objects.SCROLLBAR);
		//Create it
		scrollBar = new HorIndexScrollBar(scrollMC);
		scrollBar.setSize(canvasWidth+this.params.canvasBorderThickness/2, this.params.scrollHeight);
		scrollBar.setButtonDimensions(this.params.scrollBtnWidth, this.params.scrollBtnPadding);
		scrollBar.setColor(this.params.scrollColor);
		scrollBar.setTotalItemsCount(numItems);
		//Update other scroll properties
		updateScrollProps();
		//Reference to class
		var classRef = this;
		//Set the listener for scroll bar
		var objL:Object = new Object();
		objL.change = function(e:Object) {
			//When scroll bar position is changed, change the display subset
			classRef.resetIndexes(e.start, e.end);
		};
		scrollBar.addEventListener("change", objL);
	}
	/**
	 * Updates the scroll bar to reflect the current subset being displayed.
	*/
	private function updateScrollProps():Void {
		scrollBar.setScrollStep(subsetStep);
		scrollBar.setPaneItemsCount(scrollPaneSize);
		scrollBar.setStartIndex(subsetStartIndex);
		scrollBar.draw();
	}
	/**
	 * Draws the zoom out action handler, if required
	*/
	function drawToolbar():Void {
		//The toolbar consists of 3 buttons in the order (L to R): "Pin Mode: On", "Show All", "Zoom Out"
		//All the buttons are visible always. They just change state from enabled to disabled.
		btnZoomOut = toolbarMC.createEmptyMovieClip("ZoomOut", 1);
		btnShowAll = toolbarMC.createEmptyMovieClip("ShowAll", 2);
		btnPinMode = toolbarMC.createEmptyMovieClip("PinMode", 3);
		//Get required text style for the object
		var buttonEnabledTextStyle = this.styleM.getTextStyle(this.objects.ENABLEDBUTTON);
		var buttonDisabledTextStyle = this.styleM.getTextStyle(this.objects.DISABLEDBUTTON);
		//Calculate the maximum width required for the text of each of these buttons.
		var btnZoomOutTxtWidth:Number = createText(false, this.params.btnZoomOutTitle, this.tfTestMC, 1, testTFX, testTFY, null, buttonEnabledTextStyle, false, 0, 0).width;
		var btnShowAllTxtWidth:Number = createText(false, this.params.btnResetChartTitle, this.tfTestMC, 1, testTFX, testTFY, null, buttonEnabledTextStyle, false, 0, 0).width;
		var btnPinModeTxtWidth:Number = createText(false, this.params.btnSwitchToZoomModeTitle, this.tfTestMC, 1, testTFX, testTFY, null, buttonEnabledTextStyle, false, 0, 0).width;
		//Get height required for 1 line of text
		var maxTextHeight:Number = createText(false, "Pin Mode: Off|Zoom Out|Show All", this.tfTestMC, 1, testTFX, testTFY, null, buttonEnabledTextStyle, false, 0, 0).height;
		//Each of these movie clips will have 2 modes - enabled and disabled.
		var btnZoomOutEnabled:MovieClip = btnZoomOut.createEmptyMovieClip("Enabled", 1);
		var btnZoomOutDisabled:MovieClip = btnZoomOut.createEmptyMovieClip("Disabled", 2);
		var btnShowAllEnabled:MovieClip = btnShowAll.createEmptyMovieClip("Enabled", 1);
		var btnShowAllDisabled:MovieClip = btnShowAll.createEmptyMovieClip("Disabled", 2);
		var btnPinModeEnabled:MovieClip = btnPinMode.createEmptyMovieClip("Enabled", 1);
		var btnPinModeDisabled:MovieClip = btnPinMode.createEmptyMovieClip("Disabled", 2);
		//Based on padding, margin and text width, calculate the width and start X position
		//of each button
		var btnZoomOutWidth:Number = btnZoomOutTxtWidth+2*this.params.toolBarBtnTextHMargin;
		var btnShowAllWidth:Number = btnShowAllTxtWidth+2*this.params.toolBarBtnTextHMargin;
		var btnPinModeWidth:Number = btnPinModeTxtWidth+2*this.params.toolBarBtnTextHMargin;
		var btnZoomOutEndX:Number = canvasX+canvasWidth;
		var btnZoomOutStartX:Number = btnZoomOutEndX-btnZoomOutWidth;
		var btnShowAllEndX:Number = btnZoomOutStartX-this.params.toolBarBtnHPadding;
		var btnShowAllStartX:Number = btnShowAllEndX-btnShowAllWidth;
		var btnPinModeEndX:Number = btnShowAllStartX-this.params.toolBarBtnHPadding-5;
		var btnPinModeStartX:Number = btnPinModeEndX-btnPinModeWidth-5;
		//Button y position
		var btnYPos:Number = canvasY-this.params.toolBarBtnVPadding;
		//Text starting y position
		var txtYPos:Number = btnYPos-(this.params.toolBarBtnTextVMargin/2);
		//Derive colors
		var toolbarButtonBgColor:Number = ColorExt.getLightColor(this.params.toolbarButtonColor, 0.1);
		var toolbarButtonEnabledBorderColor:Number = parseInt(this.params.toolbarButtonColor, 16);
		var toolbarButtonDisabledBorderColor:Number = ColorExt.getLightColor(this.params.toolbarButtonColor, 0.3);
		//Create the buttons - both states
		//Zoom out button - enabled state
		btnZoomOutEnabled.lineStyle(0, toolbarButtonEnabledBorderColor, 100);
		btnZoomOutEnabled.beginFill(toolbarButtonBgColor, 100);
		drawRectangle(btnZoomOutEnabled, btnZoomOutStartX, btnYPos-maxTextHeight-this.params.toolBarBtnTextVMargin, btnZoomOutWidth, maxTextHeight+this.params.toolBarBtnTextVMargin);
		btnZoomOutEnabled.endFill();
		createText(false, this.params.btnZoomOutTitle, btnZoomOutEnabled, 2, btnZoomOutStartX+btnZoomOutWidth/2, txtYPos, null, buttonEnabledTextStyle, false, 0, 0);
		//Zoom out button - disabled state
		btnZoomOutDisabled.lineStyle(0, toolbarButtonDisabledBorderColor, 100);
		btnZoomOutDisabled.beginFill(toolbarButtonBgColor, 100);
		drawRectangle(btnZoomOutDisabled, btnZoomOutStartX, btnYPos-maxTextHeight-this.params.toolBarBtnTextVMargin, btnZoomOutWidth, maxTextHeight+this.params.toolBarBtnTextVMargin);
		btnZoomOutDisabled.endFill();
		createText(false, this.params.btnZoomOutTitle, btnZoomOutDisabled, 2, btnZoomOutStartX+btnZoomOutWidth/2, txtYPos, null, buttonDisabledTextStyle, false, 0, 0);
		//Show all button - enabled state
		btnShowAllEnabled.lineStyle(0, toolbarButtonEnabledBorderColor, 100);
		btnShowAllEnabled.beginFill(toolbarButtonBgColor, 100);
		drawRectangle(btnShowAllEnabled, btnShowAllStartX, btnYPos-maxTextHeight-this.params.toolBarBtnTextVMargin, btnShowAllWidth, maxTextHeight+this.params.toolBarBtnTextVMargin);
		btnShowAllEnabled.endFill();
		createText(false, this.params.btnResetChartTitle, btnShowAllEnabled, 2, btnShowAllStartX+btnShowAllWidth/2, txtYPos, null, buttonEnabledTextStyle, false, 0, 0);
		//Show all button - disabled state
		btnShowAllDisabled.lineStyle(0, toolbarButtonDisabledBorderColor, 100);
		btnShowAllDisabled.beginFill(toolbarButtonBgColor, 100);
		drawRectangle(btnShowAllDisabled, btnShowAllStartX, btnYPos-maxTextHeight-this.params.toolBarBtnTextVMargin, btnShowAllWidth, maxTextHeight+this.params.toolBarBtnTextVMargin);
		btnZoomOutDisabled.endFill();
		createText(false, this.params.btnResetChartTitle, btnShowAllDisabled, 2, btnShowAllStartX+btnShowAllWidth/2, txtYPos, null, buttonDisabledTextStyle, false, 0, 0);
		if (this.params.allowPinMode) {
			//Pin Mode button - enabled state
			btnPinModeEnabled.lineStyle(0, toolbarButtonEnabledBorderColor, 100);
			btnPinModeEnabled.beginFill(toolbarButtonBgColor, 100);
			drawRectangle(btnPinModeEnabled, btnPinModeStartX, btnYPos-maxTextHeight-this.params.toolBarBtnTextVMargin, btnPinModeWidth, maxTextHeight+this.params.toolBarBtnTextVMargin);
			btnPinModeEnabled.endFill();
			createText(false, this.params.btnSwitchToZoomModeTitle, btnPinModeEnabled, 2, btnPinModeStartX+btnPinModeWidth/2, txtYPos, null, buttonEnabledTextStyle, false, 0, 0);
			//Pin Mode button - disabled state. The disabled state of pin mode button still looks enabled.
			btnPinModeDisabled.lineStyle(0, toolbarButtonEnabledBorderColor, 100);
			btnPinModeDisabled.beginFill(toolbarButtonBgColor, 100);
			drawRectangle(btnPinModeDisabled, btnPinModeStartX, btnYPos-maxTextHeight-this.params.toolBarBtnTextVMargin, btnPinModeWidth, maxTextHeight+this.params.toolBarBtnTextVMargin);
			btnPinModeDisabled.endFill();
			createText(false, this.params.btnSwitchToPinModeTitle, btnPinModeDisabled, 2, btnPinModeStartX+btnPinModeWidth/2, txtYPos, null, buttonEnabledTextStyle, false, 0, 0);
		}
		//Reference to class   
		var classRef = this;
		// ---------------- Begin: Set tool tips ---------------- //		
		if (this.params.showToolBarButtonTooltext) {
			var fnZoomOutRollOver:Function;
			//Create Delegate for roll over function showToolTip
			fnZoomOutRollOver = Delegate.create(this, showToolTip);
			//Set the tool text
			fnZoomOutRollOver.toolText = this.params.btnZoomOutTooltext;
			//Assing the delegates to movie clip handler
			btnZoomOut.onRollOver = fnZoomOutRollOver;
			//Set roll out and mouse move too.
			btnZoomOut.onRollOut = btnZoomOut.onReleaseOutside=Delegate.create(this, hideToolTip);
			btnZoomOut.onMouseMove = Delegate.create(this, repositionToolTip);
			//Set for reset chart button
			var fnResetRollOver:Function;
			//Create Delegate for roll over function showToolTip
			fnResetRollOver = Delegate.create(this, showToolTip);
			//Set the tool text
			fnResetRollOver.toolText = this.params.btnResetChartTooltext;
			//Assing the delegates to movie clip handler
			btnShowAll.onRollOver = fnResetRollOver;
			//Set roll out and mouse move too.
			btnShowAll.onRollOut = btnShowAll.onReleaseOutside=Delegate.create(this, hideToolTip);
			btnShowAll.onMouseMove = Delegate.create(this, repositionToolTip);
			//Set for pin button
			btnPinMode.onRollOver = Delegate.create(this, btnPinModeRollover);
			btnPinMode.onMouseMove = Delegate.create(this, repositionToolTip);
			btnPinMode.onRollOut = btnPinMode.onReleaseOutside=Delegate.create(this, hideToolTip);
		}
		// ---------------- Begin: Set tool tips ---------------- // 
		// ---------------- Begin: Set click handlers ---------------- //
		btnZoomOut.onRelease = function() {
			//Zoom out (based on history). 
			//Pop and ignore the last value, as it contains index of the current view
			classRef.zoomHistory.pop();
			//If no items are in history then zoom out to all items
			if (classRef.zoomHistory.length == 0) {
				classRef.resetIndexes(1, classRef.numItems);
			} else {
				//Get access to index of previous zoom view
				var lastSet:Object = classRef.zoomHistory[classRef.zoomHistory.length-1];
				//Set the view for the same
				classRef.resetIndexes(lastSet.start, lastSet.end);
			}
			//Hide the tool-tip
			classRef.hideToolTip();
			//Raise the external event
			if (classRef.registerWithJS==true &&  ExternalInterface.available){
				ExternalInterface.call("FC_ZoomedOut", classRef.DOMId);
				ExternalInterface.call("__fusioncharts_event", {type:"zoomedout", sender:classRef.DOMId}, {eventObject:"toolbar"});
			}
		};
		btnShowAll.onRelease = function() {
			//Clear the zoom history
			classRef.zoomHistory.splice(0, classRef.zoomHistory.length);
			//Show all items
			classRef.resetIndexes(1, classRef.numItems);
			//Hide the tool-tip
			classRef.hideToolTip();
			//Raise the external event
			if (classRef.registerWithJS==true &&  ExternalInterface.available){
				ExternalInterface.call("FC_ResetZoomChart", classRef.DOMId);
				ExternalInterface.call("__fusioncharts_event", {type:"resetzoomchart", sender:classRef.DOMId}, {eventObject:"toolbar"});
			}
		};
		if (this.params.allowPinMode) {
			btnPinMode.onRelease = function() {
				classRef.switchMode(!classRef.zoomMode);
				//Hide the tool-tip
				classRef.hideToolTip();
			};
		}
	}
	/**
	* drawLegend method renders the legend
	*/
	private function drawLegend():Void {
		if (this.params.showLegend) {
			this.lgnd.render();
			//If it's interactive legend, listen to events
			if (this.params.interactiveLegend) {
				this.lgnd.addEventListener("legendClick", this);
			}
			//Apply filter          
			this.styleM.applyFilters(lgndMC, this.objects.LEGEND);
		}
		//Clear interval          
		clearInterval(this.config.intervals.legend);
	}
	/**
	 * Sets the state of buttons based on various parameters.
	*/
	private function setButtonState():Void {
		//If the chart is in zoom mode.
		if (zoomMode == true) {
			//Set the pin mode to off
			btnPinMode["Disabled"]._visible = true;
			btnPinMode["Enabled"]._visible = false;
			//Based on zoomHistory, set state of other 2 buttons
			if (zoomHistory.length>=1) {
				btnZoomOut.enabled = true;
				btnZoomOut["Enabled"]._visible = true;
				btnZoomOut["Disabled"]._visible = false;
				btnShowAll.enabled = true;
				btnShowAll["Enabled"]._visible = true;
				btnShowAll["Disabled"]._visible = false;
			} else {
				btnZoomOut.enabled = false;
				btnZoomOut["Enabled"]._visible = false;
				btnZoomOut["Disabled"]._visible = true;
				btnShowAll.enabled = false;
				btnShowAll["Enabled"]._visible = false;
				btnShowAll["Disabled"]._visible = true;
			}
		} else {
			//Set the pin mode to on
			btnPinMode["Disabled"]._visible = false;
			btnPinMode["Enabled"]._visible = true;
			//Hide show all and zoom out buttons
			btnZoomOut.enabled = false;
			btnZoomOut["Enabled"]._visible = false;
			btnZoomOut["Disabled"]._visible = true;
			btnShowAll.enabled = false;
			btnShowAll["Enabled"]._visible = false;
			btnShowAll["Disabled"]._visible = true;
		}
	}
	/**
	 * Sets the state of context menu items, based on chart state.
	*/
	private function setContextMenuState():Void {
		if (zoomMode == true) {
			//Set zoom/pin context menu
			if (this.params.allowPinMode) {
				zoomModeCMI.enabled = false;
				pinModeCMI.enabled = true;
			}
			//Based on zoomHistory, set state of other 2 buttons 
			if (zoomHistory.length>=1) {
				//Enable zoom related buttons
				zoomOutCMI.enabled = true;
				resetChartCMI.enabled = true;
			} else {
				//Disable zoom related buttons
				zoomOutCMI.enabled = false;
				resetChartCMI.enabled = false;
			}
		} else {
			//Disable zoom related buttons
			zoomOutCMI.enabled = false;
			resetChartCMI.enabled = false;
			//Set zoom/pin context menu
			if (this.params.allowPinMode) {
				zoomModeCMI.enabled = true;
				pinModeCMI.enabled = false;
			}
		}
	}
	/**
	 * Draws all the items related to subset.
	*/
	private function drawSubset():Void {
		drawChart();
		drawValues();
		//If y-axis is dynamic, redraw the axis, values and grid
		if (this.params.dynamicAxis == true) {
			drawYAxisValues();
			if (this.numTrendLines>0) {
				drawTrendLines();
			}
		}
		//Draw vertical trend lines
		if (this.numVTrendLines>0){
			drawVTrendLines();
		}
		if (this.params.showLabels) {
			//Draw x-axis labels      
			drawXAxisLabels();
		}
		//Set the button state   
		setButtonState();
		//Set context menu state
		setContextMenuState();
	}
	/**
	 * Draws the x-axis labels
	*/
	function drawXAxisLabels():Void {
		//Remove movie clip, if already present
		if (xAxisLabelsMC != undefined) {
			xAxisLabelsMC.removeMovieClip();
		}
		//Re-create the movie clip           
		xAxisLabelsMC = this.cMC.createEmptyMovieClip("XAXisLabels", this.dm.getDepth("XAXISLABELS"));
		//Create sub movie clips to render text fields and line separately
		var xAxisLabelsTFMC:MovieClip = xAxisLabelsMC.createEmptyMovieClip("Labels", 1);
		var xAxisLabelsLineMC:MovieClip = xAxisLabelsMC.createEmptyMovieClip("Lines", 2);
		//Set line style for vertical lines
		xAxisLabelsLineMC.lineStyle(this.params.vDivLineThickness, parseInt(this.params.vDivLineColor, 16), this.params.vDivLineAlpha);
		//Object to store label
		var labelObj:Object;
		//Label container
		var strLabel:String;
		//Label text style
		var labelStyleObj:Object = this.styleM.getTextStyle(this.objects.DATALABELS);
		//----- Calculations --------//
		//Calculate the number of label slots, and step accordingly
		var labelSlots:Number = Math.min(this.config.labelSlots, subsetItems);
		var labelStep:Number = Math.ceil(subsetItems/labelSlots);
		//Y position for labels
		var labelY:Number;
		labelY = canvasY+canvasHeight+this.params.scrollHeight+this.params.scrollPadding+this.params.labelPadding;
		//---- Begin: Stagger related configuration ----//
		//Y Shift for labels (for stagger)
		var labelYShift:Number;
		//Counters for stagger rendering
		var staggerCycle:Number = 0;
		var staggerAddFn:Number = 1;
		//---- End: Stagger related configuration ----//
		//Count of labels, and index of last label that was shown
		var count:Number = 0, lastShowIndex:Number = 0;
		var globalIndex:Number;
		//Alignment position for labels
		var align:String = "center";
		//Iterate through all labels in this subset
		var i:Number;
		for (i=1; i<=subsetItems; i=i+labelStep) {
			//Get global index
			globalIndex = localToGlobalIndex(i);
			//Store the label
			strLabel = labels[globalIndex];
			//Proceed only if the label is not empty
			if (strLabel != "") {
				//Increment count
				count++;
				//Default to normal wrap width and height
				var labelWidth:Number = (canvasWidth/labelSlots);
				var labelHeight:Number = this.config.labelAreaHeight;
				//If it's the last label to be shown, then check for overflow
				//Also, do not perform this if there is only 1 label to show
				if (numItems>1 && ((i-1)/labelStep)==Math.floor((subsetItems-1))/labelStep) {
					//If in wrap mode, check width
					if (this.params.labelDisplay == "WRAP") {
						if ((canvasX+subsetX[1][i]+labelWidth)>(this.width-this.params.chartRightMargin)) {
							//Allot max available space available between this and previous label.							
							labelWidth = (subsetX[1][i]-subsetX[1][lastShowIndex])/2;
							//Set alignment to right
							align = "right";
						}
					}
				}
				//Set index of last shown label         
				lastShowIndex = i;
				//Now, render them
				if (this.params.labelDisplay == "ROTATE") {
					labelStyleObj.align = "center";
					labelStyleObj.vAlign = "bottom";
					//Create text box and get height
					labelObj = createText(false, strLabel, xAxisLabelsTFMC, i, canvasX+subsetX[1][i], labelY, this.config.labelAngle, labelStyleObj, true, labelHeight, labelWidth);
				} else if (this.params.labelDisplay == "WRAP") {
					//Case 2 (WRAP)
					//Set alignment
					labelStyleObj.align = align;
					labelStyleObj.vAlign = "bottom";
					labelObj = createText(false, strLabel, xAxisLabelsTFMC, i, canvasX+subsetX[1][i], labelY, 0, labelStyleObj, true, labelWidth, labelHeight);
				} else if (this.params.labelDisplay == "STAGGER") {
					//Case 3 (Stagger)
					//Label width can be multiplied by number of lines (as they're distributed)
					labelWidth = labelWidth*this.params.staggerLines;
					//Set alignment
					labelStyleObj.align = "center";
					labelStyleObj.vAlign = "bottom";
					//Need to get cyclic position for staggered textboxes
					//Matrix formed is of 2*this.params.staggerLines - 2 rows
					var pos:Number = count%(2*this.params.staggerLines-2);
					//Last element needs to be reset
					pos = (pos == 0) ? (2*this.params.staggerLines-2) : pos;
					//Cyclic iteration
					pos = (pos>this.params.staggerLines) ? (this.params.staggerLines-(pos%this.params.staggerLines)) : pos;
					//Get position to 0 base
					pos--;
					//Shift accordingly
					var labelYShift:Number = this.config.maxLabelHeight*pos;
					labelObj = createText(false, strLabel, xAxisLabelsTFMC, i, canvasX+subsetX[1][i], labelY+labelYShift, 0, labelStyleObj, true, Math.max(this.config.labelWrapMinWidth, labelWidth), this.config.labelSingleLineHeight);
				} else {
					//Render normal label
					labelStyleObj.align = "center";
					labelStyleObj.vAlign = "bottom";
					labelObj = createText(false, strLabel, xAxisLabelsTFMC, i, canvasX+subsetX[1][i], labelY, 0, labelStyleObj, true, Math.max(this.config.labelWrapMinWidth, labelWidth), this.config.labelSingleLineHeight);
				}
				//Draw the v-div line
				if (this.params.showVDivLines) {
					//Draw the line
					if (this.params.vDivLineIsDashed) {
						//If it's a dashed lined
						DrawingExt.dashTo(xAxisLabelsLineMC, canvasX+subsetX[1][i], canvasY, canvasX+subsetX[1][i], canvasY+canvasHeight, this.params.vDivLineDashLen, this.params.vDivLineDashGap);
					} else {
						//Else, plot normal line
						xAxisLabelsLineMC.moveTo(canvasX+subsetX[1][i], canvasY);
						xAxisLabelsLineMC.lineTo(canvasX+subsetX[1][i], canvasY+canvasHeight);
					}
				}
			}
		}
		//Apply style to entire text movie clip
		this.styleM.applyFilters(xAxisLabelsTFMC, this.objects.DATALABELS);
	}
	/**
	 * Draws the chart.
	*/
	private function drawChart():Void {
		//Iterate through all indexes in primary data model and 	
		var i:Number, j:Number, k:Number;
		var dsMC:MovieClip;
		//Iterate through all the datasets
		//Draw the lines first
		for (i=1; i<=numDS; i++) {
			//Get reference to series movie clip
			dsMC = chartMC["Series_"+i];
			//Clear any existing drawing
			dsMC.clear();
			//Set the line style for chart drawing
			dsMC.lineStyle(this.dataset[i].lineThickness, parseInt(this.dataset[i].color, 16), this.dataset[i].alpha);
			//Find the index of the first defined data
			//Initialize with (subsetItems+1) so that if no defined data is found,
			//next loop automatically terminates
			var firstIndex:Number = subsetItems+1;
			//Storage container for next plot index
			var nxt:Number;
			for (j=1; j<subsetItems; j++) {
				if (!isNaN(data[i][subsetIndex[i][j]])) {
					firstIndex = j;
					break;
				}
			}
			//Now, we draw the lines inside chart
			for (j=firstIndex; j<subsetItems; j++) {
				//We continue only if this data is defined
				if (!isNaN(data[i][subsetIndex[i][j]])) {
					//Get next Index
					nxt = j+1;
					//Now, if next index is not defined, we can have two cases:
					//1. Draw gap between this data and next data.
					//2. Connect the next index which could be found as defined.
					//Case 1. If connectNullData is set to false and next data is not
					//defined. We simply continue to next value of the loop
					if (this.params.connectNullData == false && isNaN(data[i][subsetIndex[i][nxt]])) {
						//Discontinuous plot. So ignore and move to next.
						continue;
					}
					//Now, if nxt data is undefined, we need to find the index of the post data        
					//which is not undefined
					if (isNaN(data[i][subsetIndex[i][nxt]])) {
						//Initiate nxt as -1, so that we can later break if no next defined data
						//could be found.
						nxt = -1;
						for (k=j+1; k<=subsetItems; k++) {
							if (!isNaN(data[i][subsetIndex[i][k]])) {
								nxt = k;
								break;
							}
						}
						//If nxt is still -1, we break
						if (nxt == -1) {
							break;
						}
					}
					//Move to the point        
					dsMC.moveTo(subsetX[i][j], subsetY[i][j]);
					//Draw point to next line
					dsMC.lineTo(subsetX[i][nxt], subsetY[i][nxt]);
					//Update loop index (required when connectNullData is true and there is
					//a sequence of empty sets.) Since we've already found the "next" defined
					//data, we update loop to that to optimize.
					j = nxt-1;
				}
			}
		}
		//Iterate through the datasets and draw anchors
		for (i=1; i<=numDS; i++) {
			//If anchor is to be drawn and space is available
			if (this.dataset[i].drawAnchors && ipdistance>=this.params.anchorMinRenderDistance) {
				//Get reference to series movie clip
				dsMC = chartMC["Series_"+i];
				//Iterate through each item
				for (j=1; j<=subsetItems; j++) {
					//Check if item is a number
					if (!isNaN(data[i][subsetIndex[i][j]])) {
						//For proper fill, move to the center position
						dsMC.moveTo(subsetX[i][j], subsetY[i][j]);
						dsMC.lineStyle(this.dataset[i].anchorBorderThickness, parseInt(this.dataset[i].anchorBorderColor, 16), 100);
						dsMC.beginFill(parseInt(this.dataset[i].anchorBgColor, 16), this.dataset[i].anchorBgAlpha);
						DrawingExt.drawCircle(dsMC, subsetX[i][j], subsetY[i][j], this.dataset[i].anchorRadius, this.dataset[i].anchorRadius, 0, 360);
						dsMC.endFill();
					}
				}
			}
		}
	}
	/**
	 * Draws the values on charts
	*/
	private function drawValues():Void {
		//Iterate through all indexes in primary data model 
		var i:Number, j:Number, k:Number;
		var dvMC:MovieClip;
		var isVisible:Boolean;
		//Get value text style
		var valueStyleObj:Object = this.styleM.getTextStyle(this.objects.DATAVALUES);
		//Individual properties
		var isBold:Boolean = valueStyleObj.bold;
		var isItalic:Boolean = valueStyleObj.italic;
		var font:String = valueStyleObj.font;
		var angle:Number = 0;
		var yPos:Number;
		var align:String, vAlign:String;
		//Container object
		var valueObj:MovieClip;
		//Iterate through all the datasets
		for (i=1; i<=numDS; i++) {
			//Proceed only if values for that dataset are to be shown, or there are 
			//any display values to be added
			if (this.dataset[i].showValues || this.dataset[i].displayValueSpecified) {
				//Get reference to container movie clip
				dvMC = valuesMC["Series_"+i];
				isVisible = dvMC._visible;
				//Remove existing text fields
				dvMC.removeMovieClip();
				//Re-create
				dvMC = valuesMC.createEmptyMovieClip("Series_"+i, i);
				//Set visibility (for legend based interaction)
				dvMC._visible = isVisible;
				//Create the values now
				for (j=1; j<=subsetItems; j++) {
					//Proceed only if values are to be shown and it's not null; or if it's a display value
					if ((this.dataset[i].showValues && !isNaN(data[i][subsetIndex[i][j]])) || (this.dataset[i].displayValueSpecified && displayValueGiven[i][subsetIndex[i][j]])) {
						//Get the y position based on valuePosition & next data's position
						if (this.dataset[i].valuePosition == "AUTO") {
							//Get the y position based on next data's position
							if (i == 1) {
								//For first point, we show the value on top
								vAlign = "top";
								yPos = subsetY[i][j]-this.params.valuePadding;
							} else {
								//If this data value is more than that of previous one, we show textbox above
								if (data[i][subsetIndex[i][j]]>=data[i][subsetIndex[i][j-1]]) {
									//Above
									vAlign = "top";
									yPos = subsetY[i][j]-this.params.valuePadding;
								} else {
									//Below
									vAlign = "bottom";
									yPos = subsetY[i][j]+this.params.valuePadding;
								}
							}
						} else if (this.dataset[i].valuePosition == "ABOVE") {
							//Above
							vAlign = "top";
							yPos = subsetY[i][j]-this.params.valuePadding;
						} else {
							//Below
							vAlign = "bottom";
							yPos = subsetY[i][j]+this.params.valuePadding;
						}
						//Align position
						align = "center";
						//Convey alignment to rendering object
						valueStyleObj.align = align;
						valueStyleObj.vAlign = vAlign;
						//Now, if the labels are to be rotated
						if (this.params.rotateValues) {
							valueStyleObj.bold = isBold;
							valueStyleObj.italic = isItalic;
							valueStyleObj.font = font;
							angle = 270;
						} else {
							//Normal horizontal label - Store original properties
							valueStyleObj.bold = isBold;
							valueStyleObj.italic = isItalic;
							valueStyleObj.font = font;
							angle = 0;
						}
						valueObj = createText(false, displayValue[i][subsetIndex[i][j]], dvMC, j, subsetX[i][j], yPos, angle, valueStyleObj, false, 0, 0);
						//Next, we adjust those labels are falling out of top canvas area
						if (((yPos-valueObj.height)<=0)) {
							//Data value is colliding with the upper side of canvas. So we need to place it within
							//the area
							if (!this.params.rotateValues) {
								valueObj.tf._y = yPos+(2*this.params.valuePadding);
							} else {
								valueObj.tf._y = yPos+(2*this.params.valuePadding)+valueObj.height;
							}
						}
						//Now, we adjust those labels are falling out of bottom canvas area     
						if (((yPos+valueObj.height)>=this.elements.canvas.h)) {
							//Data value is colliding with the lower side of canvas. So we need to place it within
							//the area
							if (!this.params.rotateValues) {
								valueObj.tf._y = yPos-(2*this.params.valuePadding)-valueObj.height;
							} else {
								valueObj.tf._y = yPos-(2*this.params.valuePadding);
							}
						}
						//Adjust labels falling out of canvas width area    
						if (valueObj.tf._x<=0) {
							//Data value is colliding with the left side of canvas. 
							valueObj.tf._x = this.params.valuePadding;
						}
						if (valueObj.tf._x+(valueObj.width/2)>=this.elements.canvas.w) {
							//Data value is colliding with the right side of canvas.
							valueObj.tf._x -= (valueObj.width/2);
						}
					}
				}
			}
		}
	}
	/**
	 * Switches mode of chart from zoom to pin and vice-versa.
	*/
	function switchMode(isZoomMode:Boolean) {
		//If pin mode is not allowed, force isZoomMode to true
		if (!this.params.allowPinMode) {
			isZoomMode = true;
		}
		//If switched mode is different   
		if (zoomMode != isZoomMode) {
			//Store value
			zoomMode = isZoomMode;
			//Based on selection, take action
			if (isZoomMode) {
				//Remove pinned movie clip
				removePin();
			}
			//Set state of buttons in toolbar         
			setButtonState();
			//Set context menu state
			setContextMenuState();
		}
	}
	/**
	 * Draws the overlay pinned chart.
	 * @param	startIndex		Start index (global) from where pinning starts
	 * @param	endIndex		End index (global) where pinning ends
	*/
	function drawOverlayPin(startIndex, endIndex):Void {
		var i:Number, j:Number;
		//Store pin indexes
		pinStartIndex = startIndex;
		pinEndIndex = endIndex;
		//Convert to global index
		var globalPinStartIndex:Number = localToGlobalIndex(startIndex);
		var globalPinEndIndex:Number = localToGlobalIndex(pinEndIndex);
		//Create the container overlay movie clip
		pinContainer = this.cMC.createEmptyMovieClip("PinOverlay", this.dm.getDepth("PINOVERLAY"));
		//Position it at start of canvas (start X, top Y).
		pinContainer._x = canvasX;
		pinContainer._y = canvasY;
		var dragStartX:Number = canvasX-subsetX[1][pinStartIndex];
		var dragEndX:Number = canvasX+canvasWidth-subsetX[1][pinEndIndex];
		//Create sub movie clips for chart, border and close button
		var pinBgMC:MovieClip = pinContainer.createEmptyMovieClip("Bg", 1);
		var pinChartMC:MovieClip = pinContainer.createEmptyMovieClip("Chart", 2);
		var pinCloseBtnMC:MovieClip = pinContainer.createEmptyMovieClip("CloseBtn", 3);
		var pinOverlayBorder:MovieClip = pinContainer.createEmptyMovieClip("OverlayBorder", 4);
		
		
		var pinBg_shadow_MC:MovieClip = pinBgMC.createEmptyMovieClip('pinBg_shadow_MC', 0);
		
		pinBg_shadow_MC.filters = [new DropShadowFilter(2, 45, 0x0, 60, 4, 4, 1, 1, false, false, false)];
		
		//Create the background
		pinBg_shadow_MC.beginFill(0x0, 0);
		
		pinBg_shadow_MC.moveTo(subsetX[1][pinStartIndex], 0);
		pinBg_shadow_MC.lineStyle();
		pinBg_shadow_MC.lineTo(subsetX[1][pinEndIndex], 0);
		pinBg_shadow_MC.lineStyle(0, parseInt(this.params.pinPaneBorderColor, 16), 100);
		pinBg_shadow_MC.lineTo(subsetX[1][pinEndIndex], canvasHeight);
		pinBg_shadow_MC.lineStyle();
		pinBg_shadow_MC.lineTo(subsetX[1][pinStartIndex], canvasHeight);
		pinBg_shadow_MC.lineStyle(0, parseInt(this.params.pinPaneBorderColor, 16), 100);
		pinBg_shadow_MC.lineTo(subsetX[1][pinStartIndex], 0);
		pinBg_shadow_MC.endFill();
		
		
		var pinBg_base_MC:MovieClip = pinBgMC.createEmptyMovieClip('pinBg_base_MC', 1);
		
		//Create the background
		pinBg_base_MC.beginFill(parseInt(this.params.pinPaneBgColor, 16), this.params.pinPaneBgAlpha);
		
		pinBg_base_MC.moveTo(subsetX[1][pinStartIndex], 0);
		pinBg_base_MC.lineStyle();
		pinBg_base_MC.lineTo(subsetX[1][pinEndIndex], 0);
		pinBg_base_MC.lineStyle(0, parseInt(this.params.pinPaneBorderColor, 16), 100);
		pinBg_base_MC.lineTo(subsetX[1][pinEndIndex], canvasHeight);
		pinBg_base_MC.lineStyle();
		pinBg_base_MC.lineTo(subsetX[1][pinStartIndex], canvasHeight);
		pinBg_base_MC.lineStyle(0, parseInt(this.params.pinPaneBorderColor, 16), 100);
		pinBg_base_MC.lineTo(subsetX[1][pinStartIndex], 0);
		pinBg_base_MC.endFill();
		
		
		//Add left white border to enhance the pinning shadow effect
		pinOverlayBorder.lineStyle(1, 0xffffff, 70);
		pinOverlayBorder.moveTo(subsetX[1][pinStartIndex], 0);
		pinOverlayBorder.lineTo(subsetX[1][pinStartIndex], canvasHeight);
		
		
		//Draw the pinned chart from startIndex to endIndex
		for (i=1; i<=numDS; i++) {
			//If this dataset was in visible state when drawing
			if (chartMC["Series_"+i]._visible) {
				//Set the line style for chart drawing
				pinChartMC.lineStyle((this.dataset[i].lineThickness+this.params.pinLineThicknessDelta), parseInt(this.dataset[i].color, 16), 100);
				//Move to first item of the dataset 
				pinChartMC.moveTo(subsetX[i][pinStartIndex], subsetY[i][pinStartIndex]);
				//Draw the rest of items in series
				for (j=pinStartIndex+1; j<=pinEndIndex; j++) {
					//Draw line
					//If both points are defined
					if (!isNaN(data[i][subsetIndex[i][j-1]]) && !isNaN(data[i][subsetIndex[i][j]])) {
						//pinChartMC.lineTo(subsetX[i][j], subsetY[i][j]);
						DrawingExt.dashTo(pinChartMC, subsetX[i][j-1], subsetY[i][j-1], subsetX[i][j], subsetY[i][j], (this.dataset[i].lineThickness+this.params.pinLineThicknessDelta), ((this.dataset[i].lineThickness*2)+this.params.pinLineThicknessDelta));
					}
				}
			}
		}
		//Reference to class
		var classRef = this;
		//Draw close button
		pinCloseBtnMC._x = subsetX[1][pinEndIndex]-20;
		pinCloseBtnMC._y = 5;
		pinCloseBtnMC.lineStyle(0, parseInt(this.params.pinPaneBorderColor, 16), 100);
		pinCloseBtnMC.beginFill(parseInt(this.params.pinPaneBgColor, 16), 10);
		pinCloseBtnMC.moveTo(0, 0);
		pinCloseBtnMC.lineTo(15, 0);
		pinCloseBtnMC.lineTo(15, 15);
		pinCloseBtnMC.lineTo(0, 15);
		pinCloseBtnMC.lineTo(0, 0);
		pinCloseBtnMC.endFill();
		pinCloseBtnMC.lineStyle(2, parseInt(this.params.pinPaneBorderColor, 16), 100);
		pinCloseBtnMC.moveTo(3, 3);
		pinCloseBtnMC.lineTo(12, 12);
		pinCloseBtnMC.moveTo(12, 3);
		pinCloseBtnMC.lineTo(3, 12);
		pinCloseBtnMC.onRelease = function() {
			//Remove the existing pin
			classRef.removePin();
		};
		//Make the entire overlay drag-able (within canvas)
		pinBgMC.useHandCursor = true;
		pinBgMC.onRollOver = function() {
			classRef.setMouseCursor("drag");
		};
		pinBgMC.onRollOut = function() {
			classRef.setMouseCursor("normal");
		};
		pinBgMC.onPress = function() {
			this._parent.startDrag(false, dragStartX, classRef.canvasY, dragEndX, classRef.canvasY);
		};
		pinBgMC.onRelease = pinBgMC.onReleaseOutside=function () {
			//Stop dragging
			this._parent.stopDrag();
			//Get position relative to canvas
			var pos:Number = this._parent._x-dragStartX;
			//Adjust position to a slot
			pos = classRef.subsetX[1][classRef.getNearestIndex(pos)];
			//Set the position
			this._parent._x = pos+dragStartX;
		};
		//Set flag that a pin has been drawn
		pinDrawn = true;
	}
	/**
	 * Removes the pinned chart
	*/
	function removePin():Void {
		//Clear pinned MC
		pinContainer.removeMovieClip();
		//Clear pin indexes
		pinStartIndex = 0;
		pinEndIndex = 0;
		//Update flag that no pin has been drawn
		pinDrawn = false;
	}
	//----------- Data Tool Tip related methods ----------//
	/**
	 * Adds the data tool-tip container.
	*/
	private function addToolTip():Void {
		//Draw the tool-tip now. Re-create the movie clip to empty contents.
		tooltipMC = this.cMC.createEmptyMovieClip("DataTooltip", this.dm.getDepth("DATATOOLTIP"));
	}
	/**
	 * Removes the data tool-tip container
	*/
	private function removeToolTip():Void {
		//Remove the movie clip
		tooltipMC.removeMovieClip();
	}
	/**
	 * Shows tool-tip for all points based on mouse position specified.
	*/
	private function drawToolTip(mousePos:Number):Void {
		//Based on mouse position, get the index
		var index:Number = getNearestIndex(mousePos);
		//If the last activated index is the same one do not do anything
		if(lastIndex == index){
			return;
		}
		lastIndex = index;
		var globalIndex:Number;
		var xPos:Number = canvasX+subsetX[1][index];
		//Clear
		tooltipMC.clear();
		//Remove textfield container movie clip
		tooltipMC["Text"].removeMovieClip();
		//Draw the line
		tooltipMC.lineStyle(1, parseInt(this.params.toolTipBarColor, 16), 100);
		tooltipMC.moveTo(xPos, canvasY);
		tooltipMC.lineTo(xPos, canvasY+canvasHeight);
		//Create the sub-movie clip for textfields
		var tfMC:MovieClip = tooltipMC.createEmptyMovieClip("Text", 1);
		//For each dataset item, create the text
		var i:Number;
		if (this.params.showToolTip) {
			//Create array to store reference of all text fields created
			var tTipTxt:Array = new Array();
			var tTipObj:Object;
			for (i=1; i<=numDS; i++) {
				//If the dataset is visible
				if (chartMC["Series_"+i]._visible) {
					//Get the global index			
					globalIndex = subsetIndex[i][index];
					//Show only if data is not null
					if (!isNaN(data[i][globalIndex])) {
						toolTipStyle.borderColor = this.dataset[i].color;
						toolTipStyle.color = ColorExt.getDarkColor(this.dataset[i].color, 0.65).toString(16);
						//Possible extension: Wrap tool tip labels.
						tTipObj = createText(false, String(toolText[i][globalIndex]), tfMC, i, xPos, canvasY+subsetY[i][index], null, toolTipStyle, false, 0, 0);
						//Store in array
						tTipTxt.push({tf:tTipObj.tf, width:tTipObj.width, height:tTipObj.height, xPos:tTipObj.tf._x, yPos:tTipObj.tf._y});
					}
				}
			}
			//Adjust the tool-tip positions so that they do not overlap
			adjustToolTipPosition(tTipTxt);
		}
		//Draw the label name at bottom   
		createText(false, labels[subsetIndex[numDS][index]], tfMC, i, xPos, canvasY+canvasHeight+this.params.scrollHeight+this.params.scrollPadding+this.params.labelPadding, null, this.styleM.getTextStyle(this.objects.OVERLAYLABEL), false, 0, 0);
		//For each item 
		tooltipMC.updateAfterEvent();
	}
	
	/**
	 * Adjusts the position of all the tool-tip textfields created
	 * during rollover, so that they do not: a. Overlap, and b. Go out
	 * of canvas bounds
	*/
	private function adjustToolTipPosition(arrTips:Array):Void{
		//Sort on Y Position
		arrTips.sortOn(["yPos"], Array.NUMERIC);
		
		//Calculate end positions of canvas
		var canvasEndX:Number = canvasX + canvasWidth;
		var canvasEndY:Number = canvasY + canvasHeight;
		
		// If Possible, Tool Tips are distributed in both side of the line.Otherwise its only on one side.
		// Both Side tool tips array.
		var leftLabels:Array = new Array();
		var rightLabels:Array = new Array();
		
		var totalTips:Number = arrTips.length;
		var i:Number;
		// Iterate through the total number of tooltips dividing them in left/right side.
		for(i=0; i<totalTips; i++){
			//Side property of the tooltip is stored at first time
			if((i%2) != 0){
				arrTips[i].side = "R";
			}else{
				//move the text field to the left side of the line
				arrTips[i].tf._x -= arrTips[i].tf._width;
				arrTips[i].side = "L";
			}
			
			// In case the Tool tips exeed the chart margin,flipped them horizontally
			// Now here is possible situation if a custom tooltip is Wide enough to accomodate  
			// in both the side of the line.What should Happen?
			if(arrTips[i].tf._x < canvasX){
				//exceeding the left margin; moved to the right side of the line
				arrTips[i].tf._x += arrTips[i].tf._width;
				arrTips[i].side = "R";
			}else if((arrTips[i].tf._x + arrTips[i].tf._width) > canvasEndX){
				//excceding the Right margin; moved to the left side of the line
				arrTips[i].tf._x -= arrTips[i].tf._width;
				arrTips[i].side = "L";
			}
			// After final calculation on the X cordinate.Tool tips are stored 
			// in respective array for use.
			if(arrTips[i].side == "L"){
				leftLabels.push(arrTips[i]);
				
			}else if(arrTips[i].side == "R"){
				rightLabels.push(arrTips[i]);
			}
		}
		
		//Left hand Tips are arranged according to the space available above/below them
		if(leftLabels.length > 1){
			
			//total height of the left hand labels.
			var totalHeight:Number = leftLabels[0].tf._height*leftLabels.length;
			//if all the tooltips of this side could be shown in the chart height
			if(totalHeight<canvasHeight){
				//The overlapping tips are detected and grouped.Also the total amount of 
				//overlap in a overlap cluster
				var leftHandOverlaps:Array = calculateOverlap(leftLabels);
				// place/arrange tips.
				manageOverlap(leftHandOverlaps,0);
			}else{
				//if the number of tooltips could not be shown in the available width 
				//just place the tips from top to bottom one after another in equal gapping to accomodate all 
				//in the available height
				var i:Number;
				var totTips:Number = leftLabels.length;
				var yGap:Number = canvasHeight/leftLabels.length;
				for(i = 0; i < totTips; i++){
					leftLabels[i].tf._y = canvasY+yGap*i;
				}
			}
		}
		
		//Right hand Tips are arranged according to the space available above/below them
		if(rightLabels.length > 1){
			
			//total height of the right hand labels.
			var totalHeight:Number = rightLabels[0].tf._height*rightLabels.length;
			
			//if all the tooltips of this side could be shown in the chart height
			if(totalHeight < canvasHeight){
				//The overlapping tips are detected and grouped.Also the total amount of 
				//overlap in a overlap cluster
				var rightHandOverlaps:Array = calculateOverlap(rightLabels);
				// place/arrange tips.
				manageOverlap(rightHandOverlaps, 0);
				
			}else{
				//if the number of tooltips could not be shown in the available width 
				//just place the tips from top to bottom one after another in equal gapping to accomodate all 
				//in the available height
				var i:Number;
				var totTips:Number = rightLabels.length;
				var yGap:Number = canvasHeight/rightLabels.length;
				for(i = 0; i < totTips; i++){
					rightLabels[i].tf._y = canvasY+yGap*i;
				}
			}
		}
	}
	
	/**
	* calculateOverlap checkes on the tooltips of a side and finds which one overlaps with the other.
	* All the overlaped tips are grouped and stored in an index.
	* Non-overlapped items are directly stored in an index.
	* Total amount of overlap is stored in the overlap cluster.
	*
	* @params	sourceArr	the sorted side(left or right) array of tooltip items
	* @return 				The clusted array of overlaing and non-overlapping items
	**/
	private function calculateOverlap(sourceArr:Array): Array{
		// to store overlapped / non-overlapped items
		var overlaps:Array = new Array();
		
		//current and next object.Overlapping is checked between this 2 objects
		var objTxt:Object, objTxtNext:Object;
		var i:Number;
		var totalTips:Number = sourceArr.length;
		
		for (i=0; i<totalTips ; i++) {
			//total amount of overlap.
			var delOverlp:Number = 0;
			
			objTxt = sourceArr[i];
			objTxtNext = sourceArr[i+1];
			
			//get the endY position of the text field
			objTxt.endY = objTxt.tf._y + objTxt.height;
			
			//now check whether this overlaps with the next one...
			if(objTxt.endY > objTxtNext.tf._y){
				
				//now check how many tips overlaps..
				var overlpCounter:Number = i;
				delOverlp += objTxt.endY - objTxtNext.tf._y;
				
				// craete a overlapping cluster
				var j:Number = i+1;
				//store the overlapping objects
				overlaps.push(new Array());
				overlaps[overlaps.length - 1].push(objTxt);
				overlaps[overlaps.length - 1].push(objTxtNext);
				
				//All overpping items with this cluster is stored.
				while(j < sourceArr.length)
				{
					//get the end y position of the current object
					sourceArr[j].endY = sourceArr[j].tf._y + sourceArr[j].height;
					//whether it overlaps with the next one
					if(sourceArr[j].endY > sourceArr[(j+1)].tf._y)
					{
						//total amount of overlap is added/
						delOverlp += sourceArr[j].endY - sourceArr[(j+1)].tf._y;
						//add this object to the existing overlap cluster.
						overlaps[overlaps.length - 1].push(sourceArr[j+1]);
						j++;
					}else{
						//The cluster ends here.The next item doesn't overlap.This denotes the end of 
						// this cluster.
						//The initial iterator of the main array is updated.As lot of items could have been
						//added to the previous cluster.
						i = j;
						break;
					}
					
				}
				//Total amount of overlap is stored in the cluster.
				overlaps[overlaps.length - 1]['totalOvrlp'] = delOverlp;
				
			}else{
				//non-overlapped/non-clustered item.
				overlaps.push(objTxt);
			}
			
		}
		//return array where items are stored in clustered / non-clustered sturture
		return overlaps;
	}
	
	/**
	* manageOverlap function actually place the tooltips depending on its status 
	* and on the amount gap availabale above and below it. The clustered overlapping 
	* items are placed one after another. The first item of the overlaping cluster is 
	* also placed depending on the gap available. 
	* 
	* @param	sourceArray		Array of tooltip items in clustered and non-clustered
	*							structure.
	**/
	private function manageOverlap(sourceArray:Array):Void{
		//create a temporary array to store all the tooltips 
		var allArray:Array = new Array();
		
		var i:Number;
		//Calculate end positions of canvas strat and end limits
		var canvasEndY:Number = canvasY+canvasHeight;
		
		var totalItms:Number = sourceArray.length;
		
		for(i=0; i < totalItms; i++){
			
			//previous or previous cluster's last item
			var prevLastItem:Object;
			//the next or next cluster's fisrt item.
			var nextFirstItem:Object;
			
			//if this is not the first toolTip..get the last toolTip
			if(i != 0){
				
				//check whether the last item was a group or single item
				if(sourceArray[i-1].length>0){
					//if it is a group..the last item would be
					prevLastItem = sourceArray[i-1][(sourceArray[i-1].length-1)];
				}else{
					//if a single object
					prevLastItem = sourceArray[i-1];
				}
			}
			
			//if this is not the last tooltip..get the next tooltip
			if(i != (totalItms-1)){
				//check whether the next item was a group or single item
				if(sourceArray[i+1].length>0){
					//if it is a group..the first item would be
					nextFirstItem = sourceArray[i+1][0];
				}else{
					//if a single object
					nextFirstItem =  sourceArray[i+1];
				}
			}
			
			//all the overlapped items are in grouped/clustered structured 
			if(sourceArray[i].length > 0){
			
				//if this is a overlapped group..get the total overlap amount
				var totalOverlap:Number = sourceArray[i]['totalOvrlp'];
				
				//amount of shifting for the first label
				var yShift:Number = totalOverlap/2;
				
				//total overlapped tips of this group
				var totItms:Number = sourceArray[i].length;
				
				//first tooltip of this group
				var firstItem:Object = sourceArray[i][0];
				
				//last tooltip of this group
				var lastItem:Object = sourceArray[i][totItms-1];
				
				//get the above & below gap
				if(prevLastItem != null && prevLastItem != undefined){
					var gapAbove:Number = firstItem.tf._y-(prevLastItem.tf._y+prevLastItem.tf._height);
				}else{
					//if the previtem is not define.The gap is calculated from the top margin
					var gapAbove:Number =firstItem.tf._y- canvasY;
				}
				
				if(nextFirstItem != null && nextFirstItem != undefined){
					var gapBelow:Number = nextFirstItem.tf._y-(lastItem.tf._y+lastItem.tf._height);
				}else{
					//if the next item is not defined ..the gap is calculated from the chart bottom margin
					var gapBelow:Number = canvasEndY-(lastItem.tf._y+lastItem.tf._height);
				}
				
				/** 	We must find that whether the space above this overlapped group can accomodate all the overlaping tooltips.
				* 		if the first and last item could be shifted half of the total overlapping value,this could be achieved.
				* 		There could be 4 scenarios: 
				* 			1. The gap above & below of this cluster can Shift the desired Y value to accomodate all the items.
				* 			2. There is yShift value available above but not below.
				* 			3. There is yShift Value available below but not above.
				* 			4. There is no space above & below to shift  yShift amount.
				**/
				
				//yShift value is calculated based on the above Scenarios
				if(gapAbove >= yShift && gapBelow >= yShift){
					//nothing to do as of now....the tooltips should accomodate 
				}else if(gapAbove > yShift && gapBelow < yShift){
					
					// now if there is plenty of space above to move the labels upward
					// but not enough space below.The difference is adjusted by moving
					// the clustered upward.
					var amountOfDiffBelow:Number = yShift-gapBelow;
					
					//now check whether this diff could be adjusted above
					if(gapAbove > (yShift + amountOfDiffBelow) ){
						yShift = yShift+amountOfDiffBelow;
					}else{
						//if this difference could not be adjusted,the max adjustment
						// value, which is the difference, is assigned as yShift.
						yShift = gapAbove;
					}
				}else{
					/**	now any other situations 
					*	a) if the above gap is less than the yShift.
					*	b) above and below gap is less than the required shift value.
					*	   the max value is assigned to yShift
					**/
					yShift = gapAbove;
				}
				
				//lables of this cluster is arranged on the y-axis;
				for(var j=0; j < sourceArray[i].length; j++){
					//only the first one is moved
					if(j == 0){
						sourceArray[i][j].tf._y -= yShift;
					}else{
						//the other in the group in respect to the previuos one
						sourceArray[i][j].tf._y = sourceArray[i][(j-1)].tf._y + sourceArray[i][(j-1)].tf._height;
					}
					//just store the tips in an array - non grouped ...
					allArray.push(sourceArray[i][j]);
				}
				
			}else{
				//for single items check whether this overlaps with the previous one...
				if(prevLastItem != null && prevLastItem != undefined){
					if(prevLastItem.tf._y + prevLastItem.tf._height > sourceArray[i].tf._y ){
						
						sourceArray[i].tf._y = prevLastItem.tf._y + prevLastItem.tf._height;
					}
				}else{
					if(sourceArray[i].tf._y < canvasY){
						sourceArray[i].tf._y = canvasY
					}
				}
				//just store the tips in a array
				allArray.push(sourceArray[i])
			}
		}
		
		//ToolTips Excedding from thr chart limits are readjusted from 
		//bottom to top
		var lastIndx:Number = allArray.length-1;
		
		for(i=lastIndx ; i>=0 ; i--){
			//boolean value to check whether any item in this iteration has been shifted upward
			//if shifted the iteraion is continued.
			//if not this denotes the end as the other items are already adjusted from top
			var isShiftedUp:Boolean = false;
			
			if(i == lastIndx){
				
				//whether exceeding
				if((allArray[i].tf._y + allArray[i].tf._height) > canvasEndY){
					allArray[i].tf._y = canvasEndY-allArray[i].height;
					isShiftedUp = true;
				}
				
			}else{
				//check whether this is below the previous moved item
				if(allArray[i].tf._y + allArray[i+1].tf._height > allArray[i+1].tf._y ){
					allArray[i].tf._y = allArray[i+1].tf._y - allArray[i].tf._height;
					isShiftedUp = true;
				}
			}

			if(!isShiftedUp){
				//no shift occur.
				//End of ToolTips Y placement management.
				break;
			}
		}
	}
	
	/**
	* setContextMenu method sets the context menu for the chart.
	* For this chart, the context items are "Print Chart".
	*/
	private function setContextMenu():Void {
		var chartMenu:ContextMenu = new ContextMenu();
		chartMenu.hideBuiltInItems();
		//Create the zoom out and reset chart items
		zoomOutCMI = new ContextMenuItem(this.params.zoomOutMenuItemLabel, Delegate.create(this, zoomOutCMIHandler));
		resetChartCMI = new ContextMenuItem(this.params.resetChartMenuItemLabel, Delegate.create(this, resetChartCMIHandler));
		//Push items
		chartMenu.customItems.push(zoomOutCMI);
		chartMenu.customItems.push(resetChartCMI);
		//If pin mode is to be allowed.
		if (this.params.allowPinMode) {
			zoomModeCMI = new ContextMenuItem(this.params.zoomModeMenuItemLabel, Delegate.create(this, zoomModeCMIHandler), true);
			pinModeCMI = new ContextMenuItem(this.params.pinModeMenuItemLabel, Delegate.create(this, pinModeCMIHandler));
			//Push items
			chartMenu.customItems.push(zoomModeCMI);
			chartMenu.customItems.push(pinModeCMI);
		}
		if (this.params.showPrintMenuItem) {
			//Create a print chart contenxt menu item
			var printCMI:ContextMenuItem = new ContextMenuItem("Print Chart", Delegate.create(this, printChart), true);
			//Push print item.
			chartMenu.customItems.push(printCMI);
		}
		//If the export data item is to be shown           
		if (this.params.showExportDataMenuItem) {
			chartMenu.customItems.push(super.returnExportDataMenuItem());
		}
		//Add export chart related menu items to the context menu           
		this.addExportItemsToMenu(chartMenu);
		if (this.params.showFCMenuItem) {
			//Push "About FusionCharts" Menu Item
			chartMenu.customItems.push(super.returnAbtMenuItem());
		}
		//Assign the menu to cMC movie clip           
		this.cMC.menu = chartMenu;
	}
	//------------ Helper functions & Event Handlers --------------//
	function zoomHandlerAction(startXPos, endXPos):Void {
		//Convert the x-pos to indexes
		var startIndex = getNearestIndex(Math.min(startXPos, endXPos));
		var endIndex = getNearestIndex(Math.max(startXPos, endXPos));
		//If the chart is in zoom mode, reset the subset
		if (zoomMode) {
			//Convert to global values
			startIndex = localToGlobalIndex(startIndex);
			endIndex = localToGlobalIndex(endIndex);
			//Store the index in zoomHistory
			//Push into history only if it's a valid zoom in
			if ((endIndex-startIndex)>0 && (subsetStartIndex != startIndex || subsetEndIndex != endIndex)) {
				zoomHistory.push({start:startIndex, end:endIndex});
			}
			//Reset the indexes         
			resetIndexes(startIndex, endIndex);
			//Raise the external event
			if (this.registerWithJS==true &&  ExternalInterface.available){
				ExternalInterface.call("FC_Zoomed", this.DOMId, startIndex, endIndex, this.labels[startIndex], this.labels[endIndex]);
				ExternalInterface.call("__fusioncharts_event", {type:"zoomed", sender:this.DOMId}, {startIndex:startIndex, endIndex:endIndex, startLabel:this.labels[startIndex], endLabel:this.labels[endIndex]});
			}
		} else {
			//Draw overlay pin only if difference in index >0
			if (endIndex-startIndex>0) {
				drawOverlayPin(startIndex, endIndex);
				//Convert to global values
				startIndex = localToGlobalIndex(startIndex);
				endIndex = localToGlobalIndex(endIndex);
				//Raise the external event
				if (this.registerWithJS==true &&  ExternalInterface.available){
					ExternalInterface.call("FC_Pinned", this.DOMId, startIndex, endIndex, this.labels[startIndex], this.labels[endIndex]);
					ExternalInterface.call("__fusioncharts_event", {type:"pinned", sender:this.DOMId}, {startIndex:startIndex, endIndex:endIndex, startLabel:this.labels[startIndex], endLabel:this.labels[endIndex]});
				}
			}
		}
	}
	/**
	 * Event handler for "Zoom out" context menu item.
	*/
	private function zoomOutCMIHandler():Void {
		zoomOutChart();
	}
	/**
	 * Event handler for reset chart context menu item.
	*/
	private function resetChartCMIHandler():Void {
		resetChart();
	}
	/**
	 * Event handler for switch to zoom mode CMI.
	*/
	private function zoomModeCMIHandler():Void {
		switchMode(true);
		//Reset context menu state
		setContextMenuState();
	}
	/**
	 * Event handler for switch to pin mode CMI.
	*/
	private function pinModeCMIHandler():Void {
		switchMode(false);
		//Reset context menu state
		setContextMenuState();
	}
	/**
	 * legendClick method is the event handler for legend. In this method,
	 * we toggle the visibility of dataset.
	*/
	private function legendClick(target:Object):Void {
		if (this.chartDrawn) {
			//Update the container flag that the data-set is now visible/invisible
			chartMC["Series_"+target.index]._visible = target.active;
			valuesMC["Series_"+target.index]._visible = target.active;
		} else {
			lgnd.cancelClickEvent(target.intIndex);
		}
	}
	/**
	 * Rollover defined for pin mode tool bar button
	*/
	private function btnPinModeRollover():Void {
		//Define function
		var fnRollOver:Function;
		//Create Delegate for roll over function showToolTip
		fnRollOver = Delegate.create(this, showToolTip);
		//Set the tool text
		fnRollOver.toolText = (zoomMode == true) ? this.params.btnSwitchToPinModeTooltext : this.params.btnSwitchToZoomModeTooltext;
		//Invoke
		fnRollOver();
	}
	/* --------- Helper Methods ----------*/
	/**
	 * Resets the chart
	*/
	private function resetChart():Void {
		//Clear the zoom history
		zoomHistory.splice(0, zoomHistory.length);
		//Show all items
		resetIndexes(1, numItems);
		//Reset context menu state
		setContextMenuState();
		//Raise the external event
		if (this.registerWithJS==true &&  ExternalInterface.available){
			ExternalInterface.call("FC_ResetZoomChart", this.DOMId);
			ExternalInterface.call("__fusioncharts_event", {type:"resetzoomchart", sender:this.DOMId}, {eventObject:"menu"});
		}
	}
	/**
	 * Zooms the chart out to previous level.
	*/
	private function zoomOutChart():Void {
		//Zoom out (based on history). 
		//Pop and ignore the last value, as it contains index of the current view
		zoomHistory.pop();
		//If no items are in history then zoom out to all items
		if (zoomHistory.length == 0) {
			resetIndexes(1, numItems);
		} else {
			//Get access to index of previous zoom view
			var lastSet:Object = zoomHistory[zoomHistory.length-1];
			//Set the view for the same
			resetIndexes(lastSet.start, lastSet.end);
		}
		//Reset context menu state
		setContextMenuState();
		//Raise event
		if (this.registerWithJS==true &&  ExternalInterface.available){
			ExternalInterface.call("FC_ZoomedOut", this.DOMId);
			ExternalInterface.call("__fusioncharts_event", {type:"zoomedout", sender:this.DOMId}, {eventObject:"menu"});
		}
	}
	/**
	 * Formats an interval value and returns.
	*/
	private function formatInterval(num:Number):String {
		return formatNumber(num, this.params.formatNumber, this.params.yAxisValueDecimals, this.params.forceYAxisValueDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
	}
	/**
	 * Draws a basic rectangle
	*/
	private function drawRectangle(mc:MovieClip, x:Number, y:Number, width:Number, height:Number) {
		mc.moveTo(x, y);
		mc.lineTo(x+width, y);
		mc.lineTo(x+width, y+height);
		mc.lineTo(x, y+height);
		mc.lineTo(x, y);
	}
	/**
	 * Based on the index of a data item (set), returns the x-axis
	 * position (in pixels). The index is on base of numItems (global index).
	 * and not currently displayed subset index. 
	*/
	private function getXPos(index:Number):Number {
		//If index is outside range of current subset, return -1
		if (index>=subsetStartIndex && index<=subsetEndIndex){
			//Total items
			var totalItems = subsetEndIndex-subsetStartIndex;
			var posRatio = (index-subsetStartIndex)/totalItems;
			//Now calibrate it to pixels
			var posX:Number = (posRatio*this.elements.canvas.w);
			//Return
			return posX;
		}else{
			if (index<subsetStartIndex){
				return 0;
			}else{
				return canvasWidth;
			}
		}
	}
	/**
	 * Based on the value of a data item (set), returns the y-axis
	 * position (in pixels).
	*/
	private function getYPos(value:Number):Number {
		//If axis has been defined, proceed. Else, return -1;
		if (ax != undefined) {
			//Get the ratio of position w.r.t canvas end Y
			var posRatio:Number = ax.getAxisPosition(value);
			//Now calibrate it to pixels
			var posY:Number = this.elements.canvas.h-(posRatio*this.elements.canvas.h);
			//Return
			return posY;
		} else {
			return -1;
		}
	}
	/**
	 * Based on the x-position provided (in pixels), this returns the
	 * point closest to that x-axis position.
	 * @param	pos		X-Position of the point. Has to be between 0 and canvasWidth
	 * @return			Index of closest value. Index is w.r.t subset.
	 *					If xPos is out of bounds of canvas the extreme end is assumed.
	*/
	private function getNearestIndex(pos:Number):Number {
		//If mouse position is outside canvas, restrict
		if (pos<0) {
			pos = 0;
		}
		if (pos>canvasWidth) {
			pos = canvasWidth;
		}
		//If there is only 1 item total, return the same.            
		if (numItems == 1) {
			return numItems;
		}
		var index:Number;
		var i:Number;
		//Iterate through items only in subset.	
		for (i=1; i<subsetItems; i++) {
			if (subsetX[1][i] == pos) {
				//If the position matches this item
				index = i;
				break;
			} else if (subsetX[1][i+1] == pos) {
				//If the position matches next item
				index = i+1;
				break;
			} else if (pos>subsetX[1][i] && pos<subsetX[1][i+1]) {
				//If the position is between two points.
				var pos1:Number = pos-subsetX[1][i];
				var pos2:Number = subsetX[1][i+1]-pos;
				if (pos1<=pos2) {
					index = i;
				} else {
					index = i+1;
				}
				break;
			}
		}
		//Return
		return index;
	}
	/**
	 * Converts a local index (that belongs to subset) to the
	 * global value.
	*/
	private function localToGlobalIndex(index:Number):Number {
		return subsetIndex[1][index];
	}
	/**
	* reInit method re-initializes the chart. This method is basically called
	* when the user changes chart data through JavaScript. In that case, we need
	* to re-initialize the chart, set new XML data and again render.
	* @param	isRedraw		Whether a redraw has been invoked
	*/
	public function reInit(isRedraw:Boolean):Void {
		//Proceed only if it's not a redraw
		if (!isRedraw) {
			//Invoke super class's reInit
			super.reInit();
			//Re-set indexes to 0
			numTrendLines = 0;
			numTrendLinesBelow = 0;
			numVTrendLinesBelow = 0;
			numVTrendLines = 0;
			//Initialize the number of data elements present
			numDS = 0;
			numItems = 0;
			slots = 0;
			subsetStartIndex = 0;
			subsetEndIndex = 0;
			subsetStep = 0;
			subsetItems = 0;
			pinStartIndex = 0;
			pinEndIndex = 0;
			subsetMin = 0;
			subsetMax = 0;
			//Set the chart in zoom mode by default
			zoomMode = true;
			pinDrawn = false;
			//Re-create container arrays
			labels = new Array();
			dataset = new Array();
			data = new Array();
			formattedVal = new Array();
			toolText = new Array();
			displayValue = new Array();
			displayValueGiven = new Array();
			zoomHistory = new Array();
			trendLines = new Array();
			vTrendLines = new Array();
			subsetIndex = new Array();
			subsetX = new Array();
			subsetY = new Array();
		}
		if (this.params.interactiveLegend) {
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
	}
	/**
	* remove method removes the chart by clearing the chart movie clip
	* and removing any listeners.
	*/
	public function remove():Void {
		super.remove();
		//Remove class pertinent objects
		if (this.params.interactiveLegend) {
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
		//Remove legend            
		this.lgnd.destroy();
		lgndMC.removeMovieClip();
	}
	//-------- External Interface Definitions -------------//
	/**
	 * Sets the zoom subset index. External Interface handler.
	*/
	private function extInfSetZoomIndex(startIndex:Number, endIndex:Number):Void {
		//First check if indexes are correct
		if (startIndex != undefined && endIndex != undefined && !isNaN(startIndex) && !isNaN(endIndex) && startIndex<1 && endIndex>numItems) {
			//Log error
			this.log("Invalid indexes", "Please check the indexes that you've provided to zoomTo(start, end) method", Logger.LEVEL.ERROR);
			return;
		}
		//Else, check if the chart is in pin mode. If yes, switch 
		if (zoomMode == false) {
			switchMode(true);
		}
		//Find correct order 
		startIndex = Math.min(startIndex, endIndex);
		endIndex = Math.max(startIndex, endIndex);
		//Log
		this.log("Re-setting indexes", "Setting display index of chart to "+startIndex+" (start) and "+endIndex+"(end)", Logger.LEVEL.INFO);
		//Now, set the indexes
		this.resetIndexes(startIndex, endIndex);
	}
	/**
	 * External interface handler to switch chart mode
	*/
	private function extInfSetZoomMode(isZoomMode:Boolean):Void {
		if (isZoomMode) {
			switchMode(true);
		} else {
			//Check if pin mode is allowed
			if (this.params.allowPinMode) {
				switchMode(false);
			} else {
				//Log error
				this.log("Pin mode is disabled", "The chart cannot switch to pin mode as it has been disabled via XML settings.", Logger.LEVEL.ERROR);
			}
		}
	}
	/**
	 * External interface handler for zoom out function.
	*/
	private function extInfZoomOut():Void {
		//Check if the chart is in pin mode. If yes, switch 
		if (zoomMode == false) {
			switchMode(true);
		}
		zoomOutChart();
	}
	/**
	 * External interface handler for reset chart function.
	*/
	private function extInfResetChart():Void {
		//Check if the chart is in pin mode. If yes, switch 
		if (zoomMode == false) {
			switchMode(true);
		}
		resetChart();
	}
	/**
	 * Returns the subset start index (of current view)
	*/
	private function extInfGetStartIndex():Number{
		return this.subsetStartIndex;
	}
	/**
	 * Returns the subset end index (of current view)
	*/
	private function extInfGetEndIndex():Number{
		return this.subsetEndIndex;
	}
	//---------------DATA EXPORT HANDLERS-------------------//
	/**
	 * Returns the data of the chart in CSV/TSV format. The separator, qualifier and line
	 * break character is stored in params (during common parsing).
	 * @return	The data of the chart in CSV/TSV format, as specified in the XML.
	 */
	public function exportChartDataCSV():String {
		var strData:String = "";
		var strQ:String = this.params.exportDataQualifier;
		var strS:String = this.params.exportDataSeparator;
		var strLB:String = this.params.exportDataLineBreak;
		var i:Number, j:Number;
		strData = strQ+((this.params.xAxisName != "") ? (this.params.xAxisName) : ("Label"))+strQ+strS;
		//Add all the series names
		for (i=1; i<=this.numDS; i++) {
			strData += strQ+((this.dataset[i].seriesName != "") ? (this.dataset[i].seriesName) : (""))+strQ+((i<this.numDS) ? (strS) : (strLB));
		}
		//Iterate through each data-items and add it to the output
		for (i=1; i<=this.numItems; i++) {
			//Add the category label
			strData += strQ+(labels[i])+strQ+strS;
			//Add the individual value for datasets
			for (j=1; j<=this.numDS; j++) {
				strData += strQ+(!isNaN(data[j][i]) ? ((this.params.exportDataFormattedVal == true) ? (formattedVal[j][i]) : (data[j][i])) : (""))+strQ+((j<this.numDS) ? strS : "");
			}
			if (i<this.numItems) {
				strData += strLB;
			}
		}
		return strData;
	}
}