/**
* @class FusionCharts
* @author FusionCharts Technologies LLP, www.fusioncharts.com
* @version 3.2
*
*  Copyright (C) FusionCharts Technologies LLP, 2010
*
*  FusionCharts class is the wrapper/communication class
*  which wraps AVM1 or Flash 8/ActionScrip 2 files and 
*  makes it available for AVM2 or Flash 9/ActionScript 3 
*  files and higher. 
*  The class uses FlashInterface and its methods.
*/
package com.fusioncharts.components
{
	//Importing classes
	import com.events.FCEvent;
	import com.fusioncharts.components.codec.JPEGEncoder;
	import com.fusioncharts.components.codec.PDFEncoder;
	import com.fusioncharts.components.codec.PNGEncoder;
	import com.fusioncharts.components.flx.events.FlashInterfaceEvent;
	import com.fusioncharts.components.flx.external.FlashInterface;
	
	import flash.display.*;
	import flash.events.*;
	import flash.geom.Matrix;
	import flash.geom.Rectangle;
	import flash.net.*;
	import flash.printing.PrintJob;
	import flash.utils.ByteArray;
	
	//FusionCharts class extends the Sprite class
	public class FusionCharts extends Sprite
	{
		//Loader instance
		private var loader:Loader;
		//Uniqe ID for different chart object
		private var Id:String;
		//Chart type or chart name
		private var chartType:String;
		//Folder containig the charts
		private var folder:String;
		//URL of the XML data file
		private var xmlURL:String;
		//Debug mode
		private var debug:String;
		//Data XML as String
		private var xmlDATA:String;
		//Chart width
		private var chartWidth:Number=400;
		//Chart height
		private var chartHeight:Number=300;
		//Static Flag to check main application swf publishing
		private static var isPublished:Boolean=false;
		//Flag to track chart loading on Loader
		private var loadedCheck:Boolean;
		//Flag to track whether chart type changed at runtime
		private var isChartChanged:Boolean;
		//Flag to track whether chart folder changed at runtime
		private var isFolderChanged:Boolean;
		//Flag to track whether data coming to chart from URL
		private var isURLprovided:Boolean;
		//Flag to track whether data coming to chart as XML String
		private var isXMLprovided:Boolean;
		//tracking og firstime rendering
		private var rendered:Boolean;
		//export related
		private var imgData:ByteArray;
		private var fileName:String;
		private var fileFormat:String;
		private var success:Boolean;
		//Check for largeData
		private var isLargeData:Boolean;
		
		//Constructor function
		public function FusionCharts():void
		{
			//Initialization of Loader class object
			loader=new Loader();
			//Registering listener for the loader
			//When the loading get completed, it will call the 'loaded' function
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, loaded);
			//Registering loader with IO Error event
			loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, loaderErrorHandler);
			//Add loader to display list
			addChild(loader);
			//Loader instance name as a unique Id to interact with FlashInterface 
			Id=loader.name.toString();	
			//Initilization of other properties
			folder="fusioncharts/";			
			xmlURL="";
			xmlDATA="";
			debug="0";
			loadedCheck=isChartChanged=isFolderChanged=isURLprovided=isXMLprovided=false;
		}
		
		//Load the chart swf and render
		public function FCRender():void
		{
			//Loading the chart and sending chart property values as query string
			if(rendered)
			{
				//Checking if user changing the chart type or chart containing folder
				if((isChartChanged && !loadedCheck) || isFolderChanged)
				{
					//If the loader object having loaded content
					if(loader.content!=null)
					{					
						try
						{
							//Destroying 'chart' instance in loaded chart
							//FlashInterface.call(Id+".chart.destroy");
						}
						catch(err:Error)
						{
							//custom code here to track errors, if there any
						}
						
						//Removing event listeners					
						loader.contentLoaderInfo.removeEventListener(Event.COMPLETE, loaded);
						loader.contentLoaderInfo.removeEventListener(IOErrorEvent.IO_ERROR, loaderErrorHandler);
						//Removing the loader from Display List							
						removeChild(loader);					
						//Setting the loader object into null to garbage collected
						loader=null;
						//Force garbage-collection tweak
						try
						{
						      var lc1:LocalConnection = new LocalConnection();
						      var lc2:LocalConnection = new LocalConnection();				
						      lc1.connect( "gcConnection" );
						      lc2.connect( "gcConnection" );
						}
						catch (e:Error)
						{
							//custom code here to track errors, if there any
						}			
			 									
					}				
									
					//Creating new object of Loader class								
					loader = new Loader();				
					//Registering event listeners
					loader.contentLoaderInfo.addEventListener(Event.COMPLETE, loaded);
					loader.contentLoaderInfo.addEventListener(IOErrorEvent.IO_ERROR, loaderErrorHandler);			
		 			//Adding the loader object to display list
					addChild(loader);
					//Loader instance name as a unique Id to interact with FlashInterface 
					Id=loader.name.toString();
					//Setting flag
					isChartChanged=false;
					isFolderChanged=false;
					loadedCheck=true;
					//Calling render function to load chart
					_fcRender();							
				}
				//If the 'FCRender' function call is only for changing the source data URL
				else if(isURLprovided)
				{
					try
					{
						FlashInterface.call(Id+".setDataURL",xmlURL);
					}
					catch(err:Error)
					{
						//custom code here to track errors, if there any
					}
					//Updating flag	
					isURLprovided=false;
				}
				//If the 'FCRender' function call is only for changing the data xml string
				else if(isXMLprovided)
				{
					try
					{
						FlashInterface.call(Id+".setDataXML",xmlDATA);
					}
					catch(err:Error)
					{
						//custom code here to track errors, if there any
					}
					//Updating flag	
					isXMLprovided=false;
				}
				//Only re-drawing the chart with existing data
				else
				{
					try
					{
						FlashInterface.call(Id+".setDataXML",xmlDATA);
					}
					catch(err:Error)
					{
						//custom code here to track errors, if there any
					}
				}
			}
			if(!rendered)
			{
				_fcRender();
			}
				
		}
		
		//Loads the provided chart on the loader.
		private function _fcRender():void
		{
			var urlRequest:URLRequest = new URLRequest(folder+chartType+".swf?flashId="+Id+"&defaultdatafile=Data.xml"+"&dataURL="+xmlURL+"&dataXML="+xmlDATA+"&chartwidth="+chartWidth+"&chartheight="+chartHeight+"&debugMode="+debug+"&mode=flex");
			//Checking for large URL string
			if(urlRequest.url.length>1500)
			{
				var urlRequest2:URLRequest = new URLRequest(folder+chartType+".swf?flashId="+Id+"&chartwidth="+chartWidth+"&chartheight="+chartHeight+"&debugMode="+debug+"&mode=flex");	
				loader.load(urlRequest2);
				isLargeData=true;
			}
			else
			{
				loader.load(urlRequest);
			}
			
			//render flag	
			rendered=true;
			//Mask for the loaded chart to hide the extra content outside of the 
			//specified width height (basically for the True3D chart)
			var rec:Rectangle=new Rectangle(0, 0, chartWidth, chartHeight);
		 	loader.scrollRect=rec;
		}			
		
		//Error handler related to chart loading by loader
		private function loaderErrorHandler(e:Event):void
		{
			//custom code here to track errors, if there any
		}
		
		//Function executed when charts loaded completely on loader
		private function loaded(e:Event):void
		{
			//loader x,y position
			loader.x=loader.y=0;
			//Setting of flags	
			isChartChanged=isFolderChanged=isURLprovided=isXMLprovided=loadedCheck=false;
			
			//Dispatching 'FCLoadEvent' event to access it from outside
			dispatchEvent(new FCEvent("FCLoadEvent",this.name));
			
			try
			{	
				//We dont need to publish the main swf for every chart object,
				//so we are updating the static variable to restrict it. 
				if(!isPublished)
				{	
					//Publishing the application(through FlashInterface's publish method)
					//to establish connection between AVM1 and AVM2	(or AS2 and AS3)
					FlashInterface.publish(this, true);
					isPublished=true;
				}
				
				//Listening click events from loaded chart
				FlashInterface.addEventListener(Id+"_linkClick",linkClickHandler);
				//Listening chart rendering events from loaded chart
				FlashInterface.addEventListener(Id+"_chartRendered",renderHandler);
				//Listening function execution events from loaded chart
				FlashInterface.addEventListener(Id+"_funcExecute",executeHandler);
				//Listening chart export events from loaded chart
				FlashInterface.addEventListener(Id+"_exportChart",exportHandler);
				//Listening data load events from loaded chart
				FlashInterface.addEventListener(Id+"_dataLoaded",dataLoadedHandler);
				//Listening data load error events from loaded chart
				FlashInterface.addEventListener(Id+"_dataLoadError",dataLoadErrorHandler);
				//Listening data XML invalid events from loaded chart
				FlashInterface.addEventListener(Id+"_dataXMLInvalid",dataXMLInvalidHandler);
				//Listening data XML invalid events from loaded chart
				FlashInterface.addEventListener(Id+"_noDataToDisplay",noDataToDisplayHandler);
				
				//Listening to track the time when we can invoke setDataXML
				FlashInterface.addEventListener(Id+"_internalEvent",readyForDataHandler);				
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any
			}
								
		}
		
		//giving large data to chart which is not possible with query-string
		private function readyForDataHandler(e:FlashInterfaceEvent):void
		{
						
			if(xmlURL=="" && isLargeData && e.data=="loaded")
			{
				FlashInterface.call(Id+".setDataXML",xmlDATA);
				isLargeData=false;
			}
			
			//if the dataURL itself a long one
			else if(xmlURL!="" && isLargeData && e.data=="loaded")
			{				
				FlashInterface.call(Id+".setDataURL",xmlURL);
				isLargeData=false;
			}
			
			dispatchEvent(new FCEvent("_FCInternalEvent", "readyForData"));
			
			FlashInterface.call(Id+".chart.test");
			
		}
		
		//Dispatching chart cick event as 'FCClickEvent' to listen it from outside
		private function linkClickHandler(e:FlashInterfaceEvent):void
		{
			dispatchEvent(new FCEvent("FCClickEvent", e.data));
		}
		
		//Dispatching chart rendered event as 'FCRenderEvent' to listen it outside
		private function renderHandler(e:FlashInterfaceEvent):void
		{
			dispatchEvent(new FCEvent("FCRenderEvent", this.name));
		}		
		
		//Execution of user-defined event in Flex
		private function executeHandler(e:FlashInterfaceEvent):void
		{
			//Function name
			var temp_func:Array=String(e.data).split(",");
			//Function arguments
			var temp_arg:String="";
			for(var i:Number=1;i<temp_func.length;i++)
			{
				temp_arg+=temp_func[i].toString();
				if(i!=temp_func.length-1)
					temp_arg+=",";
			}
			//Calling the function
			root[temp_func[0].toString()](temp_arg);
		}
		
		//Dispatching chart rendered event as 'FCDataLoadedEvent' to listen it from Flex
		private function dataLoadedHandler(e:FlashInterfaceEvent):void
		{
			dispatchEvent(new FCEvent(FCEvent.FCDataLoadedEvent, this.name));
		}
		
		//Dispatching data load error as 'FCDataLoadErrorEvent' to listen it from Flex
		private function dataLoadErrorHandler(e:FlashInterfaceEvent):void
		{
			dispatchEvent(new FCEvent(FCEvent.FCDataLoadErrorEvent, this.name));
		}
		
		//Dispatching data XML invalid error as 'FCDataXMLInvalidEvent' to listen it from Flex
		private function dataXMLInvalidHandler(e:FlashInterfaceEvent):void
		{
			dispatchEvent(new FCEvent(FCEvent.FCDataXMLInvalidEvent, this.name));
		}
		
		//Dispatching no data to display error as 'FCNoDataToDisplayEvent' to listen it from Flex
		private function noDataToDisplayHandler(e:FlashInterfaceEvent):void
		{
			dispatchEvent(new FCEvent(FCEvent.FCNoDataToDisplayEvent, this.name));
		}
		
		//Setter function to set chart type
		public function set FCChartType(value:String):void
		{
			chartType=value;
			//Flag to track chart change at run time
			isChartChanged=true;			
		}
		//Getter function to get chart type
		public function get FCChartType():String
		{
			return chartType;
		}
		
		//Setter function to set chart width
		public function set FCChartWidth(value:Number):void
		{
			chartWidth=value;		
		}
		//Getter function to get chart width
		public function get FCChartWidth():Number
		{
			return chartWidth;
		}
		
		//Setter function to set chart height
		public function set FCChartHeight(value:Number):void
		{
			chartHeight = value;			
		}
		//Getter function to get chart height
		public function get FCChartHeight():Number
		{
			return chartHeight;
		}		
		
		//Setter function to set chart containing folder
		public function set FCFolder(value:String):void
		{
			//If user leave the property blank, we will use the default one
			if(value=="")
				folder="fusioncharts/";	
			//If user places charts in the main project folder	
			else if(value==".")
				folder="";
			//User defined folder	
			else
				folder=value+"/";	
			
			//Updating the flag
			isFolderChanged=true;				
		}
		//Getter function to get chart containg folder
		public function get FCFolder():String
		{
			return folder;
		}		
		
		//Setter function to set data xml file URL
		public function set FCDataURL(value:String):void
		{
			xmlURL=encodeURIComponent(value);
			//Updating the flag
			isURLprovided=true;			
		}
		//Getter function to get chart's data xml file URL
		public function get FCDataURL():String
		{
			return xmlURL;
		}
		
		//Setter function to accept xml data as string to generate chart
		public function set FCDataXML(value:String):void
		{
			xmlDATA=encodeURIComponent(value);
			//Updating the flag
			isXMLprovided=true;
		}
		//Getter function to return the chart xml
		public function get FCDataXML():String
		{
			return xmlDATA;
		}
		
		//Setter function to set chart's debug mode
		public function set FCDebugMode(value:Boolean):void
		{
			debug=(value==true)?"1":"0";
		}
		//Getter function returns chart's debug status
		public function get FCDebugMode():Boolean
		{
			return (debug=="1")?true:false;
		}
		
		//FusionCharts's inbuilt setDataURL property
		public function FCSetDataURL(value:String):void
		{
			try
			{
				FlashInterface.call(Id+".setDataURL",value);
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any
			}		
		}
		
		//FusionCharts's inbuilt setDataXML property
		public function FCSetDataXML(value:String):void
		{
			try
			{
				FlashInterface.call(Id+".setDataXML",value);
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any
			}	
		}
		
		//Print chart
		public function FCPrintChart():void
		{
			var pj:PrintJob=new PrintJob();
			if(pj.start())			
			{
				try
				{
					pj.addPage(this, new Rectangle(0,0, chartWidth, chartHeight));
					pj.send();
				}
				catch(error:Error)
				{
					//custom code here to track errors, if there any	
				}
				
			}
		}
		
		//FusionCharts's inbuilt CSV data export function
		public function FCGetCSVData():String
		{
			var temp:Object;
			try
			{
				temp=FlashInterface.call(Id+".chart.exportChartDataCSV");				
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any	
			}
			return temp.result;	
		}
		
		//FusionCharts's inbuilt XML return function
		public function FCGetXMLData():String
		{
			var temp:Object;
			try
			{
				temp=FlashInterface.call(Id+".chart.returnXML");				
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any	
			}
			return temp.result;	
		}
		
		//FusionCharts's inbuilt signature return function
		public function FCGetSignature():String
		{
			var temp:Object;
			try
			{
				temp=FlashInterface.call(Id+".chart.signature");				
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any	
			}
			return temp.result;	
		}
		
		//FusionCharts's inbuilt attribute-value return function
		public function FCGetAttribute(value:String):String
		{
			var temp:Object;
			try
			{
				temp=FlashInterface.call(Id+".chart.returnChartAttribute", value);
			}
			catch(err:Error)
			{
				//custom code here to track errors, if there any	
			}	
			return temp.result;
		}
		
		//Execution of export chart event in Flex
		private function exportHandler(e:FlashInterfaceEvent):void
		{
            export();			
		}
		
		//The process to save chart images locally.
		public function FCExportChart(...value):BitmapData
		{		
           	return export();
		}
		
		//According to the format specified the raw byte array get encoded
		private function export():BitmapData
		{
			var bmp:BitmapData=new BitmapData(this.width,this.height,true);
			var matrix:Matrix = new Matrix();
            bmp.draw(this, matrix);
            
			return  bmp;
		}
		
			
	}
}