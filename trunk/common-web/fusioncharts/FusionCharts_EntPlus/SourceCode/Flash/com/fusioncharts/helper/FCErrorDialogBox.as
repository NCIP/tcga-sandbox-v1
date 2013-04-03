/**
 * @class FCErrorDialogBox
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * DialogBox class helps to generate an error DialogBox 
 * based on the parameters specified
*/
//FCButton
import com.fusioncharts.helper.FCButton;
//Math Extension
import com.fusioncharts.extensions.MathExt;
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.helper.Utils;
//Drop Shadow filter
import flash.filters.DropShadowFilter;
//Delegate
import mx.utils.Delegate;
class com.fusioncharts.helper.FCErrorDialogBox {
	//Instance variables
	private var parent:MovieClip;
	private var depth:Number;
	private var sX:Number;
	private var sY:Number;
	private var sWidth:Number;
	private var sHeight:Number;
	private var h:Number;
	private var w:Number;
	private var btnX:Number;
	private var btnY:Number;
	private var cornerRadius:Number;
	private var borderColor:String;
	private var borderThickness:Number;
	private var bgColor:String;
	private var yPadding:Number;
	private var fontProps:Object;
	private var dropShadow:Boolean;
	private var errorMessage:String;
	//MovieClip of the dialog Box
	private var db:MovieClip;
	//Movie clip for the button
	private var btnOk:FCButton;
	//Container
	private var dbCont:MovieClip;
	//Create a listener for the button
	private var btnListener:Object;
	
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
	function FCErrorDialogBox(target:MovieClip, depth:Number, sX:Number, sY:Number, sWidth:Number, sHeight:Number, yPadding:Number) {
		//Store the class reference
		var classRef:Object = this;
		//Store parameters in instance variables
		this.parent = target;		
		this.sX = sX;
		this.sY = sY;
		this.sWidth = sWidth;
		this.sHeight = sHeight;
		this.yPadding = yPadding;
		//Create the required movieclip for the dialog box
		this.dbCont = this.parent.createEmptyMovieClip("DialogBoxContainer", depth);
		//Create a movieclip within the dialog box Container for panel
		this.db = this.dbCont.createEmptyMovieClip("bgPanel", 1);
		//Define a null event to hide the tool tip
		this.db.onRollOver = this.db.onRollOut = function(){
		}
		//Create a movieclip within the dialog box for OK button
		this.btnOk = new FCButton(this.dbCont, 2);
		//Hide the movieClip initially
		this.db._visible = false;
		//Hide the hand cursor
		this.db.useHandCursor = false;
		//Initialize the listener
		this.btnListener = new Object();
		//Register the listener
		btnListener.click = function(){
			classRef.hide();
		}
	}
	/**
	 * setParams method sets the cosmetic and functional parameters 
	 * for the dialog Box.	 
	 *	@param	height		Sets the height of the rectangle
	 *	@param	width		Sets the width of the rectangle
	 *	@param	cornerRadius	Sets the corner radius of the rounded rectangle.
	 *	@param	bgColor		Background Color
	 *	@param	borderColor	Border Color of the Dialog Box
	 *	@param	fontProps	Object that holds the font properties for the error message
	 *	@param	dropShadow	Whether to drop shadow for the dialog box.
	*/
	public function setParams(height:Number, width:Number, cornerRadius:Number, bgColor:String, borderColor:String, borderThickness:Number, fontProps:Object, dropShadow:Boolean) {
		//Store parameters in instance variables.
		this.h = height;
		this.w = width;
		this.cornerRadius = cornerRadius;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
		this.dropShadow = dropShadow;
		this.fontProps = fontProps;
		this.errorMessage = "Error";
		//Set the initial position of the button based on the width and height of the dialog box
		var btnW:Number = 80;
		var btnH:Number = 20;
		this.btnX = width/2-btnW/2;
		this.btnY = (4/5*height)-btnH/2;
		//Set the default values for the button
		this.btnOk.setParams("OK", this.btnX, this.btnY, btnH, btnW,5,"FFFFFF","CCCCCC","Verdana", "000000", 10);
		//Register the event with the button
		this.btnOk.addEventListener("click", this.btnListener);
		//Create filters and apply
		if (dropShadow) {
			var shadowFilter:DropShadowFilter = new DropShadowFilter(3, 45, 0x666666, 0.8, 4, 4, 1, 1, false, false, false);
			this.db.filters = [shadowFilter];
		}
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
	 * draw method draw the initial Dialog Box
	*/
	public function draw():Void {
		//Set the border color if required
		if (this.borderColor != "" && this.borderColor != undefined && this.borderColor != null) {
			this.db.lineStyle(this.borderThickness, parseInt(this.borderColor, 16), 100);
		}
		//Set the background color if required                                        
		if (this.bgColor != "" && this.bgColor != undefined && this.bgColor != null) {
			this.db.beginFill(parseInt(this.bgColor, 16), 100);
		}
		this.drawRoundedRect(this.db, 0, 0, this.w, this.h, this.cornerRadius);
		//End Fill
		this.db.endFill();
		//Set the position with respect to the parent class
		this.db._x = this.sX+this.sWidth/2-this.w/2;
		this.db._y = this.sY+this.sHeight/2-this.h/2;
		//Show the error message
		this.showErrorMessage();
		//Now create an OK button
		//Set the position with respect to the parent class
		this.btnOk.setPosition(this.sX+this.sWidth/2-this.w/2+this.btnX, this.sY+this.sHeight/2-this.h/2+this.btnY);
		//Draw the mouse and set the visibility as true
		this.btnOk.draw();
		this.show();
	}
	/**
	 *  setErrorMessage sets the error message to display
	 *	@param		eMessage	message to display
	*/
	public function setErrorMessage(eMessage:String):Void {
		this.errorMessage = eMessage;
		this.draw();
	}
	/**
	 *  showErrorMessage sets the error message to display
	 *	@param		eMessage	message to display
	*/
	public function showErrorMessage():Void {
		//Set the text for the error message
		var styleObj:Object =  new Object();
		styleObj.bold = false;
		styleObj.align = "center";		
		styleObj.vAlign = "center";
		styleObj.italic = false;
		styleObj.underline = false;
		styleObj.font = this.fontProps.font;
		styleObj.size = this.fontProps.fontSize;
		styleObj.color = this.fontProps.fontColor;
		styleObj.isHTML = true;
		styleObj.leftMargin = 0;
		styleObj.letterSpacing= 0;
		styleObj.bgColor = "";
		styleObj.borderColor = "";
		var messageObj:Object = Utils.createText(false, this.errorMessage, this.db, 1, this.w/2, 10, 0, styleObj, true, this.w-50, this.h/2);
	}
	/**
	  * show method shows the dialog box.
	*/
	public function show():Void {
		//Show the dialog box
		this.db._visible = true;
		this.btnOk.show();
	}
	/**
	 * hide method hides the dialog box.
	*/
	public function hide():Void {
		//Hide the dialog box
		this.db._visible = false;
		this.btnOk.hide();
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//Delete the movieclip
		this.db.removeMovieClip();
		this.btnOk.destroy();
		this.btnOk.removeEventListener("click", this.btnListener);
	}
}
