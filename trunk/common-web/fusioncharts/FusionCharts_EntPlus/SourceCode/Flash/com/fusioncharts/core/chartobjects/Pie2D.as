﻿/**
* @class 		Pie2D
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010

* Pie2D class is responsible of creating a 2D pie slice.
* The pie slice is drawn on its instantiation with passing
* of parameters. Each instance is passed a (common) object
* with a host of properties in them, the movieclip 
* reference in which to draw the pie.
*/
// Import the Delegate class
import mx.utils.Delegate;
//Event Dispatcher
import mx.events.EventDispatcher;
// Import the MathExt class
import com.fusioncharts.extensions.MathExt;
// Import the ColorExt class
import com.fusioncharts.extensions.ColorExt;
// Import the Pie2DChart class (to fix a flash issue)
import com.fusioncharts.core.charts.Pie2DChart;

class com.fusioncharts.core.chartobjects.Pie2D {
	// stores the reference of the basic 2D pie chart class (stored but not yet used)
	private var chartClass;
	// stores the object, with a host of properties, passed as parameter during instantiation of this class
	private var objData:Object;
	// stores the reference of the movieclip constituting the whole pie slice
	private var mcMain:MovieClip;
	// strores the reference of MathExt.toNearestTwip()
	private var toNT:Function;
	//chart limits
	private var canvasXMin:Number;
	private var canvasXMax:Number;
	
	//
	/**
	 * Constructor function for the class. Calls the primary 
	 * drawPie method.
	 * @param	chartClassRef	Name of class instance instantiating this.
	 * @param	mcPie			A movie clip reference passed from the
	 *							main movie. This movie clip is the clip
	 *							inside which we'll draw the 2D Pie
	 *							slice. Has to be necessarily provided.
	 * @param	obj				Object with various properties necessary
	 *							for drawing pie slices.
	 * @param	canvasXMin		canvas x min
	 * @param	canvasXMax		canvas x max
	 */
	public function Pie2D(chartClassRef, mcPie:MovieClip, obj:Object, canvasXMin:Number, canvasXMax:Number) {
		
		// stores the referene of the basic class for creating a 2D pie chart 
		chartClass = chartClassRef;
		// stores the reference of the movieclip inside which all rendering either pie or label or both, 
		// for this pie, need to be done.
		mcMain = mcPie;
		// stores the object, with a host of properties
		objData = obj;
		// storing the refernce of MathExt.toNearestTwip()
		toNT = MathExt.toNearestTwip;
		
		//updating limits
		this.canvasXMin = canvasXMin;
		this.canvasXMax = canvasXMax;
		
		// drawing of the pie slice is initialised 
		drawPie();
		
		//Initialize EventDispatcher to implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
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
	
	/**
	 * drawPie method is called from constructor function.
	 * It works to generate a pie slice as an 'object'.
	 * It calls drawFace and/or drawLabel method as per
	 * requirement.
	 */
	private function drawPie():Void {
		var a:Array = objData.arrFinal;
		// movieclip reference stored in local variable
		var mcPieCanvas:MovieClip = mcMain;
		// inverting the movieclip vertically ... this removes conflict in convention of co-ordinate axes between
		// flash and traditional coordinate geometry ... applicable for this movieclip and its sub-movies ... done
		// only for the sake of simplifying the visualization process
		mcPieCanvas._yscale = -100;
		// shifts the origin of coordinate axes to the lower left corner of the root movieclip ... this completes 
		// the above mentioned attempt
		// isPlotAnimationOver - for rotational animation ..... isInitialised - for no animation at all
		if (objData.isPlotAnimationOver || objData.isInitialised) {
			mcPieCanvas._x = 0;
			mcPieCanvas._y = objData.chartHeight;
		} else {
			mcPieCanvas._x = objData.centerX;
			mcPieCanvas._y = objData.centerY;
		}
		// stores the collection of properties in the pie movieclip itsef for further use
		mcPieCanvas.store = a;
		// movement status for the pie when drawn is flagged initially (centered in)
		if (objData.isInitialised) {
			mcPieCanvas.isSlicedIn = (a['isSliced']) ? false : true;
		} else {
			mcPieCanvas.isSlicedIn = true;
		}
		var insRef:Pie2D = this;
		// objData.isPieHolder == true - to restrict call of movePie() for one of the two
		// constituting each pie. movePie() call for the other one is from this call ... see movePie() for details.
		// isPlotAnimationOver - for rotational animation ..... isInitialised - for no animation at all
		if (a['isSliced'] && !objData.isInitialised && objData.isPlotAnimationOver && objData.isPieHolder) {
			mcPieCanvas.onEnterFrame = function() {
				insRef.movePie();
				delete this.onEnterFrame;
			};
		}
		// if not rotatable and not yet initialised  and its not a singleton case      
		if (!(objData.isRotatable || objData.isInitialised) && objData.isPieHolder != null) {
			mcPieCanvas.enabled = false;
		}
		//-------------------------------------                           
		// if this pie2D instance is to create the pie graphic OR singleton case     
		if (objData.isPieHolder || objData.isPieHolder == null) {
			drawFace();
		}
		//                                                                                     
		if (!mcPieCanvas.isSlicedIn) {
			mcPieCanvas.isMoved = true;
		}
		// if labelProps is defined and this Pie2D instance is for rendering labels OR singleton case            
		if ((a['labelProps'] && !objData.isPieHolder  && a['labelText']) || objData.isPieHolder == null) {
			drawLabel();
		}
	}
	/**
	 * drawFace method is called from drawPie
	 * method. It draws fills and border of pie.
	 */
	private function drawFace():Void {
		var mcCanvas:MovieClip = mcMain.createEmptyMovieClip('mcFace', 1);
		var a:Array = objData.arrFinal;
		var radius:Number = objData.radius;
		// isPlotAnimationOver - for rotational animation ..... isInitialised - for no animation at all
		if (objData.isPlotAnimationOver || objData.isInitialised) {
			var xcenter:Number = objData.centerX;
			var ycenter:Number = objData.centerY;
		} else {
			var xcenter:Number = 0;
			var ycenter:Number = 0;
		}
		var borderThickness:Number = objData.borderThickness;
		var borderColor:Number = a['borderColor'];
		var fillAlpha:Number = a['fillAlpha'];
		var borderAlpha:Number = (a.isDashed) ? 0 : a['borderAlpha'];
		var endBorderAlpha:Number = (a['sweepAngle'] == 360) ? 0 : borderAlpha;
		var xcontrol:Number, ycontrol:Number, xend:Number, yend:Number;
		// setting values from store in Pie2D instance                    
		var steps:Number = a['no45degCurves'];
		var xtra:Number = a['remainderAngle'];
		// isPlotAnimationOver - for rotational animation ..... isInitialised - for no animation at all
		var startAng:Number = (objData.isPlotAnimationOver || objData.isInitialised) ? a['startAngle'] : -MathExt.toRadians(a['sweepAngle'])/2;
		// 
		var xstart:Number = toNT(xcenter+radius*Math.cos(startAng));
		var ystart:Number = toNT(ycenter+radius*Math.sin(startAng));
		//
		if (objData.gradientFill) {
			var strFillType:String = 'radial';
			var shadowIntensity:Number = Math.floor((0.85*(1-0.35*objData.gradientRadius/255))*100)/100;
			var shadowColor:Number = ColorExt.getDarkColor(a['pieColor'].toString(16), shadowIntensity);
			var highLightIntensity:Number = Math.floor((0.5*(1+objData.gradientRadius/255))*100)/100;
			var highLight:Number = ColorExt.getLightColor(a['pieColor'].toString(16), highLightIntensity);
			var arrColors:Array = [highLight, shadowColor];
			var arrAlphas:Array = [fillAlpha, fillAlpha];
			var arrRatios:Array = [objData.gradientRadius, 255];
			//
			var xGrad:Number = xcenter-radius;
			var yGrad:Number = ycenter-radius;
			var widthGrad:Number = 2*radius;
			var heightGrad:Number = 2*radius;
			//
			var objMatrix:Object = {matrixType:"box", x:xGrad, y:yGrad, w:widthGrad, h:heightGrad, r:0};
			mcCanvas.beginGradientFill(strFillType, arrColors, arrAlphas, arrRatios, objMatrix);
		} else {
			mcCanvas.beginFill(a['pieColor'], fillAlpha);
		}
		//
		mcCanvas.moveTo(xcenter, ycenter);
		mcCanvas.lineStyle(borderThickness, borderColor, endBorderAlpha, true, "normal", "round", "round");
		mcCanvas.lineTo(xstart, ystart);
		mcCanvas.lineStyle(borderThickness, borderColor, borderAlpha, true, "normal", "round", "round");
		// drawing 45 degree curves
		for (var j:Number = 1; j<=steps; ++j) {
			// 
			var t:Number = startAng+(Math.PI/4)*j;
			// 
			xend = toNT(xcenter+radius*Math.cos(t));
			yend = toNT(ycenter+radius*Math.sin(t));
			// 
			xcontrol = toNT(xcenter+radius*Math.cos((2*(startAng+(Math.PI/4)*(j-1))+(Math.PI/4))/2)/Math.cos((Math.PI/4)/2));
			ycontrol = toNT(ycenter+radius*Math.sin((2*(startAng+(Math.PI/4)*(j-1))+(Math.PI/4))/2)/Math.cos((Math.PI/4)/2));
			// 
			mcCanvas.curveTo(xcontrol, ycontrol, xend, yend);
		}
		// drawing remainder curve
		var s:Number = startAng+(Math.PI/4)*steps+xtra;
		xend = toNT(xcenter+radius*Math.cos(s));
		yend = toNT(ycenter+radius*Math.sin(s));
		xcontrol = toNT(xcenter+radius*Math.cos((2*(startAng+(Math.PI/4)*steps)+xtra)/2)/Math.cos(xtra/2));
		ycontrol = toNT(ycenter+radius*Math.sin((2*(startAng+(Math.PI/4)*steps)+xtra)/2)/Math.cos(xtra/2));
		//
		mcCanvas.curveTo(xcontrol, ycontrol, xend, yend);
		//
		mcCanvas.lineStyle(borderThickness, borderColor, endBorderAlpha, true, "normal", "round", "round");
		mcCanvas.lineTo(xcenter, ycenter);
		mcCanvas.endFill();
		// call to draw the dashed border, if applicable for this pie
		if (a.isDashed) {
			drawDashedBorder(mcCanvas);
		}
	}
	/**
	 * drawDashedBorder method is called to draw dashed border
	 * holding movieclips.
	 * @param	_mc		reference of mc to draw in
	 */
	private function drawDashedBorder(_mc:MovieClip):Void {
		var a:Array = objData.arrFinal;
		var radius:Number = objData.radius;
		// isPlotAnimationOver - for rotational animation ..... isInitialised - for no animation at all
		if (objData.isPlotAnimationOver || objData.isInitialised) {
			var xcenter:Number = objData.centerX;
			var ycenter:Number = objData.centerY;
		} else {
			var xcenter:Number = 0;
			var ycenter:Number = 0;
		}
		//
		var borderThickness:Number = objData.borderThickness;
		var borderColor:Number = a['borderColor'];
		var borderAlpha:Number = a['borderAlpha'];
		var endBorderAlpha:Number = (a['sweepAngle'] == 360) ? 0 : borderAlpha;
		//
		// isPlotAnimationOver - for rotational animation ..... isInitialised - for no animation at all
		var startAng:Number = (objData.isPlotAnimationOver || objData.isInitialised) ? a['startAngle'] : -MathExt.toRadians(a['sweepAngle'])/2;
		var endAng:Number = (objData.isPlotAnimationOver || objData.isInitialised) ? MathExt.toRadians(a['endAngle']) : +MathExt.toRadians(a['sweepAngle'])/2;
		var sweepAng:Number = a['sweepAngle'];
		//-------------- C A L C U L A T E --------------//
		var dashCurveLength:Number = 5;
		var dashAng:Number = MathExt.toDegrees(dashCurveLength/radius);
		var loops:Number = Math.round(sweepAng/dashAng);
		if (a['sweepAngle'] != 360) {
			// need odd value of 'loops' so that curve both, starts and ends, with dashes 
			loops = (Math.floor(loops/2) == loops/2) ? loops+1 : loops;
		} else {
			// need even value of 'loops' so that curve starts with dash but ends with blank space
			loops = (Math.floor(loops/2) == loops/2) ? loops : loops+1;
		}
		// recalculating dashAng and approximating for optimization
		dashAng = MathExt.toRadians(MathExt.toNearestTwip(sweepAng/loops));
		//------------------------
		var loopsLine:Number = Math.floor(radius/dashCurveLength);
		// need odd value of 'loops'
		loopsLine = (Math.floor(loopsLine/2) == loopsLine/2) ? loopsLine+1 : loopsLine;
		var dx:Number = MathExt.toNearestTwip(radius/loopsLine);
		//--------------- FUNCTION ------------------//
		// rounding off upto 2 significant digits after decimal point
		var roundOff = function (num:Number):Number {
			return Math.floor(num*100)/100;
		};
		//---------------  D R A W  -----------------//
		var mcCanvas:MovieClip = _mc.createEmptyMovieClip('mcDashBorder', _mc.getNextHighestDepth());
		//
		mcCanvas.moveTo(xcenter, ycenter);
		// dashed line along starting angle is drawn
		for (var i:Number = 1; i<=loopsLine; ++i) {
			var alpha:Number = (Math.floor(i/2) == i/2) ? 0 : endBorderAlpha;
			mcCanvas.lineStyle(borderThickness, borderColor, alpha);
			//
			var endX:Number = roundOff(xcenter+i*dx*Math.cos(startAng));
			var endY:Number = roundOff(ycenter+i*dx*Math.sin(startAng));
			//
			mcCanvas.lineTo(endX, endY);
		}
		// dashed curve part is drawn
		for (var i:Number = 1; i<=loops; ++i) {
			var alpha:Number = (Math.floor(i/2) == i/2) ? 0 : borderAlpha;
			mcCanvas.lineStyle(borderThickness, borderColor, alpha);
			//
			var endX:Number = roundOff(xcenter+radius*Math.cos(startAng+i*dashAng));
			var endY:Number = roundOff(ycenter+radius*Math.sin(startAng+i*dashAng));
			//
			var e:Number = radius/Math.cos(dashAng/2);
			var b:Number = startAng+i*dashAng-dashAng/2;
			//
			var controlX:Number = roundOff(xcenter+e*Math.cos(b));
			var controlY:Number = roundOff(ycenter+e*Math.sin(b));
			//
			mcCanvas.curveTo(controlX, controlY, endX, endY);
		}
		// dashed line along starting angle is drawn
		for (var i:Number = loopsLine-1; i>=0; --i) {
			var alpha:Number = (Math.floor(i/2) == i/2) ? endBorderAlpha : 0;
			mcCanvas.lineStyle(borderThickness, borderColor, alpha);
			//
			var endX:Number = roundOff(xcenter+i*dx*Math.cos(endAng));
			var endY:Number = roundOff(ycenter+i*dx*Math.sin(endAng));
			//
			mcCanvas.lineTo(endX, endY);
		}
	}
	/**
	 * drawLabel method is called to render label for the pie.
	 *
	 *				-- QUADRANT MAPPING ---
	 *							|
	 *						2	|	1
	 *							|
	 *					- - - - - - - - -
	 *							|
	 *						3	|	4
	 *							|
	 */
	private function drawLabel():Void {
		
		//whether we will apply word wrap if needed
		var manageLabelOverflow:Boolean = objData.manageLabelOverflow;

		// storing generic piechart values required  in local variables
		var a:Array = objData.arrFinal;
		var centerX:Number = objData.centerX;
		var centerY:Number = objData.centerY;
		var radius:Number = objData.radius;
		var objTextProp:Object = objData.objLabelProps;
		//--------------------------------------
		// creating and storing reference of a new movieclip to render the label with smartline in it
		var _mc:MovieClip = mcMain.createEmptyMovieClip('mcLabel', mcMain.getNextHighestDepth());
		// storing pie specific values required  in local variables
		var xSend:Number = a['labelProps'][0];
		var ySend:Number = a['labelProps'][1];
		var quadrantId:Number = a['labelProps'][2];
		var isIcon:Boolean = (a['labelProps'][3] == 'icon') ? true : false;
		
		// get the type of label - this will decide the wordwrap/ellipses and auto aligning properties
		var type:String = a['labelProps'][(a['labelProps'].length-1)];
		var wrapSpaceObj:Number = a['labelProps']['wrapSpace'];
		var maxYlimit:Number = a['labelProps']['maxYlimit'];
		
		//---------------------------------------                                     
		// text for display in label
		var txt:String = a['labelText'];
		// textformat object for text field formatting
		var fmtTxt:TextFormat = new TextFormat();
		// properties stored
		fmtTxt.font = objTextProp.font;
		fmtTxt.size = objTextProp.size;
		fmtTxt.color = parseInt(objTextProp.color, 16);
		fmtTxt.bold = objTextProp.bold;
		fmtTxt.italic = objTextProp.italic;
		fmtTxt.underline = objTextProp.underline;
		fmtTxt.letterSpacing = objTextProp.letterSpacing;
		// alignment evaluated
		fmtTxt.align = (quadrantId == 1 || quadrantId == 4) ? "left" : "right";
		// checking for the text field width and height
		var metrics:Object = fmtTxt.getTextExtent(txt);
		var w:Number = metrics.textFieldWidth;
		var h:Number = Math.ceil(metrics.textFieldHeight);
		//----------------------------------------------
		var xAdjust:Number, yAdjust:Number, yLineAdjust:Number, xTxt:Number, yTxt:Number;
		// adjustment value for label ordinate w.r.t. pie-depth
		var lowerYAdjust:Number = 0;
		// setting adjust values w.r.t. quadrant of - coordinates of label and starting ordinate of smartline
		if (quadrantId == 1) {
			xAdjust = 0;
			yAdjust = h;
			yLineAdjust = h/2;
		} else if (quadrantId == 2) {
			xAdjust = -w;
			yAdjust = h;
			yLineAdjust = h/2;
		} else if (quadrantId == 3) {
			xAdjust = -w;
			yAdjust = lowerYAdjust;
			yLineAdjust = lowerYAdjust-h/2;
		} else if (quadrantId == 4) {
			xAdjust = 0;
			yAdjust = lowerYAdjust;
			yLineAdjust = lowerYAdjust-h/2;
		}
		// final coordinates after adjustments                                                                                             
		// multi-pie
		if (objData.totalSlices>1) {
			xTxt = xSend+xAdjust;
			yTxt = ySend+yAdjust;
			// singleton
		} else {
			xTxt = xSend-w/2;
			yTxt = ySend+h/2;
		}
		//-------------------------------------------------
		// storing generic piechart values required  in local variables
		var inDisplacement:Number = 0;
		var a1:Number = radius-inDisplacement;
		var b1:Number = a1;
		var a2:Number = radius;
		var b2:Number = a2;
		// mean angle of the pie in radians
		var ang:Number = MathExt.toRadians(a['meanAngle']);
		var startX:Number, startY:Number;
		//-------------------------------------------------
		// starting coordinates of the smartline w.r.t. quadrants
		if (quadrantId == 1 || quadrantId == 2) {
			startX = toNT(centerX+a1*Math.cos(ang));
			startY = toNT(centerY+b1*Math.sin(ang));
		} else {
			startX = toNT(centerX+a2*Math.cos(ang));
			startY = toNT(centerY+b2*Math.sin(ang));
		}
		// if smart labels are enabled and display of label relevant
		if (objData.isSmartLabels && !isIcon) {
			// adjusting labels for few special cases for proper visual display of smartline with label
			if (xTxt<startX && xTxt+w>startX) {
				var m:Number;
				if (quadrantId == 1 || quadrantId == 4) {
					m = startX-xTxt;
					xTxt += (m+5);
					xSend = xTxt;
				} else {
					m = xTxt+w-startX;
					xTxt -= (m+5);
					xSend = xTxt+w;
				}
			} else if (xTxt+w<startX && (quadrantId == 1 || quadrantId == 4)) {
				xTxt = startX+5;
				xSend = xTxt;
			} else if (xTxt>startX && (quadrantId == 2 || quadrantId == 3)) {
				xTxt = startX-w-5;
				xSend = xTxt+w;
			}
			var midX:Number;
			// to find the vertex abscissa of smartline
			// if smartlines are to be slanted
			if (objData.isSmartLabelSlanted) {
				if (quadrantId == 1 || quadrantId == 4) {
					midX = xSend-5;
				} else {
					midX = xSend+5;
				}
				// else, if they are to be vertical
			} else {
				midX = startX;
			}
			//---------------------------------------------
			// coordinates of the smartline end
			var xLineEnd:Number = xSend;
			var yLineEnd:Number = ySend+yLineAdjust;
			// drawing of the smartline in full and one- shot (if initial animation of smartline is over before)
			if (objData.isInitialised) {
				_mc.lineStyle(objData.smartLineThickness, objData.smartLineColor, objData.smartLineAlpha, true, "normal", "round", "round");
				_mc.moveTo(startX, startY);
				_mc.lineTo(midX, yLineEnd);
				_mc.lineTo(xLineEnd, yLineEnd);
			}
			// storing smartline coordinate values for use during slicing animation of the pie                                                   
			_mc._parent.arrLinePoints = [startX, startY, midX, xLineEnd, yLineEnd];
		} else if (objData.isSmartLabels && isIcon) {
		}
		//-------------------------------------------------                                                                                             
		// if label is to be shown
		if (!isIcon || objData.totalSlices == 0) {
			// textfield created
			_mc.createTextField('label_txt', 11, xTxt, yTxt, w, h);
			// textfield inverted since the whole pie movieclip was inverted initially - a counter-action
			_mc.label_txt._yscale = -100;
			
			_mc.label_txt.text = txt;
			
			// if chart animation is all over beforehand or a singleton case
			if (objData.isInitialised || objData.totalSlices == 0) {
				
				// rendering border of textfield with proper color
				if (objTextProp.borderColor != '') {
					_mc.label_txt.border = true;
					_mc.label_txt.borderColor = parseInt(objTextProp.borderColor, 16);
				}
				// rendering bgColor of textfield with proper color                                                                                             
				if (objTextProp.bgColor != '') {
					_mc.label_txt.background = true;
					_mc.label_txt.backgroundColor = parseInt(objTextProp.bgColor, 16);
				}
				// else, no text display                                                                                             
			} else {
				//If labels  be hidden finally since chart will be initialised with animation
				var hideLabel:Boolean = true;
			}
			
			// check for overflow
			if(manageLabelOverflow){
				//For multi-pie case
				if (objData.totalSlices>1) {
					var isOverflow:Boolean = false;
					var xPos:Number;
					var reqWidth:Number;
					
					//check whether the text overflows the right hand limit
					if( this.canvasXMin + _mc.label_txt._x + _mc.label_txt._width > this.canvasXMax ){
						
						reqWidth = this.canvasXMax - (this.canvasXMin + _mc.label_txt._x);
						_mc.reqWidth = reqWidth;
						//store the X position - no re adjustment
						xPos = _mc.label_txt._x;
						isOverflow = true;
						
					}else if(_mc.label_txt._x < 0){
						
						//check whether the text overlaps the left hand limit
						reqWidth = _mc.label_txt._width + _mc.label_txt._x;
						_mc.reqWidth = reqWidth;
						isOverflow = true;
						xPos = 0;
					}
					
					//the value to revert back if we have to revert back to normal text after applying wordWrap
					var xPosBfrWW:Number = _mc.label_txt._x;
					
					if(isOverflow){
						//get the required height
						var reqHeightExt:Object = fmtTxt.getTextExtent( _mc.label_txt.text, reqWidth);
						//store the current y value
						var yPos:Number = _mc.label_txt._y;
						//store the height for alignment later
						var txtHeight:Number = _mc.label_txt._height;
						var txtWidth:Number =  _mc.label_txt._width;
						var txt:String =  _mc.label_txt.text;
						
						//now go for elipses instead of wordWrap when quadrant is over crowded and the 
						//labels are over flowing
						
						if(type == "type2"){
							_mc.label_txt.text = txt;
							if(objData.useEllipsesWhenOverflow){
								this.addEllipsesBasedOnWidth(_mc.label_txt, reqWidth, fmtTxt);
							}
						}else if(type == "type1" || type == "type3"){
							//delete & create the text field freshly only if word wrap is needed
							_mc.label_txt.removeTextField();
							_mc.createTextField('label_txt', 11, xPos, yPos, reqWidth, reqHeightExt.textFieldHeight);
							_mc.label_txt._yscale = -100;
							_mc.label_txt.autoSize = false;
							_mc.label_txt.wordWrap = true;
							_mc.label_txt.multiline = true;
							_mc.label_txt.text = txt;
							
							//further check whether after wrapping this textfield ovelaps with 
							if(_mc.label_txt._height > wrapSpaceObj && type == "type3" && maxYlimit == 0){
								
								//disable wordwrap
								_mc.label_txt.wordWrap = false;
								//make it single line
								_mc.label_txt.multiline = false;
								// go for elipses
								if(objData.useEllipsesWhenOverflow){
									this.addEllipsesBasedOnWidth(_mc.label_txt, reqWidth, fmtTxt);
								}else{
									//Revert back to initial text position and width
									_mc.label_txt._x = xPosBfrWW;
									_mc.label_txt.autoSize = true;
								}
								
							}else if(maxYlimit != 0 && type == "type3"){
								
								var p1:Object = {x:_mc.label_txt._x, y:_mc.label_txt._y};
								_mc.localToGlobal(p1);
								
								if((p1.y + _mc.label_txt._height) > maxYlimit){
									//disable wordwrap
									_mc.label_txt.wordWrap = false;
									//make it single line
									_mc.label_txt.multiline = false;
									// go for elipses
									if(objData.useEllipsesWhenOverflow){
										this.addEllipsesBasedOnWidth(_mc.label_txt, reqWidth, fmtTxt);
									}else{
										//Revert back to initial text position and width
										_mc.label_txt._x = xPosBfrWW;
										_mc.label_txt.autoSize = true;
									}
								}
							}
						}
						
						
						/** Now reposition on the base of label type
						 * Possible types could be
						 * Type 1 - Non Smart labels - only wordwrap and re alignment
						 * Type 2 - Smart labels on Overcrowded quadrant - only truncate/ellipses on overflow
						 * Type 3 - Smart labels on spacious quadrant - wordwrap + ellipses 
						 */
						switch(type){
							case "type1":
								//re align - non smart labels
								var diff:Number = Math.ceil(_mc.label_txt._height - txtHeight);
								//as wordWrap increase the height of the text field - move it up
								_mc.label_txt._y += Math.ceil(diff/2);
								break;
							case "type2":
								//now due to autosizing on labels, the quadrant 2 and 3 labels 
								//loses connection with the smart line, so moved
								if(quadrantId == 2 || quadrantId == 3){
									var diff:Number =  Math.ceil(txtWidth - _mc.label_txt._width);
									_mc.label_txt._x += diff;
								}
								break;
							case "type3":
								break;
						}
					}
				//For singleton case
				}else{
					var txtWidth:Number =  _mc.label_txt._width;
					if(txtWidth > this.canvasXMax - this.canvasXMin){
						_mc.label_txt._width = this.canvasXMax - this.canvasXMin;
						_mc.label_txt._x = 0;
						_mc.label_txt.wordWrap = true;
						_mc.label_txt.autoSize = true;
					}
				}
			}
			
			//To hide label now, for initial animation is due
			if(hideLabel){
				//Store the label text for use during initial animation
				mcMain.labelTxt = _mc.label_txt.text;
				_mc.label_txt.text = '';
				_mc.label_txt.border = false;
				_mc.label_txt.autoSize = true;
			}
			
			
			// selection disabled
			_mc.label_txt.selectable = false;
			// filter effect applied to text field if chart animation is already over
			if (objData.isInitialised) {
				chartClass.styleM.applyFilters(_mc.label_txt, chartClass.objects.DATALABELS);
			}
			
			//For singleton case, let long text wrap centrally
			if (objData.totalSlices == 0) {
				var align:String = fmtTxt.align;
				fmtTxt.align = 'center';
			}
			
			// text field formatted with the stored properties                                              
			_mc.label_txt.setTextFormat(fmtTxt);
			
			if (objData.totalSlices == 0) {
				fmtTxt.align = align;
			}
		}
	}
	/**
	 * movePie method is referenced to onRelease event of
	 * the pie movieclip.It calculates and calls repositionPie 
	 * method at regular intervals for the pie movement.
	 */
	public function movePie():Void {
		var _mc:MovieClip = mcMain;
		
		if(!_mc.isMoving){
			_mc.isMoving = true;
			this.dispatchEvent({target:this, type:'slicing', status:'start'});
		}
		
		// to prevent an infinite loop of movePie() calls
		if (!arguments[0]) {
			_mc.pie2dTwinRef.movePie(true);
		}
		// checking if the pie is currently sliced-in or sliced-out                                       
		if (_mc.isSlicedIn == true) {
			// will slice-out and hence set false
			_mc.isSlicedIn = false;
		}
		//----------------------------------------                      
		var m:Number = objData.movement;
		// initialisation
		if (_mc.tracker == undefined) {
			_mc.tracker = 20;
		}
		// value updated for response in this click                                                                                                          
		_mc.tracker = 20-_mc.tracker;
		// mean angle of this pie is converted to radians
		var meanAng:Number = MathExt.toRadians(_mc.store['meanAngle']);
		// increments in x and y scale movement is set
		var dx:Number = toNT(m*Math.cos(meanAng)/20);
		var dy:Number = toNT(m*Math.sin(meanAng)/20);
		// x and y increments are given sign for slicing in and slicing out of pie 
		dx = (_mc.isMoved) ? -dx : dx;
		// an extra negetive sign is added because the pie movieclips are inverted
		dy = -((_mc.isMoved) ? -dy : dy);
		
		if(!_mc.mcAnimCTRL){
			_mc.createEmptyMovieClip('mcAnimCTRL', _mc.getNextHighestDepth());
		} else {
			delete _mc.mcAnimCTRL.onEnterFrame;
		}
		
		var thisRef = this;
		_mc.mcAnimCTRL.onEnterFrame = function(){
			thisRef.repositionPie(_mc, dx, dy);
		}
		
		// updating movement status of the pie (moving in or out)
		_mc.isMoved = (_mc.isMoved) ? false : true;
	}
	/**
	 * repositionPie method called repeatedly at regular intervals
	 * from the movePie method. It shifts the pie in small 
	 * units.
	 * @param	_mc		Reference of the movieclip to control
	 * @param	dx		Numeric value increment in x-position
	 * @param	dy		Numeric value increment in y-position
	 */
	private function repositionPie(_mc:MovieClip, dx:Number, dy:Number):Void {
		// if pie movement is not complete                          
		if (_mc.tracker<20) {
			_mc.tracker++;
			//
			if (objData.isSmartLabels) {
				var objPointTxt:Object = new Object();
				objPointTxt.x = _mc.mcLabel.label_txt._x;
				objPointTxt.y = _mc.mcLabel.label_txt._y;
				_mc.localToGlobal(objPointTxt);
				//
				var objPointLine:Object = new Object();
				objPointLine.x = _mc.arrLinePoints[3];
				objPointLine.y = _mc.arrLinePoints[4];
				_mc.localToGlobal(objPointLine);
			}
			//                                                                                                                        
			// repositioning the pie movieclip
			if (_mc.isSlicedIn == false && !_mc.isMoved && _mc.tracker == 20) {
				_mc._x = 0;
				_mc._y = objData.chartHeight;
			} else {
				_mc._x += dx;
				_mc._y += dy;
			}
			//                        
			if (objData.isSmartLabels) {
				_mc.globalToLocal(objPointTxt);
				_mc.globalToLocal(objPointLine);
				//
				var a:Number = 0;
				var b:Number = dx;
				//
				// repositioning the pie labels for no overlapping
				_mc.mcLabel.label_txt._x = objPointTxt.x+b;
				_mc.mcLabel.label_txt._y = objPointTxt.y;
				//-------------------------------------
				// smartline redrawing process w.r.t. smartline coordinates retrived ... to keep the vertical placement of the labels constant
				_mc.mcLabel.clear();
				//smartline coordinates retrived
				var x1 = _mc.arrLinePoints[0];
				var y1 = _mc.arrLinePoints[1];
				var x2 = _mc.arrLinePoints[2]-a;
				var x3 = objPointLine.x+b;
				var y3 = objPointLine.y;
				// smartline drawn if initial chart animation is over earlier
				if (objData.isInitialised) {
					_mc.mcLabel.lineStyle(objData.smartLineThickness, objData.smartLineColor, objData.smartLineAlpha, true, "normal", "round", "round");
					_mc.mcLabel.moveTo(x1, y1);
					_mc.mcLabel.lineTo(x2, y3);
					_mc.mcLabel.lineTo(x3, y3);
				}
				// smartline coordinates stored for future use                                                                                             
				_mc.arrLinePoints = [x1, y1, x2, x3, y3];
			}
			//-------------------------------------                                                                                                                        
			// if pie movement is complete                                                                                            
		} else {
			// actions to be triggered after the completion of inward movement
			if (_mc.isSlicedIn == false && !_mc.isMoved) {
				// isSlicedIn updated for the clicked pie
				_mc.isSlicedIn = true;
			}
			
			_mc.isMoving = false;
			
			if (!objData.isInitialised) {
				chartClass.iniTrackerUpdate();
			}
			
			delete _mc.mcAnimCTRL.onEnterFrame;
			
			this.dispatchEvent({target:this, type:'slicing', status:'end'});
		}
	}
	
	/**
	* This function takes on a textFeild and a target width and tries to assign 
	* maximum text to it.And add elipses if the text overflows.At present this is 
	* done on a single line text field.Multiline text field need further works
	* @param	tf				The textfield to add ellipses to
	* @param	newWidth		New width of textfield for which ellipses occurs
	* @param	tFormat			The text format object
	*/
	private function addEllipsesBasedOnWidth(tf:TextField, newWidth:Number, tFormat:TextFormat):Void{
		
		var obj:Object = tFormat.getTextExtent(tf.text);
		
		tf.autoSize = false;
		tf._width = newWidth;
		tf._height = obj.textFieldHeight;
		
		tf.wordWrap = true;
		tf.multiline = true;
		
		//----------------------------------//
		
		var cutoffPercent:Number = 100;
		//Plain text (no HTML). 
		var plainText:String = tf.text;
		var plainTextLength:Number = plainText.length;
		//Now, run simulation to see what size can fit the text perfectly
		//along with ellipses. Iterate from cutOffPercent downwards to see
		//what text can fit without scroll, along with ellipses.
		
		var i:Number, found:Boolean = false;
		
		for (i=cutoffPercent; i>1; i=((cutoffPercent>19) ? (i-5) : (i/3))) {
			//Calculate the number of characters that we can show
			var viewTextNum:Number = int(plainTextLength*(i/100))-3;
			//If viewTextNum goes negative, exit - as even the minimum
			//length cannot be shown then.
			if (viewTextNum<=0) {
				break;
			}
			//Set the text along with ellipses  
			tf.text = plainText.slice(0, viewTextNum)+"...";
			//Apply the text format again.
			tf.setTextFormat(tFormat);
			//If no scroll was required, this is what is needed.
			if (tf.scroll>=tf.maxscroll) {
				//Set flag that we found it
				found = true;
				break;
			}
		}
		//If a solution was not found, just set first character and ellipses
		if (found == false) {
			//So just set the first character and ellipses
			tf.text = plainText.slice(0, 1)+"...";
			//Apply the text format again.
			tf.setTextFormat(tFormat);
		}
		
	}
}
