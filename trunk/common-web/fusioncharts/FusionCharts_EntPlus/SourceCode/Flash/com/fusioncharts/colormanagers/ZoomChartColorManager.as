/**
* @class ZoomChartColorManager
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
*
* ZoomChartColorManager class helps manage the default colors and palettes
* for zoom charts.
*/
import com.fusioncharts.helper.DefaultColors;
import com.fusioncharts.extensions.ColorExt;
class com.fusioncharts.colormanagers.ZoomChartColorManager extends DefaultColors {
	//Reference to the palette that this color Manager class uses
	private var palette:Number;
	//If the color manager has to work on a single color them
	private var themeColor:String;
	//Flag indicating whether we're using a theme color, or a single color
	private var theme:Boolean;
	//Containers to store colors, ratios and alphas for 2D Chart
	/**
	 * Constructor function.
	 *	@param	paletteId	Palette Id for the chart.
	 *	@param	themeColor	Color code if the chart uses single color theme.
	*/
	function ZoomChartColorManager(paletteId:Number, themeColor:String) {
		//Call parent constructor
		super();
		//Store parameters
		this.palette = paletteId;
		//Validation: If palette is undefined or <1 or >5, we select 1 as default palette
		if (this.palette == undefined || this.palette == null || this.palette<1 || this.palette>this.numPalettes) {
			this.palette = 1;
		}
		//Theme color   
		this.themeColor = themeColor;
		//Update flag theme, if we've to use theme color
		this.theme = (this.themeColor == "" || this.themeColor == undefined) ? false : true;
		this._iterator = 0;
		//Store colors now
	}
	// ----------- Accessor functions to access colors for different elements ------------//
	/**
	* The following functions return default colors for a 2D Chart, based on the palette
	* selected by the user. Also, if the user has selected a single color theme, we calculate
	* the same and return accordingly.
	*/
	//--------- Chart Background color -----------//
	public function get2DBgColor():String {
		//Background color for 2D Chart
		if (theme) {
			return (ColorExt.getLightColor(this.themeColor, 0.35).toString(16)+","+ColorExt.getLightColor(this.themeColor, 0.1).toString(16));
		} else {
			return this.bgColor[this.palette-1];
		}
	}
	public function get2DBgAlpha():String {
		//Background alpha for 2D Chart
		return this.bgAlpha[this.palette-1];
	}
	public function get2DBgAngle():Number {
		//Background angle for 2D Chart
		return this.bgAngle[this.palette-1];
	}
	public function get2DBgRatio():String {
		//Background ratio for 2D Chart
		return this.bgRatio[this.palette-1];
	}
	public function get2DBorderColor():String {
		//Chart Border Color
		if (theme) {
			return (ColorExt.getDarkColor(this.themeColor, 0.6).toString(16));
		} else {
			return this.borderColor[this.palette-1];
		}
	}
	public function get2DBorderAlpha():Number {
		//Chart Border Alpha 2D Chart
		return this.borderAlpha[this.palette-1];
	}
	//----------- Canvas color ------------//
	public function get2DCanvasBgColor():String {
		//Canvas background color for 2D Chart
		if (theme) {
			return (ColorExt.getLightColor(this.themeColor, 0.05).toString(16));
		} else {
			return this.canvasBgColor[this.palette-1];
		}
	}
	public function get2DCanvasBgAngle():Number {
		//Canvas background angle for 2D Chart
		return this.canvasBgAngle[this.palette-1];
	}
	public function get2DCanvasBgAlpha():String {
		//Canvas background alpha for 2D Chart
		return this.canvasBgAlpha[this.palette-1];
	}
	public function get2DCanvasBgRatio():String {
		//Canvas background ratio for 2D Chart
		return this.canvasBgRatio[this.palette-1];
	}
	public function get2DCanvasBorderColor():String {
		//Canvas border color for 2D Chart
		if (theme) {
			return (ColorExt.getDarkColor(this.themeColor, 0.8).toString(16));
		} else {
			return this.canvasBorderColor[this.palette-1];
		}
	}
	public function get2DCanvasBorderAlpha():Number {
		//Canvas border alpha for 2D Chart
		return this.canvasBorderAlpha[this.palette-1];
	}
	public function get2DScrollBarColor():String{
		//Color of scroll bar
		if (theme) {
			return (ColorExt.getLightColor(this.themeColor, 0.5).toString(16));
		} else {			
			return (ColorExt.getLightColor(this.canvasBorderColor[this.palette-1], 0.5).toString(16));
		}
	}
	//------- Div lines and grids color ----------//
	public function get2DDivLineColor (index : Number) : String
	{
		//Div line color for 2D Chart
		if (theme) {
			return (ColorExt.getLightColor(this.themeColor, 0.2).toString(16));
		} else {
			return divLineColor [getPaletteIndex (index)];
		}
	}
	public function get2DDivLineAlpha (index : Number) : Number
	{
		//Div Line alpha for 2D Chart
		return divLineAlpha [getPaletteIndex (index)];
	}
	public function get2DAltHGridColor():String {
		//Alternate horizontal grid color for 2D Chart
		if (theme) {
			return (ColorExt.getLightColor(this.themeColor, 0.2).toString(16));
		} else {
			return this.altHGridColor[this.palette-1];
		}
	}
	public function get2DAltHGridAlpha():Number {
		//Alternate horizontal grid alpha for 2D Chart
		return this.altHGridAlpha[this.palette-1];
	}
	public function get2DAltVGridColor():String {
		//Alternate vertical grid color for 2D Chart
		if (theme) {
			return (ColorExt.getLightColor(this.themeColor, 0.8).toString(16));
		} else {
			return this.altVGridColor[this.palette-1];
		}
	}
	public function get2DAltVGridAlpha():Number {
		//Alternate vertical grid alpha for 2D Chart
		return this.altVGridAlpha[this.palette-1];
	}
	//----- Tool tip color --------//
	public function get2DToolTipBgColor():String {
		//Tool Tip background Color for 2D Chart
		return this.toolTipBgColor[this.palette-1];
	}
	public function get2DToolTipBorderColor():String {
		//Tool tip Border Color for 2D Chart
		if (theme) {
			return (ColorExt.getDarkColor(this.themeColor, 0.8).toString(16));
		} else {
			return this.toolTipBorderColor[this.palette-1];
		}
	}
	public function get2DToolTipBarColor():String{
		if (theme) {
			return this.themeColor;
		} else {
			return this.canvasBorderColor[this.palette-1];
		}
	}
	//-------- Font Colors ---------//
	public function get2DBaseFontColor():String {
		//Base Font for 2D Chart
		if (theme) {
			return (this.themeColor);
		} else {
			return this.baseFontColor[this.palette-1];
		}
	}
	//------- Legend Color --------//
	public function get2DLegendBgColor():String {
		//Legend background Color for 2D Chart
		return this.legendBgColor[this.palette-1];
	}
	public function get2DLegendBorderColor():String {
		//Legend Border Color
		if (theme) {
			return (ColorExt.getDarkColor(this.themeColor, 0.8).toString(16));
		} else {
			return this.legendBorderColor[this.palette-1];
		}
	}
	//------- Plot related colors --------//
	public function get2DPlotGradientColor():String {
		//Plot Gradient Color
		return this.plotGradientColor[this.palette-1];
	}
	public function get2DPlotBorderColor():String {
		//Plot Border Color
		if (theme) {
			return (ColorExt.getDarkColor(this.themeColor, 0.85).toString(16));
		} else {
			return this.plotBorderColor[this.palette-1];
		}
	}
	public function get2DPlotFillColor():String {
		//Plot Fill Color
		if (theme) {
			return (ColorExt.getDarkColor(this.themeColor, 0.85).toString(16));
		} else {
			return this.plotFillColor[this.palette-1];
		}
	}	
	//--------- Tool bar buttons ---------------//	
	public function get2DToolBarButtonColor():String{
		//Border color for enabled tool-bar buttons
		if (theme) {
			return (this.themeColor);
		} else {
			return this.canvasBorderColor[this.palette-1];
		}
	}
	public function get2DToolBarButtonFontColor():String{
		//Border color for enabled tool-bar buttons
		if (theme) {
			return (this.themeColor);
		} else {
			return this.canvasBorderColor[this.palette-1];
		}
	}
	//----- Mouse Cursor colors ----//
	public function get2DMouseCursorColor():String{
		if (theme) {
			return this.themeColor;
		} else {
			return this.canvasBorderColor[this.palette-1];
		}
	}
	//Re-set method resets the iterator to 0.
	public function reset():Void {
		_iterator = 0;
	}
}
