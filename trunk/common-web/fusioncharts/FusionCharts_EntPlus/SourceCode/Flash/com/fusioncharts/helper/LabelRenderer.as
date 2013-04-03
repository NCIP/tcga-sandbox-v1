/**
* @class LabelRenderer
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010

* LabelRenderer class helps us render labels with new space management algorithm in v3.2.2.
*/
//Utils
import com.fusioncharts.helper.Utils;

//Class definition
class com.fusioncharts.helper.LabelRenderer {
	//Reference of text creating method from Utils class
	private var createText:Function = Utils.createText;
	//MC to create text when in simulation mode
	private  var _dummyMC:MovieClip;
	//Position of newly created textfield in simulation mode
	private  var _testXPos:Number;
	private  var _testYPos:Number;
	//To hold panel informations 
	private  var leftPanelObj:Object;
	private  var rightPanelObj:Object;
	
	//Holds region defining data
	private  var regionData:Object = new Object();
	//To check whether this is a scroll chart. Initiated to false.
	private  var _scrollChart:Boolean = false;
	//Is scroll required
	private  var _scrollRequired:Boolean = false;
	//Scroll bar params encapsulated
	private  var _scrollBarObj:Object = {scrollHeight:16, scrollPadding:0};
	//Array to store all the created labels
	private var labelObjs:Array;
	//
	//Constructor
	public function LabelRenderer(){
		//None to do
	}
	
	/**
	 * dummyMC is a setter method to let set container to create textfield in, for simulation.
	 * @param	mc	The container MC
	 */
	public  function set dummyMC(mc:MovieClip):Void{
		_dummyMC = mc;
	}
	
	/**
	 * testXPos is a setter method for setting x position of simulated textfield
	 * @param	xPos	The x position of textfield
	 */
	public  function set testXPos(xPos:Number):Void{
		_testXPos = xPos;
	}
	
	
	/**
	 * testYPos is a setter method for setting y position of simulated textfield
	 * @param	yPos	The y position of textfield
	 */
	public  function set testYPos(yPos:Number):Void{
		_testYPos = yPos;
	}
	
	/**
	 * scrollChart is the setter method to indicate if this is a scroll chart by category
	 * @param	val		The boolean to indicate the fact
	 */
	public  function set scrollChart(val:Boolean):Void{
		_scrollChart = val;
	}
	
	/**
	 * scrollRequired is the setter method to indicate if scroll bar is required.
	 * @param	val		The boolean to indicate the fact
	 */
	public  function set scrollRequired(val:Boolean):Void{
		_scrollRequired = val;
	}
	
	/**
	 * scrollBarObj is the stter method to set params for the scroller.
	 * @param	obj		Params encapsulated
	 */
	public  function set scrollBarObj(obj:Object):Void{
		_scrollBarObj.scrollHeight = (!isNaN(obj.scrollHeight)) ? obj.scrollHeight : _scrollBarObj.scrollHeight;			
		_scrollBarObj.scrollPadding = (!isNaN(obj.scrollPadding)) ? obj.scrollPadding : _scrollBarObj.scrollPadding;
	}
	
	/**
	 * renderXAxisLabels method is called to render duly managed labels along x-axis.
	 * @param	containerClip			The container MC to create textField in
	 * @param	depth					The depth of textfield
	 * @param	totalNum				Number of labels to be rendered
	 * @param	labelArr				Labels objects stored in
	 * @param	styleObj				Styles to be applied on the labels
	 * @param	config					this.config object from the chart
	 * @param	labelDisplayType		Mode of label display
	 * @param	canvasObj				this.elements.canvas object from the chart
	 * @param	labelPadding			Label padding value
	 * @param	staggerLines			Number of stagger lines, if in stagger mode
	 * @param	isAnimate				If chart be initially animated
	 * @param	styleManager			The staylemanager instance reference from the chart
	 * @param	macroRef				The macro object from the chart
	 * @param	objId					The object id to work on, from the chart
	 * @param	showDebugRegion			If regions be shown, for debugging
	 */
	public  function renderXAxisLabels(containerClip:MovieClip, depth:Number,
									  totalNum:Number, labelArr:Array, styleObj:Object,
									  config:Object, labelDisplayType:String,
									  canvasObj:Object, labelPadding:Object, staggerLines:Number, labelStep:Number,
									  isAnimate:Boolean, styleManager:Object, macroRef:Object, objId:Number,
									  showDebugRegion:Boolean):Void{
		
		if(!labelStep){
			labelStep = 1;
		}
		
		var labelObj : Object;
		var labelYShift : Number;
		//Count of labels, and index of last label that was shown
		var count:Number=0, lastShowIndex:Number=0;
		//Alignment position for labels
		var align:String = "center";
		//Space metrics for label management in the chart
		var labelMetrics:Object = config.labelMetricsObj;
		var labelYPos:Number;
		//As for WRAP, STAGGER and NONE it is 0; we inititate in to 0
		var labelRotationAngle:Number = 0; 
		var i : Number;
		//Getting the govrning metrics for single line labels
		var singleLineObj:Object = getSingleLineMetrics(styleObj, "A");
		//Define the regions for label management
		defineRegion( labelArr[1].x, labelArr[totalNum].x, canvasObj, labelMetrics);
		//The max left limit for first label
		var maxLeftLimit:Number = regionData.leftPanelStartX;
		//The max right limit for the last label
		var maxRightLimit:Number = regionData.rightPanelEndX;
		//Create the array for all labels
		labelObjs = new Array();
		 
		var maxSlantHeight:Number, xShiftDueSlantToCenter:Number;
		var arrSizes:Array;
		var labelWidth:Number;
		
		//Create the labels
		for (i = 1; i <= totalNum; i ++)
		{
			//If the label is to be shown
			if (labelArr[i].showLabel)
			{
				//Increment count
				count++;
				
				//Defaults to the unit-label-width (for normal wrap mode)
				labelWidth = labelMetrics.unitLabelWidth;
				//For wrap and stagger mode we do not need to render labels with max height. If the required width exceeds 
				//the max allowable, we render with max or we render with the required width
				if(labelDisplayType == "WRAP" || labelDisplayType == "STAGGER"){
					var lblObj:Object = getSingleLineMetrics(styleObj, labelArr [i].label);
					//Label width is at max the "labelWidth" or the required width for the label
					labelWidth = Math.min(labelWidth, lblObj.singleLineWidth);
				}
				//Width should not be less than the width required to render a single letter
				labelWidth = getLabelWidth(styleObj, labelWidth, labelDisplayType, config.labelAngle);
				
				//Set index of last shown label
				lastShowIndex = i;
				
				//Decide the y position for label
				//Consider whether this is a scroll chart
				//the y position for stagger mode should be different only
				if(labelDisplayType != "STAGGER"){
					if(_scrollChart){
						/* NO SCROLL CHART IMPLEMENTATION IN THIS APPROACH */
						labelYPos = canvasObj.h + labelPadding;
						/*if(_scrollRequired){
							labelYPos += _scrollBarObj.scrollHeight + _scrollBarObj.scrollPadding;
						}*/
					}else{
						labelYPos = canvasObj.toY + labelPadding;
					}
				} 
							
				//Now, render them
				switch(labelDisplayType){
					
					case "ROTATE":
						styleObj.align = "center";
						styleObj.vAlign = "bottom";
						//Store the y pos 
						//Check if this is a scroll chart - we need to accomodate scrollbar height
						//Store the angle
						labelRotationAngle = config.labelAngle;
						//Angle = 0 for normal rotation and angle = 45 degree for slant
						var angle:Number = 90 - (360 - labelRotationAngle);
						var lblW:Number, lblH:Number;
						var xSpaceAtleft:Number, numLines:Number;
						
						//For slant labels
						if(angle == 45){
							//The angle in radian
							var angleRad:Number = angle*Math.PI/180;
							//Evaluating the min metrics for any label, for validation
							var singleLineMetricsObj:Object = getSingleLineMetrics(styleObj, "W");
							//Mimimum wdth and height allowable for any label
							var unitW:Number = singleLineMetricsObj.singleLineWidth;
							var unitH:Number = singleLineMetricsObj.singleLineHeight;
							
							//Checking if the set of sizes of textFields for any label (for varying number of lines) is ready.
							if(!arrSizes){
								//Container to hold the textField sizes
								arrSizes = [];
								
								//Max height of the slant label possibe, so that it doesn't overlap the next slant label at its right.
								//Concept: Perpendicular distance between a pair of parallel slant lines drawn from the plot points (on x axis).
								maxSlantHeight = labelMetrics.unitLabelWidth * labelStep * Math.cos(angleRad);
								//Maximum number of lines possible in this height is evaluated. 4 is subtracted from both to avoid gutter in calculation.
								numLines = Math.floor( (maxSlantHeight - 4)/(unitH - 4));
								//However, minimum of one line is a must.
								numLines = Math.max(numLines, 1);
								
								var txtH:Number, txtW:Number;
								
								//Evaluating the textfield sizes, for all possible number of lines and storing them together
								for(var t = 1; t <= numLines; ++t){
									//Getting the textfield height for t number of lines in display.
									txtH = t * (unitH - 4) + 4;
									//Getting the width of such slanted textfield, within vertical bounds/limits.
									txtW = (config.labelAreaHeight - txtH * Math.sin(angleRad)) / Math.cos(angleRad);
									
									//The found width shouldn't be less than the minimum width allowed for any label.
									if(unitW >= txtW){
										//The tetxfield width is reset to the minimum.
										txtW = unitW;
										//Loop iterator is set to terminate by end of this loop.
										//Since, textfield width have reached the lowest point, search for more sizes for better text display is needless.
										t = numLines;
									}
									//Storing the size with others.
									arrSizes.push( {w: txtW, h: txtH} );
								}
								
								
							}
							
							//Plot position; label to be centered about it
							var xPosition:Number = labelArr [i].x;
							
							
							var wLbl:Number, hLbl:Number, maxW:Number;
							var numSizes:Number = arrSizes.length;
							
							//Slant labels have limitation at left and at bottom.
							//Bottom limitation is already worked out while finding width of textfields of varying number of lines.
							
							//Now, its time to validate the text sizes for each label individually against the left limit. This is so required as the labels vary in their
							//mean x-position. The horizontal distance of this x-position from the left end of the allowable scope, viz. till xAxisName or chart left margin if xAxisName is absent, varies.
							//Now, labels shift to left along x-axis to center the top part, about the mean x-position. Since, this x-shift varies with different text size, we have to take care of each of them.
							
							//Since, width in sizes can be changed, replica of arrSizes need to be created for each label.
							var arrSizesOfLabel:Array = [];
							
							//Checking each size against left limit.
							for(var u = 0; u < numSizes; ++u){
								//Width and height of the size
								wLbl = arrSizes[u].w;
								hLbl = arrSizes[u].h;
								
								//Left shift required for this text height
								xShiftDueSlantToCenter = hLbl * Math.cos(angleRad) / 2;
								//Horizontal space at left from left limit to top-right corner of textfield
								xSpaceAtleft = xPosition - maxLeftLimit - xShiftDueSlantToCenter;
								
								//Maximum width of slanted label possible for this plot and for this textHeight
								maxW = xSpaceAtleft/Math.cos(angleRad);
								//Now this allowable max width can't be less than the minimum width for any textfield.
								maxW = Math.max(maxW, unitW);
								
								//Validating larger width against this max allowed
								if(wLbl > maxW){
									wLbl = maxW;
								}
								//Populating the replica of data, with validated textfield width.
								arrSizesOfLabel.push({w: wLbl , h: hLbl});
							}
							
							//Create text box and get height
							labelObj = createText (false, labelArr [i].label, containerClip, depth, 
													labelArr [i].x, labelYPos, 
													labelRotationAngle, styleObj, 
													true, null, null, arrSizesOfLabel);
							
						//For normal rotation of label
						} else {
							lblW = config.wrapLabelWidth;
							lblH = labelWidth;
							
							//Create text box and get height
							labelObj = createText (false, labelArr [i].label, containerClip, depth, 
													labelArr [i].x, labelYPos, 
													labelRotationAngle, styleObj, 
													true, lblW, lblH);
						}
						
					break;
					
					case "WRAP":
						//Set alignment
						styleObj.align = align;
						styleObj.vAlign = "bottom";
						labelObj = createText (false, labelArr [i].label, containerClip, depth, 
												labelArr [i].x, labelYPos, 
												0, styleObj, 
												true, labelWidth, config.labelAreaHeight);
					break;
					
					case "STAGGER":
						//Set alignment
						styleObj.align = align;
						styleObj.vAlign = "bottom";
						//Need to get cyclic position for staggered textboxes
						//Matrix formed is of 2*this.params.staggerLines - 2 rows
						var pos : Number = count % (2 * staggerLines - 2);
						//Last element needs to be reset
						pos = (pos == 0) ? (2 * staggerLines - 2) : pos;
						//Cyclic iteration
						pos = (pos > staggerLines) ? (staggerLines - (pos % staggerLines)) : pos;
						//Get position to 0 base
						pos --;
						//Shift accordingly						
						var labelYShift : Number = config.maxLabelHeight * pos;						
						//store the y pos 
						if(_scrollChart){
							labelYPos = canvasObj.h + labelPadding + labelYShift
						}else{
							labelYPos = canvasObj.toY + labelPadding + labelYShift;
						}
						//In new label management we are adding ellipses to the text 
						//so that no twio long text overlaps with each other. But we are keeping the
						//labels restricted to single line.
						labelObj = createText (false, labelArr [i].label, containerClip, depth, 
												labelArr [i].x, labelYPos,
												0, styleObj,
												true, labelWidth, singleLineObj.singleLineHeight);	
					break;
					
					default:
						//Render normal label - No improved label management implemented.
						styleObj.align = "center";
						styleObj.vAlign = "bottom";
						//store the y pos 
						labelYPos = canvasObj.toY + labelPadding;
						labelObj = createText (false, labelArr [i].label, containerClip, depth, 
												labelArr [i].x, labelYPos,
												0, styleObj,
												false, 0, 0);
					break;
					
				}
				
				//Need to store few values to re-create any of the labels independently, later on
				labelObj.simulation = false;
				labelObj.str = labelArr[i].label;
				//Need to store the next and prev dataplot x position for further label management.
				//This is needed as the next or prev plot may be hidden and no rendering calculations will be
				//executed on them
				if(labelStep == 1){
					labelObj.nextXPos = (i == totalNum) ? maxRightLimit : labelArr [(i+1)].x;
					labelObj.prevXPos = (i == 1) ? maxLeftLimit : labelArr [(i-1)].x;
				} else {
					if(labelDisplayType != "STAGGER"){
						labelObj.nextXPos = (i == totalNum) ? maxRightLimit : Math.min(labelArr [i].x + labelStep * labelMetrics.unitLabelWidth/2, maxRightLimit);
						labelObj.prevXPos = (i == 1) ? maxLeftLimit : Math.max(labelArr [i].x - labelStep * labelMetrics.unitLabelWidth/2, maxLeftLimit);
					} else {
						labelObj.nextXPos = (i == totalNum) ? maxRightLimit : Math.min(labelArr [i].x + labelStep * labelMetrics.unitLabelWidth, maxRightLimit);
						labelObj.prevXPos = (i == 1) ? maxLeftLimit : Math.max(labelArr [i].x - labelStep * labelMetrics.unitLabelWidth, maxLeftLimit);
					}
				}
				labelObj.xPos = labelArr [i].x;
				
				labelObj.yPos = labelYPos;
				labelObj.rotaionAngle = labelRotationAngle;
				labelObj.depth = depth;
				labelObj.styleObj = styleObj;
				labelObj.containerMC = containerClip;
				
				//Apply filter
				styleManager.applyFilters (labelObj.tf, objId);
				
				//Store them as per index, Blank index denauts skipped labels.
				labelObjs[i] = labelObj
				//Increase depth
				depth ++;
			}
		}
		
		/*
		//check to show/hide debug regions.
		if(showDebugRegion){
			showRegion(containerClip, regionData)
		}
		*/
		
		//Process for inter-label management if necessary
		interLabelManagement(labelDisplayType);
		
		//If animation required
		if (isAnimate){
			//Animate each label
			for (i = 1; i <= totalNum; i ++){
				labelObj = labelObjs[i];
				styleManager.applyAnimation (labelObj.tf, objId, macroRef, labelObj.tf._x, 0, labelObj.tf._y, 0, 100, null, null, null);
			}
		}
	}
	
	/**
	 * defineRegion method defines the space metrics.
	 * @param	regionStartX	Starting x position of the region
	 * @param	regionEndX		Ending x position of the region
	 * @param	canvasObj		Object holding params for canvas
	 * @param	labelMetrics	Space metrics for label management
	 */
	private  function defineRegion(regionStartX:Number, regionEndX:Number, canvasObj:Object, labelMetrics:Object):Void{
		//Region data
		regionData.startX = regionStartX;
		regionData.endX = regionStartX + labelMetrics.totalInterPlotWidth;
		regionData.startY = canvasObj.y;
		regionData.endY = canvasObj.toY;
		regionData.leftPanelStartX = regionData.startX - labelMetrics.leftPanelWidth;
		regionData.rightPanelEndX = regionData.endX + labelMetrics.rightPanelWidth;
		//If startX and endX of interplot region is not exactly same as the interplotWidth,  there is a problem
		if((regionData.endX - regionData.startX) != labelMetrics.totalInterPlotWidth){
			//Following commented code is kept for maintainance.
			//trace("PROBLEM ENCOUNTERED :: AMOUNT OF DIFFENCE = "+(labelMetrics.totalInterPlotWidth - (regionData.endX - regionData.startX)))
			//trace(regionData.endX+" - "+regionData.startX+" != "+labelMetrics.totalInterPlotWidth)
		}
		//Create panel objects for future use
		leftPanelObj = createPanelObj(labelMetrics.leftPanelWidth, canvasObj.h, regionData.leftPanelStartX, regionData.startX);
		rightPanelObj = createPanelObj(labelMetrics.rightPanelWidth, canvasObj.h, regionData.endX, regionData.rightPanelEndX);
	}
	
	/**
	 * getSingleLineMetrics method is called to return metrics for a single line presentation of the text.
	 * @param	styleObject		The style object
	 * @param	str				The text to present
	 * @param	displayMode		The display mode
	 * @param	labelAngle		Angle of label
	 * @return					Encapsulated line metrics
	 */
	private  function getSingleLineMetrics(styleObject:Object, str:String, displayMode:String, labelAngle:Number):Object{
		//The label angle is zero if not defined
		labelAngle = (isNaN(labelAngle)) ? 0 : labelAngle;
		//Display mode is wrap if not defined
		displayMode = (displayMode == undefined) ? "wrap" : displayMode;
		//To hold the line metrics for return
		var lineObj:Object = new Object();
		//Need to get the height of a single line with the style
		var tf:Object = createText(true, str, _dummyMC, 1, 
									_testXPos, _testYPos, 
									labelAngle, styleObject, 
									false, 0, 0);
		//Conditional width and height w.r.t. display mode
		lineObj.singleLineHeight = (displayMode.toLowerCase() == 'rotate') ? tf.width : tf.height;
		lineObj.singleLineWidth = (displayMode.toLowerCase() == 'rotate') ? tf.height : tf.width;
		
		return lineObj;
	}
	
	/**
	 * createPanelObj returns encapsulated panel metrics.
	 * @param	w	Width
	 * @param	h	Height
	 * @param	x	X position
	 * @param	y	Y position
	 * @return		The panel metrics object
	 */
	private  function createPanelObj(w:Number, h:Number, x:Number, y:Number):Object{
		return {x:x, y:y, w:w, h:h, endX:(x + w)}
	}
	
	/**
	 * interLabelManagement method checks to achieve better space management for overflowing labels
	 * by utilising unused space from its neighbouring labels.
	 * @param	displayType		The display mode
	 */
	private function interLabelManagement(displayType:String):Void{
		
		var totalPlots:Number = labelObjs.length;
		var i:Number;
		var blankSpaceAtLeft:Number;
		var blankSpaceAtRight:Number;
		
		var playerV:String = _level0.$version;
		var arrStrs:Array = playerV.split(' ');
		var platform:String = arrStrs[0];
		playerV = arrStrs[1];
		
		//Wrap mode
		for(i=1; i<= labelObjs.length; i++){
			
			//Previous label
			var prevLblObj:Object = labelObjs[(i-1)];
			//Next label
			var nextLblObj:Object = labelObjs[(i+1)];
			//Current label
			var currentLblObj:Object = labelObjs[i];
			
			//Skip this level of management for slanted labels
			if(currentLblObj.rotaionAngle == 315){
				continue;
			}
			
			//Calculate only if the current label is valid
			if(currentLblObj != undefined){
				
				switch (displayType.toLowerCase()){
					case 'stagger':
						blankSpaceAtLeft = (currentLblObj.xPos - currentLblObj.prevXPos) - currentLblObj.width/2;					
						blankSpaceAtRight = (currentLblObj.nextXPos - currentLblObj.xPos) - currentLblObj.width/2;
					break;
					
					case  'wrap':
					case 'rotate':
					
						if(prevLblObj != undefined){
							blankSpaceAtLeft = (currentLblObj.xPos - prevLblObj.xPos) - ((prevLblObj.width/2) + (currentLblObj.width/2));
						}else{
							blankSpaceAtLeft = (currentLblObj.xPos - currentLblObj.prevXPos) - currentLblObj.width/2;
						}
						
						
						if(nextLblObj != undefined){
							blankSpaceAtRight = (nextLblObj.xPos - currentLblObj.xPos) - ((nextLblObj.width/2) + (currentLblObj.width/2))
						}else{
							blankSpaceAtRight = (currentLblObj.nextXPos - currentLblObj.xPos) - currentLblObj.width/2
						}
						
					break;
				}
				
				//If current label overflown
				if(currentLblObj.overflow){
					//Considering smaller of the scope from either side
					var valueToChange:Number = Math.min(blankSpaceAtLeft, blankSpaceAtRight);
					//New width for the label
					var newWidth:Number = currentLblObj.width + (valueToChange * 2);
					
                   	if(platform != "MAC"){
						//Again this new width could be bigger than the required width to display the text.
						var dmTxtObj:Object = getSingleLineMetrics(currentLblObj.styleObj, currentLblObj.str);
						//get the minimum
						newWidth = Math.min(newWidth, dmTxtObj.singleLineWidth);
					}
					
					//Re-render the label for the new width
					currentLblObj = recreateLabel(currentLblObj, {prop:"width", value:newWidth, displayType:displayType})
				}
			}
		}
	}
	
	/**
	 * recreateLabel method is called to render the label for better space and text management.
	 * @param	labelObj	The object holding details of the label to render
	 * @param	propObj		Other requisites to re-render the label
	 * @return				The new label object
	 */
	private  function recreateLabel(labelObj:Object, propObj:Object):Object{
		//It is assumed that the wrap mode for recreated labels will be true.
		switch(propObj.prop){
			case "width" :
				//Width should not be less than the width required to render a single letter
				propObj.value = getLabelWidth(labelObj.styleObj, propObj.value, propObj.displayType, labelObj.rotaionAngle);
				if(propObj.displayType.toLowerCase() == "rotate"){
					var newLabel:Object = createText (labelObj.simulation, labelObj.str,
													  labelObj.containerMC, labelObj.depth, 
													  labelObj.xPos, labelObj.yPos, 
													  labelObj.rotaionAngle, labelObj.styleObj, 
													  true, labelObj.height, propObj.value);
				}else{
					var newLabel:Object = createText (labelObj.simulation, labelObj.str,
													  labelObj.containerMC, labelObj.depth, 
													  labelObj.xPos, labelObj.yPos, 
													  labelObj.rotaionAngle, labelObj.styleObj, 
													  true, propObj.value, labelObj.height);
				}
				return newLabel;
			break;
			default:
				//No default behavior as such 
				trace("RECREATION FAILS AS NO VALID PROPERTY DEFINED");
				return null;
			break;
		}
	}
	
	/**
	 * getLabelWidth method returns the minimum width required to render a label.
	 * @param	styleObj		The style object
	 * @param	currentWidth	The required width of the label
	 * @param	displayMode		The display mode
	 * @param	rotationAngle	Angle of label
	 * @return					The width of label to be rendered
	 */
	private function getLabelWidth(styleObj:Object, currentWidth:Number, displayMode:String, rotationAngle:Number):Number{	
		//The minimum width for any text field should at least be a single letter
		//With the style defined. Less than this will make the label meaningless
		//as the text will be obscure or cut off.
		//We assume that the letter 'w' is of standard width.
		var minimumWidth:Object = getSingleLineMetrics(styleObj, "W");
		
		
		if(displayMode.toLowerCase() == 'rotate'){
			var maxWidth:Number = Math.max(minimumWidth.singleLineHeight, currentWidth);
		} else {
			var maxWidth:Number = Math.max(minimumWidth.singleLineWidth, currentWidth);
		}
		
		return maxWidth;
	}
	
	
	
	
	/*
	//Kept commented for debugging purpose in future. Since, the label management development will be in active state for quite some time after v3.2.2.
	//This function can work through an unexposed attribute.
	
	private  function showRegion(targetClip:MovieClip, regionObj:Object){
		if(_scrollChart){
			// NO SCROLL CHART IMPLEMENTATION IN THIS APPROACH
			//targetClip = targetClip._parent;
			//var regionMC:MovieClip = targetClip.createEmptyMovieClip("region_mc", targetClip.getNextHighestDepth());
			//
		}else{
			var regionMC:MovieClip = targetClip.createEmptyMovieClip("region_mc", targetClip.getNextHighestDepth());
		}
		//drawing interplot width
		regionMC.beginFill(0xCC0000, 40);
		regionMC.moveTo(regionObj.startX, regionObj.startY);
		regionMC.lineTo(regionObj.endX, regionObj.startY);
		regionMC.lineTo(regionObj.endX, regionObj.endY);
		regionMC.lineTo(regionObj.startX, regionObj.endY);
		regionMC.lineTo(regionObj.startX, regionObj.startY);
		regionMC.endFill();
		//drawing left panel
		regionMC.beginFill(0x00CC00, 40);
		regionMC.moveTo(regionObj.leftPanelStartX, regionObj.startY);
		regionMC.lineTo(regionObj.startX, regionObj.startY);
		regionMC.lineTo(regionObj.startX, regionObj.endY);
		regionMC.lineTo(regionObj.leftPanelStartX, regionObj.endY);
		regionMC.lineTo(regionObj.leftPanelStartX, regionObj.startY);
		regionMC.endFill();
		//drawing right panel
		regionMC.beginFill(0x0000CC, 40);
		regionMC.moveTo(regionObj.endX, regionObj.startY);
		regionMC.lineTo(regionObj.rightPanelEndX, regionObj.startY);
		regionMC.lineTo(regionObj.rightPanelEndX, regionObj.endY);
		regionMC.lineTo(regionObj.endX, regionObj.endY);
		regionMC.lineTo(regionObj.endX, regionObj.startY);
	}
	*/
}