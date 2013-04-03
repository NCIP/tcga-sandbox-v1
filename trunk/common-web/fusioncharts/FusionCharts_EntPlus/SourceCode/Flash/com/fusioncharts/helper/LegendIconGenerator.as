/**
* @class LegendIconGenerator
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
* LegendIconGenerator class helps us create icon bitmapData for use in
* AdvancedLegend class.
*/
import flash.display.BitmapData;
import flash.geom.Matrix;
import flash.geom.ColorTransform
import com.fusioncharts.extensions.DrawingExt;
import flash.filters.ColorMatrixFilter;

class com.fusioncharts.helper.LegendIconGenerator {
	/**
	 * Private contructor. This class is not for instantiation.
	 */
	private function LegendIconGenerator(){
	}
	
	//Enumeration of icon types supported by the class.
	public static var RECTANGLE:String = 'rectangle';
	public static var AREA:String = 'area';
	public static var CIRCLE:String = 'circle';
	public static var POLYGON:String = 'polygon';
	public static var LINE:String = 'line';
	public static var WEDGE:String = 'wedge';
	public static var COLUMN:String = 'column';
	public static var BAR:String = 'bar';
	public static var SPOKE:String = 'spoke';
	public static var BOXNWHISKER:String = 'boxnwhisker';
	//Ratio (default) of white space padding of icon to total icon (white space + icon graphics)( 0 < intraIconPaddingPercent < 1) [it controls the blank space surrounding the icon graphics]
	private static var intraIconPaddingPercent:Number = 0.35;
	//Rectangular border color (default) of icon periphery (for non-fading mode)
	private static var iconBorderColor:Number = 0xA7AAA3;
	
	/**
	 * getIcons method is the only public method available to 
	 * create icons and get them returned.
	 * @param	type			shape type
	 * @param	length			side length of square icon
	 * @param	fadeDisabled	if disabled state be faded
	 * @param	objParams		drawing parameters
	 * @return					Object containing 2 bitmapData, one for each state
	 */
	public static function getIcons(type:String, length:Number, fadeDisabled:Boolean, objParams:Object):Object{
		//N.B: All colors be fetched as Number.
		/**
		 * objParams may contain the following properties:
		 *		fillColor			:Number		Fill color (except for 'line')
		 *		lineColor			:Number		Line color
		 *		borderColor			:Number		Border color (except for 'line')
		 *		showAnchor			:Boolean	If anchor be shown
		 *		anchorSides			:Number		Number of sides of the polygon as anchor
		 *		anchorBorderColor	:Number		Border color of anchor
		 *		anchorBgColor		:Number		background fill color of anchor
		 *		startingAngle		:Number		Starting angle of polygon
		 *		sides				:Number		Number of sides of polygon
		 *	
		 *		iconBorderColor 	:Number		Rectangular border color of icon periphery (for non-fading mode), to show state change
		 *		intraIconPaddingPercent 		:Number		Ratio of graphical bounds of icon to total icon (white space + icon graphics)( 0 < intraIconPaddingPercent <= 1)
		 */
		
		/**
		 * objParams must contain properties as follows for different icon types (optional params in square brackets):
		 *	 Rectangle: 	fillColor:Number, [borderColor:Number];
		 *	 Area: 			fillColor:Number, [borderColor:Number];
		 *	 Circle:		fillColor:Number, [borderColor:Number];
		 *	 Polygon:		fillColor:Number, [borderColor:Number, sides:Number, startingAngle:Number];
		 *	 Line:			lineColor:Number, [showAnchor:Boolean, anchorBgColor:Number, [anchorSides:Number, anchorBorderColor:Number, startingAngle:Number]];
		 *	 Wedge:			fillColor:Number, [borderColor:Number];
		 *	 Column:		fillColor:Number, [borderColor:Number];
		 *	 Bar:			fillColor:Number, [borderColor:Number];
		 */
		 
		//The temporary MC for drawing the icons to create bitmapData from.
		var mcCanvas:MovieClip = _root.createEmptyMovieClip('tempCanvas', _root.getNextHighestDepth());
		
		//Validating and checking for applicable white space around icon graphics in the icon.
		if(isNaN(objParams.intraIconPaddingPercent) || objParams.intraIconPaddingPercent <= 0 || objParams.intraIconPaddingPercent >= 1){
			//If valid value not specified, apply the the default value for it.
			objParams.intraIconPaddingPercent = LegendIconGenerator.intraIconPaddingPercent;
		}
		
		objParams.intraIconGraphicsPercent = 1 - objParams.intraIconPaddingPercent;
		
		//Create the icon graphics in the temporary MC.
		LegendIconGenerator['draw_'+type](mcCanvas, length, length, objParams);
		
		//Required 2 Bitmapdata containers created
		var bmpd1:BitmapData = new BitmapData(length, length, true, 0x00FFFFFF);
		var bmpd2:BitmapData = new BitmapData(length, length, true, 0x00FFFFFF);
		
		//First bitmapData drawn
		bmpd1.draw(mcCanvas);
		
		//--------------------//
		//If bounding rectangle be drawn around the other icon
		if(!fadeDisabled){
			var w:Number = length - 1;
			var h:Number = length - 1;
			var iconBorderColor:Number = (isNaN(objParams.iconBorderColor))? LegendIconGenerator.iconBorderColor : objParams.iconBorderColor;
			mcCanvas.lineStyle(0, iconBorderColor, 100);
			mcCanvas.moveTo(0, 0);
			mcCanvas.lineTo(w, 0);
			mcCanvas.lineTo(w, h);
			mcCanvas.lineTo(0, h);
			mcCanvas.lineTo(0, 0);
			
			bmpd2.draw(mcCanvas);
		//Else if icon be faded
		}else{
			//Standard desaturating coefficients for R, G and B 
			var r:Number = 0.30;
			var g:Number = 0.59;
			var b:Number = 0.11;
			
			var matrixSaturation:Array = [	r, g, b, 0, 0,
											r, g, b, 0, 0,
											r, g, b, 0, 0,
											0, 0, 0, 1, 0
											];
			//Desaturation
			var filter:ColorMatrixFilter = new ColorMatrixFilter(matrixSaturation);
			mcCanvas.filters = new Array(filter);
			
			var colorTrans:ColorTransform = new ColorTransform();
			//50% alpha
			colorTrans.alphaMultiplier = 0.5;
			var matrix:Matrix = new Matrix;
			matrix.identity();
			
			bmpd2.draw(mcCanvas, matrix, colorTrans);
		}
		//clean up canvas
		mcCanvas.clear();
		//Remove the temporary MC.
		mcCanvas.removeMovieClip();
		//return
		return {active:bmpd1, inactive:bmpd2};
	}
	/**
	 * getDarkColor method gives the darker shade of the color provided.
	 * @param	sourceHexColor	The hexadecimal number
	 * @param	intensity		shading intensity
	 */
	private static function getDarkColor(sourceHexColor:Number, intensity:Number):Number {
		//Check whether the intensity is in right range
		intensity = ((intensity>1) || (intensity<0)) ? 1 : intensity;
		//Now, get the r,g,b values separated out of the specified color
		//Initial red channel value.
		var r:Number = sourceHexColor >> 16;
		//Initial green channel value.
		var g:Number = (sourceHexColor >> 8) & 0xff;
		//Initial blue channel value.
		var b:Number = sourceHexColor & 0xff;
		//Now, get the darker color based on the Intesity Specified
		var darkColor:Number = (r*intensity) << 16 | (g*intensity) << 8 | (b*intensity);
		return (darkColor);
	}
	/**
	 * draw_column method draws the icon graphics for column chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_column(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		//---------- FILL ------------//
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var w:Number = width * intraIconGraphicsPercent;
		var h:Number = height * intraIconGraphicsPercent;
		var t:Number = w/4;
		
		var xIni:Number = ((1 - intraIconGraphicsPercent)/2) * width;
		var yIni:Number = (1 - (1 - intraIconGraphicsPercent)/2) * height;
		var xStart:Number = xIni;
		//Column 1
		canvas_mc.lineStyle(0, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//-------------------------//
		//Column 2
		canvas_mc.lineStyle(0, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h);
		canvas_mc.lineTo(xStart, yIni - h);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//------------------------//
		//Column 3
		canvas_mc.lineStyle(0, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		
		//------------------------------- EFFECTS ---------------------------------------//
		var matrix:Matrix = new Matrix();
		var ratio:Number = 0.2;
		var alpha:Number = 40;
		matrix.identity();
		matrix.createGradientBox(w, h, Math.PI/2, 0, ratio*2*h/3 + h/3);
		
		var xStart:Number = xIni;
		//Column 1
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [0, alpha, 0], [0, 70, 140], matrix);
		
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//-------------------------//
		//Column 2
		matrix.identity();
		matrix.createGradientBox(w, h, Math.PI/2, 0, ratio*h);
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [0, alpha, 0], [0, 70, 140], matrix);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h);
		canvas_mc.lineTo(xStart, yIni - h);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//------------------------//
		//Column 3
		matrix.identity();
		matrix.createGradientBox(w, h, Math.PI/2, 0, ratio*h/2 + h/2);
		
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [0, alpha, 0], [0, 70, 140], matrix);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		
	}
	
	/**
	 * draw_bar method draws the icon graphics for bar chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_bar(base_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		//--------------------//
		//Actually drawing icon for column chart and jsut rotating it by 90 degree to get the bar chart icon.
		var canvas_mc:MovieClip = base_mc.createEmptyMovieClip('subMC', 0);
		canvas_mc._x = width;
		canvas_mc._rotation = 90;
		//------------------- FILL ---------------------//
		
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var w:Number = width * intraIconGraphicsPercent;
		var h:Number = height * intraIconGraphicsPercent;
		var t:Number = w/4;
		
		var xIni:Number = ((1 - intraIconGraphicsPercent)/2) * width;
		var yIni:Number = (1 - (1 - intraIconGraphicsPercent)/2) * height;
		var xStart:Number = xIni;
		//Bar 1
		canvas_mc.lineStyle(0, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//-------------------------//
		//Bar 2
		canvas_mc.lineStyle(0, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h);
		canvas_mc.lineTo(xStart, yIni - h);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//------------------------//
		//Bar 3
		canvas_mc.lineStyle(0, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		
		//------------------------------- EFFECTS ---------------------------------------//
		var matrix:Matrix = new Matrix();
		var ratio:Number = 0.2;
		var alpha:Number = 40;
		matrix.identity();
		matrix.createGradientBox(w, h, Math.PI/2, 0, ratio*2*h/3 + h/3);
		
		var xStart:Number = xIni;
		
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [0, alpha, 0], [0, 70, 140], matrix);
		//Bar 1
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni - 2*h/3);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//-------------------------//
		//Bar 2
		matrix.identity();
		matrix.createGradientBox(w, h, Math.PI/2, 0, ratio*h);
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [0, alpha, 0], [0, 70, 140], matrix);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h);
		canvas_mc.lineTo(xStart, yIni - h);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
		//------------------------//
		//Bar 3
		matrix.identity();
		matrix.createGradientBox(w, h, Math.PI/2, 0, ratio*h/2 + h/2);
		
		canvas_mc.lineStyle();
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [0, alpha, 0], [0, 70, 140], matrix);
		xStart += t+t/2;
		canvas_mc.moveTo(xStart, yIni);
		canvas_mc.lineTo(xStart + t, yIni);
		canvas_mc.lineTo(xStart + t, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni - h/2);
		canvas_mc.lineTo(xStart, yIni);
		
		canvas_mc.endFill();
	}
	
	/**
	 * draw_rectangle method draws the rectangular icon graphics.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_rectangle(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		
		//-------------- FILL ---------------//
		
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var w:Number = width * intraIconGraphicsPercent;
		var h:Number = height * intraIconGraphicsPercent;
		
		var xIni:Number = ((1 - intraIconGraphicsPercent)/2) * width;
		var yIni:Number = ((1 - intraIconGraphicsPercent)/2) * height;
		
		canvas_mc.lineStyle(2, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		
		canvas_mc.moveTo(xIni, yIni);
		canvas_mc.lineTo(xIni + w, yIni);
		canvas_mc.lineTo(xIni + w, yIni + h);
		canvas_mc.lineTo(xIni, yIni + h);
		canvas_mc.lineTo(xIni, yIni);
		
		canvas_mc.endFill();
		
		//----------- EFFECTS ------------//
		canvas_mc.lineStyle();
		
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h, Math.PI/2, 0, 0);
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [90, 50, 0], [0, 70, 120], matrix);
		var gutter:Number = 1;
		canvas_mc.moveTo(xIni + gutter, yIni + gutter);
		canvas_mc.lineTo(xIni + w - gutter, yIni + gutter);
		canvas_mc.lineTo(xIni + w - gutter, yIni + h - gutter);
		canvas_mc.lineTo(xIni + gutter, yIni + h - gutter);
		canvas_mc.lineTo(xIni + gutter, yIni + gutter);
		
		canvas_mc.endFill();
	}
	
	/**
	 * draw_area method draws the icon graphics for area chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_area(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		
		//-------------------  FILL --------------------//
		
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var w:Number = width * intraIconGraphicsPercent;
		var h:Number = height * intraIconGraphicsPercent;
		var i:Number = w/3;
		
		var xIni:Number = ((1 - intraIconGraphicsPercent)/2) * width;
		var yIni:Number = (1 - (1 - intraIconGraphicsPercent)/2) * height*4/5;
		
		canvas_mc.lineStyle(2, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(fillColor);
		
		var down:Number = h/3;
		var h1:Number = yIni - 3*h/5 + down;
		var h2:Number = yIni - 4*h/5 + down;
		var h3:Number = yIni - 3*h/5 + down;
		var h4:Number = yIni - 6*h/7 + down;
		
		canvas_mc.moveTo(xIni, yIni);
		canvas_mc.lineTo(xIni, h1);
		canvas_mc.lineTo(xIni + i, h2);
		canvas_mc.lineTo(xIni + 2*i, h3);
		canvas_mc.lineTo(xIni + 3*i, h4);
		canvas_mc.lineTo(xIni + w, yIni);
		canvas_mc.lineTo(xIni, yIni);
		
		canvas_mc.endFill();
		
		//----------- EFFECTS -------------//
		//Creating gradient in MC and rotating it to match the faces.
		var gutter:Number = 1;
		
		var mc1:MovieClip = canvas_mc.createEmptyMovieClip('mcFx1', 0);
		mc1._x = xIni;
		mc1._y = h1;
		// First face effect
		var ang:Number = Math.atan2(h2-h1, i) * 180/Math.PI;
		
		var d1:Number = Math.sqrt(i*i + (h2-h1)*(h2-h1));
		var matrix1:Matrix = new Matrix();
		matrix1.createGradientBox(d1, h-h1, Math.PI/2);
		
		mc1.lineStyle();
		mc1.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF], [70, 0], [0, 80], matrix1);
		mc1.lineTo(d1, 0);
		mc1.lineTo(d1, h-h1);
		mc1.lineTo(0, h-h1);
		mc1.lineTo((h-h1)*Math.sin(-ang), (h-h1)*Math.cos(-ang));
		mc1.lineTo(0, 0);
		
		mc1._rotation = ang;
		
		//-----------------------//
		// Second face effect
		
		var mc2:MovieClip = canvas_mc.createEmptyMovieClip('mcFx2', 1);
		mc2._x = xIni + 2*i;
		mc2._y = h3;
		var d2:Number = Math.sqrt(i*i + (h4-h3)*(h4-h3));
		var matrix2:Matrix = new Matrix();
		matrix2.createGradientBox(d2, h-h3, Math.PI/2);
		
		mc2.lineStyle();
		mc2.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF], [70, 0], [0, 80], matrix2);
		mc2.lineTo(d2, 0);
		mc2.lineTo((h-h3)*Math.tan(ang), h-h3);
		mc2.lineTo(0, h-h3);
		mc2.lineTo(0, 0);
		var ang:Number = Math.atan2(h4-h3, i) * 180/Math.PI;
		mc2._rotation = ang;
		
		//--------------------------//
		// Central radial effect
		
		var mc3:MovieClip = canvas_mc.createEmptyMovieClip('mcFx3', 2);
		
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h, 0);
		
		mc3.beginGradientFill('radial', [0xFFFFFF, 0xFFFFFF], [60, 0], [0, 100], matrix, "pad", "RGB", 0.3);
		
		mc3.moveTo(xIni + gutter, yIni - gutter);
		mc3.lineTo(xIni + gutter, h1 + gutter);
		mc3.lineTo(xIni + i, h2 + gutter);
		mc3.lineTo(xIni + 2*i - gutter, h3 + gutter);
		mc3.lineTo(xIni + 2*i - gutter, yIni -gutter);
		mc3.lineTo(xIni + gutter, yIni - gutter);
		
		mc3.endFill();
		
	}
	
	/**
	 * draw_circle method draws the icon graphics for bubble chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_circle(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		
		//-----------------  FILL ---------------//
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var radius:Number = Math.min(width, height) * intraIconGraphicsPercent/2;
		var startingAngle:Number = 0;
		var arcAngle:Number = 360;
		
		var xCenter:Number = width/2;
		var yCenter:Number = height/2;
		
		canvas_mc.lineStyle(2, borderColor);
		canvas_mc.beginFill(fillColor);
		
		DrawingExt.drawCircle(canvas_mc, xCenter, yCenter, radius, radius, startingAngle, arcAngle);
		canvas_mc.endFill();
		//---------------- EFFECTS -----------------//
		var mc:MovieClip = canvas_mc.createEmptyMovieClip('mcFx', 0);
		mc._x = xCenter;
		mc._y = yCenter;
		
		mc.lineStyle();
		var matrix1:Matrix = new Matrix();
		matrix1.createGradientBox(2*radius, 2*radius, -Math.PI/2, -radius, -radius);
		matrix1.scale(1, 0.5);
		matrix1.translate(0, -5*radius/10);
		
		mc.beginGradientFill('radial', [0xFFFFFF, 0xFFFFFF], [80, 0], [0, 150], matrix1, "pad", "RGB", 0.7);
		
		DrawingExt.drawCircle(mc, 0, 0, radius, radius, startingAngle, arcAngle);
		mc.endFill();
		
	}
	
	/**
	 * draw_polygon method draws the icon graphics for scatter chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_polygon(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		
		//------------- FILL --------------//
		
		var gutter:Number = 1.2;
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var radius:Number = Math.min(width, height) * intraIconGraphicsPercent/2;
		var sides:Number = (objParams.sides >= 3)? Math.round(objParams.sides) : 5;
		var startingAngle:Number = (isNaN(objParams.startingAngle))? 0 : objParams.startingAngle;
		
		var xCenter:Number = width/2;
		var yCenter:Number = height/2;
		
		canvas_mc.lineStyle(1, borderColor);
		canvas_mc.beginFill(fillColor);
		
		DrawingExt.drawPoly(canvas_mc, xCenter, yCenter, sides, radius, startingAngle);
		canvas_mc.endFill();
		
		//----------------- EFFECTS -----------------//
		
		
		var mc:MovieClip = canvas_mc.createEmptyMovieClip('mcFx', 0);
		mc._x = xCenter;
		mc._y = yCenter;
		
		mc.lineStyle();
		var matrix1:Matrix = new Matrix();
		matrix1.createGradientBox(2*radius, 2*radius, -Math.PI/2, -radius, -radius);
		matrix1.scale(1, 0.5);
		matrix1.translate(0, -5*radius/10);
		
		mc.beginGradientFill('radial', [0xFFFFFF, 0xFFFFFF], [80, 0], [0, 150], matrix1, "pad", "RGB", 0.9);
		
		DrawingExt.drawPoly(mc, 0, 0, sides, radius - gutter, startingAngle);
		mc.endFill();
		
	}
	
	/**
	 * draw_line method draws the icon graphics for line chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_line(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var lineColor:Number = objParams.lineColor;
		
		if(isNaN(lineColor)){
			//throw new Error('Line color required!');
		}
		
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		//If anchors be shown
		if(objParams.showAnchor){
			//Anchor bg color be specified for certain
			var anchorBgColor:Number = objParams.anchorBgColor;
			if(isNaN(anchorBgColor)){
				//throw new Error('Anchor fill color required!');
			}
			//Derive the darker shade of anchor fill color as border color if not specified.
			var anchorBorderColor:Number = (isNaN(objParams.anchorBorderColor))? getDarkColor(anchorBgColor, 0.9) : objParams.anchorBorderColor;
			//Number of sides of anchor polygon (default ten, which simulates a circular anchor)
			var anchorSides:Number = (isNaN(objParams.anchorSides)) ? 10 : objParams.anchorSides;
			
			var anchorRadius:Number = Math.min(width, height) * intraIconGraphicsPercent*0.25;
			var startingAngle:Number = objParams.startingAngle;
		}
		
		//------------ DRAWING ------------//
		
		var xCenter:Number = width/2;
		var yCenter:Number = height/2;
		
		var r:Number = intraIconGraphicsPercent * width/2;
		
		//Draw line
		canvas_mc.lineStyle(2, lineColor);
		canvas_mc.moveTo(xCenter - r, yCenter);
		canvas_mc.lineTo(xCenter + r, yCenter);
		
		//Draw anchor
		if(objParams.showAnchor){
			canvas_mc.moveTo(0,0);
			canvas_mc.lineStyle(0, anchorBorderColor);
			canvas_mc.beginFill(anchorBgColor);
			DrawingExt.drawPoly(canvas_mc, xCenter, yCenter, anchorSides, anchorRadius, startingAngle);
			canvas_mc.endFill();
		}
	}
	
	/**
	 * draw_wedge method draws the icon graphics for pie/doughnut chart.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_wedge(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var fillColor:Number = objParams.fillColor;
		
		if(isNaN(fillColor)){
			//throw new Error('Fill color required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(fillColor, 0.9) : objParams.borderColor;
		
		//---------//
		
		var gutter:Number = 1.2;
		
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		var radius:Number = Math.min(width, height) * intraIconGraphicsPercent/2;
		
		var xCenter:Number = width/2;
		var yCenter:Number = height/2;
		
		var arcAngle:Number = 120;
		//----------------- FILL -------------------//
		//Primary wedge (filled with the primary color)
		var startingAngle:Number = -arcAngle/2;
		
		canvas_mc.lineStyle(0, borderColor);
		canvas_mc.beginFill(fillColor);
		DrawingExt.drawCircle(canvas_mc, xCenter+gutter, yCenter, radius, radius, startingAngle, arcAngle);
		canvas_mc.lineTo(xCenter+gutter, yCenter);
		canvas_mc.endFill();
		//--------------------//
		// One of the two secondary wedges
		startingAngle += arcAngle;
		var delX:Number = gutter*Math.cos(Math.PI/3);
		var delY:Number = gutter*Math.sin(Math.PI/3);
		
		canvas_mc.lineStyle(0, borderColor, 40);
		canvas_mc.beginFill(fillColor, 30);
		DrawingExt.drawCircle(canvas_mc, xCenter-delX, yCenter-delY, radius, radius, startingAngle, arcAngle);
		canvas_mc.lineTo(xCenter-delX, yCenter-delY);
		canvas_mc.endFill();
		//----------------------//
		// The other one of the two secondary wedges
		startingAngle += arcAngle;
		
		canvas_mc.lineStyle(0, borderColor, 40);
		canvas_mc.beginFill(fillColor, 15);
		DrawingExt.drawCircle(canvas_mc, xCenter-delX, yCenter+delY, radius, radius, startingAngle, arcAngle);
		canvas_mc.lineTo(xCenter-delX, yCenter+delY);
		canvas_mc.endFill();
		//----------------------//
	}
	
	/**
	 * draw_spoke method draws the icon graphics for charts like Box and whisker.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_spoke(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var lineColor:Number = objParams.lineColor;
		var lineThickness:Number = (isNaN(objParams.lineThickness))? 0 : objParams.lineThickness;
		
		if(isNaN(lineColor)){
			//throw new Error('Line color required!');
		}
		
		//------------- FILL --------------//
		
		var gutter:Number = 1.2;
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var radius:Number = Math.min(width, height) * intraIconGraphicsPercent/2;
		var sides:Number = (!isNaN(objParams.sides) && objParams.sides >= 3)? Math.round(objParams.sides) : 5;
		var startingAngle:Number = objParams.startingAngle;
		
		var xCenter:Number = width/2;
		var yCenter:Number = height/2;
		
		canvas_mc.lineStyle(lineThickness, lineColor);
		DrawingExt.drawSpoke(canvas_mc, xCenter, yCenter, sides, radius, startingAngle);
	}
	
	/**
	 * draw_rectangle method draws the rectangular icon graphics.
	 * @param	canvas_mc	The MC to draw in
	 * @param	width		Width of bounding rectangle of icon
	 * @param	height		Height of bounding rectangle of icon
	 * @param	objParams	Icon cosmetic params
	 */
	private static function draw_boxnwhisker(canvas_mc:MovieClip, width:Number, height:Number, objParams:Object):Void{
		
		var upperFillColor:Number = objParams.upperFillColor;
		var lowerFillColor:Number = objParams.lowerFillColor;
		
		if(isNaN(upperFillColor) || isNaN(lowerFillColor) ){
			//throw new Error('Fill colors required!');
		}
		//Derive the darker shade of fill color as border color if not specified.
		var borderColor:Number = (isNaN(objParams.borderColor))? getDarkColor(upperFillColor, 0.9) : objParams.borderColor;
		
		//-------------- FILL ---------------//
		
		var intraIconGraphicsPercent:Number = objParams.intraIconGraphicsPercent;
		
		var w:Number = width * intraIconGraphicsPercent;
		var h:Number = height * intraIconGraphicsPercent;
		
		var xIni:Number = ((1 - intraIconGraphicsPercent)/2) * width;
		var yIni:Number = ((1 - intraIconGraphicsPercent)/2) * height;
		
		canvas_mc.lineStyle(2, borderColor, 100, true, "normal", "none");
		
		
		canvas_mc.beginFill(upperFillColor);
		
		canvas_mc.moveTo(xIni, yIni);
		canvas_mc.lineTo(xIni + w, yIni);
		canvas_mc.lineTo(xIni + w, yIni + h/2);
		//Do not draw the border line for the central divider - alpha set to 0
		canvas_mc.lineStyle(2, borderColor, 0, true, "normal", "none");
		//Reset line style
		canvas_mc.lineTo(xIni, yIni + h/2);
		canvas_mc.lineStyle(2, borderColor, 100, true, "normal", "none");
		canvas_mc.lineTo(xIni, yIni);
		
		canvas_mc.endFill();
		
		//Derive the darker shade of fill color as border color if not specified.
		borderColor = (isNaN(objParams.borderColor))? getDarkColor(lowerFillColor, 0.9) : objParams.borderColor;
		
		canvas_mc.lineStyle(2, borderColor, 100, true, "normal", "none");
		canvas_mc.beginFill(lowerFillColor);
		
		canvas_mc.moveTo(xIni, yIni + h/2);
		//Do not draw the border line for the central divider - alpha set to 0
		canvas_mc.lineStyle(2, borderColor, 0, true, "normal", "none");
		//Reset line style
		canvas_mc.lineTo(xIni + w, yIni + h/2);
		canvas_mc.lineStyle(2, borderColor, 100, true, "normal", "none");
		canvas_mc.lineTo(xIni + w, yIni + h);
		canvas_mc.lineTo(xIni, yIni + h);
		canvas_mc.lineTo(xIni, yIni + h/2);
		
		canvas_mc.endFill();
		
		//----------- EFFECTS ------------//
		canvas_mc.lineStyle();
		
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h, Math.PI/2, 0, 0);
		canvas_mc.beginGradientFill('linear', [0xFFFFFF, 0xFFFFFF, 0xFFFFFF], [90, 50, 0], [0, 70, 120], matrix);
		var gutter:Number = 1;
		canvas_mc.moveTo(xIni + gutter, yIni + gutter);
		canvas_mc.lineTo(xIni + w - gutter, yIni + gutter);
		canvas_mc.lineTo(xIni + w - gutter, yIni + h - gutter);
		canvas_mc.lineTo(xIni + gutter, yIni + h - gutter);
		canvas_mc.lineTo(xIni + gutter, yIni + gutter);
		
		canvas_mc.endFill();
		
	}
	
}