/**
 * @class SelectRect
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * SelectRectangle class helps to generate a selectable rectangle with resize and dragging property
 * based on the parameters specified
*/
import com.fusioncharts.helper.FCEnum;
//Drop Shadow filter
import flash.filters.DropShadowFilter;
//Delegate
import mx.utils.Delegate;
//Event Dispatcher
import mx.events.EventDispatcher;
//Class
class com.fusioncharts.helper.SelectRect {
	//Instance variables
	//Index - unique id to identify each rectangle by the parent class
	private var index:String;
	//parent MC properties
	private var parent:MovieClip;
	private var parentX:Number;
	private var parentY:Number;
	private var parentW:Number;
	private var parentH:Number;
	//Initial X and Y position of the MC
	private var initialX:Number;
	private var initialY:Number;
	//X and Y position of the rectangle for bounds
	private var startX:Number;
	private var startY:Number;
	//End X and Y position of the MC
	private var endX:Number;
	private var endY:Number;
	//Height and width of the selectable rectangle
	private var h:Number;
	private var w:Number;
	//Cosmetic properties
	private var borderColor:String;
	private var borderThickness:Number;
	private var bgColor:String;
	private var bgAlpha:Number;
	private var yPadding:Number;
	//Flag to indicate release of the mouse for drawing the rectangle
	private var ifDrawn:Boolean;
	//MovieClip of the selectable rectangle
	private var mc:MovieClip;
	//Container
	private var mcCont:MovieClip;
	//Resize Handler Cursor
	private var resizeHMC:MovieClip;
	//Drag Cursor
	private var dragCursorMC:MovieClip;
	//Configuration options
	private var enableDrag:Boolean;
	private var enableResize:Boolean;
	private var resizeCMI:ContextMenuItem;
	//Constant - resize handler rectangle width and height
	private var RESIZEH:Number = 5;
	//Create enumeration for the various resize Handlers type that's supported
	public var TYPE;
	//Constructor function
	/**
	 * Here, we initialize the dialog Box objects.
	 *	@param		target		Movie clip in which we've to create the
	 *							dialog box.
	 *	@param		mc			Movie clip reference for the selectable rectangle
	 *	@param		index		Unique indentification string for the rectangle.
	 *	@param		parentX		X position for the parent MC.
	 *	@param		parentY		Y position of the parent MC.
	 *	@param		parentW		width of the parent MC.
	 *	@param		parentH		height of the parent MC.
	 *	@param		yPadding	y Padding (if any required).
	*/
	function SelectRect(target:MovieClip, mc:MovieClip, index:String, parentX:Number, parentY:Number, parentW:Number, parentH:Number, yPadding:Number) {
		//Store parameters in instance variables
		this.parent = target;
		this.parentX = parentX;
		this.parentY = parentY;
		this.parentH = parentH;
		this.parentW = parentW;
		this.yPadding = yPadding;
		//ID
		this.index = index;
		//Store the reference where the Selectrect has to be drawn
		this.mcCont = mc;
		//Hide the movieClip initially
		this.mcCont._visible = false;
		//Hide the hand cursor
		this.mcCont.useHandCursor = false;
		//Set the flag
		this.ifDrawn = false;
		this.enableDrag = false;
		this.enableResize = true;
		//Draw the resize Handler Cursor
		this.drawResizeHCursor();
		//Draw the Drag Cursor
		this.drawDragCursor();
		//Enumerate the list
		TYPE = new FCEnum("TOPLEFT", "TOPCENTER", "TOPRIGHT", "MIDDLELEFT", "MIDDLERIGHT", "BOTTOMLEFT", "BOTTOMCENTER", "BOTTOMRIGHT");
		//call initialize on EventDispatcher to
		//implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
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
	 * setInitialPos method sets the initial Start X and Y Pos of the rectangle
	 *	@param	initialX	startX pos of the rectangle
	 *	@param	initialY	startY pos of the rectangle
	*/
	public function setInitialPos(initialX:Number, initialY:Number) {
		//Store parameters in instance variables.
		this.startX = this.initialX=initialX;
		this.startY = this.initialY=initialY;
	}
	/**
	 * setParams method sets the cosmetic and functional parameters 
	 * for the Select rectangle.	 
	 *	@param	bgColor			Background Color
	 *	@param	bgAlpha			Background Alpha
	 *	@param	borderColor		Border Color of the selectable rectangle
	 *	@param	borderThickness	Border thickness of the selectable rectangle
	*/
	public function setParams(bgColor:String, bgAlpha:Number, borderColor:String, borderThickness:Number) {
		//Store parameters in instance variables.
		this.bgColor = bgColor;
		this.bgAlpha = bgAlpha;
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
	}
	/**
	 * draw method draw the initial Select Rectangle and define the mouse move event.
	*/
	public function draw():Void {
		this.mc = this.mcCont.createEmptyMovieClip("rect", 1);
		//Track the onMouseMove for cMC - as we create the initial rectangle using dragging of mouse
		this.parent.onMouseMove = Delegate.create(this, fnOnMouseMove);
		//Set the initial position of the container to 0,0
		this.mcCont._x = 0;
		this.mcCont._y = 0;
	}
	/**
	 * hideHandlers method make the handlers hidden
	*/
	public function hideHandlers():Void {
		//Now hide all the resize handlers of this MC
		for (var i:Number = 1; i<9; i++) {
			var mcRH:MovieClip = eval(this.mcCont+".ResizeH_"+i);
			mcRH._visible = false;
		}
	}
	/**
	 * showHandlers method make the handlers visible
	*/
	private function showHandlers():Void {
		//Set the resizeFlag
		this.enableResize = true;
		//Show all the handlers
		for (var i:Number = 1; i<9; i++) {
			var mcRH:MovieClip = eval(this.mcCont+".ResizeH_"+i);
			mcRH._visible = true;
		}
	}
	/**
	 * clearHandlers method clears all the handlers MC
	 *	@param	flag	Reverse flag to indicate whether we need to reset the resize flags.
	*/
	private function clearHandlers(flag:Boolean):Void {
		//Flag to check whether this function is called while redrawing the handlers
		//We do not want to reset the resize flags
		//If flag==false | undefined
		if (!flag) {
			//Disable resize flag
			this.enableResize = false;
			//Enable the resize context menu item
			this.resizeCMI.enabled = true;
		}
		//Now clear all the 8 handlers  
		for (var i:Number = 1; i<9; i++) {
			var mcRH:MovieClip = eval(this.mcCont+".ResizeH_"+i);
			mcRH.clear();
		}
	}
	/**
	 * drawResizeH method draw the resize Handlers of the rectangle
	*/
	private function drawResizeH():Void {
		//There will be all together 8 resize handlers in the rectangle
		//Loop Variables;
		var i:Number, deltaX:Number, deltaY:Number;
		var w:Number, h:Number;
		//Store the height and width of the rectangle
		w = this.mc._width;
		h = this.mc._height;
		//Class Reference
		var classRef:Object = this;
		//Clear the previously drawn rectangles with upsetting any flags
		this.clearHandlers(true);
		//Draw the rectangular handler.
		for (i=1; i<9; i++) {
			//Movie clip for each handler
			var mcRH:MovieClip = this.mcCont.createEmptyMovieClip("ResizeH_"+i, i+1);
			//Set the linestyle
			mcRH.lineStyle(0, parseInt("000000", 16), 100);
			//Begin Fill
			mcRH.beginFill(parseInt("000000", 16), 100);
			//Draw the handler
			mcRH.moveTo(-RESIZEH/2, -RESIZEH/2);
			mcRH.lineTo(RESIZEH/2, -RESIZEH/2);
			mcRH.lineTo(RESIZEH/2, RESIZEH/2);
			mcRH.lineTo(-RESIZEH/2, RESIZEH/2);
			mcRH.lineTo(-RESIZEH/2, -RESIZEH/2);
			mcRH.endFill();
			mcRH.lineStyle();
			//For the outer boundary - to catch the handler event (relaxation)
			mcRH.beginFill(parseInt("000000", 16), 0);
			mcRH.moveTo(-RESIZEH/2-5, -RESIZEH/2-5);
			mcRH.lineTo(RESIZEH/2+5, -RESIZEH/2-5);
			mcRH.lineTo(RESIZEH/2+5, RESIZEH/2+5);
			mcRH.lineTo(-RESIZEH/2-5, RESIZEH/2+5);
			mcRH.lineTo(-RESIZEH/2-5, -RESIZEH/2-5);
			mcRH.endFill();
			//For each handler position we define different listener
			switch (i) {
				//Case of top left position
			case this.TYPE.TOPLEFT :
				//Initial position would be top left of the MC
				mcRH._x = this.initialX;
				mcRH._y = this.initialY;
				//Align and vertical alignment for redrawing would be diagonal opposite node
				//To fix the base line and hence scaling makes it easier to handle
				mcRH.align = "R";
				mcRH.vAlign = "B";
				//rotate the cursor
				mcRH.rotationVal = -45;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX+w, classRef.initialY+h, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, classRef.parentX, classRef.parentY, classRef.initialX+classRef.mc.width, classRef.initialY+classRef.mc.height);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the needful change
						//else we do not allow dragging
						if (classRef.parent._xmouse>classRef.parentX) {
							deltaX = (startX-classRef.parent._xmouse);
						}
						if (classRef.parent._ymouse>classRef.parentY) {
							deltaY = (startY-classRef.parent._ymouse);
						}
						//Change the width and height of the drawn rectangle  
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Set the new initial X and Y position as the top left of the rectangle has been dragged
						classRef.initialX = classRef.mc._x-classRef.mc._width;
						classRef.initialY = classRef.mc._y-classRef.mc._height;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of top center position
			case this.TYPE.TOPCENTER :
				//Initial position would be top center of the MC
				mcRH._x = this.initialX+(this.mc._width)/2;
				mcRH._y = this.initialY;
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle				
				mcRH.align = "C";
				mcRH.vAlign = "B";
				//rotate the cursor
				mcRH.rotationVal = 0;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX+w/2, classRef.initialY+h, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, this._x, classRef.parentY, this._x, classRef.initialY+classRef.mc.height);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					this.onEnterFrame = function() {
						//Now while dragging of the handler we calculate the deviation of the handler from 
						//previous position and hence new position is calculated (global position)
						deltaX = 0;
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						if (classRef.parent._ymouse>classRef.parentY) {
							deltaY = (startY-classRef.parent._ymouse);
						}
						//Change the width and height of the drawn rectangle  
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Set the new initial Y position as the top center of the rectangle has been dragged						
						classRef.initialY = classRef.mc._y-classRef.mc._height;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of top left position
			case this.TYPE.TOPRIGHT :
				//Initial position would be top right of the MC
				mcRH._x = (this.initialX+this.mc._width);
				mcRH._y = this.initialY;
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle
				mcRH.align = "L";
				mcRH.vAlign = "B";
				//rotate the cursor
				mcRH.rotationVal = 45;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX, classRef.initialY+h, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, classRef.initialX, classRef.initialY+classRef.mc.height, classRef.parentX+classRef.parentW, classRef.parentY);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						if (classRef.parent._xmouse<classRef.parentX+classRef.parentW) {
							deltaX = (classRef.parent._xmouse-startX);
						}
						if (classRef.parent._ymouse>classRef.parentY) {
							deltaY = (startY-classRef.parent._ymouse);
						}
						//Change the width and height of the drawn rectangle 
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Set the new initial Y position as the top of the rectangle has been dragged
						classRef.initialY = classRef.mc._y-classRef.mc._height;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of top left position
			case this.TYPE.MIDDLELEFT :
				//Initial position would be top left of the MC
				mcRH._x = this.initialX;
				mcRH._y = this.initialY+(this.mc._height)/2;
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle
				mcRH.align = "R";
				mcRH.vAlign = "M";
				//rotate the cursor
				mcRH.rotationVal = 90;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX+w, classRef.initialY+h/2, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, classRef.parentX, this._y, classRef.initialX+classRef.mc.width, this._y);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						if (classRef.parent._xmouse>classRef.parentX) {
							deltaX = (startX-classRef.parent._xmouse);
						}
						//Change the width and height of the drawn rectangle 
						deltaY = 0;
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Set the new initial X position as the left of the rectangle has been dragged
						classRef.initialX = classRef.mc._x-classRef.mc._width;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of middle right position
			case this.TYPE.MIDDLERIGHT :
				//Initial position would be top left of the MC
				mcRH._x = this.initialX+this.mc._width;
				mcRH._y = this.initialY+(this.mc._height)/2;
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle
				mcRH.align = "L";
				mcRH.vAlign = "M";
				//rotate the cursor
				mcRH.rotationVal = 90;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX, classRef.initialY+h/2, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, classRef.parentX+classRef.parentW, this._y, classRef.initialX, this._y);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						if (classRef.parent._xmouse<classRef.parentX+classRef.parentW) {
							deltaX = (classRef.parent._xmouse-startX);
						}
						deltaY = 0;
						//Change the width and height of the drawn rectangle
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of bottom left position
			case this.TYPE.BOTTOMLEFT :
				//Initial position would be top left of the MC
				mcRH._x = this.initialX;
				mcRH._y = (this.initialY+this.mc._height);
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle
				mcRH.align = "R";
				mcRH.vAlign = "T";
				//rotate the cursor
				mcRH.rotationVal = 45;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX+w, classRef.initialY, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, classRef.parentX, classRef.parentY+classRef.parentH, classRef.initialX+classRef.mc.width, classRef.initialY);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						if (classRef.parent._xmouse>classRef.parentX) {
							deltaX = (startX-classRef.parent._xmouse);
						}
						if (classRef.parent._ymouse<classRef.parentY+classRef.parentH) {
							deltaY = (classRef.parent._ymouse-startY);
						}
						//Change the width and height of the drawn rectangle 
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Set the new initial X position as the left of the rectangle has been dragged
						classRef.initialX = classRef.mc._x-classRef.mc._width;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of bottom center position
			case this.TYPE.BOTTOMCENTER :
				//Initial position would be top left of the MC
				mcRH._x = this.initialX+(this.mc._width)/2;
				mcRH._y = (this.initialY+this.mc._height);
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle
				mcRH.align = "C";
				mcRH.vAlign = "T";
				//rotate the cursor
				mcRH.rotationVal = 0;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX+w/2, classRef.initialY, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, this._x, classRef.initialY, this._x, classRef.parentY+classRef.parentH);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						deltaX = 0;
						if (classRef.parent._ymouse<classRef.parentY+classRef.parentH) {
							deltaY = (classRef.parent._ymouse-startY);
						}
						//Change the width and height of the drawn rectangle 
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;						
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
				//Case of bottom right position
			case this.TYPE.BOTTOMRIGHT :
				//Initial position would be top left of the MC
				mcRH._x = this.initialX+this.mc._width;
				mcRH._y = (this.initialY+this.mc._height);
				//Align and vertical alignment for redrawing would be diagonal opposite
				//To fix the base line and hence scalling makes it easier to handle
				mcRH.align = "L";
				mcRH.vAlign = "T";
				//rotate the cursor
				mcRH.rotationVal = -45;
				//Define the onPress event
				mcRH.onPress = function() {
					//delete the mouse move event if any stored in memory
					delete classRef.parent.onMouseMove;
					//Clear the drawn rectangle
					classRef.mc.clear();
					//Make it hidden
					classRef.mc._visible = false;
					//Redraw the rectangle based on the registration point
					classRef.drawRectangle(classRef.mc, classRef.initialX, classRef.initialY, w, h, this.align, this.vAlign, classRef.bgColor, classRef.bgAlpha, classRef.borderThickness, classRef.borderColor, 100);
					//Make the handler draggable
					this.startDrag(false, classRef.initialX, classRef.initialY, classRef.parentX+classRef.parentW, classRef.parentY+classRef.parentH);
					//Store the startX and startY of the handler
					var startX:Number = classRef.parent._xmouse;
					var startY:Number = classRef.parent._ymouse;
					//Hide the handlers
					classRef.hideHandlers();
					//Now while dragging of the handler we calculate the deviation of the handler from 
					//previous position and hence new position is calculated (global position)
					this.onEnterFrame = function() {
						//Only if the delta is greater than the previous height and width we do the neeedful change
						//else we do not allow dragging
						if (classRef.parent._xmouse<classRef.parentX+classRef.parentW) {
							deltaX = (classRef.parent._xmouse-startX);
						}
						if (classRef.parent._ymouse<classRef.parentY+classRef.parentH) {
							deltaY = (classRef.parent._ymouse-startY);
						}
						//Change the width and height of the drawn rectangle 
						classRef.mc._width = classRef.mc.width+deltaX;
						classRef.mc._height = classRef.mc.height+deltaY;
						//Make it visible
						classRef.mc._visible = true;
						//Set the new position of the handler
						classRef.resizeHMC._x = this._parent._xmouse;
						classRef.resizeHMC._y = this._parent._ymouse;
						//Update after every event to prevent any flickering
						updateAfterEvent();
					};
				};
				break;
			}
			//On Release event
			mcRH.onReleaseOutside = mcRH.onRelease=function () {
				//Stop the dragging if remained true
				this.stopDrag();
				//redraw the handlers
				classRef.drawResizeH();
				//delete the onEnterFrames
				delete this.onEnterFrame;
				//Show the mouse cursor
				Mouse.show();
				//hide the resize cursor
				classRef.resizeHMC._visible = false;
				//Store the height and width
				classRef.mc.width = classRef.mc._width;
				classRef.mc.height = classRef.mc._height;
				//Since we changed the registration point while resizing the rectangle
				//For the bounds we need the left top position of the rectangle so here we calculate the 
				//left top of the rectangle for all the alignments set during resizing
				switch (this.align.concat(this.vAlign)) {
					//Right Bottom
				case "RB" :
					classRef.startX = classRef.mc._x-classRef.mc._width;
					classRef.startY = classRef.mc._y-classRef.mc._height;
					break;
					//Center Bottom
				case "CB" :
					classRef.startX = classRef.mc._x-classRef.mc._width/2;
					classRef.startY = classRef.mc._y-classRef.mc._height;
					break;
					//Left Bottom
				case "LB" :
					classRef.startX = classRef.mc._x;
					classRef.startY = classRef.mc._y-classRef.mc._height;
					break;
					//Right Middle
				case "RM" :
					classRef.startX = classRef.mc._x-classRef.mc._width;
					classRef.startY = classRef.mc._y-classRef.mc._height/2;
					break;
					//Left Middle
				case "LM" :
					classRef.startX = classRef.mc._x;
					classRef.startY = classRef.mc._y-classRef.mc._height/2;
					break;
					//Right Top
				case "RT" :
					classRef.startX = classRef.mc._x-classRef.mc._width;
					classRef.startY = classRef.mc._y;
					break;
					//Center Top
				case "CT" :
					classRef.startX = classRef.mc._x-classRef.mc._width/2;
					classRef.startY = classRef.mc._y;
					break;
					//Left Top
				case "LT" :
					classRef.startX = classRef.mc._x;
					classRef.startY = classRef.mc._y;
					break;
				}
			};
			//onRollover to any handlers we hide the mouse cursor and show the resize cursor
			mcRH.onRollOver = function() {
				//Hide the mouse
				Mouse.hide();
				//Set the resize cursor position as the current mouse cursor
				classRef.resizeHMC._x = this._parent._xmouse;
				classRef.resizeHMC._y = this._parent._ymouse;
				//Set the rotation according to the selected handler
				classRef.resizeHMC._rotation = this.rotationVal;
				//Make it visible
				classRef.resizeHMC._visible = true;
				//On enter frame we keep the visibility to true 
				//And we hide it only when it is roll out of handler
				this.onEnterFrame = function() {
					//Set the new position of the cursor
					classRef.resizeHMC._x = this._parent._xmouse;
					classRef.resizeHMC._y = this._parent._ymouse;
					//to avoid flickers
					updateAfterEvent();
				};
			};
			//On RollOut we hide the cursor and show the normal mouse cursor
			mcRH.onRollOut = function() {
				//Show the mouse
				Mouse.show();
				//Delete the onEnterFrame event for efficiency
				delete this.onEnterFrame;
				//Set the cursor visibility to false
				classRef.resizeHMC._visible = false;
			};
		}
	}
	/**
	 * drawRectangle method draws a generic rectangle from a desired registration point.
	 *	@param		mc		Movie clip in which we've to create the selectable rectangle
	 *	@param		xPos	x Position at which we need to draw the rectangle
	 *	@param		yPos	y Position at which we need to draw the rectangle
	 *	@param		width	width of the rectangle to draw
	 *	@param		height	height of the rectangle to draw
	 *	@param		align		horizontal alignment of the rectangle with respect to registration point.
	 *	@param		vAlign		vertical alignment of the rectangle with respect to registration point.
	 *	@param		bgColor		background color of the rectangle
	 *	@param		bgAlpha		background transparency of the rectangle
	 *	@param		borderThickness		border thickness of the drawn rectangle
	 *	@param		borderColor			border color of the rectangle
	 *	@param		borderAlpha			border transparency of the rectangle
	*/
	private function drawRectangle(mc:MovieClip, xPos:Number, yPos:Number, width:Number, height:Number, align:String, vAlign:String, bgColor:String, bgAlpha:Number, borderThickness:Number, borderColor:String, borderAlpha:Number):Void {
		//Now draw the rectangle
		//Set the linestyle
		mc.lineStyle(0, parseInt(borderColor, 16), borderAlpha);
		//begin fill
		mc.beginFill(parseInt(bgColor, 16), bgAlpha);
		//based on the alignment and vertical alignment
		switch (align.concat(vAlign)) {
			//Based on the alignments we set the registration point of the rectangle
		case "LT" :
			mc.moveTo(0, 0);
			mc.lineTo(width, 0);
			mc.lineTo(width, height);
			mc.lineTo(0, height);
			mc.lineTo(0, 0);
			break;
		case "CT" :
			mc.moveTo(0, 0);
			mc.lineTo(width/2, 0);
			mc.lineTo(width/2, height);
			mc.lineTo(-width/2, height);
			mc.lineTo(-width/2, 0);
			mc.lineTo(0, 0);
			break;
		case "RT" :
			mc.moveTo(0, 0);
			mc.lineTo(0, height);
			mc.lineTo(-width, height);
			mc.lineTo(-width, 0);
			mc.lineTo(0, 0);
			break;
		case "LM" :
			mc.moveTo(0, 0);
			mc.lineTo(0, height/2);
			mc.lineTo(width, height/2);
			mc.lineTo(width, -height/2);
			mc.lineTo(0, -height/2);
			mc.lineTo(0, 0);
			break;
		case "RM" :
			mc.moveTo(0, 0);
			mc.lineTo(0, height/2);
			mc.lineTo(-width, height/2);
			mc.lineTo(-width, -height/2);
			mc.lineTo(0, -height/2);
			mc.lineTo(0, 0);
			break;
		case "LB" :
			mc.moveTo(0, 0);
			mc.lineTo(0, -height);
			mc.lineTo(width, -height);
			mc.lineTo(width, 0);
			mc.lineTo(0, 0);
			break;
		case "CB" :
			mc.moveTo(0, 0);
			mc.lineTo(width/2, 0);
			mc.lineTo(width/2, -height);
			mc.lineTo(-width/2, -height);
			mc.lineTo(-width/2, 0);
			mc.lineTo(0, 0);
			break;
		case "RB" :
			mc.moveTo(0, 0);
			mc.lineTo(0, -height);
			mc.lineTo(-width, -height);
			mc.lineTo(-width, 0);
			mc.lineTo(0, 0);
			break;
		}
		mc._x = xPos;
		mc._y = yPos;
		//End fill
		mc.endFill();
	}
	/** 
	* drawResizeHCursor method draws a resize handler Mouse Cursor
	*/
	public function drawResizeHCursor():Void {
		this.resizeHMC = this.mcCont.createEmptyMovieClip("resizeHandler", 11);
		//Hide the cursor initially
		this.resizeHMC._visible = false;
		//Start Filling
		this.resizeHMC.beginFill(parseInt("000000", 16), 100);
		//Draw the cursor
		this.resizeHMC.moveTo(-1, 4);
		this.resizeHMC.lineTo(-1, -4);
		this.resizeHMC.lineTo(-3.5, -4);
		this.resizeHMC.lineTo(0, -6);
		this.resizeHMC.lineTo(3.5, -4);
		this.resizeHMC.lineTo(1, -4);
		this.resizeHMC.lineTo(1, 4);
		this.resizeHMC.lineTo(3.5, 4);
		this.resizeHMC.lineTo(0, 6);
		this.resizeHMC.lineTo(-3.5, 4);
		this.resizeHMC.lineTo(-1, 4);
		this.resizeHMC.endFill();
		var shadowFilter:DropShadowFilter = new DropShadowFilter(3, 45, 0x999999, 0.8, 4, 4, 1, 1, false, false, false);
		this.resizeHMC.filters = [shadowFilter];
	}
	/** 
	* drawDragCursor method draws a resize handler Mouse Cursor
	*/
	public function drawDragCursor():Void {
		this.dragCursorMC = this.mcCont.createEmptyMovieClip("dragCursor", 12);
		//Hide the cursor initially
		this.dragCursorMC._visible = false;
		//First  Draw the UPDOWN arrow
		//Start Filling
		this.dragCursorMC.beginFill(parseInt("000000", 16), 100);
		//Draw the cursor
		this.dragCursorMC.moveTo(-1, 4);
		this.dragCursorMC.lineTo(-1, -4);
		this.dragCursorMC.lineTo(-3.5, -4);
		this.dragCursorMC.lineTo(0, -6);
		this.dragCursorMC.lineTo(3.5, -4);
		this.dragCursorMC.lineTo(1, -4);
		this.dragCursorMC.lineTo(1, 4);
		this.dragCursorMC.lineTo(3.5, 4);
		this.dragCursorMC.lineTo(0, 6);
		this.dragCursorMC.lineTo(-3.5, 4);
		this.dragCursorMC.lineTo(-1, 4);
		this.dragCursorMC.endFill();
		//Now Draw the LEFT ROGHT arrow
		//Start Filling
		this.dragCursorMC.beginFill(parseInt("000000", 16), 100);
		//Draw the cursor
		this.dragCursorMC.moveTo(4, -1);
		this.dragCursorMC.lineTo(-4, -1);
		this.dragCursorMC.lineTo(-4, -3.5);
		this.dragCursorMC.lineTo(-6, 0);
		this.dragCursorMC.lineTo(-4, 3.5);
		this.dragCursorMC.lineTo(-4, 1);
		this.dragCursorMC.lineTo(4, 1);
		this.dragCursorMC.lineTo(4, 3.5);
		this.dragCursorMC.lineTo(6, 0);
		this.dragCursorMC.lineTo(4, -3.5);
		this.dragCursorMC.lineTo(4, -1);
		this.dragCursorMC.endFill();
		var shadowFilter:DropShadowFilter = new DropShadowFilter(3, 45, 0x999999, 0.8, 4, 4, 1, 1, false, false, false);
		this.dragCursorMC.filters = [shadowFilter];
	}
	/**
	  * returns the position of the rectangle start and end position 
	*/
	public function getBound():Object {
		var posObj:Object = new Object();
		posObj.fromX = this.startX+this.mcCont._x;
		posObj.toX = (posObj.fromX+this.mc._width);
		posObj.fromY = this.startY+this.mcCont._y;
		posObj.toY = (posObj.fromY+this.mc._height);
		//object which holds the positions
		return posObj;
	}
	/**
	 * setContextMenu method sets the context menu for the chart. It is invoked
	 * automatically by mouse-up handler whenever a new SelectRect has been created
	 * and visually rendered on the canvas. This method enables the context menu items
	 * like dragging, resize and delete.
	*/
	private function setContextMenu():Void {
		//Get reference to Context Menu
		var chartMenu:ContextMenu = new ContextMenu();
		//And hide built in items
		chartMenu.hideBuiltInItems();
		//Create a drag chart context menu item
		var dragCMI:ContextMenuItem = new ContextMenuItem("Drag Selection", dragHandler);
		//Push drag item.
		chartMenu.customItems.push(dragCMI);
		//Create a resize contenxt menu item
		var resizeCMI:ContextMenuItem = new ContextMenuItem("Resize Selection", resizeHandler, false, !this.enableResize);
		//Push resize item.
		chartMenu.customItems.push(resizeCMI);
		//Store in the class variable
		this.resizeCMI = resizeCMI;
		//Create a delete contenxt menu item
		var delCMI:ContextMenuItem = new ContextMenuItem("Delete Selection", deleteHandler);
		//Push delete item.
		chartMenu.customItems.push(delCMI);
		//Store the local reference for the handlers
		var instanceRef:Object = this;
		// functions invoked due selection of the menu items are defined
		function dragHandler() {
			// enabling/disabling the ContextMenuItems
			dragCMI.enabled = false;
			resizeCMI.enabled = true;
			delCMI.enabled = true;
			// updating flags about current menu enable status
			instanceRef.enableDrag = true;
			instanceRef.enableResize = false;
			//Clear Handlers
			instanceRef.clearHandlers();
			instanceRef.startDragging();
		}
		function resizeHandler() {
			// enabling/disabling the ContextMenuItems
			dragCMI.enabled = true;
			resizeCMI.enabled = false;
			delCMI.enabled = true;
			// updating flags about current menu enable status
			instanceRef.enableDrag = false;
			instanceRef.enableResize = true;
			instanceRef.mc.onPress = instanceRef.mc.onRelease=function () {
			};
			//Hide the Drag Cursor
			instanceRef.dragCursorMC._visible = false;
			//Show the mouse
			Mouse.show();
			//Do not use hand cursor
			instanceRef.mc.useHandCursor = false;
			//Redraw the handlers;
			instanceRef.drawResizeH();
			//Define the rollOut and rollOver event
			instanceRef.mc.onRollOut = instanceRef.mc.onRollOver=function () {
				instanceRef.dragCursorMC._visible = false;
				Mouse.show();
			};
			//Dispatch this event to notify the parent class 
			instanceRef.dispatchEvent({type:"onRectRHSet", target:instanceRef, index:instanceRef.index});
		}
		function deleteHandler() {
			Mouse.show();
			// enabling/disabling the ContextMenuItems
			dragCMI.enabled = false;
			resizeCMI.enabled = false;
			delCMI.enabled = true;
			// updating flags about current menu enable status
			instanceRef.enableDrag = false;
			instanceRef.enableResize = false;
			//Dispatch the event - for any modification required from parent class
			instanceRef.dispatchEvent({type:"onRectDelete", target:instanceRef, index:instanceRef.index});
			//We do not delete the rectangle - this will be handled from the parent class
			//instanceRef.destroy();
		}
		//Assign the menu to cMC movie clip
		this.mc.menu = chartMenu;
	}
	//-------------------EVENT HANDLERS-------------------------//
	/**
	 * startDragging method makes the rectangle MC draggable
	*/
	private function startDragging():Void {
		//Store the class reference
		var classRef:Object = this;
		//if the dragging mode is ON then define the events for dragging
		if (this.enableDrag) {
			//If the rectangle is pressed then start dragging the rectangle
			this.mc.onPress = function() {
				//Set the cursor visibility to true
				classRef.dragCursorMC._visible = true;
				//Start dragging to the extent of canvas
				this._parent.startDrag(false, classRef.parentX-classRef.initialX, classRef.parentY-classRef.initialY, classRef.parentW+classRef.parentX-classRef.initialX-classRef.mc._width, classRef.parentH+classRef.parentY-classRef.initialY-classRef.mc._height);
			};
			//On release we stop dragging mode and store the initial height and width parameters
			//We do not unset the dragging flags - as this doesn't imply OFF for the dragging mode
			//Drag mode is set to OFF only once the resize mode is set to ON.
			this.mc.onReleaseOutside = function() {
				//Make this rectangle draggable
				this.stopDrag();
				//Set the cursor visibility to false
				classRef.dragCursorMC._visible = false;
				//Store the height and width. this here refers to "rect" (depth: 1)
				this.width = this._width;
				this.height = this._height;
			};
			//Copy of mc.onReleaseOutside but doesn't hide the drag-cursor
			this.mc.onRelease = function() {
				//Make this rectangle draggable
				this.stopDrag();
				//Store the height and width. this here refers to "rect" (depth: 1)
				this.width = this._width;
				this.height = this._height;
			};
			//onRollover to any Movie we hide the mouse cursor and show the drag cursor
			this.mc.onRollOver = function() {
				//Hide the mouse
				Mouse.hide();
				//Set the resize cursor position as the current mouse cursor
				classRef.dragCursorMC._x = this._parent._xmouse+2;
				classRef.dragCursorMC._y = this._parent._ymouse-2;
				//Make it visible
				classRef.dragCursorMC._visible = true;
				//On enter frame we keep the visibility to true 
				//And we hide it only when it is roll out of handler
				this.onEnterFrame = function() {
					//Set the new position of the cursor
					classRef.dragCursorMC._x = this._parent._xmouse+2;
					classRef.dragCursorMC._y = this._parent._ymouse-2;
					//to avoid flickers
					updateAfterEvent();
				};
			};
			//On RollOut we hide the cursor and show the normal mouse cursor
			this.mc.onRollOut = function() {
				//Show the mouse
				Mouse.show();
				//Delete the onEnterFrame event for efficiency
				delete this.onEnterFrame;
				//Set the cursor visibility to false
				classRef.dragCursorMC._visible = false;
			};
		}
	}
	/**
	  * fnOnMouseUp method define the enter frame event.
	*/
	public function fnOnMouseUp() {
		//delete the onMouseMove event
		delete this.parent.onMouseMove;
		//Store the class Reference
		var classRef:Object = this;
		if (this.ifDrawn) {
			//Set the initial X and Y pos as the min of initialX and endX
			this.initialX = this.startX=this.mc._x;
			this.initialY = this.startY=this.mc._y;
			this.endX = this.mc._x+this.mc._width;
			this.endY = this.mc._y+this.mc._height;
			//Set the context Menu
			this.setContextMenu();
			//Store the height and width
			this.mc.width = this.mc._width;
			this.mc.height = this.mc._height;
			//Check whether we need to draw the resize handlers
			if (this.enableResize) {
				//Redraw the handlers;
				this.drawResizeH();
				//Do not allow mouse handlers within the movieclip while resizing
				this.mc.onPress = this.mc.onRelease=function () {
				};
				this.mc.onRollOut = this.mc.onRollOver=function () {
					classRef.dragCursorMC._visible = false;
					Mouse.show();
				};
				//We do not show the hand cursor
				this.mc.useHandCursor = false;
			}
			//Set the flag to false - as we have finished updation after completion 
			this.ifDrawn = false;
		}
	}
	/**
	  * fnOnMouseMove method defines the mouse movement event for drawing a new rectangle.
	*/
	private function fnOnMouseMove() {
		//Set the flag
		this.ifDrawn = true;
		//Store the starting and ending position
		var startXPos:Number = this.initialX;
		var startYPos:Number = this.initialY;
		var endXPos:Number = this.parent._xmouse;
		var endYPos:Number = this.parent._ymouse;
		//Check whether the point is within the canvas area - if not then reset it to canvas end points
		if (endXPos>this.parentX+this.parentW) {
			endXPos = this.parentX+this.parentW;
		}
		if (endXPos<this.parentX) {
			endXPos = this.parentX;
		}
		if (endYPos>this.parentY+this.parentH) {
			endYPos = this.parentY+this.parentH;
		}
		if (endYPos<this.parentY) {
			endYPos = this.parentY;
		}
		//Get the minimum (in case dragging is from right to left or bottom to top) 
		var minX:Number = Math.min(startXPos, endXPos);
		var maxX:Number = Math.max(startXPos, endXPos);
		var minY:Number = Math.min(startYPos, endYPos);
		var maxY:Number = Math.max(startYPos, endYPos);
		//Store in correct order now.
		startXPos = minX;
		endXPos = maxX;
		startYPos = minY;
		endYPos = maxY;
		//Clear the previously drawn movieclip
		this.mc.clear();
		//Set the border color if required       
		if (this.borderColor != "" && this.borderColor != undefined && this.borderColor != null) {
			this.mc.lineStyle(0, parseInt(this.borderColor, 16), 100);
		}
		//Set the background color if required                                                
		if (this.bgColor != "" && this.bgColor != undefined && this.bgColor != null) {
			this.mc.beginFill(parseInt(this.bgColor, 16), this.bgAlpha);
		}
		//Now draw the rectangle        
		this.mc.moveTo(0, 0);
		this.mc.lineTo(0, (endYPos-startYPos));
		this.mc.lineTo((endXPos-startXPos), (endYPos-startYPos));
		this.mc.lineTo((endXPos-startXPos), 0);
		this.mc.lineTo(0, 0);
		//End Fill
		this.mc.endFill();
		this.mc._x = startXPos;
		this.mc._y = startYPos;
	}
	/**
	  * show method shows the select rectangle.
	*/
	public function show():Void {
		//Show the select rectangle
		this.mcCont._visible = true;
	}
	/**
	 * hide method hides the select rectangle.
	*/
	public function hide():Void {
		//Hide the select rectangle
		this.mcCont._visible = false;
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//clear all handlers and reset the flags 
		this.clearHandlers();
		//clear the container
		this.mcCont.clear();
		//Delete the movieclip
		this.mcCont.removeMovieClip();
	}
}
