 /**
* @class Logger
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* Logger class helps us store a log of information related to
* the application. Whenever, an exception is generated in the
* application, instead of using throw statement, we add the error
* message to logger, as we do not want the user to see the error.
* The error should be shown only in debug mode to the developer
* who is developing the application using FusionCharts.
*
* Why we haven't chosen Singleton design pattern for this, even
* though one chart can have only one debugger?
* When a user loads a chart into HTML page, a chart can have one
* debugger only. However, when he loads multiple instances of the
* chart in his Flash movie, each chart will have it's own debugger.
* In that case, a Singleton pattern won't work.
*
* The Logger class categorizes each message into these levels:
* INFO 	-	Information Message
*				Example: Chart Version, When data is retrieved, or
* 				what data is retrieved, version number.
* ERROR	- 	Error Message
*				Example: data could not be loaded, invalid character
* CODE		-	Any code that needs to be displayed to user
* LINK		- 	Link
*
* Movie Clip Structure
* LoggerMC -|
*			 |-Main -|
*					 |-tf (Text field)
*					 |-scrollMC (Scroll bar component)
*			 |-DebugMsg - Textbox containing the debug message
*/
import mx.utils.Delegate;
import com.fusioncharts.helper.FCEnum;
import com.fusioncharts.helper.ScrollBar;
class com.fusioncharts.helper.Logger 
{
	//Public Log Message LEVEL Enumeration
	public static var LEVEL : FCEnum;
	//Private variables
	//Array to store the log messages
	private var log : Array;
	//Boolean value to indicate whether the log is visible or not.
	private var logVisible : Boolean;
	//Chart width and height
	private var cWidth : Number;
	private var cHeight : Number;
	//Log width and co-ordinates
	private var lX : Number;
	private var lY : Number;
	private var lWidth : Number;
	private var lHeight : Number;
	//Text box width and co-ordinates
	private var tX : Number;
	private var tY : Number;
	private var tWidth : Number;
	private var tHeight : Number;
	//Text box padding
	private var tVPadding : Number = 15;
	private var tHPadding : Number = 15;
	//Scroll bar width
	private var scrollWidth : Number = 16;
	//Horizontal padding between the scroll bar and the textbox
	private var scrollPadding : Number = 3;
	//Cosmetic properties
	private var bgColor : Number = 0xFBFBFB;
	private var borderColor : Number = 0x999999;
	//Boolean value indicating whether the log has been drawn
	private var isDrawn : Boolean = false;
	//Movie clip containers which contain the log visual elements
	private var logMC : MovieClip;
	//Sub-movies
	private var logMainMC : MovieClip;
	private var logDebugMsgTF : TextField;
	private var bgMC : MovieClip;
	private var tf : TextField;
	private var scrollMC : MovieClip;
	//Scroll bar
	private var logScrollBar : ScrollBar;
	//Iterator
	private var _iterator : Number;
	//Listener Object to listen to key
	private var objListener : Object;
	//Debugger state
	private var debuggerState : String;
	//Create a style sheet
	private var cssStyle;
	/**
	* Constructor function
	* @param	lMC				Reference to movieclip in which we'll draw the
	*							log elements.
	* @param	showInitially	Boolean value indicating whether we've to show
	*							the log initially or not.
	* @param	chartWidth		Total width of the chart
	* @param	chartHeight		Height of the chart
	*/
	function Logger (lMC : MovieClip, chartWidth : Number, chartHeight : Number)
	{
		//Store parameters in instance properties
		logMC = lMC;
		cWidth = chartWidth;
		cHeight = chartHeight;
		//Initialize our LEVEL enumeration
		LEVEL = new FCEnum ("INFO", "ERROR", "CODE", "LINK");
		//Initialize log
		log = new Array ();
		//Initialize iterator
		_iterator = 0;
		//Initialize the style sheet
		this.cssStyle = new TextField.StyleSheet ();
		cssStyle.setStyle (".infoTitle", 
		{
			fontFamily : "Arial", fontSize : 11, color : "#005900", fontWeight : "normal", textDecoration : "none"
		});
		cssStyle.setStyle (".info", 
		{
			fontFamily : "Arial", fontSize : 11, color : "#333333", fontWeight : "normal", textDecoration : "none"
		});
		cssStyle.setStyle (".codeTitle", 
		{
			fontFamily : "Arial", fontSize : 11, color : "#005900", fontWeight : "normal", textDecoration : "none"
		});
		cssStyle.setStyle (".code", 
		{
			fontFamily : "Courier New", fontSize : 11, color : "#333333", marginLeft : 40, fontWeight : "normal", textDecoration : "none"
		});
		cssStyle.setStyle (".linkTitle", 
		{
			fontFamily : "Arial", fontSize : 11, color : "#005900", fontWeight : "normal", textDecoration : "none"
		});
		cssStyle.setStyle (".link", 
		{
			fontFamily : "Courier New", fontSize : 11, color : "#0000FF", fontWeight : "normal", textDecoration : "underline"
		});
		cssStyle.setStyle (".errorTitle", 
		{
			fontFamily : "Arial", fontSize : 11, color : "#CC0000", fontWeight : "normal"
		});
		cssStyle.setStyle (".error", 
		{
			fontFamily : "Arial", fontSize : 11, color : "#CC0000", fontWeight : "normal", textDecoration : "none"
		});
	}
	/**
	* show method shows the logger interface visually
	*/
	public function show () : Void 
	{
		//Update the visible flag
		logVisible = true;
		if ( ! isDrawn)
		{
			//If the interface is not already drawn, draw it
			draw ();
		} else 
		{
			//Else, just show the elements (previously hidden)
			logMainMC._visible = true;
		}
		//Update the log textbox to show the log messages recorded till
		//show() method was invoked.
		updateDisplay ();
	}
	/**
	* hide method hides the visual elements of log. But, the
	* debug message on the top stays.
	*/
	public function hide () : Void 
	{
		//Hide the movie clips
		logMainMC._visible = false;
		//Set the flag
		logVisible = false;
	}
	/**
	* draw method draws the log visual elements. This method is
	* to be called only once. Post which, show and hide log can be
	* called.
	*/
	private function draw () : Void 
	{
		//Calculate the log co-ordinates
		//Width is by default 80% of chart width
		//Height is by default 70% of chart height
		//Minimum values - width - 200 & Height - 125
		var wRatio : Number = cWidth * 0.8;
		var hRatio : Number = cHeight * 0.7;
		//Check for minimum values
		lWidth = (wRatio < 200) ? 200 : wRatio;
		lHeight = (hRatio < 125) ? 125 : hRatio;
		//Start position of the logger - center of screen
		lX = (cWidth - lWidth) / 2;
		lY = (cHeight - lHeight) / 2;
		//Now, start position and dimensions of the textbox
		tWidth = lWidth - ((2 * tHPadding) + scrollPadding + scrollWidth);
		tHeight = lHeight - (2 * tVPadding);
		tY = lY + tVPadding;
		tX = lX + tHPadding;
		//Create the movie clip containers
		logMainMC = logMC.createEmptyMovieClip ("Main", 1);
		logDebugMsgTF = logMC.createTextField ("DebugMsg", 2, 0, 0, cWidth, 0);
		bgMC = logMainMC.createEmptyMovieClip ("Bg", 1);
		tf = logMainMC.createTextField ("LogTF", 2, tX, tY, tWidth, tHeight);
		scrollMC = logMainMC.createEmptyMovieClip ("ScrollB", 3);
		//Draw the background
		bgMC.moveTo (lX, lY);
		bgMC.lineStyle (0, borderColor, 100);
		bgMC.beginFill (bgColor, 100);
		bgMC.lineTo (lX + lWidth, lY);
		bgMC.lineTo (lX + lWidth, lY + lHeight);
		bgMC.lineTo (lX, lY + lHeight);
		bgMC.lineTo (lX, lY);
		//Render the text-field properties
		tf.background = false;
		tf.border = false;
		tf.wordWrap = true;
		tf.multiline = true;
		tf.selectable = true;
		tf.html = true;
		tf.styleSheet = cssStyle;
		//Render the scroll bar
		logScrollBar = new ScrollBar (tf, scrollMC, tX + tWidth + scrollPadding, tY, scrollWidth, tHeight, "E2E2E2", "999999", "666666");
		//Render the debug message
		logDebugMsgTF.background = false;
		logDebugMsgTF.background = true;
		logDebugMsgTF.backgroundColor = 0xffffff;
		logDebugMsgTF.border = false;
		logDebugMsgTF.selectable = false;
		logDebugMsgTF.wordWrap = true;
		logDebugMsgTF.autoSize = "left";
		logDebugMsgTF.html = true;
		logDebugMsgTF.fontFamily = "Verdana";
		//Set the text.
		logDebugMsgTF.htmlText = "<font face='Verdana' size='9'><B>Debug Mode:</B> Click & press Shift + D to hide debugger</font>";
		//Add the listener object
		objListener = new Object ();
		//Delegate the onKeyDown event to alterLogVisibleState method of this class
		objListener.onKeyDown = Delegate.create (this, alterLogVisibleState);
		//Register the listener
		Key.addListener (objListener);
		//Update the isDrawn flag
		isDrawn = true;
	}
	
	/**
	 * Re-sets the size of the Logger component.
	 * @param	chartWidth	Width of the chart.
	 * @param	chartHeight	Height of the chart
	*/
	public function setSize(chartWidth:Number, chartHeight:Number):Void{
		//Store the parameters
		cWidth = chartWidth;
		cHeight = chartHeight;
		//Now, calculate the new co-ordinates for various elements
		//Width is by default 80% of chart width
		//Height is by default 70% of chart height
		//Minimum values - width - 200 & Height - 125
		var wRatio : Number = cWidth * 0.8;
		var hRatio : Number = cHeight * 0.7;
		//Check for minimum values
		lWidth = (wRatio < 200) ? 200 : wRatio;
		lHeight = (hRatio < 125) ? 125 : hRatio;
		//Start position of the logger - center of screen
		lX = (cWidth - lWidth) / 2;
		lY = (cHeight - lHeight) / 2;
		//Now, start position and dimensions of the textbox
		tWidth = lWidth - ((2 * tHPadding) + scrollPadding + scrollWidth);
		tHeight = lHeight - (2 * tVPadding);
		tY = lY + tVPadding;
		tX = lX + tHPadding;
		//Now, apply to each of the objects
		//Re-draw the background
		bgMC.clear();
		bgMC.moveTo (lX, lY);
		bgMC.lineStyle (0, borderColor, 100);
		bgMC.beginFill (bgColor, 100);
		bgMC.lineTo (lX + lWidth, lY);
		bgMC.lineTo (lX + lWidth, lY + lHeight);
		bgMC.lineTo (lX, lY + lHeight);
		bgMC.lineTo (lX, lY);
		//Re-create the textfield
		tf.removeTextField();
		tf = logMainMC.createTextField ("LogTF", 2, tX, tY, tWidth, tHeight);
		tf.background = false;
		tf.border = false;
		tf.wordWrap = true;
		tf.multiline = true;
		tf.selectable = true;
		tf.html = true;
		tf.styleSheet = cssStyle;
		//Re-draw scroll
		logScrollBar.destroy();
		logScrollBar = new ScrollBar (tf, scrollMC, tX + tWidth + scrollPadding, tY, scrollWidth, tHeight, "E2E2E2", "999999", "666666");
		//Set _iterator to 0 so as to record all
		_iterator = 0;
		//Show all log messages again in the text-field
		updateDisplay();
	}
	
	/**
	* record method adds a message to the log. It creates a generic object
	* and stores the level and log message.
	* We use object instead of a custom class (say LogMessage) to keep
	* overheads down, as the design is very simple here.
	*	@param	strMsg	Message to be added to the log.
	*	@param	level	Level of the message (from the LEVEL enumeration)
	*/
	public function record (strTitle : String, strMsg : String, level : Number)
	{
		//Create an object
		var logMessage : Object = new Object ();
		logMessage.title = strTitle;
		logMessage.msg = strMsg;
		logMessage.level = level;
		//Add the message to our log array
		this.log.push (logMessage);
		//Delete object
		delete logMessage;
		//If the log is visible, update display
		if (logVisible)
		{
			updateDisplay ();
		}
	}
	/**
	* This method alters the visibility of the logger when the
	* user presses Shift + D.
	*/
	private function alterLogVisibleState () : Void 
	{
		if (Key.isDown (Key.SHIFT) && Key.isDown (new String ("D").charCodeAt (0)))
		{
			//Switch the visibility
			logMainMC._visible = ! logMainMC._visible;
			logVisible = logMainMC._visible;
			debuggerState = (logVisible) ? "hide" : "show";
			logDebugMsgTF.htmlText = "<font face='Verdana' size='9'><B>Debug Mode:</B> Click & press Shift + D to " + debuggerState + " debugger</font>";
			//Also, update the display for any messages that might have been logged when invisible
			if (logVisible)
			{
				updateDisplay ();
			}
		}
	}
	/**
	* In this method, we add the log's message to the textbox
	* and update the iterator to the last message displayed.
	*/
	private function updateDisplay () : Void 
	{
		//This function adds the log messages to textbox.
		//Clone array to avoid overlapping at runtime - while another
		//message is being added to log.
		var logLength : Number = log.length;
		var msgQueue : String = tf.htmlText;
		for (var i : Number = _iterator; i < logLength; i ++)
		{
			switch (log [i].level)
			{
				case Logger.LEVEL.INFO :
				msgQueue = msgQueue + "<p><span class='infoTitle'>" + log [i].title + ": </span><span class='info'>" + log [i].msg + "</span></p>";
				break;
				case Logger.LEVEL.ERROR :
				msgQueue = msgQueue + "<p><span class='errorTitle'>" + log [i].title + ": </span><span class='error'>" + log [i].msg + "</span></p>";
				break;
				case Logger.LEVEL.CODE :
				msgQueue = msgQueue + "<p><span class='codeTitle'>" + log [i].title + ": </span><span class='code'>" + log [i].msg + "</span></p>";
				break;
				case Logger.LEVEL.LINK :
				msgQueue = msgQueue + "<p><span class='linkTitle'>" + log [i].title + ": </span><span class='link'>" + log [i].msg + "</span></p>";
				break;
			}
		}
		//Assign the text to textbox
		tf.htmlText = msgQueue;
		//Update iterator's index
		_iterator = logLength;
		//Invalidate the scroll bar
		//We have to do it manually - as textfield.onScroller is not invoked
		//when the text is changed using code.
		logScrollBar.invalidate ();
	}
	/**
	* destroy method MUST be called whenever you wish to delete this class's
	* instance.
	*/
	public function destroy ()
	{
		//Remove listener.
		Key.removeListener (objListener);
		//Destroy scroll bar
		logScrollBar.destroy ();
		//Remove the movie clips
		bgMC.removeMovieClip ();
		tf.removeMovieClip ();
		scrollMC.removeMovieClip ();
		logMainMC.removeMovieClip ();
		logDebugMsgTF.removeMovieClip ();
	}
}
