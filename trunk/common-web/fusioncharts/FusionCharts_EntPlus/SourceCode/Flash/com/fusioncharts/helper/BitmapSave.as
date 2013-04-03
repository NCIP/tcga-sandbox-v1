/**
 * @class BitmapData
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 *
 * Version History:
 * 25th January, 2008:
 * Fixed issue where right border was not being exported.
 * It was because of -1 in if (clr==lClr && i<classRef.w-1)
 * We were not iterating through the rightmost pixel. 
 *
 * BitmapData class helps capture the view state of a given MC
 * in a string consisting of color codes for each pixel.
*/
import flash.display.BitmapData;
import mx.events.EventDispatcher;
import flash.geom.Rectangle;
import flash.geom.Matrix;
import flash.geom.ColorTransform;
class com.fusioncharts.helper.BitmapSave {
	//Interval id
	private var iid:Number;
	//Movie clip to capture
	private var mc:MovieClip;
	//Points
	private var x:Number;
	private var y:Number;
	private var w:Number;
	private var h:Number;
	//Color which is to be left as blank
	private var blankColor:Number;
	//Captured data
	private var out:String;
	//What row are we in?
	private var row:Number = 0;
	//Reference to the bitmap data
	private var bmp:BitmapData;
	//Flag indicating whether export is in progress
	private var inProcess:Boolean;
	/**
	 * Constructor to store the parameters.
	*/
	function BitmapSave(mc:MovieClip, x:Number, y:Number, w:Number, h:Number, blankColor:Number) {
		//Store the parameters
		this.mc = mc;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.blankColor = blankColor;
		this.inProcess = false;
		//Initialize the event dispatcher
		mx.events.EventDispatcher.initialize(this);
	}
	/**
	 * capture method captures the bitmap data in the array.
	*/
	public function capture():Void {
		//Here, we simply take a snapshot of the bitmap in
		//a pre defined interval, so as not to freeze the UI.
		//Initialize as empty string
		out = "";
		row = 0;
		//Create a matrix and flip it
		var mt = new Matrix();
		//Shift the matrix by -x,-y (if we've to take snap of a displaced portion)
		mt.translate(-x, -y);
		//Take snapshot of bitmap
		this.bmp = new BitmapData(this.w, this.h, false);
		//Paint into the bitmap.
		this.bmp.draw(this.mc, mt, new ColorTransform(), 1, new Rectangle(0, 0, w, h));
		//Next, run the parse method every 5 ms
		this.iid = setInterval(generatePixels, 5, this);
		//Set flag that bitmap export is in process
		this.inProcess = true;
		//Delete matrix
		delete mt;
	}
	/**
	 * generatePixels generates the string representation of the pixels and stores
	 * it in out.
	 * @param	classRef	Reference to self class.
	*/
	private function generatePixels(classRef:BitmapSave) {
		var clr:Number;
		//Last used color
		var lClr:Number = classRef.blankColor;
		//Repeat factor
		var rpt:Number = 0;
		//Flag to set whether we've to ignore the color.
		var igColor:Boolean = false;
		if (classRef.row<classRef.h) {
			for (var i:Number = 0; i<=classRef.w; i++) {
				clr = classRef.bmp.getPixel(i, classRef.row);
				//If its the first column, set last found color as color of this pixel.
				if (i == 0) {
					lClr = clr;
				}
				//Do a horizontal scan of how many repeats this color has. 
				if (clr == lClr && i<classRef.w) {
					rpt++;
				} else {
					//Add the last color which was not same
					if (igColor) {
						//Append empty string
						classRef.out += "_"+rpt+((i == classRef.w) ? "" : ",");
					} else {
						classRef.out += lClr.toString(16)+"_"+rpt+((i == classRef.w) ? "" : ",");
					}
					//Set flag back to normal
					igColor = false;
					//Reset repeat counter
					rpt = 1;
					//Set last color as current color
					lClr = clr;
				}
				//If this pixel contains color that we've to ignore
				if (!igColor && clr == classRef.blankColor) {
					igColor = true;
				}
			}
			//Append row separator character (semi colon)
			classRef.out += ((classRef.row == classRef.h-1) ? "" : ";");
			//Dispatch the progress achieved
			classRef.conveyProgress(Math.floor((classRef.row/classRef.h)*100));
			//Increment row
			classRef.row++;
		} else {
			//Stop iteration
			classRef.stopIteration();
		}
	}
	/**
	 * stopIteration method stops the iteration of generatePixels method, when
	 * the entire bitmap has been converted to pixels.
	*/
	private function stopIteration():Void {
		//Clear interval
		clearInterval(this.iid);
		//Dispose the bitmap
		this.bmp.dispose();
		//Update flag that process is over
		this.inProcess = false;
		//Dispatch the event and result
		this.dispatchEvent({type:"onCaptureComplete", target:this, out:out});
	}
	/**
	 * Allows for cancellation of export as bitmap mid-way
	*/
	public function cancel():Void {		
		if (inProcess){
			//Clear interval
			clearInterval(this.iid);
			//Dispose the bitmap
			this.bmp.dispose();
			//Update flag that process is over
			this.inProcess = false;
		}
	}
	/**
	 * Returns flag whether the bitmap exporting is currently in progress.
	*/
	public function isInProcess():Boolean{
		return this.inProcess;
	}
	
	/**
	 * Conveys the percent collection done to calling module
	*/
	private function conveyProgress(progress:Number) {
		this.dispatchEvent({type:"onProgress", target:this, percentDone:progress});
	}
	//These functions are defined in the class to prevent
	//the compiler from throwing an error when they are called
	//They are not implemented until EventDispatcher.initalize(this)
	//overwrites them.
	public function dispatchEvent() {
	}
	public function addEventListener() {
	}
	public function removeEventListener() {
	}
	public function dispatchQueue() {
	}
}
