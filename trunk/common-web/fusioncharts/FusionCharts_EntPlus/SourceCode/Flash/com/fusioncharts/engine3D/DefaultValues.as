/**
 * @class DefaultValues
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
 *
 * DefaultValues class holds various default values.
 */
 // class definition
class com.fusioncharts.engine3D.DefaultValues {
	// not to be instantiated (so kept private)
	private function DefaultValues() {
	}
	// default light intensity 
	public static var LIGHT_INTENSITY:Number = 0.2;
	// lower limit of chart scaling
	public static var MIN_SCALE:Number = 20;
	// defalut width of single data  (for LINE and AREA)
	public static var WIDTH_SINGLE_DATA:Number = 2;
	
}
