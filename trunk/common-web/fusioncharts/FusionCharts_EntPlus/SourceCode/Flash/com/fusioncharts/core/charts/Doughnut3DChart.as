﻿/**
 * Doughnut3DChart chart extends the Chart class to render a 
 * 3D Pie Chart.
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 */
// Import the Delegate class
import mx.utils.Delegate;
//External Interface - to expose methods via JavaScript
import flash.external.ExternalInterface;
//Import parent Chart class
import com.fusioncharts.core.Chart;
//Error class
import com.fusioncharts.helper.FCError;
//Import Logger Class
import com.fusioncharts.helper.Logger;
//Import BitmapData
import flash.display.BitmapData;
//Import LegendIconGenerator
import com.fusioncharts.helper.LegendIconGenerator;
//Legend Class
import com.fusioncharts.helper.AdvancedLegend;
// Import the MathExt class
import com.fusioncharts.extensions.MathExt;
// Import the ColorExt class
import com.fusioncharts.extensions.ColorExt;
//Import the Style Object
import com.fusioncharts.core.StyleObject;
//Pie Class
import com.fusioncharts.core.chartobjects.Doughnut3D;
class com.fusioncharts.core.charts.Doughnut3DChart extends Chart {
	//Version number (if different from super Chart class)	
	//private var _version:String = "3.0.6 (b)";
	//Instance variables
	//Container for data
	private var data:Array;
	//num keeps a count of number of data sets provided to the chart
	private var num:Number = 0;
	//List of chart objects
	private var _arrObjects:Array;
	// XML object 
	private var xmlData:XML;
	//Plot area is the rectangle in which the entire pie chart
	//will be contained. The caption, sub caption and chart margins
	//do NOT form at part of the plot area.
	//Plot height - pertinent to pie only
	private var plotHeight:Number;
	//Plot width - pertinent to pie only
	private var plotWidth:Number;
	//Top X and Y position of plot area
	private var plotX:Number;
	private var plotY:Number;
	//Movie clip container to hold all pies
	private var mcPieH:MovieClip;
	//A consolidated object to stores the ids of setInterval
	private var objIntervalIds:Object;
	// listener object for handling the mouse events for rotation
	private var objMouseListener:Object;
	//To store the reference of the MathExt.toNearestTwip() for
	//easy and short reference to the function.
	private var toNT:Function;
	//Reference to legend component of chart
	private var lgnd : AdvancedLegend;
	//Reference to legend movie clip
	private var lgndMC : MovieClip;
	//Set node counter
	private var setNodeCounter:Number;
	//Map of Pie3D objects with their respective entries in this.data
	private var map:Array = new Array();
	//Holds the number of pies slicing at any point of time
	private var numSlicing:Number = 0;
	//Flag to rotating state of the chart
	private var rotating:Boolean = false;
	//Flag to indicate that resizing is pending due 2D/3D toggling or slicing
	private var resizePending:Boolean = false;
	//Flag to denote that chart has initialised (config.isInitialised is changed in between to handle situations, so not an absolute flag at any point of time)
	private var chartInit:Boolean = false;
	//Context menu item for rotation
	private var cmiRotation:ContextMenuItem;
	//Context menu item for Slicing
	private var cmiSlicing:ContextMenuItem;
	//Context menu item for Link
	private var cmiLink:ContextMenuItem;
	//
	/**
	 * Constructor function. We invoke the super class'
	 * constructor and then set the objects for this chart.
	*/
	function Doughnut3DChart(targetMC:MovieClip, depth:Number, width:Number, height:Number, x:Number, y:Number, debugMode:Boolean, lang:String, scaleMode:String, registerWithJS:Boolean, DOMId:String) {
		//Invoke the super class constructor
		super(targetMC, depth, width, height, x, y, debugMode, lang, scaleMode, registerWithJS, DOMId);
		//Log additional information to debugger
		//We log version from this class, so that if this class version
		//is different, we can log it
		this.log("Version", _version, Logger.LEVEL.INFO);
		this.log("Chart Type", "3D Doughnut Chart", Logger.LEVEL.INFO);
		//List Chart Objects and set them
		_arrObjects = new Array("BACKGROUND", "CANVAS", "CAPTION", "SUBCAPTION", "DATALABELS", "DATAPLOT", "TOOLTIP", "LEGEND");
		super.setChartObjects(_arrObjects);
		//Initialize data container
		this.data = new Array();
		//Initialize container to store interval ids
		objIntervalIds = new Object();
		//Initialize container to store mouse listener objects
		objMouseListener = new Object();
		//By default assume that the plot area width and height
		//will be same as the width and height of the pie chart.
		//We later deduct caption and subcaption space if required.
		plotWidth = this.width;
		plotHeight = this.height;
		//Variable to store the sum of all values in pie
		this.config.sumOfValues = 0;
		//Configuration whether links have been defined - by default assume no.
		this.config.linksDefined = false;
		//Store the reference of MathExt.toNearestTwip()
		toNT = MathExt.toNearestTwip;
		//Flag to skip pre-processing of image saving for this chart
		this.cMC.skipBmpCacheCheck = true;
		
		//setting watch on flag holding state of dimension change
		this.config.watch('changingDimension', checkResizePendingForDimensionChange, this);
		
		//setting watch on number indicating the state of slicing
		this.watch('numSlicing', checkResizePendingForSlicing, this);
		
		//setting watch on number indicating the state of slicing
		this.watch('chartInit', checkResizePendingForChartInit, this);
		
		if (this.registerWithJS==true && ExternalInterface.available){
			//Expose slicing functionality to JS. 
			ExternalInterface.addCallback("togglePieSlice", this, sliceViaJS);
		}
		//Add external Interface APIs exposed by this chart
		extInfMethods += ",togglePieSlice";
		
		if (this.registerWithJS==true && ExternalInterface.available){
			//Expose slicing functionality to JS. 
			ExternalInterface.addCallback("enableLink", this, enablelinkViaJS);
		}
		//Add external Interface APIs exposed by this chart
		extInfMethods += ",enableLink";
		
		if (this.registerWithJS==true && ExternalInterface.available){
			//Expose slicing functionality to JS. 
			ExternalInterface.addCallback("enableSlicingMovement", this, enableSlicingViaJS);
		}
		//Add external Interface APIs exposed by this chart
		extInfMethods += ",enableSlicingMovement";
		//Reset set node counter to zero
		this.setNodeCounter = 0;
	}
	
	/**
	 * checkResizePendingForChartInit method responds on change in flag
	 * for chart initialisation start and end to check if resizing is on 
	 * hold and be initiated now.
	 * @params	prop		property watched
	 * @params	oldVal		old value of the property
	 * @params	newVal		new value of the property
	 * @returns				the new value
	 */
	private function checkResizePendingForChartInit(prop:String, oldVal:Boolean, newVal:Boolean, insRef:Doughnut3DChart):Boolean{
		//if new value for the flag is true indicating chart initialissation end
		if(newVal){
			//if resize is pending
			if(insRef.resizePending){
				//call to render with params indicating that its resizing and is a late call.
				insRef.render(true, true);
				insRef.resizePending = false;
			}
		}
		return newVal;
	}
	
	/**
	 * checkResizePendingForDimensionChange method responds on change in flag
	 * for dimension change start and end to check if resizing is on hold and
	 * be initiated now.
	 * @params	prop		property watched
	 * @params	oldVal		old value of the property
	 * @params	newVal		new value of the property
	 * @returns				the new value
	 */
	private function checkResizePendingForDimensionChange(prop:String, oldVal:Boolean, newVal:Boolean, insRef:Doughnut3DChart):Boolean{
		//if new value for the flag is false indicating end of 2D-3D toggling
		if(!newVal){
			//if resize is pending
			if(insRef.resizePending){
				//call to render with params indicating that its resizing and is a late call.
				insRef.render(true, true);
				insRef.resizePending = false;
			}
		}
		return newVal;
	}
	/**
	 * checkResizePendingForSlicing method responds on change in the number of
	 * pies slicing to check if resizing is on hold and be initiated now.
	 * @params	prop		property watched
	 * @params	oldVal		old value of the property
	 * @params	newVal		new value of the property
	 * @returns				the new value
	 */
	private function checkResizePendingForSlicing (prop:String, oldVal:Number, newVal:Number, insRef:Doughnut3DChart):Number{
		//if new value is zero indicating that the last pie slicing stops
		if(newVal == 0){
			//if resize is pending
			if(insRef.resizePending){
				//call to render with params indicating that its resizing and is a late call.
				insRef.render(true, true);
				insRef.resizePending = false;
			}
		}
		return newVal;
	}
	
	//-------------------- CORE CHART METHODS --------------------------//
	/**
	 * render method is the single call method that does the rendering of chart:
	 * - Parsing XML
	 * - Calculating values and co-ordinates
	 * - Visual layout and rendering
	 * - Event handling
	 * @param	isRedraw	Whether the method is called during the re-draw of chart
	 *						during dynamic resize.
	 * @param	lateCall	Whether the method is called due to a resize pending
	*/
	public function render(isRedraw:Boolean, lateCall:Boolean):Void {
		
		//If it's a re-draw
		if (isRedraw){
			//If not a late call for pending resize command
			if(!lateCall){
				//Check to see if its proper to go for the resize now or not
				if(this.config.changingDimension || this.numSlicing > 0){
					//For either case of dimension changing or pie slicing at this point of time, its inappropriate to go for resizing
					//So set the flag to indicate that resize is pending
					this.resizePending = true;
					//Abort the call render() call
					return;
				}
			}
			//Return if no set node
			if(setNodeCounter == 0 ){
				tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
				//Add a message to log.
				this.log("No Data to Display", "No set node defined.", Logger.LEVEL.ERROR);
				return;
			}
			//If its proper to go foe resizing now
			//Recalculate and/or re-evaluate and set those properties/params so required for a change in width/height of the chart.
			//And, pass the boolean to indicate that this is for a resizing.
			this.calculateForWidthAndHeight(true);
			//Reset the chart center
			this.setChartCenter();
			//Reset the radii if not specified to go for best value evaluation
			this.resetRadius();
			//Reset macros, for chart width/height changed
			this.feedMacros();
			//Redraw background for the changed chart canvas
			this.drawBackground();
			//So, redraw the click handler
			this.drawClickURLHandler();
			//Refresh the bg swf if any
			this.loadBgSWF();
			//Redraw headers
			this.drawHeaders();
			
			//Update the current slicing status of pies in the this.data, before previous pies are destroyed and recreated
			this.loadCurrentSlicingStatus();
			//Call the draw() method with flag to indicate that its resizing; the draw method will help re-evaluate the best fit radii if not specified
			//Also cosidering there is valid data to display.
			if(this.num > 0 && this.config.sumOfValues > 0){
				this.draw(true);
				//Redraw the legend (but not the data)
				this.drawLegend();
			}
			//Expose chart rendered
			this.exposeChartRendered();
			//purpose for the render() call is over
			return;
		}
		
		
		//Parse the XML Data document
		this.parseXML();
		
		//Now, if the number of data elements is 0, we show pertinent
		//error.
		if (this.num == 0 && this.setNodeCounter == 0) {
			tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
			//Add a message to log.
			this.log("No Data to Display", "No data was found in the XML data document provided. Possible cases can be: <LI>There isn't any data generated by your system. If your system generates data based on parameters passed to it using dataURL, please make sure dataURL is URL Encoded.</LI><LI>You might be using a Single Series Chart .swf file instead of Multi-series .swf file and providing multi-series data or vice-versa.</LI>", Logger.LEVEL.ERROR);
			//Expose rendered method
			this.exposeChartRendered();
			
			this.raiseNoDataExternalEvent();
			
		} else {
			//If the sum of all pie is equal to 0, we show pertinent error.
			if (this.config.sumOfValues == 0 || this.setNodeCounter == 0) {
				tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
				
				//Add a message to log.
				this.log("No Data to Display", "The sum of all the pies in XML resulted to 0. A 0 value pie chart cannot be plotted.", Logger.LEVEL.ERROR);
				
				//Expose rendered method
				this.exposeChartRendered();
			}
		} 
		//Return if no set node
		if(setNodeCounter == 0 ){
			tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
			//Add a message to log.
			this.log("No Data to Display", "No set node defined.", Logger.LEVEL.ERROR);
			return;
		}
		//Set style defaults
		this.setStyleDefaults();
		//Detect number scales
		this.detectNumberScales();
		//Allot the depths for various charts objects now
		this.allotDepths();
		//Calculate Points
		this.calculatePoints();
		// to set mouse for chart rotation due drag
		this.setMouseListener();
		//Feed macro values
		this.feedMacros();
		//Set tool tip parameter
		this.setToolTipParam();
		//-------- Start Visual Rendering Now ------//
		//Draw background
		this.drawBackground();
		// set the global URL click
		this.drawClickURLHandler();
		// load the background SWF, if any
		this.loadBgSWF();
		//Update timer
		this.timeElapsed = (this.params.animation) ? this.styleM.getMaxAnimationTime(this.objects.BACKGROUND) : 0;
		//Draw headers - caption and sub-caption
		this.config.intervals.headers = setInterval(Delegate.create(this, drawHeaders), this.timeElapsed);
		//Update timer
		this.timeElapsed += (this.params.animation) ? this.styleM.getMaxAnimationTime(this.objects.CAPTION, this.objects.SUBCAPTION) : 0;
		//Call the unified draw method to render chart.
		if(this.num > 0 && this.config.sumOfValues > 0){
			this.config.intervals.plot = setInterval(Delegate.create(this, draw), this.timeElapsed);
			//Legend
			this.config.intervals.legend = setInterval (Delegate.create (this, drawLegend) , this.timeElapsed);
		}
		//Set context menu
		this.config.intervals.menu = setInterval(Delegate.create(this, setContextMenu), this.timeElapsed);
		//Remove application message
		this.removeAppMessage(this.tfAppMsg);
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
		//Caption
		this.dm.reserveDepths("CAPTION", 1);
		//Sub-caption
		this.dm.reserveDepths("SUBCAPTION", 1);
		// piechart holder
		this.dm.reserveDepths("DATAPLOT", 1);
		//Legend
		this.dm.reserveDepths("LEGEND", 1);
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
		captionFont.valign = "top";
		captionFont.bold = "1";
		captionFont.font = this.params.baseFont;
		captionFont.size = this.params.baseFontSize+3;
		captionFont.color = this.params.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.CAPTION, captionFont, this.styleM.TYPE.FONT, null);
		delete captionFont;
		//-----------------------------------------------------------------//
		//Default font object for SubCaption
		//-----------------------------------------------------------------//
		var subCaptionFont = new StyleObject();
		subCaptionFont.name = "_SdSubCaptionFont";
		subCaptionFont.align = "center";
		subCaptionFont.valign = "top";
		subCaptionFont.bold = "1";
		subCaptionFont.font = this.params.baseFont;
		subCaptionFont.size = this.params.baseFontSize+1;
		subCaptionFont.color = this.params.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.SUBCAPTION, subCaptionFont, this.styleM.TYPE.FONT, null);
		delete subCaptionFont;
		//-----------------------------------------------------------------//
		//Default font object for Legend
		//-----------------------------------------------------------------//
		var legendFont = new StyleObject ();
		legendFont.name = "_SdLegendFont";
		legendFont.font = this.params.baseFont;
		legendFont.size = this.params.baseFontSize;
		legendFont.color = this.params.baseFontColor;
		legendFont.ishtml = 1;
		legendFont.leftmargin = 3;
		//Over-ride
		this.styleM.overrideStyle (this.objects.LEGEND, legendFont, this.styleM.TYPE.FONT, null);
		delete legendFont;
		//-----------------------------------------------------------------//
		//Default font object for DataLabels
		//-----------------------------------------------------------------//
		var dataLabelsFont = new StyleObject();
		dataLabelsFont.name = "_SdDataLabelsFont";
		dataLabelsFont.align = "center";
		dataLabelsFont.valign = "bottom";
		dataLabelsFont.font = this.params.baseFont;
		dataLabelsFont.size = this.params.baseFontSize;
		dataLabelsFont.color = this.params.baseFontColor;
		//Over-ride
		this.styleM.overrideStyle(this.objects.DATALABELS, dataLabelsFont, this.styleM.TYPE.FONT, null);
		delete dataLabelsFont;
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
		//Default Animation object for DataPlot (if required)
		//-----------------------------------------------------------------//
		if (this.params.defaultAnimation) {
			var dataPlotAnim = new StyleObject();
			dataPlotAnim.name = "_SdDataPlotAnim";
			dataPlotAnim.param = "_alpha";
			dataPlotAnim.easing = "regular";
			dataPlotAnim.wait = 0;
			dataPlotAnim.start = 0;
			dataPlotAnim.duration = 1;
			//Over-ride
			this.styleM.overrideStyle(this.objects.DATAPLOT, dataPlotAnim, this.styleM.TYPE.ANIMATION, "_alpha");
			delete dataPlotAnim;
		}
		//-----------------------------------------------------------------//                                                                                            
	}
	/**
	 * cleanUp method is called to purge the basics before
	 * regeneration process of chart can begin.
	 */
	private function cleanUp():Void {
		// iterating to get all movieclip and Doughnut3D instances in piechart holder movieclip in order 
		// to clean them up and set the stage ready for next redraw
		for (var p in mcPieH) {
			if (mcPieH[p] instanceof MovieClip) {
				// clearing all event handlers from the movieclips to be removed
				delete mcPieH[p].onRollOver;
				delete mcPieH[p].onRollOut;
				delete mcPieH[p].onRelease;
				delete mcPieH[p].onReleaseOutside;
				//
				mcPieH[p].removeMovieClip();
			}
		}
		
		// deleting all Doughnut3D instances
		for (var p in this.config.objDoughnut3D) {
			
			this.config.objDoughnut3D[p].removeEventListener('slicing', this);
			delete this.config.objDoughnut3D[p];
		}
		// array emptied
		this.config.arrFinal.splice(0);
		// certain properties are set to null
		this.config.rightPie = null;
		this.config.leftPie = null;
		this.config.totalSlices = null;
		this.map = [];
	}
	/**
	 * reInit method re-initializes the chart. This method is basically called
	 * when the user changes chart data through JavaScript. In that case, we need
	 * to re-initialize the chart, set new XML data and again render.
	 * @param	isRedraw	Whether the method is called during the re-draw of chart
	 *						during dynamic resize.
	 */
	public function reInit(isRedraw:Boolean):Void {
		//If call for redraw, ignore the call.
		if(isRedraw){
			return;
		}
		
		//Invoke super class's reInit
		super.reInit();
		//Set the flag that we've to skip bitmap check for image saving
		this.cMC.skipBmpCacheCheck = true;
		//Now initialize things that are pertinent to this class
		//but not defined in super class.
		//Num
		this.num = 0;
		this.lgnd.reset ();
		if (this.params.interactiveLegend){
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
		//Initialize data container
		this.data = new Array();
		//Initialize container to store interval ids
		objIntervalIds = new Object();
		//Initialize container to store mouse listener objects
		objMouseListener = new Object();
		//Variable to store the sum of all values in pie
		this.config.sumOfValues = 0;
		//Configuration whether links have been defined - by default assume no.
		this.config.linksDefined = false;
		// reinitialise counter to be incremented initially to track pie slicing out animation ends
		this.config.iniTracker = 0;
		// reinitialise counter to be incremented initially to track label animation ends of pies
		this.config.iniFinishTracker = 0;
		
		//Map of Doughnut3D objects with their respective entries in this.data
		this.map = [];
		
		//The number pies slicing is reset to zero.
		this.numSlicing = 0;
		
		//The rotating status of the chart is reset to false.
		this.rotating = false;
		
		//Flag to hold status of chart initialisation (config.isInitialised does more than storing that info)
		this.chartInit = false;

		//Reset set node counter to zero
		this.setNodeCounter = 0;
	}
	/**
	 * remove method removes the chart by clearing the chart movie clip
	 * and removing any listeners. However, the logger still stays on.
	 * To remove the logger too, you need to call destroy method of chart.
	*/
	public function remove():Void {
		//Remove listeners associated with this class.
		Mouse.removeListener(this);
		// clear all setInterval ids
		var arrIdObj:Array = [this.objIntervalIds];
		for (var i:Number = 0; i<arrIdObj.length; ++i) {
			for (var j in arrIdObj[i]) {
				clearInterval(arrIdObj[i][j]);
			}
		}
		for (var k in mcPieH) {
			if (mcPieH[k] instanceof MovieClip) {
				clearInterval(mcPieH[k].id);
				// clearing all event handlers from the movieclips to be removed
				delete mcPieH[k].onRollOver;
				delete mcPieH[k].onRollOut;
				delete mcPieH[k].onRelease;
				delete mcPieH[k].onReleaseOutside;
			}
		}
		//Remove class pertinent objects
		if (this.params.interactiveLegend){
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
		//Remove legend
		this.lgnd.destroy ();
		lgndMC.removeMovieClip ();
		//Call super remove
		super.remove();
	}
	/**
	 * destroy method destroys the chart by removing the chart movie clip,
	 * logger movie clip, and removing any listeners. 
	*/
	public function destroy():Void {
		//Remove the chart first
		super.destroy();
		//Now destroy anything additional pertinent to this chart, but
		//not included as a part of parent Chart class.
	}
	// ----------------- DATA READING, PARSING AND STORING ----------------- //
	/**
	 * parseXML method parses the XML data, sets defaults and validates
	 * the attributes before storing them to data storage objects.
	*/
	private function parseXML():Void {
		//Get the element nodes
		var arrDocElement:Array = this.xmlData.childNodes;
		//Look for <graph> element
		for (var i = 0; i<arrDocElement.length; i++) {
			//If it's a <graph> element, proceed.
			//Do case in-sensitive mathcing by changing to upper case
			if (arrDocElement[i].nodeName.toUpperCase() == "GRAPH" || arrDocElement[i].nodeName.toUpperCase() == "CHART") {
				//Extract attributes of <graph> element
				this.parseAttributes(arrDocElement[i]);
				//Extract common attributes/over-ride chart specific ones
				this.parseCommonAttributes (arrDocElement [i], false);
				//Now, get the child nodes - first level nodes
				var arrLevel1Nodes:Array = arrDocElement[i].childNodes;
				var setNode:XMLNode;
				//Iterate through all level 1 nodes.
				for (var j = 0; j<arrLevel1Nodes.length; j++) {
					//If it's Data nodes
					if (arrLevel1Nodes[j].nodeName.toUpperCase() == "SET") {
						//Set Node. So extract the data.
						//Get reference to node.
						setNode = arrLevel1Nodes[j];
						//Get attributes
						var atts:Array;
						atts = this.getAttributesArray(setNode);
						//Extract values.
						//Take absolute values for pie chart, as negative numbers have no place in a pie chart.
						var setValue:Number = Math.abs(this.getSetValue(atts["value"]));
						//if data pertinent for the chart
						if ((setValue != 0 || this.params.showZeroPies) && !isNaN(setValue)) {
							//First, updated counter
							this.num++;
							var setLabel:String = getFV(atts["label"], atts["name"], "");
							var setLink:String = getFV(atts["link"], "");
							var setToolText:String = getFV(atts["tooltext"], atts["hovertext"]);
							//Get explicitly specified display value
							var setExDispVal : String = getFV( atts["displayvalue"], "");
							// string form of hexadecimal code stored, but in a form whose number equivalent can't be 
							// recognised by flash as hexadecimal
							var color:String = String(formatColor(getFV(atts["color"], this.defColors.getColor())));
							// hexadecimal color code stored for the pie
							var setColor:Number = parseInt(color, 16);
							// hexadecimal color code stored for the pie border; ColorExt.getDarkColor() was returning value in form
							// ultimately required by flash, but odd one of the 3, and thus worked upon. 
							var setBorderColor:Number = parseInt(formatColor(getFV(atts["bordercolor"], this.params.pieBorderColor, ColorExt.getDarkColor(color, 0.75).toString(16))), 16);
							// initial slicing staus of the pie
							var setIsSliced:Boolean = toBoolean(getFN(atts["issliced"], 0));
							//Summing up the values
							this.config.sumOfValues += setValue;
							// flag to be used to enable links for user interaction, initially and to keep the same option in the context menu
							this.config.linksDefined = (setLink.length>1) ? true : this.config.linksDefined;
							// Store all these attributes as object.
							this.data[this.num-1] = this.returnDataAsObject(setLabel, setValue, setColor, color, setExDispVal,  setBorderColor, setToolText, setLink, setIsSliced);
						}
						//Reset setNodeCounter
						setNodeCounter++ ;
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
		this.params.palette = getFN(atts["palette"], 2);
		//Palette colors to use
		this.params.paletteColors = getFV(atts["palettecolors"],"");
		//Set palette colors before parsing the <set> nodes.
		this.setPaletteColors();
		//Background properties - Gradient
		this.params.bgColor = getFV(atts["bgcolor"], this.defColors.getBgColor3D(this.params.palette));
		this.params.bgAlpha = getFV(atts["bgalpha"], this.defColors.getBgAlpha3D(this.params.palette));
		this.params.bgRatio = getFV(atts["bgratio"], this.defColors.getBgRatio3D(this.params.palette));
		this.params.bgAngle = getFV(atts["bgangle"], this.defColors.getBgAngle3D(this.params.palette));
		//Border Properties of chart
		this.params.showBorder = toBoolean(getFN(atts["showborder"], 0));
		this.params.borderColor = formatColor(getFV(atts["bordercolor"], "666666"));
		this.params.borderThickness = getFN(atts["borderthickness"], 1);
		this.params.borderAlpha = getFN(atts["borderalpha"], 100);
		//Background swf
		this.params.bgSWF = getFV(atts["bgswf"], "");
		this.params.bgSWFAlpha = getFV(atts["bgswfalpha"], 100);
		// global URL
		this.params.clickURL = getFV(atts["clickurl"], "");
		//Chart Caption and sub Caption
		this.params.caption = getFV(atts["caption"], "");
		this.params.subCaption = getFV(atts["subcaption"], "");
		//captionPadding = Space between caption/subcaption and canvas start Y
		this.params.captionPadding = getFN(atts["captionpadding"], 10);
		//Whether to set animation for entire chart.                                                                                                                                      
		this.params.animation = toBoolean(getFN(atts["animation"], 1));
		//Whether to set the default chart animation
		this.params.defaultAnimation = toBoolean(getFN(atts["defaultanimation"], 1));
		//Configuration to set whether to show the names or not
		this.params.showNames = toBoolean(getFN(atts["showlabels"], atts["shownames"], 1));
		this.params.showValues = toBoolean(getFN(atts["showvalues"], 1));
		//Percentage values in data labels?
		this.params.showPercentValues = toBoolean(getFN(atts["showpercentvalues"], atts["showpercentagevalues"], atts["showpercentageinlabel"], 0));
		//Percentage values in tool tip
		this.params.showPercentInToolTip = toBoolean(getFN(atts["showpercentintooltip"], 1));
		//Seperator character
		this.params.toolTipSepChar = getFV(atts["tooltipsepchar"], atts["hovercapsepchar"], ", ");
		this.params.labelSepChar = getFV(atts["labelsepchar"], this.params.toolTipSepChar);
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
		// the input decimal character in xml
		this.params.inDecimalSeparator = getFV(atts["indecimalseparator"], "");
		// the input thousand seperator character in xml
		this.params.inThousandSeparator = getFV(atts["inthousandseparator"], "");
		//Decimal Precision (number of decimal places to be rounded to)
		this.params.decimals = getFN(atts["decimals"], atts["decimalprecision"], 2);
		//Force Decimal Padding
		this.params.forceDecimals = toBoolean(getFN(atts["forcedecimals"], 0));
		//Legend properties
		this.params.showLegend = toBoolean (getFN (atts ["showlegend"] , 0));
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
		
		//------------------------//
		//Pie related properties                                                                                                                                                                                    
		this.params.pieRadius = Math.abs(getFN(atts["pieradius"], 0));
		this.params.doughnutRadius = Math.abs(getFN(atts["doughnutradius"], 0));
		
		this.params.startingAngle = getFN(atts["startingangle"], 0);
		//Border and Fill Attributes
		this.params.showPlotBorder = toBoolean(getFN(atts["showplotborder"], 1));
		this.params.pieBorderThickness = getFN(atts["plotborderthickness"], atts["pieborderthickness"], 1);
		this.params.pieBorderAlpha = getFN(atts["plotborderalpha"], atts["pieborderalpha"], 10);
		//Validate pieBorderAlpha's existance with respect to showPlotBorder
		this.params.pieBorderAlpha = (this.params.showPlotBorder == true) ? this.params.pieBorderAlpha : 0;
		this.params.pieBorderColor = getFV(atts["plotbordercolor"], atts["piebordercolor"]);
		this.params.pieFillAlpha = getFN(atts["plotfillalpha"], atts["piefillalpha"], 100);
		//
		this.params.pieBottomAlpha = getFN(atts["piebottomalpha"], 0);
		this.params.pieInnerFaceAlpha = getFN(atts["pieinnerfacealpha"], 100);
		this.params.pieOuterFaceAlpha = getFN(atts["pieouterfacealpha"], 100);
		this.params.pieSliceDepth = getFN(atts["pieslicedepth"], 15);
		if (this.params.pieSliceDepth<1) {
			this.params.pieSliceDepth = 1;
		} else if (this.params.pieSliceDepth>this.plotHeight/3) {
			this.params.pieSliceDepth = this.plotHeight/3;
		}
		this.config.pieSliceDepth = this.params.pieSliceDepth;
		this.params.pieYScale = getFN(atts["pieyscale"], 40);
		//Convert yScale to a base of 1 (as the user enters it in base of 100)
		this.config.pieYScale = this.params.pieYScale/100;
		//Slicing distance - Indicates how far a pie will move out when clicked
		//or, if by default the pie is sliced out
		this.params.slicingDistance = getFN(atts["slicingdistance"], 20);
		//Label Distance indicates the space (pixels) (elliptical discounted when 
		//not using smart labels) - During smart labels, we use another reference
		//ellipse to draw the levels on circular. In summation, labelDistance is used
		//only when normal labels are used.
		this.params.labelDistance = getFN(atts["labeldistance"], atts["nametbdistance"], 5);
		// the clearance distance of a label (for pie sliced in) from an adjacent sliced out pie; value to be discounted as per requirement
		this.params.smartLabelClearance = getFN(atts["smartlabelclearance"], 5);
		//Flag to set whether 0 pies (and their values) will be shown
		this.params.showZeroPies = toBoolean(getFN(atts["showzeropies"], 1));
		//Enable rotation on start - only for charts have no links in pie
		this.params.enableRotation = toBoolean(getFN(atts["enablerotation"], 0));
		//Attributes relating to Smart Label
		//Whether to enable smart labels?
		this.params.enableSmartLabels = toBoolean(getFN(atts["enablesmartlabels"], atts["enablesmartlabel"], 1));
		//Skip Labels that are overlapping even when using smart labels?
		this.params.skipOverlapLabels = toBoolean(getFN(atts["skipoverlaplabels"], atts["skipoverlaplabel"], 1));
		//Whether the smart lines are slanted?
		this.params.isSmartLineSlanted = toBoolean(getFN(atts["issmartlineslanted"], 1));
		//Smart line cosmetic properties
		this.params.smartLineColor = String(formatColor(getFV(atts["smartlinecolor"], this.defColors.getDivLineColor3D(this.params.palette))));
		this.params.smartLineThickness = getFN(atts["smartlinethickness"], 1);
		this.params.smartLineAlpha = getFN(atts["smartlinealpha"], 100);
		
		//Whether to apply wordWrap when the labels overlaps/Exceed boundary
		this.params.manageLabelOverflow = toBoolean(getFN(atts["managelabeloverflow"], 0));
		//whether to apply elipses when the label overflows.
		this.params.useEllipsesWhenOverflow = toBoolean(getFN(atts["useellipseswhenoverflow"], atts["useellipsewhenoverflow"],1));
		//whether to use lighting effects to get a more realistic 3D look
		this.params.useLighting = toBoolean(getFN(atts["use3dlighting"], 1));
		//Padding of legend from right/bottom side of canvas
		this.params.legendPadding = getFN (atts ["legendpadding"] , 6);
		//Chart Margins                      
		this.params.chartLeftMargin = getFN(atts["chartleftmargin"], 15);
		this.params.chartRightMargin = getFN(atts["chartrightmargin"], 15);
		this.params.chartTopMargin = getFN(atts["charttopmargin"], 15);
		this.params.chartBottomMargin = getFN(atts["chartbottommargin"], 15);
	}
	/**
	 * returnDataAsObject method creates an object out of the parameters
	 * passed to this method. The idea is that we store each data point
	 * as an object with multiple (flexible) properties. So, we do not 
	 * use a predefined class structure. Instead we use a generic object.
	 */
	private function returnDataAsObject(dataLabel:String, dataValue:Number, color:Number, hexColor:String, displayValue:String, bordercolor:Number, toolText:String, link:String, isSliced:Boolean):Object {
		//Create a container
		var dataObj:Object = new Object();
		//Store the values
		dataObj.label = dataLabel;
		dataObj.value = dataValue;
		//Explicitly specified display value
		dataObj.exDispVal = displayValue;
		dataObj.color = color;
		dataObj.hexColor = hexColor;
		dataObj.borderColor = bordercolor;
		dataObj.toolText = toolText;
		dataObj.link = link;
		dataObj.isSliced = isSliced;
		//dataObj.position is reserved for storing coordinates of the pie
		//Return the container
		return dataObj;
	}
	// ---------------- CALCULATION AND OPTIMIZATION ----------------- //
	/**
	* calculatePoints method calculates the various points on the chart.
	*/
	private function calculatePoints():Void {
		//Always keep to a decimal precision of minimum 2 if the number 
		//scale is defined, as we've just checked for decimal precision of numbers
		//and not the numbers against number scale. So, even if they do not need yield a 
		//decimal, we keep 2, as we do not force decimals on numbers.
		if (this.config.numberScaleDefined == true) {
			maxDecimals = (maxDecimals>2) ? maxDecimals : 2;
		}
		//Get proper value for decimals                                     
		this.params.decimals = Number(getFV(this.params.decimals, maxDecimals));
		//Decimal Precision cannot be less than 0 - so adjust it
		if (this.params.decimals<0) {
			this.params.decimals = 0;
		}
		//Format all the numbers on the chart and store their display and percent values                                     
		//We format and store here itself, so that later, whenever needed,
		//we just access displayValue instead of formatting once again.
		var displayNumToolTip:String, displayNumLabel:String;
		for (var i:Number = 0; i<this.num; i++) {
			//Format and store
			this.data[i].displayValue = formatNumber(this.data[i].value, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, this.params.formatNumberScale, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, this.params.numberPrefix, this.params.numberSuffix);
			this.data[i].percentValue = formatNumber((this.data[i].value/this.config.sumOfValues)*100, this.params.formatNumber, this.params.decimals, this.params.forceDecimals, false, this.params.defaultNumberScale, this.config.nsv, this.config.nsu, "", "%");
			//Set default values for toolText if not specified
			displayNumToolTip = (this.params.showPercentInToolTip == true) ? (this.data[i].percentValue) : (this.data[i].displayValue);
			// setting the seperation character, if any
			var toolSepChar:String = (this.data[i].label != '') ? this.params.toolTipSepChar : '';
			// toolTip text is set and stored for ready reference
			this.data[i].toolText = getFV(this.data[i].toolText, this.data[i].label+toolSepChar+displayNumToolTip);
			//Set values for label text
			displayNumLabel = (this.params.showPercentValues == true) ? (this.data[i].percentValue) : (this.data[i].displayValue);
			//Store another copy of formatted value
			this.data [i].formattedValue = displayNumLabel;
			var strLabel:String = '';
			if (this.params.showNames) {
				strLabel += this.data[i].label;
			}
			if (this.params.showValues) {
				// setting the seperation character, if any
				if (this.params.showNames && this.data[i].label != '') {
					strLabel += this.params.labelSepChar;
				}
				strLabel += displayNumLabel;
			}
			//Choose display value for item (choice between explicit and actual value)
			if (this.data[i].exDispVal != "") {
				this.data[i].labelText = this.data[i].exDispVal;
			}else {
				this.data[i].labelText = strLabel;
			}
		}
		
		//Calculate and/or evaluate and set those properties/params so required w.r.t width/height of the chart.
		this.calculateForWidthAndHeight()
		//----------------------------------------------------------------------//
		this.setChartCenter();
		//---------------------------------------------------------------------//
		//Now to initialise all other calculating methods before the chart can be drawn.
		//initialise containers and flags
		this.initContainers();
		// if the number of pies are more than one
		if (this.num>1) {
			// now methods need to be called to process and set all properties necessary for a multipie data chart
			// to set the number of pies to be sliced initially
			// counter initialised to zero
			this.config.numSlicedPies = 0;
			for (var i:Number = 0; i<this.data.length; ++i) {
				if (this.data[i].isSliced) {
					this.config.numSlicedPies++;
				}
			}
			// horizontal relaxation for smart labels is set to have no overlapping of labels with pies (sliced in or out).
			this.config.relaxation = this.params.slicingDistance+this.params.smartLabelClearance;
			// this.data is populated and hence called for initial processing of data set
			this.calculatePieProps();
		}
	}
	/*
	 * calculateForWidthAndHeight method is called to calculate and/or evaluate and set those
	 * properties/params so required w.r.t width/height of the chart. For the case of resizing,
	 * the properties/params are recalculated and/or re-evaluated.
	 * @param	isRedraw	Whether the method is called during the re-draw of chart
	 *						during dynamic resize.
	 */
	private function calculateForWidthAndHeight(isRedraw:Boolean):Void{
		
		//We now need to calculate the available plot Width on the canvas.
		//Available width = total Chart width minus 
		// - Left and Right Margin
		var canvasWidth:Number = this.width-(this.params.chartLeftMargin+this.params.chartRightMargin);
		//Set canvas startX
		var canvasStartX:Number = this.params.chartLeftMargin;
		//We finally have canvas Width and canvas Start X
		//-----------------------------------------------------------------------------------//
		//Now, we need to calculate the available plot Height on the canvas.
		//Available height = total Chart height minus the list below
		// - Chart Top and Bottom Margins
		// - Space for Caption, Sub Caption and caption padding
		//Initialize canvasHeight to total height minus margins
		var canvasHeight:Number = this.height-(this.params.chartTopMargin+this.params.chartBottomMargin);
		//Set canvasStartY
		var canvasStartY:Number = this.params.chartTopMargin;
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
		//We have canvas start Y and canvas height
		
		
		
		
		//We now check whether the legend is to be drawn
		if (this.params.showLegend)
		{
			//Object to store dimensions
			var lgndDim : Object;
			
			//for the case of resizing
			if(isRedraw){
				//reallocate the original data for legend to the new legend, for changed status of chart is maintained in it.
				var lgndItems:Array = lgnd.items;
				var lgndIdMap:Array = lgnd.arrIdMap;
				
				//"true" for new legend to save bitmaps from getting removed from memory.
				lgnd.destroy(true);
				lgnd = null;
				this.lgndMC.removeMovieClip();
			}
			
			//Create container movie clip for legend
			this.lgndMC = this.cMC.createEmptyMovieClip ("Legend", this.dm.getDepth ("LEGEND"));
			
			if(!isRedraw){
				
				//Blocking the legend interactivity till chart initialisation
				lgndMC.onRelease = function() {};
				//No hand cursor
				lgndMC.useHandCursor = false;
			}
		
			//the chart won't be interactive for singleton case
			var interactive:Boolean = (this.num > 1)? this.params.interactiveLegend : false;
			//Create instance of legend
			if (this.params.legendPosition == "BOTTOM")
			{
				//Maximum Height - 50% of stage
				lgnd = new AdvancedLegend (lgndMC, this.styleM.getTextStyle (this.objects.LEGEND) , interactive, this.params.legendPosition, canvasStartX + canvasWidth / 2, this.height / 2, canvasWidth, (this.height - (this.params.chartTopMargin + this.params.chartBottomMargin)) * 0.5, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			} 
			else 
			{
				//Maximum Width - 40% of stage
				lgnd = new AdvancedLegend (lgndMC, this.styleM.getTextStyle (this.objects.LEGEND) , interactive, this.params.legendPosition, this.width / 2, canvasStartY + canvasHeight / 2, (this.width - (this.params.chartLeftMargin + this.params.chartRightMargin)) * 0.4, canvasHeight, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			}
			
			if(this.params.minimiseWrappingInLegend){
				lgnd.minimiseWrapping = true;
			}
			
			//
			if(!isRedraw){
				
				var iconHeight:Number = lgnd.getIconHeight()*this.params.legendIconScale;
				
				//Feed data set series Name for legend
				for (var i:Number = 0; i<this.num; i++) {
					
					var k:Number = (this.params.reverseLegend) ? this.num - 1 - i : i;
					
					if(!this.data[k].label){
						continue;
					}
					var objIconParams:Object = {fillColor: parseInt(this.data[k].hexColor, 16)};
					var objIcons:Object = LegendIconGenerator.getIcons(LegendIconGenerator.WEDGE, iconHeight, false, objIconParams);	
					var bmpd1:BitmapData = objIcons.active;
					var bmpd2:BitmapData = objIcons.inactive;
					
					lgnd.addItem (this.data[k].label, k, (this.params.interactiveLegend)? !this.data[k].isSliced : true, bmpd1, bmpd2);
				}
				
			}else{
				//
				lgnd.items = lgndItems;
				lgnd.count = lgndItems.length;
				lgnd.arrIdMap = lgndIdMap;
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
		}
		//We now have canvasStartX, canvasStartY, canvasPlot & canvasHeight.                                                                                                    
		//Allot canvasWidth & canvasHeight to plotWidth and plotHeight
		this.plotWidth = canvasWidth;
		this.plotHeight = canvasHeight;
		//Also store X and Y Position
		this.plotX = canvasStartX;
		this.plotY = canvasStartY;
		
	}
	/**
	 * resetRadius method is called to reset initial value of radius for the chart
	 * to start with its search of best fit radius.
	 */
	private function resetRadius():Void{
		//if radius not specified
		if (this.params.pieRadius == 0 || isNaN(this.params.pieRadius)) {
			// chart is initialised temporarily for avoiding initial delay due animations
			this.config.isInitialised = true;
			
			//changed in params directly!
			this.params.animation = false;
			// lesser of the two is taken
			var k:Number = Math.min(plotWidth, plotHeight);
			if (this.num != 1) {
				// 20 % of the former value (k) is the minimum limit of radius ... starting with this value
				// and incrementing untill the best and maximum value for radius is obtained
				this.config.radius = k*0.2;
				// that radius is yet to set is flagged
				this.config.isRadiusGiven = false;
			} else {
				this.config.radius = k*0.4;
				// that radius is yet to set is flagged
				this.config.isRadiusGiven = true;
			}
		}
		
		// inner radius is stored in config
		// if radius is defined
		if (this.config.isRadiusGiven) {
			// if doughnutRadius is properly defined in params
			if (this.params.doughnutRadius != 0 && !(isNaN(this.params.doughnutRadius))) {
				
				
				//if chart is resizing in 3D mode
				if(this.config.pieYScale == this.params.pieYScale/100){
					
					// if outer radius is greater than the inner radius
					if (this.config.radius>this.params.doughnutRadius) {
						this.config.doughnutRadius = this.params.doughnutRadius;
					} else {
						this.config.doughnutRadius = this.config.radius*0.5;
					}
				//else if chart is resizing in 2D mode
				} else{
					
					// if outer radius is greater than the inner radius
					if (this.config.radius3D>this.params.doughnutRadius) {
						this.config.doughnutRadius = this.params.doughnutRadius;
					} else {
						this.config.doughnutRadius = this.config.radius3D*0.5;
					}
					
				}
				
			// else if doughnutRadius is not properly defined in params
			} else {
				
				//if chart is resizing in 3D mode
				if(this.config.pieYScale == this.params.pieYScale/100){
					this.config.doughnutRadius = this.config.radius*0.5;
				//else if chart is resizing in 2D mode
				}else{
					this.config.doughnutRadius = this.config.radius3D*0.5;
				}
			}
		} else {
			this.config.doughnutRadius = this.config.radius*0.5;
		}
	}
	/*
	 * setChartCenter is the method to calculate and set the
	 * coordinates of the center of the chart.
	 */
	private function setChartCenter():Void {
		// storing abscissa of the piechart centre w.r.t. mcPieH
		this.config.centerX = plotWidth/2;
		// storing ordinate of the piechart centre w.r.t. mcPieH
		this.config.centerY = plotHeight/2-this.config.pieSliceDepth/2;
	}
	/**
	 * feedMacros method feeds macros and their respective values
	 * to the macro instance. This method is to be called after
	 * calculatePoints, as we set the canvas and chart co-ordinates
	 * in this method, which is known to us only after calculatePoints.
	 * WE OVER-RIDE THE CHART CLASS METHOD BECAUSE CANVAS IS NOT DEFINED
	 * FOR A PIE CHART. SO, WE JUST ASSUME THE VALUES OF FULL PLOT AREA.
	 *	@returns	Nothing
	*/
	private function feedMacros():Void {
		//Feed macros one by one
		//Chart dimension macros
		this.macro.addMacro("$chartStartX", this.x);
		this.macro.addMacro("$chartStartY", this.y);
		this.macro.addMacro("$chartWidth", this.width);
		this.macro.addMacro("$chartHeight", this.height);
		this.macro.addMacro("$chartEndX", this.width);
		this.macro.addMacro("$chartEndY", this.height);
		this.macro.addMacro("$chartCenterX", this.width/2);
		this.macro.addMacro("$chartCenterY", this.height/2);
		//Canvas dimension macros
		this.macro.addMacro("$canvasStartX", this.x);
		this.macro.addMacro("$canvasStartY", this.y);
		this.macro.addMacro("$canvasWidth", this.width);
		this.macro.addMacro("$canvasHeight", this.height);
		this.macro.addMacro("$canvasEndX", this.width);
		this.macro.addMacro("$canvasEndY", this.height);
		this.macro.addMacro("$canvasCenterX", this.width/2);
		this.macro.addMacro("$canvasCenterY", this.height/2);
	}
	/**
	 * initContainers method instantiates the container objects
	 * for storing calculated results.
	 */
	private function initContainers():Void {
		// Initially check whether this.params.pieRadius is defined.
		// If yes, still store it in this.config.radius
		// Else, calculate and store in this.config.radius
		if (this.params.pieRadius != 0 && !(isNaN(this.params.pieRadius))) {
			// chart is yet to initialise
			// if initial animation is required
			if (this.params.animation) {
				this.config.isInitialised = false;
				// else, behave as if initial animation part is over
			} else {
				this.config.isInitialised = true;
			}
			// radius set
			this.config.radius = this.params.pieRadius;
			// that radius is set is flagged
			this.config.isRadiusGiven = true;
		} else {
			// chart is initialised temporarily for avoiding initial delay due animations
			this.config.isInitialised = true;
			// lesser of the two is taken
			var k:Number = Math.min(plotWidth, plotHeight);
			if (this.num != 1) {
				// 20 % of the former value (k) is the minimum limit of radius ... starting with this value
				// and incrementing untill the best and maximum value for radius is obtained
				this.config.radius = k*0.2;
				// that radius is yet to set is flagged
				this.config.isRadiusGiven = false;
			} else {
				this.config.radius = k*0.4;
				// that radius is yet to set is flagged
				this.config.isRadiusGiven = true;
			}
		}
		// inner radius is stored in config
		// if radius is defined
		if (this.config.isRadiusGiven) {
			// if doughnutRadius is properly defined in params
			if (this.params.doughnutRadius != 0 && !(isNaN(this.params.doughnutRadius))) {
				// if outer radius is greater than the inner radius
				if (this.config.radius>this.params.doughnutRadius) {
					this.config.doughnutRadius = this.params.doughnutRadius;
				} else {
					this.config.doughnutRadius = this.config.radius*0.5;
				}
				// else if doughnutRadius is not properly defined in params
			} else {
				this.config.doughnutRadius = this.config.radius*0.5;
			}
		} else {
			this.config.doughnutRadius = this.config.radius*0.5;
		}
		// defines the starting angle of the first pie in data set - initially
		this.config.startingAngle = this.params.startingAngle;
		// counter to be incremented initially to track doughnut slicing out animation ends
		this.config.iniTracker = 0;
		// counter to be incremented initially to track label animation ends of doughnuts
		this.config.iniFinishTracker = 0;
		// stores the number of pies to be sliced initially
		this.config.numSlicedPies = 0;
		// stores the number of pixels along the x-axis to manage the labels around
		this.config.relaxation = 0;
		// stores whether the chart is currently being recreated to render a diferent view (fine tuning)
		this.config.isStaticRecreation = false;
		// the array stores processed angles and certain other properties for the pie set
		this.config.arrMid = new Array();
		// the final multidimensional array for storing the processed
		// data and properties for the pie set to be drawn
		this.config.arrFinal = new Array();
		// to enable links initially or not 
		this.config.enableLinks = this.config.linksDefined;
		// whether rotation is enabled
		this.config.enableRotation = (this.config.enableLinks) ? false : this.params.enableRotation;
		// boolean denoting whether the plot animations are over
		this.config.isPlotAnimationOver = true;
		// to store pie3D instances
		this.config.objDoughnut3D = new Object();
	}
	/**
	 * loadCurrentSlicingStatus method is called to update
	 * certain database to keep track of the slicing status of
	 * chart, so required for multiple redraw of the chart in
	 * different starting angles, but with same slicing status.
	 */
	private function loadCurrentSlicingStatus():Void {
		// iterating to get all movieclip instances in piechart holder movieclip in order to get their current slicing status 
		for (var p in mcPieH) {
			if (mcPieH[p] instanceof MovieClip) {
				// getting the slicing status
				var isSliced:Boolean = (mcPieH[p].isSlicedIn) ? false : true;
				// location index of the pie in this.data is retrived to reset data 
				var index:Number = mcPieH[p].store['id'];
				// data reset
				this.data[index].isSliced = isSliced;
				// to remove slight shift in location due recreation by right clicked rotation enabling (flashplayer 7.0)
				this.data[index].position = [mcPieH[p]._x, mcPieH[p]._y];
			}
		}
	}
	/**
	 * calculatePieProps method calculates for each pie,
	 * all angle parameters  w.r.t. the startingAngle
	 * obtained from XML, assigns color and tracks
	 * left/right/both boundary conditions. All these are
	 * stored in a multidimensional array. Calls sortZ method.
	 */
	private function calculatePieProps():Void {
		// arrMid is referened in a short local variable
		var a:Array = this.config.arrMid;
		// length of this.data is stored
		var s:Number = this.data.length;
		var angleAdjustment:Number = this.config.startingAngle;
		// angleAdjustment is bounded between 0 to 360
		angleAdjustment = MathExt.boundAngle(angleAdjustment);
		// variable declared to store the updated unbounded cummulative angle for each pie under looping 
		var cummulativeAngle:Number = angleAdjustment;
		//------------------------------------//
		// reference of Pie3DChart instance
		var insRef = this;
		// local function to return total of remaining over data values, yet to be processed
		var valuesLeft:Function = function (j:Number):Number {
			// initialised
			var totalLeft:Number = 0;
			// loop to find  the total value
			for (var i:Number = j; i<s; ++i) {
				// incremented
				totalLeft += insRef.data[i].value;
			}
			// total returned
			return totalLeft;
		};
		//------------------------------------//
		// variable declared to store the sum total of all data set values obtained initially
		var totalValue:Number = valuesLeft(0);
		// looping to calculate and set angle (and few other) properties in sub-arrays in arrMid
		// all angle values are in degrees for now
		for (var i:Number = 0; i<s; ++i) {
			// sub-array created
			a[i] = new Array();
			//----------------------------------------//
			// bounded start angle of the pie is stored
			a[i]['startAngle'] = (valuesLeft(i)>0) ? MathExt.boundAngle(cummulativeAngle) : angleAdjustment;
			// sweepAngle of pie is calculated
			var sweepAngle:Number = (this.data[i].value/totalValue)*360;
			//Very small angles are effectively zero visually.
			if(sweepAngle < 0.00001){
			   sweepAngle = 0;
			} 
			// stores the number of 45 degrees curve drawings
			a[i]['no45degCurves'] = Math.floor(sweepAngle/45);
			// stores the remainder, if any, after the 45 degrees curve drawings are over
			a[i]['remainderAngle'] = MathExt.remainderOf(sweepAngle, 45);
			// cummulativeAngle is updated
			cummulativeAngle += sweepAngle;
			// bounded end angle of the pie is stored
			a[i]['endAngle'] = (valuesLeft(i+1)>0) ? MathExt.boundAngle(cummulativeAngle) : angleAdjustment;
			// storing sweep angle
			a[i]['sweepAngle'] = sweepAngle;
			// calculating bounded mean angle of the pie
			var meanAng:Number = MathExt.boundAngle(a[i]['startAngle']+a[i]['sweepAngle']/2);
			// number - mean angle of the pie is stored
			a[i]['meanAngle'] = meanAng;
			//----------------------------------------//
			// storing hexadecimal color code of pie
			a[i]['pieColor'] = this.data[i].color;
			// storing hexadecimal color code of pie border
			a[i]['borderColor'] = this.data[i].borderColor;
			//----------------------------------------//
			// storing pie label (not used in this form to display text)
			a[i]['label'] = this.data[i].label;
			// text to be displayed in the label
			a[i]['labelText'] = this.data[i].labelText;
			a[i]['toolText'] = this.data[i].toolText;
			// a[i]['labelProps'] is a sub-array (reserved) storing the coordinates for label and quadrant id
			//----------------------------------------//
			// Boolean - stores whether the pie will be sliced initially
			a[i]['isSliced'] = this.data[i].isSliced;
			// original index in this.data is stored (for recreation due rotation)
			a[i]['id'] = i;
			// coordinates of the pie slice movieclip (fine tuning) -- on recreation
			if (this.data[i].position) {
				a[i]['position'] = this.data[i].position;
			}
			// storing the link for the pie if any                                                                                                                                                                                                   
			a[i]['link'] = this.data[i].link;
			//----------------------------------------//
			//getting values in local variables
			var startAngle:Number = a[i]['startAngle'];
			var endAngle:Number = a[i]['endAngle'];
			var sweepAngle:Number = a[i]['sweepAngle'];
			// to check for if the pie includes the 0 degree and stores the information in private instance property                                                                      
			// as well as in the arrMid
			if (startAngle>endAngle) {
				this.config.rightPie = i;
				a[i]['sidePie'] = 'right';
			}
			// to check for if the pie includes the 180 degree and stores the information in private instance property                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
			// as well as in the arrMid
			if ((startAngle<180 && startAngle+sweepAngle>180) || (endAngle>180 && endAngle-sweepAngle<180)) {
				this.config.leftPie = i;
				a[i]['sidePie'] = 'left';
			}
			// to check for if the pie includes the 0 degree as well as the 180 degree and stores the information                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
			// in the arrMid
			if ((this.config.leftPie == i && this.config.rightPie == i) || (startAngle == endAngle && sweepAngle != 0)) {
				a[i]['sidePie'] = 'both';
				if (startAngle == endAngle && sweepAngle != 0) {
					this.config.leftPie = i;
					this.config.rightPie = i;
				}
			}
		}
		// arrMid is ready for further processing to get the data set suffled w.r.t. z-scale levels...so sortZ()
		sortZ();
	}
	/**
	 * sortZ method is called from calculatePieProps method.
	 * It resuffles sub-arrays and redefines various 
	 * properties in the multidimensional array storage obtained 	 
	 * from calculatePieProps(). The main purpose is to get the 
	 * order of the levels ordering of the pie set for proper
	 * mutual display in 3d and during animation. Methods 
	 * for labelling are called in between. 
	 */
	private function sortZ():Void {
		// sub-arrays are to be removed from arrMid and placed in a blank array (arrFinal) after sorting.
		// set reference of arrMid to a short local variable
		var d:Array = this.config.arrMid;
		// local variables of boolean datatype declared
		var isConjugation:Boolean, isUpperjunction:Boolean, isLowerjunction:Boolean;
		// local variables of number datatype declared
		var upperjunctionID:Number, lowerjunctionID:Number, upperRightLimit:Number, lowerRightLimit:Number, upperLeftLimit:Number, lowerLeftLimit:Number;
		// length of arrMid is stored
		var aLength:Number = d.length;
		// storing reference of the pie3DChart instance to be used within the local function
		var insRef = this;
		// local function defined to return the value unBounded value of endAngle as required
		var getEndAngle:Function = function (id):Number {
			var eAng:Number = d[id]['endAngle'];
			//if the pie includes zero degree properly
			if (insRef.config.rightPie == id) {
				return 360+eAng;
			} else {
				return eAng;
			}
		};
		// looping through arrMid to demark the pie set w.r.t. 90 and 270 degrees, 
		// to get the sets of pie in left and right of central vertical symmetry line
		for (var i = 0; i<aLength; ++i) {
			// storing start, end and sweep angles of the pie under current loop ( in degrees )
			// static methods of this class are called to do so
			var sAngle:Number = d[i]['startAngle'];
			var eAngle:Number = d[i]['endAngle'];
			var swAngle:Number = d[i]['sweepAngle'];
			// tracking the case of 90 degree as the interface of two pie
			if ((eAngle == 90 || (i == 0 && sAngle == 90)) && isUpperjunction == undefined) {
				//('case 1');
				// then interface exists at 90 degree
				isUpperjunction = true;
				if (eAngle == 90) {
					// stores id of the pie in the lowermost level in z-scale for the right pie-set 
					upperRightLimit = i;
					// stores id of the pie in the lowermost level in z-scale for the left pie-set 
					upperLeftLimit = (i+1 == aLength) ? 0 : i+1;
				} else if (sAngle == 90) {
					// stores id of the pie in the lowermost level in z-scale for the right pie-set 
					upperLeftLimit = i;
					// stores id of the pie in the lowermost level in z-scale for the left pie-set 
					upperRightLimit = aLength-1;
				}
				// tracking if 270 degree is properly included in this pie                                                              
				if ((sAngle<270 && sAngle>90) || ((eAngle>270 || this.config.rightPie == i) && sAngle == 90)) {
					// then no interface possible at 270 degree
					isLowerjunction = false;
					// stores id of the pie which rules out the presence of a lower junction
					lowerjunctionID = i;
					// tracking the case of 270 degree as the interface of two pie
				} else if (sAngle == 90 && eAngle == 270) {
					// then interface exists at 270 degree
					isLowerjunction = true;
					// stores id of the pie in the uppermost level in z-scale for the left pie-set 
					lowerLeftLimit = i;
					// stores id of the pie in the uppermost level in z-scale for the right pie-set 
					lowerRightLimit = (i+1 == aLength) ? 0 : i+1;
				}
				// tracking the case of 90 degree being properly included in a pie                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			} else if (((sAngle<90 && getEndAngle(i)>90) || (this.config.rightPie == i && eAngle>90 && sAngle != 270)) && (eAngle != 270 || i == aLength-1) && isUpperjunction == undefined && isConjugation == undefined) {
				//('case 2');
				// then no interface exists at 90 degree
				isUpperjunction = false;
				// stores id of the pie which rules out the presence of an upper junction
				upperjunctionID = i;
				// tracking if 270 degree is also properly included in this pie
				if ((this.config.rightPie == i && sAngle<270) || (this.config.leftPie == i && eAngle>270)) {
					// private property is set to true to flag the existence of a pie including 
					// both 90 and 270 degrees and hence the requirement for a conjugated pie
					isConjugation = true;
					// then no interface possible at 270 degree
					isLowerjunction = false;
					// stores id of the pie which rules out the presence of a lower junction
					lowerjunctionID = i;
				}
				// tracking the case of 270 degree as the interface of two pie  ... with a special case                  
				// of multi pie set with all but one is zero and starting angle is 270 (---> swAngle != 0)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
			} else if ((eAngle == 270 || (i == 0 && sAngle == 270)) && swAngle != 0 && isLowerjunction == undefined) {
				//('case 3');
				// then interface exists at 270 degree
				isLowerjunction = true;
				if (eAngle == 270) {
					// stores id of the pie in the uppermost level in z-scale for the left pie-set 
					lowerLeftLimit = i;
					// stores id of the pie in the uppermost level in z-scale for the right pie-set 
					lowerRightLimit = (i+1 == aLength) ? 0 : i+1;
				} else if (sAngle == 270) {
					// stores id of the pie in the uppermost level in z-scale for the left pie-set 
					lowerRightLimit = i;
					// stores id of the pie in the uppermost level in z-scale for the right pie-set 
					lowerLeftLimit = aLength-1;
				}
				// tracking if 90 degree is properly included in this pie   ... with a special case                  
				// of multi pie set with all but one is zero and starting angle is 270 (---> swAngle == 360)                                              
				if ((((sAngle>=0 && sAngle<90) || this.config.rightPie == i) && sAngle != 270) || (sAngle == 270 && eAngle>90 && eAngle<270) || swAngle == 360) {
					// then no interface possible at 90 degree
					isUpperjunction = false;
					// stores id of the pie which rules out the presence of an upper junction
					upperjunctionID = i;
				}
				// tracking the case of 270 degree being properly included in a pie                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
			} else if (((sAngle<270 && getEndAngle(i)>270) || (this.config.rightPie == i && eAngle>270)) && isLowerjunction == undefined && isConjugation == undefined) {
				//('case 4');
				// then no interface exists at 270 degree
				isLowerjunction = false;
				// stores id of the pie which rules out the presence of a lower junction
				lowerjunctionID = i;
				// tracking if 90 degree is also properly included in this pie
				if ((this.config.rightPie == i && eAngle>90) || (this.config.leftPie == i && sAngle<90)) {
					// private property is set to true to flag the existence of a pie including 
					// both 90 and 270 degrees and hence the requirement for a conjugated pie
					isConjugation = true;
					// then no interface possible at 90 degree
					isUpperjunction = false;
					// stores id of the pie which rules out the presence of an upper junction
					upperjunctionID = i;
				}
			}
		}
		// reconstituting junction related informations just obtained 
		if (isConjugation) {
			// upperjunctionID ( = lowerjunctionID ) is chosen arbitrarily 
			var CJ_id:Number = upperjunctionID;
		} else {
			// Reconstitution is actually required when interface at 90 or 270 degree exists.
			// left side limit values are chosen arbitrarily
			var UJ_id:Number = (!isUpperjunction) ? upperjunctionID : upperLeftLimit;
			var LJ_id:Number = (!isLowerjunction) ? lowerjunctionID : lowerLeftLimit;
			// special case convertion for simpler sorting logic ... with a special case of both undefined
			if (UJ_id == LJ_id && UJ_id != undefined) {
				isConjugation = true;
				// UJ_id (= LJ_id)is set arbitrarily
				var CJ_id:Number = UJ_id;
			}
		}
		// set reference of arrFinal (blank array) to a short local variable
		var q:Array = this.config.arrFinal;
		var a1:Array, count:Number, strFlag:String;
		//
		// now arrMid is spliced thrice untill nothing is left over in it (except a string flag).
		// first splice is done to extract:
		// the single major pie in case of isConjugation is true 
		// other wise, extracts a continuous subset of the pie-set for the full rightside 
		// or leftside including both junction pie, lower and upper. A string flag replaces
		// the extracted section.
		//
		// the major pie slice is processed below
		if (isConjugation) {
			// replacement flag stored
			strFlag = 'CJ';
			// sub-array for the major pie is extracted and stored in a different array
			a1 = d.splice(CJ_id, 1, 'CJ');
			// the single sub-array is pushed in arrFinal
			q.push(a1[0]);
			// flags whether this major pie slice covers the whole of left or right. This determines
			// which side is left over for all the other minor pie slices for their occurence.
			if (q[0]['startAngle']>90 && q[0]['startAngle']<=270) {
				var strMajorPieSide:String = 'right';
			} else {
				var strMajorPieSide:String = 'left';
			}
			// left subset of pie slices are processed below
		} else if (UJ_id<LJ_id) {
			// replacement flag stored
			strFlag = 'UJ';
			// the number of sub-arrays to extract
			count = LJ_id-UJ_id+1;
			// sub-arrays for the left side are extracted and stored in a different array
			a1 = d.splice(UJ_id, count, 'UJ');
			// sub-arrays pushed in arrFinal by looping
			for (var f = 0; f<a1.length; ++f) {
				q.push(a1[f]);
			}
			// right subset of pie slices are processed below
		} else if (UJ_id>LJ_id) {
			// replacement flag stored
			strFlag = 'LJ';
			// the number of sub-arrays to extract
			count = UJ_id-LJ_id+1;
			// sub-arrays for the right side are extracted and stored in a different array
			a1 = d.splice(LJ_id, count, 'LJ');
			// array reversed to get the proper z-scale sorting order ... not the case for left side
			// cause: initial data set was ordered anticlockwise, but right side ordering neeed be clockwise
			a1.reverse();
			// sub-arrays pushed in arrFinal by looping
			for (var f = 0; f<a1.length; ++f) {
				q.push(a1[f]);
			}
		} else {
			// else, simply copy the array ... for both ids are equal or one/both undefined
			for (var f = 0; f<d.length; ++f) {
				q.push(d[f]);
			}
		}
		// the last sub-array in the extracted subset (if isConjugation is not true) denotes either the                                                                                                                                                                                                                                                                 
		// lower junction pie (including 270 degree) or the topmost (in z-scale) pie of left side. thus, either
		// way, the last sub-array will be extracted and kept aside to be inserted at last position finally.
		if (!isConjugation) {
			//last sub-array extracted and saved
			var objEndSaved:Object = q.pop();
		}
		// locating the flag position in arrMid                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
		for (var f = 0; f<d.length; ++f) {
			if (d[f] == strFlag) {
				// flag position stored
				var flagPos = f;
			}
		}
		// ArrMid is spliced second time to extract sub-arrays preceding the flag
		var a2:Array = d.splice(0, flagPos);
		// ArrMid is spliced third time to extract sub-arrays succeeding the flag
		var a3:Array = d.splice(1);
		// job of arrMid is over
		// now, sub-array elements of a2 and a3 together constitute one side of the pie chart.
		// they will be inserted in arrFinal in proper order as per the case.
		if (strFlag == 'LJ' || (strFlag == 'CJ' && strMajorPieSide == 'right')) {
			for (var f = 0; f<a3.length; ++f) {
				q.push(a3[f]);
			}
			for (var f = 0; f<a2.length; ++f) {
				q.push(a2[f]);
			}
		} else if (strFlag == 'UJ' || (strFlag == 'CJ' && strMajorPieSide == 'left')) {
			a2.reverse();
			a3.reverse();
			for (var f = 0; f<a2.length; ++f) {
				q.push(a2[f]);
			}
			for (var f = 0; f<a3.length; ++f) {
				q.push(a3[f]);
			}
		}
		// Finally, if isConjugation is not true, the sub-array previously extracted and kept aside is                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
		// inserted at the end of the arrFinal, otherwise ... conjugated pie is required instead of a single
		// major pie. Conjugated pie is equivalent to the major pie but without the shortcoming in z-leveling.
		// The major pie is split into two at the angle 270 degree. The one including the 90 degree is maintained
		// in the z-level of the original major pie, while the other one is z-leveled at the top of all. So at
		// the end of the arrFinal is inserted an extra sub-array to achieve that.
		//
		// the sub-array stored in objEndSaved is inserted at the end of arrFinal
		if (!isConjugation) {
			q.push(objEndSaved);
			if (this.params.showNames || this.params.showValues) {
				// label positions need to set at this point for the very next major step is call for Pie3D instantiation
				// if smart labels is required as per xml
				if (this.params.enableSmartLabels) {
					// labels are managed to avoid overlap
					setSmartLabels();
					// else, labels are not managed to avoid overlap
				} else {
					setLabels();
				}
			}
			// job to set conjugated pie is done below                                                                                                                                                                
		} else {
			if (this.params.showNames || this.params.showValues) {
				// label positions need to set at this point for what follows splits the major pie to be a pair of conjugated ones but with the same label.
				// To avoid duplication of same label at different positions.
				if (this.params.enableSmartLabels) {
					// labels are managed to avoid overlap
					setSmartLabels();
					// else, labels are not managed to avoid overlap
				} else {
					setLabels();
				}
			}
			// A duplicate of the first sub-array is to be created at the extreme end of arrFinal.                                                                                                                                                                
			// a blank array is created
			var k:Array = new Array();
			// loop runs to duplicate the content of the sub-array without having any reference to the original one
			for (var u in q[0]) {
				k[u] = q[0][u];
			}
			// duplicate sub-array is inserted at the end of the arrFinal
			q.push(k);
			// Now modify the data in the first and last sub-arrays of arrFinal where two cases are possible.
			// The major pie will totally cover either the left or right side.
			// storing the start and end angles of the original pie for check in processing the cases
			var sAng:Number = q[0]['startAngle'];
			var eAng:Number = q[0]['endAngle'];
			// calculating sweep angle for the left hand pie (d1)
			if (sAng>270) {
				var d1:Number = 270+(360-sAng);
			} else {
				var d1:Number = 270-sAng;
			}
			// calculating sweep angle for the right hand pie (d2)
			if (eAng>=0 && eAng<270) {
				var d2:Number = eAng+90;
			} else {
				var d2:Number = eAng-270;
			}
			if (sAng == 270 && eAng == 270) {
				var d1:Number = 360;
				var d2:Number = 0;
			}
			// final modification of the two polar sub-arrays:                  
			// for the case of major pie including the whole of right side
			if (sAng>90 && sAng<270) {
				// for left hand pie:
				// start angle remains same 
				q[q.length-1]['no45degCurves'] = Math.floor(d1/45);
				q[q.length-1]['remainderAngle'] = MathExt.remainderOf(d1, 45);
				// end angle is 270 degree
				q[q.length-1]['endAngle'] = 270;
				// sweep angle
				q[q.length-1]['sweepAngle'] = d1;
				// flaggged that this pie is the left one of the two
				q[q.length-1]['junctionSide'] = 'left';
				q[q.length-1]['twinId'] = 0;
				// One of the two pies is set to true and the other null
				q[q.length-1]['isConjugated'] = null;
				// 
				// for right hand pie:
				// start angle is 270 degree
				q[0]['startAngle'] = 270;
				q[0]['no45degCurves'] = Math.floor(d2/45);
				q[0]['remainderAngle'] = MathExt.remainderOf(d2, 45);
				q[0]['sweepAngle'] = d2;
				// end angle remains same
				// to avoid superimposed labels (this case is chosen arbitrarily)
				q[0]['isLabelInvisible'] = true;
				// flaggged that this pie is the right one of the two
				q[0]['junctionSide'] = 'right';
				q[0]['twinId'] = 1;
				// One of the two pies is set to true and the other null
				q[0]['isConjugated'] = true;
				// setting 'sidePie' property of the two if the original one covers both 0 and 180 degrees
				if (q[0]['sidePie'] == 'both') {
					// two cases: 
					// either they share the two extreme horizontal ends
					if (q[0]['endAngle']<180) {
						q[0]['sidePie'] = 'right';
						q[q.length-1]['sidePie'] = 'left';
						// or one possess both while the other none
					} else {
						q[q.length-1]['sidePie'] = null;
					}
				} else {
					q[q.length-1]['sidePie'] = null;
				}
				// for the case of major pie including the whole of left side                                                                                                                                                                                                                                                                                                                                                                                                                 
			} else {
				// for left hand pie:
				// start angle remains same 
				q[0]['no45degCurves'] = Math.floor(d1/45);
				q[0]['remainderAngle'] = MathExt.remainderOf(d1, 45);
				// end angle is 270 degree
				q[0]['endAngle'] = 270;
				q[0]['sweepAngle'] = d1;
				// to avoid superimposed labels (this case is chosen arbitrarily)
				q[0]['isLabelInvisible'] = true;
				// flaggged that this pie is the left one of the two
				q[0]['junctionSide'] = 'left';
				q[0]['twinId'] = 0;
				// One of the two pies is set to true and the other null
				q[0]['isConjugated'] = true;
				// 
				// for right hand pie:
				// start angle is 270 degree
				q[q.length-1]['startAngle'] = 270;
				q[q.length-1]['no45degCurves'] = Math.floor(d2/45);
				q[q.length-1]['remainderAngle'] = MathExt.remainderOf(d2, 45);
				q[q.length-1]['sweepAngle'] = d2;
				// end angle remains same
				// flaggged that this pie is the right one of the two
				q[q.length-1]['junctionSide'] = 'right';
				q[q.length-1]['twinId'] = 1;
				// One of the two pies is set to true and the other null
				q[q.length-1]['isConjugated'] = null;
				// setting 'sidePie' property of the two if the original one covers both 0 and 180 degrees
				if (q[0]['sidePie'] == 'both') {
					// two cases: 
					// either one possess both while the other none
					// = ... for pie set like 0,0,4,0 and starting angle = 270
					if (q[0]['startAngle']>=270) {
						q[q.length-1]['sidePie'] = null;
						// or they share the two extreme horizontal ends
					} else {
						q[0]['sidePie'] = 'left';
						q[q.length-1]['sidePie'] = 'right';
					}
				} else {
					q[q.length-1]['sidePie'] = null;
				}
			}
		}
		// private instance property is set to the length of arrFinal or the actual number of pie to be drawn
		this.config.totalSlices = q.length;
		// till now, all angles were in degrees - so change the required ones (for drawing) to radians
		for (var i = 0; i<this.config.totalSlices; ++i) {
			// start angle to radian
			q[i]['startAngle'] = MathExt.toRadians(q[i]['startAngle']);
			// remainder angle to radian
			q[i]['remainderAngle'] = MathExt.toRadians(q[i]['remainderAngle']);
		}
		// Its all ready for a final call to generate the pie chart.
	}
	/**
	 * setLabels method is called if smart labelling is not 
	 * required, to set position of the labels.
	 */
	private function setLabels():Void {
		var arrX:Array = this.config.arrFinal;
		// distance of the label from the periphery of the pie if label is along x-axis or for pieYScale = 100 ie. no squeeze
		var outDisplacement:Number = this.params.labelDistance;
		// total distance from chart center for no squeeze (length of semi-major axis of reference ellipse for placing labels)
		var a:Number = this.config.radius+outDisplacement;
		// length of semi-minor axis of reference ellipse for placing labels
		var b:Number = a*this.config.pieYScale;
		var d:Number = this.config.pieSliceDepth;
		// iterating to set label position of the pies one at a time
		for (var i:Number = 0; i<arrX.length; ++i) {
			// mean angle in degrees
			var m:Number = arrX[i]['meanAngle'];
			var meanAng:Number = MathExt.toRadians(m);
			// using concepts of ellipse --- (x,y) on the ellipse with semi major and minor axis lengths as a and b respectively.
			var labelX:Number = toNT(this.config.centerX+a*Math.cos(meanAng));
			var labelY:Number = toNT(this.config.centerY+d+b*Math.sin(meanAng));
			var quadrantId:Number;
			if (m<=90) {
				quadrantId = 1;
			} else if (m>90 && m<=180) {
				quadrantId = 2;
				// lower quadrants need their labels be shifted down by d more to keep labels clear from the chart.
			} else if (m>180 && m<=270) {
				quadrantId = 3;
				labelY -= d;
			} else {
				quadrantId = 4;
				labelY -= d;
			}
			// positions along with quadrant id is stored in respective cells in sub-arrays for pies in arrFinal
			arrX[i]['labelProps'] = [labelX, labelY, quadrantId];
			
			// a special value of label type is set type1 = NonSmart labels - where only wordwrap applied on overflow.
			//always push .. as the 3rd index could also be used for icon
			arrX[i]['labelProps'].push("type1");
		}
	}
	/**
	 * setSmartLabels method is called at the end of sortZ().
	 * It calculates the coordinates of the labels to be
	 * placed with the pies and stores them in the respective
	 * sub-arrays of arrFinal.
	 */
	private function setSmartLabels():Void {
		var d:Array = this.config.arrFinal;
		//to clearup all previous settings in the very initial phases due checkBounds for evaluating the best fit 2D/3D radii
		for (var m in d) {
			d[m].labelProps = null;
		}
		// an array to store the number of pies to be labelled in the 4 quadrants ... all elements initialised to zero
		var e:Array = [0, 0, 0, 0];
		// empty containers to hold objects respective to pies and their quadrants ... arr1 for pies of quadrant#1 
		var arr1:Array = new Array();
		var arr2:Array = new Array();
		var arr3:Array = new Array();
		var arr4:Array = new Array();
		//
		// iterated to  get the quadrant of pies, one at a time and hence formation and push of objects holding certain pie properties, in the respective array 
		for (var v:Number = 0; v<d.length; ++v) {
			// to avoid one of the conjugated pair ... 
			if (d[v]['isConjugated']) {
				continue;
			}
			// mean angle stored                                             
			var m:Number = d[v]['meanAngle'];
			// sweep angle stored
			var s:Number = d[v]['sweepAngle'];
			// checking over mean angle 
			if (m<=90) {
				arr1.push({meanAng:m, id:v, sweepAng:s});
				// incrementing the counter to get the number of pies in the quadrant
				e[0]++;
			} else if (m>90 && m<=180) {
				arr2.push({meanAng:m, id:v, sweepAng:s});
				e[1]++;
			} else if (m>180 && m<=270) {
				arr3.push({meanAng:m, id:v, sweepAng:s});
				e[2]++;
			} else {
				arr4.push({meanAng:m, id:v, sweepAng:s});
				e[3]++;
			}
		}
		// sorting the arrays to have their elements arranged such that the mean angles are in the ascending order.
		arr1.sortOn(['meanAng', 'id'], [16, 16 | 2]);
		arr2.sortOn(['meanAng', 'id'], [16, 16]);
		arr3.sortOn(['meanAng', 'id'], [16, 16]);
		arr4.sortOn(['meanAng', 'id'], [16, 16 | 2]);
		// Only these two are reversed because, the next algorithm for setting smart label positions are based on 
		// calculations using vertical extremes of the reference ellipse as starting point of setting labels.
		// For example, first element of arr1 is a pie with mean angle smallest than all others in arr1, say,10 degree,
		// but if arr1 is kept unreversed, this pie, among others in arr1, will be dealt first with its label placed 
		// close to upper end of reference ellipse (close to 90 degree) where as it should be near middle portion of 
		// the chart ie. close to 10 degree. But in arr2, first element (pie) is closest to upper extreme while the 
		// second one is farther and so on. The case is opposite for arr1. So reversed.
		arr1.reverse();
		arr3.reverse();
		//------------------------------------
		// to track the height of the label text fields ... will be used in numerous calculations to follow
		var strTxt:String = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=/*-+~`';
		var objProp:Object = this.styleM.getTextStyle(this.objects.DATALABELS);
		//
		var fmtTxt:TextFormat = new TextFormat();
		fmtTxt.font = objProp.font;
		fmtTxt.size = objProp.size;
		fmtTxt.italic = objProp.italic;
		fmtTxt.bold = objProp.bold;
		fmtTxt.underline = objProp.underline;
		fmtTxt.letterSpacing = objProp.letterSpacing;
		//
		var objMetrics:Object = fmtTxt.getTextExtent(strTxt);
		var h:Number = Math.ceil(objMetrics.textFieldHeight);
		// maximum number of labels that can be arranged vertically in a quadrant (not yet used ... but probably will be in future)
		var n:Number = Math.floor((plotHeight/2)/h);
		// to have the maximum number of labels of all quadrants
		var f:Number = 0;
		for (var i:Number = 0; i<4; ++i) {
			f = Math.max(f, e[i]);
		}
		var eb:Number;
		// length of semi-minor axis of the pie ellipse
		var sb:Number = this.config.radius*this.config.pieYScale;
		// difference between the lengths of semi-minor axes of the pie ellipses for sliced in and out conditions
		var xb:Number = this.params.slicingDistance*this.config.pieYScale;
		// the minimum length of relaxation for the smart labels from the periphery of the pies 
		// - to avoid overlap of label of a sliced-in pie with a sliced-out pie itself
		// - length of semi-minor axis of the reference ellipse for the labels must be atleast this plus sb defined above
		var extMin:Number = this.config.pieSliceDepth+xb;
		// setting the length of semi-minor axis of labelling ellipse.
		// (f-1) because, f th label will just touch the top end of the reference ellipse
		var eb1:Number = Math.max(sb+extMin, (f-1)*h);
		// -h because, label touching the topmost point of reference ellipse must have atleast h space 
		// vertically between topmost extreme of the ellipse and the plot boundary
		var eb2:Number = (plotHeight/2)-h;
		eb = Math.min(eb1, eb2);
		if (eb == sb+extMin) {
			var isArbit:Boolean = true;
		}
		// not math.ceil() ... the maximum number of labels that can be accomodated in any quadrant without overlap                                                                                                                                                                                                                                                                                                                                    
		var max:Number = Math.floor(eb/h)+1;
		//------------------------------------------------------
		// iterated to get and call the respective case method for the 4 quadrants
		for (var p:Number = 0; p<4; ++p) {
			// quadrant id generated
			var quadId:Number = p+1;
			// specific array to work on is referenced
			var arrX:Array = eval('arr'+quadId);
			// checking for 3 possible cases and call for action accordingly to set the labels smartly
			if (e[p]<max || isArbit) {
				arrangeLabels(arrX, eb, h, quadId, false);
			} else if (e[p] == max) {
				arrangeMaxLabels(arrX, eb, h, quadId);
			} else if (e[p]>max) {
				setOverloadedLabels(arrX, eb, h, quadId, max);
			}
		}
	}
	/**
	 * setOverloadedLabels is the method called to set labels
	 * in a quadrant having number of labels greater than the
	 * maximum permissible with no overlap.
	 * @param	arrX			A sorted array of objects with properties
	 *							like mean angle,sweep angle and an intezer id
	 *							denoting the original of the pie in arrFinal.
	 * @param	b				A number denoting the length of semi-minor
	 *							axis of the reference ellipse for drawing smart
	 *							labels.
	 * @param	h				A number denoting the height of the text
	 *							fields w.r.t. the font size of labels.
	 * @param	quadrantId		A number indicating the quadrant
	 *							processing. Values can be - 1,2,3,4 only
	 * @param	max				A number denoting the maximum number of 
	 *							labels permissible in any quadrant with no 
	 *							overlap.
	 */
	private function setOverloadedLabels(arrX:Array, b:Number, h:Number, quadrantId:Number, max:Number):Void {
		// if specified in the xml to manage quadrants with overloaded labels, then:
		// 1. form an array containing 'max' number of elements - with pie references
		//    in descending order of sweep angles
		// 2. call for arrangeMaxLabels() passing the above array
		// 3. leftover pie references are also stored in another array
		if (this.params.skipOverlapLabels) {
			arrX.sortOn('sweepAng', 16);
			// to have the descending order of sweep angles
			arrX.reverse();
			// array holding pie references for which labels will be displayed is formed
			var arrY:Array = arrX.splice(0, max);
			// to have the array in ascending order of mean angles
			arrY.sortOn('meanAng', 16);
			// for these two quadrants, the array need to be reversed to have the proper order required by 
			// the label setting algorithm (refer to setSmartLabels() for detailed explanation)
			if (quadrantId == 1 || quadrantId == 3) {
				arrY.reverse();
			}
			// called for setting labels, passing the respective array                                                                                                    
			arrangeMaxLabels(arrY, b, h, quadrantId);
			// else, if overloaded quadrants are not asked to manage in xml, just distribute the labels equally in the available space
		} else {
			setOverLoadedLabelsEqually(arrX, b, h, quadrantId);
		}
	}
	/**
	 * arrangeLabels method is the main among the methods
	 * dealing smart labelling. If the number of labels
	 * is less than the maximum permissible in a quadrant 
	 * without overlapping, then this method is called to 
	 * set the coordinates of the labels w.r.t. their parent
	 * movieclip. Its called for all labels in a quadrant.
	 * @param	arrX			A sorted array of objects with properties
	 *							like mean angle,sweep angle and an intezer id
	 *							denoting the original of the pie in arrFinal.
	 * @param	b				A number denoting the length of semi-minor
	 *							axis of the reference ellipse for drawing smart
	 *							labels.
	 * @param	h				A number denoting the height of the text
	 *							fields w.r.t. the font size of labels.
	 * @param	quadrantId		A number indicating the quadrant
	 *							processing. Values can be - 1,2,3,4 only
	 * @param	isRecurring		This method is called recursively
	 *							to set labels smartly. Boolean value indicating
	 *							whether this call is recursive or not.
	 * @param	recurTimes		A number denoting the number of 
	 *							recursing calls.
	 */
	private function arrangeLabels(arrX:Array, b:Number, h:Number, quadrantId:Number, isRecurring:Boolean, recurTimes:Number):Void {
		var extension:Number = this.config.relaxation;
		// length of semi-major axis of the pie ellipse
		var a1:Number = this.config.radius;
		// length of semi-minor axis of the pie ellipse
		var b1:Number = this.config.radius*this.config.pieYScale;
		// length of semi-major axis of the reference ellipse for labels
		var a2:Number = this.config.radius+extension;
		// length of semi-minor axis of the reference ellipse for labels
		var b2:Number = b;
		// ordinate of the chart center
		var yCenter:Number = this.config.centerY+this.config.pieSliceDepth/2;
		// to hold sub-arrays containing ordinate and location id of the pie in the arrFinal for setting final data in
		var arr1:Array = new Array();
		// iterating to take up a pie at a time
		for (var i:Number = 0; i<arrX.length; ++i) {
			// if this is a recursive call to set label positions for this quadrant
			if (isRecurring) {
				// don't calculate the ordinate to start with ... rather take up the latest value for this label so far calculated 
				var y1:Number = this.config.arrFinal[arrX[i].id]['labelProps'][1];
				// if this is an initial call to set label positions for this quadrant
			} else {
				// calculate the ordinate to start with for this pie
				// mean angle of the pie in radians stored (eccentric angle)
				var meanAng:Number = MathExt.toRadians(arrX[i].meanAng);
				// eccentric angle w.r.t. reference ellipse for labels corresponding to meanAng of the pie w.r.t. pie ellipse
				var angX:Number = Math.atan(a2*b1*Math.tan(meanAng)/(b2*a1));
				// adjustment for Math.atan which returns angle between  -90 to 90 degreees (obviously in radians) only
				if (arrX[i].meanAng>90 && arrX[i].meanAng<=270) {
					angX += Math.PI;
				}
				// initial ordinate for the pie label is calculated                                                                                                                         
				var y1:Number = toNT(yCenter+b2*Math.sin(angX));
			}
			// sub-arrays are assigned ordinate and location id of the pie (in the arrFinal)
			arr1[i] = [y1, arrX[i].id];
		}
		// an undefined variable to store if overlap is tracked between labels in this quadrant, in this call of the method
		var isOverlap:Boolean;
		// will store the location id for the pies having free space below/above its label and the next pie label in that quadrant accordingly as its a upper/lower quadrant
		var arr2:Array = new Array();
		// will store sub-arrays containing continuous sequence of pie ids w.r.t. arr1, denoting aggregates of pies with no free space with their next ones
		var arr3:Array = new Array();
		// Iterated to take up a pie at a time excluding the last one. Cause, overlap is checked between 
		// two consecutive pies, currently under loop and the very next one, but for the last pie, there 
		// won't be a next one to check overlap with.
		for (var i:Number = 0; i<arr1.length-1; ++i) {
			// ordinate of the pie label under loop
			var y1:Number = arr1[i][0];
			// ordinate of the next pie label
			var y2:Number = arr1[i+1][0];
			// difference in ordinates of the pie label ... in proper order
			var t:Number = (quadrantId == 1 || quadrantId == 2) ? y1-y2 : y2-y1;
			// checking if difference in ordinates is greater than label height - just touching case 
			// is excluded to take care of adjustments made in previous attempts
			if (t>h) {
				arr2.push(i);
			}
			// checking if difference in ordinates is less than label height                                                                                                                        
			if (t<h) {
				isOverlap = true;
			}
		}
		// arr2.length = 0 implies that none of the pies are having free space between it and its next pie (as in arr1)
		// therefore, free space exists only after the last pie in arr1
		if (arr2.length == 0) {
			// Index of last element in arr1
			arr2[0] = arr1.length-1;
		}
		// iterated over arr2 to have sub-arrays in arr3, containing ids of pies in continuous                                                                                                                         
		// sequence, having no free space relaxation in between with their next neighbours.
		for (var i:Number = 0; i<=arr2.length; ++i) {
			var startId:Number, endId:Number;
			// blank sub-array created
			arr3[i] = new Array();
			// for first entry, there is no previous entry, hence k is set to zero
			if (i == 0) {
				// starting id
				startId = 0;
				// ending id
				endId = arr2[i];
				// for entries other than the first one
			} else {
				// starting id
				startId = arr2[i-1]+1;
				// to get ending id, checking for if the entry of arr2 under loop is the last one, in which
				// case, k is set to the index of the last pie in arr1 else the current pie id under loop
				endId = (i == arr2.length) ? arr1.length-1 : arr2[i];
			}
			// looping to get the sequencial entries in the sub-arrays
			for (var j:Number = startId; j<=endId; ++j) {
				arr3[i].push(j);
			}
		}
		// setting upper and lower limits (not vertically) for setting label positions
		// for lower quadrants
		if (quadrantId == 3 || quadrantId == 4) {
			// upper limit is the lower extreme of the reference ellipse for labels
			var limit_1:Number = yCenter-b2;
			// for upper quadrants
		} else {
			// upper limit is the upper extreme of the reference ellipse for labels
			var limit_1:Number = yCenter+b2;
		}
		// lower limit is ordinate of chart center, same for all quadrants
		var limit_2:Number = yCenter;
		// to calculate the ordinates of the labels and updating the same in arr1
		for (var i:Number = 0; i<arr3.length; ++i) {
			// the number of labels in this overlapping aggregate
			var numOfLabels:Number = arr3[i].length;
			// if equals to one, then no overlap actually and ordinate in arr1 remains unchanged
			// but we need to set labels for overlapping issue only
			if (numOfLabels>1) {
				// To find the average position of the labels in the aggregate:
				// id of the initial label in the aggregate
				var t1:Number = arr3[i][0];
				// id of the final label in the aggregate
				var t2:Number = arr3[i][numOfLabels-1];
				// we are trying to find out average position and not average ordinate of the labels
				// so, we are adding up the two ordinates and subtracting a height of the label to get the numerator of the formula
				var addUp:Number = arr1[t1][0]+arr1[t2][0]-h;
				// formula to find the average position of the labels
				var meanH:Number = toNT(addUp/2);
				// the labels in this aggregate will have be placed to occupy minimum space vertically
				// so they need to be arranged one after another - touching
				var totalHeight:Number = numOfLabels*h;
				// displacement is the height by which the the extremities of the aggregate will deviate from the meanH value(normally)
				var displacement:Number = totalHeight/2;
				// to find the ordinate of first label in the aggregate
				// for upper quadrants
				if (quadrantId == 1 || quadrantId == 2) {
					var sign:Number = -1;
					var startY:Number = Math.min(limit_1, meanH+displacement);
					if (limit_2>meanH-displacement+h) {
						//  minus h ---- since , for first quadrant ... all txt fields are shifted upwards by h in instances of pie3d
						startY = limit_2+totalHeight-h;
					}
				} else {
					var sign:Number = +1;
					var startY:Number = Math.max(limit_1, meanH-displacement+h);
					if (limit_2<meanH+displacement) {
						// part of the labels outside calculated along y-axis
						var delY:Number = meanH+displacement-limit_2;
						// adjusted by shifting down
						startY -= delY;
					}
				}
				// updating ordinates in arr1
				for (var j:Number = 0; j<numOfLabels; ++j) {
					var t:Number = arr3[i][j];
					// arranging labels one after another - touching
					arr1[t][0] = startY+sign*h*j;
				}
			}
		}
		// To have the multipicating factor of +1 or -1 depending on the quadrant, thus required to calculate 
		// the abscissa of labels. This is the step of actually choosing the sign of the square root in the formula
		// for abscissa obtained from the general formula of ellipse.
		// for left quadrants
		if (quadrantId == 2 || quadrantId == 3) {
			var sign:Number = -1;
			// for right quadrants
		} else {
			var sign:Number = +1;
		}
		// updated value of ordinates of the labels are used to calculate the abscissa of the labels and hence the coordinates storing/updating in arrFinal
		for (var j:Number = 0; j<arr1.length; ++j) {
			var id:Number = arr1[j][1];
			var curveY:Number = arr1[j][0];
			var curveX:Number = toNT(sign*a2*Math.sqrt(toNT(1-((curveY-yCenter)/b2)*((curveY-yCenter)/b2)))+this.config.centerX);
			
			if(isNaN(curveX)){
				curveX = 0;
			}
			
			var xTxt:Number = curveX;
			var yTxt:Number = curveY;
			//
			this.config.arrFinal[id]['labelProps'] = [xTxt, yTxt, quadrantId];
			
			//another special type property to define the type of labels
			//always push as the 3rd index could also be used for icon
			this.config.arrFinal[id]['labelProps'].push("type3");
		}
		// updating recurring counter for having an upper limit of recursive calls to avoid system getting hanged (as the case maybe)
		if (recurTimes == undefined) {
			recurTimes = 0;
		} else {
			recurTimes++;
		}
		// recursive call ... if overlap exists and recurring counter is within limit
		if (isOverlap && recurTimes<128) {
			arrangeLabels(arrX, b2, h, quadrantId, true, recurTimes);
		} else {
			
			var labelsObj:Array = new Array();
			var mainArr:Array = this.config.arrFinal;
			var numLoop:Number = arr1.length;
			
			for (var j:Number = 0; j < numLoop; ++j) {
				var id:Number = arr1[j][1];
				var itemObj:Object = new Object();
				itemObj.objIndx = id;
				itemObj.sortFactor = mainArr[id]['labelProps'][1];
				labelsObj.push(itemObj);
			}
			
			//sort all the arrays on the y position values
			labelsObj.sortOn("sortFactor", Array.NUMERIC|Array.DESCENDING);
			
			//calculate and assign wordSpace limit on each labels according to labelsObj
			if(labelsObj.length > 0){
				var limitY:Number = (quadrantId == 3 || quadrantId == 4)? this.plotY + this.plotHeight : this.plotY + this.plotHeight/2;
				calculateWrapSpace(labelsObj, limitY);
			}
			
			//sort all items for easy assigning back to the main array
			labelsObj.sortOn("objIndx", Array.NUMERIC);
			
			//re assign the objects in the main array
			for(var aq:Number = 0; aq < labelsObj.length; aq++){
				var indx:Number = labelsObj[aq].objIndx;
				var mainObj:Object = mainArr[indx];
				
				mainObj['labelProps']['wrapSpace'] = labelsObj[aq].wrapSpace;
				mainObj['labelProps']['maxYlimit'] = labelsObj[aq].maxLimit;
			}
		}
	}
	
	/**
	 * calculateWrapSpace method calculates vertical space available for labels
	 * to wrap.
	 * @param	quadrantArray	Array with objects for labels
	 * @param	maxYlimit		vertically lower limit of the quadrant
	 */
	private function calculateWrapSpace(quadrantArray:Array, maxYlimit:Number):Void{
		var num:Number = quadrantArray.length;
		for(var i:Number = 0; i < num; i++){
			//for the lowest label of this quadrant ...assign the quadrant limit as 
			//its word wrap space
			if(i == num - 1){
				var itmObj:Object = quadrantArray[i];
				itmObj.wrapSpace = Math.abs(maxYlimit-itmObj.sortFactor);
				itmObj.maxLimit = maxYlimit;
			}else{
				var itmObj:Object = quadrantArray[i];
				var nxtObj:Object = quadrantArray[(i+1)];
				itmObj.wrapSpace =  Math.abs(nxtObj.sortFactor - itmObj.sortFactor)-1;
				itmObj.maxLimit = 0;
			}
		}
	}
	
	
	/**
	 * setOverLoadedLabelsEqually is the method to calculate
	 * for the label arrangements equally (vertically) spaced,
	 * when the number of labels in the quadrant is greater 
	 * than the maximum number of permissible labels in any 
	 * quadrant with no overlap.
	 * @param	arrX			A sorted array of objects with properties
	 *							like mean angle,sweep angle and an intezer id
	 *							denoting the original of the pie in arrFinal.
	 * @param	b				A number denoting the length of semi-minor
	 *							axis of the reference ellipse for drawing smart
	 *							labels.
	 * @param	h				A number denoting the height of the text
	 *							fields w.r.t. the font size of labels.
	 * @param	quadrantId		A number indicating the quadrant
	 *							processing. Values can be - 1,2,3,4 only
	 */
	private function setOverLoadedLabelsEqually(arrX:Array, b:Number, h:Number, quadrantId:Number):Void {
		// reversing the array
		arrX.reverse();
		// setting the signs
		var signX:Number = (quadrantId == 1 || quadrantId == 4) ? +1 : -1;
		var signY:Number = (quadrantId == 3 || quadrantId == 4) ? -1 : +1;
		// getting the relaxation value for the labels from the pies
		var extension:Number = this.config.relaxation;
		// lengths of semi major and minor axes of the main pie ellipse
		var a1:Number = this.config.radius;
		var b1:Number = this.config.radius*this.config.pieYScale;
		// lengths of semi major and minor axes of the reference label ellipse
		var a2:Number = this.config.radius+extension;
		var b2:Number = b;
		// ordinate of the piechart center
		var yCenter:Number = this.config.centerY+this.config.pieSliceDepth/2;
		// the vertical displacement between the successive labels
		var H:Number = toNT(b2/(arrX.length-1));
		//
		// to take up a pie one at a time
		for (var i:Number = 0; i<arrX.length; ++i) {
			// ordinate of label
			var curveY:Number = toNT(yCenter+signY*i*H);
			// abscissa of label
			var B:Number = Math.pow((curveY-yCenter)/b2, 2);
			var A:Number = Math.sqrt((1-B));
			if (isNaN(A)) {
				A = 0;
			}
			var curveX:Number = toNT(signX*a2*A+this.config.centerX);
			var xTxt:Number = curveX;
			var yTxt:Number = curveY;
			// storing values calculated in array
			this.config.arrFinal[arrX[i].id]['labelProps'] = [xTxt, yTxt, quadrantId];
			
			// another speacial attribute of label type - here set to type 2
			// type 2 means lebels are already over crowded in the quadrant. And in case of overlapping
			// will truncate and add ellipses in the text field.No wordwrap
			//always push .. as the 3rd index could also be used
			this.config.arrFinal[arrX[i].id]['labelProps'].push("type2");
		}
	}
	/**
	 * arrangeMaxLabels is the method to calculate for the 
	 * label arrangements equally (vertically) spaced with no
	 * overlap, when the number of labels in the quadrant is 
	 * equal to the maximum number of permissible labels 
	 * in any quadrant with no overlap.
	 * @param	arrX			A sorted array of objects with properties
	 *							like mean angle,sweep angle and an intezer id
	 *							denoting the original of the pie in arrFinal.
	 * @param	b				A number denoting the length of semi-minor
	 *							axis of the reference ellipse for drawing smart
	 *							labels.
	 * @param	h				A number denoting the height of the text
	 *							fields w.r.t. the font size of labels.
	 * @param	quadrantId		A number indicating the quadrant
	 *							processing. Values can be - 1,2,3,4 only
	 */
	private function arrangeMaxLabels(arrX:Array, b:Number, h:Number, quadrantId:Number):Void {
		// reversing the array
		arrX.reverse();
		// setting the signs
		var signX:Number = (quadrantId == 1 || quadrantId == 4) ? +1 : -1;
		var signY:Number = (quadrantId == 3 || quadrantId == 4) ? -1 : +1;
		// getting the relaxation value for the labels from the pies
		var extension:Number = this.config.relaxation;
		// lengths of semi major and minor axes of the main pie ellipse
		var a1:Number = this.config.radius;
		var b1:Number = this.config.radius*this.config.pieYScale;
		// lengths of semi major and minor axes of the reference label ellipse
		var a2:Number = this.config.radius+extension;
		var b2:Number = b;
		// ordinate of the piechart center
		var yCenter:Number = this.config.centerY+this.config.pieSliceDepth/2;
		// to take up a pie one at a time
		for (var i:Number = 0; i<arrX.length; ++i) {
			// ordinate of label
			var curveY:Number = toNT(yCenter+signY*i*h);
			// abscissa of label
			var curveX:Number = toNT(signX*a2*Math.sqrt((1-((curveY-yCenter)/b2)*((curveY-yCenter)/b2)))+this.config.centerX);
			// the extreme smart label often have y beyond reference ellipse
			if (isNaN(curveX)) {
				// vertical extreme of ellipse means horizontal center of the same
				curveX = this.config.centerX;
			}
			var xTxt:Number = curveX;
			var yTxt:Number = curveY;
			// storing values calculated in array
			this.config.arrFinal[arrX[i].id]['labelProps'] = [xTxt, yTxt, quadrantId];
			
			// another speacial attribute of label type - here set to type 2
			// type 2 means levels are already over crowded in the quadrant.And in case of overlapping
			// we will truncate and add elipses in the text field.No wordwrap
			// always push as the third index could also be used for icon
			this.config.arrFinal[arrX[i].id]['labelProps'].push("type2");
		}
	}
	/**
	 * checkBounds is the method called repeatedly to check
	 * for maximum permissible radius (when unspecified) with
	 * the chart visible in totality.
	 * @param	isGetting2DRadius	Boolean to denote if this call
	 *								is to get the best fit 2D or 3D radius
	 */
	private function checkBounds(isGetting2DRadius:Boolean):Void {
		// to hold the returned object
		var objMetrics:Object = mcPieH.getBounds(mcPieH);
		// checking if the aggregate of pies is contained in the plotWidth and plotHeight
		if (objMetrics.xMin>0 && objMetrics.xMax<plotWidth && this.config.radius<0.4*plotWidth && this.config.radius*this.config.pieYScale<0.4*plotHeight) {
			// radius is incremented by 10%
			this.config.radius *= 1.1;
			this.config.radius = Math.round(this.config.radius);
			// set temporarily as a working value
			this.config.doughnutRadius = this.config.radius*0.5;
			// if the aggregate of pies is not contained in the plotWidth and plotHeight
		} else {
			//to avoid decreasing radius below the initial value (20% of the smaller of plotWidth and plotHeight)
			if ((this.config.radius3D<this.config.radius && !isGetting2DRadius) || (this.config.radius2D<this.config.radius && isGetting2DRadius)) {
				// hence the last radius set is just exceeding the limit and hence decremented down by the same ratio to get the best fit radius
				this.config.radius /= 1.1;
				this.config.radius = Math.round(this.config.radius);
			}
			// flag updated to indicate that radius is set                       
			this.config.isRadiusGiven = true;
			// flag updated to indicate that the chart will need initial animation ... was kept true to avoid wastage of time due animation during setting of radius
			// if initial animation is required
			if (!isGetting2DRadius) {
				if (this.params.animation) {
					this.config.isInitialised = false;
					// else, behave as if initial animation part is over
				} else {
					this.config.isInitialised = true;
				}
				// if doughnutRadius is properly defined in params
				if (this.params.doughnutRadius != 0 && !(isNaN(this.params.doughnutRadius))) {
					// if outer radius is greater than the inner radius (checking from params for original value)
					if (this.config.radius>this.params.doughnutRadius) {
						this.config.doughnutRadius = this.params.doughnutRadius;
					} else {
						this.config.doughnutRadius = this.config.radius*0.5;
					}
					// else if doughnutRadius is not properly defined in params
				} else {
					this.config.doughnutRadius = this.config.radius*0.5;
				}
				isGetting2DRadius = false;
			}
		}
		// all other calculations being ready, positions for labels for this new radius is the only requisite before final call for draw
		// if labels are at all to be displayed
		if (this.params.showNames || this.params.showValues) {
			if (this.params.enableSmartLabels) {
				// set label positions smartly
				setSmartLabels();
			} else {
				// set label positions as it is without any overlap management
				setLabels();
			}
		}
		// final call to draw the chart for subsequent check only                                           
		drawChart(isGetting2DRadius);
	}
	/**
	 * setNeighbors method is called from drawChart method.
	 * It sets the neighbours of each pie in xy - plane, to
	 * be stored in respective pie movieclip, for use during
	 * animation.
	 */
	private function setNeighbors():Void {
		var mc:MovieClip;
		// a temporary array is created
		var e:Array = new Array();
		// the array is populated with an object for each pie
		for (var g in mcPieH) {
			// if instance of movieclip ... its a pie
			if (mcPieH[g] instanceof MovieClip) {
				// storing reference of the movieclip
				mc = mcPieH[g];
				// populating the array with an object for this pie with properties for sorting 
				// in anti-clockwise order to find nearest neighbours:
				// mcRef - reference of the pie movieclip
				// id - index in anti-clockwise order of arrangement of pies, as in this.data
				// twinId - exists only for conjugated pie pair; denotes their order in anti-clockwise sense
				e[mc.getDepth()] = {mcRef:mc, id:mc.store['id'], twinId:mc.store['twinId']};
			}
		}
		// Sorting the array elements in order of their ordering in xy-plane.
		// "twinId" : left one at 270 degree junction, w.r.t. anti-clockwise sense, is indexed by 0, while the right one by 1
		// "id" can be same only in the case of conjugation, wherein "twinId" comes in use
		e.sortOn(['id', 'twinId'], [16, 16]);
		// final allocation of neighbours to each pie
		for (var v:Number = 0; v<e.length; ++v) {
			// reference of current pie movieclip in loop
			mc = e[v].mcRef;
			// storing index of preceding neighbour in anti-clockwise sense
			var m1:Number = (v == 0) ? e.length-1 : v-1;
			// storing index of succeeding neighbour in anti-clockwise sense
			var m2:Number = (v == e.length-1) ? 0 : v+1;
			// storing movieclip reference of preceding neighbour in anti-clockwise sense
			mc.prevPieRef = e[m1].mcRef;
			// storing movieclip reference of succeeding neighbour in anti-clockwise sense
			mc.nextPieRef = e[m2].mcRef;
		}
		// extra care is not taken for conjugated pie case ... its the duty of movePie and movePieCallback
		// methods of Doughnut3D class instances.
	}
	/**
	 * getDragAngle is the method to calculate and return the
	 * the current angle of the mouse cursor w.r.t. the chart
	 * center. The angle is calculated in perspective sense.
	 * @param	m	number indicating the _root._xmouse
	 * @param	n	number indicating the _root._ymouse
	 * @returns		number denoting eccentric angle
	 */
	public function getDragAngle(m:Number, n:Number):Number {
		// length of semi-major axis of the pie ellipse
		var a:Number = this.config.radius;
		// length of semi-minor axis of the pie ellipse
		var b:Number = a*this.config.pieYScale;
		// 
		var x1:Number = m;
		var y1:Number = n;
		// abscissa of the chart center w.r.t. _root
		var x0:Number = this.x+mcPieH._x+this.config.centerX;
		// ordinate of the chart center w.r.t. _root
		var y0:Number = this.y+mcPieH._y+this.config.centerY;
		// differences
		var dx:Number = x1-x0;
		var dy:Number = y1-y0;
		// angle formed between the mouse cursor and chart center
		var ellipticAngle:Number = Math.atan2(dy, dx);
		// adjustment for Math.atan2 which returns angle between  -90 to 90 degreees (obviously in radians) only
		var addAngle:Number = (dx<0) ? Math.PI : 0;
		// formula applied
		var eccentricAngle:Number = 360-MathExt.boundAngle(MathExt.toDegrees(Math.atan((a/b)*Math.tan(ellipticAngle))+addAngle));
		//
		return eccentricAngle;
	}
	// -------------- VISUAL RENDERING METHODS ---------------------//
	/**
	 * setInitialStatus is the method to set initial status
	 * of the pies, viz. slicing status, label position,
	 * smartlines and inner/cut face visibilities, during
	 * redraw. Actually works for the pies sliced out only.
	 */
	private function setInitialStatus():Void {
		// Let there be an ellipse with lengths of semi major and minor axes be (radius+slicingDistance) and squeeze*(radius+slicingDistance)
		// Then the difference in length between semi-major axes of the above defined ellipse and our pie ellipse is 'sa' 
		var sa:Number = this.params.slicingDistance;
		// And the difference in length between semi-minor axes of the above defined ellipse and our pie ellipse is 'sb' 
		var sb:Number = sa*this.config.pieYScale;
		// iterating on main piechart holder movieclip
		for (var g in mcPieH) {
			// if instance of movieclip ,its a pie; works for the pies sliced out only (store['isSliced'] is true)
			if (mcPieH[g] instanceof MovieClip && mcPieH[g].store['isSliced']) {
				// storing reference of the movieclip
				var _mc:MovieClip = mcPieH[g];
				// mean angle stored in radians
				var meanAng:Number = MathExt.toRadians(_mc.store['meanAngle']);
				// the difference in abscissae for the same eccentric angle (meanAng) for the two ellipses defined above
				var sx:Number = toNT(sa*Math.cos(meanAng));
				// the difference in ordinates for the same eccentric angle (meanAng) for the two ellipses defined above
				var sy:Number = toNT(sb*Math.sin(meanAng));
				// set position of the sliced pie movieclip
				// if this is not a redraw to generate chart of the same view, ie. to have a different look due to 
				// different starting angle
				if (!this.config.isStaticRecreation) {
					// positioned by calculated values
					_mc._x += sx;
					_mc._y -= sy;
					// else, if its redrawn to have the same look (ie. same starting angle)
				} else {
					// positioned by previously stored values (fine tuning measure to avoid a jerk)
					// store['position'] - stored due and by the call of loadCurrentSlicingStatus() on selection of
					// 'Enable Rotation' or 'Enable Links' from the context menu
					_mc._x = _mc.store['position'][0];
					_mc._y = _mc.store['position'][1];
				}
				// now, if smart labelling is enabled, then set positions of labels and draw the connecting smartlines
				if (this.params.enableSmartLabels) {
					// repositioning the pie labels
					_mc.mcLabel.label_txt._y -= sy;
					// clearing all drawings before redraw of smartlines
					_mc.mcLabel.clear();
					// value set in local variables to be used in drawing smartlines and updating an array keeping 
					// track of the the vital points of smartlines
					// starting abscissa
					var x1:Number = _mc.arrLinePoints[0];
					// starting ordinate
					var y1:Number = _mc.arrLinePoints[1];
					// vertex abscissa
					var x2:Number = _mc.arrLinePoints[2];
					// ending abscissa
					var x3:Number = _mc.arrLinePoints[3];
					// vertex and ending ordinate (same value)
					var y3:Number = _mc.arrLinePoints[4]-sy;
					_mc.mcLabel.lineStyle(this.params.smartLineThickness, parseInt(this.params.smartLineColor, 16), this.params.smartLineAlpha);
					_mc.mcLabel.moveTo(x1, y1);
					_mc.mcLabel.lineTo(x2, y3);
					_mc.mcLabel.lineTo(x3, y3);
					// array updated with current values
					_mc.arrLinePoints = [x1, y1, x2, x3, y3];
				}
				// inner/cut faces' visibility controlled                                                                                                    
				Doughnut3D.cutFaceVisibilityToggler(_mc);
			}
		}
	}
	/**
	 * setInitialWatch is the method called to set a watchman 
	 * to monitor the overall initial animating process 
	 * sequencially.
	 */
	private function setInitialWatch():Void {
		// store the reference of this Doughnut3DChart instance
		var insRef:Doughnut3DChart = this;
		// to track if all the pies have completed their initial slicing movements, so that the next phase of
		// line drawing animations (if smartLabel is enabled) can begin or else the text appearance animations
		this.objIntervalIds.id = setInterval(function () {
			// checking over the updated flag 'iniTracker' against 'numSlicedPies', to know if the initial phase of 
			// pie slicing movements is over
			if (insRef.config.numSlicedPies<=insRef.config.iniTracker) {
				var index:Number = 0;
				// iterating over the elements in main piechart holder movieclip
				for (var g in insRef.mcPieH) {
					// if its a movieclip, then its a pie; smartline and hence text animation is required for only
					// one of the conjugated pair ('isConjugated' is set 'true' for one and 'null' for the other)
					if (insRef.mcPieH[g] instanceof MovieClip && !insRef.mcPieH[g].store['isConjugated']) {
						index++;
						// generating a unique string, to be stored as a property-name within an object storing all similar
						// returned setInterval ids
						var idx:String = 'id'+g;
						// assigning returned id in unique object property; animateIni method is referenced to handle smartline (if 
						// smartLabel is enabled) and text animation
						insRef.objIntervalIds[idx] = setInterval(Delegate.create(insRef, insRef.animateIni), 10, insRef.mcPieH[g], idx, index);
					}
				}
				// this part of watching is over and action taken
				clearInterval(insRef.objIntervalIds.id);
			}
		}, 20);
	}
	/**
	* drawHeaders method renders the following on the chart:
	* CAPTION, SUBCAPTION
	*/
	private function drawHeaders():Void {
		//Sub-caption start y positio
		var subCaptionY:Number = this.params.chartTopMargin;
		//Render caption
		if (this.params.caption != "")
		{
			var captionStyleObj : Object = this.styleM.getTextStyle (this.objects.CAPTION);
			captionStyleObj.vAlign = "bottom";
			//Switch the alignment to lower case
			captionStyleObj.align = captionStyleObj.align.toLowerCase();
			//Now, based on alignment, decide the xPosition of the caption
			var xPos:Number = (captionStyleObj.align=="center")?(this.x + (this.width / 2)):((captionStyleObj.align=="left")?(this.x + this.params.chartLeftMargin):(this.width - this.params.chartRightMargin));
			var captionObj : Object = createText (false, this.params.caption, this.cMC, this.dm.getDepth ("CAPTION") , xPos , this.params.chartTopMargin, 0, captionStyleObj, true, this.elements.caption.w, this.elements.caption.h);
			//Add for sub-caption y position
			subCaptionY = subCaptionY + captionObj.height;
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
			subCaptionStyleObj.vAlign = "bottom";
			//Switch the alignment to lower case
			subCaptionStyleObj.align = subCaptionStyleObj.align.toLowerCase();
			//Now, based on alignment, decide the xPosition of the caption
			var xPos:Number = (subCaptionStyleObj.align=="center")?(this.x + (this.width / 2)):((subCaptionStyleObj.align=="left")?(this.x + this.params.chartLeftMargin):(this.width - this.params.chartRightMargin));
			var subCaptionObj : Object = createText (false, this.params.subCaption, this.cMC, this.dm.getDepth ("SUBCAPTION") , xPos , subCaptionY, 0, subCaptionStyleObj, true, this.elements.subCaption.w, this.elements.subCaption.h);
			
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
		//Clear interval                                                                                                    
		clearInterval(this.config.intervals.headers);
	}
	/**
	 * recreate method is called to redraw the whole chart,
	 * after erasing the existing one, with a different
	 * look characterised by a change in starting angle.
	 * @param	delAngle						Number denoting the change in angle
	 *											(in degrees) by which the chart will
	 *											be rotated in effect by redrawing.
	 * @param	isStaticRecreationForRotation	Boolean denoting 
	 *											whether the chart is redrawn with delAngle = 0,
	 *											happens when rotation is selected from context
	 *											menu. Slicing status need to be updated for
	 *											proper rotational views.
	 */
	public function recreate(delAngle:Number, isStaticRecreationForRotation:Boolean):Void {
		// flag updated to be used in setInitialStatus()
		this.config.isStaticRecreation = isStaticRecreationForRotation;
		// starting angle updated w.r.t. the current dragging status of the mouse
		this.config.startingAngle += delAngle;
		//rounding up to 2 decimal places
		this.config.startingAngle = MathExt.roundUp(this.config.startingAngle);
		// flag updated to avoid initial animation
		this.config.isInitialised = true;
		// there are elements to be cleaned for proper next redraw
		cleanUp();
		// individual pie properties need to be recalculated for redraw
		calculatePieProps();
		// to generate the chart
		drawChart();
	}
	/** 
	 * draw method draws the pie chart by calling various other 
	 * methods of this class.
	 * @param	isRedraw	Whether the method is called during the re-draw of chart
	 *						during dynamic resize.
	 */
	private function draw(isRedraw:Boolean):Void {
		
		//if method called to resize
		if(isRedraw){
			//flag updated to be used in setInitialStatus()
			this.config.isStaticRecreation = false;
			// flag updated to avoid initial animation
			this.config.isInitialised = true;
			// there are elements to be cleaned for proper next redraw
			cleanUp();
			// individual pie properties need to be recalculated for redraw
			calculatePieProps();
			//remove the main movieclip
			mcPieH.removeMovieClip();
		}
		
		// Create movie clip to hold pies
		mcPieH = this.cMC.createEmptyMovieClip("PieHolder", this.dm.getDepth('DATAPLOT'));
		
		//Set it's X and Y
		mcPieH._x = this.plotX;
		mcPieH._y = this.plotY;
		//If there's only 1 pie, it takes a different course
		if (this.num == 1) {
			// no processing of single data set is required 
			// totalSlices set to zero for preventing mouse interaction of the pie
			this.config.totalSlices = 0;
			// to have no plot animation sequence
			this.config.isPlotAnimationOver = true;
			// to avoid rotational interactivity
			this.config.enableRotation = false;
		}
		
		// To find out the best fit 2D radius for the chart, few original values are stored back to be replaced                                                                     
		// after this process is over
		var isRadiusGiven:Boolean = this.config.isRadiusGiven;
		var pieYScale:Number = this.config.pieYScale;
		var radius3D:Number = this.config.radius;
		var radiusDough3D:Number = this.config.doughnutRadius;
		var pieThickness:Number = this.config.pieSliceDepth;
		var isInitialised:Boolean = this.config.isInitialised;
		
		// These config properties are set with required values to get the best fit 2D radius
		this.config.isRadiusGiven = false;
		this.config.pieYScale = 1;
		
		this.config.radius = 0.2*Math.min(plotWidth, plotHeight);
		this.config.doughnutRadius = 0.5*this.config.radius;
		
		this.config.pieSliceDepth = 0;
		this.config.isInitialised = true;
		// coordinates of the chart center is reevaluated (due to change in pieSliceDepth, though temporarily)
		this.setChartCenter();
		//set for checking in checkbounds
		this.config.radius2D = this.config.radius;
		// call to calculate the best fit 2D radius (parameter set to true to indicate the process)
		//where is the cleanup (multiple redrawing!)?
		drawChart(true);
		//thus found best fit 2D radius is set temporarily
		this.config.radius2D = this.config.radius;
		// Now the process of generating the initial 3D pieChart proceeds.
		
		if(this.params.pieRadius != 0 && !isNaN(this.params.pieRadius) ){
			this.config.radius2D = (this.config.radius2D < this.params.pieRadius )? this.config.radius2D : this.params.pieRadius;
		}
		
		//-------------------------------------------//
		
		//for the case of chart resizing in 2D mode, we need to evaluate the 3D radius and call to generate the chart in 2D mode.
		if(pieYScale != this.params.pieYScale/100){
			
			if(this.params.pieRadius == 0 || isNaN(this.params.pieRadius) ){
				
				
				// The config properties are assigned with values to evaluate the 3D radius.
				this.config.isRadiusGiven = isRadiusGiven;
				this.config.pieYScale = this.params.pieYScale/100;
				this.config.radius = radius3D;
				this.config.doughnutRadius = radiusDough3D;
				this.config.pieSliceDepth = this.params.pieSliceDepth;
				this.config.isInitialised = isInitialised;
				
				// coordinates of the chart center is reevaluated (due to change in pieSliceDepth)
				this.setChartCenter();
				// label coordinates (smart or not) is reevaluated for 3D radius
				if (this.params.showNames || this.params.showValues) {
					if (this.params.enableSmartLabels) {
						// set label positions smartly
						setSmartLabels();
					} else {
						// set label positions as it is without any overlap management
						setLabels();
					}
				}
				//set for checking in checkbounds (if applicable)                                                                                               
				this.config.radius3D = this.config.radius;
				// final call to draw chart from this class instance (parameter set to false to indicate that this is a call for original drawing of chart)                                         
				
				drawChart(false);
				// value of 3D radius (either specified or calculated) is stored in config
				this.config.radius3D = this.config.radius;
				
				
				// final evaluation of 2D radius ... 
				// 1. if radius2D is greater than or equal to radius3D, then radius3D is used in 2D display
				// 2. else if, radius2D is less than radius3D, then radius2D is used in 2D display
				this.config.radius2D = (this.config.radius2D<this.config.radius3D) ? this.config.radius2D : this.config.radius3D;
				this.config.radiusDough3D = this.config.doughnutRadius;
				this.config.radiusDough2D = (this.config.radiusDough3D/this.config.radius3D)*this.config.radius2D;
				
			}else{
				
				this.config.radius = this.params.pieRadius;
				this.config.radius3D = this.config.radius;
				
				this.config.radiusDough3D = radiusDough3D
				this.config.radiusDough2D = (this.config.radiusDough3D/this.config.radius3D)*this.config.radius2D;
				
			}
		
			//-----------------//
			
			// The config properties are restored back with original values
			//true instead of isRadiusGiven! to optimize.
			this.config.isRadiusGiven = true
			this.config.pieYScale = pieYScale;
			//this.config.radius2D instead of radius3D! to optimize.
			this.config.radius = this.config.radius2D;
			
			this.config.doughnutRadius = this.config.radiusDough2D;
			this.config.pieSliceDepth = pieThickness;
			this.config.isInitialised = isInitialised;
			// coordinates of the chart center is reevaluated (due to change in pieSliceDepth)
			this.setChartCenter();
			// label coordinates (smart or not) is reevaluated for 3D radius
			if (this.params.showNames || this.params.showValues) {
				if (this.params.enableSmartLabels) {
					// set label positions smartly
					setSmartLabels();
				} else {
					// set label positions as it is without any overlap management
					setLabels();
				}
			}
			
			drawChart(false);
		}else{
			// The config properties are restored back with original values
			this.config.isRadiusGiven = isRadiusGiven;
			this.config.pieYScale = pieYScale;
			this.config.radius = radius3D;
			this.config.doughnutRadius = radiusDough3D;
			this.config.pieSliceDepth = pieThickness;
			this.config.isInitialised = isInitialised;
			// coordinates of the chart center is reevaluated (due to change in pieSliceDepth)
			this.setChartCenter();
			// label coordinates (smart or not) is reevaluated for 3D radius
			if (this.params.showNames || this.params.showValues) {
				if (this.params.enableSmartLabels) {
					// set label positions smartly
					setSmartLabels();
				} else {
					// set label positions as it is without any overlap management
					setLabels();
				}
			}
			//set for checking in checkbounds (if applicable)                                     
			this.config.radius3D = this.config.radius;
			// final call to draw chart from this class instance (parameter set to false to indicate that this is a call for original drawing of chart)                                                        
			//
			drawChart(false);
			// value of 3D radius (either specified or calculated) is stored in config
			this.config.radius3D = this.config.radius;
			
			// final evaluation of 2D radius ... 
			// 1. if radius2D is greater than or equal to radius3D, then radius3D is used in 2D display
			// 2. else if, radius2D is less than radius3D, then radius2D is used in 2D display
			this.config.radius2D = (this.config.radius2D<this.config.radius3D) ? this.config.radius2D : this.config.radius3D;
			this.config.radiusDough3D = this.config.doughnutRadius;
			this.config.radiusDough2D = (this.config.radiusDough3D/this.config.radius3D)*this.config.radius2D;
		}
		
		//if not resizing, means very first call during chart creation
		if(!isRedraw){
			// if not singleton
			if (this.config.totalSlices>1) {
				// call function to set watch on chart initialsation end; handles all cases except for singleton
				this.setInitWatch();
			}
			//Clear sequence interval  
			clearInterval(this.config.intervals.plot);
		}
	}
	/**
	* drawLegend method renders the legend
	*/
	private function drawLegend () : Void
	{
		if (this.params.showLegend)
		{
			this.lgnd.render ();
			//Apply filter
			this.styleM.applyFilters (lgndMC, this.objects.LEGEND);
			//Apply animation
			if (this.params.animation)
			{
				this.styleM.applyAnimation (lgndMC, this.objects.LEGEND, this.macro, null, 0, null, 0, 100, null, null, null);
			}
			//If it's interactive legend, listen to events
			if (this.params.interactiveLegend){
				this.lgnd.addEventListener("legendClick",this);
			}
		}
		//Clear interval
		clearInterval (this.config.intervals.legend);
	}
	/**
	 * drawChart method is called from sortZ method or 
	 * parseXml method. It creates  instances of Doughnut3D class,
	 * each for a sub-array in the multidimensional array 
	 * storage obtained after sortZ() is done. Each instance
	 * is passed a (common) object with a host of properties 
	 * in them, the movieclip reference in which to draw the 
	 * pie and the unique z-level for proper 3d presentation 
	 * of the pie set, mutually.
	 * @param	isGetting2DRadius	to denote if this call is to
	 *								get the best fit 2D or 3D radius
	 */
	private function drawChart(isGetting2DRadius:Boolean):Void {
		
		// all properties independent of data set are stored in an object instance to be passed as parameter
		var objProps:Object = {squeeze:this.config.pieYScale, radius:this.config.radius, innerRadius:this.config.doughnutRadius, pieThickness:this.config.pieSliceDepth, centerX:this.config.centerX, centerY:this.config.centerY, bottomAlpha:this.params.pieBottomAlpha, topAlpha:this.params.pieFillAlpha, borderAlpha:this.params.pieBorderAlpha, curveFaceAlpha:this.params.pieOuterFaceAlpha, cutFaceAlpha:this.params.pieInnerFaceAlpha, totalSlices:this.config.totalSlices, movement:this.params.slicingDistance, chartHeight:plotHeight, isRadius:this.config.isRadiusGiven, borderThickness:this.params.pieBorderThickness, smartLineColor:parseInt(this.params.smartLineColor, 16), smartLineThickness:this.params.smartLineThickness, smartLineAlpha:this.params.smartLineAlpha, useLighting:this.params.useLighting};
		objProps.objLabelProps = this.styleM.getTextStyle(this.objects.DATALABELS);
		objProps.isPlotAnimationOver = this.config.isPlotAnimationOver;
		// checking whether the data set is singleton or not
		if (this.config.totalSlices>1) {
			// store the pie independent properties in the objProps
			objProps.isSmartLabels = this.params.enableSmartLabels;
			objProps.isSmartLabelSlanted = this.params.isSmartLineSlanted;
			objProps.isRotatable = this.config.enableRotation;
			objProps.isLinkable = this.config.enableLinks;
			objProps.isInitialised = this.config.isInitialised;
			objProps.manageLabelOverflow = this.params.manageLabelOverflow;
			objProps.useEllipsesWhenOverflow = this.params.useEllipsesWhenOverflow;
			//
			// if not a single case ... loops to store the pie dependent properties in the objProps
			// and call to instantiate the Doughnut3D class for creation of the specific pie slice
			for (var i:Number = 0; i<this.config.totalSlices; ++i) {
				// store the pie dependent properties as an array in the objProps
				objProps.arrFinal = this.config.arrFinal[i];
				// unique name of moviclip for this pie slice
				var strMcName:String = 'mcPie_'+i;
				// new movieclip created and reference stored in private instance property
				var mcMain:MovieClip = mcPieH.createEmptyMovieClip(strMcName, i);
				//
				// setting unique variable name to assign the Doughnut3D instance
				var strName:String = 'pie'+i;
				
				// storing the index of the pie data in this.data within the movieclip
				mcMain.dataId = objProps.arrFinal.id;
				
				// storing the string id by which the Doughnut3d instance is stored in config.objDoughnut3D, mapped to the index of the pie data in this.data
				this.map[objProps.arrFinal.id] = strName;
				
				// call to instantiate Doughnut3D passing references of this class and movieclip to draw in,
				// z-scale level and all governing properties in an object
				this.config.objDoughnut3D[strName] = new Doughnut3D(this, mcPieH, mcMain, objProps, this.plotX, this.plotX + this.plotWidth);
				// method called to set mouse event listener for this Doughnut3D instance 
				setEventHandlers(mcMain, objProps, this.config.objDoughnut3D[strName]);
			}
			// at end of looping and pie set generation ...
			if (this.config.isRadiusGiven) {
				// setting neighbors of the pies
				if (this.config.isInitialised || this.config.isPlotAnimationOver) {
					setNeighbors();
				}
				// to set the initial status of sliced pies by rendering visually                                                                             
				if (this.config.isInitialised) {
					setInitialStatus();
					// to set for initial animation of the pies
				} else if (this.config.isPlotAnimationOver) {
					setInitialWatch();
				}
				// If radius is not specified and not yet set too, then radius need to be set with top priority                                     
			} else {
				// method called to set the pie sliced without animation for checking of bounds of the chart
				setInitialStatus();
				// method called to check and set radius for the next step
				checkBounds(isGetting2DRadius);
			}
		} else {
			// If a singleton case ... stores the pie dependent properties in the objProps
			// and call to instantiate the Doughnut3D class for creation of the only pie .
			// calculating the only label coordinates
			var xTxt:Number = this.config.centerX;
			var yTxt:Number = this.config.centerY;
			// setting arrFinal properties in objProps
			objProps.arrFinal = new Array();
			objProps.arrFinal.startAngle = 0;
			objProps.arrFinal.no45degCurves = 8;
			objProps.arrFinal.remainderAngle = 0;
			objProps.arrFinal.endAngle = 0;
			objProps.arrFinal.sweepAngle = 360;
			objProps.arrFinal.pieColor = getFV(this.data[0].color, parseInt(this.defColors.getColor(), 16));
			objProps.arrFinal.borderColor = ColorExt.getDarkColor(this.defColors.getColor(), 0.65);
			objProps.arrFinal.meanAngle = 0;
			objProps.arrFinal.labelText = this.data[0].labelText;
			objProps.arrFinal.labelProps = [xTxt, yTxt];
			objProps.arrFinal.link = this.data[0].link;
			objProps.arrFinal.toolText = this.data[0].toolText;
			// specially added to avoid borders drawn at start angle and end angle of top face
			objProps.arrFinal.junctionSide = 'both';
			
			// store whether to wrap labels if needed
			objProps.manageLabelOverflow = this.params.manageLabelOverflow;
			//
			// unique name of moviclip for this pie slice
			var strMcName:String = 'mcPie';
			// new movieclip created and reference stored in private instance property
			var mcMain:MovieClip = mcPieH.createEmptyMovieClip(strMcName, 0);
			
			// call to instantiate Doughnut3D passing references of this class and movieclip to draw in,
			// z-scale level and all governing properties in an object
			this.config.objDoughnut3D.pie = new Doughnut3D(this, mcPieH, mcMain, objProps, this.plotX, this.plotX + this.plotWidth);
			// method called to set mouse event listener for this Doughnut3D instance
			setEventHandlers(mcMain, objProps, this.config.objDoughnut3D.pie);
			// N.B: initial alpha transition effect is deliberately not applied due visual coniderations (unlike pieChart3D)
			// Change code below,to handle chart init end, if its applied later on.
			// if this drawChart() call is for final rendering and not for internal evaluation of best radius
			if (!isGetting2DRadius) {
				// since alpha transition not applied to this doughnutChart3D, we don't wait for DATAPLOT animation time like 
				// pieChart3D, and call to securely handle chart initialisation end.
				this.setInitWatch();
			}
		}
	}
	/**
	 * setInitWatch method handles watching chart initialisation
	 * end.
	 */
	private function setInitWatch() {
		// if not singleton case and initial animation is to occur
		if (this.params.animation && this.config.totalSlices) {
			// set a watch on the property which tracks total number of pie/doughnuts ending their label animation.
			this.config.watch('iniFinishTracker', checkInitStatus, this);
			// cover 2 cases:
			// 1. singleton case
			// 2. multi-pie system without initial animation
		} else {
			var insRef = this;
			// MC created to control through frame change
			var mcIniWatch:MovieClip = this.mcPieH.createEmptyMovieClip('mcIniRenderWatch', this.mcPieH.getNextHighestDepth());
			// counter initialised to render chart after few more frames to secure and/or keep provision of further
			// development for a more complex initial chart rendering.
			mcIniWatch.frameCount = 0;
			// set the frame change function
			mcIniWatch.onEnterFrame = function() {
				// if the number of frames elapsed after setting this function is 2
				if (++this.frameCount>=2) {
					// notify chart initialisation end
					insRef.exposeChartRendered();
					
					//Unblocking the legend interactivity after chart initialisation
					delete insRef.lgndMC.onRelease;
					
					// destroy the frame change function
					delete this.onEnterFrame;
					// remove the functional MC
					this.removeMovieClip();
					
					//update the flag to note that chart is initialised (last function to be called)
					insRef.chartInit = true;
				}
			};
		}
	}
	/**
	 * checkInitStatus method is the callback function for
	 * taking action due watch set on a counter which tracks
	 * labelling animation ends.
	 */
	private function checkInitStatus(prop, oldVal, newVal, insRef):Number {
		// if all pie/doughnuts have finished label rendering via animation
		//------- Flash compiler Bug ---------//
		// 'this.totalSlices' generates compile time error though it shouldn't
		// and actually works fine in runtime. Compiler assumes "this" as the class instance in which this method
		// exists, while "this" denotes the object instance whose property is set to watch.
		//------------------------------------//
		if (newVal>=insRef.config.totalSlices) {
			var _mc:MovieClip = insRef.cMC.createEmptyMovieClip('mc_exposeRendered', insRef.cMC.getNextHighestDepth());
			_mc.onEnterFrame = function() {
				// to let all rendering be done, wait for the next frame.
				// call to notify initial chart rendering end
				insRef.exposeChartRendered();
				
				//Unblocking the legend interactivity after chart initialisation
				delete insRef.lgndMC.onRelease;
					
				delete this.onEnterFrame;
				this.removeMovieClip();
				//
				//update the flag to note that chart is initialised (last function to be called)
				insRef.chartInit = true;
			};
			
			//to display overflown label managed on initial animation end
			if(insRef.params.manageLabelOverflow){
				insRef.recreate(0, false);
			}
			
			// remove the watch
			this.unwatch(prop);
		}
		// update the counter/property with the new value                   
		return newVal;
	}
	/**
	 * animateIni is the method to regulate individual pie 
	 * unfolding itself by animation.
	 * @param	_mc		reference of the movieclip to animate in
	 * @param	strId	String denoting unique object property
	 *					name, storing the setInterval id for this method
	 *					call.
	 * @param	index	number denoting the index of the pie 
	 * 					w.r.t. arrFinal
	 */
	private function animateIni(_mc:MovieClip, strId:String, index:Number):Void {
		// storing the reference of this Doughnut3DChart instance
		var insRef:Doughnut3DChart = this;
		// initialising counter to zero
		var tracker:Number = 0;
		// smart line (having 3 points viz. start,vertex,end) is drawn as follows:
		// 1. line from starting point (static) extends by animation to the final vertex point (multiple draw)
		// 2. if final vertex is achieved, line to end point is drawn in one shot (a small distance)
		// setting smartline animation
		this.objIntervalIds['id_line'+index] = setInterval(function () {
			// incrementing counter
			if (insRef.params.enableSmartLabels) {
				// then smartline is relevant and hence requires animation
				tracker++;
				// clearing preexisting line drawing, if any
				_mc.mcLabel.clear();
				// value set in local variables to be used in drawing smartlines
				// starting abscissa
				var x1:Number = _mc.arrLinePoints[0];
				// starting ordinate
				var y1:Number = _mc.arrLinePoints[1];
				// final vertex abscissa 
				var x2:Number = _mc.arrLinePoints[2];
				// ending abscissa
				var x3:Number = _mc.arrLinePoints[3];
				// vertex and ending ordinate (same value)
				var y3:Number = _mc.arrLinePoints[4];
				var x4:Number, y4:Number;
				// coordinates of the dynamic end point of the line towards vertex point is calculated
				if (tracker<6) {
					x4 = (x2-x1)*tracker/5+x1;
					y4 = (y3-y1)*tracker/5+y1;
					// vertex already achieved .... its now required to finish drawing upto the end point 
				} else {
					x4 = x2;
					y4 = y3;
				}
				_mc.mcLabel.lineStyle(insRef.params.smartLineThickness, parseInt(insRef.params.smartLineColor, 16), insRef.params.smartLineAlpha);
				// starting point (static)
				_mc.mcLabel.moveTo(x1, y1);
				// dynamic end point of line with vertex as its limit
				_mc.mcLabel.lineTo(x4, y4);
				if (tracker == 6) {
					// end point
					_mc.mcLabel.lineTo(x3, y3);
				}
			} else {
				// else, no smartline and no draw
				tracker = 6;
			}
			// on end of smartline creation                                    
			if (tracker>=6) {
				// label text stored
				var txt:String = _mc.store['labelText'];
				var objProp:Object = insRef.styleM.getTextStyle(insRef.objects.DATALABELS);
				//
				var fmtTxt:TextFormat = new TextFormat();
				fmtTxt.font = objProp.font;
				fmtTxt.size = objProp.size;
				fmtTxt.color = parseInt(objProp.color, 16);
				fmtTxt.italic = objProp.italic;
				fmtTxt.bold = objProp.bold;
				fmtTxt.underline = objProp.underline;
				fmtTxt.letterSpacing = objProp.letterSpacing;
				// if this pie is one of the conjugated pair (only one of the two set to animate), then
				// enable the other one for mouse interaction, from thereon
				if (_mc.store.junctionSide) {
					// conjugated pie pair is characterised to be the first and last ones in the stacking z-order
					// and hence they are named likewise
					if (_mc._name == 'mcPie_0') {
						_mc._parent['mcPie_'+(insRef.config.totalSlices-1)].enabled = true;
					} else if (_mc._name == 'mcPie_'+(insRef.config.totalSlices-1)) {
						_mc._parent.mcPie_0.enabled = true;
					}
				}
				// enable the original pie under animation                                                                                                    
				_mc.enabled = true;
				// retrieving depth of the pie movieclip to control the Doughnut3D instance associated with it
				var k:Number = _mc.getDepth();
				// updating a flag in the Doughnut3D instance
				insRef.config.objDoughnut3D['pie'+k].objData.isInitialised = true;
				
				// now showing up the text field border if applicable
				if (objProp.borderColor != '') {
					_mc.mcLabel.label_txt.border = true;
					_mc.mcLabel.label_txt.borderColor = parseInt(objProp.borderColor, 16);
				}
				if (objProp.bgColor != '') {
					_mc.mcLabel.label_txt.background = true;
					_mc.mcLabel.label_txt.backgroundColor = parseInt(objProp.bgColor, 16);
				}
				
				var quadrantId:Number = _mc.store['labelProps'][2];
				if(quadrantId == 2 || quadrantId == 3 ){
					fmtTxt.align = 'right';
				}
				_mc.mcLabel.label_txt.text = txt;
				insRef.styleM.applyFilters(_mc.mcLabel.label_txt, insRef.objects.DATALABELS);
				_mc.mcLabel.label_txt.setTextFormat(fmtTxt);
				// line animation is over    
				clearInterval(insRef.objIntervalIds[['id_line'+index]]);
				
				// update counter for label animation end for this pie
				insRef.config.iniFinishTracker++;
				
				if (_mc.store.junctionSide) {
					// its updated for conjuagted extra one too, cause totalSlices counts this extra conjuagted one
					insRef.config.iniFinishTracker++;
				}
			}
		}, 50);
		// this method call is due to setInterval, but to run only once
		clearInterval(objIntervalIds[strId]);
	}
	/**
	 * animateTo2D is the method called to change the dimension
	 * of the chart in animation, from 3D to 2D.
	 */
	private function animateTo2D():Void {
		// the number of steps to change the dimension as well as the change (compensation) of radius (individually)
		var steps:Number = 10;
		// storing the reference of the pie3DChart instance
		var instanceRef = this;
		// applying a blank onRelease function on the parent mc of the pies to disable the pie(mc) level 
		// mouse interaction during transtion
		mcPieH.onRelease = function() {
		};
		// hand cursor due to former action is avoided
		mcPieH.useHandCursor = false;
		
		//
		//Blocking the legend interactivity to avoid slicing during 2D-3D animation.
		lgndMC.onRelease = function() {};
		//No hand cursor
		lgndMC.useHandCursor = false;
		
		
		
		// flag updated to indicate that dimension changing animation is under progress
		this.config.changingDimension = true;
		// 3D depth decrement in each 'step'
		var depthDecrement:Number = this.config.pieSliceDepth/steps;
		// perspective incrementing factor (3D to 2D) in each 'step'
		var scaleIncrement:Number = (100-this.config.pieYScale*100)/steps;
		// if 2D radius need to be set for better fitting in canvas
		if (this.config.radius>this.config.radius2D) {
			//radius decrement in each 'step'
			var radiusDecrement:Number = (this.config.radius-this.config.radius2D)/steps;
			radiusDecrement = Math.max(radiusDecrement, 5);
		}
		// clearing any uncleared setInterval for transtion to 3D call due to premature toggling of                                           
		// context menu option (2D/3D)
		clearInterval(this.objIntervalIds.to3DId);
		// all set to go for the required transtion
		this.objIntervalIds.to2DId = setInterval(function () {
			// getting the current status of the yScale of the pieChart in scale of 100
			var scale:Number = instanceRef.config.pieYScale*100;
			// For transition from 3D to 2D, we first go for change (decrease) in radius for proper fitting in canvas,
			// and thereafter the perspective is changed to have the 2D look.
			//
			// if radius can be decremented by the previously calculated amount
			if (instanceRef.config.radius-instanceRef.config.radius2D>radiusDecrement) {
				// radius decremented
				instanceRef.config.radius -= radiusDecrement;
				instanceRef.config.doughnutRadius = (instanceRef.config.radiusDough2D/instanceRef.config.radius2D)*instanceRef.config.radius;
				// coordinates of the chart center is re-evaluated
				instanceRef.setChartCenter();
				// updating repository with the current slicing status of the pies
				instanceRef.loadCurrentSlicingStatus();
				// call to recreate chart
				instanceRef.recreate(0, false);
				// now if perspective is yet to give the 2D look
			} else if (scale<100) {
				// pieRadius is set to pre-calculated value for 2D radius
				instanceRef.config.radius = instanceRef.config.radius2D;
				instanceRef.config.doughnutRadius = instanceRef.config.radiusDough2D;
				// if scaling can be altered by the value of 'scaleIncrement'
				if (100-scale>scaleIncrement) {
					scale += scaleIncrement;
					// else, just set the value to maximum (i.e 100)
				} else {
					scale = 100;
				}
				// scale value assigned on the scale of 1 (not 100)
				instanceRef.config.pieYScale = scale/100;
				// 3D depth of the chart decremented
				instanceRef.config.pieSliceDepth -= depthDecrement;
				// coordinates of the chart center is re-evaluated
				instanceRef.setChartCenter();
				// updating repository with the current slicing status of the pies
				instanceRef.loadCurrentSlicingStatus();
				// call to recreate chart
				instanceRef.recreate(0, false);
				// else if transition is over, put an end to the process
			} else {
				// flag updated to indicate that dimension changing animation is over
				instanceRef.config.changingDimension = false;
				//
				//Remove the event handler from the parent to enable interactivity of the children items of the legend
				delete instanceRef.lgndMC.onRelease;
				
				// empty onRelease handler set previously to disable pie(mc) mouse interaction requirement is over and hence deleted
				delete instanceRef.mcPieH.onRelease;
				// clear setInterval to end up the process
				clearInterval(instanceRef.objIntervalIds.to2DId);
				
			}
			updateAfterEvent();
		}, 20);
	}
	/**
	 * animateTo3D is the method called to change the dimension
	 * of the chart in animation, from 2D to 3D.
	 */
	private function animateTo3D():Void {
		// the number of steps to change the dimension as well as the change (compensation) of radius (individually)
		var steps:Number = 10;
		// storing the reference of the pie3DChart instance
		var instanceRef = this;
		// applying a blank onRelease function on the parent mc of the pies to disable the pie(mc) level 
		// mouse interaction during transtion
		mcPieH.onRelease = function() {
		};
		// hand cursor due to former action is avoided
		mcPieH.useHandCursor = false;
		
		//
		//Blocking the legend interactivity to avoid slicing during 2D-3D animation.
		lgndMC.onRelease = function() {};
		//No hand cursor
		lgndMC.useHandCursor = false;
		
		// flag updated to indicate that dimension changing animation is under progress
		this.config.changingDimension = true;
		// 3D depth increment in each 'step'
		var depthIncrement:Number = this.params.pieSliceDepth/steps;
		// perspective decrementing factor (2D to 3D) in each 'step'
		var scaleDecrement:Number = (100-this.params.pieYScale)/steps;
		// if 3D radius need to be set to the original
		if (this.config.radius<this.config.radius3D) {
			//radius increment in each 'step'
			var radiusIncrement:Number = (this.config.radius3D-this.config.radius)/steps;
			radiusIncrement = Math.max(radiusIncrement, 5);
		}
	
		// clearing any uncleared setInterval for transtion to 2D call due to premature toggling of                                            
		// context menu option (2D/3D)
		clearInterval(this.objIntervalIds.to2DId);
		// all set to go for the required transtion
		this.objIntervalIds.to3DId = setInterval(function () {
			// getting the current status of the yScale of the pieChart in scale of 100
			var scale:Number = (instanceRef.config.pieYScale*100) >> 0;
			// For transition from 2D to 3D, we first go for change (decrease) in pieYScale  to have the required 3D perspective look,
			// and thereafter the radius is changed to original value.
			//
			// if perspective is yet to give the final 3D look
			if (scale>instanceRef.params.pieYScale) {
				// if scaling can be altered by the value of 'scaleDecrement'
				if (scale-instanceRef.params.pieYScale>scaleDecrement) {
					scale -= scaleDecrement;
					// else, just assign the final scale value
				} else {
					scale = instanceRef.params.pieYScale;
				}
				// scale value assigned on the scale of 1 (not 100)
				instanceRef.config.pieYScale = scale/100;
				// 3D depth of the chart incremented
				instanceRef.config.pieSliceDepth += depthIncrement;
				// coordinates of the chart center is re-evaluated
				instanceRef.setChartCenter();
				// updating repository with the current slicing status of the pies
				instanceRef.loadCurrentSlicingStatus();
				// call to recreate chart
				instanceRef.recreate(0, false);
				// if radius can be incremented by the previously calculated amount
			} else if (instanceRef.config.radius3D-instanceRef.config.radius>radiusIncrement) {
				//code was missing (logic may skip this condition .. so added in the "else" part to ascertain the step.)
				instanceRef.config.pieYScale = instanceRef.params.pieYScale/100;
				
				// radius incremented
				instanceRef.config.radius += radiusIncrement;
				instanceRef.config.doughnutRadius = (instanceRef.config.radiusDough3D/instanceRef.config.radius3D)*instanceRef.config.radius;
				
				// coordinates of the chart center is re-evaluated
				instanceRef.setChartCenter();
				// updating repository with the current slicing status of the pies
				instanceRef.loadCurrentSlicingStatus();
				// call to recreate chart
				instanceRef.recreate(0, false);
				// else, recreate the chart once again with the final radius, and thereafter end up the transition process
			} else {
				//code was missing (logic may skip the previous condition .. so added in the "else" part to ascertain the step.)
				instanceRef.config.pieYScale = instanceRef.params.pieYScale/100;
				
				instanceRef.config.radius = instanceRef.config.radius3D;
				instanceRef.config.doughnutRadius = instanceRef.config.radiusDough3D;
				
				// coordinates of the chart center is re-evaluated
				instanceRef.setChartCenter();
				// updating repository with the current slicing status of the pies
				instanceRef.loadCurrentSlicingStatus();
				// call to recreate chart
				instanceRef.recreate(0, false);
				// flag updated to indicate that dimension changing animation is over
				instanceRef.config.changingDimension = false;
				// empty onRelease handler set previously to disable pie(mc) mouse interaction requirement is over and hence deleted
				delete instanceRef.mcPieH.onRelease;
				//
				//Remove the event handler from the parent to enable interactivity of the children items of the legend
				delete instanceRef.lgndMC.onRelease;
				
				// clear setInterval to end up the process
				clearInterval(instanceRef.objIntervalIds.to3DId);
				
			}
			updateAfterEvent();
		}, 20);
	}
	//--------------- UTILITY METHODS ----------------//
	/**
	 * iniTrackerUpdate method is called from Doughnut3D instances
	 * on their initial animation end (if isSliced is true)
	 * update a counter.
	 */
	public function iniTrackerUpdate():Void {
		this.config.iniTracker++;
	}
	//--------------- EVENT HANDLERS -----------------//
	/**
	 * setEventHandlers method is called to set event handlers
	 * for the pie movieclips.
	 * @param	_mc			Reference of the movieclip to set handlers
	 *						on
	 * @param	objProps	Object holding properties for the pie
	 * @param	Doughnut3DRef	reference of the Doughnut3D instance associated
	 *							with the pie
	 */
	private function setEventHandlers(_mc:MovieClip, objProps:Object, Doughnut3DRef):Void {
		var Doughnut3DInsRef = Doughnut3DRef;
		var link:String = objProps.arrFinal.link;
		var insRef:Doughnut3DChart = this;
		//---------------------
		var fnRollOver:Function;
		//Create Delegate for RollOver function pieOnRollOver
		fnRollOver = Delegate.create(this, pieOnRollOver);
		//Set the mc
		fnRollOver.mc = _mc;
		//Set the link
		fnRollOver.link = link;
		//Assing the delegates to movie clip handler
		_mc.onRollOver = fnRollOver;
		//---------------------
		var fnRollOut:Function;
		//Create Delegate for RollOut function pieOnRollOut
		fnRollOut = Delegate.create(this, pieOnRollOut);
		//Set the mc
		fnRollOut.mc = _mc;
		//Assing the delegates to movie clip handler
		_mc.onRollOut = _mc.onReleaseOutside=fnRollOut;
		//---------------------
		if (this.params.clickURL == '') {
			if (this.config.enableLinks && objProps.arrFinal.link != '') {
				var fnRelease:Function;
				//Create Delegate for onRelease function pieOnClick
				fnRelease = Delegate.create(this, pieOnClick);
				//Set the link
				fnRelease.link = link;
				//Assing the delegates to movie clip handler
				_mc.onRelease = fnRelease;
			}
			//---------------------//                                                                                               
			if (!(this.config.enableRotation || this.config.enableLinks || this.config.totalSlices == 0)) {
				//
				_mc.onRelease = function(){
					// if legend be there in the chart
					if(insRef.params.showLegend  && insRef.params.interactiveLegend && insRef.data[this.dataId].label){
						//var id:Number = (insRef.params.reverseLegend) ? insRef.num - this.dataId - 1: this.dataId;
						// slice/unslice the pie via legend which takes care of all management issues
						insRef.lgnd.clickEvent(this.dataId);
					// else if legend is not there in the chart	
					}else{
						// ask the Pie3D instance to move the pie 
						Doughnut3DInsRef.movePie();
					}
				}
			}
		}
		//
		if(this.config.totalSlices > 1){
			//Add listener for "slicing" event from Doughnut3D objects for slice movement start or end.
			Doughnut3DInsRef.addEventListener('slicing', this);
		}
	}
	
	/** 
	 * slicing method listens to slicing start and end of pies to update a counter
	 * holding the number of pies currently slicing.
	 */
	private function slicing(e:Object):Void{
		this.numSlicing += (e.status == 'start')? 1 : -1;
	}
	
	/** 
	 * sliceViaJS method is called by JS to slice in/out a pie.
	 * @param 	id		The index of data for the pie
	 */
	private function sliceViaJS(id:Number):Void{
		if(isNaN(id) || id < 0 || Math.round(id) != id || id >= this.num){
			this.log("Index Ignored", "The index provided for toggling slice was invalid. Please check the same", Logger.LEVEL.ERROR);
			return;
		}
		
		// If not a singleton case and if the chart is not currently undergoing a transition when this slicing option is not pertinent
		if(this.config.totalSlices > 1 && !this.config.changingDimension && !this.rotating && this.chartInit){
			// if legend be there in the chart
			if(this.params.showLegend && this.params.interactiveLegend && this.data[id].label){
				// slice/unslice the pie via legend which takes care of all management issues
				this.lgnd.clickEvent(id);
			// else if legend is not there in the chart	
			}else{
				// get the reference of the Doughnut3D instance for the pie
				var Doughnut3DInsRef:Doughnut3D = this.config.objDoughnut3D[this.map[id]];
				// call to move the pie
				Doughnut3DInsRef.movePie();
			}
		}
	}
	
	/**
	 * enablelinkViaJS method is called by JS to enable pie click.
	 */
	private function enablelinkViaJS():Void {
		if(this.numSlicing != 0 || this.rotating){
			return;
		}
		// updating flags about current menu enable status
		this.config.enableLinks = true;
		this.config.enableRotation = false;
		cmiRotation.enabled = true;
		cmiSlicing.enabled = true;
		cmiLink.enabled = false;
		// if the previous enabled menu is "Enable Slicing Movement", then one could have clicked different pie to change their 
		// slicing states. Now , this latest status should be updated in the internal database before redrawing
		this.loadCurrentSlicingStatus();
		// call to redraw; 0(zero) - no change in overall view; false - redraw is static and not due to rotation, a flag to be used to in setInitialStatus() to set positions of pies
		this.recreate(0, true);
	}
	
	/**
	 * enableSlicingViaJS method is called by JS to enable pie slicing.
	 */
	private function enableSlicingViaJS() {
		if(this.numSlicing != 0 || this.rotating){
			return;
		}
		cmiRotation.enabled = true;
		cmiSlicing.enabled = false;
		cmiLink.enabled = true;
		this.config.enableLinks = false;
		this.config.enableRotation = false;
		// if the previous enabled menu is "Enable Slicing Movement", then one could have clicked different pie to change their 
		// slicing states. Now , this latest status should be updated in the internal database before redrawing
		this.loadCurrentSlicingStatus();
		this.recreate(0, true);
	}
	
	/**
	 * pieOnRollOver method is invoked when mouse is over a 
	 * pie.
	 */
	private function pieOnRollOver():Void {
		var _mc:MovieClip = arguments.caller.mc;
		var link:String = arguments.caller.link;
		//
		var strDisplay:String = _mc.store['toolText'];
		if (strDisplay != '' && this.params.showToolTip) {
			this.tTip.setText(strDisplay);
			this.tTip.show();
		}
		if ((link == '' || link == undefined || !this.config.enableLinks) && this.params.clickURL == '') {
			_mc.useHandCursor = false;
		}
		_mc.onMouseMove = Delegate.create(this, pieOnMouseMove);
	}
	/**
	 * pieOnRollOut method is invoked when mouse rolls out of
	 * a pie.
	 */
	private function pieOnRollOut():Void {
		this.tTip.hide();
		var _mc:MovieClip = arguments.caller.mc;
		delete _mc.onMouseMove;
	}
	/**
	 * pieOnMouseMove method is invoked when mouse moves over
	 * a pie.
	 */
	private function pieOnMouseMove():Void {
		this.tTip.rePosition();
	}
	/**
	 * pieOnClick method is invoked when mouse is released 
	 * over a pie.
	 */
	private function pieOnClick():Void {
		super.invokeLink(arguments.caller.link);
	}
	/**
	 * rotateChart method is called onEnterFrame of mcPieH
	 * to recreate the chart for rotational effect w.r.t. 
	 * latest mouse position of dragging. Since, its executed
	 * by onenterFrame, CPU speed of the system or the load of
	 * data set is immaterial and rotation proceeds with the 
	 * best speeed possible on the system , with given data set.
	 */
	private function rotateChart():Void {
		// mouse coordinates w.r.t. _root
		var xRef:Number = _root._xmouse;
		var yRef:Number = _root._ymouse;
		// eccentric angle w.r.t. pie ellipse is returned w.r.t. current mouse position
		var angFinal:Number = getDragAngle(xRef, yRef);
		// inserting the returned angle at the beginning of the array storing the angles at each invokation 
		// of this method, in this dragging session
		var arrLength:Number = objMouseListener.arrAnglesDragged.unshift(angFinal);
		// if this is not the first invokation, then redraw chart with a different starting angle
		if (arrLength>1) {
			//
			// reload the slicing status of pies, so required since slicing is possible (now by v3.2) in rotation mode via legend and/or JS API 
			this.loadCurrentSlicingStatus();
			var d:Array = objMouseListener.arrAnglesDragged;
			// the change in starting angle that should occur due to rotation
			var delAng:Number = d[0]-d[1];
			// now redraw
			recreate(delAng, false);
		}
	}
	/**
	 * onMousedown method is called whenever mouse is clicked
	 * to initialise drag and rotate utility.
	 */
	private function onMouseDown():Void {
		//
		// if user interaction is set for drag and rotate
		if (this.config.enableRotation && !this.config.changingDimension && this.numSlicing == 0) {
			// mouse coordinates w.r.t. _root
			var xRef:Number = _root._xmouse;
			var yRef:Number = _root._ymouse;
			// if mouseDown is on a pie/pies
			if (mcPieH.hitTest(xRef, yRef, true)) {
				this.tTip.hide();
				var insRef = this;
				// set onEnterFrame on mcPieH to set rotation
				mcPieH.onEnterFrame = function() {
					insRef.rotateChart();
					//
					insRef.rotating = true;
				};
			}
		}
	}
	/**
	 * onMouseup method is called whenever mouse is released
	 * to terminate drag and rotate utility.
	 */
	private function onMouseUp():Void {
		// if user interaction is set for drag and rotate
		if (this.config.enableRotation && !this.config.changingDimension) {
			// mouse coordinates w.r.t. _root
			var xRef:Number = _root._xmouse;
			var yRef:Number = _root._ymouse;
			// remove onEnterFrame from mcPieH to stop rotation
			delete mcPieH.onEnterFrame;
			//
			this.rotating = false;
			// array holding the angles for each invokation of onMouseMove() is emptied
			objMouseListener.arrAnglesDragged.splice(0);
			// if mouseUp is on a pie/pies
			if (mcPieH.hitTest(xRef, yRef, true) && this.params.showToolTip) {
				this.tTip.show();
			}
		}
	}
	/**
	 * setMouseListener method is called initially to set the
	 * mouse triggered events for drag and rotate utility.
	 */
	private function setMouseListener():Void {
		objMouseListener = new Object();
		// array to hold the angles subtended by the mouse, sequencially for each invokation of onMouseMove()
		objMouseListener.arrAnglesDragged = new Array();
		// listener object assigned to mouse
		Mouse.addListener(this);
	}
	/**
	 * legendClick method is the event handler for legend. In this method,
	 * slice/unslice the pie.
	*/
	private function legendClick(target:Object):Void{
		//If not a singleton piechart
		if (this.config.totalSlices > 1 && this.chartDrawn) {
			//
			// slice/unslice the pie
			this.config.objDoughnut3D[this.map[target.index]].movePie();
		}else{
			lgnd.cancelClickEvent(target.intIndex);
		}
	}
	/**
	 * setContextMenu method is called only once initially
	 * to set right click behavior and options.
	 */
	private function setContextMenu():Void {
		// ContextMenu instance is created
		var cmCustom:ContextMenu = new ContextMenu();
		// hide the default menu items
		cmCustom.hideBuiltInItems();
		if (this.params.showPrintMenuItem) {
			//Create a print chart contenxt menu item
			var printCMI:ContextMenuItem = new ContextMenuItem("Print Chart", Delegate.create(this, printChart));
			//Push print item.
			cmCustom.customItems.push(printCMI);
		}
		//If the export data item is to be shown
		if (this.params.showExportDataMenuItem){
			cmCustom.customItems.push(super.returnExportDataMenuItem());
		}
		//Add export chart related menu items to the context menu
		this.addExportItemsToMenu(cmCustom);
		if (this.params.clickURL == '') {
			// initially, if links are defined for atleast one pie, then the initial mode of user interaction is set to 'Enable Links'
			if (this.config.linksDefined) {
				// setting enable status of the 3 menu items
				var isRotationCMIEnabled:Boolean = true;
				var isSlicingCMIEnabled:Boolean = true;
				var isLinkCMIEnabled:Boolean = false;
				// otherwise, initial user interaction mode is set to what ever is specified in the xml
			} else {
				// setting enable status of the 2 menu items ("Enable Links" is irrelevant for none of the pies are having links)
				var isRotationCMIEnabled:Boolean = (!this.config.enableRotation);
				var isSlicingCMIEnabled:Boolean = this.config.enableRotation;
			}
		}
		// enabled status of the view 2D/3D options (always true, their visibility changes only)                   
		var isTo2DCMIEnabled:Boolean = true;
		var isTo3DCMIEnabled:Boolean = true;
		// instantiating ContextMenuItem for each menu item
		cmiRotation = new ContextMenuItem("Enable Rotation", rotationHandler, false, isRotationCMIEnabled);
		cmiRotation.separatorBefore = true;
		cmiSlicing = new ContextMenuItem("Enable Slicing Movement", movementHandler, false, isSlicingCMIEnabled);
		var cmiTo2D:ContextMenuItem = new ContextMenuItem("View 2D", to2DHandler, true, isTo2DCMIEnabled);
		var cmiTo3D:ContextMenuItem = new ContextMenuItem("View 3D", to3DHandler, true, isTo3DCMIEnabled, false);
		// "Enable Links" will be available if and only if link is defined for atleast one pie.
		if (this.config.linksDefined) {
			cmiLink = new ContextMenuItem("Enable Links", linkHandler, false, isLinkCMIEnabled);
		}
		if (this.num != 1) {
			if (this.params.clickURL == '') {
				// inclusion of the items in the custom items section of context menu                                                        
				cmCustom.customItems.push(cmiRotation);
				cmCustom.customItems.push(cmiSlicing);
				if (this.config.linksDefined) {
					cmCustom.customItems.push(cmiLink);
				}
			}
			cmCustom.customItems.push(cmiTo2D);
			cmCustom.customItems.push(cmiTo3D);
		}
		// Doughnut3DChart instance reference is stored                                                                                                    
		var instanceRef = this;
		// functions invoked due selection of the menu items are defined
		function to2DHandler(obj, item) {
			//"rotating" is checked to see that context menu and hence its item is not opted during chart rotation!
			if(instanceRef.numSlicing == 0 && !instanceRef.rotating){
				cmiTo2D.visible = false;
				cmiTo3D.visible = true;
				instanceRef.animateTo2D();
			}
		}
		function to3DHandler(obj, item) {
			//"rotating" is checked to see that context menu and hence its item is not opted during chart rotation!
			if(instanceRef.numSlicing == 0 && !instanceRef.rotating){
				cmiTo2D.visible = true;
				cmiTo3D.visible = false;
				instanceRef.animateTo3D();
			}
		}
		function linkHandler(obj, item) {
			//"rotating" is checked to see that context menu and hence its item is not opted during chart rotation!
			if(instanceRef.numSlicing != 0 || instanceRef.rotating){
				return;
			}
			// disabling action due choice of this option during transition between 2D and 3D
			if (!instanceRef.config.changingDimension) {
				// enabling/disabling the ContextMenuItems
				instanceRef.cmiRotation.enabled = true;
				instanceRef.cmiSlicing.enabled = true;
				instanceRef.cmiLink.enabled = false;
				// updating flags about current menu enable status
				instanceRef.config.enableLinks = true;
				instanceRef.config.enableRotation = false;
				//
				// if the previous enabled menu is "Enable Slicing Movement", then one could have clicked different pie to change their 
				// slicing states. Now , this latest status should be updated in the internal database before redrawing
				instanceRef.loadCurrentSlicingStatus();
				// call to redraw; 0(zero) - no change in overall view; false - redraw is static and not due to rotation, a flag to be used to in setInitialStatus() to set positions of pies
				instanceRef.recreate(0, true);
			}
		}
		function movementHandler(obj, item) {
			//"rotating" is checked to see that context menu and hence its item is not opted during chart rotation!
			if(instanceRef.numSlicing != 0 || instanceRef.rotating){
				return;
			}
			// disabling action due choice of this option during transition between 2D and 3D
			if (!instanceRef.config.changingDimension) {
				instanceRef.cmiRotation.enabled = true;
				instanceRef.cmiSlicing.enabled = false;
				instanceRef.cmiLink.enabled = true;
				instanceRef.config.enableLinks = false;
				instanceRef.config.enableRotation = false;
				//
				instanceRef.loadCurrentSlicingStatus();
				instanceRef.recreate(0, true);
			}
		}
		function rotationHandler(obj, item) {
			//
			if(instanceRef.numSlicing != 0){
				return;
			}
			// disabling action due choice of this option during transition between 2D and 3D
			if (!instanceRef.config.changingDimension) {
				instanceRef.cmiRotation.enabled = false;
				instanceRef.cmiSlicing.enabled = true;
				instanceRef.cmiLink.enabled = true;
				instanceRef.config.enableLinks = false;
				instanceRef.config.enableRotation = true;
				//
				instanceRef.loadCurrentSlicingStatus();
				instanceRef.recreate(0, true);
			}
		}
		if (this.params.showFCMenuItem) {
			//Push "About FusionCharts" Menu Item
			cmCustom.customItems.push(super.returnAbtMenuItem());
		}
		// applying the custom menu formed to the chart movieclip ... vital for multichart display in one swf                  
		mcPieH._parent.menu = cmCustom;
		//Clear interval
		clearInterval(this.config.intervals.menu);
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
		for (i = 0; i < this.num; i++) {
			strData += strQ + this.data[i].label + strQ + strS + strQ + ((this.params.exportDataFormattedVal==true)?(this.data[i].formattedValue):(this.data[i].value)) + strQ + ((i<(this.num-1))?strLB:""); 
		}
		return strData;
	}
}
