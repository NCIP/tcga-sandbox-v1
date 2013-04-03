/**
* @class Axis
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
* Axis class represents a linear or log axis and provides methods
* to calculate the various aspects, but does NOT render.
* Inverse is not handled in axis, as it's a function of rendered. 
* Even in an inverse axis, numerically the lower limit of axis is
* less than upper limit. It's just a change of rendering.
*/
class com.fusioncharts.helper.Axis {
	//Static variables to indicate axis type
	public static var TYPE_LINEAR:Number = 1;
	public static var TYPE_LOG:Number = 2;
	//If no data is provided to the axis, max/min value to scale to
	public static var DEFAULT_MAX:Number = 100;
	public static var DEFAULT_MIN:Number = 0;
	//Instance variables
	//State to store whether axis is in dirty state. We calculate only
	//when the axis is dirty (i.e., anything has changed).
	private var dirty:Boolean;
	//Type of axis - linear or log
	private var type:Number;
	//Cut-off for Maximum intervals. If any interval hint results in 
	//intervals more than this number, it is ignored.
	private var maxIntervals:Number = 20;
	//Storage for calculates intervals and interval hints (user provided/calculated)
	private var intervals:Array, intervalHint:Array, forcedIntervalHints:Boolean;
	//Array to store number of intervals per set, and total count
	private var intervalSetCount:Array, numIntervalSets:Number;
	//Calculated range of axis and internal interval of axis (10^x)
	private var range:Number, interval:Number;
	//Calculated minimum and Maximum (limits) for the axis
	private var min:Number, max:Number;
	//Manually set minimum and maximum (limits) for the axis
	private var forcedMin:Number, forcedMax:Number, forcedLimits:Boolean;
	//Minimum and maximum data value that'll be plotted against the axis.
	private var dataMin:Number, dataMax:Number;
	//Buffered log power of upper and lower log limit w.r.t logBase
	private var logPowerMax:Number, logPowerMin:Number;
	//Number of minor intervals on log-scale between any two major interval
	private var numMinorIntervalsLog:Number;
	//Logarithmic base
	private var logBase:Number;
	//Upper and lower limit configuration for linear axis
	//Whether to restrict the max value at minimum zero
	private var allowNegativeMax:Boolean;
	//Whether to have min as zero
	private var allowPositiveMin:Boolean;
	/**
	 * Constructor.
	*/
	function Axis() {
		//Initialize the parameters
		init();
	}
	/**
	 * Initializes the axis
	*/
	private function init():Void {
		//When intialized, it's dirty
		dirty = true;
		//By default, assume it to be a linear axis
		type = TYPE_LINEAR;
		//Set logarithmic base to 10 by default
		logBase = 10;
		//By default, do not restrict max at 0. Allow it to be negative
		allowNegativeMax = true;
		//By default, set min to 0 or negative
		allowPositiveMin = false;
		//Set forcedLimits to false by default, as we intend to calculate
		forcedLimits = false;
		//Initialize axis interval hint array
		intervalHint = new Array();
		//By default assume that it's forced interval hints
		forcedIntervalHints = true;
	}
	/**
	* Calculates the axis limits (min and max), along with intervals.
	*/
	public function calculate():Void {
		//Calculate only if anything has changed
		if (dirty) {
			//Check whether forced limits or data limits have been provided
			if ((forcedLimits == true && isNaN(forcedMin) == false && isNaN(forcedMax) == false) || (isNaN(dataMin) == false && isNaN(dataMax) == false && dataMin != undefined && dataMax != undefined)) {
				//Calculate limits and intervals, based on axis type.
				//Also, force linear axis if any of the data values are <0 (as log
				//of negative numbers is not real)
				if (type == TYPE_LINEAR || (dataMin<0 || dataMax<0)) {
					//Set axis as linear
					type = TYPE_LINEAR;
					//Calculate limits
					calculateLinearLimits();
					//Now, calculate the axis intervals
					calculateLinearAxisIntervals();
				} else {
					//Calculate limits of log axis
					calculateLogLimits();
					//Calculate intervals
					calculateLogAxisIntervals();
				}
			} else {
				//Neither data values, nor a forced limit is provided.
				//So set axis to default limits
				setForcedLimits(Axis.DEFAULT_MIN, Axis.DEFAULT_MAX);
				//Calculate the rest of parameters
				calculate();
			}
		}
		//Change dirty flag back to normal       
		dirty = false;
	}
	/**
	* Syncs the zero plane position of all the linear axis passed
	* to this method. Any log axis is ignored, as it cannot have 
	* negative numbers. If any of the axis limits have not been
	* calculated, it is first calculated.
	* @param	Reference to all axis instances that need to be synced.
	*/
	public static function syncZeroPlane():Void {
		//Loop var
		var i:Number;
		//Number of axes passed to the function
		var numAxis:Number = 0;
		//Maximum ratio for upper arm
		var maxru:Number = 0;
		//Maximum ratio for lower arm
		var maxrl:Number = 0;
		//Array to store all valid axes
		var axes:Array = new Array();
		//If the axis is dirty, first calculate
		for (i=0; i<arguments.length; i++) {
			//If it's an Axis and a linear axis
			if (arguments[i] instanceof Axis && arguments[i].type == Axis.TYPE_LINEAR) {
				numAxis++;
				//Store the axis reference
				axes[numAxis] = new Object();
				axes[numAxis].axis = arguments[i];
				//Also, if the axis is dirty, calculate values
				if (axes[numAxis].axis.dirty) {
					axes[numAxis].axis.calculate();
				}
				//Copy of Min and Max 
				axes[numAxis].min = axes[numAxis].axis.getMin();
				axes[numAxis].max = axes[numAxis].axis.getMax();
				//Range of axis. Always a positive value.
				axes[numAxis].range = axes[numAxis].max-axes[numAxis].min;
				//Ratio of zero to max (upper arm) to entire axis range. Default 0.
				axes[numAxis].ru = 0;
				//Ratio of zero to min (lower arm) to entire axis range. Default 0.
				axes[numAxis].rl = 0;
				//Calculate the arm ratio, based on axis min/max
				if (axes[numAxis].min<0 && axes[numAxis].max>0) {
					axes[numAxis].ru = axes[numAxis].max/axes[numAxis].range;
					axes[numAxis].rl = Math.abs(axes[numAxis].min/axes[numAxis].range);
				} else if (axes[numAxis].min == 0 && axes[numAxis].max>0) {
					axes[numAxis].ru = 1;
				} else if (axes[numAxis].min<0 && axes[numAxis].max == 0) {
					axes[numAxis].rl = 1;
				} else if (axes[numAxis].min>0 && axes[numAxis].max>0) {
					axes[numAxis].ru = axes[numAxis].max/axes[numAxis].range;
				} else if (axes[numAxis].min<0 && axes[numAxis].min<0) {
					axes[numAxis].rl = Math.abs(axes[numAxis].min/axes[numAxis].range);
				}
				//Compare and store maximum ratios
				maxru = Math.max(maxru, axes[numAxis].ru);
				maxrl = Math.max(maxrl, axes[numAxis].rl);
			}
		}
		//Now, if there are no axes to work with, return
		if (numAxis == 0) {
			return;
		}
		//Else, synchronize axis. To synchronize zero plane of various axis, 
		//we work on ratios. For each axis, we find the ratio of (Max to Zero):(Zero to Min)
		//Then we take the maximum ratio among all axis for (Max to Zero) and for (Zero to Min)
		//as well. Each axis's arm is them multiplied to that max ratio to sync.		
		for (i=1; i<=numAxis; i++) {
			//Calibrate the axis scale for each axis, based on ratios
			if (axes[i].ru>0) {
				axes[i].max = (maxru/axes[i].ru)*axes[i].max;
			}
			if (axes[i].rl>0) {
				axes[i].min = (maxrl/axes[i].rl)*axes[i].min;
			}
			if (axes[i].ru == 0) {
				axes[i].max = -1*(maxru/maxrl)*axes[i].min;
			}
			if (axes[i].rl == 0) {
				axes[i].min = -1*(maxrl/maxru)*axes[i].max;
			}
			//Set forced limits back 
			if (axes[i].min!=axes[i].axis.getMin() || axes[i].max!=axes[i].axis.getMax()){
				//Force the new limits
				axes[i].axis.setForcedLimits(axes[i].min, axes[i].max);
				//Re-calculate intervals
				axes[i].axis.calculate();
			}
		}
	}
	/**
	* Calculates axis limits in case of a linear axis.
	*/
	private function calculateLinearLimits():Void {
		//Calculate the interval for data limits, based on whether we to've work on forced limits
		//First identify which data value we'll have to use for calculation
		var dMin:Number, dMax:Number;
		//Boolean flags to indicate whether the min and max limits have to be calculated.
		var bCalcMin:Boolean, bCalcMax:Boolean;
		//Find the lower range value for calculation
		if (dataMin == undefined || (forcedLimits && isNaN(forcedMin) == false && forcedMin<=dataMin)) {
			//Set min value to be forced min value. This has been already set in calculate(), even
			//if they were not provided by the user.
			dMin = forcedMin;
			//Set flag that minimum is NOT to be calculated.
			//Instead, forcedMin is to be used.
			bCalcMin = false;
		} else {
			//Set min value to be data range min
			dMin = dataMin;
			//Set flag that minimum has to be calculated
			bCalcMin = true;
		}
		//Find the upper range value for calculation
		if (dataMax == undefined || (forcedLimits && isNaN(forcedMax) == false && forcedMax>=dataMax)) {
			//Set max value to be forced max value
			dMax = forcedMax;
			//Set flag that maximum is NOT to be calculated.
			//Instead, forcedMax is to be used.
			bCalcMax = false;
		} else {
			//Set max value to be data range max
			dMax = dataMax;
			//Set flag that maximum has to be calculated
			bCalcMax = true;
		}
		//Calculate the intervals for the selected range
		calculateLinearInterval(dMin, dMax);
		//Now calculate and set the min/max values for axis
		var y_topBound:Number, y_lowerBound:Number;
		//Y-axis upper limit calculation
		if (bCalcMax == true) {
			//Calculate the y-axis upper limit by adding some numerical padding
			y_topBound = (Math.floor(dataMax/interval)+1)*interval;
		} else {
			//Set the forced max value
			y_topBound = forcedMax;
		}
		//Y-axis lower limit calculation
		if (bCalcMin == true) {
			//If the min value is less than 0
			if (dataMin<0) {
				//Then calculate by multiplying negative numbers with y-axis interval
				y_lowerBound = -1*((Math.floor(Math.abs(dataMin/interval))+1)*interval);
			} else {
				//Check if positive min is to be set. If not, simply set it to 0.
				if (allowPositiveMin == false) {
					y_lowerBound = 0;
				} else {
					//Find the apt positive min.
					y_lowerBound = Math.floor(Math.abs(dataMin/interval)-1)*interval;
					//Now, if dataMin>=0, we keep y_lowerBound to 0 - as for values like dataMin 2
					//lower bound goes negative, which is not required.
					y_lowerBound = (y_lowerBound<0) ? 0 : y_lowerBound;
				}
			}
		} else {
			//Set the forced min value
			y_lowerBound = forcedMin;
		}
		//Upper limit cannot be less than 0 if allowNegativeMax is set to false
		if (allowNegativeMax == false && dMax<=0) {
			y_topBound = 0;
		}
		max = y_topBound;
		min = y_lowerBound;
		//Store axis range
		range = Math.abs(max-min);
	}
	/**
	* Calculates axis limits in case of a logarithmic axis.
	*/
	private function calculateLogLimits():Void {
		//Calculate the interval for data limits, based on whether we to've work on forced limits
		//First identify which data value we'll have to use for calculation
		var dMin:Number, dMax:Number;
		//Boolean flags to indicate whether the min and max limits have to be calculated.
		var bCalcMin:Boolean, bCalcMax:Boolean;
		//Find the lower range value for calculation
		if (dataMin == undefined || (forcedLimits && isNaN(forcedMin) == false && forcedMin<=dataMin)) {
			//Set min value to be forced min value. This has been already set in calculate(), even
			//if they were not provided by the user.
			dMin = forcedMin;
			//Set flag that minimum is NOT to be calculated.
			//Instead, forcedMin is to be used.
			bCalcMin = false;
		} else {
			//Set min value to be data range min
			dMin = dataMin;
			//Set flag that minimum has to be calculated
			bCalcMin = true;
		}
		//Find the upper range value for calculation
		if (dataMax == undefined || (forcedLimits && isNaN(forcedMax) == false && forcedMax>=dataMax)) {
			//Set max value to be forced max value
			dMax = forcedMax;
			//Set flag that maximum is NOT to be calculated.
			//Instead, forcedMax is to be used.
			bCalcMax = false;
		} else {
			//Set max value to be data range max
			dMax = dataMax;
			//Set flag that maximum has to be calculated
			bCalcMax = true;
		}
		//Calculate the log power for the selected range
		calculateLogPower(dMin, dMax);
		//Now, if limits are to be calculated.
		if (bCalcMax){
			max = Math.pow(logBase, logPowerMax);
		}else{
			max = forcedMax;
		}
		//Do the same for lower limit
		if (bCalcMin){
			min = Math.pow(logBase, logPowerMin);
		}else{
			min = forcedMin;
		}
	}
	
	/**
	* Calculates and stores the intervals to be added on the log axis.
	*/
	private function calculateLogAxisIntervals():Void{
		//If numMinorIntervalsLog is undefined, then assume a value for the same.
		if (numMinorIntervalsLog==undefined){
			//If the base is a power of 10, we choose 8 intervals, else
			//choose 4 intervals as default.
			//Get log of base w.r.t. base=10
			var logOfBase:Number = Number(String(Math.log(logBase)/Math.log (10)));
			//If logOfBase is integer, then logBase is an integer power of 10. 
			//Select number of default intervals accordingly
			numMinorIntervalsLog = (Math.floor (logOfBase) == logOfBase) ? 8 : 4;
		}
		//Set container
		intervals = new Array();
		numIntervalSets=1;
		//Step through the major intervals. This is done by iterating from
		//logBase^logPowerMin to logBase^logPowerMax and checking all
		//such values that are within the limits (in case of forced limits)
		var i:Number, j:Number;
		var startValue:Number, endValue:Number, multipleVal:Number;
		var intVal:Number, intValNext:Number, intGap:Number, minorInterval:Number, minorIntervalVal:Number;
		//Based on whether logBase>1, or 0<logBase<1, startValue and endValue
		//will be reversed
		if (logBase>1){
			startValue = logPowerMin;
			endValue = logPowerMax;
			multipleVal = 1;
		}else{
			startValue = logPowerMax;
			endValue = logPowerMin;
			multipleVal = -1;
		}
		//Iterate through the values
		for (i=startValue; i<=endValue; i++){
			//Check if interval is within limits
			intVal = Math.pow(logBase, i);
			//Proceed only if interval value is within range
			if (intVal>=min && intVal<=max){
				//Add to intervals array
				intervals.push(returnIntervalAsObject(intVal, true));
				//Calculate the next higher interval
				intValNext = Math.pow(logBase, i + multipleVal);
				//Calculate the interval between this and next
				intGap = intValNext - intVal;
				//Divide this interval gap into numMinorIntervalsLog and add
				//each of such interval as minor interval
				minorIntervalVal = Number(String(intGap/(numMinorIntervalsLog+1)));
				//Add them as minor intervals
				for (j=1; j<=numMinorIntervalsLog; j++){
					minorInterval = Number(String(intVal + (j*minorIntervalVal)));
					//Proceed only if interval value is within range				
					if (minorInterval>=min && minorInterval<=max){
						//Add to intervals array
						intervals.push(returnIntervalAsObject(minorInterval, false));
					}
				}
			}
		}
		//Sort intervals array (ascending).
		intervals.sortOn("position", Array.NUMERIC);
	}
	
	/**
	* Calculates the logarithmic power for min and max value (either data
	* or forced limits provided)
	*/
	private function calculateLogPower(minVal:Number, maxVal:Number):Void {
		//To store log value.
		var logPower:Number;
		//Calculate the log base for max value.
		if (logBase>1) {
			//For base >1.
			logPower = Math.ceil(Math.log(maxVal)/Math.log(logBase));
		} else {
			//For 0 < base < 1
			logPower = Math.floor(Math.log(maxVal)/Math.log(logBase));
		}
		logPowerMax = logPower;
		//Calculate log base for min value.
		if (logBase>1) {
			//For base >1.
			logPower = Math.floor(Math.log(minVal)/Math.log(logBase));
		} else {
			//For 0 < base < 1
			logPower = Math.ceil(Math.log(minVal)/Math.log(logBase));
		}	
		logPowerMin = logPower;
	}

	/**
	* Calculates all the intervals for the axis. Multiple sets of
	* intervals are calculated, either based on interval hint 
	* provided by the user, or the internally adopted interval hints.
	* Multiple sets are then passed to Axis rendered class which
	* decides the set to use, based on space available on the screen.
	* Since all are rounded to the same value as limits, any of them
	* can be used, without rounding required. 
	* To calculate each set, we take each item of interval hint and
	* see if it fits into the range. For each such value from intervalHint
	* that fits, we calculate the number of axis intervals.
	*/
	private function calculateLinearAxisIntervals():Void {
		//Initialize axis intervals array
		intervals = new Array();
		//Initialize interval sets count array
		intervalSetCount = new Array();
		//Initialize counter of total number of calculated interval sets to zero
		numIntervalSets = 0;
		//If the interval hints are not provided or were previously forced, assume default intervals
		if (intervalHint.length == 0 || forcedIntervalHints == true) {
			//Intervals are not specified by user. So push custom intervals
			//based on range interval. Push interval itself and multiples of 2,3,4,5,10,20,25,50,100
			intervalHint = new Array();
			intervalHint.push(interval, interval*2, interval*3, interval*4, interval*5, interval*10, interval*20, interval*25, interval*50, interval*100);
		}
		//Sort in ascending.    
		intervalHint.sort(Array.NUMERIC);
		//Now, for each interval, store the calculate div lines.
		var i:Number;
		for (i=0; i<intervalHint.length; i++) {
			//Check if the interval is less than range and creates divisions less than maxIntervals
			if (range>intervalHint[i] && (range/intervalHint[i]<maxIntervals)) {
				//intervalHint[i] is valid. So calculate the intervals for this and store
				numIntervalSets++;
				//Calculate the interval set
				createLinearIntervalSet(intervalHint[i]);
			}
			//If none of the intervalHint was valid. Then store just min/max/zero*.   
			if (numIntervalSets == 0) {
				numIntervalSets++;
				if (min<0 && max>0) {
					//Push in min/max/zero 
					intervals[3] = new Array();
					intervals[3][0] = returnIntervalAsObject(min, true);
					intervals[3][1] = returnIntervalAsObject(0, true);
					intervals[3][2] = returnIntervalAsObject(max, true);
					intervalSetCount.push(3);
				} else {
					//If there is no zero in between, push in just the min/max
					intervals[2] = new Array();
					intervals[2][0] = returnIntervalAsObject(min, true);
					intervals[2][1] = returnIntervalAsObject(max, true);
					intervalSetCount.push(2);
				}
			}
		}
	}
	/**
	* Creates a set of linear intervals based on the internal division provided.
	* There are four scenarios of axis, each of which entails a different way of
	* distribution of intervals.
	* Case 1: Axis min=0. Axis max=pos/negative. Intervals then start from 0 and 
	*		  keep adding/substracting, till both extremes have been reached.
	* Case 2: Both axis min and max are either positive or negative. In this case,
	*		  intervals are calculated as integer multiplication of interval hints.
	* Case 3: Axis min is negative, and axis max is positive. Then intervals start
	*		  from 0 and keep adding/substracting till both extremes have reached.
	* @param	intervalDiv		Interval division to be used.
	*/
	private function createLinearIntervalSet(intervalDiv:Number):Void {
		//Count of intervals possible using this interval division
		var count:Number = 0, i:Number = 0, intVal:Number = 0;
		//Calculate numIntervals. Add 2 to possibly include both limits.
		var numInterval:Number = Math.floor(range/intervalDiv)+2;
		//Local scope array for storing intervals
		var ints:Array = new Array();
		//Calculate intervals based on the axis min and max
		if ((min>0 && max>0) || (min<0 && max<0)) {
			//If both min and max are either positive or negative.
			//Then we base the intervals as integer multiples of interval div.
			var baseMultiple:Number = min/intervalDiv;
			//If the min value is not an exact multiple of interval div, increment
			if (Math.floor(baseMultiple) != baseMultiple) {
				baseMultiple = Math.floor(baseMultiple)+1;
			}
			//Iterate through each interval and check   
			for (i=1; i<=numInterval; i++) {
				intVal = baseMultiple*intervalDiv;
				baseMultiple++;
				//Check with limits
				if (intVal<=max) {
					//Increase count
					count++;
					//Add it to local array
					ints.push(intVal);
				} else {
					break;
				}
			}
		} else if (min == 0) {
			//If axis min is 0, then start from min and keep adding intervalDiv
			//Iterate through each interval and check
			for (i=0; i<=numInterval; i++) {
				intVal = i*intervalDiv;
				//Check with limits
				if (intVal<=max) {
					//Increase count
					count++;
					//Add it to local array
					ints.push(intVal);
				} else {
					break;
				}
			}
		} else if (max == 0) {
			//If axis max is 0, start from max and keep deducting intervalDiv
			//Iterate through each interval and check
			for (i=0; i<=numInterval; i++) {
				intVal = -1*i*intervalDiv;
				//Check with limits
				if (intVal>=min) {
					//Increase count
					count++;
					//Add it to local array
					ints.push(intVal);
				} else {
					break;
				}
			}
		} else if (max>0 && min<0) {
			//If the min value is negative, and max value positive. 
			//Probe in both directions till limits are reached.
			//First from 0(inclusive) to max
			for (i=0; i<=numInterval; i++) {
				intVal = i*intervalDiv;
				//Check with limits
				if (intVal<=max) {
					//Increase count
					count++;
					//Add it to local array
					ints.push(intVal);
				} else {
					break;
				}
			}
			//Now from 0(exclusive) to min
			for (i=1; i<=numInterval; i++) {
				intVal = -1*i*intervalDiv;
				//Check with limits
				if (intVal>=min) {
					//Increase count
					count++;
					//Add it to local array
					ints.push(intVal);
				} else {
					break;
				}
			}
		}
		//Sort the local array in ascending order   
		ints.sort(Array.NUMERIC);
		//Now, create interval object for each interval and store in final array.
		//Create a new nested array. Note: We're allowing over-rides by a larger
		//intervalDiv value (that gets called later), as a larger value with same
		//number of interval would cover more range. (E.g., if a div interval of 12
		//generates 4 intervals, and so does 13, we use 13, as it'll then cover 4 
		//points more).
		intervals[count] = new Array();
		//Feed all intervals as objects
		for (i=0; i<count; i++) {
			//Convert to interval object, and mark as major interval.
			intervals[count][i] = returnIntervalAsObject(ints[i], true);
		}
		//Push in intervalSetCount the number of interval items in this set
		intervalSetCount.push(count);
	}
	/**
	* Creates an object for each interval and returns it.
	*  @param	position	Numeric position of the interval on axi
	*  @param	isMajor		Whether the interval is a major interval
	*  @return				Object containing interval properties
	*/
	private function returnIntervalAsObject(position:Number, isMajor:Boolean):Object {
		var interval:Object = new Object();
		//Store propeties
		interval.position = position;
		interval.isMajor = isMajor;
		//Return 
		return interval;
	}
	/**
	* Calculates the interval provided for the data values.
	* @param	minVal	Minimum value in the range.
	* @param	maxVal	Maximum value of the range.
	*/
	private function calculateLinearInterval(minVal:Number, maxVal:Number):Void {
		//Get the maximum power of 10 that is applicable to maxVal
		//Number = 10 to the power maxPowerOfTen + x (where x is another number)
		//For e.g., in 99 the maxPowerOfTen will be 1 = 10^1 + 89
		//And for 102, it will be 2 = 10^2 + 2
		var maxPowerOfTen:Number = Math.floor(Math.log(Math.abs(maxVal))/Math.LN10);
		//Get the maximum power of 10 that is applicable to minVal
		var minPowerOfTen:Number = Math.floor(Math.log(Math.abs(minVal))/Math.LN10);
		//Find which powerOfTen (the max power or the min power) is bigger
		//It is this which will be multiplied to get the y-interval
		var powerOfTen:Number = Math.max(minPowerOfTen, maxPowerOfTen);
		var y_interval:Number = Math.pow(10, powerOfTen);
		//Stage 1 tuning.
		//For accomodating smaller range values (so that scale doesn't have large interval)
		//Example: if min and max values are 32 & 120, then the interval comes to 100,
		//thereby allowing only 1 interval in between and setting max to 200. 
		//This allows the max to be more in sync with actul data. So the new min, max becomes 0,130
		if (Math.abs(maxVal)/y_interval<2 && Math.abs(minVal)/y_interval<2) {
			powerOfTen--;
			y_interval = Math.pow(10, powerOfTen);
		}
		//Stage 2 tuning. More applicable when allowPositiveMin is true.    
		//As this is valid when the range difference is low (e.g., 1281000 & 1282000), but 
		//difference between low and min is high. 
		if (allowPositiveMin == true) {
			//If the y_interval of min and max is way more than that of range.
			//We need to reset the y-interval as per range
			var rangePowerOfTen:Number = Math.floor(Math.log(maxVal-minVal)/Math.LN10);
			var rangeInterval:Number = Math.pow(10, rangePowerOfTen);
			//Now, if rangeInterval is 10 times less than y_interval, we need to re-set
			//the limits, as the range is too less to adjust the axis for max,min.
			//We do this only if range is greater than 0 (in case of 1 data on chart).
			if (((maxVal-minVal)>0) && ((y_interval/rangeInterval)>=10)) {
				y_interval = rangeInterval;
				powerOfTen = rangePowerOfTen;
			}
		}
		//Store interval      
		interval = y_interval;
	}
	//----------- BEGIN: GETTERS AND SETTERS --------------//
	/**
	* Sets the default max value for the axis, in case no data is provided.
	* The axis then assumes this as the max value.
	*/
	public static function setDefaultMax(dataMax:Number):Void {
		Axis.DEFAULT_MAX = dataMax;
	}
	/**
	* Sets the default min value for the axis, in case no data is provided.
	* The axis then assumes this as the min value.
	*/
	public static function setDefaultMin(dataMin:Number):Void {
		Axis.DEFAULT_MIN = dataMin;
	}
	/**
	* Sets the maximum number of intervals that are allowed for the axis. Works
	* only for linear axis.
	*/
	public function setMaxIntervals(maxIntervals:Number):Void {
		if (this.maxIntervals != maxIntervals) {
			this.maxIntervals = maxIntervals;
			dirty = true;
		}
	}
	/**
	 * Sets the type of this axis. If valid and different, it also
	 * re-calculates the intervals and other related parameters.
	*/
	public function setType(axisType:Number):Void {
		if (type != axisType && (axisType == TYPE_LINEAR || axisType == TYPE_LOG)) {
			//Store new axis type
			type = axisType;
			//Set flag that the axis is dirty. External invocation of calcuate() will
			//then recompute axis
			dirty = true;
		}
	}
	/**
	* Sets the hints for axis intervals. Works only for linear axis.
	* @param	strHints	Comma separated values for axis interval
	*/
	public function setIntervalHints(strHints:String):Void {
		//If it has any hints
		if (strHints.length>0) {
			var hints:Array = strHints.split(",");
			var i:Number, count:Number = 0;
			//Re-initialize array
			intervalHint = new Array();
			for (i=0; i<hints.length; i++) {
				//Push it into the intervalHint array, if it's a number
				if (isNaN(Number(hints[i])) == false) {
					//Store
					intervalHint[count] = Number(hints[i]);
					count++;
				}
			}
			//Sort the array in ascending order
			intervalHint.sort(Array.NUMERIC);
			//Set flag that it's not forced interval hints
			forcedIntervalHints = false;
		}
		//Set flag that axis is dirty    
		dirty = true;
	}
	/**
	* Sets the base of logarithmic scale, in case of a linear chart.
	*/
	public function setLogBase(base:Number):Void {
		//If different and valid, store
		if (base>0 && base != 1 && logBase != base) {
			logBase = base;
			//Set flag that the axis is dirty. External invocation of calcuate() will
			//then recompute axis
			dirty = true;
		}
	}
	/**
	* Sets forced limits for the axis. If these limits are lesser than the 
	* data min and max values, they're ignored later.
	*/
	public function setForcedLimits(axisMin:Number, axisMax:Number):Void {
		//Store
		forcedMin = axisMin;
		forcedMax = axisMax;
		//Update flags
		forcedLimits = true;
		dirty = true;
	}
	/**
	 * Clears the forced limits earlier provided.
	*/
	public function clearForcedLimits():Void {
		//Clear limits
		delete forcedMin;
		delete forcedMax;
		//Reset flags
		forcedLimits = false;
		dirty = true;
	}
	
	/**
	* Sets the number of minor intervals to be added, when calculating
	* the log-axis.
	*/
	public function setNumMinorIntervalsLog(num:Number):Void{
		//Store
		if (numMinorIntervalsLog!=num){
			numMinorIntervalsLog = num;
			dirty=true;
		}
	}
	/**
	* Sets the data limits for axis (i.e., value of minimum and maximum data
	* that will be plotted on this axis).
	* @param	minData		Minimum value among all data points that will be plotted
	*						against the axis.
	* @param	maxData		Maximum value among all data points that will be plotted
	*						against the axis.
	*/
	public function setDataLimits(minData:Number, maxData:Number):Void {
		if (dataMin != minData || dataMax != maxData) {
			//Store
			dataMin = minData;
			dataMax = maxData;
			//Swap if dataMax<dataMin
			if (dataMax<dataMin) {
				var tmp:Number = dataMax;
				dataMax = dataMin;
				dataMin = tmp;
			}
			//If both values are 0, set max to 1. This is to avoid infinity NaN errors.    
			if (dataMin == 0 && dataMax == 0) {
				dataMax = 1;
			}
			//Set flag that the axis is dirty. External invocation of calcuate() will      
			//then recompute axis
			dirty = true;
		}
	}
	/**
	 * Sets parameters for linear axis.
	*/
	public function setLinearAxisParams(allowNegativeMax:Boolean, allowPositiveMin:Boolean):Void {
		//If different store
		if (this.allowNegativeMax != allowNegativeMax) {
			this.allowNegativeMax = allowNegativeMax;
			//Set flag that the axis is dirty. External invocation of calcuate() will
			//then recompute axis
			dirty = true;
		}
		if (this.allowPositiveMin != allowPositiveMin) {
			this.allowPositiveMin = allowPositiveMin;
			//Set flag that the axis is dirty. External invocation of calcuate() will
			//then recompute axis
			dirty = true;
		}
	}
	/**
	* Returns the lower limit for axis.
	*/
	public function getMin():Number {
		return min;
	}
	/**
	* Returns the upper limit for axis.
	*/
	public function getMax():Number {
		return max;
	}
	/**
	* Returns the intervals to be plotted on chart. The axis calculates
	* multiple sets of intervals internally. 
	* In case of log axis: Only 1 set of interval is calculated. Interval
	* hints are not used. 
	* In case of linear axis:  If multiple interval
	* hints are given, each valid value has its own set of intervals.
	* So while requesting intervals, the invoker has to say the approximate
	* number of intervals that it is seeking on the axis. If the the axis
	* finds an exact value, it returns that. Else it returns a value lesser
	* to that. If no such value is found, it returns the first set.
	* If no value has been passed, it returns the set having median
	* number of interval sets.
	* @return	Array containing internal objects for the axis
	*/
	public function getIntervals(numIntervals:Number):Array {
		if (type==TYPE_LINEAR){
			//Sort intervalSetCount in ascending order. We'll probe for numIntervals
			//directly in this array.
			intervalSetCount.sort(Array.NUMERIC);
			if (arguments.length == 0) {
				//No arguments have been provided. So, return the median set.
				return intervals[intervalSetCount[Math.floor(numIntervalSets/2)]];
			} else {
				//Return the value closest to the passed value (first more, then less)
				var i:Number, index:Number = intervalSetCount[0];
				for (i=numIntervalSets-1; i>=0; i--) {
					//Check if the number of intervals in this set is less than or equal
					if (intervalSetCount[i]<=numIntervals) {
						//Store index
						index = intervalSetCount[i];
						break;
					}
				}
				//Return the relevant array
				return intervals[index];
			}
		}else{
			//Return the only set of intervals
			return intervals;
		}
	}
	/**
	* Returns the internal interval used by axis for calculating min/max.
	* Even in case of forced min/max, it returns the interval used.
	*/
	private function getInterval():Number {
		return interval;
	}
	/**
	* Returns the position (in % ratio terms from min) for the specified
	* value on the axis. If the value is outside the range of axis, it returns
	* positive and negative ratio (percent), without checking. 
	* If the value is within range, return is a value between 0-1, 0 indicating
	* that the value==min. 1 indicates value==max. Anything in between indicates
	* the % shift from min towards max.
	*/
	public function getAxisPosition(value:Number):Number {
		if (type==TYPE_LINEAR){
			//If linear axis, simple ratio
			return ((value-min)/range);
		}else{
			//If log axis, find position and return.
			//To calculate the position of value on axis, convert everything
			//on axis to log base - so that it becomes a simple linear axis.
			//Get the log base of min and max
			var logBaseMin:Number, logBaseMax:Number;
			//Get log of lower and upper limit
			logBaseMax = Math.log(max)/Math.log(logBase);
			logBaseMin = Math.log(min)/Math.log(logBase);
			//Get log of value
			var logBaseVal:Number = Math.log(value)/Math.log(logBase);
			//Now, calculate the position of value on linear scale.
			return ((logBaseVal-logBaseMin)/(logBaseMax-logBaseMin));
		}
	}
	//----------- END: GETTERS AND SETTERS --------------//
	/**
	* dispose function cleans up the given instance of Axis class.
	*/
	public function dispose():Void {
		delete intervalHint;
		delete intervals;
	}
}
