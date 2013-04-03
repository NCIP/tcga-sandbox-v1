/**
* @class ColorPicker
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* ColorPicker class presents a color selection utility
* when its object is added in UI.
*/

//Required classes imported
import mx.events.EventDispatcher;
import mx.utils.Delegate;
import flash.geom.Matrix;
import flash.display.BitmapData;
import flash.geom.ColorTransform;
import flash.geom.Transform;
import flash.filters.DropShadowFilter;

//Class definition
class com.fusioncharts.helper.ColorPicker implements com.fusioncharts.helper.IComponent{
	
	//Keep count of ColorPicker instantiations in the application
	private static var count:Number = 0;
	
	//Enable state of the component
	private var state:Boolean = true;
	//Parent container of colorPicker
	private var mcBase:MovieClip;
	//The colorPicker container
	private var mcColorPicker:MovieClip;
	//Hexadecimal color Value displayer
	private var txtValue:TextField;
	//Color swatch/box showing the current color
	private var mcColorBox:MovieClip;
	//ColorBox border
	private var mcColorBoxBorder:MovieClip;
	//Color spectrum
	private var mcSpectrum:MovieClip;
	//Holder of info - color box and txt
	private var mcInfo:MovieClip;
	//Custom cursor
	private var mcCursor:MovieClip;
	
	//Gutter
	private var gutter:Number = 2;
	//Box and text height
	private var boxHeight:Number = 20;
	//Spectrum bitmapdata
	private var bmpd:BitmapData;
	//Mouse listener object
	private var mouseListenerObj:Object;
	//Key listener object
	private var keyListener:Object;
	//Mouse over spectrum flag
	private var pick:Boolean;
	//Current color value
	private var colorValue:Number = 0xE2FFAE;
	//Spectrum alignment
	private var rightAlignSpectrum:Boolean;
	//Border color of objects
	private var borderColor:Number = 0xADADAD;
	//Spectrum dimensions
	private var spectrumWidth:Number = 200;
	private var spectrumHeight:Number = 100;
	//Flag for transparency in component
	private var needTransparency:Boolean = false;
	//Font properties
	private var fontProps:Object;
	 
	/**
	 * ColorPicker constructor.
	 * @param mcBase				Movieclip container of the component
	 * @param depth					level depth to add the component
	 * @param defaultColor			starting color to display in colorbox
	 * @param borderColor			border color of component elements
	 * @param rightAlignSpectrum	Should spectrum be left or right aligned
	 * @param spectrumWidth			width of spectrum
	 * @param spectrumHeight		height of spectrum
	 * @param fontProps				font properties 
	 * 
	 */
	public function ColorPicker(mcBase:MovieClip, depth:Number, defaultColor:Number, borderColor:Number, rightAlignSpectrum:Boolean, spectrumWidth:Number, spectrumHeight:Number, fontProps:Object) {
		//Count of instantiation of the component
		ColorPicker.count++;
		
		this.mcBase = mcBase;
		this.rightAlignSpectrum = rightAlignSpectrum ? true : false;
		//
		if(!isNaN(defaultColor)){
			this.colorValue = Math.round(defaultColor);
		}
		if(!isNaN(borderColor)){
			this.borderColor = Math.round(Math.abs(borderColor));
		}
		if(!isNaN(spectrumWidth)){
			this.spectrumWidth = Math.abs(spectrumWidth);
		}
		if(!isNaN(spectrumHeight)){
			this.spectrumHeight = Math.abs(spectrumHeight);
		}
		if(!fontProps){
			this.fontProps = fontProps;
		}
		//Listener object for mouse
		mouseListenerObj = new Object();
		mouseListenerObj.thisRef = this;
		//
		var depth:Number = isNaN(depth) ? mcBase.getNextHighestDepth() : depth;
		//Container of the componant created
		mcColorPicker = mcBase.createEmptyMovieClip('colorPicker_'+ ColorPicker.count, depth);
		
		//Picker drawn
		this.drawColorPicker();
		//TexField configured
		this.configureTxtFld();
		//Custom cursor
		this.createCursor();
		//Initial color display done through setter method
		this.color = colorValue;
		//Transparency is depicted for colorValue = -1.
		//If not transparent
		if(colorValue != -1){
			//Set color value in text field or else keep it blank
			txtValue.text = formatColorString(colorValue.toString(16));
		}
		//===============================//
		var thisRef = this;
		//Release event programmed for colorBox
		mcColorBox.onRelease = function(){
			//If flyout is to be closed
			if(thisRef.mcSpectrum._visible){
				//Set color
				thisRef.setColors(parseInt(thisRef.txtValue.text, 16));
			} else {
				//Programm mouseUp event here, and will be deprogrammed on the very first such event trigger.
				thisRef.mouseListenerObj.onMouseUp = function(){
					//If color not be picked
					if(!thisRef.pick){
						//Close spectrum
						thisRef.closeFlyout();
						//Deprogram event
						delete this.onMouseUp;
					}
				}
			}
			//Toggle spectrum visibility
			thisRef.mcSpectrum._visible = !thisRef.mcSpectrum._visible;
		}
		//==============================//
		//On text change in the color textfield
		txtValue.onChanged = function(){
			this.text = this.text.toUpperCase();
		}
		//On focus kill of color text field
		txtValue.onKillFocus = function(newFocus){
			//If transparency not required or if defined color value exists (irrespective of transparency requirement)
			//1. If no scope of transparency, textfield blank or not
			//2. If transparency opted and textfield not blank
			if( !thisRef.needTransparency || this.text != '' ){
				this.text = thisRef.formatColorString(this.text);
				thisRef.setColors(parseInt(this.text, 16));
			//Or if transparency opted and textfield blank
			}else if(this.text == ''){
				//Display transparency symbol
				thisRef.portrayTransparency();
				//Set colorValue as -1, indicating no color.
				thisRef.colorValue = -1;
			}
		}
		//=======================================//
		
		mcSpectrum.thisRef = this;
		
		//Events programmed for spectrum
		mcSpectrum.onRelease = function(){
			//To stop picking anymore
			thisRef.pick = false;
			//Set latest color as selected one
			thisRef.setColors(parseInt(thisRef.txtValue.text, 16));
			//Make spectrum invisible
			this._visible = false;
			//Deprogram mouseUp event
			delete thisRef.mouseListenerObj.onMouseUp;
			
		}
		
		mcSpectrum.onReleaseOutside = function(){
			//Make spectrum invisible
			this._visible = false;
		}
		
		mcSpectrum.onDragOut = stopColorPicking;
		mcSpectrum.onRollOver = startColorPicking;
		mcSpectrum.onRollOut = stopColorPicking;
		
		//=========================================//
		//Program to pick color on mouse move over spectrum
		mouseListenerObj.onMouseMove = pickColor;
		//Program mouse event listening
		Mouse.addListener(mouseListenerObj);
		
		//Key event listener
		keyListener = new Object();
		//Key down programmed
		keyListener.onKeyDown = function(){
			//For ENTER key down
			if(Key.isDown(Key.ENTER)){
				//Set focus to none
				 Selection.setFocus(null);
			}
		};
		//Program key event listening
		Key.addListener(keyListener);
	}
	/**
	 * closeFlyout is a public method for developers to close the spectrum
	 * from application.
	 */
	public function closeFlyout(){
		//If spectrum visible
		if(this.mcSpectrum._visible){
			//Make it invisible
			this.mcSpectrum._visible = false;
			//For specific color value
			if(this.colorValue != -1){
				//Reset back latest selected color
				this.setColors(this.colorValue);
			//For no color value
			} else {
				//depict transparency
				this.portrayTransparency();
				//Note that latest selection had no color
				this.colorValue = -1;
			}
		}
	}
	
	/**
	 * inValidate method redraws the component without any code repetition.
	 */
	public function invalidate():Void{
		//None to do as of now
	}
	/**
	 * setColor method is use to set the Color of the Component.
	 * @param	colorCode	color value
	 */
	function setColor(colorCode:String):Void{
		//There no base color for the component.
	}
	/**
	 * setState method is use to set the state of the Component.
	 * @param	state	enabling/disabling state
	 */
	function setState(state:Boolean):Void{
		this.state = state;
		//Set enable state sought for
		this.mcColorPicker.enabled = state;
		//If be enabled/active state
		if(state){
			this.mcColorPicker._alpha = 100;
			Mouse.addListener(mouseListenerObj);
			Key.addListener(keyListener);
		//Or for disabled/deactive state
		} else {
			this.closeFlyout();
			this.mcColorPicker._alpha = 70;
			Mouse.removeListener(mouseListenerObj);
			Key.removeListener(keyListener);
		}
	}
	/**
	 * getState method returns the state of the Component.
	 * @return		the enabled state
	 */
	function getState():Boolean{
		return this.state;
	}
	/**
	 * show method shows the Component.
	 */
	function show():Void{
		this.mcColorPicker._visible = true;
	}
	/**
	 * hide method hides the Component.
	 */
	function hide():Void{
		this.mcColorPicker._visible = false;
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	 */
	function destroy():Void{
		//Flush bitmapdata
		bmpd.dispose();
		//Color Value holder
		txtValue.removeTextField();
		//Selected color Box
		mcColorBox.removeMovieClip();
		//ColorBox border
		mcColorBoxBorder.removeMovieClip();
		//Holder of info - color box and txt
		mcInfo.removeMovieClip();
		//Color spectrum
		mcSpectrum.removeMovieClip();
		//Custom cursor
		mcCursor.removeMovieClip();
		//The main movieclip
		mcColorPicker.removeMovieClip();
		//Deprogram event listening for mouse and key
		Mouse.removeListener(mouseListenerObj);
		Key.removeListener(keyListener);
	}
	
	//========================================//
	/**
	 * depth is a setter method for level depth of component.
	 * @param 	depth	level depth
	 * 
	 */
	public function set depth(depth:Number){
		this.mcColorPicker.swapDepths(depth);
	}
	/**
	 * x is a setter method for x position of component. 
	 * @param 	xValue		x position
	 * 
	 */
	public function set x(xValue:Number){
		this.mcColorPicker._x = xValue;
	}
	/**
	 * y is a setter method for y position of component. 
	 * @param 	yValue		y position
	 * 
	 */
	public function set y(yValue:Number){
		this.mcColorPicker._y = yValue;
	}
	/**
	 * color is a setter method for default color.
	 * @param 	colorValue	default color
	 * 
	 */
	public function set color(colorValue:Number){
		//If textField is in focus, as when one is editing, set focus to none.
		Selection.setFocus(null);
		//If valid color specified
		if(colorValue != -1){
			//If spectrum is not open
			if(!this.pick){
				//Set color
				this.setColors(colorValue);
			}
		//Else for transparency
		} else {
			//Depict tramsparency
			this.portrayTransparency();
			this.colorValue = -1;
			this.txtValue.text = '';
		}
	}
	/**
	 * transparency is a setter method for opting transparency feature in the component.
	 * @param 	value	boolean option
	 * 
	 */
	public function set transparency(value:Boolean){
		this.needTransparency = value;
	}
	//========================================//
	/**
	 * x is a getter method to return x position.
	 * @return		x position
	 * 
	 */
	public function get x():Number{
		return this.mcColorPicker._x;
	}
	/**
	 * y is a getter method to return y position.
	 * @return 		y position
	 * 
	 */
	public function get y():Number{
		return this.mcColorPicker._y;
	}
	/**
	 * width is a getter method to return width of component.
	 * @return 		width of component
	 * 
	 */
	public function get width():Number{
		return this.mcColorPicker._width;
	}
	/**
	 * height is a getter method to return height of component.
	 * @return 		height of component
	 * 
	 */
	public function get height():Number{
		return this.mcColorPicker._height;
	}
	/**
	 * initWidth is a getter method to return init Width of component.
	 * @return 		init Width of component
	 * 
	 */
	public function get initWidth():Number{
		return this.mcInfo._width;
	}
	/**
	 * initHeight is a getter method to return init Height of component.
	 * @return 		init Height of component
	 * 
	 */
	public function get initHeight():Number{
		return this.mcInfo._height;
	}
	/**
	 * color is a getter method to return current color.
	 * @return 		current color
	 * 
	 */
	public function get color():Number{
		return colorValue;
	}
	//=========================================//
	//Hack to avoid compiler error warning, which is a bug!
	private var thisRef;
	/**
	 * configureTxtFld method formats the only textfield.
	 */
	private function configureTxtFld(){
		txtValue.background = true;
		txtValue.restrict = "0-9A-Fa-f";
		txtValue.type = "input";
		txtValue.maxChars = 6;
		
		var tfmt:TextFormat = new TextFormat();
		//Font properties default set to match that of flash components
		tfmt.font = (fontProps.font)? fontProps.font : "_sans";
		tfmt.size = (fontProps.size)? fontProps.size : 12;
		tfmt.color = (fontProps.color)? fontProps.color : 0x0B333C;
		tfmt.indent = 2;
		txtValue.setNewTextFormat(tfmt);
	}
	/**
	 * drawColorPicker method draws the graphical presentation of the color picker.
	 */
	private function drawColorPicker(){
		//Container of clorBox and textField displaying colorValue
		mcInfo = mcColorPicker.createEmptyMovieClip('infoHolder', mcColorPicker.getNextHighestDepth());
		//Draw the colorBox
		mcColorBox = mcInfo.createEmptyMovieClip('ColorBox', mcInfo.getNextHighestDepth());
		mcColorBox.lineStyle();
		mcColorBox.beginFill(colorValue, 100);
		this.createBox(mcColorBox, 0, 0, boxHeight, boxHeight);
		mcColorBox.endFill();
		//===============================//
		//Box border to generate knockout dropShadow effect
		mcColorBoxBorder = mcInfo.createEmptyMovieClip('ColorBoxBorder', mcInfo.getNextHighestDepth());
		mcColorBoxBorder.lineStyle(1, this.borderColor);
		mcColorBoxBorder.beginFill(0xFFFFFF, 100);
		this.createBox(mcColorBoxBorder, 2, 2, boxHeight-4, boxHeight-4);
		
		//Apply effect to colorBox
		var myDropFilter = new DropShadowFilter();
		myDropFilter.inner = true;
		myDropFilter.distance = 1;
		myDropFilter.knockout = true;
		myDropFilter.alpha = 0.4;
		var myFilters:Array = mcColorBoxBorder.filters;
		myFilters.push(myDropFilter);
		mcColorBoxBorder.filters = myFilters;
		
		//Outer border
		var mcColorBoxBorderOuter:MovieClip = mcInfo.createEmptyMovieClip('ColorBoxBorderOuter', mcInfo.getNextHighestDepth());
		mcColorBoxBorderOuter.lineStyle(2, 0xFFFFFF, 100, false, "normal", "none", "miter");
		mcColorBoxBorderOuter.beginFill(0xFFFFFF, 0);
		this.createBox(mcColorBoxBorderOuter, 1, 1, boxHeight-2, boxHeight-2);
		
		
		//Mask for color swatch
		var mcMask:MovieClip = mcInfo.createEmptyMovieClip('mask', mcInfo.getNextHighestDepth());
		mcMask.lineStyle(0);
		mcMask.beginFill(0xFFFFFF, 100);
		this.createBox(mcMask, 2, 2, boxHeight-4, boxHeight-4);
		
		mcColorBox.setMask(mcMask);
		
		//Final effect to colorBox
		var myDropFilter = new DropShadowFilter();
		myDropFilter.distance = 1;
		myDropFilter.alpha = 0.6;
		myDropFilter.blurX = 1;
		myDropFilter.blurY = 1;
		var myFilters:Array = mcColorBoxBorderOuter.filters;
		myFilters.push(myDropFilter);
		mcColorBoxBorderOuter.filters = myFilters;
		
		//Draw the base of arrow
		var divX:Number = 3;
		var divY:Number = 3.5;
		var mcColorBoxArrow:MovieClip = mcInfo.createEmptyMovieClip('ColorBoxArrow', mcInfo.getNextHighestDepth());
		mcColorBoxArrow.lineStyle();
		mcColorBoxArrow.beginFill(0xFFFFFF, 100);
		this.createBox(mcColorBoxArrow, boxHeight - boxHeight/divX, boxHeight - boxHeight/divY, boxHeight/divX, boxHeight/divY);
		//Draw the arrow
		mcColorBoxArrow.beginFill(0x0, 100);
		mcColorBoxArrow.moveTo(boxHeight - boxHeight/divX + 2, boxHeight - boxHeight/divY + 2);
		mcColorBoxArrow.lineTo(boxHeight - boxHeight/(divX*2) + 0.5, boxHeight - 1);
		mcColorBoxArrow.lineTo(boxHeight-1, boxHeight - boxHeight/divY + 2);
		mcColorBoxArrow.lineTo(boxHeight - boxHeight/divX + 2, boxHeight - boxHeight/divY + 2);
		mcColorBoxArrow.endFill();
		//==============================//
		//TextField ccreated to hold the current color value
		txtValue = mcInfo.createTextField('ValueTxtBox', mcInfo.getNextHighestDepth(), mcColorBox._x + mcColorBox._width + 3*gutter, 0, 60, boxHeight);
		//Fffect applied to it
		var myDropFilter = new DropShadowFilter();
		myDropFilter.inner = true;
		myDropFilter.distance = 1;
		myDropFilter.alpha = 0.6;
		var myFilters:Array = txtValue.filters;
		myFilters.push(myDropFilter);
		txtValue.filters = myFilters;
		
		//===============================//
		//Container to hold the spectrum
		mcSpectrum = mcColorPicker.createEmptyMovieClip('spectrum', mcColorPicker.getNextHighestDepth());
		//And made invisible initially
		mcSpectrum._visible = false;
		//Draw the spectrum
		this.createSpectrum(mcSpectrum, this.spectrumWidth, this.spectrumHeight);
		
		//Set x position to match required alignment
		if(this.rightAlignSpectrum){
			mcSpectrum._x = txtValue._x + txtValue._width - mcSpectrum._width;
		}
		mcSpectrum._y = txtValue._y + txtValue._height + gutter;
		
		//Nullify flash rendering issue related to fractional x position and/or y position w.r.t stage
		var p:Object = {x:mcSpectrum._x, y:mcSpectrum._y};
		//Spectrum position converted to global
		mcSpectrum.localToGlobal(p);
		//Global position rounded
		p.x = Math.round(p.x);
		p.y = Math.round(p.y);
		//Spectrum position converted to local
		mcSpectrum.globalToLocal(p);
		//Position reset with adjusted values.
		mcSpectrum._x = p.x;
		mcSpectrum._y = p.y;
		
		//Effect apllied to spectrum
		var myDropFilter = new DropShadowFilter();
		myDropFilter.distance = 2;
		myDropFilter.alpha = 0.6;
		var myFilters:Array = mcSpectrum.filters;
		myFilters.push(myDropFilter);
		mcSpectrum.filters = myFilters;
	}
	
	/**
	 * portrayTransparency method draws the graphical presentation for transparemcy.
	 */
	private function portrayTransparency(){
		//To add transparency symbol to colorBox, the color must be removed first
		this.mcColorBox.transform.colorTransform = new ColorTransform();
		//Then graphics cleared
		mcColorBox.clear();
		//Transparency symbol drawn
		mcColorBox.lineStyle();
		mcColorBox.beginFill(0xFFFFFF, 100);
		this.createBox(mcColorBox, 0, 0, boxHeight, boxHeight);
		mcColorBox.endFill();
		mcColorBox.lineStyle(2, 0xFF0000, 100, false, "normal", "none");
		mcColorBox.moveTo(boxHeight-1, 1);
		mcColorBox.lineTo(1, boxHeight-1);
		
	}
	/**
	 * startColorPicking method enables color picking.
	 */
	private function startColorPicking(){
		//Indicate that color can be picked
		this.thisRef.pick = true;
		//Hide mouse pinter and attach custom cursor
		Mouse.hide();
		this.thisRef.mcCursor._visible = true;
	}
	/**
	 * stopColorPicking method disables color picking.
	 */
	private function stopColorPicking(){
		//Indicate that color can't be picked
		this.thisRef.pick = false;
		//Reset back latest color value
		this.thisRef.setColors(this.thisRef.colorValue);
		//Resume normal mouse cursor
		Mouse.show();
		//Hide custom cursor
		this.thisRef.mcCursor._visible = false;
	}
	/**
	 * pickColor method picks color.
	 */
	private function pickColor(){
		//If color can be picked
		if(this.thisRef.pick){
			var thisRef = this.thisRef;
			//If mouse is outside of spectrum, don't pick color
			if(thisRef.mcSpectrum._xmouse < 0 || thisRef.mcSpectrum._width < thisRef.mcSpectrum._xmouse || thisRef.mcSpectrum._ymouse < 0 || thisRef.mcSpectrum._height < thisRef.mcSpectrum._ymouse){
				return;
			}
			//Update custom cursor position with mouse move
			thisRef.mcCursor._x = thisRef.mcSpectrum._x + thisRef.mcSpectrum._xmouse;
			thisRef.mcCursor._y = thisRef.mcSpectrum._y + thisRef.mcSpectrum._ymouse;
			//Get the color value of the pixel under mouse pointer
			var colorValue:Number = thisRef.bmpd.getPixel(thisRef.mcSpectrum._xmouse, thisRef.mcSpectrum._ymouse);
			//For valid color value
			if(!isNaN(colorValue)){
				//Set color value in info boxes
				thisRef.setColors(colorValue, true);
			}
		}
	}
	/**
	 * setColors method updates front end of the component for latest color.
	 * @param colorValue	latest color to be reflected
	 * @param noUpdate		if this color be stored as last color selected 
	 * 
	 */
	private function setColors(colorValue:Number, noUpdate:Boolean){
		//Set color in color box
		var clrTrans:ColorTransform = new ColorTransform();
		clrTrans.rgb = colorValue;
		this.mcColorBox.transform.colorTransform = clrTrans;
		//Set the color value display as hexadecimal
		this.txtValue.text = this.formatColorString(colorValue.toString(16));
		//Store the current color value
		if(!noUpdate){
			this.colorValue = colorValue;
		}
	}
	/**
	 * formatColorString method formats the string version of color with preceding zeros.
	 * @param strColor		the color string to be formatted
	 * @return 		formatted color string
	 * 
	 */
	private function formatColorString(strColor:String):String{
		//Take no action for if color value not valid, meaning transparency
		if(this.needTransparency && strColor == '-1'){
			return '';
		}
		//Add preceding zeros to color string to make it a 6 digit one
		while(strColor.length < 6){
			strColor = '0' + strColor;
		}
		return strColor.toUpperCase();
	}
	/**
	 * createSpectrum method creates the spectrum to select the color from using mouse.
	 * @param canvas_mc		mc to create in
	 * @param width			width of mc
	 * @param height		height of mc
	 * 
	 */
	private function createSpectrum(canvas_mc:MovieClip, width:Number, height:Number) {
		//depth/height of gray scale (horizontal) band to be added at upper end of the spectrum
		var heightGrayScale:Number = height/10;
		
		//Draw gray scale band
		var arrColors:Array = [0x0, 0xFFFFFF];
		var arrAlphas:Array = [100, 100];
		var arrRatios:Array = [0, 255];
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(width, heightGrayScale);
		
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', arrColors, arrAlphas, arrRatios, matrix);
		
		this.createBox(canvas_mc, 0, 0, width, heightGrayScale);
		canvas_mc.endFill();
		
		//=========================================//
		//Draw linear horizontal spectrum of 6 primary colors (red, yellow, green, cyan, blue, magenta) [spectrum ending with starting color red to complete the cycle]
		arrColors = [0xFF0000, 0xFFFF00, 0x00FF00, 0x00FFFF, 0x0000FF, 0xFF00FF, 0xFF0000];
		arrAlphas = [100, 100, 100, 100, 100, 100, 100];
		arrRatios = [0, Math.round(255 / 6), Math.round(2 * 255 / 6), Math.round(3 * 255 / 6), Math.round(4 * 255 / 6), Math.round(5 * 255 / 6), 255];
		matrix = new Matrix();
		matrix.createGradientBox(width, height - heightGrayScale, 0, 0, heightGrayScale);
		
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', arrColors, arrAlphas, arrRatios, matrix);
		
		this.createBox(canvas_mc, 0, heightGrayScale, width, height - heightGrayScale);
		canvas_mc.endFill();
		//==========================================//
		//Draw vertical overlay spectrum to add variation of saturation upwards from center [saturation zero at top and max at center], while brightness varies downwards from center [brightness max] to bottom [brightness 0]
		arrColors = [0xFFFFFF, 0xFFFFFF, 0x0, 0x0];
		arrAlphas = [100, 0, 0, 100];
		arrRatios = [0, 127, 127, 255];
		matrix = new Matrix();
		matrix.createGradientBox(width, height, Math.PI / 2, 0, heightGrayScale);
		
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', arrColors, arrAlphas, arrRatios, matrix);
		
		this.createBox(canvas_mc, 0, heightGrayScale, width, height - heightGrayScale);
		canvas_mc.endFill();
		
		//Thus spectrum created
		//==========================================//
		//Bitmap created based on the vector spectrum
		this.bmpd = new BitmapData(canvas_mc._width, canvas_mc._height);
		this.bmpd.draw(canvas_mc);
		//Vector counterpart of spectrum flushed
		canvas_mc.clear();
		//Bitmap is set for use to retrive color of pixel uner mouse pointer
		canvas_mc.attachBitmap(bmpd, canvas_mc.getNextHighestDepth());
		//==========================================//
		//Border added to spectrum
		var spectrumBorder:MovieClip = canvas_mc.createEmptyMovieClip('border', canvas_mc.getNextHighestDepth());
		spectrumBorder.lineStyle(0, this.borderColor);
		this.createBox(spectrumBorder, 0, 0, width, height);
		
	}
	/**
	 * createBox method draws line rectangle.
	 * @param canvas_mc		mc to draw in
	 * @param tx			x shift to start from
	 * @param ty			y shift to start from
	 * @param width			width of rectangle
	 * @param height		height of rectangle
	 * 
	 */
	private function createBox(canvas_mc:MovieClip, tx:Number, ty:Number, width:Number, height:Number){
		canvas_mc.moveTo(tx, ty);
		canvas_mc.lineTo(tx, ty + height);
		canvas_mc.lineTo(tx + width, ty + height);
		canvas_mc.lineTo(tx + width, ty);
		canvas_mc.lineTo(tx, ty);
	}
	/**
	 * createCursor method creates the custom cursor for mouse on spectrum.
	 */
	private function createCursor(){
		var radius:Number = 5;
		
		mcCursor = this.mcColorPicker.createEmptyMovieClip('cursor', this.mcColorPicker.getNextHighestDepth());
		mcCursor.lineStyle(0);
		mcCursor.moveTo(-radius, 0);
		mcCursor.lineTo(radius, 0);
		mcCursor.moveTo(0, -radius);
		mcCursor.lineTo(0, radius);
		
		mcCursor._visible = false;
		//Inverted blendmode applied to cursor for better visibility
		mcCursor.blendMode = 10;
	}
}
