
/**
 * @class HorIndexScrollBar
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * Low foot print & no-frills horizontal Scroll Bar class that scrolls on index
 * values. This scroll bar doesn't actually scroll any movie clip or display object.
 * Instead it just scrolls itself and returns its position and size to control code,
 * which can then adjust the display.
 * This scroll bar is useful when you've a sequentially arranged index of items which
 * you've to scroll through a scroll bar. The base index (first item) is 1 and the 
 * rest of items assume indexes sequentially. Steps can be defined to skip x elements
 * during scroll (in case of large datasets).
 * You can change the cosmetics of this scroll bar using a single color.
 * To use a scroll bar, just instantiate it with the properties and call
 * draw().
 * To set color, use the setColor() method and then draw().
 * Any change needs to be followed by draw() call to take effect.
 * If you want to set the X and Y Position of scroll bar, you can directly
 * do in your parent code, by setting the x/y position of parent movie clip.
 * Mappings: Total number of items = total number of data items to be displayed
 * If all items are to be shown in a single pane (without scroll bar having space to be
 * dragged), set numPaneItems=total Items - 1 (as it's shift) 											  
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
class com.fusioncharts.components.HorIndexScrollBar extends MovieClip {
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
	//Total number of items in the universe (w.r.t scroll) - based on this the
	//selection of subset of items results in scrolling
	private var totalItemsCount:Number;
	//Start and end item index
	private var startItemIndex:Number, endItemIndex:Number;
	//Number of items that the size of scroll face represents
	private var numPaneItems:Number;
	//Scroll step
	private var stepItems:Number;
	//Adjusted number of items on scroll bar face width for scroll
	private var scrollSizeDeltaItems:Number;
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
	//State for last dispatched start and end positioned items
	private var lastDsptStartItem:Number, lastDsptEndItem:Number;
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
	//Pixel width required per item
	private var perItemPixel:Number;
	//Scroll pixel shift based on step value
	private var stepPixelShift:Number;
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
	function HorIndexScrollBar(parentMC:MovieClip) {
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
		startItemIndex = 1;
		numPaneItems = 1;
		stepItems = 1;
		btnWidth = 16;
		btnPadding = 0;
		lastDsptStartItem =0;
		lastDsptEndItem = 0;
		//Initially, set delta to 0
		scrollSizeDeltaItems = 0;
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
	 * Sets the total number of items that this scroll bar is supposed to scroll.
	 * Each scroll view will be a subset of these items. Post constructor, this
	 * should be the first function call to be able to validate other values.
	 * @param	numItems		Total number of items. 
	*/
	public function setTotalItemsCount(numItems:Number):Void{
		//Items cannot be a fraction
		numItems = int(numItems);
		//Proceed, if valid & different		
		if (numItems>0 && numItems!=totalItemsCount) {
			//Store
			totalItemsCount = numItems;
			//Set dirty flag to true, so that it can be picked in next redraw
			dirty = true;
		}
	}
	
	/**
	* Sets the starting item index for scroll bar. The maximum value of this 
	* can be totalItemsCount-1 (or totalItemsCount-stepItems)
	* @param	posPercent		Index of the starting item, from where the scroll
	*							bar should start.
	*/
	public function setStartIndex(index:Number):Void {
		//Index cannot be a fraction
		index = int(index);
		//Proceed, if valid & different		
		if (index>0 && startItemIndex != index) {
			//Store
			startItemIndex = index;
			//Set dirty flag to true, so that it can be picked in next redraw
			//(as this function is for only setting the value initially).
			dirty = true;
		}
	}
	
	/**
	 * Sets (or later, re-sets) the size of scroll bar face. This affects the number
	 * of items the scroll bar shows in one single visible pane. So in case of line
	 * chart, the number of pane items = number of anchors -1 (i.e., number of shifts).
	 * In case of a column chart, it'll be number of categories (and not number of columns).
	 * @param	numItems		Number of items that can be shown in one visible panel
	*/
	public function setPaneItemsCount(numItems:Number):Void {
		//Number of items cannot be a fraction
		numItems = int(numItems);
		//If different and valid, proceed
		if (numPaneItems!=numItems && (numItems>0)){
			//Store
			numPaneItems = numItems;
			//Re-set adjustment items count, as it'll be re-calculated
			scrollSizeDeltaItems = 0;
			//Mark dirty, as it'll need a redraw
			dirty = true;
		}
	}
	
	/**
	 * Sets the number of items which will be skipped in scroll pane each time a 
	 * scroll action is initiated. This is again equal to number of shifts or
	 * equivalent to number of items shifted from start index 1.
	*/
	public function setScrollStep(step:Number):Void {
		//Step cannot be fraction
		step = int(step);
		//If the specified step percent is different and valid
		if (stepItems != step && (stepItems>0)) {
			//Store
			stepItems = step;
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
	* Scrolls to a specified index posotion terms w.r.t start of scroll
	* bar. So to move to start, position will be 1. To move to end, the position
	* will be totalItems-numPaneItems.
	*	@param	index			Index Position (w.r.t start position as 1) to which
	*							the scroll face should be moved.
	*	@param	raiseEvent		Whether "change" event should be dispatched after
	*							the scrolling is completed.
	*/
	public function scrollTo(index:Number, raiseEvent:Boolean):Void {
		//Round index to nearest value to avoid fractional issues
		index = Math.round(index);
		//Calculate last scroll block size
		var lastBlockIndex:Number = (totalItemsCount-numPaneItems);
		//If index value <1 or >endBlockSize or same as current position, return
		if (index<1 || index>lastBlockIndex || index==startItemIndex){
			return;
		}
		//Rationalize inde
		index = rationalizeIndex(index);
		//Store rationalized scroll start index
		startItemIndex = index;
		//Convert to pixel value
		faceStartX = getPixelPositionFromIndex(index);		
		//Set face position
		setFacePos();
		//Raise change event, if required
		if (raiseEvent){
			//Dispatch the change event
			dispatchChangeEvent(getStartIndex(), getEndIndex(), getPaneSize());
		}
	}
	/**
	 * Returns the size of the scroll bar in number of items. If the scroll face
	 * was adjusted to increase size (owing to minimum size), it returns
	 * the minimum size.
	*/
	public function getPaneSize():Number{
		return numPaneItems-scrollSizeDeltaItems;
	}
	/**
	 * Returns the starting index of scroll bar face. Here, if there is
	 * an adjustment value, we take that into consideration.
	*/
	public function getStartIndex():Number{
		var startIndex:Number = startItemIndex;
		//If the face was forcibly adjusted, count for the same in case
		//of end block. 
		if (scrollSizeDeltaItems!=0 && ((startIndex+numPaneItems)>=totalItemsCount)){
			startIndex = startIndex + scrollSizeDeltaItems;
		}
		return startIndex;
	}
	/**
	 * Returns the end position of scroll bar face.
	*/
	public function getEndIndex():Number{
		return (getStartIndex() + getPaneSize());
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
		//Validation: If numPaneItems is more than totalItemsCount, set to totalItemsCount-1
		if (numPaneItems>=totalItemsCount){
			//We set to totalItemsCount-1 as it represents shifts (and not exact points)
			numPaneItems = totalItemsCount-1;
		}
		//Validation: If step is more than totalItemsCount, set back to 1
		if (stepItems>totalItemsCount){
			stepItems = 1;
		}
		//Validation: If startIndex is more than totalItemsCount-numPaneItems, set to end
		if (startItemIndex>(totalItemsCount-numPaneItems)){
			startItemIndex = (totalItemsCount-numPaneItems);
		}
		//Track width
		trackWidth = cmpWidth-2*(btnPadding+btnWidth);
		//Track start position (w.r.t parent container - parentMC)
		trackStartX = btnPadding+btnWidth;
		//Track end position  (w.r.t parent container - parentMC)
		trackEndX = trackStartX+trackWidth;
		//Shift of x-position required for each item. The shift is the distance
		//between two items. So for x items, x-1 slots (or shifts) are required
		//to move from 1st item to last one.
		perItemPixel = (trackWidth/(totalItemsCount-1));
		//Shift required based on step value
		stepPixelShift = perItemPixel*stepItems;
		//Width of the scroll face.
		faceWidth = numPaneItems*perItemPixel;
		//Adjust faceWidth for minimum value.
		if (faceWidth<SCROLL_MIN_WIDTH) {
			//Conver the pixel width into number of items, and round it
			var minWidthItems:Number = Math.ceil((SCROLL_MIN_WIDTH/perItemPixel));
			//Set max to totalItemsCount
			minWidthItems = Math.min(minWidthItems, totalItemsCount);
			//Set the face width to minimum value
			faceWidth = (minWidthItems/totalItemsCount)*trackWidth;
			//Store adjustment
			scrollSizeDeltaItems = minWidthItems-numPaneItems;
			//Store new face width
			numPaneItems = minWidthItems;
		}		
		//Set the scroll start and end X position w.r.t parentMC
		startPosPixel = trackStartX;
		endPosPixel = trackEndX-faceWidth;
		//Position of scroll face, based on starting position value provided.
		faceStartX = getPixelPositionFromIndex(startItemIndex);				
	}
	/**
	 * Rationalizes the starting index of the scroll face to ensure that it
	 * is a multiple of stepItems. Restricts between 1 to total Items-numPaneItems
	 * @param	index		Starting Index Position of the scroll face.
	 * @returns				Rationalized value of position .
	*/
	private function rationalizeIndex(index:Number):Number {
		//Rationalize it, if not 1 or endBlockSize. This is done to avoid these values
		//getting rationalized, and hence never been able to scrolled-to.
		//E.g., when step is 30, 100 index will be rationalized to 90.
		//So, the scroll bar will never reach end.
		if (index!=1 && index!=((totalItemsCount-numPaneItems))){
			//Round to the nearest scroll step.
			//Add 1, as base position for starting index is 1
			//Deduct 1 from index when dividing by stepitems, as base is 1
			index = 1 + Math.round((index-1)/stepItems) * stepItems;
			//If it's <1, set at 1
			index = Math.max(1, index);
			//If it's >(totalItemsCount-numPaneItems), set at that
			index = Math.min((totalItemsCount-numPaneItems), index);
		}
		//Return
		return index;
	}
	/** 
	 * Returns the pixel position for the scroll face, based on the item index
	 * @param	index		Position (in index) for the scroll face
	 * @return				Position (in pixels) that maps to the specified item index position
	*/
	private function getPixelPositionFromIndex(index:Number):Number {
		//If index+pane size>total items, set to max
		if (index>(totalItemsCount-numPaneItems)){
			//totalItemsCount-numPaneItems is end position in calibrated mode
			index = totalItemsCount-numPaneItems;
		}
		return (startPosPixel+((index-1)*perItemPixel));
	}
	/**
	 * Based on the x-position of scroll face, returns the starting index position
	 * @param	rationalize		Whether to rationalize the index position returned.
	*/
	private function getIndexPositionFromPixel(rationalize:Boolean):Number {
		//Get position of faceX
		var faceX:Number = faceMC._x;
		var deltaX:Number = faceX-startPosPixel;
		//Index position based on delta
		var indexPos:Number = Math.round(deltaX/perItemPixel)+1;
		//Round to previous one if it's 0.5
		if (indexPos-Math.floor(indexPos)==0.5){
			indexPos--;
		}
		//Rationalize if required 
		if (rationalize){
			indexPos = rationalizeIndex(indexPos);
		}
		//Return rationalized index position
		return indexPos;
	}	
	/**
	 * Scrolls by the specific step either in forward or backward direction.
	 *	@param	forward		Whether to scroll in forward direction.
	*/
	private function scrollByStep(forward:Boolean):Void {
		var percent:Number;
		//Replicate index
		var index:Number = startItemIndex;
		//Add/deduct the value
		if (forward){
			index = index + stepItems;
		}else{
			index = index - stepItems;
		}
		//Restrict range to 1<=startItemIndex<=(totalItemsCount-numPaneItems)
		index = Math.max(1, index);
		index = Math.min((totalItemsCount-numPaneItems), index);
		//Scroll to that position
		scrollTo(index, true);
		//NOTE: startItemIndex is not updated, as it's done in scrollTo.
		//This is done to allow a valid comparison between current index
		//and new index to be set. 
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
			//Store last starting index for checking change in state during drag
			var currStartPos:Number = classRef.startItemIndex;
			classRef.faceClickMC.onEnterFrame = function() {
				//Update the x-position w.r.t click handler				
				classRef.faceMC._x = this._x;
				//Record the starting position of the face, when dragging has started
				var dragStartPos:Number = classRef.getIndexPositionFromPixel(true);
				//If the new dragged-to position is different from original starting position (before drag)
				if (dragStartPos!=currStartPos){
					//Calculate the drag end position by adding size
					var dragEndPos:Number = dragStartPos + classRef.getPaneSize();
					//Dispatch change event with rationalized & calibrated percent position.
					classRef.dispatchChangeEvent(dragStartPos, dragEndPos, classRef.getPaneSize());
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
			//Set final scroll position
			//Force the shift of face, as scrollTo wouldn't validate new_index==existing_index
			classRef.faceMC._x = classRef.getPixelPositionFromIndex(classRef.getIndexPositionFromPixel(true));
			//To update vars and dispatch, invoke scrollTo
			classRef.scrollTo(classRef.getIndexPositionFromPixel(false), true);
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
					classRef.scrollByStep(false);
				} else if (Key.isDown(Key.RIGHT)) {
					//Forward Shift
					classRef.scrollByStep(true);
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
					classRef.scrollByStep(true);
				} else {
					//Backward Shift
					classRef.scrollByStep(false);
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
		if (lastDsptStartItem!=startPos || lastDsptEndItem != endPos){
			//Validate start and end positions
			startPos = Math.max(1, startPos);
			endPos = Math.min(totalItemsCount, endPos);
			size = endPos-startPos;
			//Dispatch event
			dispatchEvent({type:"change", target:this, start:startPos, end:endPos, size:size});
			//Store state
			lastDsptStartItem = startPos;
			lastDsptEndItem = endPos
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
		btnInterval = setInterval(Delegate.create(this, scrollByStep), 50, false);
	}	
	/**
	 * plusButtonOnPress is a delegated method which is invoked, when the user presses
	 * the right button.
	*/
	private function plusButtonOnPress():Void {
		//Dispatch the click event
		dispatchEvent({type:"click", target:this});
		btnInterval = setInterval(Delegate.create(this, scrollByStep), 50, true);
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
			classRef.scrollByStep(false);
		} else if (clickXPos>(classRef.faceMC._x+classRef.faceWidth-classRef.trackStartX)) {
			//Scroll forward
			classRef.scrollByStep(true);
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
