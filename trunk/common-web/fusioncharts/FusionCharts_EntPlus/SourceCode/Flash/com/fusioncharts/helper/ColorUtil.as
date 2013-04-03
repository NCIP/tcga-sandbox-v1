/**
* @class ColorUtil
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* ColorUtil chart contains generic functions for color conversion
* between Hexadecimal color value and RGB or HSB color model.
* It also helps to draw gradient between two colors with a specific range.
* We can also manipulate Color in HSB model with this class.
*/

import flash.geom.Matrix;
import flash.display.BitmapData;

import com.fusioncharts.extensions.MathExt;


class com.fusioncharts.helper.ColorUtil{
	
	/**
	 * ColorUtil is the private constructor.We need not to 
	 * create any instance of this class.
	 * 
	 */
	
	private function ColorUtil()
	{
		//empty constructor
	}
	
	/**
	 * HEXtoRGB method convert one hexColor string to HSB color
	 * values.And return the value in an array, where the index value 
	 * containes Hue, saturaion and Brightness value of the provided
	 * HexColor respectively.
	 *   
	 * @param   sourceColor 	The hexadecimal Color string value.
	 * @return  Array			The HSB color values in respective order.
	 */
	
	public static function HEXtoRGB(sourceColor:String):Array{
		var rgb:Array;
		
		var sourceClrRGB:Number = parseInt(sourceColor,16);
		
		var r:Number = Math.floor(sourceClrRGB/65536);
		var g:Number =  Math.floor((sourceClrRGB-r*65536)/256);
		var b:Number =  Math.floor((sourceClrRGB-r*65536)-(g*256));
	
		rgb = [r,g,b];
		
		return rgb;
		
	}
	
	/**
	 * RGBtoHex methods takes an array of RGB color values and convert them to 
	 * a hexadecimal color value.And return the string.
	 *  
	 * @param   rgb Array	The array of RGB color values respective.The 0 index contains
	 * 						Red color value, 1 index contains the Green color value and the 
	 * 						2nd or the last index conatins the blue color value.
	 * 
	 * @return  String	The hexadecimal color string of the provided RGB value.
	 */
	
	public static function RGBtoHex(rgb:Array):String{
		
		var r:Number = rgb[0]; var g:Number = rgb[1]; var b:Number =rgb[2];
		
		var hexColor:Number = r << 16 | g << 8 | b;
		return hexColor.toString(16);
	}
	
	/**
	 * RGBtoHSB methods takes an array of RGB color values and convert them 
	 * HSB color values and return an array conatining the Hue,Saturation and 
	 * Brightness of the specified RGB color.
	 *
	 * @param   rgb Array	The array of RGB color values respective.The 0 index contains
	 * 						Red color value, 1 index contains the Green color value and the 
	 * 						2nd or the last index conatins the blue color value.
	 * 
	 * @return  Array	Color value array containing the Hue, Saturation and brightness of 
	 * 					The provided Color.
	 */
	 
	public static function RGBtoHSB(rgb:Array):Array{
		
		var r:Number = rgb[0]; var g:Number = rgb[1]; var b:Number =rgb[2];
		
		var max:Number = Math.max(Math.max(r,g),b);
		var min:Number = Math.min(Math.min(r,g),b);
		
		var hue:Number = 0; var saturation:Number = 0; var value:Number = 0;
		
		var hsb:Array = [];
		
		//get Hue
	    if(max == min){
	       hue = 0;
	    }else if(max == r){
	        hue = (60 * (g-b) / (max-min) + 360) % 360;
	    }else if(max == g){
	        hue = (60 * (b-r) / (max-min) + 120);
	    }else if(max == b){
	        hue = (60 * (r-g) / (max-min) + 240);
	    }
		
		//get Value
	    var value = max;
	 
	    //get Saturation
	    if(max == 0){
	       saturation = 0;
	    }else{
	       saturation = (max - min) / max;
	    }
	 
	    hsb = [Math.round(hue), Math.round(saturation * 100), Math.round(value / 255 * 100)];
	    return hsb;
	}
	
	/**
	 * HSBtoRGB methods takes an array of HSB color values and convert them 
	 * RGB color values and return an array conatining the Red,Green and 
	 * Blue color value of the provided HSB color.
	 * 
	 * @param   hsb	Array	The array of Hue,Saturation and Brightness property
	 * 						of a color.
	 * 
	 * @return  Array	The Array of RGB color value of the specified color.
	 */
	
	public static function HSBtoRGB(hsb:Array):Array{
		var h:Number = hsb[0]; var s:Number = hsb[1]; var v:Number = hsb[2];
		
		var r:Number = 0;
	    var g:Number = 0;
	    var b:Number = 0;
	    var rgb:Array = [];
	 
	    var tempS:Number = s / 100;
	    var tempV:Number = v / 100;
	 
	    var hi:Number = Math.floor(h/60) % 6;
	    var f:Number = h/60 - Math.floor(h/60);
	    var p:Number = (tempV * (1 - tempS));
	    var q:Number = (tempV * (1 - f * tempS));
	    var t:Number = (tempV * (1 - (1 - f) * tempS));
	 
	    switch(hi){
	        case 0: r = tempV; g = t; b = p; break;
	        case 1: r = q; g = tempV; b = p; break;
	        case 2: r = p; g = tempV; b = t; break;
	        case 3: r = p; g = q; b = tempV; break;
	        case 4: r = t; g = p; b = tempV; break;
	        case 5: r = tempV; g = p; b = q; break;
		}
 
	    rgb = [Math.round(r * 255), Math.round(g * 255), Math.round(b * 255)];
	    return rgb;
	}
	
	/**
	 * HEXtoHSB methods takes a hexadecimal color String value and convert them 
	 * HSB color values and return an array conatining the Hue,Saturation and 
	 * Brightness color value of the provided Hex string.
	 * 
	 * @param   HexStr	String	The hex color string value
	 * 
	 * @return  Array	The Array of HSB color value of the specified color.
	 */
	public static function HEXtoHSB(HexStr:String):Array{
		//get the rgb component of the provided color...
		var rgb:Array = HEXtoRGB(HexStr);
		//get the hsb color value for the provided color...
		var hsb:Array = RGBtoHSB(rgb);
		
		return hsb;
	}
	
	/**
	 * HSBtoHEX methods takes an array of HSB color values and convert them 
	 * to a hexadecimal color value and return 
	 * 
	 * @param   hsb	Array	The array of Hue,Saturation and Brightness property
	 * 						of a color.
	 * 
	 * @return  String		The Hexadecimal string value.
	 */
	
	public static function HSBtoHEX(hsb:Array):String{
		var hexStr:String;
		//convert the hsb color to rgb
		var rgb:Array = HSBtoRGB(hsb);
		//convert to hex string
		hexStr = RGBtoHex(rgb);
		
		return hexStr;
	}
	
	/**
	 * getGradientColors method calculates the gradient colors in-between two colors.
	 * 	NOTE - the process could slow down if the range value difference or the number 
	 * 	of gradients colors are really large.
	 * 
	 * @usage   
	 * @param   color1     		String	The start color for the gradient.
	 * @param   color2     		String	The End Color for the gradient.
	 * @param   startValue 		Number	The start value for the gradinet.
	 * @param   endValue   		Number	The End value for the gradient.
	 * @param	valueContainer	Array 	The conatiner array of heat objects of which we need to define color.
	 * @return  
	 */
	
	public static function getGradientColors(color1:String, color2:String, startValue:Number, endValue:Number, 
											 valueContainer:Array, 
											 mapByPercent:Boolean, minHeatValue:Number, maxHeatValue:Number, decimalPrecision:Number):Array{
		var i:Number;
		var R:Number;
		var G:Number;
		var B:Number;
		var numRange:Number = endValue - startValue;
		
		var gradientArray:Array = new Array();
		var rgb1:Array = ColorUtil.HEXtoRGB(color1);
		var rgb2:Array = ColorUtil.HEXtoRGB(color2);
		
		var deltaR:Number = Number(rgb2[0])-Number(rgb1[0]);
		var deltaG:Number =  Number(rgb2[1])-Number(rgb1[1]);
		var deltaB:Number =  Number(rgb2[2])-Number(rgb1[2]);
		
		var totDiffrnce:Number = maxHeatValue - minHeatValue;
		// now if we are not calculating range on the basis of percentage; we will derive 
		// only the colors we need.
		//if(!mapByPercent){
		var numValues : Number = valueContainer.length;
		for(i = 0; i < numValues; i++){
			var val:Number = Number(valueContainer[i].value);
			//var val:Number = Math.floor(Number(valueContainer[i].value));
			if(!mapByPercent){
				var rangeKey : String = "range_"+val.toString();
				var valFactor : Number = (val - startValue)/numRange;
			}else{
				var diff:Number = val - minHeatValue;
				var percentHeatValue : Number = diff / totDiffrnce * 100;
				percentHeatValue = MathExt.roundUp(percentHeatValue, decimalPrecision);
				//store the percentage value for future usage.
				valueContainer[i].percentValue = percentHeatValue;
				var rangeKey : String = "range_"+percentHeatValue.toString();
				var valFactor : Number = (percentHeatValue - startValue)/numRange;
			}
			//check whether any color already associated with this value.
			var colorExist : Boolean = (gradientArray[rangeKey] != undefined) ? true : false ;
			if(colorExist){
				continue;
			}
			
			//var valFactor : Number = (val - startValue)/numRange;
			R =  Math.floor(Number(rgb1[0]) + deltaR * valFactor);
			G =  Math.floor(Number(rgb1[1]) + deltaG * valFactor);
			B =  Math.floor(Number(rgb1[2]) + deltaB * valFactor);
			
			var RGB:Array = [R,G,B];
			var colorVal:String = "0x"+ColorUtil.RGBtoHex(RGB);
			gradientArray[rangeKey] = colorVal;
			gradientArray.push(colorVal);
		}
		
		return gradientArray;
	}
	
	//getGradientColorsHashTable creates a gradient based on a sepcific numeric range and 
	//create an associative array(Hash Table) with each value assigned to a specific color
	//and returns that array.
	//	NOTE - As we are using Flash's BitmapData the process will fail if we have to draw a 
	// 	bit map wider than 2880 px.Since we prefer to use the above getGradientColors method.
	public static function getGradientColorsHashTable(color1:String, color2:String, startValue:Number, endValue:Number, isAlphaRGB:Boolean):Array{
		isAlphaRGB = (isAlphaRGB == undefined || isAlphaRGB == null) ? false : true;
		
		var gradientArray:Array = new Array();
		var colorsArray : Array = new Array();
		
		//store the starting and ending color and range values for usage later
		
		gradientArray["startingColor"] = color1;
		gradientArray["endingColor"] = color2;
		gradientArray["startingRange"] = startValue;
		gradientArray["endingRange"] = endValue;
		
		var mtrxWidth:Number = (endValue-startValue)+1;
		var mtrxHeight:Number = 10;
		var mtrxTx:Number = 0;
		var mtrxTy:Number = 0;
		
		colorsArray.push(parseInt(color1, 16));
		colorsArray.push(parseInt(color2, 16));
		var alphaArray : Array = [100, 100];
		var ratioArray : Array = [0,255];
		
		var matrix : Matrix = new Matrix();
		
		matrix.createGradientBox(mtrxWidth, mtrxHeight, 0, mtrxTx, mtrxTy);
		
		var tempGradScale : MovieClip = _root.createEmptyMovieClip("tempGradMC",_root.getNextHighestDepth());
		
		tempGradScale.beginGradientFill("linear", colorsArray, alphaArray, ratioArray, matrix);
		
		tempGradScale.moveTo(0, 0);
		tempGradScale.lineTo(mtrxWidth, 0);
		tempGradScale.lineTo(mtrxWidth, mtrxHeight);
		tempGradScale.lineTo(0, mtrxHeight);
		tempGradScale.lineTo(0, 0);
		
		tempGradScale.endFill();
		
		//clone bitmap
		var myBMP : BitmapData = new BitmapData(mtrxWidth, mtrxHeight);
		myBMP.draw(tempGradScale);
		
		for(var i =0; i<mtrxWidth; i++){
			if(isAlphaRGB){
				var alpha:String = (myBMP.getPixel32(i, 0) >> 24 & 0xFF).toString(16);
				var red:String = (myBMP.getPixel32(i, 0) >> 16 & 0xFF).toString(16);
				var green:String = (myBMP.getPixel32(i, 0) >> 8 & 0xFF).toString(16);
				var blue:String = (myBMP.getPixel32(i, 0) & 0xFF).toString(16);
				var color : String = "0x" + alpha + red + green + blue
				
			}else{
				var color : String = "0x"+myBMP.getPixel(i,0).toString(16);
			}
			//store the color n the indexed manner
			gradientArray.push(color);
			var rangeKey : String = "range_"+((i)+startValue);
			gradientArray[rangeKey] = color;
		}
		
		//dispose the mc & bmp
		myBMP.dispose();
		delete myBMP;
		tempGradScale.removeMovieClip();
		delete tempGradScale;
		
		return gradientArray;
	}
	
	
	/**
	 * getHSBColorVariations method accept one color value in hexadecimal string and tries to 
	 * derive a draker or lighter gradient of that color.The colorQoutient parameter decides 
	 * Whether to get darker or lighter color
	 * 
	 * @usage   
	 * @param   property      String	The property of the HSB color module to modify.
	 * 									This could be Hue, Saturation or Brightness.
	 * @param   sourceColor   String	The hexadecimal source color value.We will try
	 * 									to get a lighter/darker shade of this color.
	 * @param   colorQuotient Number	The change value to be applied on the specified
	 * 									property
	 * 
	 * @return  String 					Returns the hexadecimal color code of the calculated shade
	 */
	
	public static function getHSBColorVariations(property:String,sourceColor:String,colorQuotient:Number):String{
		
		var sourceClrRGB:Number = parseInt(sourceColor,16);
		
		var r:Number = Math.floor(sourceClrRGB/65536);
		var g:Number =  Math.floor((sourceClrRGB-r*65536)/256);
		var b:Number =  Math.floor((sourceClrRGB-r*65536)-(g*256));
		
		var srcRgb:Array = [r,g,b];
		
		// GET THE HSB COLOR VALUE 
		var hsb:Array = ColorUtil.RGBtoHSB(srcRgb);
		
		//Now replace the required property (Hue or Saturation or Brightness)
		//value in the HSB color mode and get a new RGB color.
		switch(property.toLowerCase()){
			case "hue" :
				hsb.splice(0,1,colorQuotient);
				break;
			case "saturation" :
				hsb.splice(1,1,colorQuotient);
				break;
			case "brightness" :
				hsb.splice(2,1,colorQuotient);
				break;
			default :
				hsb.splice(2,1,colorQuotient);
				break;
				
		}
		
		// GET THE RGB COLOR VALUE OF THE HSB 
		var rgb:Array = ColorUtil.HSBtoRGB(hsb);
		r = rgb[0];
		g = rgb[1];
		b = rgb[2];
		
		var hexColor:Number = r << 16 | g << 8 | b;
		
		return hexColor.toString(16)
	}
	
}