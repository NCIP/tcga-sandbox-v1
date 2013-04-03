/**
* @class GradientLegend
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* GradientLegend class creates a special type of gradient bar based
* on the combination of colors and numeric value.This class is based 
* on AdvancedLegend.This enables the instance of this class to have the
* single icon legend functionalities as well.
*/

import com.fusioncharts.helper.AdvancedLegend;
import com.fusioncharts.helper.Utils;
import com.fusioncharts.extensions.StringExt;
import com.fusioncharts.extensions.MathExt;

import flash.geom.Matrix;
import flash.display.BitmapData;
import flash.geom.ColorTransform;
import flash.geom.Transform;

class com.fusioncharts.helper.GradientLegend extends AdvancedLegend {
	
	//the booelan value to decide whether to use gradient bar or singular legend icon
	private var isGradient:Boolean; 
	
	private var _fixedDimention:Number;
	private var _gradients : Array;
	private var _colorRanges : Array;
	private var _reverseLegend : Boolean;
	private var _decimalPrecision:Number;
	private var _snap:Boolean;
	private var _snapRange:Number;
	
	private var _legendItemCosmetics:Object;
	
	private var scaleObj:Object;
	private var savedScaleObj : Object;
	
	private var valuesToSnap:Array = new Array();
	
	//the default width and height of each gradient between 2 colors
	private var gradWidth : Number;
	private var gradHeight : Number;
	//the scale width and height;
	private var scaleWidth : Number = 0;
	private var scaleHeight : Number = 0;
	private var minScaleWidth : Number;
	private var minScaleHeight : Number;
	private var minTickerLabel:Number;
	private var maxTickerLabel:Number;
	
	//padding
	private var vPadding : Number = 10;
	private var hPadding : Number = 10;
	
	private var secondPointer_depth : Number;
	private var firstPointerLabel_depth : Number;
	private var secondPointerLabel_depth : Number;
	
	private var gradientScaleMC : MovieClip;
	private var gradientScaleBorderMC:MovieClip;
	private var tickMC : MovieClip;
	private var firstPointerMC : MovieClip;
	private var secondPointerMC : MovieClip;
	private var firstPointerCentralBoxMC : MovieClip;
	private var secondPointerCentralBoxMC : MovieClip;
	private var dragController : MovieClip;
	private var firstPointerLabelContMC:MovieClip;
	private var secondPointerLabelContMC:MovieClip;
	private var firstTickerLabel : TextField;
	private var secondTickerLabel : TextField;
	private var labelHolder : MovieClip;
	private var startLabelHolder : MovieClip;
	private var endLabelHolder : MovieClip;
	
	private var xIni:Number;
	private var yIni:Number;
	
	
	/**
	* Constructor function to initialize and store parameters.
	*	@param	targetMC		Parent movie clip in which we've to draw legend
	*	@param	isGradient			This param defines whether single color or gradient scale.
	*	@param	fontProp		An object containing the following font properties:
	*							- bold
	*							- italic
	*							- underline
	*							- font
	*							- size
	*							- color
	*							- isHTML
	*							- leftMargin
	*	@param	clickMode		Whether to use the legend in click mode?
	*	@param	alignPos		Alignment position of legend - BOTTOM or RIGHT
	*	@param	xPos			Center X Position of the legend
	*	@param	yPos			Center Y Position of the legend
	*	@param	maxWidth		Maximum width that the legend can assume.
	*	@param	maxHeight		Maximum height that the legend can assume
	*	@param	allowDrag		Flag whether the legend should be allowed to drag
	*	@param	stageWidth		Width of the container stage
	*	@param	stageHeight		Height of the container stage
	*	@param	bgColor			Background color (if any) for the legend
	*	@param	bgAlpha			Background alpha
	*	@param	borderColor		Border color of the legend box.
	*	@param	borderThickness	Border thickness (in pixels).
	*	@param	borderAlpha		Border Alpha of the box.
	*	@param	scrollBgColor	Background color of scroll bar
	*	@param	scrollBarColor	Scroll bar color
	*	@param	btnColor		Scroll Button Color
	*	@param	numColumns		Number of columns
	*/
	function GradientLegend (targetMC:MovieClip, isGradient:Boolean,
							 fontProp:Object, clickMode:Boolean,
							  alignPos:String, xPos:Number, yPos:Number,
							  maxWidth:Number, maxHeight:Number, allowDrag:Boolean,
							  stageWidth:Number, stageHeight:Number,
							  bgColor:String, bgAlpha:Number,
							  borderColor:String, borderThickness:Number, borderAlpha:Number,
							  scrollBgColor:String, scrollBarColor:String, btnColor:String,
							  numColumns:Number)
	{
		super(targetMC, fontProp, clickMode, alignPos, xPos, yPos, maxWidth, maxHeight, allowDrag, stageWidth, 
			  stageHeight, bgColor, bgAlpha, borderColor, borderThickness, borderAlpha, scrollBgColor, scrollBarColor, btnColor, numColumns);
		
		this.isGradient = isGradient;
		
		_gradients = new Array();
		
		//set defaults for the width and height of each gradient
		gradWidth = Math.floor(maxWidth/10);
		gradHeight = Math.floor(maxHeight/10);
		//set default for the minimum scale width and height
		//max limit is already defined in maxWidth & maxHeight
		minScaleWidth = Math.floor(maxWidth*0.5);
		minScaleHeight = Math.floor(maxHeight*0.5);
	}
	
	/* getter function to get the gradient scale values
	 *
	 * @return 	The object containing all the property of the scale.
	 */
	public function get scaleObject():Object{
		return scaleObj;
	}
	/* setter function to easy overwriting of scale values
	 *	@param	obj		The scale object to overwrite all the internal Scale property
	 */
	public function set scaleObject(obj:Object):Void{
		savedScaleObj = obj;
	}
	
	/*
	 * fixedDimention is the property where we store the fixed dimention of this
	 * gradient scale.Depending on the position this could be width or height
	 *
	 * @param	dim		The fixed part (widht or height) of the scale.
	 */
	public function set fixedDimension(dim : Number) : Void{
		_fixedDimention = dim;
	}
	
	/*
	 * set colorRanges function sets the actual colorRanges defined/parsed from the Data XML
	 * @param	cR	The array of color ranges for the legend.
	 */
	public function set colorRanges(cR : Array) : Void{
		_colorRanges = cR;
		_colorRanges.minRangeVal = _colorRanges[0].minValue;
		_colorRanges.maxRangeVal = _colorRanges[(_colorRanges.length - 1)].maxValue;
	}
	
	/*
	 * setter function for reverseLegend
	 * @param	 val	whether to set the legend in reverse order
	 *					(At present inapplicable to GradientLegend.)			
	 */
	public function set reverseLegend(val : Boolean) : Void{
		_reverseLegend = val;
	}
	
	/*
	 * getter function for reverse Legend
	 * @returns		Whether the gradient legend is in reverse order.
	 *				(At present inapplicable to GradientLegend.)
	 */
	public function get reverseLegend() : Boolean{
		return _reverseLegend;
	}
	
	/*
	 * setter function for decimal precision
	 *	@param	precision	The number of precison to set after the decimal.
	 */
	public function set decimalPrecision(precision:Number):Void{
		_decimalPrecision = precision;
	}
	
	/*
	 * getter function for decimal precision
	 *	@returns	The number of precision set in the gradient legend.
	 */
	public function get decimalPrecision():Number{
		return _decimalPrecision;
	}
	
	/*
	 * setter function for the cosmetics property for the gradient legend. 
	 * @params	cosmetic	Object containing all the cosmetics of the legend.
	 */
	public function set legendItemCosmetics(cosmetics:Object):Void{
		_legendItemCosmetics = cosmetics;
	}
	
	/**
	* getter function for the cosmetics property for the gradient legend.
	* @returns	Object containing all the cosmetics of the legend.
	**/
	public function get legendItemCosmetics():Object{
		return _legendItemCosmetics;
	}
	
	/*
	 * setter function for legend spanning option
	 *	@param	val	Whether to set or remove snapping in the legend.
	 */
	public function set snap(val:Boolean):Void{
		_snap = val;
	}
	
	/*
	 * getter function for legend spanning option
	 * @returns		The present snap value of the Legend.
	 */
	public function get snap():Boolean{
		return _snap;
	}
	
	/*
	 * setter function to assign the percentage value for snapping pointers
	 *	@param	val		The percentage value of the scale for the snapping range.
	 */
	public function set snapRange(val:Number):Void{
		_snapRange = val;
	}
	
	/*
	 * getter function to retrieve the percentage value for snapping pointers
	 *	@returns	The present snapping value of the scale in percentage.
	 */
	public function get snapRange():Number{
		return _snapRange;
	}
	/**
	* initContainers create all the container clips for this legend.
	**/
	private function initContainers():Void{
		//create sub clips.
		gradientScaleMC = mcBase.createEmptyMovieClip("gradientScaleMC",1);
		gradientScaleBorderMC = mcBase.createEmptyMovieClip("gradientScaleBorderMC", 2)
		tickMC = mcBase.createEmptyMovieClip("tickMC",3);
		labelHolder = mcBase.createEmptyMovieClip("labelHolder",4);
		startLabelHolder = mcBase.createEmptyMovieClip("startLabelHolder",5);
		endLabelHolder = mcBase.createEmptyMovieClip("endLabelHolder",6);
		firstPointerMC = mcBase.createEmptyMovieClip("firstPointerMC",7);
		secondPointerMC = firstPointerMC.duplicateMovieClip("secondPinter", 8);
		// depth 6 & 7 are reserved for the two pointer labels.
		firstPointerLabel_depth = 9;
		secondPointerLabel_depth = 10;
		dragController = mcBase.createEmptyMovieClip("dragController",11);
		
		
	}
	
	/**
	* addItem adds a legend item to be displayed.
	* Overwritten as we have to inculde two more parameters in min value and max value
	*	@param	itemName		String value to be displayed
	*	@param	itemId			Internal Item ID - for use when the legend items
	*							are click-able.
	*	@param	active			Whether the initial state of item is active
	*	@param	bmpd1			active icon bitmapdata
	*	@param	bmpd2			inactive icon bitmapdata
	*	@param	minVal			The minimum value.
	*	@param	maxValue		The Maximum value.
	*/
	public function addItem (itemName:String, itemId:Number, active:Boolean, bmpd1:BitmapData, bmpd2:BitmapData, minVal:Number, maxVal:Number){
		//We store each item as an object in items array.
		//Create an object from the given parameters and store it
		var objItem:Object = new Object ();
		//Replace < with &lt; if not in HTML mode
		itemName = StringExt.replace (itemName, "<", "&lt;");
		
		objItem.name = itemName;
		objItem.id = itemId;
		objItem.active = active;
		objItem.bmpd1 = bmpd1;
		objItem.bmpd2 = bmpd2;
		objItem.minVal = minVal;
		objItem.maxVal = maxVal;
		//Map the item's chart specific index to legend specific index
		this.arrIdMap[itemId] = count;
		
		//Store it in items array
		this.items.push (objItem);
		count ++;
		
		//Delete
		delete objItem;
	}
	
	/**
	 * getDimensions method is called to get back metrics of space required
	 * to render the legend. Its here that the legend is actually created.
	 * depending on the type of legend we are either going to draw the gradient
	 * scale or we will be using the parent class getDimensions function
	 *
	 * @return		Object with legend width and height
	 */
	public function getDimensions():Object{
		var dimension : Object;
		
		if(this.isGradient){
			// we are drawing a gradient and we will not add any item to its parent advanceLegend class which do its work based on the count
			// property.We update that property to 4 as mostly we will create 4 types of fixed elements[gradientBar, scale, pointers and labels]. 
			this.count = 4;
			//Legend is made invisible for now.
			this.legendMC._visible = false;
			//create the container clips for all the elements.
			initContainers();
			//Create and set caption if specified.
			if(this.caption){
				this.captionTxt = this.createCaption(mcBase);
				if(this.captionTxt._width > this.maxWidth){
					this.captionTxt._width = this.maxWidth;
					this.captionTxt.wordWrap = true;
				}
			}
			//get the scale's width and height.
			dimension = getGradientScaleDimention();
			//set up the scale
			scaleObj = setupScaleParameters(dimension.width, dimension.height);
			
			//Now we have the scale dimention and the max and min value draw the gradient scale
			drawGradientScale();
			//draw labels
			drawLabels();
			//drawTickers
			drawScaleTickers();
			//drawPointers
			createPointers();
			//draw poiters labels
			drawPointersLabel();
			
			//now calculte the final width and height of this legend
			if(this.alignPos == "BOTTOM"){	
				this.width = gradientScaleMC._width + startLabelHolder._width + endLabelHolder._width + (hPadding*2);
				this.height = gradientScaleMC._height + tickMC._height + firstPointerMC._height + (vPadding*2);
				//add the caption height if caption is set
				this.height = (this.caption) ? (this.height + this.captionTxt._height) : this.height;
			}else{
				// [NOTE] poiters are rotated..
				this.width = gradientScaleMC._width + tickMC._width + firstPointerMC._height + (hPadding * 2);
				this.height = gradientScaleMC._height + startLabelHolder._height + endLabelHolder._height +  (hPadding * 2);
				//add the caption height if caption is set
				this.height = (this.caption) ? (this.height + this.captionTxt._height) : this.height;
			}
			//Apply the pertinent width for the caption (if any) and enable wrapping here too
			if(this.caption){
				/* Now we must try to render the caption in one line. Like we had in V3.1. Thus in case the required
				*  Width to render the caption is less than the max permissible width but greater than the calculated total
				*  width to render all the items. We should increase the total width.
				*  possible cases can be :
				*  1. required width for caption < maxWidth && required width for caption > width for all item
						We assign the width required to render the caption
				*  2. required width for caption > maxWidth && required width for caption > width for all item
						We have to wrap the caption to a max permissible width
				*  3. required width for caption > maxWidth && required width for caption < width for all item
						We assign the width required to render all the items.
				*/
				//required width is greater than the required width to render all items
				if(this.captionTxt._width > this.width){
					var tempFinalWidth:Number = this.width;
					//required width for caption is less than max width
					if(this.captionTxt._width < this.maxWidth){
						this.width = this.captionTxt._width;
					//required width for caption is greater than max width
					}else{
						this.width = this.maxWidth;
						this.captionTxt._width = this.maxWidth;
						this.captionTxt.wordWrap = true;
					}
					xIni = (this.width - tempFinalWidth) / 2;
				}else{
					//We assign the total width required to render all the items and wordwrap the caption
					this.captionTxt._width = this.width;
					this.captionTxt.wordWrap = true;
				}
			}
			//---------- arrange scale items -----------//
			xIni = (xIni == undefined) ? 0 : xIni;
			//The starting y position for placing items
			yIni = (this.caption) ? this.captionTxt._height : 0;
			//The height of total legend with caption (if any) and arranged items
			var itemsBaseHeight:Number = mcBase._height;
			//asign the final width
			super.width = dimension.width = this.width;
			super.height = dimension.height = this.height;
		}else{
			//if legacy advanced legend implementation call the super function.
			dimension = super.getDimensions();
		}
		
		return dimension;
	}
	
	/*
	* render method finally renders the gradient legend.
	*/
	public function render(isResize:Boolean):Void{
		//call the parent class render to create basic items.
		super.render();
		
		//Extra functionalities.. REPOSITIONING 
		if(this.isGradient){
			//reposition the mcBase clip and its elements (centrally aligned)
			if(this.alignPos == "BOTTOM"){
				//starting x
				startLabelHolder._x = xIni + startLabelHolder._width / 2 + hPadding;
				//starting y
				var firstObjEndY:Number = (this.caption) ? this.captionTxt._height : 0;
				firstPointerMC._y = secondPointerMC._y = firstObjEndY + firstPointerMC._height / 2 + vPadding;
				// x alignments
				gradientScaleMC._x = tickMC._x = labelHolder._x = firstPointerMC._x = startLabelHolder._x + (startLabelHolder._width/2) + hPadding / 4;
				gradientScaleBorderMC._x = gradientScaleMC._x;
				secondPointerMC._x = gradientScaleMC._x + gradientScaleMC._width;
				endLabelHolder._x = (gradientScaleMC._x + gradientScaleMC._width) + (endLabelHolder._width/2)// + hPadding / 4;
				// y alignments
				gradientScaleMC._y = firstPointerMC._y + firstPointerMC._height / 2;
				gradientScaleBorderMC._y = gradientScaleMC._y;
				startLabelHolder._y = endLabelHolder._y = labelHolder._y = gradientScaleMC._y + (gradientScaleMC._height - labelHolder._height) / 2;
				tickMC._y = gradientScaleMC._y + gradientScaleMC._height + vPadding/4;
			}else{
				//rotation
				firstPointerMC._rotation = secondPointerMC._rotation = 270;
				//starting x
				firstPointerMC._x =secondPointerMC._x =  xIni + firstPointerMC._width / 2 + hPadding;
				//starting y
				var firstObjEndY:Number = (this.caption) ? this.captionTxt._height : 0;
				startLabelHolder._y = firstObjEndY + startLabelHolder._height / 2 + vPadding;
				//x alignments
				gradientScaleMC._x = firstPointerMC._x + firstPointerMC._width/2;
				gradientScaleBorderMC._x = gradientScaleMC._x;
				//firstTickerLabel._x = secondTickerLabel._x = firstPointerMC._x + firstPointerMC._height / 2;
				startLabelHolder._x = endLabelHolder._x = gradientScaleMC._x + gradientScaleMC._width / 2;
				labelHolder._x = gradientScaleMC._x; 
				tickMC._x = gradientScaleMC._x + gradientScaleMC._width + hPadding/4;
				//y alignments
				gradientScaleMC._y = tickMC._y = labelHolder._y = firstPointerMC._y = startLabelHolder._y + (startLabelHolder._height / 2) + vPadding/4;
				gradientScaleBorderMC._y  = gradientScaleMC._y;
				secondPointerMC._y = gradientScaleMC._y + gradientScaleMC._height;
				endLabelHolder._y = (gradientScaleMC._y + gradientScaleMC._height)+ (startLabelHolder._height/2) + vPadding / 4;
			}
			//for ticker label alignment
			moveTickerLabels();
			updateScaleProp();
			//to assign the color to the pointer depending on its position
			updatePointersColor(firstPointerMC);
			updatePointersColor(secondPointerMC);
			//now if this rendering process is due to resizing overwrite the base properties with the previously stored values
			if(isResize){
				overwriteScaleProp();
			}
		}
	}
	
	/**
	* setupScaleParameters method sets the different parameters for the scale.
	* It gets the minimum and maximum value specified in gradients and
	* calciulates a few vital factors for the legend scale.
	*
	*	@param	scaleWidth	The Width of the Scale.
	*	@param	scaleHeight	The Height of the Scale.
	*
	*	@return				The base Scale object with all the required properties.
	*/
	private function setupScaleParameters(scaleWidth:Number, scaleHeight:Number):Object {
		//the object to return
		var scaleObj : Object = new Object();
		// lowest and highest limit
		scaleObj.lowerLimit  = this._gradients[0].minValue;
		scaleObj.upperLimit  = this._gradients[(this._gradients.length-1)].maxValue;
		
		//calculte the value of each scale unit in pixel
		scaleObj.width = scaleWidth;
		scaleObj.height = scaleHeight;
		scaleObj.scaleDiff = scaleObj.upperLimit - scaleObj.lowerLimit;
		scaleObj.pxUnit = (this.alignPos == "BOTTOM") ? scaleObj.width/scaleObj.scaleDiff : scaleObj.height/scaleObj.scaleDiff;
		//initial displacement
		scaleObj.iniDisplacemnt = scaleObj.lowerLimit * scaleObj.pxUnit;
		
		//get the difference in color ratio for this scale color ratio is defined in a range of 0-255
		scaleObj.clrRatioUnit = 255/scaleObj.scaleDiff;
		
		//other static attributes
		scaleObj.stopMaxAtZero  = false;
		scaleObj.setMinAsZero  = false;
		scaleObj.numMajorTM  = -1;
		scaleObj.numMinorTM  = 4;
		scaleObj.adjustTM  = true;
		scaleObj.tickValueStep  = 1;
		
		return scaleObj;
	}
	
	
	
	/**
	* getGradientScaleDimention is the function where we decide the width and height of the gradient scale.
	**/
	
	private function getGradientScaleDimention():Object{
		//Object to be returned.
		var rtnObj:Object = new Object ();
		var i : Number;
		//labels to render at the bothe side of the scale
		var LabelObj : Object;
		var strLbl : String = _colorRanges["gradientStartLabel"];
		var endLbl : String = _colorRanges["gradientEndLabel"];
		var finalWidth : Number, finalHeight : Number;
		//iterate on all the gradients.
		var totRanges : Number = _colorRanges.length;
		//Now the maximum length(width/height) of the gradient scale could not be more than
		//maximumWidth - (startLabelWidth + endLabelWidth)
		if(this.alignPos == "BOTTOM"){
			this.maxHeight = (this.caption) ? (this.maxHeight - this.captionTxt._height) : this.maxHeight;
			var maxLabelWidth:Number = Math.round(this.maxWidth / 5);
			LabelObj = Utils.createText(true, strLbl, startLabelHolder, 1, 0, 0, 0, this.fontProp, false); 
			var startLabelWidth:Number = (LabelObj.tf._width > maxLabelWidth) ? maxLabelWidth : LabelObj.tf._width;
			LabelObj = Utils.createText(true, endLbl, endLabelHolder, 1, 0, 0, 0, this.fontProp, false);
			var endLabelWidth:Number = (LabelObj.tf._width > maxLabelWidth) ? maxLabelWidth : LabelObj.tf._width;
			var maxGradientScaleLength:Number = this.maxWidth - (startLabelWidth + endLabelWidth);
		}else{
			this.maxHeight = (this.caption) ? (this.maxHeight - this.captionTxt._height) : this.maxHeight;
			
			var maxLabelHeight:Number = Math.round(this.maxHeight / 5);
			LabelObj = Utils.createText(false, strLbl, startLabelHolder, 1, 0, 0, 270, this.fontProp, false);
			var startLabelHeight:Number = (LabelObj.tf._height > maxLabelHeight) ? maxLabelHeight : LabelObj.tf._height;
			LabelObj = Utils.createText(false, endLbl, endLabelHolder, 1, 0, 0, 270, this.fontProp, false);
			var endLabelHeight:Number = (LabelObj.tf._height > maxLabelHeight) ? maxLabelHeight : LabelObj.tf._height;
			var maxGradientScaleLength:Number = this.maxHeight - (startLabelHeight + endLabelHeight);
		}
		//we do not need the textfield object any more.
		LabelObj.tf.removeTextField();
		
		for(i = 0; i<totRanges; i++){
			addGradients(_colorRanges[i], maxGradientScaleLength);
		}
		
		//now depending on the position of the legend we have to draw the scale
		if(this.alignPos == "BOTTOM"){
			finalWidth = (scaleWidth < minScaleWidth) ? minScaleWidth : scaleWidth;
			finalHeight = _fixedDimention;
		}else{
			finalWidth = _fixedDimention;
			finalHeight = (scaleHeight < minScaleHeight) ? minScaleHeight : scaleHeight;
		}
		
		rtnObj.width = finalWidth;
		rtnObj.height = finalHeight;
		
		return rtnObj;
	}
	
	/**
	* addGradients methods adds a gradient object in the gradients array we draw the scale based on this array 
	* and each gradient object. gradObj has the following values
	* 
	* 	startColor	- 	The starting color for the gradient.
	* 	startValue	- 	The starting value of the gradient scale.
	* 	endColor	-	The end Color value for the gradient.
	* 	endValue	- 	The end value of the gradient scale.
	*
	*	@param	gradObj				The Object containing all the gradient related properties
	*	@param	maxGradScaleLength	The maximum length(width or height) of the gradient
	**/
	
	private function addGradients(gradObj : Object, maxGradScaleLength:Number):Void{
		_gradients.push(gradObj);
		//update scale width and height
		if(this.alignPos == "BOTTOM") {
			scaleWidth += gradWidth;
			scaleWidth = (scaleWidth > maxGradScaleLength) ? maxGradScaleLength : scaleWidth;
		}else{
			scaleHeight += gradHeight;
			scaleHeight = (scaleHeight > maxGradScaleLength) ? maxGradScaleLength : scaleHeight;
		}
	}
	
	/*
	* updateScaleProp methods adds a few more properties to the base scale object for easy reference.
	*/
	private function updateScaleProp():Void{
		//first pointer's position
		scaleObj.frstPntrXPos = firstPointerMC._x;
		scaleObj.frstPntrYPos = firstPointerMC._y;
		//second pointer's position
		scaleObj.scndPntrXPos = secondPointerMC._x;
		scaleObj.scndPntrYPos = secondPointerMC._y;
		//first label's position
		scaleObj.frstLabelXPos = firstTickerLabel._x;
		scaleObj.frstLabelYPos = firstTickerLabel._y;
		scaleObj.frstLabelTxt = firstTickerLabel.text;
		//second label's position
		scaleObj.scndLabelXPos = secondTickerLabel._x;
		scaleObj.scndLabelYPos = secondTickerLabel._y;
		scaleObj.scndLabelTxt = secondTickerLabel.text;
	}
	
	/*
	*	overwriteScaleProp overwrites pointer's and its corresponding label's properties at the
	*  	time of resizing
	*/
	private function overwriteScaleProp():Void{
		//assign last labels stored
		firstTickerLabel.text = savedScaleObj.frstLabelTxt;
		secondTickerLabel.text = savedScaleObj.scndLabelTxt;
		//reposition
		if(this.alignPos == "BOTTOM"){
			firstPointerMC._x = ((Number(firstTickerLabel.text) - scaleObj.lowerLimit) * scaleObj.pxUnit) + gradientScaleMC._x;
			secondPointerMC._x = ((Number(secondTickerLabel.text) - scaleObj.lowerLimit) * scaleObj.pxUnit) + gradientScaleMC._x;
		}else{
			firstPointerMC._y = ((Number(firstTickerLabel.text) - scaleObj.lowerLimit) * scaleObj.pxUnit) + gradientScaleMC._y;
			secondPointerMC._y = ((Number(secondTickerLabel.text) - scaleObj.lowerLimit) * scaleObj.pxUnit) + gradientScaleMC._y;
		}
		moveTickerLabels();
		updateScaleProp();
	}
	
	// ---------------------- DRAWING METHODS ----------------------------
	
	/**
	* drawGradientScale draws the main gradient scale with colors.
	**/
	private function drawGradientScale():Void{
		// initiate variables
		var i : Number;
		var staringGradientColor : String = _colorRanges["gradientStartColor"];
		var startingGradientValue : Number = _colorRanges["gradientStartValue"];
		var prevGradRatio : Number = 0;
		
		var outerBevelThickness : Number = 1;
		
		var matrix:Matrix = new Matrix();
		var mtrxWidth:Number = scaleObj.width;
		var mtrxHeight:Number = scaleObj.height;
		var mtrxTx:Number = 0;
		var mtrxTy:Number = 0;
		var mtrxRotation : Number = (this.alignPos == "BOTTOM") ? 0 : Math.PI/2;
		
		var colorsArray : Array = new Array();
		colorsArray.push(parseInt(staringGradientColor, 16));
		var alphaArray : Array = new Array();
		alphaArray.push(100);
		var ratioArray : Array = new Array();
		ratioArray.push(0);
		
		//iterate through all the gradients..
		var numGradients : Number = _gradients.length;
		for(i = 0; i<numGradients; i++){
			// now we have the total width or height of the scale and all the gradients with max & min value.Calculate each gradient's length
			var gradientLength : Number = _gradients[i].maxValue - _gradients[i].minValue;
			//now convert this value for the gradient's ratio array in a scale of 0-255
			var gradRatioDiff : Number = gradientLength * scaleObj.clrRatioUnit;
			//as ratio should be continuously incremented.
			var gradRatio : Number = Math.floor(prevGradRatio + gradRatioDiff);
			prevGradRatio = gradRatio;
			//prepare the three necessary arrays for gradient drawing.
			colorsArray.push( parseInt(_gradients[i].color, 16));
			//colorsArray.push(_gradients[i].color);
			alphaArray.push(100);
			ratioArray.push(gradRatio);
		}
		
		var maxGradColorNum:Number = 14;
		if(colorsArray.length > maxGradColorNum){
			var numOfSubGrads:Number = Math.ceil(colorsArray.length / maxGradColorNum);
			
			
			var subGradWidth = (this.alignPos == "BOTTOM") ? scaleObj.width / numOfSubGrads : scaleObj.width;
			//var subGradHeight = scaleObj.height / numOfSubGrads;
			var subGradHeight = (this.alignPos == "BOTTOM") ? scaleObj.height :  scaleObj.height / numOfSubGrads;
			var matrix:Matrix = new Matrix();
			
			for(i = 0; i<numOfSubGrads; i++){
				var startIndx:Number = 0;
				if(subClrsArr.length > 0){
					var lastColor:Number = subClrsArr[(subClrsArr.length - 1)];
					var lastAlpha:Number = subAlphArr[(subAlphArr.length - 1)];
					var lastRatio:Number = 0//subRatArr[(subRatArr.length - 1)];
					var subClrsArr:Array = colorsArray.splice(startIndx, (maxGradColorNum-1));
					var subAlphArr:Array = alphaArray.splice(startIndx, (maxGradColorNum-1));
					var subRatArr:Array = ratioArray.splice(startIndx, (maxGradColorNum-1));
					subClrsArr.unshift(lastColor);
					subAlphArr.unshift(lastAlpha);
					subRatArr.unshift(lastRatio);
				}else{
					var subClrsArr:Array = colorsArray.splice(startIndx, maxGradColorNum);
					var subAlphArr:Array = alphaArray.splice(startIndx, maxGradColorNum);
					var subRatArr:Array = ratioArray.splice(startIndx, maxGradColorNum);
				}
				
				var numOfRat:Number = subRatArr.length;
				var startRatio:Number = subRatArr[0];
				var endRatio:Number = subRatArr[(numOfRat - 1)];
				var ratioRange:Number = endRatio - startRatio;
				for(var j:Number = 0; j<numOfRat; j++){
					var rat:Number = subRatArr[j];
					var convrtRat:Number = Math.floor(((rat - startRatio) / ratioRange) * 255);
					subRatArr[j] = convrtRat//rat;
				}
				
				var startX:Number = (this.alignPos == "BOTTOM") ? subGradWidth * i : 0;
				var startY:Number = (this.alignPos == "BOTTOM") ? 0 : subGradHeight * i;
				var endX:Number = (this.alignPos == "BOTTOM") ? startX + subGradWidth : subGradWidth;
				var endY:Number = (this.alignPos == "BOTTOM") ? subGradHeight : startY + subGradHeight;
				
				with(gradientScaleMC){
				
					matrix.createGradientBox(subGradWidth, subGradHeight, mtrxRotation, startX, startY);
					
					beginGradientFill("linear", subClrsArr, subAlphArr, subRatArr, matrix);
					moveTo(startX, startY);
					lineTo(endX, startY);
					lineTo(endX, endY);
					lineTo(startX, endY);
					lineTo(startX, startY);
					
					
					endFill();
				}
			}
		}else{
			var matrix:Matrix = new Matrix();
			gradientScaleMC.lineStyle();
			//draw the gradient.
			matrix.createGradientBox(mtrxWidth, mtrxHeight, mtrxRotation, mtrxTx, mtrxTy);
			
			gradientScaleMC.beginGradientFill("linear", colorsArray, alphaArray, ratioArray, matrix);
			
			gradientScaleMC.moveTo(0, 0);
			gradientScaleMC.lineTo(scaleObj.width, 0);
			gradientScaleMC.lineTo(scaleObj.width, scaleObj.height);
			gradientScaleMC.lineTo(0, scaleObj.height);
			gradientScaleMC.lineTo(0, 0);
			
			gradientScaleMC.endFill();
			
		}
		
		// ---- Border -----
		//draw the border
		gradientScaleBorderMC.lineStyle(outerBevelThickness, 0x000000);
		gradientScaleBorderMC.moveTo(0, 0);
		gradientScaleBorderMC.lineTo(scaleObj.width, 0);
		gradientScaleBorderMC.lineTo(scaleObj.width, scaleObj.height);
		gradientScaleBorderMC.lineTo(0, scaleObj.height);
		gradientScaleBorderMC.lineTo(0, 0);
		//draw the shadow / light effect
		gradientScaleBorderMC.lineStyle(outerBevelThickness, 0x666666);
		gradientScaleBorderMC.moveTo(-outerBevelThickness, (scaleObj.height + outerBevelThickness));
		gradientScaleBorderMC.lineTo(-outerBevelThickness, -outerBevelThickness);
		gradientScaleBorderMC.lineTo((scaleObj.width + outerBevelThickness), -outerBevelThickness);
		
		gradientScaleBorderMC.lineStyle(outerBevelThickness, 0xFFFFFF);
		gradientScaleBorderMC.lineTo((scaleObj.width + outerBevelThickness), (scaleObj.height + outerBevelThickness));
		gradientScaleBorderMC.lineTo(-outerBevelThickness, (scaleObj.height + outerBevelThickness));
		
		// --- effect ----
		var effctMatrxRotation : Number = (this.alignPos == "BOTTOM") ? (Math.PI/2) : 0;
		gradientScaleMC.lineStyle();
		matrix.createGradientBox(mtrxWidth, mtrxHeight, effctMatrxRotation, mtrxTx, mtrxTy);
		gradientScaleMC.beginGradientFill("linear", [0xffffff, 0xFFFFFF, 0xCCCCCC, 0xCCCCCC, 0x333333], [30, 40, 10, 10, 30], [0, 60, 150, 150, 255], matrix);
		
		gradientScaleMC.moveTo(0, 0);
		gradientScaleMC.lineTo(scaleObj.width, 0);
		gradientScaleMC.lineTo(scaleObj.width, scaleObj.height);
		gradientScaleMC.lineTo(0, scaleObj.height);
		gradientScaleMC.lineTo(0, 0);
		
		gradientScaleMC.endFill();
	}
	
	/**
	* drawLabels draws all the labels.
	**/
	
	private function drawLabels():Void{
		//initiate variables
		var i : Number;
		var noOfLabels : Number = _gradients.length;
		var strLbl : String = _colorRanges["gradientStartLabel"];
		var endLbl : String = _colorRanges["gradientEndLabel"];
		
		if(this.alignPos == "BOTTOM"){
			//draw start and end label
			var labelWidth:Number = (this.maxWidth - (gradientScaleMC._width + (hPadding*2))) / 2;
			var labelHeight:Number = gradientScaleMC._height + tickMC._height// + (vPadding*2);
			
			if(strLbl != undefined || strLbl != "" || strLbl != null){
				var startLabelObj : Object = Utils.createText(false, strLbl, startLabelHolder, 1, 0, 0, 0, this.fontProp, false);
				//check whether the width exceed the maximum
				if(startLabelObj.tf._width > labelWidth){
					startLabelObj.tf.removeTextField();
					startLabelObj = Utils.createText(false, strLbl, startLabelHolder, 1, 0, 0, 0, this.fontProp, true, labelWidth, labelHeight);
				}
				//startLabelObj.tf.border = true;
				//vertically center align text..
				startLabelObj.tf._y += startLabelObj.tf._height / 2;
			}
			
			if(endLbl != undefined || endLbl != "" || endLbl != null){
				var endLabelObj : Object = Utils.createText(false, endLbl, endLabelHolder, 1, 0, 0, 0, this.fontProp, false);
				//check whether the width exceed the maximum
				if(endLabelObj.tf._width > labelWidth){
					endLabelObj.tf.removeTextField();
					endLabelObj = Utils.createText(false, endLbl, endLabelHolder, 1, 0, 0, 0, this.fontProp, true, labelWidth, labelHeight);
				}
				//endLabelObj.tf.border = true;
				//vertically center align text..
				endLabelObj.tf._y += endLabelObj.tf._height / 2;
			}
			//draw the range specific labels if any....
			var labels:Array = new Array();
			var frontAlignProp:String = fontProp.align;
			fontProp.align = "left";
			
			for(i=0; i<noOfLabels; i++){
				var gradLabel : String = _gradients[i].label;
				if(gradLabel != undefined && gradLabel != ""){
					var textLabel:String = gradLabel;
					var xPos : Number = (_gradients[i].maxValue * scaleObj.pxUnit) - scaleObj.iniDisplacemnt;
					var yPos : Number = 0;
					//create texts without wordwrap or ellipses.just align them initially 
					var txtObj : Object = Utils.createText(false, textLabel, labelHolder, i, xPos, yPos, 0, this.fontProp, false);
					//vertically middle align text..
					txtObj.tf._y += txtObj.tf._height / 2;
					//also store the depth of the field
					txtObj.tf.depth = i;
					//store the object for future reference.
					labels.push(txtObj);
				}
			}
			
			// now check and modify if textfield overlaps with each other.
			var nextRightLimit:Number = gradientScaleMC._x + gradientScaleMC._width;
			//travrse from the last one to the first
			var startNum : Number = labels.length-1;
			for(i = startNum; i>=0; i--){
				var txtObj : Object = labels[i];
				var xPos:Number = txtObj.tf._x;
				if((txtObj.tf._x + txtObj.tf._width) > nextRightLimit){
					var maxWidth:Number = nextRightLimit - txtObj.tf._x;
					//remove and redraw with wordwrap ellipses
					var textLabel:String = txtObj.tf.text;
					
					var yPos:Number = txtObj.tf._y;
					var depth:Number = txtObj.tf.depth;
					txtObj.tf.removeTextField();
					txtObj = Utils.createText(false, textLabel, labelHolder, depth, xPos, yPos, 0, this.fontProp, true, maxWidth, txtObj.height);
					
					//vertically middle align text..
					txtObj.tf._y += txtObj.tf._height / 2;
					labels[i] = txtObj;
				}
				//txtObj.tf.border = true;
				xPos = xPos - txtObj.tf._width / 2;
				txtObj.tf._x = (xPos <= 0) ? txtObj.tf._x : xPos;
				//update limit
				nextRightLimit = txtObj.tf._x;
			}
			
			fontProp.align = frontAlignProp;
		}else{
			//draw start and end label
			var labelHeight:Number = (this.maxHeight - (gradientScaleMC._height + (vPadding * 2))) / 2;
			var labelWidth:Number = gradientScaleMC._width + tickMC._width + (hPadding*2);
			
			if(strLbl != undefined || strLbl != "" || strLbl != null){
				var startLabelObj : Object = Utils.createText(false, strLbl, startLabelHolder, 1, 0, 0, 270, this.fontProp, false);
				//check whether the height exceed the maximum
				if(startLabelObj.tf._height > labelHeight){
					startLabelObj.tf.removeMovieClip();
					startLabelObj = Utils.createText(false, strLbl, startLabelHolder, 1, 0, 0, 270, this.fontProp, true, labelHeight, labelWidth);
				}
			}
			if(endLbl != undefined || endLbl != "" || endLbl != null){
				var endLabelObj : Object = Utils.createText(false, endLbl, endLabelHolder, 1, 0, 0, 270, this.fontProp, false);
				//check whether the height exceed the maximum
				if(endLabelObj.tf._height > labelHeight){
					endLabelObj.tf.removeMovieClip();
					endLabelObj = Utils.createText(false, endLbl, endLabelHolder, 1, 0, 0, 270, this.fontProp, true, labelHeight, labelWidth);
				}
			}
			//draw the range specific labels if any....
			var labels:Array = new Array();
			var frontLeftMarginProp:Number = fontProp.leftMargin;
			fontProp.leftMargin = 0;
			
			for(i=0; i<noOfLabels; i++){
				var textLabel = _gradients[i].label;
				if(gradLabel != undefined && gradLabel != ""){
					var xPos : Number = gradientScaleMC._width / 2;
					var yPos : Number = (_gradients[i].maxValue * scaleObj.pxUnit) - scaleObj.iniDisplacemnt;
					//initially craete text fields without wordwrap / ellipses
					var txtObj : Object = Utils.createText(false, textLabel, labelHolder, i, xPos, yPos, 270, this.fontProp, false);
					txtObj.tf._y = yPos;
					txtObj.tf.depth = i;
					//as with rotation there will be no text.Store the text separately on the object.
					txtObj.txtLabel = textLabel;
					labels.push(txtObj);
				}
			}
			//now check and modify if texts overlaps with each other
			var nextTopLimit:Number = gradientScaleMC._y;
			//travrse from the top to bootom
			var numLabels : Number = labels.length;
			for(i=0; i<numLabels; i++){
				var txtObj:Object = labels[i];
				var yPos:Number = txtObj.tf._y;
				if((txtObj.tf._y - txtObj.tf._height) < nextTopLimit){
					//remove and recreate with wordwrap / ellipses
					var maxWidth:Number = txtObj.tf._y - nextTopLimit;
					var txtLabel:String = txtObj.txtLabel;
					var xPos:Number = gradientScaleMC._width / 2;
					var depth:Number = txtObj.tf.depth;
					txtObj.tf.removeMovieClip();
					txtObj = Utils.createText(false, txtLabel, labelHolder, depth, xPos, yPos, 270, this.fontProp, true,maxWidth, gradientScaleMC._width);
					txtObj.tf._y = yPos;
				}
				//txtObj.tf.border = true;
				yPos = yPos + txtObj.tf._height / 2;
				txtObj.tf._y = (yPos >= (gradientScaleMC._y + gradientScaleMC._height)) ? txtObj.tf._y : yPos;
				//update limit.
				nextTopLimit = txtObj.tf._y;
			}
			
			fontProp.leftMargin = frontLeftMarginProp;
		}
	}
	
	/**
	* drawScaleTickers draws the major and minor tickers along with markers
	* on the legend gradient scale.
	**/
	
	private function drawScaleTickers():Void{
		//Major and minor ticker height.
		//No of major tickers are equal to the number of major gradient colors.
		var majorTickerLength : Number = 8;
		var textPadding : Number = 5;
		// no of major markers are 1 extra than the no of gradients
		// we are at present not providing any text marker for the minor ticks
		var numMajorTM : Number = _gradients.length+1;
		var rangeDiff : Number = _gradients[(_gradients.length-1)].maxValue - _gradients[0].minValue;
		var tickerThickness : Number = 1;
		var tickerColor : Number = 0x000000;
		var tickerAlpha : Number = 100;
		//get the labels...
		var textLabel : String = _colorRanges["gradientStartLabel"];
		//loop variable
		var i : Number;
		var j : Number;
		//depth couter for text marker
		var depthCounter : Number = 1;
		//line style
		tickMC.lineStyle(_legendItemCosmetics.legendScaleLineThickness, parseInt(_legendItemCosmetics.legendScaleLineColor, 16), _legendItemCosmetics.legendScaleLineAlpha);
		
		var opt_scalePxUnit : Number = scaleObj.pxUnit;
		var opt_sclaeIniDisplacement : Number = scaleObj.iniDisplacemnt;
		var opt_mapByPercent : Boolean = _colorRanges.mapbypercent;
		
		if(this.alignPos == "BOTTOM"){
			//draw the scale line..
			tickMC.moveTo(0, 0);
			tickMC.lineTo(scaleObj.width, 0);
			//required space to render a single digit
			var singleDigtVal : Object = Utils.createText(true,  "0", tickMC, depthCounter, 0, 0, 0, this.fontProp);
			var singleDigtSpace : Number = singleDigtVal.tf._width;
			singleDigtVal.tf.removeTextField();
			//get a middle value to approximate the average text height
			var avgTxtVal : String = String(MathExt.roundUp(_gradients[Math.ceil(_gradients.length / 2)].minValue, _decimalPrecision));
			avgTxtVal = (opt_mapByPercent) ? avgTxtVal+"%" : avgTxtVal;
			var avgTxt:Object = Utils.createText(true,  avgTxtVal, tickMC, depthCounter, 0, 0, 0, this.fontProp);
			//avgTxt.tf.autoSize = true;
			var textMrkrAvgWidth:Number = avgTxt.tf._width;
			avgTxt.tf.removeTextField()
			avgTxt = null;
			//calculating the available width for each text marker.
			var textMrkrWidth : Number = scaleObj.width / (_gradients.length - 1);
			var textMrkrHeight : Number = 40;
			//if average width of each text marker is greater than the available width 
			//we have to go for label management.
			if(textMrkrAvgWidth >= Math.floor(textMrkrWidth)){
				var alternateTicker:Boolean = true;
				var tickerTxtWidth:Number = textMrkrWidth;
				var tickerLblRotation:Number = 270;
				var tickerLblwrap:Boolean = false;
				//in case we have to make mojor-minor ticks we may assign the difference in this value.
				//At present tickers have same height
				var largeTickHeightDiff:Number = 5;
			}else{
				var alternateTicker:Boolean = false;
				var tickerTxtWidth:Number = textMrkrAvgWidth;
				var tickerLblRotation:Number = 0;
				var tickerLblwrap:Boolean = true
				//in case we have to make mojor-minor ticks we may assign the difference in this value.
				//At present tickers have same height
				var largeTickHeightDiff:Number = 0;
			}
			
			//create major tickers..
			var numGrads : Number = _gradients.length;
			var prevEndLabelXPos:Number;
			for(i=0; i<numGrads; i++){
				var gradMinVal : Number = _gradients[i].minValue;
				var gradMaxVal : Number = _gradients[i].maxValue;
				
				//if we are not rotating scale tickers for space management - we further try to arrange 
				//each label depending on its available space.Only overlapping should be wrapped.
				if(!alternateTicker){
					//amount of space for this label.
					var space:Number = (gradMaxVal - gradMinVal) * opt_scalePxUnit;
					//the space can not be less than what is required to render a single digit
					space = (space > singleDigtSpace) ? space : singleDigtSpace;
					//the drawing width of the text
					tickerTxtWidth = space;
				}
			
				var xPos : Number = (gradMinVal * opt_scalePxUnit) - opt_sclaeIniDisplacement;
				var yPos : Number = 0;
				var endYPos:Number = (alternateTicker && (i%2 == 0) && (i != (numGrads - 1))) ? (yPos+majorTickerLength+largeTickHeightDiff) : (yPos+majorTickerLength);
				//draw this ticker.
				tickMC.moveTo(xPos, yPos);
				tickMC.lineTo(xPos, endYPos);
				//store the value for snap calculation.only x as it is horizontal scale
				valuesToSnap.push(xPos);
				//Also create the text label for this marker;
				var tickerVal : String = String(MathExt.roundUp(gradMinVal, _decimalPrecision));
				// add percentage sign if mapping is on percentage basis
				tickerVal = (opt_mapByPercent) ? tickerVal+"%" : tickerVal;
				//now just simply render the text to get the width it requires to render
				var dummyTxt : Object = Utils.createText(true, tickerVal, tickMC, depthCounter, xPos, (endYPos + textPadding), tickerLblRotation, this.fontProp);
				var reqTxtFldWidth:Number = dummyTxt.tf._width;
				//if the required space is less than the avalable space we should render the text width requires width only
				tickerTxtWidth = (tickerTxtWidth > reqTxtFldWidth) ? reqTxtFldWidth : tickerTxtWidth;
				//remove the dummy field
				dummyTxt.tf.removeTextField();
				//create the texts.
				//var tickerTxt : Object = Utils.createText(false, tickerVal, tickMC, depthCounter, xPos, (yPos + majorTickerLength + textPadding), 90, this.fontProp, true, textMrkrWidth, textMrkrHeight);
				var tickerTxt : Object = Utils.createText(false, tickerVal, tickMC, depthCounter, xPos, (endYPos + textPadding), tickerLblRotation, this.fontProp, tickerLblwrap, tickerTxtWidth, textMrkrHeight);
				//place each ticker at the end of the ticker line
				tickerTxt.tf._y = (tickerLblRotation == 0) ? endYPos : endYPos + tickerTxt.tf._height// / 2 + textPadding;
				//in case the label overlaps with the previous onr place it just after the previous one
				//we should not calculate for the first label and when labels are auto rotated.
				tickerTxt.tf._x = (tickerTxt.tf._x < prevEndLabelXPos && i != 0 && !alternateTicker) ? prevEndLabelXPos : tickerTxt.tf._x;
				//update end position.
				prevEndLabelXPos = tickerTxt.tf._x + tickerTxt.tf._width / 2;
				//increment the depth ...
				depthCounter++;
				//we also need to draw the last ticker
				if(i == (numGrads - 1)){
					//Now the last text fields value and this ones going to be same
					var xPos : Number = (gradMaxVal * opt_scalePxUnit) - opt_sclaeIniDisplacement;
					if(_colorRanges["gradientEndLabel"] == "" || _colorRanges["gradientEndLabel"] == null || _colorRanges["gradientEndLabel"] == undefined){
						var originalAlign:String = this.fontProp.align
						this.fontProp.align = 'Right'
					}
					if(!alternateTicker){
						//now space for the last label should be calculated form the legend's extreme right edge.
						var rigthEdge:Number = gradientScaleMC._x + gradientScaleMC._width + endLabelHolder._width + vPadding;
						space = rigthEdge - xPos;
						//can not be less than minimum space to render a single digit
						space = (space > singleDigtSpace) ? space : singleDigtSpace;
						tickerTxtWidth = space //(space < textMrkrAvgWidth) ? textMrkrAvgWidth : space;
					}
					
					var yPos : Number = 0;
					//draw this ticker.
					tickMC.moveTo(xPos, yPos);
					tickMC.lineTo(xPos, (yPos+majorTickerLength+largeTickHeightDiff));
					//store the value for snap calculation.only x as it is horizontal scale
					valuesToSnap.push(xPos);
					//Also create the text label for this marker;
					var tickerVal : String = String(MathExt.roundUp(gradMaxVal, _decimalPrecision));
					// add percentage sign if mapping is on percentage basis
					tickerVal = (opt_mapByPercent) ? tickerVal+"%" : tickerVal;
					//create the text.
					tickerTxt = Utils.createText(false, tickerVal, tickMC, depthCounter, xPos, (yPos + majorTickerLength+ largeTickHeightDiff + textPadding), tickerLblRotation, this.fontProp, false, tickerTxtWidth, textMrkrHeight);
					//place each ticker at the end of the ticker line
					tickerTxt.tf._y = (tickerLblRotation == 0) ? endYPos : endYPos + tickerTxt.tf._height// / 2 + textPadding;
					//align after the previous one in case overlapping
					tickerTxt.tf._x = (tickerTxt.tf._x < prevEndLabelXPos && !alternateTicker) ? prevEndLabelXPos : tickerTxt.tf._x;
					//revert alignment
					this.fontProp.align = originalAlign;
				}
			}
		}else{
			//draw the scale line..
			tickMC.moveTo(0,0);
			tickMC.lineTo(0,scaleObj.height);
			//calculating the width of each text marker.
			var textMrkrWidth : Number = 60;
			var textMrkrHeight : Number =  scaleObj.height / numMajorTM;
			//create major tickers..
			var numGrads : Number = _gradients.length;
			for(i=0; i<numGrads; i++){
				var xPos : Number = 0;
				var gradMinValue : Number = _gradients[i].minValue;
				var gradMaxValue : Number = _gradients[i].maxValue;
				var yPos : Number = (gradMinValue * opt_scalePxUnit) - opt_sclaeIniDisplacement;
				//draw this ticker.
				tickMC.moveTo(xPos, yPos);
				tickMC.lineTo((xPos + majorTickerLength), yPos);
				//store the value for snap calculation.only y as it is vertical scale
				valuesToSnap.push(yPos);
				//Also create the text label for this marker;
				var tickerVal : String = String(MathExt.roundUp(gradMinValue, _decimalPrecision));
				// add percentage sign if mapping is on percentage basis
				tickerVal = (opt_mapByPercent) ? tickerVal+"%" : tickerVal;
				//create the text.
				this.fontProp.leftMargin = 1;
				
				var txtObj : Object = Utils.createText(false, tickerVal, tickMC, depthCounter, (xPos + majorTickerLength), yPos, 0, this.fontProp);
				txtObj.tf._x += txtObj.tf._width / 2;
				//increment the depth ...
				depthCounter++;
				//we also need to draw the last ticker
				if(i == (numGrads-1)){
					//Now the last text fields value and this one's going to be same
					var xPos : Number = 0;
					var yPos : Number = (gradMaxValue * opt_scalePxUnit) - opt_sclaeIniDisplacement;
					//draw this ticker.
					tickMC.moveTo(xPos, yPos);
					tickMC.lineTo((xPos+majorTickerLength), yPos);
					//store the value for snap calculation.only y as it is vertical scale
					valuesToSnap.push(yPos);
					//Also create the text label for this marker;
					var tickerVal : String = String(MathExt.roundUp(gradMaxValue, _decimalPrecision));
					// add percentage sign if mapping is on percentage basis
					tickerVal = (opt_mapByPercent) ? tickerVal+"%" : tickerVal;
					//create the text.
					this.fontProp.leftMargin = 1;
					var txtObj : Object = Utils.createText(false, tickerVal, tickMC, depthCounter,(xPos + majorTickerLength), yPos, 0, this.fontProp);
					txtObj.tf._x += txtObj.tf._width / 2;
				}
			}
		}
		minTickerLabel = _gradients[0].minValue;
		maxTickerLabel = _gradients[(numGrads - 1)].maxValue;
	}
	
	
	/**
	* createPointers create both the dragable pointers and initially place them in the star and end point
	* of the scale.Assign events.
	**/
	private function createPointers():Void{
		//draw first pointer
		drawPointer(firstPointerMC);
		firstPointerCentralBoxMC = firstPointerMC.centralBox_mc;
		//draw second pointer
		drawPointer(secondPointerMC);
		secondPointerCentralBoxMC = secondPointerMC.centralBox_mc;
		//now place the pointers in the extreme ends
		secondPointerMC._x = scaleObj.width;
	
		var classRef = this;
		//firts pointer's press and relaese event.
		firstPointerMC.onPress = function(){
			//inititate dragging.
			classRef.startPointerDrag(this);
		}
		firstPointerMC.onRollOver = function(){
			//initiate roll over
			classRef.setRollOver(this);
		}
		firstPointerMC.onRollOut = function(){
			//initiate roll over
			classRef.setRollOut(this);
		}
		firstPointerMC.onRelease = firstPointerMC.onReleaseOutside = function(){
			//stop dragging and dispatch event
			classRef.stopPointerDrag(this);
		}
		
		//second pointer's press and relaese event
		secondPointerMC.onPress = function(){
			//inititate dragging.
			classRef.startPointerDrag(this);
		}
		secondPointerMC.onRollOver = function(){
			//initiate roll over
			classRef.setRollOver(this);
		}
		secondPointerMC.onRollOut = function(){
			//initiate roll over
			classRef.setRollOut(this);
		}
		secondPointerMC.onRelease = secondPointerMC.onReleaseOutside = function(){
			//stop dragging and dispatch event
			classRef.stopPointerDrag(this);
		}
	}
	
	/*
	 * drawPoinetr draws the pointer with all its cosmetics
	 *
	 * @params target_mc	MovieClip	The container to draw the poinetr
	 */
	private function drawPointer(target_mc):Void{
		//poinetr attributes
		var pointerWidth : Number = _legendItemCosmetics.legendPointerWidth;
		var pointerHeight : Number = _legendItemCosmetics.legendPointerHeight;
		var pointerFillColor : Number = parseInt(_legendItemCosmetics.legendPointerBgColor, 16);
		var pointerBorderColor : Number =  parseInt(_legendItemCosmetics.legendPointerBorderColor, 16);
		var pointerHighlightBorderColor : Number =  parseInt(_legendItemCosmetics.legendPointerHighlightColor, 16);
		var pointerHighlightShadowColor : Number =  parseInt(_legendItemCosmetics.legendPointerShadowColor, 16);
		var pointerBorderThickness : Number = _legendItemCosmetics.legendPointerBorderThickness;
		var pointerAlpha : Number = _legendItemCosmetics.legendPointerBgAlpha;
		
		var centralBoxColor : Number = 0xE4E4E4;
		var centralBoxAlpha : Number = 100;
		var dottedLineColor : Number = 0x9B3E00;
		//draw popinter.
		target_mc.beginFill(pointerFillColor, pointerAlpha);
		//drawing the pointer
		target_mc.moveTo(-pointerWidth/2,-pointerHeight/2);
		target_mc.lineTo(pointerWidth/2,-pointerHeight/2);
		target_mc.lineTo(pointerWidth/2,pointerHeight/4);
		target_mc.lineTo(0,pointerHeight/2);
		target_mc.lineTo(-pointerWidth/2,pointerHeight/4);
		target_mc.lineTo(-pointerWidth/2,-pointerHeight/2);
		target_mc.endFill();
		//drawing the light/shade effect
		//the top line shadow
		target_mc.lineStyle(pointerBorderThickness, pointerBorderColor);
		target_mc.moveTo((-pointerWidth/2 + pointerBorderThickness), (-pointerHeight/2 + pointerBorderThickness));
		target_mc.lineTo((pointerWidth/2 - pointerBorderThickness), (-pointerHeight/2 + pointerBorderThickness));
		//draw the box highlight
		target_mc.lineStyle(pointerBorderThickness, pointerHighlightBorderColor);
		target_mc.moveTo((-pointerWidth/2 + pointerBorderThickness), (pointerHeight/4 - pointerBorderThickness));
		target_mc.lineTo((-pointerWidth/2 + pointerBorderThickness), (-pointerHeight/2 + (pointerBorderThickness*2)));
		target_mc.lineTo((pointerWidth/2 - pointerBorderThickness), (-pointerHeight/2 + (pointerBorderThickness*2)));
		//draw the box shadow
		target_mc.lineStyle(pointerBorderThickness, pointerHighlightShadowColor);
		target_mc.lineTo((pointerWidth/2 - pointerBorderThickness), (pointerHeight/4 - pointerBorderThickness));
		target_mc.lineTo((-pointerWidth/2 + pointerBorderThickness), (pointerHeight/4 - pointerBorderThickness));
		//Now create the central box this might show the preently selected color
		var cetralBoxMC:MovieClip = target_mc.createEmptyMovieClip("centralBox_mc", 1);
		var boxHeight : Number = (pointerHeight - (pointerHeight / 4)) - (pointerBorderThickness * 4);
		var boxWidth : Number = pointerWidth - (pointerBorderThickness * 3);
		
		cetralBoxMC.beginFill(centralBoxColor, centralBoxAlpha);
		cetralBoxMC.moveTo(0,0);
		cetralBoxMC.lineTo(boxWidth, 0);
		cetralBoxMC.lineTo(boxWidth, boxHeight);
		cetralBoxMC.lineTo(0, boxHeight);
		cetralBoxMC.lineTo(0, 0);
		//place it
		cetralBoxMC._x = -(boxWidth / 2);
		cetralBoxMC._y = -pointerHeight/2 + (pointerBorderThickness*2);
	}
		
	
	/*
	 * drawPointersLabel draws the two pointer labels 
	 */
	
	private function drawPointersLabel():Void{
		//create container clip
		firstPointerLabelContMC = mcBase.createEmptyMovieClip("firstPointerLabelCont_mc", firstPointerLabel_depth);
		secondPointerLabelContMC = mcBase.createEmptyMovieClip("secondPointerLabelCont_mc", secondPointerLabel_depth);
		// label styles
		var labelProp : Object = this.fontProp;
		labelProp.bgColor = 'FFFFCC';
		labelProp.borderColor = '333333';
		// first labels
		var initLablStr : String = String(MathExt.roundUp(scaleObj.lowerLimit, _decimalPrecision));
		// double check
		initLablStr = (initLablStr == undefined) ? "" : initLablStr;
		var tempTfObj : Object = Utils.createText(false, initLablStr, firstPointerLabelContMC, 1, 0 , 0, null, labelProp, false);
		firstTickerLabel = tempTfObj.tf;
		
		// second label
		initLablStr = String(MathExt.roundUp(scaleObj.upperLimit, _decimalPrecision));
		// double check
		initLablStr = (initLablStr == undefined) ? "" : initLablStr;
		tempTfObj = Utils.createText(false, initLablStr ,secondPointerLabelContMC, 1, 0 , 0, null, labelProp, false);
		secondTickerLabel = tempTfObj.tf;

		//make it invisible at first
		firstPointerLabelContMC._visible = false;
		secondPointerLabelContMC._visible = false;
	}
	
	// ------------- END OF DRAWING METHOD -----------------
	
	// ------------ EVENT METHODS AND EVENT DISPATCHER ------------------\
	
	/*
	 * method setRollOver deals with the roll over event on the pointers
	 *
	 *	param	target_mc	The movieClip of which we invoke the roll over event
	 */
	public function setRollOver(target_mc):Void{
		if(target_mc._name == "firstPointerMC"){
			firstPointerLabelContMC._visible = true;
		}else{
			secondPointerLabelContMC._visible = true;
		}
	}
	
	/*
	 * method setRollOut deals with the roll out event on the pointers
	 *
	 *	param	target_mc	The movieClip of which we invoke the roll out event
	 */
	public function setRollOut(target_mc):Void{
		if(target_mc._name == "firstPointerMC"){
			firstPointerLabelContMC._visible = false;
		}else{
			secondPointerLabelContMC._visible = false;
		}
	}
	
	/*
	 * startPointerDrag initiates dragging on the pointers.
	 *
	 * param	target_mc	The movieClip of which we invoke the drag.
	 */
	public function startPointerDrag(target_mc){
		//bring the last clicked pointer at the higher depth
		var maxDepth : Number = Math.max(firstPointerMC.getDepth(), secondPointerMC.getDepth());
		target_mc.swapDepths(maxDepth);
		//fade out the selected pointer. to make it look selected
		var alignPos : String = this.alignPos;
		//LEFT LIMITS - position of the pointer and (2* 1/2) width of a pointer.
		var leftLimitForSecondPointer : Number = firstPointerMC._x;
		var leftLimitForFirstPointer : Number = gradientScaleMC._x;
		//RIGHT LIMITS - 
		var rightLimitForSecondPointer : Number = gradientScaleMC._x + gradientScaleMC._width;
		var rightLimitForFirstPointer : Number = secondPointerMC._x;
		
		//variables when the legend is at RIGHT .
		// TOP LIMITS
		var topLimitForFirstPointer : Number =  Math.floor(gradientScaleMC._y);
		var bottomLimitForFirstPointer : Number = Math.floor(secondPointerMC._y - firstPointerMC._height); // as rotated
		// BOTTOM LIMITS
		var topLimitForSecondLimit : Number = Math.ceil(firstPointerMC._y + secondPointerMC._height); // as rotated
		var bottomLimitForSecondPointer : Number = Math.ceil(gradientScaleMC._y + gradientScaleMC._height);
		
		if(alignPos == "BOTTOM"){
			var left : Number = (target_mc._name == "firstPointerMC") ? leftLimitForFirstPointer : leftLimitForSecondPointer;
			var top : Number = target_mc._y;
			var right : Number = (target_mc._name == "firstPointerMC") ? rightLimitForFirstPointer : rightLimitForSecondPointer;
			var bottom : Number = target_mc._y;
			target_mc.startDrag(false, left, top, right, bottom);
		}else{
			var left : Number = target_mc._x;
			var top : Number = (target_mc._name == "firstPointerMC") ? topLimitForFirstPointer : topLimitForSecondLimit;
			var right : Number = target_mc._x;
			var bottom : Number = (target_mc._name == "firstPointerMC") ? bottomLimitForFirstPointer : bottomLimitForSecondPointer;
			target_mc.startDrag(false, left, top, right, bottom);
		}
		//assign events and callback functions
		var classRef = this;
		target_mc.onMouseMove = function(){
			classRef.updatePointersColor(target_mc)
			classRef.moveTickerLabels();
			classRef.calculateRange();
		}
		//make labels visible
		if(target_mc._name == "firstPointerMC"){
			firstPointerLabelContMC._visible = true;
		}else{
			secondPointerLabelContMC._visible = true;
		}
	}
	
	/*
	 * stop Pointer drag srops the pointer drag and dispatch the final event
	 *
	 * param	target_mc	The movieClip of which we stop drag
	 */
	
	public function stopPointerDrag(target_mc){
		//get back to the normal state
		target_mc._alpha = 100;
		target_mc.stopDrag();
		//check for snapping only if snap is enabled
		if(_snap){
			checkSnapping(target_mc);
		}
		//final reposition and calculation..
		moveTickerLabels();
		calculateRange();
		//If legend be interactive
		if(this.clickMode){
			//Dispatch the click event
			var minRange : Number = Number(firstTickerLabel.text);
			var maxRange : Number = Number(secondTickerLabel.text);
			this.dispatchEvent({type:"legendClick", target:this, minRange:minRange, maxRange:maxRange});
		}
		//make it invisible at first
		firstPointerLabelContMC._visible = false;
		secondPointerLabelContMC._visible = false;
		delete target_mc.onMouseMove;
	}
	
	/*
	 * checkSnapping method snaps the pointers to its nearest poiter; only if
	 * any pointer is within the snapping range.
	 *	@param	targetPointer	The Pointer Clip of which we need to check snapping.
	 */
	public function checkSnapping(targetPointer:MovieClip):Void{
		//to store lower values for snapping.
		var snapToLow:Array = new Array();
		//to store higher values to snap.
		var snapToHigh:Array = new Array();
		var i:Number;
		
		if(this.alignPos == "BOTTOM"){
			var snapStartPoint:Number = targetPointer._x
			var maxPxToSnap:Number = (gradientScaleMC._width * _snapRange) / 100;
			//as each tick values are inside the tickMc we need to add the tickMC x to the values stored 
			var valToAdd:Number = tickMC._x;
		}else{
			var snapStartPoint:Number = targetPointer._y
			var maxPxToSnap:Number = (gradientScaleMC._height * _snapRange) / 100;
			//as each tick values are inside the tickMc we need to add the tickMC y to the values stored 
			var valToAdd:Number = tickMC._y;
		}
		var minSnapValue:Number = snapStartPoint - maxPxToSnap;
		var maxSnapValue:Number = snapStartPoint + maxPxToSnap;
		//check how many snap points available within the range
		var numOfSnapValues:Number = valuesToSnap.length;
		for(i = 0; i<numOfSnapValues; i++){
			var snapVal:Number = valuesToSnap[i] +  valToAdd;
			//greater or equal to the minimum snap value but less than current value.
			if(snapVal >= minSnapValue && snapVal < snapStartPoint){
				snapToLow.push(snapVal);
			}else if(snapVal <= maxSnapValue && snapVal > snapStartPoint){
			//lower or equal to the maximum snap value but greater than the current value.
				snapToHigh.push(snapVal);
			}
		}
		/*	Now the possible situations could be
		*	1. We have got values in the low range array and the high range array is blank
		*	2. We have got values in the high range array and the low range array is blank.
		*	3. We have got values in both the array and we have to find the nearest one
		*/
		var lowerSnapValNum:Number = snapToLow.length;
		var higherSnapValNum:Number = snapToHigh.length;
		if(lowerSnapValNum >= 1 && higherSnapValNum == 0){
			snapToLow.sort(Array.NUMERIC);
			//The highest value in the low ranges should be nearest
			var lowerValueToSnap:Number = snapToLow[(snapToLow.length - 1)];
			//SNAP TO THE CLOSEST LOWER VALUE
			if(this.alignPos == "BOTTOM"){
				targetPointer._x = lowerValueToSnap;
			}else{
				targetPointer._y = lowerValueToSnap;
			}
		}else if(higherSnapValNum >= 1 && lowerSnapValNum == 0){
			snapToHigh.sort(Array.NUMERIC);
			//The lowest value in the high range should be nearest
			var higherValueToSnap:Number =  snapToHigh[0];
			//SNAP TO THE CLOSEST HIGHER VALUE
			if(this.alignPos == "BOTTOM"){
				targetPointer._x = higherValueToSnap;
			}else{
				targetPointer._y = higherValueToSnap;
			}
		}else if(higherSnapValNum >= 1 && lowerSnapValNum >= 1){
			snapToLow.sort(Array.NUMERIC);
			snapToHigh.sort(Array.NUMERIC);
			//The highest value in the low ranges should be nearest and the lowest value
			//in the high range should be nearest from the current position
			var lowerValueToSnap:Number = snapToLow[(snapToLow.length - 1)];
			var higherValueToSnap:Number =  snapToHigh[0];
			//Get the diffrence of two value from the current position
			var diff1:Number = snapStartPoint - lowerValueToSnap;
			var diff2:Number = higherValueToSnap - snapStartPoint;
			if(diff1 < diff2){
				if(this.alignPos == "BOTTOM"){
					targetPointer._x = lowerValueToSnap;
				}else{
					targetPointer._y = lowerValueToSnap;
				}
			}else {
				if(this.alignPos == "BOTTOM"){
					targetPointer._x = higherValueToSnap;
				}else{
					targetPointer._y = higherValueToSnap;
				}
			}
		}
	}
	
	/*
	 * updatePointersColor updates the color at the middle of the pointer
	 * depending on its position.
	 *
	 *	@param	targetPointer	The pointer Clip, of which we need to update the Color. 
	 */
	public function updatePointersColor(targetPointer:MovieClip):Void{
		var xPos:Number;
		var yPos:Number;
		
		var clipToColor:MovieClip = targetPointer.centralBox_mc;
		
		var clrTranform:ColorTransform = new ColorTransform();
		var transfrm:Transform = new Transform(clipToColor);
		
		var gradScaleBMP:BitmapData = new BitmapData(gradientScaleMC._width, gradientScaleMC._height);
		gradScaleBMP.draw(gradientScaleMC);

		if(this.alignPos == "BOTTOM"){
			xPos = targetPointer._x - gradientScaleMC._x;
			yPos = gradientScaleMC._height / 2;
			//as we have 2 x 1px wide lines for effect
			xPos = (xPos <= 2) ? 2 : Math.floor(xPos);
			xPos = (xPos >= (gradientScaleMC._width - 2)) ? Math.floor(xPos-2) : Math.floor(xPos);
		}else{
			xPos = gradientScaleMC._width / 2 //gradientScaleMC._x;
			yPos = targetPointer._y - gradientScaleMC._y;
			//as we have 2 x 1px wide lines for effect
			yPos = (yPos <= 2) ? 2 :  Math.floor(yPos);
			yPos = (yPos >= (gradientScaleMC._height - 2)) ? Math.floor(yPos-2) :  Math.floor(yPos);
		}
		//we should not count the border line
		var color:Number = gradScaleBMP.getPixel(xPos, yPos);
		clrTranform.rgb = color;
		transfrm.colorTransform = clrTranform;
		
		gradScaleBMP.dispose();
		clrTranform = null;
		transfrm = null;
	}
	
	/**
	 * clickEvent method is overidden as we have to pass two more extra parameters.
	 *	@param	index	The index of the clicked item.
	 **/
	public function clickEvent(index:Number):Void{
		//Get reference of the legend specific id of the item from chart specific id.
		index = this.arrIdMap[index];
		//Change the item icon state
		this.reverseItemState(index);
		var objItem:Object = this.items[index];
		//Dispatch the click event
		this.dispatchEvent({type:"legendClick", target:this, index:objItem.id, active:objItem.active, intIndex:index, minRange:objItem.minVal, maxRange:objItem.maxVal});
	}
	
	/**
	* moveTickerLabels moves the ticker labels along with the tickers.
	**/
	public function moveTickerLabels():Void{
		if(this.alignPos == "BOTTOM"){
			firstPointerLabelContMC._x = (firstPointerMC._x + (firstPointerMC._width / 2)) + firstPointerLabelContMC._width / 2 ;
			firstPointerLabelContMC._y = firstPointerMC._y - firstPointerMC._height/2 ;
			
			secondPointerLabelContMC._x = (secondPointerMC._x + (secondPointerMC._width/2)) + secondPointerLabelContMC._width / 2;
			secondPointerLabelContMC._y = secondPointerMC._y - (secondPointerMC._height/2);
		}else{
			firstPointerLabelContMC._x = (firstPointerMC._x - firstPointerMC._width/2) - (firstPointerLabelContMC._width/2 + 2);
			firstPointerLabelContMC._y = (firstPointerMC._y + firstPointerMC._height/2) - (firstPointerLabelContMC._height + 2);
			
			secondPointerLabelContMC._x = (secondPointerMC._x - secondPointerMC._width/2) - (secondPointerLabelContMC._width/2 + 2);
			secondPointerLabelContMC._y = (secondPointerMC._y + secondPointerMC._height/2) - (secondPointerLabelContMC._height + 2);
		}
	}
	
	/**
	*	calculateRange calculates the active range from the gradient scale.
	*	based on this we get the minimum and maximum selected range value.
	*	It also update the text value of the poiter's label.
	**/
	public function calculateRange():Void{
		checkExtremeAlignment();
		if(this.alignPos == "BOTTOM"){
			//amount of displacement in pixel
			var xDis1stPntr : Number = firstPointerMC._x - gradientScaleMC._x;
			var xDis2ndPntr : Number = secondPointerMC._x - gradientScaleMC._x;
			//amount of change on the basis of the scale.
			var scalerDis1stPontr : Number = (xDis1stPntr / scaleObj.pxUnit) + scaleObj.lowerLimit;
			//if the calculated number is lower than the lowest label displyed.Assign the lowest label
			scalerDis1stPontr = (scalerDis1stPontr < minTickerLabel) ? minTickerLabel : scalerDis1stPontr;
			scalerDis1stPontr = (scalerDis1stPontr > maxTickerLabel) ? maxTickerLabel : scalerDis1stPontr;
			
			scalerDis1stPontr = MathExt.roundUp(scalerDis1stPontr, _decimalPrecision);
			
			var scalerDis2ndPontr : Number = (xDis2ndPntr / scaleObj.pxUnit) + scaleObj.lowerLimit;
			//if the calculated number is higher than the highest label displyed.Assign the highest label
			scalerDis2ndPontr = (scalerDis2ndPontr > maxTickerLabel) ? maxTickerLabel : scalerDis2ndPontr;
			scalerDis2ndPontr = (scalerDis2ndPontr < minTickerLabel) ? minTickerLabel : scalerDis2ndPontr;
			scalerDis2ndPontr = MathExt.roundUp(scalerDis2ndPontr, _decimalPrecision);
			//update pointer's label
			firstTickerLabel.text = scalerDis1stPontr.toString();
			secondTickerLabel.autoSize = true;
			secondTickerLabel.text = scalerDis2ndPontr.toString();
			secondTickerLabel.autoSize = true;
		}else{
			//amount of displacement in pixel
			var yDis1stPntr : Number = firstPointerMC._y - gradientScaleMC._y;
			var yDis2ndPntr : Number = secondPointerMC._y - gradientScaleMC._y;
			//amount of change on the basis of the scale.
			var scalerDis1stPontr : Number = (yDis1stPntr / scaleObj.pxUnit) + scaleObj.lowerLimit;
			//if the calculated number is lower than the lowest label displyed.Assign the lowest label
			scalerDis1stPontr = (scalerDis1stPontr < minTickerLabel) ? minTickerLabel : scalerDis1stPontr;
			scalerDis1stPontr = (scalerDis1stPontr > maxTickerLabel) ? maxTickerLabel : scalerDis1stPontr;
			scalerDis1stPontr = MathExt.roundUp(scalerDis1stPontr, _decimalPrecision);
			
			var scalerDis2ndPontr : Number = (yDis2ndPntr / scaleObj.pxUnit) + scaleObj.lowerLimit;
			//if the calculated number is higher than the highest label displyed.Assign the highest label
			scalerDis2ndPontr = (scalerDis2ndPontr > maxTickerLabel) ? maxTickerLabel : scalerDis2ndPontr;
			scalerDis2ndPontr = (scalerDis2ndPontr < minTickerLabel) ? minTickerLabel : scalerDis2ndPontr;
			scalerDis2ndPontr = MathExt.roundUp(scalerDis2ndPontr, _decimalPrecision);
			//update pointer's label
			firstTickerLabel.text = scalerDis1stPontr.toString();
			secondTickerLabel.text = scalerDis2ndPontr.toString();
		}
		//update the scales dynamic properties
		updateScaleProp();
	}
	
	/*
	*	checkExtremeAlignment method aligns the two pointers if they exceed their extreme
	*	limits.This could be due to startDrag method which seems to round its values.
	*	NOTE - Now when we apply startDrag limits.Flash seems to make those number whole number
	* 		  So if the number is 16.5 it is rounded to either 16 or 17.This makes a slight error
	*		  In our scale.To prevent this following checkings applied.
	*
	*/
	
	private function checkExtremeAlignment():Void{
		if(this.alignPos == "BOTTOM"){
			firstPointerMC._x = (firstPointerMC._x < gradientScaleMC._x) ? gradientScaleMC._x : firstPointerMC._x;
			secondPointerMC._x = (secondPointerMC._x < gradientScaleMC._x) ? gradientScaleMC._x : secondPointerMC._x;
			var rightLmt:Number = gradientScaleMC._x + gradientScaleMC._width;
			secondPointerMC._x = (secondPointerMC._x > rightLmt) ? rightLmt : secondPointerMC._x;
			firstPointerMC._x = (firstPointerMC._x > rightLmt) ? rightLmt : firstPointerMC._x;
		}else{
			firstPointerMC._y = (firstPointerMC._y < gradientScaleMC._y) ? gradientScaleMC._y : firstPointerMC._y;
			secondPointerMC._y = (secondPointerMC._y < gradientScaleMC._y) ? gradientScaleMC._y : secondPointerMC._y;
			var bottomLmt:Number = gradientScaleMC._y + gradientScaleMC._height;
			secondPointerMC._y = (secondPointerMC._y > bottomLmt) ? bottomLmt : secondPointerMC._y;
			firstPointerMC._y = (firstPointerMC._y > bottomLmt) ? bottomLmt : firstPointerMC._y;
		}
		
	}
	
	/*
	* destroy methods destroys all the events and data related to this legend
	* and calls the parent class's destroy method
	*
	*	@param	keepData	The booelan value whether to retain data.
	*/
	
	public function destroy(keepData:Boolean):Void{
		//clear events pertinent to this object
		delete firstPointerMC.onPress;
		delete firstPointerMC.onRelease;
		delete firstPointerMC.onMouseMove;
		delete firstPointerMC.onReleaseOutside;
		
		delete secondPointerMC.onPress;
		delete secondPointerMC.onRelease;
		delete secondPointerMC.onMouseMove;
		delete secondPointerMC.onReleaseOutside;
		
		
		// remove movie clips
		gradientScaleMC.removeMovieClip();
		tickMC.removeMovieClip();
		firstPointerMC.removeMovieClip();
		secondPointerMC.removeMovieClip();
		labelHolder.removeMovieClip();
		startLabelHolder.removeMovieClip();
		endLabelHolder.removeMovieClip();
		
		firstTickerLabel.removeTextField();
		secondTickerLabel.removeTextField();
		
		//clear objects
		scaleObj = null;
		
		//clear data arrays
		_fixedDimention = undefined;
		_gradients = new Array();
		_colorRanges = new Array();
		
		//call parent class's destroy method
		super.destroy(keepData);
		
	}
}