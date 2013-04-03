/**
 * @class VerticalAxis
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * VerticalAxis generates a vertical Axis
 * based on the parameters specified
*/
//Math Extension
import com.fusioncharts.extensions.MathExt;
//Drawing Extension
import com.fusioncharts.extensions.DrawingExt;
//Utility Class
import com.fusioncharts.helper.Utils;
//FCEnum
import com.fusioncharts.helper.FCEnum;
//Number formatting Function
import com.fusioncharts.helper.NumberFormatting;
//Tween Class
import mx.transitions.Tween;
import mx.transitions.easing.*;
//CheckBox
import com.fusioncharts.helper.FCCheckBox;
//Style Object
import com.fusioncharts.core.StyleManager;
//Macro Class
import com.fusioncharts.helper.Macros;
//Delegate
import mx.utils.Delegate;
//Event Dispatcher
import mx.events.EventDispatcher;
//Class
class com.fusioncharts.helper.VerticalAxis {
	//MovieClip Container
	private var axisContMC:MovieClip;
	private var divLineContMC:MovieClip;
	//Cursor
	private var cursorMC:MovieClip;
	//MC
	private var mcAxis:MovieClip;
	private var mcAxisBg:MovieClip;
	//Temporary MC for the simulation Mode of the CreateText
	private var tempMC:MovieClip;
	//whether to show the axis
	private var showAxis:Boolean;
	private var isPrimary:Boolean;
	//Axis Position
	private var axisOnLeft:Boolean;
	//Axis Unique for identification
	private var axisId:Number;
	//Label
	private var title:String;
	private var titlePosition:String;
	//CheckBox
	private var cb:FCCheckBox;
	private var checkBoxColor:String;
	private var showHide:Boolean;
	private var showTitle:Boolean;
	//Axis title font object
	private var axisTitleFontObj:Object;
	//Store the position
	private var x:Number;
	private var y:Number;
	//Maximum height for the Axis
	private var height:Number;
	//Canvas X and Width - used for the divLines
	private var canvasX:Number;
	private var canvasW:Number;
	//Number properties
	private var numberPrefix:String;
	private var numberSuffix:String;
	private var formatNumber:Boolean;
	//Given Scale Unit for Number Formatting
	private var nsv:Array;
	private var nsu:Array;
	//Animation
	private var animationFlag:Boolean;
	private var allowAxisShift:Boolean;
	//Number properties
	private var numberScaleDefined:Boolean;
	private var defaultNumberScale:String;
	private var formatNumberScale:Boolean;
	private var decimalSeparator:String;
	private var maxDecimals:Number = 0;
	private var thousandSeparator:String;
	private var decimals:Number;
	private var forceDecimals:Boolean;
	private var yAxisValueDecimals:Number;
	//Zero Plane
	private var showZeroPlane:Boolean;
	private var zeroPlaneAlpha:Number;
	private var zeroPlaneThickness:Number;
	private var zeroPlaneColor:String;
	private var zeroPRequired:Boolean;
	private var zeroPIncluded:Boolean;
	private var zeroPlaneTf:Object;
	//DivLine
	//Storage for div lines
	private var divLines:Array;
	private var showDivLineValues:Boolean;
	private var adjustDiv:Boolean;
	private var formatDivDecimals:Boolean;
	private var numDivLines:Number;
	private var divInterval:Number;
	private var setAdaptiveYMin:Boolean;
	private var divLineFontObj:Object;
	private var divLineThickness:Number;
	private var divLineColor:String;
	private var divLineAlpha:Number;
	private var divLineIsDashed:Boolean;
	private var divLineDashLen:Number;
	private var divLineDashGap:Number;
	private var fcEnumObj:FCEnum;
	//Range for the axis
	private var minValue:Number;
	private var maxValue:Number;
	//Users Input value
	private var yAxisMaxValue:Number;
	private var yAxisMinValue:Number;
	private var yAxisValuesStep:Number;
	//Y Axis final Max and Min Value
	private var yMax:Number;
	private var yMin:Number;
	private var yMaxGiven:Boolean;
	private var yMinGiven:Boolean;
	private var range:Number;
	private var interval:Number;
	private var showLimits:Boolean;
	//Cosmetic Property
	private var lineThickness:Number;
	private var axisColor:String;
	private var tickWidth:Number;
	private var maxWReqd:Number;
	private var maxHReqd:Number;
	private var maxTW:Number;
	//Style Object
	private var styleM:StyleManager;
	//Macro Object
	private var macro : Macros;
	//Listeners
	private var checkListener:Object;
	//CheckBox and Axis Padding
	private var CB_PADDING:Number = 25;
	//Value and TickMark Padding
	private var VALUE_PADDING:Number = 0;
	//To refer to the tween object
	private var twMC:Tween;
	//Duration of animation in seconds
	private var tweenDuration:Number = 0.3;
	
	//Constructor function
	/**
	*	@param	parentMC	MovieClip in which we create the container.
	*	@param	cursorMC	MovieClip reference for the reposition cursor.
	*	@param	axisId	Sets an unique id for the axis.
	*	@param	depth	Depth at which we create the axis container
	*	@param	depthDiv	Depth at which we create the divlines within the parentMC
	*	@param	showAxis	Whether the axis is visible or an imaginary one.
	*	@param	x			Sets the x position of the axis.
	*	@param	x			Sets the y position of the axis.
	*	@param	height		Sets the maximum height for the axis.
	*	@param	axisOnLeft	Specifies whether we need the axis on left or right of the canvas.
	*	@param	title		Sets the title of the axis.
	*	@param	titlePosition	Sets the title position of the axis.
	*	@param	maxDataValue 	Specify the maximum data value for the divLines and ticks.
	*	@param	minDataValue 	Specify the minimum data value for the divLines and ticks.
	*	@param	yAxisMaxValue	Specify the maximum data value given in the xml.
	*	@param	yAxisMinValue	Specify the minimum data value given in the xml.
	*	@param	setAdaptiveYMin	Specifies whether the y min will be based on the values provided.s
	*	@param	adjustDiv		Whether to automatically adjust div lines
	*	@param	numDivLines		Number of div lines required
	*	@param	divLineProp		Div line properties specific to each axis of the chart.
		- Following the property lists for the divLineProp Object
			-showDivLineValues 	Whether we need to show the div line values.
			divLineThickness 	Specifies the thickness of the divlines
			divLineAlpha 		Specifies the alpha value of the divlines
			divLineIsDashed 	Specifies the dash property of the divlines
			divLineDashLen 		Length of the dash(if required)
			divLineDashGap 		Gap between the dashes.
			divLineColor 		Divline color
			divLineFontObj 		Font object of the divLines( object required for the createtext function)
	*	@param	numberFormattingProp	number formatting properties for the axis values.
			//Number formatting properties
				formatNumber 	Whether formatting option is required
				formatNumberScale 	Whether formatting scale option is required
				defaultNumberScale	Specifies the default scale to use for formatting the number.
				nsu 				Array which has the list of all the number scale units
				nsv 				Array which has the list of all the number scale Values
				numberScaleDefined  Whether the number scale is defined or not
				numberPrefix 		Number prefix to use
				numberSuffix 		Number suffix to use while formatting the number
				decimalSeparator 	Which character is used as the decimal seperator
				thousandSeparator 	Which character is used as the thousand seperator
				yAxisValueDecimals 	Decimal precision for the y axis values.
				forceDecimals 		Whether we force the decimal precision with the given decimal precision
				decimals			decimal precision of the numbers.
	*	@param	zeroPlaneProp	Sets the zero plane properties specific to the axis.
		//All the zero plane properties
				showZeroPlane		Whether the zero plane is required
				zeroPlaneColor		Color of the zero plane
				zeroPlaneThickness 	Thickness of the line of the zero plane
				zeroPlaneAlpha		Alpha for the zero plane
	*	@param	showLimits	Whether we need to display the limits
	*	@param	yAxisValuesStep		Step value to show the y Axis values in the axis.
	*	@param	axisTitleFontObj	Sets the font object for the title of the axis(Object required for createText function)
	*	@param	showHide	Whether we need to show the checkBox to allow selection
	*	@param	showTitle	When the checkbox is hidden- do we need the title
	*	@param	styleM		Reference object to the style manager class.
	*	@param	fcEnumObj	Reference object to the style Enum class.
	*	@param	macro		Reference object to the style macro class.
	*	@param	animation	whether we need animation for the axis.
	*	@param	allowAxisShift	Whether the axis click event has to be handled for movement of axis.
	*	@param	checkBoxColor	Sets the color of the checkbox - which works on 1 color method.
	*/
	function VerticalAxis(parentMC:MovieClip, cursorMC:MovieClip, axisId:Number, depth:Number, depthDiv:Number, showAxis:Boolean, x:Number, y:Number, height:Number, axisOnLeft:Boolean, title:String, titlePosition:String, maxDataValue:Number, minDataValue:Number, yAxisMaxValue:Number, yAxisMinValue:Number, setAdaptiveYMin:Boolean, adjustDiv:Boolean, numDivLines:Number, divLineProp:Object, numberFormattingProp:Object, zeroPlaneProp:Object, showLimits:Boolean, yAxisValuesStep:Number, axisTitleFontObj:Object, showHide:Boolean, showTitle:Boolean, styleM:StyleManager, fcEnumObj:FCEnum, macro:Macros, animation:Boolean, allowAxisShift:Boolean, checkBoxColor:String) {
		//MC
		this.axisContMC = parentMC.createEmptyMovieClip("AxisCont_"+depth, depth);
		this.cursorMC = cursorMC;
		//DivLine Container
		this.divLineContMC = parentMC.createEmptyMovieClip("DivLineCont_"+depthDiv, depthDiv);
		//Axis background visual element
		this.mcAxisBg = this.axisContMC.createEmptyMovieClip("AxisBg", this.axisContMC.getNextHighestDepth());
		//Main line and tick marks
		this.mcAxis = this.axisContMC.createEmptyMovieClip("Axis", this.axisContMC.getNextHighestDepth());
		//Create a temp MC for Simulation Mode of the Text
		this.tempMC = this.axisContMC.createEmptyMovieClip("TempMC", this.axisContMC.getNextHighestDepth());
		//Imaginary or not
		this.showAxis = showAxis;
		this.axisId = axisId;
		//Store the position
		this.x = x;
		this.y = y;
		this.height = height;
		//Position
		this.axisOnLeft = axisOnLeft;
		this.title = title;
		//title position
		this.titlePosition = titlePosition;
		this.setAdaptiveYMin = setAdaptiveYMin;
		this.adjustDiv = adjustDiv;
		this.numDivLines = numDivLines;
		//DivLine properties
		this.showDivLineValues = divLineProp.showDivLineValues;
		this.divLineThickness = divLineProp.divLineThickness;
		this.divLineColor = divLineProp.divLineColor;
		this.divLineAlpha = divLineProp.divLineAlpha;
		this.divLineIsDashed = divLineProp.divLineIsDashed;
		this.divLineDashLen = divLineProp.divLineDashLen;
		this.divLineDashGap = divLineProp.divLineDashGap;
		this.divLineFontObj = divLineProp.divLineFontObj;
		//Store the number formatting properties
		this.formatNumber = numberFormattingProp.formatNumber;
		this.formatNumberScale = numberFormattingProp.formatNumberScale;
		this.defaultNumberScale = numberFormattingProp.defaultNumberScale;
		this.nsu = numberFormattingProp.nsu;
		this.nsv = numberFormattingProp.nsv;
		this.numberScaleDefined = numberFormattingProp.numberScaleDefined;
		this.numberPrefix = numberFormattingProp.numberPrefix;
		this.numberSuffix = numberFormattingProp.numberSuffix;
		this.decimalSeparator = numberFormattingProp.decimalSeparator;
		this.thousandSeparator = numberFormattingProp.thousandSeparator;
		this.yAxisValueDecimals = numberFormattingProp.yAxisValueDecimals;
		this.decimals = numberFormattingProp.decimals;
		this.forceDecimals = numberFormattingProp.forceDecimals;
		//Zero Plane properties
		this.showZeroPlane = zeroPlaneProp.showZeroPlane;
		this.zeroPlaneColor = zeroPlaneProp.zeroPlaneColor;
		this.zeroPlaneThickness = zeroPlaneProp.zeroPlaneThickness;
		this.zeroPlaneAlpha = zeroPlaneProp.zeroPlaneAlpha;
		//Store the max and min Data value
		this.yAxisMaxValue = yAxisMaxValue;
		this.yAxisMinValue = yAxisMinValue;
		this.maxValue = maxDataValue;
		this.minValue = minDataValue;
		this.showLimits = showLimits;
		this.yAxisValuesStep = yAxisValuesStep;
		this.axisTitleFontObj = axisTitleFontObj;
		this.showHide = showHide;
		this.showTitle = showTitle;
		this.styleM = styleM;
		this.macro = macro;
		this.fcEnumObj = fcEnumObj;
		this.animationFlag = animation;
		this.allowAxisShift = allowAxisShift;
		//Initialize the array
		this.divLines = new Array();
		this.maxWReqd = 0;
		this.maxTW= 0;
		//Color for the checkBox
		this.checkBoxColor = checkBoxColor;
		//Initialize the listeners Object
		this.checkListener = new Object();
		//Initialize the checkBox Component
		this.cb = new FCCheckBox(this.axisContMC, axisId, this.axisContMC.getNextHighestDepth());
		//Store parameters in instance variables
		//call initialize on EventDispatcher to
		//implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
	}
	/**
	 * setParams method sets the cosmetic and functional parameters 
	 * for the Axis.	 
	*	@param	lineThickness	Sets the axis line thickness
	*	@param	axisColor	Sets the axis drawing color
	*	@param	tickWidth	Sets the tick width for the axis
	*/
	public function setParams(lineThickness:Number, axisColor:String, tickWidth:Number) {
		//Store in instance variables.
		this.lineThickness = lineThickness;
		this.axisColor = axisColor;
		this.tickWidth = tickWidth;
	}
	//these functions are defined in the class to prevent
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
	 * getPosition method returns the new position of the Axis
	*/
	public function getPosition():Object {
		//Create the object
		var pos:Object = new Object();
		//If tween animation is on and final position is yet to be achieved
		if(this.twMC.finish != this.twMC.position){
			pos.x = this.twMC.finish;
		}else{
			//Store the x and y position
			pos.x = this.axisContMC._x;
		}
		pos.y = this.axisContMC._y;
		
		//return
		return pos;
	}
	/**
	 * setPosition method sets the new position of the Axis
	*	@param	X	Sets the X position of the axis 
	*	@param	Y	Sets the Y position of the axis 
	*/
	public function setPosition(x:Number, y:Number):Void {
		//Set the position
		if(!this.twMC){
			this.twMC = new Tween(this.axisContMC, "_x", Regular.easeOut, this.axisContMC._x, x, tweenDuration, true);
		}else{
			this.twMC.stop();
			this.twMC.continueTo(x, tweenDuration);
		}
	}
	/**
	 * calcPoints method calculates the maximum width and height required for axis
	*/
	public function calcPoints():Void {
		//Variable for the simulated Textfield
		var testTFY:Number = -2000;
		var testTFX:Number = -2000;
		//title width the height required
		var titleW:Number, titleH:Number;
		//Object for the text field
		var tmpObj:Object;
		//Get the axis limits
		this.getAxisLimits(false, !this.setAdaptiveYMin);
		//Calculate the div lines properties
		this.calcDivs();
		//Calculate the maximum width required for the divLine Value
		for (var i:Number = 1; i<this.divLines.length; i++) {
			//if the limits or the divLines has to be shown
			if (this.showLimits || this.divLines[i].showValue) {
				//Get the maximum height and width required
				tmpObj = Utils.createText(false, this.divLines[i].displayValue, this.tempMC, 1, testTFX, testTFY, 0, this.divLineFontObj, false, 0, 0);
			}
			//Check for the maximum among the divLine Value and the title of the axis
			this.maxTW = this.maxWReqd = (tmpObj.width>this.maxWReqd) ? (tmpObj.width) : (this.maxWReqd);
			//Remove the simulated textfield
			tmpObj.tf.removeTextField();
		}
		//Add the tickWidth and the padding required
		this.maxWReqd += this.tickWidth+VALUE_PADDING;
		//If the checkBox has to be shown
		if(this.showHide) {
			//Register the listener and delegate it
			this.checkListener.onCheck = Delegate.create(this, onCheck);
			//Add event listener
			this.cb.addEventListener("onCheck", this.checkListener);
			//Set the cosmetic paramter 
			//Send the max Width based on the position of the axis
			var w:Number = (this.axisOnLeft == false)?(-this.maxWReqd):(this.maxWReqd);
			//Show label in checkBox or not
			if(this.showTitle) {
				this.cb.setParams(this.title, this.x, this.y+this.height+this.CB_PADDING, 0, w, checkBoxColor, this.titlePosition, axisTitleFontObj);
			}else{
				this.cb.setParams("", this.x, this.y+this.height+this.CB_PADDING, 0, w, checkBoxColor, this.titlePosition, axisTitleFontObj);
			}
			//Calculate the dimension of the checkbox
			this.cb.calcDimension();
			//Get the width of the checkbox
			titleW = this.cb.getWidth();
			//get the height
			titleH = this.cb.getHeight();
			//Else we just show the title
		} else if(this.showTitle) {
			//Set the alignment
			axisTitleFontObj.align = "left";
			//Create a simulated text to get the width require
			var titleObj:Object = Utils.createText(true, this.title, this.axisContMC, this.axisContMC.getNextHighestDepth(), -2000, -2000, 0, axisTitleFontObj, false, 0, 0);
			//get the width and height required
			titleW = titleObj.width/2;
			titleH = titleObj.height;
			//Remove the textfield
			titleObj.tf.removeTextField();
		}
		//Set the maximum height and width parameter
		this.maxWReqd = (titleW>this.maxWReqd) ? (titleW) : (this.maxWReqd);
		//Maximum height is always the height required for the checkbox or the title component
		this.maxHReqd = titleH;
	}
	/**
	 * drawAxis method draw the Axis, tick values and the tickmarks
	*/
	public function drawAxis():Void {
		//Draw Axis
		//Draw only when the axis is not imaginary
		if(this.showAxis) {
			//Create an Object for the DivLines
			var divLineValueObj:Object;
			//Y Position of the ticks
			var yPos:Number = 0;
			//Movie clip container
			var divLineMC:MovieClip;
			//Create a BG Axis to define the clickable property of the axis
			this.mcAxisBg.beginFill(parseInt("000000", 16), 0);
			if(this.axisOnLeft) {
				this.mcAxisBg.moveTo(-(this.maxTW + this.tickWidth), 0);
				this.mcAxisBg.lineTo(0, 0);
				this.mcAxisBg.lineTo(0, this.height);
				this.mcAxisBg.lineTo(-(this.maxTW + this.tickWidth), this.height);
				this.mcAxisBg.lineTo(-(this.maxTW + this.tickWidth), 0);
			} else {
				this.mcAxisBg.moveTo(0, 0);
				this.mcAxisBg.lineTo((this.maxTW + this.tickWidth), 0);
				this.mcAxisBg.lineTo((this.maxTW + this.tickWidth), this.height);
				this.mcAxisBg.lineTo(0, this.height);
				this.mcAxisBg.lineTo(0, 0);
			}
			this.mcAxisBg.endFill();
			//Add onRelease listener for the axis BG
			//Register and Add the listeners
			this.mcAxisBg.onPress = Delegate.create(this, onAxisClick);
			if(this.allowAxisShift) { 
				this.mcAxisBg.onRollOver = Delegate.create(this, onAxisRollOver);
				this.mcAxisBg.onRollOut= Delegate.create(this, onAxisRollOut);
				this.mcAxisBg.onReleaseOutside = Delegate.create(this, onAxisRollOut);
			}
			else {
				this.mcAxisBg.useHandCursor = false;
			}
			//Draw each of the tick marks for the number of divLines
			//Now draw the axis with TickMarks
			//Set the linestyle
			this.mcAxis.lineStyle(this.lineThickness, parseInt(this.axisColor, 16), 100);
			for (var i:Number = 0; i<this.divLines.length; i++) {
				//Get y position
				yPos = this.getAxisPosition(this.divLines[i].value, this.yMax, this.yMin, 0, this.height, true, 0);
				//Draw the ticks
				this.mcAxis.moveTo(0, yPos);
				//Check whether we need to show the zero plane tickmark
				if(!(this.divLines[i].displayValue ==0 &&  !this.showZeroPlane)) {
					//Depending upon the position place it in the canvas
					if (this.axisOnLeft) {
						//tick Mark
						this.mcAxis.lineTo(-this.tickWidth, yPos);
						//DivLine Value alignment
						divLineFontObj.align = "right";
					} else {
						this.mcAxis.lineTo(this.tickWidth, yPos);
						//DivLine Value alignment
						divLineFontObj.align = "left";
					}
				}
				//If it's the first or last div Line (limits), and limits are to be shown
				if ((i == 0) || (i == this.divLines.length-1)) {
					if (this.showLimits && this.divLines[i].showValue && this.showDivLineValues) {
						//Create the limits text - based on its position
						if (this.axisOnLeft) {
							divLineValueObj = Utils.createText(false, this.divLines[i].displayValue, this.axisContMC, this.axisContMC.getNextHighestDepth(), -this.tickWidth-VALUE_PADDING, yPos, 0, divLineFontObj, false, 0, 0);
						} else {
							divLineValueObj = Utils.createText(false, this.divLines[i].displayValue, this.axisContMC, this.axisContMC.getNextHighestDepth(), this.tickWidth+VALUE_PADDING, yPos, 0, divLineFontObj, false, 0, 0);
						}
						//Apply filters
						this.styleM.applyFilters (divLineValueObj.tf, this.fcEnumObj.YAXISVALUES);
					}
					//If the zeroPlane has to be shown
				} else if (this.divLines [i].value == 0) {
					//It's a zero value div line - check if we've to show
					if (this.showZeroPlane)
					{
						//Render the line
						var zeroPlaneMC:MovieClip = this.divLineContMC.createEmptyMovieClip("ZeroPlane_"+this.axisId, this.divLineContMC.getNextHighestDepth());
						//Draw the line
						zeroPlaneMC.lineStyle (this.zeroPlaneThickness, parseInt (this.zeroPlaneColor, 16) , this.zeroPlaneAlpha);
						//If dashed line is required for the divLines
						if (this.divLineIsDashed)
						{
							//Dashed line
							DrawingExt.dashTo (zeroPlaneMC, - this.canvasW / 2, 0, this.canvasW / 2, 0, this.divLineDashLen, this.divLineDashGap);
						} else 
						{
							//Draw the line keeping 0,0 as registration point
							zeroPlaneMC.moveTo ( - this.canvasW / 2, 0);
							//Normal line
							zeroPlaneMC.lineTo (this.canvasW / 2, 0);
						}
						//Re-position the div line to required place
						zeroPlaneMC._x = this.canvasX+this.canvasW/2;
						zeroPlaneMC._y = this.y+yPos-(this.zeroPlaneThickness/2);
						//Apply animation and filter effects to div line
						//Apply animation
						if (this.animationFlag)
						{
							this.styleM.applyAnimation (zeroPlaneMC, this.fcEnumObj.DIVLINES, this.macro, null, 0, zeroPlaneMC._y, 0, 100, 100, null, null);
						}
						//Apply filters
						this.styleM.applyFilters (zeroPlaneMC, this.fcEnumObj.DIVLINES);
						//So, check if we've to show div line values
						if (this.divLines[i].showValue && this.showDivLineValues)
						{
							//Create the text
							//Create the zerPlane text - based on its position
							if (this.axisOnLeft) {
								this.zeroPlaneTf = Utils.createText (false, this.divLines [i].displayValue, this.axisContMC, this.axisContMC.getNextHighestDepth(), -this.tickWidth-VALUE_PADDING, yPos, 0, divLineFontObj, false, 0, 0);
							} else {
								this.zeroPlaneTf = Utils.createText (false, this.divLines [i].displayValue, this.axisContMC, this.axisContMC.getNextHighestDepth(), this.tickWidth+VALUE_PADDING, yPos, 0, divLineFontObj, false, 0, 0);
							}
						}
						//Apply animation and filter effects to div line (y-axis) values
						if (this.divLines [i].showValue && this.showDivLineValues)
						{
							//Apply filters
							this.styleM.applyFilters (this.zeroPlaneTf.tf, this.fcEnumObj.YAXISVALUES);
						}
				}
			}  else {
					if (this.divLines[i].showValue && this.showDivLineValues) {
						divLineMC = this.divLineContMC.createEmptyMovieClip("DivLine_" + i, this.divLineContMC.getNextHighestDepth());
						//Draw the line
						divLineMC.lineStyle (this.divLineThickness, parseInt (this.divLineColor, 16) , this.divLineAlpha);
						divLineMC._visible = false;
						if (this.divLineIsDashed)
						{
							//Dashed line
							DrawingExt.dashTo (divLineMC, - this.canvasW / 2, 0, this.canvasW / 2, 0, this.divLineDashLen, this.divLineDashGap);
						} else 
						{
							//Draw the line keeping 0,0 as registration point
							divLineMC.moveTo ( - this.canvasW / 2, 0);
							//Normal line
							divLineMC.lineTo (this.canvasW / 2, 0);
						} 
						//Re-position the div line to required place
						divLineMC._x = this.canvasW / 2 +(this.canvasX);
						divLineMC._y = this.y +yPos - (this.divLineThickness / 2);
						//Apply animation and filter effects to div line
						//Apply animation
						if (this.animationFlag)
						{
							this.styleM.applyAnimation (divLineMC, this.fcEnumObj.DIVLINES, this.macro, null, 0, divLineMC._y, 0, 100, 100, null, null);
						}
						//Apply filters
						this.styleM.applyFilters (divLineMC, this.fcEnumObj.DIVLINES);
						if (this.axisOnLeft) {
							divLineValueObj = Utils.createText(false, this.divLines[i].displayValue, this.axisContMC, this.axisContMC.getNextHighestDepth(), -this.tickWidth-VALUE_PADDING, yPos, 0, divLineFontObj, false, 0, 0);
						} else {
							divLineValueObj = Utils.createText(false, this.divLines[i].displayValue, this.axisContMC, this.axisContMC.getNextHighestDepth(), this.tickWidth+VALUE_PADDING, yPos, 0, divLineFontObj, false, 0, 0);
						}
						//Apply filters
						this.styleM.applyFilters (divLineValueObj.tf, this.fcEnumObj.YAXISVALUES);
					}
				}
	
			}
			//Draw from top to bottom (base line)
			this.mcAxis.moveTo(0, 0);
			this.mcAxis.lineTo(0, this.height);
			//Now reset the position by placing the container at the specified location
			if (this.axisOnLeft) {
				//If the axis is placed on the left of canvas
				axisTitleFontObj.align = "center";
				//Set the position
				this.axisContMC._x =this.x+this.maxWReqd+VALUE_PADDING;
				this.axisContMC._y =this.y;
				//Set the checkBox position
				if(this.showHide && this.titlePosition.toUpperCase()=="LEFT") {
					//If the checkBox has to be placed on either side of the label
					this.cb.setPosition(0, this.height+this.CB_PADDING);				
				} else if(this.showHide && this.titlePosition.toUpperCase()=="RIGHT" ) {
					//If the checkBox has to be placed on either side of the label
					this.cb.setPosition(-this.maxWReqd, this.height+this.CB_PADDING);				
				}
				if(this.showHide && this.titlePosition.toUpperCase()=="BOTTOM") {
					//If the checkBox has to be placed on the next line
					this.cb.setPosition(0, this.height+this.CB_PADDING);				
				}
			} else {
				//If the axis is placed on the right of canvas
				axisTitleFontObj.align = "center";
				//place the container
				this.axisContMC._x =this.x+this.canvasX+this.canvasW;
				this.axisContMC._y =this.y;
				//Reset the checkbox position
				//Set the checkBox position
				if(this.showHide && this.titlePosition.toUpperCase()=="LEFT") {
					//If the checkBox has to be placed on either side of the label
					this.cb.setPosition(this.maxWReqd, this.height+this.CB_PADDING);				
				} else if(this.showHide && this.titlePosition.toUpperCase()=="RIGHT" ) {
					//If the checkBox has to be placed on either side of the label
					this.cb.setPosition(0, this.height+this.CB_PADDING);				
				}
				if(this.showHide && this.titlePosition.toUpperCase()=="BOTTOM") {
					//If the checkBox has to be placed on the next line
					this.cb.setPosition(0, this.height+this.CB_PADDING);				
				}
			}
			//Now draw the checkbox
			if(this.showHide) {
				//Draw
				this.cb.draw();
				this.cb.setColor(checkBoxColor);
				//else place the title if required
			} else if(this.showTitle) {
				//Create the title Text
				Utils.createText(false, this.title, this.axisContMC, this.axisContMC.getNextHighestDepth(), 0, this.height+this.CB_PADDING, 0, axisTitleFontObj, true, this.maxWReqd*1.2, 30);
			}
		}
	}
	/**
	* getAxisLimits method helps calculate the axis limits based
	* on the given maximum and minimum value.
	*	@param	stopMaxAtZero	Flag indicating whether maximum value can
	*							be less than 0.
	*	@param	setMinAsZero	Whether to set the lower limit as 0 or a greater
	*							appropriate value (when dealing with positive numbers)
	*/
	private function getAxisLimits(stopMaxAtZero:Boolean, setMinAsZero:Boolean):Void {
		//Use global max and min Data value
		//First check if both this.maxValue and this.minValue are proper numbers.
		//Else, set defaults as 90,0
		this.maxValue = (isNaN(this.maxValue) == true || this.maxValue == undefined) ? 0.9 : this.maxValue;
		this.minValue = (isNaN(this.minValue) == true || this.minValue == undefined) ? 0 : this.minValue;
		//Or, if only 0 data is supplied
		if ((this.maxValue == this.minValue) && (this.maxValue == 0)) {
			this.maxValue = 90;
		}
		//Defaults for stopMaxAtZero and setMinAsZero    
		stopMaxAtZero = Utils.getFirstValue(stopMaxAtZero, false);
		setMinAsZero = Utils.getFirstValue(setMinAsZero, true);
		//Get the maximum power of 10 that is applicable to this.maxValue
		//The Number = 10 to the power maxPowerOfTen + x (where x is another number)
		//For e.g., in 99 the maxPowerOfTen will be 1 = 10^1 + 89
		//And for 102, it will be 2 = 10^2 + 2
		var maxPowerOfTen:Number = Math.floor(Math.log(Math.abs(this.maxValue))/Math.LN10);
		//Get the minimum power of 10 that is applicable to this.maxValue
		var minPowerOfTen:Number = Math.floor(Math.log(Math.abs(this.minValue))/Math.LN10);
		//Find which powerOfTen (the max power or the min power) is bigger
		//It is this which will be multiplied to get the y-interval
		var powerOfTen:Number = Math.max(minPowerOfTen, maxPowerOfTen);
		var y_interval:Number = Math.pow(10, powerOfTen);
		//For accomodating smaller range values (so that scale doesn't represent too large an interval
		if (Math.abs(this.maxValue)/y_interval<2 && Math.abs(this.minValue)/y_interval<2) {
			powerOfTen--;
			y_interval = Math.pow(10, powerOfTen);
		}
		//If the y_interval of min and max is way more than that of range.    
		//We need to reset the y-interval as per range
		var rangePowerOfTen:Number = Math.floor(Math.log(this.maxValue-this.minValue)/Math.LN10);
		var rangeInterval:Number = Math.pow(10, rangePowerOfTen);
		//Now, if rangeInterval is 10 times less than y_interval, we need to re-set
		//the limits, as the range is too less to adjust the axis for max,min.
		//We do this only if range is greater than 0 (in case of 1 data on chart).
		if (((this.maxValue-this.minValue)>0) && ((y_interval/rangeInterval)>=10)) {
			y_interval = rangeInterval;
			powerOfTen = rangePowerOfTen;
		}
		//Calculate the y-axis upper limit    
		var y_topBound:Number = (Math.floor(this.maxValue/y_interval)+1)*y_interval;
		//Calculate the y-axis lower limit
		var y_lowerBound:Number;
		//If the min value is less than 0
		if (this.minValue<0) {
			//Then calculate by multiplying negative numbers with y-axis interval
			y_lowerBound = -1*((Math.floor(Math.abs(this.minValue/y_interval))+1)*y_interval);
		} else {
			//Else, simply set it to 0.
			if (setMinAsZero) {
				y_lowerBound = 0;
			} else {
				y_lowerBound = Math.floor(Math.abs(this.minValue/y_interval)-1)*y_interval;
				//Now, if this.minValue>=0, we keep x_lowerBound to 0 - as for values like this.minValue 2
				//lower bound goes negative, which is not required.
				y_lowerBound = (y_lowerBound<0) ? 0 : y_lowerBound;
			}
		}
		//this.maxValue cannot be less than 0 if stopMaxAtZero is set to true
		if (stopMaxAtZero && this.maxValue<=0) {
			y_topBound = 0;
		}
		//Now, we need to make a check as to whether the user has provided an upper limit    
		//and lower limit.
		if (this.yAxisMaxValue == null || this.yAxisMaxValue == undefined || this.yAxisMaxValue == "") {
			this.yMaxGiven = false;
		} else {
			this.yMaxGiven = true;
		}
		if (this.yAxisMinValue == null || this.yAxisMinValue == undefined || this.yAxisMinValue == "" || Number(this.yAxisMinValue) == NaN) {
			this.yMinGiven = false;
		} else {
			this.yMinGiven = true;
		}
		//If he has provided it and it is valid, we leave it as the upper limit
		//Else, we enforced the value calculate by us as the upper limit.
		if (this.yMaxGiven == false || (this.yMaxGiven == true && Number(this.yAxisMaxValue)<this.maxValue)) {
			this.yMax = y_topBound;
		} else {
			this.yMax = Number(this.yAxisMaxValue);
		}
		//Now, we do the same for y-axis lower limit
		if (this.yMinGiven == false || (this.yMinGiven == true && Number(this.yAxisMinValue)>this.minValue)) {
			this.yMin = y_lowerBound;
		} else {
			this.yMin = Number(this.yAxisMinValue);
		}
		//Store axis range
		this.range = Math.abs(this.yMax-this.yMin);
		//Store interval
		this.interval = y_interval;
	}
	/**
	* calcDivs method calculates the best div line interval for the given/calculated
	* yMin, yMax, specified numDivLines and adjustDiv.
	* We re-set the y axis min and max value, if both were calculated by our
	* us, so that we get a best value according to numDivLines. The idea is to have equal
	* intervals on the axis, based on numDivLines specified. We do so, only if both yMin and
	* yMax have been calculated as per our values. Else, we adjust the numDiv Lines.
	*/
	private function calcDivs():Void {
		/**
		* There can be four cases of yMin, yMax.
		* 1. User doesn't specify either. (our program calculates it).
		* 2. User specifies both in XML. (which our program still validates)
		* 3. User specifies only yMin. (we provide missing data)
		* 4. User specifies only yMax. (we provide missing data)
		*
		* Apart from this, the user can specify numDivLines (which if not specified takes a
		* default value of 4). Also, the user can specify adjustDiv (which can be 1 or 0).
		* adjustDiv works in all four cases (1,2,3,4).
		* Case 1 is modified to occur as under now: User doesn't specify either yMin or yMax
		* and adjustDiv is set to true (by default). If the user doesn't specify either yMin or
		* yMax, but adjustDiv is set to false, it doesn't appear as Case 1. However, if adjustDiv
		* is set to true and yMin,yMax is automatically calculated by FusionCharts, we adjust the
		* calculated yMin,yMax so that the given number of div lines can be well adjusted within.
		*
		* In case 2,3,4, we adjust numDivLines so that they space up equally based on the interval
		* and decimals required.
		*
		* So, the difference between Case 1 and Case 2,3,4 is that in Case 1, we adjust limits
		* to accomodate specified number of div lines. In Case 2,3,4, we adjust numDivLines to
		* accomodate within the given limits (y-axis range).
		*
		* numDivLines is always our primary focus when calculating them in all cases. In Case 1,
		* it's kept constant as center of calculation. In Case 2, it's modified to get a best
		* value.
		*
		* Now, for case 1, we can have three sub cases:
		* 1.1. yMax, yMin >=0
		* 1.2. yMin, yMax <=0
		* 1.3. yMax > 0 and yMin <0.
		* Case 1.1 and 1.2 are simple, as we just need to adjust the range between two positive
		* or two negative numbers such that the range can be equally divided into (numDivLines+1)
		* division.
		* Case 1.3 is tricky, as here, we additionally need to make sure that 0 plane is included
		* as a division.
		* We use two different methods to solved Case 1.1,1.2 and Case 1.3.
		* Note that in all Cases (1.1, 1.2 & 1.3), we adjust the auto-calculated yMax and yMin
		* to get best div line value. We do NOT adjust numDivLines here.
		*/
		//Check condition for case 1 first - limits not specified and adjustDiv is true
		if (this.yMinGiven == false && this.yMaxGiven == false && this.adjustDiv == true) {
			//Means neither chart max value nor min value has been specified and adjustDiv is true
			//Set flag that we do not have to format div (y-axis values) decimals
			this.formatDivDecimals = false;
			//Now, if it's case 1.3 (yMax > 0 & yMin <0)
			if (this.yMax>0 && this.yMin<0) {
				//Case 1.3
				/**
				* Here, in this case, we start by generating the best fit range
				* for the given yMin, yMax, numDivLines and interval. We generate
				* range by adding sequential increments (rangeDiv * (ND+1) * interval).
				* Interval has been adjusted to smaller interval for larger values.
				* Now, for each divisible range generated by the program, we adjust the
				* yMin and yMax to check if 0 can land as a division in between them on
				* a proper distance.
				* We divide the y-axis range into two parts - small arm and big arm.
				* Say y-axis range is from 1 to -5. So, small arm is 1 and big arm is -5.
				* Or, if its from 16 to -3, small arm is -3 and big arm is 16.
				* Now, we try and get a value for extended small arm, which is multiple
				* of rangeDiv. Say chart min,max is 16,-3. So range becomes 19.
				* Let's assume numDivLines to be 2. So for 2 numDivLines, we get closest
				* adjusted range value as 21. Delta range = 21 - 19 (original range) = 2
				* Also, range division value = 21 / (ND + 1) = 21 / 3 = 7
				* We now get values for extended small arm as i*range division, where i
				* runs from 1 to (numDivLines+1)/2. We go only halfway as it's the smaller
				* arm and so cannot extend to a division beyond half way - else it would have
				* been the bigger arm.
				* So, first extended small arm = -7 * 1 = -7.
				* We get the difference between extended small arm and original small arm.
				* In this case it's 7 - 3 = 4 (all absolute values now - to avoid sign disparities).
				* We see that delta arm > delta range. So, we ignore this range and get a new range.
				* So, next range comes as = prev Range (21) + (numDivLines + 1)*interval
				* = 21 + (2+1)*1 = 24
				* Since the increment is sequential as a multiplication factor of
				* (numDivLines + 1)*interval, it is also a valid divisible range.
				* So we again check whether 0 can appear as a division. In this case, we
				* get rangeDiv as 8 and extended smaller arm as 8. For this extended smaller
				* arm, we get bigger arm as 16. Both of these are divisible by rangeDiv. That
				* means, this range can include 0 as division line. So, we store it and proceed.
				*/
				//Flag to indicate whether we've found the perfect range or not.
				var found:Boolean = false;
				//We re-calculate the interval to get smaller increments for large values.
				//For example, for 300 to -100 (with ND as 2), if we do not adjust interval, the min
				//max come as -200, 400. But with adjusted intervals, it comes as -150, 300, which is
				//more appropriate.
				var adjInterval = (this.interval>10) ? (this.interval/10) : this.interval;
				//Get the first divisible range for the given yMin, yMax, numDivLines and interval.
				//We do not intercept and adjust interval for this calculation here.
				var range:Number = getDivisibleRange(this.yMin, this.yMax, this.numDivLines, adjInterval, false);
				//Now, deduct delta range from nextRange initially, so that in while loop,
				//there's a unified statement for increment instead of 2 checks.
				var nextRange:Number = range-(this.numDivLines+1)*(adjInterval);
				//Range division
				var rangeDiv:Number;
				//Delta in range
				var deltaRange:Number;
				//Multiplication factor
				var mf:Number;
				//Smaller and bigger arm of y-axis
				var smallArm:Number, bigArm:Number;
				//Exntended small and big arm
				var extSmallArm:Number, extBigArm:Number;
				//Loop variable
				var i:Number;
				//Now, we need to search for a range which is divisible in (this.params.numDivLines+1)
				//segments including 0. Run a while loop till that is found.
				while (found == false) {
					//Get range
					nextRange = nextRange+(this.numDivLines+1)*(adjInterval);
					//If it's divisible for the given range and adjusted interval, proceed
					if (isRangeDivisible(nextRange, this.numDivLines, adjInterval)) {
						//Delta Range
						deltaRange = nextRange-this.range;
						//Range division
						rangeDiv = nextRange/(this.numDivLines+1);
						//Get the smaller arm of axis
						smallArm = Math.min(Math.abs(this.yMin), this.yMax);
						//Bigger arm of axis.
						bigArm = this.range-smallArm;
						//Get the multiplication factor (if smaller arm is negative, set -1);
						mf = (smallArm == Math.abs(this.yMin)) ? -1 : 1;
						//If num div lines ==0, we do not calculate anything
						if (this.numDivLines == 0) {
							//Set flag to true to exit loop
							found = true;
						} else {
							//Now, we need to make sure that the smaller arm of axis is a multiple
							//of rangeDiv and the multiplied result is greater than smallArm.
							for (var i = 1; i<=Math.floor((this.numDivLines+1)/2); i++) {
								//Get extended small arm
								extSmallArm = rangeDiv*i;
								//If extension is more than original intended delta, we move to next
								//value of loop as this range is smaller than our intended range
								if ((extSmallArm-smallArm)>deltaRange) {
									//Iterate to next loop value
									continue;
								}
								//Else if extended arm is greater than smallArm, we do the 0 div test 
								if (extSmallArm>smallArm) {
									//Get extended big arm
									extBigArm = nextRange-extSmallArm;
									//Check whether for this range, 0 can come as a div
									//By checking whether both extBigArm and extSmallArm
									//are perfectly divisible by rangeDiv
									if (((extBigArm/rangeDiv) == (Math.floor(extBigArm/rangeDiv))) && ((extSmallArm/rangeDiv) == (Math.floor(extSmallArm/rangeDiv)))) {
										//Store in global containers
										this.range = nextRange;
										this.yMax = (mf == -1) ? extBigArm : extSmallArm;
										this.yMin = (mf == -1) ? (-extSmallArm) : (-extBigArm);
										//Set found flag to true to exit loop
										found = true;
									}
								} else {
									//Iterate to next loop value, as we need the arm to be greater
									//than original value.
									continue;
								}
							}
						}
					}
				}
			} else {				
				//Case 1.1 or 1.2
				/**
				* In this case, we first get apt divisible range based on yMin, yMax,
				* numDivLines and the calculated interval. Thereby, get the difference
				* between original range and new range and store as delta.
				* If yMax>0, add this delta to yMax. Else substract from yMin.
				*/
				//Get the adjusted divisible range
				var adjRange:Number = getDivisibleRange(this.yMin, this.yMax, this.numDivLines, this.interval, true);
				//Get delta (Calculated range minus original range)
				var deltaRange:Number = adjRange-this.range;
				//Update global range storage
				this.range = adjRange;
				//Now, add the change in range to yMax, if yMax > 0, else deduct from yMin
				if (this.yMax>0) {
					this.yMax = this.yMax+deltaRange;
				} else {
					this.yMin = this.yMin-deltaRange;
				}
			}
		} else {
			/**
			* Here, we've to handle the following cases
			* 2. User specifies both yMin, yMax in XML. (which our program still validates)
			* 3. User specifies only yMin. (we provide yMax)
			* 4. User specifies only yMax. (we provide yMin)
			* Now, for each of these, there can be two cases. If the user has opted to
			* adjust div lines or not. If he has opted to adjustDiv, we calculate the best
			* possible number of div lines for the given range. If not, we simply divide
			* the given (or semi-calculated) axis limits by the number of div lines.
			*/
			if (this.adjustDiv == true) {
				//We iterate from given numDivLines to 0,
				//Count helps us keep a counter of how many div lines we've checked
				//For the sake of optimization, we check only 25 div lines values
				//From (numDivLines to 0) and (numDivLines to (25-numDivLines))
				//We do it in a yoyo order - i.e., if numDivLines is set as 5,
				//we first check 6 and then 4. Next would be (8,3), (9,2), (10,1),
				//(11, (no value here, as we do not check for 0), 12, 13, 14, 15, 16,
				//17,18,19,20. So, in this way, we check for 25 possible numDivLines and
				//see if any one them fit in. If yes, we store that value. Else, we set it
				//as 0 (indicating no div line feasible for the given value).
				//Perform only if this.params.numDivLines>0
				if (this.numDivLines>0) {
					var counter:Number = 0;
					var multiplyFactor:Number = 1;
					var numDivLines:Number;
					while (1 == 1) {
						//Increment,Decrement numDivLines
						numDivLines = this.numDivLines+(counter*multiplyFactor);
						//Cannot be 0
						numDivLines = (numDivLines == 0) ? 1 : numDivLines;
						//Check whether this number of numDivLines satisfy our requirement
						if (isRangeDivisible(this.range, numDivLines, this.interval)) {
							//Exit loop
							break;
						}
						//Each counter comes twice: one for + count, one for - count 
						counter = (multiplyFactor == -1 || (counter>this.numDivLines)) ? (++counter) : (counter);
						if (counter>25) {
							//We do not go beyond 25 count to optimize.
							//If the loop comes here, it means that divlines
							//counter is not able to achieve the target.
							//So, we assume no div lines are possible and exit.
							numDivLines = 0;
							break;
						}
						//Switch to increment/decrement mode. If counter 
						multiplyFactor = (counter<=this.numDivLines) ? (multiplyFactor*-1) : (1);
					}
					//Store the value in params
					this.numDivLines = numDivLines;
					//Set flag that we do not have to format div (y-axis values) decimals
					this.formatDivDecimals = false;
				}
			} else {
				//We need to set flag that div lines intevals need to formatted
				//to the given precision.
				//Set flag that we have to format div (y-axis values) decimals
				this.formatDivDecimals = true;
			}
		}
		//Set flags pertinent to zero plane
		if (this.yMax>0 && this.yMin<0) {
			this.zeroPRequired = true;
		} else {
			this.zeroPRequired = false;
		}
		//Div interval
		this.divInterval = (this.yMax-this.yMin)/(this.numDivLines+1);
		//Flag to keep a track whether zero plane is included
		this.zeroPIncluded = false;
		//We now need to store all the div line segments in the array this.divLines
		//We include yMin and yMax too in div lines to render in a single loop
		var divLineValue:Number = this.yMin-this.divInterval;
		//Keeping a count of div lines
		var count:Number = 0;
		while (count<=(this.numDivLines+1)) {
			//Converting to string and back to number to avoid Flash's rounding problems.
			divLineValue = Number(String(divLineValue+this.divInterval));
			//Check whether zero plane is included
			this.zeroPIncluded = (divLineValue == 0) ? true : this.zeroPIncluded;
			//Add the div line to this.divLines
			this.divLines[count] = this.returnDataAsDivLine(divLineValue);
			//Based on yAxisValueStep, we need to hide required div line values
			if (count%this.yAxisValuesStep == 0) {
				this.divLines[count].showValue = true;
			} else {
				this.divLines[count].showValue = false;
			}
			//Increment counter
			count++;
		}
		//Now, the array this.divLines contains all the divisional values. But, it might
		//not contain 0 value in Case 2,3,4 (i.e., when the user manually sets things).
		//So, if zero plane is required but not included, we include it.
		if (this.zeroPRequired == true && this.zeroPIncluded == false) {
			//Include zero plane at the right place in the array.
			this.divLines.push(this.returnDataAsDivLine(0));
			//Now, sort on value so that 0 automatically appears at right place
			this.divLines.sortOn("value", Array.NUMERIC);
			//Also increase this.params.numDivLines
			this.numDivLines++;
		}
	}
	/**
	* returnDataAsDivLine method returns the data provided to the method
	* as a div line object.
	*	@param	value	Value of div line
	*	@return		An object with the parameters of div line
	*/
	private function returnDataAsDivLine(value:Number):Object {
		//Create a new object
		var divLineObject = new Object();
		divLineObject.value = value;
		//Display value
		//Now, if numbers are to be restricted to decimal places,
		if (this.formatDivDecimals) {
			//Round off the div line value to this.params.yAxisValueDecimals precision
			divLineObject.displayValue = NumberFormatting.formatNumber(value, this.formatNumber, this.yAxisValueDecimals, this.forceDecimals, this.formatNumberScale, this.defaultNumberScale, this.nsv, this.nsu, this.numberPrefix, this.numberSuffix, this.decimalSeparator, this.thousandSeparator, this.numberScaleDefined);
		} else {
			if (this.numberScaleDefined || this.forceDecimals) {
				//If number scale is defined, we round the numbers
				//Round off the div line value to this.params.decimals precision
				divLineObject.displayValue = NumberFormatting.formatNumber(value, this.formatNumber, this.decimals, this.forceDecimals, this.formatNumberScale, this.defaultNumberScale, this.nsv, this.nsu, this.numberPrefix, this.numberSuffix, this.decimalSeparator, this.thousandSeparator, this.numberScaleDefined);
			} else {
				//Set decimal places as 10, so that none of the decimals get stripped off
				divLineObject.displayValue = NumberFormatting.formatNumber(value, this.formatNumber, 10, this.forceDecimals, this.formatNumberScale, this.defaultNumberScale, this.nsv, this.nsu, this.numberPrefix, this.numberSuffix, this.decimalSeparator, this.thousandSeparator, this.numberScaleDefined);
			}
		}
		//Flag to show values
		divLineObject.showValue = true;
		return divLineObject;
	}
	/**
	* isRangeDivisible method helps us judge whether the given range is
	* perfectly divisible for specified y-interval, numDivLines, yMin and yMax.
	* To check that, we divide the given range into (numDivLines+1) section.
	* If the decimal places of this division value is <= that of interval,
	* that means, this range fits in our purpose. We return a boolean value
	* accordingly.
	*	@param	range		Range of y-axis (Max - Min). Absolute value
	*	@param	numDivLines	Number of div lines to be plotted.
	*	@param	interval	Y-axis Interval (power of ten).
	*	@return			Boolean value indicating whether this range is divisible
	*						by the given number of div lines.
	*/
	private function isRangeDivisible(range:Number, numDivLines:Number, interval:Number):Boolean {
		//Get range division
		var rangeDiv:Number = range/(numDivLines+1);
		//Now, if the decimal places of rangeDiv and interval do not match,
		//it's not divisible, else it's divisible
		if (MathExt.numDecimals(rangeDiv)>MathExt.numDecimals(interval)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	* getDivisibleRange method calculates a perfectly divisible range based
	* on y-interval, numDivLines, yMin and yMax specified.
	* We first get the range division for the existing range
	* and user specified number of div lines. Now, if that division satisfies
	* our needs (decimal places of division and interval is equal), we do NOT
	* change anything. Else, we round up the division to the next higher value {big delta
	* in case of smaller values i.e., interval <1 and small delta in case of bigger values >1).
	* We multiply this range division by number of div lines required and calculate
	* the new range.
	*	@param	yMin			Min value of y-axis
	*	@param	yMax			Max value of y-axis
	*	@param	numDivLines		Number of div lines to be plotted.
	*	@param	interval		Y-axis Interval (power of ten).
	*	@param	interceptRange	Boolean value indicating whether we've to change the range
	*							by altering interval (based on it's own value).
	*	@return				A range that is perfectly divisible into given number of sections.
	*/
	private function getDivisibleRange(yMin:Number, yMax:Number, numDivLines:Number, interval:Number, interceptRange:Boolean):Number {
		//Get the range division for current yMin, yMax and numDivLines
		var range = Math.abs(yMax-yMin);
		var rangeDiv:Number = range/(numDivLines+1);
		//Now, the range is not divisible
		if (!isRangeDivisible(range, numDivLines, interval)) {
			//We need to get new rangeDiv which can be equally distributed.
			//If intercept range is set to true
			if (interceptRange) {
				//Re-adjust interval so that gap is not much (conditional)
				//Condition check limit based on value
				var checkLimit:Number = (interval>1) ? 2 : 0.5;
				if ((Number(rangeDiv)/Number(interval))<checkLimit) {
					//Decrease power of ten to get closer rounding
					interval = interval/10;
				}
			}
			//Adjust range division based on new interval   
			rangeDiv = (Math.floor(rangeDiv/interval)+1)*interval;
			//Get new range
			range = rangeDiv*(numDivLines+1);
		}
		//Return range   
		return range;
	}
	/**
	* getAxisPosition method gets the pixel position of a particular
	* point on the axis based on its value.
	*	@param	value			Numerical value for which we need pixel axis position
	*	@param	upperLimit		Numerical upper limit for that axis
	*	@param	lowerLimit		Numerical lower limit for that axis
	*	@param	startAxisPos	Pixel start position for that axis
	*	@param	endAxisPos		Pixel end position for that axis
	*	@param	isYAxis			Flag indicating whether it's y axis
	*	@param	xPadding		Padding at left and right sides in case of a x-axis
	*	@return				The pixel position of the value on the given axis.
	*/
	private function getAxisPosition(value:Number, upperLimit:Number, lowerLimit:Number, startAxisPos:Number, endAxisPos:Number, isYAxis:Boolean, xPadding:Number):Number {
		//Define variables to be used locally
		var numericalInterval:Number;
		var axisLength:Number;
		var relativePosition:Number;
		var absolutePosition:Number;
		//Get the numerical difference between the limits
		numericalInterval = (upperLimit-lowerLimit);
		if (isYAxis) {
			//If it's y axis, the co-ordinates are opposite in Flash
			axisLength = (endAxisPos-startAxisPos);
			relativePosition = (axisLength/numericalInterval)*(value-lowerLimit);
			//If it's a y axis co-ordinate then go according to Flash's co-ordinate system
			//(y decreases as we go upwards)
			absolutePosition = endAxisPos-relativePosition;
		} else {
			axisLength = (endAxisPos-startAxisPos)-(2*xPadding);
			relativePosition = (axisLength/numericalInterval)*(value-lowerLimit);
			//The normal x-axis rule - increases as we go right
			absolutePosition = startAxisPos+xPadding+relativePosition;
		}
		return absolutePosition;
	}
	/**
	 *  setCanvasPoints sets the calculated Canvas Points to use for DivLines
	 *	@param		canvasX		Starting X position of the canvas
	 *	@param		canvasW		Width of the canvas
	*/
	public function setCanvasPoints(canvasX:Number, canvasW:Number):Void {
		this.canvasX = canvasX;
		this.canvasW = canvasW;
	}
	/**
	 * onCheck is the delegat-ed event handler method that'll
	 * be invoked when the user checks the checkBox to hide/show the lines.
	 * @param	evtObj		Event's object for each CheckBox.
	*/
	private function onCheck(evtObj):Void {
		//Dispatch to its parent class
		this.dispatchEvent({type:"onCheck", target:this, index:evtObj.index, state:evtObj.state});
	}
	/**
	 *  getMinValue returns the minValue of the axis
	*/
	public function getMinValue():Number {
		return this.yMin;
	}
	/**
	 *  getMaxValue returns the maxValue of the axis
	*/
	public function getMaxValue():Number {
		return this.yMax;
	}
	/**
	 *  getHeight returns the Height of the Axis
	*/
	public function getHeight():Number {
		return this.maxHReqd;
	}
	/**
	 *  getWidth returns the Width of the Axis
	*/
	public function getWidth():Number {
		return this.maxWReqd;
	}
	/**
	  * onAxisRollOver method shows the cursor and hides the mouse
	*/
	private function onAxisRollOver():Void {
		if(!this.isPrimary) {
			Mouse.hide();
			this.cursorMC._visible =true;
			this.cursorMC._x = this.mcAxisBg._parent._parent._xmouse;
			this.cursorMC._y = this.mcAxisBg._parent._parent._ymouse;
			var classRef:Object = this;
			//On enter frame we keep the visibility to true 
			//And we hide it only when it is roll out of handler
			this.mcAxisBg.onEnterFrame = function() {
				//Set the new position of the cursor
				classRef.cursorMC._x = this._parent._parent._xmouse;
				classRef.cursorMC._y = this._parent._parent._ymouse;
				//to avoid flickers
				updateAfterEvent();
			};
		} else {
			this.mcAxisBg.useHandCursor = false;
		}
	}
		/**
	  * onAxisRollOut method hides the cursor
	*/
	private function onAxisRollOut():Void {
	    this.cursorMC._visible =false;
		Mouse.show();
		delete this.mcAxisBg.onEnterFrame;
	}

	/**
	  * onAxisClick method dispatches the event for the movement of axis as the primary axis
	  *	of the chart.
	*/
	private function onAxisClick(evtObj):Void {
		//Dispatch to its parent class
		this.dispatchEvent({type:"onAxisClick", target:this, index:this.axisId});
	}
	/**
	  * setPrimary method Sets the axis as the primary/Secondary Axis.
	*/
	public function setPrimary(state:Boolean):Void {
		this.isPrimary = state;
	}
	/**
	  * show method display the axis.
	*/
	public function show():Void {
		//Show the Axis
		this.showAxis = true;
		this.axisContMC._visible = true;
	}
	/**
	 * hide method hides the Axis.
	*/
	public function hide():Void {
		//Hide the Axis
		this.showAxis = false;
	}
	/**
	  * showDivLines method display the axis divlines.
	*/
	public function showDivLines():Void {
		//Show the DivLines
		//Calculate the maximum width required for the divLine Value
		for (var i:Number = 1; i<=this.divLines.length; i++) {
			var divLineMC:MovieClip = eval(this.divLineContMC+".DivLine_" + i);
			//Show the line
			divLineMC._visible = true;
			
		}
		if (this.showZeroPlane)
		{
			//Render the line
			var zeroPlaneMC:MovieClip = eval(this.divLineContMC+".ZeroPlane_"+this.axisId);
			zeroPlaneMC._visible = true;
		}
	}
	/**
	 * hideDivLines method hides the Axis DivLines
	*/
	public function hideDivLines():Void {
		//Hide the DivLines
		//Calculate the maximum width required for the divLine Value
		for (var i:Number = 1; i<=this.divLines.length; i++) {
			var divLineMC:MovieClip = eval(this.divLineContMC+".DivLine_" + i);
			//Hide the line
			divLineMC._visible = false;
			
		}
		if (this.showZeroPlane)
		{
			//Render the line
			var zeroPlaneMC:MovieClip = eval(this.divLineContMC+".ZeroPlane_"+this.axisId);
			zeroPlaneMC._visible = false;
		}
	}
	/**
	  * showCheckBox method display the axis's checkbox.
	*/
	public function showCheckBox():Void {
		//Show the checkbox
		this.cb.show();
	}
	/**
	 * hideCheckBox method hides the Axis's CheckBox.
	*/
	public function hideCheckBox():Void {
		//Hide the CheckBox
		this.cb.hide();
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//Delete the Axis Container
		this.axisContMC.clear();
		//remove the movieclip
		this.axisContMC.removeMovieClip();
		//delete the reference
		delete this.axisContMC;
		this.twMC = null;
		//Delete the divLines
		this.divLineContMC.clear();
		this.divLineContMC.removeMovieClip();
		delete this.divLineContMC;
	}
}

