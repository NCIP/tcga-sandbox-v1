/**
* @class AdvancedLegend
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
* AdvancedLegend class helps us generate legend for a chart
* that allows click on individual legend items with 
* state-specific toggling icons.
*/
import  mx.utils.Delegate;
import com.fusioncharts.helper.ScrollVBar;
import com.fusioncharts.extensions.StringExt;
import flash.display.BitmapData;
import mx.events.EventDispatcher;
//
class com.fusioncharts.helper.AdvancedLegend {
	
	//Array to store legend items
	public var items:Array;
	public var count:Number;
	//Containers to store parameters
	private var parentMC:MovieClip;
	
	private var fontProp:Object;
	private var alignPos:String;
	
	private var xPos:Number;
	private var yPos:Number;
	
	private var allowDrag:Boolean;
	//Caption of legend
	private var caption:String;
	//Whether to use in clickMode
	private var clickMode:Boolean;
	//Maximum and minimum height the legend can assume
	private var maxWidth:Number;
	private var maxHeight:Number;
	//Container size
	private var stageWidth:Number;
	private var stageHeight:Number;
	//Legend box background properties
	private var bgColor:String;
	private var bgAlpha:Number;
	//Legend box border properties
	private var borderColor:String;
	private var borderThickness:Number;
	private var borderAlpha:Number;
	//Scroll bar cosmetic properties
	private var scrollBgColor:String;
	private var scrollBarColor:String;
	private var btnColor:String;
	//Width and height of the legend (will be calculated later)
	private var width:Number;
	private var height:Number;
	//Reference to legend MC
	private var legendMC:MovieClip;
	
	//Reference to caption text field, background movie clip and scroller MC
	private var captionTxt:TextField;
	private var bgMC:MovieClip;
	private var scrollMC:MovieClip;
	//Reference to base MC of legend
	private var mcBase:MovieClip ;
	
	//Scroll bar
	var sBar:ScrollVBar;
	//Reference to text format object
	private var tFormat:TextFormat;
	//Undefined variables which will be required to simulate text field dimensions
	private var a, b;
	
	//Flag whether we've to show scroll bar
	private var showScroll:Boolean;
	//Scroll bar properties
	var scrollWidth:Number = 12;
	var scrollPad:Number = 2;
	var scrollVPad:Number = 2;
	//Number of columns in the legend
	private var numColumns:Number;
	//Gutter space around legend contents
	private var gutter:Number = 2;
	//Maximum height of icons provided
	private var maxIconHeight:Number = 0;
	//Flag to control the algorithm of items layout/display management
	public var minimiseWrapping:Boolean = false;
	//Array to store the references of item MCs
	private var arrItemMc:Array;
	//Map of items' chart specific index to legend specific index
	public var arrIdMap:Array;
	//Variables to track movement if dragged
	private var startDragXPos:Number;
	private var startDragYPos:Number;
	//-------------------------//
	/**
	* Constructor function to initialize and store parameters.
	*	@param	targetMC		Parent movie clip in which we've to draw legend
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
	function AdvancedLegend (targetMC:MovieClip, fontProp:Object, clickMode:Boolean,
							  alignPos:String, xPos:Number, yPos:Number,
							  maxWidth:Number, maxHeight:Number, allowDrag:Boolean,
							  stageWidth:Number, stageHeight:Number,
							  bgColor:String, bgAlpha:Number,
							  borderColor:String, borderThickness:Number, borderAlpha:Number,
							  scrollBgColor:String, scrollBarColor:String, btnColor:String,
							  numColumns:Number)
							{
		//Store parameters in instance variables
		this.parentMC = targetMC;
		
		this.fontProp = fontProp;
		this.clickMode = clickMode;
		this.alignPos = alignPos.toUpperCase ();
		
		//For legend at bottom of chart
		if(this.alignPos == 'BOTTOM'){
			//Taking whole number for number of columns
			this.numColumns = (numColumns>0)? Math.round(numColumns) : 0;
		//For legend on right of chart
		}else{
			//Number of columns is surely one.
			this.numColumns = 1;
		}
		
		this.xPos = xPos;
		this.yPos = yPos;
		
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		
		this.allowDrag = allowDrag;
		
		this.stageWidth = stageWidth;
		this.stageHeight = stageHeight;
		
		this.bgColor = bgColor;
		this.bgAlpha = bgAlpha;
		
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
		this.borderAlpha = borderAlpha;
		
		this.scrollBgColor = scrollBgColor;
		this.scrollBarColor = scrollBarColor;
		this.btnColor = btnColor;
		
		//Initialize count to 0
		count = 0;
		//Initialize items array
		items = new Array ();
		arrItemMc = new Array();
		arrIdMap = [];
		
		//By default, we do not show scroll bar
		showScroll = false;
		//Set caption to blank value
		caption = "";
		//Create the TextFormat object to be used.
		this.createTxtFormat(fontProp);
		
		//Create Legend movie clip inside parent movie clip
		legendMC = this.parentMC.createEmptyMovieClip ("Legend", this.parentMC.getNextHighestDepth ());		
		bgMC = this.legendMC.createEmptyMovieClip ("Bg", this.legendMC.getNextHighestDepth());
		mcBase = this.legendMC.createEmptyMovieClip('base', this.legendMC.getNextHighestDepth());
		
		//Initialize EventDispatcher to implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
	}
	
	/**
	 * getDimensions method is called to get back metrics of space required
	 * to render the legend. Its here that the legend is actually created.
	 * @return		Object with legend width and height
	 */
	public function getDimensions():Object{
		//Object to be returned.
		var rtnObj:Object = new Object ();
		
		if (count < 1){
			//Store width and height as 0
			rtnObj.width = 0;
			rtnObj.height = 0;
			return rtnObj;
		}
		
		var finalWidth:Number, finalHeight:Number;
		//Find and store the maximum icon height.
		this.getMaxIconHeight();
		//---------------------------------------//
		//Legend is made invisible for now.
		this.legendMC._visible = false;
		//To store widths of items (without wrapping).
		var arrWidths:Array = new Array();
		//Variable to find the maximum width of all items is initialised to zero.
		var maxMcWidth:Number = 0;
		//To sum up all item widths
		var totalWidth:Number = 0;
		
		var mcWidth:Number;
		var mcItem:MovieClip;
		//Create and set caption is specified.
		if(this.caption){
			this.captionTxt = this.createCaption(mcBase);
		}
		
		//Iterate over all items for multi purpose.
		for(var i:Number = 0; i<count; ++i){
			//Create the item.
			mcItem = this.createItem(i, mcBase);
			//Store the mc reference for later use
			arrItemMc.push(mcItem);
			//The item width
			mcWidth = mcItem._width;
			//Store the item width
			arrWidths.push(mcWidth);
			//Sum up the widths
			totalWidth += mcWidth;
			//Find the maximum width
			if(maxMcWidth < mcWidth){
				maxMcWidth = mcWidth;
			}
		}
		//The average width
		var averageWidth:Number = Math.round(totalWidth/count);
		
		var columnWidth:Number;
		var numColumns:Number;
		var numRows:Number;
		var itemsWidth:Number;
		var itemsHeight:Number;
		
		//------------------- Column Algo -----------------//
		//Flag to find number of columns initialised to false.
		var calcNumColumns:Boolean = false;
		
		//Note: Number of columns equal to one is always accepted and requires no validation.
		//If number of columns is greater than one, then validate it
		if(this.numColumns>1){
			//Validating number of columns for number of legend items added
			if(this.numColumns > this.count){
				this.numColumns = this.count;
			}
			//Calculate column width depending on number of columns
			columnWidth = Math.round(this.maxWidth / this.numColumns);
			//Column width can't be less than average width
			if(columnWidth < averageWidth){
				calcNumColumns = true;
			}
		//If number of columns is undefined or zero
		}else if(!this.numColumns){
			calcNumColumns = true;
		}
		
		
		//If required to find the number of columns
		if(calcNumColumns){
			//1. If largest width is less than maximum permissible legend width
			//and
			//2. either of
			//		a) if wrapping be minimised (wrapping only possible for number of columns equal to one)
			//		Or
			//		b) largest width is not more than 50% wider than average width (when a lot of white space is lost)
			if(this.maxWidth > maxMcWidth && (this.minimiseWrapping || averageWidth * 1.5 > maxMcWidth) ){
				//The number of columns each of width as the largest width
				numColumns = Math.floor(this.maxWidth / maxMcWidth);
				//If numColumns is greater than number of items
				if(this.count < numColumns){
					//Get rid of empty column(space)
					numColumns = this.count;
				}
				
				//Set column width as the largest width
				columnWidth = maxMcWidth;
				//Find the number of rows
				numRows = Math.ceil(count/numColumns);
				//Legend items width
				itemsWidth = numColumns * columnWidth;
				//Expecting that all icons have same height, height of first item is taken as the height for all.
				//Base height of all items (single line text)
				var heightMc:Number = arrItemMc[0]._height;
				//Legend items height
				itemsHeight = numRows * heightMc;
				//The final width of legend as width of items arranged in rows and columns
				finalWidth = itemsWidth;
			
			} else {
				
				//If maximum permisible Legend width is greater than average width
				if(this.maxWidth > averageWidth){
					//The number of columns each of width as the averageWidth
					numColumns = Math.floor(this.maxWidth / averageWidth);
					
					if(numColumns > this.count){
						numColumns = this.count;
					}
					//For one column in legend, provide the maximum possible width space, for this is the case due failure in the upper conidition.
					if(numColumns == 1){
						itemsWidth = this.maxWidth;
						columnWidth = itemsWidth;
					}else{
						//Provide best width space, which is greater than average width, generally (for numColumns is found above via Math.floor())
						columnWidth = Math.floor(this.maxWidth / numColumns);
						
						if(columnWidth > maxMcWidth){
							columnWidth = maxMcWidth;
						}
						itemsWidth = numColumns * columnWidth;
					}
					finalWidth = itemsWidth;
				} else {
					numColumns = 1;
					itemsWidth = this.maxWidth;
					columnWidth = itemsWidth;
					finalWidth = itemsWidth;
				}
			}
		//For numColumns known
		} else{
			columnWidth = Math.floor(this.maxWidth / this.numColumns);
			//If column width found above is grater than the largest item width
			if(maxMcWidth < columnWidth){
				//Squeeze it to fit the largest one
				columnWidth = maxMcWidth;
			}
			numColumns = this.numColumns;
			itemsWidth = numColumns * columnWidth;
			finalWidth = itemsWidth;
		}
		
		var label_txt:TextField;
		//Iterate over each item and reset its width as found
		for(var i:Number = 0; i<count; ++i){
			mcItem = arrItemMc[i];
			label_txt = mcItem.label_txt;
			label_txt._width = columnWidth+1;
			//Wrapping enabled now
			label_txt.wordWrap = true;
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
			if(this.captionTxt._width > itemsWidth){
				var tempFinalWidth:Number = finalWidth;
				//required width for caption is less than max width
				if(this.captionTxt._width < this.maxWidth){
					finalWidth = this.captionTxt._width;
				//required width for caption is greater than max width
				}else{
					finalWidth = this.maxWidth;
					this.captionTxt._width = this.maxWidth;
					this.captionTxt.wordWrap = true;
				}
				var xIni:Number = (finalWidth - tempFinalWidth) / 2;
			}else{
				//We assign the total width required to render all the items and wordwrap the caption
				this.captionTxt._width = itemsWidth;
				this.captionTxt.wordWrap = true;
			}
		}
		
		//---------- arrange items -----------//
		var xIni:Number = (xIni == undefined) ? 0 : xIni;
		//The starting y position for placing items
		var yIni:Number = (this.caption) ? this.captionTxt._height : 0;
		//Items arranged
		this.arrangeItems(arrItemMc, numColumns, columnWidth, xIni, yIni);
		//The height of total legend with cation (if any) and arranged items
		var itemsBaseHeight:Number = mcBase._height;
		
		//Checking if scroller is required
		if(itemsBaseHeight > this.maxHeight){
			this.showScroll = true;
			finalHeight = this.maxHeight;
			
			//To add and accomodate scroll in legend:
			//If legend width can be increased
			if(this.maxWidth > finalWidth){
				var addWidthBy:Number = (this.maxWidth - finalWidth >= this.scrollWidth + 2 * this.scrollPad) ? this.scrollWidth + 2 * this.scrollPad : this.maxWidth - finalWidth ;
				finalWidth += addWidthBy;
			//For maxWidth = finalWidth
			}else{
				//No change in finalWidth; can't increase any more 
			}
			
			//width to accomodate for scroller
			var widthToAdjust:Number = this.scrollWidth + 2 * this.scrollPad;
			//the width available without the scroller
			var avlblWidth:Number =  finalWidth - widthToAdjust;
			//previously calculated width could be decided based on the width required by the caption or width required by the items.
			//if caption width is greater than the available width. Change the caption width.
			if(this.captionTxt._width > avlblWidth){
				this.captionTxt._width = avlblWidth;
				this.captionTxt.wordWrap = true;
			}
			//if width required to render all the items is greater than available width.Recalculate columnWidth.
			if(itemsWidth > avlblWidth){
				itemsWidth = avlblWidth;
				//Modified column width
				columnWidth = Math.floor(itemsWidth / numColumns);
				//align it to 0.
				xIni = 0;
			}
			
		//For all getting accomodated in max space permisible
		} else {
			finalHeight = itemsBaseHeight;
		}
		
		//----------------------//
		this.width  = rtnObj.width = finalWidth;
		this.height = rtnObj.height = finalHeight;
		
		//So, its time to finally set the items' width and rearrange them. 
		//Same is the case for caption too.
		if(this.caption){
			this.captionTxt._y ++;
		}
		
		var unitHeight:Number = this.getBaseRowHeight();
		
		//Iterate over each item and reset its width as found
		for(var i:Number = 0; i<count; ++i){
			mcItem = arrItemMc[i];
			mcItem.label_txt._width = columnWidth+1;
			
			var txtH:Number = mcItem.label_txt._height;
			
			if(txtH == unitHeight){
				mcItem.label_txt.wordWrap = false;
				mcItem.label_txt.autoSize = true;
			}
			//Height of bitmap icon
			var bmpH:Number = mcItem.mcBmp._height;
			//If icon height is greater than text height, align textfield centrally with icon, vertically.
			if(bmpH > txtH){
				mcItem.label_txt._y =  mcItem.mcBmp._y;
				mcItem.label_txt._y +=  (bmpH - txtH)/2;
			}
		}
		
		var xIni:Number = (xIni == undefined) ? 0 : xIni;
		//The starting y position for placing items
		var yIni:Number = (this.caption) ? this.captionTxt._height : 0;
		//Items rearranged for column width may have changed
		this.arrangeItems(arrItemMc, numColumns, columnWidth, xIni, yIni);
		//----------------------//
		//return legend dimensions
		return rtnObj;
	}
	/*
	* itemOnRelease is called when the mouse click each item
	*/
	private function itemOnRelease(){
		var objItem:Object = this.items[arguments.caller.index];
		if(allowDrag){
			bgMC._parent.stopDrag();
			//now comapre whether the legend has been dragged. Click event should only get fired if no dragging occured.
			if(startDragXPos == bgMC._parent._x && startDragYPos == bgMC._parent._y){
				this.clickEvent(objItem.id);
			}
		}else{
			this.clickEvent(objItem.id);
		}
	}
	/*
	* itemOnPress is called when the mouse click each item
	*/
	private function itemOnPress(){
		var x:Number = this.xPos;
		var y:Number = this.yPos;
		var w:Number = bgMC._width;
		var h:Number = bgMC._height;
		var sW:Number = this.stageWidth;
		var sH:Number = this.stageHeight;
		bgMC.useHandCursor = false;
		//store the position at the time of drag start - to compare when dragging ends
		startDragXPos = bgMC._parent._x;
		startDragYPos = bgMC._parent._y;
		bgMC._parent.startDrag (false, - x + w / 2, - y + h / 2, sW - x - w / 2, sH - y - h / 2);
	}
	/**
	 * createItem method creates the legend item.
	 * @param	index	item id
	 * @param	mcBase	container (MC) to create in
	 * @param			the item (MC) created
	 */
	private function createItem(index:Number, mcBase:MovieClip):MovieClip{
		//The encapsulated item properties
		var objItem:Object = this.items[index];
		var insRef = this;
		var gutter:Number = this.gutter;
		
		var mcItem:MovieClip = mcBase.createEmptyMovieClip('mcItem_'+index, mcBase.getNextHighestDepth());
		mcItem.index = index;
		//If legend be interactive
		if(this.clickMode){
			var fnOnRelease:Function = Delegate.create (this, itemOnRelease);
			//Set the index
			fnOnRelease.index = index;
			mcItem.onRelease = fnOnRelease;
			//If legend drag is allowed then irrespective of interactive mode
			//legend needs to be dragable.
			if(allowDrag){
				var fnOnPress:Function = Delegate.create (this, itemOnPress);
				mcItem.onPress = fnOnPress;
			}
		}
		//----------------------------------//
		var mcBmp:MovieClip = mcItem.createEmptyMovieClip('mcBmp', 0);
		mcBmp._x = gutter;
		mcBmp._y = gutter;
		//The bitmap depending on state
		var bmp:BitmapData = (objItem.active)? objItem.bmpd1: objItem.bmpd2;
		mcBmp.attachBitmap(bmp, 0);
		//-------------------------------//
		var txtWidth:Number;
		var txtHeight:Number;
		
		var txt:TextField = mcItem.createTextField('label_txt', 1, 0, 0, txtWidth, txtHeight);
		txt.html = true;
		txt.htmlText = objItem.name;
		txt.selectable = false;
		//Apply the block indent to keep icon and text seperate horizontally
		var blockIndent:Number = tFormat.blockIndent;
		tFormat.blockIndent = gutter*3 + bmp.width;
		txt.setTextFormat(this.tFormat);
		tFormat.blockIndent = blockIndent;
		//autosize true for now
		txt.autoSize = true;
		//------------------------------//
		//The item mc returned
		return mcItem;
	}
	/**
	 * createCaption method creates the caption.
	 * @param	mcBase	the container to create in
	 * @return		The caption textfield
	 */
	private function createCaption(mcBase:MovieClip):TextField{
		var txtWidth:Number;
		var txtHeight:Number;
		
		var txt:TextField = mcBase.createTextField('caption_txt', mcBase.getNextHighestDepth(), 0, 0, txtWidth, txtHeight);
		txt.html = true;
		//replace &lt; and &gt; with less than and greater than sign
		this.caption = StringExt.replace (this.caption, "<", "&lt;");
		this.caption = StringExt.replace (this.caption, ">", "&gt;");
		
		txt.htmlText = this.caption;
		txt.selectable = false;
		
		var bold:Boolean = tFormat.bold;
		var align:String = tFormat.align;
		
		tFormat.bold = true;
		tFormat.align = 'center';
		txt.setTextFormat(this.tFormat);
		
		tFormat.bold = bold;
		tFormat.align = align;
		
		txt.autoSize = true;
		//The textfield returned
		return txt;
	}
	/**
	 * clickEvent method is called when a legend item has been clicked
	 * by the user. Here, we dispatch the event to parent class.
	 *	@param	index	Internal index (w.r.t. parent class) of the 
	 *					item which was clicked.
	 */
	public function clickEvent(index:Number):Void{
		//Get reference of the legend specific id of the item from chart specific id.
		index = this.arrIdMap[index];
		//Change the item icon state
		this.reverseItemState(index);
		var objItem:Object = this.items[index];
		//Dispatch the click event
		this.dispatchEvent({type:"legendClick", target:this, index:objItem.id, active:objItem.active, intIndex:index});
	}
	/**
	* If the legend has been clicked before chart has finished drawing, the
	* chart can prevent event from happening. In that case, legend reverses
	* the effect.
	* @param	index	Internal index of item which was clicked and is now
	*					to be cancelled.
	*/
	public function cancelClickEvent(index:Number):Void{
		//Simply reverse the icon state
		this.reverseItemState(index);
	}
	/**
	 * reverseItemState method reverses the state of the item icon.
	 * @tparam	index	id of item
	 */
	private function reverseItemState(index:Number):Void{
		var objItem:Object = this.items[index];
		//Set the active flag to reverse - for the item.
		objItem.active = !objItem.active;
		//The item MC
		var mcItem:MovieClip = arrItemMc[index];
		var bmp:BitmapData = (objItem.active)? objItem.bmpd1: objItem.bmpd2;
		mcItem.mcBmp.attachBitmap(bmp, 0);
	}
	/**
	 * arrangeItems method arranges the items in rows and columns.
	 * @param	arrItemMc		array storing MCs to be arranged
	 * @param	numColumns		number of columns
	 * @param	columnWidth		column width
	 * @param	xIni			starting x position
	 * @param	yIni			starting y position
	 */
	private function arrangeItems(arrItemMc:Array, numColumns:Number, columnWidth:Number, xIni:Number, yIni:Number):Void{
		var mcItem:MovieClip, label_txt:TextField;
		var gutter:Number = this.gutter;
		var count:Number = this.count;
		//Helper array to hold row available w.r.t. columns
		var arrRowMetrics:Array = [];
		//All columns have zeroth row available initially
		while(arrRowMetrics.length < numColumns){
			arrRowMetrics.push(0);
		}
		//--------------------------------//
		//A local function to return lowest (lowest row id) row available and in which column.
		var findFirstLeastRowAvailable:Function = function(arrTest:Array):Object{
			//row and column ids initialised
			var obj:Object = {row:Number.MAX_VALUE, column:0};
			//Searching for the best position
			for(var u:Number = 0; u<numColumns; ++u){
				if(arrTest[u] < obj.row){
					obj.row = arrTest[u];
					obj.column = u;
				}
			}
			return obj;
		}
		//-------------------------------//
		//The unit row height
		var baseRowHeight:Number = Math.max(this.getBaseRowHeight() - 2*gutter, this.maxIconHeight + 2*gutter);
		//Iterating over the items to arrange them
		for(var i:Number = 0; i<count; ++i){
			mcItem = arrItemMc[i];
			//Get the best position for the item
			var objPos:Object = findFirstLeastRowAvailable(arrRowMetrics);
			mcItem._x = xIni + columnWidth * objPos.column;
			//For single column, arrangement is different when items are placed one below another, irrespective of unit row height.
			//But for first item, it makes no difference.
			if(i==0 || numColumns > 1){
				mcItem._y = yIni + baseRowHeight * objPos.row;
			}else{
				mcItem._y = arrItemMc[i-1]._y + arrItemMc[i-1]._height;
			}
			//The item height
			var itemHeight:Number = mcItem._height;
			//Number of rows used by the item (fractional use makes the row unusable for next items)
			var rowsUsed:Number = Math.ceil(itemHeight/baseRowHeight);
			//Update the metrics in helper array.
			arrRowMetrics[objPos.column] += rowsUsed;
		}
	}
	
	/**
	 * getIconHeight method is called to get the suggested height of icons
	 * w.r.t. single text line height duly formatted.
	 * @return		the suggested height of icons
	 */
	public function getIconHeight():Number{
		return this.getBaseRowHeight() - 2*gutter;
	}
	
	/**
	 * getBaseRowHeight method returns the height of single
	 * line text duly formatted.
	 * @return		the text height
	 */
	private function getBaseRowHeight():Number{
		var tf:TextField = this.legendMC.createTextField ("test_txt", this.legendMC.getNextHighestDepth(), 0, 0, a, b);
		tf.autoSize = true;
		tf.selectable = false;
		//Set text format
		tf.setNewTextFormat (tFormat);
		tf.html = true;
		//Set text
		tf.htmlText = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
		var h:Number = tf._height;
		tf.removeTextField();
		return h;
	}
	
	/**
	 * render method renders the legend finally.
	 */
	public function render():Void{
		if (count < 1){
			return;
		}
		//Makes the legend visible which was created during evaluation of dimensions.
		this.legendMC._visible = true;
		//Background drawn
		this.drawBg();
		//--------------------------//
		//Draw the background
		var hW:Number = this.width / 2;
		var hH:Number = this.height / 2;
		//Legend content position set
		mcBase._x = this.xPos - hW;
		mcBase._y = this.yPos - hH;
		//--------------------------//
		//Create scroll bar if required.
		if (this.showScroll) {
			//Scrollbar containing MC
			scrollMC = this.legendMC.createEmptyMovieClip ("ScrollBar", legendMC.getNextHighestDepth());
			//Scrollbar created
			sBar = new ScrollVBar(mcBase, scrollMC, 
								   mcBase._x + mcBase._width + scrollPad, mcBase._y + scrollVPad,
								  scrollWidth, this.height - (2 * scrollVPad) ,
								  scrollBgColor, scrollBarColor, btnColor);
		}
		
		//Set up the dragging facility
		if (allowDrag) {
			var x:Number = this.xPos;
			var y:Number = this.yPos;
			var sW:Number = this.stageWidth;
			var sH:Number = this.stageHeight;
			bgMC.useHandCursor = false;
			bgMC.onPress = function(){
				this._parent.startDrag (false, - x + this._width / 2, - y + this._height / 2, sW - x - this._width / 2, sH - y - this._height / 2);
			}
			bgMC.onRelease = bgMC.onReleaseOutside = function (){
				this._parent.stopDrag ();
			}
		}
	}
	/**
	 * drawBg method draws background.
	 */
	private function drawBg():Void{
		
		if(this.bgColor == "" || this.borderColor == ""){
			return;
		}
		
		bgMC.clear();
		
		//Draw the background or border
		if (this.borderColor != ""){
			bgMC.lineStyle (this.borderThickness, parseInt (this.borderColor, 16) , this.borderAlpha);
		}
		if (this.bgColor != ""){
			bgMC.beginFill (parseInt (this.bgColor, 16) , this.bgAlpha);
		}
		
		//Draw the background
		var hW:Number = this.width / 2;
		var hH:Number = this.height / 2;
		
		//Also, accomodate the scroll bar within this rectangle (if required)
		var xScrollExt:Number = 0;
		if (showScroll){
			xScrollExt = this.scrollWidth + 2 * this.scrollPad;
		}
		
		bgMC.moveTo (this.xPos - hW, this.yPos - hH);
		bgMC.lineTo (this.xPos + hW , this.yPos - hH);
		bgMC.lineTo (this.xPos + hW , this.yPos + hH);
		bgMC.lineTo (this.xPos - hW, this.yPos + hH);
		bgMC.lineTo (this.xPos - hW, this.yPos - hH);
		
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
	* addItem adds a legend item to be displayed.
	*	@param	itemName		String value to be displayed
	*	@param	itemId			Internal Item ID - for use when the legend items
	*							are click-able.
	*	@param	active			Whether the initial state of item is active
	*	@param	bmpd1			active icon bitmapdata
	*	@param	bmpd2			inactive icon bitmapdata
	*/
	public function addItem (itemName:String, itemId:Number, active:Boolean, bmpd1:BitmapData, bmpd2:BitmapData){
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
		//Map the item's chart specific index to legend specific index
		this.arrIdMap[itemId] = count;
		
		//Store it in items array
		this.items.push (objItem);
		count ++;
		
		//Delete
		delete objItem;
	}
	/**
	 * getMaxIconHeight method returns the maximum height of all icons.
	 */
	private function getMaxIconHeight():Void{
		var items:Array = this.items;
		var objItem:Object;
		var bmpd1:BitmapData, bmpd2:BitmapData;
		
		for(var i:Number = 0; i<this.count; ++i){
			
			objItem = items[i];
			
			bmpd1 = objItem.bmpd1;
			bmpd2 = objItem.bmpd2;
			
			if(bmpd1.height > this.maxIconHeight){
				this.maxIconHeight = bmpd1.height;
			}
			if(bmpd2.height > this.maxIconHeight){
				this.maxIconHeight = bmpd2.height;
			}
		}
	}
	/**
	 * setCaption method helps chart to set caption.
	 */
	public function setCaption(strCaption:String):Void{
		//Store the caption
		this.caption = strCaption;
	}
	
	/** 
	 * createTxtFormat method creates the TextFormat object.
	 */
	private function createTxtFormat(fontProp:Object){
		//Create the text format object
		tFormat = new TextFormat ();
		tFormat.align = "left";
		tFormat.font = fontProp.font;
		tFormat.color = parseInt (fontProp.color, 16);
		tFormat.size = fontProp.size;
		tFormat.bold = fontProp.bold;
		tFormat.italic = fontProp.italic;
		tFormat.underline = fontProp.underline;
		tFormat.leftMargin = fontProp.leftMargin;
		tFormat.rightMargin = fontProp.leftMargin;
		tFormat.leading = 3;
	}
	/**
	* resetXY method should be called BEFORE render method is called
	* to reset the X and Y position of the legend. During chart rendering,
	* we do not know the actual position of legend, as the canvas needs to
	* be calculated earlier. But, to calculate canvas position, we need to
	* simulate legend. So, that time, we just provide an arbitrary X,Y value.
	* Later, once we've the X and Y of canvas, we re-set the X,Y of legend for
	* proper display
	*	@param	x	New X Position
	*	@param	y	New Y Position
	*/
	public function resetXY (x:Number, y:Number):Void {
		this.xPos = x;// + xShift;
		this.yPos = y;
	}
	/**
	* reset method resets the legend class.
	*/
	public function reset():Void {
		//Initialize count to 0
		count = 0;
		//Initialize items array
		items = new Array ();
		arrItemMc = new Array();
		maxIconHeight = 0;
		
		this.cleanUp();
	}
	/**
	 * cleanUp method clears the display assests of the legend.
	 */
	private function cleanUp(){
		for(var i in mcBase){
			if(mcBase[i] instanceof MovieClip){
				if (this.clickMode){
					delete mcBase[i].onRelease;
				}
				mcBase[i].removeMovieClip();
			} else if(mcBase[i] instanceof TextField){
				mcBase[i].removeTextField();
			}
		}
		
		sBar.destroy ();
		
	}
	/**
	 * disposeBmpds method disposes all bitmapdata in memory.
	 */
	private function disposeBmpds(){
		var arrItems:Array = this.items;
		var objItem:Object;
		
		for(var i:String in arrItems){
			objItem = arrItems[i];
			objItem.bmpd1.dispose();
			objItem.bmpd2.dispose();
		}
	}
	/*
	* destroy function cleans up the given instance of Legend class.
	*/
	public function destroy(keepData:Boolean):Void {
		if(!keepData){
			this.disposeBmpds();
			delete this.arrIdMap;
		}
		
		//Clear containers
		delete this.items;
		delete this.arrItemMc;
		delete this.fontProp;
		delete this.tFormat;
		//Remove the movie clips created
		bgMC.removeMovieClip ();
		
		//Destroy scroll bar
		sBar.destroy ();
		scrollMC.removeMovieClip ();
		
		for(var i in mcBase){
			if(mcBase[i] instanceof MovieClip){
				if (this.clickMode){
					delete mcBase[i].onRelease;
				}
				mcBase[i].label_txt.removeTextField();
				mcBase[i].mcBmp.removeMovieClip();
				
				mcBase[i].removeMovieClip();
				
			} else if(mcBase[i] instanceof TextField){
				mcBase[i].removeTextField();
			}
			
		}
		mcBase.removeMovieClip();
		//Remove legend MC too
		legendMC.removeMovieClip();
	}
}