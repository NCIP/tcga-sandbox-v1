/**
 * @class MathExt
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 
 * MathExt class bunches a group of mathematical functions
 * which will be used by other classes. All the functions in
 * this class are declared as static, as the methods do not
 * relate to any specific instance.
 */
class com.fusioncharts.extensions.MathExt {
	/**
	 * Private constructor function so that instances of 
	 * this class cannot be initialized.
	*/
	private function MathExt() {
		//Nothing to do.
	}
	/**
	 * numDecimals method returns the number of decimal places provided
	 * in the given number.
	 *	@param	num	Number for which we've to find the decimal places.
	 *	@returns	Number of decimal places found.
	*/
	public static function numDecimals(num:Number):Number {
		//Absolute value (to avoid floor disparity for negative num)
		num = Math.abs(num);
		//Convert to string and find the position of dot. 
		var strNum = String(num);
		var decimals:Number = 0;
		var dotPos:Number = strNum.indexOf(".");
		//See if the number has decimal at all
		if (dotPos!=-1){
			//Find the position of decimal.
			decimals = strNum.length - dotPos - 1;
		}
		//Return the number of decimal digits
		return decimals;
	}
	/**
	 * toRadians method converts angle from degrees to radians
	 * @param	angle	The numeric value of the angle in 
	 * 					degrees
	 * @returns			The numeric value of the angle in radians
	 */
	public static function toRadians(angle:Number):Number {
		return (angle/180)*Math.PI;
	}
	/**
	 * toDegrees method converts angle from radians to degrees
	 * @param	angle	The numeric value of the angle in 
	 * 					radians
	 * @returns			The numeric value of the angle in degrees
	 */
	public static function toDegrees(angle:Number):Number {
		return (angle/Math.PI)*180;
	}
	/**
	 * remainderOf method calculates the remainder in 
	 * a division to the nearest twip.
	 * @param	a	dividend in a division
	 * @param	b	divisor in a division
	 * @returns		Remainder in the division rounded 
	 * 				to the nearest twip.
	 */
	public static function remainderOf(a:Number, b:Number):Number {
		return roundUp(a%b, 5);
	}
	/**
	 * boundAngle method converts any angle in degrees
	 * to its equivalent in the range of 0 to 360 degrees.
	 * @param	angle	Angle in degrees to be procesed;
	 *					can take negetive values.
	 * @returns			Equivalent non-negetive angle in degrees
	 *					less than or equal to 360 degrees
	 */
	public static function boundAngle(angle:Number):Number {
		// if angle is non-negative 
		if (angle>=0) {
			// bound the angle in 0 to 360 degree range
			return remainderOf(angle, 360);
		} else {
			// if negative angle, bound the angle in 0 to 360 degree range
			return 360-remainderOf(Math.abs(angle), 360);
		}
	}
	/**
	 * minimiseAngle method returns the passed angle 
	 * transformed in the range of -180 to 180 degrees.
	 * @param	angle	angle in degrees
	 * @return			transformed angle in degree
	 */
	public static function minimiseAngle(angle:Number):Number {
		// to hold transformed angle
		var ang:Number;
		// if angle is non-negative 
		if (angle >= 0) {
			// bound the angle in 0 to 360 degree range
			ang = remainderOf(angle, 360);
		} else {
			// if negative angle:
			// bound the angle in 0 to 360 degree range
			ang = 360 - remainderOf(Math.abs(angle), 360);
		}
		// now minisise the angle in the range of -180 to 180 degrees
		if (ang > 180) {
			ang = ang - 360;
		}
		// minimised angle return
		return ang;
	}
	/**
	 * toNearestTwip method converts a numeric value by
	 * rounding it to the nearest twip value ( one twentieth
	 * of a pixel ) for propermost rendering in flash.
	 * @param	num		Number to rounded
	 * @returns			Number rounded upto 2 decimal places and
	 *					second significant digit right of decimal
	 *					point, if exists at all is 5.
	 */
	public static function toNearestTwip(num:Number):Number {
		var n:Number = num;
		var s:Number = (n<0) ? -1 : 1;
		var k:Number = Math.abs(n);
		var r:Number = Math.round(k*100);
		var b:Number = Math.floor(r/5);
		var t:Number = Number(String(r-b*5));
		var m:Number = (t>2) ? b*5+5 : b*5;
		return s*(m/100);
	}
	/**
	 * roundUp method is used to format trailing decimal 
	 * places to the required precision, with default base 2.
	 * @param		num		number to be formatted
	 * @param		base	number of precision digits
	 * @returns		formatted number
	 */
	public static function roundUp(num:Number, base:Number):Number {
		// precise to number of decimal places
		base = (base == undefined) ? 2 : base;
		var factor:Number = Math.pow(10, base);
		num *= factor;
		num = Math.round(Number(String(num)));
		num /= factor;
		return num;
	}
}
