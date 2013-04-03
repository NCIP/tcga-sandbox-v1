/**
 * @class FCHScrollPane
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * Horizontal Scroll class
*/
import mx.transitions.Tween;
import mx.transitions.easing.*;
//Extensions
import com.fusioncharts.extensions.ColorExt;
import mx.events.EventDispatcher;
//Class
class com.fusioncharts.helper.FCHScrollPane implements com.fusioncharts.helper.IComponent {
	//Target MC for the scroller
	private var target:MovieClip;
	private var widthTarget:MovieClip;
	//Masking Movie
	private var mask_mc:MovieClip;
	//Hit MC for the click event in the slider
	private var Hit_mc:MovieClip;
	//MovieClip for the Scroller and the container
	private var scroller:MovieClip;
	private var scrollerCont:MovieClip;
	//Style object 
	private var styles:Object;
	//MovieClip Object which holds the property related to the HScroll
	private var movieclip:Object;
	//Listener object for the Mouse
	private var mouseListener:Object;
	private var keyListener:Object;
	//X and Y position of the target MC
	private var tX:Number;
	private var tY:Number;
	//Content Height and Width
	private var contentWidth:Number;
	private var contentHeight:Number;
	//Pane Width and Height
	private var paneWidth:Number;
	private var paneHeight:Number;
	//Constructor function
	/**
	 * Here, we initialize the ScrollPane object.
	 *	@param	target	Movie clip in which we've to create the HScrollerPane
	 *	@param	scrollMC	Movie clip of the HScrollerPane
	 *	@param	maskMC	Movie clip for the masking the HScrollPane
	 *	@param	style	Style object for the cosmetic look of the scroll Pane
	 *	@param	props	props object stores the pane width and height
	 *	@param	widthTargetMC	widthTargetMC is the MovieClip which will be used for maximum width reference in the ScrollPane
	*/
	function FCHScrollPane(target:MovieClip, scrollMC:MovieClip, maskMC:MovieClip, style:Object, props:Object, widthTargetMC:MovieClip) {
		//Set the target MC
		this.target = target;
		this.scrollerCont = scrollMC;
		this.mask_mc = maskMC;
		//Set the pane Width and Height
		this.paneWidth = props.w;
		this.paneHeight = props.h;
		this.widthTarget = widthTargetMC;
		//Set the style for the Slider
		this.setStyles(style);
		//Set up the properties of the slider
		this.defineProps();
		//Store the width and height of the pane in a local variable for reference
		var w:Number = this.paneWidth;
		var h:Number = this.paneHeight;
		//Draw a hidden rectangle in the MC for the Masking of the Content MC
		this.drawRectangle(this.mask_mc, 0, 0, w, h, "0xFFFFFF", 0);
		this.target.setMask(this.mask_mc);
		//Set the position of the masking movie
		this.mask_mc._x = this.tX;
		this.mask_mc._y = this.tY;
		//Check whether we need a scrollpane or not
		if (this.checkDir()) {
			//Set up the elements of the slider MovieClip
			this.draw();
		}
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
	 * setStyles method sets the style object either from parameter set or the default
	 *	@param	style	Style object for the cosmetic look of the scroll Pane
	 */
	private function setStyles(style:Object):Void {
		//If we do not have the style defined from the Object we take the default Style
		if (style != undefined) {
			this.styles = style;
		} else {
			this.defaultStyle();
		}
		// Set the constant for the height of the slider
		this.styles.Bg.h = Number(20);
		this.styles.Track.h = Number(15);
		this.styles.Btn.bg.h = Number(15);
		this.styles.Btn.arrow.h = Number(14);
		this.styles.Slider.h = Number(15);
		// Set the constant for the width of the slider
		this.styles.Bg.w = Number(20);
		this.styles.Track.w = Number(15);
		this.styles.Btn.bg.w = Number(8);
		this.styles.Btn.arrow.w = Number(14);
		this.styles.Slider.w = Number(10);
	}
	/**
	 * defaultStyle method defines the default style to use when we do not have the param in Constructor
	 */
	private function defaultStyle():Void {
		//Create all the objects
		this.styles = new Object();
		this.styles.Bg = new Object();
		this.styles.Track = new Object();
		this.styles.Btn = new Object();
		this.styles.Slider = new Object();
		this.styles.Bg.alpha = Number(0);
		this.styles.Bg.borderThickness = Number(0);
		this.styles.Bg.borderColor = "0xcccccc";
		this.styles.Bg.borderAlpha = Number(0);
		this.styles.Bg.y = Number(0);
		this.styles.Bg.x = Number(-1);
		//Track
		this.styles.Track.alpha = Number(100);
		this.styles.Track.borderThickness = Number(0);
		this.styles.Track.borderAlpha = Number(100);
		this.styles.Track.x = Number(15);
		this.styles.Track.y = Number(2);
		// Btn bg
		this.styles.Btn.bg = new Object();
		this.styles.Btn.bg.h = Number(8);
		this.styles.Btn.bg.x = Number(2);
		//Slider
		this.styles.Slider.alpha = Number(100);
		this.styles.Slider.borderThickness = Number(0);
		this.styles.Slider.borderAlpha = Number(100);
		this.styles.Slider.x = Number(15);
		this.styles.Slider.y = Number(2);
		// Btn arrow
		this.styles.Btn.arrow = new Object();
		this.styles.Btn.arrow.h = Number(12);
		this.styles.Btn.arrow.alpha = Number(100);
		this.styles.Btn.arrow.cornerRadius = Number(0);
		this.styles.Btn.arrow.x = Number(1);
		this.styles.Btn.arrow.y = Number(3);
		this.calcColor("8FBAF8");
	}
	/**
	 * defineProps method sets the properties of the scroller(which are constant to scroller).
	 */
	private function defineProps():Void {
		//Set the position of the target MC
		this.tX = this.target._x;
		this.tY = this.target._y;
		//Set the X and Y position of the Scroller
		this.scrollerCont._y = this.tY;
		this.scrollerCont._x = this.tX;
		//Define the slider properties
		this.movieclipProps();
	}
	/**
	 * movieclipProps method sets the properties of the scroller.
	 */
	private function movieclipProps():Void {
		this.movieclip = new Object();
		this.contentWidth = (this.widthTarget == null || this.widthTarget == undefined) ? (this.target._width) : (this.widthTarget._width);
		this.contentHeight = this.target._height;
		if (this.checkDir()) {
			//Create a MC for the Horizontal ScrollBar
			var Hs:MovieClip = this.movieclip.HorScroller=this.scrollerCont.createEmptyMovieClip("HorScroller", this.scrollerCont.getNextHighestDepth());
			//Set the properties
			Hs._Cstart = this.tX;
			//Set the pane and Content width
			Hs._Vbound = this.paneWidth;
			Hs._Cbound = this.contentWidth;
			//Calculate the track Size by reducing the padding
			Hs.TSize = Number(this.paneWidth-(this.styles.Track.x*2));
			//Create a new object for the arrow
			Hs.arrowProps = new Object();
			//Background object
			var bg:Object = Hs.arrowProps.bg=new Object();
			var arw:Object = Hs.arrowProps.ar=new Object();
			//Set the arrow position
			Hs.arrowProps.x1 = 0;
			Hs.arrowProps.y1 = this.styles.Btn.bg.y;
			Hs.arrowProps.x2 = this.paneWidth-this.styles.Btn.bg.w;
			Hs.arrowProps.y2 = this.styles.Btn.bg.y;
			//Set the background Properties as the default style property
			bg.h = this.styles.Btn.bg.h;
			bg.w = this.styles.Btn.bg.w;
			//Set the arrow Properties as the default style property
			arw.h = this.styles.Btn.arrow.h;
			arw.w = this.styles.Btn.arrow.w;
			arw.x = this.styles.Btn.arrow.x;
			arw.y = this.styles.Btn.arrow.y;
			//Set the slider Movt as 1/3rd of the total distance
			Hs.stepPro = Hs._Vbound/3;
			//Calculate the movement in pixel when the arrow buttons are clicked
			//i.e. proportionally 1/3rd of the pane width
			Hs.Spro = (100/(Hs._Cbound-Hs._Vbound))*Hs.stepPro;
			//Calculate the movement in pixel when the mouse is clicked in the track
			// i.e. always a percentage value of the total pane with respect to the content width
			Hs.Ppro = (100/(Hs._Cbound-Hs._Vbound))*(Hs._Vbound);
			//Set the class object for further reference
			this.scroller = Hs;
		}
	}
	/**
	 * draw method renders the final scroller in the target MC.
	 */
	private function draw():Void {
		//Store the style object
		var _s:Object = this.styles;
		//Store the Horizontal scroller MC
		var Hs:MovieClip = this.movieclip.HorScroller;
		//Set the y position - which is fixed for the Horizontal scroller
		Hs._y = this.paneHeight;
		//Build different rectangle components of the scroller
		Hs.Bg_mc = this.buildComps(Hs, "Bg_mc_h", _s.Bg, _s.Bg.lightColor, _s.Bg.darkColor, this.paneWidth, _s.Bg.h);
		Hs.Track_mc = this.buildComps(Hs, "Track_mc_h", _s.Track, _s.Track.darkColor, _s.Track.lightColor, Hs.TSize, _s.Track.h);
		//Create a new MC for slider - Container
		//Here we have a container to fix the slider LINES problem - as we do not want the xscale property
		//in the slider lines - so we create a container but scale it on the slider comp. inside the container.
		var sliderCont:MovieClip = Hs.createEmptyMovieClip("SliderCont", Hs.getNextHighestDepth());
		Hs.Slider_mc = this.buildComps(sliderCont, "Slider_mc_h", _s.Slider, _s.Slider.lightColor, _s.Slider.darkColor, Hs.TSize, _s.Slider.h);
		//Set the slider position to 0,0
		Hs.Slider_mc._x = 0;
		Hs.Slider_mc._y = 0;
		//Set the slider size
		this.setSliderSize();
		//Make the container as the Parent MC
		Hs.Slider_mc = sliderCont;
		//Set the position of the container for alignment
		sliderCont._x = _s.Slider.x;
		sliderCont._y = _s.Slider.y;
		//Calculate the bounds of the scroller
		Hs.Slider_mc.bounds = this.returnBoundObject(Hs.Slider_mc);
		//Draw the arrow
		this.drawArrows();
		//Define wheel hit
		this.WheelHit();
		//Draw the lines within the slider
		this.drawSliderLines(sliderCont, Hs.TSize, _s.Slider.h, _s.Slider.darkColor);
		//Calculate different ratios which are required
		this.sizeRatio();
		//Set the slider action
		this.sliderAction();
		//Check for onPress in the slider's track
		this.onTrackClick();
		//Register the wheel movement
		this.registerWheelMovt();
		this.registerKey();
	}
	/**
	 * checkDir method check whether content width is greater than the pane width.
	 */
	private function checkDir():Boolean {
		//Flag to check whether a scroller is required on the MC or not
		var flag:Boolean = false;
		if (this.contentWidth>this.paneWidth) {
			flag = true;
		} else {
			flag = false;
		}
		//return the flag
		return flag;
	}
	/**
	 * WheelHit method register the action to take on wheel movement.
	 */
	private function WheelHit():Void {
		//Store the width and height of the pane in a local variable for reference
		var w:Number = this.paneWidth;
		var h:Number = this.paneHeight;
		h = this.paneHeight+this.styles.Bg.h;
		//Create a new MC for the Wheel and Key registration
		this.Hit_mc = this.scrollerCont.createEmptyMovieClip("Hit_mc", this.scrollerCont.getNextHighestDepth());
		//Create a hidden rectangle which will listen to the mouse and key movements
		this.drawRectangle(this.Hit_mc, 0, 0, w, h, "0xFFFFFF", 0);
	}
	/**
	 * registerKey method register the on Key Press event
	 */
	private function registerKey():Void {
		//Store the class Reference
		var classRef = this;
		//Initialize the mouse listener
		this.keyListener = new Object();
		//Define the onMouseWheel Event for the slider
		this.keyListener.onKeyDown = function() {
			if (classRef.scroller._parent.Hit_mc.hitTest(_root._xmouse, _root._ymouse)) {
				//if the left key is pressed
				if (Key.isDown(Key.LEFT)) {
					//Move the slider to the left position which is "back" for the Horizontal Scroller
					classRef.moveSlider("back", classRef.scroller.Spro, false);
				} else if (Key.isDown(Key.RIGHT)) {
					//Move the slider to the right position which is "forward" for the Horizontal Scroller
					classRef.moveSlider("forward", classRef.scroller.Spro, false);
				}
			}
		};
		//Register the key listener for the slider
		Key.addListener(this.keyListener);
	}
	/**
	 * Wheel method register the wheel movement on the slider
	 */
	private function registerWheelMovt():Void {
		//Store the class Reference
		var classRef = this;
		//Initialize the mouse listener
		this.mouseListener = new Object();
		//Define the onMouseWheel Event for the slider
		this.mouseListener.onMouseWheel = function(delta) {
			if (classRef.scroller._parent.Hit_mc.hitTest(_root._xmouse, _root._ymouse)) {
				if (delta<0) {
					//Move the slider to the right position which is "forward" for the Horizontal Scroller
					classRef.moveSlider("forward", classRef.scroller.Spro, false);
				} else {
					//Move the slider to the left position which is "back" for the Horizontal Scroller
					classRef.moveSlider("back", classRef.scroller.Spro, false);
				}
			}
		};
		//Register the listener for the slider
		Mouse.addListener(this.mouseListener);
	}
	/**
	 * buildComps method builds different components which requires a rounded rectangle.
	 *	@param	trgt	Movie clip in which we've to create the component
	 *	@param	Name	Name by which we create the MC
	 *	@param	style	style object for the component
	 *	@param	col1	one of the 2 Color code for gradient fill of the component
	 *	@param	col1	one of the 2 Color code for gradient fill of the component
	 *	@param	width	width of the arrow
	 *	@param	height	height of the arrow
	 */
	private function buildComps(trgt:MovieClip, Name:String, style:Object, col1:Number, col2:Number, width:Number, height:Number):MovieClip {
		//Create a new MC for rounded rectangle
		var rectMC:MovieClip = trgt.createEmptyMovieClip(Name, trgt.getNextHighestDepth());
		//Set the position
		rectMC._x = style.x;
		rectMC._y = style.y;
		//Draw the rectangle
		this.drawRoundedRect(rectMC, 0, 0, width, height, 0, col1, col2, style.color, style.alpha, style.borderThickness, style.borderColor, style.borderAlpha);
		//return the object reference
		return rectMC;
	}
	/**
	 * drawArrows method draw both the arrow one on left and other on right of slider.
	 */
	private function drawArrows():Void {
		//Store the class Reference
		var classRef = this;
		//Store the Scroller MovieClip reference
		var mc:MovieClip = this.scroller;
		//Create MC for the left Arrow
		mc.arrowleft = mc.createEmptyMovieClip("arrowleft", mc.getNextHighestDepth());
		//Create the component ARROW on the left position
		this.drawArrow(mc.arrowleft, mc.arrowProps.ar.x, mc.arrowProps.ar.y, mc.arrowProps.ar.w, mc.arrowProps.ar.h, this.styles.Btn.arrow.cornerRadius, this.styles.Btn.arrow.lightColor, this.styles.Btn.arrow.darkColor, this.styles.Btn.arrow.color, this.styles.Btn.arrow.alpha, this.styles.Btn.arrow.borderColor, "leftarrow");
		//Set the position
		mc.arrowleft._x = mc.arrowProps.x1;
		mc.arrowleft._y = mc.arrowProps.y1;
		//Define the onPress Event for the left Arrow
		mc.arrowleft.onPress = function() {
			//Now if the btn is not released then we keep moving the slider backward
			this.onEnterFrame = function() {
				classRef.moveSlider("back", this._parent.Spro, true);
			};
		};
		//Define the onRelease Event for the left Arrow
		mc.arrowleft.onReleaseOutside = mc.arrowleft.onRelease=function () {
			//delete the onEnterFrame event
			delete this.onEnterFrame;
		};
		//Create MC for the right Arrow
		mc.arrowright = mc.createEmptyMovieClip("arrowright", mc.getNextHighestDepth());
		//Create the component ARROW on the left position
		this.drawArrow(mc.arrowright, mc.arrowProps.ar.x, mc.arrowProps.ar.y, mc.arrowProps.ar.w, mc.arrowProps.ar.h, this.styles.Btn.arrow.cornerRadius, this.styles.Btn.arrow.lightColor, this.styles.Btn.arrow.darkColor, this.styles.Btn.arrow.color, this.styles.Btn.arrow.alpha, this.styles.Btn.arrow.borderColor, "rightarrow");
		//Set the position
		mc.arrowright._x = mc.arrowProps.x2;
		mc.arrowright._y = mc.arrowProps.y2;
		//Define the onPress Event for the right Arrow
		mc.arrowright.onPress = function() {
			//Now if the btn is not released then we keep moving the slider forward
			this.onEnterFrame = function() {
				classRef.moveSlider("forward", this._parent.Spro, true);
			};
		};
		//Define the onRelease Event for the left Arrow
		mc.arrowright.onReleaseOutside = mc.arrowright.onRelease=function () {
			//delete the onEnterFrame event
			delete this.onEnterFrame;
		};
	}
	/**
	 * sizeRatio method sets the different ratios of the scroller.
	 */
	private function sizeRatio():Void {
		//Store the scroller reference
		var mc:MovieClip = this.scroller;
		//Calculate the slider Ratio ( path slider going to move )
		mc._sRatio = (mc.TSize-mc.Slider_mc._width)/100;
		// calculate in value slider's allowed movement
		mc._sDem = 100/(mc.TSize-mc.Slider_mc._width);
		//Calculate in percent for the content to pane width - this is max width a scroller can move
		mc._cRatio = (mc._Cbound-mc._Vbound)/100;
	}
	/**
	 * setSliderSize method sets the slider size according to proportion of the pane width
	 */
	private function setSliderSize() {
		//Store in a local reference
		var mc:MovieClip = this.scroller;
		//Set the xscale of the slider
		mc.Slider_mc._xscale = mc._Vbound/(mc._Cbound/100);
		//We cannot have slider size less than 15 for visual purpose
		if (mc.Slider_mc._width<=15) {
			mc.Slider_mc._width = 15;
		}
	}
	/**
	 * returnBoundObject method returns an object with the bounded values.
	 */
	private function returnBoundObject(mc):Object {
		//Create a new object 
		var data:Object = new Object();
		//Store all the properties
		data.xMin = mc._x;
		data.yMin = mc._y;
		data.xMax = mc._width-mc._x;
		data.yMax = mc._height-mc._y;
		//return the object
		return data;
	}
	/**
	 * sliderAction method defines the activities of the slider.
	 * i.e the onPress and onrelease event of the slider
	 * defines and calculates by how much the slider is move when the slider is released to a new place
	 */
	private function sliderAction():Void {
		//Store the class Reference
		var classRef:Object = this;
		//Store the style object for reference
		var _s:Object = this.styles;
		//Define the onPress event for the slider
		this.scroller.Slider_mc.onPress = function() {
			//Define the onEnterFrame event
			this.onEnterFrame = function() {
				var progression:Number = (this._parent._sDem*((this._x-_s.Slider.x)));
				//Define any value above the limit or below the limit within the bound value
				//is set to 98 for visual purpose - so that any value near to the end is taken as the end value
				if (progression>98) {
					progression = 100;
					//We do not move if its hardly moved
				} else if (progression<1) {
					progression = 0;
				}
				//Set the X position of the slider to a new calculated position  
				classRef.target._x = Number(this._parent._Cstart-(this._parent._cRatio*progression));
				//Dispacth the on click event for reference
				classRef.dispatchEvent({type:"click", target:classRef});
			};
			//Start dragging the slider within the bounded track
			this.startDrag(false, this.bounds.xMin, this.bounds.yMin, Number(this.bounds.xMin+this._parent.TSize-this._width), this.bounds.yMin);
		};
		//Define the onRelease event
		this.scroller.Slider_mc.onRelease = this.scroller.Slider_mc.onReleaseOutside=function () {
			//Stop Dragging
			this.stopDrag();
			//Delete the onEnterFrame event
			delete this.onEnterFrame;
		};
		//Define the onRollOver event and dispatch it
		this.scroller.Slider_mc.onRollOver = function() {
			classRef.dispatchEvent({type:"rollOver", target:classRef});
		};
		//Define the onRollOut event and dispatch it
		this.scroller.Slider_mc.onRollOut = function() {
			classRef.dispatchEvent({type:"rollOut", target:classRef});
		};
	}
	/**
	 * onTrackClick method register the click event for the slider's track.
	 */
	private function onTrackClick():Void {
		//Store the class reference
		var classRef:Object = this;
		//Define the onPress event for the slider's track
		this.scroller.Track_mc.onPress = function() {
			//Store the slider track MC
			var slider:MovieClip = this._parent.Slider_mc;
			//Check whether the slider is going to move forward or back
			//if the slider Xposition is < the mouse clicked position - then move forward else backward
			if (this._parent._xmouse>slider._x) {
				classRef.moveSlider("forward", this._parent.Ppro, true);
			} else {
				classRef.moveSlider("back", this._parent.Ppro, true);
			}
		};
	}
	/**
	 * calcBound method calculates the bound of the slider progression.
	 *	@param		Data	object which has the final progression value.
	 */
	private function calcBound(Data:Object):Void {
		//Here we check the progression value of the slider
		if (Data._pro<0) {
			Data._pro = 0;
		}
		if (Data._pro>100) {
			Data._pro = 100;
		}
		//Once we have set the desired progression value we define the movement type for the slider  
		this.movtType(Data);
	}
	/**
	 * moveSlider method set up the slider movement and store the property in the Date object.
	 *	@param		direction	specifies to which direction slider is going to move
	 *	@param		Step		value in Pixel to move at one click of slider
	 *	@param		animationReqd	whether the movement of slider is animated or static
	 */
	private function moveSlider(direction:String, Step:Number, animationReqd:Boolean):Void {
		//Store the MC reference 
		var Scroller:MovieClip = this.scroller;
		//Store the style object for reference in a local variable
		var _s:Object = this.styles;
		switch (direction) {
			//If the slider movt. is backward
		case "back" :
			//Calculate the step slider is going to go back
			//We calculate the progression value
			var pro:Number = Number((Scroller._sDem*(Scroller.Slider_mc._x-Scroller.Track_mc._x))-Step);
			//Create a new object and store all the desired properties required for the movt.
			var Data:Object = {_pro:pro, mc:Scroller, animationReqd:animationReqd, _d:"back"};
			break;
		case "forward" :
			//Calculate the step slider is going to go forward
			//We calculate the progression value
			var pro:Number = Number((Scroller._sDem*(Scroller.Slider_mc._x-Scroller.Track_mc._x))+Step);
			//Create a new object and store all the desired properties required for the movt.
			var Data:Object = {_pro:pro, mc:Scroller, animationReqd:animationReqd, _d:"forward"};
			break;
		}
		this.calcBound(Data);
	}
	/**
	 * movtType method creates an arrow with appropiate fill colors
	 *	@param		mc	Movie clip in which we've to create the arrow
	 *	@param		x	Starting X position of the arrow
	 *	@param		y	Starting Y position of the arrow
	 *	@param		w	width of the arrow
	 *	@param		h	height of the arrow
	 *	@param		radius	radius of the arrow
	 *	@param		col	fill color of the arrow
	 *	@param		alpha	alpha of the arrow
	 *	@param		dir	 direction of the arrow - left or right
	 */
	private function movtType(Data:Object):Void {
		//Dispatch Click Event
		this.dispatchEvent({type:"click", target:this});
		if (Data.animationReqd) {
			//New slider X value
			var sD = (Data.mc._sRatio*Data._pro)+this.styles.Slider.x;
			//new start X value for the content MC
			var cD = Number(Data.mc._Cstart-(Data.mc._cRatio*Data._pro));
			var twMC:Tween = new Tween(this.target, "_x", Strong.easeOut, this.target._x, cD, 0.3, true);
			var twMC:Tween = new Tween(Data.mc.Slider_mc, "_x", Strong.easeOut, Data.mc.Slider_mc._x, sD, 0.3, true);
		} else {
			//New slider X value
			var sD = (Data.mc._sRatio*Data._pro)+this.styles.Slider.x;
			//new start X value for the content MC
			var cD = Number(Data.mc._Cstart-(Data.mc._cRatio*Data._pro));
			//Widthout any animation we set the new position of the slider and the content MC
			Data.mc.Slider_mc._x = sD;
			this.target._x = cD;
		}
		//Dispatch Change Event for any reference if required
		this.dispatchEvent({type:"change", target:this});
	}
	/**
	 * drawArrow method creates an arrow with appropiate fill colors
	 *	@param		mc	Movie clip in which we've to create the arrow
	 *	@param		x	Starting X position of the arrow
	 *	@param		y	Starting Y position of the arrow
	 *	@param		w	width of the arrow
	 *	@param		h	height of the arrow
	 *	@param		radius	radius of the arrow
	 *	@param		col	fill color of the arrow
	 *	@param		alpha	alpha of the arrow
	 *	@param		dir	 direction of the arrow - left or right
	 */
	private function drawArrow(mc:MovieClip, x:Number, y:Number, w:Number, h:Number, radius:Number, col1:Number, col2:Number, col:String, alpha:Number, borderColor:Number, direction:String):Void {
		//Based on the type of the arrow required 
		switch (direction) {
		case "leftarrow" :
			//Create an Outer Boundary
			this.drawRoundedRect(mc, x-1, y-1, w+1, h+1, radius, col1, col2, col, alpha, 1, borderColor, 100);
			//Now draw the left arrow
			mc.moveTo(x+4, (h/2+2));
			mc.lineStyle(2, ColorExt.getDarkColor(col, 0.30), 100);
			mc.lineTo(w-4, y+2);
			mc.moveTo(x+4, (h/2+2));
			mc.lineTo(w-4, h-1);
			break;
		case "rightarrow" :
			//Create an Outer Boundary
			this.drawRoundedRect(mc, x-1-w/2, y-1, x+w-1, h+1, radius, col1, col2, col, alpha, 1, borderColor, 100);
			//Now draw the right arrow
			mc.moveTo(x+3, (h/2+2));
			mc.lineStyle(2, ColorExt.getDarkColor(col, 0.30), 100);
			mc.lineTo(x-2, y+2);
			mc.moveTo(x+3, (h/2+2));
			mc.lineTo(x-2, h-1);
			break;
		}
	}
	/**
	 * drawSliderLines method draws 4 lines in the slider
	 */
	private function drawSliderLines(sliderLines:MovieClip, w:Number, h:Number, col:Number):Void {
		//Create a new MC for slider - Container
		var mc:MovieClip = sliderLines.createEmptyMovieClip("SliderLines", sliderLines.getNextHighestDepth());
		//Difference between the lines
		var xDiff:Number = 3;
		//Calculate the x and y positions of the slider lines
		var x:Number = Math.floor((this.scroller._Vbound/(this.scroller._Cbound/w))/2);
		var y:Number = Math.floor(h/2);
		//Set the linestyle
		mc.lineStyle(1, col, 100, true, "none");
		//Now draw the 4 lines
		mc.moveTo(x-xDiff, y-3);
		mc.lineTo(x-xDiff, y+3);
		mc.moveTo(x, y-3);
		mc.lineTo(x, y+3);
		mc.moveTo(x+xDiff, y-3);
		mc.lineTo(x+xDiff, y+3);
		mc.moveTo(x+(2*xDiff), y-3);
		mc.lineTo(x+(2*xDiff), y+3);
		//To prevent any rounding issues while rendering the lines
		mc._x = Math.floor(mc._x);
	}
	/**
	 * drawRectangle method creates a rectangle with appropiate fill colors
	 *	@param		mc	Movie clip in which we've to create the rectangle
	 *	@param		x1	Starting X position of the rectangle
	 *	@param		y1	Starting Y position of the rectangle
	 *	@param		x2	Ending X position of the rectangle
	 *	@param		y2	Ending Y position of the rectangle
	 *	@param		col	fill color of the rectangle
	 *	@param		alpha	alpha of the rectangle
	 */
	private function drawRectangle(mc:MovieClip, x1:Number, y1:Number, x2:Number, y2:Number, col:String, alpha:Number):Void {
		//mc.lineStyle(1, 0xFFFFFF, 100);
		//Start the fill.
		mc.beginGradientFill("linear", [parseInt(col, 16), ColorExt.getLightColor(col, 0.45)], [alpha, alpha], [127, 255], {matrixType:"box", x:0, y:0, w:Math.abs(x2-x1), h:Math.abs(y2-y1), r:(90/180)*Math.PI});
		//Now draw the rectangle
		mc.moveTo(x1, y1);
		mc.lineTo(x1, y2);
		mc.lineTo(x2, y2);
		mc.lineTo(x2, y1);
		//End the fill
		mc.endFill();
	}
	/**
	 * calcColor method calculates the color of the slider
	 */
	private function calcColor(col:String):Void {
		this.styles.Bg.color = ColorExt.getLightColor(col, 0.25);
		//Track
		this.styles.Track.color = col;
		this.styles.Track.darkColor = ColorExt.getLightColor(col, 0.35);
		this.styles.Track.lightColor = ColorExt.getLightColor(col, 0.15);
		this.styles.Track.borderColor = ColorExt.getDarkColor(col, 0.75);
		// Btn bg
		this.styles.Btn.bg.color = ColorExt.getLightColor(col, 0.25);
		// Btn arrow
		this.styles.Btn.arrow.color = col;
		this.styles.Btn.arrow.darkColor = ColorExt.getDarkColor(col, 0.75);
		this.styles.Btn.arrow.lightColor = ColorExt.getLightColor(col, 0.25);
		this.styles.Btn.arrow.borderColor = ColorExt.getDarkColor(col, 0.55);
		//Slider
		this.styles.Slider.color = col;
		this.styles.Slider.darkColor = ColorExt.getDarkColor(col, 0.75);
		this.styles.Slider.lightColor = ColorExt.getLightColor(col, 0.25);
		this.styles.Slider.borderColor = ColorExt.getDarkColor(col, 0.55);
	}
	/**
	 * setColor method sets a new color for the scroller component
	 */
	public function setColor(col:String):Void {
		//Calculate the required colors
		this.calcColor(col);
		//Rerender the scroll
		if (this.checkDir()) {
			this.draw();
		}
	}
	/**
	 * invalidate method redraws the component without any code repitition.
	 */
	public function invalidate():Void {
		//Create a MC for the Horizontal ScrollBar
		var Hs:MovieClip = this.movieclip.HorScroller;
		//Set the properties
		Hs._Cstart = this.tX;
		this.scroller.Slider_mc = this.scroller.Slider_mc["Slider_mc_h"];
		var w:Number = this.scroller.Slider_mc._width;
		this.scroller.Slider_mc._xscale = this.scroller._Cbound/(this.scroller._Vbound/100);
		//Set the pane and Content width
		this.contentWidth = (this.widthTarget == null || this.widthTarget == undefined) ? (this.target._width) : (this.widthTarget._width);
		//Get the new height of the target MC
		this.contentHeight = this.target._height;
		//Make the slider visible
		this.show();
		Hs._Vbound = this.paneWidth;
		Hs._Cbound = this.contentWidth;
		//Calculate the track Size by reducing the padding
		Hs.TSize = Number(this.paneWidth-(this.styles.Track.x*2));
		//Set the slider Movt as 1/3rd of the total distance
		Hs.stepPro = Hs._Vbound/3;
		//Calculate the movement in pixel when the arrow buttons are clicked
		//i.e. proportionally 1/3rd of the pane width
		Hs.Spro = (100/(Hs._Cbound-Hs._Vbound))*Hs.stepPro;
		//Calculate the movement in pixel when the mouse is clicked in the track
		// i.e. always a percentage value of the total pane with respect to the content width
		Hs.Ppro = (100/(Hs._Cbound-Hs._Vbound))*(Hs._Vbound);
		//Set the class object for further reference
		this.scroller = Hs;
		//Set the new slider size
		this.setSliderSize();
		//Calculate the new width of the target MC
		var newW:Number = this.scroller.Slider_mc._width;
		//Set the container as the slider reference
		this.scroller.Slider_mc = this.scroller.Slider_mc._parent;
		//Clear previously drawn slider lines
		var SliderLines:MovieClip = this.scroller.Slider_mc["SliderLines"];
		//Clear the previously drawn slider lines
		SliderLines.clear();
		//Remove the slider lines
		SliderLines.removeMovieClip();
		//Draw the lines within the slider
		this.drawSliderLines(this.scroller.Slider_mc, Hs.TSize, this.styles.Slider.h, this.styles.Slider.darkColor);
		//Calculate the ratio's
		this.sizeRatio();
		if (!this.checkDir()) {
			this.hide();
		} else {
			//Here we reset the target MC position based on the new MC size
			//Also reset the slider position
			var progression:Number = (this.scroller.Slider_mc._parent._sDem*((this.scroller.Slider_mc._x-this.styles.Slider.x)));
			//Define any value above the limit or below the limit within the bound value
			//is set to 98 for visual purpose - so that any value near to the end is taken as the end value
			if (progression>98) {
				progression = 100;
				//We do not move if its hardly moved
			} else if (progression<1) {
				progression = 0;
			}
			//Set the X position of the slider to a new calculated position  
			this.target._x = Number(this.scroller.Slider_mc._parent._Cstart-(this.scroller.Slider_mc._parent._cRatio*progression));
			//Reset position
			this.scroller.Slider_mc._x -= Math.abs(newW-w);
			//Check whether the slider is going below the start Position 
			if (this.scroller.Slider_mc._x<this.styles.Slider.x) {
				//Set the start position of the slider
				this.scroller.Slider_mc._x = this.styles.Slider.x;
			}
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
	private function drawRoundedRect(mc:MovieClip, x:Number, y:Number, w:Number, h:Number, cornerRadius:Number, col1:Number, col2:Number, col:String, alpha:Number, borderThickness:Number, borderColor:Number, borderAlpha:Number):Void {
		//We first see if the user has defined a corner radius for us. If yes, we need to create curves and calculate
		// draw top line
		mc.lineStyle(borderThickness, borderColor, borderAlpha);
		//Start the fill.
		mc.beginGradientFill("linear", [col1, col2], [alpha, alpha], [127, 255], {matrixType:"box", x:0, y:0, w:w, h:h, r:(90/180)*Math.PI});
		if (cornerRadius>0) {
			//Initialize variables to be usd
			var theta:Number, angle:Number, cx:Number, cy:Number, px:Number, py:Number;
			//Make sure that (w,h) is larger than 2*cornerRadius
			if (cornerRadius>Math.min(w, h)/2) {
				cornerRadius = Math.min(w, h)/2;
			}
			// theta = 45 degrees in radians   
			theta = Math.PI/4;
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
	  * setState method is use to set the state of the slider enabled/disabled
	*/
	public function setState(state:Boolean):Void {
		//set the Slider state
		this.movieclip.HorScroller.Slider_mc._visible = state;
		if (!state) {
			this.setColor("666666");
		}
		if (!state) {
			this.scroller.onPress = function() {
			};
		}
		//Do not/use hand cursor
		this.movieclip.HorScroller.Slider_mc.useHandCursor = state;
		this.scrollerCont.useHandCursor = state;
		this.scroller.useHandCursor = state;
	}
	/**
	 * getPosition method returns the position of the viewable area with respect to content Area in percent.
	*/
	public function getPosition():Object {
		return ({startX:this.movieclip.HorScroller.Slider_mc._x, endX:this.movieclip.HorScroller.Slider_mc._x+(this.scroller._Vbound/(this.scroller._Cbound/this.movieclip.HorScroller.TSize))});
	}
	/**
	 * getState method returns the state of the Slider
	*/
	public function getState():Boolean {
		//return the Slider state
		return this.movieclip.HorScroller.Slider_mc._visible;
	}
	/**
	  * show method shows the Sldier
	*/
	public function show():Void {
		//Show the Slider
		this.scrollerCont._visible = true;
	}
	/**
	 * hide method hides the Slider
	*/
	public function hide():Void {
		//Hide the Slider
		this.scrollerCont._visible = false;
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//Remove the listeners
		Key.removeListener(this.keyListener);
		Mouse.removeListener(this.mouseListener);
		//Delete the listener object
		delete this.mouseListener;
		delete this.keyListener;
		//Masking Movie
		this.mask_mc.removeMovieClip();
		//Hit MC for the click event in the slider
		this.Hit_mc.removeMovieClip();
		//Scroller Container
		this.scrollerCont.removeMovieClip();
		this.scroller.removeMovieClip();
	}
}
