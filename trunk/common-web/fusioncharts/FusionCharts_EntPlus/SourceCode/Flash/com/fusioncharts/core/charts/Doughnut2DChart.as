﻿/**
 * Doughnut2DChart chart extends the Chart class to render a 
 * 2D Pie Chart.
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
import com.fusioncharts.core.chartobjects.Doughnut2D;
//
class com.fusioncharts.core.charts.Doughnut2DChart extends Chart {
	//Version number (if different from super Chart class)
	//private var _version:String = "3.0.0";
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
	//A consolidated object to store the ids of setInterval
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
	//
	//Map of Doughnut2D objects with their respective entries in this.data
	private var map:Array = new Array();
	//
	//Holds the number of pies slicing at any point of time
	private var numSlicing:Number = 0;
	//Flag to rotating state of the chart
	private var rotating:Boolean = false;
	//Flag to indicate that resizing is pending due slicing
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
	function Doughnut2DChart(targetMC:MovieClip, depth:Number, width:Number, height:Number, x:Number, y:Number, debugMode:Boolean, lang:String, scaleMode:String, registerWithJS:Boolean, DOMId:String) {
		//Invoke the super class constructor
		super(targetMC, depth, width, height, x, y, debugMode, lang, scaleMode, registerWithJS, DOMId);
		//Log additional information to debugger
		//We log version from this class, so that if this class version
		//is different, we can log it
		this.log("Version", _version, Logger.LEVEL.INFO);
		this.log("Chart Type", "2D Doughnut Chart", Logger.LEVEL.INFO);
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
		
		//setting watch on number indicating the state of slicing
		this.watch('numSlicing', checkResizePendingForSlicing, this);
		//
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
		setNodeCounter = 0;
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
	private function checkResizePendingForChartInit(prop:String, oldVal:Boolean, newVal:Boolean, insRef:Doughnut2DChart):Boolean{
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
	 * checkResizePendingForSlicing method responds on change in the number of
	 * pies slicing to check if resizing is on hold and be initiated now.
	 * @params	prop		property watched
	 * @params	oldVal		old value of the property
	 * @params	newVal		new value of the property
	 * @returns				the new value
	 */
	private function checkResizePendingForSlicing (prop:String, oldVal:Number, newVal:Number, insRef:Doughnut2DChart):Number{
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
	
	// -------------------- CORE CHART METHODS -------------------------- //
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
				if(this.numSlicing > 0){
					//For pie slicing at this point of time, its inappropriate to go for resizing
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
		if (this.num == 0) {
			tfAppMsg = this.renderAppMessage(_global.getAppMessage("NODATA", this.lang));
			//Add a message to log.
			this.log("No Data to Display", "No data was found in the XML data document provided. Possible cases can be: <LI>There isn't any data generated by your system. If your system generates data based on parameters passed to it using dataURL, please make sure dataURL is URL Encoded.</LI><LI>You might be using a Single Series Chart .swf file instead of Multi-series .swf file and providing multi-series data or vice-versa.</LI>", Logger.LEVEL.ERROR);
			//Expose rendered method
			this.exposeChartRendered();
			
			this.raiseNoDataExternalEvent();
			
		} else {
			//If the sum of all pie is equal to 0, we show pertinent error.
			if (this.config.sumOfValues == 0) {
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
		//Call the unified draw method to render chart. Also cosidering there is valid data to display.
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
		/**
		 * For the 2D Pie chart, we need to set defaults for the
		 * following object - property combinations:
		 * CAPTION - FONT
		 * SUBCAPTION - FONT
		 * DATALABELS - FONT
		 * TOOLTIP - FONT
		 * DATAPLOT - Default Animation (Alpha)
		 */
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
		//Default Effect (Shadow) object for DataPlot
		//-----------------------------------------------------------------//
		if (this.params.showShadow) {
			var dataPlotShadow = new StyleObject();
			dataPlotShadow.name = "_SdDataPlotShadow";
			dataPlotShadow.angle = 45;
			//If we do not have to show column shadow
			dataPlotShadow.alpha = this.params.shadowAlpha;
			dataPlotShadow.color = this.params.shadowColor;
			//Over-ride     
			this.styleM.overrideStyle(this.objects.DATAPLOT, dataPlotShadow, this.styleM.TYPE.SHADOW, null);
			delete dataPlotShadow;
		}
		//-----------------------------------------------------------------//                                            
		//Default Animation object for DataPlot (if required)
		//-----------------------------------------------------------------//
		if (this.params.defaultAnimation) {
			var dataPlotAnim = new StyleObject();
			dataPlotAnim.name = "_SdDataPlotAnim";
			dataPlotAnim.easing = "regular";
			dataPlotAnim.start = 0;
			dataPlotAnim.duration = 1;
			var strEffect:String = (this.num>1) ? "_rotation" : "_alpha";
			dataPlotAnim.param = strEffect;
			//Over-ride
			this.styleM.overrideStyle(this.objects.DATAPLOT, dataPlotAnim, this.styleM.TYPE.ANIMATION, strEffect);
			delete dataPlotAnim;
		}
		//-----------------------------------------------------------------//                                                                                                                                                                                                                
	}
	/**
	 * cleanUp method is called to purge the basics before
	 * regeneration process of chart can begin.
	 */
	private function cleanUp():Void {
		// iterating to get all movieclips in piechart in order 
		// to clean them up and set the stage ready for next redraw
		// array holding name of sub-movieclips of mcPieH
		var arrMcName:Array = ['mcPieHolder', 'mcLabelHolder'];
		for (var t:Number = 0; t<2; ++t) {
			// taking up a movieclip at a time
			var mc:MovieClip = mcPieH[arrMcName[t]];
			for (var p in mc) {
				if (mc[p] instanceof MovieClip) {
					// clearing all event handlers from the movieclips to be removed
					delete mc[p].onRollOver;
					delete mc[p].onRollOut;
					delete mc[p].onRelease;
					delete mc[p].onReleaseOutside;
					// movieclip removed
					mc[p].removeMovieClip();
				}
			}
		}
		
		//main processed data holding array emptied
		this.config.arrFinal.splice(0);
		//certain properties are set to null
		this.config.totalSlices = null;
		
		//reset the map to a blank array
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
		//
		//Map of Doughnut2D objects with their respective entries in this.data
		this.map = [];
		//
		//The number pies slicing is reset to zero.
		this.numSlicing = 0;
		//
		//The rotating status of the chart is reset to false.
		this.rotating = false;
		//
		//Flag to hold status of chart initialisation (config.isInitialised does more than storing that info)
		this.chartInit = false;
		//Reset set node counter to zero
		setNodeCounter = 0;
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
		// array holding name of sub-movieclips of mcPieH
		var arrMcName:Array = ['mcPieHolder', 'mcLabelHolder'];
		for (var t:Number = 0; t<2; ++t) {
			// taking up a movieclip at a time
			var mc:MovieClip = mcPieH[arrMcName[t]];
			for (var k in mc) {
				if (mc[k] instanceof MovieClip) {
					clearInterval(mc[k].id);
					// clearing all event handlers from the movieclips to be removed
					delete mc[k].onRollOver;
					delete mc[k].onRollOut;
					delete mc[k].onRelease;
					delete mc[k].onReleaseOutside;
				}
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
	/**
	 * roundUp method is defined to round up to 2 decimal 
	 * places (else can be a source of error).
	 * @param	param	number to be rounded
	 * @returns			number rounded
	 */
	private function roundUp(param:Number):Number {
		param *= 100;
		param = Math.round(Number(String(param)));
		param /= 100;
		return param;
	}
	// ----------------- DATA READING, PARSING AND STORING -----------------//
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
						// pie fill opacity
						var setFillAlpha:Number = getFN(atts["alpha"], this.params.pieFillAlpha);
						// if this data is worth display in the chart
						if ((setValue != 0 || this.params.showZeroPies) && !isNaN(setValue) && setFillAlpha != 0) {
							//First, updated counter
							this.num++;
							var setLabel:String = getFV(atts["label"], atts["name"], "");
							//Get explicitly specified display value
							var setExDispVal : String = getFV( atts["displayvalue"], "");
							var setLink:String = getFV(atts["link"], "");
							var setToolText:String = getFV(atts["tooltext"], atts["hovertext"]);
							// string form of hexadecimal code stored, but in a form whose number equivalent can't be 
							// recognised by flash as hexadecimal
							var color:String = String(formatColor(getFV(atts["color"], this.defColors.getColor())));
							// hexadecimal color code stored for the pie
							var setColor:Number = parseInt(color, 16);
							// hexadecimal color code stored for the pie border; ColorExt.getDarkColor() was returning value in form
							// ultimately required by flash, but odd one of the 3, and thus worked upon. 
							var setBorderColor:Number = parseInt(formatColor(getFV(atts["bordercolor"], this.params.pieBorderColor, ColorExt.getLightColor(color, 0.25).toString(16))), 16);
							// pie border opacity
							var setBorderAlpha:Number = getFN(atts["borderalpha"], this.params.pieBorderAlpha);
							// initial slicing staus of the pie
							var setIsSliced:Boolean = toBoolean(getFN(atts["issliced"], 0));
							// if the pie border is dashed or not
							var setIsDashed:Boolean = toBoolean(getFN(atts["dashed"], 0));
							//Summing up the values
							this.config.sumOfValues += setValue;
							// Store all these attributes as object.
							// flag to be used to enable links for user interaction, initially and to keep the same option in the context menu
							this.config.linksDefined = (setLink.length>1) ? true : this.config.linksDefined;
							this.data[this.num-1] = this.returnDataAsObject(setLabel, setValue, setColor, color, setExDispVal, setBorderColor, setFillAlpha, setBorderAlpha, setToolText, setLink, setIsSliced, setIsDashed);
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
		this.params.palette = getFN(atts["palette"], 1);
		//Palette colors to use
		this.params.paletteColors = getFV(atts["palettecolors"],"");
		//Set palette colors before parsing the <set> nodes.
		this.setPaletteColors();
		//Background properties - Gradient
		this.params.bgColor = getFV(atts["bgcolor"], this.defColors.get2DBgColor(this.params.palette));
		this.params.bgAlpha = getFV(atts["bgalpha"], this.defColors.get2DBgAlpha(this.params.palette));
		this.params.bgRatio = getFV(atts["bgratio"], this.defColors.get2DBgRatio(this.params.palette));
		this.params.bgAngle = getFV(atts["bgangle"], this.defColors.get2DBgAngle(this.params.palette));
		//Border Properties of chart
		this.params.showBorder = toBoolean(getFN(atts["showborder"], 1));
		this.params.borderColor = formatColor(getFV(atts["bordercolor"], this.defColors.get2DBorderColor(this.params.palette)));
		this.params.borderThickness = getFN(atts["borderthickness"], 1);
		this.params.borderAlpha = getFN(atts["borderalpha"], this.defColors.get2DBorderAlpha(this.params.palette));
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
		// Decimal Precision (number of decimal places to be rounded to)
		this.params.decimals = getFN(atts["decimals"], atts["decimalprecision"], 2);
		// Force Decimal Padding
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
		
		//----------------------------//
		// shadow related params                                                                                                                                            
		this.params.showShadow = toBoolean(getFN(atts["showshadow"], 1));
		this.params.shadowColor = formatColor(getFV(atts["shadowcolor"], "666666"));
		this.params.shadowAlpha = getFN(atts["shadowalpha"], 100);
		this.params.shadowXShift = getFN(atts["shadowxshift"], 2);
		this.params.shadowYShift = getFN(atts["shadowyshift"], 2);
		// Pie related properties                                                                                                                                                                            
		this.params.pieRadius = Math.abs(getFN(atts["pieradius"], 0));
		this.params.doughnutRadius = Math.abs(getFN(atts["doughnutradius"], 0));
		this.params.startingAngle = getFN(atts["startingangle"], 0);
		// Border Properties
		this.params.showPlotBorder = toBoolean(getFN(atts["showplotborder"], 1));
		this.params.pieBorderThickness = getFN(atts["plotborderthickness"], atts["pieborderthickness"], 1);
		this.params.pieBorderAlpha = getFN(atts["plotborderalpha"], atts["pieborderalpha"], 20);
		//Validate pieBorderAlpha's existance with respect to showPlotBorder
		this.params.pieBorderAlpha = (this.params.showPlotBorder == true) ? this.params.pieBorderAlpha : 0;
		this.params.pieBorderColor = getFV(atts["plotbordercolor"], atts["piebordercolor"]);
		this.params.pieFillAlpha = getFN(atts["plotfillalpha"], atts["piefillalpha"], 100);
		// gradient fill
		this.params.gradientFill = toBoolean(getFN(atts["use3dlighting"], 1));
		var gradientRadius:Number = getFN(atts["radius3d"], atts["3dradius"], 50);
		if (gradientRadius>100) {
			gradientRadius = 100;
		} else if (gradientRadius<0) {
			gradientRadius = 0;
		}
		this.params.gradientRadius = Math.floor(gradientRadius*255/100);
		//Slicing distance - Indicates how far a pie will move out when clicked or, if by default the pie is sliced out
		this.params.slicingDistance = getFN(atts["slicingdistance"], 20);
		//Label Distance indicates the space (pixels)
		this.params.labelDistance = getFN(atts["labeldistance"], atts["nametbdistance"], 5);
		// the clearance distance of a label (for pie sliced in) from an adjacent sliced out pie
		this.params.smartLabelClearance = getFN(atts["smartlabelclearance"], 5);
		//Flag to set whether 0 pies (and their values) will be shown
		this.params.showZeroPies = toBoolean(getFN(atts["showzeropies"], 1));
		//Enable rotation on start - only for charts have no links in pie
		this.params.enableRotation = toBoolean(getFN(atts["enablerotation"], 0));
		//Attributes relating to Smart Label
		//Whether to enable smart labels
		this.params.enableSmartLabels = toBoolean(getFN(atts["enablesmartlabels"], atts["enablesmartlabel"], 1));
		//Skip Labels that are overlapping even when using smart labels
		this.params.skipOverlapLabels = toBoolean(getFN(atts["skipoverlaplabels"], atts["skipoverlaplabel"], 1));
		//Whether the smart lines are slanted
		this.params.isSmartLineSlanted = toBoolean(getFN(atts["issmartlineslanted"], 1));
		//whether to apply elipses when the label overflows.
		this.params.useEllipsesWhenOverflow = toBoolean(getFN(atts["useellipseswhenoverflow"], atts["useellipsewhenoverflow"],1));
		//Smart line cosmetic properties
		this.params.smartLineColor = String(formatColor(getFV(atts["smartlinecolor"], this.defColors.get2DPlotFillColor(this.params.palette))));
		this.params.smartLineThickness = getFN(atts["smartlinethickness"], 1);
		this.params.smartLineAlpha = getFN(atts["smartlinealpha"], 100);
		
		//Whether to apply wordWrap when the labels overlaps/Exceed boundary
		this.params.manageLabelOverflow = toBoolean(getFN(atts["managelabeloverflow"], 0));
		
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
	private function returnDataAsObject(dataLabel:String, dataValue:Number, color:Number, hexColor:String, displayValue:String, bordercolor:Number, fillAlpha:Number, borderAlpha:Number, toolText:String, link:String, isSliced:Boolean, isDashed:Boolean):Object {
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
		dataObj.fillAlpha = fillAlpha;
		dataObj.borderAlpha = borderAlpha;
		dataObj.toolText = toolText;
		dataObj.link = link;
		dataObj.isSliced = isSliced;
		dataObj.isDashed = isDashed;
		//dataObj.position is reserved for storing coordinates of the pie
		//Return the container
		return dataObj;
	}
	// ---------------- CALCULATION AND OPTIMIZATION -----------------//
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
		
		//----------------------------------------------//
		//
		//Calculate and/or evaluate and set those properties/params so required w.r.t width/height of the chart.
		this.calculateForWidthAndHeight()
		
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
			//
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
			
			//If not resizing
			if(!isRedraw){
				//Getting single line text height for legend items
				var iconHeight:Number = lgnd.getIconHeight()*this.params.legendIconScale;
				
				//Feed data set series Name for legend
				for (var i:Number = 0; i<this.num; i++) {
					
					var k:Number = (this.params.reverseLegend) ? this.num - 1 - i : i;
					
					if(!this.data[k].label){
						continue;
					}
					//Icon cosmetics
					var objIconParams:Object = {fillColor: parseInt(this.data[k].hexColor, 16)};
					//Getting icons returned
					var objIcons:Object = LegendIconGenerator.getIcons(LegendIconGenerator.WEDGE, iconHeight, false, objIconParams);	
					//Setting icons for the 2 states
					var bmpd1:BitmapData = objIcons.active;
					var bmpd2:BitmapData = objIcons.inactive;
					//Adding items to legend
					lgnd.addItem (this.data[k].label, k, (this.params.interactiveLegend)? !this.data[k].isSliced : true, bmpd1, bmpd2);
				}
			}else{
				//Resetting legend 'items' array to that of previous one
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
		//to set chart center coordinates
		// storing abscissa of the piechart centre w.r.t. mcPieH
		this.config.centerX = plotWidth/2;
		// storing ordinate of the piechart centre w.r.t. mcPieH
		this.config.centerY = plotHeight/2;
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
			//
			this.params.animation = false;
			// lesser of the two is taken
			var k:Number = Math.min(plotWidth, plotHeight);
			if (this.num != 1) {
				this.config.minRadiusAsFraction = 0.2;
				// 20 % of the former value (k) is the minimum limit of radius ... starting with this value
				// and incrementing untill the best and maximum value for radius is obtained
				this.config.radius = k*this.config.minRadiusAsFraction;
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
				this.config.minRadiusAsFraction = 0.2;
				// 20 % of the former value (k) is the minimum limit of radius ... starting with this value
				// and incrementing untill the best and maximum value for radius is obtained
				this.config.radius = k*this.config.minRadiusAsFraction;
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
		// counter to be incremented initially to track pie slicing out animation ends
		this.config.iniTracker = 0;
		// counter to be incremented initially to track label animation ends of pies
		this.config.iniFinishTracker = 0;
		// stores the number of pies to be sliced initially
		this.config.numSlicedPies = 0;
		// stores the number of pixels along the x-axis to manage the labels around
		this.config.relaxation = 0;
		// stores whether the chart is currently being recreated to render a diferent view (fine tuning)
		this.config.isStaticRecreation = false;
		// the array stores processed angles and certain other properties for the pie set
		this.config.arrMid = new Array();
		// the final multidimensional array for storing the processed data and properties for the pie set to be drawn
		this.config.arrFinal = new Array();
		// to store pie2D instances
		this.config.objDoughnut2D = new Object();
		// to enable links initially or not 
		this.config.enableLinks = this.config.linksDefined;
		// whether rotation is enabled
		this.config.enableRotation = (this.config.enableLinks) ? false : this.params.enableRotation;
		// boolean denoting whether the plot animations are over
		this.config.isPlotAnimationOver = false;
		// counter to track the number of times the drawChart method is called
		this.config.drawCounter = 0;
	}
	/**
	 * loadCurrentSlicingStatus method is called to update
	 * certain database to keep track of the slicing status of
	 * chart, so required for multiple redraw of the chart in
	 * different starting angles, but with same slicing status.
	 */
	private function loadCurrentSlicingStatus():Void {
		// iterating to get all movieclip instances in piechart holder movieclip in order to get their current slicing status 
		var mc:MovieClip = mcPieH.mcPieHolder;
		for (var p in mc) {
			if (mc[p] instanceof MovieClip) {
				// getting the slicing status
				var isSliced:Boolean = (mc[p].isSlicedIn) ? false : true;
				// location index of the pie in this.data is retrived to reset data 
				var index:Number = mc[p].store['id'];
				// data reset
				this.data[index].isSliced = isSliced;
				// to remove slight shift in location due recreation by right clicked rotation enabling (flashplayer 7.0)
				this.data[index].position = [mc[p]._x, mc[p]._y];
			}
		}
	}
	/**
	 * calculatePieProps method calculates for each pie,
	 * all angle parameters  w.r.t. the startingAngle
	 * obtained from XML, assigns color and are
	 * stored in a multidimensional array.
	 */
	private function calculatePieProps():Void {
		// length of this.data is stored
		var s:Number = this.data.length;
		var angleAdjustment:Number = this.config.startingAngle;
		// angleAdjustment is bounded between 0 to 360
		angleAdjustment = MathExt.boundAngle(angleAdjustment);
		// variable declared to store the sum total of all data set values obtained initially
		var totalValue:Number = 0;
		// sum totalling is done by looping in this.data
		for (var i:Number = 0; i<s; ++i) {
			// incrementing over previous value of totalValue
			totalValue += this.data[i].value;
		}
		// arrMid is referened in a short local variable
		var a:Array = this.config.arrFinal;
		// variable declared to store the updated unbounded cummulative angle for each pie under looping 
		var cummulativeAngle:Number = angleAdjustment;
		// looping to calculate and set angle (and few other) properties in sub-arrays in arrMid
		// all angle values are in degrees for now
		for (var i:Number = 0; i<s; ++i) {
			// sub-array created
			a[i] = new Array();
			// bounded start angle of the pie is stored
			a[i]['startAngle'] = MathExt.boundAngle(cummulativeAngle);
			// sweepAngle of pie is calculated with a checking over the last loop to avoid error due to
			// rounding approximation in using MathExt.boundAngle() which rounds to nearest twip
			var sweepAngle:Number = (i != s-1) ? (this.data[i].value/totalValue)*360 : toNT(360-(cummulativeAngle-angleAdjustment));
			// stores the number of 45 degrees curve drawings
			a[i]['no45degCurves'] = Math.floor(sweepAngle/45);
			// stores the remainder, if any, after the 45 degrees curve drawings are over
			a[i]['remainderAngle'] = MathExt.remainderOf(sweepAngle, 45);
			// cummulativeAngle is updated
			cummulativeAngle += sweepAngle;
			// bounded end angle of the pie is stored
			a[i]['endAngle'] = MathExt.boundAngle(cummulativeAngle);
			// storing sweep angle
			a[i]['sweepAngle'] = sweepAngle;
			// calculating bounded mean angle of the pie
			var meanAng:Number = MathExt.boundAngle(a[i]['startAngle']+a[i]['sweepAngle']/2);
			// number - mean angle of the pie is stored
			a[i]['meanAngle'] = meanAng;
			// storing hexadecimal color code of pie
			a[i]['pieColor'] = this.data[i].color;
			// storing hexadecimal color code of pie border
			a[i]['borderColor'] = this.data[i].borderColor;
			// pie fill opacity
			a[i]['fillAlpha'] = this.data[i].fillAlpha;
			// pie border opacity
			a[i]['borderAlpha'] = this.data[i].borderAlpha;
			// storing pie label (not used in this form to display text)
			a[i]['label'] = this.data[i].label;
			// text to be displayed in the label
			a[i]['labelText'] = this.data[i].labelText;
			// a[i]['labelProps'] is a sub-array (reserved) storing the coordinates for label and quadrant id
			// Boolean - stores whether the pie will be sliced initially
			a[i]['isSliced'] = this.data[i].isSliced;
			// whether to draw the pie with dashed border
			a[i]['isDashed'] = this.data[i].isDashed;
			// original index in this.data is stored (for recreation due rotation)
			a[i]['id'] = i;
			// coordinates of the pie slice movieclip (fine tuning) -- on recreation
			if (this.data[i].position) {
				a[i]['position'] = this.data[i].position;
			}
			// storing the link for the pie if any                                                                                                                                                                                                                        
			a[i]['link'] = this.data[i].link;
			a[i]['toolText'] = this.data[i].toolText;
		}
		//
		if (this.params.showNames || this.params.showValues) {
			if (this.params.enableSmartLabels) {
				// labels are managed to avoid overlap
				setSmartLabels();
				// else, labels are not managed to avoid overlap
			} else {
				setLabels();
			}
		}
		// private instance property is set to the length of arrFinal                                                                                                                                                              
		this.config.totalSlices = a.length;
		// till now, all angles were in degrees - so change the required ones (for drawing) to radians
		for (var i:Number = 0; i<this.config.totalSlices; ++i) {
			// start angle to radian
			a[i]['startAngle'] = MathExt.toRadians(a[i]['startAngle']);
			// remainder angle to radian
			a[i]['remainderAngle'] = MathExt.toRadians(a[i]['remainderAngle']);
		}
		// Its all ready for a final call to generate the pie chart.
	}
	/**
	 * setLabels method is called if smart labelling is not 
	 * required, to set position of the labels.
	 */
	private function setLabels():Void {
		var arrX:Array = this.config.arrFinal;
		// distance of the label from the periphery of the pie
		var outDisplacement:Number = this.params.labelDistance;
		// total distance from chart center
		var a:Number = this.config.radius+outDisplacement;
		// iterating to set label position of the pies one at a time
		for (var i:Number = 0; i<arrX.length; ++i) {
			// mean angle in degrees
			var m:Number = arrX[i]['meanAngle'];
			var meanAng:Number = MathExt.toRadians(m);
			// coordinates calculated for placing labels (polar coordinate calculation)
			var labelX:Number = toNT(this.config.centerX+a*Math.cos(meanAng));
			var labelY:Number = toNT(this.config.centerY+a*Math.sin(meanAng));
			// setting quadrant id for each pie label to be placed in
			var quadrantId:Number;
			if (m<=90) {
				quadrantId = 1;
			} else if (m>90 && m<=180) {
				quadrantId = 2;
				// lower quadrants need their labels be shifted down by d more to keep labels clear from the chart.
			} else if (m>180 && m<=270) {
				quadrantId = 3;
			} else {
				quadrantId = 4;
			}
			// positions along with quadrant id is stored in respective cells in sub-arrays for pies in arrFinal
			arrX[i]['labelProps'] = [labelX, labelY, quadrantId];
			
			// a special value of label type is set type1 = NonSmart labels - where only wordwrap applied on overflow.
			//always push .. as the 3rd index could also be used for icon
			arrX[i]['labelProps'].push("type1");
		}
	}
	/**
	 * setSmartLabels method is called at the end of 
	 * calculatePieProps() and also from checkBounds().
	 * It calculates the coordinates of the labels to be
	 * placed with the pies and stores them in the respective
	 * sub-arrays of arrFinal.
	 */
	private function setSmartLabels():Void {
		var d:Array = this.config.arrFinal;
		//to clearup all previous settings in the very initial phases due checkBounds for evaluating the best fit radius
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
		arr1.sortOn('meanAng', 16);
		arr2.sortOn('meanAng', 16);
		arr3.sortOn('meanAng', 16);
		arr4.sortOn('meanAng', 16);
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
		// parameters controlling the look of the dataLabels are obtained encapsulated in an object
		var objProp:Object = this.styleM.getTextStyle(this.objects.DATALABELS);
		//
		var fmtTxt:TextFormat = new TextFormat();
		fmtTxt.font = objProp.font;
		fmtTxt.size = objProp.size;
		fmtTxt.italic = objProp.italic;
		fmtTxt.bold = objProp.bold;
		fmtTxt.underline = objProp.underline;
		fmtTxt.letterSpacing = objProp.letterSpacing;
		//------------------------------------
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
		// radius of the chart
		var sb:Number = this.config.radius;
		// difference between the radii for sliced in and out conditions
		var xb:Number = this.params.slicingDistance;
		// the minimum length of relaxation for the smart labels from the periphery of the pies 
		// - to avoid overlap of label of a sliced-in pie with a sliced-out pie itself
		// - length of semi-minor axis of the reference ellipse for the labels must be atleast this plus sb defined above
		var extMin:Number = xb;
		// setting the length of semi-minor axis of labelling ellipse.
		// (f-1) because, f th label will just touch the top end of the reference ellipse
		var eb1:Number = Math.max(sb+extMin, (f-1)*h);
		// -h because, label touching the topmost point of reference ellipse must have atleast h space 
		// vertically between topmost extreme of the reference ellipse and the plot boundary
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
		// radius of piechart
		var a1:Number = this.config.radius;
		// length of semi-major axis of the reference ellipse for labels
		var a2:Number = this.config.radius+extension;
		// length of semi-minor axis of the reference ellipse for labels
		var b2:Number = b;
		// ordinate of the chart center
		var yCenter:Number = this.config.centerY;
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
				// eccentric angle w.r.t. reference ellipse for labels corresponding to meanAng of the pie
				var angX:Number = Math.atan(a2*Math.tan(meanAng)/b2);
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
						//  minus h ---- since , for first quadrant ... all txt fields are shifted upwards by h in instances of pie2D
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
			
			//re assign the objects in the main array..
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
		// radius of the piechart
		var a1:Number = this.config.radius;
		// lengths of semi major and minor axes of the reference label ellipse
		var a2:Number = this.config.radius+extension;
		var b2:Number = b;
		// ordinate of the piechart center
		var yCenter:Number = this.config.centerY;
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
		// radius of the piechart
		var a1:Number = this.config.radius;
		// lengths of semi major and minor axes of the reference label ellipse
		var a2:Number = this.config.radius+extension;
		var b2:Number = b;
		// ordinate of the piechart center
		var yCenter:Number = this.config.centerY;
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
	 */
	private function checkBounds():Void {
		// to hold the returned object
		var objMetrics:Object = mcPieH.getBounds(mcPieH);
		// checking if the aggregate of pies is contained in the plotWidth and plotHeight 
		if (objMetrics.xMin>0 && objMetrics.xMax<plotWidth && this.config.radius<0.4*plotWidth && this.config.radius<0.4*plotHeight) {
			// radius is incremented by 10%
			this.config.radius *= 1.1;
			this.config.radius = Math.round(this.config.radius);
			// set temporarily as a working value
			this.config.doughnutRadius = this.config.radius*0.5;
			// if the aggregate of pies is not contained in the plotWidth and plotHeight
		} else {
			//to avoid decrease of radius below the initial assigned value (ie. if chart overflows for the very first radius value)
			if (this.config.minRadiusAsFraction*Math.min(plotHeight, plotWidth)<this.config.radius) {
				// hence the last radius set is just exceeding the limit and hence decremented down by the same ratio to get the best fit radius
				this.config.radius /= 1.1;
				this.config.radius = Math.round(this.config.radius);
			}
			// flag updated to indicate that radius is set              
			this.config.isRadiusGiven = true;
			// flag updated to indicate that the chart will need initial animation ... was kept true to avoid wastage of time due animation during setting of radius
			// if initial animation is required
			if (this.params.animation) {
				this.config.isInitialised = false;
				// else, behave as if initial animation part is over
			} else {
				this.config.isInitialised = true;
			}
			//----------------------------------------------------------//
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
			//---------------------------------------------------------//
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
		drawChart();
	}
	/**
	 * getDragAngle is the method to calculate and return the
	 * the current angle of the mouse cursor w.r.t. the chart
	 * center. The angle is calculated in perspective sense.
	 * @param	m	number indicating the _root._xmouse
	 * @param	n	number indicating the _root._ymouse
	 * @returns		number denoting the eccentric angle
	 */
	public function getDragAngle(m:Number, n:Number):Number {
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
		// adjustment for Math.atan2 which returns angle between  -90 to 90 degrees (obviously in radians) only
		var addAngle:Number = (dx<0) ? Math.PI : 0;
		// formula applied
		var eccentricAngle:Number = 360-MathExt.boundAngle(MathExt.toDegrees(Math.atan(Math.tan(ellipticAngle))+addAngle));
		//
		return eccentricAngle;
	}
	// -------------- VISUAL RENDERING METHODS ---------------------//
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
	 * setInitialStatus is the method to set initial status
	 * of the pies, viz. slicing status, label position,
	 * smartlines, during redraw. Actually works for
	 * the pies sliced out only.
	 */
	private function setInitialStatus():Void {
		// difference between the (outer) radii of the 2 circles corresponding to the sliced in and out condition of the piechart
		var sa:Number = this.params.slicingDistance;
		// iterating on main piechart holder movieclip
		// array holding the name of the 2 sub-movieclips of mcPieH
		var arrMcName:Array = ['mcPieHolder', 'mcLabelHolder'];
		for (var t:Number = 0; t<2; ++t) {
			// to take up a sub-movieclip at a time
			var MC:MovieClip = mcPieH[arrMcName[t]];
			for (var g in MC) {
				// if instance of movieclip ,its a pie; works for the pies sliced out only (store['isSliced'] is true)
				if (MC[g] instanceof MovieClip && mcPieH.mcPieHolder[g].store['isSliced']) {
					// storing reference of the movieclip
					var _mc:MovieClip = MC[g];
					// mean angle stored in radians
					var meanAng:Number = MathExt.toRadians(_mc.store['meanAngle']);
					// the difference in abscissae for the same mean angle for the two concentric circles 
					// corresponding to the sliced in and out condition of the piechart
					var sx:Number = toNT(sa*Math.cos(meanAng));
					// the difference in ordinates for the same mean angle for the two concentric circles 
					// corresponding to the sliced in and out condition of the piechart
					var sy:Number = toNT(sa*Math.sin(meanAng));
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
				}
			}
		}
	}
	/**
	 * setInitialWatch is the method called to set a watchman 
	 * to monitor the overall initial animating process 
	 * sequencially.
	 */
	private function setInitialWatch():Void {
		// store the reference of this Doughnut2DChart instance
		var insRef:Doughnut2DChart = this;
		// to track if all the pies have completed their initial slicing movements, so that the next phase of
		// line drawing animations (if smartLabel enabled) can begin or else the text appearance animations
		this.objIntervalIds.idIniWatch = setInterval(function () {
			// checking over the updated flag 'iniTracker' against 'numSlicedPies', to know if the initial phase of 
			// pie slicing movements is over
			// numSlicedPies is multiplied by 2 since each pie is actually made up of 2 movieclips
			if (2*insRef.config.numSlicedPies<=insRef.config.iniTracker) {
				// resetting the counter to zero
				insRef.config.iniTracker = 0;
				// to monitor over end of the entire initial chart animation
				insRef.setFinalWatch();
				var index:Number = 0;
				// iterating over the elements in main piechart holder movieclip
				for (var g in insRef.mcPieH.mcLabelHolder) {
					// if its a movieclip, then its a pie; smartline and hence text animation is required for only
					// one of the conjugated pair ('isConjugated' is set 'true' for one and 'null' for the other)
					if (insRef.mcPieH.mcLabelHolder[g] instanceof MovieClip) {
						index++;
						// generating a unique string, to be stored as a property-name within an object storing all similar
						// returned setInterval ids
						var idx:String = 'id'+g;
						// assigning returned id in unique object property; animateLabel method is referenced to handle smartline (if 
						// smartLabel is enabled) and text animation
						insRef.objIntervalIds[idx] = setInterval(Delegate.create(insRef, insRef.animateLabel), 10, insRef.mcPieH.mcLabelHolder[g], idx, index);
					}
				}
				// this part of watching is over and action taken
				clearInterval(insRef.objIntervalIds.idIniWatch);
			}
		}, 20);
	}
	/**
	 * setFinalWatch is the method called from setInitialWatch() 
	 * to set a watchman to monitor the completion of the entire
	 * initial animating process and to act likewise. 
	 */
	private function setFinalWatch():Void {
		// store the reference of this Doughnut2DChart instance
		var insRef:Doughnut2DChart = this;
		this.objIntervalIds.idFinalWatch = setInterval(function () {
			// if entire initial chart animation is over
			if (insRef.config.numSlicedPies<=insRef.config.iniTracker) {
				// to update the current slicing status of the whole chart
				insRef.loadCurrentSlicingStatus();
				// chart recreated
				insRef.recreate(0, true);
				// this part of watching is over and action taken
				clearInterval(insRef.objIntervalIds.idFinalWatch);
			}
			// keeping this interval value lower than 500 ms may lead to error due to non-completion of previous processes.                                             
		}, 500);
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
		this.config.startingAngle = this.roundUp(this.config.startingAngle);
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
		
		
		// Create movie clip to hold pies and labels
		mcPieH = this.cMC.createEmptyMovieClip("pieMainHolder", this.dm.getDepth('DATAPLOT'));
		//Set it's X and Y
		mcPieH._x = this.plotX;
		mcPieH._y = this.plotY;
		// Create sub movieclip to hold labels
		var mcLabelHolder:MovieClip = mcPieH.createEmptyMovieClip("mcLabelHolder", 0);
		// Create sub movieclip to hold pies
		var mcPieHolder:MovieClip = mcPieH.createEmptyMovieClip("mcPieHolder", 1);
		// clear any pre-existing filters applied
		mcPieHolder.filters = [];
		// apply few selective filters on the whole pie holder movieclip (SHADOW and GLOW)
		this.styleM.applyFilters(mcPieHolder, this.objects.DATAPLOT, [this.styleM.TYPE.BEVEL, this.styleM.TYPE.BLUR]);
		//If there's only 1 pie, it takes a different course
		if (this.num == 1) {
			// no processing of single data set is required 
			// totalSlices set to zero for preventing mouse interaction of the pie
			this.config.totalSlices = 0;
			this.config.isPlotAnimationOver = true;
			// to avoid rotational interactivity
			this.config.enableRotation = false;
		}
		// final call to draw chart from this class instance                                                                                                                      
		drawChart();
		
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
	 * parseXml method. It creates  instances of Doughnut2D class,
	 * each for a sub-array in the multidimensional array 
	 * storage obtained after sortZ() is done. Each instance
	 * is passed a (common) object with a host of properties 
	 * in them, the movieclip reference in which to draw the 
	 * pie and the unique z-level for proper 2D presentation 
	 * of the pie set, mutually.
	 */
	private function drawChart():Void {
		//updating counter tracking the calls of this method
		this.config.drawCounter++;
		// all properties independent of data set are stored in an object instance to be passed as parameter
		var objProps:Object = {radius:this.config.radius, innerRadius:this.config.doughnutRadius, centerX:this.config.centerX, centerY:this.config.centerY, totalSlices:this.config.totalSlices, movement:this.params.slicingDistance, chartHeight:plotHeight, isRadius:this.config.isRadiusGiven, borderThickness:this.params.pieBorderThickness, smartLineColor:parseInt(this.params.smartLineColor, 16), smartLineThickness:this.params.smartLineThickness, smartLineAlpha:this.params.smartLineAlpha, gradientFill:this.params.gradientFill, gradientRadius:this.params.gradientRadius};
		// parameters controlling the look of the dataLabels are obtained encapsulated in an object
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
			// and call to instantiate the Doughnut2D class for creation of the specific pie slice
			for (var i:Number = 0; i<this.config.totalSlices; ++i) {
				// store the pie dependent properties as an array in the objProps
				objProps.arrFinal = this.config.arrFinal[i];
				// unique name of moviclip for this pie slice
				var strMcName:String = 'mcPie_'+i;
				// array holding the names of the sub-movieclips of mcPieH
				var arrMcName:Array = ['mcPieHolder', 'mcLabelHolder'];
				for (var t:Number = 0; t<arrMcName.length; ++t) {
					// flag to indicate Doughnut2D instance that its related mc is for holding pie graphic or label
					objProps.isPieHolder = (t == 0) ? true : false;
					// pre-existing movieclip removed, if any
					mcPieH[arrMcName[t]][strMcName].removeMovieClip();
					// new movieclip created and reference stored in private instance property
					var mcMain:MovieClip = mcPieH[arrMcName[t]].createEmptyMovieClip(strMcName, i);
					//
					// setting unique variable name to assign the Doughnut2D instance
					var strName:String = 'pie'+t+''+i;
					
					//
					// storing the index of the pie data in this.data within the movieclip
					mcMain.dataId = objProps.arrFinal.id;
					//
					// storing the string id by which the Doughnut2d instance is stored in config.objDoughnut2d, mapped to the index of the pie data in this.data
					this.map[objProps.arrFinal.id] = strName;
					
					// call to instantiate Doughnut2D passing references of this class , parent mc, movieclip to draw in
					// and all governing properties as and in an object
					this.config.objDoughnut2D[strName] = new Doughnut2D(this, mcMain, objProps, this.plotX, this.plotX + this.plotWidth);
					// storing references of the Doughnut2D instances of both the movieclips related to a pie, in the pie mc 
					mcMain.pie2dRef = this.config.objDoughnut2D['pie'+t+''+i];
					mcMain.pie2dTwinRef = this.config.objDoughnut2D['pie'+((t == 0) ? 1 : 0)+''+i];
					// apply few selective filters on the pie holder movieclip only(BEVEL and BLUR)
					if (t == 0) {
						this.styleM.applyFilters(mcMain.mcFace, this.objects.DATAPLOT, [this.styleM.TYPE.SHADOW, this.styleM.TYPE.GLOW]);
					}
					// for initial animation using StyleManager class                                                                                                                
					if (!this.config.isPlotAnimationOver && !this.config.isInitialised) {
						// subracted from 360 degrees because tween class used from StyleManager class 
						// is calculating up positioning elements clockwise
						var rotAng:Number = -this.config.arrFinal[i]['meanAngle'];
						// for chart appearance with  animating effects
						this.styleM.applyAnimation(mcMain, this.objects.DATAPLOT, this.macro, null, 0, null, 0, 100, null, null, rotAng);
					} else {
						// method called to set mouse event listener for this Doughnut2D instance
						setEventHandlers(mcMain, objProps, this.config.objDoughnut2D[strName]);
					}
				}
			}
			// at end of looping and pie set generation ...
			if (this.config.isRadiusGiven) {
				// a measure to have the dashed borders be not overlaped by other non-dashed pies
				for (var u in mcPieH.mcPieHolder) {
					if (mcPieH.mcPieHolder[u] instanceof MovieClip && mcPieH.mcPieHolder[u].store.isDashed) {
						mcPieH.mcPieHolder[u].swapDepths(mcPieH.mcPieHolder.getNextHighestDepth());
					}
				}
				// to set the initial status of sliced pies by rendering visually                                                                                        
				if (this.config.isInitialised) {
					setInitialStatus();
					// to fix a pie-slicing issue in initially formed chart (for given radius and no initial animation)
					if (this.config.drawCounter == 1) {
						var insRef:Doughnut2DChart = this;
						
						var mcIniWatch:MovieClip = this.mcPieH.createEmptyMovieClip('mcIniRenderCTRL', this.mcPieH.getNextHighestDepth());
						mcIniWatch.onEnterFrame = function(){
							insRef.drawChart();
							delete this.onEnterFrame;
							this.removeMovieClip();
						}
					}
					// to set for initial animation of the pies                     
				} else if (this.config.isPlotAnimationOver) {
					setInitialWatch();
					// else, it is in the very early stage of chart appearance with effects
				} else {
					var insRef:Doughnut2DChart = this;
					// returned animation time is assigned
					var plotTime:Number = this.styleM.getMaxAnimationTime(this.objects.DATAPLOT);
					// setting a post-appearance call of the chart generation leading to pie slicing animation
					this.objIntervalIds.plotId = setInterval(function () {
						clearInterval(insRef.objIntervalIds.plotId);
						
						var mcIniWatch:MovieClip = insRef.mcPieH.createEmptyMovieClip('mcIniRenderCTRL', insRef.mcPieH.getNextHighestDepth());
						mcIniWatch.onEnterFrame = function (){
							// this drawChart call is w.r.t. the isPlotAnimationOver set to true which leads to initial pie slicing animation
							insRef.config.isPlotAnimationOver = true;
							insRef.drawChart();
							delete this.onEnterFrame;
							this.removeMovieClip();
						}
					}, plotTime);
				}
				// If radius is not specified and not yet set too, then radius need to be set with top priority
			} else {
				// method called to set the pie sliced without animation for checking of bounds of the chart
				setInitialStatus();
				// method called to check and set radius for the next step
				checkBounds();
			}
		} else {
			// If a singleton case ... stores the pie dependent properties in the objProps
			// and call to instantiate the Doughnut2D class for creation of the only pie .
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
			objProps.arrFinal.meanAngle = 0;
			objProps.arrFinal.pieColor = this.data[0].color;
			objProps.arrFinal.borderColor = this.data[0].borderColor;
			objProps.arrFinal.fillAlpha = this.data[0].fillAlpha;
			objProps.arrFinal.borderAlpha = this.data[0].borderAlpha;
			objProps.arrFinal.labelText = this.data[0].labelText;
			objProps.arrFinal.labelProps = [xTxt, yTxt];
			objProps.arrFinal.link = this.data[0].link;
			objProps.arrFinal.toolText = this.data[0].toolText;
			objProps.arrFinal.isDashed = this.data[0].isDashed;
			
			// store whether to wrap labels if needed
			objProps.manageLabelOverflow = this.params.manageLabelOverflow;
			
			// flag to indicate Doughnut2D instance that its related mc is for holding pie graphic as well as label i.e. a singleton case
			objProps.isPieHolder = null;
			// unique name of moviclip for this pie slice
			var strMcName:String = 'mcPie';
			// new movieclip created and reference stored in private instance property
			var mcMain:MovieClip = mcPieH.mcPieHolder.createEmptyMovieClip(strMcName, 0);
			
			// call to instantiate Doughnut2D passing references of this class, movieclip to 
			// draw in and all governing properties in an object
			this.config.objDoughnut2D.pie = new Doughnut2D(this, mcMain, objProps, this.plotX, this.plotX + this.plotWidth);
			// method called to set mouse event listener for this Doughnut2D instance
			setEventHandlers(mcMain, objProps, this.config.objDoughnut2D.pie);
			// apply few selective filters on the pie movieclip (BEVEL and BLUR)
			this.styleM.applyFilters(mcMain.mcFace, this.objects.DATAPLOT, [this.styleM.TYPE.SHADOW, this.styleM.TYPE.GLOW]);
			// for chart appearance with  animating effects
			this.styleM.applyAnimation(mcPieH, this.objects.DATAPLOT, this.macro, null, 0, null, 0, 100, null, null, null);
			//
			var thisRef = this;
			// set a timeout function to handle chart initialisation end
			var idIniAnim:Number = setInterval(function () {
				// call function to securely handle and notify chart initialisation end
				thisRef.setInitWatch();
				// timeout
				clearInterval(idIniAnim);
				// timeout time is set to chart alpha transition time span
			}, this.styleM.getMaxAnimationTime(this.objects.DATAPLOT));
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
					
					//
					//Unblocking the legend interactivity after chart initialisation
					delete insRef.lgndMC.onRelease;
					
					// destroy the frame change function
					delete this.onEnterFrame;
					// remove the functional MC
					this.removeMovieClip();
					//
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
		// 'this.config.totalSlices' generates compile time error though it shouldn't
		// and actually works fine in runtime. Compiler assumes "this" as the class instance in which this method
		// exists, while "this" denotes the object instance whose property is set to watch.
		//------------------------------------//
		if (newVal>=insRef.config.totalSlices) {
			var _mc:MovieClip = insRef.cMC.createEmptyMovieClip('mc_exposeRendered', insRef.cMC.getNextHighestDepth());
			_mc.i = 0;
			_mc.onEnterFrame = function() {
				this.i++;
				if (this.i>10) {
					insRef.recreate(0, false);
					// to let all rendering be done, wait for the next frame.
					// call to notify initial chart rendering end
					insRef.exposeChartRendered();
					
					//
					//Unblocking the legend interactivity after chart initialisation
					delete insRef.lgndMC.onRelease;
					
					delete this.onEnterFrame;
					this.removeMovieClip();
					//
					//update the flag to note that chart is initialised (last function to be called)
					insRef.chartInit = true;
				}
			};
			
			// remove the watch
			this.unwatch(prop);
		}
		// update the counter/property with the new value    
		return newVal;
	}
	/**
	 * animateLabel is the method to regulate individual pie 
	 * unfolding labels by animation. This is called only
	 * for the label holder movieclip. So certain ending 
	 * actions for both the movieclips (for each pie)need
	 * to taken from here.
	 * @param	_mc		reference of the movieclip to animate in
	 * @param	strId	String denoting unique object property
	 *					name, storing the setInterval id for this method
	 *					call.
	 * @param	index	number denoting the index of the pie 
	 * 					w.r.t. arrFinal
	 */
	private function animateLabel(_mc:MovieClip, strId:String, index:Number):Void {
		// storing the reference of this Doughnut2DChart instance
		var insRef:Doughnut2DChart = this;
		// initialising counter to zero
		var tracker:Number = 0;
		// smart line (having 3 points viz. start,vertex,end) is drawn as follows:
		// 1. line from starting point (static) extends by animation to the final vertex point (multiple draw)
		// 2. if final vertex is achieved, line to end point is drawn in one shot (a small distance)
		// setting smartline animation
		this.objIntervalIds['id_label'+index] = setInterval(function () {
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
				// parameters controlling the look of the dataLabels are obtained encapsulated in an object
				var objTextProp:Object = insRef.styleM.getTextStyle(insRef.objects.DATALABELS);
				var fmtTxt:TextFormat = new TextFormat();
				fmtTxt.font = objTextProp.font;
				fmtTxt.size = objTextProp.size;
				fmtTxt.color = parseInt(objTextProp.color, 16);
				fmtTxt.italic = objTextProp.italic;
				fmtTxt.bold = objTextProp.bold;
				fmtTxt.underline = objTextProp.underline;
				fmtTxt.letterSpacing = objTextProp.letterSpacing;
				// enable the 2 movieclips for each pie under animation                                                                                                              
				//_mc.enabled = true;
				//_mc.pie2dTwinRef.mcMain.enabled = true;
				// updating isInitialised flag in the 2 Doughnut2D instances for each pie
				_mc.pie2dRef.objData.isInitialised = true;
				_mc.pie2dTwinRef.objData.isInitialised = true;
				
				// showing up the text field border if applicable
				if (objTextProp.borderColor != '') {
					_mc.mcLabel.label_txt.border = true;
					_mc.mcLabel.label_txt.borderColor = parseInt(objTextProp.borderColor, 16);
				}
				// showing up the text field bg if applicable                                                      
				if (objTextProp.bgColor != '') {
					_mc.mcLabel.label_txt.background = true;
					_mc.mcLabel.label_txt.backgroundColor = parseInt(objTextProp.bgColor, 16);
				}
				
				var quadrantId:Number = _mc.store['labelProps'][2];
				if(quadrantId == 2 || quadrantId == 3 ){
					fmtTxt.align = 'right';
				}
				
				if(insRef.params.manageLabelOverflow){
					//To tackle the intermittent case of label getting undefined value initially (actually not showing up label at this point, which goes imperceptible)
					if(_mc.labelTxt != undefined){
						_mc.mcLabel.label_txt.text = _mc.labelTxt;
					}
				} else {
					_mc.mcLabel.label_txt.text = _mc.store.labelText;
				}
				
				// filters applied to text field
				insRef.styleM.applyFilters(_mc.mcLabel.label_txt, insRef.objects.DATALABELS);
				// text is formatted
				_mc.mcLabel.label_txt.setTextFormat(fmtTxt);
				// to intimate the updated status to a setInterval function called to check over the entire completion of chart initiation
				insRef.iniTrackerUpdate();
				// update counter for label animation end for this pie
				insRef.config.iniFinishTracker++;
				
				// line animation is over
				clearInterval(insRef.objIntervalIds[['id_label'+index]]);
			}
		}, 50);
		// this method call is due to setInterval, but to run only once
		clearInterval(objIntervalIds[strId]);
	}
	// --------------- UTILITY METHODS --------------- //
	/**
	 * iniTrackerUpdate method is called from Doughnut2D instances
	 * on their initial slicing animation end (if isSliced is
	 * true) update a counter. This is also called by all 
	 * slices on their initial label generation completion.
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
	 * @param	Doughnut2DRef	reference of the Doughnut2D instance associated
	 *						with the pie
	 */
	private function setEventHandlers(_mc:MovieClip, objProps:Object, Doughnut2DRef):Void {
		var Doughnut2DInsRef = Doughnut2DRef;
		var strLink:String = objProps.arrFinal.link;
		var insRef:Doughnut2DChart = this;
		//---------------------
		var fnRollOver:Function;
		//Create Delegate for RollOver function pieOnRollOver
		fnRollOver = Delegate.create(this, pieOnRollOver);
		//Set the mc
		fnRollOver.mc = _mc;
		//Set the link
		fnRollOver.link = strLink;
		//Assing the delegates to movie clip handler
		_mc.onRollOver = fnRollOver;
		//---------------------
		var fnRollOut:Function;
		//Create Delegate for RollOut function pieOnRollOut
		fnRollOut = Delegate.create(this, pieOnRollOut);
		//Set the mc
		fnRollOut.mc = _mc;
		//Assing the delegates to movie clip handler
		_mc.onRollOut = fnRollOut;
		_mc.onReleaseOutside = fnRollOut;
		//---------------------
		if (this.params.clickURL == '') {
			if (this.config.enableLinks && objProps.arrFinal.link != '') {
				var fnRelease:Function;
				//Create Delegate for onRelease function pieOnClick
				fnRelease = Delegate.create(this, pieOnClick);
				//Set the link
				fnRelease.link = strLink;
				//Assing the delegates to movie clip handler
				_mc.onRelease = fnRelease;
			}
			//---------------------                                                                                                                                                                                                                
			if (!(this.config.enableRotation || this.config.enableLinks || this.config.totalSlices == 0)) {
				_mc.onRelease = function(){
					// if legend be there in the chart
					if(insRef.params.showLegend && insRef.params.interactiveLegend && insRef.data[this.dataId].label){
						// slice/unslice the pie via legend which takes care of all management issues
						insRef.lgnd.clickEvent(this.dataId);
					// else if legend is not there in the chart	
					}else{
						// ask the Doughnut2D instance to move the pie 
						Doughnut2DInsRef.movePie();
					}
				}
			}
		}
		
		//
		if(this.config.totalSlices > 1){
			//Add listener for "slicing" event from Doughnut2D objects for slice movement start or end.
			Doughnut2DInsRef.addEventListener('slicing', this);
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
		if(this.config.totalSlices > 1 && !this.rotating && this.chartInit){
			// if legend be there in the chart
			if(this.params.showLegend && this.params.interactiveLegend && this.data[id].label){
				// slice/unslice the pie via legend which takes care of all management issues
				this.lgnd.clickEvent(id);
			// else if legend is not there in the chart	
			}else{
				// get the reference of the Doughnut2D instance for the pie
				var Doughnut2DInsRef:Doughnut2D = this.config.objDoughnut2D[this.map[id]];
				// call to move the pie
				Doughnut2DInsRef.movePie();
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
		// reference of the mc for which tooltip text will be shown
		var _mc:MovieClip = arguments.caller.mc;
		// link as defined for the mc is stored 
		var strLink:String = arguments.caller.link;
		// text to be displayed in tooltip
		var strDisplay:String = _mc.store['toolText'];
		// if tooltip have something for display and tooltip display is enabled
		if (strDisplay != '' && this.params.showToolTip) {
			// called to show tooltip text
			this.tTip.setText(strDisplay);
			this.tTip.show();
		}
		// mc is disabled if link for the mc is irrelevant                                                      
		if ((strLink == '' || strLink == undefined || !this.config.enableLinks) && this.params.clickURL == '') {
			_mc.useHandCursor = false;
		}
		// mouse move actions are set as reference to function/method                                                      
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
		if (this.config.enableRotation && this.numSlicing == 0) {
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
		if (this.config.enableRotation) {
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
			this.config.objDoughnut2D[this.map[target.index]].movePie();
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
		//   
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
		// instantiating ContextMenuItem for each menu item     
		cmiRotation = new ContextMenuItem("Enable Rotation", rotationHandler, false, isRotationCMIEnabled);
		cmiRotation.separatorBefore = true;
		cmiSlicing = new ContextMenuItem("Enable Slicing Movement", movementHandler, false, isSlicingCMIEnabled);
		// "Enable Links" will be available if and only if link is defined for atleast one pie.
		if (this.config.linksDefined) {
			cmiLink = new ContextMenuItem("Enable Links", linkHandler, false, isLinkCMIEnabled);
		}
		// context menu options available if not a singleton chart                                                                                
		if (this.num != 1 && this.params.clickURL == '') {
			// inclusion of the items in the custom items section of context menu    
			cmCustom.customItems.push(cmiRotation);
			cmCustom.customItems.push(cmiSlicing);
			if (this.config.linksDefined) {
				cmCustom.customItems.push(cmiLink);
			}
		}
		// Doughnut2DChart instance reference is stored                                                                                                                                                                                                                        
		var instanceRef = this;
		// functions invoked due selection of the menu items are defined
		function linkHandler(obj, item) {
			//
			if(instanceRef.numSlicing != 0 || instanceRef.rotating){
				return;
			}
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
		function movementHandler(obj, item) {
			//
			if(instanceRef.numSlicing != 0 || instanceRef.rotating){
				return;
			}
			instanceRef.cmiRotation.enabled = true;
			instanceRef.cmiSlicing.enabled = false;
			instanceRef.cmiLink.enabled = true;
			instanceRef.config.enableLinks = false;
			instanceRef.config.enableRotation = false;
			//
			instanceRef.loadCurrentSlicingStatus();
			instanceRef.recreate(0, true);
		}
		function rotationHandler(obj, item) {
			//
			if(instanceRef.numSlicing != 0){
				return;
			}
			instanceRef.cmiRotation.enabled = false;
			instanceRef.cmiSlicing.enabled = true;
			instanceRef.cmiLink.enabled = true;
			instanceRef.config.enableLinks = false;
			instanceRef.config.enableRotation = true;
			//
			instanceRef.loadCurrentSlicingStatus();
			instanceRef.recreate(0, true);
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