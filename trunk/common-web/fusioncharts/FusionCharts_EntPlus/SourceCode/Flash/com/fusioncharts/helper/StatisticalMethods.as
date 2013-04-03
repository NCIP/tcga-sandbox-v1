/**
* @class StatisticalMethods
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010

* Utils class helps us bunch a group of utility functions
*/
class com.fusioncharts.helper.StatisticalMethods {
	
	/**
	* Since StatisticalMethods class is just a grouping of statistical functions,
	* we do not want any instances of it (as all methods wil be static).
	* So, we declare a private constructor
	*/
	private function StatisticalMethods() {
		//Private constructor to avoid creation of instances
	}
	/**
	 * getMedian calculate the median from a set 
	 * of unsorted numbers.
	 */
	public static function getMedian(values:Array):Number{
		var median:Number;
		values.sort(Array.NUMERIC);
		var dataLength:Number = values.length;
		var midVal:Number = dataLength / 2;
		if (dataLength == 1) {
			median = values[0];
		}
		median = (dataLength % 2 == 0)? (Number(values[midVal]) + Number(values[midVal - 1])) / 2 : Number(values[Math.floor(midVal)]);
		return median;
	}
	/**
	 * getQuartils calculate the median from a set 
	 * of unsorted numbers.
	 */
	public static function getQuartiles(values:Array):Object {
		var quartiles:Object = new Object();
		var i:Number;
		values.sort(Array.NUMERIC);
		var dataLength:Number = values.length;
		
		//q1Pos = (n+1)/4, q3Pos = 3*(n+1)/4
		var q1Position:Number = Number((dataLength+1)/4);
		var q3Position:Number = 3*q1Position;
		
		//Find the indexed position of the quartiles owithin the array.
		q1Position = q1Position - 1;
		q3Position = q3Position - 1;
		
		if ((dataLength+1)%4 == 0) {
			quartiles.Q1 = values[q1Position];
			quartiles.Q3 = values[q3Position];
		} else {
			var q1LowerLimitPos:Number = Math.floor(q1Position);
			var q1LowerLimitValue:Number = values[q1LowerLimitPos];
			var q1UpperLimitPos:Number = Math.ceil(q1Position);
			var q1UpperLimitValue:Number = values[q1UpperLimitPos];
			var multiplicationFactorQ1:Number = q1Position - q1LowerLimitPos;
			quartiles.Q1 = q1LowerLimitValue + (q1UpperLimitValue - q1LowerLimitValue) * multiplicationFactorQ1;
			
			var q3LowerLimitPos:Number = Math.floor(q3Position);
			var q3LowerLimitValue:Number = values[q3LowerLimitPos];
			var q3UpperLimitPos:Number = Math.ceil(q3Position);
			var q3UpperLimitValue:Number = values[q3UpperLimitPos];
			var multiplicationFactorQ3:Number = q3Position - q3LowerLimitPos;
			quartiles.Q3 = q3LowerLimitValue + (q3UpperLimitValue - q3LowerLimitValue) * multiplicationFactorQ3;
		}
		return quartiles;
	}
	/**
	 * getExtremeValues return min and max values from an array.  
	 */
	public static function getExtremeValues(values:Array):Object{
		var obj:Object = new Object();
		values.sort(Array.NUMERIC);
		var dataLength:Number = values.length
		obj.min = values[0];
		obj.max = values[dataLength - 1];
		return obj;
	}
	/**
	 * getMean calculate the mean from a set 
	 * of numbers.
	 */
	public static function getMean(values:Array):Number{
		values.sort(Array.NUMERIC);
		var valueNum:Number = values.length;
		var mean:Number, i:Number, sum:Number = 0;
		for(i = 0; i < valueNum; i++){
			sum += Number(values[i]);
		}
		mean = sum/valueNum;
		return mean;
	}
	/**
	 * getMeanDeviation calculate the mean deviation from a set
	 * of numbers.
	 */
	public static function getMD(values:Array):Number{
		var deviation:Number, mean:Number, i:Number, sum:Number = 0;
		//mean = ∑fx/n
		mean = getMean(values);
		var f:Array = getFrequencies(values);
		var valueNum:Number = f.length;
		var N:Number = values.length;
		var a:Number = 0;
		for(i = 0; i < valueNum; i++){
			//Math.abs((value[i] - mean)) = |x - mean|
			//f|x - mean| = Math.abs((value[i] - mean)) (Here f = 1)
			//sum = ∑f|x - mean| (Here f = 1)
			sum += f[i].frequency * Math.abs((Number(f[i].value) - mean));
		}
		//MeanDeviation = ∑f|x - mean|)/N (here N = f0 + f1 + f2 + ..... + fn)
		deviation = sum / N;
		return deviation;
	}
	/**
	 * getStandardDeviation calculate the standard deviation from a set
	 * of numbers.
	 */
	public static function getSD(values:Array):Number{
		var deviation:Number, mean:Number, i:Number, sum:Number = 0;
		var values:Array = values.sort(Array.NUMERIC);
		var f:Array = getFrequencies(values);
		var valueNum:Number = values.length;
		//mean = ∑fx/n
		mean = getMean(values);
		for(i = 0; i < valueNum; i++){
			//sum = ∑(Xi - mean)²
			sum += Math.pow(Number(values[i]) - mean, 2);
		}
		//deviation = sqrt(1/n ∑(Xi - mean)²) [valueNum = n];
		deviation = Math.sqrt(sum) / valueNum;
		return deviation;
	}
	/**
	 * getQuartileDeviation calculate the quartile deviation from a set
	 * of numbers.
	 */
	public static function getQD(values:Array):Number{
		var deviation:Number;
		var obj:Object = getQuartiles(values);
		deviation = (obj.Q3 - obj.Q1) / 2;
		return deviation;
	}
	/**
	 * getFrequencies pull count the repitaion of a
	 * return a numerically sorted unique nos array.
	 */
	public static function getFrequencies(values:Array):Array{
		var num:Number = values.length;
		
		var f:Array = [];
		var a:Array = [];
		var obj:Object;
		var i:Number, iv:Number;
		
		for(i = 0; i < num; ++i)
		{
			iv = values[i];
			if( isNaN( f['a'+iv]) )
			{
				obj = new Object();
				obj.value = iv;
				obj.frequency = 1;
				
				a.push(obj);
				
				f['a' + iv] = iv;
				f['a_obj' + iv] = obj;
			}else{
				f['a_obj'+iv].frequency++;
			}
		}
		return a;
	}
}