/**
* @class Utils
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010

* Utils class helps us bunch a group of utility functions
*/
import com.fusioncharts.extensions.StringExt;
import flash.display.BitmapData;
import com.fusioncharts.helper.ToolTip;
class com.fusioncharts.helper.Utils {
	//Flag that stores whether ellipses are to be added to textfields.
	public static var ADD_ELLIPSES:Boolean = true;
	//Reference to application tool-tip instance. If not set, any overflowing
	//Rotated text fields wouldn't show tool-tips
	public static var APP_TOOLTIP:ToolTip;
	//Limiting number of calls to avoid infinite recursion
	private static var RECURSION_LIMIT:Number = 200;
	/**
	* Since Utils class is just a grouping of utility functions,
	* we do not want any instances of it (as all methods wil be static).
	* So, we declare a private constructor
	*/
	private function Utils() {
		//Private constructor to avoid creation of instances
	}
	/**
	* getFirstValue method is is used to return the first non-null
	* non-undefined non-empty value in a list of values provided to
	* this method. To this method you can pass a list of values like
	* a,b,c,d,e in a preferential order and this method will return the
	* first non-null non-undefined value in this list.
	*/
	public static function getFirstValue() {
		for (var i = 0; i<arguments.length; i++) {
			if (arguments[i] != null && arguments[i] != undefined && arguments[i] != "") {
				return arguments[i];
			}
		}
		return "";
	}
	/**
	* getFirstNumber method is is used to return the first non-null
	* non-undefined non-empty NUMBER in a list of values provided to
	* this method. To this method you can pass a list of numbers like
	* a,b,c,d,e in a preferential order and this method will return the
	* first non-null non-undefined NUMBER in this list.
	*/
	public static function getFirstNumber():Number {
		for (var i = 0; i<arguments.length; i++) {
			if (arguments[i] != null && arguments[i] != undefined && arguments[i] != "" && isNaN(Number(arguments[i])) == false) {
				return Number(arguments[i]);
			}
		}
		return 0;
	}
	/**
	* createText method returns textfield or MovieClip, 
	* whichever pertinent for text display, taking care of
	* rotation.
	*	@param		simulationMode	Whether we're simulating the text field creation
	*								to get width & height. Or, if we're actually
	*								rendering it. In simulation mode, we do not
	*								re-position the textfield to optimize the flow.
	*	@param		targetMC	Movie clip inside which we've to
	*							create the text field.
	*	@param		strText		Text to be shown in the text field
	*	@param		depth		Depth at which the text field is to
	*							be created
	*	@param		xPos		x-position of the text field
	*	@param		yPos		y-position of the text field
	*	@param		rotation	Numerical value of rotation
	*	@param		objStyle	Object containing style properties for
	*							the text field.
	*							This object should necessarily contain
	*							the following parameters:
	*				align		Horizontal alignment. Possible values:
	*							"left", "center" or "right"
	*				vAlign		Vertical alignment of text. Possible values:
	*							"top", "middle" or "bottom"
	*				bold		Boolean value indicating whether the text
	*							would be bold.
	*				italic		Boolean value indicating whether the text
	*							would be italic.
	*				underline	Boolean value indicating whether the text
	*							would be underline.
	*				font		Font face for the text.
	*				size		Font size for the text
	*				color		Color in RRGGBB format for the text.
	*				isHTML		Boolean value indicating whether text would
	*							be rendered as HTML.
	*				leftMargin	Left margin for text (in pixels)
	*				letterSpacing	Numerical value indicating the spacing
	*								between two letters
	*				bgColor		Hex Color RRGGBB or undefined value indicating
	*							the background color of the text field. If undefined,
	*							that means text field shouldn't have a background.
	*				borderColor	Hex Color RRGGBB or undefined value indicating
	*							border color of text field.
	*	@param		wrap		Boolean value indicating whether we need to wrap
	*							labels
	*	@param		width		If we've to wrap, width of textbox
	*	@param		height		If we've to wrap, height of text box
	*	@param		arrSizes	Array of textfield sizes to find best fit for slant labels (new label management)
	*	@return					An object cotaining reference to the text field that it
	*							creates and dimensions
	*/
	public static function createText(simulationMode:Boolean, strText:String, targetMC:MovieClip, depth:Number, xPos:Number, yPos:Number, rotation:Number, objStyle:Object, wrap:Boolean, width:Number, maxHeight:Number, arrSizes:Array):Object {
		//First up, we create a text format object and set the properties
		var tFormat:TextFormat = new TextFormat();
		//Font properties
		tFormat.font = objStyle.font;
		tFormat.size = objStyle.size;
		tFormat.color = parseInt(objStyle.color, 16);
		//Text decoration
		tFormat.bold = objStyle.bold;
		tFormat.italic = objStyle.italic;
		tFormat.underline = objStyle.underline;
		//Margin and spacing
		tFormat.leftMargin = objStyle.leftMargin;
		tFormat.letterSpacing = objStyle.letterSpacing;
		//---------------------//
		//Replace {br}, {BR} with new line break
		strText = StringExt.replace(strText, "{BR}", "\n");
		strText = StringExt.replace(strText, "{br}", "\n");
		//Also convert <BR> to /n.
		strText = StringExt.replace(strText, "<BR>", "\n");
		strText = StringExt.replace(strText, "&lt;BR&gt;", "\n");
		strText = StringExt.replace(strText, "<br>", "\n");
		strText = StringExt.replace(strText, "&lt;br&gt;", "\n");
		//---------------------// 
		if(!arrSizes){
			// Work with textfield for no rotation or Movieclip with Bitmap for rotation
			if (rotation != null && rotation != undefined && rotation != 0) {
				// For rotation, call to get the MC returned encapsulated with other params.
				return getTextMc(simulationMode, strText, targetMC, depth, xPos, yPos, rotation, tFormat, objStyle, wrap, width, maxHeight);
			} else {
				// For no rotation, call to get the horizontal textfield returned encapsulated with other params.
				return getTextField(simulationMode, strText, targetMC, depth, xPos, yPos, tFormat, objStyle, wrap, width, maxHeight);
			}
		} else {
			//For advanced label management in slant mode
			return getSlantTextMc(simulationMode, strText, targetMC, depth, xPos, yPos, rotation, tFormat, objStyle, wrap, arrSizes);
		}
	}
	
	
	/**
	* getSlantTextMc method creates a slanted text displaying Movieclip 
	* using bitmap on the chart based on the parameters 
	* provided.
	*	@param		simulationMode	Whether we're simulating the text field creation
	*								to get width & height. Or, if we're actually
	*								rendering it. In simulation mode, we do not
	*								re-position the textfield to optimize the flow.
	*	@param		targetMC		Movie clip inside which we've to
	*								create the text field.
	*	@param		strText			Text to be shown in the text field
	*	@param		depth			Depth at which the text field is to
	*								be created
	*	@param		xPos			x-position of the text field
	*	@param		yPos			y-position of the text field
	*	@param		rotation		Numerical value of rotation
	*	@param		tFormat			TextFormat object
	*	@param		objStyle		Object containing style properties for
	*								the text field.
	*								This object should necessarily contain
	*								the following parameters:
	*				align			Horizontal alignment. Possible values:
	*								"left", "center" or "right"
	*				vAlign			Vertical alignment of text. Possible values:
	*								"top", "middle" or "bottom"
	*				bold			Boolean value indicating whether the text
	*								would be bold.
	*				italic			Boolean value indicating whether the text
	*								would be italic.
	*				underline		Boolean value indicating whether the text
	*								would be underline.
	*				font			Font face for the text.
	*				size			Font size for the text
	*				color			Color in RRGGBB format for the text.
	*				isHTML			Boolean value indicating whether text would
	*								be rendered as HTML.
	*				leftMargin		Left margin for text (in pixels)
	*				letterSpacing	Numerical value indicating the spacing
	*								between two letters
	*				bgColor			Hex Color RRGGBB or undefined value indicating
	*								the background color of the text field. If undefined,
	*								that means text field shouldn't have a background.
	*				borderColor		Hex Color RRGGBB or undefined value indicating
	*								border color of text field.
	*	@param		wrap			Boolean value indicating whether we need to wrap
	*								labels
	*	@param		arrSizes		Set of different textfield sizes to search for best text fit
	*	@return						An object cotaining reference to the text field that it
	*								creates and dimensions. The dimensions returned are those of
	*								rotated text field (so width is the width of rotated text field)
	*/
	public static function getSlantTextMc(simulationMode:Boolean, strText:String, targetMC:MovieClip, depth:Number, xPos:Number, yPos:Number, rotation:Number, tFormat:TextFormat, objStyle:Object, wrap:Boolean, arrSizes:Array):Object {
		//Extract align and vAlign Properties from style Obj
		var alignPos:String = objStyle.align;
		var vAlignPos:String = objStyle.vAlign;
		//Whether text is overflowing - in wrap mode.
		var overflow:Boolean = false;
		//Special alignment position conditions for 315 rotation
		if (rotation == 315) {
			alignPos = "RIGHT";
			vAlignPos = "BOTTOM";
		}
		//Set internal text field alignment option for rotated text.  
		//This is applicable when the text needs wrapping or has
		//multiple lines. Then the different lines of text align
		//according to the angle and vertical position of the text field
		//w.r.t given x,y point.
		if (rotation>180) {
			switch (vAlignPos.toUpperCase()) {
			case "TOP" :
				tFormat.align = "left";
				break;
			case "MIDDLE" :
				tFormat.align = "center";
				break;
			case "BOTTOM" :
				tFormat.align = "right";
				break;
			}
		} else {
			switch (vAlignPos.toUpperCase()) {
			case "TOP" :
				tFormat.align = "right";
				break;
			case "MIDDLE" :
				tFormat.align = "center";
				break;
			case "BOTTOM" :
				tFormat.align = "left";
				break;
			}
		}
		
		//Create the actual text field object now. This will be used to draw the bitmap and flushed thereof.
		var a, b;
		var tf:TextField = targetMC.createTextField("Text_"+depth, depth, xPos, yPos, a, b);
		
		//Set the properties
		tf.multiline = true;
		tf.selectable = false;
		
		//Set the border color if required
		if (objStyle.borderColor != "") {
			tf.border = true;
			tf.borderColor = parseInt(objStyle.borderColor, 16);
		}
		//Set the background color if required   
		if (objStyle.bgColor != "") {
			tf.background = true;
			tf.backgroundColor = parseInt(objStyle.bgColor, 16);
		}
		//Whether HTML text or not?   
		tf.html = objStyle.isHTML;
		
		//Apply the text format
		tf.setNewTextFormat(tFormat);
		
		//For wrapped text (normally always true for this method call)
		if (wrap == true) {
			
			//Get metrics for single line display of the text
			var minWidthReqExt:Object = tFormat.getTextExtent(strText);
			//The original string length to check if best fit is achieved.
			var strLength:Number = strText.length;
			//Variable to hold the index of the size, that is found to display best text at any point of the best fit search.
			//By best fit, its meant to be maximum amount of text in display and in minimum number of lines. So, if same amount of text is displayed 
			//in 2 different sizes, the size with lower number of lines is considered as the better one.
			var sizeIdWithMaxText:Number = 0;
			//The sub-string length of the best size found at any point of the search.
			var maxTextSizeFound:Number = 0;
			
			var numSizes:Number = arrSizes.length;
			var width:Number, maxHeight:Number;
			
			//Searching for best fit of text against varying textfield sizes.
			for(var i = 0; i < numSizes; ++i){
				
				//Text in textfield gets changed when it undergoes overflow management. So, text is reset at the start of every new search.
				//Set the text 
				if (objStyle.isHTML) {
					//If it's HTML text, set as htmlText
					tf.htmlText = strText;
				} else {
					//Else, set as plain text
					tf.text = strText;
				}
				
				//Dimensions of textfield for the size under test.
				width = arrSizes[i].w;
				maxHeight = arrSizes[i].h;
				
				//Get text extent for this text. We simulate the textfield in given width
				//to see how much height it takes.
				//Tricky: Adding 1 to width to work around internal FP bug.
				var reqHeightExt:Object = tFormat.getTextExtent(strText, width+1);
				
				//Store the minimum width required - add 1 pixel for fixing internal rendering error
				width = Math.min(minWidthReqExt.textFieldWidth, reqHeightExt.textFieldWidth);
				//Tricky: Add 1 to width, in case of wrapped labels. This is avoid to 
				//a weird bug where Flash Player reports un-necessary overflow of textfield
				//content.
				width++;
				//Create the textfield with allotted width & a height that is the minimum
				//of height taken by textfield or the max height that was specified. This
				//enables the textfield to get contained in given width and height. And if
				//the given space was bigger, this uses lesser space.
				var reqHt:Number = (maxHeight == undefined) ? (reqHeightExt.textFieldHeight) : (Math.min(reqHeightExt.textFieldHeight, maxHeight));
				
				//Textfield width and height is reset.
				tf._width = width;
				tf._height = reqHt;
				
				//Set wordwrap
				tf.wordWrap = true;
				
				//Check if the height allotted cannot accommodate text, thereby resulting
				//to an overflow condition.
				if (reqHeightExt.textFieldHeight > maxHeight) {
					overflow = true;
				}
				
				//If the text is overflowing and ellipses need to be added.
				if (overflow && (Utils.ADD_ELLIPSES == true)) {
					handleTextOverflow(tf, tFormat);
				}
				
				//Finally, the sub-string length for this size is obtained.
				var finalTxtLength:Number = tf.text.length;
				
				//To check if full text is being shown.
				if(finalTxtLength == strLength){
					//This is definitely the best size.
					sizeIdWithMaxText = i;
					//Search ends.
					break;
				//To check for best sub-string found till now.
				} else {
					//If current sub-string is larger than the previously found best one.
					//Since, arrSizes holds textfield sizes in ascending order of number of lines, so equality is omitted in the check, to meet our criteria of best fit, as described above.
					if(maxTextSizeFound < finalTxtLength){
						//Best length recorded.
						maxTextSizeFound = finalTxtLength;
						//Id for best size recorded.
						sizeIdWithMaxText = i;
					}
				}
				
			}
			
			//------------------------------------//
			//Best size being found, its time to render the text finally.
			//Best width and height.
			width = arrSizes[sizeIdWithMaxText].w;
			maxHeight = arrSizes[sizeIdWithMaxText].h;
			
			//Reset the text 
			if (objStyle.isHTML) {
				//If it's HTML text, set as htmlText
				tf.htmlText = strText;
			} else {
				//Else, set as plain text
				tf.text = strText;
			}
			
			//Get text extent for this text. We simulate the textfield in given width
			//to see how much height it takes.
			//Tricky: Adding 1 to width to work around internal FP bug.
			var reqHeightExt:Object = tFormat.getTextExtent(strText, width+1);
			
			//Store the minimum width required - add 1 pixel for fixing internal rendering error
			width = Math.min(minWidthReqExt.textFieldWidth, reqHeightExt.textFieldWidth);
			//Tricky: Add 1 to width, in case of wrapped labels. This is avoid to 
			//a weird bug where Flash Player reports un-necessary overflow of textfield
			//content.
			width++;
			//Create the textfield with allotted width & a height that is the minimum
			//of height taken by textfield or the max height that was specified. This
			//enables the textfield to get contained in given width and height. And if
			//the given space was bigger, this uses lesser space.
			var reqHt:Number = (maxHeight == undefined) ? (reqHeightExt.textFieldHeight) : (Math.min(reqHeightExt.textFieldHeight, maxHeight));
			
			tf._width = width;
			tf._height = reqHt;
			
			//Set wordwrap
			tf.wordWrap = true;
			
			//Check if the height allotted cannot accommodate text, thereby resulting
			//to an overflow condition.
			if (reqHeightExt.textFieldHeight>maxHeight) {
				overflow = true;
			}
			
			//If the text is overflowing and ellipses have to be added.
			if (overflow && (Utils.ADD_ELLIPSES == true)) {
				handleTextOverflow(tf, tFormat);
			}
			
			//---------------------------------------//
			
		} else {
			//Set align position
			tf.autoSize = 'left';
		}

		//---------- Conversion of textfield to movieclip (bitmapdata) begin --------//
		
		//Get the height of textfield
		var originalH = tf._height;
		//Getting the BitmapData of the textfield, for using it instead of the textField itself to avoid embedding fonts.
		var bmp:BitmapData = getTxtBmp(tf);
		//The textfield is required no more.
		tf.removeTextField();
		//Movieclip created at the depth of the textfield destroyed.
		var dispItem:MovieClip = targetMC.createEmptyMovieClip('TextBmp_'+depth, depth);
		//Sub-container mc for alignment adjustments
		var mcBmp:MovieClip = dispItem.createEmptyMovieClip('mcBmp', 0);
		//Bitmapdata attached to the sub-movieclip created.
		mcBmp.attachBitmap(bmp, 0, 'auto', true);
		//---------- Conversion of textfield to movieclip (bitmapdata) end --------//
		//Set its position
		dispItem._x = xPos;
		dispItem._y = yPos;
		//Reposition the text display to achieve alignment for 315 degree rotation
		if (rotation == 315) {
			if (alignPos == 'RIGHT') {
				mcBmp._x -= mcBmp._width;
			} else if (alignPos == 'CENTER') {
				mcBmp._x -= mcBmp._width/2;
			}
		}
		//Set rotation  
		dispItem._rotation = rotation;
		//If tool-tip has to be shown for rotated text-fields that are overflowing
		if (!simulationMode && overflow == true && (Utils.APP_TOOLTIP instanceof ToolTip)) {
			//Set tool-text
			setItemToolTip(dispItem, strText);
		}
		//We now re-position the MC if not in simulation mode  
		if (!simulationMode) {
			//------------------------------------------------------------------//
			if (rotation == 270) {
				//Now, adjust the y orientation of the MC
				switch (alignPos.toUpperCase()) {
				case "LEFT" :
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						//Nothing
						break;
					case "MIDDLE" :
						dispItem._y = dispItem._y+(dispItem._height/2);
						break;
					case "BOTTOM" :
						dispItem._y = dispItem._y+dispItem._height;
						break;
					}
					break;
				case "CENTER" :
					dispItem._x = dispItem._x-(dispItem._width/2);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						break;
					case "MIDDLE" :
						dispItem._y += dispItem._height/2;
						break;
					case "BOTTOM" :
						dispItem._y += dispItem._height;
						break;
					}
					break;
				case "RIGHT" :
					dispItem._x = dispItem._x-(dispItem._width);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						break;
					case "MIDDLE" :
						dispItem._y += dispItem._height/2;
						break;
					case "BOTTOM" :
						dispItem._y += dispItem._height;
						break;
					}
					break;
				}
			}
			if (rotation == 90) {
				//Now, adjust the y orientation of the MC
				switch (alignPos.toUpperCase()) {
				case "LEFT" :
					dispItem._x += (dispItem._width);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						dispItem._y = dispItem._y-dispItem._height;
						break;
					case "MIDDLE" :
						dispItem._y = dispItem._y-(dispItem._height/2);
						break;
					case "BOTTOM" :
						//Nothing
						break;
					}
					break;
				case "CENTER" :
					dispItem._x += (dispItem._width/2);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						dispItem._y -= dispItem._height;
						break;
					case "MIDDLE" :
						dispItem._y -= dispItem._height/2;
						break;
					case "BOTTOM" :
						break;
					}
					break;
				case "RIGHT" :
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						dispItem._y -= dispItem._height;
						break;
					case "MIDDLE" :
						dispItem._y -= dispItem._height/2;
						break;
					case "BOTTOM" :
						break;
					}
					break;
				}
			} else if (rotation == 315) {
				//If the rotation angle is 315, it can only be for x-axis
				//names in the chart. So we directly alter the x and y position
				//irrespective of alignment position specified.
				var root2 = 1.42;
				dispItem._x -= (originalH/root2)/2;
				//Minus 4 to avoid gutter space
				dispItem._y -= 4;
			}
			//Round the dispItem x and y, to avoid blurring  
			dispItem._x = Math.round(dispItem._x);
			dispItem._y = Math.round(dispItem._y);
		}
		//------------------------------------------------------------------//   
		//Create an object which we'll return
		var rtnObj:Object = new Object();
		//Set 3 properties of the temporary object
		//width, height, tf
		rtnObj.width = dispItem._width;
		rtnObj.height = dispItem._height;
		//For fonts not included
		if (rtnObj.height<=4) {
			rtnObj.height = objStyle.size*2;
		}
		//Set the text field   
		rtnObj.tf = dispItem;
		//Set overflow flag
		rtnObj.overflow = overflow;
		//Delete the temporary objects
		delete tFormat;
		delete tf;
		//Return this object
		return rtnObj;
	}
	
	
	
	
	
	/**
	* getTextField method creates a text field on the chart based
	* on the parameters provided.
	*	@param		simulationMode	Whether we're simulating the text field creation
	*								to get width & height. Or, if we're actually
	*								rendering it. In simulation mode, we do not
	*								re-position the textfield to optimize the flow.
	*	@param		targetMC	Movie clip inside which we've to
	*							create the text field.
	*	@param		strText		Text to be shown in the text field
	*	@param		depth		Depth at which the text field is to
	*							be created
	*	@param		xPos		x-position of the text field
	*	@param		yPos		y-position of the text field
	*	@param		rotation	Numerical value of rotation
	*	@param		tFormat		TextFormat object
	*	@param		objStyle	Object containing style properties for
	*							the text field.
	*							This object should necessarily contain
	*							the following parameters:
	*				align		Horizontal alignment. Possible values:
	*							"left", "center" or "right"
	*				vAlign		Vertical alignment of text. Possible values:
	*							"top", "middle" or "bottom"
	*				bold		Boolean value indicating whether the text
	*							would be bold.
	*				italic		Boolean value indicating whether the text
	*							would be italic.
	*				underline	Boolean value indicating whether the text
	*							would be underline.
	*				font		Font face for the text.
	*				size		Font size for the text
	*				color		Color in RRGGBB format for the text.
	*				isHTML		Boolean value indicating whether text would
	*							be rendered as HTML.
	*				leftMargin	Left margin for text (in pixels)
	*				letterSpacing	Numerical value indicating the spacing
	*								between two letters
	*				bgColor		Hex Color RRGGBB or undefined value indicating
	*							the background color of the text field. If undefined,
	*							that means text field shouldn't have a background.
	*				borderColor	Hex Color RRGGBB or undefined value indicating
	*							border color of text field.
	*	@param		wrap		Boolean value indicating whether we need to wrap
	*							labels
	*	@param		width		If we've to wrap, width of textbox (in normal horizontal mode)
	*	@param		maxHeight	If we've to wrap, max height of text box  (in normal horizontal mode)
	*	@return					An object cotaining reference to the text field that it
	*							creates and dimensions
	*/
	public static function getTextField(simulationMode:Boolean, strText:String, targetMC:MovieClip, depth:Number, xPos:Number, yPos:Number, tFormat:TextFormat, objStyle:Object, wrap:Boolean, width:Number, maxHeight:Number):Object {
		//Extract align and vAlign Properties from style Obj
		var alignPos:String = objStyle.align;
		var vAlignPos:String = objStyle.vAlign;
		//Whether text is overflowing - in wrap mode.
		var overflow:Boolean = false;
		//Create the actual text field object now.
		var tf:TextField;
		//a & b are undefined variables
		//We want the initial text field size to be of flexible size as we're
		//not wrapping. So we do not define the width and height here
		//Based on wrap, we create the textfield
		if (wrap == true) {
			//Tricky: Add 1 to width, in case of wrapped labels. This is avoid to 
			//a weird bug where Flash Player reports un-necessary overflow of textfield
			//content.
			width++;
			//Get text extent for this text.
			var reqHeightExt:Object = tFormat.getTextExtent(strText, width);
			//Create the textfield with allotted width & a height that is the minimum
			//of height taken by textfield or the max height that was specified. This
			//enables the textfield to get contained in given width and height. And if
			//the given space was bigger, this uses lesser space.
			var reqHt:Number = (maxHeight == undefined) ? (reqHeightExt.textFieldHeight) : (Math.min(reqHeightExt.textFieldHeight, maxHeight));			
			//Create the textfield with minimum required width & height
			tf = targetMC.createTextField("Text_"+depth, depth, xPos, yPos, width, reqHt);
			//Set align position for text format in case of wrapping
			tFormat.align = alignPos;
			//Set wordwrap
			tf.wordWrap = true;
			//Check if the height allotted cannot accommodate text, thereby resulting
			//to an overflow condition.
			if (reqHeightExt.textFieldHeight>maxHeight) {
				tf.multiline = true;
				overflow = true;
			}
		} else {
			var a, b;
			tf = targetMC.createTextField("Text_"+depth, depth, xPos, yPos, a, b);
			//Set align position
			tf.autoSize = alignPos;
		}
		//Set the properties
		tf.multiLine = true;
		tf.selectable = false;
		//Set the border color if required
		if (objStyle.borderColor != "") {
			tf.border = true;
			tf.borderColor = parseInt(objStyle.borderColor, 16);
		}
		//Set the background color if required   
		if (objStyle.bgColor != "") {
			tf.background = true;
			tf.backgroundColor = parseInt(objStyle.bgColor, 16);
		}
		//Whether HTML text or not?   
		tf.html = objStyle.isHTML;
		//Set the text 
		if (objStyle.isHTML) {
			//If it's HTML text, set as htmlText
			tf.htmlText = strText;
		} else {
			//Else, set as plain text
			tf.text = strText;
		}
		//Apply the text format
		tf.setTextFormat(tFormat);
		//Store a copy of text
		var textCpy:String = strText;
		//----------- Overflow handling begin -------------//
		//If the text is overflowing and ellipses have to be added.
		if (overflow && (Utils.ADD_ELLIPSES == true)) {
			handleTextOverflow(tf, tFormat);
		}
		if (!simulationMode && overflow == true && (Utils.APP_TOOLTIP instanceof ToolTip)) {
			//Getting the BitmapData of the textfield, for using it instead of the textField itself to avoid embedding fonts.
			var bmp:BitmapData = getTxtBmp(tf);
			//The textfield is required no more.
			tf.removeTextField();
			//Movieclip created at the depth of the textfield destroyed.
			var dispItem:MovieClip = targetMC.createEmptyMovieClip('TextBmp_'+depth, depth);
			//Sub-container mc for alignment adjustments
			var mcBmp:MovieClip = dispItem.createEmptyMovieClip('mcBmp', 0);
			//Bitmapdata attached to the sub-movieclip created.
			mcBmp.attachBitmap(bmp, 0, 'auto', true);
			//Set its position
			dispItem._x = xPos;
			dispItem._y = yPos;
			//Set tool-text
			setItemToolTip(dispItem, textCpy);
		}
		//----------- Overflow handling complete -------------//  
		//Get the height of textfield
		var originalH = tf._height;
		//If the textfield is not a movieclip, store accordingly.
		if (!(!simulationMode && overflow == true && (Utils.APP_TOOLTIP instanceof ToolTip))) {
			//Store reference based on action
			var dispItem:TextField = tf;
		}
		//We now re-position the text field if not in simulation mode 
		if (!simulationMode) {
			switch (vAlignPos.toUpperCase()) {
			case "TOP" :
				//Top(of the ypos mid line)
				//        TEXT HERE
				//---------MID LINE---------
				//       (empty space)
				dispItem._y = dispItem._y-(dispItem._height);
				break;
			case "MIDDLE" :
				//       (empty space)
				//---------TEXT HERE---------
				//       (empty space)
				dispItem._y = dispItem._y-(dispItem._height/2);
				break;
			case "BOTTOM" :
				//       (empty space)
				//---------MID LINE---------
				//         TEXT HERE
				//No need to change - already at this position
				break;
			}
			//If in wrap mode, we need to horizontal align too
			if (wrap == true) {
				switch (alignPos.toUpperCase()) {
				case "LEFT" :
					//Nothing to do - pre-left aligned
					break;
				case "CENTER" :
					dispItem._x = dispItem._x-(dispItem._width/2);
					break;
				case "RIGHT" :
					dispItem._x = dispItem._x-(dispItem._width);
					break;
				}
			}
		}
		//------------------------------------------------------------------//   
		//Create an object which we'll return
		var rtnObj:Object = new Object();
		//Set 3 properties of the temporary object
		//width, height, tf
		rtnObj.width = dispItem._width;
		rtnObj.height = dispItem._height;
		//For fonts not included
		if (rtnObj.height<=4) {
			rtnObj.height = objStyle.size*2;
		}
		//Set the text field   
		rtnObj.tf = dispItem;
		//Set overflow flag
		rtnObj.overflow = overflow;
		//Delete the temporary objects
		delete tFormat;
		delete tf;
		//Return this object
		return rtnObj;
	}
	/**
	* getTextMc method creates a rotated text displaying Movieclip 
	* using bitmap on the chart based on the parameters 
	* provided.
	*	@param		simulationMode	Whether we're simulating the text field creation
	*								to get width & height. Or, if we're actually
	*								rendering it. In simulation mode, we do not
	*								re-position the textfield to optimize the flow.
	*	@param		targetMC		Movie clip inside which we've to
	*								create the text field.
	*	@param		strText			Text to be shown in the text field
	*	@param		depth			Depth at which the text field is to
	*								be created
	*	@param		xPos			x-position of the text field
	*	@param		yPos			y-position of the text field
	*	@param		rotation		Numerical value of rotation
	*	@param		tFormat			TextFormat object
	*	@param		objStyle		Object containing style properties for
	*								the text field.
	*								This object should necessarily contain
	*								the following parameters:
	*				align			Horizontal alignment. Possible values:
	*								"left", "center" or "right"
	*				vAlign			Vertical alignment of text. Possible values:
	*								"top", "middle" or "bottom"
	*				bold			Boolean value indicating whether the text
	*								would be bold.
	*				italic			Boolean value indicating whether the text
	*								would be italic.
	*				underline		Boolean value indicating whether the text
	*								would be underline.
	*				font			Font face for the text.
	*				size			Font size for the text
	*				color			Color in RRGGBB format for the text.
	*				isHTML			Boolean value indicating whether text would
	*								be rendered as HTML.
	*				leftMargin		Left margin for text (in pixels)
	*				letterSpacing	Numerical value indicating the spacing
	*								between two letters
	*				bgColor			Hex Color RRGGBB or undefined value indicating
	*								the background color of the text field. If undefined,
	*								that means text field shouldn't have a background.
	*				borderColor		Hex Color RRGGBB or undefined value indicating
	*								border color of text field.
	*	@param		wrap			Boolean value indicating whether we need to wrap
	*								labels
	*	@param		width			If we've to wrap, width of textbox (in normal horizontal mode)
	*	@param		maxHeight		If we've to wrap, max height of text box  (in normal horizontal mode)
	*	@return						An object cotaining reference to the text field that it
	*								creates and dimensions. The dimensions returned are those of
	*								rotated text field (so width is the width of rotated text field)
	*/
	public static function getTextMc(simulationMode:Boolean, strText:String, targetMC:MovieClip, depth:Number, xPos:Number, yPos:Number, rotation:Number, tFormat:TextFormat, objStyle:Object, wrap:Boolean, width:Number, maxHeight:Number):Object {
		//Extract align and vAlign Properties from style Obj
		var alignPos:String = objStyle.align;
		var vAlignPos:String = objStyle.vAlign;
		//Whether text is overflowing - in wrap mode.
		var overflow:Boolean = false;
		//Special alignment position conditions for 315 rotation
		if (rotation == 315) {
			alignPos = "RIGHT";
			vAlignPos = "BOTTOM";
		}
		//Set internal text field alignment option for rotated text.  
		//This is applicable when the text needs wrapping or has
		//multiple lines. Then the different lines of text align
		//according to the angle and vertical position of the text field
		//w.r.t given x,y point.
		if (rotation>180) {
			switch (vAlignPos.toUpperCase()) {
			case "TOP" :
				tFormat.align = "left";
				break;
			case "MIDDLE" :
				tFormat.align = "center";
				break;
			case "BOTTOM" :
				tFormat.align = "right";
				break;
			}
		} else {
			switch (vAlignPos.toUpperCase()) {
			case "TOP" :
				tFormat.align = "right";
				break;
			case "MIDDLE" :
				tFormat.align = "center";
				break;
			case "BOTTOM" :
				tFormat.align = "left";
				break;
			}
		}
		//Create the actual text field object now. This will be used to draw the bitmap and flushed thereof.
		var tf:TextField;
		//a & b are undefined variables
		//We want the initial text field size to be of flexible size as we're
		//not wrapping. So we do not define the width and height here
		//Based on wrap, we create the textfield. In wrap mode, we try to simulate
		//a textfield within allotted height, thereby letting it assume a width
		//that fits it within that.
		if (wrap == true) {
			//Get text extent for this text. We simulate the textfield in given width
			//to see how much height it takes.
			//Tricky: Adding 1 to width to work around internal FP bug.
			var reqHeightExt:Object = tFormat.getTextExtent(strText, width+1);
			//Simulate again for text that is lesser in length for minimum width
			var minWidthReqExt:Object = tFormat.getTextExtent(strText);
			//Store the minimum width required - add 1 pixel for fixing internal rendering error
			width = Math.min(minWidthReqExt.textFieldWidth, reqHeightExt.textFieldWidth);
			//Tricky: Add 1 to width, in case of wrapped labels. This is avoid to 
			//a weird bug where Flash Player reports un-necessary overflow of textfield
			//content.
			width++;
			//Create the textfield with allotted width & a height that is the minimum
			//of height taken by textfield or the max height that was specified. This
			//enables the textfield to get contained in given width and height. And if
			//the given space was bigger, this uses lesser space.
			var reqHt:Number = (maxHeight == undefined) ? (reqHeightExt.textFieldHeight) : (Math.min(reqHeightExt.textFieldHeight, maxHeight));
			tf = targetMC.createTextField("Text_"+depth, depth, xPos, yPos, width, reqHt);
			//Set wordwrap
			tf.wordWrap = true;
			//Check if the height allotted cannot accommodate text, thereby resulting
			//to an overflow condition.
			if (reqHeightExt.textFieldHeight>maxHeight) {
				tf.multiline = true;
				overflow = true;
			}
		} else {
			var a, b;
			tf = targetMC.createTextField("Text_"+depth, depth, xPos, yPos, a, b);
			//Set align position
			tf.autoSize = 'left';
		}
		
		//Set the properties
		tf.multiLine = true;
		tf.selectable = false;
		//Set the border color if required
		if (objStyle.borderColor != "") {
			tf.border = true;
			tf.borderColor = parseInt(objStyle.borderColor, 16);
		}
		//Set the background color if required   
		if (objStyle.bgColor != "") {
			tf.background = true;
			tf.backgroundColor = parseInt(objStyle.bgColor, 16);
		}
		//Whether HTML text or not?   
		tf.html = objStyle.isHTML;
		//Set the text 
		if (objStyle.isHTML) {
			//If it's HTML text, set as htmlText
			tf.htmlText = strText;
		} else {
			//Else, set as plain text
			tf.text = strText;
		}
		//Apply the text format
		tf.setTextFormat(tFormat);
		//Store a copy of text -required later for tool-tip
		//For tool-tip, maintain the formatting as specified
		//for text field.
		var textCpy:String = strText;
		//----------- Overflow handling begin -------------//
		//If the text is overflowing and ellipses have to be added.
		if (overflow && (Utils.ADD_ELLIPSES == true)) {
			handleTextOverflow(tf, tFormat);
		}
		//----------- Overflow handling complete -------------//  
		//---------- Conversion of textfield to movieclip (bitmapdata) begin --------//
		//Get the height of textfield
		var originalH = tf._height;
		//Getting the BitmapData of the textfield, for using it instead of the textField itself to avoid embedding fonts.
		var bmp:BitmapData = getTxtBmp(tf);
		//The textfield is required no more.
		tf.removeTextField();
		//Movieclip created at the depth of the textfield destroyed.
		var dispItem:MovieClip = targetMC.createEmptyMovieClip('TextBmp_'+depth, depth);
		//Sub-container mc for alignment adjustments
		var mcBmp:MovieClip = dispItem.createEmptyMovieClip('mcBmp', 0);
		//Bitmapdata attached to the sub-movieclip created.
		mcBmp.attachBitmap(bmp, 0, 'auto', true);
		//---------- Conversion of textfield to movieclip (bitmapdata) end --------//
		//Set its position
		dispItem._x = xPos;
		dispItem._y = yPos;
		//Reposition the text display to achieve alignment for 315 degree rotation
		if (rotation == 315) {
			if (alignPos == 'RIGHT') {
				mcBmp._x -= mcBmp._width;
			} else if (alignPos == 'CENTER') {
				mcBmp._x -= mcBmp._width/2;
			}
		}
		//Set rotation  
		dispItem._rotation = rotation;
		//If tool-tip has to be shown for rotated text-fields that are overflowing
		if (!simulationMode && overflow == true && (Utils.APP_TOOLTIP instanceof ToolTip)) {
			//Set tool-text
			setItemToolTip(dispItem, textCpy);
		}
		//We now re-position the MC if not in simulation mode  
		if (!simulationMode) {
			//------------------------------------------------------------------//
			if (rotation == 270) {
				//Now, adjust the y orientation of the MC
				switch (alignPos.toUpperCase()) {
				case "LEFT" :
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						//Nothing
						break;
					case "MIDDLE" :
						dispItem._y = dispItem._y+(dispItem._height/2);
						break;
					case "BOTTOM" :
						dispItem._y = dispItem._y+dispItem._height;
						break;
					}
					break;
				case "CENTER" :
					dispItem._x = dispItem._x-(dispItem._width/2);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						break;
					case "MIDDLE" :
						dispItem._y += dispItem._height/2;
						break;
					case "BOTTOM" :
						dispItem._y += dispItem._height;
						break;
					}
					break;
				case "RIGHT" :
					dispItem._x = dispItem._x-(dispItem._width);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						break;
					case "MIDDLE" :
						dispItem._y += dispItem._height/2;
						break;
					case "BOTTOM" :
						dispItem._y += dispItem._height;
						break;
					}
					break;
				}
			}
			if (rotation == 90) {
				//Now, adjust the y orientation of the MC
				switch (alignPos.toUpperCase()) {
				case "LEFT" :
					dispItem._x += (dispItem._width);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						dispItem._y = dispItem._y-dispItem._height;
						break;
					case "MIDDLE" :
						dispItem._y = dispItem._y-(dispItem._height/2);
						break;
					case "BOTTOM" :
						//Nothing
						break;
					}
					break;
				case "CENTER" :
					dispItem._x += (dispItem._width/2);
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						dispItem._y -= dispItem._height;
						break;
					case "MIDDLE" :
						dispItem._y -= dispItem._height/2;
						break;
					case "BOTTOM" :
						break;
					}
					break;
				case "RIGHT" :
					//Adjust y-position based on vAlignPos
					switch (vAlignPos.toUpperCase()) {
					case "TOP" :
						dispItem._y -= dispItem._height;
						break;
					case "MIDDLE" :
						dispItem._y -= dispItem._height/2;
						break;
					case "BOTTOM" :
						break;
					}
					break;
				}
			} else if (rotation == 315) {
				//If the rotation angle is 315, it can only be for x-axis
				//names in the chart. So we directly alter the x and y position
				//irrespective of alignment position specified.
				var root2 = 1.42;
				dispItem._x -= (originalH/root2)/2;
				//Minus 4 to avoid gutter space
				dispItem._y -= 4;
			}
			//Round the dispItem x and y, to avoid blurring  
			dispItem._x = Math.round(dispItem._x);
			dispItem._y = Math.round(dispItem._y);
		}
		//------------------------------------------------------------------//   
		//Create an object which we'll return
		var rtnObj:Object = new Object();
		//Set 3 properties of the temporary object
		//width, height, tf
		rtnObj.width = dispItem._width;
		rtnObj.height = dispItem._height;
		//For fonts not included
		if (rtnObj.height<=4) {
			rtnObj.height = objStyle.size*2;
		}
		//Set the text field   
		rtnObj.tf = dispItem;
		//Set overflow flag
		rtnObj.overflow = overflow;
		//Delete the temporary objects
		delete tFormat;
		delete tf;
		//Return this object
		return rtnObj;
	}
	/**
	 * getTxtBmp method is a static method called to get the
	 * BitmapData of the textfield returned.
	 * @param	txt		the textfield
	 * @return			its BitmapData
	 */
	private static function getTxtBmp(txt:TextField):BitmapData {
		//Create BitmapData 
		var bmp:BitmapData = new BitmapData(txt._width+1, txt._height+1, true, 0x0);
		//Draw image of textfield in the same
		bmp.draw(txt);
		//Return textfield with the bitmap data
		return bmp;
	}
	/** 
	 * When a text-field is over-flowing, this method adds ellipses to the
	 * same.
	 * @param	tf		Textfield in which overflow management has to be done
	 * @param	tFormat	TextFormat object applied to the textfield
	*/
	public static function handleTextOverflow(tf:TextField, tFormat:TextFormat):Void
	{
		// trim spaces and ellipses from left side.	
		tf.text = trimNewLineNSpaceChar(tf.text, true);
		
		// Hold the given text
		var plainText:String = tf.text;		
		
		// Given textfield width
		var textFieldWidth:Number = tf._width;
	  
	   //Get text extent for this text.
		var reqTextExtent:Object =  tFormat.getTextExtent(plainText);			
		
		// Calculate text width. Text which is present in text field.
		var totalStrWidth:Number = reqTextExtent.width;
		// Total string length
		var totalStrLength:Number = plainText.length;
	   
		// Calculate ellipses string width.
		var ellipsesStr:String = "...";
		tf.text = ellipsesStr;
		tf.setTextFormat(tFormat);
		// Find out ellipses string width
		var ellipsesWidth:Number = tf.textWidth;
	  
		// Calculate the permissible width in text field upto which string will show.
		var permissibleWidth:Number = textFieldWidth - ellipsesWidth;  
		
		// Calculate the index number of string.
		var stringIndex:Number = totalStrLength;
		
		//stringIndex zero means, text already overflow in zero index so return
		if(stringIndex == 0)
	    {			
		   tf.text = plainText;
		   tf.setTextFormat(tFormat);
		   return;
	    }
		
		// Calculate value factor
		var valueFactor:Number = Math.round(stringIndex / 2)  ;
		
		// Set again actual text and ellipses text into text field
		tf.text = plainText.slice(0, stringIndex)+ellipsesStr;
		tf.setTextFormat(tFormat);
		
		// Set  isIncremental value according to text field 's maxscroll. 
		var isIncremental:Boolean = tf.maxscroll > 1 ? false : true;
		checkStringFrEllipses(plainText,									
								 tf,
								 tFormat,
								 isIncremental,                                
								 stringIndex,
								 valueFactor,
								 ellipsesStr, 
								 0);
	
	  
	}
	/**
	 * checkStringFrEllipses method follows divide and Conquer algorithm to check the given string 
	 * according to text field and add ellipses if require.
	 *
	 * @param   plainText	  Original text
	 * @param   tf	          Textfield in which overflow management has to be done
	 * @param   tFormat	      TextFormat object applied to the textfield
	 * @param   isIncremental A boolean value which decide, search process will be incremental or decremental 
	 * @param   stringIndex   Index number of string
	 * @param   valueFactor   A number which will add or subtract with string index number
	 * @param   ellipsesStr   A post fix string which will add with text if ellipses occur 	    
	 */
	
	public static function checkStringFrEllipses(plainText:String,
												 	tf:TextField,								  
								   					tFormat:TextFormat,
													isIncremental:Boolean,
								   					stringIndex:Number,
								   					valueFactor:Number,
								   					ellipsesStr:String, 
													recursionTimes:Number):Void
	{ 
		// updating the number of recursive calls 
		recursionTimes++;
		//if recursion times exceeds recursion limit then stop the recursion
		if(recursionTimes >= RECURSION_LIMIT) return;
		
		// if this process is incremental then increase index no of string otherwise decrease
		if(isIncremental)
		{
			stringIndex += valueFactor ;	  
		}
		else 
		{
			// check to avoid negative value 
			if(stringIndex >= valueFactor)
				stringIndex -= valueFactor;
		}
		// Set actual text and ellipses text into text field
		tf.text = plainText.slice(0, stringIndex)+ellipsesStr; 
		
		// Set text format
		tf.setTextFormat(tFormat);
		
		// maxscroll of text field is greater  than 1 mean existing text is overflowing the text field   
		if(tf.maxscroll > 1)
		{  			
			// valueFactor is 1 mean only one character is exceeding from actual requirement
			if(valueFactor == 1)
			{
				// store the rest characters if exits after ellipses				
			   var getRestStr:String = plainText.slice(stringIndex, plainText.length);			   
				// trim spaces and ellipses from right side.				   
			   getRestStr = trimNewLineNSpaceChar(getRestStr, false);
			   
				// if there are no characters present in rest of the string then ellipsesStr is not require 
			   if(getRestStr == "")
			   {				
					tf.text = plainText.slice(0, plainText.length);					
					tf.setTextFormat(tFormat);	
					return;
			   }
				
				// trim spaces and store final string 
			    var s:String = trimNewLineNSpaceChar(plainText.slice(0, stringIndex - 1), false);				
				// when text field have minimum space(space for ellipses)then only one character will be taken. 
			    if(s == "")
				{
					// assign first character
					s = trimNewLineNSpaceChar(plainText.slice(0, 1), false);
				}
				// ellipses from right side.	
				tf.text = s +ellipsesStr;
				tf.setTextFormat(tFormat);	
				return;				
			}
			
			// set valueFactor value 
			valueFactor = Math.round(valueFactor/2);
			// check again backward for require  result.
			checkStringFrEllipses( plainText, tf,  tFormat, false, stringIndex, valueFactor, ellipsesStr, recursionTimes);
		}
		else
		{	
			if(stringIndex >= (plainText.length + ellipsesStr.length)) 
			{
				tf.text = plainText.slice(0, stringIndex);				
				tf.setTextFormat(tFormat);
				return;
			}
		   // set valueFactor value 
			valueFactor = Math.round(valueFactor/2);
		   // check again forward for require  result. 
		   checkStringFrEllipses( plainText, tf,  tFormat, true, stringIndex, valueFactor, ellipsesStr, recursionTimes);
		}
		
	}
	/**
	* trimNewLineNSpaceChar trims the "spaces" and "newline" character from the string.
	* @param  str			The string which need right trim
	* @param  isFromLeft	A Boolean value for left side trim or right side trim
	*					
	* @return			String with the removed of spaces and newline from left/right
	*/
	
	private static function trimNewLineNSpaceChar(str:String, isFromLeft:Boolean):String
	{		
		//If the string is undefined return ""
		if (str == undefined)
		{
			return "";
		}  
		//If the specified character is present in the string, continue...
		
			var i:Number;
			i = !isFromLeft ? str.length - 1:0;
			
			for(i ; !isFromLeft ? i>=0:i < str.length ; !isFromLeft ? i--:i++)
			{	
				// charCodeAt 10 and 13 means new line.charCodeAt 32 is for spaces 
				if(str.charCodeAt(i) != 10 
					&& str.charCodeAt(i) != 13
					&& str.charCodeAt(i) != 32)
				{					
					break;
				}
			}
			
			str = !isFromLeft ? str.slice(0, i+1):str.substr(i, str.length); 
			if(i == -1) str = "";   	
		return str;
	}
	/**
	 * Sets tool-tip handles for the specified display item (in this context,
	 * textfield)													 
	 * @param	dispItem	Reference to movie clip which has to show tool-text
	 * @param	tooltext	Tool text to be shown
	*/
	private static function setItemToolTip(dispItem:MovieClip, tooltext:String) {
		//Set tool-tip handlers
		dispItem.useHandCursor = false;
		//Set the roll-over function
		dispItem.onRollOver = function() {
			//Set the text for this tool-tip
			Utils.APP_TOOLTIP.setText(tooltext);
			//Show the tool-tip
			Utils.APP_TOOLTIP.show();
			//Create a mouse-move handler for reposition tool-tip
			this.onMouseMove = function() {
				Utils.APP_TOOLTIP.rePosition();
			};
		};
		//Handler for when mouse moves out
		dispItem.onRollOut = function() {
			//Hide the tool-tip
			Utils.APP_TOOLTIP.hide();
			//Remove the mouse-move event
			delete dispItem.onMouseMove;
		};
	}
	/**
	* getAttributesArray method helps convert the list of attributes
	* for an object into an array.
	* Reason:
	* Starting ActionScript 2, OBJECT/EMBED attributes have also
	* become case-sensitive. However, prior versions of FusionCharts
	* supported case-insensitive attributes. So we need to parse all
	* attributes as case-insensitive to maintain backward compatibility.
	* To do so, we first extract all attributes, convert it into
	* lower case and then store it in an array. Later, we extract value from
	* this array.
	* Once this array is returned, IT'S VERY NECESSARY IN THE CALLING CODE TO
	* REFERENCE THE NAME OF ATTRIBUTE IN LOWER CASE (STORED IN THIS ARRAY).
	* ELSE, UNDEFINED VALUE WOULD SHOW UP.
	*/
	public static function getAttributesArray(objSource:Object):Array {
		//Array that will store the attributes
		var atts:Array = new Array();
		//Object used to iterate through the attributes collection
		var obj:Object;
		//Iterate through each attribute in the attributes collection,
		//convert to lower case and store it in array.
		for (obj in objSource) {
			//Store it in array
			atts[obj.toString().toLowerCase()] = objSource[obj];
		}
		//Return the array
		return atts;
	}
	/**
	* getTextMetrics helps to get textfield width, height, textwidth & textheight for a particular string
	* with a specified text format without rendering it.
	* @param	strText		String against which we need width and height.
	* @param	styleObj	Object that contains the information for text format.
	* @param	wrapWidth	Optional parameter for wrap text width.
	* @return	Object with textfield properties.
	*/
	public static function getTextMetrics (strText:String, styleObj:Object, wrapWidth:Number):Object {
		var txtMetricsObj:Object;
		//First up, we create a text format object and set the properties
		var txtFormat:TextFormat = new TextFormat();
		//Font properties
		txtFormat.font = styleObj.font;
		txtFormat.size = styleObj.size;
		txtFormat.color = parseInt(styleObj.color, 16);
		//Text decoration
		txtFormat.bold = styleObj.bold;
		txtFormat.italic = styleObj.italic;
		txtFormat.underline = styleObj.underline;
		//Margin and spacing
		txtFormat.leftMargin = styleObj.leftMargin;
		txtFormat.letterSpacing = styleObj.letterSpacing;
		//---------------------//
		//Replace {br}, {BR} with new line break
		strText = StringExt.replace(strText, "{BR}", "\n");
		strText = StringExt.replace(strText, "{br}", "\n");
		//Also convert <BR> to /n.
		strText = StringExt.replace(strText, "<BR>", "\n");
		strText = StringExt.replace(strText, "&lt;BR&gt;", "\n");
		strText = StringExt.replace(strText, "<br>", "\n");
		strText = StringExt.replace(strText, "&lt;br&gt;", "\n");
		//---------------------//
		//if no specified width is defined we get dimention as single line
		if(isNaN(wrapWidth) || wrapWidth < 1 ) {
			txtMetricsObj = txtFormat.getTextExtent(strText);
		} else {
			//if a width specified we get height based on the specified width.
			//This is required to get the height in rotated mode when we have to render text in a max width.
			txtMetricsObj = txtFormat.getTextExtent(strText, wrapWidth);
		}
		return txtMetricsObj;
	}
}
