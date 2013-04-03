 /**
* @class ScrollVBar
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
* ScrollVBar class lets you add a very low footprint vertical scroll bar
* to any MovieClip to allow a smeller window than its actual dimension.
*/
import mx.utils.Delegate;
import flash.geom.Rectangle;

class com.fusioncharts.helper.ScrollVBar 
{
	//Private variables
	//Movie clip in which the scroll elements would be drawn
	private var scrollMC : MovieClip;
	//The movieclip for which this scroll bar is applicable
	private var mc : MovieClip;
	//Position
	private var x : Number;
	private var y : Number;
	//Width and height
	private var w : Number;
	private var h : Number;
	//Vertical Padding between the scroll bar and the buttons (above and below it)
	private var scrollVPadding : Number = 3;
	//Horizontal and vertical padding between the scroll bar and the scroll bg
	private var scrollBarHPadding : Number = 3;
	private var scrollBarVPadding : Number = 3;
	//Height of button
	private var btnHeight : Number = 10;
	//Cosmetic Properties
	private var scrollBgColor : Number;
	private var scrollBarColor : Number;
	private var btnColor : Number;
	//More co-ordinates
	private var btnCenterX : Number;
	private var upBtnEnd : Number;
	private var downBtnStart : Number;
	//Scroll background properties
	private var scrollBgX : Number;
	private var scrollBgY : Number;
	private var scrollBgW : Number;
	private var scrollBgH : Number;
	//Scroll bar properties
	private var scrollBarX : Number;
	private var scrollBarY : Number;
	private var scrollBarH : Number;
	private var scrollBarW : Number;
	private var scrollBarDragH : Number;
	//Reference to sub movie clips
	private var mcBtnUp : MovieClip;
	private var mcBtnDown : MovieClip;
	private var mcScrollBg : MovieClip;
	private var mcScrollVBar : MovieClip;
	//Boolean indicating whether scroll bar would be visible
	private var showScrollVBar : Boolean;
	
	//Scroll interval
	private var scrollInterval : Number;
	//Scroll speed - in Milliseconds - the lesser the faster
	private var scrollSpeedInterval : Number = 50;
	//Flag to indicate whether it's currently scrolling using.
	private var isBarScrolling : Boolean = false;
	
	//Ratio moved - bar to bg
	private var ratioMoved : Number;
	//Listener Object
	private var scrollListener : Object;
	//Starting y position for placing legend content
	private var yIniMc:Number;
	//Scrolling unit
	private var scrollSpeed:Number = 5;
	//Mask movieclip
	private var mcMask:MovieClip;
	
	
	/**
	* Constructor function.
	* Draws the scroll bar and attaches it to the required movieclip.
	* @param	mc				MovieClip to which this scroll bar will be attached.
	* @param	targetMC		Target movie clip in which we'll draw the scroll bar
	* @param	x				Top X Position of the entire scroll bar
	* @param	y				Top Y Position of the entire scroll bar
	* @param	w				Width of the scroll bar
	* @param	h				Height of the scroll bar
	* @param	scrollBgColor	Background color for scroll bar
	* @param	scrollBarColor	Scroll bar color
	* @param	btnColor		Color of scroll button
	*/
	function ScrollVBar (mc : MovieClip, targetMC : MovieClip, x : Number, y : Number, w : Number, h : Number, scrollBgColor : String, scrollBarColor : String, btnColor : String)
	{
		//Store in private properties
		this.mc = mc;
		scrollMC = targetMC;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		//Cosmetic parameters
		this.scrollBgColor = parseInt (scrollBgColor, 16);
		this.scrollBarColor = parseInt (scrollBarColor, 16);
		this.btnColor = parseInt (btnColor, 16);
		//Set the starting y pos of content
		yIniMc = this.mc._y;
		//Draw the base
		this.drawBase ();
		
		//Draw the scroll bar too
		this.drawScrollVBar ();
		//Draw and apply the mask
		this.drawMask();
		
	}
	/**
	 * drawMask method is called to draw and apply the mask on the scrollable MC.
	 */
	private function drawMask():Void{
		//Mask dimensions
		var w:Number = mc._width;
		var h:Number = this.h;
		//Create the mask container and place it
		mcMask = scrollMC.createEmptyMovieClip('mask', scrollMC.getNextHighestDepth());
		mcMask._x = this.mc._x;
		mcMask._y = this.mc._y;
		//Draw mask
		mcMask.lineStyle(1);
		mcMask.beginFill(0xFFFFFF, 100);
		mcMask.lineTo(0, h);
		mcMask.lineTo(w, h);
		mcMask.lineTo(w, 0);
		mcMask.lineTo(0, 0);
		//Apply the mask
		this.mc.setMask(mcMask);
	}
	
	/**
	* drawBase method draws the scroll bar background and scroll buttons
	* |-scrollMC -|
	*		  |-bg (Scroll Bar background) - Depth 1
	*		  |-bar (Drag-able bar) 	   - Depth 2
	*		  |-btnUp					   - Depth 3
	*		  |-btnDown					   - Depth 4
	*/
	private function drawBase ()
	{
		//Create the sub movie clips
		mcScrollBg = scrollMC.createEmptyMovieClip ("bg", 1);
		mcScrollVBar = scrollMC.createEmptyMovieClip ("bar", 2);
		mcBtnUp = scrollMC.createEmptyMovieClip ("btnUp", 3);
		mcBtnDown = scrollMC.createEmptyMovieClip ("btnDown", 4);
		//Calculate the co-ordinates and height,width of scroll bg and bar
		//Scroll bg properties
		scrollBgX = x;
		scrollBgY = y + btnHeight + scrollVPadding;
		scrollBgW = w;
		scrollBgH = h - ((2 * btnHeight) + (2 * scrollVPadding));
		//Calculate the position for buttons
		btnCenterX = x + (w / 2);
		upBtnEnd = y + btnHeight;
		downBtnStart = upBtnEnd + scrollBgH + (2 * scrollVPadding);
		//Draw the buttons
		//Up button
		mcBtnUp.lineStyle ();
		mcBtnUp.beginFill (btnColor, 100);
		mcBtnUp.moveTo (btnCenterX, y);
		mcBtnUp.lineTo (btnCenterX - (w / 2) , upBtnEnd);
		mcBtnUp.lineTo (btnCenterX + (w / 2) , upBtnEnd);
		mcBtnUp.lineTo (btnCenterX, y);
		mcBtnUp.endFill ();
		//Down button
		mcBtnDown.lineStyle ();
		mcBtnDown.beginFill (btnColor, 100);
		mcBtnDown.moveTo (btnCenterX - (w / 2) , downBtnStart);
		mcBtnDown.lineTo (btnCenterX + (w / 2) , downBtnStart);
		mcBtnDown.lineTo (btnCenterX, downBtnStart + btnHeight);
		mcBtnDown.lineTo (btnCenterX - (w / 2) , downBtnStart);
		mcBtnDown.endFill ();
		//Draw the scroll bg
		mcScrollBg.moveTo (scrollBgX, scrollBgY);
		mcScrollBg.lineStyle ();
		mcScrollBg.beginFill (scrollBgColor, 100);
		mcScrollBg.lineTo (scrollBgX + scrollBgW, scrollBgY);
		mcScrollBg.lineTo (scrollBgX + scrollBgW, scrollBgY + scrollBgH);
		mcScrollBg.lineTo (scrollBgX, scrollBgY + scrollBgH);
		mcScrollBg.lineTo (scrollBgX, scrollBgY);
		mcScrollBg.endFill ();
		//Event handlers for the up and down buttons
		//When the up button is pressed, we delegate the handler to scrollUp method
		mcBtnUp.onPress = Delegate.create (this, scrollUp);
		mcBtnUp.onRelease = mcBtnUp.onReleaseOutside = Delegate.create (this, clearScrollInterval);
		//When the down button is pressed, we delegate the handler to scrollDown method
		mcBtnDown.onPress = Delegate.create (this, scrollDown);
		mcBtnDown.onRelease = mcBtnDown.onReleaseOutside = Delegate.create (this, clearScrollInterval);
	}
	/**
	* scrollDown function is called as a delegate function when the down button
	* is pressed to scroll the content up.
	*/
	private function scrollDown () : Void 
	{
		//Set interval for scrolling down
		scrollInterval = setInterval (scrollDownByOne, scrollSpeedInterval, this);
		//Nested function scrollDownByOne which scrolls the content.
		function scrollDownByOne (thisRef)
		{
			var delH:Number = thisRef.mc._height - thisRef.h;
			
			if(thisRef.mc._y > thisRef.yIniMc - delH){
				thisRef.mc._y -= thisRef.scrollSpeed;
				
				if(thisRef.mc._y < thisRef.yIniMc - delH){
					thisRef.mc._y = thisRef.yIniMc - delH;
				}
				
				thisRef.updateScrollVBar();
			}
		}
	}
	/**
	* scrollUp function is called as a delegate function when the down button
	* is pressed to scroll the content down.
	*/
	private function scrollUp () : Void 
	{
		
		//Set interval for scrolling up
		scrollInterval = setInterval (scrollUpByOne, scrollSpeedInterval, this);
		//Nested function scrollDownByOne which scrolls the content.
		function scrollUpByOne (thisRef)
		{
			if(thisRef.mc._y < thisRef.yIniMc){
				thisRef.mc._y += thisRef.scrollSpeed;
				
				if(thisRef.mc._y > thisRef.yIniMc){
					thisRef.mc._y = thisRef.yIniMc;
				}
				thisRef.updateScrollVBar();
			}
		}
	}
	/**
	* clearScrollInterval is called as a delegate function when either
	* the down or up button is released. Here, we clear the interval, which
	* was invoked for scrolling.
	*/
	private function clearScrollInterval () : Void 
	{
		//Clear the interval
		clearInterval (scrollInterval);
	}
	/**
	* drawScrollVBar draws the actual scroll bar.
	* We have kept two functions separate so that a user can invalidate
	* the scroll bar once something has been added to the movieclip at runtime.
	*/
	public function drawScrollVBar ()
	{
		//The scroll bar would be displayed only if the content of the
		//movieclip exceeds the current view. So check
		var totalHeight:Number = mc._height;
		var viewHeight:Number = h;
		
		showScrollVBar = (totalHeight > viewHeight)? true : false;
		
		//Perform other things only if we've to show the scroll bar
		if (showScrollVBar)
		{
			//Calculate scroll bar co-ordinates and dimensions
			scrollBarX = scrollBgX + scrollBarHPadding;
			scrollBarY = scrollBgY + scrollBarVPadding;
			scrollBarW = scrollBgW - (2 * scrollBarHPadding);
			
			scrollBarH = int ((viewHeight / totalHeight) * (scrollBgH - (2 * scrollBarVPadding)));
			//Minimum height of scroll bar can be 5 pixels.
			scrollBarH = (scrollBarH < 5) ? 5 : scrollBarH;
			//Drag-able height of scroll bar
			scrollBarDragH = scrollBgH - scrollBarH - (2 * scrollBarVPadding);
			//Draw the scroll bar
			mcScrollVBar.clear ();
			mcScrollVBar.moveTo (scrollBarX, scrollBarY);
			mcScrollVBar.lineStyle ();
			mcScrollVBar.beginFill (scrollBarColor, 100);
			mcScrollVBar.lineTo (scrollBarX + scrollBarW, scrollBarY);
			mcScrollVBar.lineTo (scrollBarX + scrollBarW, scrollBarY + scrollBarH);
			mcScrollVBar.lineTo (scrollBarX, scrollBarY + scrollBarH);
			mcScrollVBar.lineTo (scrollBarX, scrollBarY);
			mcScrollVBar.endFill ();
			//Define the onPress and onRelease handlers
			mcScrollVBar.onPress = Delegate.create (this, scrollBarDown);
			mcScrollVBar.onRelease = mcScrollVBar.onReleaseOutside = Delegate.create (this, scrollBarUp);
			
			//Create a listener object, whose onMouseWheel property would listen for scrolling of mc.
			scrollListener = new Object ();
			//Delegate the onMouseWheel event to updateScrollVBar method of this class.
			scrollListener.onMouseWheel = Delegate.create (this, updateScrollVBar);
			//Add the listener to Mouse.
			Mouse.addListener (scrollListener);
		}
	}
	/**
	* invalidate method is called, whenever we want to invalidate our
	* current scroll bar and re-adjust itself to the movieclip.
	* Specifically, it should be called, when new content is added to the
	* attached movieclip from code itself.
	*/
	public function invalidate () : Void 
	{
		//Re-draw the scroll bar - rest can remain same.
		this.drawScrollVBar ();
	}
	/**
	* This method is invoked when the user presses the scroll bar.
	* Here, we allow the user to drag the scroll bar down.
	*/
	private function scrollBarDown () : Void 
	{
		//Update scrolling flag
		isBarScrolling = true;
		//Allow dragging
		mcScrollVBar.startDrag (false, 0, 0, 0, scrollBgH - scrollBarH - (2 * scrollBarVPadding));
		//Create the enterFrame event
		mcScrollVBar.onEnterFrame = Delegate.create (this, scrollContent);
	}
	/**
	* This method is invoked when the user released the scroll bar.
	* Here, we set the proper flags and stop dragging.
	*/
	private function scrollBarUp () : Void 
	{
		//Update scrolling flag
		isBarScrolling = false;
		//Stop dragging
		mcScrollVBar.stopDrag ();
		//Delete the enterFrame event
		delete mcScrollVBar.onEnterFrame;
	}
	/**
	* This method is invoked under onEnterFrame of the scroll bar.
	* Here, we first check if the user is dragging the scroll bar.
	* If yes, we update the scroll position of the mc.
	*/
	private function scrollContent () : Void 
	{
		//We perform the routine only if the scroll bar is pressed
		//i.e., the user is scrolling using the scroll bar
		if (isBarScrolling)
		{
			//Update the scroll position of the mc
			//Find ratio moved.
			ratioMoved = mcScrollVBar._y / scrollBarDragH;
			//Scroll the content
			this.mc._y = yIniMc - (this.mc._height - this.h)* ratioMoved ;
		}
	}
	/**
	* updateScrollVBar method updates the position of the scroll bar
	* based on the scroll position of the mc
	* This method is invoked when the onMouseWheel event of the mouse
	* is invoked.
	*/
	private function updateScrollVBar (delta:Number, scrollTarget:String)
	{
		//If the user is not scrolling using the bar, we update the scroll bar position
		if ( ! isBarScrolling)
		{
			//For defined delta, indicating mouse wheeling
			if(delta){
				//If mouse wheeling is on proper location to trigger the content scroll
				if(this.mcMask.hitTest(_root._xmouse, _root._ymouse)){
					//Check the limiting cases
					if( (delta>0 && this.mc._y >= this.yIniMc) || (delta<0 && this.mc._y <= this.yIniMc - (this.mc._height - this.h))){
						return;
					}
					//Scroll content
					this.mc._y += delta;
					
					//Reposition content to limits if required
					if(delta>0 && this.mc._y >= this.yIniMc){
						this.mc._y = this.yIniMc;
					} else if(delta<0 && this.mc._y <= this.yIniMc - (this.mc._height - this.h)){
						this.mc._y = this.yIniMc - (this.mc._height - this.h);
					}
				}
			}
			//Set position of scroll bar
			ratioMoved = (yIniMc - this.mc._y) / (this.mc._height - this.h);
			mcScrollVBar._y = ratioMoved * scrollBarDragH;
		}
	}
	/**
	* destroy method MUST be called whenever you wish to delete this class
	* instance.
	*/
	public function destroy ()
	{
		//Remove listeners.
		Mouse.removeListener (scrollListener);
		//Delete events
		delete mcBtnUp.onPress;
		delete mcBtnUp.onRelease;
		delete mcBtnUp.onReleaseOutside;
		
		delete mcBtnDown.onPress;
		delete mcBtnDown.onRelease;
		delete mcBtnDown.onReleaseOutside
		
		delete mcScrollVBar.onPress;
		delete mcScrollVBar.onRelease;
		delete mcScrollVBar.onReleaseOutside
		//Clear setInterval if running
		clearInterval (scrollInterval);
		//Remove the movie clips
		mcScrollBg.removeMovieClip ();
		mcScrollVBar.removeMovieClip ();
		mcBtnUp.removeMovieClip ();
		mcBtnDown.removeMovieClip ();
		mcMask.removeMovieClip();
	}
}
