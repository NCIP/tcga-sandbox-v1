/**
 * @class CreateImageLabel
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 * CreateImageLabel class helps us to create a Node with Image support.
*/
import com.fusioncharts.helper.Utils;
class com.fusioncharts.helper.CreateImageLabel extends MovieClip {
	//Instance variables
	private var parent:MovieClip;
	private var imageNodeCont:MovieClip;
	private var imageMC:MovieClip;
	private var label:String;
	private var pW:Number;
	private var pH:Number;
	private var imageW:Number;
	private var imageH:Number;
	private var imageAlign:String;
	private var labelAlign:String;
	//Label Object
	private var labelStyleObj:Object;
	private var url:String;
	//MovieClip Loader
	private var mcLoader:MovieClipLoader;
	//Listener
	private var mclListener:Object;
	//Constructor function
	/**
	 * Here, we initialize the objects.
	 *	@param		target	Movie clip in which we've to create the
	 *						image Node.
	 *	@param		depth	Depth at which we create image Node.
	 *	@param		label	Label to display in the Node.
	 *	@param		pW		Width of the node.
	 *	@param		pH		Height of the node.
	 *	@param		labelStyleObj	label object having all the style properties of the DataLabels
	*/
	function CreateImageLabel(target:MovieClip, depth:Number, label:String, pW:Number, pH:Number, labelStyleObj:Object) {
		//Store parameters in instance variables
		this.parent = target;
		this.label = label;
		this.pW = pW;
		this.pH = pH;
		this.labelStyleObj = labelStyleObj;
		//Create the required movieclip for the dialog box
		this.imageNodeCont = this.parent.createEmptyMovieClip("ImageNodeContainer", depth);
		//Create a movieclip within the dialog box Container for panel
		this.imageMC = this.imageNodeCont.createEmptyMovieClip("Image", 1);
		//Hide the hand cursor
		this.imageMC.useHandCursor = false;
		//Set the default parameters
		this.imageAlign = "top";
		this.labelAlign = "top";
		this.imageW = pW;
		this.imageH = pH;
		//MovieClip Loader for the image
		this.mcLoader = new MovieClipLoader();
		//Create a listener so that we can set the height and width during initialization
		//Initialize the listener
		this.mclListener = new Object();
	}
	/**
	 * setParams method assign the parameters for the image and text 
	*/
	public function setParams(url:String, imageW:Number, imageH:Number, imageAlign:String, labelAlign:String):Void {
		this.url = url;
		this.imageW = imageW;
		this.imageH = imageH;
		this.imageAlign = imageAlign;
		this.labelAlign = labelAlign;
	}
	/**
	 * draw method draw the final Node with image and text properly aligned
	*/
	public function draw():Void {
		//Store the class reference
		var classRef:Object = this;
		//Check the image Height and width 
		if (this.imageW>this.pW) {
			this.imageW = this.pW;
		}
		if (this.imageH>this.pH) {
			this.imageH = this.pH;
		}
		//Define the onLoadInit of the Loader Class  
		mclListener.onLoadInit = function(target_mc:MovieClip) {
			//Check if height and width is given
			if(classRef.imageW ==-1) { 
				classRef.imageMC._width = (classRef.imageMC._width > classRef.pW )?(classRef.pW):(classRef.imageW);
			} else {
				classRef.imageMC._width = classRef.imageW;
			}
			if (classRef.imageH == -1) {
				classRef.imageMC._height = (classRef.imageMC._height > classRef.pH )?(classRef.pH ):(classRef.imageH );
			}
			else {
				classRef.imageMC._height = classRef.imageH;
			}
			//Set the new position based on the alignment
			//X position is always the same for all the Vertical Alignment
			classRef.imageMC._x = -classRef.imageMC._width/2;
			//Now set the Y position
			if (classRef.imageAlign.toUpperCase() == "TOP") {
				classRef.imageMC._y = -classRef.pH/2;
			} else if (classRef.imageAlign.toUpperCase() == "BOTTOM") {
				classRef.imageMC._y = classRef.pH/2-classRef.imageMC._height;
			} else {
				classRef.imageMC._y = -classRef.imageMC._height/2;
			}
			//Now Set the text alignment
			if (classRef.labelAlign.toUpperCase() == "TOP")
			{
				classRef.labelStyleObj.vAlign = "BOTTOM";
				//Now create the Text field having the required label
				//Utils.createText(false, classRef.label, classRef.imageNodeCont, 2, 0, -classRef.pH/2, 0, classRef.labelStyleObj, true, classRef.imageMC._width, classRef.imageMC._height);
				Utils.createText(false, classRef.label, classRef.imageNodeCont, 2, 0, -classRef.pH/2, 0, classRef.labelStyleObj, true, classRef.pW, (classRef.pH-classRef.imageMC._height));
			}
			else if (classRef.labelAlign.toUpperCase() == "BOTTOM")
			{
				classRef.labelStyleObj.vAlign = "TOP";
				//Now create the Text field having the required label
				//Utils.createText(false, classRef.label, classRef.imageNodeCont, 2, 0, classRef.pH/2, 0, classRef.labelStyleObj, true, classRef.imageMC._width, classRef.imageMC._height);
				Utils.createText(false, classRef.label, classRef.imageNodeCont, 2, 0, classRef.pH/2, 0, classRef.labelStyleObj, true, classRef.pW, (classRef.pH-classRef.imageMC._height));
			}
			else
			{
				classRef.labelStyleObj.vAlign = "MIDDLE";
				//Now create the Text field having the required label
				//Utils.createText(false, classRef.label, classRef.imageNodeCont, 2, 0, 0, 0, classRef.labelStyleObj, true, classRef.pW, classRef.imageMC._height);
				Utils.createText(false, classRef.label, classRef.imageNodeCont, 2, 0, 0, 0, classRef.labelStyleObj, true, classRef.imageMC._width, (classRef.pH-classRef.imageMC._height));
			}
		};
		//Load the image in the image Container
		this.mcLoader.loadClip(this.url, this.imageMC);
		//Register the listener for the loader Class
		this.mcLoader.addListener(mclListener);
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	*/
	public function destroy():Void {
		//Delete the movieclip
		this.imageNodeCont.removeMovieClip();
		//Remove the listener
		this.mcLoader.removeListener(this.mclListener);
		//Delete the listener object
		delete this.mclListener;
	}
}
