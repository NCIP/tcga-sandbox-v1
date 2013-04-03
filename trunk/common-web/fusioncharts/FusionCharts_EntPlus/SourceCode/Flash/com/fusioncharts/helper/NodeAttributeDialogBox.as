/**
 * @class NodeAttributeDialogBox
 * @author FusionCharts Technologies LLP, www.fusioncharts.com
 * @version 3.2
 *
 * Copyright (C) FusionCharts Technologies LLP, 2010
 *
 * NodeAttributeDialogBox class helps to accept some attribute for a NODE - to add new Nodes
 */
//FCButton
import com.fusioncharts.helper.FCButton;
import com.fusioncharts.helper.Utils;
import com.fusioncharts.helper.ColorPicker;
//Drop Shadow filter
import flash.filters.DropShadowFilter;
//Combo box
import mx.controls.ComboBox;
//Check Box
import mx.controls.CheckBox;
//Text Input
import mx.controls.TextInput;
//Delegate
import mx.utils.Delegate;
import mx.events.EventDispatcher;

//Class definition
class com.fusioncharts.helper.NodeAttributeDialogBox {
	//Instance variables
	private var parent:MovieClip;
	private var depth:Number;
	private var sX:Number;
	private var sY:Number;
	private var h:Number;
	private var w:Number;
	private var btnX:Number;
	private var btnY:Number;
	private var cornerRadius:Number;
	private var borderColor:String;
	private var borderThickness:Number;
	private var bgColor:String;
	private var yPadding:Number;
	private var fontProps:Object;
	private var dropShadow:Boolean;
	//MovieClip of the dialog Box
	private var db:MovieClip;
	//Movie clip for the button
	private var btnOk:FCButton;
	//Movie clip for the Cancel button
	private var btnCancel:FCButton;
	private var btnCancelX:Number;
	private var btnCancelY:Number;
	//Update information
	private var dsSeriesName:String;
	private var nodeShape:String;
	private var imageAlign:String;
	private var labelAlign:String;
	private var seriesNameArr:Array;
	private var idArr:Array;
	//Container
	private var dbCont:MovieClip;
	//Create a listener for the button
	private var btnListener:Object;
	private var btnCancelListener:Object;
	private var listenerObject:Object;
	
	//Check Box
	private var image_ch:CheckBox;
	private var showValue_ch:CheckBox;
	private var allowDrag_ch:CheckBox;
	
	
	private var errorTf:TextField;
	private var errorInvalidTf:TextField;
	private var xPosTf:TextField;
	private var yPosTf:TextField;
	
	//Input Textfield
	private var idTf:TextInput;
	private var nameTf:TextInput;
	private var alphaTf:TextInput;
	private var widthTf:TextInput;
	private var heightTf:TextInput;
	private var radiusTf:TextInput;	
	private var numSidesTf:TextInput;	
	private var urlTf:TextInput;
	private var tooltextTf:TextInput;
	private var imageWidthTf:TextInput;
	private var imageHeightTf:TextInput;
	private var imageURLTf:TextInput;
	
	//Combo boxes
	private var shapes_cb:ComboBox;
	private var seriesName_cb:ComboBox;
	private var imageAlign_cb:ComboBox;
	private var labelAlign_cb:ComboBox;
	//these static labels must have a global reference in order to show/hide them
	private var imageWidthLabel;
	private var imageHeightLabel;
	private var imageAlignLabel;
	private var imageURLLabel;
	private var widthLabelTf;
	private var heightLabelTf;
	private var radiusLabelTf;
	private var numSideLabelTf;
	private var clrPicker:ColorPicker;
	
	//check whether the dialog box is due to edit or add node
	private var isEditNodeDialog:Boolean = false;
	
	//Constructor function
	/**
	 * Here, we initialize the dialog Box objects.
	 *	@param		target				Movie clip in which we've to create the
	 *									dialog box.
	 *	@param		depth				Depth at which we create dialog box.
	 *	@param		sX					Top X of the stage.
	 *	@param		sY					Top Y of the stage.
	 *	@param		yPadding			y-axis padding (pixels)
	 *	@param		seriesNameArr		array holding all the seriesName of the datasets
	 *	@param		idArr				array holding all the previous data items id - for checking new ID
	*/
	function NodeAttributeDialogBox(target:MovieClip, depth:Number, sX:Number, sY:Number, yPadding:Number, seriesNameArr:Array, idArr:Array) {
		//Store parameters in instance variables
		this.parent = target;		
		this.sX = sX;
		this.sY = sY;
		this.yPadding = yPadding;
		this.seriesNameArr = seriesNameArr;
		this.idArr = idArr;
		//Create the required movieclip for the dialog box
		this.dbCont = this.parent.createEmptyMovieClip("DialogBoxContainer", depth);
		//Create a movieclip within the dialog box Container for panel
		this.db = this.dbCont.createEmptyMovieClip("bgPanel", 1);
		//Create a movieclip within the dialog box for OK button
		this.btnOk = new FCButton(this.dbCont, 2);
		//Create a movieclip within the dialog box for Cancel button
		this.btnCancel = new FCButton(this.dbCont, 3);
		//Hide the movieClip initially
		this.db._visible = false;
		//Hide the hand cursor
		this.db.useHandCursor = false;
		//Initialize the listener
		this.btnListener = new Object();
		this.btnCancelListener = new Object();
		listenerObject = new Object();
		//call initialize on EventDispatcher to
		//implement the event handling functions
		mx.events.EventDispatcher.initialize(this);
	}
	//these functions are defined in the class to prevent
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
	/**
	 * setParams method sets the cosmetic and functional parameters 
	 * for the dialog Box.	 
	 *	@param	height			Sets the height of the rectangle
	 *	@param	width			Sets the width of the rectangle
	 *	@param	cornerRadius	Sets the corner radius of the rounded rectangle.
	 *	@param	bgColor			Background Color
	 *	@param	borderColor		Border Color of the tool tip
	 *  @param	borderThickness	border thickness
	 *	@param	fontProps		Object that holds the font properties for the error message
	 *	@param	dropShadow		Whether to drop shadow for the tool tip.
	*/
	public function setParams(height:Number, width:Number, cornerRadius:Number, bgColor:String, borderColor:String, borderThickness:Number, fontProps:Object, dropShadow:Boolean) {
		//Store parameters in instance variables.
		this.h = height;
		this.w = width;
		this.cornerRadius = cornerRadius;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.borderThickness = borderThickness;
		this.dropShadow = dropShadow;
		this.fontProps = fontProps;
		//Set the initial position of the button based on the width and height of the dialog box
		var btnW:Number = 80;
		var btnH:Number = 20;
		this.btnX = width/2-btnW/2-5;
		this.btnY = (7/8*height)+5;
		this.btnCancelX = this.btnX+btnW+10;
		this.btnCancelY = (7/8*height)+5;
		//Set the default values for the button
		this.btnOk.setParams("Submit", this.btnX, this.btnY, btnH, btnW,5,"FFFFFF","CCCCCC","Verdana", "000000", 10);
		//Register the event with the button
		this.btnOk.addEventListener("click", this.btnListener);
		//Register the listener
		btnListener.click = Delegate.create( this, onClick);
		//Set the default values for the Cancel button
		this.btnCancel.setParams("Cancel", this.btnCancelX, this.btnCancelY, btnH, btnW, 5,"FFFFFF","CCCCCC","Verdana", "000000", 10);
		//Register the event with the button
		this.btnCancel.addEventListener("click", this.btnCancelListener);
		//Register the listener
		btnCancelListener.click = Delegate.create( this, onCancelClick);
		//Create filters and apply
		if (dropShadow) {
			var shadowFilter:DropShadowFilter = new DropShadowFilter(3, 45, 0x666666, 0.8, 4, 4, 1, 1, false, false, false);
			this.db.filters = [shadowFilter];
		}
	}
	/*
	 * setCurrentValue method sets the current value of an existing node at the 
	 * time of edit.
	 */
	 public function setCurrentValue(editNode:Object):Void{
		 //this function gets call only when we edit a node.Set edit dialog box flasg to true.
		 this.isEditNodeDialog = true;
		 //loop variable
		 var i:Number;
		 //input text flds
		this.idTf.text = editNode.id;
		this.idTf.enabled = false
		this.nameTf.text = editNode.label;
		this.tooltextTf.text = editNode.toolText;
		this.alphaTf.text = editNode.alpha;
		this.widthTf.text = editNode.w;
		this.heightTf.text = editNode.h;
		this.radiusTf.text = editNode.r;
		this.numSidesTf.text = editNode.numSides;
		this.imageWidthTf.text = editNode.imageWidth;
		this.imageHeightTf.text = editNode.imageHeight;
		this.urlTf.text = editNode.link;
		this.imageURLTf.text = editNode.imageUrl;
		//combo box and default values
		this.labelAlign = editNode.labelAlign;
		this.imageAlign = editNode.imageAlign;
		this.seriesName_cb.selectedIndex = editNode.dsIndex;
		this.dsSeriesName = seriesName_cb.selectedItem.label;
		for(i=0; i<this.shapes_cb.length; i++){
			if(this.shapes_cb.dataProvider[i].label.toLowerCase() == editNode.shape.toLowerCase()){
				this.shapes_cb.selectedIndex = i;
				if(this.shapes_cb.dataProvider[i].label.toLowerCase() == "rectangle" ){
					 heightTf._visible = true;
					 widthTf._visible = true;
					 radiusTf._visible = false;
					 numSidesTf._visible = false;
					 widthLabelTf._visible = true;
					 heightLabelTf._visible = true;
					 radiusLabelTf._visible = false;
					 numSideLabelTf._visible = false;
				}else{
					 heightTf._visible = false;
					 widthTf._visible = false;
					 radiusTf._visible = true;
					 numSidesTf._visible = false;
					 numSideLabelTf._visible = false;
					 if(this.shapes_cb.dataProvider[i].label.toLowerCase() == "polygon") {
						 numSidesTf._visible = true;
						 numSideLabelTf._visible = true;
					 }
					 widthLabelTf._visible = false;
					 heightLabelTf._visible = false;
					 radiusLabelTf._visible = true;
				}
				break;
			}
		}
		this.shapes_cb.enabled = false;
		this.seriesName_cb.enabled = false;
		for(i=0; i<this.labelAlign_cb.length; i++){
			if(this.labelAlign_cb.dataProvider[i].label.toLowerCase() == editNode.labelAlign.toLowerCase()){
				this.labelAlign_cb.selectedIndex = i;
			}
		}
		for(i=0; i<this.imageAlign_cb.length; i++){
			if(this.imageAlign_cb.dataProvider[i].label.toLowerCase() == editNode.imageAlign.toLowerCase()){
				this.imageAlign_cb.selectedIndex = i;
			}
		}
		//color picker 
		clrPicker.color = parseInt(editNode.color, 16);
		//check boxes
		this.showValue_ch.selected = true;
		this.allowDrag_ch.selected = editNode.isDraggable;
		this.image_ch.selected = editNode.imageNode;
		if(this.image_ch.selected){
			this.imageWidthLabel._visible = true;
			this.imageHeightLabel._visible = true;
			this.imageAlignLabel._visible = true;
			this.imageURLLabel._visible = true;
			this.imageWidthTf._visible = true;
			this.imageHeightTf._visible = true;
			this.imageURLTf._visible = true;
			this.imageAlign_cb.visible = true;
		}
		this.image_ch.enabled = false;
	 }
	/**
	 * drawRoundedRect method helps draw a rounded Rectangle based on the parameters specified.
	 *	Assumptions: Movie clip is already created and line/fill style has
	 *				 already been set before this method is called to
	 *				 draw a rounded rectangle.
	 *	@param		mc		Movie clip in which we've to draw the rounded rectangle
	 *	@param		x		X position of the rectangle
	 *	@param		y		Y position of the rectangle
	 *	@param		w		w width of the rectangle
	 *	@param		h		height of the rectangle
	 *	@param		cornerRadius	corner radius of the rectangle to show the roundness.
	 */
	public function drawRoundedRect(mc:MovieClip, x:Number, y:Number, w:Number, h:Number, cornerRadius:Number):Void {
		//We first see if the user has defined a corner radius for us. If yes, we need to create curves and calculate
		if (cornerRadius>0) {
			//Initialize variables to be usd
			var theta:Number, angle:Number, cx:Number, cy:Number, px:Number, py:Number;
			//Make sure that (w,h) is larger than 2*cornerRadius
			if (cornerRadius>Math.min(w, h)/2) {
				cornerRadius = Math.min(w, h)/2;
			}
			// theta = 45 degrees in radians 
			theta = Math.PI/4;
			// draw top line
			mc.moveTo(x+cornerRadius, y);
			mc.lineTo(x+w-cornerRadius, y);
			//angle is currently 90 degrees
			angle = -Math.PI/2;
			// draw tr corner in two parts
			cx = Math.round(x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			cy = Math.round(y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			px = Math.round(x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius));
			py = Math.round(y+cornerRadius+(Math.sin(angle+theta)*cornerRadius));
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = Math.round(x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			cy = Math.round(y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2)));
			px = Math.round(x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius));
			py = Math.round(y+cornerRadius+(Math.sin(angle+theta)*cornerRadius));
			mc.curveTo(cx, cy, px, py);
			// draw right line
			mc.lineTo(x+w, y+h-cornerRadius);
			// draw br corner
			angle += theta;
			cx = x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = x+w-cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+w-cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			// draw bottom line
			mc.lineTo(x+cornerRadius, y+h);
			// draw bl corner
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+h-cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+h-cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			// draw left line
			mc.lineTo(x, y+cornerRadius);
			// draw tl corner
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
			angle += theta;
			cx = x+cornerRadius+(Math.cos(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			cy = y+cornerRadius+(Math.sin(angle+(theta/2))*cornerRadius/Math.cos(theta/2));
			px = x+cornerRadius+(Math.cos(angle+theta)*cornerRadius);
			py = y+cornerRadius+(Math.sin(angle+theta)*cornerRadius);
			mc.curveTo(cx, cy, px, py);
		} else {
			//No corner radius defined - so draw a simple rectangle
			mc.moveTo(x, y);
			mc.lineTo(x+w, y);
			mc.lineTo(x+w, y+h);
			mc.lineTo(x, y+h);
			mc.lineTo(x, y);
		}
	}
	/**
	 * draw method draw the initial Dialog Box
	*/
	public function draw():Void {
		//Set the border color if required
		if (this.borderColor != "" && this.borderColor != undefined && this.borderColor != null) {
			this.db.lineStyle(this.borderThickness, parseInt(this.borderColor, 16), 100);
		}
		//Set the background color if required                                        
		if (this.bgColor != "" && this.bgColor != undefined && this.bgColor != null) {
			this.db.beginFill(parseInt(this.bgColor, 16), 100);
		}
		this.drawRoundedRect(this.db, 0, 0, this.w, this.h, this.cornerRadius);
		//End Fill
		this.db.endFill();
		//Set the position with respect to the parent class
		this.db._x = this.sX-this.w/2;
		this.db._y = this.sY-this.h/2;
		this.createPanel();
		//Now create an OK button
		//Set the position with respect to the parent class
		this.btnOk.setPosition(this.sX-this.w/2+105, this.sY-this.h/2+275);
		//Draw the mouse and set the visibility as true
		this.btnOk.draw();
		this.btnOk.show();
		//Now create a Cancel button
		//Set the position with respect to the parent class
		this.btnCancel.setPosition(this.sX-this.w/2+195, this.sY-this.h/2+275);
		//Draw the mouse and set the visibility as true
		this.btnCancel.draw();
		this.btnCancel.show();
	}
	/**
	 *  createPanel method sets the panel with textfield and buttons
	*/
	public function createPanel():Void {
		//Loop Variable
		var i:Number;
		//Set the text for the error message
		var styleObj:Object =  new Object();
		styleObj.bold = false;
		styleObj.align = "left";		
		styleObj.vAlign = "center";
		styleObj.italic = false;
		styleObj.underline = false;
		styleObj.font = this.fontProps.font;
		styleObj.size = this.fontProps.fontSize;
		styleObj.color = "FF0000";
		styleObj.isHTML = true;
		styleObj.leftMargin = 0;
		styleObj.letterSpacing= 0;
		styleObj.bgColor = "";
		styleObj.borderColor = "";
		//Initial position within the DB
		var startX:Number = 20;
		var startY:Number = 20;
		var endX:Number = this.w - 20
		var middleX:Number = this.w / 2;
		//Starting depth for all the components in the DB
		var startDepth:Number = 2;
		//Create an error in ID text field
		this.errorTf = Utils.createText(false, "This Id exists for another node. Please use a different Id.", this.db, startDepth++, startX, startY+235, 0, styleObj, true, (this.w - 40), 50).tf;
		this.errorInvalidTf = Utils.createText(false, "Id cannot be blank.", this.db, startDepth++, startX, startY+235, 0, styleObj).tf;
		//Make it hidden initially
		this.errorTf._visible = false;
		this.errorInvalidTf._visible = false;
		//Set the default color style
		styleObj.color = this.fontProps.fontColor;
		
		var objectPadding:Number = 55
		
		/* ------- first line - id & dataset --------- */
		//Now create the ID label TF
		Utils.createText(false, "Id", this.db, startDepth++, 20, 17, 0, styleObj);
		//Create an input textfield
		this.idTf = createInputFld("id_ti", startDepth++, 63, 14, 100);
		
		var datasetLabel:Object = Utils.createText(false, "Dataset", this.db, startDepth++, 174, 17, 0, styleObj);
		//Loop to add all the seriesnames for the dataset series
		var items:Array = new Array();
		for(i=0;i<this.seriesNameArr.length;i++) {
			items.push({data:i, label:this.seriesNameArr[i].sn+ "," + this.seriesNameArr[i].dsId});
		}
		// Create listener object for shape_cb
		var cbSeriesListener:Object = new Object();
		//Store the class Reference
		var classRef:Object = this;
		// Create event handler function.
		cbSeriesListener.change = function (evt_obj:Object) {
			 classRef.dsSeriesName = evt_obj.target.selectedItem.label;
			 classRef.clrPicker.color = parseInt(classRef.seriesNameArr[evt_obj.target.selectedIndex].color ,16)
		}
		//Now, we draw a combo box which lists all the series Names
		seriesName_cb = createCombo("seriesName_cb", startDepth++, 226, 14, items, cbSeriesListener, true);
		seriesName_cb.setSize(132, seriesName_cb.height);
		//Set the default seriesname depending upon the first element of dropdown
		this.dsSeriesName = seriesName_cb.selectedItem.label;		
		/* ------------- end of 1st line ---------------- */ 
		
		/* ---------- second line - label & tooltext ----------- */
		var secondLineYPos:Number = startY + 25;
		Utils.createText(false, "Label", this.db, startDepth++, 20, 43, 0, styleObj);
		//Create an input textfield
		this.nameTf = createInputFld("label_ti", startDepth++, 63, 42, 100);
		//Now create the URL TF
		var tooltextLabelTf:TextField = Utils.createText(false, "Tooltip", this.db, startDepth++, 174, 43, 0, styleObj).tf;
		//Create an input textfield
		this.tooltextTf = createInputFld("tooltext_ti", startDepth++, 226, 42, 132);
		/*---------- end of second line ---------------*/
		
		/*--------- Third line - Color & alpha ---------*/
		var thirdLineYPos:Number = startY + (25 * 2);
		//instantiate color component here
		clrPicker = new ColorPicker(this.db, startDepth++);
		clrPicker.x = 111;
		clrPicker.y = 71;
		clrPicker.color = parseInt(this.seriesNameArr[seriesName_cb.selectedIndex].color ,16)
		//color picker should be at last --- to have the highest depth;
		var alphaLabel:Object = Utils.createText(false, "Alpha", this.db, startDepth++, 20, 73, 0, styleObj);
		//Create an input textfield
		this.alphaTf = createInputFld("alpha_ti", startDepth++, 63, 71, 35, "0-9");
		this.alphaTf.text = '100';
		//Create the label align label 
		var labelAlignLabel:Object = Utils.createText(false, "Label Align", this.db, startDepth++, 206, 73, 0, styleObj);
		var itemArray:Array = [{data:1, label:'TOP'}, {data:2, label:'MIDDLE'}, {data:3, label:'BOTTOM'}];
		// Create listener object for labelAlign_cb
		var laListener:Object = new Object();
		// Create event handler function.
		laListener.change = function (evt_obj:Object) {
			 classRef.labelAlign = evt_obj.target.selectedItem.label;
		}
		this.labelAlign = "TOP";
		this.labelAlign_cb = createCombo("labelAlign_cb", startDepth++, 274, 71, itemArray, laListener, true);
		this.labelAlign_cb.setSize(85, this.labelAlign_cb.height)
		/*-------- end of third line -----------*/
		
		/* ---------- Fourth line - showValue & allowDrag ----------- */
		var fourthLineYPos:Number = startY + (25 * 3);
		//Draw a checkBox for accepting allowDrag parameters
		this.allowDrag_ch = createCheckBox("allowDrag_ch", startDepth++, {label:' Allow Drag', selected:true}, 24, 98)
		/*-------------- end of fourth line --------------------*/
		
		/*------------- Fifth line - shape, width, height, radius & num sides ------ */
		var fifthLineYPos:Number = startY + (25 * 4);
		//Create the SHAPE label 
		var shapeLabel:Object = Utils.createText(false, "Shape", this.db, startDepth++, 20, 123, 0, styleObj);
		itemArray = [{data:1, label:'Rectangle'}, {data:2, label:'Circle'}, {data:3, label:"Polygon"}]
		this.nodeShape = "Rectangle";
		// Create listener object for shape_cb
		var cbListener:Object = new Object();
		// Create event handler function.
		cbListener.change = function (evt_obj:Object) {
			 classRef.nodeShape = evt_obj.target.selectedItem.label;
			 if(evt_obj.target.selectedItem.data == 1)  {
				 //Hide and show as per the selected shape
				 classRef.heightTf._visible = true;
				 classRef.widthTf._visible = true;
				 classRef.radiusTf._visible = false;
				 classRef.numSidesTf._visible = false;
				 classRef.widthLabelTf._visible = true;
				 classRef.heightLabelTf._visible = true;
				 classRef.radiusLabelTf._visible = false;
				 classRef.numSideLabelTf._visible = false;
			 }
			 else if(evt_obj.target.selectedItem.data == 2 || evt_obj.target.selectedItem.data == 3 ){
				 classRef.heightTf._visible = false;
				 classRef.widthTf._visible = false;
				 classRef.radiusTf._visible = true;
				 classRef.numSidesTf._visible = false;
				 classRef.numSideLabelTf._visible = false;
				 if(evt_obj.target.selectedItem.data == 3) {
					 classRef.numSidesTf._visible = true;
					 classRef.numSideLabelTf._visible = true;
				 }
				 classRef.widthLabelTf._visible = false;
				 classRef.heightLabelTf._visible = false;
				 classRef.radiusLabelTf._visible = true;
			 }
		}
		shapes_cb = createCombo("shapes_cb", startDepth++, 63, 121, itemArray, cbListener, true);
		widthLabelTf = Utils.createText(false, "Width", this.db, startDepth++, 174, 123, 0, styleObj, true, 40, 20).tf;
		widthLabelTf.autoSize = true;
		//Create an input textfield
		this.widthTf = createInputFld("width_ti", startDepth++, 216, 121, 45, "0-9");
		this.widthTf.text = '30'
		heightLabelTf = Utils.createText(false, "Height", this.db, startDepth++, 270, 123, 0, styleObj, true, 40, 20).tf;
		heightLabelTf.autoSize = true;
		radiusLabelTf = Utils.createText(false, "Radius", this.db, startDepth++, 174, 123, 0, styleObj, true, 50, 20).tf;
		radiusLabelTf.autoSize = true;
		radiusLabelTf._visible = false;
		//Create an input textfield
		this.heightTf = createInputFld("height_ti", startDepth++, 313, 121, 45, "0-9");
		this.heightTf.text = '30'
		//Create an input textfield for the radius
		this.radiusTf = createInputFld("radius_ti", startDepth++, 216, 121, 45, "0-9", false);
		numSideLabelTf = Utils.createText(false, "Sides", this.db, startDepth++, 270, 123, 0, styleObj, true, 40, 20).tf;
		numSideLabelTf.autoSize = true;
		//Hide it initially - as Rectangle is the default shape chosen
		numSideLabelTf._visible = false;
		//Create an input textfield for the radius
		this.numSidesTf = createInputFld("numSide_ti", startDepth++, 313, 121, 45, "0-9", false);
		/* ------- end of fifth line --------------- */
		
		/* ------ sixth line - link -------- */
		var sixthLineYPos:Number = startY + (25 * 5);
		//Now create the URL TF
		var urlLabelTf:TextField = Utils.createText(false, "URL", this.db, startDepth++, 20, 151, 0, styleObj).tf;
		//Create an input textfield
		this.urlTf = createInputFld("url_ti", startDepth++, 63, 150, 295);
		/* ------ End of sixth line ----------- */
		
		/* ------ Seventh line - Image, Image Align, Image Width & Image Height ------ */
		var seventhLineYPos:Number = startY + (25 * 6);
		//Draw a checkBox for accepting Image parameters
		this.image_ch = createCheckBox("image_ch", startDepth++, {label:' Image', selected:false}, 24, 178);
		//Register the listener
		this.listenerObject.click = function(eventObject:Object) {
			//Negate the visibility
			classRef.imageWidthLabel._visible = !classRef.imageWidthLabel._visible;
			classRef.imageHeightLabel._visible = !classRef.imageHeightLabel._visible;
			classRef.imageURLLabel._visible = !classRef.imageURLLabel._visible;
			classRef.imageAlignLabel._visible = !classRef.imageAlignLabel._visible;
			classRef.imageWidthTf.visible = !classRef.imageWidthTf.visible;
			classRef.imageHeightTf.visible = !classRef.imageHeightTf.visible;
			classRef.imageAlign_cb.visible = !classRef.imageAlign_cb.visible;
			classRef.imageURLTf.visible = !classRef.imageURLTf.visible;
		};
		this.image_ch.addEventListener("click", this.listenerObject);
		//Now create the image width TF
		imageWidthLabel = Utils.createText(false, "Image Width", this.db, startDepth++, 85, 180, 0, styleObj).tf;
		imageWidthLabel._visible = false;
		//Create an input textfield
		this.imageWidthTf = createInputFld("imageWidth_ti", startDepth++, 166, 178, 45, '0-9', false);
		//Now create the image Height TF
		imageHeightLabel = Utils.createText(false, "Image Height", this.db, startDepth++, 222, 180, 0, styleObj).tf;
		imageHeightLabel._visible = false;
		//Create an input textfield
		this.imageHeightTf = createInputFld("imageHeight_ti", startDepth++, 313, 178, 45, '0-9', false);
		/* -------- End of Seventh line ------- */
		
		/* -------- Eighth line - Image align & URL ------- */
		var eighthLineYPos:Number = startY + (25 * 7);
		//Create the image align label 
		imageAlignLabel = Utils.createText(false, "Image Align", this.db, startDepth++, 20, 205, 0, styleObj).tf;
		imageAlignLabel._visible = false;
		
		itemArray = [{data:1, label:'TOP'}, {data:2, label:'MIDDLE'}, {data:3, label:'BOTTOM'}];
		//Set the default nodeShape as First element of DropDown
		this.imageAlign = "BOTTOM";
		// Create listener object for shape_cb
		var iaListener:Object = new Object();
		// Create event handler function.
		iaListener.change = function (evt_obj:Object) {
			 classRef.imageAlign = evt_obj.target.selectedItem.label;
		}
		imageAlign_cb = createCombo("imageAlign_cb", startDepth++, 100, 205, itemArray, iaListener, false);
		imageAlign_cb.setSize(111, imageAlign_cb.height);
		/* -------- end of Eighth line -------- */
		
		/* ------- Strat of Ninth line - Image URL-------- */
		var ninthLineYPos:Number = startY + (25 * 8);
		//Now create the URL TF
		imageURLLabel = Utils.createText(false, "Image URL", this.db, startDepth++, 20, 234, 0, styleObj).tf;
		imageURLLabel._visible = false;
		//Create an input textfield
		this.imageURLTf = createInputFld("imageURL_ti", startDepth++, 100, 232, 260, '', false);
		/* -------- End of ninth line -------- */
		clrPicker.depth = startDepth++
	}
	/**
	 * onClick method Handles the event to process once the Ok Button is pressed
	 */
	private function onClick():Void {
		//Error Flag
		var errFlag:Boolean = false;
		//Loop Variable
		var i:Number;
		//If there is no error we dispatch the event to its parent class
		//Check the id entered with the already stored ID's
		for(i=0;i<this.idArr.length;i++) {
			//Check
			if(idArr[i] == this.idTf.text && !this.isEditNodeDialog) {
				//Set the flag
				errFlag = true;
				//Break
				break;
			}
		}
		// If error found then show the error label
		if(errFlag) { 
			//Make the error TF visible
			this.errorTf._visible = true;
			//Make any other error TF false
			this.errorInvalidTf._visible = false;
		}
		else if(this.idTf.text == "") {
			//Make the error TF visible
			this.errorInvalidTf._visible = true;
			//Make any other error TF false
			this.errorTf._visible = false;
		}
		else {
			//Check if the width and height of the node is given some value
			if(this.widthTf.text == "") {
				this.widthTf.text = "30";
			}
			if(this.heightTf.text == "") {
				this.heightTf.text = "30";
			}
			if(this.radiusTf.text == "") {
				this.radiusTf.text = "20";
			}
			if(this.tooltextTf.text == ""){
				this.tooltextTf.text = this.nameTf.text;
			}
			if(this.alphaTf.text == ""){
				this.alphaTf.text = '100';
			}
			//Dispatch information to parent class to update the required arrays(Connectors, data )
			this.dispatchEvent({type:"onNodeUpdate", target:this, 
							   id:this.idTf.text, dsSeriesName:this.dsSeriesName, 
							   nodeH:this.heightTf.text, nodeW:this.widthTf.text, 
							   nodeR:this.radiusTf.text, 
							   color:clrPicker.color, alpha:this.alphaTf.text,
							   showValue:this.showValue_ch.selected,
							   allowDrag:this.allowDrag_ch.selected,
							   nodeShape:this.nodeShape, 
							   nodeName:this.nameTf.text, 
							   labelAlign:this.labelAlign,
							   numSide:this.numSidesTf.text, 
							   link:this.urlTf.text, 
							   tooltext:this.tooltextTf.text,
							   imageNode: this.image_ch.selected,
							   imageWidth: this.imageWidthTf.text,
							   imageHeight: this.imageHeightTf.text,
							   imageURL: this.imageURLTf.text,
							   imageAlign: this.imageAlign});
			//Hide the dialog box
			this.destroy();
		}
	}
	/**
	 * onCancelClick method Handles the event to process once the Cancel Button is pressed
	 */
	private function onCancelClick():Void {
		//Dispatch information to parent class to update the required arrays(Connectors, data )
		this.dispatchEvent({type:"onCancel", target:this});
		//delete the dialog box
		this.destroy();
	}
	/**
	 * createInputFld method creates a input text field
	 */
	 private function createInputFld(fldName:String, depth:Number, xPos:Number, yPos:Number, fldWidth:Number, restrictStr:String, visible:Boolean):TextInput{
		var newFld:TextInput = this.db.createClassObject(mx.controls.TextInput, fldName, depth);
		newFld._x = xPos;
		newFld._y = yPos;
		newFld.setSize(fldWidth, newFld._height);
		if(restrictStr != undefined && restrictStr != "" && restrictStr != null){
			newFld.restrict = restrictStr;
		}
		if(visible != undefined){
			newFld._visible = visible;
		}
		return newFld;
	 }
	 /**
	  * createCheckBox method creates a check box item
	  */
	  private function createCheckBox(chkBoxName:String, depth:Number, labelObj:Object, xPos:Number, yPos:Number):CheckBox{
		 var newChkBox:CheckBox = this.db.createClassObject(mx.controls.CheckBox, chkBoxName, depth, labelObj);
		 newChkBox._x = xPos;
		 newChkBox._y = yPos;
		 
		 return newChkBox;
	  }
	/**
	 * createCombo method creates a combo box item 
	 * @param	comboBoxName	comboBox Name
	 * @param	depth			level depth
	 * @param	xPos			x position
	 * @param	yPos			y position
	 * @param	itemArray		item Array
	 * @param	listenerObj		listener Object
	 * @param	isVisible		visiblilty
	 * @return					the comboBox created
	 */
	 private function createCombo(comboBoxName:String, depth:Number, xPos:Number, yPos:Number, itemArray:Array, listenerObj:Object, isVisible:Boolean):ComboBox{
		//Draw a ComboBox
		var new_cb = this.db.createClassObject(mx.controls.ComboBox, comboBoxName, depth);
		//Set the position
		new_cb._x = xPos;
		new_cb._y = yPos;
		//Set the size of the comboBox
		new_cb.setSize(new_cb.width, new_cb.height);
		//Add all the different items available for the combos
		for(var i:Number = 0; i<itemArray.length; i++){
			var itemObj:Object = new Object();
			itemObj.data = itemArray[i].data;
			itemObj.label = itemArray[i].label
			new_cb.addItem(itemObj);
		}
		// Add event listener.
		new_cb.addEventListener("change", listenerObj);
		new_cb.visible = isVisible; 
		
		return new_cb;
	 }
	/**
	 * show method shows the Dialog Box.
	 */
	public function show():Void {
		//Show the dialog box
		this.db._visible = true;
	}
	/**
	 * hide method hides the tool tip.
	 */
	public function hide():Void {
		//Hide the dialog box
		this.db._visible = false;
		this.btnOk.hide();
		this.btnCancel.hide();
	}
	/**
	 * destroy method MUST be called whenever you wish to delete this class's
	 * instance.
	 */
	public function destroy():Void {
		this.clrPicker.destroy();
		//remove all the listeners
		this.image_ch.removeEventListener("click", this.listenerObject);
		//Remove Components
		this.shapes_cb.destroyObject();
		this.seriesName_cb.destroyObject();
		//Remove textfields
		this.idTf.destroyObject();
		this.nameTf.destroyObject();
		this.widthTf.destroyObject();
		this.heightTf.destroyObject();
		this.radiusTf.destroyObject();
		this.numSidesTf.destroyObject();	
		this.image_ch.destroyObject();	
		//remove textfields
		this.errorInvalidTf.removeTextField();
		this.errorTf.removeTextField();
		//Delete the movieclip
		this.db.removeMovieClip();
		this.btnOk.destroy();
		this.btnOk.removeEventListener("click", this.btnListener);
		this.btnCancel.destroy();
		this.btnCancel.removeEventListener("click", this.btnCancelListener);
	}
}
