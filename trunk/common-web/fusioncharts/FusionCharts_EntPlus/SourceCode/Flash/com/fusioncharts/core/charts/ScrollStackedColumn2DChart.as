﻿ /**
* @class ScrollStackedColumn2DChart
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* ScrollStackedColumn2DChart chart extends the SingleYAxis2DVerticalChart class to render a
* Stacked 2D Column Chart with scrolling capabilities.
*/
//Import Chart class
import com.fusioncharts.core.Chart;
//Parent SingleYAxis2DVerticalChart Class
import com.fusioncharts.core.SingleYAxis2DVerticalChart;
//Error class
import com.fusioncharts.helper.FCError;
//Import Logger Class
import com.fusioncharts.helper.Logger;
//Style Object
import com.fusioncharts.core.StyleObject;
//Delegate
import mx.utils.Delegate;
//Columns
import com.fusioncharts.core.chartobjects.Column2D;
import com.fusioncharts.core.chartobjects.RoundColumn2D;
//Legend Class
import com.fusioncharts.helper.AdvancedLegend;
import com.fusioncharts.helper.LegendIconGenerator;
import com.fusioncharts.helper.LabelMetrics;
import com.fusioncharts.helper.LabelRenderer;
import flash.display.BitmapData;
//Extensions
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.extensions.StringExt;
import com.fusioncharts.extensions.MathExt;
import com.fusioncharts.extensions.DrawingExt;
//Scroll Bar 
import com.fusioncharts.components.FCChartHScrollBar;
class com.fusioncharts.core.charts.ScrollStackedColumn2DChart extends SingleYAxis2DVerticalChart
{
	//Version number (if different from super Chart class)
	//private var _version:String = "3.0.0";
	//Instance variables
	//List of chart objects
	private var _arrObjects : Array;
	private var xmlData : XML;
	//Array to store x-axis categories (labels)
	private var categories : Array;
	//Array to store datasets
	private var dataset : Array;
	//Number of data sets
	private var numDS : Number;
	//Array to store Sums
	private var sums : Array;
	private var sumStored : Boolean;
	//Number of data items
	private var num : Number;
	//Reference to legend component of chart
	private var lgnd :AdvancedLegend;
	//Reference to legend movie clip
	private var lgndMC : MovieClip;
	//Flag to indicate whether positive number is present
	private var positivePresent : Boolean;
	//Flag to indicate whether negative number is present
	private var negativePresent : Boolean;
	//Flag to store whether scroll bar would be required or not
	private var scrollRequired:Boolean;
	//Scroll Content holder MC - We'll draw the chart and reference movie clips within this MC.
	private var scrollableMC:MovieClip;
	//Chart Content holder - scrollable
	private var scrollContentMC:MovieClip;
	//Reference movie clip, based on which we'll scroll
	private var scrollRefMC:MovieClip;
	//Masking MovieClip
	private var maskMC:MovieClip;	
	//Scroll Bar Container MC
	private var scrollBarMC:MovieClip;
	//Reference to scroll bar
	private var scrollB:FCChartHScrollBar;
	//Boolean value to check whether the plotSpacePercent is explicitly defined or calculated as default
	private var defaultPlotSpacePercent : Boolean;
	//Variable to store the instance of the label metrics class.
	private var labelMetrics:LabelMetrics;
	/**
	* Constructor function. We invoke the super class'
	* constructor and then set the objects for this chart.
	*/
	function ScrollStackedColumn2DChart (targetMC : MovieClip, depth : Number, width : Number, height : Number, x : Number, y : Number, debugMode : Boolean, lang : String, scaleMode:String, registerWithJS:Boolean, DOMId:String)
	{
		//Invoke the super class constructor
		super (targetMC, depth, width, height, x, y, debugMode, lang, scaleMode, registerWithJS, DOMId);
		//Log additional information to debugger
		//We log version from this class, so that if this class version
		//is different, we can log it
		this.log ("Version", _version, Logger.LEVEL.INFO);
		this.log ("Chart Type", "Scroll Stacked 2D Column Chart", Logger.LEVEL.INFO);
		//List Chart Objects and set them
		_arrObjects = new Array ("BACKGROUND", "CANVAS", "CAPTION", "SUBCAPTION", "YAXISNAME", "XAXISNAME", "DIVLINES", "YAXISVALUES", "HGRID", "DATALABELS", "DATAVALUES", "TRENDLINES", "TRENDVALUES", "DATAPLOT", "TOOLTIP", "VLINES", "LEGEND", "SCROLLPANE", "VLINELABELS");
		super.setChartObjects (_arrObjects);
		//Initialize the containers for chart
		this.categories = new Array ();
		this.dataset = new Array ();
		//Initialize the number of data elements present
		this.numDS = 0;
		this.num = 0;
		this.negativePresent = false;
		this.positivePresent = false;
		//Array to store sum of data sets
		this.sums = new Array ();
		this.sumStored = false;
		//By default, we assume that scroll bar wouldn't be required.
		this.scrollRequired = false;
		//PlotSpacePercent initially considered as explicitly defined.
		this.defaultPlotSpacePercent = false;
		//create the instance
		labelMetrics = new LabelMetrics();
		//set the chart type - for labels management / panel calculation.
		labelMetrics.chartType = "column";
	}
	/**
	* render method is the single call method that does the rendering of chart:
	* - Parsing XML
	* - Calculating values and co-ordinates
	* - Visual layout and rendering
	* - Event handling
	*/
	public function render (isRedraw:Boolean) : Void
	{
		//reset all variables for label management
		labelMetrics.reset();
		//Parse the XML Data document
		this.parseXML ();
		//If it's a re-draw then do not animate
		if (isRedraw){
			this.params.animation = false;
			this.defaultGlobalAnimation = 0;
		}
		//Now, if the number of data elements is 0, we show pertinent
		//error.
		if (this.numDS * this.num == 0)
		{
			tfAppMsg = this.renderAppMessage (_global.getAppMessage ("NODATA", this.lang));
			//Add a message to log.
			this.log ("No Data to Display", "No data was found in the XML data document provided. Possible cases can be: <LI>There isn't any data generated by your system. If your system generates data based on parameters passed to it using dataURL, please make sure dataURL is URL Encoded.</LI><LI>You might be using a Single Series Chart .swf file instead of Multi-series .swf file and providing multi-series data or vice-versa.</LI>", Logger.LEVEL.ERROR);
			//Expose rendered method
			this.exposeChartRendered();
			//Also raise the no data event
			if (!isRedraw){
				this.raiseNoDataExternalEvent();
			}
		} else
		{
			//validation for single category item. In case of single category we revert back to previous label management methods
			if(this.num == 1){
				this.params.XTLabelManagement = false;
			}
			//Detect number scales
			this.detectNumberScales ();
			//Feed empty data
			this.feedEmptyData();
			//Convert to percent data
			this.preparePercentData();
			//Calculate the axis limits
			this.calculateAxisLimits ();
			//Calculate exact number of div lines
			this.calcDivs ();
			//Set Style defaults
			this.setStyleDefaults ();
			//Validate trend lines
			this.validateTrendLines ();
			//Allot the depths for various charts objects now
			this.allotDepths ();
			//Calculate Points
			this.calculatePoints (isRedraw);
			//Calculate vLine Positions
			this.calcVLinesPos ();
			//Calculate trend line positions
			this.calcTrendLinePos ();
			//Feed macro values
			super.feedMacros ();
			//Remove application message
			this.removeAppMessage (this.tfAppMsg);
			//Set tool tip parameter
			this.setToolTipParam ();
			//-----Start Visual Rendering Now------//
			//Create scroll related container movie clips
			this.createContainerMC();
			//Draw background
			this.drawBackground ();
			//Set click handler
			this.drawClickURLHandler ()
			//Load background SWF
			this.loadBgSWF ();
			//Update timer
			this.timeElapsed = (this.params.animation) ? this.styleM.getMaxAnimationTime (this.objects.BACKGROUND) : 0;
			//Draw canvas
			this.config.intervals.canvas = setInterval (Delegate.create (this, drawCanvas) , this.timeElapsed);
			//Draw headers
			this.config.intervals.headers = setInterval (Delegate.create (this, drawHeaders) , this.timeElapsed);
			//Update timer
			this.timeElapsed += (this.params.animation) ? this.styleM.getMaxAnimationTime (this.objects.CANVAS, this.objects.CAPTION, this.objects.SUBCAPTION, this.objects.YAXISNAME, this.objects.XAXISNAME) : 0;
			//Draw div lines
			this.config.intervals.divLines = setInterval (Delegate.create (this, drawDivLines) , this.timeElapsed);
			//Update timer
			this.timeElapsed += (this.params.animation) ? this.styleM.getMaxAnimationTime (this.objects.DIVLINES, this.objects.YAXISVALUES) : 0;
			//Horizontal grid
			this.config.intervals.hGrid = setInterval (Delegate.create (this, drawHGrid) , this.timeElapsed);
			//Update timer
			this.timeElapsed += (this.params.animation) ? this.styleM.getMaxAnimationTime (this.objects.HGRID) : 0;						
			//Draw labels
			this.config.intervals.labels = setInterval (Delegate.create (this, drawLabels) , this.timeElapsed);
			//Draw scroll bar
			this.config.intervals.scrollBar = setInterval (Delegate.create (this, createScrollBar) , this.timeElapsed);
			//Draw columns
			this.config.intervals.plot = setInterval (Delegate.create (this, drawColumns) , this.timeElapsed);
			//Legend
			this.config.intervals.legend = setInterval (Delegate.create (this, drawLegend) , this.timeElapsed);
			//Update timer
			this.timeElapsed += (this.params.animation) ? this.styleM.getMaxAnimationTime (this.objects.DATALABELS, this.objects.DATAPLOT, this.objects.LEGEND) : 0;
			//Data Values
			this.config.intervals.dataValues = setInterval (Delegate.create (this, drawValues) , this.timeElapsed);
			//Data Sum
			this.config.intervals.dataSum = setInterval (Delegate.create (this, drawSum) , this.timeElapsed);
			//Draw trend lines
			this.config.intervals.trend = setInterval (Delegate.create (this, drawTrendLines) , this.timeElapsed);
			//Draw vertical div lines
			this.config.intervals.vLine = setInterval (Delegate.create (this, drawVLines) , this.timeElapsed);
			//Update timer
			this.timeElapsed += (this.params.animation) ? this.styleM.getMaxAnimationTime (this.objects.TRENDLINES, this.objects.TRENDVALUES, this.objects.VLINES, this.objects.DATAVALUES) : 0;
			//Dispatch event that the chart has loaded.
			this.config.intervals.renderedEvent = setInterval(Delegate.create(this, exposeChartRendered) , this.timeElapsed);
			//Render context menu
			//We do not put context menu interval as we need the menu to appear
			//right from start of the play.
			this.setContextMenu ();
		}
	}
	/**
	* returnDataAsObject method creates an object out of the parameters
	* passed to this method. The idea is that we store each data point
	* as an object with multiple (flexible) properties. So, we do not
	* use a predefined class structure. Instead we use a generic object.
	*	@param	label		Label of the data column.
	*	@param	value		Value for the column.
	*	@param	color		Hex Color code (or comma separated list).
	* 	@param	displayValue	Value that will be displayed on the chart
	*	@param	alpha		List of alphas separated by comma
	*	@param	ratio		List of color ratios separated by comma
	*	@param	toolText	Tool tip text (if specified).
	*	@param	link		Link (if any) for the column.
	*	@param	showLabel	Flag to show/hide label for this column.
	*	@param	showValue	Flag to show/hide value for this column.
	*	@param	isDashed	Flag whether the column would have dashed border.
	*	@return			An object encapsulating all these properies.
	*/
	private function returnDataAsObject (value : Number, color : String, displayValue:String, alpha : String, ratio : String, toolText : String, link : String, showValue : Number, isDashed : Boolean) : Object
	{
		//Create a container
		var dataObj : Object = new Object ();
		//Store the values
		dataObj.value = value;
		//Explicitly specified display value
		dataObj.exDispVal = displayValue;
		//Extract and save colors, ratio, alpha as array so that we do not have to parse later.
		dataObj.color = ColorExt.parseColorList (color);
		dataObj.alpha = ColorExt.parseAlphaList (alpha, dataObj.color.length);
		dataObj.ratio = ColorExt.parseRatioList (ratio, dataObj.color.length);
		dataObj.toolText = toolText;
		dataObj.link = link;
		dataObj.showValue = (showValue == 1) ? true : false;
		dataObj.dashed = isDashed;
		//If the given number is not a valid number or it's missing
		//set appropriate flags for this data point
		dataObj.isDefined = ((dataObj.alpha [0] == 0) || isNaN (value)) ? false : true;
		//Other parameters
		//X & Y Position of data point
		dataObj.x = 0;
		dataObj.y = 0;
		//Width and height
		dataObj.w = 0;
		dataObj.h = 0;
		//X & Y Position of value tb
		dataObj.valTBX = 0;
		dataObj.valTBY = 0;
		//Rounded corner radius
		dataObj.cornerRadius = 0;
		//Return the container
		return dataObj;
	}
	/**
	* returnDataAsCat method returns data of a <category> element as
	* an object
	*	@param	label		Label of the category.
	*	@param	showLabel	Whether to show the label of this category.
	*	@param	toolText	Tool-text for the category
	*	@return			A container object with the given properties
	*/
	private function returnDataAsCat (label : String, showLabel : Number, toolText : String) : Object
	{
		//Create container object
		var catObj : Object = new Object ();
		catObj.label = label;
		catObj.showLabel = ((showLabel == 1) && (label != undefined) && (label != null) && (label != "")) ? true : false;
		catObj.toolText = toolText;
		//X and Y Position
		catObj.x = 0;
		catObj.y = 0;
		//Return container
		return catObj;
	}
	/**
	* parseXML method parses the XML data, sets defaults and validates
	* the attributes before storing them to data storage objects.
	*/
	private function parseXML () : Void
	{
		//Get the element nodes
		var arrDocElement : Array = this.xmlData.childNodes;
		//Loop variable
		var i : Number;
		var j : Number;
		var k : Number;
		//Look for <graph> element
		for (i = 0; i < arrDocElement.length; i ++)
		{
			//If it's a <graph> element, proceed.
			//Do case in-sensitive mathcing by changing to upper case
			if (arrDocElement [i].nodeName.toUpperCase () == "GRAPH" || arrDocElement [i].nodeName.toUpperCase () == "CHART")
			{
				//Extract attributes of <graph> element
				this.parseAttributes (arrDocElement [i]);
				//Extract common attributes/over-ride chart specific ones
				this.parseCommonAttributes (arrDocElement [i], true);
				//Now, get the child nodes - first level nodes
				//Level 1 nodes can be - CATEGORIES, DATASET, TRENDLINES, STYLES etc.
				var arrLevel1Nodes : Array = arrDocElement [i].childNodes;
				var setNode : XMLNode;
				//Iterate through all level 1 nodes.
				for (j = 0; j < arrLevel1Nodes.length; j ++)
				{
					if (arrLevel1Nodes [j].nodeName.toUpperCase () == "CATEGORIES")
					{
						//Categories Node.
						var categoriesNode : XMLNode = arrLevel1Nodes [j];
						//Convert attributes to array
						var categoriesAtt : Array = this.getAttributesArray (categoriesNode);
						//Extract attributes of this node.
						this.params.catFont = getFV (categoriesAtt ["font"] , this.params.outCnvBaseFont);
						this.params.catFontSize = getFN (categoriesAtt ["fontsize"] , this.params.outCnvBaseFontSize);
						this.params.catFontColor = formatColor (getFV (categoriesAtt ["fontcolor"] , this.params.outCnvBaseFontColor));
						//Get reference to child node.
						var arrLevel2Nodes : Array = arrLevel1Nodes [j].childNodes;
						//Iterate through all child-nodes of CATEGORIES element
						//and search for CATEGORY or VLINE node
						for (k = 0; k < arrLevel2Nodes.length; k ++)
						{
							if (arrLevel2Nodes [k].nodeName.toUpperCase () == "CATEGORY")
							{
								//Category Node.
								//Update counter
								this.num ++;
								//Extract attributes
								var categoryNode : XMLNode = arrLevel2Nodes [k];
								var categoryAtt : Array = this.getAttributesArray (categoryNode);
								//Category label.
								var catLabel : String = getFV (categoryAtt ["label"] , categoryAtt ["name"] , "");
								var catShowLabel : Number = getFN (categoryAtt ["showlabel"] , categoryAtt ["showname"] , this.params.showLabels);
								var catToolText : String = getFV (categoryAtt ["tooltext"] , categoryAtt ["hovertext"] , catLabel);
								//Store it in data container.
								this.categories [this.num] = this.returnDataAsCat (catLabel, catShowLabel, catToolText);
							} 
							else if (arrLevel2Nodes [k].nodeName.toUpperCase () == "VLINE")
							{
								//Vertical axis division Node - extract child nodes
								var vLineNode : XMLNode = arrLevel2Nodes [k];
								//Parse and store
								this.parseVLineNode (vLineNode, this.num);
							}
						}
					} else if (arrLevel1Nodes [j].nodeName.toUpperCase () == "DATASET")
					{
						//Increment
						this.numDS ++;
						//Dataset node.
						var dataSetNode : XMLNode = arrLevel1Nodes [j];
						//Get attributes array
						var dsAtts : Array = this.getAttributesArray (dataSetNode);
						//Create storage object in dataset array
						this.dataset [this.numDS] = new Object ();
						//Store attributes
						this.dataset [this.numDS].seriesName = getFV (dsAtts ["seriesname"] , "");
						this.dataset [this.numDS].color = formatColor (getFV (dsAtts ["color"] , this.defColors.getColor ()));
						//If plot gradient color has been defined, add it
						if (this.params.plotGradientColor != "")
						{
							this.dataset [this.numDS].color = this.dataset [this.numDS].color + "," + this.params.plotGradientColor;
						}
						this.dataset [this.numDS].alpha = getFV (dsAtts ["alpha"] , this.params.plotFillAlpha);
						this.dataset [this.numDS].ratio = getFV (dsAtts ["ratio"] , this.params.plotFillRatio);
						this.dataset [this.numDS].showValues = toBoolean (getFN (dsAtts ["showvalues"] , this.params.showValues));
						this.dataset [this.numDS].dashed = toBoolean (getFN (dsAtts ["dashed"] , this.params.plotBorderDashed));
						this.dataset [this.numDS].includeInLegend = toBoolean (getFN (dsAtts ["includeinlegend"] , 1));
						//Create data array under it.
						this.dataset [this.numDS].data = new Array ();
						//Get reference to child node.
						var arrLevel2Nodes : Array = arrLevel1Nodes [j].childNodes;
						//Iterate through all child-nodes of DATASET element
						//and search for SET node
						//Counter
						var setCount : Number = 0;
						//Whether any plot is visible
						var anyPlotVisible:Boolean = false;
						for (k = 0; k < arrLevel2Nodes.length; k ++)
						{
							if (arrLevel2Nodes [k].nodeName.toUpperCase () == "SET")
							{
								//Set Node. So extract the data.
								//Update counter
								setCount ++;
								//Get reference to node.
								setNode = arrLevel2Nodes [k];
								//Get attributes
								var atts : Array;
								atts = this.getAttributesArray (setNode);
								//Now, get value.
								var setValue : Number = this.getSetValue (atts ["value"]);
								//If it's a 100% stacked chart, take absolute value
								if (this.params.stack100Percent){
									setValue = Math.abs(setValue);
								}
								//Get explicitly specified display value
								var setExDispVal : String = getFV( atts["displayvalue"], "");
								//We do NOT unescape the link, as this will be done
								//in invokeLink method for the links that user clicks.
								var setLink : String = getFV (atts ["link"] , "");
								var setToolText : String = getFV (atts ["tooltext"] , atts ["hovertext"]);
								var setColor : String = getFV (atts ["color"] , this.dataset [this.numDS].color);
								var setAlpha : String = getFV (atts ["alpha"] , this.dataset [this.numDS].alpha);
								var setRatio : String = getFV (atts ["ratio"] , this.dataset [this.numDS].ratio);
								var setShowValue : Number = getFN (atts ["showvalue"] , this.dataset [this.numDS].showValues);
								var setDashed : Boolean = toBoolean (getFN (atts ["dashed"] , this.dataset [this.numDS].dashed));
								//Whether plots are to be shown or not
								if(!anyPlotVisible){
									if(!isNaN(setValue) && setAlpha > 0){
										anyPlotVisible = true;
									}
								}
								//Store all these attributes as object.
								this.dataset [this.numDS].data [setCount] = this.returnDataAsObject (setValue, setColor, setExDispVal,  setAlpha, setRatio, setToolText, setLink, setShowValue, setDashed);
								//Update negative flag
								this.negativePresent = (setValue < 0) ?true : negativePresent;
								this.positivePresent = (setValue >= 0) ?true : positivePresent;
							}
						}
						//If any plot is visible in the dataset, that dataset's legend item should appear.
						//Also it has to consider that dataset legend is included.
						if(this.dataset [this.numDS].includeInLegend){
							this.dataset [this.numDS].includeInLegend = (anyPlotVisible)? true:false;
						}
					} else if (arrLevel1Nodes [j].nodeName.toUpperCase () == "STYLES")
					{
						//Styles Node - extract child nodes
						var arrStyleNodes : Array = arrLevel1Nodes [j].childNodes;
						//Parse the style nodes to extract style information
						super.parseStyleXML (arrStyleNodes);
					} else if (arrLevel1Nodes [j].nodeName.toUpperCase () == "TRENDLINES")
					{
						//Trend lines node
						var arrTrendNodes : Array = arrLevel1Nodes [j].childNodes;
						//Parse the trend line nodes
						super.parseTrendLineXML (arrTrendNodes);
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
	private function parseAttributes (graphElement : XMLNode) : Void
	{
		//Array to store the attributes
		var atts : Array = this.getAttributesArray (graphElement);
		//NOW IT'S VERY NECCESARY THAT WHEN WE REFERENCE THIS ARRAY
		//TO GET AN ATTRIBUTE VALUE, WE SHOULD PROVIDE THE ATTRIBUTE
		//NAME IN LOWER CASE. ELSE, UNDEFINED VALUE WOULD SHOW UP.
		//Extract attributes pertinent to this chart
		//Which palette to use?
		this.params.palette = getFN (atts ["palette"] , 1);
		//Palette colors to use
		this.params.paletteColors = getFV(atts["palettecolors"],"");
		//Set palette colors before parsing the <set> nodes.
		this.setPaletteColors();
		// ---------- PADDING AND SPACING RELATED ATTRIBUTES ----------- //
		//captionPadding = Space between caption/subcaption and canvas start Y
		this.params.captionPadding = getFN (atts ["captionpadding"] , 10);
		//Padding for x-axis name - to the right
		this.params.xAxisNamePadding = getFN (atts ["xaxisnamepadding"] , 5);
		//Padding for y-axis name - from top
		this.params.yAxisNamePadding = getFN (atts ["yaxisnamepadding"] , 5);
		//Y-Axis Values padding - Horizontal space between the axis edge and
		//y-axis values or trend line values (on left/right side).
		this.params.yAxisValuesPadding = getFN (atts ["yaxisvaluespadding"] , 2);
		//Label padding - Vertical space between the labels and canvas end position
		this.params.labelPadding = getFN (atts ["labelpadding"] , atts ["labelspadding"] , 3);
		//Percentage space on the plot area
		this.params.plotSpacePercent = getFN (atts ["plotspacepercent"] , 20);
		///Cannot be less than 0 and more than 80
		if ((this.params.plotSpacePercent < 0) || (this.params.plotSpacePercent > 80))
		{
			//Reset to 20
			this.params.plotSpacePercent = 20;
		}
		//Padding of legend from right/bottom side of canvas
		this.params.legendPadding = getFN (atts ["legendpadding"] , 6);
		//Sum value padding - from top (of caption/sub-caption)
		this.params.sumPadding = getFN (atts ["sumpadding"] ,  3);
		//Chart Margins - Empty space at the 4 sides
		this.params.chartLeftMargin = getFN (atts ["chartleftmargin"] , 15);
		this.params.chartRightMargin = getFN (atts ["chartrightmargin"] , 15);
		this.params.chartTopMargin = getFN (atts ["charttopmargin"] , 15);
		this.params.chartBottomMargin = getFN (atts ["chartbottommargin"] , 15);
		//Maximum allowed column width
		this.params.maxColWidth = getFN (atts ["maxcolwidth"] , 100);
		labelMetrics.updateProp("chartObj", {chartWidth: this.width, chartLeftMargin: this.params.chartLeftMargin, chartRightMargin:this.params.chartRightMargin});
		// -------------------------- HEADERS ------------------------- //
		//Chart Caption and sub Caption
		this.params.caption = getFV (atts ["caption"] , "");
		this.params.subCaption = getFV (atts ["subcaption"] , "");
		//X and Y Axis Name
		this.params.xAxisName = getFV (atts ["xaxisname"] , "");
		this.params.yAxisName = getFV (atts ["yaxisname"] , "");
		// --------------------- CONFIGURATION ------------------------- //
		//The upper and lower limits of y and x axis
		this.params.yAxisMinValue = atts["yaxisminvalue"];
		this.params.yAxisMaxValue = atts ["yaxismaxvalue"];
		//Number of columns to show on the chart at any point of time
		//We do not assume a default value now, as we'll later calculate the same.
		this.params.numVisiblePlot = atts["numvisibleplot"];
		//Whether to set animation for entire chart.
		this.params.animation = toBoolean (getFN (this.defaultGlobalAnimation, atts ["animation"] , 1));
		//Whether to set the default chart animation
		this.params.defaultAnimation = toBoolean (getFN (atts ["defaultanimation"] , 1));
		//Configuration to set whether to show the labels
		this.params.showLabels = toBoolean (getFN (atts ["showlabels"] , atts ["shownames"] , 1));
		//Label Display Mode - WRAP, STAGGER, ROTATE or NONE
		this.params.labelDisplay = getFV (atts ["labeldisplay"] , "AUTO");
		//Remove spaces and capitalize
		this.params.labelDisplay = StringExt.removeSpaces (this.params.labelDisplay);
		this.params.labelDisplay = this.params.labelDisplay.toUpperCase ();
		//Option to show vertical x-axis labels
		this.params.rotateLabels = getFV (atts ["rotatelabels"] , atts ["rotatenames"]);
		//Whether to slant label (if rotated)
		this.params.slantLabels = toBoolean (getFN (atts ["slantlabels"] , atts ["slantlabel"] , 0));
		//Angle of rotation based on slanting
		this.config.labelAngle = (this.params.slantLabels == true) ? 315 : 270;
		//If rotateLabels has been explicitly specified, we assign ROTATE value to this.params.labelDisplay
		this.params.labelDisplay = (this.params.rotateLabels == "1") ? "ROTATE" : this.params.labelDisplay;
		//Step value for labels - i.e., show all labels or skip every x label
		this.params.labelStep = int (getFN (atts ["labelstep"] , 1));
		//Cannot be less than 1
		this.params.labelStep = (this.params.labelStep < 1) ? 1 : this.params.labelStep;
		//Number of stagger lines
		this.params.staggerLines = int (getFN (atts ["staggerlines"] , 2));
		//Cannot be less than 2
		this.params.staggerLines = (this.params.staggerLines < 2) ? 2 : this.params.staggerLines;
		//Configuration whether to show data sum
		this.params.showSum = toBoolean (getFN (atts ["showsum"] , 0));
		//Configuration whether to show data values
		this.params.showValues = toBoolean (getFN (atts ["showvalues"] , 1));
		//Whether to rotate values
		this.params.rotateValues = toBoolean (getFN (atts ["rotatevalues"] , 0));
		//Option to show/hide y-axis values
		this.params.showYAxisValues = getFN (atts ["showyaxisvalues"] , atts ["showyaxisvalue"] , 1);
		this.params.showLimits = toBoolean (getFN (atts ["showlimits"] , this.params.showYAxisValues));
		this.params.showDivLineValues = toBoolean (getFN (atts ["showdivlinevalue"] , atts ["showdivlinevalues"] , this.params.showYAxisValues));
		//Y-axis value step- i.e., show all y-axis or skip every x(th) value
		this.params.yAxisValuesStep = int (getFN (atts ["yaxisvaluesstep"] , atts ["yaxisvaluestep"] , 1));
		//Cannot be less than 1
		this.params.yAxisValuesStep = (this.params.yAxisValuesStep < 1) ? 1 : this.params.yAxisValuesStep;
		//Show column shadows
		this.params.showColumnShadow = toBoolean (getFN (atts ["showshadow"] , atts ["showcolumnshadow"] , this.defColors.get2DShadow (this.params.palette)));
		//Whether to automatically adjust div lines
		this.params.adjustDiv = toBoolean (getFN (atts ["adjustdiv"] , 1));
		//Whether to rotate y-axis name
		this.params.rotateYAxisName = toBoolean (getFN (atts ["rotateyaxisname"] , 1));
		//Max width to be alloted to y-axis name - No defaults, as it's calculated later.
		this.params.yAxisNameWidth = (this.params.yAxisName != undefined && this.params.yAxisName != "") ? atts ["yaxisnamewidth"] : 0;
		//Click URL
		this.params.clickURL = getFV (atts ["clickurl"] , "");
		// ------------------------- COSMETICS -----------------------------//
		//Background properties - Gradient
		this.params.bgColor = getFV (atts ["bgcolor"] , this.defColors.get2DBgColor (this.params.palette));
		this.params.bgAlpha = getFV (atts ["bgalpha"] , this.defColors.get2DBgAlpha (this.params.palette));
		this.params.bgRatio = getFV (atts ["bgratio"] , this.defColors.get2DBgRatio (this.params.palette));
		this.params.bgAngle = getFV (atts ["bgangle"] , this.defColors.get2DBgAngle (this.params.palette));
		//Border Properties of chart
		this.params.showBorder = toBoolean (getFN (atts ["showborder"] , 1));
		this.params.borderColor = formatColor (getFV (atts ["bordercolor"] , this.defColors.get2DBorderColor (this.params.palette)));
		this.params.borderThickness = getFN (atts ["borderthickness"] , 1);
		this.params.borderAlpha = getFN (atts ["borderalpha"] , this.defColors.get2DBorderAlpha (this.params.palette));
		//Canvas background properties - Gradient
		this.params.canvasBgColor = getFV (atts ["canvasbgcolor"] , this.defColors.get2DCanvasBgColor (this.params.palette));
		this.params.canvasBgAlpha = getFV (atts ["canvasbgalpha"] , this.defColors.get2DCanvasBgAlpha (this.params.palette));
		this.params.canvasBgRatio = getFV (atts ["canvasbgratio"] , this.defColors.get2DCanvasBgRatio (this.params.palette));
		this.params.canvasBgAngle = getFV (atts ["canvasbgangle"] , this.defColors.get2DCanvasBgAngle (this.params.palette));
		//Canvas Border properties
		this.params.canvasBorderColor = formatColor (getFV (atts ["canvasbordercolor"] , this.defColors.get2DCanvasBorderColor (this.params.palette)));
		this.params.canvasBorderThickness = getFN (atts ["canvasborderthickness"] , 1);
		this.params.canvasBorderAlpha = getFN (atts ["canvasborderalpha"] , this.defColors.get2DCanvasBorderAlpha (this.params.palette));
		//Whether to use round edges?
		this.params.useRoundEdges = getFN (atts ["useroundedges"] , 0);
		//Scroll Properties
		//Color for the scroll bar
		this.params.scrollColor = formatColor(getFV(atts["scrollcolor"], this.defColors.get2DAltHGridColor (this.params.palette)));
		//Vertical padding between the canvas end Y and scroll bar
		//Now canvas border is drawn completely outside the canvas area for scroll charts.
		//Scroll padding default value is 1 due to show every plot's tooltip even that is very minor and scrollbar does not come above plots. 
		this.params.scrollPadding = getFN (atts ["scrollpadding"] ,1);
		//Validate scrollpadding
		this.params.scrollPadding = (this.params.scrollPadding < 1) ? 1 : this.params.scrollPadding;
		//Height of scroll bar
		this.params.scrollHeight = getFN (atts ["scrollheight"] , 16);
		//Width of plus and minus button
		this.params.scrollBtnWidth = getFN (atts ["scrollbtnwidth"] , 16);
		//Padding between the button and the face.
		this.params.scrollBtnPadding = getFN (atts ["scrollbtnpadding"] , 0);
		//Whether to scroll to end
		this.params.scrollToEnd = getFN (atts ["scrolltoend"] , 0);
		//Plot cosmetic properties
		this.params.showPlotBorder = toBoolean (getFN (atts ["showplotborder"] , 1));
		this.params.plotBorderColor = formatColor (getFV (atts ["plotbordercolor"] , this.defColors.get2DPlotBorderColor (this.params.palette)));
		this.params.plotBorderThickness = getFN (atts ["plotborderthickness"] , (this.params.useRoundEdges)?0:1);
		this.params.plotBorderAlpha = getFN (atts ["plotborderalpha"] , 100);
		//If showPlotBorder is false  set the plotBorderAlpha to 0 value.
		this.params.plotBorderAlpha = (this.params.showPlotBorder == true) ? this.params.plotBorderAlpha : 0 ;
		//Plot is dashed
		this.params.plotBorderDashed = toBoolean (getFN (atts ["plotborderdashed"] , 0));
		//Dash Properties
		this.params.plotBorderDashLen = getFN (atts ["plotborderdashlen"] , 5);
		this.params.plotBorderDashGap = getFN (atts ["plotborderdashgap"] , 4);
		//Fill properties
		this.params.plotFillAngle = getFN (atts ["plotfillangle"] , 270);
		this.params.plotFillRatio = getFV (atts ["plotfillratio"] , "");
		this.params.plotFillAlpha = getFV (atts ["plotfillalpha"] , "100");
		//Plot gradient color
		if (atts ["plotgradientcolor"] == "")
		{
			//If some one doesn't want to specify a plot gradient color
			//i.e., he opts for solid fills
			this.params.plotGradientColor = ""
		} else
		{
			this.params.plotGradientColor = formatColor (getFV (atts ["plotgradientcolor"] , this.defColors.get2DPlotGradientColor (this.params.palette)));
		}
		//Legend properties
		this.params.showLegend = toBoolean (getFN (atts ["showlegend"] , 1));
		//Alignment position
		this.params.legendPosition = getFV (atts ["legendposition"] , "BOTTOM");
		//Legend position can be either RIGHT or BOTTOM -Check for it
		this.params.legendPosition = (this.params.legendPosition.toUpperCase () == "RIGHT") ?"RIGHT" : "BOTTOM";
		this.params.interactiveLegend = toBoolean(getFN(atts ["interactivelegend"] , 1));
		this.params.legendCaption = getFV(atts ["legendcaption"] , "");
		this.params.legendMarkerCircle = toBoolean(getFN(atts ["legendmarkercircle"] , 0));
		this.params.legendBorderColor = formatColor (getFV (atts ["legendbordercolor"] , this.defColors.get2DLegendBorderColor (this.params.palette)));
		this.params.legendBorderThickness = getFN (atts ["legendborderthickness"] , 1);
		this.params.legendBorderAlpha = getFN (atts ["legendborderalpha"] , 100);
		this.params.legendBgColor = formatColor (getFV (atts ["legendbgcolor"] , this.defColors.get2DLegendBgColor (this.params.palette)));
		this.params.legendBgAlpha = getFN (atts ["legendbgalpha"] , 100);
		this.params.legendShadow = toBoolean (getFN (atts ["legendshadow"] , 1));
		this.params.legendAllowDrag = toBoolean (getFN (atts ["legendallowdrag"] , 0));
		this.params.legendScrollBgColor = formatColor (getFV (atts ["legendscrollbgcolor"] , "CCCCCC"));
		this.params.legendScrollBarColor = formatColor (getFV (atts ["legendscrollbarcolor"] , this.params.legendBorderColor));
		this.params.legendScrollBtnColor = formatColor (getFV (atts ["legendscrollbtncolor"] , this.params.legendBorderColor));
		this.params.reverseLegend = toBoolean (getFN (atts ["reverselegend"] , 0));
		this.params.legendIconScale = getFN (atts ["legendiconscale"] , 1);
		if(this.params.legendIconScale <= 0 || this.params.legendIconScale > 5){
			this.params.legendIconScale = 1;
		}
		this.params.legendNumColumns = Math.round(getFN (atts ["legendnumcolumns"] , 0));
		if(this.params.legendNumColumns < 0){
			this.params.legendNumColumns = 0;
		}
		this.params.minimiseWrappingInLegend = toBoolean (getFN (atts ["minimisewrappinginlegend"] , 0));
		//Horizontal grid division Lines - Number, color, thickness & alpha
		//Necessarily need a default value for numDivLines.
		this.params.numDivLines = getFN (atts ["numdivlines"] , 4);
		this.params.divLineColor = formatColor (getFV (atts ["divlinecolor"] , this.defColors.get2DDivLineColor (this.params.palette)));
		this.params.divLineThickness = getFN (atts ["divlinethickness"] , 1);
		this.params.divLineAlpha = getFN (atts ["divlinealpha"] , this.defColors.get2DDivLineAlpha (this.params.palette));
		this.params.divLineIsDashed = toBoolean (getFN (atts ["divlineisdashed"] , 0));
		this.params.divLineDashLen = getFN (atts ["divlinedashlen"] , 4);
		this.params.divLineDashGap = getFN (atts ["divlinedashgap"] , 2);
		//Zero Plane properties
		this.params.showZeroPlane = true;
		this.params.zeroPlaneColor = formatColor (getFV (atts ["zeroplanecolor"] , this.params.divLineColor));
		this.params.zeroPlaneThickness = getFN (atts ["zeroplanethickness"] , (this.params.divLineThickness == 1) ?2 : this.params.divLineThickness);
		this.params.zeroPlaneAlpha = getFN (atts ["zeroplanealpha"] , this.params.divLineAlpha * 2);
		//Alternating grid colors
		this.params.showAlternateHGridColor = toBoolean (getFN (atts ["showalternatehgridcolor"] , 1));
		this.params.alternateHGridColor = formatColor (getFV (atts ["alternatehgridcolor"] , this.defColors.get2DAltHGridColor (this.params.palette)));
		this.params.alternateHGridAlpha = getFN (atts ["alternatehgridalpha"] , this.defColors.get2DAltHGridAlpha (this.params.palette));
		//Stack properties
		this.params.stack100Percent = toBoolean (getFN (atts ["stack100percent"] , 0));
		this.params.showPercentValues = toBoolean (getFN (atts ["showpercentvalues"] , this.params.stack100Percent));
		this.params.showPercentInToolTip = toBoolean (getFN (atts ["showpercentintooltip"] , !this.params.showPercentValues));
		// ------------------------- NUMBER FORMATTING ---------------------------- //
		//Option whether the format the number (using Commas)
		this.params.formatNumber = toBoolean (getFN (atts ["formatnumber"] , 1));
		//Option to format number scale
		this.params.formatNumberScale = toBoolean (getFN (atts ["formatnumberscale"] , 1));
		//Number Scales
		this.params.defaultNumberScale = getFV (atts ["defaultnumberscale"] , "");
		this.params.numberScaleUnit = getFV (atts ["numberscaleunit"] , "K,M");
		this.params.numberScaleValue = getFV (atts ["numberscalevalue"] , "1000,1000");
		//Number prefix and suffix
		this.params.numberPrefix = getFV (atts ["numberprefix"] , "");
		this.params.numberSuffix = getFV (atts ["numbersuffix"] , "");
		//Decimal Separator Character
		this.params.decimalSeparator = getFV (atts ["decimalseparator"] , ".");
		//Thousand Separator Character
		this.params.thousandSeparator = getFV (atts ["thousandseparator"] , ",");
		//Input decimal separator and thousand separator. In some european countries,
		//commas are used as decimal separators and dots as thousand separators. In XML,
		//if the user specifies such values, it will give a error while converting to
		//number. So, we accept the input decimal and thousand separator from user, so that
		//we can covert it accordingly into the required format.
		this.params.inDecimalSeparator = getFV (atts ["indecimalseparator"] , "");
		this.params.inThousandSeparator = getFV (atts ["inthousandseparator"] , "");
		//Decimal Precision (number of decimal places to be rounded to)
		this.params.decimals = getFV (atts ["decimals"] , atts ["decimalprecision"]);
		//Force Decimal Padding
		this.params.forceDecimals = toBoolean (getFN (atts ["forcedecimals"] , 0));
		//y-Axis values decimals
		this.params.yAxisValueDecimals = getFV (atts ["yaxisvaluedecimals"] , atts ["yaxisvaluesdecimals"] , atts ["divlinedecimalprecision"] , atts ["limitsdecimalprecision"]);
	}
	/**
	* Feeds empty data. By default there should be equal number of <categories>
	* and <set> element within each dataset. If in case, <set> elements fall short,
	* we need to append empty data at the end.
	*/
	private function feedEmptyData():Void{
		//Feed empty data - By default there should be equal number of <categories>
		//and <set> element within each dataset. If in case, <set> elements fall short,
		//we need to append empty data at the end.
		var i:Number, j:Number;
		for (i = 1; i <= this.numDS; i ++)
		{
			for (j = 1; j <= this.num; j ++)
			{
				if (this.dataset [i].data [j] == undefined)
				{
					this.dataset [i].data [j] = this.returnDataAsObject (NaN);
				}
			}
		}
	}
	/**
	* getSumOfValues method returns the sum of values of a particular data
	* index in all the data sets. It is used to get the positive and negative
	* sum for the given index.
	*	@param	index	Index for which we've to calculate sum
	*	@return		An object containing PSum and NSum as positive
	*					and negative sum respectively.
	*/
	private function getSumOfValues (index : Number) : Object
	{
		//Loop variables
		var i : Number;
		//Variable to store positive and negative sum
		var pSum : Number, nSum : Number;
		//Return Object
		var rtnObj : Object = new Object ();
		//Iterate through all the datasets for this index
		for (i = 1; i <= this.numDS; i ++)
		{
			//Check only if the data is defined
			if (this.dataset [i].data [index].isDefined)
			{
				if (this.dataset [i].data [index].value >= 0)
				{
					pSum = (pSum == undefined) ? (this.dataset [i].data [index].value) : (pSum + this.dataset [i].data [index].value);
				} else
				{
					nSum = (nSum == undefined) ? (this.dataset [i].data [index].value) : (nSum + this.dataset [i].data [index].value);
				}
			}
		}
		if (this.sumStored == false)
		{
			//Store the sum of this index in array
			this.sums [index] = new Object ();
			this.sums [index].sum = ((pSum == undefined) ?0 : pSum) + ((nSum == undefined) ?0 : nSum);
			this.sums [index].pSum = pSum;
			this.sums [index].nSum = nSum;
			//Update flag
			if (index == this.num)
			{
				this.sumStored = true;
			}
		}
		//Store values in return object and return
		rtnObj.pSum = pSum;
		rtnObj.nSum = nSum;
		return rtnObj;
	}
	/**
	* getMaxDataValue method gets the maximum y-axis data sum present
	* in the data.
	*	@return	The maximum value present in the data provided.
	*/
	private function getMaxDataValue () : Number
	{
		var maxValue : Number;
		var firstSumFound : Boolean = false;
		var i : Number;
		var pSum : Number, sNum : Number;
		var sumObj : Object;
		var vitalSum : Number;
		for (i = 1; i <= this.num; i ++)
		{
			sumObj = this.getSumOfValues (i);
			vitalSum = (this.positivePresent == false) ? (sumObj.nSum) : (sumObj.pSum);
			//By default assume the first non-null sum to be maximum
			if (firstSumFound == false)
			{
				if (vitalSum != undefined)
				{
					//Set the flag that "We've found first non-null sum".
					firstSumFound = true;
					//Also assume this value to be maximum.
					maxValue = vitalSum;
				}
			} else
			{
				//If the first sum has been found and the current sum is defined, compare
				if (vitalSum != undefined)
				{
					//Store the greater number
					maxValue = (vitalSum > maxValue) ? vitalSum : maxValue;
				}
			}
		}
		return maxValue;
	}
	/**
	* getMinDataValue method gets the minimum y-axis data sum present
	* in the data
	*	@reurns		The minimum value present in data
	*/
	private function getMinDataValue () : Number
	{
		var minValue : Number;
		var firstSumFound : Boolean = false;
		var i : Number;
		var pSum : Number, sNum : Number;
		var sumObj : Object;
		var vitalSum : Number;
		for (i = 1; i <= this.num; i ++)
		{
			sumObj = this.getSumOfValues (i);
			vitalSum = (this.negativePresent == true) ? (sumObj.nSum) : (sumObj.pSum);
			//By default assume the first non-null sum to be minimum
			if (firstSumFound == false)
			{
				if (vitalSum != undefined)
				{
					//Set the flag that "We've found first non-null sum".
					firstSumFound = true;
					//Also assume this value to be minimum.
					minValue = vitalSum;
				}
			} else
			{
				//If the first sum has been found and the current sum is defined, compare
				if (vitalSum != undefined)
				{
					//Store the lesser number
					minValue = (vitalSum < minValue) ? vitalSum : minValue;
				}
			}
		}
		return minValue;
	}
	/**
	* Prepares the data for percent values, if need be.
	*/
	private function preparePercentData(): Void{
		//If the chart is to be rendered as 100% stacked chart.
		if (this.params.stack100Percent){
			var i:Number;
			var j:Number;
			var percentValue:Number;
			//Iterate through all indexes and prepare sum
			for (i=1; i<=this.num; i++){
				//Calculate sum of each number
				this.getSumOfValues(i);
			}
			//Change all individual values to % values
			for (i = 1; i <= this.numDS; i ++)
			{
				for (j = 1; j <= this.num; j ++)
				{
					if (this.dataset [i].data [j].isDefined){
						//Calculate the % value of the data
						percentValue = (this.dataset [i].data [j].value/this.sums[j].sum)*100;
						//If the user has not explicitly set display value, calculate and set for him
						if (this.dataset[i].data[j].exDispVal==""){
							//Based on what the user has opted for, set display value as the formatted actual value
							if (this.params.showPercentValues){
								//Store % value
								this.dataset[i].data[j].exDispVal = formatNumber (percentValue, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, false, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, "", "%");
							}else{
								//Store formatted value
								this.dataset[i].data[j].exDispVal = formatNumber (this.dataset [i].data [j].value, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
							}
						}
						//Also store tool-tip value
						if (this.params.showPercentInToolTip){
							//Store % value
							this.dataset[i].data[j].toolTipDispVal = formatNumber (percentValue, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, false, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, "", "%");
						}else{
							//Store formatted value
							this.dataset[i].data[j].toolTipDispVal = formatNumber (this.dataset [i].data [j].value, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
						}
						//Store the % value, for calculations
						this.dataset [i].data [j].value = percentValue;
						//Store a copy of original value
						this.dataset[i].data[j].origValue = this.dataset [i].data [j].value;						
						//Round to 1 decimal on lower side
						//To avoid the decimal summation of >100.00000x%
						this.dataset [i].data [j].value = int(this.dataset [i].data [j].value*10)/10;
					}
				}
			}
			//Convert the displayValue of sums as well
			for (j = 1; j <= this.num; j ++)
			{
				this.sums [j].displayValue = formatNumber (this.sums [j].sum, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
			}
			//Over-ride numberprefix, numbersuffix
			this.params.numberPrefix = "";
			this.params.numberSuffix = "%";			
		}
	}
	/**
	* calculateAxisLimits method sets the axis limits for the chart.
	* It gets the minimum and maximum value specified in data and
	* based on that it calls super.getAxisLimits();
	*/
	private function calculateAxisLimits () : Void
	{
		//If it's a 100% stacked chart, force y-max to be 100
		if (this.params.stack100Percent==true){
			this.params.yAxisMaxValue = 100;
			this.params.yAxisMinValue = 0;
		}
		//Normalize the y-axis min value if specified
		if (this.params.yAxisMinValue != null && this.params.yAxisMinValue != undefined && this.params.yAxisMinValue != "" && Number (this.params.yAxisMinValue) != NaN){
			//Loop vars
			var i:Number, j:Number;
			var exit:Boolean = false;
			//Store as number
			this.params.yAxisMinValue = Number(this.params.yAxisMinValue);
			//Check if it exceeds each of the value provided
			for (i = 1; i <= this.numDS; i ++)
			{
				for (j = 1; j <= this.num; j ++)
				{
					if (this.dataset [i].data [j].isDefined && this.dataset [i].data [j].value<this.params.yAxisMinValue){
						//Mark it as undefined
						delete this.params.yAxisMinValue;
						exit = true;
						break;
					}
				}
				//Break from outer loop as well
				if (exit){
					break;
				}
			}
		}
		this.getAxisLimits (this.getMaxDataValue () , this.getMinDataValue () , true, true);
	}
	/**
	* setStyleDefaults method sets the default values for styles or
	* extracts information from the attributes and stores them into
	* style objects.
	*/
	private function setStyleDefaults () : Void
	{
		//Default font object for Caption
		//-----------------------------------------------------------------//
		var captionFont = new StyleObject ();
		captionFont.name = "_SdCaptionFont";
		captionFont.align = "center";
		captionFont.valign = "top";
		captionFont.bold = "1";
		captionFont.font = this.params.outCnvBaseFont;
		captionFont.size = this.params.outCnvBaseFontSize+3;
		captionFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.CAPTION, captionFont, this.styleM.TYPE.FONT, null);
		delete captionFont;
		//-----------------------------------------------------------------//
		//Default font object for SubCaption
		//-----------------------------------------------------------------//
		var subCaptionFont = new StyleObject ();
		subCaptionFont.name = "_SdSubCaptionFont";
		subCaptionFont.align = "center";
		subCaptionFont.valign = "top";
		subCaptionFont.bold = "1";
		subCaptionFont.font = this.params.outCnvBaseFont;
		subCaptionFont.size = this.params.outCnvBaseFontSize+1;
		subCaptionFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.SUBCAPTION, subCaptionFont, this.styleM.TYPE.FONT, null);
		delete subCaptionFont;
		//-----------------------------------------------------------------//
		//Default font object for YAxisName
		//-----------------------------------------------------------------//
		var yAxisNameFont = new StyleObject ();
		yAxisNameFont.name = "_SdYAxisNameFont";
		yAxisNameFont.align = "center";
		yAxisNameFont.valign = "middle";
		yAxisNameFont.bold = "1";
		yAxisNameFont.font = this.params.outCnvBaseFont;
		yAxisNameFont.size = this.params.outCnvBaseFontSize;
		yAxisNameFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.YAXISNAME, yAxisNameFont, this.styleM.TYPE.FONT, null);
		delete yAxisNameFont;
		//-----------------------------------------------------------------//
		//Default font object for XAxisName
		//-----------------------------------------------------------------//
		var xAxisNameFont = new StyleObject ();
		xAxisNameFont.name = "_SdXAxisNameFont";
		xAxisNameFont.align = "center";
		xAxisNameFont.valign = "middle";
		xAxisNameFont.bold = "1";
		xAxisNameFont.font = this.params.outCnvBaseFont;
		xAxisNameFont.size = this.params.outCnvBaseFontSize;
		xAxisNameFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.XAXISNAME, xAxisNameFont, this.styleM.TYPE.FONT, null);
		delete xAxisNameFont;
		//-----------------------------------------------------------------//
		//Default font object for trend lines
		//-----------------------------------------------------------------//
		var trendFont = new StyleObject ();
		trendFont.name = "_SdTrendFontFont";
		trendFont.font = this.params.outCnvBaseFont;
		trendFont.size = this.params.outCnvBaseFontSize;
		trendFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.TRENDVALUES, trendFont, this.styleM.TYPE.FONT, null);
		delete trendFont;
		//-----------------------------------------------------------------//
		//Default font object for yAxisValues
		//-----------------------------------------------------------------//
		var yAxisValuesFont = new StyleObject ();
		yAxisValuesFont.name = "_SdYAxisValuesFont";
		yAxisValuesFont.align = "right";
		yAxisValuesFont.valign = "middle";
		yAxisValuesFont.font = this.params.outCnvBaseFont;
		yAxisValuesFont.size = this.params.outCnvBaseFontSize;
		yAxisValuesFont.color = this.params.outCnvBaseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.YAXISVALUES, yAxisValuesFont, this.styleM.TYPE.FONT, null);
		delete yAxisValuesFont;
		//-----------------------------------------------------------------//
		//Default font object for DataLabels
		//-----------------------------------------------------------------//
		var dataLabelsFont = new StyleObject ();
		dataLabelsFont.name = "_SdDataLabelsFont";
		dataLabelsFont.align = "center";
		dataLabelsFont.valign = "bottom";
		dataLabelsFont.font = this.params.catFont;
		dataLabelsFont.size = this.params.catFontSize;
		dataLabelsFont.color = this.params.catFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.DATALABELS, dataLabelsFont, this.styleM.TYPE.FONT, null);
		delete dataLabelsFont;
		//-----------------------------------------------------------------//
		//Default font object for Legend
		//-----------------------------------------------------------------//
		var legendFont = new StyleObject ();
		legendFont.name = "_SdLegendFont";
		legendFont.font = this.params.outCnvBaseFont;
		legendFont.size = this.params.outCnvBaseFontSize;
		legendFont.color = this.params.outCnvBaseFontColor;
		legendFont.ishtml = 1;
		legendFont.leftmargin = 3;
		//Over-ride
		this.styleM.overrideStyle (this.objects.LEGEND, legendFont, this.styleM.TYPE.FONT, null);
		delete legendFont;
		//-----------------------------------------------------------------//
		//Default font object for DataValues
		//-----------------------------------------------------------------//
		var dataValuesFont = new StyleObject ();
		dataValuesFont.name = "_SdDataValuesFont";
		dataValuesFont.align = "center";
		dataValuesFont.valign = "middle";
		dataValuesFont.font = this.params.baseFont;
		dataValuesFont.size = this.params.baseFontSize;
		dataValuesFont.color = this.params.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.DATAVALUES, dataValuesFont, this.styleM.TYPE.FONT, null);
		delete dataValuesFont;
		//-----------------------------------------------------------------//
		//Default font object for ToolTip
		//-----------------------------------------------------------------//
		var toolTipFont = new StyleObject ();
		toolTipFont.name = "_SdToolTipFont";
		toolTipFont.font = this.params.baseFont;
		toolTipFont.size = this.params.baseFontSize;
		toolTipFont.color = this.params.baseFontColor;
		toolTipFont.bgcolor = this.params.toolTipBgColor;
		toolTipFont.bordercolor = this.params.toolTipBorderColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.TOOLTIP, toolTipFont, this.styleM.TYPE.FONT, null);
		delete toolTipFont;
		//-----------------------------------------------------------------//
		//Default font object for V-line labels
		//-----------------------------------------------------------------//
		var vLineLabelsFont = new StyleObject ();
		vLineLabelsFont.name = "_SdDataVLineLabelsFont";
		vLineLabelsFont.align = "center";
		vLineLabelsFont.valign = "bottom";
		vLineLabelsFont.font = this.params.baseFont;
		vLineLabelsFont.size = this.params.baseFontSize;
		vLineLabelsFont.color = this.params.baseFontColor;
		vLineLabelsFont.bgcolor = this.params.canvasBgColor;
		//Over-ride
		this.styleM.overrideStyle (this.objects.VLINELABELS, vLineLabelsFont, this.styleM.TYPE.FONT, null);
		delete vLineLabelsFont;
		//-----------------------------------------------------------------//
		//Default Effect (Shadow) object for Legend
		//-----------------------------------------------------------------//
		if (this.params.legendShadow)
		{
			var legendShadow = new StyleObject ();
			legendShadow.name = "_SdLegendShadow";
			legendShadow.distance = 2;
			legendShadow.alpha = 90;
			legendShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle (this.objects.LEGEND, legendShadow, this.styleM.TYPE.SHADOW, null);
			delete legendShadow;
		}
		//-----------------------------------------------------------------//
		//Default Effect (Shadow) object for DataPlot
		//-----------------------------------------------------------------//
		if (this.params.showColumnShadow || this.params.useRoundEdges)
		{
			var dataPlotShadow = new StyleObject ();
			dataPlotShadow.name = "_SdDataPlotShadow";
			dataPlotShadow.distance = 2;
			dataPlotShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle (this.objects.DATAPLOT, dataPlotShadow, this.styleM.TYPE.SHADOW, null);
			delete dataPlotShadow;
		}
		//Also, if round edges are to be plotted, we need a shadow object for canvas
		if (this.params.useRoundEdges){
			var canvasShadow = new StyleObject ();
			canvasShadow.name = "_SdCanvasShadow";
			canvasShadow.distance = 0;
			canvasShadow.blurx = 8;
			canvasShadow.blury = 8;
			canvasShadow.alpha = 90;
			canvasShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle (this.objects.CANVAS, canvasShadow, this.styleM.TYPE.SHADOW, null);
			delete canvasShadow;
		}
		//-----------------------------------------------------------------//
		//Default Animation object for DataPlot (if required)
		//-----------------------------------------------------------------//
		if (this.params.defaultAnimation)
		{
			var dataPlotAnim = new StyleObject ();
			dataPlotAnim.name = "_SdDataPlotAnim";
			dataPlotAnim.param = "_yscale";
			dataPlotAnim.easing = "regular";
			dataPlotAnim.wait = 0;
			dataPlotAnim.start = 0;
			dataPlotAnim.duration = 1;
			//Over-ride
			this.styleM.overrideStyle (this.objects.DATAPLOT, dataPlotAnim, this.styleM.TYPE.ANIMATION, "_yscale");
			delete dataPlotAnim;
		}
		//-----------------------------------------------------------------//
	}
	/**
	* calcVLinesPos method calculates the x position for the various
	* vLines defined. Also, it validates them.
	*/
	private function calcVLinesPos ()
	{
		var i : Number;
		//Iterate through all the vLines
		for (i = 1; i <= numVLines; i ++)
		{
			//If the vLine is after 1st data and before last data
			if (this.vLines [i].index > 0 && this.vLines [i].index < this.num)
			{
				//Set it's x position
				this.vLines [i].x = this.categories [this.vLines [i].index].x + (this.categories [this.vLines [i].index + 1].x - this.categories [this.vLines [i].index].x) * this.vLines[i].linePosition;
			} else
			{
				//Invalidate it
				this.vLines [i].isValid = false;
			}
		}
	}
	/**
	* calculatePoints method calculates the various points on the chart.
	*/
	private function calculatePoints (isRedraw:Boolean)
	{
		//Loop variable
		var i : Number, j : Number;
		//If use has not explicitly defined numVisiblePlot, we need to assume a value.
		if(this.params.numVisiblePlot ==undefined || this.params.numVisiblePlot=="" || isNaN(this.params.numVisiblePlot)){
			this.params.numVisiblePlot = Math.floor((this.width*0.8)/60);
		} 
		// numVisiblePlot cannot be more than the max point or less than 2
		if(this.params.numVisiblePlot < 2 ||  this.params.numVisiblePlot > this.num)  {
			this.params.numVisiblePlot = this.num;
		}
		//Condition to check whether scroll is not required.
		if(this.params.numVisiblePlot < (this.num))  {
			this.scrollRequired = true;
		} else {
			this.scrollRequired = false;
		}
		//Format all the numbers on the chart and store their display values
		//We format and store here itself, so that later, whenever needed,
		//we just access displayValue instead of formatting once again.
		//Also set tool tip text values
		var toolText : String
		for (i = 1; i <= this.numDS; i ++)
		{
			for (j = 1; j <= this.num; j ++)
			{
				//Format and store
				this.dataset [i].data [j].displayValue = formatNumber (this.dataset [i].data [j].value, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
				//Store formatted values
				this.dataset [i].data [j].formattedValue = this.dataset [i].data [j].displayValue;
				//Format sum of values
				if (this.params.showSum && (i == 1)  && this.sums [j].displayValue==undefined)
				{
					this.sums [j].displayValue = formatNumber (this.sums [j].sum, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
				}
				//Tool tip text.
				//Preferential Order - Set Tool Text (No concatenation) > SeriesName + Cat Name + Value
				if (this.dataset [i].data [j].toolText == undefined || this.dataset [i].data [j].toolText == "")
				{
					//If the tool tip text is not already defined
					//If labels have been defined
					toolText = (this.params.seriesNameInToolTip && this.dataset [i].seriesName != "") ? (this.dataset [i].seriesName + this.params.toolTipSepChar) : "";
					toolText = toolText + ((this.categories [j].toolText != "") ? (this.categories [j].toolText + this.params.toolTipSepChar) : "");
					toolText = toolText + ((this.params.stack100Percent==true)?(this.dataset [i].data [j].toolTipDispVal):(this.dataset [i].data [j].displayValue));
					this.dataset [i].data [j].toolText = toolText;
				}
				//Choose display value for item (choice between explicit and actual value)
				if (this.dataset[i].data[j].exDispVal != "") {
					this.dataset[i].data[j].displayValue = this.dataset[i].data[j].exDispVal;
				}
			}
		}
		//We now need to calculate the available Width on the canvas.
		//Available width = total Chart width minus the list below
		// - Left and Right Margin
		// - yAxisName (if to be shown)
		// - yAxisValues
		// - Trend line display values (both left side and right side).
		// - Legend (If to be shown at right)
		var canvasWidth : Number = this.width - (this.params.chartLeftMargin + this.params.chartRightMargin);
		//Set canvas startX
		var canvasStartX : Number = this.params.chartLeftMargin;
		//Now, if y-axis name is to be shown, simulate it and get the width
		if (this.params.yAxisName != "")
		{
			//Get style object
			var yAxisNameStyle : Object = this.styleM.getTextStyle (this.objects.YAXISNAME);
			if (this.params.rotateYAxisName)
			{
				//Create text field to get width
				var yAxisNameObj : Object = createText (true, this.params.yAxisName, this.tfTestMC, 1, testTFX, testTFY, 90, yAxisNameStyle, true, this.height-(this.params.chartTopMargin+this.params.chartBottomMargin), canvasWidth/2);
				//Accomodate width and padding
				canvasStartX = canvasStartX + yAxisNameObj.width + this.params.yAxisNamePadding;
				canvasWidth = canvasWidth - yAxisNameObj.width - this.params.yAxisNamePadding;
				//Create element for yAxisName - to store width/height
				this.elements.yAxisName = returnDataAsElement (0, 0, yAxisNameObj.width, yAxisNameObj.height);
				this.params.yAxisNameWidth = yAxisNameObj.width;
			} else
			{
				//If the y-axis name is not to be rotated
				//Calculate the width of the text if in full horizontal mode
				//Create text field to get width
				var yAxisNameObj : Object = createText (true, this.params.yAxisName, this.tfTestMC, 1, testTFX, testTFY, 0, yAxisNameStyle, false, 0, 0);
				//Get a value for this.params.yAxisNameWidth
				this.params.yAxisNameWidth = Number (getFV (this.params.yAxisNameWidth, yAxisNameObj.width));
				//Get the lesser of the width (to avoid un-necessary space)
				this.params.yAxisNameWidth = Math.min (this.params.yAxisNameWidth, yAxisNameObj.width);
				//Accomodate width and padding
				canvasStartX = canvasStartX + this.params.yAxisNameWidth + this.params.yAxisNamePadding;
				canvasWidth = canvasWidth - this.params.yAxisNameWidth - this.params.yAxisNamePadding;
				//Create element for yAxisName - to store width/height
				this.elements.yAxisName = returnDataAsElement (0, 0, this.params.yAxisNameWidth, yAxisNameObj.height);
			}
			delete yAxisNameStyle;
			delete yAxisNameObj;
		}
	
		labelMetrics.updateProp("yAxisNameObj", {yAxisNameWidth: this.params.yAxisNameWidth, yAxisNamePadding: this.params.yAxisNamePadding});
		
		//Accomodate width for y-axis values. Now, y-axis values conists of two parts
		//(for backward compatibility) - limits (upper and lower) and div line values.
		//So, we'll have to individually run through both of them.
		var yAxisValMaxWidth : Number = 0;
		//Also store the height required to render the text field
		var yAxisValMaxHeight:Number = 0
		var divLineObj : Object;
		var divStyle : Object = this.styleM.getTextStyle (this.objects.YAXISVALUES);
		//Iterate through all the div line values
		for (i = 1; i < this.divLines.length; i ++)
		{
			//If div line value is to be shown
			if (this.divLines [i].showValue)
			{
				//If it's the first or last div Line (limits), and it's to be shown
				if ((i == 1) || (i == this.divLines.length - 1))
				{
					if (this.params.showLimits)
					{
						//Get the width of the text
						divLineObj = createText (true, this.divLines [i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, divStyle, false, 0, 0);
						//Accomodate
						yAxisValMaxWidth = (divLineObj.width > yAxisValMaxWidth) ? (divLineObj.width) : (yAxisValMaxWidth);
						yAxisValMaxHeight = (divLineObj.height > yAxisValMaxHeight) ? (divLineObj.height) : (yAxisValMaxHeight);
					}
				} else
				{
					//It's a div interval - div line
					//So, check if we've to show div line values
					if (this.params.showDivLineValues)
					{
						//Get the width of the text
						divLineObj = createText (true, this.divLines [i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, divStyle, false, 0, 0);
						//Accomodate
						yAxisValMaxWidth = (divLineObj.width > yAxisValMaxWidth) ? (divLineObj.width) : (yAxisValMaxWidth);
						yAxisValMaxHeight = (divLineObj.height > yAxisValMaxHeight) ? (divLineObj.height) : (yAxisValMaxHeight);
					}
				}
			}
		}
		delete divLineObj;
		//Also iterate through all trend lines whose values are to be shown on
		//left side of the canvas.
		//Get style object
		var trendStyle : Object = this.styleM.getTextStyle (this.objects.TRENDVALUES);
		var trendObj : Object;
		for (i = 1; i <= this.numTrendLines; i ++)
		{
			if (this.trendLines [i].isValid == true && this.trendLines [i].valueOnRight == false)
			{
				//If it's a valid trend line and value is to be shown on left
				//Get the width of the text
				trendObj = createText (true, this.trendLines [i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				yAxisValMaxWidth = (trendObj.width > yAxisValMaxWidth) ? (trendObj.width) : (yAxisValMaxWidth);
			}
		}
		//Accomodate for y-axis/left-trend line values text width
		if (yAxisValMaxWidth > 0)
		{
			canvasStartX = canvasStartX + yAxisValMaxWidth + this.params.yAxisValuesPadding;
			canvasWidth = canvasWidth - yAxisValMaxWidth - this.params.yAxisValuesPadding;
		}
		var trendRightWidth : Number = 0;
		//Now, also check for trend line values that fall on right
		for (i = 1; i <= this.numTrendLines; i ++)
		{
			if (this.trendLines [i].isValid == true && this.trendLines [i].valueOnRight == true)
			{
				//If it's a valid trend line and value is to be shown on right
				//Get the width of the text
				trendObj = createText (true, this.trendLines [i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				trendRightWidth = (trendObj.width > trendRightWidth) ? (trendObj.width) : (trendRightWidth);
			}
		}
		delete trendObj;
		//Accomodate trend right text width
		if (trendRightWidth > 0)
		{
			canvasWidth = canvasWidth - trendRightWidth - this.params.yAxisValuesPadding;
		}
		
		labelMetrics.updateProp("yAxisValueObj", {yAxisValMaxWidth: yAxisValMaxWidth, yAxisValuesPadding: this.params.yAxisValuesPadding, yAxisValueAtRight:trendRightWidth});
		
		//Round them off finally to avoid distorted pixels
		canvasStartX = int (canvasStartX);
		canvasWidth = int (canvasWidth);
		//We finally have canvas Width and canvas Start X
		//-----------------------------------------------------------------------------------//
		//Now, we need to calculate the available Height on the canvas.
		//Available height = total Chart height minus the list below
		// - Chart Top and Bottom Margins
		// - Space for Caption, Sub Caption and caption padding
		// - Height of data labels
		// - xAxisName
		// - Legend (If to be shown at bottom position)
		//Initialize canvasHeight to total height minus margins
		var canvasHeight : Number = this.height - (this.params.chartTopMargin + this.params.chartBottomMargin);
		//Set canvasStartY
		var canvasStartY : Number = this.params.chartTopMargin;
		//Now, if we've to show caption
		if (this.params.caption != "")
		{
			//Create text field to get height
			var captionObj : Object = createText (true, this.params.caption, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle (this.objects.CAPTION) , true, canvasWidth, canvasHeight/4);
			//Store the height
			canvasStartY = canvasStartY + captionObj.height;
			canvasHeight = canvasHeight - captionObj.height;
			//Create element for caption - to store width & height
			this.elements.caption = returnDataAsElement (0, 0, captionObj.width, captionObj.height);
			delete captionObj;
		}
		//Now, if we've to show sub-caption
		if (this.params.subCaption != "")
		{
			//Create text field to get height
			var subCaptionObj : Object = createText (true, this.params.subCaption, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle (this.objects.SUBCAPTION) , true, canvasWidth, canvasHeight/4);
			//Store the height
			canvasStartY = canvasStartY + subCaptionObj.height;
			canvasHeight = canvasHeight - subCaptionObj.height;
			//Create element for sub caption - to store height
			this.elements.subCaption = returnDataAsElement (0, canvasStartY, subCaptionObj.width, subCaptionObj.height);
			delete subCaptionObj;
		}
		//Now, if either caption or sub-caption was shown, we also need to adjust caption padding
		if (this.params.caption != "" || this.params.subCaption != "")
		{
			//Account for padding
			canvasStartY = canvasStartY + this.params.captionPadding;
			canvasHeight = canvasHeight - this.params.captionPadding;
		}
		//Reserve space for showing sum of data (in case of 10)% chart).
		if (this.params.showSum == true)
		{
			//Create text field to get height
			var sumObj : Object = createText (true, "!2345.6780", this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle (this.objects.DATAVALUES) , false, 0, 0);
			//Store the height
			canvasStartY = canvasStartY + sumObj.height + this.params.sumPadding;
			canvasHeight = canvasHeight - sumObj.height - this.params.sumPadding;
			//Store height of sum value
			this.elements.sumValues = returnDataAsElement (0, 0, 0, sumObj.height);
			delete sumObj;
		}
		//Accomodate space for scroll bar (if to be shown)
		if (this.scrollRequired){
			canvasHeight = canvasHeight - this.params.scrollHeight - this.params.scrollPadding;
		}
		//-------- Begin: Data Label calculation --------------//
		var labelObj : Object;
		var labelStyleObj : Object = this.styleM.getTextStyle (this.objects.DATALABELS);		
		//Maximum allowable label height	
		this.config.maxAllowableLabelHeight = canvasHeight/3;
		//Based on label step, set showLabel of each data point as required.
		//Visible label count
		var visibleCount : Number = 0;
		for (i=1; i<=this.num; i++) {
			//Now, the label can be preset to be hidden (set via XML)
			if (this.categories[i].showLabel) {
				visibleCount++;
			}
		}
		//First if the label display is set as Auto, figure the best way 
		//to render them.
		if (this.params.labelDisplay=="AUTO"){
			//Here, we check how many labels are to be shown.
			//Based on that, we decide a fit between WRAP and ROTATE
			//Priority is given to WRAP mode over ROTATE mode
			//WRAP mode is set when at least 5 characters can be shown
			//on the chart, without having to break up into multiple lines.
			//Else, rotate mode is selected.
			//If in rotate mode and a single line cannot be fit, we also 
			//set labelStep, based on available space.
			//Get the width required to plot the characters in wrap mode
			//Storage for string to be simulated.
			var str:String = "";
			//Minimum width required to plot this string in both wrap
			//and rotate mode.
			var wrapMinWidth:Number = 0, rotateMinWidth:Number=0;
			//Width of total drawing canvas
			var canvasTotalWidth:Number = (canvasWidth*this.num)/this.params.numVisiblePlot;
			//Build the string by adding upper-case letters A,B,C,D...
			for (i=1; i<=WRAP_MODE_MIN_CHARACTERS; i++){
				//Build the string from upper case A,B,C...
				str = str + String.fromCharCode(64+i);
			}
			//Simulate width of this text field - without wrapping
			labelObj = createText (true, str, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
			wrapMinWidth = labelObj.width;
			//If we've space to accommodate this width for all labels, render in wrap mode
			if (visibleCount*wrapMinWidth <= canvasTotalWidth){
				//Render all labels in wrap mode
				this.params.labelDisplay="WRAP";
			}else{
				//Since we do not have space to accommodate the width of wrap mode:
				//Render in rotate mode
				this.params.labelDisplay="ROTATE";
				//Figure the minimum width required to display 1 line of text in rotated mode				
				labelObj = createText (true, str, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, false, 0, 0);
				//Store the mimimum width required for rotated mode
				rotateMinWidth = labelObj.width;
				//If all the labels cannot be accommodated in minimum width (without getting trimmed),
				//then go for labelStep
				if ((visibleCount/this.params.labelStep)*rotateMinWidth>= canvasTotalWidth){
					//Figure out how many labels we can display
					var numFitLabels:Number = (canvasTotalWidth/rotateMinWidth);
					//Based on how many labels we've to display and how many we can, reset labelStep
					this.params.labelStep = Math.ceil(visibleCount/numFitLabels);					
				}
			}
		}
		//Now, based on user set/auto-calculated label step, set showLabel 
		//of each data point as required.
		//Reset Visible label count
		visibleCount = 0;
		var finalVisibleCount : Number = 0;
		for (i = 1; i <= this.num; i ++)
		{
			//Now, the label can be preset to be hidden (set via XML)
			if (this.categories [i].showLabel)
			{
				visibleCount ++;
				//If label step is defined, we need to set showLabel of those
				//labels which fall on step as false.
				if ((i - 1) % this.params.labelStep == 0)
				{
					this.categories [i].showLabel = true;
				} else
				{
					this.categories [i].showLabel = false;
				}
			}
			//Update counter
			finalVisibleCount = (this.categories [i].showLabel) ? (finalVisibleCount + 1) : (finalVisibleCount);
		}
		//Store the final visible count
		this.config.finalVisibleCount = finalVisibleCount;
		//now depending on the final showLabel property on fisrt and last label and its display mode
		//we set the first label width property for improve label management. This values will only be used
		//when we go for new label management.
		var maxAllowableLabelHeight:Number = canvasHeight/3;
		if(this.categories [1].showLabel){
			if(this.params.labelDisplay == "ROTATE"){
				labelObj = getTextMetrics(this.categories [1].label, labelStyleObj, maxAllowableLabelHeight);
				labelMetrics.firstLabelWidth = labelObj.textFieldHeight;				
			}else{
				labelObj = getTextMetrics(this.categories [1].label, labelStyleObj);
				labelMetrics.firstLabelWidth = labelObj.textFieldWidth;
			}
		}else{
			labelMetrics.firstLabelWidth = 0;
		}
		if(this.categories [this.num].showLabel){
			if(this.params.labelDisplay == "ROTATE"){
				labelObj = getTextMetrics(this.categories [this.num].label, labelStyleObj, maxAllowableLabelHeight);
				labelMetrics.lastLabelWidth = labelObj.textFieldHeight;				
			}else{
				labelObj = getTextMetrics(this.categories [this.num].label, labelStyleObj);
				labelMetrics.lastLabelWidth = labelObj.textFieldWidth;
			}
		}else{
			labelMetrics.lastLabelWidth = 0;
		}
		labelObj = null;
		//Accomodate space for xAxisName (if to be shown);
		if (this.params.xAxisName != "")
		{
			//Create text field to get height
			var xAxisNameObj : Object = createText (true, this.params.xAxisName, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle (this.objects.XAXISNAME) , true, canvasWidth, canvasHeight/2);
			//Store the height
			canvasHeight = canvasHeight - xAxisNameObj.height - this.params.xAxisNamePadding;
			//Object to store width and height of xAxisName
			this.elements.xAxisName = returnDataAsElement (0, 0, xAxisNameObj.width, xAxisNameObj.height);
			delete xAxisNameObj;
		}
		//We have canvas start Y and canvas height
		//We now check whether the legend is to be drawn
		if (this.params.showLegend)
		{
			
			//Object to store dimensions
			var lgndDim : Object;
			
			//MC removed if present before recreation of the MC
			if(this.lgndMC){
				this.lgndMC.removeMovieClip();
			}

			//Create container movie clip for legend
			this.lgndMC = this.cMC.createEmptyMovieClip ("Legend", this.dm.getDepth ("LEGEND"));
			
			//Create instance of legend
			if (this.params.legendPosition == "BOTTOM")
			{
				//Maximum Height - 50% of stage
				lgnd = new AdvancedLegend (lgndMC, this.styleM.getTextStyle (this.objects.LEGEND) , this.params.interactiveLegend , this.params.legendPosition, canvasStartX + canvasWidth / 2, this.height / 2, canvasWidth, (this.height - (this.params.chartTopMargin + this.params.chartBottomMargin)) * 0.5, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			} 
			else 
			{
				//Maximum Width - 40% of stage
				lgnd = new AdvancedLegend (lgndMC, this.styleM.getTextStyle (this.objects.LEGEND) , this.params.interactiveLegend , this.params.legendPosition, this.width / 2, canvasStartY + canvasHeight / 2, (this.width - (this.params.chartLeftMargin + this.params.chartRightMargin)) * 0.4, canvasHeight, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			}
			
			if(this.params.minimiseWrappingInLegend){
				lgnd.minimiseWrapping = true;
			}
			
			//Get the height of single line text for legend items (to be used to specify icon width and height)
			var iconHeight:Number = lgnd.getIconHeight()*this.params.legendIconScale;
			
			var j:Number;
			var objIcon:Object;
			
			for (i = 1; i <= this.numDS; i ++)
			{
				//Adjust working index for reverseLegend option
				j = (this.params.reverseLegend) ? this.numDS - i + 1 : i;
				//Validation of item eligibility
				if (this.dataset [j].includeInLegend && this.dataset [j].seriesName != "")
				{
					objIcon = {fillColor: parseInt(this.dataset [j].color, 16), intraIconPaddingPercent: 0.3};
					//Create the 2 icons
					var objIcons:Object = LegendIconGenerator.getIcons(LegendIconGenerator.COLUMN, iconHeight, true, objIcon);
					//State specific icon BitmapData
					var bmpd1:BitmapData = objIcons.active;
					var bmpd2:BitmapData = objIcons.inactive;
					//Add the item to legend
					lgnd.addItem (this.dataset [j].seriesName, j, true, bmpd1, bmpd2);
				}
			}			
			
			//If user has defined a caption for the legend, set it
			if (this.params.legendCaption!=""){
				lgnd.setCaption(this.params.legendCaption);
			}
			
			if (this.params.legendPosition == "BOTTOM")
			{
				lgndDim = lgnd.getDimensions ();
				//Now deduct the height from the calculated canvas height
				canvasHeight = canvasHeight - lgndDim.height - this.params.legendPadding;
				//Re-set the legend position
				this.lgnd.resetXY (canvasStartX + canvasWidth / 2, this.height - this.params.chartBottomMargin - lgndDim.height / 2);
			}
			else
			{
				//Get dimensions
				lgndDim = lgnd.getDimensions ();
				//Now deduct the width from the calculated canvas width
				canvasWidth = canvasWidth - lgndDim.width - this.params.legendPadding;
				//Right position
				this.lgnd.resetXY (this.width - this.params.chartRightMargin - lgndDim.width / 2, canvasStartY + canvasHeight / 2);
			}
			
			labelMetrics.updateProp("legendObj", {legendPosition: this.params.legendPosition, legendWidth: lgndDim.width, legendPadding:this.params.legendPadding});
			
		}
		//----------- HANDLING CUSTOM CANVAS MARGINS --------------//
		//----------------------Non Scroll Mode------------------------------------//
		if(!this.scrollRequired){
			//Data labels can be rendered in 3 ways:
			//1. Normal - no staggering - no wrapping - no rotation
			//2. Wrapped - no staggering - no rotation
			//3. Staggered - no wrapping - no rotation
			//4. Rotated - no staggering - no wrapping
			//Placeholder to store max height
			this.config.maxLabelHeight = 0;
			this.config.labelAreaHeight = 0;
			if (this.params.labelDisplay == "ROTATE")
			{
				//Calculate the maximum width that could be alloted to the labels.
				//Note: Here, width is calculated based on canvas height, as the labels
				//are going to be rotated.
				var maxLabelWidth : Number = (canvasHeight / 3);
				var maxLabelHeight : Number = ((canvasWidth*this.num)/this.params.numVisiblePlot)/ finalVisibleCount;
				//Store it in config for later usage
				this.config.wrapLabelWidth = maxLabelWidth;
				this.config.wrapLabelHeight = maxLabelHeight;
				//Case 4: If the labels are rotated, we iterate through all the string labels
				//provided to us and get the height and store max.
				for (i = 1; i <= this.num; i ++)
				{
					//If the label is to be shown
					if (this.categories [i].showLabel)
					{
						//Create text box and get height
						labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
						//Store the larger
						this.config.maxLabelHeight = (labelObj.height > this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
					}
				}
				//Store max label height as label area height.
				this.config.labelAreaHeight = this.config.maxLabelHeight;
				//for slanted labels the maxLabelHeight may exceed the limit defined for the height. 
				//we take the mimimum of two
				//Store max label height as label area height.
				this.config.labelAreaHeight = Math.min(maxLabelWidth, this.config.maxLabelHeight);
			} else if (this.params.labelDisplay == "WRAP")
			{
				//Case 2 (WRAP): Create all the labels on the chart. Set width as
				//((canvasWidth*this.num)/this.params.numVisiblePlot)/ finalVisibleCount);
				//Set max height as 50% of available canvas height at this point of time. Find all
				//and select the max one.
				var maxLabelWidth : Number = ((canvasWidth*this.num)/this.params.numVisiblePlot)/ finalVisibleCount;
				var maxLabelHeight : Number = (canvasHeight / 3);
				//Store it in config for later usage
				this.config.wrapLabelWidth = maxLabelWidth;
				this.config.wrapLabelHeight = maxLabelHeight;
				for (i = 1; i <= this.num; i ++)
				{
					//If the label is to be shown
					if (this.categories [i].showLabel)
					{
						//Create text box and get height
						labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
						//Store the larger
						this.config.maxLabelHeight = (labelObj.height > this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
					}
				}
				//Store max label height as label area height.
				this.config.labelAreaHeight = this.config.maxLabelHeight;
			} else
			{
				//Case 1,3: Normal or Staggered Label
				//We iterate through all the labels, and if any of them has &lt or < (HTML marker)
				//embedded in them, we add them to the array, as for them, we'll need to individually
				//create and see the text height. Also, the first element in the array - we set as
				//ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=....
				//Create array to store labels.
				var strLabels : Array = new Array ();
				strLabels.push ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=/*-+~`");
				//Now, iterate through all the labels and for those visible labels, whcih have < sign,
				//add it to array.
				for (i = 1; i <= this.num; i ++)
				{
					//If the label is to be shown
					if (this.categories [i].showLabel)
					{
						if ((this.categories [i].label.indexOf ("&lt;") > - 1) || (this.categories [i].label.indexOf ("<") > - 1))
						{
							strLabels.push (this.categories [i].label);
						}
					}
				}
				//Now, we've the array for which we've to check height (for each element).
				for (i = 0; i < strLabels.length; i ++)
				{
					//Create text box and get height
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
					//Store the larger
					this.config.maxLabelHeight = (labelObj.height > this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
				}
				//We now have the max label height. If it's staggered, then store accordingly, else
				//simple mode
				if (this.params.labelDisplay == "STAGGER")
				{
					//Here we again validate the stagger lines. The minimum is validated at the time of parsing
					//the maximum is validated here.
					if(this.params.staggerLines > this.num){
						this.params.staggerLines = this.num;
					}
					//Multiply max label height by stagger lines.
					this.config.labelAreaHeight = this.params.staggerLines * this.config.maxLabelHeight;
				} else
				{
					this.config.labelAreaHeight = this.config.maxLabelHeight;
				}
			}
			if (this.config.labelAreaHeight > 0)
			{
				//Deduct the calculated label height from canvas height
				canvasHeight = canvasHeight - this.config.labelAreaHeight - this.params.labelPadding;
			}
		}
		//----------------------EoF Non Scroll Mode------------------------------------//
		//Delete objects
		delete labelObj;
		delete labelStyleObj;
		//Before doing so, we take into consideration, user's forced canvas margins (if any defined)
		//If the user's forced values result in overlapping of chart items, we ignore.
		if (this.params.canvasLeftMargin!=-1 && this.params.canvasLeftMargin>canvasStartX){
			//Update width (deduct the difference)
			canvasWidth = canvasWidth - (this.params.canvasLeftMargin-canvasStartX);
			//Update left start position
			canvasStartX = this.params.canvasLeftMargin;		
			//valid left margin is defined
			this.validCanvasLeftMargin = true;
		}
		if (this.params.canvasRightMargin!=-1 && (this.params.canvasRightMargin>(this.width - (canvasStartX+canvasWidth)))){
			//Update width (deduct the difference)
			canvasWidth = canvasWidth - (this.params.canvasRightMargin-(this.width - (canvasStartX+canvasWidth)));			
			//valid right margin is defined
			this.validCanvasRightMargin = true;
		}
		if (this.params.canvasTopMargin!=-1 && this.params.canvasTopMargin>canvasStartY){
			//Update height (deduct the difference)
			canvasHeight = canvasHeight - (this.params.canvasTopMargin-canvasStartY);
			//Update top start position
			canvasStartY = this.params.canvasTopMargin;		
		}
		if (this.params.canvasBottomMargin!=-1 && (this.params.canvasBottomMargin>(this.height - (canvasStartY+canvasHeight)))){
			//Update height(deduct the difference)
			canvasHeight = canvasHeight - (this.params.canvasBottomMargin-(this.height - (canvasStartY+canvasHeight)));
		}
		
		labelMetrics.updateProp("canvasMarginObj", {canvasLeftMargin: this.params.canvasLeftMargin, canvasRightMargin: this.params.canvasRightMargin,
													validCanvasLeftMargin:this.validCanvasLeftMargin, validCanvasRightMargin:this.validCanvasRightMargin});
		
		//------------ END OF CUSTOM CANVAS MARGIN HANDLING --------------------//
		//Based on canvas height that has been calculated, re-adjust yaxisLabelStep
		this.adjustYAxisLabelStep(yAxisValMaxHeight, canvasHeight);
		//Create an element to represent the canvas now.
		this.elements.canvas = returnDataAsElement (canvasStartX, canvasStartY, canvasWidth, canvasHeight);
		//We now need to calculate the position of columns on the chart.
		//Base Plane position - Base plane is the y-plane from which columns start.
		//If there's a 0 value in between yMin,yMax, base plane represents 0 value.
		//Else, it's yMin
		if (this.config.yMax >= 0 && this.config.yMin < 0)
		{
			//Negative number present - so set basePlanePos as 0 value
			this.config.basePlanePos = this.getAxisPosition (0, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
		} else if (this.config.yMax < 0 && this.config.yMin < 0)
		{
			//All negative numbers - so set basePlanePos as yMax value
			this.config.basePlanePos = this.getAxisPosition (this.config.yMax, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
		} else
		{
			//No negative numbers - so set basePlanePos as yMin value
			this.config.basePlanePos = this.getAxisPosition (this.config.yMin, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
		}
		
		//We now need to calculate column width and spacing between the same.
		var columnWidth:Number = this.elements.canvas.w * ( (100-this.params.plotSpacePercent) / 100) / this.params.numVisiblePlot;
		//validate column width 
		if(columnWidth > this.params.maxColWidth){
			columnWidth = this.params.maxColWidth;
		}
		//Block Width
		var visibleColWidth : Number =  columnWidth*this.params.numVisiblePlot;
		var visibleSpaceWidth:Number = this.elements.canvas.w - visibleColWidth;
		//Number of spaces visible
		var numSpacesVisible:Number;
		//Width for each spaces
		var unitSpaceWidth:Number;
		//Calculate the no of visible space and each space
		numSpacesVisible = this.params.numVisiblePlot+1;
		unitSpaceWidth = visibleSpaceWidth/numSpacesVisible;
		//Update the percentage
		this.params.plotSpacePercent = visibleSpaceWidth*100/this.elements.canvas.w;
		
		//We finally have total plot space and column width
		var totColWidth:Number = columnWidth * this.num ;
		var plotSpace:Number = unitSpaceWidth * (this.num + 1);
		//Get space between two blocks
		var interColSpace : Number = unitSpaceWidth;
		//Store in config.
		this.config.interBlockSpace = interColSpace;
		this.config.columnWidth = columnWidth;
		this.config.blockWidth = columnWidth;
		this.config.plotSpace = plotSpace;
		//Store in config.
		this.config.interColSpace = interColSpace;
			
		//Flag indicating whether first point has been found in this column
		var firstDataFound:Boolean = false;
		//End position of data
		var dataEndY : Number, dataValue:Number;
		//reset canvas padding
		labelMetrics.canvasPadding = this.config.interBlockSpace + (this.config.blockWidth / 2);
		//For scroll mode label management 
		if(this.scrollRequired){
			this.config.unitLabelWidth = this.config.interBlockSpace + this.config.blockWidth;
			//Left panel changed width
			this.config.leftPanelWidth = this.config.interBlockSpace + this.config.blockWidth/2;
			//Right panel changed width
			this.config.rightPanelWidth = this.config.interBlockSpace + this.config.blockWidth/2;
			//Store all labels simulation width
			getSimulationWidth();
			//Manage labaels in scroll Mode
			manageScrollLabels();
			//Deduct label area height from canvas height
			if (this.config.labelAreaHeight > 0)
			{
				//Deduct the calculated label height from canvas height
				canvasHeight = canvasHeight - this.config.labelAreaHeight - this.params.labelPadding;
			}
			//Based on canvas height that has been calculated, re-adjust yaxisLabelStep for scroll mode
			this.adjustYAxisLabelStep(yAxisValMaxHeight, canvasHeight);
			//Recreate canvas element.
			this.elements.canvas = returnDataAsElement (canvasStartX, canvasStartY, canvasWidth, canvasHeight);
			//We now need to recalculate the position of columns on the chart.
			if (this.config.yMax >= 0 && this.config.yMin < 0)
			{
				//Negative number present - so set basePlanePos as 0 value
				this.config.basePlanePos = this.getAxisPosition (0, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
			} else if (this.config.yMax < 0 && this.config.yMin < 0)
			{
				// All Negative number present - so set basePlanePos as Max value
				this.config.basePlanePos = this.getAxisPosition (this.config.yMax, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
			} else 
			{
				//No negative numbers - so set basePlanePos as yMin value
				this.config.basePlanePos = this.getAxisPosition (this.config.yMin, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
			}
		}
		//now calculate the panels
		//check if new label management is required
		if(this.params.XTLabelManagement && !this.scrollRequired){
			labelMetrics.calculatePanels();
			//now we should go for final label management calculation
			var metricsObj:Object = labelMetrics.getChartMetrics( this.num, labelMetrics.totalWorkingObj.totalWorkingWidth, 
												  labelMetrics.leftPanelObj.leftPanelMinWidth, labelMetrics.rightPanelObj.rightPanelMinWidth, 
												  labelMetrics.firstLabelWidth, labelMetrics.lastLabelWidth,
												  labelMetrics.changeCanvasPadding);
			//store globally.
			this.config.labelMetricsObj = metricsObj;
			//based on the new interplot width and panel we need to recalculate the interPlotSpace
			//and column width - Following are derived formulas by numeric analysis.
			var plotSpaceRatio:Number = (this.num * this.params.plotSpacePercent / 100) / (this.num - this.params.plotSpacePercent / 100 + 1);
			
			var newBlockWidth:Number =  (1 - plotSpaceRatio) / (this.num - 1) * metricsObj.totalInterPlotWidth;
			var newColmnWidth:Number = newBlockWidth / this.num;
			//var newColumnWidth:Number = ( (1 - plotSpaceRatio) / (this.num - 1) * metricsObj.totalInterPlotWidth);
			var newSpaceWidth:Number = ( plotSpaceRatio / (this.num - 1) * metricsObj.totalInterPlotWidth);
			var newCanvasWidth:Number = ( (this.num + plotSpaceRatio) / (this.num - 1) * metricsObj.totalInterPlotWidth);
			//as the new canvas width is always les than the initial
			var amountOfDiff:Number = canvasWidth - newCanvasWidth;
			
		}
		
		
		
		//Now, store the positions of the columns
		for (i = 1; i <= this.num; i ++)
		{
			//For this column, reset flag
			firstDataFound = false;
			//Position co-ordinates
			var posY : Number = 0;
			var negY : Number = 0;
			//Index of last positive and last negative column in the stack
			var lastPosIndex:Number = -1;
			var lastNegIndex:Number = -1;
			//Categories x-Position
			if(this.params.XTLabelManagement && !this.scrollRequired){
				this.categories [i].x = (newSpaceWidth * i) + columnWidth * (i - 0.5);
			}else{
				this.categories [i].x = (this.config.interBlockSpace * i) + columnWidth * (i - 0.5);
			}
			//Iterate through all data sets for each index
			for (j = 1; j <= this.numDS; j ++)
			{
				if (this.dataset [j].data [i].isDefined)
				{
					//X-Position
					this.dataset [j].data [i].x = this.categories [i].x;
					//Store the data value
					dataValue = this.dataset [j].data [i].value;
					//If yMin is greater than 0 and it's not the first value
					if (this.config.yMin>0 && firstDataFound==true){
						//Add y-min value to calibrate on scale
						dataValue = dataValue+this.config.yMin;
					}
					//Height for each column
					dataEndY = this.getAxisPosition (dataValue, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
					//Negate to cancel Flash's reverse Y Co-ordinate system
					this.dataset [j].data [i].h = - (dataEndY - this.config.basePlanePos);
					//Set the y position
					this.dataset [j].data [i].y = (this.dataset [j].data [i].h >= 0) ?posY : negY;
					//Store value textbox y position
					this.dataset [j].data [i].valTBY = this.config.basePlanePos + this.dataset [j].data [i].y - (this.dataset [j].data [i].h / 2) - this.elements.canvas.y;
					//Update y-co ordinates
					if (this.dataset [j].data [i].h >= 0)
					{
						posY = posY - this.dataset [j].data [i].h;
						lastPosIndex = j;
					} else
					{
						negY = negY - this.dataset [j].data [i].h;
						lastNegIndex = j;
					}
					//Width
					this.dataset [j].data [i].w = columnWidth;
					//Update flag
					firstDataFound = true;
				}
			}
			//Store positive end Y and negative end Y in sums
			this.sums [i].pY = posY;
			this.sums [i].nY = negY;
			//Also update corner radius of last positive and last negative column
			if (lastPosIndex!=-1){
				this.dataset[lastPosIndex].data[i].cornerRadius = this.roundEdgeRadius;
			}
			if (lastNegIndex!=-1){
				this.dataset[lastNegIndex].data[i].cornerRadius = this.roundEdgeRadius;
			}
		}
	}
	/**
	* allotDepths method allots the depths for various chart objects
	* to be rendered. We do this before hand, so that we can later just
	* go on rendering chart objects, without swapping.
	*/
	private function allotDepths () : Void
	{
		//Background
		this.dm.reserveDepths ("BACKGROUND", 1);
		//Click URL Handler
		this.dm.reserveDepths ("CLICKURLHANDLER", 1);
		//Background SWF
		this.dm.reserveDepths ("BGSWF", 1);
		//Canvas
		this.dm.reserveDepths ("CANVAS", 1);
		//If horizontal grid is to be shown
		if (this.params.showAlternateHGridColor)
		{
			this.dm.reserveDepths ("HGRID", Math.ceil ((this.divLines.length + 1) / 2));
		}
		//Div Lines and their labels
		this.dm.reserveDepths ("DIVLINES", (this.divLines.length * 2));
		//Caption
		this.dm.reserveDepths ("CAPTION", 1);
		//Sub-caption
		this.dm.reserveDepths ("SUBCAPTION", 1);
		//X-Axis Name
		this.dm.reserveDepths ("XAXISNAME", 1);
		//Y-Axis Name
		this.dm.reserveDepths ("YAXISNAME", 1);
		//Trend lines below plot (lines and their labels)
		this.dm.reserveDepths ("TRENDLINESBELOW", this.numTrendLinesBelow);
		this.dm.reserveDepths ("TRENDVALUESBELOW", this.numTrendLinesBelow);
		//Depth for the scroll content movie
		this.dm.reserveDepths ("SCROLLCONTENT", 1);
		//Mask for scrolling
		this.dm.reserveDepths ("SCROLLMASK", 1);		
		//Vertical div lines
		this.dm.reserveDepths ("VLINES", this.numVLines);
		//Vertical div lines labels
		this.dm.reserveDepths ("VLINELABELS", this.numVLines);
		//Data Labels
		this.dm.reserveDepths ("DATALABELS", this.num);
		//Data Columns
		this.dm.reserveDepths ("DATAPLOT", this.num);
		//Zero Plane
		this.dm.reserveDepths ("ZEROPLANE", 2);
		//Trend lines below plot (lines and their labels)
		this.dm.reserveDepths ("TRENDLINESABOVE", (this.numTrendLines - this.numTrendLinesBelow));
		this.dm.reserveDepths ("TRENDVALUESABOVE", (this.numTrendLines - this.numTrendLinesBelow));
		//Canvas Border
		this.dm.reserveDepths ("CANVASBORDER", 1);
		//Scroll bar
		this.dm.reserveDepths ("SCROLLBAR", 1);
		//Data Values
		this.dm.reserveDepths ("DATAVALUES", this.num * this.numDS);
		//Data Sums
		this.dm.reserveDepths ("DATASUM", this.num);
		//Legend
		this.dm.reserveDepths ("LEGEND", 1);
	}
	//--------------- VISUAL RENDERING METHODS -------------------------//
	/**
	 * createContainerMC method creates the containers for various scrolling
	 * related movie clips.
	*/
	private function createContainerMC():Void{
		//Create the scrollable movie clip (parent)
		this.scrollableMC = this.cMC.createEmptyMovieClip("ScrollableMC",this.dm.getDepth("SCROLLCONTENT"));
		//Inside scrollableMC, we need to create 2 movie clips - Content and Ref.
		this.scrollRefMC = this.scrollableMC.createEmptyMovieClip("RefMC",1);
		this.scrollContentMC = this.scrollableMC.createEmptyMovieClip("ContentMC",2);
		
		//Create empty movie clip containers for mask and scroll bar container
		this.maskMC = this.cMC.createEmptyMovieClip("MaskMC",this.dm.getDepth("SCROLLMASK"));
		this.scrollBarMC = this.cMC.createEmptyMovieClip("ScrollBarMC",this.dm.getDepth("SCROLLBAR"));
		//Reposition the scrollableMC at the beginning of canvas
		this.scrollableMC._x = this.elements.canvas.x;
		this.scrollableMC._y = this.elements.canvas.y;
		//Position the scroll bar too
		//Scroll bar must align with canvas left edge.
		this.scrollBarMC._x = this.elements.canvas.x;
		this.scrollBarMC._y = this.elements.canvas.toY + this.params.scrollPadding;
		//Inside Ref MC, we need to draw a line from 0,0 to max possible width.
		//Draw a hidden line - we create a hidden line spanning the whole width of the chart - since flash internally
		//returns the width of the MC as the width of the API drawn within and we need width with respect to starting
		//position of the API in the parent MC. So, we have created a maximum possible line in the parent MC and hence
		//flash returns the actual width of the MC which we require.
		this.scrollRefMC.lineStyle(1, 0x000000, 0);
		this.scrollRefMC.moveTo(0, 0);
		this.scrollRefMC.lineTo((this.categories[this.num].x+this.config.interColSpace+(this.config.columnWidth * 0.5)), 0);		
	}
	/**
	* drawHeaders method renders the following on the chart:
	* CAPTION, SUBCAPTION, XAXISNAME, YAXISNAME
	*/
	private function drawHeaders ()
	{
		//Render caption
		if (this.params.caption != "")
		{
			var captionStyleObj : Object = this.styleM.getTextStyle (this.objects.CAPTION);
			captionStyleObj.vAlign = "bottom";
			//Switch the alignment to lower case
			captionStyleObj.align = captionStyleObj.align.toLowerCase();
			//Now, based on alignment, decide the xPosition of the caption
			var xPos:Number = (captionStyleObj.align=="center")?(this.elements.canvas.x + (this.elements.canvas.w / 2)):((captionStyleObj.align=="left")?(this.elements.canvas.x):(this.elements.canvas.toX));
			var captionObj : Object = createText (false, this.params.caption, this.cMC, this.dm.getDepth ("CAPTION") , xPos , this.params.chartTopMargin, 0, captionStyleObj, true, this.elements.caption.w, this.elements.caption.h);
			//Apply animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (captionObj.tf, this.objects.CAPTION, this.macro, captionObj.tf._x , 0, captionObj.tf._y , 0, 100, null, null, null);
			}
			//Apply filters
			this.styleM.applyFilters (captionObj.tf, this.objects.CAPTION);
			//Delete
			delete captionObj;
			delete captionStyleObj;
		}
		//Render sub caption
		if (this.params.subCaption != "")
		{
			var subCaptionStyleObj : Object = this.styleM.getTextStyle (this.objects.SUBCAPTION);
			subCaptionStyleObj.vAlign = "top";
			//Switch the alignment to lower case
			subCaptionStyleObj.align = subCaptionStyleObj.align.toLowerCase();
			//Now, based on alignment, decide the xPosition of the caption
			var xPos:Number = (subCaptionStyleObj.align=="center")?(this.elements.canvas.x + (this.elements.canvas.w / 2)):((subCaptionStyleObj.align=="left")?(this.elements.canvas.x):(this.elements.canvas.toX));
			var subCaptionObj : Object = createText (false, this.params.subCaption, this.cMC, this.dm.getDepth ("SUBCAPTION") , xPos , this.elements.subCaption.y, 0, subCaptionStyleObj, true, this.elements.subCaption.w, this.elements.subCaption.h);
			//Apply animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (subCaptionObj.tf, this.objects.SUBCAPTION, this.macro, subCaptionObj.tf._x , 0, subCaptionObj.tf._y, 0, 100, null, null, null);
			}
			//Apply filters
			this.styleM.applyFilters (subCaptionObj.tf, this.objects.SUBCAPTION);
			//Delete
			delete subCaptionObj;
			delete subCaptionStyleObj;
		}
		//Render x-axis name
		if (this.params.xAxisName != "")
		{
			var xAxisNameStyleObj : Object = this.styleM.getTextStyle (this.objects.XAXISNAME);
			xAxisNameStyleObj.align = "center";
			xAxisNameStyleObj.vAlign = "bottom";
			var xAxisNameObj : Object = createText (false, this.params.xAxisName, this.cMC, this.dm.getDepth ("XAXISNAME") , this.elements.canvas.x + (this.elements.canvas.w / 2) , this.elements.canvas.toY + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0)) + this.params.labelPadding + this.config.labelAreaHeight + this.params.xAxisNamePadding, 0, xAxisNameStyleObj, true, this.elements.xAxisName.w, this.elements.xAxisName.h);
			//Apply animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (xAxisNameObj.tf, this.objects.XAXISNAME, this.macro, this.elements.canvas.x + (this.elements.canvas.w / 2) - (this.elements.subCaption.w / 2) , 0, this.elements.canvas.toY + this.config.labelAreaHeight + this.params.xAxisNamePadding, 0, 100, null, null, null);
			}
			//Apply filters
			this.styleM.applyFilters (xAxisNameObj.tf, this.objects.XAXISNAME);
			//Delete
			delete xAxisNameObj;
			delete xAxisNameStyleObj;
		}
		//Render y-axis name
		if (this.params.yAxisName != "")
		{
			var yAxisNameStyleObj : Object = this.styleM.getTextStyle (this.objects.YAXISNAME);
			//Set alignment parameters
			yAxisNameStyleObj.align = "left";
			yAxisNameStyleObj.vAlign = "middle";
			//If the name is to be rotated
			if (this.params.rotateYAxisName)
			{
				if(this.params.centerYAxisName){
					//Center y axis name with respect to chart.
					var yAxisNameObj : Object = createText(false, this.params.yAxisName, this.cMC, this.dm.getDepth("YAXISNAME"), this.params.chartLeftMargin, this.height/2, 270, yAxisNameStyleObj, true, this.elements.yAxisName.h, this.elements.yAxisName.w);
				} else {
					//Center y axis name with respect to canvas.
					var yAxisNameObj : Object = createText (false, this.params.yAxisName, this.cMC, this.dm.getDepth ("YAXISNAME") , this.params.chartLeftMargin, this.elements.canvas.y + (this.elements.canvas.h / 2), 270, yAxisNameStyleObj, true, this.elements.yAxisName.h, this.elements.yAxisName.w);
				}
				//Apply animation
				if (this.params.animation)
				{
					this.styleM.applyAnimation (yAxisNameObj.tf, this.objects.YAXISNAME, this.macro, yAxisNameObj.tf._x, 0, yAxisNameObj.tf._y , 0, 100, null, null, null);
				}
			} else
			{
				//We show horizontal name
				//Adding 1 to this.params.yAxisNameWidth and then passing to avoid line breaks
				var yAxisNameObj : Object = createText (false, this.params.yAxisName, this.cMC, this.dm.getDepth ("YAXISNAME") , this.params.chartLeftMargin, this.elements.canvas.y + (this.elements.canvas.h / 2) , 0, yAxisNameStyleObj, true, this.params.yAxisNameWidth + 1, this.elements.canvas.h);
				//Apply animation
				if (this.params.animation)
				{
					this.styleM.applyAnimation (yAxisNameObj.tf, this.objects.YAXISNAME, this.macro, this.params.chartLeftMargin, 0, yAxisNameObj.tf._y, 0, 100, null, null, null);
				}
			}
			//Apply filters
			this.styleM.applyFilters (yAxisNameObj.tf, this.objects.YAXISNAME);
			//Delete
			delete yAxisNameObj;
			delete yAxisNameStyleObj;
		}
		//Clear Interval
		clearInterval (this.config.intervals.headers);
	}
	/**
	 * createScrollBar method creates the scroll bar
	*/
	private function createScrollBar(){
		//Initialize the scroll bar if required
		if (this.scrollRequired){
			var maskYShift:Number = ((this.params.showSum==true)?(this.elements.sumValues.h):(0));
			//The width of the scroller bar is now same with the canvas width. Because in scroll charts canvas-border is drawn outside canvas.
			this.scrollB = new FCChartHScrollBar(this.scrollableMC, this.scrollRefMC, this.scrollBarMC, this.maskMC, this.elements.canvas.w, maskYShift + this.elements.canvas.h + this.params.scrollPadding + this.params.scrollHeight + this.params.labelPadding + this.config.labelAreaHeight, this.elements.canvas.w, this.params.scrollHeight, this.params.scrollBtnWidth, this.params.scrollBtnPadding, 0, maskYShift);
			this.scrollB.setColor(this.params.scrollColor);			
			//If we've to scroll to end
			if (this.params.scrollToEnd){
				this.scrollB.scrollToEnd();
			}
			//Apply filters and animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (this.scrollBarMC, this.objects.SCROLLPANE, this.macro, null, 0, null, 0, 100, null, null, null);
			}
			//Apply filters
			this.styleM.applyFilters (scrollBarMC, this.objects.SCROLLPANE);
		}
		//this.scrollB.invalidate();
		clearInterval(this.config.intervals.scrollBar);
	}
	/**
	* drawLabels method draws the x-axis labels based on the parameters.
	*/
	/**
	* drawLabels method draws labels with new label management or with the previous label management
	**/
	private function drawLabels()
	{
		if(this.params.XTLabelManagement && !this.scrollRequired){
			drawExtremeLabels();
		}else{
			drawNormalLabels()
		}
		//Clear interval
		clearInterval (this.config.intervals.labels);
	}
	/**
	 * drawExtremeLabels method initiates centralised x-axis label rendering.
	 */
	 private function drawExtremeLabels()
	 {
		var labelRenderer:LabelRenderer = new LabelRenderer();
		var labelStyleObj : Object = this.styleM.getTextStyle (this.objects.DATALABELS);
		var depth : Number = this.dm.getDepth ("DATALABELS");
		var styleManager:Object = this.styleM;
		var macroRef:Object = this.macro;
		var objId:Number = this.objects.DATALABELS;
		labelRenderer.scrollChart = true;
		labelRenderer.dummyMC = this.tfTestMC;
		labelRenderer.testXPos = testTFX;
		labelRenderer.testYPos = testTFY;
		labelRenderer.renderXAxisLabels(this.scrollContentMC, depth, this.num, this.categories, labelStyleObj, this.config, this.params.labelDisplay,
										this.elements.canvas, this.params.labelPadding, this.params.staggerLines, this.params.labelStep,
										this.params.animation,
										styleManager, macroRef, objId,
										this.params.showExtremeLabelRegion)
	 }
	/**
	* drawNormalLabels method draws the x-axis labels based on the parameters. and with the old algorithm.
	*/
	private function drawNormalLabels ()
	{
		var labelObj : Object;
		var labelStyleObj : Object = this.styleM.getTextStyle (this.objects.DATALABELS);
		var labelYShift : Number;
		var staggerCycle : Number = 0;
		var staggerAddFn : Number = 1;
		var depth : Number = this.dm.getDepth ("DATALABELS");
		var i : Number;
		var count:Number = 0;
		var labelWidth:Number, labelHeight:Number;
		for (i = 1; i <= this.num; i ++)
		{
			//If the label is to be shown
			if (this.categories [i].showLabel)
			{
				if(this.scrollRequired){
			
					//Set the label width
					if (this.params.labelDisplay == "WRAP"){
						labelWidth = this.categories [i].labelWidthMax;
						labelHeight = this.config.labelAreaHeight;
					}else if (this.params.labelDisplay == "ROTATE"){
						if(this.params.slantLabels){
							var tFormat:TextFormat = new TextFormat();
							//Font properties
							tFormat.font = labelStyleObj.font;
							tFormat.size = labelStyleObj.size;
							tFormat.color = parseInt(labelStyleObj.color, 16);
							//Text decoration
							tFormat.bold = labelStyleObj.bold;
							tFormat.italic = labelStyleObj.italic;
							tFormat.underline = labelStyleObj.underline;
							//Margin and spacing
							tFormat.leftMargin = labelStyleObj.leftMargin;
							tFormat.letterSpacing = labelStyleObj.letterSpacing;
							
							var minMetrics:Object = tFormat.getTextExtent('W');
							var unitH:Number = minMetrics.textFieldHeight;
							
							//Max height of the slant label
							labelHeight = unitH;

							//Restricting slant labels to a maximum height, for a proper presentation
							//Here, bottom-right corner can't go below half-way of the height of the labelAreaBox
							//In the limiting case, the text field is almost a square
							if(this.config.labelAreaHeight/2 < labelHeight * Math.sin(45*Math.PI/180)){
								//Changing label height, so as to have a rectangular text field (where w > h)
								var reducedUnitLabelWidth:Number = (this.config.labelAreaHeight/2)/Math.sin(45*Math.PI/180);
								labelHeight = reducedUnitLabelWidth * Math.cos(45*Math.PI/180);
								
							}
							
							//Max width of the slant label
							labelWidth = (this.config.labelAreaHeight - labelHeight * Math.sin(45*Math.PI/180)) / Math.cos(45*Math.PI/180);
						}else{
							labelWidth = this.config.maxAllowableLabelHeight;
							labelHeight = this.categories [i].labelWidthMax;
						}
					}
					if (this.params.labelDisplay == "ROTATE")
					{
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						//Create text box and get height
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0)) + this.params.labelPadding, this.config.labelAngle, labelStyleObj, true, labelWidth, labelHeight);
					} else if (this.params.labelDisplay == "WRAP")
					{
						//Case 2 (WRAP)
						//Set alignment
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0))  + this.params.labelPadding, 0, labelStyleObj, true, labelWidth, labelHeight);
					} else if (this.params.labelDisplay == "STAGGER")
					{
						//Case 3 (Stagger)
						//Set alignment
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						//Need to get cyclic position for staggered textboxes
						//Matrix formed is of 2*this.params.staggerLines - 2 rows
						count++;
						//Maximum allowed width for label
						var labelAllowedWidth:Number;
						var pos : Number = count % (2 * this.params.staggerLines - 2);
						//Last element needs to be reset
						pos = (pos == 0) ? (2 * this.params.staggerLines - 2) : pos;
						//Cyclic iteration
						pos = (pos > this.params.staggerLines) ? (this.params.staggerLines - (pos % this.params.staggerLines)) : pos;
						//Get position to 0 base
						pos --;
						//For first label
						if(i == 1){
							if(this.num > 1){
								labelAllowedWidth = 2 * Math.min(this.config.unitLabelWidth, this.config.leftPanelWidth);
							}else{
								labelAllowedWidth = this.config.unitLabelWidth;
							}
						//For last label
						}else if(i == this.num){
							labelAllowedWidth = 2 * Math.min(this.config.unitLabelWidth, this.config.rightPanelWidth);
						//For in between labels
						}else{
							labelAllowedWidth = 2*this.config.unitLabelWidth;
						}
						//The minimum between allowd width and simulated width need to use as width.
						labelAllowedWidth = Math.min(this.categories [i].simulatedWidth, labelAllowedWidth);
						//Shift accordingly
						var labelYShift : Number = this.config.maxLabelHeight * pos;
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0))  + this.params.labelPadding + labelYShift, 0, labelStyleObj, true, labelAllowedWidth, this.config.maxLabelHeight);
					} else 
					{
						//Render normal label
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0))  + this.params.labelPadding, 0, labelStyleObj, false, 0, 0);
					}
				}else{
			
					if (this.params.labelDisplay == "ROTATE")
					{
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						//Create text box and get height
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0)) + this.params.labelPadding, this.config.labelAngle, labelStyleObj, true, this.config.wrapLabelWidth, this.config.wrapLabelHeight);
					} else if (this.params.labelDisplay == "WRAP")
					{
						//Case 2 (WRAP)
						//Set alignment
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0))  + this.params.labelPadding, 0, labelStyleObj, true, this.config.wrapLabelWidth, this.config.wrapLabelHeight);
					} else if (this.params.labelDisplay == "STAGGER")
					{
						//Case 3 (Stagger)
						//Set alignment
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						//Need to get cyclic position for staggered textboxes
						//Matrix formed is of 2*this.params.staggerLines - 2 rows
						count++;
						var pos : Number = count % (2 * this.params.staggerLines - 2);
						//Last element needs to be reset
						pos = (pos == 0) ? (2 * this.params.staggerLines - 2) : pos;
						//Cyclic iteration
						pos = (pos > this.params.staggerLines) ? (this.params.staggerLines - (pos % this.params.staggerLines)) : pos;
						//Get position to 0 base
						pos --;
						//Shift accordingly
						var labelYShift : Number = this.config.maxLabelHeight * pos;
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0))  + this.params.labelPadding + labelYShift, 0, labelStyleObj, false, 0, 0);
					} else 
					{
						//Render normal label
						labelStyleObj.align = "center";
						labelStyleObj.vAlign = "bottom";
						labelObj = createText (false, this.categories [i].label, this.scrollContentMC, depth, this.categories [i].x, this.elements.canvas.h + ((this.scrollRequired)?(this.params.scrollHeight + this.params.scrollPadding):(0))  + this.params.labelPadding, 0, labelStyleObj, false, 0, 0);
					}
				}
				//Apply filter
				this.styleM.applyFilters (labelObj.tf, this.objects.DATALABELS);
				//Apply animation
				if (this.params.animation)
				{
					this.styleM.applyAnimation (labelObj.tf, this.objects.DATALABELS, this.macro, null, 0, null, 0, 100, null, null, null);
				}
				//Increase depth
				depth ++;
			}
		}
	}
	/**
	* drawVLines method draws the vertical axis lines on the chart. 
	* This is an over-riding method, as need to plot the vLines inside
	* another movie clip at different x co-ordinates.
	*/
	private function drawVLines () : Void 
	{
		var depth : Number = this.dm.getDepth ("VLINES");
		//Depth of label
		var labelDepth : Number = this.dm.getDepth("VLINELABELS");
		//Label
		var vLineLabel:Object;
		//Get the font properties for v-line labels
		var vLineFont:Object = this.styleM.getTextStyle (this.objects.VLINELABELS);
		//Movie clip container
		var vLineMC : MovieClip;
		//Loop var
		var i : Number;
		//Iterate through all the v div lines
		for (i = 1; i <= this.numVLines; i ++)
		{
			if (this.vLines [i].isValid == true)
			{
				//If it's a valid line, proceed
				//Check if we've to draw the label of the same
				if (this.vLines[i].label != "") {
					//Customize the font properties for the same.
					vLineFont.borderColor = (this.vLines[i].showLabelBorder == true)?(this.vLines[i].color):("");
					//Set the color as well
					vLineFont.color = this.vLines[i].color;
					//Set the alignment position
					vLineFont.align = this.vLines[i].labelHAlign;
					vLineFont.vAlign = (this.vLines[i].labelPosition<0.95)?this.vLines[i].labelVAlign:"top";
					//Create the label now
					vLineLabel = createText (false, this.vLines[i].label, this.scrollContentMC, labelDepth, this.vLines[i].x, (this.elements.canvas.h*this.vLines[i].labelPosition), 0, vLineFont, false, 0, 0);
					//Apply filters
					this.styleM.applyFilters (vLineLabel.tf, this.objects.VLINELABELS);
					//Animation 
					if (this.params.animation){
						this.styleM.applyAnimation (vLineLabel.tf, this.objects.VLINELABELS, this.macro, null, 0, null, 0, 100, null, null, null);
					}
					//Increment depth
					labelDepth++;
				}
				//Create a movie clip for the line
				vLineMC = this.scrollContentMC.createEmptyMovieClip ("vLine_" + i, depth);
				//Just draw line
				vLineMC.lineStyle (this.vLines [i].thickness, parseInt (this.vLines [i].color, 16) , this.vLines [i].alpha);
				//Now, if dashed line is to be drawn
				if ( ! this.vLines [i].isDashed)
				{
					//Draw normal line line keeping 0,0 as registration point
					vLineMC.moveTo (0, 0);
					vLineMC.lineTo (0, - this.elements.canvas.h);
				} else 
				{
					//Dashed Line line
					DrawingExt.dashTo (vLineMC, 0, 0, 0, - this.elements.canvas.h, this.vLines [i].dashLen, this.vLines [i].dashGap);
				}
				//Re-position line
				vLineMC._x = this.vLines [i].x;
				vLineMC._y = this.elements.canvas.h;
				//Apply animation
				if (this.params.animation)
				{
					this.styleM.applyAnimation (vLineMC, this.objects.VLINES, this.macro, null, 0, null, 0, 100, null, 100, null);
				}
				//Apply filters
				this.styleM.applyFilters (vLineMC, this.objects.VLINES);
				//Increase depth
				depth ++;
			}
		}
		delete vLineMC;
		//Clear interval
		clearInterval (this.config.intervals.vLine);
	}
	/**
	* drawColumns method draws the columns on the chart
	*/
	private function drawColumns ()
	{
		//Variables
		var stackMC : MovieClip;
		var colMC : MovieClip;		
		var depth : Number = this.dm.getDepth ("DATAPLOT");
		var i : Number, j : Number;
		//Create function storage containers for Delegate functions
		var fnRollOver : Function, fnClick : Function;
		//Iterate through all columns
		for (i = 1; i <= this.num; i ++)
		{
			//Create an empty movie clip for this index
			stackMC = this.scrollContentMC.createEmptyMovieClip ("StackCol_" + i, depth);
			//Set position
			stackMC._x = this.categories [i].x;
			stackMC._y = this.config.basePlanePos - this.elements.canvas.y;
			for (j = 1; j <= this.numDS; j ++)
			{
				//If defined
				if (this.dataset [j].data [i].isDefined)
				{
					//Create column movie clip inside the stack
					colMC = stackMC.createEmptyMovieClip ("Column_" + j, j);
					//Register with object Manager
					objM.register(colMC, "DATAPLOT_" + j + "_" + i, "DATAPLOTS_"+j)
					
					if (this.params.useRoundEdges){
						var colIns:RoundColumn2D = new RoundColumn2D(colMC, this.dataset [j].data [i].w, this.dataset [j].data [i].h, this.dataset [j].data [i].cornerRadius, this.dataset [j].data [i].color[0].toString(16), this.params.plotBorderThickness, this.params.plotBorderAlpha);
					}else{					
						//Create column instance
						var colIns : Column2D = new Column2D (colMC, this.dataset [j].data [i].w, this.dataset [j].data [i].h, this.params.plotBorderColor, this.params.plotBorderAlpha, this.params.plotBorderThickness, this.dataset [j].data [i].color, this.dataset [j].data [i].alpha, this.dataset [j].data [i].ratio, this.params.plotFillAngle, false, this.dataset [j].data [i].dashed, this.params.plotBorderDashLen, this.params.plotBorderDashGap);
					}
					//Draw the column
					colIns.draw ();
					//Set y position
					colMC._y = this.dataset [j].data [i].y;
					//Set the alpha of entire column as the first alpha in list of alpha
					colMC._alpha = this.dataset [j].data [i].alpha [0];
					//Event handlers for tool tip
					if (this.params.showToolTip)
					{
						//Create Delegate for roll over function columnOnRollOver
						fnRollOver = Delegate.create (this, columnOnRollOver);
						//Set the index
						fnRollOver.dsindex = j;
						fnRollOver.index = i;
						//Assing the delegates to movie clip handler
						colMC.onRollOver = fnRollOver;
						//Set roll out and mouse move too.
						colMC.onRollOut = colMC.onReleaseOutside = Delegate.create (this, columnOnRollOut);
						colMC.onMouseMove = Delegate.create (this, columnOnMouseMove);
					}
					//Click handler for links - only if link for this column has been defined and click URL
					//has not been defined.
					if (this.dataset [j].data [i].link != "" && this.dataset [j].data [i].link != undefined && this.params.clickURL == "")
					{
						//Create delegate function
						fnClick = Delegate.create (this, columnOnClick);
						//Set index
						fnClick.dsindex = j;
						fnClick.index = i;
						//Assign
						colMC.onRelease = fnClick;
					} 
					else
					{
						//Do not use hand cursor
						colMC.useHandCursor = (this.params.clickURL == "") ?false : true;
					}
				}
			}
			//Apply animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (stackMC, this.objects.DATAPLOT, this.macro, null, 0, null, 0, 100, 100, 100, null);
			}
			//Apply filters
			this.styleM.applyFilters (stackMC, this.objects.DATAPLOT);
			//Increase depth
			depth ++;
		}
		//Clear interval
		clearInterval (this.config.intervals.plot);
	}
	/**
	* drawValues method draws the values on the chart.
	*/
	private function drawValues () : Void
	{
		//Get value text style
		var valueStyleObj : Object = this.styleM.getTextStyle (this.objects.DATAVALUES);
		//Individual properties
		var isBold : Boolean = valueStyleObj.bold;
		var isItalic : Boolean = valueStyleObj.italic;
		var font : String = valueStyleObj.font;
		var angle : Number = 0;
		//Container object
		var valueObj : MovieClip;
		//Depth
		var depth : Number = this.dm.getDepth ("DATAVALUES");
		//Loop var
		var i : Number, j : Number;
		var yPos : Number;
		var align : String, vAlign : String;
		////Iterate through all columns
		for (i = 1; i <= this.numDS; i ++)
		{
			for (j = 1; j <= this.num; j ++)
			{
				//If defined and value is to be shown
				if (this.dataset [i].data [j].isDefined && this.dataset [i].data [j].showValue)
				{
					//Get the y position based on placeValuesInside and column height
					vAlign = "middle";
					yPos = this.dataset [i].data [j].valTBY;
					//Align position
					align = "center";
					//Convey alignment to rendering object
					valueStyleObj.align = align;
					valueStyleObj.vAlign = vAlign;
					//Now, if the labels are to be rotated
					if (this.params.rotateValues)
					{
						valueStyleObj.bold = isBold;
						valueStyleObj.italic = isItalic;
						valueStyleObj.font = font;
						angle = 270;
					} else
					{
						//Normal horizontal label - Store original properties
						valueStyleObj.bold = isBold;
						valueStyleObj.italic = isItalic;
						valueStyleObj.font = font;
						angle = 0;
					}
					valueObj = createText (false, this.dataset [i].data [j].displayValue, this.scrollContentMC, depth, this.dataset [i].data [j].x, yPos, angle, valueStyleObj, false, 0, 0);
					//Register with object Manager
					objM.register(valueObj.tf, "DATAVALUE_" + i + "_" + j, "DATAVALUES_"+i)
					//Apply filter
					this.styleM.applyFilters (valueObj.tf, this.objects.DATAVALUES);
					//Apply animation
					if (this.params.animation)
					{
						this.styleM.applyAnimation (valueObj.tf, this.objects.DATAVALUES, this.macro, null, 0, null, 0, 100, null, null, null);
					}
					//Increase depth
					depth ++;
				}
			}
		}
		//Clear interval
		clearInterval (this.config.intervals.dataValues);
	}
	/**
	* drawSum method draws the sum of values on the chart.
	*/
	private function drawSum () : Void
	{
		if (this.params.showSum)
		{
			//Get value text style
			var valueStyleObj : Object = this.styleM.getTextStyle (this.objects.DATAVALUES);
			//Individual properties
			var isBold : Boolean = valueStyleObj.bold;
			var isItalic : Boolean = valueStyleObj.italic;
			var font : String = valueStyleObj.font;
			var angle : Number = 0;
			//Container object
			var valueObj : MovieClip;
			//Depth
			var depth : Number = this.dm.getDepth ("DATASUM");
			//Loop var
			var i : Number;
			var yPos : Number;
			var align : String, vAlign : String;
			////Iterate through all indexs
			for (i = 1; i <= this.num; i ++)
			{
				//If defined and value is to be shown
				if (this.sums [i].sum != 0)
				{
					//Get the y position
					vAlign = (this.sums [i].sum < 0) ?"bottom" : "top";
					yPos = this.config.basePlanePos + ((this.sums [i].sum < 0) ?this.sums [i].nY : this.sums [i].pY) - this.elements.canvas.y;
					//Align position
					align = "center";
					//Convey alignment to rendering object
					valueStyleObj.align = align;
					valueStyleObj.vAlign = vAlign;
					//Now, if the labels are to be rotated
					if (this.params.rotateValues)
					{
						valueStyleObj.bold = isBold;
						valueStyleObj.italic = isItalic;
						valueStyleObj.font = font;
						angle = 270;
					} else
					{
						//Normal horizontal label - Store original properties
						valueStyleObj.bold = isBold;
						valueStyleObj.italic = isItalic;
						valueStyleObj.font = font;
						angle = 0;
					}
					valueObj = createText (false, this.sums [i].displayValue, this.scrollContentMC, depth, this.categories [i].x, yPos, angle, valueStyleObj, false, 0, 0);
					//Apply filter
					this.styleM.applyFilters (valueObj.tf, this.objects.DATAVALUES);
					//Apply animation
					if (this.params.animation)
					{
						this.styleM.applyAnimation (valueObj.tf, this.objects.DATAVALUES, this.macro, null, 0, null, 0, 100, null, null, null);
					}
					//Increase depth
					depth ++;
				}
			}
		}
		//Clear interval
		clearInterval (this.config.intervals.dataSum);
	}
	/**
	* drawLegend method renders the legend
	*/
	private function drawLegend () : Void
	{
		if (this.params.showLegend)
		{
			this.lgnd.render ();
			if (this.params.interactiveLegend){
				//Add listener for legend object.
				this.lgnd.addEventListener("legendClick", this);
			}
			//Apply filter
			this.styleM.applyFilters (lgndMC, this.objects.LEGEND);
			//Apply animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (lgndMC, this.objects.LEGEND, this.macro, null, 0, null, 0, 100, null, null, null);
			}
		}
		//Clear interval
		clearInterval (this.config.intervals.legend);
	}
	/**
	* setContextMenu method sets the context menu for the chart.
	* For this chart, the context items are "Print Chart".
	*/
	private function setContextMenu () : Void
	{
		var chartMenu : ContextMenu = new ContextMenu ();
		chartMenu.hideBuiltInItems ();
		if (this.params.showPrintMenuItem){
			//Create a print chart contenxt menu item
			var printCMI : ContextMenuItem = new ContextMenuItem ("Print Chart", Delegate.create (this, printChart));
			//Push print item.
			chartMenu.customItems.push (printCMI);
		}
		//If the export data item is to be shown
		if (this.params.showExportDataMenuItem){
			chartMenu.customItems.push(super.returnExportDataMenuItem());
		}
		//Add export chart related menu items to the context menu
		this.addExportItemsToMenu(chartMenu);
		if (this.params.showFCMenuItem){
			//Push "About FusionCharts" Menu Item
			chartMenu.customItems.push(super.returnAbtMenuItem());		
		}
		//Assign the menu to cMC movie clip
		this.cMC.menu = chartMenu;
	}
	// -------------------- EVENT HANDLERS --------------------//
	/**
	* columnOnRollOver is the delegat-ed event handler method that'll
	* be invoked when the user rolls his mouse over a column.
	* This function is invoked, only if the tool tip is to be shown.
	* Here, we show the tool tip.
	*/
	private function columnOnRollOver () : Void
	{
		//Index of column is stored in arguments.caller.index
		var index : Number = arguments.caller.index;
		var dsindex : Number = arguments.caller.dsindex;
		//Set tool tip text
		this.tTip.setText (this.dataset [dsindex].data [index].toolText);
		//Show the tool tip
		this.tTip.show ();
	}
	/**
	* columnOnRollOut method is invoked when the mouse rolls out
	* of column. We just hide the tool tip here.
	*/
	private function columnOnRollOut () : Void
	{
		//Hide the tool tip
		this.tTip.hide ();
	}
	/*
	* columnOnMouseMove is called when the mouse position has changed
	* over column. We reposition the tool tip.
	*/
	private function columnOnMouseMove () : Void
	{
		//Reposition the tool tip only if it's in visible state
		if (this.tTip.visible ())
		{
			this.tTip.rePosition ();
		}
	}
	/**
	* columnOnClick is invoked when the user clicks on a column (if link
	* has been defined). We invoke the required link.
	*/
	private function columnOnClick () : Void
	{
		//Index of column is stored in arguments.caller.index
		var dsindex : Number = arguments.caller.dsindex;
		var index : Number = arguments.caller.index;
		//Invoke the link
		super.invokeLink (this.dataset [dsindex].data [index].link);
	}
	/**
	 * legendClick method is the event handler for legend. In this method,
	 * we toggle the visibility of dataset.
	*/
	private function legendClick(target:Object):Void{
		if (this.chartDrawn){
			//Update the container flag that the data-set is now visible/invisible
			objM.toggleGroupVisibility("DATAVALUES_"+target.index, target.active);
			objM.toggleGroupVisibility("DATAPLOTS_"+target.index, target.active);
		}else{
			lgnd.cancelClickEvent(target.intIndex);
		}
	}
	/**
	* reInit method re-initializes the chart. This method is basically called
	* when the user changes chart data through JavaScript. In that case, we need
	* to re-initialize the chart, set new XML data and again render.
	*/
	public function reInit () : Void
	{
		//Invoke super class's reInit
		super.reInit ();
		//Now initialize things that are pertinent to this class
		//but not defined in super class.
		this.categories = new Array ();
		this.dataset = new Array ();
		//Initialize the number of data elements present
		this.numDS = 0;
		this.num = 0;
		//Destroy the legend
		this.lgnd.destroy ();
		if (this.params.interactiveLegend){
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
		this.negativePresent = false;
		this.positivePresent = false;
		//Array to store sum of data sets
		this.sums = new Array ();
		this.sumStored = false;
		//Re-set scroll properties
		this.scrollRequired = false;
		//As it is scroll chart set isScrollChart to true
		this.params.isScrollChart = true;
	}
	/**
	* remove method removes the chart by clearing the chart movie clip
	* and removing any listeners.
	*/
	public function remove () : Void
	{
		super.remove ();
		//Remove class pertinent objects
		if (this.params.interactiveLegend){
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
		//Remove legend
		this.lgnd.destroy ();
		lgndMC.removeMovieClip ();
		//Remove scroll bar related objects
		this.scrollB.destroy();
		scrollableMC.removeMovieClip();
		scrollContentMC.removeMovieClip();
		scrollRefMC.removeMovieClip();
		maskMC.removeMovieClip();
		scrollBarMC.removeMovieClip();
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
		strData = strQ + ((this.params.xAxisName!="")?(this.params.xAxisName):("Label")) + strQ + strS;
		//Add all the series names
		for (i = 1; i <= this.numDS; i++) {
			strData += strQ + ((this.dataset[i].seriesName != "")?(this.dataset[i].seriesName):("")) + strQ + ((i < this.numDS)?(strS):(strLB));
		}
		//Iterate through each data-items and add it to the output
		for (i = 1; i <= this.num; i ++)
		{
			//Add the category label
			strData += strQ + (this.categories [i].label)  + strQ + strS;
			//Add the individual value for datasets
			for (j = 1; j <= this.numDS; j ++)
			{
				 strData += strQ + ((this.dataset[j].data[i].isDefined==true)?((this.params.exportDataFormattedVal==true)?(this.dataset[j].data[i].formattedValue):(this.dataset[j].data[i].value)):(""))  + strQ + ((j<this.numDS)?strS:"");
			}
			if (i < this.num) {
				strData += strLB;
			}
		}
		return strData;
	}
	/**
	  * getSimulationWidth method stores simulation width for each category object.
	  */
	private function getSimulationWidth(){
		var labelObj : Object;
		var labelStyleObj : Object = this.styleM.getTextStyle (this.objects.DATALABELS);
		var i:Number;
		for (i = 1; i <= this.num; i ++)
		{
			//Whethere the label has to be shown or not.
			if(this.categories [i].showLabel){
				if (this.params.labelDisplay == "WRAP" || this.params.labelDisplay == "STAGGER" || this.params.labelDisplay == "NONE")
				{
					//Create text box and get width and height
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1, 0, 0, 0, labelStyleObj, false, 0, 0);
					//Label width
					this.categories [i].simulatedWidth = (!isNaN(labelObj.width))? labelObj.width : 0;
					///Label height
					labelObj.height = (!isNaN(labelObj.height))? labelObj.height : 0;
					//For STAGGER and NONE label display mode set maximum label height
					if(this.params.labelDisplay == "STAGGER" || this.params.labelDisplay == "NONE"){
						this.config.maxLabelHeight = Math.max(this.config.maxLabelHeight,labelObj.height);
					}
				}else if (this.params.labelDisplay == "ROTATE")
				{
					var maxLabelWidth : Number = this.config.maxAllowableLabelHeight;
					var maxLabelHeight : Number = this.config.plotSpace + this.config.blockWidth*this.num;
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1,  0, 0,this.config.labelAngle, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					this.categories [i].simulatedWidth = (!isNaN(labelObj.width))? labelObj.width : 0;
					//Maximum single line text height for rotated text
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1, 0, 0, 0, labelStyleObj, false, 0, 0);
					this.config.maxSingleLineTextHeight = (this.config.maxSingleLineTextHeight < labelObj.height)? labelObj.height:this.config.maxSingleLineTextHeight;
				}
			//When label not to be shown set the simulation width  zero
			}else
			{
				this.categories [i].simulatedWidth = 0;
			}
		}
		//Remove simulated textfield
		labelObj.tf.removeTextField();
		//Delete objects
		delete labelObj;
		delete labelStyleObj;
	}
	/**
	  * manageScrollLabels method manages the label width
	  */
	private function manageScrollLabels(){
		var labelObj : Object;
		var labelStyleObj : Object = this.styleM.getTextStyle (this.objects.DATALABELS);
		//previous label width
		var prevLabelWidth:Number;
		//Next label width
		var nextLabelWidth:Number;
		//Width allowd to use from previous label
		var widthAllowedFromPrev:Number;
		//Width allowd to use from next label
		var widthAllowedFromNext:Number;
		this.config.labelAreaHeight = 0;
		this.config.maxLabelHeight = 0;
		//Loop Variable
		var i:Number;
		for (i = 1; i <= this.num; i ++)
		{ 
			//This calculation needed when there is more than one plot
			if( this.num > 1){
				//Reset previous label allowed width
				widthAllowedFromPrev = 0;
				//Reset next label allowed width
				widthAllowedFromNext = 0;
				//When simulated width is more than unit label width
				if(this.categories [i].simulatedWidth > this.config.unitLabelWidth){
					//Width allowed from previous
					//For first label chart will check the available space in the left panel
					if( i == 1){
						if((this.config.unitLabelWidth/2)< this.config.leftPanelWidth  ){
							
							widthAllowedFromPrev = (this.config.leftPanelWidth - (this.config.unitLabelWidth/2));
						}
					//For rest labels chart will check the available space in the previous label
					}else{
						//When step more than 1
						if(this.params.labelStep > 1){
							widthAllowedFromPrev = this.config.unitLabelWidth/2;
						}else if (this.categories [i-1].simulatedWidth < this.config.unitLabelWidth){
							widthAllowedFromPrev = (this.config.unitLabelWidth - this.categories [i-1].simulatedWidth)/2;
						}
					}
					//Width allowed from next
					//For last label chart will check the available space in the right panel
					if( i == this.num){
						if(this.config.rightPanelWidth > ( this.config.unitLabelWidth/2)){
							widthAllowedFromNext = (this.config.rightPanelWidth -  this.config.unitLabelWidth/2);
						}
					}else{
						//When step more than 1
						if(this.params.labelStep > 1){
							widthAllowedFromNext = this.config.unitLabelWidth/2;
						}else if (this.categories [i+1].simulatedWidth < this.config.unitLabelWidth){
							widthAllowedFromNext = (this.config.unitLabelWidth - this.categories [i+1].simulatedWidth)/2;
						}
					}
					//The twice min of these previous and next allowed width can be added to the unit label width for ith label.
					this.categories [i].labelWidthMax = this.config.unitLabelWidth + 2*Math.min(widthAllowedFromPrev,widthAllowedFromNext);
				//For the label smaller than unit label width
				}else{
					this.categories [i].labelWidthMax = this.categories [i].simulatedWidth;
				}
			//For 1 and only label it will equal to the unit label width.
			}else{
				this.categories [i].labelWidthMax = this.config.unitLabelWidth;
			}
			//The maximum width should the lesser one beteewn simulated width and  label max width.
			this.categories [i].labelWidthMax = Math.min(this.categories [i].simulatedWidth, this.categories [i].labelWidthMax);
			//get Max Text height-------------
			if(this.categories [i].showLabel){
				if (this.params.labelDisplay == "WRAP"){
					var maxLabelWidth : Number = this.categories [i].labelWidthMax;
					var maxLabelHeight : Number = this.config.maxAllowableLabelHeight;
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1,  0, 0,0, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					this.config.labelAreaHeight = Math.max(this.config.labelAreaHeight, labelObj.height);
				}else if (this.params.labelDisplay == "ROTATE"){
					var maxLabelWidth : Number = this.config.maxAllowableLabelHeight;
					var maxLabelHeight : Number = this.categories [i].labelWidthMax;
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1,  0, 0,this.config.labelAngle, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					this.config.labelAreaHeight = Math.max(this.config.labelAreaHeight, labelObj.height);
					if(this.params.slantLabels){
						this.config.labelAreaHeight = Math.min(this.config.labelAreaHeight,this.config.maxAllowableLabelHeight);
					}
				}else{
					labelObj = createText (true, this.categories [i].label, this.tfTestMC, 1,  0, 0,0, labelStyleObj, true, 0, 0);
					this.config.labelAreaHeight = Math.max(this.config.labelAreaHeight, labelObj.height);
					this.config.maxLabelHeight = this.config.labelAreaHeight;
				}
			}
		}
		if (this.params.labelDisplay == "STAGGER"){
			//Here we again validate the stagger lines. The minimum is validated at the time of parsing
			//the maximum is validated here.
			if(this.params.staggerLines > this.num){
				this.params.staggerLines = this.num;
			}
			this.config.labelAreaHeight = this.config.maxLabelHeight* (this.params.staggerLines);
		}
		//Remove simulated textfield
		labelObj.tf.removeTextField();
		labelObj.tf.removeMovieClip();
		//Delete objects
		delete labelObj;
		delete labelStyleObj;
	}
}
