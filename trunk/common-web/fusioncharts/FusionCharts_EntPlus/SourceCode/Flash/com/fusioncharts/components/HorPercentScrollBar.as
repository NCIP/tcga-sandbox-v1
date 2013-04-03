
/**
 * @class HorPercentScrollBar
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * Low foot print & no-frills horizontal Scroll Bar class that scrolls on percent
 * values. This scroll bar doesn't actually scroll any movie clip or display object.
 * Instead it just scrolls itself and returns its position and size to control code,
 * which can then adjust the display.
 * You can change the cosmetics of this scroll bar using a single color.
 * To use a scroll bar, just instantiate it with the properties and call
 * draw().
 * To set color, use the setColor() method and then draw().
 * Any change needs to be followed by draw() call to take effect.
 * If you want to set the X and Y Position of scroll bar, you can directly
 * do in your parent code, by setting the x/y position of parent movie clip.
 * This component dispatches the following events:
 * - click - When the scroll face or buttons are clicked (before movement).
 * - change - When the scroll bar has changed position.
 * Possible future extensions:
 * - Allow dynamic resize of scroll bar face itself. 
*/
//Color extensions - to get variations of colors
import com.fusioncharts.extensions.ColorExt;
//Utilities
import com.fusioncharts.helper.Utils;
//Event Dispatcher - as we'll dispatch various events when required.
import mx.events.EventDispatcher;
//Delegation
import mx.utils.Delegate;
class com.fusioncharts.components.HorPercentScrollBar extends MovieClip {
	//Reference to parent movie clip. Inside the parent movie clip, the
	//scroll bar is created at 0,0 position (extending right, down). To
	//reposition the scroll bar, the parent movie must be re-positioned.
	private var parentMC:MovieClip;
	//What state the component is in - enabled or disabled.
	//By default, at start, we keep the component enabled (true)
	private var state:Boolean;
	//Flag to indicate whether the scroll bar is in invalidated (dirty) mode
	//Indicates that a redraw of the entire scroll bar is required, owing
	//to change of size/property, but not state and scroll bar position.
	private var dirty:Boolean;
	//Scroll bar start position percent, Size percent & step percent
	private var startPosPercent:Number, sizePercent:Number, stepPercent:Number;
	//Adjusted width percent for scroll
	private var scrollSizeDeltaPercent:Number;
	//Object to store various cosmetic properties
	private var cosmetics:Object;
	//Current Color of the scroll bar
	private var scrollColor:String;
	//Width and height required for scroll bar (entire component)
	private var cmpWidth:Number, cmpHeight:Number;
	//To store padding between the button and track
	private var btnPadding:Number;
	//Track click interval & button click interval
	private var trackInterval:Number, btnInterval:Number;
	//Listener objects - Key & Mouse
	private var keyL:Object, mouseL:Object;
	//State for last dispatched start and endPos
	private var lastDsptStartPos:Number, lastDsptEndPos:Number;
	//---- Movie clips required for the scroll bar ----//
	//Various variables that'll store reference to movie clips
	//Reference to scroll bar face
	private var faceMC:MovieClip;
	//Reference to face Click Clip
	private var faceClickMC:MovieClip;
	//Reference to track movieclip
	private var trackMC:MovieClip;
	//Track click movie clip
	private var trackClickMC:MovieClip;
	//Reference to right button
	private var plusBtnMC:MovieClip;
	//Reference to left button
	private var minusBtnMC:MovieClip;
	//------ Dimensions and co-ordinates --------//
	//Dimensions of scroll track 
	private var trackWidth:Number, trackStartX:Number, trackEndX:Number;
	//Width of the scroll face.
	private var faceWidth:Number;
	//Width for each button
	private var btnWidth:Number;
	//Position of the face based on value
	private var faceStartX:Number;
	//Scroll start and end position of face in pixels
	private var startPosPixel:Number, endPosPixel:Number;
	//Constant - Minimum width of scroll bar for easy handling with mouse (in pixels)
	private var SCROLL_MIN_WIDTH:Number = 3;
	//Alpha of the visual elements in disabled state
	private var DISABLED_STATE_ALPHA:Number = 50;
	/**
	 * Constructor function.
	 *	@param	parentMC	Movieclip container in which scroll bar has to be drawn
	*/
	function HorPercentScrollBar(parentMC:MovieClip) {
		//Store attributes
		this.parentMC = parentMC;
		//Initialize EventDispatcher to implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
		//Initialize the scroll bar
		init();
	}
	/**
	 * init function initializes the process of scroll bar creation. This
	 * method is called once only, from constructor.
	*/
	private function init():Void {
		//Initialize instance variables
		cosmetics = new Object();
		keyL = new Object();
		mouseL = new Object();
		//Set dirty to true initially
		dirty = true;
		//Set default values
		startPosPercent = 0;
		sizePercent = 100;
		stepPercent = 1;
		btnWidth = 16;
		btnPadding = 0;
		lastDsptStartPos =0;
		lastDsptEndPos = 0;
		//Initially, set delta to 0
		scrollSizeDeltaPercent = 0;
		btnWidth = 16;
		//Create children movie clips
		createChildren();
		//Set a default grey color shade
		setColor("CCCCCC");
		//Draw the elements initially.
		setEvents();
		//Set initial state
		state = true;
	}
	/**
	 * createChildren method creates the movie clips and visual elements for
	 * the scroll.
	*/
	private function createChildren():Void {
		//Scroll bar track
		trackMC = parentMC.createEmptyMovieClip("Track", 2);
		//Track click MC 
		trackClickMC = parentMC.createEmptyMovieClip("TrackClick", 3);
		//Plus (right) button
		plusBtnMC = parentMC.createEmptyMovieClip("BtnPlus", 4);
		//Reference to left button
		minusBtnMC = parentMC.createEmptyMovieClip("BtnMinus", 5);
		//Face Click clip
		faceClickMC = parentMC.createEmptyMovieClip("FaceClick", 6);
		//Scroll bar face
		faceMC = parentMC.createEmptyMovieClip("Face", 7);
		//Hide click handlers initially
		faceClickMC._visible = false;
		trackClickMC._visible = false;
	}
	/**
	 * Sets the width and height of entire component.
	 *	@param	cmpWidth		Width of the component - including buttons, track.
	 *	@param	cmpHeight		Height of the component.
	*/
	public function setSize(width:Number, height:Number):Void {
		//Store.
		cmpWidth = Utils.getFirstNumber(width, 100);
		cmpHeight = Utils.getFirstNumber(height, 16);
		//Set flag for redraw
		dirty = true;
	}
	
	/**
	 * Sets the button width and padding.
	 *	@param	btnWidth		Width of scroll buttons
	 *	@param	btnPadding		Padding (in pixels) between the buttons and track.
	*/
	public function setButtonDimensions(btnWidth:Number, btnPadding:Number):Void{
		this.btnWidth = Utils.getFirstNumber(btnWidth, this.btnWidth);
		this.btnPadding = Utils.getFirstNumber(btnPadding, 3);
		//Set flag for redraw
		dirty = true;
	}
	
	/**
	* Sets the starting position for scroll bar.
	* @param	posPercent		Position of the scroll bar in percent 
	*							(w.r.t track as 100%)
	*/
	public function setStartPosPercent(posPercent:Number):Void {
		//Proceed, if different
		if (posPercent != startPosPercent) {
			//Store
			startPosPercent = posPercent;
			//Set dirty flag to true, so that it can be picked in next redraw
			//(as this function is for only setting the value initially).
			dirty = true;
		}
	}
	
	/**
	 * Sets (or later, re-sets) the size of scroll bar.
	 * @param	sizePercent		Percent size of the face
	*/
	public function setSizePercent(sizePercent:Number):Void {
		//If different and valid, proceed
		if (this.sizePercent!=sizePercent && (sizePercent>0 && sizePercent<=100)){
			//Store
			this.sizePercent = sizePercent;
			//Re-set adjustment width, as it'll be re-calculated
			scrollSizeDeltaPercent = 0;
			//Mark dirty, as it'll need a redraw
			dirty = true;
		}
	}
	
	/**
	 * Sets the value of percent shift by which scroll bar face is moved each
	 * time a key/mouse action is initiated.
	*/
	public function setScrollStep(stepPercent:Number):Void {
		//If the specified step percent is different and valid
		if (this.stepPercent != stepPercent && (stepPercent>0 && stepPercent<=100)) {
			//Store
			this.stepPercent = stepPercent;			
			//Calculate
			calculate();
		}
	}
	/**
	* Draws the scroll bar. This call is mandatory to draw the scroll.
	* Also, when size/property of the scroll bar is changed, it has be
	* be followed by a draw() call.
	*/
	public function draw():Void {
		//Proceed with draw, only if something is dirty (has changed)
		if (dirty) {
			//Calculate the new positions.
			calculate();
			//Set co-ordinates of all elements
			setCoords();
			//Draw all the elements.
			//Track first.
			drawTrack();
			//Then face of scroll bar
			drawFace();
			//Set face position
			setFacePos();
			//Buttons on the side of side of scroll bar.
			drawButtons();
			//Update dirty flag
			dirty = false;
			//Set final state 
			setState(state);
		}
	}
	/**
	 * setColor method skins the component by just specifying a single color
	 * for the component. We automatically calculate various other colors for
	 * the component internally. The effect will be applied only by a 
	 * subsequent draw() call.
	*/
	public function setColor(strColor:String):Void {
		if (this.scrollColor != strColor) {
			//Store color
			this.scrollColor = strColor;
			//Create new objects in this.cosmetics to store various colors
			this.cosmetics.bg = new Object();
			this.cosmetics.track = new Object();
			this.cosmetics.trackClick = new Object();
			this.cosmetics.face = new Object();
			this.cosmetics.faceClick = new Object();
			this.cosmetics.button = new Object();
			//Scroll Track Properties
			this.cosmetics.track.color = parseInt(strColor, 16);
			this.cosmetics.track.darkColor = ColorExt.getLightColor(strColor, 0.35);
			this.cosmetics.track.lightColor = ColorExt.getLightColor(strColor, 0.15);
			this.cosmetics.track.borderColor = ColorExt.getDarkColor(strColor, 0.75);
			//Scroll Track Click Properties
			this.cosmetics.trackClick.color = parseInt(strColor, 16);
			this.cosmetics.trackClick.darkColor = ColorExt.getLightColor(strColor, 0.95);
			this.cosmetics.trackClick.lightColor = ColorExt.getLightColor(strColor, 0.75);
			this.cosmetics.trackClick.borderColor = ColorExt.getDarkColor(strColor, 0.75);
			//Button properties
			this.cosmetics.button.arrowColor = ColorExt.getDarkColor(strColor, 0.30);
			this.cosmetics.button.lightColor = ColorExt.getLightColor(strColor, 0.25);
			this.cosmetics.button.darkColor = ColorExt.getDarkColor(strColor, 0.75);
			this.cosmetics.button.borderColor = ColorExt.getDarkColor(strColor, 0.55);
			//Scroll-face properties
			this.cosmetics.face.color = parseInt(strColor, 16);
			this.cosmetics.face.darkColor = ColorExt.getDarkColor(strColor, 0.75);
			this.cosmetics.face.lightColor = ColorExt.getLightColor(strColor, 0.25);
			this.cosmetics.face.borderColor = ColorExt.getDarkColor(strColor, 0.55);
			//Face click properties
			this.cosmetics.faceClick.color = parseInt(strColor, 16);
			this.cosmetics.faceClick.darkColor = ColorExt.getDarkColor(strColor, 0.95);
			this.cosmetics.faceClick.lightColor = ColorExt.getLightColor(strColor, 0.55);
			this.cosmetics.faceClick.borderColor = ColorExt.getDarkColor(strColor, 0.55);
			//Set dirty flag to true, as this needs a redraw to implement.
			dirty = true;
		}
	}
	/**
	 * getState method returns the current state of the component.
	*/
	public function getState():Boolean {
		return state;
	}
	/**
	 * setState method helps set the state (enabled/disabled) for the scroll bar.
	 * By default the component starts in enabled mode. If you set in disabled mode,
	 * the component changes to a greyed out shade with no visible scroll bar face.
	 * Also, the buttons stay inactive.
	*/
	public function setState(state:Boolean):Void {
		//If a drawing is not in progress, and state change is requested
		if (dirty == false && state != this.state) {
			//Store state
			this.state = state;
			//Disable the events
			parentMC.enabled = state;
			faceMC.enabled = state;
			faceClickMC.enabled = state;
			trackMC.enabled = state;
			trackClickMC.enabled = state;
			plusBtnMC.enabled = state;
			minusBtnMC.enabled = state;
			if (state==false){
				plusBtnMC._alpha = DISABLED_STATE_ALPHA;
				minusBtnMC._alpha = DISABLED_STATE_ALPHA;
				trackMC._alpha = DISABLED_STATE_ALPHA;
				trackClickMC._alpha = DISABLED_STATE_ALPHA;
			}else{
				plusBtnMC._alpha = 100;
				minusBtnMC._alpha = 100;
				trackMC._alpha = 100;
				trackClickMC._alpha = 100;
			}
			//Need to take care of listeners
			if (state) {
				Key.addListener(keyL);
				Mouse.addListener(mouseL);
			} else {
				Key.removeListener(keyL);
				Mouse.removeListener(mouseL);
			}
			//Alter visibility of scroll face
			faceMC._visible = state;
		}
	}
	/**
	* Scrolls to a position specified in percent terms w.r.t start of scroll
	* bar. So to move to start, position will be 0. To move to end, the position
	* will be 100-sizePercent.
	*	@param	positionPercent	Percent Position (w.r.t track as 100%) to which
	*							the scroll face should be moved.
	*	@param	raiseEvent		Whether "change" event should be dispatched after
	*							the scrolling is completed.
	*/
	public function scrollTo(percent:Number, raiseEvent:Boolean):Void {
		//Round percent to nearest value to avoid fractional issues
		percent = Math.round(percent);
		var remPercent:Number = Math.round(100-sizePercent);
		//If percent value <0 or >100-sizePercent or same as current position, return
		if (percent<0 || percent>remPercent || percent==startPosPercent){
			return;
		}
		//Rationalize it, if not 0 or 100-sizePercent. This is done to avoid these values
		//getting rationalized, and hence never been able to scrolled-to.
		//E.g., when step is 30, 100 percent will be rationalized to 90.
		//So, the scroll bar will never reach end.
		if (percent!=0 && percent!=remPercent){
			percent = rationalizePosPercent(percent);
		}
		//Store rationalized scroll position
		startPosPercent = percent;		
		//Convert to pixel value
		faceStartX = getPixelPositionFromPercent(percent);
		//Set face position
		setFacePos();
		//Raise change event, if required
		if (raiseEvent){
			//Dispatch the change event
			dispatchChangeEvent(getStartPosition(), getEndPosition(), getPercentSize());
		}
	}
	
	/**
	 * Returns the size of the scroll bar in percent. If the scroll face
	 * was adjusted to increase size (owing to minimum size), it returns
	 * the minimum size.
	*/
	public function getPercentSize():Number{
		return sizePercent-scrollSizeDeltaPercent;
	}
	
	/**
	 * Returns the starting position of scroll bar face. Here, if there is
	 * an adjustment value, we take that into consideration (for ending=100).
	*/
	public function getStartPosition():Number{
		var startPos:Number = startPosPercent;
		//If the face was forcibly adjusted, count for the same in case
		//of ending=100. That is, if the scroll bar is at end of track,
		//set ending value to 100, and starting value to end value minus
		//original width (non-adjusted set).
		if (scrollSizeDeltaPercent!=0 && ((startPos+sizePercent)==100)){
			startPos = startPos + scrollSizeDeltaPercent;
		}
		return startPos;
	}
	
	/**
	 * Returns the end position of scroll bar face.
	*/
	public function getEndPosition():Number{
		return (getStartPosition() + getPercentSize());
	}
	
	/**
	 * show method shows the scroll bar back, if hidden using hide().
	*/
	public function show():Void {
		parentMC._visible = true;
	}
	
	/**
	 * hide method hides a scroll bar.
	*/
	public function hide():Void {
		parentMC._visible = false;
	}
	//------ Private methods -----------//
	/**
	 * Calculates the co-ordinates and other parameters required to draw
	 * scroll bar.
	*/
	private function calculate():Void {
		//Track width
		trackWidth = cmpWidth-2*(btnPadding+btnWidth);
		//Track start position (w.r.t parent container - parentMC)
		trackStartX = btnPadding+btnWidth;
		//Track end position  (w.r.t parent container - parentMC)
		trackEndX = trackStartX+trackWidth;
		//Width of the scroll face.
		faceWidth = (sizePercent/100)*trackWidth;
		//Adjust faceWidth for minimum value.
		if (faceWidth<SCROLL_MIN_WIDTH) {
			//Conver the pixel width into %, and round it
			var minWidthPercent:Number = Math.ceil((SCROLL_MIN_WIDTH/trackWidth)*100);
			//Set max to 100
			minWidthPercent = Math.min(minWidthPercent, 100);
			//Set the face width to minium value
			faceWidth = (minWidthPercent/100)*trackWidth;
			//Store adjustment
			scrollSizeDeltaPercent = minWidthPercent-sizePercent;
			//Store new face width
			sizePercent = minWidthPercent;
		}
		//Set the scroll start and end X position w.r.t parentMC
		startPosPixel = trackStartX;
		endPosPixel = trackEndX-faceWidth;
		//Round off the scroll position percent initially given so as to ensure
		//exact multiplication of step.
		startPosPercent = rationalizePosPercent(startPosPercent);
		//Position of scroll face, based on starting position value provided.
		faceStartX = getPixelPositionFromPercent(startPosPercent);				
	}
	/**
	 * Rationalizes the position percent of the scroll face to ensure that it
	 * is a multiple of stepPercent. Restricts between 0 to (100-sizePercent)
	 * @param	percent		Position (in percent) of the scroll face.
	 * @returns				Rationalized value of position percent.
	*/
	private function rationalizePosPercent(percent:Number):Number {
		//Round to the nearest scroll step.
		percent = stepPercent*Math.round(percent/stepPercent);
		//If it's <0, set at 0
		percent = Math.max(0, percent);
		//If it's >100-sizePercent, set at 100
		percent = Math.min((100-sizePercent), percent);
		//Return
		return percent;
	}
	/** 
	 * Returns the pixel position for the scroll face, based on the percent
	 * position specified.
	 * @param	percent		Position (in percent) for the scroll face
	 * @return				Position (in pixels) that maps to the specified percent position
	*/
	private function getPixelPositionFromPercent(percent:Number):Number {
		//If position percent exceeds 100% in calibrated scale, set to 100%
		if (percent>(100-sizePercent)){
			//100-sizePercent is 100% in calibrated mode
			percent = 100-sizePercent;
		}
		return (startPosPixel+(percent/100)*trackWidth);
	}
	
	/**
	 * Based on the x-position of scroll face, returns the starting position percent 
	 * @param	rationalize		Whether to rationalize the percent position returned.
	*/
	private function getPercentPosFromFace(rationalize:Boolean):Number {
		//Get position of faceX
		var faceX:Number = faceMC._x;
		var deltaX:Number = faceX-startPosPixel;
		var percentPos:Number = (deltaX/trackWidth)*100;
		//Rationalize if required 
		if (rationalize){
			percentPos = rationalizePosPercent(percentPos);
		}
		//Return rationalized percent position
		return percentPos;
	}
	/**
	 * Scrolls by the specific step either in forward or backward direction.
	 *	@param	forward		Whether to scroll in forward direction.
	*/
	private function scrollByStepPercent(forward:Boolean):Void {
		var percent:Number;
		//Add/deduct the value
		if (forward){
			percent = startPosPercent + stepPercent;
		}else{
			percent = startPosPercent - stepPercent;
			//Tweak: Since we round off the percent in scrollTo(), a step of 0.5
			//will be rounded towards positive, thereby never scrolling back.
			//So, deduct 0.001 from the same
			if (stepPercent==0.5){
				percent = percent - 0.001;
			}
		}
		//Restrict range to 0<=percent<=100-sizePercent
		percent = Math.max(0, percent);
		percent = Math.min((100-sizePercent), percent);
		//Scroll to that position
		scrollTo(percent, true);
	}
	/**
	 * Sets the event handlers for all movie clips.
	*/
	private function setEvents():Void {
		//Get a reference to self class.
		var classRef = this;
		//Store starting position, for comparison during scroll
		var scrollStartX:Number = this.faceMC._x;
		//Define the drag event for the face
		faceMC.onPress = function() {
			//Dispatch the click event
			classRef.dispatchEvent({type:"click", target:classRef});
			//Hide the face movie clip
			this._alpha = 0;
			//Show the click effect movie clip
			classRef.faceClickMC._visible = true;
			//Set the dragging for click handler movie clip
			classRef.faceClickMC.startDrag(false, classRef.trackStartX, 0, classRef.trackEndX-classRef.faceWidth+1, 0);
			//Store last starting position for checking change in state during drag
			var currStartPos:Number = classRef.startPosPercent;
			classRef.faceClickMC.onEnterFrame = function() {
				//Update the x-position w.r.t click handler				
				classRef.faceMC._x = this._x;
				//Record the starting position of the face, when dragging has started
				var dragStartPos:Number = classRef.getPercentPosFromFace(true);
				//If the new dragged-to position is different from original starting position (before drag)
				if (dragStartPos!=currStartPos){
					//Calculate the drag end position by adding size
					var dragEndPos:Number = dragStartPos + classRef.getPercentSize();
					//Dispatch change event with rationalized & calibrated percent position.
					classRef.dispatchChangeEvent(dragStartPos, dragEndPos, classRef.getPercentSize());
					//Update delta register for next change. This ensures that even if scroll
					//position was changed from O (original) to N1 (new 1) and then back to O, the
					//change event is still dispatched.
					currStartPos = dragStartPos;
				}
			};
		};
		faceMC.onRelease = faceMC.onReleaseOutside=function () {
			//Stop dragging
			classRef.faceClickMC.stopDrag();
			//Delete enterFrame
			delete classRef.faceClickMC.onEnterFrame;			
			//Show the face back
			this._alpha = 100;
			//Hide the click movie
			classRef.faceClickMC._visible = false;
			classRef.scrollTo(classRef.getPercentPosFromFace(false), true);
		};
		//Events for button
		//Left Button
		minusBtnMC.onPress = Delegate.create(this, minusButtonOnPress);
		minusBtnMC.onRelease = minusBtnMC.onReleaseOutside=function () {
			//Clear the interval
			clearInterval(classRef.btnInterval);			
		};
		//Right Button
		plusBtnMC.onPress = Delegate.create(this, plusButtonOnPress);
		plusBtnMC.onRelease = plusBtnMC.onReleaseOutside=function () {
			//Clear the interval
			clearInterval(classRef.btnInterval);
		};
		//Click events for track
		//We need to delegate the onPress event to another member function, as we've
		//to use setInterval within that, which in turn calls another member function.
		//trackMC.onPress = Delegate.create(this, trackOnPress);
		trackMC.onPress = Delegate.create(this, trackOnPress);
		trackMC.onRelease = trackMC.onReleaseOutside=function () {
			//Show the track MC
			this._alpha = 100;
			//Hide the track click MC
			classRef.trackClickMC._visible = false;
			//Clear interval			
			clearInterval(classRef.trackInterval);
		};
		//Add Key Listeners to enable key movement
		//We define the onKeyDown event for the key listener.
		keyL.onKeyDown = function() {
			if (classRef.parentMC.hitTest(_root._xmouse, _root._ymouse)) {
				//if the left key is pressed
				if (Key.isDown(Key.LEFT)) {
					//Backward Shift
					classRef.scrollByStepPercent(false);
				} else if (Key.isDown(Key.RIGHT)) {
					//Forward Shift
					classRef.scrollByStepPercent(true);
				}
			}
		};
		//Register the key listener
		Key.addListener(keyL);
		//Add mouse listeners to enable mouse wheel movement
		//Define the onMouseWheel Event for the scroll
		mouseL.onMouseWheel = function(delta) {
			if (classRef.parentMC.hitTest(_root._xmouse, _root._ymouse)) {
				if (delta<0) {
					//Forward Shift
					classRef.scrollByStepPercent(true);
				} else {
					//Backward Shift
					classRef.scrollByStepPercent(false);
				}
			}
		};
		//Register the listener for the scroll
		Mouse.addListener(mouseL);
	}
	/**
	 * Queues and dispatches the change event, when the scroll face position is changed.
	 * Internally, it dispatches events only if values have changed
	*/
	private function dispatchChangeEvent(startPos:Number, endPos:Number, size:Number):Void{
		//If the last dispatched start and end position are different, only then proceed
		if (lastDsptStartPos!=startPos || lastDsptEndPos != endPos){
			startPos = Math.max(0,startPos);
			endPos = Math.min(100, endPos);
			size = endPos-startPos;
			//Dispatch event
			dispatchEvent({type:"change", target:this, start:startPos, end:endPos, size:size});
			//Store state
			lastDsptStartPos = startPos;
			lastDsptEndPos = endPos
		}
	}
	
	/**
	 * Sets the co-ordinates of child elements.
	*/
	private function setCoords():Void {
		//Set track position.
		trackMC._x = btnWidth+btnPadding;
		trackClickMC._x = trackMC._x;
		//Set button position
		minusBtnMC._x = 0;
		plusBtnMC._x = btnWidth+2*btnPadding+trackWidth;
		//NOTE: Face position is set by setFacePos();
	}
	
	/**
	 * Sets the position for face movie clips, based on calculated values.
	*/
	private function setFacePos():Void {
		//Set face position
		faceMC._x = faceStartX;
		faceClickMC._x = faceStartX;
	}	
	//---------- Drawing functions --------------//
	/**
	 * drawTrack method does the job of drawing the scroll background & track.
	*/
	private function drawTrack():Void {
		//Clear the track movie clip of any previous drawings
		trackMC.clear();
		trackClickMC.clear();
		//Set line style
		trackMC.lineStyle(0, cosmetics.track.borderColor, 100);
		//Set fill gradient style
		trackMC.beginGradientFill("linear", [cosmetics.track.darkColor, cosmetics.track.lightColor], [100, 100], [127, 255], {matrixType:"box", x:0, y:0, w:trackWidth, h:cmpHeight, r:(90/180)*Math.PI});
		//Draw the rectangle
		drawRectangle(trackMC, 0, 0, trackWidth, cmpHeight);
		//End Fill
		trackMC.endFill();
		//Draw the track click
		//Set line style
		trackClickMC.lineStyle(0, cosmetics.trackClick.borderColor, 100);
		//Set fill gradient style
		trackClickMC.beginGradientFill("linear", [cosmetics.trackClick.darkColor, cosmetics.trackClick.lightColor], [100, 100], [127, 255], {matrixType:"box", x:0, y:0, w:trackWidth, h:cmpHeight, r:(90/180)*Math.PI});
		//Draw the rectangle
		drawRectangle(trackClickMC, 0, 0, trackWidth, cmpHeight);
		//End Fill
		trackClickMC.endFill();
	}
	/**
	 * drawFace method does the job of drawing the scroll face & face click.
	*/
	private function drawFace():Void {
		//Clear any previous drawings
		faceMC.clear();
		faceClickMC.clear();
		//Set line style
		faceMC.lineStyle(0, cosmetics.face.borderColor, 100);
		//Set fill gradient style
		faceMC.beginGradientFill("linear", [cosmetics.face.lightColor, cosmetics.face.darkColor], [100, 100], [127, 255], {matrixType:"box", x:0, y:0, w:trackWidth, h:cmpHeight, r:(90/180)*Math.PI});
		//Draw the rectangle
		drawRectangle(faceMC, 0, 0, faceWidth, cmpHeight);
		//End Fill
		faceMC.endFill();
		//Draw the face lines
		drawFaceLines(faceMC, 0, 0);
		//Also create the face click clip (for click effect)
		//Set line style
		faceClickMC.lineStyle(0, cosmetics.faceClick.borderColor, 100);
		//Set fill gradient style
		faceClickMC.beginGradientFill("linear", [cosmetics.faceClick.darkColor, cosmetics.faceClick.lightColor], [100, 100], [127, 255], {matrixType:"box", x:0, y:0, w:trackWidth, h:cmpHeight, r:(90/180)*Math.PI});
		//Draw the rectangle
		drawRectangle(faceClickMC, 0, 0, faceWidth, cmpHeight);
		//End Fill
		faceClickMC.endFill();
		//Draw the face lines
		drawFaceLines(faceClickMC, 1, 1);
	}
	/**
	 * drawFaceLines draws the 4 vertical (cosmetic) lines on the scroll face
	 *	@param	mc		In which movie clip to draw?
	 *	@param	offsetX	Offset X from center for drawing
	 *	@param	offsetY	Offest Y from center for drawing
	*/
	private function drawFaceLines(mc:MovieClip, offsetX:Number, offsetY:Number):Void {
		//Draw only if face width > 12 pixels
		if (faceWidth>12){
			//x-distance between each line
			var xGap:Number = 3;
			//Height of each line
			var hLine:Number = 6;
			//Start X, start Y, end Y
			var sX:Number = Math.round(this.faceWidth/2+offsetX)-5;
			var sY:Number = (this.cmpHeight-hLine)/2+offsetY;
			var eY:Number = sY+hLine;
			//Set the linestyle
			mc.lineStyle(1, this.cosmetics.face.darkColor, 100, true, "none");
			//Now draw the 4 lines
			for (var i:Number = 0; i<=3; i++) {
				mc.moveTo(sX+(xGap*i), sY);
				mc.lineTo(sX+(xGap*i), eY);
			}
		}
	}
	/**
	 * drawButtons method does the job of drawing the buttons.
	*/
	private function drawButtons():Void {
		//Clear of any previous drawing
		plusBtnMC.clear();
		minusBtnMC.clear();
		//Draw minus button first
		minusBtnMC.lineStyle(0, cosmetics.button.borderColor, 100);
		//Set fill gradient style
		minusBtnMC.beginGradientFill("linear", [cosmetics.button.lightColor, cosmetics.button.darkColor], [100, 100], [127, 255], {matrixType:"box", x:0, y:0, w:trackWidth, h:cmpHeight, r:(90/180)*Math.PI});
		//Draw the rectangle
		drawRectangle(minusBtnMC, 0, 0, btnWidth, cmpHeight);
		//End Fill
		minusBtnMC.endFill();
		//Draw Plus Button now
		plusBtnMC.lineStyle(0, cosmetics.button.borderColor, 100);
		//Set fill gradient style
		plusBtnMC.beginGradientFill("linear", [cosmetics.button.lightColor, cosmetics.button.darkColor], [100, 100], [127, 255], {matrixType:"box", x:0, y:0, w:trackWidth, h:cmpHeight, r:(90/180)*Math.PI});
		//Draw the rectangle
		drawRectangle(plusBtnMC, 0, 0, btnWidth, cmpHeight);
		//End Fill
		plusBtnMC.endFill();
		//Draw the arrows
		var cX:Number = btnWidth/2;
		var cY:Number = cmpHeight/2;
		var h:Number = cmpHeight/4;
		//Draw the left arrow
		minusBtnMC.lineStyle(2, cosmetics.button.arrowColor, 100);
		minusBtnMC.moveTo(cX-4, cY);
		minusBtnMC.lineTo(cX+2, cY-h);
		minusBtnMC.moveTo(cX-4, cY);
		minusBtnMC.lineTo(cX+2, cY+h);
		//Draw the right arrow
		plusBtnMC.lineStyle(2, this.cosmetics.button.arrowColor, 100);
		plusBtnMC.moveTo(cX+4, cY);
		plusBtnMC.lineTo(cX-2, cY-h);
		plusBtnMC.moveTo(cX+4, cY);
		plusBtnMC.lineTo(cX-2, cY+h);
	}
	//----------- Event Handlers -------------//
	/**
	 * minusButtonOnPress is a delegated method which is invoked, when the user presses
	 * the left button.
	*/
	private function minusButtonOnPress():Void {
		//Dispatch the click event
		dispatchEvent({type:"click", target:this});
		btnInterval = setInterval(Delegate.create(this, scrollByStepPercent), 50, false);
	}
	/**
	 * plusButtonOnPress is a delegated method which is invoked, when the user presses
	 * the right button.
	*/
	private function plusButtonOnPress():Void {
		//Dispatch the click event
		dispatchEvent({type:"click", target:this});
		btnInterval = setInterval(Delegate.create(this, scrollByStepPercent), 50, true);
	}
	/**
	 * trackOnPress is a delegated method which is invoked, when the user presses
	 * the scroll track.
	*/
	private function trackOnPress():Void {
		//Dispatch the click event
		dispatchEvent({type:"click", target:this});
		//Hide the track MC
		trackMC._alpha = 0;
		//Show the track click MC
		trackClickMC._visible = true;
		//Set an interval to scroll the content in periodic interval
		trackInterval = setInterval(scrollUsingTrack, 50, this, trackMC._xmouse);
	}
	
	/**
	 * scrollUsingTrack method is called when the user clicks
	 * on the track.
	*/
	private function scrollUsingTrack(classRef, clickXPos:Number):Void {
		//First check where the user clicked on the track. Left or right or
		//scroll face. Now, if clickXPos is on the left side of the face movie clip
		//we need to scroll to left. Else, we need to scroll to right
		if (clickXPos<(classRef.faceMC._x-classRef.trackStartX)) {
			//Scroll backwards
			classRef.scrollByStepPercent(false);
		} else if (clickXPos>(classRef.faceMC._x+classRef.faceWidth-classRef.trackStartX)) {
			//Scroll forward
			classRef.scrollByStepPercent(true);
		}
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
	 * drawRectangle method draws a rectangle in the given movie clip.
	 *	@param	mc	Movieclip in which we've to draw rectangle.
	 *	@param	x	Top Left X Position from where we've to start drawing.
	 *	@param	y	Top Left Y Position from where we've to start drawing.
	 *	@param	w	Width of the rectangle.
	 *	@param	h	Height of the rectangle.
	 *	@return		Nothing
	*/
	private function drawRectangle(mc:MovieClip, x:Number, y:Number, w:Number, h:Number):Void {
		mc.moveTo(x, y);
		mc.lineTo(x+w, y);
		mc.lineTo(x+w, y+h);
		mc.lineTo(x, y+h);
		mc.lineTo(x, y);
	}
	/** 
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance. It removes all listeners and removes all movie clips that
	 * are created as a part of scroll bar.
	*/
	public function destroy():Void {
		//Collect garbage
		delete cosmetics;
		//Clear any intervals
		clearInterval(trackInterval);
		clearInterval(btnInterval);
		//Remove all listeners
		Key.removeListener(keyL);
		Mouse.removeListener(mouseL);
		//Remove all movie clips
		parentMC.removeMovieClip();
	}
}
