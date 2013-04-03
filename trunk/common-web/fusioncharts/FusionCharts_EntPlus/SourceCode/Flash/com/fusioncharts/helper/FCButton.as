/**
 * @class FCButton
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * Button generates a configurable button
 * based on the parameters specified
*/
//Math Extension
import com.fusioncharts.extensions.MathExt;
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.helper.Utils;
//Delegate
import mx.utils.Delegate;
import mx.events.EventDispatcher;
class com.fusioncharts.helper.FCButton {
	//Instance variables
	private var parent:MovieClip;
	private var depth:Number;
	//Parameters for the button
	private var btnOk:MovieClip;
	private var btnH:Number;
	private var btnW:Number;
	private var btnX:Number;
	private var btnY:Number;
	private var btnLabel:String;
	private var btnBgColor:String;
	private var btnBorderColor:String;
	private var btnCornerRadius:Number;
	private var btnFont:String;
	private var btnFontColor:String;
	private var btnFontSize:Number;
	//Constructor function
	/**
	 * Here, we initialize the dialog Box objects.
	 *	@param		target	Movie clip in which we've to create the
	 *						dialog box.
	 *	@param		depth	Depth at which we create dialog box.
	 *	@param		sX		Top X of the stage.
	 *	@param		sY		Top Y of the stage.
	 *	@param		sWidth	Width of the stage.
	 *	@param		sHeight	Height of the stage.
	 *	@param		yPadding	y-axis padding (pixels)
	*/
	function FCButton(target:MovieClip, depth:Number) {
		//Store parameters in instance variables
		this.parent = target;		
		this.depth = depth;
		//Create the required movieclip for the dialog box
		this.btnOk = this.parent.createEmptyMovieClip("Button_"+depth, depth);
		//Set the default values for the button
		this.btnLabel = "OK";
		this.btnBgColor = "FFFFFF";
		this.btnBorderColor = "CCCCCC";
		this.btnH = 20;
		this.btnW = 80;
		this.btnCornerRadius = 5;
		this.btnFont = "Verdana";
		this.btnFontColor = "000000";
		this.btnFontSize = 10;
		//call initialize on EventDispatcher to
		//implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
	}
	/**
	 * setParams method sets the cosmetic and functional parameters 
	 * for the Button.	 
	 *	@param	label		Sets the button label
	 *	@param	x			Sets the X Position of the button
	 *	@param	y		 	Sets the Y Position of the button
	 *	@param	height	 	Sets the height of the button
	 *	@param	width	 	Sets the width of the button
	 *	@param	bgColor		Background Color
	 *	@param	borderColor	Border Color of the tool tip
	 *	@param	font		Sets the font face for the button label
	 *	@param	fontColor	Sets the font color for the button label
	 *	@param	fontSize	Sets the font size for the button label
	*/
	public function setParams(label:String, x:Number, y:Number, height:Number, width:Number, cornerRadius:Number, bgColor:String, borderColor:String, font:String, fontColor:String, fontSize:Number) {
		//Store parameters in instance variables.
		this.btnX = x;
		this.btnY = y;
		this.btnH = height;
		this.btnW = width;
		this.btnLabel = label;
		this.btnCornerRadius = cornerRadius;
		this.btnBgColor = bgColor;
		this.btnBorderColor = borderColor;
		this.btnFont = font;
		this.btnFontColor = fontColor;
		this.btnFontSize = fontSize;
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
	 * drawRoundedRect method helps draw a rounded Rectangle based on the parameters specified.
	 *	Assumptions: Movie clip is already created and line/fill style has
	 *				 already been set before this method is called to
	 *				 draw a rounded rectangle.
	 *	@param		mc		Movie clip in which we've to draw the rounded rectangle
	 *	@param		x		X position of the rectangle
	 *	@param		y		Y position of the rectangle
	 *	@param		w		w width of the rectangle
	 *	@param		h		height of the rectangle
	 *	@param		cornerRadius	corner radius of the rectangle to show the roundness.
	 */
	public function drawRoundedRect(mc:MovieClip, x:Number, y:Number, w:Number, h:Number, cornerRadius:Number):Void {
		//We first see if the user has defined a corner radius for us. If yes, we need to create curves and calculate
		if (cornerRadius>0) {
			//Initialize variables to be usd
			var theta:Number, angle:Number, cx:Number, cy:Number, px:Number, py:Number;
			//Make sure that (w,h) is larger than 2*cornerRadius
			if (cornerRadius>Math.min(w, h)/2) {
				cornerRadius = Math.min(w, h)/2;
			}
			// theta = 45 degrees in radians 
			theta = Math.PI/4;
			// draw top line
			mc.moveTo(x+cornerRadius, y);
			mc.lineTo(x+w-cornerRadius, y);
			//angle is currently 90 degrees
			angle = -Math.PI/2;
			// draw tr corner in two parts
			cx = Math.round(x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			cy = Math.round(y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			px = Math.round(x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius));
			py = Math.round(y+cornerRadius+(Math.sin(angle+theta)*cornerRadius));
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = Math.round(x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			cy = Math.round(y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			px = Math.round(x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius));
			py = Math.round(y+cornerRadius+(Math.sin(angle+theta)*cornerRadius));
			mc.curveTo(cx, cy, px, py);
			// draw right line
			mc.lineTo(x+w, y+h-cornerRadius);
			// draw br corner
			angle += theta;
			cx = x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			// draw bottom line
			mc.lineTo(x+cornerRadius, y+h);
			// draw bl corner
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			// draw left line
			mc.lineTo(x, y+cornerRadius);
			// draw tl corner
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
		} else {
			//No corner radius defined - so draw a simple rectangle
			mc.moveTo(x, y);
			mc.lineTo(x+w, y);
			mc.lineTo(x+w, y+h);
			mc.lineTo(x, y+h);
			mc.lineTo(x, y);
		}
	}
	/**
	 * setPosition method Sets the new position for the button class variables
	*/
	public function setPosition(x:Number, y:Number):Void {
		this.btnX = x;
		this.btnY = y;
	}
	/**
	 * draw method draw the initial Dialog Box
	*/
	public function draw():Void {
		//Now create an OK button
		//Set the border color if required
		if (this.btnBorderColor != "" && this.btnBorderColor != undefined && this.btnBorderColor != null) {
			this.btnOk.lineStyle(2, parseInt(this.btnBorderColor, 16), 100);
		}
		//Set the background color if required                                        
		if (this.btnBgColor != "" && this.btnBgColor != undefined && this.btnBgColor != null) {
			this.btnOk.beginFill(parseInt(this.btnBgColor, 16), 100);
		}
		this.drawRoundedRect(this.btnOk, 0, 0, this.btnW, this.btnH, this.btnCornerRadius);
		//End Fill
		this.btnOk.endFill();
		//Set the position with respect to the parent class
		this.btnOk._x = this.btnX;
		this.btnOk._y = this.btnY;
		//Set the text of the button
		var btnStyleObj:Object =  new Object();
		btnStyleObj.bold = true;
		btnStyleObj.align = "center";		
		btnStyleObj.vAlign = "center";
		btnStyleObj.italic = false;
		btnStyleObj.underline = false;
		btnStyleObj.font = this.btnFont;
		btnStyleObj.size = this.btnFontSize;
		btnStyleObj.color = this.btnFontColor;
		btnStyleObj.isHTML = false;
		btnStyleObj.leftMargin = 0;
		btnStyleObj.letterSpacing= 0;
		btnStyleObj.bgColor = "";
		btnStyleObj.borderColor = "";
		var captionObj:Object = Utils.createText(false, this.btnLabel, this.btnOk, 1, this.btnW/2, 1, 0, btnStyleObj, false, 0, 0);
		//Define the onPress Event for the OK button
		this.btnOk.onPress = function() {
			//To show the effect of button press
			this._x +=1;
			this._y +=1;
		}
		//Define the onReleaseOutside Event for the OK button
		this.btnOk.onReleaseOutside = function() {
			//To unset the effect of button press
			this._x -=1;
			this._y -=1;
		}
		//Define the onRelease Event for the OK button
		this.btnOk.onRelease = Delegate.create(this, onBtnRelease);
	}
	/**
	 *  setLabel sets the Label of the button
	 *	@param		label	label to display in button
	*/
	public function setLabel(label:String):Void {
		this.btnLabel = label;
	}
	/**
	  * onBtnRelease method handles the release of the ok button
	*/
	public function onBtnRelease():Void {
		//To show the effect of button release
		this.btnOk._x--;
		this.btnOk._y--;
		this.dispatchEvent({type:"click", target:this});
	}
	/**
	  * show method display the button.
	*/
	public function show():Void {
		//Show the Button
		this.btnOk._visible = true;
	}
	/**
	 * hide method hides the button.
	*/
	public function hide():Void {
		//Hide the button
		this.btnOk._visible = false;
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//Delete the movieclip
		this.btnOk.removeMovieClip();
	}
}
