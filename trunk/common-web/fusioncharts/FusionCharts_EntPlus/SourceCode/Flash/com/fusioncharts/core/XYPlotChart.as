/**
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
* XYPlotChart chart extends the Chart class to render the generic
* template for a chart with X and Y Plot axis(like Scatter, Bubble)
*/
//Import parent Chart class
import com.fusioncharts.core.Chart;
import com.fusioncharts.helper.Utils;
import com.fusioncharts.helper.Logger;
import com.fusioncharts.extensions.DrawingExt;
import com.fusioncharts.extensions.ColorExt;
import com.fusioncharts.extensions.MathExt;
//Legend Class
import com.fusioncharts.helper.AdvancedLegend;
import com.fusioncharts.helper.LegendIconGenerator;
import flash.display.BitmapData;
import mx.transitions.Tween;
//Delegate
import mx.utils.Delegate;
class com.fusioncharts.core.XYPlotChart extends Chart {
	//Version number (if different from super Chart class)
	//private var _version:String = "3.0.0";
	//Instance variables
	//Storage for data
	private var dataset:Array;
	//Array to store x-axis categories (labels)
	private var categories:Array;
	//Number of data sets
	private var numDS:Number = 0;
	//Number of data items (max)
	private var num:Number = 0;
	//Number of categories
	private var numCat:Number = 0;
	//Storage for div lines
	private var divLines:Array;
	//Axis charts can have trend lines. trendLines array
	//stores the trend lines for an axis chart.
	private var trendLines:Array;
	//Vertical trend lines
	private var vTrendLines:Array;
	//numTrendLines stores the number of trend lines
	private var numTrendLines:Number = 0;
	//Number of trend lines below
	private var numTrendLinesBelow:Number = 0;
	//Vertical trend lines
	private var numVTrendLines:Number = 0;
	//Reference to legend component of chart
	private var lgnd:AdvancedLegend;
	//Reference to legend movie clip
	private var lgndMC:MovieClip;
	//Storage for vertical div lines
	private var vDivLines:Array;
	// Store scales values for yaxis 
	private var yNumberScales:Object = {};
	// Store scales values for xaxis 
	private var xNumberScales:Object = {};
	/**
	* Constructor function. We invoke the super class'
	* constructor.
	*/
	function XYPlotChart(targetMC:MovieClip, depth:Number, width:Number, height:Number, x:Number, y:Number, debugMode:Boolean, lang:String, scaleMode:String, registerWithJS:Boolean, DOMId:String) {
		//Invoke the super class constructor
		super(targetMC, depth, width, height, x, y, debugMode, lang, scaleMode, registerWithJS, DOMId);
		//Data Storage array
		this.categories = new Array();
		this.dataset = new Array();
		//Initialize div lines array
		this.divLines = new Array();
		//Initialize the trendlines array
		this.trendLines = new Array();
		this.vTrendLines = new Array();
		//Initialize vertical div lines array
		this.vDivLines = new Array();
	}
	/**
	* getMaxYDataValue method gets the maximum y-axis data value present
	* in the data.
	*	@return	The maximum value present in the data provided.
	*/
	private function getMaxYDataValue():Number {
		var maxValue:Number;
		var firstNumberFound:Boolean = false;
		var i:Number, j:Number, k:Number;
		var max:Number;
		var dataset:Object;

		for (i=1; i<=this.numDS; i++) {
			for (j=1; j<=this.num; j++) {
				//By default assume the first non-null number to be maximum
				if (firstNumberFound == false) {
					if (this.dataset[i].data[j].isDefined == true) {
						//Set the flag that "We've found first non-null number".
						firstNumberFound = true;
						//Also assume this value to be maximum.
						maxValue = this.dataset[i].data[j].yv;
					}
				} else {
					//If the first number has been found and the current data is defined, compare
					if (this.dataset[i].data[j].isDefined) {
						//Store the greater number
						maxValue = (this.dataset[i].data[j].yv>maxValue) ? this.dataset[i].data[j].yv : maxValue;
					}
				}
			}
		}
		for (k=1; k<=this.numDS; k++) {
			//Reference of current dataset.
			dataset = this.dataset[k];
			
			if(!dataset.showRegressionLine){
				continue;
			}
			max = Math.max(dataset.regressionPoints[0].yPos, dataset.regressionPoints[1].yPos);
			maxValue = Math.max(max, maxValue);
		}
		return maxValue;
	}
	/**
	* getMinYDataValue method gets the minimum y-axis data value present
	* in the data
	*	@reurns		The minimum value present in data
	*/
	private function getMinYDataValue():Number {
		var minValue:Number;
		var firstNumberFound:Boolean = false;
		var i:Number, j:Number, k:Number;
		var min:Number;
		var dataset:Object;
		for (i=1; i<=this.numDS; i++) {
			for (j=1; j<=this.num; j++) {
				//By default assume the first non-null number to be minimum
				if (firstNumberFound == false) {
					if (this.dataset[i].data[j].isDefined == true) {
						//Set the flag that "We've found first non-null number".
						firstNumberFound = true;
						//Also assume this value to be minimum.
						minValue = this.dataset[i].data[j].yv;
					}
				} else {
					//If the first number has been found and the current data is defined, compare
					if (this.dataset[i].data[j].isDefined) {
						//Store the lesser number
						minValue = (this.dataset[i].data[j].yv<minValue) ? this.dataset[i].data[j].yv : minValue;
					}
				}
			}
		}
		for (k=1; k<=this.numDS; k++) {
			//Reference of current dataset.
			dataset = this.dataset[k];
			
			if(!dataset.showRegressionLine){
				continue;
			}
			min = Math.min(dataset.regressionPoints[0].yPos, dataset.regressionPoints[1].yPos);
			minValue = Math.min(min, minValue);
		}
		return minValue;
	}
	/**
	* getMaxXDataValue method gets the maximum x-axis data value present
	* in the data.
	*	@return	The maximum value present in the data provided.
	*/
	private function getMaxXDataValue():Number {
		var maxValue:Number;
		var firstNumberFound:Boolean = false;
		var i:Number, j:Number, k:Number;
		var dataset:Object;
		var max:Number;
		for (i=1; i<=this.numDS; i++) {
			for (j=1; j<=this.num; j++) {
				//By default assume the first non-null number to be maximum
				if (firstNumberFound == false) {
					if (this.dataset[i].data[j].isDefined == true) {
						//Set the flag that "We've found first non-null number".
						firstNumberFound = true;
						//Also assume this value to be maximum.
						maxValue = this.dataset[i].data[j].xv;
					}
				} else {
					//If the first number has been found and the current data is defined, compare
					if (this.dataset[i].data[j].isDefined) {
						//Store the greater number
						maxValue = (this.dataset[i].data[j].xv>maxValue) ? this.dataset[i].data[j].xv : maxValue;
					}
				}
			}
		}
		for (k=1; k<=this.numDS; k++) {
			//Reference of current dataset.
			dataset = this.dataset[k];
			
			if(!dataset.showRegressionLine){
				continue;
			}
			max = Math.max(dataset.regressionPoints[0].xPos, dataset.regressionPoints[1].xPos);
			maxValue = Math.max(max, maxValue);
		}
		return maxValue;
	}
	/**
	* getMinXDataValue method gets the minimum x-axis data value present
	* in the data
	*	@reurns		The minimum value present in data
	*/
	private function getMinXDataValue():Number {
		var minValue:Number;
		var firstNumberFound:Boolean = false;
		var i:Number, j:Number, k:Number;
		var min:Number;
		var dataset:Object;
		for (i=1; i<=this.numDS; i++) {
			for (j=1; j<=this.num; j++) {
				//By default assume the first non-null number to be minimum
				if (firstNumberFound == false) {
					if (this.dataset[i].data[j].isDefined == true) {
						//Set the flag that "We've found first non-null number".
						firstNumberFound = true;
						//Also assume this value to be minimum.
						minValue = this.dataset[i].data[j].xv;
					}
				} else {
					//If the first number has been found and the current data is defined, compare
					if (this.dataset[i].data[j].isDefined) {
						//Store the lesser number
						minValue = (this.dataset[i].data[j].xv<minValue) ? this.dataset[i].data[j].xv : minValue;
					}
				}
			}
		}		
		for (k=1; k<=this.numDS; k++) {
			//Reference of current dataset.
			dataset = this.dataset[k];
			
			if(!dataset.showRegressionLine){
				continue;
			}
			min = Math.min(dataset.regressionPoints[0].xPos, dataset.regressionPoints[1].xPos);
			minValue = Math.min(min, minValue);
		}
		return minValue;
	}
	/**
	* getXAxisLimits method helps calculate the X-axis limits based
	* on the given maximum and minimum value.
	* @param	maxValue		Maximum numerical value (X) present in data
	*	@param	minValue		Minimum numerical value (X) present in data
	*/
	private function getXAxisLimits(maxValue:Number, minValue:Number, stopMaxAtZero:Boolean, setMinAsZero:Boolean):Void {
		//Flag variable to keep track on parameters values
		var isMinValid:Boolean = true;
		var isMaxValid:Boolean = true;
		
		//First check if both maxValue and minValue are proper numbers.
		//Else, set defaults as 0.1,0
		if(isNaN(maxValue) == true || maxValue == undefined){
			maxValue = 0.1;
			isMaxValid = false;
		}
		if(isNaN(minValue) == true || minValue == undefined){
			minValue = 0;
			isMinValid = false;
		}
		//Or, if only 0 data is supplied
		if ((maxValue == minValue) && (maxValue == 0)) {
			maxValue = 0.1;
		}
		//Defaults for stopMaxAtZero and setMinAsZero  
		stopMaxAtZero = Utils.getFirstValue(stopMaxAtZero, false);
		setMinAsZero = Utils.getFirstValue(setMinAsZero, true);
		
		//Get the maximum power of 10 that is applicable to maxvalue  
		//The Number = 10 to the power maxPowerOfTen + x (where x is another number)
		//For e.g., in 99 the maxPowerOfTen will be 1 = 10^1 + 89
		//And for 102, it will be 2 = 10^2 + 2
		var maxPowerOfTen:Number = Math.floor(Math.log(Math.abs(maxValue))/Math.LN10);
		//Get the minimum power of 10 that is applicable to maxvalue
		var minPowerOfTen:Number = Math.floor(Math.log(Math.abs(minValue))/Math.LN10);
		//Find which powerOfTen (the max power or the min power) is bigger
		//It is this which will be multiplied to get the x-interval
		var powerOfTen:Number = Math.max(minPowerOfTen, maxPowerOfTen);
		var x_interval:Number = Math.pow(10, powerOfTen);
		//For accomodating smaller range values (so that scale doesn't represent too large an interval
		if (Math.abs(maxValue)/x_interval<2 && Math.abs(minValue)/x_interval<2) {
			powerOfTen--;
			x_interval = Math.pow(10, powerOfTen);
		}
		//If the x_interval of min and max is way more than that of range.  
		//We need to reset the x-interval as per range
		var rangePowerOfTen:Number = Math.floor(Math.log(maxValue-minValue)/Math.LN10);
		var rangeInterval:Number = Math.pow(10, rangePowerOfTen);
		//Now, if rangeInterval is 10 times less than x_interval, we need to re-set
		//the limits, as the range is too less to adjust the axis for max,min.
		//We do this only if range is greater than 0 (in case of 1 data on chart).
		if (((maxValue-minValue)>0) && ((x_interval/rangeInterval)>=10)) {
			x_interval = rangeInterval;
			powerOfTen = rangePowerOfTen;
		}
		//Calculate the x-axis upper limit  
		var x_topBound:Number = (Math.floor(maxValue/x_interval)+1)*x_interval;
		//Calculate the x-axis lower limit
		var x_lowerBound:Number;
		//If the min value is less than 0
		if (minValue<0) {
			//Then calculate by multiplying negative numbers with x-axis interval
			x_lowerBound = -1*((Math.floor(Math.abs(minValue/x_interval))+1)*x_interval);
		} else {
			if (setMinAsZero) {
				x_lowerBound = 0;
			}
			else
			{
				x_lowerBound = Math.floor(Math.abs(minValue/x_interval)-1)*x_interval;
				//Now, if minValue>=0, we keep x_lowerBound to 0 - as for values like minValue 2
				//lower bound goes negative, which is not required.
				x_lowerBound = (x_lowerBound<0) ? 0 : x_lowerBound;
			}			
		
		}		
		//MaxValue cannot be less than 0 if stopMaxAtZero is set to true
		if (stopMaxAtZero && maxValue<=0) {
			x_topBound = 0;
		}
		//Need to make a check as to whether the user has provided an upper limit  
		//and lower limit.
		if (isNaN(this.params.xAxisMaxValue)) {
			this.config.xMaxGiven = false;
		} else {
			this.config.xMaxGiven = true;
		}
		if (isNaN(this.params.xAxisMinValue)) 
		{
			this.config.xMinGiven = false;
		} else {
			this.config.xMinGiven = true;
		}
		//If he has provided it and it is valid, we leave it as the upper limit
		//Else, we enforced the value calculate by us as the upper limit.
		if (isNaN(this.params.xAxisMaxValue) || (Number(this.params.xAxisMaxValue)< maxValue && isMaxValid)) {
			this.config.xMax = x_topBound;
		} else {
			this.config.xMax = Number(this.params.xAxisMaxValue);
		}
		//Now, we do the same for x-axis lower limit
		if (isNaN(this.params.xAxisMinValue) ||(Number(this.params.xAxisMinValue)>minValue && isMinValid)) {
			this.config.xMin = x_lowerBound;
		} else {
			this.config.xMin = Number(this.params.xAxisMinValue);
		}
		//If xMax is lower than xMax, reset it
		if(this.config.xMax < this.config.xMin){
			this.config.xMax = this.config.xMin + this.config.xMax;
		}
		//Store axis range
		this.config.vRange = Math.abs(this.config.xMax-this.config.xMin);
		//Store interval
		this.config.xInterval = x_interval;	
	}
	/**
	* getYAxisLimits method helps calculate the Y-axis limits based
	* on the given maximum and minimum value.
	* @param	maxValue		Maximum numerical value (Y) present in data
	*	@param	minValue		Minimum numerical value (Y) present in data
	*	@param	stopMaxAtZero	Flag indicating whether maximum value can
	*							be less than 0.
	*	@param	setMinAsZero	Whether to set the lower limit as 0 or a greater
	*							appropriate value (when dealing with positive numbers)
	*/
	private function getYAxisLimits(maxValue:Number, minValue:Number, stopMaxAtZero:Boolean, setMinAsZero:Boolean):Void {
		//Flag variable to keep track on parameters values
		var isMinValid:Boolean = true;
		var isMaxValid:Boolean = true;
		
		//First check if both maxValue and minValue are proper numbers.
		//Else, set defaults as 0.1,0
		//For Max Value
		if(isNaN(maxValue) == true || maxValue == undefined){
			maxValue = 0.1;
			isMaxValid = false;
		}
		//For Min Value
		if(isNaN(minValue) == true || minValue == undefined){
			minValue = 0;
			isMinValid = false;
		}
		//Or, if only 0 data is supplied
		if ((maxValue == minValue) && (maxValue == 0)) {
			maxValue = 0.1;
		}
		//Defaults for stopMaxAtZero and setMinAsZero  
		stopMaxAtZero = Utils.getFirstValue(stopMaxAtZero, false);
		setMinAsZero = Utils.getFirstValue(setMinAsZero, true);
		//Get the maximum power of 10 that is applicable to maxvalue
		//The Number = 10 to the power maxPowerOfTen + x (where x is another number)
		//For e.g., in 99 the maxPowerOfTen will be 1 = 10^1 + 89
		//And for 102, it will be 2 = 10^2 + 2
		var maxPowerOfTen:Number = Math.floor(Math.log(Math.abs(maxValue))/Math.LN10);
		//Get the minimum power of 10 that is applicable to maxvalue
		var minPowerOfTen:Number = Math.floor(Math.log(Math.abs(minValue))/Math.LN10);
		//Find which powerOfTen (the max power or the min power) is bigger
		//It is this which will be multiplied to get the y-interval
		var powerOfTen:Number = Math.max(minPowerOfTen, maxPowerOfTen);
		var y_interval:Number = Math.pow(10, powerOfTen);
		//For accomodating smaller range values (so that scale doesn't represent too large an interval
		if (Math.abs(maxValue)/y_interval<2 && Math.abs(minValue)/y_interval<2) {
			powerOfTen--;
			y_interval = Math.pow(10, powerOfTen);
		}
		//If the y_interval of min and max is way more than that of range.  
		//We need to reset the y-interval as per range
		var rangePowerOfTen:Number = Math.floor(Math.log(maxValue-minValue)/Math.LN10);
		var rangeInterval:Number = Math.pow(10, rangePowerOfTen);
		//Now, if rangeInterval is 10 times less than y_interval, we need to re-set
		//the limits, as the range is too less to adjust the axis for max,min.
		//We do this only if range is greater than 0 (in case of 1 data on chart).
		if (((maxValue-minValue)>0) && ((y_interval/rangeInterval)>=10)) {
			y_interval = rangeInterval;
			powerOfTen = rangePowerOfTen;
		}
		//Calculate the y-axis upper limit  
		var y_topBound:Number = (Math.floor(maxValue/y_interval)+1)*y_interval;
		//Calculate the y-axis lower limit
		var y_lowerBound:Number;
		//If the min value is less than 0
		if (minValue<0) {
			//Then calculate by multiplying negative numbers with y-axis interval
			y_lowerBound = -1*((Math.floor(Math.abs(minValue/y_interval))+1)*y_interval);
		} else {
			//Else, simply set it to 0.
			if (setMinAsZero) {
				y_lowerBound = 0;
			} else {
				y_lowerBound = Math.floor(Math.abs(minValue/y_interval)-1)*y_interval;
				//Now, if minValue>=0, we keep x_lowerBound to 0 - as for values like minValue 2
				//lower bound goes negative, which is not required.
				y_lowerBound = (y_lowerBound < 0) ?0 : y_lowerBound;
			}
		}
		//MaxValue cannot be less than 0 if stopMaxAtZero is set to true
		if (stopMaxAtZero && maxValue<=0) {
			y_topBound = 0;
		}
		//Now, we need to make a check as to whether the user has provided an upper limit  
		//and lower limit.
		if (this.params.yAxisMaxValue == null || this.params.yAxisMaxValue == undefined || this.params.yAxisMaxValue == "" || Number(this.params.yAxisMaxValue) == NaN || this.params.yAxisMaxValue == this.params.yAxisMinValue ) {
			this.config.yMaxGiven = false;
		} else {
			this.config.yMaxGiven = true;
		}
		if (this.params.yAxisMinValue == null || this.params.yAxisMinValue == undefined || this.params.yAxisMinValue == "" || Number(this.params.yAxisMinValue) == NaN) {
			this.config.yMinGiven = false;
		} else {
			this.config.yMinGiven = true;
		}
		//If he has provided it and it is valid, we leave it as the upper limit
		//Else, we enforced the value calculate by us as the upper limit.
		if (this.config.yMaxGiven == false || (this.config.yMaxGiven == true && Number(this.params.yAxisMaxValue)<maxValue && isMaxValid)) {
			this.config.yMax = y_topBound;
		} else {
			this.config.yMax = Number(this.params.yAxisMaxValue);
		}
		//Now, we do the same for y-axis lower limit
		if (this.config.yMinGiven == false || (this.config.yMinGiven == true && Number(this.params.yAxisMinValue)>minValue && isMinValid)) {
			this.config.yMin = y_lowerBound;
		} else {
			this.config.yMin = Number(this.params.yAxisMinValue);
		}
		//If ymin is greater than ymax then reset it
		if(this.config.yMin >= this.config.yMax){
			this.config.yMax = this.config.yMax + this.config.yMin;
		}
		//Store axis range
		this.config.range = Math.abs(this.config.yMax-this.config.yMin);
		//Store interval
		this.config.interval = y_interval;
	}
	/**
	* calcDivs method calculates the best div line interval for the given/calculated
	* yMin, yMax, specified numDivLines and adjustDiv.
	* We re-set the y axis min and max value, if both were calculated by our
	* us, so that we get a best value according to numDivLines. The idea is to have equal
	* intervals on the axis, based on numDivLines specified. We do so, only if both yMin and
	* yMax have been calculated as per our values. Else, we adjust the numDiv Lines.
	*/
	private function calcDivs():Void {
		/**
		* There can be four cases of yMin, yMax.
		* 1. User doesn't specify either. (our program calculates it).
		* 2. User specifies both in XML. (which our program still validates)
		* 3. User specifies only yMin. (we provide missing data)
		* 4. User specifies only yMax. (we provide missing data)
		*
		* Apart from this, the user can specify numDivLines (which if not specified takes a
		* default value of 4). Also, the user can specify adjustDiv (which can be 1 or 0).
		* adjustDiv works in all four cases (1,2,3,4).
		* Case 1 is modified to occur as under now: User doesn't specify either yMin or yMax
		* and adjustDiv is set to true (by default). If the user doesn't specify either yMin or
		* yMax, but adjustDiv is set to false, it doesn't appear as Case 1. However, if adjustDiv
		* is set to true and yMin,yMax is automatically calculated by FusionCharts, we adjust the
		* calculated yMin,yMax so that the given number of div lines can be well adjusted within.
		*
		* In case 2,3,4, we adjust numDivLines so that they space up equally based on the interval
		* and decimals required.
		*
		* So, the difference between Case 1 and Case 2,3,4 is that in Case 1, we adjust limits
		* to accomodate specified number of div lines. In Case 2,3,4, we adjust numDivLines to
		* accomodate within the given limits (y-axis range).
		*
		* numDivLines is always our primary focus when calculating them in all cases. In Case 1,
		* it's kept constant as center of calculation. In Case 2, it's modified to get a best
		* value.
		*
		* Now, for case 1, we can have three sub cases:
		* 1.1. yMax, yMin >=0
		* 1.2. yMin, yMax <=0
		* 1.3. yMax > 0 and yMin <0.
		* Case 1.1 and 1.2 are simple, as we just need to adjust the range between two positive
		* or two negative numbers such that the range can be equally divided into (numDivLines+1)
		* division.
		* Case 1.3 is tricky, as here, we additionally need to make sure that 0 plane is included
		* as a division.
		* We use two different methods to solved Case 1.1,1.2 and Case 1.3.
		* Note that in all Cases (1.1, 1.2 & 1.3), we adjust the auto-calculated yMax and yMin
		* to get best div line value. We do NOT adjust numDivLines here.
		*/
		//Check condition for case 1 first - limits not specified and adjustDiv is true
		if (this.config.yMinGiven == false && this.config.yMaxGiven == false && this.params.adjustDiv == true) {
			//Means neither chart max value nor min value has been specified and adjustDiv is true
			//Set flag that we do not have to format div (y-axis values) decimals
			this.config.formatDivDecimals = false;
			//Now, if it's case 1.3 (yMax > 0 & yMin <0)
			if (this.config.yMax>0 && this.config.yMin<0) {
				//Case 1.3
				/**
				* Here, in this case, we start by generating the best fit range
				* for the given yMin, yMax, numDivLines and interval. We generate
				* range by adding sequential increments (rangeDiv * (ND+1) * interval).
				* Interval has been adjusted to smaller interval for larger values.
				* Now, for each divisible range generated by the program, we adjust the
				* yMin and yMax to check if 0 can land as a division in between them on
				* a proper distance.
				* We divide the y-axis range into two parts - small arm and big arm.
				* Say y-axis range is from 1 to -5. So, small arm is 1 and big arm is -5.
				* Or, if its from 16 to -3, small arm is -3 and big arm is 16.
				* Now, we try and get a value for extended small arm, which is multiple
				* of rangeDiv. Say chart min,max is 16,-3. So range becomes 19.
				* Let's assume numDivLines to be 2. So for 2 numDivLines, we get closest
				* adjusted range value as 21. Delta range = 21 - 19 (original range) = 2
				* Also, range division value = 21 / (ND + 1) = 21 / 3 = 7
				* We now get values for extended small arm as i*range division, where i
				* runs from 1 to (numDivLines+1)/2. We go only halfway as it's the smaller
				* arm and so cannot extend to a division beyond half way - else it would have
				* been the bigger arm.
				* So, first extended small arm = -7 * 1 = -7.
				* We get the difference between extended small arm and original small arm.
				* In this case it's 7 - 3 = 4 (all absolute values now - to avoid sign disparities).
				* We see that delta arm > delta range. So, we ignore this range and get a new range.
				* So, next range comes as = prev Range (21) + (numDivLines + 1)*interval
				* = 21 + (2+1)*1 = 24
				* Since the increment is sequential as a multiplication factor of
				* (numDivLines + 1)*interval, it is also a valid divisible range.
				* So we again check whether 0 can appear as a division. In this case, we
				* get rangeDiv as 8 and extended smaller arm as 8. For this extended smaller
				* arm, we get bigger arm as 16. Both of these are divisible by rangeDiv. That
				* means, this range can include 0 as division line. So, we store it and proceed.
				*/
				//Flag to indicate whether we've found the perfect range or not.
				var found:Boolean = false;
				//We re-calculate the interval to get smaller increments for large values.
				//For example, for 300 to -100 (with ND as 2), if we do not adjust interval, the min
				//max come as -200, 400. But with adjusted intervals, it comes as -150, 300, which is
				//more appropriate.
				var adjInterval = (this.config.interval>10) ? (this.config.interval/10) : this.config.interval;
				//Get the first divisible range for the given yMin, yMax, numDivLines and interval.
				//We do not intercept and adjust interval for this calculation here.
				var range:Number = getDivisibleRange(this.config.yMin, this.config.yMax, this.params.numDivLines, adjInterval, false);
				//Now, deduct delta range from nextRange initially, so that in while loop,
				//there's a unified statement for increment instead of 2 checks.
				var nextRange:Number = range-(this.params.numDivLines+1)*(adjInterval);
				//Range division
				var rangeDiv:Number;
				//Delta in range
				var deltaRange:Number;
				//Multiplication factor
				var mf:Number;
				//Smaller and bigger arm of y-axis
				var smallArm:Number, bigArm:Number;
				//Exntended small and big arm
				var extSmallArm:Number, extBigArm:Number;
				//Loop variable
				var i:Number;
				//Now, we need to search for a range which is divisible in (this.params.numDivLines+1)
				//segments including 0. Run a while loop till that is found.
				while (found == false) {
					//Get range
					nextRange = nextRange+(this.params.numDivLines+1)*(adjInterval);
					//If it's divisible for the given range and adjusted interval, proceed
					if (isRangeDivisible(nextRange, this.params.numDivLines, adjInterval)) {
						//Delta Range
						deltaRange = nextRange-this.config.range;
						//Range division
						rangeDiv = nextRange/(this.params.numDivLines+1);
						//Get the smaller arm of axis
						smallArm = Math.min(Math.abs(this.config.yMin), this.config.yMax);
						//Bigger arm of axis.
						bigArm = this.config.range-smallArm;
						//Get the multiplication factor (if smaller arm is negative, set -1);
						mf = (smallArm == Math.abs(this.config.yMin)) ? -1 : 1;
						//If num div lines ==0, we do not calculate anything
						if (this.params.numDivLines == 0) {
							//Set flag to true to exit loop
							found = true;
						} else {
							//Now, we need to make sure that the smaller arm of axis is a multiple
							//of rangeDiv and the multiplied result is greater than smallArm.
							for (var i = 1; i<=Math.floor((this.params.numDivLines+1)/2); i++) {
								//Get extended small arm
								extSmallArm = rangeDiv*i;
								//If extension is more than original intended delta, we move to next
								//value of loop as this range is smaller than our intended range
								if ((extSmallArm-smallArm)>deltaRange) {
									//Iterate to next loop value
									continue;
								}
								//Else if extended arm is greater than smallArm, we do the 0 div test  
								if (extSmallArm>smallArm) {
									//Get extended big arm
									extBigArm = nextRange-extSmallArm;
									//Check whether for this range, 0 can come as a div
									//By checking whether both extBigArm and extSmallArm
									//are perfectly divisible by rangeDiv
									if (((extBigArm/rangeDiv) == (Math.floor(extBigArm/rangeDiv))) && ((extSmallArm/rangeDiv) == (Math.floor(extSmallArm/rangeDiv)))) {
										//Store in global containers
										this.config.range = nextRange;
										this.config.yMax = (mf == -1) ? extBigArm : extSmallArm;
										this.config.yMin = (mf == -1) ? (-extSmallArm) : (-extBigArm);
										//Set found flag to true to exit loop
										found = true;
									}
								} else {
									//Iterate to next loop value, as we need the arm to be greater
									//than original value.
									continue;
								}
							}
						}
					}
				}
			} else {
				//Case 1.1 or 1.2
				/**
				* In this case, we first get apt divisible range based on yMin, yMax,
				* numDivLines and the calculated interval. Thereby, get the difference
				* between original range and new range and store as delta.
				* If yMax>0, add this delta to yMax. Else substract from yMin.
				*/
				//Get the adjusted divisible range
				var adjRange:Number = getDivisibleRange(this.config.yMin, this.config.yMax, this.params.numDivLines, this.config.interval, true);
				//Get delta (Calculated range minus original range)
				var deltaRange:Number = adjRange-this.config.range;
				//Update global range storage
				this.config.range = adjRange;
				//Now, add the change in range to yMax, if yMax > 0, else deduct from yMin
				if (this.config.yMax>0) {
					this.config.yMax = this.config.yMax+deltaRange;
				} else {
					this.config.yMin = this.config.yMin-deltaRange;
				}
			}
		} else {
			/**
			* Here, we've to handle the following cases
			* 2. User specifies both yMin, yMax in XML. (which our program still validates)
			* 3. User specifies only yMin. (we provide yMax)
			* 4. User specifies only yMax. (we provide yMin)
			* Now, for each of these, there can be two cases. If the user has opted to
			* adjust div lines or not. If he has opted to adjustDiv, we calculate the best
			* possible number of div lines for the given range. If not, we simply divide
			* the given (or semi-calculated) axis limits by the number of div lines.
			*/
			if (this.params.adjustDiv == true) {
				//We iterate from given numDivLines to 0,
				//Count helps us keep a counter of how many div lines we've checked
				//For the sake of optimization, we check only 25 div lines values
				//From (numDivLines to 0) and (numDivLines to (25-numDivLines))
				//We do it in a yoyo order - i.e., if numDivLines is set as 5,
				//we first check 6 and then 4. Next would be (8,3), (9,2), (10,1),
				//(11, (no value here, as we do not check for 0), 12, 13, 14, 15, 16,
				//17,18,19,20. So, in this way, we check for 25 possible numDivLines and
				//see if any one them fit in. If yes, we store that value. Else, we set it
				//as 0 (indicating no div line feasible for the given value).
				//Perform only if this.params.numDivLines>0
				if (this.params.numDivLines>0) {
					var counter:Number = 0;
					var multiplyFactor:Number = 1;
					var numDivLines:Number;
					while (1 == 1) {
						//Increment,Decrement numDivLines
						numDivLines = this.params.numDivLines+(counter*multiplyFactor);
						//Cannot be 0
						numDivLines = (numDivLines == 0) ? 1 : numDivLines;
						//Check whether this number of numDivLines satisfy our requirement
						if (isRangeDivisible(this.config.range, numDivLines, this.config.interval)) {
							//Exit loop
							break;
						}
						//Each counter comes twice: one for + count, one for - count  
						counter = (multiplyFactor == -1 || (counter>this.params.numDivLines)) ? (++counter) : (counter);
						if (counter>25) {
							//We do not go beyond 25 count to optimize.
							//If the loop comes here, it means that divlines
							//counter is not able to achieve the target.
							//So, we assume no div lines are possible and exit.
							numDivLines = 0;
							break;
						}
						//Switch to increment/decrement mode. If counter  
						multiplyFactor = (counter<=this.params.numDivLines) ? (multiplyFactor*-1) : (1);
					}
					//Store the value in params
					this.params.numDivLines = numDivLines;
					//Set flag that we do not have to format div (y-axis values) decimals
					this.config.formatDivDecimals = false;
				}
			} else {
				//We need to set flag that div lines intevals need to formatted
				//to the given precision.
				//Set flag that we have to format div (y-axis values) decimals
				this.config.formatDivDecimals = true;
			}
		}
		//Set flags pertinent to zero plane
		if (this.config.yMax>0 && this.config.yMin<0) {
			this.config.zeroPRequired = true;
		} else {
			this.config.zeroPRequired = false;
		}
		//Div interval
		this.config.divInterval = (this.config.yMax-this.config.yMin)/(this.params.numDivLines+1);
		//Flag to keep a track whether zero plane is included
		this.config.zeroPIncluded = false;
		//We now need to store all the div line segments in the array this.divLines
		//We include yMin and yMax too in div lines to render in a single loop
		var divLineValue:Number = this.config.yMin-this.config.divInterval;
		//Keeping a count of div lines
		var count:Number = 0;
		while (count<=(this.params.numDivLines+1)) {
			//Converting to string and back to number to avoid Flash's rounding problems.
			divLineValue = Number(String(divLineValue+this.config.divInterval));
			//Check whether zero plane is included
			this.config.zeroPIncluded = (divLineValue == 0) ? true : this.config.zeroPIncluded;
			//Add the div line to this.divLines
			this.divLines[count] = this.returnDataAsDivLine(divLineValue, this.params.yFormatNumber, this.params.yFormatNumberScale, this.params.yDefaultNumberScale, this.config.formatDivDecimals, this.params.yAxisValueDecimals, this.params.forceYAxisValueDecimals, yNumberScales, this.params.yNumberPrefix, this.params.yNumberSuffix);
			//Based on yAxisValueStep, we need to hide required div line values
			if (count%this.params.yAxisValuesStep == 0) {
				this.divLines[count].showValue = true;
			} else {
				this.divLines[count].showValue = false;
			}
			//Increment counter
			count++;
		}
		//Now, the array this.divLines contains all the divisional values. But, it might
		//not contain 0 value in Case 2,3,4 (i.e., when the user manually sets things).
		//So, if zero plane is required but not included, we include it.
		if (this.config.zeroPRequired == true && this.config.zeroPIncluded == false) {
			//Include zero plane at the right place in the array.
			this.divLines.push(this.returnDataAsDivLine(0, this.params.yFormatNumber, this.params.yFormatNumberScale, this.params.yDefaultNumberScale, this.config.formatDivDecimals, this.params.yAxisValueDecimals, this.params.forceYAxisValueDecimals, yNumberScales, this.params.yNumberPrefix, this.params.yNumberSuffix));
			//Now, sort on value so that 0 automatically appears at right place
			this.divLines.sortOn("value", Array.NUMERIC);
		}
	}
	/**
	 * Calculate two regression poins for each dataset 
	 * to draw the regression line.
	 */
	private function calculateRegressionPoins():Void {
		
		//Variable declation.
		var i:Number, j:Number, N:Number;
		var dataset:Object, data:Object;
		
		var slopeB:Number, firstX:Number, firstY:Number, secondX:Number, secondY:Number;
		var fx:Number, fy:Number, sx:Number, sy:Number;
				
		//Iteration for each dataset
		for(i = 1; i <= this.numDS; i++){
			//Reference of current dataset.
			dataset = this.dataset[i];
			
			if(!dataset.showRegressionLine){
				continue;
			}
			
			N = dataset.num;
			
			//Create a container for regression points
			dataset.regressionPoints = [];
			
			var sumX:Number = 0, sumY:Number = 0, sumXY:Number = 0, sumXsqure:Number = 0, sumYsqure:Number = 0;
			var xMin:Number, xMax:Number, yMin:Number, yMax:Number;
			
			if(dataset.showYOnX){
				var xValues:Array = [];
			}else{
				var yValues:Array = [];
			}
			
			//Iteration for each set
			for(j = 1; j <= N; j++){
				
				//Reference of current set.
				data = dataset.data[j];
				//Calculating the summation of X.
				sumX += data.xv;
				//Calculating the summation of Y.
				sumY += data.yv;
				//Calculating the summation of XY.
				sumXY += data.xv * data.yv;
				
				if(dataset.showYOnX){
					//Calculating the summation of X²
					sumXsqure += Math.pow(data.xv, 2);
					//Push all the x values
					xValues.push(data.xv);
				} else {
					//Calculating the summation of Y²
					sumYsqure += Math.pow(data.yv, 2);
					//Push all the y values
					yValues.push(data.yv);
				}
			}
			
			if(dataset.showYOnX){
				//Short the array
				xValues.sort(Array.NUMERIC);
				//Store X minimum and maximum no
				xMin = xValues[0];
				xMax = xValues[xValues.length - 1];
				
				slopeB = (N*sumXY - sumX*sumY) / (N*sumXsqure - Math.pow(sumX, 2));
				firstY = (!isNaN(slopeB))? (slopeB * (xMin - sumX/N)) + sumY/N : sumY/N; 
				secondY = (!isNaN(slopeB))? (slopeB * (xMax - sumX/N)) + sumY/N : sumY/N;
				
				dataset.regressionPoints.push({yPos:firstY, xPos:xMin, yv:firstY, xv_xMin:xMin}, {yPos:secondY, xPos:xMax, yv:secondY, xv_xMax:xMax});
			} else {
				//Short the array
				yValues.sort(Array.NUMERIC);
				//Store Y minimum and maximum no
				yMin = yValues[0];
				yMax = yValues[yValues.length - 1];
				
				slopeB = (N*sumXY - sumX*sumY) / (N*sumYsqure - Math.pow(sumY, 2));
				firstX = (!isNaN(slopeB))? (slopeB * (yMin - sumY/N)) + sumX/N : sumX/N;
				secondX = (!isNaN(slopeB))? (slopeB * (yMax - sumY/N)) + sumX/N : sumX/N;
				
				dataset.regressionPoints.push({yPos:yMin, xPos:firstX, yv:yMin, xv_xMin:firstX}, {yPos:yMax, xPos:secondX, yv:yMax, xv_xMax:secondX});
			}
		}
	}

	//-----------------------------For vertical div line ---------------------------  
	/**
	* calcVDivs method calculates the best div line interval for the given/calculated
	* xMin, xMax, specified numVDivLines and adjustVDiv.
	* Re-set the x axis min and max value, if both were calculated, 
	* so that it gives a best value according to numVDivLines. The idea is to have equal
	* intervals on the axis, based on numVDivLines specified. Its do so, only if both xMin and
	* xMax have been calculated as per our values. Else, adjust the numDiv Lines.
	*/
	private function calcVDivs():Void {
		/**
		* There can be four cases of xMin, xMax.
		* 1. User doesn't specify either. (our program calculates it).
		* 2. User specifies both in XML. (which our program still validates)
		* 3. User specifies only xMin. (we provide missing data)
		* 4. User specifies only xMax. (we provide missing data)
		*
		* Apart from this, the user can specify numVDivLines (which if not specified takes a
		* default value of 4). Also, the user can specify adjustVDiv (which can be 1 or 0).
		* adjustVDiv works in all four cases (1,2,3,4).
		* Case 1 is modified to occur as under now: User doesn't specify either xMin or xMax
		* and adjustVDiv is set to true (by default). If the user doesn't specify either xMin or
		* xMax, but adjustVDiv is set to false, it doesn't appear as Case 1. However, if adjustVDiv
		* is set to true and xMin,xMax is automatically calculated by FusionCharts, we adjust the
		* calculated xMin,xMax so that the given number of div lines can be well adjusted within.
		*
		* In case 2,3,4, adjust numVDivLines so that they space up equally based on the interval
		* and decimals required.
		*
		* So, the difference between Case 1 and Case 2,3,4 is that in Case 1, adjust limits
		* to accomodate specified number of div lines. In Case 2,3,4, we adjust numVDivLines to
		* accomodate within the given limits (x-axis range).
		*
		* numVDivLines is always our primary focus when calculating them in all cases. In Case 1,
		* it's kept constant as center of calculation. In Case 2, it's modified to get a best
		* value.
		*
		* Now, for case 1, it can have three sub cases:
		* 1.1. xMax, xMin >=0
		* 1.2. xMin, xMax <=0
		* 1.3. xMax > 0 and xMin <0.
		* Case 1.1 and 1.2 are simple, just need to adjust the range between two positive
		* or two negative numbers such that the range can be equally divided into (numVDivLines+1)
		* division.
		* Case 1.3 is tricky, as here, additionally need to make sure that 0 plane is included
		* as a division.
		* use two different methods to solved Case 1.1,1.2 and Case 1.3.
		* Note that in all Cases (1.1, 1.2 & 1.3), adjust the auto-calculated xMax and xMin
		* to get best div line value. Do NOT adjust numVDivLines here.
		*/		
		//Check condition for case 1 first - limits not specified and adjustVDiv is true
		if (this.config.xMinGiven == false && this.config.xMaxGiven == false && this.params.adjustVDiv == true) {  
						
			//Means neither chart max value nor min value has been specified and adjustVDiv is true
			//Set flag that do not have to format div (y-axis values) decimals
			this.config.formatVDivDecimals = false;			
			
			//Now, if it's case 1.3 (xMax > 0 & xMin <0)
			if (this.config.xMax>0 && this.config.xMin<0) {  
				//Case 1.3
				/**
				* Here, in this case, start by generating the best fit range
				* for the given xMin, xMax, numVDivLines and interval. Generate
				* range by adding sequential increments (rangeDiv * (ND+1) * interval).
				* Interval has been adjusted to smaller interval for larger values.
				* Now, for each divisible range generated by the program, adjust the
				* xMin and xMax to check if 0 can land as a division in between them on
				* a proper distance.
				* Divide the x-axis range into two parts - small arm and big arm.
				* Say x-axis range is from 1 to -5. So, small arm is 1 and big arm is -5.
				* Or, if its from 16 to -3, small arm is -3 and big arm is 16.
				* Now, try and get a value for extended small arm, which is multiple
				* of rangeDiv. Say chart min,max is 16,-3. So range becomes 19.
				* Let's assume numVDivLines to be 2. So for 2 numVDivLines, get closest
				* adjusted range value as 21. Delta range = 21 - 19 (original range) = 2
				* Also, range division value = 21 / (ND + 1) = 21 / 3 = 7
				* Now get values for extended small arm as i*range division, where i
				* runs from 1 to (numVDivLines+1)/2. Go only halfway as it's the smaller
				* arm and so cannot extend to a division beyond half way - else it would have
				* been the bigger arm.
				* So, first extended small arm = -7 * 1 = -7.
				* Get the difference between extended small arm and original small arm.
				* In this case it's 7 - 3 = 4 (all absolute values now - to avoid sign disparities).
				* If delta arm > delta range. So, ignore this range and get a new range.
				* So, next range comes as = prev Range (21) + (numVDivLines + 1)*interval
				* = 21 + (2+1)*1 = 24
				* Since the increment is sequential as a multiplication factor of
				* (numVDivLines + 1)*interval, it is also a valid divisible range.
				* So check again whether 0 can appear as a division. In this case, it
				* get rangeDiv as 8 and extended smaller arm as 8. For this extended smaller
				* arm, it get bigger arm as 16. Both of these are divisible by rangeDiv. That
				* means, this range can include 0 as division line. So, we store it and proceed.
				*/
				//Flag to indicate whether found the perfect range or not.
				var found:Boolean = false;
				//Re-calculate the interval to get smaller increments for large values.
				//For example, for 300 to -100 (with ND as 2), if we do not adjust interval, the min
				//max come as -200, 400. But with adjusted intervals, it comes as -150, 300, which is
				//more appropriate.
				var adjInterval = (this.config.xInterval>10) ? (this.config.xInterval/10) : this.config.xInterval; 
				//Get the first divisible range for the given xMin, xMax, numVDivLines and interval.
				//Do not intercept and adjust interval for this calculation here.
				var range:Number = getDivisibleRange(this.config.xMin, this.config.xMax, this.params.numVDivLines, adjInterval, false);
				//Now, deduct delta range from nextRange initially, so that in while loop,
				//there's a unified statement for increment instead of 2 checks.
				var nextRange:Number = range-(this.params.numVDivLines+1)*(adjInterval);
				//Range division
				var rangeDiv:Number;
				//Delta in range
				var deltaRange:Number;
				//Multiplication factor
				var mf:Number;
				//Smaller and bigger arm of x-axis
				var smallArm:Number, bigArm:Number;
				//Exntended small and big arm
				var extSmallArm:Number, extBigArm:Number;
				//Loop variable
				var i:Number;
				
				
				//Now, need to search for a range which is divisible in (this.params.numVDivLines+1)
				//segments including 0. Run a while loop till that is found.
				while (found == false) {
					//Get range
					nextRange = nextRange+(this.params.numVDivLines+1)*(adjInterval);
					//If it's divisible for the given range and adjusted interval, proceed
					if (isRangeDivisible(nextRange, this.params.numVDivLines, adjInterval)) {
						//Delta Range
						deltaRange = nextRange-this.config.vRange;
						//Range division
						rangeDiv = nextRange/(this.params.numVDivLines+1);
						//Get the smaller arm of axis
						smallArm = Math.min(Math.abs(this.config.xMin), this.config.xMax);
						//Bigger arm of axis.
						bigArm = this.config.vRange-smallArm;
						//Get the multiplication factor (if smaller arm is negative, set -1);
						mf = (smallArm == Math.abs(this.config.xMin)) ? -1 : 1;
						//If num div lines ==0, we do not calculate anything
						if (this.params.numVDivLines == 0) {
							//Set flag to true to exit loop
							found = true;
						} else {
							//Now, need to make sure that the smaller arm of axis is a multiple
							//of rangeDiv and the multiplied result is greater than smallArm.
							for (var i = 1; i<=Math.floor((this.params.numVDivLines+1)/2); i++) {
								//Get extended small arm
								extSmallArm = rangeDiv*i;
								//If extension is more than original intended delta, move to next
								//value of loop as this range is smaller than intended range
								if ((extSmallArm-smallArm)>deltaRange) {
									//Iterate to next loop value
									continue;
								}
								//Else if extended arm is greater than smallArm, need do the 0 div test  
								if (extSmallArm>smallArm) {
									//Get extended big arm
									extBigArm = nextRange-extSmallArm;
									//Check whether for this range, 0 can come as a div
									//By checking whether both extBigArm and extSmallArm
									//are perfectly divisible by rangeDiv
									if (((extBigArm/rangeDiv) == (Math.floor(extBigArm/rangeDiv))) && ((extSmallArm/rangeDiv) == (Math.floor(extSmallArm/rangeDiv)))) {
										//Store in global containers
										this.config.vRange = nextRange;
										this.config.xMax = (mf == -1) ? extBigArm : extSmallArm;
										this.config.xMin = (mf == -1) ? (-extSmallArm) : (-extBigArm);
										//Set found flag to true to exit loop
										found = true;
									}
								} else {
									//Iterate to next loop value, as need the arm to be greater
									//than original value.
									continue;
								}
							}
						}
					}
				}
			} else {
				//Case 1.1 or 1.2
				/**
				* In this case, first get apt divisible range based on xMin, xMax,
				* numVDivLines and the calculated interval. Thereby, get the difference
				* between original range and new range and store as delta.
				* If xMax>0, add this delta to xMax. Else substract from xMin.
				*/
				//Get the adjusted divisible range
				var adjRange:Number = getDivisibleRange(this.config.xMin, this.config.xMax, this.params.numVDivLines, this.config.xInterval, true);
				
				//Get delta (Calculated range minus original range)
				var deltaRange:Number = adjRange-this.config.vRange;
				
				//Update global range storage
				this.config.vRange = adjRange;
				
				//Now, add the change in range to xMax, if xMax > 0, else deduct from xMin
				if (this.config.xMax>0) {
					this.config.xMax = this.config.xMax+deltaRange;
				} else {
					this.config.xMin = this.config.xMin-deltaRange;
				}
			}
		} else {
			/**
			* Here, need to handle the following cases
			* 2. User specifies both xMin, xMax in XML. (which our program still validates)
			* 3. User specifies only xMin. (we provide xMax)         
			* 4. User specifies only xMax. (we provide xMin)
			* Now, for each of these, there can be two cases. If the user has opted to
			* adjust div lines or not. If he has opted to adjustVDiv, need calculate the best
			* possible number of div lines for the given range. If not, simply divide
			* the given (or semi-calculated) axis limits by the number of div lines.
			*/
			if (this.params.adjustVDiv == true) {
				//iterate from given numVDivLines to 0,
				//Count helps to keep a counter of how many div lines need to checked
				//For the sake of optimization, need check only 25 div lines values
				//From (numVDivLines to 0) and (numVDivLines to (25-numVDivLines))
				//It doing in a yoyo order - i.e., if numVDivLines is set as 5,
				// first check 6 and then 4. Next would be (8,3), (9,2), (10,1),
				//(11, (no value here, so do not check for 0), 12, 13, 14, 15, 16,
				//17,18,19,20. So, in this way, check for 25 possible numVDivLines and
				//see if any one them fit in. If yes, it store that value. Else, set it
				//as 0 (indicating no div line feasible for the given value).
				//Perform only if this.params.numVDivLines>0
				if (this.params.numVDivLines>0) {
					var counter:Number = 0;
					var multiplyFactor:Number = 1;
					var numVDivLines:Number;
					while (true) {
						//Increment,Decrement numVDivLines
						numVDivLines = this.params.numVDivLines+(counter*multiplyFactor);
						//Cannot be 0
						numVDivLines = (numVDivLines == 0) ? 1 : numVDivLines;
						//Check whether this number of numVDivLines satisfy our requirement
						if (isRangeDivisible(this.config.vRange, numVDivLines, this.config.xInterval)) {
							//Exit loop
							break;
						}
						//Each counter comes twice: one for + count, one for - count  
						counter = (multiplyFactor == -1 || (counter>this.params.numVDivLines)) ? (++counter) : (counter);
						
						if (counter>25) {
							//do not go beyond 25 count to optimize.
							//If the loop comes here, it means that divlines
							//counter is not able to achieve the target.
							//So, assume no div lines are possible and exit.
							numVDivLines = 0;
							break;
						}
						
						//Switch to increment/decrement mode. If counter  
						multiplyFactor = (counter<=this.params.numVDivLines) ? (multiplyFactor*-1) : (1);
					}
					//Store the value in params
					this.params.numVDivLines = numVDivLines;
					//Set flag that do not have to format div (x-axis values) decimals
					this.config.formatVDivDecimals = false;
				}
				
			} else {
				
				//Set flag that have to format div (x-axis values) decimals
				this.config.formatVDivDecimals = true;
			}
		}
		//Set flags pertinent to zero plane
		if (this.config.xMax>0 && this.config.xMin<0) {
			this.config.zeroPRequired = true;
		} else {
			this.config.zeroPRequired = false;
		}
		//Div interval
		this.config.divInterval = (this.config.xMax-this.config.xMin)/(this.params.numVDivLines+1);
		//Flag to keep a track whether zero plane is included
		this.config.zeroPIncluded = false;
		//We now need to store all the div line segments in the array this.vDivLines
		//We include xMin and xMax too in div lines to render in a single loop
		var divLineValue:Number = this.config.xMin-this.config.divInterval;		
		//Keeping a count of div lines
		var count:Number = 0;
		while (count<=(this.params.numVDivLines+1)) {
			//Converting to string and back to number to avoid Flash's rounding problems.
			divLineValue = Number(String(divLineValue+this.config.divInterval));
			//Check whether zero plane is included

			this.config.zeroPIncluded = (divLineValue == 0) ? true : this.config.zeroPIncluded;
			//Add the div line to this.vDivLines
			this.vDivLines[count] = this.returnDataAsDivLine(divLineValue, this.params.xFormatNumber, this.params.xFormatNumberScale, this.params.xDefaultNumberScale, this.config.formatVDivDecimals, this.params.xAxisValueDecimals, this.params.forceXAxisValueDecimals, xNumberScales, this.params.xNumberPrefix, this.params.xNumberSuffix)
			//Based on yAxisValueStep, we need to hide required div line values
			if (count%this.params.xAxisValuesStep == 0) {
				this.vDivLines[count].showValue = true;
			} else {
				this.vDivLines[count].showValue = false;
			}
			//Increment counter
			count++;
		}
		//Now, the array this.vDivLines contains all the divisional values. But, it might
		//not contain 0 value in Case 2,3,4 (i.e., when the user manually sets things).
		//So, if zero plane is required but not included, we include it.
		if (this.config.zeroPRequired == true && this.config.zeroPIncluded == false) {
			//Include zero plane at the right place in the array.
			this.vDivLines.push(this.returnDataAsDivLine(0, this.params.xFormatNumber, this.params.xFormatNumberScale, this.params.xDefaultNumberScale, this.config.formatVDivDecimals, this.params.xAxisValueDecimals, this.params.forceXAxisValueDecimals, xNumberScales, this.params.xNumberPrefix, this.params.xNumberSuffix));
			//Now, sort on value so that 0 automatically appears at right place
			this.vDivLines.sortOn("value", Array.NUMERIC);
		}		
		
	}
	
	
	
	/**
	* isRangeDivisible method helps us judge whether the given range is
	* perfectly divisible for specified y-interval, numDivLines, yMin and yMax.
	* To check that, we divide the given range into (numDivLines+1) section.
	* If the decimal places of this division value is <= that of interval,
	* that means, this range fits in our purpose. We return a boolean value
	* accordingly.
	*	@param	range		Range of y-axis (Max - Min). Absolute value
	*	@param	numDivLines	Number of div lines to be plotted.
	*	@param	interval	Y-axis Interval (power of ten).
	*	@return			Boolean value indicating whether this range is divisible
	*						by the given number of div lines.
	*/
	private function isRangeDivisible(range:Number, numDivLines:Number, interval:Number):Boolean {
		//Get range division
		var rangeDiv:Number = range/(numDivLines+1);
		//Now, if the decimal places of rangeDiv and interval do not match,
		//it's not divisible, else it's divisible
		if (MathExt.numDecimals(rangeDiv)>MathExt.numDecimals(interval)) {
			return false;
		} else {
			return true;
		}
	}
	/**
	* getDivisibleRange method calculates a perfectly divisible range based
	* on y-interval, numDivLines, yMin and yMax specified.
	* We first get the range division for the existing range
	* and user specified number of div lines. Now, if that division satisfies
	* our needs (decimal places of division and interval is equal), we do NOT
	* change anything. Else, we round up the division to the next higher value {big delta
	* in case of smaller values i.e., interval <1 and small delta in case of bigger values >1).
	* We multiply this range division by number of div lines required and calculate
	* the new range.
	*	@param	yMin			Min value of y-axis
	*	@param	yMax			Max value of y-axis
	*	@param	numDivLines		Number of div lines to be plotted.
	*	@param	interval		Y-axis Interval (power of ten).
	*	@param	interceptRange	Boolean value indicating whether we've to change the range
	*							by altering interval (based on it's own value).
	*	@return				A range that is perfectly divisible into given number of sections.
	*/
	private function getDivisibleRange(yMin:Number, yMax:Number, numDivLines:Number, interval:Number, interceptRange:Boolean):Number {
		//Get the range division for current yMin, yMax and numDivLines
		var range = Math.abs(yMax-yMin);
		var rangeDiv:Number = range/(numDivLines+1);
		//Now, the range is not divisible
		if (!isRangeDivisible(range, numDivLines, interval)) {
			//We need to get new rangeDiv which can be equally distributed.
			//If intercept range is set to true
			if (interceptRange) {
				//Re-adjust interval so that gap is not much (conditional)
				//Condition check limit based on value
				var checkLimit:Number = (interval>1) ? 2 : 0.5;
				if ((Number(rangeDiv)/Number(interval))<checkLimit) {
					//Decrease power of ten to get closer rounding
					interval = interval/10;
				}
			}
			//Adjust range division based on new interval  
			rangeDiv = (Math.floor(rangeDiv/interval)+1)*interval;
			//Get new range
			range = rangeDiv*(numDivLines+1);
		}
		//Return range  
		return range;
	}
	/**
	 * Sets yAxisValuesStep such that no y-axis values overlap.
	 *	@param	maxItemDimension	Maximum Width/height required by an individual label
	 *	@param	availableDimension	Total available dimension to place all labels
	*/
	private function adjustYAxisLabelStep(maxItemDimension:Number, availableDimension:Number):Void {
		//Store the yAxisValuesStep defined
		var defaultYAxisStep:Number = this.params.yAxisValuesStep;
		//Number of divlines
		var numDiv:Number = this.divLines.length;
		//Check if all the labels can fit in
		if (numDiv>0 && ((availableDimension/numDiv)<maxItemDimension)) {
			//Calculate how many labels can fit in well
			var numFitLabels = (availableDimension/maxItemDimension);
			//Calculate the auto step			
			var autoStep:Number = Math.ceil(numDiv/numFitLabels);
			//Update the property only if the auto calculated value is Higher.
			if(autoStep > defaultYAxisStep){
				this.params.yAxisValuesStep = autoStep;
				//log the change.
				this.log ("'yAxisValuesStep' value updated", "The value to provide step for Y Axis Values is resulting in overlapping Y Axis values. Hence, automatically calculating yAxisValuesStep and changed it to " + autoStep +".", Logger.LEVEL.INFO);
			}
			//Re-set the individual display property
			//Keeping a count of div lines
			var count:Number = 0;
			while (count<=numDiv) {
				//Based on yAxisValueStep, we need to hide required div line values
				if (count%this.params.yAxisValuesStep == 0) {
					this.divLines[count].showValue = true;
				} else {
					this.divLines[count].showValue = false;
				}
				//Increment counter
				count++;
			}
		}
	}
	
	/**
	 * Sets xAxisValuesStep such that no x-axis values overlap.
	 *	@param	maxItemDimension	Maximum Width/height required by an individual label
	 *	@param	availableDimension	Total available dimension to place all labels
	*/
	private function adjustXAxisLabelStep(maxItemDimension:Number, availableDimension:Number):Void 
	{
			//Store the xAxisValuesStep defined
			var defaultXAxisStep:Number = this.params.xAxisValuesStep;
			//Number of divlines
			var numDiv:Number = this.vDivLines.length;
			//Check if all the labels can fit in
			if (numDiv>0 && ((availableDimension/numDiv)<maxItemDimension)) {
				//Calculate how many labels can fit in well
				var numFitLabels = (availableDimension/maxItemDimension);
				//Calculate the auto step			
				var autoStep:Number = Math.ceil(numDiv/numFitLabels);
				//Update the property only if the auto calculated value is Higher.
				if(autoStep > defaultXAxisStep){
					this.params.xAxisValuesStep = autoStep;
					//log the change.
					this.log ("'xAxisValuesStep' value updated", "The value to provide step for X Axis Values is resulting in overlapping X Axis values. Hence, automatically calculating xAxisValuesStep and changed it to " + autoStep +".", Logger.LEVEL.INFO);
				}
				//Re-set the individual display property
				//Keeping a count of div lines
				var count:Number = 0;
				while (count<=numDiv) {
					//Based on xAxisValueStep, we need to hide required div line values
					if (count%this.params.xAxisValuesStep == 0) {
						this.vDivLines[count].showValue = true;
					} else {
						this.vDivLines[count].showValue = false;
					}
					//Increment counter
					count++;
				}
			}
		}
	
	
	/**
	* returnDataAsDivLine method returns the data provided to the method
	* as a div line object.
	*	@param	value	Value of div line
	*	@return		An object with the parameters of div line
	*/
	private function returnDataAsDivLine(value:Number, formatNumber:Boolean, formatNumberScale:Boolean, defaultNumberScale:String, formatDecimal:Boolean, axisValueDecimal:Number, forceaxisValueDecimals:Boolean, nsObj:Object, numberPrefix:String, numberSuffix:String):Object {
		//Create a new object
		var divLineObject = new Object();
		divLineObject.value = value;		
		if (formatDecimal) {
			//Round off the div line value to axisValueDecimal precision
			divLineObject.displayValue = this.formatNumber(value, formatNumber, axisValueDecimal, forceaxisValueDecimals, formatNumberScale, defaultNumberScale, nsObj.configNumberScaleDefined, nsObj.nsv, nsObj.nsu, numberPrefix, numberSuffix);
		} else {
			//Set decimal places as 10, so that none of the decimals get stripped off
			divLineObject.displayValue = this.formatNumber(value, formatNumber, 10, forceaxisValueDecimals, formatNumberScale, defaultNumberScale, nsObj.configNumberScaleDefined, nsObj.nsv, nsObj.nsu, numberPrefix, numberSuffix);
		}
		//Flag to show values
		divLineObject.showValue = true;
		return divLineObject;
	}
	/**
	* getAxisPosition method gets the pixel position of a particular
	* point on the axis based on its value.
	*	@param	value			Numerical value for which we need pixel axis position
	*	@param	upperLimit		Numerical upper limit for that axis
	*	@param	lowerLimit		Numerical lower limit for that axis
	*	@param	startAxisPos	Pixel start position for that axis
	*	@param	endAxisPos		Pixel end position for that axis
	*	@param	isYAxis			Flag indicating whether it's y axis
	*	@param	xPadding		Padding at left and right sides in case of a x-axis
	*	@return				The pixel position of the value on the given axis.
	*/
	private function getAxisPosition(value:Number, upperLimit:Number, lowerLimit:Number, startAxisPos:Number, endAxisPos:Number, isYAxis:Boolean, xPadding:Number):Number {
		//Define variables to be used locally
		var numericalInterval:Number;
		var axisLength:Number;
		var relativePosition:Number;
		var absolutePosition:Number;
		//Get the numerical difference between the limits
		numericalInterval = (upperLimit-lowerLimit);
		if (isYAxis) {
			//If it's y axis, the co-ordinates are opposite in Flash
			axisLength = (endAxisPos-startAxisPos);
			relativePosition = (axisLength/numericalInterval)*(value-lowerLimit);
			//If it's a y axis co-ordinate then go according to Flash's co-ordinate system
			//(y decreases as we go upwards)
			absolutePosition = endAxisPos-relativePosition;
		} else {
			axisLength = (endAxisPos-startAxisPos)-(2*xPadding);
			relativePosition = (axisLength/numericalInterval)*(value-lowerLimit);
			//The normal x-axis rule - increases as we go right
			absolutePosition = startAxisPos+xPadding+relativePosition;
		}
		return absolutePosition;
	}
	/**
	* returnDataAsTrendObj method takes in functional parameters, and creates
	* an object to represent the trend line as a unified object.
	*	@param	startValue		Starting value of the trend line.
	*	@param	endValue		End value of the trend line (if different from start)
	*	@param	displayValue	Display value for the trend (if custom).
	* 	@param	toolText		Tool-text to be displayed for the trendline			
	*	@param	color			Color of the trend line
	*	@param	thickness		Thickness (in pixels) of line
	*	@param	alpha			Alpha of the line
	*	@param	isTrendZone		Flag to control whether to show trend as a line or block(zone)
	*	@param	showOnTop		Whether to show trend over data plot or under it.
	*	@param	isDashed		Whether the line would appear dashed.
	*	@param	dashLen			Length of dash (if isDashed selected)
	*	@param	dashGap			Gap of dash (if isDashed selected)
	*	@param	valueOnRight	Whether to put the trend value on right side of canvas
	*	@return				An object encapsulating these values.
	*/
	private function returnDataAsTrendObj(startValue:Number, endValue:Number, displayValue:String, toolText:String, color:String, thickness:Number, alpha:Number, isTrendZone:Boolean, showOnTop:Boolean, isDashed:Boolean, dashLen:Number, dashGap:Number, valueOnRight:Boolean):Object {
		//Create an object that will be returned.
		var rtnObj:Object = new Array();
		//Store parameters as object properties
		rtnObj.startValue = startValue;
		rtnObj.endValue = endValue;
		rtnObj.displayValue = displayValue;
		rtnObj.toolText = toolText;
		rtnObj.color = color;
		rtnObj.thickness = thickness;
		rtnObj.alpha = alpha;
		rtnObj.isTrendZone = isTrendZone;
		rtnObj.showOnTop = showOnTop;
		rtnObj.isDashed = isDashed;
		rtnObj.dashLen = dashLen;
		rtnObj.dashGap = dashGap;
		rtnObj.valueOnRight = valueOnRight;
		//Flag whether trend line is proper
		rtnObj.isValid = true;
		//Return
		return rtnObj;
	}
	/**
	* parseHTrendLineXML method parses the XML node containing horizontal trend line nodes
	* and then stores it in local objects.
	*	@param		arrTrendLineNodes		Array containing Trend LINE nodes.
	*	@return							Nothing.
	*/
	private function parseHTrendLineXML(arrTrendLineNodes:Array):Void {
		//Define variables for local use
		var startValue:Number, endValue:Number, displayValue:String;
		var toolText:String;
		var color:String, thickness:Number, alpha:Number;
		var isTrendZone:Boolean, showOnTop:Boolean, isDashed:Boolean;
		var dashLen:Number, dashGap:Number, valueOnRight:Boolean;
		//Loop variable
		var i:Number;
		//Iterate through all nodes in array
		for (i=0; i<=arrTrendLineNodes.length; i++) {
			//Check if LINE node
			if (arrTrendLineNodes[i].nodeName.toUpperCase() == "LINE") {
				//Update count
				numTrendLines++;
				//Store the node reference
				var lineNode:XMLNode = arrTrendLineNodes[i];
				//Get attributes array
				var lineAttr:Array = this.getAttributesArray(lineNode);
				//Extract and store attributes
				startValue = getFN(this.getSetValue(lineAttr["startvalue"]), this.getSetValue(lineAttr["value"]));
				endValue = getFN(this.getSetValue(lineAttr["endvalue"]), startValue);
				displayValue = lineAttr["displayvalue"];
				toolText = getFV(lineAttr["tooltext"], "");
				color = String(formatColor(getFV(lineAttr["color"], "333333")));
				thickness = getFN(lineAttr["thickness"], 1);
				isTrendZone = toBoolean(Number(getFV(lineAttr["istrendzone"], 0)));
				alpha = getFN(lineAttr["alpha"], (isTrendZone == true) ? 40 : 99);
				showOnTop = toBoolean(getFN(lineAttr["showontop"], 0));
				isDashed = toBoolean(getFN(lineAttr["dashed"], 0));
				dashLen = getFN(lineAttr["dashlen"], 5);
				dashGap = getFN(lineAttr["dashgap"], 2);
				valueOnRight = toBoolean(getFN(lineAttr["valueonright"], 0));
				//Create trend line object
				this.trendLines[numTrendLines] = returnDataAsTrendObj(startValue, endValue, displayValue, toolText, color, thickness, alpha, isTrendZone, showOnTop, isDashed, dashLen, dashGap, valueOnRight);
				//Update numTrendLinesBelow
				numTrendLinesBelow = (showOnTop == false) ? (++numTrendLinesBelow) : numTrendLinesBelow;
			}
		}
	}
	/**
	* parseVTrendLineXML method parses the XML node containing vertical trend line nodes
	* and then stores it in local objects.
	*	@param		arrTrendLineNodes		Array containing Trend LINE nodes.
	*	@return							Nothing.
	*/
	private function parseVTrendLineXML(arrTrendLineNodes:Array):Void {
		//Define variables for local use
		var startValue:Number, endValue:Number, displayValue:String;
		var toolText:String;
		var color:String, thickness:Number, alpha:Number;
		var isTrendZone:Boolean, showOnTop:Boolean, isDashed:Boolean;
		var dashLen:Number, dashGap:Number, valueOnRight:Boolean;
		//Loop variable
		var i:Number;
		//Iterate through all nodes in array
		for (i=0; i<=arrTrendLineNodes.length; i++) {
			//Check if LINE node
			if (arrTrendLineNodes[i].nodeName.toUpperCase() == "LINE") {
				//Update count
				numVTrendLines++;
				//Store the node reference
				var lineNode:XMLNode = arrTrendLineNodes[i];
				//Get attributes array
				var lineAttr:Array = this.getAttributesArray(lineNode);
				//Extract and store attributes
				startValue = getFN(this.getSetValue(lineAttr["startvalue"]), this.getSetValue(lineAttr["value"]));
				endValue = getFN(this.getSetValue(lineAttr["endvalue"]), startValue);
				displayValue = lineAttr["displayvalue"];
				toolText = getFV(lineAttr["tooltext"], "");
				color = String(formatColor(getFV(lineAttr["color"], "333333")));
				thickness = getFN(lineAttr["thickness"], 1);
				isTrendZone = toBoolean(Number(getFV(lineAttr["istrendzone"], 1)));
				alpha = getFN(lineAttr["alpha"], (isTrendZone == true) ? 40 : 99);
				showOnTop = false;
				isDashed = toBoolean(getFN(lineAttr["dashed"], 0));
				dashLen = getFN(lineAttr["dashlen"], 5);
				dashGap = getFN(lineAttr["dashgap"], 2);
				valueOnRight = false;
				//Create trend line object
				this.vTrendLines[numVTrendLines] = returnDataAsTrendObj(startValue, endValue, displayValue, toolText, color, thickness, alpha, isTrendZone, showOnTop, isDashed, dashLen, dashGap, valueOnRight);
			}
		}
	}
	/**
	* validateTrendLines method helps us validate the different trend line
	* points entered by user in XML. Some trend points may fall out of
	* chart range (yMin,yMax - xMin,xMax) and we need to invalidate them. Also, we
	* need to check if displayValue has been specified. Else, we specify
	* formatted number as displayValue.
	*	@return		Nothing
	*/
	private function validateTrendLines() {
		//Sequentially do the following.
		//- Check if start value and end value are numbers. If not,
		//  invalidate them
		//- Check range of each trend line against chart yMin,yMax and
		//  devalidate wrong ones.
		//- Resolve displayValue conflict.
		//- Calculate and store y position of start and end position.
		//Loop variable
		var i:Number;
		for (i=0; i<=this.numTrendLines; i++) {
			//If the trend line start/end value is NaN or out of range
			if (isNaN(this.trendLines[i].startValue) || (this.trendLines[i].startValue<this.config.yMin) || (this.trendLines[i].startValue>this.config.yMax) || isNaN(this.trendLines[i].endValue) || (this.trendLines[i].endValue<this.config.yMin) || (this.trendLines[i].endValue>this.config.yMax)) {
				//Invalidate it
				this.trendLines[i].isValid = false;
			} else {
				//We resolve displayValue conflict
				this.trendLines[i].displayValue = getFV(this.trendLines[i].displayValue, this.formatNumber(this.trendLines[i].startValue, this.params.yFormatNumber, this.params.yAxisValueDecimals, false, this.params.yFormatNumberScale, this.params.yDefaultNumberScale, yNumberScales.configNumberScaleDefined, yNumberScales.nsv, yNumberScales.nsu, this.params.yNumberPrefix, this.params.yNumberSuffix));
			}
		}
		//Do the same for vertical trend lines too
		for (i=0; i<=this.numVTrendLines; i++) {
			//If the trend line start/end value is NaN or out of range
			if (isNaN(this.vTrendLines[i].startValue) || (this.vTrendLines[i].startValue<this.config.xMin) || (this.vTrendLines[i].startValue>this.config.xMax) || isNaN(this.vTrendLines[i].endValue) || (this.vTrendLines[i].endValue<this.config.xMin) || (this.vTrendLines[i].endValue>this.config.xMax)) {
				//Invalidate it
				this.vTrendLines[i].isValid = false;
			} else {
				//We resolve displayValue conflict
				this.vTrendLines[i].displayValue = getFV(this.vTrendLines[i].displayValue, "");	
				//this.vTrendLines[i].isValid = this.vTrendLines[i].displayValue == "" ? false : true;
			}
		}
	}
	/**
	* calcCanvasCoords method calculates the co-ordinates of the canvas
	* taking into consideration everything that is to be drawn on the chart.
	* It finally stores the canvas as an object.
	*/
	private function calcCanvasCoords(isRedraw:Boolean) {
		//Loop variable
		var i:Number;
		//We now need to calculate the available Width on the canvas.
		//Available width = total Chart width minus the list below
		// - Left and Right Margin
		// - yAxisName (if to be shown)
		// - yAxisValues
		// - Trend line display values (both left side and right side).
		// - Legend (If to be shown at right)
		var canvasWidth:Number = this.width-(this.params.chartLeftMargin+this.params.chartRightMargin);
		//Set canvas startX
		var canvasStartX:Number = this.params.chartLeftMargin;
		//Now, if y-axis name is to be shown, simulate it and get the width
		if (this.params.yAxisName != "") {
			//Get style object
			var yAxisNameStyle:Object = this.styleM.getTextStyle(this.objects.YAXISNAME);
			if (this.params.rotateYAxisName) {
				//Create text field to get width
				var yAxisNameObj:Object = createText(true, this.params.yAxisName, this.tfTestMC, 1, testTFX, testTFY, 90, yAxisNameStyle, true, this.height-(this.params.chartTopMargin+this.params.chartBottomMargin), canvasWidth/2);
				//Accomodate width and padding
				canvasStartX = canvasStartX+yAxisNameObj.width+this.params.yAxisNamePadding;
				canvasWidth = canvasWidth-yAxisNameObj.width-this.params.yAxisNamePadding;
				//Create element for yAxisName - to store width/height
				this.elements.yAxisName = returnDataAsElement(0, 0, yAxisNameObj.width, yAxisNameObj.height);
				this.params.yAxisNameWidth = yAxisNameObj.width;
			} else {
				//If the y-axis name is not to be rotated
				//Calculate the width of the text if in full horizontal mode
				//Create text field to get width
				var yAxisNameObj:Object = createText(true, this.params.yAxisName, this.tfTestMC, 1, testTFX, testTFY, 0, yAxisNameStyle, false, 0, 0);
				//Get a value for this.params.yAxisNameWidth
				this.params.yAxisNameWidth = Number(getFV(this.params.yAxisNameWidth, yAxisNameObj.width));
				//Get the lesser of the width (to avoid un-necessary space)
				this.params.yAxisNameWidth = Math.min(this.params.yAxisNameWidth, yAxisNameObj.width);
				//Accomodate width and padding
				canvasStartX = canvasStartX+this.params.yAxisNameWidth+this.params.yAxisNamePadding;
				canvasWidth = canvasWidth-this.params.yAxisNameWidth-this.params.yAxisNamePadding;
				//Create element for yAxisName - to store width/height
				this.elements.yAxisName = returnDataAsElement(0, 0, this.params.yAxisNameWidth, yAxisNameObj.height);
			}
			delete yAxisNameStyle;
			delete yAxisNameObj;
		}
		//Accomodate width for y-axis values. Now, y-axis values consist of two parts  
		//(for backward compatibility) - limits (upper and lower) and div line values.
		//So, we'll have to individually run through both of them.
		var yAxisValMaxWidth:Number = 0;
		//Also store the height required to render the text field
		var yAxisValMaxHeight:Number = 0;
		var divLineObj:Object;
		var divStyle:Object = this.styleM.getTextStyle(this.objects.YAXISVALUES);
		//Iterate through all the div line values
		for (i=1; i<this.divLines.length; i++) {
			//If div line value is to be shown
			if (this.divLines[i].showValue) {
				//If it's the first or last div Line (limits), and it's to be shown
				if ((i == 1) || (i == this.divLines.length-1)) {
					if (this.params.showLimits) {
						//Get the width of the text
						divLineObj = createText(true, this.divLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, divStyle, false, 0, 0);
						//Accomodate
						yAxisValMaxWidth = (divLineObj.width>yAxisValMaxWidth) ? (divLineObj.width) : (yAxisValMaxWidth);
						yAxisValMaxHeight = (divLineObj.height>yAxisValMaxHeight) ? (divLineObj.height) : (yAxisValMaxHeight);
					}
				} else {
					//It's a div interval - div line
					//So, check if we've to show div line values
					if (this.params.showDivLineValues) {
						//Get the width of the text
						divLineObj = createText(true, this.divLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, divStyle, false, 0, 0);
						//Accomodate
						yAxisValMaxWidth = (divLineObj.width>yAxisValMaxWidth) ? (divLineObj.width) : (yAxisValMaxWidth);
						yAxisValMaxHeight = (divLineObj.height>yAxisValMaxHeight) ? (divLineObj.height) : (yAxisValMaxHeight);
					}
				}
			}
		}
		delete divLineObj;
		//Also iterate through all trend lines whose values are to be shown on
		//left side of the canvas.
		//Get style object
		var trendStyle:Object = this.styleM.getTextStyle(this.objects.TRENDVALUES);
		var trendObj:Object;
		for (i=1; i<=this.numTrendLines; i++) {
			if (this.trendLines[i].isValid == true && this.trendLines[i].valueOnRight == false) {
				//If it's a valid trend line and value is to be shown on left
				//Get the width of the text
				trendObj = createText(true, this.trendLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				yAxisValMaxWidth = (trendObj.width>yAxisValMaxWidth) ? (trendObj.width) : (yAxisValMaxWidth);
			}
		}
		//Accomodate for y-axis/left-trend line values text width
		if (yAxisValMaxWidth>0) {
			canvasStartX = canvasStartX+yAxisValMaxWidth+this.params.yAxisValuesPadding;
			canvasWidth = canvasWidth-yAxisValMaxWidth-this.params.yAxisValuesPadding;
		}
		var trendRightWidth:Number = 0;
		//Now, also check for trend line values that fall on right
		for (i=1; i<=this.numTrendLines; i++) {
			if (this.trendLines[i].isValid == true && this.trendLines[i].valueOnRight == true) {
				//If it's a valid trend line and value is to be shown on right
				//Get the width of the text
				trendObj = createText(true, this.trendLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				trendRightWidth = (trendObj.width>trendRightWidth) ? (trendObj.width) : (trendRightWidth);
			}
		}
		delete trendObj;
		//Accomodate trend right text width
		if (trendRightWidth>0) {
			canvasWidth = canvasWidth-trendRightWidth-this.params.yAxisValuesPadding;
		}
		//Round them off finally to avoid distorted pixels  
		canvasStartX = int(canvasStartX);
		canvasWidth = int(canvasWidth);
		//We finally have canvas Width and canvas Start X
		//-----------------------------------------------------------------------------------//
		//Now, we need to calculate the available Height on the canvas.
		//Available height = total Chart height minus the list below
		// - Chart Top and Bottom Margins
		// - Space for Caption, Sub Caption and caption padding
		// - Height of data labels
		// - xAxisName
		// - Vertical trend line values
		// - Legend (If to be shown at bottom position)
		//Initialize canvasHeight to total height minus margins
		var canvasHeight:Number = this.height-(this.params.chartTopMargin+this.params.chartBottomMargin);
		//Set canvasStartY
		var canvasStartY:Number = this.params.chartTopMargin;
		//Now, if we've to show caption
		if (this.params.caption != "") {
			//Create text field to get height
			var captionObj:Object = createText(true, this.params.caption, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.CAPTION), true, canvasWidth, canvasHeight/4);
			//Store the height
			canvasStartY = canvasStartY+captionObj.height;
			canvasHeight = canvasHeight-captionObj.height;
			//Create element for caption - to store width & height
			this.elements.caption = returnDataAsElement(0, 0, captionObj.width, captionObj.height);
			delete captionObj;
		}
		//Now, if we've to show sub-caption  
		if (this.params.subCaption != "") {
			//Create text field to get height
			var subCaptionObj:Object = createText(true, this.params.subCaption, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.SUBCAPTION), true, canvasWidth, canvasHeight/4);
			//Store the height
			canvasStartY = canvasStartY+subCaptionObj.height;
			canvasHeight = canvasHeight-subCaptionObj.height;
			//Create element for sub caption - to store height
			this.elements.subCaption = returnDataAsElement(0, 0, subCaptionObj.width, subCaptionObj.height);
			delete subCaptionObj;
		}
		//Now, if either caption or sub-caption was shown, we also need to adjust caption padding  
		if (this.params.caption != "" || this.params.subCaption != "") {
			//Account for padding
			canvasStartY = canvasStartY+this.params.captionPadding;
			canvasHeight = canvasHeight-this.params.captionPadding;
		}
		//-------- Begin: Data Label calculation --------------//  
		var labelObj:Object;
		var labelStyleObj:Object = this.styleM.getTextStyle(this.objects.DATALABELS);
		//Based on label step, set showLabel of each data point as required.
		//Visible label count
		var visibleCount:Number = 0;
		for (i=1; i<=this.numCat; i++) {
			//Now, the label can be preset to be hidden (set via XML)
			if (this.categories[i].isValid && this.categories[i].showLabel) {
				visibleCount++;
			}
		}
		//First if the label display is set as Auto, figure the best way 
		//to render them.
		if (this.params.labelDisplay == "AUTO") {
			//Here, we check how many labels are to be shown.
			//Based on that, we decide a fit between WRAP and ROTATE
			//Priority is given to WRAP mode over ROTATE mode
			//WRAP mode is set when at least 5 characters can be shown
			//on the chart, without having to break up into multiple lines.
			//Else, rotate mode is selected.
			//If in rotate mode and a single line cannot be fit, we also 
			//set labelStep, based on available space.
			//Get the width required to plot the characters in wrap mode
			//Storage for string to be simulated.
			var str:String = "";
			//Minimum width required to plot this string in both wrap
			//and rotate mode.
			var wrapMinWidth:Number = 0, rotateMinWidth:Number = 0;
			//Build the string by adding upper-case letters A,B,C,D...
			for (i=1; i<=WRAP_MODE_MIN_CHARACTERS; i++) {
				//Build the string from upper case A,B,C...
				str = str+String.fromCharCode(64+i);
			}
			//Simulate width of this text field - without wrapping
			labelObj = createText(true, str, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
			wrapMinWidth = labelObj.width;
			//If we've space to accommodate this width for all labels, render in wrap mode
			if (visibleCount*wrapMinWidth<=canvasWidth) {
				//Render all labels in wrap mode
				this.params.labelDisplay = "WRAP";
			} else {
				//Since we do not have space to accommodate the width of wrap mode:
				//Render in rotate mode
				this.params.labelDisplay = "ROTATE";
				//Figure the minimum width required to display 1 line of text in rotated mode				
				labelObj = createText(true, str, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, false, 0, 0);
				//Store the mimimum width required for rotated mode
				rotateMinWidth = labelObj.width;
				//If all the labels cannot be accommodated in minimum width (without getting trimmed),
				//then go for labelStep
				if (visibleCount*rotateMinWidth>=canvasWidth) {
					//Figure out how many labels we can display
					var numFitLabels:Number = (canvasWidth/rotateMinWidth);
					//Based on how many labels we've to display and how many we can, reset labelStep
					this.params.labelStep = Math.ceil(visibleCount/numFitLabels);
				}
			}
		}
		//Now, based on user set/auto-calculated label step, set showLabel   
		//of each data point as required.
		//Reset Visible label count
		visibleCount = 0;
		var finalVisibleCount:Number = 0;
		for (i=1; i<=this.numCat; i++) {
			//Now, the label can be preset to be hidden (set via XML)
			if (this.categories[i].isValid && this.categories[i].showLabel) {
				visibleCount++;
				//If label step is defined, we need to set showLabel of those
				//labels which fall on step as false.
				if ((i-1)%this.params.labelStep == 0) {
					this.categories[i].showLabel = true;
				} else {
					this.categories[i].showLabel = false;
				}
			}
			//Update counter  
			finalVisibleCount = (this.categories[i].showLabel) ? (finalVisibleCount+1) : (finalVisibleCount);
		}
		//Store the final visible count
		this.config.finalVisibleCount = finalVisibleCount;
		//Data labels can be rendered in 3 ways:
		//1. Normal - no staggering - no wrapping - no rotation
		//2. Wrapped - no staggering - no rotation
		//3. Staggered - no wrapping - no rotation
		//4. Rotated - no staggering - no wrapping
		//Placeholder to store max height
		this.config.maxLabelHeight = 0;
		this.config.labelAreaHeight = 0;
		if (this.params.labelDisplay == "ROTATE") {
			//Calculate the maximum width that could be alloted to the labels.
			//Note: Here, width is calculated based on canvas height, as the labels
			//are going to be rotated.
			var maxLabelWidth:Number = (canvasHeight/3);
			var maxLabelHeight:Number = (canvasWidth/finalVisibleCount);
			//Store it in config for later usage
			this.config.wrapLabelWidth = maxLabelWidth;
			this.config.wrapLabelHeight = maxLabelHeight;
			//Case 4: If the labels are rotated, we iterate through all the string labels
			//provided to us and get the height and store max.
			for (i=1; i<=this.numCat; i++) {
				//If the label is to be shown
				if (this.categories[i].isValid && this.categories[i].showLabel) {
					//Create text box and get height
					labelObj = createText(true, this.categories[i].label, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					//Store the larger
					this.config.maxLabelHeight = (labelObj.height>this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
				}
			}
			//Store max label height as label area height.
			this.config.labelAreaHeight = this.config.maxLabelHeight;
		} else if (this.params.labelDisplay == "WRAP") {
			//Case 2 (WRAP): Create all the labels on the chart. Set width as
			//totalAvailableWidth/finalVisibleCount.
			//Set max height as 50% of available canvas height at this point of time. Find all
			//and select the max one.
			var maxLabelWidth:Number = (canvasWidth/finalVisibleCount);
			var maxLabelHeight:Number = (canvasHeight/3);
			//Store it in config for later usage
			this.config.wrapLabelWidth = maxLabelWidth;
			this.config.wrapLabelHeight = maxLabelHeight;
			for (i=1; i<=this.numCat; i++) {
				//If the label is to be shown
				if (this.categories[i].isValid && this.categories[i].showLabel) {
					//Create text box and get height
					labelObj = createText(true, this.categories[i].label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, maxLabelWidth, maxLabelHeight);
					//Store the larger
					this.config.maxLabelHeight = (labelObj.height>this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
				}
			}
			//Store max label height as label area height.
			this.config.labelAreaHeight = this.config.maxLabelHeight;
		} else {
			//Case 1,3: Normal or Staggered Label
			//We iterate through all the labels, and if any of them has &lt or < (HTML marker)
			//embedded in them, we add them to the array, as for them, we'll need to individually
			//create and see the text height. Also, the first element in the array - we set as
			//ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=....
			//Create array to store labels.
			var strLabels:Array = new Array();
			strLabels.push("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_=/*-+~`");
			//Now, iterate through all the labels and for those visible labels, whcih have < sign,
			//add it to array.
			for (i=1; i<=this.numCat; i++) {
				//If the label is to be shown
				if (this.categories[i].isValid && this.categories[i].showLabel) {
					if ((this.categories[i].label.indexOf("&lt;")>-1) || (this.categories[i].label.indexOf("<")>-1)) {
						strLabels.push(this.categories[i].label);
					}
				}
			}
			//Now, we've the array for which we've to check height (for each element).
			for (i=0; i<strLabels.length; i++) {
				//Create text box and get height
				labelObj = createText(true, this.categories[i].label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
				//Store the larger
				this.config.maxLabelHeight = (labelObj.height>this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
			}
			//We now have the max label height. If it's staggered, then store accordingly, else
			//simple mode
			if (this.params.labelDisplay == "STAGGER") {
				//Multiply max label height by stagger lines.
				//No stagger mode support in numeric / auto label mode
				//At present its a single line.
				if(this.params.xAxisLabelMode == 'auto'){
					this.config.labelAreaHeight = this.config.maxLabelHeight;
				}else{
					this.config.labelAreaHeight = this.params.staggerLines*this.config.maxLabelHeight;
				}
			} else {
				this.config.labelAreaHeight = this.config.maxLabelHeight;
			}
		}		
		// labelAreaHeight value can be zero for two reason.
		// a) If no category value is  available 
		// b) label attribute is blank in the category node
		//
		// So calculate label area height labelAreaHeight .
		// a) extract text line height from numeric value 
		// b) maxLabelHeight assign into labelAreaHeight
		if(this.config.labelAreaHeight == 0)
		{
			labelObj = createText(true, this.vDivLines[0].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, 0, 0);  
			//Store the larger
			this.config.maxLabelHeight = (labelObj.height>this.config.maxLabelHeight) ? (labelObj.height) : (this.config.maxLabelHeight);
			this.config.labelAreaHeight = this.config.maxLabelHeight;
						
		}
		if (this.config.labelAreaHeight>0) {
			//Deduct the calculated label height from canvas height
			canvasHeight = canvasHeight-this.config.labelAreaHeight-this.params.labelPadding;
		}
		//Delete objects  
		delete labelObj;
		delete labelStyleObj;
		//If any vertical trend line values are to be shown
		var trendObj:Object;
		var trendStyle:Object = this.styleM.getTextStyle(this.objects.TRENDVALUES);
		var trendHeight:Number = 0;
		//Now, also check for trend line values that fall on right
		this.config.vTrendHeight = 0;
		for (i=1; i<=this.numVTrendLines; i++) {
			if (this.vTrendLines[i].isValid == true && this.vTrendLines[i].displayValue != "") {
				//If it's a valid trend line
				//Get the height of the text
				trendObj = createText(true, this.vTrendLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, trendStyle, false, 0, 0);
				//Accomodate
				trendHeight = (trendObj.height>trendHeight) ? (trendObj.height) : (trendHeight);
			}
		}
		delete trendObj;
		//Accomodate
		if (trendHeight>0) {
			canvasHeight = canvasHeight-trendHeight;
			//Store vertical trend line text height - will be used later to adjust x-axis name.
			this.config.vTrendHeight = trendHeight;
		}
		//Accomodate space for xAxisName (if to be shown);  
		if (this.params.xAxisName != "") {
			//Create text field to get height
			var xAxisNameObj:Object = createText(true, this.params.xAxisName, this.tfTestMC, 1, testTFX, testTFY, 0, this.styleM.getTextStyle(this.objects.XAXISNAME), true, canvasWidth, canvasHeight/2);
			//Store the height
			canvasHeight = canvasHeight-xAxisNameObj.height-this.params.xAxisNamePadding;
			//Object to store width and height of xAxisName
			this.elements.xAxisName = returnDataAsElement(0, 0, xAxisNameObj.width, xAxisNameObj.height);
			delete xAxisNameObj;
		}
		//We have canvas start Y and canvas height  
		//We now check whether the legend is to be drawn
		if (this.params.showLegend) {
			//Object to store dimensions
			var lgndDim:Object;
			//MC removed if present before recreation of the MC
			if(this.lgndMC){
				this.lgndMC.removeMovieClip();
			}
			//Create container movie clip for legend  
			this.lgndMC = this.cMC.createEmptyMovieClip("Legend", this.dm.getDepth("LEGEND"));
			//Create instance of legend
			if (this.params.legendPosition == "BOTTOM") {
				//Maximum Height - 50% of stage
				lgnd = new AdvancedLegend(lgndMC, this.styleM.getTextStyle(this.objects.LEGEND), this.params.interactiveLegend, this.params.legendPosition, canvasStartX+canvasWidth/2, this.height/2, canvasWidth, (this.height-(this.params.chartTopMargin+this.params.chartBottomMargin))*0.5, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			} else {
				//Maximum Width - 40% of stage
				lgnd = new AdvancedLegend(lgndMC, this.styleM.getTextStyle(this.objects.LEGEND), this.params.interactiveLegend, this.params.legendPosition, this.width/2, canvasStartY+canvasHeight/2, (this.width-(this.params.chartLeftMargin+this.params.chartRightMargin))*0.4, canvasHeight, this.params.legendAllowDrag, this.width, this.height, this.params.legendBgColor, this.params.legendBgAlpha, this.params.legendBorderColor, this.params.legendBorderThickness, this.params.legendBorderAlpha, this.params.legendScrollBgColor, this.params.legendScrollBarColor, this.params.legendScrollBtnColor, this.params.legendNumColumns);
			}
			if (this.params.minimiseWrappingInLegend) {
				lgnd.minimiseWrapping = true;
			}
			//Get the height of single line text for legend items (to be used to specify icon width and height)
			var iconHeight:Number = lgnd.getIconHeight()*this.params.legendIconScale;
			var j:Number;
			var objIcon:Object;
			for (i=1; i<=this.numDS; i++) {
				//Adjust working index for reverseLegend option
				j = (this.params.reverseLegend) ? this.numDS-i+1 : i;
				//Validation of item eligibility
				if (this.dataset[j].includeInLegend && this.dataset[j].seriesName != "") {
					//Based on whether it's a bubble chart or XY (scatter), add icons
					if (this.config.isBubbleChart) {
						//Bubble chart
						objIcon = {fillColor:parseInt(this.dataset[j].color, 16), intraIconPaddingPercent:0.3};
						//Create the 2 icons
						var objIcons:Object = LegendIconGenerator.getIcons(LegendIconGenerator.CIRCLE, iconHeight, true, objIcon);
					} else {
						//Scatter chart
						objIcon = {fillColor:parseInt(this.dataset[j].anchorBgColor, 16), borderColor:parseInt(this.dataset[j].anchorBorderColor, 16), sides:this.dataset[j].anchorSides, startingAngle:90, intraIconPaddingPercent:0.3};
						//Create the 2 icons 
						var objIcons:Object = LegendIconGenerator.getIcons(LegendIconGenerator.POLYGON, iconHeight, true, objIcon);
					}
					//State specific icon BitmapData
					var bmpd1:BitmapData = objIcons.active;
					var bmpd2:BitmapData = objIcons.inactive;
					//Add the item to legend
					lgnd.addItem(this.dataset[j].seriesName, j, true, bmpd1, bmpd2);
				}
			}
			//If user has defined a caption for the legend, set it
			if (this.params.legendCaption != "") {
				lgnd.setCaption(this.params.legendCaption);
			}
			if (this.params.legendPosition == "BOTTOM") {
				lgndDim = lgnd.getDimensions();
				//Now deduct the height from the calculated canvas height
				canvasHeight = canvasHeight-lgndDim.height-((lgndDim.height > 0) ? this.params.legendPadding : 0);
				//Re-set the legend position
				this.lgnd.resetXY(canvasStartX+canvasWidth/2, this.height-this.params.chartBottomMargin-lgndDim.height/2);
				this.elements.legend = returnDataAsElement(canvasStartX+canvasWidth/2, this.height-this.params.chartBottomMargin-lgndDim.height/2, lgndDim.width, lgndDim.height);
			} else {
				//Get dimensions
				lgndDim = lgnd.getDimensions();
				//Now deduct the width from the calculated canvas width
				canvasWidth = canvasWidth-lgndDim.width-((lgndDim.width > 0) ? this.params.legendPadding : 0);
				//Right position
				this.lgnd.resetXY(this.width-this.params.chartRightMargin-lgndDim.width/2, canvasStartY+canvasHeight/2);
				this.elements.legend = returnDataAsElement(this.width-this.params.chartRightMargin-lgndDim.width/2, canvasStartY+canvasHeight/2, lgndDim.width, lgndDim.height);
			}
		}
		//----------- HANDLING CUSTOM CANVAS MARGINS --------------//  
		//Before doing so, we take into consideration, user's forced canvas margins (if any defined)
		//If the user's forced values result in overlapping of chart items, we ignore.
		if (this.params.canvasLeftMargin != -1 && this.params.canvasLeftMargin>canvasStartX) {
			//Update width (deduct the difference)
			canvasWidth = canvasWidth-(this.params.canvasLeftMargin-canvasStartX);
			//Update left start position
			canvasStartX = this.params.canvasLeftMargin;
		}
		if (this.params.canvasRightMargin != -1 && (this.params.canvasRightMargin>(this.width-(canvasStartX+canvasWidth)))) {
			//Update width (deduct the difference)
			canvasWidth = canvasWidth-(this.params.canvasRightMargin-(this.width-(canvasStartX+canvasWidth)));
		}
		if (this.params.canvasTopMargin != -1 && this.params.canvasTopMargin>canvasStartY) {
			//Update height (deduct the difference)
			canvasHeight = canvasHeight-(this.params.canvasTopMargin-canvasStartY);
			//Update top start position
			canvasStartY = this.params.canvasTopMargin;
		}
		if (this.params.canvasBottomMargin != -1 && (this.params.canvasBottomMargin>(this.height-(canvasStartY+canvasHeight)))) {
			//Update height(deduct the difference)
			canvasHeight = canvasHeight-(this.params.canvasBottomMargin-(this.height-(canvasStartY+canvasHeight)));
		}
		//------------ END OF CUSTOM CANVAS MARGIN HANDLING --------------------//  
		//Based on canvas height that has been calculated, re-adjust yaxisLabelStep
		this.adjustYAxisLabelStep(yAxisValMaxHeight, canvasHeight);
		
		
		//------------ CODE ADDED FOR VERTICAL LABEL -------------------------- // 
		//Accomodate width for x-axis values. Now, x-axis values consist of two parts  
		//(for backward compatibility) - limits (upper and lower) and div line values.
		//So, need individually run through both of them.
		var xAxisValMaxWidth:Number = 0;
		//Also store the height required to render the text field
		var xAxisValMaxHeight:Number = 0;
		var vDivLineObj:Object;
		var vDivStyle:Object = this.styleM.getTextStyle(this.objects.XAXISVALUES);		
		
		//Iterate through all the div line values
		for (i=1; i<this.vDivLines.length; i++) {
			//If div line value is to be shown
			if (this.vDivLines[i].showValue) {
				//If it's the first or last div Line (limits), and it's to be shown
				if ((i == 1) || (i == this.vDivLines.length-1)) {
					if (this.params.showLimits) {
						//Get the width of the text
						vDivLineObj = createText(true, this.vDivLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, vDivStyle, true, 0, 0);
						//Accomodate
						xAxisValMaxWidth = (vDivLineObj.width>xAxisValMaxWidth) ? (vDivLineObj.width) : (xAxisValMaxWidth);
						xAxisValMaxHeight = (vDivLineObj.height>xAxisValMaxHeight) ? (vDivLineObj.height) : (xAxisValMaxHeight);
					}
				} else {
					//It's a div interval - div line
					//So, check if we've to show div line values
					if (this.params.showVDivLineValues) {
						//Get the width of the text
						vDivLineObj = createText(true, this.vDivLines[i].displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, vDivStyle, true, 0, 0);
						//Accomodate
						xAxisValMaxWidth = (vDivLineObj.width>xAxisValMaxWidth) ? (vDivLineObj.width) : (xAxisValMaxWidth);
						xAxisValMaxHeight = (vDivLineObj.height>xAxisValMaxHeight) ? (vDivLineObj.height) : (xAxisValMaxHeight);
					}
				}
			}
		}
		delete vDivLineObj;
		
		//Based on canvas height that has been calculated, re-adjust yaxisLabelStep
		this.adjustXAxisLabelStep(xAxisValMaxWidth, canvasWidth);
		
		
		//get difference to be made for first valid / visible label
		var canvasMetrics:Object = getCanvMatrcsForFirstLabel(canvasWidth, canvasStartX, canvasHeight, canvasStartY, lgndDim, labelStyleObj);
		canvasWidth = canvasMetrics.canvasWidth;
		canvasStartX = canvasMetrics.canvasStartX;
		//calculate final canvas width in order to central align the last label.
		var newCanvasWidth:Number = getFinalCanvasWidth(canvasWidth, canvasStartX, canvasHeight, canvasStartY, lgndDim, labelStyleObj);
		//Now just to accomodate the last label we can not make a huge impact in the change of canvas width.
		//This is possible if there is very less number of labels and the average label width becomes almost
		//equal to the canvas width. At present the maximum allowable change for the last label is limited to
		//one third of the actual canvas.
		canvasWidth = ((canvasWidth - newCanvasWidth) > (canvasWidth/3)) ? canvasWidth - Math.floor(canvasWidth/3) : newCanvasWidth;
		
		//Create an element to represent the canvas now.
		this.elements.canvas = returnDataAsElement(canvasStartX, canvasStartY, canvasWidth, canvasHeight);
	}
	
	/**
	* getCanvMatrcsForFirstLabel method modifies the canvas width and its start position
	* based on the width of the first valid category label. We ignore any calculation for the
	* numeric label management.
	*
	* @param	canvW		The width of the canvas.
	* @param	cnvStrtX	The start x position of the canvas.
	* @param	canvH		The Height of the canvas
	* @param	cnvStrtY	The start y position of the canvas.
	* @param	lgndObj		The legend object.
	* @param	styleObj	The style object for all the labels.
	*
	* @return 				The final canvas dimention object considering the first label in central align mode.
	**/
	private function getCanvMatrcsForFirstLabel(canvW:Number, cnvStrtX:Number, canvH:Number, cnvStrtY:Number, lgndObj:Object, styleObj:Object):Object{
		//maximum left limit
		var maxLeftLimit:Number = this.params.chartLeftMargin;
		//if yAxisName is defined add it and its padding.
		if(this.params.yAxisNameWidth > 0){
			maxLeftLimit += this.params.yAxisNameWidth + this.params.yAxisNamePadding;
		}
		//get the first valid category label
		var lblNo:Number = 0;
		while(lblNo <= this.categories.length){
			var firstCatObj:Object = this.categories[lblNo];
			if(firstCatObj.showLabel && firstCatObj.label.length > 0){
				//label is visible and have texts. Now check whether this is within the valid range.
				if (firstCatObj.xv >= this.config.xMin && firstCatObj.xv <= this.config.xMax){
					//this is a valid category. no more checking required.
					break;
				}
			}
			lblNo++;
		}
		//minimum width required to render the label.
		var minReqWidth:Number;
		//check for category and Mixed mode. We ignore the numeric labels for left hand calculation. As :
		/*	if the numeric label's x position is greater than the category label we ignore further process at this label because.
		* 	1. There is a methods which will hide the numeric labels if ther are same with the category or will overlap with 
		*	anu other category.
		*	2. Numeric labels are genrally small added (K, M when long) and left panel will always have some space in y axis vales.
		*	Therefor minimum scope for decreasing.
		*/
		if(this.params.xAxisLabelMode == 'categories' || this.params.xAxisLabelMode == 'mixed'){
			//first category text without wrap
			var firstCatTxtObj:Object = createText(true, firstCatObj.label, this.tfTestMC, 1, testTFX, testTFY, 0, styleObj, false, 0, 0);
			//update min width required.
			minReqWidth = Math.min(firstCatTxtObj.width, this.config.wrapLabelWidth);
			//only if we need to show the label
			if(firstCatObj.showLabel){
				//approximate x position of the label. as canvas width is not yet final.
				var apprxXPos:Number = this.getAxisPosition (firstCatObj.xv, this.config.xMax, this.config.xMin, cnvStrtX, (cnvStrtX + canvW), false, 0);
				//manage as per display mode
				if (this.params.labelDisplay == "ROTATE"){
					//approximate width with wrapping.
					firstCatTxtObj = createText(true, firstCatObj.label, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, styleObj, true, this.config.wrapLabelHeight, minReqWidth);
				}else{
					//approximate width with wrapping.
					firstCatTxtObj = createText(true, firstCatObj.label, this.tfTestMC, 1, testTFX, testTFY, 0, styleObj, true, minReqWidth, this.config.wrapLabelHeight);
				}
				//approximate start position of the label.
				var catStartPos:Number = apprxXPos - firstCatTxtObj.width / 2;
				//check whether the start is overlapping with the left limit.
				if(catStartPos < maxLeftLimit){
					//amount of overlapping
					var diff:Number = maxLeftLimit - catStartPos;
					//decrease canvas width by the amout of overlapping
					canvW -= diff;
					//shift the canvas same amount
					cnvStrtX += diff
				}
			}
		}
		return {canvasWidth: canvW, canvasStartX: cnvStrtX}
	}
	
	
	
	/**
	* getReqSapceForLastLabel calculates canvas width considering the the width required 
	* for the last label. The purpose of this function is to keep the last label centrally
	* aligned. Instead of making it right align.
	*
	* NOTE	:	We first need to decide which xAxisLabelMode is available (category, auto or mixed)
	* 			In case of mixed we need to decide which among category or numeric is having the last label
	* 			We also have to manage accorind to display mode (rotate or wrap / stagger).
	*
	* @param	canvX		The width of the canvas.
	* @param	cnvStrtX	The start x position of the canvas.
	* @param	canvH		The Height of the canvas
	* @param	cnvStrtY	The start y position of the canvas.
	* @param	lgndObj		The legend object.
	* @param	styleObj	The style object for all the labels.
	*
	* @return 				The final canvas width considering the last label in central align mode.
	*
	* TO DO :: This function could be optimized with proper logical tree.
	*
	**/
	private function getFinalCanvasWidth(canvW:Number, cnvStrtX:Number, canvH:Number, cnvStrtY:Number, lgndObj:Object, styleObj:Object):Number{
		//variables
		var canvasWidth:Number = canvW;
		var canvasStartX:Number = cnvStrtX;
		var canvasHeight:Number = canvH;
		var canvasStartY:Number = cnvStrtY;
		var lgndDim:Object = lgndObj;
		var labelStyleObj:Object = styleObj;
		//the extreme right limit. Where no label can be placed.
		var maxRightLimit:Number = this.width - this.params.chartRightMargin;
		//If the legend is at right the right limit get changed.
		if(this.params.legendPosition == "RIGHT" && lgndDim.width > 0){
			//we further check whether there is a possibility of labels overlapping 
			//the legend
			var legendEndYPos:Number = this.elements.legend.y + this.elements.legend.h / 2;
			var canvasEndYPos:Number = canvasStartY + canvasHeight;
			//if legend height is going beyond the canvas end y postion the labels may 
			//overlap with the legend
			if(legendEndYPos > canvasEndYPos){
				//now we modify the limit.
				maxRightLimit -= lgndDim.width + this.params.legendPadding;
			}
		}
		//minimum width required to render the label.
		var minReqWidth:Number;
		//check for category mode
		if(this.params.xAxisLabelMode == 'categories'){
			//search for the last valid category label object
			var lblNo:Number = this.categories.length - 1;
			while(lblNo >= 0){
				var lastCatObj:Object = this.categories[lblNo];
				if(lastCatObj.showLabel && lastCatObj.label.length > 0){
					//label is visible and have texts. Now check whether this is within the valid range.
					if (lastCatObj.xv >= this.config.xMin && lastCatObj.xv <= this.config.xMax){
						//this is a valid category. no more checking required.
						break;
					}
				}
				lblNo--;
			}
			
			//last category text without wrap
			var lastCatTxtObj:Object = createText(true, lastCatObj.label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
			//update min width required.
			minReqWidth = Math.min(lastCatTxtObj.width, this.config.wrapLabelWidth);
			//only if we need to show the label
			if(lastCatObj.showLabel){
				//approximate x position of the label. as canvas width is not yet final.
				var apprxXPos:Number = this.getAxisPosition (lastCatObj.xv, this.config.xMax, this.config.xMin, canvasStartX, (canvasStartX + canvasWidth), false, 0);
				
				
				//manage as per display mode
				if (this.params.labelDisplay == "ROTATE"){
					//approximate width with wrapping.
					lastCatTxtObj = createText(true, lastCatObj.label, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, this.config.wrapLabelHeight, minReqWidth);
				}else{
					//approximate width with wrapping.
					lastCatTxtObj = createText(true, lastCatObj.label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, minReqWidth, this.config.wrapLabelHeight);
				}
				//approximate end position of the label.
				var catEndPos:Number = apprxXPos + lastCatTxtObj.width / 2;
				//check whether the end is overlapping with the right limit.
				if(catEndPos > maxRightLimit){
					//amount of overlapping
					var diff:Number = catEndPos - maxRightLimit;
					//decrease canvas width by the amout of overlapping
					canvasWidth -= diff;
				}
			}
		}else if(this.params.xAxisLabelMode == 'auto'){
			//auto mode checking
			//last numeric label object
			//Note : In numeric label management there is no scope of any label explicitly hidden by showLabel = 0 or
			//		 Any label doesn't have a valid string.
			var lastVLnObj:Object = this.vDivLines[(this.vDivLines.length - 1)];
			//last numeric text without wrap
			var lastNumTxtObj:Object = createText(true, lastVLnObj.displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
			//update min width required.
			minReqWidth = Math.min(lastNumTxtObj.width, this.config.wrapLabelWidth);
			//only if we need to show the label
			if(lastVLnObj.showValue){
				//approximate x position of the label. as canvas width is not yet final.
				var apprxXPos:Number = this.getAxisPosition (lastVLnObj.value, this.config.xMax, this.config.xMin, canvasStartX, (canvasStartX + canvasWidth), false, 0);
				//manage as per display mode
				if (this.params.labelDisplay == "ROTATE"){
					//approximate width with wrapping.
					lastNumTxtObj = createText(true, lastVLnObj.displayValue, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, this.config.wrapLabelHeight, minReqWidth);
				}else{
					//approximate width with wrapping.
					lastNumTxtObj = createText(true, lastVLnObj.displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, minReqWidth, this.config.wrapLabelHeight);
				}
				//approximate end position of the label.
				var lblEndPos:Number = apprxXPos + lastNumTxtObj.width / 2;
				//check whether the end is overlapping with the right limit.
				if(lblEndPos > maxRightLimit){
					//amount of overlapping
					var diff:Number = lblEndPos - maxRightLimit;
					//decrease canvas width by the amout of overlapping
					canvasWidth -= diff;
				}
			}
		}else{
			//In mixed mode we first have to decide which label is at extreme right.
			//search for the last valid category label object
			var lblNo:Number = this.categories.length - 1;
			while(lblNo >= 0){
				var lastCatObj:Object = this.categories[lblNo];
				if(lastCatObj.showLabel && lastCatObj.label.length > 0){
					//label is visible and have texts. Now check whether this is within the valid range.
					if (lastCatObj.xv >= this.config.xMin && lastCatObj.xv <= this.config.xMax){
						//this is a valid category. no more checking required.
						break;
					}
				}
				lblNo--;
			}
			//last numeric label object
			var lastVLnObj:Object = this.vDivLines[(this.vDivLines.length - 1)];
			//approximate x position
			var apprxCatXPos:Number = 0;
			var apprxNumXPos:Number = 0;
			//only if the last category label is to be shown
			if(lastCatObj.showLabel){
				apprxCatXPos = this.getAxisPosition (lastCatObj.xv, this.config.xMax, this.config.xMin, canvasStartX, (canvasStartX + canvasWidth), false, 0);
			}
			//only if the last numeric label is to be shown
			if(lastVLnObj.showValue){
				apprxNumXPos = this.getAxisPosition (lastVLnObj.value, this.config.xMax, this.config.xMin, canvasStartX, (canvasStartX + canvasWidth), false, 0);
			}
			//if the numeric label's x position is greater than the category label 
			if(apprxNumXPos > apprxCatXPos){
				//only if the last numeric label is to be shown
				if(lastVLnObj.showValue){
					//last numeric text without wrap
					var lastNumTxtObj:Object = createText(true, lastVLnObj.displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
					//update min width required.
					minReqWidth = Math.min(lastNumTxtObj.width, this.config.wrapLabelWidth);
					//manage as per display mode
					if (this.params.labelDisplay == "ROTATE"){
						//approximate width with wrapping.
						lastNumTxtObj = createText(true, lastVLnObj.displayValue, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, this.config.wrapLabelHeight, minReqWidth);
					}else{
						//approximate width with wrapping.
						lastNumTxtObj = createText(true, lastVLnObj.displayValue, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, minReqWidth, this.config.wrapLabelHeight);
					}
					//approximate end position of the label.
					var lblEndPos:Number = apprxNumXPos + lastNumTxtObj.width / 2;
					//check whether the end is overlapping with the right limit.
					if(lblEndPos > maxRightLimit){
						//amount of overlapping
						var diff:Number = lblEndPos - maxRightLimit;
						//decrease canvas width by the amout of overlapping
						canvasWidth -= diff;
					}
				}
			}else{
				//if the category label's x position is greater than the last numeric label 
				if(lastCatObj.showLabel){
					//last category text without wrap
					var lastCatTxtObj:Object = createText(true, lastCatObj.label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, false, 0, 0);
					//update min width required.
					minReqWidth = Math.min(lastCatTxtObj.width, this.config.wrapLabelWidth);
					//manage as per display mode
					if (this.params.labelDisplay == "ROTATE"){
						//approximate width with wrapping.
						lastCatTxtObj = createText(true, lastCatObj.label, this.tfTestMC, 1, testTFX, testTFY, this.config.labelAngle, labelStyleObj, true, this.config.wrapLabelHeight, minReqWidth);
					}else{
						//approximate width with wrapping.
						lastCatTxtObj = createText(true, lastCatObj.label, this.tfTestMC, 1, testTFX, testTFY, 0, labelStyleObj, true, minReqWidth, this.config.wrapLabelHeight);						
					}
					//approximate end position of the label.
					var catEndPos:Number = apprxCatXPos + lastCatTxtObj.width / 2;
					//check whether the end is overlapping with the right limit.
					if(catEndPos > maxRightLimit){
						//amount of overlapping
						var diff:Number = catEndPos - maxRightLimit;
						//decrease canvas width by the amout of overlapping
						canvasWidth -= diff;
					}
				}
			}
		}
		return canvasWidth;
	}
	
	
	/**
	* calcTrendLinePos method helps us calculate the y-co ordinates for the
	* trend lines
	* NOTE: validateTrendLines and calcTrendLinePos could have been composed
	*			into a single method. However, in calcTrendLinePos, we need the
	*			canvas position, which is possible only after calculatePoints
	*			method has been called. But, in calculatePoints, we need the
	*			displayValue of each trend line, which is being set in
	*			validateTrendLines. So, validateTrendLines is invoked before
	*			calculatePoints method and calcTrendLinePos is invoked after.
	*	@return		Nothing
	*/
	private function calcTrendLinePos() {
		//Loop variable
		var i:Number;
		//Run for horizontal trend lines
		for (i=0; i<=this.numTrendLines; i++) {
			//We proceed only if the trend line is valid
			if (this.trendLines[i].isValid == true) {
				//Calculate and store y-positions
				this.trendLines[i].y = this.getAxisPosition(this.trendLines[i].startValue, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
				//If end value is different from start value
				if (this.trendLines[i].startValue != this.trendLines[i].endValue) {
					//Calculate y for end value
					this.trendLines[i].toY = this.getAxisPosition(this.trendLines[i].endValue, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
					//Now, if it's a trend zone, we need mid value
					if (this.trendLines[i].isTrendZone) {
						//For textbox y position, we need mid value.
						this.trendLines[i].tbY = Math.min(this.trendLines[i].y, this.trendLines[i].toY)+(Math.abs(this.trendLines[i].y-this.trendLines[i].toY)/2);
					} else {
						//If the value is to be shown on left, then at left
						if (this.trendLines[i].valueOnRight) {
							this.trendLines[i].tbY = this.trendLines[i].toY;
						} else {
							this.trendLines[i].tbY = this.trendLines[i].y;
						}
					}
					//Height
					this.trendLines[i].h = (this.trendLines[i].toY-this.trendLines[i].y);
				} else {
					//Just copy
					this.trendLines[i].toY = this.trendLines[i].y;
					//Set same position for value tb
					this.trendLines[i].tbY = this.trendLines[i].y;
					//Height
					this.trendLines[i].h = 0;
				}
			}
		}
		//Run for vertical trend lines
		for (i=0; i<=this.numVTrendLines; i++) {
			//We proceed only if the trend line is valid
			if (this.vTrendLines[i].isValid == true) {
				//Calculate and store x-positions
				this.vTrendLines[i].x = this.getAxisPosition(this.vTrendLines[i].startValue, this.config.xMax, this.config.xMin, this.elements.canvas.x, this.elements.canvas.toX, false, 0);
				//If end value is different from start value
				if (this.vTrendLines[i].startValue != this.vTrendLines[i].endValue) {
					//Calculate x for end value
					this.vTrendLines[i].toX = this.getAxisPosition(this.vTrendLines[i].endValue, this.config.xMax, this.config.xMin, this.elements.canvas.x, this.elements.canvas.toX, false, 0);
					//Now, if it's a trend zone, we need mid value
					if (this.vTrendLines[i].isTrendZone) {
						//For textbox x position, we need mid value.
						this.vTrendLines[i].tbX = Math.min(this.vTrendLines[i].x, this.vTrendLines[i].toX)+(Math.abs(this.vTrendLines[i].x-this.vTrendLines[i].toX)/2);
					} else {
						this.vTrendLines[i].tbX = this.vTrendLines[i].x;
					}
					//Width
					this.vTrendLines[i].w = (this.vTrendLines[i].toX-this.vTrendLines[i].x);
				} else {
					//Just copy
					this.vTrendLines[i].toX = this.vTrendLines[i].x;
					//Set same position for value tb
					this.vTrendLines[i].tbX = this.vTrendLines[i].x;
					//Width
					this.vTrendLines[i].w = 0;
				}
			}
		}
	}
	/**
	* feedMacros method feeds macros and their respective values
	* to the macro instance. This method is to be called after
	* calculatePoints, as we set the canvas and chart co-ordinates
	* in this method, which is known to us only after calculatePoints.
	*	@return	Nothing
	*/
	private function feedMacros():Void {
		//Feed macros one by one
		//Chart dimension macros
		this.macro.addMacro("$chartStartX", this.x);
		this.macro.addMacro("$chartStartY", this.y);
		this.macro.addMacro("$chartWidth", this.width);
		this.macro.addMacro("$chartHeight", this.height);
		this.macro.addMacro("$chartEndX", this.width);
		this.macro.addMacro("$chartEndY", this.height);
		this.macro.addMacro("$chartCenterX", this.width/2);
		this.macro.addMacro("$chartCenterY", this.height/2);
		//Canvas dimension macros
		this.macro.addMacro("$canvasStartX", this.elements.canvas.x);
		this.macro.addMacro("$canvasStartY", this.elements.canvas.y);
		this.macro.addMacro("$canvasWidth", this.elements.canvas.w);
		this.macro.addMacro("$canvasHeight", this.elements.canvas.h);
		this.macro.addMacro("$canvasEndX", this.elements.canvas.toX);
		this.macro.addMacro("$canvasEndY", this.elements.canvas.toY);
		this.macro.addMacro("$canvasCenterX", this.elements.canvas.x+(this.elements.canvas.w/2));
		this.macro.addMacro("$canvasCenterY", this.elements.canvas.y+(this.elements.canvas.h/2));
	}
	// ----------------- VISUAL RENDERING METHODS ---------------- //
	/**
	* drawCanvas method renders the chart canvas.
	*	@return	Nothing
	*/
	private function drawCanvas():Void {
		//Create a new movie clip container for canvas
		var canvasMC = this.cMC.createEmptyMovieClip("Canvas", this.dm.getDepth("CANVAS"));
		//Parse the color, alpha and ratio array
		var canvasColor:Array = ColorExt.parseColorList(this.params.canvasBgColor);
		var canvasAlpha:Array = ColorExt.parseAlphaList(this.params.canvasBgAlpha, canvasColor.length);
		var canvasRatio:Array = ColorExt.parseRatioList(this.params.canvasBgRatio, canvasColor.length);
		//Set border properties - not visible
		//canvasMC.lineStyle(this.params.canvasBorderThickness, parseInt(this.params.canvasBorderColor, 16), this.params.canvasBorderAlpha);
		canvasMC.lineStyle();
		//Move to (-w/2, 0);
		canvasMC.moveTo(-(this.elements.canvas.w/2), -(this.elements.canvas.h/2));
		//Create matrix object
		var matrix:Object = {matrixType:"box", w:this.elements.canvas.w, h:this.elements.canvas.h, x:-(this.elements.canvas.w/2), y:-(this.elements.canvas.h/2), r:MathExt.toRadians(this.params.canvasBgAngle)};
		//Start the fill.
		canvasMC.beginGradientFill("linear", canvasColor, canvasAlpha, canvasRatio, matrix);
		//Draw the rectangle with center registration point
		canvasMC.lineTo(this.elements.canvas.w/2, -(this.elements.canvas.h/2));
		canvasMC.lineTo(this.elements.canvas.w/2, this.elements.canvas.h/2);
		canvasMC.lineTo(-(this.elements.canvas.w/2), this.elements.canvas.h/2);
		canvasMC.lineTo(-(this.elements.canvas.w/2), -(this.elements.canvas.h/2));
		//Set the x and y position
		canvasMC._x = this.elements.canvas.x+this.elements.canvas.w/2;
		canvasMC._y = this.elements.canvas.y+this.elements.canvas.h/2;
		//End Fill
		canvasMC.endFill();
		// --------------------------- DRAW CANVAS BORDER --------------------------//
		//Canvas Border
		if (this.params.canvasBorderAlpha>0) {
			//Create a new movie clip container for canvas
			var canvasBorderMC = this.cMC.createEmptyMovieClip("CanvasBorder", this.dm.getDepth("CANVASBORDER"));
			//Set border properties
			canvasBorderMC.lineStyle(this.params.canvasBorderThickness, parseInt(this.params.canvasBorderColor, 16), this.params.canvasBorderAlpha);
			//Move to (-w/2, 0);
			canvasBorderMC.moveTo(-(this.elements.canvas.w/2), -(this.elements.canvas.h/2));
			//Draw the rectangle with center registration point
			canvasBorderMC.lineTo(this.elements.canvas.w/2, -(this.elements.canvas.h/2));
			canvasBorderMC.lineTo(this.elements.canvas.w/2, this.elements.canvas.h/2);
			canvasBorderMC.lineTo(-(this.elements.canvas.w/2), this.elements.canvas.h/2);
			canvasBorderMC.lineTo(-(this.elements.canvas.w/2), -(this.elements.canvas.h/2));
			//Set the x and y position
			canvasBorderMC._x = this.elements.canvas.x+this.elements.canvas.w/2;
			canvasBorderMC._y = this.elements.canvas.y+this.elements.canvas.h/2;
		}
		//Apply animation  
		if (this.params.animation) {
			this.styleM.applyAnimation(canvasBorderMC, this.objects.CANVAS, this.macro, canvasBorderMC._x, -this.elements.canvas.w/2, canvasBorderMC._y, -this.elements.canvas.h/2, 100, 100, 100, null);
			this.styleM.applyAnimation(canvasMC, this.objects.CANVAS, this.macro, canvasMC._x, -this.elements.canvas.w/2, canvasMC._y, -this.elements.canvas.h/2, 100, 100, 100, null);
		}
		//Apply filters  
		this.styleM.applyFilters(canvasMC, this.objects.CANVAS);
		clearInterval(this.config.intervals.canvas);
	}
	/**
	* drawTrendLines method draws the vertical trend lines on the chart
	* with their respective values.
	*/
	private function drawTrendLines():Void {
		var trendFontObj:Object;
		var trendValueObj:Object;
		var lineBelowDepth:Number = this.dm.getDepth("TRENDLINESBELOW");
		var valueBelowDepth:Number = this.dm.getDepth("TRENDVALUESBELOW");
		var lineAboveDepth:Number = this.dm.getDepth("TRENDLINESABOVE");
		var valueAboveDepth:Number = this.dm.getDepth("TRENDVALUESABOVE");
		var lineDepth:Number;
		var valueDepth:Number;
		var tbAnimX:Number = 0;
		//Movie clip container
		var trendLineMC:MovieClip;
		//Get font
		trendFontObj = this.styleM.getTextStyle(this.objects.TRENDVALUES);
		//Set vertical alignment
		trendFontObj.vAlign = "middle";
		//Delegate handler function
		var fnRollOver:Function;
		//Loop variable
		var i:Number;
		//Boolean value to determine trend zone status changed due to negotiable height.
		var trendZoneStatChange:Boolean;
		//Iterate through all the trend lines
		for (i=1; i<=this.numTrendLines; i++) {
			//Set trend zone change status to false
			trendZoneStatChange = false;
			//When trendzone height is less than 0.3 the zone is not visible. So need to display the single trend line.
			//At that situation isTrendZone needs to make false.
			if(this.trendLines [i].isTrendZone && Math.abs(this.trendLines [i].h) < 0.3){
				this.trendLines [i].isTrendZone = false;
				trendZoneStatChange = true;
			}
			if (this.trendLines[i].isValid == true) {
				//If it's a valid trend line
				//Get the depth and update counters
				if (this.trendLines[i].showOnTop) {
					//If the trend line is to be shown on top.
					lineDepth = lineAboveDepth;
					valueDepth = valueAboveDepth;
					lineAboveDepth++;
					valueAboveDepth++;
				} else {
					//If it's to be shown below columns.
					lineDepth = lineBelowDepth;
					valueDepth = valueBelowDepth;
					lineBelowDepth++;
					valueBelowDepth++;
				}
				trendLineMC = this.cMC.createEmptyMovieClip("TrendLine_"+i, lineDepth);
				//Now, draw the line or trend zone
				if (this.trendLines[i].isTrendZone) {
					//Create rectangle
					trendLineMC.lineStyle();
					//Absolute height value
					this.trendLines[i].h = Math.abs(this.trendLines[i].h);
					//We need to align rectangle at L,M
					trendLineMC.moveTo(0, 0);
					//Begin fill
					trendLineMC.beginFill(parseInt(this.trendLines[i].color, 16), this.trendLines[i].alpha);
					//Draw rectangle
					trendLineMC.lineTo(0, -(this.trendLines[i].h/2));
					trendLineMC.lineTo(this.elements.canvas.w, -(this.trendLines[i].h/2));
					trendLineMC.lineTo(this.elements.canvas.w, (this.trendLines[i].h/2));
					trendLineMC.lineTo(0, (this.trendLines[i].h/2));
					trendLineMC.lineTo(0, 0);
					//Re-position
					trendLineMC._x = this.elements.canvas.x;
					trendLineMC._y = this.trendLines[i].tbY;
				} else {
					//If the tooltext is to be shown for this trend line and the thickness is less than 3 pixels
					//we increase the hit area to 4 pixels with 0 alpha.
					if (this.params.showToolTip && (this.trendLines[i].toolText != "") && (this.trendLines[i].thickness<4)) {
						//Set line style with alpha 0
						trendLineMC.lineStyle(4, parseInt(this.trendLines[i].color, 16), 0);
						//Draw the hit area
						trendLineMC.moveTo(0, 0);
						trendLineMC.lineTo(this.elements.canvas.w, this.trendLines[i].h);
					}
					//When trend zone status is changed draw a hair line instead of using trendline thickness.
					this.trendLines[i].thickness = (trendZoneStatChange)? 0 : this.trendLines[i].thickness;
					//Just draw line  
					trendLineMC.lineStyle(this.trendLines[i].thickness, parseInt(this.trendLines[i].color, 16), this.trendLines[i].alpha);
					//Now, if dashed line is to be drawn
					if (!this.trendLines[i].isDashed) {
						//Draw normal line line keeping 0,0 as registration point
						trendLineMC.moveTo(0, 0);
						trendLineMC.lineTo(this.elements.canvas.w, this.trendLines[i].h);
					} else {
						//Dashed Line line
						DrawingExt.dashTo(trendLineMC, 0, 0, this.elements.canvas.w, this.trendLines[i].h, this.trendLines[i].dashLen, this.trendLines[i].dashGap);
					}
					//Re-position line
					trendLineMC._x = this.elements.canvas.x;
					trendLineMC._y = this.trendLines[i].y;
				}
				//Set the trend line tool-text
				if (this.params.showToolTip && this.trendLines[i].toolText != "") {
					//Do not use hand cursor
					trendLineMC.useHandCursor = false;
					//Create Delegate for roll over function showToolTip
					fnRollOver = Delegate.create(this, showToolTip);
					//Set the tool text
					fnRollOver.toolText = this.trendLines[i].toolText;
					//Assing the delegates to movie clip handler
					trendLineMC.onRollOver = fnRollOver;
					//Set roll out and mouse move too.
					trendLineMC.onRollOut = trendLineMC.onReleaseOutside=Delegate.create(this, hideToolTip);
					trendLineMC.onMouseMove = Delegate.create(this, repositionToolTip);
				}
				//Apply animation  
				if (this.params.animation) {
					this.styleM.applyAnimation(trendLineMC, this.objects.TRENDLINES, this.macro, null, 0, trendLineMC._y, 0, 100, 100, 100, null);
				}
				//Apply filters  
				this.styleM.applyFilters(trendLineMC, this.objects.TRENDLINES);
				//---------------------------------------------------------------------------//
				//Set color
				trendFontObj.color = this.trendLines[i].color;
				//Now, render the trend line value, based on its position
				if (this.trendLines[i].valueOnRight == false) {
					//Value to be placed on right
					trendFontObj.align = "right";
					//Create text
					trendValueObj = createText(false, this.trendLines[i].displayValue, this.cMC, valueDepth, this.elements.canvas.x-this.params.yAxisValuesPadding, this.trendLines[i].tbY, 0, trendFontObj, false, 0, 0);
					//X-position for text box animation
					tbAnimX = this.elements.canvas.x-this.params.yAxisValuesPadding-trendValueObj.width;
				} else {
					//Left side
					trendFontObj.align = "left";
					//Create text
					trendValueObj = createText(false, this.trendLines[i].displayValue, this.cMC, valueDepth, this.elements.canvas.toX+this.params.yAxisValuesPadding, this.trendLines[i].tbY, 0, trendFontObj, false, 0, 0);
					//X-position for text box animation
					tbAnimX = this.elements.canvas.toX+this.params.yAxisValuesPadding;
				}
				//Animation and filter effect
				if (this.params.animation) {
					this.styleM.applyAnimation(trendValueObj.tf, this.objects.TRENDVALUES, this.macro, tbAnimX, 0, this.trendLines[i].tbY-(trendValueObj.height/2), 0, 100, null, null, null);
				}
				//Apply filters  
				this.styleM.applyFilters(trendValueObj.tf, this.objects.TRENDVALUES);
			}
		}
		delete trendLineMC;
		delete trendValueObj;
		delete trendFontObj;
		//Clear interval
		clearInterval(this.config.intervals.trend);
	}
	/**
	* drawVTrendLines method draws the vertical trend lines on the chart
	* with their respective values.
	*/
	private function drawVTrendLines():Void {
		var trendFontObj:Object;
		var trendValueObj:Object;
		var lineDepth:Number = this.dm.getDepth("VTRENDLINES");
		var valueDepth:Number = this.dm.getDepth("VTRENDVALUES");
		var tbAnimY:Number = 0;
		//Movie clip container
		var trendLineMC:MovieClip;
		//Get font
		trendFontObj = this.styleM.getTextStyle(this.objects.TRENDVALUES);
		//Set vertical alignment
		trendFontObj.align = "center";
		trendFontObj.vAlign = "bottom";
		//Delegate handler function
		var fnRollOver:Function;
		//Loop variable
		var i:Number;
		//Boolean value to determine trend zone status changed due to negotiable width
		var trendZoneStatChange:Boolean;
		//Iterate through all the trend lines
		for (i=1; i<=this.numVTrendLines; i++) {
			//Set trend zone change status to false
			trendZoneStatChange = false;
			//When trendzone width is less than 0.3 the zone is not visible. So need to display the single trend line.
			//At that situation isTrendZone needs to make false.
			if(this.vTrendLines [i].isTrendZone && Math.abs(this.vTrendLines [i].w) < 0.3){
				this.vTrendLines [i].isTrendZone = false;
				trendZoneStatChange = true;
			}
			if (this.vTrendLines[i].isValid == true) {
				//If it's a valid trend line
				trendLineMC = this.cMC.createEmptyMovieClip("VTrendLine_"+i, lineDepth);
				if (this.vTrendLines[i].isTrendZone) {
					//Create rectangle
					trendLineMC.lineStyle();
					//Absolute width value
					this.vTrendLines[i].w = Math.abs(this.vTrendLines[i].w);
					//We need to align rectangle at C,B
					trendLineMC.moveTo(0, 0);
					//Begin fill
					trendLineMC.beginFill(parseInt(this.vTrendLines[i].color, 16), this.vTrendLines[i].alpha);
					//Draw rectangle
					trendLineMC.lineTo(-(this.vTrendLines[i].w/2), 0);
					trendLineMC.lineTo(-(this.vTrendLines[i].w/2), this.elements.canvas.h);
					trendLineMC.lineTo(this.vTrendLines[i].w/2, this.elements.canvas.h);
					trendLineMC.lineTo(this.vTrendLines[i].w/2, 0);
					trendLineMC.lineTo(-(this.vTrendLines[i].w/2), 0);
					//Re-position
					trendLineMC._x = this.vTrendLines[i].tbX;
					trendLineMC._y = this.elements.canvas.y;
				} else {
					//If the tooltext is to be shown for this trend line and the thickness is less than 3 pixels
					//we increase the hit area to 4 pixels with 0 alpha.
					if (this.params.showToolTip && (this.vTrendLines[i].toolText != "") && (this.vTrendLines[i].thickness<4)) {
						//Set line style with alpha 0
						trendLineMC.lineStyle(4, parseInt(this.vTrendLines[i].color, 16), 0);
						//Draw the hit area
						trendLineMC.moveTo(0, 0);
						trendLineMC.lineTo(this.vTrendLines[i].w, this.elements.canvas.h);
					}
					//When trend zone status is changed draw a hair line instead of using trendline thickness.					}
					this.vTrendLines[i].thickness = (trendZoneStatChange)? 0 : this.vTrendLines[i].thickness;
					//Just draw line  
					trendLineMC.lineStyle(this.vTrendLines[i].thickness, parseInt(this.vTrendLines[i].color, 16), this.vTrendLines[i].alpha);
					//Now, if dashed line is to be drawn
					if (!this.vTrendLines[i].isDashed) {
						//Draw normal line line keeping 0,0 as registration point
						trendLineMC.moveTo(0, 0);
						trendLineMC.lineTo(this.vTrendLines[i].w, this.elements.canvas.h);
					} else {
						//Dashed Line line
						DrawingExt.dashTo(trendLineMC, 0, 0, this.vTrendLines[i].w, this.elements.canvas.h, this.vTrendLines[i].dashLen, this.vTrendLines[i].dashGap);
					}
					//Re-position line
					trendLineMC._x = this.vTrendLines[i].x;
					trendLineMC._y = this.elements.canvas.y;
				}
				//Set the trend line tool-text
				if (this.params.showToolTip && this.vTrendLines[i].toolText != "") {
					//Do not use hand cursor
					trendLineMC.useHandCursor = false;
					//Create Delegate for roll over function showToolTip
					fnRollOver = Delegate.create(this, showToolTip);
					//Set the tool text
					fnRollOver.toolText = this.vTrendLines[i].toolText;
					//Assing the delegates to movie clip handler
					trendLineMC.onRollOver = fnRollOver;
					//Set roll out and mouse move too.
					trendLineMC.onRollOut = trendLineMC.onReleaseOutside=Delegate.create(this, hideToolTip);
					trendLineMC.onMouseMove = Delegate.create(this, repositionToolTip);
				}
				//Apply animation  
				if (this.params.animation) {
					this.styleM.applyAnimation(trendLineMC, this.objects.VTRENDLINES, this.macro, trendLineMC._x, 0, null, 0, 100, 100, 100, null);
				}
				//Apply filters  
				this.styleM.applyFilters(trendLineMC, this.objects.VTRENDLINES);
				//---------------------------------------------------------------------------//
				//If text is to be shown
				if (this.vTrendLines[i].displayValue != "") {
					//Set color
					trendFontObj.color = this.vTrendLines[i].color;
					//Now, render the trend line value
					trendFontObj.align = "center";
					//Create text
					var yPos:Number;
					if(this.params.labelDisplay == "STAGGER"){
						yPos = this.elements.canvas.toY+this.params.labelPadding +(this.params.staggerLines * this.config.maxLabelHeight)
					}else{
						yPos = this.elements.canvas.toY+this.params.labelPadding+this.config.maxLabelHeight
					}
					trendValueObj = createText(false, this.vTrendLines[i].displayValue, this.cMC, valueDepth, this.vTrendLines[i].tbX, yPos, 0, trendFontObj, false, 0, 0);
					//Animation and filter effect
					if (this.params.animation) {
						this.styleM.applyAnimation(trendValueObj.tf, this.objects.TRENDVALUES, this.macro, trendValueObj.tf._x, 0, trendValueObj.tf._y, 0, 100, null, null, null);
					}
					//Apply filters  
					this.styleM.applyFilters(trendValueObj.tf, this.objects.TRENDVALUES);
				}
				//Increment depths  
				lineDepth++;
				valueDepth++;
			}
		}
		delete trendLineMC;
		delete trendValueObj;
		delete trendFontObj;
		//Clear interval
		clearInterval(this.config.intervals.vTrend);
	}
   /**
	* drawNumericVLines method draws the vertical axis lines on the basis of numeric value.
	*/
	
	private function drawNumericVLines():Void 
	{		
		var depth:Number = this.dm.getDepth("NUMERICVLINES") ;
		//Movie clip container
		var nvLineMC:MovieClip =  this.cMC.createEmptyMovieClip("numericVLineMC", depth);
		//Children Movie Clip
		var childNVLineMC:MovieClip;
		
		//Loop var
		var i:Number;
		var xPos:Number;
		
		//Iterate through all the vdiv lines
		for (i=0; i<this.vDivLines.length; i++) 
		{		
			if ((i == 0) || (i == this.vDivLines.length-1))
			{
				// code
			}
			else if (this.vDivLines[i].value == 0) 
			{
				
				if (this.params.showVZeroPlane) 
				{					
					//Depth for zero plane
					var zpDepth:Number = this.dm.getDepth("VZEROPLANE");
					//Get x position
					xPos = this.getAxisPosition(this.vDivLines[i].value, this.config.xMax, this.config.xMin, this.elements.canvas.x, this.elements.canvas.toX, false, 0);
					
					// when xAxisLabelMode is mixed and numeric and category value overlap with each other then category value will get the priority 
					if(this.params.xAxisLabelMode == "mixed" && checkValueOf("x", "catLineValues", this.categories, xPos, "notDefine"))
					{
						continue;
					}
					
					//Render the line
					var vZeroPlaneMC = this.cMC.createEmptyMovieClip("vZeroPlane", zpDepth);
					//Draw the line								  
					vZeroPlaneMC.lineStyle(this.params.vZeroPlaneThickness, parseInt(this.params.vZeroPlaneColor, 16), this.params.vZeroPlaneAlpha);
					if (this.params.vDivLineIsDashed) {
						//Dashed line
						DrawingExt.dashTo(vZeroPlaneMC, 0, 0, 0, -this.elements.canvas.h, this.params.vDivLineDashLen, this.params.vDivLineDashGap);
					} else {
						//Draw normal line line keeping 0,0 as registration point
						vZeroPlaneMC.moveTo(0, 0);
						vZeroPlaneMC.lineTo(0, -this.elements.canvas.h);					
					}					
					//Re-position line
					vZeroPlaneMC._x = xPos - (this.params.vDivLineThickness/2);
					vZeroPlaneMC._y = this.elements.canvas.toY;
					
					//Apply animation
					if (this.params.animation) {
						this.styleM.applyAnimation(vZeroPlaneMC, this.objects.VDIVLINES, this.macro, vZeroPlaneMC._x, 0, vZeroPlaneMC._y, 0, 100, null, 100, null);
					}
					//Apply filters  
					this.styleM.applyFilters(vZeroPlaneMC, this.objects.VDIVLINES);					
				}
			}			
			else
			{				
				
				//Get x position
				xPos = this.getAxisPosition(this.vDivLines[i].value, this.config.xMax, this.config.xMin, this.elements.canvas.x, this.elements.canvas.toX, false, 0);
				// when xAxisLabelMode is mixed and numeric and category value overlap with each other then category value will get the priority 
				if(this.params.xAxisLabelMode == "mixed" && checkValueOf("x", "catLineValues", this.categories, xPos, "notDefine"))
				{					
					continue;
				}
				//If we've to show the line, create a movie clip
				childNVLineMC = nvLineMC.createEmptyMovieClip("numericvLine_"+i, i);
				//Just draw line
				childNVLineMC.lineStyle(this.params.vDivLineThickness, parseInt(this.params.vDivLineColor, 16), this.params.vDivLineAlpha);
				//Now, if dashed line is to be drawn				
				if (this.params.vDivLineIsDashed) {
					//Dashed Line line
					DrawingExt.dashTo(childNVLineMC, 0, 0, 0, -this.elements.canvas.h, this.params.vDivLineDashLen, this.params.vDivLineDashGap);
					
				} else {
					//Draw the line keeping 0,0 as registration point
					childNVLineMC.moveTo(0, 0);
					childNVLineMC.lineTo(0, -this.elements.canvas.h);
					
				}				
				//Re-position line
				childNVLineMC._x = xPos - (this.params.vDivLineThickness/2);
				childNVLineMC._y = this.elements.canvas.toY;
				//Apply animation
				if (this.params.animation) {
					this.styleM.applyAnimation(childNVLineMC, this.objects.VDIVLINES, this.macro, childNVLineMC._x, 0, childNVLineMC._y, 0, 100, null, 100, null);
				}
				//Apply filters  
				this.styleM.applyFilters(childNVLineMC, this.objects.VDIVLINES);
			}	
		}	
		
		delete childNVLineMC;		
		delete nvLineMC;
		//Clear interval
		clearInterval(this.config.intervals.nVLine);
	}	
	/**
	* drawVLines method draws the vertical axis lines on the chart
	*/
	private function drawVLines():Void {
		var depth:Number = this.dm.getDepth("VLINES");
		//Movie clip container
		var vLineMC:MovieClip = this.cMC.createEmptyMovieClip("vLineMC", depth);
		// Child movie clip of vLineMC movie clip
		var childVLineMC:MovieClip;
		
		//Loop var
		var i:Number;
		//Iterate through all the v div lines
		for (i=1; i<=this.numCat; i++) {
			if (this.categories[i].isValid && this.categories[i].showLine == true) {
				//If we've to show the line, create a movie clip
				childVLineMC = vLineMC.createEmptyMovieClip("vLine_"+i, i);
				//Just draw line
				childVLineMC.lineStyle(this.params.catVerticalLineThickness, parseInt(this.params.catVerticalLineColor, 16), this.params.catVerticalLineAlpha);
				//Now, if dashed line is not to be drawn
				if (!this.categories[i].lineDashed) {
					//Draw the line keeping 0,0 as registration point
					childVLineMC.moveTo(0, 0);
					childVLineMC.lineTo(0, -this.elements.canvas.h);
				} else {
					//Dashed Line 
					DrawingExt.dashTo(childVLineMC, 0, 0, 0, -this.elements.canvas.h, this.params.catVerticalLineDashLen, this.params.catVerticalLineDashGap);
				}				
				//Re-position line
				childVLineMC._x = this.categories[i].x;
				childVLineMC._y = this.elements.canvas.toY;
				
				//Apply animation
				if (this.params.animation) {		
					this.styleM.applyAnimation(childVLineMC, this.objects.VLINES, this.macro, childVLineMC._x, 0, childVLineMC._y, 0, 100, null, 100, null);
				}
				//Apply filters  
				this.styleM.applyFilters(childVLineMC, this.objects.VLINES);			
			}
		}
		delete childVLineMC;
		//Clear interval
		clearInterval(this.config.intervals.vLine);
	}
	/**
	* drawDivLines method draws the div lines on the chart
	*/
	private function drawDivLines():Void {
		var divLineValueObj:Object;
		var divLineFontObj:Object;
		var yPos:Number;
		var depth:Number = this.dm.getDepth("DIVLINES");
		//Movie clip container
		var divLineMCContainer:MovieClip = this.cMC.createEmptyMovieClip("divLineMC", depth);
		//Children Movie Clip
		var divLineMC:MovieClip;
		//Get div line font
		divLineFontObj = this.styleM.getTextStyle(this.objects.YAXISVALUES);
		//Set alignment
		divLineFontObj.align = "right";
		divLineFontObj.vAlign = "middle";
		//Iterate through all the div line values
		var i:Number;
		for (i=0; i<this.divLines.length; i++) {
			//If it's the first or last div Line (limits), and limits are to be shown
			if ((i == 0) || (i == this.divLines.length-1)) {
				if (this.params.showLimits && this.divLines[i].showValue) {
					depth++;
					//Get y position for textbox
					yPos = this.getAxisPosition(this.divLines[i].value, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
					//Create the limits text
					divLineValueObj = createText(false, this.divLines[i].displayValue, this.cMC, depth, this.elements.canvas.x-this.params.yAxisValuesPadding, yPos, 0, divLineFontObj, false, 0, 0);
				}
			} else if (this.divLines[i].value == 0) {
				//It's a zero value div line - check if we've to show
				if (this.params.showZeroPlane) {
					//Depth for zero plane
					var zpDepth:Number = this.dm.getDepth("ZEROPLANE");
					//Depth for zero plane value
					var zpVDepth:Number = zpDepth++;
					//Get y position
					yPos = this.getAxisPosition(0, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
					//Render the line
					var zeroPlaneMC = this.cMC.createEmptyMovieClip("ZeroPlane", zpDepth);
					//Draw the line
					zeroPlaneMC.lineStyle(this.params.zeroPlaneThickness, parseInt(this.params.zeroPlaneColor, 16), this.params.zeroPlaneAlpha);
					if (this.params.divLineIsDashed) {
						var len:Number = this.params.divLineDashLen * this.params.zeroPlaneThickness;
						var gap:Number = this.params.divLineDashGap * this.params.zeroPlaneThickness;
						//Dashed line
						DrawingExt.dashTo(zeroPlaneMC, -this.elements.canvas.w/2, 0, this.elements.canvas.w/2, 0, len, gap);
					} else {
						//Draw the line keeping 0,0 as registration point
						zeroPlaneMC.moveTo(-this.elements.canvas.w/2, 0);
						//Normal line
						zeroPlaneMC.lineTo(this.elements.canvas.w/2, 0);
					}
					//Re-position the div line to required place
					zeroPlaneMC._x = this.elements.canvas.x+this.elements.canvas.w/2;
					zeroPlaneMC._y = yPos-(this.params.zeroPlaneThickness/2);
					//Apply animation and filter effects to div line
					//Apply animation
					if (this.params.animation) {
						this.styleM.applyAnimation(zeroPlaneMC, this.objects.DIVLINES, this.macro, null, 0, zeroPlaneMC._y, 0, 100, 100, 100, null);
					}
					//Apply filters  
					this.styleM.applyFilters(zeroPlaneMC, this.objects.DIVLINES);
					//So, check if we've to show div line values
					if (this.params.showDivLineValues && this.divLines[i].showValue) {
						//Create the text
						divLineValueObj = createText(false, this.divLines[i].displayValue, this.cMC, zpVDepth, this.elements.canvas.x-this.params.yAxisValuesPadding, yPos, 0, divLineFontObj, false, 0, 0);
					}
					//Apply animation and filter effects to div line (y-axis) values  
					if (this.divLines[i].showValue) {
						if (this.params.animation) {
							this.styleM.applyAnimation(divLineValueObj.tf, this.objects.YAXISVALUES, this.macro, this.elements.canvas.x-this.params.yAxisValuesPadding-divLineValueObj.width, 0, yPos-(divLineValueObj.height/2), 0, 100, null, null, null);
						}
						//Apply filters  
						this.styleM.applyFilters(divLineValueObj.tf, this.objects.YAXISVALUES);
					}
				}
			} else {
				//It's a div interval - div line
				//Get y position
				yPos = this.getAxisPosition(this.divLines[i].value, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
				//Render the line
				depth++;
				divLineMC = divLineMCContainer.createEmptyMovieClip("DivLine_"+i, depth);
				//Draw the line
				divLineMC.lineStyle(this.params.divLineThickness, parseInt(this.params.divLineColor, 16), this.params.divLineAlpha);
				if (this.params.divLineIsDashed) {
					//Dashed line
					DrawingExt.dashTo(divLineMC, -this.elements.canvas.w/2, 0, this.elements.canvas.w/2, 0, this.params.divLineDashLen, this.params.divLineDashGap);
				} else {
					//Draw the line keeping 0,0 as registration point
					divLineMC.moveTo(-this.elements.canvas.w/2, 0);
					//Normal line
					divLineMC.lineTo(this.elements.canvas.w/2, 0);
				}
				//Re-position the div line to required place
				divLineMC._x = this.elements.canvas.x+this.elements.canvas.w/2;
				divLineMC._y = yPos-(this.params.divLineThickness/2);
				//Apply animation and filter effects to div line
				//Apply animation
				if (this.params.animation) {
					this.styleM.applyAnimation(divLineMC, this.objects.DIVLINES, this.macro, null, 0, divLineMC._y, 0, 100, 100, null, null);
				}
				//Apply filters  
				this.styleM.applyFilters(divLineMC, this.objects.DIVLINES);
				//So, check if we've to show div line values
				if (this.params.showDivLineValues && this.divLines[i].showValue) {
					//Increase Depth
					depth++;
					//Create the text
					divLineValueObj = createText(false, this.divLines[i].displayValue, this.cMC, depth, this.elements.canvas.x-this.params.yAxisValuesPadding, yPos, 0, divLineFontObj, false, 0, 0);
				}
			}
			//Apply animation and filter effects to div line (y-axis) values
			if (this.divLines[i].showValue) {
				if (this.params.animation) {
					this.styleM.applyAnimation(divLineValueObj.tf, this.objects.YAXISVALUES, this.macro, this.elements.canvas.x-this.params.yAxisValuesPadding-divLineValueObj.width, 0, yPos-(divLineValueObj.height/2), 0, 100, null, null, null);
				}
				//Apply filters  
				this.styleM.applyFilters(divLineValueObj.tf, this.objects.YAXISVALUES);
			}
		}
		delete divLineValueObj;
		delete divLineFontObj;
		//Clear interval
		clearInterval(this.config.intervals.divLines);
	}
	
	/**
	* hideNumericLabels hides those auto numeric labels, in mixed mode, which overlaps 
	* with any category label.
	*/
	private function hideNumericLabels(labelArr:Array){
		var i:Number = 0;
		var j:Number = 0;
		var totalLabels:Number = labelArr.length;
		//we sort all the labels according to its x pos
		labelArr.sortOn("xPos", Array.NUMERIC);
		for(i = 0; i < totalLabels; i++){
			//get the object
			var lblObj:Object = labelArr[i]
			//We know that only category labels may overlap with each other or may 
			//overlap with other numeric labels.
			//Even if two numeric labels overlap with each other we do not manage them
			if(lblObj.ObjectType == 'DATALABELS'){
				var amountOfOverlapInLeft:Number = 0;
				var amountOfOverlapInRight:Number = 0;
				
				var labelStartX:Number = lblObj.xPos - (lblObj.width / 2);
				var labelEndX:Number = lblObj.xPos + (lblObj.width / 2);
				//check previous labels
				for(j = (i - 1); j >= 0; j--){
					var prevLblObj:Object = labelArr[j];
					var prevLblEndX:Number = prevLblObj.xPos + prevLblObj.width/2;
					//now this label could be auto numeric or category
					//if this is an auto numeric and overlapping with the category label - we hide it
					//due to STAGGER MODE we also need to check whether they exist on the same y pos
					if(prevLblObj.ObjectType == "XAXISVALUES"){
						if(labelStartX <= prevLblEndX && lblObj.yPos == prevLblObj.yPos){
							//hide this.
							prevLblObj.tf._visible = false;
						}else{
							//No Overlapping. we do not need to hide any further.
							break;
						}
					}else{
						//we can not hide any category labels. and as we alreay have taken care of 
						//overlapping in category labels we can terminate further search.
						break;
					}
				}
				//check next
				for(j = (i + 1); j<totalLabels; j++){
					var nextLblObj:Object = labelArr[j];
					var nextLblStartX:Number = nextLblObj.xPos - nextLblObj.width/2;
					if(nextLblObj.ObjectType == "XAXISVALUES"){
						if(labelEndX > nextLblStartX && lblObj.yPos == nextLblObj.yPos){
							//hide this.
							nextLblObj.tf._visible = false;
						}else{
							//No Overlapping. we do not need to hide any further.
							break;
						}
					}else{
						//we can not hide any category labels. terminate here.
						break;
					}
				}
			}
		}
	}
	
	
	/**
	* drawHGrid method draws the horizontal grid background color
	*/
	private function drawHGrid():Void {
		//If we're required to draw horizontal grid color
		//and numDivLines > 3
		if (this.params.showAlternateHGridColor && this.divLines.length>3) {
			
			//Get depth
			var depth:Number = this.dm.getDepth("HGRID");
			//Movie clip container
			var gridMC:MovieClip;
			
			var hGridMC:MovieClip = this.cMC.createEmptyMovieClip ("hGridMC", depth);
			//Loop variable
			var i:Number;			
			//Y Position
			var yPos:Number, yPosEnd:Number;
			var height:Number;
			for (i=1; i<this.divLines.length-1; i=i+2) {
				//Get y position
				yPos = this.getAxisPosition(this.divLines[i].value, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
				yPosEnd = this.getAxisPosition(this.divLines[i+1].value, this.config.yMax, this.config.yMin, this.elements.canvas.y, this.elements.canvas.toY, true, 0);
				height = yPos-yPosEnd;
				//Create the movie clip
				gridMC = hGridMC.createEmptyMovieClip("GridBg_"+i, i);;
				//Set line style to null
				gridMC.lineStyle();
				//Set fill color
				gridMC.moveTo(-(this.elements.canvas.w/2), -(height/2));
				gridMC.beginFill(parseInt(this.params.alternateHGridColor, 16), this.params.alternateHGridAlpha);
				//Draw rectangle
				gridMC.lineTo(this.elements.canvas.w/2, -(height/2));
				gridMC.lineTo(this.elements.canvas.w/2, height/2);
				gridMC.lineTo(-(this.elements.canvas.w/2), height/2);
				gridMC.lineTo(-(this.elements.canvas.w/2), -(height/2));
				//End Fill
				gridMC.endFill();
				//Place it in right location
				gridMC._x = this.elements.canvas.x+this.elements.canvas.w/2;
				gridMC._y = yPos-(height)/2;
				//Apply animation
				if (this.params.animation) {
					this.styleM.applyAnimation(gridMC, this.objects.HGRID, this.macro, null, 0, gridMC._y, 0, 100, 100, 100, null);
				}
				//Apply filters  
				this.styleM.applyFilters(gridMC, this.objects.HGRID);				
			}
		}
		//Clear interval  
		clearInterval(this.config.intervals.hGrid);		
	}
	
	/**
	* drawVGrid method draws the horizontal grid background color
	*/
	private function drawVGrid () : Void 
	{
		//If required to draw vertical grid color
		//and numVDivLines > 3
		if (this.params.showAlternateVGridColor && this.vDivLines.length > 3)
		{
			//Movie clip container
			var gridMC : MovieClip;
			//Loop variable
			var i : Number;
			//Get depth
			var depth : Number = this.dm.getDepth ("VGRID");
			//X Position
			var xPos : Number, xPosEnd : Number;
			var width : Number;
			
			var vGridMC:MovieClip = this.cMC.createEmptyMovieClip ("vGridMC", depth);
					
			for (i = 1; i < this.vDivLines.length - 1 ; i = i + 2)
			{					
				//Get x Position
				xPos = this.getAxisPosition(this.vDivLines[i].value, this.config.xMax, this.config.xMin, this.elements.canvas.x, this.elements.canvas.toX, false, 0);
				//Get x end position
				xPosEnd = this.getAxisPosition(this.vDivLines[i+1].value, this.config.xMax, this.config.xMin, this.elements.canvas.x, this.elements.canvas.toX, false, 0);
				width = xPos - xPosEnd;				
				//Create the movie clip
				gridMC = vGridMC.createEmptyMovieClip ("VGridBg_" + i, i);
				//Set line style to null
				gridMC.lineStyle ();
				//Set fill color
				gridMC.moveTo ( - (width / 2) , - (this.elements.canvas.h / 2));
				gridMC.beginFill (parseInt (this.params.alternateVGridColor, 16) , this.params.alternateVGridAlpha);
				//Draw rectangle
				gridMC.lineTo (width / 2, - (this.elements.canvas.h / 2));
				gridMC.lineTo (width / 2, this.elements.canvas.h / 2);
				gridMC.lineTo ( - (width / 2) , this.elements.canvas.h / 2);
				gridMC.lineTo ( - (width / 2) , - (this.elements.canvas.h / 2));
				//End Fill
				gridMC.endFill ();
				//Place it in right location
				gridMC._x = xPos - (width / 2);
				gridMC._y = this.elements.canvas.y + this.elements.canvas.h / 2;
				//Apply animation
				if (this.params.animation)
				{
					this.styleM.applyAnimation (gridMC, this.objects.VGRID, this.macro, gridMC._x, 0, null, 0, 100, 100, 100, null);
				}
				//Apply filters
				this.styleM.applyFilters (gridMC, this.objects.VGRID);				
			}
		}
		//Clear interval
		clearInterval (this.config.intervals.vGrid);		
	}
	/**
	* reInit method re-initializes the chart. This method is basically called
	* when the user changes chart data through JavaScript. In that case, we need
	* to re-initialize the chart, set new XML data and again render.
	*/
	public function reInit():Void {
		//Invoke super class's reInit
		super.reInit();
		//Initialize the number of data elements present
		this.numDS = 0;
		this.num = 0;
		this.numCat = 0;
		//Re-set indexes to 0
		this.numTrendLines = 0;
		this.numTrendLinesBelow = 0;
		this.numVTrendLines = 0;
		//Re-create container arrays
		this.categories = new Array();
		this.dataset = new Array();
		this.divLines = new Array();
		this.trendLines = new Array();
		this.vTrendLines = new Array();
		// Vertical div line holder
		this.vDivLines = new Array();		
		//Destroy the legend
		this.lgnd.destroy ();
		if (this.params.interactiveLegend) {
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
	}
	/**
	* remove method removes the chart by clearing the chart movie clip
	* and removing any listeners.
	*/
	public function remove():Void {
		super.remove();
		//Remove class pertinent objects
		this.lgnd.destroy ();
		if (this.params.interactiveLegend) {
			//Remove listener for legend object.
			this.lgnd.removeEventListener("legendClick", this);
		}
		//Remove legend  
		lgndMC.removeMovieClip();
	}
	
	/**
	 * checkValueOf method checks given value is present in given array or not.
	 * 
	 * @param prop1       properties name of given array(collection of object)
	 * @param prop2       properties name of given array(for second check)
	 * @param inObject   an array of group object
	 * @param checkValue1 a number which need to check
	 * @param checkValue2 a number which need to check(for second check)
	 *
	 * @return Boolean   if given value is present in given array(collection of object) then it 
	 					 will return true otherwise false
	 */
	public function checkValueOf(prop1:String, 
								 prop2:String, 
								 inObject:Array, 
								 checkValue1:Number, 
								 checkValue2:String):Boolean
	{
		for(var objprop:String in inObject)
		{
			if(inObject[objprop][prop1] == checkValue1)
			{				
				if(inObject[objprop][prop2] != checkValue2)
				{
					return true;
				}
			}			
		}
		return false;
	}
	/**
	 *  Calculate number scale value, number scale unit and configNumberScaleDefined value for different axis, according to given parameter 
	 * 	
	 * 	@param numberScaleValue  Range of the various blocks that constitute the scale.
	 * 	@param numberScaleUnit	 Unit of each block of the scale
	 *	@param formatNumberScale A flag, which decide whether to add K (thousands) and M (millions) to a number after truncating and rounding it or not
	 *
	 * 	@return 				 An object with calculated number scale value, number scale unit and configNumberScaleDefined value. 
	 */
	private function getNumberScales (numberScaleValue:String, numberScaleUnit:String, formatNumberScale:Boolean) : Object
	{
		var nsObj:Object = {};		
		//Check if either has been defined
		if (numberScaleValue.length == 0 || numberScaleUnit.length == 0 || formatNumberScale == false)
		{
			//Set flag to false
			nsObj.configNumberScaleDefined = false;
		} else 
		{
			//Set flag to true
			nsObj.configNumberScaleDefined = true;
			//Split the data into arrays
			nsObj.nsv = new Array ();
			nsObj.nsu = new Array ();
			//Parse the number scale value
			nsObj.nsv = numberScaleValue.split (",");
			//Convert all number scale values to numbers as they're
			//currently in string format.
			var i : Number;
			for (i = 0; i < nsObj.nsv.length; i ++)
			{
				
				nsObj.nsv [i] = Number (nsObj.nsv [i]);
				
				//If any of numbers are NaN, set defined to false
				if (isNaN (nsObj.nsv [i]))
				{
					nsObj.configNumberScaleDefined = false;
				}
			}
			//Parse the number scale unit
			nsObj.nsu = numberScaleUnit.split (",");
			//If the length of two arrays do not match, set defined to false.
			if (nsObj.nsu.length != nsObj.nsv.length)
			{
				nsObj.configNumberScaleDefined = false;
			}
		}		
		return nsObj;		
	}
	
	/**
	 * override super class method
	 */
	private function detectNumberScales () : Void
	{
		super.detectNumberScales();		
		//Get proper value for yAxisValueDecimals and xAxisValueDecimals when this.params.decimals value already defined
		this.params.yAxisValueDecimals = Number (getFV (this.params.yAxisValueDecimals, this.params.decimals));
		this.params.xAxisValueDecimals = Number (getFV (this.params.xAxisValueDecimals, this.params.decimals));
	}
	/**
	* formatNumber method helps format a number as per the user
	* requirements.
	* Requires this.config.numberScaleDefined to be defined (boolean)
	*	@param		intNum						Number to be formatted
	*	@param		bFormatNumber				Flag whether we've to format
	*											decimals and add commas
	*	@param		decimalPrecision			Number of decimal places we need to
	*											round the number to.
	*	@param		forceDecimals				Whether we force decimal padding.
	*	@param		bFormatNumberScale			Flag whether we've to format number
	*											scale
	*	@param		defaultNumberScale			Default scale of the number provided.
	*   @param		configNumberScaleDefined	Flag whether numberScale defined or not
	*	@param		numScaleValues				Array of values (for scaling)
	*	@param		numScaleUnits				Array of Units (for scaling)
	*	@param		numberPrefix				Number prefix to be added to number.
	*	@param		numberSuffix				Number Suffix to be added.
	*	@return									Formatted number as string.
	*
	*/
	private function formatNumber (intNum : Number, 
									bFormatNumber : Boolean, 
									decimalPrecision : Number, 
									forceDecimals : Boolean, 
									bFormatNumberScale : Boolean, 
									defaultNumberScale : String, 
									configNumberScaleDefined:Boolean,
									numScaleValues : Array, 
									numScaleUnits : Array, 
									numberPrefix : String, 
									numberSuffix : String) : String 
	{
		
		//First, if number is to be scaled, scale it
		//Number in String format
		var strNum : String = String (intNum);
		//Number Scale
		var strScale : String
		if (bFormatNumberScale)
		{
			strScale = defaultNumberScale;
		}else
		{
			strScale = "";
		}
		
		if (bFormatNumberScale && configNumberScaleDefined)
		{			
			//Get the formatted scale and number
			var objNum : Object = formatNumberScale (intNum, defaultNumberScale, numScaleValues, numScaleUnits);
			//Store from return in local primitive variables
			strNum = String (objNum.value);
			intNum = objNum.value;
			strScale = objNum.scale;
		}
		//Now, if we've to format the decimals and commas
		if (bFormatNumber)
		{
			//Format decimals
			strNum = formatDecimals (intNum, decimalPrecision, forceDecimals);
			//Format commas now
			strNum = formatCommas (strNum);
		}
		//Now, add scale, number prefix and suffix
		strNum = numberPrefix + strNum + strScale + numberSuffix;
		return strNum;
	}
	
	/**
	* getLimits is the common function that determines the space available in left and right of any category label.
	* 	@startIndex		The start index no of the category array.
	* 	@totItems		The total number of categories.
	*
	*	@returns 		Object containing the left and right space.
	*/
	private function getLimits(startIndex:Number, totItems:Number){
		var i:Number = startIndex;
		var totalItems:Number = totItems;
		var j:Number, leftLimit:Number, rightLimit:Number, catObj:Object, lbl:String;
		//lowest and highest limit for data labels
		var minXPos:Number = this.params.chartLeftMargin + getFN (this.elements.yAxisName.w , 0);
		var maxXPos:Number = this.width - this.params.chartRightMargin;
		if(this.params.legendPosition.toLowerCase() == "right" && this.params.showLegend ){
			maxXPos -= getFN (this.elements.legend.w, 0);
		}
		//traverse to previous
		if(i == 0){
			leftLimit = minXPos;
		}else{
			for(j = (i-1); j >= 0; j--){
				catObj = this.categories[j];
				lbl = catObj.label;
				if(catObj.showLabel && lbl.length > 0 && !isNaN(catObj.x)){
					if(this.params.labelDisplay == "STAGGER"){
						leftLimit = catObj.x;
					}else{
						leftLimit = catObj.x + getFN(catObj.labelObj.width / 2, 0);
					}
					break;
				}
				//if the end is reached we assign the min limit
				if(j == 0){
					leftLimit = minXPos;
				}
			}
		}
		//traverse to nexts
		if(i == (totalItems - 1)){
			rightLimit = maxXPos;
		}else{
			for(j = (i+1); j < totalItems; j++){
				catObj = this.categories[j];
				lbl = catObj.label;
				if(catObj.showLabel && lbl.length > 0){
					if(this.params.labelDisplay == "STAGGER"){
						rightLimit = catObj.x;
					}else{
						rightLimit = this.categories[i].x + (catObj.x - this.categories[i].x) / 2;
					}
					break;
				}
				//if the end is reached we assign the max limit
				if(j == (totalItems - 1)){
					rightLimit = maxXPos;
				}
			}
		}
		return {leftLimit : leftLimit, rightLimit : rightLimit}
	}
	
}
