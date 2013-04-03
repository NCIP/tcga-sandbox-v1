﻿/**
* @class SSGrid
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* SSGrid chart extends the Chart class to render a
* single series page-able grid
*/
//Import Chart class
import com.fusioncharts.core.Chart;
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
//Extensions
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.extensions.StringExt;
import com.fusioncharts.extensions.MathExt;
import com.fusioncharts.extensions.DrawingExt;
class com.fusioncharts.core.charts.SSGrid extends Chart {
	//Version number (if different from super Chart class)
	//private var _version:String = "3.0.0";
	//Instance variables
	//List of chart objects
	private var _arrObjects:Array;
	private var xmlData:XML;
	//Array to store data objects
	private var data:Array;
	//Number of data items
	private var num:Number;
	//Parameters for the grid
	private var GParams:Object;
	/**
	* Constructor function. We invoke the super class'
	* constructor and then set the objects for this chart.
	*/
	function SSGrid(targetMC:MovieClip, depth:Number, width:Number, height:Number, x:Number, y:Number, debugMode:Boolean, lang:String, scaleMode:String, registerWithJS:Boolean, DOMId:String) {
		//Invoke the super class constructor
		super (targetMC, depth, width, height, x, y, debugMode, lang, scaleMode, registerWithJS, DOMId);
		//Log additional information to debugger
		//We log version from this class, so that if this class version
		//is different, we can log it
		this.log("Version", _version, Logger.LEVEL.INFO);
		this.log("Chart Type", "Single Series Grid", Logger.LEVEL.INFO);
		//List Chart Objects and set them
		_arrObjects = new Array("BACKGROUND", "COLORBOX", "DATALABELS", "DATAVALUES", "NAVIGATION");
		super.setChartObjects(_arrObjects);
		//Initialize containers
		this.initContainers();
	}
	/**
	* render method is the single call method that does the rendering of chart:
	* - Parsing XML
	* - Calculating values and co-ordinates
	* - Visual layout and rendering
	* - Event handling
	* @param	isRedraw	Whether the method is called during the re-draw of chart
	*						during dynamic resize.
	*/
	public function render(isRedraw:Boolean):Void {
		//Parse the XML Data document
		this.parseXML();
		//If it's a re-draw then do not animate
		if (isRedraw){
			this.params.animation = false;
			this.defaultGlobalAnimation = 0;
		}
		//Parse OBJECT/EMBED attributes for grid
		this.parseGridAttributes();
		//Now, if the number of data elements is 0, we show pertinent
		//error.
		if (this.num == 0) {
			tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
			//Add a message to log.
			this.log("No Data to Display", "No data was found in the XML data document provided. Possible cases can be: <LI>There isn't any data generated by your system. If your system generates data based on parameters passed to it using dataURL, please make sure dataURL is URL Encoded.</LI><LI>You might be providing multi-series data.</LI>", Logger.LEVEL.ERROR);
			//Expose rendered method
			this.exposeChartRendered();
			//Also raise the no data event
			if (!isRedraw){
				this.raiseNoDataExternalEvent();
			}
		} else {
			//Detect number scales
			this.detectNumberScales();
			//Set Style defaults
			this.setStyleDefaults();
			//Calculate Points
			this.calculatePoints();
			//Allot the depths for various charts objects now
			this.allotDepths();
			//Remove application message
			this.removeAppMessage(this.tfAppMsg);
			//-----Start Visual Rendering Now------//			
			//Draw background
			this.drawBackground();
			//Set click handler
			this.drawClickURLHandler();
			//Navigation buttons
			this.drawNavBtn();
			//Render grid
			this.renderGridState();
			//Dispatch event that the chart has loaded.
			this.exposeChartRendered();
			//We do not put context menu interval as we need the menu to appear
			//right from start of the play.
			this.setContextMenu();
		}
	}
	/**
	* returnDataAsObject method creates an object out of the parameters
	* passed to this method. The idea is that we store each data point
	* as an object with multiple (flexible) properties. So, we do not
	* use a predefined class structure. Instead we use a generic object.
	*	@param	label		Label of the data column.
	*	@param	value		Value for the column.
	*	@param	color		Hex Color code
	*	@param	alpha		Alpha
	*	@return			An object encapsulating all these properies.
	*/
	private function returnDataAsObject(label:String, value:Number, color:String):Object {
		//Create a container
		var dataObj:Object = new Object();
		//Store the values
		dataObj.label = label;
		dataObj.value = value;
		//Extract and save colors, ratio, alpha as array so that we do not have to parse later.
		dataObj.color = color;
		//If the given number is not a valid number or it's missing
		//set appropriate flags for this data point
		dataObj.isDefined = (isNaN(value)) ? false : true;
		//Return the container
		return dataObj;
	}
	/**
	 * initContainers initializes the container values.
	*/
	private function initContainers():Void {
		//Initialize the data structure
		this.data = new Array();
		//Initialize the number of data elements present
		this.num = 0;
		//Initialize Grid Parameters container
		this.GParams = new Object();
		//Total sum of values
		this.config.sumOfValues = 0;
		//Start and end visible items index
		//These are inclusive.
		this.config.startIndex = 0;
		this.config.endIndex = 0;
		//Page length
		this.config.pageLen = 0;
		//Height for each data row
		this.config.rowHeight = 0;
		//Maximum width for value column
		this.config.maxValWidth = 0;
		//Label width and x position
		this.config.maxLabelWidth = 0;
		this.config.labelX = 0;
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
		//Look for <graph> element
		for (i=0; i<arrDocElement.length; i++) {
			//If it's a <graph> element, proceed.
			//Do case in-sensitive mathcing by changing to upper case
			if (arrDocElement[i].nodeName.toUpperCase() == "GRAPH" || arrDocElement[i].nodeName.toUpperCase() == "CHART") {
				//Extract attributes of <graph> element
				this.parseAttributes(arrDocElement[i]);
				//Extract common attributes/over-ride chart specific ones
				this.parseCommonAttributes (arrDocElement [i], true);
				//Now, get the child nodes - first level nodes
				//Level 1 nodes can be - CATEGORIES, DATASET, TRENDLINES, STYLES etc.
				var arrLevel1Nodes:Array = arrDocElement[i].childNodes;
				var setNode:XMLNode;
				//Iterate through all level 1 nodes.
				for (j=0; j<arrLevel1Nodes.length; j++) {
					//If it's Data nodes
					if (arrLevel1Nodes[j].nodeName.toUpperCase() == "SET") {
						//Set Node. So extract the data.
						//First, updated counter
						this.num++;
						//Get reference to node.
						setNode = arrLevel1Nodes[j];
						//Get attributes
						var atts:Array;
						atts = this.getAttributesArray(setNode);
						//Extract values.
						var setName:String = getFV(atts["label"], atts["name"], "");
						//Now, get value.
						var setValue:Number = this.getSetValue(atts["value"]);
						var setColor:String = getFV(atts["color"], this.defColors.getColor());
						//We add only if data label is a string value and data value is defined
						if (setName != "" || !isNaN(setValue)) {
							//Store all these attributes as object.
							this.data[this.num] = this.returnDataAsObject(setName, setValue, setColor);
							//Add to sum.
							this.config.sumOfValues = this.config.sumOfValues+setValue;
						} else {
							//Decrease sum
							this.num--;
						}
					} else if (arrLevel1Nodes[j].nodeName.toUpperCase() == "STYLES") {
						//Styles Node - extract child nodes
						var arrStyleNodes:Array = arrLevel1Nodes[j].childNodes;
						//Parse the style nodes to extract style information
						super.parseStyleXML(arrStyleNodes);
					}
				}
			}
		}
		//Delete all temporary objects used for parsing XML Data document
		delete setNode;
		delete arrDocElement;
		delete arrLevel1Nodes;
	}
	/**
	 * parseGridAttributes method parses the local grid parameters. It takes
	 * in input from the _root (Flash Object/EMBED variables) and then fills in
	 * defaults from XML.
	*/
	private function parseGridAttributes():Void {
		//First get all the data from _root into a iterate-able array.
		var atts:Array = getObjPropArray(_root);
		//Now, store all parameters
		//Whether to show percent values?
		this.GParams.showPercentValues = getFN(atts["showpercentvalues"], 0);
		//Number of items per page
		this.GParams.numberItemsPerPage = atts["numberitemsperpage"];
		//Whether to show shadow
		this.GParams.showShadow = getFN(atts["showshadow"], 0);
		//Background properties - Over-ride params
		this.params.bgColor = getFV(atts["bgcolor"], this.params.bgColor);
		this.params.bgAlpha = getFV(atts["bgalpha"], this.params.bgAlpha);
		this.params.bgRatio = getFV(atts["bgratio"], this.params.bgRatio);
		this.params.bgAngle = getFV(atts["bgangle"], this.params.bgAngle);
		//Border Properties of legend
		this.params.showBorder = toBoolean(getFN(atts["showborder"], this.params.showBorder ? 1 : 0));
		this.params.borderColor = formatColor(getFV(atts["bordercolor"], this.params.borderColor));
		this.params.borderThickness = getFN(atts["borderthickness"], this.params.borderThickness);
		this.params.borderAlpha = getFN(atts["borderalpha"], this.params.borderAlpha);
		//Font Properties
		this.GParams.baseFont = getFV(atts["basefont"], this.params.baseFont);
		this.GParams.baseFontSize = getFN(atts["basefontsize"], this.params.baseFontSize);
		this.GParams.baseFontColor = formatColor(getFV(atts["basefontcolor"], this.params.baseFontColor));
		//Alternate Row Color
		this.GParams.alternateRowBgColor = formatColor(getFV(atts["alternaterowbgcolor"], this.defColors.get2DAltHGridColor(this.params.palette)));
		this.GParams.alternateRowBgAlpha = getFN(atts["alternaterowbgalpha"], this.defColors.get2DAltHGridAlpha(this.params.palette));
		//List divider properties
		this.GParams.listRowDividerThickness = getFN(atts["listrowdividerthickness"], 1);
		this.GParams.listRowDividerColor = getFN(atts["listrowdividercolor"], this.params.borderColor);
		this.GParams.listRowDividerAlpha = getFN(atts["listrowdivideralpha"], this.defColors.get2DAltHGridAlpha(this.params.palette)+15);
		//Color box properties
		this.GParams.colorBoxWidth = getFN(atts["colorboxwidth"], 8);
		this.GParams.colorBoxHeight = getFN(atts["colorboxheight"], 8);
		//Navigation Properties
		this.GParams.navButtonRadius = getFN(atts["navbuttonradius"], 7);
		this.GParams.navButtonColor = formatColor(getFV(atts["navbuttoncolor"], this.defColors.get2DCanvasBorderColor(this.params.palette)));
		this.GParams.navButtonHoverColor = formatColor(getFV(atts["navbuttonhovercolor"], this.defColors.get2DAltHGridColor(this.params.palette)));
		//Paddings
		this.GParams.textVerticalPadding = getFN(atts["textverticalpadding"], 3);
		this.GParams.navButtonPadding = getFN(atts["navbuttonpadding"], 5);
		this.GParams.colorBoxPadding = getFN(atts["colorboxpadding"], 10);
		this.GParams.valueColumnPadding = getFN(atts["valuecolumnpadding"], 10);
		this.GParams.nameColumnPadding = getFN(atts["namecolumnpadding"], 5);
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
		this.params.paletteColors = getFV(atts["palettecolors"],"");
		//Set palette colors before parsing the <set> nodes.
		this.setPaletteColors();
		//Whether to set animation for entire chart.
		this.params.animation = toBoolean(getFN(this.defaultGlobalAnimation, atts["animation"], 1));
		//Whether to set the default chart animation
		this.params.defaultAnimation = toBoolean(getFN(atts["defaultanimation"], 1));
		//Click URL
		this.params.clickURL = getFV(atts["clickurl"], "");
		// ------------------------- COSMETICS -----------------------------//
		//Background properties - Gradient
		this.params.bgColor = getFV(atts["bgcolor"], "FFFFFF");
		this.params.bgAlpha = getFV(atts["bgalpha"], 100);
		this.params.bgRatio = getFV(atts["bgratio"], "100");
		this.params.bgAngle = getFV(atts["bgangle"], 0);
		//Border Properties of chart
		this.params.showBorder = toBoolean(getFN(atts["showborder"], 1));
		this.params.borderColor = formatColor(getFV(atts["bordercolor"], this.defColors.get2DBorderColor(this.params.palette)));
		this.params.borderThickness = getFN(atts["borderthickness"], 1);
		this.params.borderAlpha = getFN(atts["borderalpha"], this.defColors.get2DBorderAlpha(this.params.palette));
		//Font Properties
		this.params.baseFont = getFV(atts["basefont"], "Verdana");
		this.params.baseFontSize = getFN(atts["basefontsize"], 10);
		this.params.baseFontColor = formatColor(getFV(atts["basefontcolor"], this.defColors.get2DBaseFontColor(this.params.palette)));
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
		//Input decimal separator and thousand separator. In some european countries,
		//commas are used as decimal separators and dots as thousand separators. In XML,
		//if the user specifies such values, it will give a error while converting to
		//number. So, we accept the input decimal and thousand separator from user, so that
		//we can covert it accordingly into the required format.
		this.params.inDecimalSeparator = getFV(atts["indecimalseparator"], "");
		this.params.inThousandSeparator = getFV(atts["inthousandseparator"], "");
		//Decimal Precision (number of decimal places to be rounded to)
		this.params.decimals = getFV(atts["decimals"], atts["decimalprecision"]);
		//Force Decimal Padding
		this.params.forceDecimals = toBoolean(getFN(atts["forcedecimals"], 0));
	}
	/**
	* getObjPropArray method helps convert the list of attributes
	* for an object into an array.
	* Once this array is returned, IT'S VERY NECESSARY IN THE CALLING CODE TO
	* REFERENCE THE NAME OF ATTRIBUTE IN LOWER CASE (STORED IN THIS ARRAY).
	* ELSE, UNDEFINED VALUE WOULD SHOW UP.
	*/
	private function getObjPropArray(objSource:Object):Array {
		//Array that will store the attributes
		var atts:Array = new Array();
		//Object used to iterate through the attributes collection
		var obj:Object;
		//Iterate through each attribute in the attributes collection,
		//convert to lower case and store it in array.
		for (obj in objSource) {
			//Store it in array
			atts[obj.toString().toLowerCase()] = objSource[obj];
		}
		//Return the array
		return atts;
	}
	/**
	* setStyleDefaults method sets the default values for styles or
	* extracts information from the attributes and stores them into
	* style objects.
	*/
	private function setStyleDefaults():Void {
		//For data labels
		var dataLabelsFont = new StyleObject();
		dataLabelsFont.name = "_SdDataLabelsFont";
		dataLabelsFont.align = "center";
		dataLabelsFont.valign = "bottom";
		dataLabelsFont.font = this.GParams.baseFont;
		dataLabelsFont.size = this.GParams.baseFontSize;
		dataLabelsFont.color = this.GParams.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.DATALABELS, dataLabelsFont, this.styleM.TYPE.FONT, null);
		delete dataLabelsFont;
		var dataValuesFont = new StyleObject();
		dataValuesFont.name = "_SdDataValuesFont";
		dataValuesFont.align = "center";
		dataValuesFont.valign = "middle";
		dataValuesFont.font = this.GParams.baseFont;
		dataValuesFont.size = this.GParams.baseFontSize;
		dataValuesFont.color = this.GParams.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.DATAVALUES, dataValuesFont, this.styleM.TYPE.FONT, null);
		delete dataValuesFont;
		//Shadows 
		if (this.GParams.showShadow) {
			var navShadow = new StyleObject();
			navShadow.name = "_SdShadow";
			navShadow.angle = 45;
			//Over-ride
			this.styleM.overrideStyle(this.objects.NAVIGATION, navShadow, this.styleM.TYPE.SHADOW, null);
			this.styleM.overrideStyle(this.objects.COLORBOX, navShadow, this.styleM.TYPE.SHADOW, null);
			delete navShadow;
		}
	}
	/**
	* calculatePoints method calculates the various points on the chart.
	*/
	private function calculatePoints() {
		//Loop variable
		var i:Number;
		//Format all the numbers on the chart and store their display values
		//We format and store here itself, so that later, whenever needed,
		//we just access displayValue instead of formatting once again.
		for (i=1; i<=this.num; i++) {
			//Format and store
			//Display Value
			this.data[i].displayValue = formatNumber(this.data[i].value, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
			//Percent Value
			this.data[i].percentValue = formatNumber((this.data[i].value / this.config.sumOfValues) * 100, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, false, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, "", "%");
			//Store another copy of formatted value
			this.data [i].formattedValue = this.data [i].displayValue;
		}
		//Now, there are two different flows from here on w.r.t calculation of height
		//Case 1: If the user has specified his own number of items per page
		if (isNaN(this.GParams.numberItemsPerPage) == false) {
			this.GParams.numberItemsPerPage = Number(this.GParams.numberItemsPerPage);
			//In this case, we simply divide the page into the segments chosen by user
			//If all items are able to fit in this single page
			if (this.GParams.numberItemsPerPage>=this.num) {
				//This height is perfectly alright and we can fit all
				//items in a single page
				//Set number items per page to total items.
				this.GParams.numberItemsPerPage = this.num;
				//So, NO need to show the navigation buttons
				this.config.rowHeight = this.height/this.GParams.numberItemsPerPage;
				//Start index to 0
				this.config.startIndex = 0;
				//End Index
				this.config.endIndex = this.num;
				//Page length
				this.config.pageLen = this.num;
			} else {
				//We need to allot space for the navigation buttons
				var cHeight:Number = this.height;
				//Deduct the radius and padding of navigation buttons from height
				cHeight = cHeight-2*(this.GParams.navButtonPadding+this.GParams.navButtonRadius);
				//Now, get the maximum possible number of items that we can fit in each page
				this.config.pageLen = this.GParams.numberItemsPerPage;
				this.config.startIndex = 0;
				//Update endIndex
				this.config.endIndex = this.config.pageLen;
				//Height for each row
				this.config.rowHeight = cHeight/this.config.pageLen;
			}
		} else {
			//Case 2: If we've to calculate best fit. We already have the maximum height
			//required by each row of data. 	
			//Storage for maximum height
			var maxHeight:Number = 0;
			var numItems:Number = 0;
			//Now, get the height required for any single text field
			//We do not consider wrapping.
			var labelObjFont = this.styleM.getTextStyle(this.objects.DATALABELS);
			var valueObjFont = this.styleM.getTextStyle(this.objects.DATAVALUES);
			//Create text box and get height
			var labelObj:Object = createText(true, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=/*-+~`", this.tfTestMC, 1, testTFX, testTFY, 0, labelObjFont, false, 0, 0);
			var valueObj:Object = createText(true, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=/*-+~`", this.tfTestMC, 1, testTFX, testTFY, 0, valueObjFont, false, 0, 0);
			//Get the max of two
			maxHeight = Math.max(labelObj.height, valueObj.height);
			//Add text vertical padding (for both top and bottom)
			maxHeight = maxHeight+2*this.GParams.textVerticalPadding;
			//Also compare with color box height - as that's also an integral part
			maxHeight = Math.max(maxHeight, this.GParams.colorBoxHeight);
			//Now that we have the max possible height, we need to calculate the page length.
			//First check if we can fit all items in a single page
			numItems = this.height/maxHeight;
			if (numItems>=this.num) {
				//We can fit all items in one page
				this.config.rowHeight = (this.height/this.num);
				//Navigation buttons are not required.
				//Start index to 0
				this.config.startIndex = 0;
				//End Index
				this.config.endIndex = this.num;
				//Page length
				this.config.pageLen = this.num;
			} else {
				//We cannot fit all items in same page. So, need to show
				//navigation buttons. Reserve space for them.				
				//We need to allot space for the navigation buttons
				var cHeight:Number = this.height;
				//Deduct the radius and padding of navigation buttons from height
				cHeight = cHeight-2*(this.GParams.navButtonPadding+this.GParams.navButtonRadius);
				//Now, get the maximum possible number of items that we can fit in each page
				this.config.pageLen = Math.floor(cHeight/maxHeight);
				//Update endIndex
				this.config.endIndex = this.config.pageLen;
				//Height for each row
				this.config.rowHeight = cHeight/this.config.pageLen;
			}
		}
		//Now, we need to iterate through the value fields to get the max width
		var maxWidth:Number = 0;
		var valueObjFont = this.styleM.getTextStyle(this.objects.DATAVALUES);
		for (i=1; i<=this.num; i++) {
			if (!isNaN(this.data[i].value)) {
				//Simulate
				var valueObj:Object = createText(true, (this.GParams.showPercentValues) ? (this.data[i].percentValue) : (this.data[i].displayValue), this.tfTestMC, 1, testTFX, testTFY, 0, valueObjFont, false, 0, 0);
				//Store maximum width
				this.config.maxValWidth = Math.max(this.config.maxValWidth, valueObj.width);
			}
		}
		//Now, we calculate the maximum avaiable width for data label column
		this.config.maxLabelWidth = (this.width-this.GParams.colorBoxPadding-this.GParams.colorBoxWidth-this.GParams.nameColumnPadding-this.config.maxValWidth-this.GParams.valueColumnPadding);
		this.config.labelX = this.GParams.colorBoxPadding+this.GParams.colorBoxWidth+this.GParams.nameColumnPadding;
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
		//List Row dividers
		this.dm.reserveDepths("ROWDIVIDERS", this.config.pageLen+1);
		//Color Boxes
		this.dm.reserveDepths("COLORBOXES", this.num);
		//Data Labels
		this.dm.reserveDepths("DATALABELS", this.num);
		//Data Values
		this.dm.reserveDepths("DATAVALUES", this.num);
		//Navigation Buttons
		this.dm.reserveDepths("PREVBTN", 1);
		this.dm.reserveDepths("NEXTBTN", 1);
	}
	/**
	 * renderGridState method renders the grid at any point of time. Even
	 * when a navigation button is clicked, we update the interal indexes,
	 * and then call this common function to render grid state
	 *	@param	alreadyRendered	Boolean value indicating if we've already
	 *							rendered the grid before.
	*/
	private function renderGridState(alreadyRendered:Boolean):Void {
		
		//Loop variable
		var i:Number;
		//If it's not being rendered first time, we have to remove
		//any movie clips or textfield previously created
		if (alreadyRendered == true) {
			//Remove the text fields and movie clips already generated
			//in last state. We iterate through the depths, get instances
			//and remove them			
			//Generic Object iterator
			var obj;
			//Depth iterator
			var d:Number;
			for (obj in this.cMC) {
				//Get depth
				d = this.cMC[obj].getDepth();
				//Delete Movie clips
				if (d>=this.dm.getDepth("ROWDIVIDERS") && d<this.dm.getDepth("DATALABELS")) {
					//Remove movie clips
					this.cMC[obj].removeMovieClip();
				}
				if (d>=this.dm.getDepth("DATALABELS") && d<this.dm.getDepth("PREVBTN")) {
					//Remove all text fields
					this.cMC[obj].removeTextField();
				}
			}
		}
		//Depth indexer
		var depthCounter:Number = 0;
		//Y Position containers
		var yStart:Number = 0;
		var yCenter:Number = 0;
		//Font Objects
		var labelObjFont = this.styleM.getTextStyle(this.objects.DATALABELS);
		var valueObjFont = this.styleM.getTextStyle(this.objects.DATAVALUES);
		//Set alignment parameters
		labelObjFont.align = "left";
		labelObjFont.vAlign = "middle";
		valueObjFont.align = "right";
		valueObjFont.vAlign = "middle";
		//Color box position indexers
		var hw:Number = this.GParams.colorBoxWidth/2;
		var hh:Number = this.GParams.colorBoxHeight/2;
		//Draw now.
		for (i=this.config.startIndex+1; i<=this.config.endIndex; i++) {
			//Get y Position - Center of this row
			yCenter = yCenter+(this.config.rowHeight/2);
			//Create the color box
			var cBox:MovieClip = this.cMC.createEmptyMovieClip("ColorBox_"+i, this.dm.getDepth("COLORBOXES")+depthCounter);
			cBox.beginFill(parseInt(this.data[i].color, 16), 100);
			cBox.moveTo(-hw, 0);
			cBox.lineTo(-hw, -hh);
			cBox.lineTo(hw, -hh);
			cBox.lineTo(hw, hh);
			cBox.lineTo(-hw, hh);
			cBox.lineTo(-hw, 0);
			cBox.endFill();
			//Set position
			cBox._x = this.GParams.colorBoxPadding+hw;
			cBox._y = yCenter;
			//Apply filters
			this.styleM.applyFilters(cBox, this.objects.COLORBOX);
			//List dividers
			if (depthCounter%2 == 0) {
				var listDv:MovieClip = this.cMC.createEmptyMovieClip("ListDivider_"+i, this.dm.getDepth("ROWDIVIDERS")+depthCounter);
				listDv.lineStyle(this.GParams.listRowDividerThickness, parseInt(this.GParams.listRowDividerColor, 16), this.GParams.listRowDividerAlpha);
				listDv.beginFill(parseInt(this.GParams.alternateRowBgColor, 16), this.GParams.alternateRowBgAlpha);
				listDv.moveTo(0, yStart);
				listDv.lineTo(this.width, yStart);
				listDv.lineStyle();
				listDv.lineTo(this.width, yStart+this.config.rowHeight);
				listDv.lineStyle(this.GParams.listRowDividerThickness, parseInt(this.GParams.listRowDividerColor, 16), this.GParams.listRowDividerAlpha);
				listDv.lineTo(0, yStart+this.config.rowHeight);
				listDv.lineStyle();
				listDv.lineTo(0, yStart);
				listDv.endFill();
			} else {
				//Draw last line
				if (i == this.config.endIndex) {
					var listDv:MovieClip = this.cMC.createEmptyMovieClip("ListDivider_"+i, this.dm.getDepth("ROWDIVIDERS")+depthCounter);
					listDv.lineStyle(this.GParams.listRowDividerThickness, parseInt(this.GParams.listRowDividerColor, 16), this.GParams.listRowDividerAlpha);
					listDv.moveTo(0, yStart+this.config.rowHeight);
					listDv.lineTo(this.width, yStart+this.config.rowHeight);
				}
			}
			//Create text boxes
			//If the label is not empty
			if (this.data[i].label != "") {
				var labelObj:Object = createText(false, this.data[i].label, this.cMC, this.dm.getDepth("DATALABELS")+depthCounter, this.config.labelX, yCenter, 0, labelObjFont, true, this.config.maxLabelWidth, this.config.rowHeight);
			}
			//If the value is not naN  
			if (!isNaN(this.data[i].value)) {
				var valueObj:Object = createText(false, (this.GParams.showPercentValues) ? (this.data[i].percentValue) : (this.data[i].displayValue), this.cMC, this.dm.getDepth("DATAVALUES")+depthCounter, this.width-this.GParams.valueColumnPadding, yCenter, 0, valueObjFont, false, 0, 0);
			}
			//Update y Positions  
			yStart = yStart+this.config.rowHeight;
			yCenter = yCenter+(this.config.rowHeight/2);
			//Increase depth counter
			depthCounter++;
		}
		//Set the button states
		setBtnStates();
		//Set the button x-positions
		this.config.prevBtnMC._x = 20;
		this.config.nextBtnMC._x = this.width-20;
		//Set the button y-positions
		this.config.prevBtnMC._y = yStart+this.GParams.navButtonPadding+this.GParams.navButtonRadius;
		this.config.nextBtnMC._y = yStart+this.GParams.navButtonPadding+this.GParams.navButtonRadius;
		//Clear interval
		if (alreadyRendered != true) {
			clearInterval(this.config.intervals.grid);
		}
	}
	/**
	 * setBtnStates method sets the button states as per 
	 * grid state.
	*/
	private function setBtnStates():Void {
		//This method hides the buttons as and when required
		if (this.config.startIndex == 0) {
			this.config.prevBtnMC._visible = false;
		} else {
			this.config.prevBtnMC._visible = true;
		}
		if (this.config.endIndex == this.num) {
			this.config.nextBtnMC._visible = false;
		} else {
			this.config.nextBtnMC._visible = true;
		}
	}
	/**
	 * drawNavBtn method draws both the navigation buttons
	*/
	private function drawNavBtn() {
		//Create movie clips for both of them and store in config
		this.config.prevBtnMC = this.cMC.createEmptyMovieClip("MCPrevBtn", this.dm.getDepth("PREVBTN"));
		this.config.nextBtnMC = this.cMC.createEmptyMovieClip("MCNextBtn", this.dm.getDepth("NEXTBTN"));
		//Draw both of them		
		//We first draw a circle with 0 alpha below each of them for better hit test.
		this.config.prevBtnMC.beginFill(0xaaffff, 0);
		DrawingExt.drawPoly(this.config.prevBtnMC, 0, 0, 10, this.GParams.navButtonRadius+2, 0);
		this.config.prevBtnMC.endFill();
		//Now draw the triangle
		this.config.prevBtnMC.beginFill(parseInt(this.GParams.navButtonColor, 16), 100);
		DrawingExt.drawPoly(this.config.prevBtnMC, 0, 0, 3, this.GParams.navButtonRadius, 180);
		this.config.prevBtnMC.endFill();
		//We first draw a circle with 0 alpha below each of them for better hit test.
		this.config.nextBtnMC.beginFill(0xaaffff, 0);
		DrawingExt.drawPoly(this.config.nextBtnMC, 0, 0, 10, this.GParams.navButtonRadius+2, 0);
		this.config.nextBtnMC.endFill();
		//Now draw the triangle
		this.config.nextBtnMC.beginFill(parseInt(this.GParams.navButtonColor, 16), 100);
		DrawingExt.drawPoly(this.config.nextBtnMC, 0, 0, 3, this.GParams.navButtonRadius, 0);
		this.config.nextBtnMC.endFill();
		//Apply effects
		this.styleM.applyFilters(this.config.prevBtnMC, this.objects.NAVIGATION);
		this.styleM.applyFilters(this.config.nextBtnMC, this.objects.NAVIGATION);
		//Create roll over color change effects for both
		var rollOverColor:String = this.GParams.navButtonHoverColor;
		var originalColor:String = this.GParams.navButtonColor;
		//Roll over events
		this.config.prevBtnMC.onRollOver = function() {
			var clrBtn:Color = new Color(this);
			clrBtn.setRGB(parseInt(rollOverColor, 16));
		};
		this.config.nextBtnMC.onRollOver = function() {
			var clrBtn:Color = new Color(this);
			clrBtn.setRGB(parseInt(rollOverColor, 16));
		};
		//Roll-out events
		this.config.prevBtnMC.onRollOut = function() {
			var clrBtn:Color = new Color(this);
			clrBtn.setRGB(parseInt(originalColor, 16));
		};
		this.config.nextBtnMC.onRollOut = function() {
			var clrBtn:Color = new Color(this);
			clrBtn.setRGB(parseInt(originalColor, 16));
		};
		//Click effect handlers
		this.config.prevBtnMC.onPress = function() {
			this._x++;
			this._y++;
		};
		this.config.nextBtnMC.onPress = function() {
			this._x++;
			this._y++;
		};
		//Create the click handlers and delegate to class methods
		this.config.nextBtnMC.onRelease = Delegate.create(this, pageNext);
		this.config.prevBtnMC.onRelease = Delegate.create(this, pagePrevious);
		//Clear interval
		clearInterval(this.config.intervals.nav);
	}
	/**
	 * pageNext method pages the records to next page.
	*/
	private function pageNext():Void {
		//Complete release effect
		this.config.nextBtnMC._x--;
		this.config.nextBtnMC._y--;
		//Update indexes.
		this.config.startIndex = this.config.endIndex;
		this.config.endIndex = this.config.endIndex+this.config.pageLen;
		//If we crossed the total number, move back
		this.config.endIndex = (this.config.endIndex>this.num) ? this.num : this.config.endIndex;
		//Render the grid state again
		this.renderGridState(true);
	}
	/**
	 * pagePrevious method pages the records to previous page.
	*/
	private function pagePrevious():Void {
		//Complete release effect
		this.config.prevBtnMC._x--;
		this.config.prevBtnMC._y--;
		//Update indexes.
		this.config.endIndex = this.config.startIndex;
		this.config.startIndex = this.config.startIndex-this.config.pageLen;
		//If we got below 0, set it back to 0.
		this.config.startIndex = (this.config.startIndex<0) ? 0 : this.config.startIndex;
		//Render the grid state again
		this.renderGridState(true);
	}
	/**
	* setContextMenu method sets the context menu for the chart.
	* For this chart, the context items are "Print Chart".
	*/
	private function setContextMenu():Void {
		var chartMenu:ContextMenu = new ContextMenu();
		chartMenu.hideBuiltInItems();
		if (this.params.showPrintMenuItem){
			//Create a print chart contenxt menu item
			var printCMI:ContextMenuItem = new ContextMenuItem("Print Grid", Delegate.create(this, printChart));
			//Push print item.
			chartMenu.customItems.push(printCMI);
		}
		//If the export data item is to be shown
		if (this.params.showExportDataMenuItem){
			chartMenu.customItems.push(super.returnExportDataMenuItem());
		}
		if (this.params.showFCMenuItem){
			//Push "About FusionCharts" Menu Item
			chartMenu.customItems.push(super.returnAbtMenuItem());		
		}
		//Assign the menu to cMC movie clip
		this.cMC.menu = chartMenu;
	}
	/**
	* reInit method re-initializes the chart. This method is basically called
	* when the user changes chart data through JavaScript. In that case, we need
	* to re-initialize the chart, set new XML data and again render.
	*/
	public function reInit():Void {
		//Invoke super class's reInit
		super.reInit();
		//Now initialize things that are pertinent to this class
		//but not defined in super class.
		//Initialize containers
		this.initContainers();
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
		var i:Number;
		strData = strQ + "Label" + strQ + strS + strQ + "Value" + strQ + strLB;
		//Iterate through each data-item and add it to the output
		for (i = 1; i <= this.num; i++) {
			strData += strQ + this.data[i].label + strQ + strS + strQ + ((this.data [i].isDefined==true)?((this.params.exportDataFormattedVal==true)?(this.data[i].formattedValue):(this.data[i].value)):("")) + strQ + ((i<this.num)?strLB:""); 
		}
		return strData;
	}
}
