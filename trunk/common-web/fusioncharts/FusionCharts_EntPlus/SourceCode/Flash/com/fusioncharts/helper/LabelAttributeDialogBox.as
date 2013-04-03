/**
 * @class LabelAttributeDialogBox
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 *
 * LabelAttributeDialogBox class helps to accept attributes for creating
 * dialog Box to accept attributes for new label addition.
 */
//FCButton
import com.fusioncharts.helper.FCButton;
import com.fusioncharts.helper.Utils;
import com.fusioncharts.helper.ColorPicker;
//Drop Shadow filter
import flash.filters.DropShadowFilter;
//Combo box
import mx.controls.ComboBox;
//Check Box
import mx.controls.CheckBox;
//Text Input
import mx.controls.TextInput;
//Numeric Stepper
import mx.controls.NumericStepper;
//Delegate
import mx.utils.Delegate;
import mx.events.EventDispatcher;
//Class definition
class com.fusioncharts.helper.LabelAttributeDialogBox {
	//Instance variables
	private var parent:MovieClip;
	private var depth:Number;
	private var sX:Number;
	private var sY:Number;
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
	//MovieClip of the dialog Box
	private var db:MovieClip;
	//Movie clip for the button
	private var btnOk:FCButton;
	//Movie clip for the Cancel button
	private var btnCancel:FCButton;
	private var btnCancelX:Number;
	private var btnCancelY:Number;
	//Update information
	private var dsSeriesName:String;
	//private var nodeShape:String;
	private var seriesNameArr:Array;
	private var idArr:Array;
	//Container
	private var dbCont:MovieClip;
	//Create a listener for the button
	private var btnListener:Object;
	private var btnCancelListener:Object;
	private var errorTf:TextField;
	private var errorInvalidTf:TextField;
	private var label_Tf:TextInput;
	private var padding_Tf:TextInput;
	private var font_cb:ComboBox;
	private var fontSizeTf:NumericStepper;
	private var alpha_ns:NumericStepper;
	private var allowDrag_ch:CheckBox;
	
	private var labelBgColor:String;
	private var labelBorderColor:String;
	private var labelFontColor:String;
	private var isEditLabel:Boolean;	
	private var labels:Array;
	private var dataSet:Array;
	private var dsIndex:Number;
	private var index:Number;
	private var bgCP:ColorPicker;
	private var borderCP:ColorPicker;
	private var fontCP:ColorPicker;
	private var fontDefaultColor:Number;
	
	//Constructor function
	/**
	 * Here, we initialize the dialog Box objects.
	 *	@param		target			Movie clip in which we've to create the
	 *								dialog box.
	 *	@param		depth			Depth at which we create dialog box.
	 *	@param		sX				Top X of the stage.
	 *	@param		sY				Top Y of the stage.
	 *	@param		yPadding		y-axis padding (pixels)
	 *	@param		labels			label 
	 *	@param		isEditLabel		isEditLabel
	 *	@param		index			index
	 *	@param		fontDefaultColor	DefaultColor of font
	 */
	function LabelAttributeDialogBox(target:MovieClip, depth:Number, sX:Number, sY:Number, yPadding:Number, labels:Array, isEditLabel:Boolean, index:Number, fontDefaultColor:Number)
	{
		//Store parameters in instance variables
		this.parent = target;		
		this.sX = sX;
		this.sY = sY;
		this.yPadding = yPadding;
		this.seriesNameArr = seriesNameArr;
		this.idArr = idArr;
		this.fontDefaultColor = fontDefaultColor;
		//Create the required movieclip for the dialog box
		this.dbCont = this.parent.createEmptyMovieClip("DialogBoxContainer", depth);
		//Create a movieclip within the dialog box Container for panel
		this.db = this.dbCont.createEmptyMovieClip("bgPanel", 1);
		//Create a movieclip within the dialog box for OK button
		this.btnOk = new FCButton(this.db, 0);
		//Create a movieclip within the dialog box for Cancel button
		this.btnCancel = new FCButton(this.db, 1);
		//Hide the movieClip initially
		this.db._visible = false;
		//Hide the hand cursor
		this.db.useHandCursor = false;
		//Initialize the listener
		this.btnListener = new Object();
		this.btnCancelListener = new Object();
		//call initialize on EventDispatcher to
		//implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
		this.labelBgColor="66FFFF";
		this.labelBorderColor="000000";
		this.labelFontColor="000000";
		this.isEditLabel=isEditLabel;
		this.labels=labels;
		this.dsIndex=dsIndex;
		this.index=index;		
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
	 * setParams method sets the cosmetic and functional parameters 
	 * for the dialog Box.	 
	 *	@param	height				Sets the height of the rectangle
	 *	@param	width				Sets the width of the rectangle
	 *	@param	cornerRadius		Sets the corner radius of the rounded rectangle.
	 *	@param	bgColor				Background Color
	 *	@param	borderColor			Border Color of the tool tip
	 *	@param	borderThickness		Border thickness
	 *	@param	fontProps			Object that holds the font properties for the error message
	 *	@param	dropShadow			Whether to drop shadow for the tool tip.
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
		//Set the initial position of the button based on the width and height of the dialog box
		var btnW:Number = 80;
		var btnH:Number = 20;
		this.btnX = width/2-btnW/2-5;
		this.btnY = (4/5*height)-5;
		this.btnCancelX = this.btnX+btnW+10;
		this.btnCancelY = (4/5*height)-5;
		//Set the default values for the button
		this.btnOk.setParams("Submit", this.btnX, this.btnY, btnH, btnW,5,"FFFFFF","CCCCCC","Verdana", "000000", 10);
		//Register the event with the button
		this.btnOk.addEventListener("click", this.btnListener);
		//Register the listener
		btnListener.click = Delegate.create( this, onClick);
		//Set the default values for the Cancel button
		this.btnCancel.setParams("Cancel", this.btnCancelX, this.btnCancelY, btnH, btnW, 5,"FFFFFF","CCCCCC","Verdana", "000000", 10);
		//Register the event with the button
		this.btnCancel.addEventListener("click", this.btnCancelListener);
		//Register the listener
		btnCancelListener.click = Delegate.create( this, onCancelClick);
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
		this.db._x = this.sX-this.w/2;
		this.db._y = this.sY-this.h/2;
		this.createPanel();
		//Now create an OK button
		//Set the position with respect to the parent class
		this.btnOk.setPosition(65, 209);
		//Draw the mouse and set the visibility as true
		this.btnOk.draw();
		this.btnOk.show();
		//Now create a Cancel button
		//Set the position with respect to the parent class
		this.btnCancel.setPosition(155, 209);
		//Draw the mouse and set the visibility as true
		this.btnCancel.draw();
		this.btnCancel.show();
	}
	/**
	 *  createPanel method sets the panel with textfield and buttons
	 */
	public function createPanel():Void {
		//Loop Variable
		var i:Number;		
		//Set the text for the error message
		var styleObj:Object =  new Object();
		styleObj.bold = false;
		styleObj.align = "left";		
		styleObj.vAlign = "center";
		styleObj.italic = false;
		styleObj.underline = false;
		styleObj.font = this.fontProps.font;
		styleObj.size = this.fontProps.fontSize;
		styleObj.color = "FF0000";
		styleObj.isHTML = true;
		styleObj.leftMargin = 0;
		styleObj.letterSpacing= 0;
		styleObj.bgColor = "";
		styleObj.borderColor = "";
		//Initial position within the DB
		var startX:Number = 20;
		var startY:Number = 20;
		//Starting depth for all the components in the DB
		var startDepth:Number = 2;
		//Set the default color style
		styleObj.color = this.fontProps.fontColor;
		//Lablel text
		Utils.createText(false, "Label", this.db, startDepth++, startX, startY+10, 0, styleObj);
		//label text
		this.label_Tf = this.db.createClassObject(mx.controls.TextInput, "label_1_ti", startDepth++);
		this.label_Tf._x = startX+75;
		this.label_Tf._y = startY+5;
		this.label_Tf.setSize(180, this.label_Tf._height);
		this.label_Tf.tabIndex=1;
		
		//Label alpha
		Utils.createText(false, "Alpha", this.db, startDepth++, startX, startY+42, 0, styleObj);
		//NumericStepper
		this.alpha_ns = this.db.createClassObject(mx.controls.NumericStepper, "labelAlpha_ns", startDepth++);
		this.alpha_ns._x = startX+75;
		this.alpha_ns._y = startY+40;
		this.alpha_ns.maximum = 100;
		this.alpha_ns.minimum = 0;
		this.alpha_ns.setSize(55, 20);
		this.alpha_ns.stepSize = 1;
		this.alpha_ns.value = 100;
		this.alpha_ns.tabIndex = 2;
		
		//Label font size
		Utils.createText(false, "Size", this.db, startDepth++, startX+164, startY+42, 0, styleObj);
		//Numeric Stepper
		this.fontSizeTf = this.db.createClassObject(mx.controls.NumericStepper, "fontSize_ti", startDepth++);
		this.fontSizeTf._x = startX+205;
		this.fontSizeTf._y = startY+40;
		this.fontSizeTf.maximum = 32;
		this.fontSizeTf.minimum = 10;
		this.fontSizeTf.setSize(50, 20);
		this.fontSizeTf.stepSize = 2;
		this.fontSizeTf.value = 10;
		this.fontSizeTf.tabIndex=3;
		
		Utils.createText(false, "Background", this.db, startDepth++, startX, startY+77, 0, styleObj);
		Utils.createText(false, "Font Color", this.db, startDepth++, startX, startY+112, 0, styleObj);
		Utils.createText(false, "Border Color", this.db, startDepth++, startX, startY+147, 0, styleObj);
		
		Utils.createText(false, "Padding", this.db, startDepth++, startX+169, startY+77, 0, styleObj);
		//label text
		this.padding_Tf = this.db.createClassObject(mx.controls.TextInput, "padding_1_ti", startDepth++);
		this.padding_Tf.restrict = "0-9";
		this.padding_Tf.text = '5';
		this.padding_Tf._x = startX+226;
		this.padding_Tf._y = startY+77;
		this.padding_Tf.setSize(30, this.padding_Tf._height);
		this.padding_Tf.tabIndex=1;
		
		//Draw a checkBox for accepting allowDrag parameters
		this.allowDrag_ch = createCheckBox("allowDrag_ch", startDepth++, {label:'  Allow Drag', selected:true}, startX+175, startY+110)
		
		borderCP = new ColorPicker(this.db, startDepth++);
		borderCP.transparency = true;
		borderCP.x = startX+75;
		borderCP.y = startY+145;
		borderCP.color = -1;
		
		fontCP = new ColorPicker(this.db, startDepth++);
		fontCP.x = startX+75;
		fontCP.y = startY+110;
		fontCP.color = parseInt(('0x' + fontDefaultColor), 16);
		
		bgCP = new ColorPicker(this.db, startDepth++);
		bgCP.transparency = true;
		bgCP.x = startX+75;
		bgCP.y = startY+75;
		bgCP.color = -1;
		//Reset the font to red error massage
		styleObj.color = "FF0000";
		this.errorInvalidTf = Utils.createText(false, "Label cannot be blank.", this.db, startDepth++, startX, 189, 0, styleObj).tf;
		//Make it hidden initially
		this.errorInvalidTf._visible = false;
	}
	/**
	 * createCheckBox method creates a check box item.
	 * @param	chkBoxName	chkBox Name
	 * @param	depth		level depth
	 * @param	labelObj	label Object
	 * @param	xPos		x position
	 * @param	yPos		y position
	 */
	 private function createCheckBox(chkBoxName:String, depth:Number, labelObj:Object, xPos:Number, yPos:Number):CheckBox{
		var newChkBox:CheckBox = this.db.createClassObject(mx.controls.CheckBox, chkBoxName, depth, labelObj);
		newChkBox._x = xPos;
		newChkBox._y = yPos;
		 
		return newChkBox;
	 }
	/**
	 * onClick method Handles the event to process once the Ok Button is pressed.
	 */
	private function onClick():Void {
		if(this.label_Tf.text == "") {
			//Make the error TF visible
			this.errorInvalidTf._visible = true;
		}else{
			//Dispatch information to parent class to update the required arrays(Connectors, data )
			this.dispatchEvent({type:"onLabelUpdate", target:this, index:this.index, text:this.label_Tf.text, color:(fontCP.color).toString(16), alpha:this.alpha_ns.value, fontSize:this.fontSizeTf.value, bgColor:(bgCP.color).toString(16), borderColor:(borderCP.color).toString(16), padding:(this.padding_Tf.text == '')? 5 : Number(this.padding_Tf.text), allowDrag:this.allowDrag_ch.selected});
			//Hide the dialog box
			this.destroy();
		}
	}
	/**
	 * onCancelClick method Handles the event to process once the Cancel Button is pressed.
	 */
	private function onCancelClick():Void {
		//Dispatch information to parent class to update the required arrays(Connectors, data )
		this.dispatchEvent({type:"onCancel", target:this});
		//delete the dialog box
		this.destroy();
	}
	/**
	 * show method shows the Dialog Box.
	 */
	public function show():Void {
		//Show the dialog box
		this.db._visible = true;
	}
	/**
	 * hide method hides the tool tip.
	*/
	public function hide():Void {
		//Hide the dialog box
		this.db._visible = false;
		this.btnOk.hide();
		this.btnCancel.hide();
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	 */
	public function destroy():Void {
		this.bgCP.destroy();
		this.borderCP.destroy();
		this.fontCP.destroy();
		this.label_Tf.destroyObject();
		this.padding_Tf.destroyObject();
		this.allowDrag_ch.destroyObject();
		//remove textfields
		this.errorInvalidTf.removeTextField();
		this.errorTf.removeTextField();
		//Delete the movieclip
		this.db.removeMovieClip();
		this.btnOk.destroy();
		this.btnOk.removeEventListener("click", this.btnListener);
		this.btnCancel.destroy();
		this.btnCancel.removeEventListener("click", this.btnCancelListener);
	}
}
