/** --- Init.as ---
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* Use and/or redistribution of this file, in whole or in part, is subject
* to the License Files, which was distributed with this component.
*
* Chart Initialization functions
* This file contains functions and constant definitions only, and is not
* associated with a class/movie clip.
*/
//Get all the attributes specified to chart as an array.
//This is done to get it in case-insensitive form.
var rootAttr : Array = getAttributesArray (_root);
/**
* _DOMId represents the DOM Id of the chart. DOM id is the id of
* the chart in HTML container.
*/
var _DOMId : String = rootAttr ["domid"];

//Now, get the values from FusionCharts JavaScript managed state
if (flash.external.ExternalInterface.available){
	//Get the states from FusionChats JavaScript managed state and convert to case-insensitive associative array
	var jsStateVars:Array = getAttributesArray(flash.external.ExternalInterface.call("__fusioncharts_vars", _DOMId));
	//Now, update the local vars in rootAttr 
	//DOMID and registerWithJS is not disturbed here
	rootAttr["scalemode"] = getFirstValue(jsStateVars["scalemode"], rootAttr["scalemode"]);
	rootAttr["mode"] = getFirstValue(jsStateVars["mode"], rootAttr["mode"]);
	rootAttr["chartwidth"] = getFirstValue(jsStateVars["chartwidth"], rootAttr["chartwidth"]);
	rootAttr["chartheight"] = getFirstValue(jsStateVars["chartheight"], rootAttr["chartheight"]);
	rootAttr["dataurl"] = getFirstValue(jsStateVars["dataurl"], rootAttr["dataurl"]);
	rootAttr["dataxml"] = getFirstValue(jsStateVars["dataxml"], rootAttr["dataxml"]);
	rootAttr["debugmode"] = getFirstValue(jsStateVars["debugmode"], rootAttr["debugmode"]);
	rootAttr["lang"] = getFirstValue(jsStateVars["lang"], rootAttr["lang"]);
	rootAttr["defaultdatafile"] = getFirstValue(jsStateVars["defaultdatafile"], rootAttr["defaultdatafile"]);
	rootAttr["animation"] = getFirstValue(jsStateVars["animation"], rootAttr["animation"]);
	//Whether data loading has to be halted (in case data is loaded by wrapper JS)
	rootAttr["stallload"] = getFirstValue(jsStateVars["stallload"], rootAttr["stallload"]);
}
/*
* To include or load  XML data files that are not Unicode-encoded,
* we set system.useCodepage to true. The Flash Player will now interpret
* the XML file using the traditional code page of the operating system
* running the Flash Player. This is generally CP1252 for an English
* Windows operating system and Shift-JIS for a Japanese operating system.
*/
System.useCodepage = true;
/*
* _isOnline represents whether the chart is working in Local or online mode.
* If it's local mode, FusionCharts would cache the data, else it would apply
* methods to always received updated data from the defined source
*/
var _isOnline : Boolean = (this._url.toLowerCase().substr (0, 7) == "http://") || (this._url.toLowerCase().substr (0, 8) == "https://");
//Set the chart's scale mode and align position
var _scaleMode : String = getFirstValue (rootAttr ["scalemode"] , "noScale");
//Loading mode of map
var _loadMode:String  = getFirstValue(rootAttr["mode"], "");
//For case insensitive check
_loadMode = _loadMode.toLowerCase();
//Set a flag based on _loadMode. 
var _loadModeExternal:Boolean = false;
if (_loadMode=="flex" || _loadMode=="laszlo"){
	_loadModeExternal = true;
}
//Check the loading mode and set scaling/alignment accordingly
if (!_loadModeExternal){
	//If we're not loading the SWF for Laszlo or Flex mode, set the scaling 
	//and alignment of movie. Otherwise, it'll change the same for parent
	//container, which is not our motive.
	//Set the scale mode of stage
	Stage.scaleMode = _scaleMode;
	//Set align to Top-left
	Stage.align = "TL";
}else{
	//If the mode is flex, we set some defaults.
	rootAttr["registerwithjs"] = 1;
}
//Get chart width and height
var _chartWidth : Number = Stage.width;
var _chartHeight : Number = Stage.height;

//If chart width and chart height have registered as 0, we update to Flashvars value
if (_loadMode=="flex" || _loadMode=="laszlo" || _chartWidth==0 || _chartHeight==0){
	//Also, if we're loading for Flex/Laszlo, we just set the width/height provided.
	_chartWidth = Number (rootAttr ["chartwidth"]);
	_chartHeight = Number (rootAttr ["chartheight"]);
}
//If chart width and height is still NaN or 0, get values from External Interface, if available
//This is provided by FusionCharts.js using __fusioncharts_dimension
if ((_chartWidth==0 || isNaN(_chartWidth) || _chartHeight==0 || isNaN(_chartHeight)) & flash.external.ExternalInterface.available) {
	//Get dimension from JavaScript
	var dimension:Object = flash.external.ExternalInterface.call("__fusioncharts_dimension", _DOMId);
	//Get width from dimension object
	if (dimension.width!=0 && !isNaN(dimension.width)){
		_chartWidth = dimension.width;
	}
	//Get height from dimension object
	if (dimension.height!=0 && !isNaN(dimension.height)){
		_chartHeight = dimension.height;
	}
}		
//Get chart horizontal and vertical center
var _chartXCenter : Number = _chartWidth / 2;
var _chartYCenter : Number = _chartHeight / 2;
/**
* _lang sets the language in which we've to display application
* message
*/
var _lang : String = getFirstValue (rootAttr ["lang"] , "EN");
//Convert to upper case
_lang = _lang.toUpperCase ();
/**
* _debugMode sets whether the chart is operating in debug
* mode or end-user mode. In debug mode, we show the debugger
* to the developer.
*/
var _debugMode : Number = Number (getFirstValue (rootAttr ["debugmode"] , 0));
/**
* _registerWithJS indicates whether this movie will register with JavaScript
* contained in the container HTML page. If yes, the movie will convey
* events to JavaScript functions present in the page.
*/
var _registerWithJS : Boolean = (Number (getFirstValue (rootAttr ["registerwithjs"] , 0)) == 1) ? true : false;
//Flag whether the chart's rendering has been registered with JS
/**
* defaultDataFile represents the XML data file URI which would
* be loaded if no other URI or XML data has been provided to us.
*/
var _defaultDataFile : String = unescape (getFirstValue (rootAttr ["defaultdatafile"] , "Data.xml"));
/**
* _isSafeDataURL flag keeps a track of whether the user has provided a safe dataURL.
* If not, we do not add that to debug Mode.
*/
var _isSafeDataURL:Boolean = true;
/**
* Global flag that dictates whether HTML entities in data URL are to be parsed and converted
* to normal characters. True, by default.
*/
var _parseHTMLEntities:Boolean = true;
/**
* Whether the chart should animate by default (globally)
*/
if (rootAttr ["animation"]!=undefined && rootAttr ["animation"]!=""){
	//Get the proper value from FlashVars
	var _defaultGlobalAnimation : Number = Number (rootAttr ["animation"]);
}else{
	//Leave it to undefined
	var _defaultGlobalAnimation : Number ;
}
/**
* Whether data loading should be stalled (in case of data being loaded by JS wrapper)
*/
if (isNaN(Number(rootAttr["stallload"])) || rootAttr["stallload"]==undefined || rootAttr["stallload"]==""){
	//By default, stalling does NOT occur
	rootAttr["stallload"] = "0";
}
var _stallDataLoad:Boolean = (Number(rootAttr ["stallload"]) == 1) ? true : false;
/**
* Flag to indicate whether data change methods were invoked on the SWF itself.
*/
var _dataInvokedOnSWF:Boolean = true;