/**
* @class LegendWithSubItems
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* AdvancedLegend class helps us generate legend for a chart
* that allows click on individual legend items with 
* state-specific toggling icons.
*/
//Legend Class
import com.fusioncharts.helper.AdvancedLegend;
import com.fusioncharts.helper.ScrollVBar;
import com.fusioncharts.extensions.StringExt;
import flash.display.BitmapData;
import mx.events.EventDispatcher;
//
class com.fusioncharts.helper.LegendWithSubItems extends  AdvancedLegend{
	public var itemMap:Array;
	//Scroll bar
	var sBar:ScrollVBar;
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
	function LegendWithSubItems (targetMC:MovieClip, fontProp:Object, clickMode:Boolean, alignPos:String, xPos:Number, yPos:Number, maxWidth:Number, maxHeight:Number, allowDrag:Boolean, stageWidth:Number, stageHeight:Number,bgColor:String, bgAlpha:Number, borderColor:String, borderThickness:Number, borderAlpha:Number, scrollBgColor:String, scrollBarColor:String, btnColor:String, numColumns:Number)
	{
		super(targetMC, fontProp, clickMode, alignPos, xPos, yPos, maxWidth, maxHeight, allowDrag, stageWidth, stageHeight,bgColor, bgAlpha, borderColor, borderThickness, borderAlpha, scrollBgColor, scrollBarColor, btnColor, numColumns);
		this.itemMap = [];
	}
	/**
	* addSubItem adds a legend item to be displayed.
	*	@param	itemName			String value to be displayed
	*	@param	itemDetails			All information this current item.
	*	@param	active				Whether the initial state of item is active
	*	@param	bmpd1				active icon bitmapdata
	*	@param	bmpd2				inactive icon bitmapdata
	*/
	public function addItem(itemName:String, itemDetails:Object, active:Boolean, bmpd1:BitmapData, bmpd2:BitmapData):Void
	{
		//We store each item as an object in items array.
		//Create an object from the given parameters and store it
		var objItem:Object = new Object ();
		
		//Replace < with &lt; if not in HTML mode
		itemName = StringExt.replace (itemName, "<", "&lt;");
		objItem.name = itemName;
		
		objItem.id = itemDetails.itemId;
		objItem.subId = itemDetails.subItemId;
		
		objItem.active = active;
		objItem.bmpd1 = bmpd1;
		objItem.bmpd2 = bmpd2;
		
		objItem.color = '0x' + itemDetails.color;
		//check that this is a main item or su item.
		//to create the arraIdMap  
		if(objItem.subId == null){
			this.arrIdMap[objItem.id] = {count: count, data: []};
		} else {
			this.arrIdMap[objItem.id].data[objItem.subId] = count;
		}
		
		//Store it in items array
		this.items.push (objItem);
		count ++;
		//Crete an internal map between item and subitems
		//when ever addItem hase been called this method trak the
		//the internal id of this newly added items.
		createItemMap();
		
		//Delete
		delete objItem;
	}
	/**
	 * render method renders the legend finally.
	 */
	public function render():Void
	{
		super.render();
		for(var i:Number = 0; i<count; ++i){
			var objItem:Object = this.items[i];
			var mcItem:MovieClip = arrItemMc[i];
			var nextMcItem:MovieClip = arrItemMc[(i+1)];
			var index:Number = mcItem.getNextHighestDepth()
			var mcBg:MovieClip = mcItem.createEmptyMovieClip('mcBg_'+index, index);
			var startX:Number = 0;
			var startY:Number = 0;
			var endX:Number =  (mcItem._y == nextMcItem._y ) ? (nextMcItem._x - mcItem._x) : (bgMC._x + bgMC._width) - mcItem._x;
			var endY:Number = mcItem._height;
			mcBg.lineStyle();
			mcBg._alpha = 20;
			mcBg.beginFill(parseInt(objItem.color, 16));
			mcBg.moveTo(startX,startY);
			mcBg.lineTo(endX, startY);
			mcBg.lineTo(endX, endY);
			mcBg.lineTo(startX, endY);
			mcBg.lineTo(startX, startY);
		}
		//Create the id map between mainItem and subItem
	}
	/**
	 * itemOnRelease method called when a item has been clicked.
	 * by the user
	 */
	private function itemOnRelease(){
		var objItem:Object = this.items[arguments.caller.index];
		this.clickEvent(objItem.id, objItem.subId);
	}
	/**
	 * clickEvent method is called when a legend item has been clicked
	 * by the user. Here, we dispatch the event to parent class.
	 * @param	index	    External index (sent from the parent class data series index)  
	 *					    of the item which was clicked irrespective of main or sub item.
	 * @param	subIndex    External index (sent from the parent class sub item index of 
	 *                      that data series) of the item which was clicked if it;s a sub item
	 *                      a numeric id was received otherwise null received.
	 */
	public function clickEvent(index:Number, subIndex:Number):Void
	{
		
		//Get reference of the legend specific id of the item from chart specific id.
		
		var seriesObj:Object = this.arrIdMap[index];
		var itemIndex:Number;
		
		if(subIndex == null){
			itemIndex = seriesObj.count;
		}else{
			itemIndex = seriesObj.data[subIndex];
		}
		
		var objItem:Object = this.items[itemIndex];
		//Change the item icon state
		var proceed:Boolean = this.reverseItemState(itemIndex);
		
		if(!proceed){
			return;
		}
		//Dispatch the click event
		this.dispatchEvent({type:"legendClick", target:this, name:objItem.name, active:objItem.active, index:index});
	}
	/**
	 * createItemMap method is called to create a item map.
	 */
	private function createItemMap():Void
	{
		var i:Number;
		var obj:Object;
		var objSub:Object;
		var len:Number = this.items.length;
		var itemMap:Array = this.itemMap;
		var mapObj:Object;
		
		for(i = 0; i < len; i++){
			obj = this.items[i];
			
			if( obj.subId == null ){
				
				if( !itemMap[obj.id] ){
					itemMap[obj.id] = {};
					itemMap[obj.id]['subItems'] = [];
				}
				
				mapObj = itemMap[obj.id];
				
				mapObj['active'] = obj.active;
				mapObj['internalId'] = i;
				
			}else{
				
				objSub = {};
				objSub['active'] = obj.active;
				objSub['internalId'] = i;
				
				itemMap[obj.id]['subItems'][obj.subId] = objSub;
				
			}
		}
	}
	/**
	 * reverseItemState method reverses the state of the item icon.
	 * @tparam	index	Id of item.
	 */
	private function reverseItemState(index:Number):Boolean
	{
		/* 
		if main item
			if main inactive
				make main active
				revert back last states of its sub-items
			else if main active
				make main inactive
				store active states of its sub-items
				make all sub-items inactive
		else if sub item
			if main active
				if item active
					make inactive
				else if inactive
					make active
		*/
		var objItem:Object = this.items[index];
		var obj:Object, subObj:Object, mainObj:Object;
		var i:Number;
		
		//Set the active flag to reverse - for the item.
		var mcItem:MovieClip;
		var bmp:BitmapData;
		var mapSubItem:Object;
		
		var mapSeriesObj:Object = this.itemMap[objItem.id];
		
		//for main
		if(objItem.subId == null){
			mainObj = objItem;
			var mapSubItems:Array = mapSeriesObj['subItems'];
			
			var len:Number = mapSubItems.length;
			
			for(i = 0; i < len; i++){
				mapSubItem = mapSubItems[i];
				var subIndex:Number = mapSubItem.internalId;
				
				subObj = this.items[subIndex];
				
				mcItem = arrItemMc[subIndex];
				
				if(mainObj.active){
					//store the state
					mapSubItem.active = subObj.active;
					
					if(mapSubItem.active){
						bmp = subObj.bmpd2;
						mcItem.mcBmp.attachBitmap(bmp, 0);
						subObj.active = false;
					}
					
				}else{
					//check the state and act
					if(mapSubItem.active){
						bmp = subObj.bmpd1;
						mcItem.mcBmp.attachBitmap(bmp, 0);
						subObj.active = true;
					}
				}
			}
			mainObj.active = !mainObj.active;
			mapSeriesObj.active = mainObj.active;
			
			//The item MC
			mcItem = arrItemMc[mapSeriesObj.internalId];
			
			bmp = (mainObj.active)? mainObj.bmpd1: mainObj.bmpd2;
			mcItem.mcBmp.attachBitmap(bmp, 0);
			
			return true;
			
		// for subItem click
		} else {
			
			var seriesItem:Object = this.items[mapSeriesObj.internalId];
			
			if(!seriesItem.active){
				return false;
			}
			
			bmp = (objItem.active)? objItem.bmpd2 : objItem.bmpd1;
			mcItem = arrItemMc[mapSeriesObj['subItems'][objItem.subId].internalId];
			mcItem.mcBmp.attachBitmap(bmp, 0);
			objItem.active = !objItem.active;
			
			return true;
		}
	}
}