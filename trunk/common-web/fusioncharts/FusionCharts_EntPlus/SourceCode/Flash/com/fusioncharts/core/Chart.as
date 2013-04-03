/**
* @class Chart
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* Chart class is the super class for a FusionCharts chart from
* which individual chart classes inherit. The chart class is
* responsible for a lot of features inherited by child classes.
* Chart class also bunches code that is used by all other charts
* so as to avoid code duplication.
*/
//Utilities
import com.fusioncharts.helper.Utils;
//Log class
import com.fusioncharts.helper.Logger;
//Enumeration class
import com.fusioncharts.helper.FCEnum;
//String extension
import com.fusioncharts.extensions.StringExt;
//Color Extension
import com.fusioncharts.extensions.ColorExt;
//Math Extension
import com.fusioncharts.extensions.MathExt;
//Drawing Extension
import com.fusioncharts.extensions.DrawingExt;
//Custom Error Object
import com.fusioncharts.helper.FCError;
//Tool Tip
import com.fusioncharts.helper.ToolTip;
//Style Managers
import com.fusioncharts.core.StyleObject;
import com.fusioncharts.core.StyleManager;
//Default Colors Class
import com.fusioncharts.helper.DefaultColors;
//Macro Class
import com.fusioncharts.helper.Macros;
//Depth Manager
import com.fusioncharts.helper.DepthManager;
//Delegate
import mx.utils.Delegate;
//Class to help as saving as image
import com.fusioncharts.helper.BitmapSave;
//Progress bar
import com.fusioncharts.helper.FCProgressBar;
//External Interface - to expose methods via JavaScript
import flash.external.ExternalInterface;
//Event Dispatcher
import mx.events.EventDispatcher;
//Drop-shadow filter
import flash.filters.DropShadowFilter;
//Object Manager
import com.fusioncharts.helper.ObjectManager;
//Import ImageUtils class
import com.fusioncharts.helper.ImageUtils;

//Class Definition
class com.fusioncharts.core.Chart 
{
	//Instance properties
	//Version
	private var _version : String = "3.2.2(XT)";
	//XML data object for the chart.
	private var xmlData : XML;
	//Stores raw XML string (as passed to chart or loaded from URL)
	//This string is stored before parsing XML. So all XML entities
	//retain their original value.
	private var rawXMLString:String;
	//Array and Enumeration listing charts objects
	//arrObjects array would store the list of chart
	//objects as string. The motive is to retrieve this
	//string information to be added to log.
	public var arrObjects : Array;
	//Object Enumeration stores the above array elements
	//(chart objects) as enumeration, so that we can refer
	//to each chart element as a numeric value.
	public var objects : FCEnum;
	//Object to store chart elements
	private var elements : Object;
	//Stores a list of all the ExternalInterface methods provided by the chart.
	private var extInfMethods:String;
	//Object to store chart parameters
	//All attributes retrieved from XML will be stored in
	//params object.
	private var params : Object;
	//Object to store chart configuration
	//Any calculation done by our code will be stored in
	//config object. Or, if we over-ride any param values
	//we store in config.
	private var config : Object;
	//DepthManager instance. The DepthManager class helps us
	//allot and retrieve depths of various objects in the chart.
	private var dm : DepthManager;
	//Movie clip in which the entire chart will be built.
	//If chart is not being loaded into another Flash movie,
	//parentMC is set as _root (as we need only 1 chart per
	//movie timeline).
	private var parentMC : MovieClip;
	//Movie clip reference for actual chart MC
	//All chart objects (movie clips) would be rendered as
	//sub-movie clips of this movie clip.
	private var cMC : MovieClip;
	//Movie clip reference for log MC. The logger elements
	//are contained as a part of this movie clip. Even if the
	//movie is not in debug mode, we create at least the
	//parent log movie clip.
	private var logMC : MovieClip;
	//Movie clip reference for tool tip. We created a separate
	//tool tip movie clip because of two reasons. One, tool tip
	//always appears above the chart. So, we've created tool tip
	//movie clip at a depth greater than that of cMC(chart movie
	//clip). Secondly, the tool tip is not an integral part of
	//chart - it's a helper class.
	private var ttMC : MovieClip;
	//Movie clip reference to hold any overlay logo for the chart.
	private var logoMC : MovieClip;
	//Movie clip loader for the logo.
	private var logoMCLoader:MovieClipLoader;
	//Listener object for the logo MC
	private var logoMCListener : Object;
	//Movieclip for background SWF 
	private var bgSWFMC:MovieClip;
	//Movie clip loader for the background swf.
	private var bgSWFMCLoader:MovieClipLoader;
	//Listener object for the backbround SWF MC
	private var bgSWFMCListener : Object;	
	//Movie clip reference to hold overlay button link
	private var btnMC:MovieClip;
	//Storage for overlay button parameters
	private var overlayButtonParams:Object;
	//Tool Tip Object. This object is common to all charts.
	//Whenever we need to show/hide tool tips, we called methods
	//of this class.
	private var tTip : ToolTip;
	//Flag that stores whether the chart should be animated by default. This
	//attribute is set from FlashVars. This attribute over-rides the chart's 
	//animation attribute provided in XML. So, if this attribute is set to
	//false and animation in XML is set to true, the chart will ignore the value
	//set in XML.
	private var defaultGlobalAnimation:Number;
	//Movie clip reference for text box which will be used to determine
	//text width, height for various text fields. During calculation
	//of points (width/height) for chart, we need to simulate various
	//text fields so that we come to know their exact width/height.
	//Based on that, we accomodate other elements on chart. This
	//movie clip is the container for that test text field movie clip.
	//This text field never shows on the chart canvas.
	private var tfTestMC : MovieClip;
	//Co-ordinates for generating test TF
	//We put it outside stage so that it is never visible.
	private var testTFX : Number = - 2000;
	private var testTFY : Number = - 2000;
	//Reference to object maanger
	private var objM:ObjectManager;
	//[Deprecated] Embedded Font
	//Denotes which font is embedded as a part of the chart. If you're
	//loading the chart in your movie, you need to embed the same font
	//face (plain - not bold - not italics) in your movie, to enable
	//rotated labels. Else, the rotated labels won't show up at all.
	//[Deprecated] - As we not longer used embedded fonts. Instead,
	//bitmapdata is used.
	//private var _embeddedFont : String = "Verdana";
	//Reference to logger class instance.
	private var lgr;
	//Depth in parent movie clip in which we've to create chart
	//This is useful when you are loading this chart class as a part
	//of your Flash movie, as then you can create various charts at
	//various depths of a single movie clip. In case of single chart
	//(non-load), this is set to 3 (as 1 and 2 are reserved for global
	//progress bar and global application text).
	private var depth : Number;
	//Width & Height of chart in pixels. If the chart is in exactFit
	//mode, the width and height remains the same as that of original
	//document (.fla). However, everything is scaled in proportion.
	//In case of noScale, these variables assume the width and height
	//provided either by chart constructor (when loading chart in your
	//flash movie) or HTML page.
	private var width : Number, height : Number;
	//X and Y Position of top left of chart. When loading the chart in
	//your flash movie, you might want to shift the chart to particular
	//position. These x and y denote that shift.
	private var x : Number, y : Number;
	//Debug mode - Flag whether the chart is in debug mode. It's passed
	//from the HTML page as OBJECT/EMBED variable debugMode=1/0.
	private var debugMode : Boolean;
	//Counter to store timeElapsed. The chart animates sequentially.
	//e.g., the background comes first, then canvas, then div lines.
	//So, we internally need to keep a track of time passed, so that
	//we can call next what to render.
	private var timeElapsed : Number = 0;
	//Language for application messages. By default, we show application
	//messages in English. However, if you need to define your application
	//messages, you can do so in com\fusioncharts\includes\AppMessages.as
	//This value is passed from HTML page as OBJECT/EMBED variable.
	private var lang : String;
	//Scale mode - noScale or exactFit.
	//This value is passed from HTML page as OBJECT/EMBED variable.
	private var scaleMode : String;
	//Is Online Mode. If the chart is working online, we avoid caching
	//of data. Else, we cache data.
	private var isOnline : Boolean;
	//Style Manager object. The style manager object handles the style
	//quotient (FONT, BLUR, BEVEL, GLOW, SHADOW, ANIMATION) of different
	//elements of chart.
	private var styleM : StyleManager;
	//Macros container. Macros help the user define pre-defined dynamic
	//values in XML for setting animation position.
	private var macro : Macros;
	//Storage for default color class. If the user doesn't specify
	//colors in XML for his data, we've a list of colors which we can
	//choose from. DefaultColors class helps us cyclic iterate through
	//the same.
	public var defColors : DefaultColors;
	//State whether default colors uses 2D values
	private var defColors2D:Boolean;	
	//Store a short name reference for Utils.getFirstValue function
	//and Utils.getFirstNumber function
	//As we'll be using this function a lot.
	private var getFV : Function;
	private var getFN : Function;
	//Short name for ColorExt.formatHexColor function
	private var formatColor : Function;
	//Short name for Utils.createText function
	private var createText : Function;
	//Short name for Utils.getTextMetrics function
	private var getTextMetrics : Function;
	//Error handler. We've a custom error object to represent
	//any chart error. All such errors get logged and none are visible
	//to end user, to make their experience smooth.
	var e : FCError;
	//Whether to register chart with JS
	private var registerWithJS:Boolean;
	//DOM Id
	private var DOMId:String;
	//Flag to indicate whether we've conveyed the chart rendering event
	//to JavaScript and loader Flash
	private var renderEventConveyed:Boolean = false;
	//Flag to indicate whether the chat has finished rendering.
	private var chartRendered:Boolean = false;
	//Flag to indicate whether the chart capture process is on
	private var exportCaptureProcessOn:Boolean = false;
	//Flag to indicate whether a resize even is queued or not.
	private var resizeQueued:Boolean = false;
	//Flag to indicate whether chart automatic resize is locked
	private var resizeLocked:Boolean = false;
	//Flag to indicate whether drawing is complete
	private var chartDrawn:Boolean = false;
	//Count of how many times the chart has drawn
	private var chartDrawCount:Number = 0;
	//Text field to hold application messages.
	private var tfAppMsg : TextField;
	//Variable to store the maximum number of decimal places in any given
	//data
	private var maxDecimals : Number = 0;
	//Global variable to store the corner radius of 2D bars/columns.
	private var roundEdgeRadius:Number = 6;
	//Global object references pertaining to export chart dialog box
	//The movie clip encompassing dialog box
	private var exportDialogMC:MovieClip;
	//The text field showing progress
	private var exportDialogTF:TextField;
	//The progree bar showing progress
	private var exportDialogPB:FCProgressBar;
	//Instance of BitmapSave object used for exporting chart
	private var bmpS:BitmapSave;
	//Instance of BitmapSave object used for exporting chart for printing
	private var bmpPrintExport:BitmapSave;
	//Arrays to store cache of objects during exporting. These are arrays
	//used to keep track of objects for which we alter bitmap state at 
	//run-time, so that interactivity is not lost.
	private var exportPrintObjCache:Array;
	private var exportObjCache:Array;
	//Global listener for export handler
	private var exportListenerObj:Object;
	//Export params object that contains parameters for exporting. This is
	//passed post export to the exportProcess handler.
	private var exportParamsObj:Object;
	//Timer as to when the app was last resized
	private var lastResized:Number;
	//Minimum number of characters to show in wrap mode, before
	//switch to alternate mode
	private var WRAP_MODE_MIN_CHARACTERS:Number = 5;
	//Dynamic resize time constant - between two successive resize calls
	//in milli-seconds
	private var RESIZE_DELAY:Number = 1000;
	//boolean to track wether the explicitly defined canvas margins are valid
	public var validCanvasLeftMargin:Boolean = false;
	public var validCanvasRightMargin:Boolean = false;
	/**
	* Constructor method for chart. Here, we store the
	* properties of the chart from constructor parameters
	* in instance variables.
	* @param	targetMC	Parent movie clip reference in which
	*						we'll create chart movie clips
	* @param	depth		Depth inside parent movie clip in which
	*						we'll create chart movie clips
	* @param	width		Width of chart
	* @param	height		Height of chart
	* @param	x			x Position of chart
	* @param	y			y Position of chart
	* @param	debugMode	Boolean value indicating whether the chart
	*						is in debug mode.
	* @param	lang		2 Letter ISO code for the language of application
	*						messages
	* @param	scaleMode	Scale mode of the movie - noScale or exactFit
	*/
	function Chart (targetMC : MovieClip, depth : Number, width : Number, height : Number, x : Number, y : Number, debugMode : Boolean, lang : String, scaleMode : String, registerWithJS : Boolean, DOMId : String)
	{
		//Get the reference to Utils.getFirstValue
		this.getFV = Utils.getFirstValue;
		//Get the reference to getFirstNumber
		this.getFN = Utils.getFirstNumber;
		//Get reference to ColorExt.formatHexColor
		this.formatColor = ColorExt.formatHexColor;
		//Get reference to Utils.createText
		this.createText = Utils.createText;
		//Get reference to Utils.getTextMetrics
		this.getTextMetrics = Utils.getTextMetrics;
		//Store properties in instance variables
		this.parentMC = targetMC;
		this.depth = depth;
		this.width = width;
		this.height = height;
		this.x = getFN (x, 0);
		this.y = getFN (y, 0);
		this.debugMode = getFV (debugMode, false);
		this.lang = getFV (lang, "EN");
		this.scaleMode = getFV (scaleMode, "noScale");
		this.registerWithJS = getFV(registerWithJS, false);
		this.DOMId = getFV(DOMId, "");
		//Initialize parameter storage object
		this.params = new Object ();
		//Initialize chart configuration storage object
		this.config = new Object ();
		//Object to store chart rendering intervals
		this.config.intervals = new Object ();
		//Elements object to store the various elements of chart.
		this.elements = new Object ();
		//Initialize style manager
		this.styleM = new StyleManager (this);
		//Initialize default colors class
		this.defColors = new DefaultColors ();
		//Initialize Macros
		this.macro = new Macros ();
		//Initialize Depth Manager
		this.dm = new DepthManager (0);
		//Initialize object manager
		this.objM = new ObjectManager();
		//Movie clip loader for the logo.
		this.logoMCLoader = new MovieClipLoader();
		//Listener object for the logo MC
		this.logoMCListener = new Object();
		//Movie clip loader for the background swf MC
		this.bgSWFMCLoader = new MovieClipLoader();
		//Listener object for the background swf MC
		this.bgSWFMCListener = new Object();
		//When the chart has started, it is not in export capture process
		this.exportCaptureProcessOn = false;
		//No resize is queued at init
		this.resizeQueued = false;
		//Chart has not drawn
		this.chartDrawn = false;
		//Chart should animate by default
		this.defaultGlobalAnimation = 1;
		//Raw XML string - empty by default
		this.rawXMLString = "";
		//Record the external interfaces exposed by this chart
		this.extInfMethods = "saveAsImage,print,exportChart,getXML,getChartAttribute,getDataAsCSV,hasRendered,signature,cancelExport";
		//Set export listener functions
		this.setExportListeners();
		// ----- CREATE REQUIRED MOVIE CLIPS NOW -----//
		//Create the chart movie clip container
		this.cMC = parentMC.createEmptyMovieClip ("Chart", depth + 1);
		//Re-position the chart Movie clip to required x and y position
		this.cMC._x = this.x;
		this.cMC._y = this.y;
		//Create movie clip for tool tip
		this.ttMC = parentMC.createEmptyMovieClip ("ToolTip", depth + 3);
		//Initialize tool tip by setting co-ordinates and span area
		this.tTip = new ToolTip (this.ttMC, this.x, this.y, this.width, this.height, 8);
		//Text-field test movie clip
		this.tfTestMC = parentMC.createEmptyMovieClip ("TestTF", depth + 4);
		//Logo holder - Setting at depth 100000 in cMC
		this.logoMC  = this.cMC.createEmptyMovieClip ("Logo", 100000);
		//We do NOT reposition logoMC/btnMC to x,y here as it's done during rendering.
		//Create the movie clip holder for log
		this.logMC = parentMC.createEmptyMovieClip ("Log", depth + 2);
		//Re-position the log Movie clip to required x and y position
		this.logMC._x = this.x;
		this.logMC._y = this.y;
		//Create the log instance
		this.lgr = new Logger (logMC, this.width, this.height);
		if (this.debugMode)
		{
			/**
			* If the chart is in debug mode, we:
			* - Show log.
			*/
			//Log the chart parameters
			this.log ("Info", "Chart loaded and initialized.", Logger.LEVEL.INFO);
			this.log ("Initial Width", String (this.width) , Logger.LEVEL.INFO);
			this.log ("Initial Height", String (this.height) , Logger.LEVEL.INFO);
			this.log ("Scale Mode", this.scaleMode, Logger.LEVEL.INFO);
			this.log ("Debug Mode", (this.debugMode == true) ? "Yes" : "No", Logger.LEVEL.INFO);
			this.log ("Application Message Language", this.lang, Logger.LEVEL.INFO);
			//Now show the log
			lgr.show ();
		}
		if (this.registerWithJS==true && ExternalInterface.available){
			//Expose image saving functionality to JS. 
			ExternalInterface.addCallback("saveAsImage",this, exportTriggerHandlerJS);
			//Expose the methods to JavaScript using ExternalInterface		
			ExternalInterface.addCallback("print", this, printChart);
			//Export chart as image
			ExternalInterface.addCallback("exportChart", this, exportTriggerHandlerJS);
			//For cancellation of export
			ExternalInterface.addCallback("cancelExport", this, cancelExport);			
			//Get chart's image date
			ExternalInterface.addCallback("getImageData", this, exportTriggerHandlerGI);
			//Get chart's image data stream
			ExternalInterface.addCallback("prepareImageDataStream", this, prepareImageDataStream);
			//Returns the XML data of chart
			ExternalInterface.addCallback("getXML", this, returnXML);
			//Returns the attribute of a specified chart element
			ExternalInterface.addCallback("getChartAttribute", this, returnChartAttribute);
			//Returns the data of chart as CSV/TSV
			ExternalInterface.addCallback("getDataAsCSV", this, exportChartDataCSV);
			//Returns whether the chart has rendered
			ExternalInterface.addCallback("hasRendered", this, hasChartRendered);
			//Returns the signature of the chart
			ExternalInterface.addCallback("signature", this, signature);
			//Draws the overlay button
			ExternalInterface.addCallback("drawOverlayButton", this, drawOverlayButton);
			//Returns the external interface APIs
			ExternalInterface.addCallback("getExternalInterfaceMethods", this, getExternalInterfaceMethods);
			//Externally forced resize
			ExternalInterface.addCallback("resize", this, forceResize);
			//Lock resize
			ExternalInterface.addCallback("lockResize", this, lockResize);			
		}
		//Initialize EventDispatcher to implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
		//Set the stage resize handlers for dynamic resizing
		Stage.addListener(this);		
	}
	
	/**
	 * Forward declaration for actual rendering method of chart.
	*/
	public function render():Void{
		//Empty. To be implemented by concreted classes.
	}
	
	/**
	 * Resize handler for the stage. Whenever the Flash object is resized, this 
	 * is invoked.
	*/
	private function onResize(){
		//Check if this chart responds to dynamic resizing
		if (this.config.allowDynamicResize==undefined || this.config.allowDynamicResize!=false){
			//Only resize if not locked
			if (resizeLocked==false){
				//Do not resize when the chart is exporting
				if (this.exportCaptureProcessOn==false){
					//Whenever we stage is resized, we do not instantly react. This is to optimize
					//the re-draw. Instead, we see that if the last size and current size have been same
					//for a given span of time (RESIZE_DELAY), we then re-draw. This lets us re-draw only
					//when resizing is complete, and not mid-way.
					//Check if stage width and height have assumed proper values
					if (Stage.width!=0 && Stage.height!=0){
						//Set the last resized time to current time
						lastResized = getTimer();
						//Set up an Enterframe handler to check for timing of resize. This will let only
						//the last resize handler (i.e., acceptable delay between this call and previous
						//call) invoke the resize handler.
						this.cMC.onEnterFrame = Delegate.create(this, resizeEnterFrameHandler);
					}
				}else{
					//Queue the resize event
					resizeQueued = true;
				}
			}
		}else{			
			//Remove the listener.
			Stage.removeListener(this);
		}
	}
	
	/**
	 * Invoked during enter-frame of this Sprite. The enter-frame is set whenever
	 * stage resize is invoked. Here, we check if the user is still resizing the stage
	 * (by checking time duration between current and last resize). If the user is still
	 * resizing, we do not resize the chart and logger. Else, we do.
	 */
	private function resizeEnterFrameHandler():Void{
		//If we detect that resizing has stopped, actually resize
		if ((getTimer()-lastResized)>RESIZE_DELAY){				
			//Clear all existing intervals
			var i:Object;
			for (i in this.config.intervals){
				//Clear the interval
				clearInterval(this.config.intervals[i]);
			}
			//Delete the enter frame handler
			delete this.cMC.onEnterFrame;
			//Set back last resized to -1
			lastResized = -1;
			//Call to resize
			forceResize(Stage.width, Stage.height);
		}
	}
	
	/**
	 * Implementation that actually resizes the chart.
	*/
	private function forceResize(w:Number, h:Number):Void{		
		//Resize if the values are different from existing dimensions
		if (!isNaN(w) && !isNaN(h) && (w!=width || h!=height)){
			//Log the same
			log("Chart Resized", "Resizing chart to " + w + " x " + h + " pixels.", Logger.LEVEL.INFO);
			//Store old width and height
			var oWidth:Number = width;
			var oHeight:Number = height;
			//Store new width and height
			width = w;
			height = h;		
			//Reset the tool-tips stage area
			this.tTip.resetStage(width, height);
			//Resize the logger (if in dedug mode)
			if (debugMode){
				this.lgr.setSize(width, height);
			}
			//Reposition the application message display box
			tfAppMsg._x = this.x + width/2;
			tfAppMsg._y = this.x + height/2;
			//Clear and render only if data has been loaded
			if (this.xmlData!=undefined){
				//Re-init the chart - with the flag that it's a re-draw
				reInit(true);
				//And now render again - with the flag that it's a re-draw
				render(true);
				//Render overlay button again
				drawOverlayButton(this.overlayButtonParams);
			}
			//Expose event to JS
			if (this.registerWithJS==true &&  ExternalInterface.available){
				ExternalInterface.call("FC_Resized", this.DOMId, width, height, oWidth, oHeight);
				ExternalInterface.call("__fusioncharts_event", {type:"resized", sender:this.DOMId}, {width:this.width, height:this.height, prevWidth:oWidth, prevHeight:oHeight});
			}
			//Clear resized queued flag
			resizeQueued = false;
		}
	}
	
	/**
	 * Sets whether automatic resizing is to be locked
	*/
	private function lockResize(lock:Boolean):Void{
		//Store
		resizeLocked = lock;
	}
	
	//These functions are defined in the class to prevent
	//the compiler from throwing an error when they are called
	//They are not implemented until EventDispatcher.initalize(this)
	//overwrites them.
	public function dispatchEvent() {
	}
	public function addEventListener() {
	}
	public function removeEventListener() {
	}
	public function dispatchQueue() {
	}
	/**
	 * exposeChartRendered method is called when the chart has rendered. 
	 * Here, we expose the event to JS (if required) & also dispatch a
	 * event (so that, if other movies are loading this chart, they can listen).
	*/
	private function exposeChartRendered():Void {
		//Set flag that chart has rendered as true
		this.chartRendered = true;
		//Proceed, if we've not already conveyed the event
		if (this.renderEventConveyed==false){
			//Expose event to JS
			if (this.registerWithJS==true &&  ExternalInterface.available){
				ExternalInterface.call("FC_Rendered", this.DOMId);
				ExternalInterface.call("__fusioncharts_event", {type:"rendered", sender:this.DOMId}, {width:this.width, height:this.height});
			}
			//Dispatch an event to loader class
			this.dispatchEvent({type:"rendered", target:this});
			//Update flag that we've conveyed both rendered events now.
			this.renderEventConveyed = true;
		}
		//Expose chart drawn 
		exposeChartDrawn();
		//Clear calling interval
		clearInterval(this.config.intervals.renderedEvent);
	}
	/**
	* exposeChartDrawn method is called each time the chart finishes drawing.
	*/
	private function exposeChartDrawn():Void{
		//Set flag that the chart has finished drawing
		this.chartDrawn = true;
		//Increase count
		chartDrawCount++;
		//Expose event to JS
		if (this.registerWithJS==true &&  ExternalInterface.available){
			ExternalInterface.call("__fusioncharts_event", {type:"drawcomplete", sender:this.DOMId}, {drawCount:chartDrawCount, width:this.width, height:this.height});
		}
	}
	
	/**
	 * Raises the FC_NoDataToDisplay event to ExternalInterface, when the
	 * chart doesn't have any data to display.
	 */
	private function raiseNoDataExternalEvent():Void {
		if (this.registerWithJS==true &&  ExternalInterface.available){
			ExternalInterface.call("FC_NoDataToDisplay", this.DOMId);
			ExternalInterface.call("__fusioncharts_event", {type:"nodatatodisplay", sender:this.DOMId}, {});
		}
	}
	//----------- DATA RELATED AND PARSING METHODS ----------------//
	/**
	* setXMLData helps set the XML data for the chart. The XML data
	* is acquired from external source. Like, if you load the chart
	* in your movie, you need to create/load the XML data and handle
	* the loading events (etc.). Finally, when the proper XML is loaded,
	* you need to pass it to Chart class. When FusionCharts is loaded
	* in HTML, the .fla file loads the XML and displays loading progress
	* bar and text. Finally, when loaded, errors are checked for, and if
	* ok, the XML is passed to chart for further processing and rendering.
	*	@param	objXML	XML Object containing the XML Data
	*	@return		Nothing.
	*/
	public function setXMLData (objXML : XML) : Void 
	{
		//If the XML data has no child nodes, we display error
		if ( ! objXML.hasChildNodes ())
		{
			//Show "No data to display" error
			tfAppMsg = this.renderAppMessage (_global.getAppMessage ("NODATA", this.lang));
			//Add a message to the log.
			this.log ("ERROR", "No data to display. There isn't any node/element in the XML document. Please check if your dataURL is properly URL Encoded or, if XML has been correctly embedded in case of dataXML.", Logger.LEVEL.ERROR);
		} else 
		{
			//We've data.
			//Store the XML data in class
			this.xmlData = new XML ();
			this.xmlData = objXML;
			//Show rendering chart message
			tfAppMsg = this.renderAppMessage (_global.getAppMessage ("RENDERINGCHART", this.lang));
			//If the chart is in debug mode, then add XML data to log
			if (this.debugMode)
			{
				var strXML : String = this.xmlData.toString ();
				//First we need to convert < and > in XML to &lt; and &gt;
				//As our logger textbox is HTML enabled.
				strXML = StringExt.replace (strXML, "<", "&lt;");
				strXML = StringExt.replace (strXML, ">", "&gt;");
				//Also convert carriage returns to <BR> for better viewing.
				strXML = StringExt.replace (strXML, "/r", "<BR>");
				this.log ("XML Data", strXML, Logger.LEVEL.CODE);
			}
		}
	}
	
	/**
	 * Stores the raw XML string as passed to chart, before any parsing
	 * invocation, thereby retaining originally escaped characters.
	 * @para	str		Raw XML String.
	*/
	public function setRawXMLString(str:String):Void{
		//Just store
		this.rawXMLString = str;
	}
	
	/**
	 * Searches for XML data of linked chart in current chart's XML by 
	 * means of provided ID. If no such ID is found, empty string is returned.
	 * @param	linkedDataId	Linked data's ID in the XML.
	*/
	private function getLinkedChartDataXML(linkedDataId:String):String{
		//Convert ID to upper case for case-insensitive search
		linkedDataId = linkedDataId.toUpperCase();
		//String that contains data
		var strXML:String = "";
		//----- Iterate through XML -------//
		//Get the element nodes
		var arrDocElement : Array = this.xmlData.childNodes;
		//Loop variable
		var i : Number, j : Number, k:Number;
		//Look for <graph> element
		for (i = 0; i < arrDocElement.length; i ++)
		{
			//If it's a <graph> element, proceed.
			//Do case in-sensitive mathcing by changing to upper case
			if (arrDocElement [i].nodeName.toUpperCase () == "GRAPH" || arrDocElement [i].nodeName.toUpperCase () == "CHART")
			{
				//Now, get the child nodes - first level nodes
				var arrLevel1Nodes : Array = arrDocElement [i].childNodes;
				//Node to store data for the same
				var dataNode : XMLNode;
				//Iterate through all level 1 nodes.
				for (j = 0; j < arrLevel1Nodes.length; j ++)
				{
					//If it's Data nodes
					if (arrLevel1Nodes [j].nodeName.toUpperCase () == "LINKEDDATA")
					{
						//Data Node. So extract the attributes.
						//Get reference to node.
						dataNode = arrLevel1Nodes [j];
						//Get attributes
						var atts : Array;
						atts = this.getAttributesArray (dataNode);
						//Extract id.
						var id:String = getFV (atts ["id"] , "");
						//Convert to upper-case
						id = id.toUpperCase();
						//Now match and extract
						if (id==linkedDataId)
						{
							//Find the chart element first.
							var arrLevel2Nodes : Array = arrLevel1Nodes [j].childNodes;
							for (k = 0; k < arrLevel2Nodes.length; k++)
							{
								if (arrLevel2Nodes [k].nodeName.toUpperCase () == "CHART")
								{
									//Store
									strXML = arrLevel2Nodes[k].toString();
								}
							}
						}
					}
				}
			}
		}
		//Delete all temporary objects used for parsing XML Data document
		delete dataNode;
		delete arrDocElement;
		delete arrLevel1Nodes;
		delete arrLevel2Nodes;
		//Return the data
		return strXML;
	}
	
	/**
	 * returnXML method returns the XML data of the chart as string. It 
	 * returns the XML stored in rawXMLString, so that incoming escaped
	 * characters are returned in original format.
	*/
	public function returnXML(escaped:Boolean):String{		
		//If the rawXMLString  is "" (owing to error in loading), return
		//an empty <chart /> element. Else return actual data.
		if (rawXMLString!=""){
			if (arguments.length==0 || escaped==false){
				return rawXMLString;
			}else{
				return escape(rawXMLString);
			}
		}else{
			return "<chart></chart>";
		}
	}
	
	/**
	 * Clears chart's cache of data. This is required for dynamic resizing in this
	 * scenario: First, serve a propert data. Then resize. Then serve an invalid data.
	 * Again, when resize is called, since xmlData wasn't updated (owing to error XML),
	 * data is shown from cache.
	*/
	public function clearDataCache():Void{
		delete this.xmlData;
	}
	/**
	 * Returns a comma separated list of all the external interface methods
	 * exposed by the chart.
	*/
	public function getExternalInterfaceMethods():String{
		return this.extInfMethods;
	}
	/**
	 * Returns the value for a specified attribute. The attribute value is returned from 
	 * the values initially specified in the XML. We do not take into consideration any 
	 * forced values imposed in the code.
	 * @param	strAttribute	Name of the attribute whose value is to be returned.
	 * @return	The value of the attribute, as specified in the XML. Returns an empty
	 * 			string, if the attribute was not found in XML.
	 */
	public function returnChartAttribute(strAttribute:String):String {
		//To get the attribute's value, we directly parse the XML of the chart.
		var i : Number;
		//Get the element nodes
		var arrDocElement : Array = this.xmlData.childNodes;
		//Look for <graph> or <chart> element
		for (i = 0; i < arrDocElement.length; i ++)
		{
			//If it's a <graph> or <chart> element, proceed.
			//Do case in-sensitive mathcing by changing to upper case
			if (arrDocElement [i].nodeName.toUpperCase () == "GRAPH" || arrDocElement [i].nodeName.toUpperCase () == "CHART")
			{
				//Now, get the list of attributes for <chart> element and get the required value
				var chartElement:XMLNode = arrDocElement [i];
				//Get the list of attributes as array
				//Array to store the attributes
				var atts : Array = this.getAttributesArray (chartElement);
				//Now, return the value
				return (getFV(atts[strAttribute.toLowerCase()], ""));
			}
		}
	}
	/**
	* parseStyleXML method parses the XML nodes which defines the Styles
	* elements (application and definition). This method is common to all
	* charts, as STYLE is an integral part of FusionCharts v3. So, we've
	* defined this parsing in Chart class, to avoid code duplication.
	*	@param	arrStyleNodes	Array of XML Nodes containing style definition
	*	@return				Nothing
	*/
	private function parseStyleXML (arrStyleNodes : Array) : Void
	{
		//Loop variables
		var k : Number;
		var l : Number;
		var m : Number;
		//Search for Definition Node first
		for (k = 0; k < arrStyleNodes.length; k ++)
		{
			if (arrStyleNodes [k].nodeName.toUpperCase () == "DEFINITION")
			{
				//Store the definition nodes in arrDefNodes
				var arrDefNodes : Array = arrStyleNodes [k].childNodes;
				//Iterate through each definition node and extract the style parameters
				for (l = 0; l < arrDefNodes.length; l ++)
				{
					//If the node name is STYLE, store it
					if (arrDefNodes [l].nodeName.toUpperCase () == "STYLE")
					{
						//Store the node reference
						var styleNode : XMLNode = arrDefNodes [l];
						//Get the attributes array
						var styleAttr : Array = this.getAttributesArray (styleNode)
						//Get attributes of style definition
						var styleName : String = styleAttr ["name"];
						var styleTypeName : String = styleAttr ["type"];
						//Now, if the style type identifier is valid, we proceed
						try 
						{
							//Get the style type id from style type name
							var styleTypeId : Number = this.styleM.getStyleTypeId (styleTypeName);
							//If the control comes here, that means the style type identifier is correct.
							//Create a style object to store the attributes for this style
							var styleObj : StyleObject = new StyleObject ();
							//Now, iterate through all attributes and store them in style obj
							//WE NECESSARILY NEED TO CONVERT ALL ATTRIBUTES TO LOWER CASE
							//BEFORE STORING IT IN STYLE OBJECT
							var attr : Object;
							for (attr in styleNode.attributes)
							{
								styleObj [attr.toLowerCase ()] = styleNode.attributes [attr];
							}
							//Add this style to style manager
							this.styleM.addStyle (styleName, styleTypeId, styleObj);
						} catch (e : com.fusioncharts.helper.FCError)
						{
							//If the control comes here, that means the given style type
							//identifier is invalid. So, we log the error message to the
							//logger.
							this.log (e.name, e.message, e.level);
						}
					}
				}
			}
		}
		//Definition nodes have been stored. So search and store application nodes
		for (k = 0; k < arrStyleNodes.length; k ++)
		{
			if (arrStyleNodes [k].nodeName.toUpperCase () == "APPLICATION")
			{
				//Store the application nodes in arrAppNodes
				var arrAppNodes : Array = arrStyleNodes [k].childNodes;
				for (l = 0; l < arrAppNodes.length; l ++)
				{
					//If it's an application definition
					if (arrAppNodes [l].nodeName.toUpperCase () == "APPLY")
					{
						//Get attributes array
						var appAttr : Array = this.getAttributesArray (arrAppNodes [l]);
						//Extract the attributes
						var toObject : String = appAttr ["toobject"];
						//NECESSARILY CONVERT toObject TO UPPER CASE FOR MATCHING
						toObject = toObject.toUpperCase ();
						var styles : String = appAttr ["styles"];
						//Now, we need to check if the given Object is a valid chart object
						if (isChartObject (toObject))
						{
							//If it's a valid chart object, we get the id of the object
							//and associate the list of styles.
							//First, we need to convert the comma separated list of styles
							//into an array
							var arrStyles : Array = new Array ();
							arrStyles = styles.split (",");
							//Get the numeric id of the chart object
							var objectId = this.objects [toObject];
							//Now iterate through each style defined for this object and associate
							for (m = 0; m < arrStyles.length; m ++)
							{
								try
								{
									//Associate object with style.
									this.styleM.associateStyle (objectId, arrStyles [m]);
								}
								catch (e : com.fusioncharts.helper.FCError)
								{
									//If the control comes here, that means the given object name
									//is invalid. So, we log the error message to the logger.
									this.log (e.name, e.message, e.level);
								}
							}
						} 
						else
						{
							this.log ("Invalid Chart Object in Style Definition", "\"" + toObject + "\" is not a valid Chart Object. Please see the list above for valid Chart Objects.", Logger.LEVEL.ERROR);
						}
					}
				}
			}
		}
		//Clear garbage
		delete arrDefNodes;
		delete arrAppNodes;
		delete styleNode;
		delete attr;
	}
	/**
	 * Parses attributes that are common to all charts and have same default values across.
	 * This method can also be used to over-ride any attributes that have been 
	 * parsed at common level and then needs to be generalized.
	 * @param	graphElement	Reference to <chart> element node.
	 * @param	is2D			Whether the chart is a 2D chart; Required for selection of palette
	 */
	private function parseCommonAttributes (graphElement : XMLNode, is2D:Boolean) : Void 
	{
		//Array to store the attributes
		var atts : Array = this.getAttributesArray (graphElement);
		//Store whether chart is 2D
		defColors2D = is2D;
		//NOW IT'S VERY NECCESARY THAT WHEN WE REFERENCE THIS ARRAY
		//TO GET AN ATTRIBUTE VALUE, WE SHOULD PROVIDE THE ATTRIBUTE
		//NAME IN LOWER CASE. ELSE, UNDEFINED VALUE WOULD SHOW UP.
		//Whether to show about Menu Item - by default set to on
		this.params.showFCMenuItem = toBoolean(getFN (atts["showaboutmenuitem"], atts ["showfcmenuitem"] , 1));
		//Additional parameters of about menu item
		this.params.aboutMenuItemLabel = getFV(atts["aboutmenuitemlabel"], "About FusionCharts");
		this.params.aboutMenuItemLink = getFV(atts["aboutmenuitemlink"], "n-http://www.fusioncharts.com/?BS=AboutMenuLink");
		//Whether to show print Menu Item - by default set to on
		this.params.showPrintMenuItem = toBoolean(getFN(atts ["showprintmenuitem"] , 1));
		//Whether to force y-axis values decimals
		this.params.forceYAxisValueDecimals = toBoolean(getFN(atts ["forceyaxisvaluedecimals"] , this.params.forceDecimals));
		this.params.forceSYAxisValueDecimals = toBoolean(getFN(atts ["forcesyaxisvaluedecimals"] , this.params.forceYAxisValueDecimals));		
		//Whether to show zero plane value
		this.params.showZeroPlaneValue = toBoolean(getFN(atts ["showzeroplanevalue"] , this.params.showDivLineValues));		
		//Options related to export of chart data
		this.params.showExportDataMenuItem = toBoolean(getFN(atts["showexportdatamenuitem"], 0));
		this.params.exportDataMenuItemLabel = getFV(atts["exportdatamenuitemlabel"], "Copy data to clipboard");
		this.params.exportDataSeparator = getFV(atts["exportdataseparator"], ",");
		//Whether to export formatted values
		this.params.exportDataFormattedVal = toBoolean(getFN(atts["exportdataformattedval"], 0));
		//Normalize the export data separator for special characters
		this.params.exportDataSeparator = this.normalizeKeyword(this.params.exportDataSeparator);
		//Qualifier for the exported data
		this.params.exportDataQualifier = getFV(atts["exportdataqualifier"], "{quot}");
		//If it's empty space, we assume no qualifiers are needed
		this.params.exportDataQualifier = (this.params.exportDataQualifier == " ")?"":this.params.exportDataQualifier;
		//Normalize the qualifier
		this.params.exportDataQualifier = this.normalizeKeyword(this.params.exportDataQualifier);
		//Fixed line break for export data
		this.params.exportDataLineBreak = "\r\n";
		//Background swf
		this.params.bgSWF = getFV (atts ["bgimage"], atts ["bgswf"] , "");
		this.params.bgSWFAlpha = getFN (atts ["bgimagealpha"], atts ["bgswfalpha"] , 100);
		//Overlay (foreground) logo parameters
		this.params.logoURL = getFV(atts["logourl"], "");
		this.params.logoPosition = getFV(atts["logoposition"], "TL");
		this.params.logoAlpha = getFN(atts["logoalpha"], 100);
		this.params.logoLink = getFV(atts["logolink"], "");
		this.params.logoScale = getFN(atts["logoscale"], 100);
		//Tool Tip - Show/Hide, Background Color, Border Color, Separator Character
		this.params.showToolTip = toBoolean (getFN (atts ["showtooltip"] , atts ["showhovercap"] , 1));
		this.params.showToolTipForWrappedLabels = toBoolean (getFN (atts ["showtooltipforwrappedlabels"] , ((this.params.showToolTip==true)?1:0), 1));
		this.params.toolTipBgColor = formatColor (getFV (atts ["tooltipbgcolor"] , atts ["hovercapbgcolor"] , atts ["hovercapbg"] , (is2D==true)?(this.defColors.get2DToolTipBgColor (this.params.palette)):(this.defColors.getToolTipBgColor3D(this.params.palette))));
		this.params.toolTipBorderColor = formatColor (getFV (atts ["tooltipbordercolor"] , atts ["hovercapbordercolor"] , atts ["hovercapborder"] , (is2D==true)?(this.defColors.get2DToolTipBorderColor (this.params.palette)):(this.defColors.getToolTipBorderColor3D(this.params.palette))));
		this.params.toolTipSepChar = getFV (atts ["tooltipsepchar"] , atts ["hovercapsepchar"] , ", ");
		this.params.showToolTipShadow = toBoolean (getFN (atts ["showtooltipshadow"] , 0));
		//Series name in tool-tip
		this.params.seriesNameInToolTip = toBoolean (getFN (atts ["seriesnameintooltip"] , 1));
		//Whether to add ellipses to labels
		this.params.useEllipsesWhenOverflow = toBoolean (getFN (atts ["useellipseswhenoverflow"] , 1));		
		//Font Properties
		this.params.baseFont = getFV (atts ["basefont"] , "Verdana");
		this.params.baseFontSize = getFN (atts ["basefontsize"] , 10);
		this.params.baseFontColor = formatColor (getFV (atts ["basefontcolor"] , (is2D==true)?(this.defColors.get2DBaseFontColor (this.params.palette)):(this.defColors.getBaseFontColor3D(this.params.palette))));
		this.params.outCnvBaseFont = getFV (atts ["outcnvbasefont"] , this.params.baseFont);
		this.params.outCnvBaseFontSize = getFN (atts ["outcnvbasefontsize"] , this.params.baseFontSize);
		this.params.outCnvBaseFontColor = formatColor (getFV (atts ["outcnvbasefontcolor"] , this.params.baseFontColor));
		//Whether to show v-line label borders
		this.params.showVLineLabelBorder = toBoolean(getFN(atts["showvlinelabelborder"], 1));
		//Whether to centered the yAxis label with respect to chart height.
		this.params.centerYAxisName = toBoolean(getFN(atts["centeryaxisname"], 1));
		//Whether to centered the xAxis label with respect to chart width, only applicable for bar charts.
		this.params.centerXAxisName = toBoolean(getFN(atts["centerxaxisname"], this.params.centerYAxisName, 1));
		//Export chart related attributes
		this.params.exportEnabled = toBoolean (getFN (atts ["exportenabled"], atts ["imagesave"] , 0));
		//Whether to show export Menu items
		this.params.exportShowMenuItem = toBoolean(getFN(atts["exportshowmenuitem"], atts["showexportmenuitem"], this.params.exportEnabled?1:0));
		//Export formats to be supported, along with their names in context menu
		this.params.exportFormats = getFV(atts["exportformats"], "JPG=Save as JPEG Image|PNG=Save as PNG Image|PDF=Save as PDF");
		//Whether to save the chart at client? Default is server side export
		this.params.exportAtClient = toBoolean (getFN (atts ["exportatclient"] , 0));
		//Export action - Save or Download. Only applicable when exporting at server.
		this.params.exportAction = String(getFV(atts["exportaction"], "download")).toLowerCase();
		//Can only be save or download
		this.params.exportAction = (this.params.exportAction != "save" && this.params.exportAction != "download")?"download":this.params.exportAction;
		//Target window for download of image - only applicable during server-side download
		//Currently, we support only _self and _blank
		this.params.exportTargetWindow = String(getFV(atts["exporttargetwindow"], "_self")).toLowerCase();
		//Can only be _self or _blank
		this.params.exportTargetWindow = (this.params.exportTargetWindow != "_self" && this.params.exportTargetWindow != "_blank")?"_self":this.params.exportTargetWindow;
		//URL of server side script, or DOM ID of the DIV that contains export component
		this.params.exportHandler = getFV (atts["exporthandler"], atts ["imagesaveurl"] , "");
		//File name to be exported
		this.params.exportFileName = getFV(atts["exportfilename"], "FusionCharts");
		//Export parameters - for future use (gets passed to server/client exporter)
		this.params.exportParameters = getFV(atts["exportparameters"], "");
		//Export call back function name
		//This attribute specifies the name of the callback JavaScript function which would 
		//be called when the export event is complete.
		//Scenarios:
		//Server-side Save: the chart would call this function passing all the 
		//confirm-response received from the server. 
		//Server-side Download:  no callback
		//Client-side export: The client side exporter component (SWF) would call 
		//the function once the export event complete.
		this.params.exportCallback = getFV(atts["exportcallback"], "FC_Exported");
		//Export dialog box propertiES
		this.params.showExportDialog = toBoolean (getFN (atts ["showexportdialog"] , 1));
		this.params.exportDialogMessage = getFV(atts["exportdialogmessage"],"Capturing Data : ");
		this.params.exportDialogColor = formatColor (getFV (atts["exportdialogcolor"], atts ["imagesavedialogcolor"] , "FFFFFF"));
		this.params.exportDialogBorderColor = formatColor (getFV (atts["exportdialogbordercolor"], "999999"));
		this.params.exportDialogFontColor = formatColor (getFV (atts["exportdialogfontcolor"], atts ["imagesavedialogfontcolor"] , "666666"));
		this.params.exportDialogPBColor = formatColor (getFV (atts["exportdialogpbcolor"], atts ["imagesavedialogcolor"] , "E2E2E2"));
		//Internal callback function to be invoked when capturing is done
		this.params.exportDataCaptureCallback = "FC_ExportDataReady";
		//Whether to unescape links specified in XML
		this.params.unescapeLinks = toBoolean (getFN (atts ["unescapelinks"] , 1));
		//Custom canvas margins (forced by the user)
		this.params.canvasLeftMargin = getFN(atts ["canvasleftmargin"] , -1);
		this.params.canvasRightMargin = getFN(atts ["canvasrightmargin"] , -1);
		this.params.canvasTopMargin = getFN(atts ["canvastopmargin"] , -1);
		this.params.canvasBottomMargin = getFN(atts ["canvasbottommargin"] , -1);
		// Set background swf param
		this.params.bgImageDisplayMode = getFV(atts["bgimagedisplaymode"], "none").toLowerCase();
		this.params.bgImageVAlign = atts["bgimagevalign"].toLowerCase();
		this.params.bgImageHAlign = atts["bgimagehalign"].toLowerCase();
		//when background mode is tile, fill and fit then default value of vertical alignment and horizontal alignment will be middle and middle
		if(this.params.bgImageDisplayMode == "tile" || this.params.bgImageDisplayMode == "fill" || this.params.bgImageDisplayMode == "fit")
		{
			if( this.params.bgImageVAlign != "top" && this.params.bgImageVAlign != "middle" && this.params.bgImageVAlign != "bottom") this.params.bgImageVAlign = "middle";
			if( this.params.bgImageHAlign != "left" && this.params.bgImageHAlign != "middle" && this.params.bgImageHAlign != "right") this.params.bgImageHAlign = "middle";			
		}
		else
		{			
			if( this.params.bgImageVAlign != "top" && this.params.bgImageVAlign != "middle" && this.params.bgImageVAlign != "bottom") this.params.bgImageVAlign = "top";
			if( this.params.bgImageHAlign != "left" && this.params.bgImageHAlign != "middle" && this.params.bgImageHAlign != "right") this.params.bgImageHAlign = "left";			
		}
		this.params.bgImageScale = getFN(atts["bgimagescale"], 100);
		
		//The hidden attribute to enable/disable new label management
		this.params.XTLabelManagement = toBoolean (getFN (atts ["xtlabelmanagement"], 1));
		this.params.showExtremeLabelRegion = toBoolean (getFN (atts ["showextremelabelregion"], 0));
		
	}
	
	/**
	 * Sets the flag from Flashvars on whether the chart has to animate.
	 * @param	animate		Number (0/1) representing whether to animate
	*/
	public function setDefaultGlobalAnimation(animate:Number):Void{		
		this.defaultGlobalAnimation = animate;
	}
	//----------- CORE FUNCTIONAL METHODS ----------//
	/**
	* setChartObjects method stores the list of chart objects
	* in local arrObjects array and objects enumeration.
	*	@param	arrObjects	Array listing chart objects.
	*	@return			Nothing
	*/
	private function setChartObjects (arrObjects : Array) : Void 
	{
		//Copy array to instance variable
		this.arrObjects = arrObjects;
		//Store the list of chart objects in enumeration
		this.objects = new FCEnum (arrObjects);
		//Create the list if in debug mode and add to log
		var i : Number;
		if (this.debugMode)
		{
			var strChartObjects : String = "";
			for (i = 0; i < arrObjects.length; i ++)
			{
				strChartObjects += "<LI>" + arrObjects [i] + "</LI>";
			}
			this.log ("Chart Objects", strChartObjects, Logger.LEVEL.INFO);
		}
	}
	/**
	* isChartObject method helps check whether the given
	* object name is a valid chart object (pre-defined).
	*	@param	isChartObject	Name of chart object
	*	@return				Boolean value indicating whether
	*							it's a valid chart object.
	*/
	private function isChartObject (strObjName : String) : Boolean 
	{
		//By default assume invalid
		var isValid : Boolean = false;
		var i : Number;
		//Iterate through the list of objects to see if this is
		//valid
		for (i = 0; i < arrObjects.length; i ++)
		{
			if (arrObjects [i].toUpperCase () == strObjName)
			{
				isValid = true;
				break;
			}
		}
		return isValid;
	}
	
	/**
	 * Normalizes the keyword. For example, tab cannot be specified
	 * in XML as a tab character. So instead we use pseudo codes as {tab} as 
	 * keyword in XML. Internally, this method normalizes the specified
	 * pseudo keyword.
	 * @param	strKeyword	Pseudo code specified in XML.
	 * @return	Normalized string representation of the pseudo keyword specified
	 * 			in XML.
	 */
	private function normalizeKeyword(strKeyword:String):String {
		switch (strKeyword.toLowerCase()) {
			case "{tab}":
			return "\t";
			break;
			case "{quot}":
			return String.fromCharCode(34);
			break;
			case "{apos}":
			return String.fromCharCode(39);
			default:
			return strKeyword;
			break;
		}
	}
	
	/**
	 * Sets the list of colors for palette. The palette colors are
	 * provided in XML as <chart paletteColors='list of colors separated by comma'.
	*/
	private function setPaletteColors(){
		//Proceed only if the user has specified his own set of colors in the XML.
		if (this.params.paletteColors!=undefined && this.params.paletteColors!=""){
			//Get the list of colors
			var strColors:String = this.params.paletteColors;
			//Remove any spaces from the same
			strColors = StringExt.removeSpaces(strColors);
			//Set the colors
			this.defColors.setPaletteColors(strColors);
		}
	}
	/**
	* log method records a message to the chart's logger. We record
	* messages in the logger, only if the chart is in debug mode to
	* save memory
	*	@param	strTitle	Title of messsage
	*	@param	strMsg		Message to be logged
	*	@param	intLevel	Numeric level of message - a value from
	*						Logger.LEVEL Enumeration
	*/
	public function log (strTitle : String, strMsg : String, intLevel : Number)
	{
		if (debugMode)
		{
			lgr.record (strTitle, strMsg, intLevel);
		}
	}
	/**
	* printChart method prints the chart.
	*/
	public function printChart () : Void
	{
		//Create a Print Job Instance
		var PrintQueue = new PrintJob ();
		//Show the Print box.
		var PrintStart : Boolean = PrintQueue.start ();
		//If User has selected Ok, set the parameters.
		if (PrintStart)
		{
			//Add the chart MC to the print job with the required dimensions
			//If the chart width/height is bigger than page width/height, we need to scale.
			if (this.width>PrintQueue.pageWidth || this.height>PrintQueue.pageHeight){				
				//Scale on the lower factor
				var factor:Number = Math.min((PrintQueue.pageWidth/this.width),(PrintQueue.pageHeight/this.height));
				//Scale the movie clip to fit paper size 
				this.cMC._xScale = factor*100;
				this.cMC._yScale = factor*100;
			}
			//Add the chart to printer queue
			PrintQueue.addPage (this.cMC, {xMin : 0, xMax : this.width, yMin : 0, yMax : this.height}, {printAsBitmap : true});
			//Send the page for printing
			PrintQueue.send ();
			//Re-scale back to normal form (as the printing is over).
			this.cMC._xScale = this.cMC._yScale = 100;
		}		
		delete PrintQueue;
	}
	/**
	* reInit method re-initializes the chart. This method is basically called
	* when the user changes chart data through JavaScript. In that case, we need
	* to re-initialize the chart, set new XML data and again render.
	*/
	public function reInit () : Void
	{
		//Re-initialize params and config object
		this.params = new Object ();
		this.config = new Object ();
		//Re-initialize object manager
		this.objM.reset();
		//Object to store chart rendering intervals
		this.config.intervals = new Object ();
		//Re-create an empty chart movie clip
		this.cMC = parentMC.createEmptyMovieClip ("Chart", depth + 1);		
		//Re-position the chart Movie clip to required x and y position
		this.cMC._x = this.x;
		this.cMC._y = this.y;
		//Movie clip loader for the logo.
		this.logoMCLoader = new MovieClipLoader();
		//Listener object for the logo MC
		this.logoMCListener = new Object();
		//MovieClipLoader for background SWF.
		this.bgSWFMCLoader = new MovieClipLoader();
		//Listener object for the background SWF
		this.bgSWFMCListener = new Object();			
		//Logo holder
		this.logoMC  = this.cMC.createEmptyMovieClip ("Logo", 100000);
		//Reset the style manager
		this.styleM = new StyleManager (this);
		//Reset macros
		this.macro = new Macros ();
		//Reset the default colors iterator
		this.defColors.reset ();
		//re initiate the defColors to remove all previously passed colors
		this.defColors = new DefaultColors();
		//Reset depth manager
		this.dm.clear ();
		this.dm.setStartDepth (0);
		//Set timeElapsed to 0
		this.timeElapsed = 0;
		//Set chartRendered to false again
		this.chartRendered = false;
		//Export capture process back to false
		this.exportCaptureProcessOn = false;
		//Resize queued back to false
		this.resizeQueued = false;
		//Chart is not draw
		this.chartDrawn = false;
		//Re-initialize raw XML string
		this.rawXMLString = "";
	}
	/**
	* remove method removes the chart by clearing the chart movie clip
	* and removing any listeners. However, the logger still stays on.
	* To remove the logger too, you need to call destroy method of chart.
	*/
	public function remove () : Void 
	{
		//Remove all the intervals (which might not have been cleared)
		//from this.config.intervals
		var item : Object;
		for (item in this.config.intervals)
		{
			//Clear interval
			clearInterval (this.config.intervals [item]);
		}
		//If any export related function is happening, cancel the same
		bmpPrintExport.cancel();
		bmpS.cancel();
		//Remove application message (if any)
		this.removeAppMessage (this.tfAppMsg);
		//Remove listener of logo and its associated clips
		this.logoMCLoader.removeListener(this.logoMCListener);
		//Unloading movie clip after listeners have been removed, so that
		//onLoadError is NOT invoked.
		this.logoMCLoader.unloadClip(this.logoMC);
		//Remove the logoMC itself
		logoMC.removeMovieClip();
		//Remove listener of background swf loader and its associated clips
		this.bgSWFMCLoader.removeListener(this.bgSWFMCListener);
		//Unloading movie clip after listeners have been removed, so that
		//onLoadError is NOT invoked.
		this.bgSWFMCLoader.unloadClip(this.bgSWFMC.ImgMC);
		//Remove the bgSWFMC itself
		bgSWFMC.removeMovieClip();
		//Remove the chart movie clip
		cMC.removeMovieClip ();
		//Hide tool tip
		tTip.hide ();
	}
	/**
	* destroy method destroys the chart by removing the chart movie clip,
	* logger movie clip, and removing any listeners.
	*/
	public function destroy () : Void 
	{
		//Remove the chart first
		this.remove ();
		//Remove the chart movie clip
		cMC.removeMovieClip ();
		//Destroy the logger
		this.lgr.destroy ();
		//Destroy tool tip
		this.tTip.destroy ();
		//Remove tool tip movie clip
		this.ttMC.removeMovieClip ();
		//Remove test text field movie clip
		this.tfTestMC.removeMovieClip ();
		//Remove logger movie clip
		this.logMC.removeMovieClip ();
		//Remove logo MC
		this.logoMC.removeMovieClip();
		//Remove overlay button
		this.btnMC.removeMovieClip();
		//Remove Background SWF MC
		this.bgSWFMC.removeMovieClip();
		
	}
	//------------ External Interface Methods -----------//
	/**
	 * Returns a boolean value indicating whether the chart has finished
	 * rendering.
	 * @return	Boolean value indicating whether the chart has finished
	 * 			rendering.
	 */
	private function hasChartRendered():Boolean {
		return this.chartRendered;
	}
	/**
	 * Returns the signature of the chart in format:
	 */
	public function signature():String {
		var sgn:String = "FusionCharts/" + this._version;
		return sgn;
	}
	//-------------------- Context Menu related methods ----------------------//
	/**
	 * returnAbtMenuItem method returns a context menu item that reads
	 * "About FusionCharts".
	*/
	private function returnAbtMenuItem():ContextMenuItem{
		//Create a about context menu item
		var aboutCMI : ContextMenuItem = new ContextMenuItem (this.params.aboutMenuItemLabel, Delegate.create (this, openAboutMenuLink));
		aboutCMI.separatorBefore = true;		
		return aboutCMI;
	}
	/**
	 * Adds all the export chart related menu items to the context menu. This 
	 * method is invoked by each chart class. Here, we look at exportFormats and
	 * add all provided formats to the context menu.
	 * @param	cm	Context Menu to which we've to add export chart items.
	 */
	private function addExportItemsToMenu(cm:ContextMenu) {
		if (this.params.exportEnabled && this.params.exportShowMenuItem) {
			//First, parse the export formats given by user
			var expFrm:Array = this.params.exportFormats.split("|");
			var itm:String, itmLabel:String, itmFormat:String;
			//Iterate through each item and add to menu
			for (var i:Number = 0; i < expFrm.length; i++) {
				//If the item is not blank, proceed only then
				if (expFrm[i] != "") {
					//Set containers empty
					itmLabel = "";
					itmFormat = "";
					//If there's an equal to sign
					if (expFrm[i].indexOf("=") != -1) {
						//User has specified both format and context menu label
						itm = String(expFrm[i])
						itmFormat = itm.substring(0, itm.indexOf("="));
						itmLabel = itm.substring(itm.indexOf("=") + 1, itm.length + 1);						
					}else {
						//User has just specified format. So, automatically set context menu label.
						itmFormat = expFrm[i];
						itmLabel = "Save as " + itmFormat;
					}
					//Now, add it to context menu
					var exportCMI : ContextMenuItem = new ContextMenuItem (itmLabel, Delegate.create (this, exportTriggerHandlerCM));
					//Set the item format within the item, so that we do not need to track it individually
					exportCMI.format = itmFormat;
					cm.customItems.push(exportCMI);
				}
			}
		}
	}
	/**
	 * Returns a context menu item to represent Export Data. 
	 * @return
	 */
	private function returnExportDataMenuItem():ContextMenuItem {
		//Create a about context menu item
		var exportDataCMI : ContextMenuItem = new ContextMenuItem (this.params.exportDataMenuItemLabel, Delegate.create (this, exportChartDataMenuItemHandler));
		return exportDataCMI;
	}
	/**
	 * openAboutMenuLink is the handler for About Menu Item
	 * context menu item
	*/
	private function openAboutMenuLink():Void{
		//Open the link
		this.invokeLink(this.params.aboutMenuItemLink);
	}	
	/**
	 * Invoked when user selects the export data handler from
	 * context menu. Here, we get the export data and copy it to clipboard.
	 */
	private function exportChartDataMenuItemHandler() {
		//Copy the data to clipboard
		System.setClipboard(this.exportChartDataCSV());
	}
	/**
	 * Forward declaration block, as individual charts export their
	 * own data in the required format. Child classes can build on this class.
	 * @return	CSV separated data of the chart.
	 */
	public function exportChartDataCSV():String {
		return "";
	}
	//---------------------------------------------------------------------//
	//			           Export Chart Related Routines
	//---------------------------------------------------------------------//
	/**
	* Defines the event listener methods for export handler.
	*/
	private function setExportListeners():Void{
		//Create the global object
		exportListenerObj = new Object();
		//Reference to the class
		var classRef = this;
		//Event to detect when capturing is complete.
		exportListenerObj.onCaptureComplete = function(eventObj:Object) {
			//Hide the dialog
			if (classRef.exportParamsObj.showExportDialog){
				classRef.exportDialogHide();
			}
			//Capturing is complete. Now process the data.
			classRef.exportParamsObj.stream = eventObj.out;
			//Proceed to rest of export process
			classRef.exportProcess(classRef.exportParamsObj);			
		}
		
		//Event to detect progress of capturing
		exportListenerObj.onProgress = function(eventObj:Object){
			//Update the progess status
			if (classRef.exportCaptureProcessOn && classRef.exportParamsObj.showExportDialog){
				classRef.exportDialogUpdate(eventObj.percentDone);
			}
		}
	}
	//---- Export Chart Trigger Handlers ------//
	/**
	 * Handles all the export chart triggers raised from the context menu
	 * of chart.
	 * @param	obj		Object on which the context menu was clicked
	 * @param	item	Representation of context menu item that was clicked.
	 * 					item.format represents the format that the user selected.
	 */
	private function exportTriggerHandlerCM(obj:Object, item:Object):Void {
		//Begin capture process
		this.exportCapture(item.format, this.params.exportHandler, this.params.exportAtClient, this.params.exportDataCaptureCallback, this.params.exportCallback, this.params.exportAction, this.params.exportTargetWindow, this.params.exportFileName, this.params.exportParameters, this.params.showExportDialog);
	}
	
	/**
	 * Handles export chart triggers raised from getImageData() JS function
	 * @param	exportSettings	Object containing over-riding settings of export parameters.
	 * 
	 */
	private function exportTriggerHandlerGI(exportSettings:Object):Void {
		//We proceed only if export is enabled
		if (this.params.exportEnabled) {
			//Convert all attributes in exportSettings to small case.
			var atts:Array = Utils.getAttributesArray(exportSettings);
			var exportCallback:String = getFV(atts["exportcallback"], atts["callback"], this.params.exportCallback);
			var showExportDialog:Boolean = toBoolean(getFN(atts["showexportdialog"], this.params.showExportDialog?1:0));
			this.exportCapture("BMP", this.params.exportHandler, true, exportCallback, exportCallback, this.params.exportAction, this.params.exportTargetWindow, this.params.exportFileName, this.params.exportParameters, showExportDialog);
		}else {
			this.log("Export not enabled", "Exporting has not been enabled for this chart. Please set exportEnabled='1' in XML to allow exporting of chart.", Logger.LEVEL.ERROR);
		}
	}
	
	/**
	 * Handles export chart triggers raised from exportChart() JS function.
	 * @param	exportSettings	Object containing over-riding settings of all
	 * 							export related parameters.
	 */
	private function exportTriggerHandlerJS(exportSettings:Object):Void {
		//We proceed only if export is enabled
		if (this.params.exportEnabled) {
			//Convert all attributes in exportSettings to small case.
			var atts:Array = Utils.getAttributesArray(exportSettings);
			//Now create a local list of parameters - based on over-riding/original
			var exportHandler:String = getFV(atts["exporthandler"], this.params.exportHandler);
			var exportAtClient:Boolean = toBoolean(getFN(atts["exportatclient"], this.params.exportAtClient?1:0));
			var exportCallback:String = getFV(atts["exportcallback"], this.params.exportCallback);
			var exportAction:String = String(getFV(atts["exportaction"], this.params.exportAction)).toLowerCase();
			var exportTargetWindow:String = String(getFV(atts["exporttargetwindow"], this.params.exportTargetWindow)).toLowerCase();
			var exportFileName:String = getFV(atts["exportfilename"], this.params.exportFileName);
			var exportParameters:String = getFV(atts["exportparameters"], this.params.exportParameters);
			//To get a default export format value, we need to find the first value specified in export formats
			var expFrm:Array = this.params.exportFormats.split("|");
			var firstExportFormat:String = expFrm[0].split("=")[0];
			var exportFormat:String = getFV(atts["exportformat"], firstExportFormat);
			var showExportDialog:Boolean = toBoolean(getFN(atts["showexportdialog"], this.params.showExportDialog?1:0));
			//Validation of over-written fields
			//Can only be save or download
			exportAction = (exportAction != "save" && exportAction != "download")?"download":exportAction;
			//Can only be _self or _blank
			exportTargetWindow = (exportTargetWindow != "_self" && exportTargetWindow != "_blank")?"_self":exportTargetWindow;			
			//Now, initiate the capture process
			this.exportCapture(exportFormat, exportHandler, exportAtClient, this.params.exportDataCaptureCallback, exportCallback, exportAction, exportTargetWindow, exportFileName, exportParameters, showExportDialog);
		}else {
			this.log("Export not enabled", "Exporting has not been enabled for this chart. Please set exportEnabled='1' in XML to allow exporting of chart.", Logger.LEVEL.ERROR);
		}
	}
	
	/**
	 * Starts the capture method of chart. This is the common method that is called
	 * from any of the export triggers.
	 * @param	exportFormat			The format in which export has to take place.
	 * @param	exportHandler			Handler for the exported data - either server side script or local export component.
	 * @param	exportAtClient			Whether to export the chart at client or at server.
	 * @param	exportCaptureCallback	In case of client side export, name of call back function to be invoked when data has finished capturing.
	 * @param	exportFinalCallback		Final call back function to invoked, when exported data has been saved/exported.
	 * @param	exportAction			In case of server side export, action to be taken.
	 * @param	exportTargetWindow		In case of server side and download-action, target window which would open the result chart 
	 * @param	exportFileName			Name of resultant export file
	 * @param	exportParameters		Any parameters to be passed to and fro.
	 * @param	showExportDialog		Whether to show export dialog box	
	 */
	private function exportCapture(exportFormat:String, exportHandler:String, exportAtClient:Boolean, exportCaptureCallback:String, exportFinalCallback:String, exportAction:String, exportTargetWindow:String, exportFileName:String, exportParameters:String, showExportDialog:Boolean):Void {
		//If the chart is already in export capture process, cancel the current one
		if (this.exportCaptureProcessOn == true) {
			this.cancelExport();
		}
		//Set flag to on
		this.exportCaptureProcessOn = true;
		//If format or handler is not specified, we do not export
		if (exportFormat == "" || exportHandler == "") {
			//Log that we're not exporting
			this.exportCaptureProcessOn = false;
			this.log("Incomplete export parameters", "You need to specify the mandatory export parameters (exportEnabled, exportFormat, exportHandler) before the chart can be exported", Logger.LEVEL.ERROR);
			return;
		}
		//Show the export dialog, if need be
		if (showExportDialog){
			exportDialogShow();
		}
		
		//1. Populate exportParamsObj encapsulating all the properties passed to this method.
		//2. Use instance of BitmapSave to capture the chart's image.
		//3. Use global listener objects to track progress of it.
		
		//Object to store all export properties 
		exportParamsObj = new Object();
		exportParamsObj.exportFormat = exportFormat;
		exportParamsObj.exportHandler =  exportHandler;
		exportParamsObj.exportAtClient = exportAtClient;
		exportParamsObj.exportCaptureCallback = exportCaptureCallback;
		exportParamsObj.exportFinalCallback = exportFinalCallback;
		exportParamsObj.exportAction = exportAction;
		exportParamsObj.exportTargetWindow = exportTargetWindow;
		exportParamsObj.exportFileName = exportFileName;
		exportParamsObj.exportParameters = exportParameters;
		exportParamsObj.showExportDialog = showExportDialog;
		
		//Initialize a new instance of BitmapSave 
		bmpS = new BitmapSave(this.cMC,this.x,this.y,this.width,this.height,0xffffff);	
		
		//Before we start capturing, we need to make sure that none of the movie clips
		//are cached as bitmap. So run a function that does this job.
		if(!this.cMC.skipBmpCacheCheck){
			exportObjCache = this.exportSetPreSaving(this.cMC);
		}
		
		//Capture the bitmap now.
		this.log("Export Capture Process Start", "The chart has started capturing bitmap data for export.", Logger.LEVEL.INFO);
		bmpS.capture();
		
		//Now that the bitmap is captured, we need to set the cache property to original
		if(!this.cMC.skipBmpCacheCheck){
			this.exportResetPostSaving(this.cMC, exportObjCache)
		}
		
		//Add the event listeners
		bmpS.addEventListener("onCaptureComplete", exportListenerObj);
		bmpS.addEventListener("onProgress", exportListenerObj);
	}
	
	/**
	 * Cancels currently running export function.
	*/
	public function cancelExport():Void{
		//If a previous instance is currently in progress, cancel the same first
		if (bmpS.isInProcess()){
			//Remove event listeners
			bmpS.removeEventListener("onCaptureComplete", exportListenerObj);
			bmpS.removeEventListener("onProgress", exportListenerObj);
			//Cancel it
			bmpS.cancel();
			//Hide the dialog
			if (exportParamsObj.showExportDialog){
				exportDialogHide();
			}
			//Now that the capturing is cancelled, we need to set the cache property to original
			if(!this.cMC.skipBmpCacheCheck){
				this.exportResetPostSaving(this.cMC, exportObjCache);
			}
			//Reset flag to false
			this.exportCaptureProcessOn = false;
		}
	}
	/**
	 * Processes the chart's export data once the capture process is over.
	 * @param	expObj	Object containing the data stream and all export
	 * 					parameters.
	 */
	private function exportProcess(expObj:Object):Void {
		//Based on whether the export is to be done at client side or server side, we 
		//take different courses. In case of client side, we just pass the JS object
		//to the callback function and our job in done.
		//In case of server side, there are 2 options based on action - save and download
		//In case of download, we do not have to do anything.
		//In case of save, we need to track the return status and pass it to callback function.
		if (expObj.exportAtClient == true) {
			//Export at client. Build an object in the required format and send it out.
			this.log("Export Trasmit Data Start", "The chart has finished capture stage of bitmap export and is now initiating transfer of data to JS function '" + expObj.exportCaptureCallback + "'.", Logger.LEVEL.INFO);
			//Create an object to represent the transfer data.
			var out:Object = new Object();
			out.stream = expObj.stream;
			//Append the meta information
			out.meta = new Object();
			out.meta.caption = this.params.caption;
			out.meta.width = this.width;
			out.meta.height = this.height;
			out.meta.bgColor = "FFFFFF";
			out.meta.DOMId = this.DOMId;
			//Append the parameters that were passed as over-riding or XML
			out.parameters = new Object();
			out.parameters.exportAtClient = (expObj.exportAtClient==true)?"1":"0";
			out.parameters.exportFormat =  expObj.exportFormat;
			out.parameters.exportFormats =  this.params.exportFormats;
			out.parameters.exportCallback =  expObj.exportFinalCallback;
			out.parameters.exportAction =  expObj.exportAction;
			out.parameters.exportTargetWindow =  expObj.exportTargetWindow;
			out.parameters.exportFileName =  expObj.exportFileName;
			out.parameters.exportParameters =  expObj.exportParameters;
			out.parameters.exportHandler =  expObj.exportHandler;
			//Now, transfer it to the JS method
			if (this.registerWithJS==true && ExternalInterface.available){
				if (expObj.exportCaptureCallback!=""){
					ExternalInterface.call (expObj.exportCaptureCallback, out);
				}
				ExternalInterface.call("__fusioncharts_event", {type:"exportdataready", sender:this.DOMId}, {meta: {bgColor: "FFFFFF", caption: this.params.caption, height: this.height, width: this.width}, parameters: out.parameters, stream:expObj.stream});
			}
		}else {
			//Export at client. Build an object in the required format and send it out.
			this.log("Export Transmit Data Start", "The chart has finished capture stage of bitmap export and is now initiating transfer of data to the module at '" + expObj.exportHandler + "'.", Logger.LEVEL.INFO);
			//Create the LoadVars object to be sent
			var l:LoadVars = new LoadVars();		
			//Set data
			l.stream = expObj.stream;
			//Set meta information
			l.meta_width = this.width;
			l.meta_height = this.height;
			l.meta_bgColor = "FFFFFF";
			l.meta_DOMId = this.DOMId;
			l.parameters = "exportAtClient=" + ((expObj.exportAtClient==true)?"1":"0") + "|" + 
				"exportFormat=" + expObj.exportFormat + "|" + "exportCallback=" + expObj.exportCallback + "|" +
				"exportAction=" + expObj.exportAction + "|" + "exportTargetWindow=" + expObj.exportTargetWindow + "|" +
				"exportFileName=" + expObj.exportFileName + "|" + "exportParameters=" + expObj.exportParameters + "|" +
				"exportHandler=" + expObj.exportHandler;
			//Now, based on whether the action is save or download, we invoke different course of action
			if (expObj.exportAction == "download") {
				//We just the data and get request in specified window.
				l.send(expObj.exportHandler, expObj.exportTargetWindow, "POST");
				//Delete the loadvars object right away
				delete l;
			}else {
				//Here, we send the data to server in background and then wait for status to be returned
				//We then invoke the callback function.
				//Create the results loadvar
				var result_lv:LoadVars = new LoadVars();
				var classRef = this;
				result_lv.onLoad = function(success:Boolean) {
					if (success) {
						//Output object
						var out:Object = new Object();
						//Append DOM Id
						DOMId = classRef.DOMId;
						//Iterate through all variables of result loadvars and add it to output objects	
						//This allows custom parameters to be passed from server side script to export JS.
						for (var name:String in result_lv) {
							//Only add string values. We remove function(s) as they do not serialize.
							if (typeof(result_lv[name])=="string"){
								out[name] = result_lv[name];
							}
						}
						//If the server returned a response, we check the status code and then take an action
						if (result_lv.statusCode == "1") {
							//If it comes here, it means that the export image was saved on server. So, call
							//the callback function and pass parameters to it.
							//Just over-ride necessary parameters
							out.width = classRef.width;
							out.height = classRef.height;
							out.fileName = result_lv.fileName;
							out.statusCode = result_lv.statusCode;
							out.statusMessage = result_lv.statusMessage;
						}else {
							//If the status code isn't one, it means there has been an error.
							classRef.log("Error in exporting", "The server side export module was unable to save the chart on server. Please check that the folder permissions have been correctly set and the requisite modules for handling graphics are installed on the server.", Logger.LEVEL.ERROR);
							//Over-ride necessary parameters
							out.width = 0;
							out.height = 0;
							out.fileName = "";
							out.statusCode = result_lv.statusCode;
							out.statusMessage = result_lv.statusMessage;
						}
						//Invoke the JS.
						if (classRef.registerWithJS==true && ExternalInterface.available && expObj.exportFinalCallback!=""){
							ExternalInterface.call (expObj.exportFinalCallback, out);
						}
					} else {
						//Log the error
						classRef.log("Error in connection", "The server side export module for exporting the chart could not be reached or it did not respond correctly. Please check the exportHandler path that you've specified in XML. Also, please check that the requisite modules are installed on the server to be able to generate the images.", Logger.LEVEL.ERROR);
					}
				};
				l.sendAndLoad(expObj.exportHandler, result_lv, "POST");
				//Delete loadvars after sending data
				delete l;
			}			
		}
		//Export capture process has finished. So reset flag
		this.exportCaptureProcessOn = false;
		//If any resize is queued, invoke the same
		if (resizeQueued){
			forceResize();
		}
	}
	/**
	 * Shows the dialog box that is shown during export chart capture.
	 */
	private function exportDialogShow() {
		//Progress bar positioning and dimension
		var PBWidth:Number = (this.width > 200) ? 150 : (this.width - 25);
		var PBStartX:Number = this.x + this.width/2 - PBWidth/2;
		var PBStartY:Number = this.y + this.height/2 - 15;

		//Create the empty movie clips
		exportDialogMC = this.parentMC.createEmptyMovieClip("exportChartDialogBg", this.depth + 5);
		var exportDialogSubMC = exportDialogMC.createEmptyMovieClip("InternalDialog", 1);
		//Create a black overlay rectangle
		exportDialogMC.beginFill(0x000000,20);
		exportDialogMC.moveTo(this.x, this.y);
		exportDialogMC.lineTo(this.x + this.width, this.y);
		exportDialogMC.lineTo(this.x + this.width, this.y + this.height);
		exportDialogMC.lineTo(this.x, this.y + this.height);
		exportDialogMC.lineTo(this.x, this.y);
		
		//The main dialog at center of center
		var pad:Number = 20;
		exportDialogSubMC.beginFill(parseInt(this.params.exportDialogColor, 16),100);
		exportDialogSubMC.lineStyle(1, parseInt(this.params.exportDialogBorderColor,16), 100);
		exportDialogSubMC.moveTo(PBStartX - pad, PBStartY - pad);
		exportDialogSubMC.lineTo(PBStartX  + PBWidth + pad, PBStartY - pad);
		exportDialogSubMC.lineTo(PBStartX  + PBWidth + pad, PBStartY + 40 + pad);
		exportDialogSubMC.lineTo(PBStartX  - pad , PBStartY + 40 + pad);
		exportDialogSubMC.lineTo(PBStartX - pad, PBStartY - pad);
		
		//Add shadow the the dialog
		var shadowfilter:DropShadowFilter = new DropShadowFilter(2, 45, 0x333333, 0.8, 8, 8, 1, 1, false, false, false);
		exportDialogSubMC.filters = [shadowfilter];
		
		//Capture mouse event from everything otherwise underneath
		exportDialogMC.useHandCursor = false;
		exportDialogMC.onRollOver = function(){
		}
		
		//Instantiate the progress bar
		this.exportDialogPB = new FCProgressBar(this.parentMC, this.depth+6, 0, 100, PBStartX, PBStartY, PBWidth, 15, this.params.exportDialogPBColor, this.params.exportDialogPBColor, 1);
		
		//Create the text
		this.exportDialogTF = Utils.createText (false, this.params.exportDialogMessage, this.parentMC, this.depth+7, this.x + this.width/2, this.y + this.height/2 + 15, null, {align:"center", vAlign:"bottom", bold:false, italic:false, underline:false, font:"Verdana", size:10, color:this.params.exportDialogFontColor, isHTML:true, leftMargin:0, letterSpacing:0, bgColor:"", borderColor:""}, true, PBWidth, 40).tf;		
		
		//Set an empty context menu during export
		var cmEmpty:ContextMenu =  new ContextMenu();
		cmEmpty.hideBuiltInItems();		
		exportDialogMC.menu = cmEmpty;
	}
	/**
	 * Updates the progress of capture in export chart dialog box
	 * @param	percentValue	Current state of capture progress
	 */
	private function exportDialogUpdate(percentValue:Number) {
		//Get the text format of text field
		var tFormat:TextFormat = exportDialogTF.getTextFormat();
		//Update the text field
		exportDialogTF.htmlText = "<font face='Verdana' size='10' color='#" + this.params.exportDialogFontColor + "'>" + this.params.exportDialogMessage + percentValue + "%</font>";
		exportDialogTF.setTextFormat(tFormat);
		//Set the value of progress bar
		exportDialogPB.setValue(percentValue);
	}
	/**
	 * Hides the dialog box once the capture process has completed.
	 */
	private function exportDialogHide() {
		//Remove all progress bar related movie clips
		exportDialogPB.destroy();
		exportDialogTF.removeTextField();
		exportDialogMC.removeMovieClip();
	}
	
	//------- Methods aiding exporting for print --------//
	/**
	 * Prepares the chart's image as an object containing stream and other properties. 
	 * @returns	Returns whether the stream is being prepared.
	*/
	private function prepareImageDataStream():Boolean{
		//Proceed only if the chart has rendered
		if (this.hasChartRendered()){
			//If a previous instance is currently in progress, cancel the same first
			if (bmpPrintExport.isInProcess()){
				//Remove event listener
				bmpPrintExport.removeEventListener("onCaptureComplete", this);
				//Cancel it
				bmpPrintExport.cancel();
				//Set the cache property to original
				if(!this.cMC.skipBmpCacheCheck){
					this.exportResetPostSaving(this.cMC, exportPrintObjCache);
				}
			}
			
			//Create a new instance of BitmapSave 
			bmpPrintExport = new BitmapSave(this.cMC,this.x,this.y,this.width,this.height,0xffffff);	
			
			//Before we start capturing, we need to make sure that none of the movie clips
			//are cached as bitmap. So run a function that does this job.
			if(!this.cMC.skipBmpCacheCheck){
				exportPrintObjCache = this.exportSetPreSaving(this.cMC);
			}
			
			//Capture the bitmap now.
			this.log("Image Stream Capture Start", "The chart has started capturing image as data stream.", Logger.LEVEL.INFO);
			bmpPrintExport.capture();
			
			//Now that the bitmap is captured, we need to set the cache property to original
			if(!this.cMC.skipBmpCacheCheck){
				this.exportResetPostSaving(this.cMC, exportPrintObjCache)
			}
			
			//Add the event listeners			
			bmpPrintExport.addEventListener("onCaptureComplete", this);
			//Return true indicating that the stream is being prepared
			return true;
		}else{
			//Return false as the chart has not rendered yet
			return false;
		}
	}
	
	/**
	 * Invoked when the export (for printing) has completed. Here, the 
	 * data is processed and then redirected to processImageDataStream
	*/
	private function onCaptureComplete(eventObj:Object):Void{
		//Object to store all export properties 
		var expO:Object = new Object();
		//Capturing is complete. Store in return object.
		expO.stream = eventObj.out;
		//Send control to processImageDataStream for processing it.
		this.processImageDataStream(expO);			
	}
	
	/**
	 * This method actually returns the chart's prepared image stream, along
	 * with meta information to a pre-defined method FC_ImageStreamReady
	*/
	private function processImageDataStream(exportObject:Object){
		//Log that it has finished
		this.log("Image Stream Capture End", "The chart has finished capturing image as data stream.", Logger.LEVEL.INFO);
		//Create object for return
		var out:Object = new Object();
		out.stream = exportObject.stream;
		//Append the meta information
		out.meta = new Object();
		out.meta.caption = this.params.caption;
		out.meta.width = this.width;
		out.meta.height = this.height;
		out.meta.bgColor = "FFFFFF";
		out.meta.DOMId = this.DOMId;
		//Now, transfer it to the JS method
		if (this.registerWithJS==true && ExternalInterface.available){
			ExternalInterface.call ("FC_ImageStreamReady", out);
			ExternalInterface.call("__fusioncharts_event", {type:"imagestreamready", sender:this.DOMId}, {stream:exportObject.stream, caption:this.params.caption, width:this.width, height:this.height, bgColor:"FFFFFF"});
		}
	}
	
	//------- Common methods aiding --------//
	/**
	 * This method sets the bitmap caching of all objects in the chart
	 * so as to avoid freezing of interface.
	*/
	private function exportSetPreSaving(mc:MovieClip):Array{
		//Get the list of filters.
		var arrMcFilters:Array = new Array()
		//Iterate through each movie clip
		for(var i in mc){
			//Work only if it's a movie clip.
			if(mc[i] instanceof MovieClip){
				//Store the filters for this MC
				arrMcFilters[i] = new Array();
				arrMcFilters[i]['filters'] = mc[i].filters;
				mc[i].filters = [];
				//Store the cache property
				arrMcFilters[i]['cache'] = mc[i].cacheAsBitmap;
				mc[i].cacheAsBitmap = false;
				//Store children
				arrMcFilters[i]['children'] = arguments.callee(mc[i]);
			}
		}
		//Return the array
		return arrMcFilters;
	}
	/**
	 * This method restores the bitmap caching state of all the objects
	 * in the chart, once capturing is done.
	*/
	private function exportResetPostSaving(mc:MovieClip, arrMcFilters:Array){
		for(var i in arrMcFilters){			
			mc[i].filters = arrMcFilters[i]['filters'];
			mc[i].cacheAsBitmap = arrMcFilters[i]['cache'];
			arguments.callee(mc[i],arrMcFilters[i]['children']);
		}
	}
	// -------------------- UTILITY METHODS --------------------//
	/**
	* getAttributesArray method helps convert the list of attributes
	* for an XML node into an array.
	* Reason:
	* Starting ActionScript 2, the parsing of XML attributes have also
	* become case-sensitive. However, prior versions of FusionCharts
	* supported case-insensitive attributes. So we need to parse all
	* attributes as case-insensitive to maintain backward compatibility.
	* To do so, we first extract all attributes from XML, convert it into
	* lower case and then store it in an array. Later, we extract value from
	* this array.
	* Once this array is returned, IT'S VERY NECESSARY IN THE CALLING CODE TO
	* REFERENCE THE NAME OF ATTRIBUTE IN LOWER CASE (STORED IN THIS ARRAY).
	* ELSE, UNDEFINED VALUE WOULD SHOW UP.
	*	@param	xmlNd	XML Node for which we've to get the attributes.
	*	@return		An associative array containing the attributes. The name
	*					of attribute (in all lower case) is the key and attribute value
	*					is value.
	*/
	private function getAttributesArray (xmlNd : XMLNode) : Array
	{
		//Array that will store the attributes
		var	atts : Array = new Array ();
		//Object used to iterate through the attributes collection
		var obj : Object;
		//Iterate through each attribute in the attributes collection,
		//convert to lower case and store it in array.
		for (obj in xmlNd.attributes)
		{
			//Store it in array
			atts [obj.toString ().toLowerCase ()] = xmlNd.attributes [obj];
		}
		//Return the array
		return atts;
	}
	/**
	* returnDataAsElement method returns the data passed to this
	* method as an Element Object. We store each chart element as an
	* obejct to unify the various properties.
	*	@param	x		Start X of the element
	*	@param	y		Start Y of the element
	*	@param	w		Width of the element
	*	@param	h		Height of the element
	*	@return		Object representing the element
	*/
	private function returnDataAsElement (x : Number, y : Number, w : Number, h : Number) : Object
	{
		//Create new object
		var element : Object = new Object ();
		element.x = x;
		element.y = y;
		element.w = w;
		element.h = h;
		//Calculate and store toX and toY
		element.toX = x + w;
		element.toY = y + h;
		//Return
		return element;
	}
	/**
	* toBoolean method converts numeric form (1,0) to Flash
	* boolean.
	*	@param	num		Number (0,1)
	*	@return		Boolean value based on above number
	*/
	private function toBoolean (num : Number) : Boolean
	{
		return ((num == 1) ? (true) : (false));
	}
	/**
	* invokeLink method invokes a link for any defined drill down item.
	* A link in XML needs to be URL Encoded. Also, there are a few prefixes,
	* parameters that can be added to link so as to defined target link
	* opener object. For e.g., a link can open in same window, new window,
	* frame, pop-up window. Or, a link can invoke a JavaScript method.
	* Prefixes can be N - new window, F - Frame, P - Pop up
	*	@param	strLink	Link to be invoked.
	*/
	private function invokeLink (strLink : String) : Void
	{
		//We continue only if the link is not empty
		if (strLink != undefined && strLink != null && strLink != "")
		{
			//Unescape the link - as it might be URL Encoded
			strLink = (this.params.unescapeLinks)?(unescape (strLink)):(strLink);
			//Store a copy of the link
			var linkURL:String = strLink;
			//Whether link click event has to be raised
			var raiseEvent:Boolean = true;
			//Now based on what the first character in the link is (N, F, P, S, J)
			//we invoke the link.
			if (strLink.charAt (0).toUpperCase () == "N" && strLink.charAt (1) == "-")
			{
				//Means we have to open the link in a new window.
				linkURL = strLink.slice (2);
				getURL (linkURL , "_blank");
			} else if (strLink.charAt (0).toUpperCase () == "F" && strLink.charAt (1) == "-")
			{
				//Means we have to open the link in a frame.
				var dashPos : Number = strLink.indexOf ("-", 2);
				//strLink.slice(dashPos+1) indicates link
				linkURL = strLink.slice (dashPos + 1);
				//strLink.substr(2, dashPos-2) indicates frame name
				getURL (linkURL , strLink.substr (2, dashPos - 2));
			} else if (strLink.charAt (0).toUpperCase () == "P" && strLink.charAt (1) == "-")
			{
				//Means we have to open the link in a pop-up window.
				var dashPos : Number = strLink.indexOf ("-", 2);
				var commaPos : Number = strLink.indexOf (",", 2);
				linkURL = strLink.slice (dashPos + 1);
				getURL ("javaScript:var " + strLink.substr (2, commaPos - 2) + " = window.open('" + linkURL + "','" + strLink.substr (2, commaPos - 2) + "','" + strLink.substr (commaPos + 1, dashPos - commaPos - 1) + "'); " + strLink.substr (2, commaPos - 2) + ".focus(); void(0);");
			} else if (strLink.charAt (0).toUpperCase () == "J" && strLink.charAt (1) == "-") {
				//We can operate JS link only if ExternalInterface is available and the chart 
				//has been registered
				if (this.registerWithJS==true &&  ExternalInterface.available){
					//Means we have to open the link as JavaScript
					var dashPos : Number = strLink.indexOf ("-", 2);
					//strLink.slice(dashPos+1) indicates arguments if any
					//strLink.substr(2, dashPos-2) indicates link
					//If no arguments, just call the link
					if (dashPos == -1) {
						linkURL = strLink.substr(2, strLink.length-2);
						ExternalInterface.call(strLink.substr(2, strLink.length-2));
					} else {
						//There could be multiple parameters. We just pass them as a single string to JS method.
						linkURL = strLink.substr(2, dashPos-2);
						ExternalInterface.call(strLink.substr(2, dashPos-2), strLink.slice(dashPos+1));
					}
				}
			} else if (strLink.substr(0,8).toUpperCase()=="NEWCHART"){ 
				//Linked chart.				
				//Find the second dash position
				var dashPos : Number = strLink.indexOf ("-", 9);
				//Figure out whether the sub-linked chart is xml or url
				var linkType:String = strLink.substring(9, dashPos).toUpperCase();
				//Set flag that individual link-click event doesn't have to be invoked
				raiseEvent = false;
				//Based on linkType, take appropriate action
				if (linkType=="XMLURL"){
					//Extract linked XML URL
					var linkedDataURL:String = strLink.substring(dashPos+1, strLink.length);
					//If it's URL, just invoke linked chart event
					ExternalInterface.call("__fusioncharts_event", {type:"linkedchartinvoked", sender:this.DOMId}, {linkType:"XMLURL", data:linkedDataURL});
				}else if (linkType=="JSONURL"){
					//Extract linked JSON URL
					var linkedDataURL:String = strLink.substring(dashPos+1, strLink.length);
					//If it's URL, just invoke linked chart event
					ExternalInterface.call("__fusioncharts_event", {type:"linkedchartinvoked", sender:this.DOMId}, {linkType:"JSONURL", data:linkedDataURL});
				} else if (linkType=="XML" || linkType=="JSON"){
					//Extract linked data identifier
					var linkedDataId:String = strLink.substring(dashPos+1, strLink.length);
					//Get the data for the linked chart 
					var linkedData:String = getLinkedChartDataXML(linkedDataId);
					//If linkedData is empty string, it means data identifier or linked data was not found
					//Raise event
					ExternalInterface.call("__fusioncharts_event", {type:"linkedchartinvoked", sender:this.DOMId}, {linkType:"XML", data:linkedData});
				}
			} else if (strLink.charAt (0).toUpperCase () == "S" && strLink.charAt (1) == "-") {
				//Means we have to convey the link as a event to parent Flash movie				
				this.dispatchEvent({type:"linkClicked", target:this, link:strLink.slice (2)});				
				//Link event doesn't have to be raised in this
				raiseEvent = false;
			} else {
				//Open the link in same window
				getURL (strLink, "_self");
			}
			//If link event is to be raised, raise.
			if (raiseEvent && this.registerWithJS==true &&  ExternalInterface.available){
				ExternalInterface.call("__fusioncharts_event", {type:"linkclicked", sender:this.DOMId}, {linkProvided:strLink, linkInvoked:strLink});
			}
		}
	}
	/**
	* renderAppMessage method helps display an application message to
	* end user.
	* @param	strMessage	Message to be displayed
	* @return				Reference to the text field created
	*/
	private function renderAppMessage (strMessage : String) : TextField 
	{
		//We must render all messages to cMC in order to be captured when exported as image.
		//in an unknown scenario when, the the cMC is not defined. checking implied - Very unlikely to happen.
		//[Previously all messages were rendered on the root. (this.parentMC)]
		var containerClip:MovieClip = (!isNaN(this.cMC.getDepth())) ? this.cMC : this.parentMC;
		return _global.createBasicText (strMessage, containerClip, depth, this.x + (this.width / 2) , this.y + (this.height / 2) , "Verdana", 10, _root.LPBarTextColor, "center", "bottom");
	}
	/**
	* removeAppMessage method removes the displayed application message
	* @param	tf	Text Field reference to the message
	*/
	private function removeAppMessage (tf : TextField)
	{
		tf.removeTextField ();
	}
	// --------------------- VISUAL RENDERING METHODS ------------------//
	/**
	* drawBackground method renders the chart background. The background
	* cant be solid color or gradient. All charts have a backround. So, we've
	* defined drawBackground in Chart class itself, so that sub classes can
	* directly access it (as it's common).
	*	@return		Nothing
	*/
	private function drawBackground () : Void
	{
		//Create a new movie clip container for background
		var bgMC = this.cMC.createEmptyMovieClip ("Background", this.dm.getDepth ("BACKGROUND"));
		//Parse the color, alpha and ratio array
		var bgColor : Array = ColorExt.parseColorList (this.params.bgColor);
		var bgAlpha : Array = ColorExt.parseAlphaList (this.params.bgAlpha, bgColor.length);
		var bgRatio : Array = ColorExt.parseRatioList (this.params.bgRatio, bgColor.length);
		
		//Create matrix object
		var matrix : Object = {
			matrixType : "box", w : this.width, h : this.height, x : - (this.width / 2) , y : - (this.height / 2) , r : MathExt.toRadians (this.params.bgAngle)
		};
		//When border alpha is zero, no need to draw border.
		this.params.showBorder =  (this.params.borderAlpha == 0)? false : this.params.showBorder;
		//If border is to be shown 
		if (this.params.showBorder){
			bgMC.lineStyle (this.params.borderThickness, parseInt (this.params.borderColor, 16) , this.params.borderAlpha);
		}
		//Border thickness half
		var bth : Number = (this.params.showBorder)?(this.params.borderThickness / 2):(0);
		//Start the fill.
		bgMC.beginGradientFill ("linear", bgColor, bgAlpha, bgRatio, matrix);
		//Move to (-w/2, 0); - 0,0 registration point at center (x,y)
		bgMC.moveTo ( - (this.width / 2) + bth, - (this.height / 2) + bth);
		//Draw the rectangle with center registration point
		bgMC.lineTo (this.width / 2 - bth, - (this.height / 2) + bth);
		bgMC.lineTo (this.width / 2 - bth, this.height / 2 - bth);
		bgMC.lineTo ( - (this.width / 2) + bth, this.height / 2 - bth);
		bgMC.lineTo ( - (this.width / 2) + bth, - (this.height / 2) + bth);
		//Set the x and y position
		bgMC._x = this.width / 2;
		bgMC._y = this.height / 2;
		//End Fill
		bgMC.endFill ();
		//Apply animation
		if (this.params.animation)
		{
			this.styleM.applyAnimation (bgMC, this.objects.BACKGROUND, this.macro, bgMC._x, - this.width / 2, bgMC._y, - this.height / 2, 100, 100, 100, null);
		}
		//Apply filters
		this.styleM.applyFilters (bgMC, this.objects.BACKGROUND);		
	}
	/**
	* loadBgSWF method loads the background .swf file (if required) and also
	* loads the logo for the chart, if specified.
	*/
	private function loadBgSWF () : Void
	{
		//Load the BG SWF only if it has been specified and it doesn't contain any colon characters
		//(to disallow XSS attacks)
		if (this.params.bgSWF != "")
		{
			if (this.params.bgSWF.indexOf(":")==-1 && this.params.bgSWF.indexOf("%3A")==-1){
				// Background swf holder
				this.bgSWFMC = this.cMC.createEmptyMovieClip ("BgSWF", this.dm.getDepth ("BGSWF"));		
				// create ImgMC movie clip
				var imgMC:MovieClip = this.bgSWFMC.createEmptyMovieClip("ImgMC", 1); 	
				//Local reference to class
				var cr = this;
				
				this.bgSWFMCListener.onLoadInit = function(targetMC:MovieClip)
				{					
					var displayMode:String = cr.params.bgImageDisplayMode.toLowerCase();
					//scaling parameter works only for 'none', 'center' and 'tile' modes of display. 						
					if(displayMode == "none" || displayMode == "center" || displayMode == "tile")
					{
						
						ImageUtils.setPropOf(targetMC, {propName:"bgImageScale", value:cr.params.bgImageScale});
					}					
					// set display mode of image
					var modifyImg:MovieClip = ImageUtils.setDisplayMode(cr.params.bgSWF,
																		   targetMC,
																		   cr.bgSWFMC, 
																		   cr.width,
																		   cr.height,
																		   cr.params.borderThickness,
																		   cr.params.bgImageDisplayMode);
					
					// set alpha properties of image
					ImageUtils.setPropOf(modifyImg, {propName:"bgSWFAlpha", value:cr.params.bgSWFAlpha});
					// alignment attributes are ignored for display mode options of 'center' and 'stretch'.
					if(displayMode != "center" && displayMode != "stretch")
					{
						// set alignment properties
						ImageUtils.setAlignment(modifyImg, cr.width, cr.height, cr.params.borderThickness, cr.params.bgImageVAlign, cr.params.bgImageHAlign);
						
					}					
					// delete
					delete modifyImg;
					delete displayMode;
				}
				this.bgSWFMCListener.onLoadError = function(target_mc:MovieClip, errorCode:String, httpStatus:Number) {
					//This event indicates that there was an error in loading the bgSWF.
					//So, we just log to the logger.
					this.log ("bgSWF not loaded", "The bgSWF could not be loaded. Please check that the path for bgSWF specified in XML is valid and refers to the same sub-domain as this chart. Else, there could be network problem.", Logger.LEVEL.ERROR);
				}
				//Add the listener to loader
				this.bgSWFMCLoader.addListener(this.bgSWFMCListener);
				//Now, load the bgSWF
				this.bgSWFMCLoader.loadClip(this.params.bgSWF, imgMC); 
				
			}
			else
			{
				this.log ("bgSWF not loaded", "The bgSWF path contains special characters like colon, which can be potentially dangerous in XSS attacks. As such, FusionCharts has not loaded the bgSWF. If you've specified the absolute path for bgSWF URL, we recommend specifying relative path under the same domain.", Logger.LEVEL.ERROR);
			}
		}
		//Now load the logo for the chart.
		if (this.params.logoURL != "") {
			//Create the listeners for the loader first. We need to deal with error and finish
			//handlers only
			//Local reference to class
			var cr = this;
			this.logoMCListener.onLoadInit = function(target_mc:MovieClip) {
				//This listener is invoked when the logo has finished loading.
				//Set the scale first, as position will then depend on scale
				target_mc._xscale = cr.params.logoScale;
				target_mc._yscale = cr.params.logoScale;
				//Now position the loader's container movie clip as per
				//the position specified in XML.
				switch(cr.params.logoPosition.toUpperCase()) {
					case "TR":
					target_mc._x = cr.x + cr.width - target_mc._width - cr.params.borderThickness;
					target_mc._y = cr.y + cr.params.borderThickness;
					break;
					case "BR":
					target_mc._x = cr.x + cr.width - target_mc._width - cr.params.borderThickness;
					target_mc._y = cr.y + cr.height - target_mc._height - cr.params.borderThickness;
					break;
					case "BL":
					target_mc._x = cr.x + cr.params.borderThickness;
					target_mc._y = cr.y + cr.height - target_mc._height - cr.params.borderThickness;
					break;
					case "CC":
					target_mc._x = cr.x + (cr.width/2) - (target_mc._width/2);
					target_mc._y = cr.y + (cr.height/2) - (target_mc._height/2);
					break;
					default:
					//Also handles TL
					target_mc._x = cr.x + cr.params.borderThickness;
					target_mc._y = cr.y + cr.params.borderThickness;
					break;
				}
				//Also, we apply the alpha.
				target_mc._alpha = cr.params.logoAlpha;
				//Set the link, if needed.
				if (cr.params.logoLink != "") {
					target_mc.useHandCursor = true;
					target_mc.onRelease = function() {
						cr.invokeLink(cr.params.logoLink);
					}
				}
			}
			this.logoMCListener.onLoadError = function(target_mc:MovieClip, errorCode:String, httpStatus:Number) {
				//This event indicates that there was an error in loading the logo.
				//So, we just log to the logger.
				cr.log ("Logo not loaded", "The logo could not be loaded. Please check that the path for logo specified in XML is valid and refers to the same sub-domain as this chart. Else, there could be network problem.", Logger.LEVEL.ERROR);
			}
			//Add the listener to loader
			this.logoMCLoader.addListener(this.logoMCListener);
			//Now, load the logo
			this.logoMCLoader.loadClip(this.params.logoURL, this.logoMC);
		}
	}
	/**
	* Draws or hides the overlay button.
	*	@param	objParams	Object containing parameters for the overlay button.
	*						- id, show, message, borderColor, bgColor, font, fontColor,
	*						- fontSize, bold, padding, callback
	*/
	private function drawOverlayButton(objParams:Object){
		//Store parameters in instance var
		this.overlayButtonParams = objParams;
		//If the button is to be drawn
		if (objParams.show==1 || objParams.show==true){
			//Create button overlay movie clip
			this.btnMC = parentMC.createEmptyMovieClip("OverlayButton", 10);
			//Create a textfield inside the movie clip with required text.
			//Create style object that adapts colors from themes			
			var txtStyle:Object = {align:"right", vAlign:"bottom", bold:getFV(objParams.bold, true), italic:false, underline:false, font:getFV(objParams.font, "Verdana"), size:getFN(objParams.fontSize, 11), color:getFV(objParams.fontColor, (defColors2D==true)?(this.defColors.get2DBaseFontColor (this.params.palette)):(this.defColors.getBaseFontColor3D(this.params.palette))), leftMargin:0, letterSpacing:0, isHTML:false, bgColor: getFV(objParams.bgColor, ((defColors2D==true)?(ColorExt.getLightColor(this.defColors.get2DBaseFontColor (this.params.palette), 0.1).toString(16)):(ColorExt.getLightColor(this.defColors.getBaseFontColor3D(this.params.palette), 0.1).toString(16)))), borderColor: getFV(objParams.borderColor,((defColors2D==true)?(ColorExt.getLightColor(this.defColors.get2DBaseFontColor (this.params.palette), 0.7).toString(16)):(ColorExt.getLightColor(this.defColors.getBaseFontColor3D(this.params.palette), 0.7).toString(16))))};
			//Default value for padding
			var padding:Number = getFN(objParams.padding, 0);
			//Create text
			Utils.createText(false, getFV(objParams.message, "Back"), this.btnMC, 1, this.width-padding-1, padding, null, txtStyle, false, 0, 0);
			//Local reference to class
			var cr = this;
			//Create the on-click handler
			btnMC.useHandCursor = true;
			btnMC.onRelease = function() {
				ExternalInterface.call("__fusioncharts_event", {type:"overlaybuttonclick", sender:cr.DOMId}, {id:objParams.id});
			}
		}else{
			//Hide the existing, assuming it's there.
			if (this.btnMC instanceof MovieClip){
				this.btnMC.removeMovieClip();
			}
		}
	}
	/**
	* drawClickURLHandler method draws the rectangle over the chart
	* that responds to click URLs. It draws only if clickURL has been
	* defined for the chart.
	*/
	private function drawClickURLHandler () : Void
	{
		//Check if it needs to be created
		if (this.params.clickURL != "")
		{
			//Create a new movie clip container for background
			var clickMC = this.cMC.createEmptyMovieClip ("ClickURLHandler", this.dm.getDepth ("CLICKURLHANDLER"));
			clickMC.moveTo (0, 0);
			//Set fill with 0 alpha
			clickMC.beginFill (0xffffff, 0);
			//Draw the rectangle
			clickMC.lineTo (this.width, 0);
			clickMC.lineTo (this.width, this.height);
			clickMC.lineTo (0, this.height);
			clickMC.lineTo (0, 0);
			//End Fill
			clickMC.endFill ();
			clickMC.useHandCursor = true;
			//Set click handler
			var strLink : String = this.params.clickURL;
			var chartRef : Chart = this;
			clickMC.onMouseDown = function ()
			{
				chartRef.invokeLink (strLink);
			}
			clickMC.onRollOver = function ()
			{
				//Empty function just to show hand cursor				
			}
		}
	}
	// ---------- GENERIC TOOL-TIP RENDERER METHODS ---------//
	/**
	* setToolTipParam method sets the parameter for tool tip.
	*/
	private function setToolTipParam ()
	{
		//Get the style object for tool tip
		var tTipStyleObj : Object = this.styleM.getTextStyle (this.objects.TOOLTIP);
		this.tTip.setParams (tTipStyleObj.font, tTipStyleObj.size, tTipStyleObj.color, tTipStyleObj.bgColor, tTipStyleObj.borderColor, tTipStyleObj.isHTML, this.params.showToolTipShadow);
		//Also, in Utils, set whether tool-tip has to be shown for wrapped labels
		//this.config.showToolTipForWrappedLabels over-rides tool-text for any
		//wrapped level chart wide
		if (this.params.showToolTipForWrappedLabels && (this.config.showToolTipForWrappedLabels!=false)){
			Utils.APP_TOOLTIP = this.tTip;
		}
		//Also set whether ellipses are to be added to labels, when overflowing
		Utils.ADD_ELLIPSES = this.params.useEllipsesWhenOverflow;
	}
	/**
	 * Shows the tooltip for any arbitrary object on the chart.
	 * This method is mostly called by delegates, and as such the
	 * tool-text is contained in tooltext property of the delegated
	 * function.
	 */
	private function showToolTip():Void {
		//The text to be shown as tooltip is contained as tooltext.
		var strToolText : String = arguments.caller.toolText;
		//Set tool tip text
		this.tTip.setText (strToolText);
		//Show the tool tip
		this.tTip.show ();
	}
	/**
	 * When the tooltip is being shown at the chart level, this method
	 * repositions the same (if visible).
	 */
	private function repositionToolTip():Void {
		//Reposition the tool tip only if it's in visible state
		if (this.tTip.visible ())
		{
			this.tTip.rePosition ();
		}
	}
	/**
	 * Hides the tooltip when it's no more required.
	 */
	private function hideToolTip():Void {
		//Hide the tool tip
		this.tTip.hide ();
	}
	// ---------- NUMBER DETECTION, FORMATTING RELATED METHODS -------//
	/**
	* detectNumberScales method detects whether we've been provided
	* with number scales. If yes, we parse them. This method needs to
	* called before calculatePoints, as calculatePoint methods calls
	* formatNumber, which in turn uses number scales.
	* Here, we also adjust the numberPrefix and numberSuffix if they're
	* still present in encoded form. Like, if the user has specified encoded
	* numberPrefix and Suffix in dataURL mode, it will show the direct value.
	* Therefore, we need to change them into proper characters.
	* Finally, we set the proper number of decimals for the chart based on the
	* decimal input.
	*	@return	Nothing.
	*/
	private function detectNumberScales () : Void
	{
		//Check if either has been defined
		if (this.params.numberScaleValue.length == 0 || this.params.numberScaleUnit.length == 0 || this.params.formatNumberScale == false)
		{
			//Set flag to false
			this.config.numberScaleDefined = false;
		} else 
		{
			//Set flag to true
			this.config.numberScaleDefined = true;
			//Split the data into arrays
			this.config.nsv = new Array ();
			this.config.nsu = new Array ();
			//Parse the number scale value
			this.config.nsv = this.params.numberScaleValue.split (",");
			//Convert all number scale values to numbers as they're
			//currently in string format.
			var i : Number;
			for (i = 0; i < this.config.nsv.length; i ++)
			{
				this.config.nsv [i] = Number (this.config.nsv [i]);
				//If any of numbers are NaN, set defined to false
				if (isNaN (this.config.nsv [i]))
				{
					this.config.numberScaleDefined = false;
				}
			}
			//Parse the number scale unit
			this.config.nsu = this.params.numberScaleUnit.split (",");
			//If the length of two arrays do not match, set defined to false.
			if (this.config.nsu.length != this.config.nsv.length)
			{
				this.config.numberScaleDefined = false;
			}
		}
		//Convert numberPrefix and numberSuffix now.
		this.params.numberPrefix = this.unescapeChar (this.params.numberPrefix);
		this.params.numberSuffix = this.unescapeChar (this.params.numberSuffix);
		//Always keep to a decimal precision of minimum 2 if the number
		//scale is defined, as we've just checked for decimal precision of numbers
		//and not the numbers against number scale. So, even if they do not need yield a
		//decimal, we keep 2, as we do not force decimals on numbers.
		if (this.config.numberScaleDefined == true)
		{
			maxDecimals = (maxDecimals > 2) ?maxDecimals : 2;
		}
		//Get proper value for decimals
		this.params.decimals = Number (getFV (this.params.decimals, maxDecimals));
		//Decimal Precision cannot be less than 0 - so adjust it
		if (this.params.decimals < 0)
		{
			this.params.decimals = 0;
		}
		//Get proper value for yAxisValueDecimals
		this.params.yAxisValueDecimals = Number (getFV (this.params.yAxisValueDecimals, this.params.decimals));
		this.params.sYAxisValueDecimals = Number (getFV (this.params.sYAxisValueDecimals, this.params.yAxisValueDecimals));
	}
	/**
	* unescapeChar method helps to unescape certain escape characters
	* which might have got through the XML. Like, %25 is escaped back to %.
	* This function would be used to format the number prefixes and suffixes.
	*	@param	strChar		The character or character sequence to be unescaped.
	*	@return			The unescaped character
	*/
	private function unescapeChar (strChar : String) : String
	{
		//Perform only if strChar is defined
		if (strChar == "" || strChar == undefined)
		{
			return "";
		}
		//If it doesnt contain a %, return the original string
		if (strChar.indexOf ("%") == - 1)
		{
			return strChar;
		}
		//We're not doing a case insensitive search, as there might be other
		//characters provided in the Prefix/Suffix, which need to be present in lowe case.
		//Create the conversion table.
		var cTable : Array = new Array ();
		cTable.push (
		{
			char : "%", encoding : "%25"
		});
		cTable.push (
		{
			char : "&", encoding : "%26"
		});
		cTable.push (
		{
			char : "£", encoding : "%A3"
		});
		cTable.push (
		{
			char : "€", encoding : "%E2%82%AC"
		});
		//v2.3 Backward compatible Euro
		cTable.push (
		{
			char : "€", encoding : "%80"
		});
		cTable.push (
		{
			char : "¥", encoding : "%A5"
		});
		cTable.push (
		{
			char : "¢", encoding : "%A2"
		});
		cTable.push (
		{
			char : "₣", encoding : "%E2%82%A3"
		});
		cTable.push (
		{
			char : "+", encoding : "%2B"
		});
		cTable.push (
		{
			char : "#", encoding : "%23"
		});
		//Loop variable
		var i : Number;
		//Return string (escaped)
		var rtnStr : String = strChar;
		for (i = 0; i < cTable.length; i ++)
		{
			if (strChar == cTable [i].encoding)
			{
				//If the given character matches the encoding, convert to character
				rtnStr = cTable [i].char;
				break;
			}
		}
		//Return it
		return rtnStr;
		//Clean up
		delete cTable;
	}
	/**
	* formatNumber method helps format a number as per the user
	* requirements.
	* Requires this.config.numberScaleDefined to be defined (boolean)
	*	@param		intNum				Number to be formatted
	*	@param		bFormatNumber		Flag whether we've to format
	*									decimals and add commas
	*	@param		decimalPrecision	Number of decimal places we need to
	*									round the number to.
	*	@param		forceDecimals		Whether we force decimal padding.
	*	@param		bFormatNumberScale	Flag whether we've to format number
	*									scale
	*	@param		defaultNumberScale	Default scale of the number provided.
	*	@param		numScaleValues		Array of values (for scaling)
	*	@param		numScaleUnits		Array of Units (for scaling)
	*	@param		numberPrefix		Number prefix to be added to number.
	*	@param		numberSuffix		Number Suffix to be added.
	*	@return						Formatted number as string.
	*
	*/
	private function formatNumber (intNum : Number, bFormatNumber : Boolean, decimalPrecision : Number, forceDecimals : Boolean, bFormatNumberScale : Boolean, defaultNumberScale : String, numScaleValues : Array, numScaleUnits : Array, numberPrefix : String, numberSuffix : String) : String 
	{
		//First, if number is to be scaled, scale it
		//Number in String format
		var strNum : String = String (intNum);
		//Number Scale
		var strScale : String
		if (bFormatNumberScale)
		{
			strScale = defaultNumberScale;
		}else
		{
			strScale = "";
		}
		if (bFormatNumberScale && this.config.numberScaleDefined)
		{
			//Get the formatted scale and number
			var objNum : Object = formatNumberScale (intNum, defaultNumberScale, numScaleValues, numScaleUnits);
			//Store from return in local primitive variables
			strNum = String (objNum.value);
			intNum = objNum.value;
			strScale = objNum.scale;
		}
		//Now, if we've to format the decimals and commas
		if (bFormatNumber)
		{
			//Format decimals
			strNum = formatDecimals (intNum, decimalPrecision, forceDecimals);
			//Format commas now
			strNum = formatCommas (strNum);
		}
		//Now, add scale, number prefix and suffix
		strNum = numberPrefix + strNum + strScale + numberSuffix;
		return strNum;
	}
	/**
	* formatNumberScale formats the number as per given scale.
	* For example, if number Scale Values are 1000,1000 and
	* number Scale Units are K,M, this method will divide any
	* value over 1000000 using M and any value over 1000 (<1M) using K
	* so as to give abbreviated figures.
	* Number scaling lets you define your own scales for numbers.
	* To clarify further, let's consider an example. Say you're plotting
	* a chart which indicates the time taken by a list of automated
	* processes. Each process in the list can take time ranging from a
	* few seconds to few days. And you've the data for each process in
	* seconds itself. Now, if you were to show all the data on the chart
	* in seconds only, it won't appear too legible. What you can do is
	* build a scale of yours and then specify it to the chart. A scale,
	* in human terms, would look something as under:
	* 60 seconds = 1 minute
	* 60 minute = 1 hr
	* 24 hrs = 1 day
	* 7 days = 1 week
	* First you would need to define the unit of the data which you're providing.
	* Like, in this example, you're providing all data in seconds. So, default
	* number scale would be represented in seconds. You can represent it as under:
	* <graph defaultNumberScale='s' ...>
	* Next, the scale for the chart is defined as under:
	* <graph numberScaleValue='60,60,24,7' numberScaleUnit='min,hr,day,wk' >
	* If you carefully see this and match it with our range, whatever numeric
	* figure was present on the left hand side of the range is put in
	* numberScaleValue and whatever unit was present on the right side of
	* the scale has been put under numberScaleUnit - all separated by commas.
	
	*	@param	intNum				The number to be scaled.
	*	@param	defaultNumberScale	Scale of the number provided.
	*	@param	numScaleValues		Incremental list of values (divisors) on
	*								which the number will be scaled.
	*	@param
	*/
	private function formatNumberScale (intNum : Number, defaultNumberScale : String, numScaleValues : Array, numScaleUnits : Array) : Object 
	{
		//Create an object, which will be returned
		var objRtn : Object = new Object ();
		//Scale Unit to be stored (assume default)
		var strScale : String = defaultNumberScale;
		var i : Number = 0;
		//If the scale unit or values have something fed in them
		//we manipulate the scales.
		for (i = 0; i < numScaleValues.length; i ++)
		{
			if (Math.abs (Number (intNum)) >= numScaleValues [i])
			{
				strScale = numScaleUnits [i];
				intNum = Number (intNum) / numScaleValues [i];
			} else 
			{
				break;
			}
		}
		//Set the values as properties of objRtn
		objRtn.value = intNum;
		objRtn.scale = strScale;
		return objRtn;
	}
	/**
	* formatDecimals method formats the decimal places of a number.
	* Requires the following to be defined:
	* this.params.decimalSeparator
	* this.params.thousandSeparator
	*	@param	intNum				Number on which we've to work.
	*	@param	decimalPrecision	Number of decimal places to which we've
	*								to format the number to.
	*	@param	forceDecimals		Boolean value indicating whether to add decimal
	*								padding to numbers which are falling as whole
	*								numbers?
	*	@return					A number with the required number of decimal places
	*								in String format. If we return as Number, Flash will remove
	*								our decimal padding or un-wanted decimals.
	*/
	private function formatDecimals (intNum : Number, decimalPrecision : Number, forceDecimals : Boolean) : String 
	{
		//If no decimal places are needed, just round the number and return
		if (decimalPrecision <= 0)
		{
			return String (Math.round (intNum));
		}
		//Round the number to specified decimal places
		//e.g. 12.3456 to 3 digits (12.346)
		//Step 1: Multiply by 10^decimalPrecision - 12345.6
		//Step 2: Round it - i.e., 12346
		//Step 3: Divide by 10^decimalPrecision - 12.346
		var tenToPower : Number = Math.pow (10, decimalPrecision);
		var strRounded : String = String (Math.round (intNum * tenToPower) / tenToPower);
		//Now, strRounded might have a whole number or a number with required
		//decimal places. Our next job is to check if we've to force Decimals.
		//If yes, we add decimal padding by adding 0s at the end.
		if (forceDecimals)
		{
			//Add a decimal point if missing
			//At least one decimal place is required (as we split later on .)
			//10 -> 10.0
			if (strRounded.indexOf (".") == - 1)
			{
				strRounded += ".0";
			}
			//Finally, we start add padding of 0s.
			//Split the number into two parts - pre & post decimal
			var parts : Array = strRounded.split (".");
			//Get the numbers falling right of the decimal
			//Compare digits in right half of string to digits wanted
			var paddingNeeded : Number = decimalPrecision - parts [1].length;
			//Number of zeros to add
			for (var i = 1; i <= paddingNeeded; i ++)
			{
				//Add them
				strRounded += "0";
			}
		}
		return (strRounded);
	}
	/**
	* formatCommas method adds proper commas to a number in blocks of 3
	* i.e., 123456 would be formatted as 123,456
	*	@param	strNum	The number to be formatted (as string).
	*					Why are numbers taken in string format?
	*					Here, we are asking for numbers in string format
	*					to preserve the leading and padding 0s of decimals
	*					Like as in -20.00, if number is just passed as number,
	*					Flash automatically reduces it to -20. But, we've to
	*					make sure that we do not disturb the original number.
	*	@return		Formatted number with commas.
	*/
	private function formatCommas (strNum : String) : String 
	{
		//intNum would represent the number in number format
		var intNum : Number = Number (strNum);
		//If the number is invalid, return an empty value
		if (isNaN (intNum))
		{
			return "";
		}
		var strDecimalPart : String = "";
		var boolIsNegative : Boolean = false;
		var strNumberFloor : String = "";
		var formattedNumber : String = "";
		var startPos : Number = 0;
		var endPos : Number = 0;
		//Define startPos and endPos
		startPos = 0;
		endPos = strNum.length;
		//Extract the decimal part
		if (strNum.indexOf (".") != - 1)
		{
			strDecimalPart = strNum.substring (strNum.indexOf (".") + 1, strNum.length);
			endPos = strNum.indexOf (".");
		}
		//Now, if the number is negative, get the value into the flag
		if (intNum < 0)
		{
			boolIsNegative = true;
			startPos = 1;
		}
		//Now, extract the floor of the number
		strNumberFloor = strNum.substring (startPos, endPos);
		//Now, strNumberFloor contains the actual number to be formatted with commas
		// If it's length is greater than 3, then format it
		if (strNumberFloor.length > 3)
		{
			// Get the length of the number
			var lenNumber : Number = strNumberFloor.length;
			for (var i : Number = 0; i <= lenNumber; i ++)
			{
				//Append proper commans
				if ((i > 2) && ((i - 1) % 3 == 0))
				{
					formattedNumber = strNumberFloor.charAt (lenNumber - i) + this.params.thousandSeparator + formattedNumber; 
				} else 
				{
					formattedNumber = strNumberFloor.charAt (lenNumber - i) + formattedNumber; 
				}
			}
		} else 
		{
			formattedNumber = strNumberFloor; 
		}
		// Now, append the decimal part back
		if (strDecimalPart != "")
		{
			formattedNumber = formattedNumber + this.params.decimalSeparator + strDecimalPart; 
		}
		//Now, if neg num
		if (boolIsNegative == true)
		{
			formattedNumber = "-" + formattedNumber; 
		}
		//Return
		return formattedNumber;
	}
	/**
	* getSetValue method helps us check whether the given set value is specified.
	* If not, we take steps accordingly and return values.
	*	@param	num		Number (in string/object format) which we've to check.
	*	@return		Numeric value of the number. (or NaN)
	*/
	private function getSetValue (num) : Number
	{
		//If it's not a number, or if input separators characters
		//are explicity defined, we need to convert value.
		var setValue : Number;
		if (isNaN (num) || (this.params.inThousandSeparator != "") || (this.params.inDecimalSeparator != ""))
		{
			//Number in XML can be invalid or missing (discontinuous data)
			//So, if the length is undefined, it's missing.
			if (num.length == undefined)
			{
				//Missing data. So just add it as NaN.
				setValue = Number (num);
			}else
			{
				//It means the number can have different separator, or
				//it can be non-numeric.
				setValue = this.convertNumberSeps (num);
			}
		}else
		{
			//Simply convert it to numeric form.
			setValue = Number (num);
		}
		//Get the decimal places in data (if not integral value)
		if ( ! isNaN (setValue) && Math.floor (setValue) != setValue)
		{
			var decimalPlaces : Number = MathExt.numDecimals (setValue);
			//Store in class variable
			maxDecimals = (decimalPlaces > maxDecimals) ?decimalPlaces : maxDecimals;
		}
		//Return value
		return setValue;
	}
	/**
	* convertNumberSeps method helps us convert the separator (thousands and decimal)
	* character from the user specified input separator characters to normal numeric
	* values that Flash can handle. In some european countries, commas are used as
	* decimal separators and dots as thousand separators. In XML, if the user specifies
	* such values, it will give a error while converting to number. So, we accept the
	* input decimal and thousand separator from user, so thatwe can covert it accordingly
	* into the required format.
	* If the number is still not a valid number after converting the characters, we log
	* the error and return 0.
	*	@param	strNum	Number in string format containing user defined separator characters.
	*	@return		Number in numeric format.
	*/
	private function convertNumberSeps (strNum : String) : Number
	{
		//If thousand separator is defined, replace the thousand separators
		//in number
		//Store a copy
		var origNum : String = strNum;
		if (this.params.inThousandSeparator != "")
		{
			strNum = StringExt.replace (strNum, this.params.inThousandSeparator, "");
		}
		//Now, if decimal separator is defined, convert it to .
		if (this.params.inDecimalSeparator != "")
		{
			strNum = StringExt.replace (strNum, this.params.inDecimalSeparator, ".");
		}
		//Now, if the original number was in a valid format(with just different sep chars),
		//it has now been converted to normal format. But, if it's still giving a NaN, it means
		//that the number is not valid. So, we add to log and store it as undefined data.
		if (isNaN (strNum))
		{
			this.log ("ERROR", "Invalid number " + origNum + " specified in XML. FusionCharts can accept number in pure numerical form only. If your number formatting (thousand and decimal separator) is different, please specify so in XML. Also, do not add any currency symbols or other signs to the numbers.", Logger.LEVEL.ERROR);
		}
		return Number (strNum);
	}
	
}


