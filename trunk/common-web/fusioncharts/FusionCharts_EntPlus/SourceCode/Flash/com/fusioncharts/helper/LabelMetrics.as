/**
* @class LabelMetrics
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010

* LabelMetrics class helps us distinguish the different panels (left , middle , right) based on which  
* x-axis labels will be evenly distributed. It also helps to determine whether we need to work on
* canvasPadding (Line & Area) or plotSpacePercent(column) or we need to decrease canvas width to 
* render labels without overlapping / going out of chart.
*/

//Utils class.
import com.fusioncharts.helper.Utils;

//Class definition.
class com.fusioncharts.helper.LabelMetrics {	
	//The basic required objects. Initiated with default values to eliminate problems related to undefined values
	//in numeric calculations. Please note that the properties of each object should exactly match with the actual corresponding
	//properties defined in the chart or else they will fail to update.
	public var yAxisNameObj:Object = {yAxisNameWidth:0, yAxisNamePadding:5};
	public var sYAxisNameObj:Object = {sYAxisNameWidth:0, yAxisNamePadding:5};
	public var legendObj:Object = {legendPosition:"BOTTOM", legendWidth:0, legendPadding:0};
	//Chart width initiated to root width and margins as defined in the document chart classes.
	public var chartObj:Object = {chartWidth:_root._width, chartLeftMargin:15, chartRightMargin:15}; 
	public var canvasMarginObj:Object = {canvasLeftMargin:0, canvasRightMargin:0, validCanvasLeftMargin:false, validCanvasRightMargin:false};
	public var yAxisValueObj:Object = {yAxisValMaxWidth:0, yAxisValuesPadding:0, yAxisValueAtRight:0};
	//Some more default values
	public var canvasPadding:Number = 0;
	public var defaultCanvasPadding:Boolean = true;
	public var chartType:String = "column";
	public var firstLabelWidth:Number = 0;
	public var lastLabelWidth:Number = 0;
	//Panel objects holding metrics
	public var totalWorkingObj:Object = {totalWorkingWidth:0};												
	public var leftPanelObj:Object = {leftPanelMinWidth:0};
	public var rightPanelObj:Object = {rightPanelMinWidth:0};
	
	public var changeCanvasPadding:Boolean;
	
	//Blank constructor.
	public function LabelMetrics(){
		//None to do.
	}
	/**
	 * reset method is called to reset all initialising params for fresh calculation
	 * of label rendering metrics.
	 */
	public function reset():Void{
		yAxisNameObj = {yAxisNameWidth:0, yAxisNamePadding:5};
		sYAxisNameObj = {sYAxisNameWidth:0, yAxisNamePadding:5};
		legendObj = {legendPosition:"BOTTOM", legendWidth:0, legendPadding:0};
		chartObj = {chartWidth:_root._width, chartLeftMargin:15, chartRightMargin:15}; 
		canvasMarginObj = {canvasLeftMargin:0, canvasRightMargin:0, validCanvasLeftMargin:false, validCanvasRightMargin:false};
		yAxisValueObj = {yAxisValMaxWidth:0, yAxisValuesPadding:0, yAxisValueAtRight:0};
		canvasPadding = 0
		defaultCanvasPadding = true;
		firstLabelWidth = 0;
		lastLabelWidth = 0;
		totalWorkingObj = {totalWorkingWidth:0};												
		leftPanelObj = {leftPanelMinWidth:0};
		rightPanelObj = {rightPanelMinWidth:0};
		changeCanvasPadding;
	}
	
	/**
	 * updateProp method is the generic updater function for all the 
	 * basic objects of this class.
	 * @param	propName	The property to update with values
	 * @param	valueObj	Encapsulated values to be updated
	 */
	public function updateProp(propName:String, valueObj:Object):Void{
		var theObj:Object = this[propName];
		//Check whether the object is valid for the class
		if(theObj != undefined){
			//Check for the valid property
			for(var prop in theObj){
				var valueToUpdate = valueObj[prop];
				if(valueToUpdate != undefined && valueToUpdate != null){
					//Update 
					theObj[prop] = valueToUpdate;
				}
			}
		}
	}
	
	/**
	 * calculatePanels method coordinates to get metrics of panels calculated 
	 * before final label metrics calculation.
	 */
	public function calculatePanels():Void{
		//Calculate total working width
		getTotalWorkingWidth();
		//Calculate left panel
		getLeftPanelMinWidth();
		//Calculate right panel
		getRightPanelMinWidth();
		//Decide the change factor
		getChangeInCanvasPadding();
		
	}
	
	/**
	 * getChangeInCanvasPadding method decides and set the factors neccessary 
	 * for label metrics calculation. This is related to provision of extra space at
	 * extremities.
	 */
	private function getChangeInCanvasPadding():Void{
		//In line or area chart we may need to change canvas padding or decrease canvas width.
		//While, in column chart we always change inter column width.
		if(chartType.toLowerCase() == 'column'){
			changeCanvasPadding = true;
			return
		}
		//Else, for non-column type chart:
		//If any margin is defined we can't ignore them hence we will change canvas padding.
		if(canvasMarginObj.validCanvasLeftMargin || canvasMarginObj.validCanvasRightMargin){
			changeCanvasPadding = true;
		}else{
			changeCanvasPadding = (defaultCanvasPadding) ? true : false;
		}
	}
	/**
	 * getTotalWorkingWidth method determines total horizontal space available
	 * for label management.
	 */
	private function getTotalWorkingWidth():Void{
		//Chart margins deducted
		var totalWidth:Number = chartObj.chartWidth - (chartObj.chartLeftMargin + chartObj.chartRightMargin);
		//Then y axis name and padding is deducted, if defined
		if(yAxisNameObj.yAxisNameWidth > 0){
			totalWidth -= yAxisNameObj.yAxisNameWidth + yAxisNameObj.yAxisNamePadding;
		}
		//In case of DY axis chart, the secondary axis name specific deduction is required, as well.
		if(sYAxisNameObj.sYAxisNameWidth > 0){
			totalWidth -= sYAxisNameObj.sYAxisNameWidth + sYAxisNameObj.yAxisNamePadding;
		}
		//Legend width is deducted, if it is at right.
		if(legendObj.legendPosition.toUpperCase() == "RIGHT"){
			//Check whether legend has a valid width.
			totalWidth -= (legendObj.legendWidth > 0) ? legendObj.legendWidth + legendObj.legendPadding : 0;
		}
		//This value is the total working width - irrespective of other elements in chart.
		totalWorkingObj.totalWorkingWidth = totalWidth;
	}
	
	/**
	 * getLeftPanelMinWidth method is called to evaluate the minimum width of the 
	 * left (virtual) panel/section for the label management. This panel includes
	 * yAxisValue labels, trend labels, yAxisValue padding and canvas padding, as 
	 * the case may be. Or else is governed by valid canvas left margin.
	 */
	private function getLeftPanelMinWidth():Void{
		//Basic left panel conatins the y axis values and y axis value padding.
		var leftPanelMinWidth:Number = (yAxisValueObj.yAxisValMaxWidth > 0) ? yAxisValueObj.yAxisValMaxWidth + yAxisValueObj.yAxisValuesPadding : 0;
		//Now depending on the canvas margin defined or not, need to change the left panel.
		// Case 1. Margin is valid (greater than the above calculated value) - recalculate the panel width 
		//		   and reset canvasPadding to 0 (Line & Area).
		// Case 2. In case invalid, just add canvas padding, when the working chart type is line or area.
		//
		if(canvasMarginObj.validCanvasLeftMargin){
			// Recalculate 
			leftPanelMinWidth = canvasMarginObj.canvasLeftMargin - chartObj.chartLeftMargin;
			//In case of valid y axis name deduct y axis name and padding
			if(yAxisNameObj.yAxisNameWidth > 0){
				leftPanelMinWidth -= yAxisNameObj.yAxisNameWidth + yAxisNameObj.yAxisNamePadding;
			}
		}
		var _canvasPadding:Number = canvasPadding;
		
		//Now in case of this chart is line or area and any valid margin is defined; need to disregard the canvas padding
		//as this has already been taken care of. Reset it to 0.
		if(chartType.toLowerCase() == "line"){
			if((canvasMarginObj.validCanvasLeftMargin || canvasMarginObj.validCanvasRightMargin)){
				_canvasPadding = 0 ;
			}
		}
		
		//Add the canvas padding.
		leftPanelMinWidth += _canvasPadding;
		
		//This value is the minimum width for the left panel.
		leftPanelObj.leftPanelMinWidth = leftPanelMinWidth;
	}
	
	/**
	 * getRightPanelMinWidth method is called to evaluate the minimum width of the 
	 * right (virtual) panel/section for the label management. This panel includes
	 * yAxisValue labels, trend labels, yAxisValue padding, canvas padding and legend,
	 * as the case may be. Or else is governed by valid canvas right margin.
	 */
	private function getRightPanelMinWidth(){
		//The basic width for the right panel should be if any trendlines are defined at right.
		var rightPanelMinWidth:Number = (yAxisValueObj.yAxisValueAtRight > 0) ? yAxisValueObj.yAxisValueAtRight + yAxisValueObj.yAxisValuesPadding : 0;
		//In case a valid right margin is defined, need to re-calculate the right panel width.
		if(canvasMarginObj.validCanvasRightMargin){
			//The margin is deducted
			rightPanelMinWidth = canvasMarginObj.canvasRightMargin - chartObj.chartRightMargin;
			//If legend is defined at right, need to deduct its width + padding, as well
			if(legendObj.legendPosition.toUpperCase() == "RIGHT"){
				rightPanelMinWidth -= (legendObj.legendWidth > 0) ? legendObj.legendWidth + legendObj.legendPadding : 0;
			}
		}
		var _canvasPadding:Number = canvasPadding;
		
		//In case of this chart is being line or area and any valid margin is defined; need to disregard the canvas padding
		//as this has already been taken care of. Reset it to 0.
		if(chartType.toLowerCase() == "line"){
			if((canvasMarginObj.validCanvasLeftMargin || canvasMarginObj.validCanvasRightMargin)){
				_canvasPadding = 0 ;
			}
		}
		
		//Add the canvas padding.
		rightPanelMinWidth += _canvasPadding;
		
		//This value is the minimum width for the right panel.
		rightPanelObj.rightPanelMinWidth = rightPanelMinWidth;
	}
	
	/**
	* getChartMetrics method helps to get the matrices for smart label management. It calculates
	* The the equal divition for the labels, the distance between first and the last plot,
	* the needed width at the right side of the first plot & the needed width at the right
	* side of the last plot.
	* @param	numPlots			Number of data plots.
	* @param	workingWidth		The width where x axis label can be placed.
	* @param	leftPanelMinWidth	Left panel minimum width.
	* @param	rightPanelMinWidth	Right panel minimum width.
	* @param	firstLabelWidth		First label width.
	* @param	lastLabelWidth		RLast label width.
	* @return	Object
	*/
	public function getChartMetrics (numPlots:Number, workingWidth:Number, leftPanelMinWidth:Number, rightPanelMinWidth:Number, firstLabelWidth:Number, lastLabelWidth:Number, changePadding:Boolean):Object {
		
		//Object to be returned.
		var objMetrics:Object;

		//------ Params to be returned ------//
		//Uniform maximum width for each label.
		var unitLabelWidth:Number;
		//Left pannel width
		var leftPanelWidth:Number;
		//Right panel width
		var rightPanelWidth:Number;
		//Width between two extreme  label
		var totalInterPlotWidth:Number;
		//----------------------------------//
		
		//When no. of plot is 1 unit label width is similar to working width and  panels are same.
		if(numPlots == 1){
			objMetrics = {unitLabelWidth: workingWidth, leftPanelWidth: leftPanelMinWidth, rightPanelWidth: rightPanelMinWidth, totalInterPlotWidth: 0};
			return objMetrics;
		}
		
		
		//Calculating maximum label width based on available inputs.
		unitLabelWidth = ((workingWidth - leftPanelMinWidth - rightPanelMinWidth)/(numPlots-1));
		
		//Lower bound
		var lowerBound:Number = 0;
		//Upper bound of unit label width
		var upperBound:Number = unitLabelWidth;
		
		// When unit label width is 1 no need to check further
		if(unitLabelWidth == 1){
			return {unitLabelWidth: unitLabelWidth, leftPanelWidth: leftPanelMinWidth, rightPanelWidth: rightPanelMinWidth, totalInterPlotWidth: (workingWidth - leftPanelMinWidth - rightPanelMinWidth)};
		}
		
		//Loop variable
		var i:Number = 0;
		
		//Expression variables
		var expressionVar:Number;
		
		//Last calculated unit value
		var lastValue:Number = 0;
		var d = 0
		//Re-calculation to get unit label width.
		while(i++ < 256){
			if(changePadding){
				
				expressionVar = (numPlots-1)*unitLabelWidth + leftPanelMinWidth + rightPanelMinWidth - workingWidth + 2 * 
																														Math.max( 
																																 Math.max(leftPanelMinWidth, Math.min(unitLabelWidth, firstLabelWidth)/2) - leftPanelMinWidth, 
																																 Math.max(rightPanelMinWidth, Math.min(unitLabelWidth, lastLabelWidth)/2) - rightPanelMinWidth
																																 );
				
			} else{
				expressionVar = (numPlots-1)*unitLabelWidth + Math.max(leftPanelMinWidth, Math.min(unitLabelWidth, firstLabelWidth)/2) + Math.max(rightPanelMinWidth, Math.min(unitLabelWidth, lastLabelWidth)/2) - workingWidth;
			}
			
			//When previous value and current value (except decimal part) are same, no need to calculate further. 
			if( lastValue*10 >> 0 == unitLabelWidth*10 >> 0 ){
				break;
			}
			//Replace previous value with the current one
			lastValue = unitLabelWidth;
			
			if(expressionVar > 0){
				upperBound = unitLabelWidth;
				unitLabelWidth = (upperBound + lowerBound)/2;
			}else if (expressionVar < 0){
				lowerBound = unitLabelWidth;
				unitLabelWidth = (upperBound + lowerBound)/2;
			}else{
				break;
			}
		}
		
		//Return as object
		if(changePadding){
			
			var canvasPaddingIncrease:Number =	(workingWidth - leftPanelMinWidth - rightPanelMinWidth - ((numPlots-1)*unitLabelWidth))/2;
			
			return {unitLabelWidth: 		unitLabelWidth,
					totalInterPlotWidth: 	((numPlots-1)*unitLabelWidth),
					canvasPaddingIncrease:	canvasPaddingIncrease,
					leftPanelWidth: 		leftPanelMinWidth + canvasPaddingIncrease,
					rightPanelWidth: 		rightPanelMinWidth + canvasPaddingIncrease
					};
		} else {
			return {unitLabelWidth: 		unitLabelWidth,
					totalInterPlotWidth: 	(numPlots-1)*unitLabelWidth,
					leftPanelWidth: 		Math.max(leftPanelMinWidth, Math.min(unitLabelWidth, firstLabelWidth)/2),
					rightPanelWidth: 		Math.max(rightPanelMinWidth, Math.min(unitLabelWidth, lastLabelWidth)/2)
					};
		}
	}
}