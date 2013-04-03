package com.events
{
	//Import Event class
	import flash.events.*;
	
	//FCEvent class extends the Event class
	//We can use the default Event class to track and listen FusionCharts events.
	//But FCEvent class will work here as specific to our component
	public class FCEvent extends Event
	{
		//Function constants
		public static const FCClickEvent:String="FCClickEvent";
		public static const FCRenderEvent:String="FCRenderEvent";
		public static const FCLoadEvent:String="FCLoadEvent";
		//public static const FCErrorEvent:String="FCErrorEvent";
		public static const FCExported:String="FCExported";
		public static const FCDataLoadedEvent:String="FCDataLoadedEvent";
		public static const FCDataLoadErrorEvent:String="FCDataLoadErrorEvent";
		public static const FCDataXMLInvalidEvent:String="FCDataXMLInvalidEvent";
		public static const FCNoDataToDisplayEvent:String="FCNoDataToDisplayEvent";
		//public static const FCChartUpdatedEvent:String="FCChartUpdatedEvent";
		//public static const FCAlertEvent:String="FCAlertEvent";
		//public static const FCMessageEvent:String="FCMessageEvent";
		
		public var param:*;
		
		public function FCEvent(type:String, param:*):void
		{
			super(type);
			this.param=param;
		}
		
		//As we are dealing with custom events with custom event class,
		//overriding the method clone and dispatch the event as FCEvent type.
		override public function clone():Event
		{
            return new FCEvent(type, param);
  		} 

	} 
}