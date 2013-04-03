/** --- DataFunctions.as ---
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* Use and/or redistribution of this file, in whole or in part, is subject
* to the License Files, which was distributed with this component.
*
* Data loading and parsing functions
* This file contains functions and constant definitions only, and is not
* associated with a class/movie clip.
*/
/**
* isDataURLProvided function checks whether the chart has been
* provided with dataURL or dataXML
*/
function isDataURLProvided():Boolean {
	//By default we assume that the dataURL has been provided and store the value in a temporary flag
	var isURLProvided:Boolean;
	isURLProvided = true;
	//Now check.
	if (rootAttr["dataurl"] == "" || rootAttr["dataurl"] == null || rootAttr["dataurl"] == undefined) {
		//Since the length of dataURL is less than 1
		//We haven't been provided with dataURL
		if (rootAttr["dataxml"] == "" || rootAttr["dataxml"] == null || rootAttr["dataxml"] == undefined) {
			//Now, if we haven't been provided with dataXML also.
			//We set the data URL to a default data file
			rootAttr["dataurl"] = _defaultDataFile;
			//Add to log
			chart.log("WARNING", "Could not find dataXML or dataURL parameter. Setting dataURL to default data file "+_defaultDataFile+".", Logger.LEVEL.ERROR);
		} else {
			//We have been provided with the full XML document
			//So, re-set the flag
			isURLProvided = false;
			//Add to log
			chart.log("INFO", "XML Data provided using dataXML method.", Logger.LEVEL.INFO);
		}
	} else {
		//Add to log
		chart.log("INFO", "XML Data provided using dataURL method.", Logger.LEVEL.INFO);
	}
	return isURLProvided;
}
/**
* getFilteredDataURL function filters the dataURL provided to it.
* Steps to filter it before we can invoke the XML request.
* The filter involves the following jobs:
* 1. Convert from old * encoded format to the normal format -
*	  to support backward compatibility (pre v2.2 AutoFit charts)
* 2. Convert the URL Encoded dataURL back to normal form.
* 3. Create the no-cache form of the URL
*/
function getFilteredDataURL(strURL:String):String {
	//Convert from old format to new
	strURL = convertFromOldDataUrl(strURL);
	//Unescape the XML URL to convert the hexadecimal coded characters back into normal
	strURL = convertEscapedChars(strURL);
	//Prevent any cross site scripting attacks
	strURL = filterXSSChars(strURL);
	//Get the no-cache URL	
	strURL = getNoCacheURL(strURL);
	//Return it
	return strURL;
}
/**
* loadData function loads the data for the chart and stores
* it in xmlData XML object (level0 timeline).
*/
function loadData() {
	//If data loading has to be stalled, wait
	if (_stallDataLoad){
		//Set data loading text
		tf = createBasicText(getAppMessage("RETRIEVINGDATA", _lang), this, 2, _chartXCenter, _chartYCenter, "Verdana", 10, LPBarTextColor, "center", "bottom");			
	}else{
		//If dataURL has been provided, then we load the data
		if (isDataURLProvided()) {
			//Get the filtered data URL
			var strURL:String = getFilteredDataURL(rootAttr["dataurl"]);
			//Add the URL to Log (if it's safe)
			if (_isSafeDataURL) {
				chart.log("dataURL provided", "<A HREF='"+rootAttr["dataurl"]+"' target='_blank'>"+rootAttr["dataurl"]+"</A>", Logger.LEVEL.LINK);
			} else {
				chart.log("dataURL reset", "A javascript/asfunction was invoked in dataURL, which can be potentially dangerous as it allows XSS attacks. Re-setting dataURL to Data.xml. ", Logger.LEVEL.ERROR);
			}
			//Add filtered URL to log
			chart.log("dataURL invoked", "<A HREF='"+strURL+"' target='_blank'>"+strURL+"</A>", Logger.LEVEL.LINK);
			//Show the loading data text and progress bar
			mcProgressBar = drawProgressBar(this, 1, pBXPos, pBYPos, LPBarWidth, LPBarHeight, LPBarBorderColor, LPBarBorderThickness);
			//Create the loading text
			tf = createBasicText(getAppMessage("RETRIEVINGDATA", _lang), this, 2, _chartXCenter, _chartYCenter+5, "Verdana", 10, LPBarTextColor, "center", "bottom");
			xmlData.ignoreWhite = true;
			//Intercept onData to get raw XML string and set on chart object
			//This is done to store raw string (to be returned in getXML() call)
			//so that special characters are left unparsed.
			xmlData.onData = function(data:String) {
				if (data == undefined) {
					//Invoke onLoad with success=false. This is when there was
					//an error loading XML document.
					this.onLoad(false);
				} else {
					//Store the XML string in Chart
					chart.setRawXMLString(data);
					//If data is present, parse it
					this.parseXML(data);
					//Invoke onLoad implementation
					this.loaded = true;
					this.onLoad(true);
				}
			};
			//Set the onLoad handler to dataLoaded function
			xmlData.onLoad = Delegate.create(this, dataLoaded);
			//Load the data.
			xmlData.load(strURL);
			//Now, create the onEnterFrame handler to update progress bar
			this.onEnterFrame = function() {
				//If the total bytes of the XML document has been detected,
				//we update the progress bar
				if (xmlData.getBytesTotal() != undefined && xmlData.getBytesTotal()>0) {
					//Update the progress bar value
					setProgressValue(mcProgressBar, 0, xmlData.getBytesTotal(), xmlData.getBytesLoaded(), pBXPos, pBYPos, LPBarWidth, LPBarHeight, LPBarBgColor);
					//If XML data is loaded, delete the enterFrame event
					if (xmlData.getBytesLoaded()>=xmlData.getBytesTotal()) {
						//Delete the progress bar
						mcProgressBar.removeMovieClip();
						//Delete the enter frame event
						delete this.onEnterFrame;
					}
				}
			};
		} else {
			//Show message to the end user
			tf = createBasicText(getAppMessage("READINGDATA", _lang), this, 2, _chartXCenter, _chartYCenter, "Verdana", 10, LPBarTextColor, "center", "middle");
			//Else we read the data, parse it and store in XML object
			xmlData.ignoreWhite = true;
			xmlData.parseXML(rootAttr["dataxml"]);
			//Set the raw XML in chart
			chart.setRawXMLString(rootAttr["dataxml"]);
			//Goto FDataLoadDone
			gotoAndPlay("DataLoad", "FDataLoadDone");
		}
	}
}
/**
* dataLoaded function acts as the onLoad event handler for XML Data Object.
* @param	success		Boolean value indicating whether the XML object
*						was successfully loaded. If the XML document is
*						received successfully, the success parameter is true.
*						If the document was not received, or if an error occurred
*						in receiving the response from the server, the success
*						parameter is false.
* Based on this success parameter, we'll show the required msgs to the user.
*/
function dataLoaded(success:Boolean):Void {
	//Hide the progress bar
	mcProgressBar.removeMovieClip();
	//Hide the text
	tf.removeTextField();
	//Hide the loading data text
	if (success) {
		//Data has been loaded successfully
		//Delete the enter frame event
		delete this.onEnterFrame;
		//So jump to FDataLoadDone Frame
		gotoAndPlay("DataLoad", "FDataLoadDone");
	} else {
		//Delete the enter frame event
		delete this.onEnterFrame;
		//An error occurred while fetching the data. Show an error to viewer
		tf = createBasicText(getAppMessage("LOADERROR", _lang), this, 2, _chartXCenter, _chartYCenter, "Verdana", 10, LPBarTextColor, "center", "middle");
		//Add to log.
		chart.log("ERROR", "An error occurred while loading data. Please check your dataURL, by clicking on the \"dataURL invoked\" link above, to see if it's returing valid XML data. Common causes for error are: <LI>No URL Encoding provided for querystrings in dataURL. If your dataURL contains querystrings as parameters, you'll need to URL Encode the same. e.g., Data.asp?id=101&subId=242 should be Data%2Easp%3Fid%3D101%26subId%3D242</LI><LI>Different sub-domain of chart .swf and dataURL. Both need to be same owing to sandbox security.</LI><LI>Network error</LI>", Logger.LEVEL.ERROR);
		//Raise an error event (if in JS register mode)
		if (_registerWithJS && ExternalInterface.available) {
			ExternalInterface.call("FC_DataLoadError", _DOMId);
		}
		//Stop   
		stop();
	}
}
/**
* convertFromOldDataUrl function converts the old format dataURL into
* normalized form to provide backward compatibility.
* In the old format, the parameters in dataURL were separated
* by * instead of ? and &. e.g., DataProvider.asp*id=1*subId=34
* instead of DataProvider.asp?id=1&subId=34
*/
function convertFromOldDataUrl(strOldUrl:String):String {
	var strURL = strOldUrl;
	//First thing, we check if the dataURL is actually in old format
	if (strURL.indexOf("*") != -1) {
		//Use the split function of array to split the URL wherever a * is found
		var arrUrl:Array = new Array();
		arrUrl = strURL.split("*");
		var finalUrl:String = "";
		//Now, join them depending on their position
		var loopvar:Number;
		for (loopvar=0; loopvar<arrUrl.length; loopvar++) {
			if (loopvar == 0) {
				finalUrl = arrUrl[0];
			} else if (loopvar == 1) {
				finalUrl = finalUrl+"?"+arrUrl[1];
			} else {
				finalUrl = finalUrl+"&"+arrUrl[loopvar];
			}
		}
		//Return the formatted URL
		return finalUrl;
	} else {
		//Simply return the URL sent to this function
		return strOldUrl;
	}
}
/**
 * filterXSSChars method filters the XSS scripting attack characters like 
 * Basically, if we find a colon in the dataURL and the file is not running
 * locally (local file system), we reset the dataURL to Data.xml
*/
function filterXSSChars(strURL:String):String {
	//If we're working in online mode and a javascript protocol was found in the URL.
	//In offline mode, the chart could call a dataURL like c:\...\File.xml. As such we do
	//not restrict colons in local file system. Moreover, files from local file system cannot
	//be used for cross site scripting attack, as the domain of file is local and it cannot
	//access cookies for any domain.
	//Parse HTML entities (e.g. &#30 or &#x58), if required
	if (_parseHTMLEntities==true){
		//Convert them to normal characters
		strURL = convertEntitiesToChars(strURL);
	}
	if ((_isOnline == true) && ((strURL.toLowerCase().indexOf("javascript") != -1 || strURL.toLowerCase().indexOf("asfunction") != -1) && (strURL.indexOf(":") != -1 || strURL.toLowerCase().indexOf("%3a") != -1))) {
		//Means, we've an external URL specified or something like javascript: or asfunction:
		//This could be potentially dangerous as it allows for an XSS attack. 		
		//So, default to Data.xml
		strURL = "Data.xml";
		//Flag the URL as unsafe
		_isSafeDataURL = false;
		//Message logging is not done here, as its done in the loading function
	}
	//Return safe URL   
	return strURL;
}
/**
 * Converts all escaped characters in URL recursively to normal
*/
function convertEscapedChars(strURL:String):String{
	//Store as input string
    var inputStr:String = strURL;
	//Unescape as output string
    var outputStr:String = unescape(strURL);
	//Compare and redo (recursively)
    if(inputStr != outputStr){ 
        outputStr = convertEscapedChars(outputStr);
    }
	//Return
    return outputStr; 
}
/**
* Converts all HTML entities in given string to normal characters
*/
function convertEntitiesToChars(str:String):String {
	//Find instances of either &#D; (where D is a decimal number)
	//or &#xH; where H is the hex representation of decimal
	//Entities present?
	var entPresent:Boolean = (str.indexOf("&#", lastPos) != -1);
	//Proceed only if at least 1 entity found
	if (entPresent) {
		//Positions
		var nextPos:Number = 0, lastPos:Number = 0, nextSemiColon:Number = 0;
		//Entity code to be extracted
		var encCode:String = "", encValue:String, encDValue:Number;
		//Eqivalent entity char 
		var encChar:String = "";
		//Replacement queue
		var replaceEnt:Array = new Array();
		//Find the next position of &# since the last position
		while (entPresent) {
			nextPos = str.indexOf("&#", lastPos);
			nextSemiColon = str.indexOf(";", nextPos);
			//If no further semi colon is present, do not decode hereafter
			//as even the rest of entities do not have a semicolon
			//Or, if there are more than 6 characters between them also
			if (nextSemiColon == -1 || (nextSemiColon-nextPos>6)) {
				break;
			}
			encCode = str.substring(nextPos, str.indexOf(";", nextPos));
			lastPos = nextPos+1;
			//Remove the first two characters
			encValue = encCode.substr(2, encCode.length);
			//Check if more entities are present
			entPresent = (str.indexOf("&#", lastPos) != -1);
			//If it's a hex, convert to numeric
			if (encValue.charAt(0).toLowerCase() == "x") {
				//Extract the hex value
				encValue = encValue.substr(1, encValue.length);
				//Convert to decimal base
				encDValue = parseInt(encValue, 16);
			} else {
				//Convert to number
				encDValue = parseInt(encValue);
			}
			//Push into replacement queue
			replaceEnt[encCode+";"] = String.fromCharCode(encDValue);
		}
		//If replacement queue is not empty replace.
		for (var item in replaceEnt) {
			str = replace(str, item, replaceEnt[item]);
		}
	}
	return str;
}
/**
 * String.replace function
*/
function replace(str:String, pattern:String, replacement:String):String {
	return str.split(pattern).join(replacement);
}
/**
* Using getNoCacheURLfunction, we create a non-cache URL.
* If we're not working in local mode, we'll append the time
* at the end of the dataURL so that a new XML document is sent
* by the server for each requestand the XML data is not cached.
* Suppose, the dataURL is data.asp, so we'll convert it to
* data.asp?FCTime=43743 so that we can "fool" the server and get
* new data every time we request for it.
* :Explanation: How to stop the caching of the XML data document
* If the chart is not working in local mode, we will add a continuously
* updating data (number of milliseconds that have elapsed since the movie
* started playing) at the end of the dataURL. This will result in having
* a new dataURL every time we need to get the data from the server and
* therefore the server will be "fooled" thereby passing on updated
* data each time.
* We add the time in the format ?FCTime=xxxxx or &FCTime=xxxxx
* depending on whether there's already a ? present in the dataURL
* or not. That is, if filtered dataURLis data.asp?param1=value1,
* then we add curr as data.asp?param1=value1&FCTime=xxxxx. However,
* if dataURL is simply data.asp, we add curr as data.asp?FCTime=xxxxx
*/
function getNoCacheURL(strURL:String):String {
	if (_isOnline) {
		//Do this only if we are dealing with dataURL and we are working online
		if (strURL.indexOf("?") == -1) {
			//If a ? exists in the data url
			strURL = strURL+"?FCTime="+getTimer();
		} else {
			//If a ? does NOT exist in the data url
			strURL = strURL+"&FCTime="+getTimer();
		}
	}
	return strURL;
}
/**
* getXMLStatusError function returns the XML status id
* in words, detailing the error.
*	@param	statusId	Status Id of the XML Object
*	@return			A string detailing the error
*/
function getXMLStatusError(statusId:Number):String {
	var errorMessage:String;
	switch (statusId) {
	case -2 :
		errorMessage = "A CDATA section is not properly terminated.";
		break;
	case -3 :
		errorMessage = "The XML declaration is not properly terminated.";
		break;
	case -4 :
		errorMessage = "The DOCTYPE declaration is not properly terminated.";
		break;
	case -5 :
		errorMessage = "A comment is not properly terminated.";
		break;
	case -6 :
		errorMessage = "An XML element is malformed.";
		break;
	case -7 :
		errorMessage = "Out of memory.";
		break;
	case -8 :
		errorMessage = "An attribute value is not properly terminated.";
		break;
	case -9 :
		errorMessage = "A start-tag is not matched with an end-tag.";
		break;
	case -10 :
		errorMessage = "An end-tag is encountered without a matching start-tag.";
		break;
	default :
		errorMessage = "An unknown error has occurred.";
		break;
	}
	return errorMessage;
}
//Define functions for external interface
/**
* setDataURL method interfaces with the external
* script method setDataURL. This method updates the chart
* by loading the new data and re-rendering the chart.
*	@param	strURL	New URL from where data is to be
*					loaded.
* 	@param	callOnSWF	Whether the data method is directly called on
*						SWF (DOM Object), or on the JS wrapper of the 
*						FusionCharts JavaScript class. 
*/
function setDataURL(strURL:String, callOnSWF:Boolean):Void {
	//This function re-sets the dataURL for the chart.
	//First we need to make sure that any previous calls are not running
	deletePendingCalls();
	//Set default value for callOnSWF
	if (callOnSWF==undefined || callOnSWF==null){
		callOnSWF = true;
	}
	//Store 
	_dataInvokedOnSWF = callOnSWF;
	//Reset default animation (global state) - read from Flashvars
	chart.setDefaultGlobalAnimation(undefined);
	//Invoke FusionCharts JavaScript state management to update new dataURL
	if (flash.external.ExternalInterface.available) {
		//Update the state. 
		var updated:Boolean = getAttributesArray(flash.external.ExternalInterface.call("__fusioncharts_vars", _DOMId, {DOMId:_DOMId, dataURL:strURL}));
	}
	//Set new dataURL   
	rootAttr["dataurl"] = strURL;
	//Set dataXML to null
	rootAttr["dataxml"] = "";
	//Remove the chart
	chart.remove();
	//Re-initialize
	chart.reInit();
	//Log the event
	chart.log("INFO", "setDataURL method invoked from external script.", Logger.LEVEL.INFO);
	//Call loadData
	loadData();
}
/**
* setDataXML method interfaces with the external
* script method setDataXML. This method updates the chart
* by reading the new data and re-rendering the chart.
*	@param	strXML	New XML Data.
* 	@param	callOnSWF	Whether the data method is directly called on
*						SWF (DOM Object), or on the JS wrapper of the 
*						FusionCharts JavaScript class. 
*/
function setDataXML(strXML:String, callOnSWF:Boolean):Void {
	//This function re-sets the dataXML for the chart.
	//First we need to make sure that any previous calls are not running
	deletePendingCalls();
	//Set default value for callOnSWF
	if (callOnSWF==undefined || callOnSWF==null){
		callOnSWF = true;
	}
	//Store 
	_dataInvokedOnSWF = callOnSWF;
	//Reset default animation (global state) - read from Flashvars
	chart.setDefaultGlobalAnimation(undefined);
	//Invoke FusionCharts JavaScript state management to update new dataURL
	if (flash.external.ExternalInterface.available) {
		//Update the state
		var updated:Boolean = getAttributesArray(flash.external.ExternalInterface.call("__fusioncharts_vars", _DOMId, {DOMId:_DOMId, dataXML:escape(strXML)}));
	}
	//Now set XML   
	rootAttr["dataxml"] = strXML;
	//Set dataURL to null
	rootAttr["dataurl"] = "";
	//Remove the chart
	chart.remove();
	//Re-initialize
	chart.reInit();
	//Log the event
	chart.log("INFO", "setDataXML method invoked from external script.", Logger.LEVEL.INFO);
	//Call loadData
	loadData();
}
/**
* deletePendingCalls method clears any currently in-process XML loading
* call.
*/
function deletePendingCalls() {
	//Delete the onEnterFrame event handler
	delete this.onEnterFrame;
	//Re-defined onLoad handler of XML to empty reference
	xmlData.onLoad = function() {
	};
	//Delete XML Data object
	delete xmlData;
	//Set stalling of data load to false (as this call sets the data)
	//So if the call has come to SWF, it means SWF is now set to 
	//receive data (or even load data, if JS wrapper fails)
	_stallDataLoad = false;
	//Re-initialize for further usage
	xmlData = new XML();
	//Remove the text field and progress bar
	tf.removeTextField();
	mcProgressBar.removeMovieClip();
}
/**
 * Returns a boolean value indicating whether the data method was invoked on SWF
 *	@return		Boolean value indicating whether data method was directly invoked
 *				on SWF object.
*/
function dataInvokedOnSWF():Boolean{
	return _dataInvokedOnSWF;
}
/**
 * Clears the current chart and shows a custom message.
*/
function showChartMessage(msg:String):Void{
	switch (msg){
		case "LoadDataErrorText":
		msg = getAppMessage("LOADERROR", _lang);
		break;
		case "XMLLoadingText":
		msg = getAppMessage("RETRIEVINGDATA", _lang)
		break;
		case "InvalidXMLText":
		msg = getAppMessage("INVALIDXML", _lang)
		break;
		case "NoDataText":
		msg = getAppMessage("NODATA", _lang)
		break;
		case "ReadingDataText":
		msg = getAppMessage("READINGDATA", _lang)
		break;
	}
	//First we need to make sure that any previous calls are not running
	deletePendingCalls();
	//Remove the chart
	chart.remove();
	//Re-initialize
	chart.reInit();
	//Show the message
	tf = createBasicText(msg, this, 2, _chartXCenter, _chartYCenter, "Verdana", 10, LPBarTextColor, "center", "bottom");			
}
