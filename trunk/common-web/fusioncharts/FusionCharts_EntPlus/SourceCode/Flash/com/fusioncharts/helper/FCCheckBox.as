/**
 * @class FCCheckBox
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * FCCheckBox generates a configurable CheckBox
 * based on the parameters specified
*/
//Drop Shadow filter
import flash.filters.DropShadowFilter;
//Color Extension
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.extensions.MathExt;
//Utility
import com.fusioncharts.helper.Utils;
//Delegate
import mx.utils.Delegate;
//Event Dispatcher
import mx.events.EventDispatcher;
class com.fusioncharts.helper.FCCheckBox implements com.fusioncharts.helper.IComponent {
	//Instance variables
	//MovieClips
	private var parent:MovieClip;
	private var parentContMC:MovieClip;
	private var cbLabelMC:MovieClip;
	private var cb:MovieClip;
	//Temporary MovieClips for the Simulation Mode of the CreateText
	private var tm:MovieClip;
	private var tempMC:MovieClip;
	//Depth at which component is created
	private var depth:Number;
	//Unique ID for identification
	private var id:Number;
	//Parameters for the checkBox
	private var cbH:Number;
	private var cbW:Number;
	private var cbX:Number;
	private var cbY:Number;
	//Maximum Height and width available
	private var maxH:Number;
	private var maxW:Number;
	private var givenW:Number;
	private var titleW:Number;
	//CheckBox Label
	private var cbLabel:String;
	private var cbBgColor:Number;
	private var cbBorderColor:String;
	private var tmBorderColor:Number;
	//Style Object for CheckBox Label
	private var cbStyleObj:Object;
	//Label Position
	private var labelPosition:String;
	//Constant
	private var cbSize:Number = 13;
	//Constructor function
	/**
	 * Here, we initialize the dialog Box objects.
	 *	@param		target	Movie clip in which we've to create the
	 *						CheckBox
	 *	@param		id		id of the checkBox to identify when checked/unchecked.
	 *	@param		depth	Depth at which we create CheckBox.
	*/
	function FCCheckBox(target:MovieClip, id:Number, depth:Number) {
		//Store parameters in instance variables
		this.parent = target;		
		//Set the HandCursor to false
		this.parent.useHandCursor = false;
		this.id = id;
		//Create a parent Container
		this.parentContMC = this.parent.createEmptyMovieClip("ParentCheckBoxMC", depth);
		//Create the required movieclip for the CheckBox
		this.cb = this.parentContMC.createEmptyMovieClip("CheckBox", this.parentContMC.getNextHighestDepth());
		this.tm = this.parentContMC.createEmptyMovieClip("TickMark", this.parentContMC.getNextHighestDepth());
		this.cbLabelMC = this.parentContMC.createEmptyMovieClip("cbLabel", this.parentContMC.getNextHighestDepth());
		//Create a temp MC for Simulation Mode of the Text
		this.tempMC = this.parentContMC.createEmptyMovieClip("TickMark", this.parentContMC.getNextHighestDepth());
		//call initialize on EventDispatcher to
		//implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
	}
	/**
	 * setParams method sets the cosmetic and functional parameters 
	 * for the CheckBox.	 
	 *	@param	label		Sets the CheckBox label
	 *	@param	x			Sets the X Position of the CheckBox.
	 *	@param	y		 	Sets the Y Position of the CheckBox.
	 *	@param	height	 	Sets the height of the CheckBox.
	 *	@param	width	 	Sets the width of the CheckBox.
	 *	@param	bgColor		Background Color of the CheckBox.
	 *	@param	borderColor	Border Color of the checkbox
	 *	@param	tmBorderColor	tickMarks's border Color
	 *	@param	labelPosition	Sets the label alignment with respect to the checkbox left, right or bottom.
	 *	@param	cbStyleObj	Sets the font Style for the checkbox label.
	*/
	public function setParams(label:String, x:Number, y:Number, height:Number, width:Number, bgColor:String, labelPosition:String, cbStyleObj:Object) {
		//Store parameters in instance variables.
		this.cbX = x;
		this.cbY = y;
		this.cbH = height;
		this.givenW = width;
		this.cbLabel = label;
		this.calcColor(bgColor);
		this.labelPosition = labelPosition;
		this.cbStyleObj = cbStyleObj;
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
	 * setPosition method sets the new position of the checkbox
	*/
	public function setPosition(x:Number, y:Number):Void {
		this.cbX = x;
		this.cbY = y;
	}
	/**
	 * draw method draw the initial CheckBox
	*/
	public function draw():Void {
		//Set the label before drawing the checkBox - to know the maximum Label Width
		this.setLabel(this.cbLabel);
		//Set the height and width fixed as 20 - for visual purpose.
		this.cbW = this.cbSize;
		this.cbH = this.cbSize;
		//Create the TickMark
		this.drawTick();
		//Now create a checkbox
		//Set the background color if required                                        
		if (!isNaN(this.cbBgColor)  && this.cbBgColor != undefined && this.cbBgColor != null) {
			//Create matrix object
			var matrix:Object = {matrixType:"box", w:this.cbW, h:this.cbH, x:0, y:-this.cbH/2, r:MathExt.toRadians(90)};
			this.cb.beginGradientFill("linear", [this.cbBgColor, 0xFFFFFF] , [100,100], [0,255], matrix);
			//beginFill(this.cbBgColor, 100);
		}
		//Draw the rectangle
		this.drawRectangle();
		//End Fill
		this.cb.endFill();
		//Based on the label alignment - set the position
		if(this.labelPosition.toUpperCase() == "LEFT" ) {
			//Set the position with respect to the parent class
			this.cb._x = this.cbX;
			this.cb._y = this.cbY;
		} else if(this.labelPosition.toUpperCase() == "RIGHT" ) {
			//Set the position with respect to the parent class
			this.cb._x = this.cbX;
			this.cb._y = this.cbY;
		} else {
			//Set the position with respect to the parent class
			this.cb._x = this.cbX-this.givenW/2;
			this.cb._y = this.cbY;
		}
		//Define the onPress Event for the CheckBox
		//Store the class reference
		var classRef:Object = this;
		//Define the onPress for the title and the CheckBox MC
		this.cbLabelMC.onPress = this.cb.onPress = function() {
			//Make the tickMark visible or hidden
			classRef.tm._visible = !classRef.tm._visible;
			//Dispatch the event to its parent class
			classRef.dispatchEvent({type:"onCheck", target:classRef, index:classRef.id, state:classRef.getState()});
		}
	}
	/**
	 * drawRectangle method draws the shape rectangle for the CheckBox
	*/
	private function drawRectangle():Void {
		//Set the dark border color if required
		if (this.cbBorderColor != "" && this.cbBorderColor != undefined && this.cbBorderColor != null) {
			this.cb.lineStyle(1, ColorExt.getDarkColor(this.cbBorderColor, 0.5), 100);
		}
		//Move to (-w/2, -h/2); 
		this.cb.moveTo(-this.cbW/2, this.cbH/2);
		//Draw rectangle
		this.cb.lineTo(-this.cbW/2, -this.cbH/2);
		this.cb.lineTo(this.cbW/2, -this.cbH/2);
		//Set the light border color if required
		if (this.cbBorderColor != "" && this.cbBorderColor != undefined && this.cbBorderColor != null) {
			this.cb.lineStyle(1, ColorExt.getDarkColor(this.cbBorderColor, 0.5), 100);
		}
		this.cb.lineTo(this.cbW/2, this.cbH/2);
		this.cb.lineTo(-this.cbW/2, this.cbH/2);
	}
	/**
	 * drawTick method draws the tickMark for the CheckBox
	*/
	private function drawTick():Void {
		//Set the line style of the checkbox
		if (!isNaN(this.tmBorderColor) && this.tmBorderColor != undefined && this.tmBorderColor != null) {
			this.tm.lineStyle(2, this.tmBorderColor, 100, true, "normal", "round", "bevel");
		}
		//Move 
		this.tm.moveTo(-3, 0);
		this.tm.lineTo(0, this.cbH/2-4);
		this.tm.lineTo(this.cbW/2+2, -this.cbH/2-2);
		//Set the position with respect to the parent class
		if(this.labelPosition.toUpperCase() == "LEFT" ) {
			//Set the position with respect to the parent class
			this.tm._x = this.cbX;
			this.tm._y = this.cbY;
		} else if(this.labelPosition.toUpperCase() == "RIGHT" ) {
			//Set the position with respect to the parent class
			this.tm._x = this.cbX;
			this.tm._y = this.cbY;
		} else {
			//Set the position with respect to the parent class
			this.tm._x = this.cbX-this.givenW/2;
			this.tm._y = this.cbY;
		}
		//Apply the filter
		var shadowFilter:DropShadowFilter = new DropShadowFilter(3, 45, 0x666666, 0.4, 4, 4, 1, 1, false, false, false);
		this.tm.filters = [shadowFilter];
		//Make it visible initially
		this.tm._visible = true;
	}
	/**
	 *  calcDimension method calculates the maximum Height and width required
	*/
	public function calcDimension():Void {
		//Simulated Text - X and Y position
		var testTFY:Number = -2000;
		var testTFX:Number = -2000;
		//Set the text of the CheckBox
		//Calculate max Width and Height required for the label
		var titleObj:Object = Utils.createText(true, this.cbLabel, this.tempMC, 1, testTFX, testTFY, 0, this.cbStyleObj, false, 0, 0);		
		titleObj.tf.removeTextField();
		//Store the title width
		this.titleW = titleObj.width;
		//Based on the alignment set the maximum height and width
		if(this.labelPosition.toUpperCase() == "RIGHT" || this.labelPosition.toUpperCase() == "LEFT" ) {
			//Height is the title height
			this.maxH = titleObj.height;
			//Width is the title width and checkBox width
			this.maxW = titleObj.width+this.cbSize;
		} else {
			this.maxH = titleObj.height+this.cbSize;
			this.maxW = (titleObj.width>this.cbSize)?(titleObj.width/2):(this.cbSize);
		}
	}
	/**
	 *  setLabel sets the Label of the checkBox
	 *	@param		label	label to display in checkBox
	*/
	public function setLabel(label:String):Void {
		//Calculate the dimension before setting the label
		this.calcDimension();
		//Set the label
		this.cbLabel = label;
		//Check the alignment - accordingly set the correct position
		if(this.labelPosition.toUpperCase() == "RIGHT" ) {
			//Left alignment
			Utils.createText(false, label, this.cbLabelMC, 3, (this.maxW-this.cbSize)/2+this.cbSize/2, -this.maxH/2, 0, this.cbStyleObj, false, 0, 0);
		} else if(this.labelPosition.toUpperCase() == "LEFT" ) {
			//Right alignment
			Utils.createText(false, label, this.cbLabelMC, 3, (-this.cbSize-this.titleW/2), -this.maxH/2, 0, this.cbStyleObj, false, 0, 0);			
		} else {
			//Bottom Alignment
			Utils.createText(false, label, this.cbLabelMC, this.cbLabelMC.getNextHighestDepth(), -this.givenW/2, 5, 0, this.cbStyleObj, true, Math.abs(this.givenW), 30);
		}
		//Set the position
		this.cbLabelMC._x = this.cbX;
		this.cbLabelMC._y = this.cbY;
	}
	/**
	 * inValidate method redraws the component without any code repitition.
	 */
	public function invalidate():Void {
		this.draw();
	}
	/**
	 * calcColor method calculates the color of the Component
	 */
	private function calcColor(col:String):Void {
		//CheckBox
		this.cbBorderColor = col;
		this.cbBgColor = ColorExt.getLightColor(col, 0.15);
		// TickMark
		this.tmBorderColor = ColorExt.getDarkColor(col, 0.15);
	}
	/**
	 * setColor method sets a new color for the scroller component
	 */
	public function setColor(col:String):Void {
		this.calcColor(col);
		this.draw();
	}
	/**
	 *  getLabel returns the Label of the checkBox
	*/
	public function getLabel(label:String):String {
		return this.cbLabel;
	}
	/**
	  * setState method Sets the tickMark to visible.
	  *	@param		state	state of the checkBox
	*/
	public function setState(state:Boolean):Void {
		//Show the tick Mark
		this.tm._visible = state;
	}
	/**
	  * getState method returns the status of the CheckBox
	*/
	public function getState():Boolean {
		//return the tick Mark visibility
		return this.tm._visible;
	}
	/**
	  * getHeight method returns the total Height required for the CheckBox
	*/
	public function getHeight():Number{
		//return the tick Mark visibility
		return this.maxH;
	}
	/**
	  * getWidth method returns the total Width required for the CheckBox
	*/
	public function getWidth():Number {
		//return the tick Mark visibility
		return this.maxW;
	}
	/**
	  * show method display the checkbox.
	*/
	public function show():Void {
		//Show the Button
		this.parentContMC._visible = true;
	}
	/**
	 * hide method hides the checkbox.
	*/
	public function hide():Void {
		//Hide the button
		this.parentContMC._visible = false;
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//Delete the movieclip
		this.parentContMC.removeMovieClip();
	}
}
