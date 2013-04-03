/**
* @class ImageUtils
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2.2
*
* Copyright (C) FusionCharts Technologies LLP, 2010
* The ImageUtils class is an all-static class with methods for working with Image. ImageUtils class help to 
* reset Image/Swf by diplaymode, vertical alignment,  horizontal alignment , alpha and scale.
* 
*/

//Import the Matrix class
import flash.geom.Matrix;
//Import the BitmapData class
import flash.display.BitmapData;
//Import the Rectangle class
import flash.geom.Rectangle;

//Class definition
class com.fusioncharts.helper.ImageUtils
{		
	//Need four properties, when display mode is tile and image movieclip is swf.
	//A counter which keep track, no of tile movieclip already loaded.
	private static var completeCounter:Number;
	//Movie clip loader for the sequencial image loading.
	private static var mcLoader:MovieClipLoader;
	//Listener object for the sequencial image loading 
	private static var mcListener:Object;
	//An array to store swf movieclip 
	private static var mcArray:Array;
	
	
	/**
	 * Set image according to display mode 
	 * @param imageUrl   		Url of target image movieclip
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset. 
	 * @param bgHeight  		Background height. According to which target image will be reset. 
	 * @param borderThickness   Border thickness. If any border thickness is preset in background.
	 * @param displayMode		Display mode ['none', 'center', 'stretch', 'tile', 'fit', 'fill'].
	 *
	 * @return imageMC			Updated target image movieclip
	 */
	public static function setDisplayMode(imageUrl:String,
										  imageMC:MovieClip, 
										  parentMC:MovieClip,
										  bgWidth:Number, 
										  bgHeight:Number, 
										  borderThickness:Number, 
										  displayMode:String):MovieClip
	{		
		displayMode = displayMode.toLowerCase();
		switch(displayMode)
		{
			case "center":
				setCenterDisplayMode(imageMC, parentMC, bgWidth, bgHeight, borderThickness);
				break;
			
			case "stretch"://Stretch Display Mode 
				setStretchDisplayMode(imageMC, bgWidth, bgHeight, borderThickness);
				break;
				
			case "tile"://Tile Display Mode
				return setTileDisplayMode(imageUrl, imageMC, parentMC, bgWidth, bgHeight, borderThickness);
				
			case "fit"://Fit Display Mode
				setFitDisplayMode(imageMC, parentMC, bgWidth, bgHeight, borderThickness);
				break;
				
			case "fill"://Fill Display Mode
				setFillDisplayMode(imageMC, parentMC, bgWidth, bgHeight, borderThickness);
				break;
			
			default : // None
				setNoneDisplayMode(imageMC, parentMC, bgWidth, bgHeight, borderThickness);
		}
		
		return imageMC;
	}
	
	/**
	 * Set image according to center mode. 
	 *
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset. 
	 * @param bgHeight  		Background height. According to which target image will be reset. 
	 * @param borderThickness   Border thickness. If any border thickness is preset in background.
	 *
	 */
	public static function setCenterDisplayMode(imageMC:MovieClip, 
												  parentMC:MovieClip,
												  bgWidth:Number, 
												  bgHeight:Number, 
												  borderThickness:Number):Void
	{
			
		//In "center" display mode image will show with original aspect ratio and it will position in the center of screen. 
		setAlignment(imageMC, bgWidth, bgHeight, borderThickness, "middle", "middle");
		//Set masking
		setMaskMcOf(imageMC, parentMC, bgWidth, bgHeight, borderThickness);	
	}
	/**
	 * Set image according to stretch mode. 
	 *
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset. 
	 * @param bgHeight  		Background height. According to which target image will be reset.  
	 * @param borderThickness   Border thickness. If any border thickness is preset in background.
	 *
	 */	
	public static function setStretchDisplayMode(imageMC:MovieClip,
												  bgWidth:Number, 
												  bgHeight:Number, 
												  borderThickness:Number):Void
	{
		//In stretch mode image alwasys will be in top and left position.
		setAlignment(imageMC, bgWidth, bgHeight, borderThickness, "top", "left");
		
		//Consider the borderthickness and set image's actual width and height  
		imageMC._width = bgWidth - (borderThickness * 2);
	    imageMC._height = bgHeight - (borderThickness * 2);		
	}
	/**
	 * Set image according to tile mode. 
	 *
	 * @param imageUrl   		Url of target image movieclip
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset. 
	 * @param bgHeight  		Background height. According to which target image will be reset. 
	 * @param borderThickness   Border thickness. If any border thickness is preset in background.
	 * @return MovieClip        A movieclip where tile is drawing.  
	 */
	public static function setTileDisplayMode(imageUrl:String,
											  	  imageMC:MovieClip,
											  	  parentMC:MovieClip,
												  bgWidth:Number, 
												  bgHeight:Number, 
												  borderThickness:Number):MovieClip
	{		
		var isSwf:Boolean;
		//Check if flash movie as background of the chart is swf 
		isSwf = (imageUrl.substr(imageUrl.length - 3, 3)).toLowerCase() == "swf" ? true : false;
		
		//Tile movieclip, using only for tile mode
		var tileMc:MovieClip = parentMC.createEmptyMovieClip("tileMc", (parentMC.getNextHighestDepth()));		
		
		//Calculate chart width and height.
		var cWidth:Number = bgWidth ;
		var cHeight:Number = bgHeight ;
		
		//Find out column number 
		var numColumns:Number = Math.ceil(cWidth/imageMC._width);
		//Find out row number
		var numRows:Number = Math.ceil(cHeight/imageMC._height);
		
		//Calculate tile width and height
		var tileWidth:Number = numColumns * Math.round(imageMC._width);
		var tileHeight:Number = numRows * Math.round(imageMC._height);
		
		//Set image movieclip invisible
		imageMC._visible = false;		
		
		//If image movieclip is a swf then it will load rowwise and columnwise sequencially. 
		if(isSwf)
		{
			//Reinitialize
			mcLoader = new MovieClipLoader();
			mcListener = {};
			mcLoader.addListener(mcListener);
			
			mcListener.onLoadComplete = function(targetMC:MovieClip)
			{						
				//All swf still not load so stop current movieclip from playing 			
				targetMC.stop();			
			}			
			
			mcListener.onLoadInit = function(targetMC:MovieClip)
			{						
				//Set scale according to image
				targetMC._xscale = imageMC._xscale;
				targetMC._yscale = imageMC._yscale;				
				updateCounter(numColumns, numRows, tileMc);
			}
			
			mcArray = [];
			
			//Set tile movieclip invisible
			tileMc._visible = false;
			
			//Set couter 
			completeCounter = 0;
			
			//Load image 
			loadTileImages(imageUrl, imageMC, tileMc, numColumns, numRows);
			
		}
		else
		{
			//Matrix object
			var matrix:Matrix = new Matrix();
			//Reinitialize matrix object.
			matrix.identity();
			//Define matrix scale according to image
			matrix.scale(imageMC._xscale/100, imageMC._yscale/100);
			
			//Create bitmap data of image movieclip.
			var bmpData:BitmapData = new BitmapData(Math.round(imageMC._width), Math.round(imageMC._height), true, 0x00ffffff);
			//Draw bitmap data with actual images scale
			bmpData.draw(imageMC, matrix);			
			
			//Reinitialize matrix object.
			matrix.identity();	
			
			//Hence background mode is tile so image will repeate rowwise and columnwise.  
			tileMc.beginBitmapFill(bmpData, matrix, true, true);				
			
		}		
		
		//Create space so image can repeat in that place

		tileMc.moveTo(0, 0);
		tileMc.lineTo(tileWidth , 0);
		tileMc.lineTo(tileWidth, tileHeight);
		tileMc.lineTo(0, tileHeight);
		tileMc.lineTo(0, 0);
		
		//Set masking
		setMaskMcOf(tileMc, parentMC, bgWidth, bgHeight, borderThickness);
		
		return tileMc;
	}
	
	/**
	 * Load all images when display mode is tile(and image type is swf). 
	 *
	 * @param imageUrl   		Url of target image movieclip
     * @param imageMC   		Target image movieclip
	 * @param tileMC 		 	Tile movieclip holder. 
 	 * @param columnNo			Number of columns.  
	 * @param numRows  			Number of rows. 
	 */
	private static function loadTileImages(imageUrl:String,
											imageMC:MovieClip,
											tileMC:MovieClip,
											numColumns:Number,
											numRows:Number):Void
	{
		var xPos:Number = 0;
		var yPos:Number = 0;
		var tMc:MovieClip;
		
		for(var i:Number = 1 ; i<= numRows; i++)
		{
			//Reset x position 
			xPos = 0;
			for(var j:Number = 1 ; j<=numColumns; j++)
			{
				tMc = tileMC.createEmptyMovieClip("tMc_"+i+"_"+j, tileMC.getNextHighestDepth());
				
				//Set x and y position 
				tMc._x = xPos;
				tMc._y = yPos;	
				
				//Update next image's x position
				xPos += imageMC._width;
				
				//Store target movieclip ref into mcArray
				mcArray.push(tMc);	
				
				//Now, load the imgage
				mcLoader.loadClip(imageUrl, tMc); 					
			}		
			//Update next image's y position.
			yPos +=   imageMC._height ;
			
		}			
	}
	
	/**
	 * This method check whether all tile movieclip completely loaded or not 
	 *
	 * @param columnNo			Number of columns.  
	 * @param numRows  			Number of rows. 
     * @param tileMC 		 	Tile movieclip holder. 
	 */   
	private static function updateCounter(numColumns:Number, numRows:Number, tileMC:MovieClip):Void
	{
		//Update counter
		completeCounter++;		
		if(completeCounter == numColumns*numRows)
		{
			var arrayLength:Number = mcArray.length;
			
			//All swf loaded so all movieclip can play now.
			for(var i:Number = 0; i < arrayLength; i++)
			{
				MovieClip(mcArray[i]).play();				
			}
			
			mcArray = [];
			
			//All swf loaded so tileMC movieclip can be visible now. 
			tileMC._visible = true;
			
			mcListener = null;
			//Remove listener
			mcLoader.removeListener(mcListener);
			//Reset counter
			completeCounter = 0;
		}
	}	
	
	/**
	 * Set image according to fit mode. 
	 *
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset. 
	 * @param bgHeight  		Background height. According to which target image will be reset.  
	 * @param borderThickness   Border thickness.If any border thickness is preset in background.
	 *
	 */	
	public static function setFitDisplayMode(imageMC:MovieClip,
											 	  parentMC:MovieClip,
												  bgWidth:Number, 
												  bgHeight:Number, 
												  borderThickness:Number):Void
	{	
		//Create matrix
		var matrix:Matrix = new Matrix();
		//Reinitialize matrix object.
		matrix.identity();
		
		//Consider the borderthickness and find out chart width and height 
		var cWidth:Number = bgWidth - (borderThickness * 2);
		var cHeight:Number = bgHeight - (borderThickness * 2);
		
		//Original image's aspect ratio. 
		var imgAspectRatio:Number = imageMC._width / imageMC._height;	
		//Chart's background aspect ratio
		var cAspectRatio:Number = cWidth / cHeight;
		var scaleFactor:Number = (imgAspectRatio > cAspectRatio) ?  cWidth / imageMC._width : cHeight / imageMC._height;		
		
		//Set matrix for scaling
		matrix.scale(scaleFactor, scaleFactor);
		//Set image transformation
		imageMC.transform.matrix = matrix;
		
		//Set mask
		setMaskMcOf(imageMC, parentMC, bgWidth, bgHeight, borderThickness);		
	}
	/**
	 * Set image according to fill mode. 
	 *
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset.

	 * @param bgHeight  		Background height. According to which target image will be reset.
	 * @param borderThickness   Border thickness. If any border thickness is preset in background.
	 *
	 */	
	public static function setFillDisplayMode(imageMC:MovieClip,
											      parentMC:MovieClip,
												  bgWidth:Number, 
												  bgHeight:Number, 
												  borderThickness:Number):Void
	{
		
		//Create matrix
		var matrix:Matrix = new Matrix();
		//Reinitialize matrix object.
		matrix.identity();
		
		//Consider the borderthickness and find out chart width and height 
		var cWidth:Number = bgWidth - (borderThickness * 2);
		var cHeight:Number = bgHeight - (borderThickness * 2);
		
		//Original image's aspect ratio. 
		var imgAspectRatio:Number = imageMC._width / imageMC._height;
		//Chart's background aspect ratio
		var cAspectRatio:Number = cWidth / cHeight;
		//Find out scale factor
		var scaleFactor:Number = (imgAspectRatio > cAspectRatio) ?  cHeight / imageMC._height : cWidth / imageMC._width;		
		
		//Set matrix for scaling
		matrix.scale(scaleFactor, scaleFactor);
		//Set image transformation
		imageMC.transform.matrix = matrix;		
		
		//Set mask
		setMaskMcOf(imageMC, parentMC, bgWidth, bgHeight, borderThickness);				
	}
	/**
	 * Set image according to none mode. 
	 *
     * @param imageMC   		Target image movieclip
	 * @param parentMC 		 	A movieclip which hold target image movieclip. 
 	 * @param bgWidth			Background width. According to which target image will be reset.
	 * @param bgHeight  		Background height. According to which target image will be reset. 
	 * @param borderThickness   Border thickness. If any border thickness is preset in background.
	 *
	 */	
	public static function setNoneDisplayMode(imageMC:MovieClip,
											      parentMC:MovieClip,
												  bgWidth:Number, 
												  bgHeight:Number, 
												  borderThickness:Number):Void
	{		
		//In none mode image only need alignment .
		
		//Set mask
		setMaskMcOf(imageMC, parentMC, bgWidth, bgHeight, borderThickness);
	}
	/**
	 * This method set alpha and scale properties  of image.
	 *
     * @param imageMC   		Target image movieclip
	 * @param propObj 		 	An object which store alpha and scale properties. e.g.  {propName:"bgImageAlpha", value: alpha value}
 	 *
	 */	
	public static function setPropOf(imageMC:MovieClip, 
									 propObj:Object):Void
	{		
		switch(propObj.propName.toLowerCase())
		{
			case "bgswfalpha":
				imageMC._alpha = propObj.value;
				break;
				
			case "bgimagealpha":
				imageMC._alpha = propObj.value;
				break;
				
			case "bgimagevalign":
				// If need in future.
				break;
				
			case "bgimagehalign":
				// If need in future.
				break;
				
			case "bgimagescale":
				// no negative value
				imageMC._xscale = Math.abs(propObj.value);
				imageMC._yscale = Math.abs(propObj.value);
				break;
		}		
	}
	
	/**
	 * setAlignment method reset the alignment of given image.
	 * @param 	imageMC  		Image movieclip which is loaded in background.
	 * @param   bgWidth			Background width. According to which target image will be reset.
	 * @param   bgHeight  		Background height. According to which target image will be reset.
	 * @param 	borderThickness Border thickness. If any border thickness is preset in background.
	 * @param 	vAlign			Vertical alignment['top', 'middle', 'bottom']
	 * @param 	hAlign			Horizontal alignment ['left', 'middle', 'right']	
	 */	
	public static function setAlignment(imageMC:MovieClip, 
										  bgWidth:Number, 
										  bgHeight:Number, 
										  borderThickness:Number, 
										  vAlign:String, 
										  hAlign:String):Void
	{		
		//Set vertical alignment
		switch(vAlign.toLowerCase()) 

		{
			case "top":
				imageMC._y = borderThickness;				
				break;
			case "middle":
				imageMC._y = (bgHeight/2) - (imageMC._height/2);
				break;
			case "bottom":
				imageMC._y = bgHeight - imageMC._height - borderThickness;
				break;	
		}
		
		//Set horizontal alignment
		switch(hAlign.toLowerCase())
		{
			case "left":
				imageMC._x = borderThickness;
				break;
			case "middle":
				imageMC._x = (bgWidth/2) - (imageMC._width/2);
				break;
			case "right":					
				imageMC._x = bgWidth - imageMC._width - borderThickness;		
				break;
		}		
	}
	/**
	 * set a mask of given movieclip according to height, width and thickness.
	 */
	private static function setMaskMcOf(imageMC:MovieClip,
										container:MovieClip,
									    bgWidth:Number, 
										bgHeight:Number, 
									  	borderThickness):Void
	{
		var maskMC:MovieClip = container.createEmptyMovieClip("maskMc", container.getNextHighestDepth());
		bgWidth = bgWidth - borderThickness;
		bgHeight = bgHeight - borderThickness;
		
		//Set height and width
		maskMC.beginFill(0x000000);
		maskMC.moveTo(borderThickness, borderThickness);
		maskMC.lineTo(bgWidth, borderThickness);
		maskMC.lineTo(bgWidth, bgHeight);
		maskMC.lineTo(borderThickness, bgHeight);
		maskMC.lineTo(borderThickness, borderThickness);
		maskMC.endFill();
		imageMC.setMask(maskMC);		
	}
}