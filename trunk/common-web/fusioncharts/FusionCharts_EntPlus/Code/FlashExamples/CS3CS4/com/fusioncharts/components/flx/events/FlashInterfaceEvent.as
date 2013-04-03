package com.fusioncharts.components.flx.events
{
	import flash.events.Event;

	public class FlashInterfaceEvent extends Event
	{
		/**
		 * Property; custom data defined by the dispatcher.
		 */
		public var data:*;
		
		public function FlashInterfaceEvent(type:String, data:*=null)
		{
			//TODO: implement function
			super(type);
			this.data = data;
		}
	}
}