//////////////////////////////////////////////////////////////////////////////////////
//
//  FlashInterface - Flash Communication between ActionScript Virtual Machines (AVMs)
//  Copyright (C) 2006  Robert Taylor
//	This program is free software; you can redistribute it and/or
//	modify it under the terms of the GNU General Public License
//	as published by the Free Software Foundation; either version 2
//	of the License, or (at your option) any later version.
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with this program; if not, write to the Free Software
//	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//
////////////////////////////////////////////////////////////////////////////////////

package com.fusioncharts.components.flx.external
{
	import com.fusioncharts.components.flx.data.Registry;
	import com.fusioncharts.components.flx.events.FlashInterfaceEvent;
	
	import flash.display.DisplayObject;
	import flash.display.LoaderInfo;
	import flash.external.ExternalInterface;

	/**
	 * When subscribing as an event listener, the event type is FlashInterfaceEvent.
	 * No event can be defined since this is handled anonymously through the ActionScript 
	 * Virtual Machines (AVMs). FlashInterfaceEvent contains all properties and methods of
	 * Event and the property <i>data</i> which contains any data send from the dispatching 
	 * object.
	 * 
	 * @eventType flx.events.FlashInterfaceEvent.*
	 */
	[Event(name="*", type="flx.events.FlashInterfaceEvent")]

	/**
	 * <p>
	 * <div align="left"><img src="http://www.flashextensions.com/products/flashinterface/examples/flashinterface_small.gif" /></div>
	 * </p>
	 * <p>
	 * <strong>Author:</strong> Robert Taylor<br />
	 * <strong>Version:</strong> 2.1.0<br />
	 * <strong>Created:</strong> 28 Oct 2006<br />
	 * <strong>Last Updated:</strong> 03 Dec 2006<br />
	 * <strong>Website: </strong><a href="http://www.flashextensions.com">http://www.flashextensions.com</a><br />
	 * <strong>Documentation: </strong><a href="http://www.flashextensions.com/products/flashinterface.php">http://www.flashextensions.com/products/flashinterface.php</a><br />
	 * <strong>Description: </strong>
	 * Provides the means to communicate directly between the Flash 8 and 9 ActionScript Virtual Machines (AVM).<br /><br />
	 * <i>FlashInterface AS2 Class</i>
	 * <ul>
	 * <li>Flash 8 to Flash 9 communication</li>
	 * <li>Flash 8 to Flash 8 communication</li>
	 * </ul>
	 * <i>FlashInterface AS3 Class</i>
	 * <ul>
	 * <li>Flash 9 to Flash 8 communication</li>
	 * <li>Flash 9 to Flash 9 communication</li>
	 * </ul>
	 * Flash 9 player has been developed with a new ActionScript Virtual Machine (AVM). Versions of 
	 * the Flash player 8 and lower run under the first AVM. The two virtual 
	 * machines do not support direct communication back and forth with each other.
	 * FlashInterface establishes a means for the two virtual machines to
	 * communicate directly with each other, both synchronously and asynchronously.
	 * </p>
	 * <p>
	 * 		<div align="center"><a href="http://www.flashextensions.com/products.php"><img src="http://www.flashextensions.com/flx/images/banners/flx_solutions_172_x52.png" alt="Flash and Flex Solutions" border="0" /></a></div>
	 * </p>
	*/
	public class FlashInterface
	{
		private static var flashIdList:Object = new Object();
		private static var staticInit:Boolean = false;
		private static var __swfID:String = getSWFId();
		private static var flashId:String;
				
		/**
		 * Method; registers a listener object with a component instance that is broadcasting 
		 * an event. When the event occurs, the listener object or function is notified. 
		 * FlashInterface uses the EventDispatcher provided by the Flash framework. You many look up 
		 * the documentation regarding its usage for any specific details.
		 * <p>
		 * <pre>
		 * import flx.events.FlashInterface;
		 * FlashInterface.addEventListener("message", messageHandler);
		 * 
		 * function messageHandler(evt:Object)
		 * {
		 * 		FlashInterface.alert("You said", evt.data);
		 * }
		 * </pre>
		 * </p> 
		 * @param	event:String a string that is the name of the event.
		 * @param	listener:Object a reference to a listener object or function. 
		 * @return 	Nothing.
		*/
		public static function addEventListener(event:String, listener:Object):void
		{
			//var pathList:Array = event.split(".");
			//var flashId:String = pathList[0].toString();
			setup(flashId);
			flashIdList[flashId].registry.addEventListener(event, listener);
			ExternalInterface.call("addAVMListener", __swfID, flashId, event)
		}
		
		/**
		 * Method; unregisters a listener object from a FlashInterface instance that is broadcasting 
		 * an event. 
		 * FlashInterface uses the EventDispatcher provided by the Flash framework. You many look up 
		 * the documentation regarding its usage for any specific details.
		 * <p>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 * 	FlashInterface.removeEventListener("message", messageHandler);
		 * 	</pre>
		 * </p>
		 * @param	event:String a string that is the name of the event.
		 * @param	listener:Object a reference to a listener object or function. 
		 * @return 	Nothing.
		 */
		public static function removeEventListener(event:String, listener:Object):void
		{
			setup(flashId);
			flashIdList[flashId].registry.removeEventListener(event, listener);
			ExternalInterface.call("removeAVMListener", __swfID, flashId, event)
		}

		/**
		 * Method; dispatches an event to any listener registered with an instance of the class. This 
		 * method is usually called from within a component's class file.
		 * FlashInterface uses the EventDispatcher provided by the Flash framework. You many look up 
		 * the documentation regarding its usage for any specific details.
		 * <p>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 * 	FlashInterface.dispatchEvent({type:"message", data:"Hello, world!"});
		 * 	</pre>
		 * </p>
		 * @param	eventObject:Object A reference to an event object. The event object must have a 
		 * type property that is a string indicating the name of the event. Generally, the event object 
		 * also has a target property that is the name of the instance broadcasting the event. You can 
		 * define other properties on the event object that help a user capture information about the 
		 * event when it is dispatched. 
		 * @return 	Nothing.
		 */
		public static function dispatchEvent(eventObject:Object):void
		{
			ExternalInterface.call("dispatchAVMEvent", eventObject);			
		}
						
		/**
		* Method; event handler for dispatched events. This handler will dispatch to all subscribing objects to
		* a particlular event. The event object contains the following data:
		* 	type event dispatched
		* 	data value containing any data to be evaluated by each event handler.
		* @param	flashId:String the id associated to the swf through html.
		* @param	eventObject:Object A reference to an event object.
		* @return 	Nothing.
		*/
		private static function dispatchHandler(flashId:String, eventObject:Object):void
		{
			flashIdList[flashId].registry.dispatchEvent(new FlashInterfaceEvent(eventObject.type, eventObject.data));
		}		
		
		/**
		 * Method; sets a control so it can be accessed through the call method. Public 
		 * properties and methods may be accessed once a control has been registered.
		 * <p>
		 * Two examples are provided below. There is a subtle difference between using publis in 
		 * AS 2.0 and AS3.0. When invoking <code>publish<code>, <code>_root</code> is referenced in AS 2.0, whereas <code>root</code> is referenced in
		 * AS 3.0. For details on using the <code>publish</code> function see documentation.
		 * </p>
		 * <p>
		 *  <i>ActionScript 2.0 Example</i>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 *  FlashInterface.publish(_root, true);
		 * 	FlashInterface.register("demo", myMovieClip);
		 *  
		 * 	myMovieClip.sendMessage = function(message:String):Void
		 * 	{
		 * 		FlashInterface.alert("You said", message);
		 * 	}
		 * </pre>
		 * </p>
		 * <p>
		 *  <i>ActionScript 3.0 Example</i>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 *  FlashInterface.publish(root, true);
		 * 	FlashInterface.register("demo", myMovieClip);
		 *  
		 * 	myMovieClip.sendMessage = function(message:String):Void
		 * 	{
		 * 		FlashInterface.alert("You said", message);
		 * 	}
		 * </pre>
		 * </p> 
 		 * @param	id:String the unique identifier by which an item may be referenced.
		 * @param	target:Object the control object being registered.
		 * @param	overwrite:Boolean overrides a previously registered control wil the same ID.
		 * @return	Boolean returns if the registration was successful.
		 */
		public static function register(id:String, target:Object, overwrite:Boolean=false):Boolean
		{	
			setup(flashId);
			
			if(overwrite)
			{
				flashIdList[flashId].registry[id] = target;
				return true;
			}
				
			if(flashIdList[flashId].registry[id] == null)
			{
				flashIdList[flashId].registry[id] = target;
				return true;	
			}
			
			return false;
		}		
		
		/**
		* Method; removes a control from being accessed through the call method.
		 * <p>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 * 	FlashInterface.unregister("demo", myMovieClip);
		 * 	</pre>
		 * </p>
		 * @param	flashId:String the id associated to the swf through html.
		 * @param	id:String the unique identifier by which an item may be referenced.
		 * @return	Boolean returns if the unregistration was successful.
		 */
		public static function unregister(id:String, target:Object):Boolean
		{
			setup(flashId);
			
			if(flashIdList[flashId].registry[id])
			{
				delete flashIdList[flashId].registry[id];
				return true
			}
			return false;
		}
		
		
		/**
		 * Method; invokes synchronous calls to public methods and properties of registered
		 * controls and receives and returns values.
		 * <p>
		 * The <code>call</code> function is power and easy to use. It allows you to communication and control another
		 * SWFs properties and functions as if they both resided inside the same AVM or scope. 
		 * To communicate with another SWF, you reference it by its assigned id. Ids are used
		 * to reference a swf. Think of it as an instance name. Ids are typically assigned by 
		 * the HTML page as an "id" or "name". However, when loading up a SWF inside another 
		 * SWF, the loaded SWF by default takes on the id of the loader. To ensure that each
		 * item has its own unique id, we can assign it an id using the <code>flashId</code> property assignment.
		 * To assign a <code>flashId</code> do one of the following:<br/><br/>
		 * 1) URL parameter - (i.e. <code>myflashapp.swf?flashId=myFlashId</code>) <font color="#0000FF">[recommended method of implementation]</font><br/>
		 * 2) Flash var - (i.e. SWFObject example - <code>setVariable("flashId", "myFlashId")</code><br/>
		 * 3) Internal - (i.e. <code>_root.flashId = "myFlashId"</code>) - <font color="#FF0000">[not recommended because it hard-codes a <code>flashId</code>
		 *  value inside of the Flash 8 SWF]</font><br/>
		 * </p>
		 * <p>
		 * To talk to another SWF you reference it by its flashId as demonstrated in the example
		 * code below, including children that have been loaded inside a Flash 9 SWF. Lets say a loaded Flash 8
		 * SWF was given the <code>flashId</code> of "flash". We would communicate with with the following code.
		 * </p>
		 * <p>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 * 	FlashInterface.call("flash.sendMessage", "Hello, world!");
		 * </pre>
		 * </p>
		 * <p>
		 * In order for the loaded Flash 8 SWF to communicate with its "parent" Flash 9 SWF, reference the Flash 9 SWF as "root".
		 * </p
		 * <p>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 * 	FlashInterface.call("root.sendMessage", "Hello, world!");
		 * 	</pre>
		 * </p> 
		 * <p>
		 * In the previous examples we demonstrated calling a function. You can also use <code>call</code> to communicate with properties, both
		 * for setting and retrieving data. Below is an example:
		 * </p>
		 * <p>
		 * 	<pre>
		 * 	import flx.events.FlashInterface;
		 * 	var visible:Boolean = FlashInterface.call("flash._visible", false);
		 * 	trace(visible) // returns false
		 * 	var name:Boolean = FlashInterface.call("flash._name");
		 *  trace(name) // returns the instance name of the movieclip.
		 * </pre>
		 * </p>
		 * 
		 * 
		 * @param	path:String path to the method or property.
		 * @param	args...n Any additional parameters you which to pass in the call
		 * @return	Object containing information regarding the event call.
		*/
 		public static function call(path:String, ...args):Object
		{	
			var pathList:Array = path.split(".");
			var flashId:String = pathList.shift().toString();
			var sID:String = flashId; // swfId
			var rID:String = flashId; // flashId
			var ids:Array = getSWFIds();
			if(ids.indexOf(flashId) == -1)
			{
				sID = __swfID;
				rID = flashId;
			}
			
			path = pathList.join(".");
			for(var e:String in args)
				if(args[e] == "") args[e] = "$empty";
			return ExternalInterface.call("getSWF('" + sID + "').callFlash_" + rID, rID, path, args);				
		} 
	
		/**
		* Methods; event handler for call events. Both properties and methods calls are controlled and maintiained
		* with this handler. 
		* @param	flashId
		* @param	path
		* @param	args
		* @return	Object containing information regarding the event call.
		*/
		private static function callHandler(flashId:String, path:String, args:Array):Object
		{	
			var resultObject:Object = new Object();
			resultObject.target = target;
			resultObject.type = type;
			resultObject.status = "error";

			// if we have not published the swf and another item is trying
			// directly to it, then return an error.
			if(!flashIdList[flashId].swf)
			{
				resultObject.message = "SWF has not been made public.";
				return resultObject;
			}		
			
			// path variations are as follows:
			// refID.METHOD
			// refID.path...n.METHOD
			// path...n.METHOD
			// refID.PROPERTY
			// refID.path...n.PROPERTY
			// path...n.PROPERTY
			
			// This functionality resolves if the item was stored in the registry.
			// If it was then we resolve any target paths by using the registered item as a starting point
			// If it wasn't we check to see if the item can be resolved from the root
			// Once we find the control, we resolve the type as a method or property
			// We then carry out the action by passing any arguments to the method or property

			var pathItems:Array = path.split("."); 			
			var target:String = pathItems[0].toString();
			var type:String = pathItems.pop().toString();
			
			var rTarget:Object = flashIdList[flashId].registry[target];
			var len:Number, n:Number;

			if(rTarget == null)
			{
				var targetPath:Object = flashIdList[flashId].swf;//Application.application;
				len = pathItems.length;
				
				for(n=0;n<len;n++)
				{
					try
					{
						targetPath = targetPath[pathItems[n]];
					}
					catch(err:Error)
					{
						alert(err.message);
						resultObject.message = err.message;
						return resultObject;
					}
				}
				
				if(targetPath == null)
				{
					resultObject.message = "Target does not exist";
					return resultObject;
				}
				else
					rTarget = targetPath;
			}
			else
			{
				pathItems.shift();
				len = pathItems.length;
				for(n=0;n<len;n++)
				{
					try
					{
						rTarget = rTarget[pathItems[n]];
					}
					catch(err:Error)
					{
						resultObject.message = err.message;
						return resultObject;
					}
				}
				
				if(rTarget == null)
				{
					resultObject.message = "Target does not exist";
					return resultObject;
				}	
			}
			
			for(var e:String in args)
			{
				if(args[e] == "$empty") args[e] = "";			
			}	
			
			try
			{
				if(typeof(rTarget[type]) == "function")
				{
					resultObject.result = rTarget[type].apply(rTarget, args)
				}	
				else
				{
					if(args.length < 1)
						resultObject.result = rTarget[type];
					else
						resultObject.result = rTarget[type] = args[0]					
				}			
			}
			catch(err:Error)
			{
				resultObject.message = err.message;
				return resultObject;
			}
						
			resultObject.status = "success";
			return resultObject;	
		}	

		/**
		* Method; used for debugging and providing error messages.
		* @param args...n any number of parameters may be passed in. Each parameter will be divided with a space.
		* @usage
		* <pre>
		* import flx.events.FlashInterface;
		* FlashInterface.alert("Hello, world!");
		* </pre>
		*/
		public static function alert(...args):void
		{
			ExternalInterface.call('alert', args.join(" : "));
		}
		
		/**
		* Method; finds a id based on the following conditions:
		* 1) "flashId" is defined
		* 2) finds id from HTML
		* 3) creates an id based on SWF name (should never get here)
		* @return String returns a unique id.
		*/
		private static function getId(control:DisplayObject):String
		{
			if(flashId == null)
			{
				try
				{
					// AS 3.0 only projects
					var params:Object = LoaderInfo(control.root.loaderInfo).parameters;
					flashId = params.flashId;
				}
				catch(err:Error)
				{
					// Flex Framework projects
					//flashId = Application.application.parameters.flashId; // change 12.4.06
					flashId = control["parameters"].flashId;
				}
				if(flashId == null)
					flashId = getSWFId();
				if(flashId == null)
					flashId = getSWFName(control);
			}
			return flashId;
		}
		
		/**
		* Method; retrieves a list of SWFs' id's located in the HTML page
		* @return Array list of swf ids.
		*/
		private static function getSWFIds():Array
		{
			return ExternalInterface.call("getSWFIds") as Array;
		}		
		
		/**
		* Method; returns a uniqueID based on the SWF name. For example, if the SWF is named, "myapp.swf",
		* the return id will be "myapp". This is a simple way of creating an id for getInstance.
		* You can alternatively create your own unique ID by setting "flashId" on the _root. The ID is 
		* how external Flash 8 SWFs or Flash 9 SWFs will communicate with you SWF application.
		* @return String - returns a unique id based on the swf name.
		* 
		* How an ID is established:
		* 1) Attempt to read read "flashId" from SWF - This can be done by assigning it through:
		* 		a) URL - flash.swf?flashId=flash_1
		* 		b) FlashVar - SWFObject example: so.addVariable("flashId", "flash_1"); 
		* 		c) _root - flashId = "flash_1";
		* 3) Attempt to read id from HTML
		* 4) Read swf name as id - (i.e products.swf => products)
		*/
		private static function getSWFName(control:*):String
		{
			var swfItem:String = control.url.split("/").join("|").split("\\").join("|").split("|").pop();
			if(swfItem.indexOf("?") != -1)
				swfItem = swfItem.split("?").shift();
			swfItem = swfItem.split(".swf").join("").split("#").shift();
			return unescape(swfItem);
		}
		
		/**
		* Method; finds the id assigned to the root SWF. 
		* This function developed and provided courtesy of Tyler Wright (codext.com).
		* AS 3.0 implementation by Robert Taylor.
		* @return
		*/
		private static function getSWFId():String
		{
			var swfUID:String = "swf" + (Math.random() * 999999);
			ExternalInterface.addCallback(swfUID, empty);
			var location:Object = ExternalInterface.call("SWFInfo.locateSWF", swfUID);
			
			if(!location)
			{
				location = ExternalInterface.call("eval",
				"(window.SWFInfo = {"+
					"locateSWF:function(swfUID) {"+
						"var swfobjects = document.getElementsByTagName('embed');"+
						"if(!swfobjects.length) swfobjects = document.getElementsByTagName('object');"+
						"for(var i=0; i<swfobjects.length; i++) {"+
							"var name = swfobjects[i].name ? swfobjects[i].name : swfobjects[i].id;"+
							"if(document[name] && document[name][swfUID]) {"+
								"return name;"+
							"}"+
						"}"+
						"return null;"+
					"}"+
				"}).locateSWF('" + swfUID + "');");
			}
			
			var id:String = location ? String(location) : null
			return id;		
		}	
		
		/**
		* Method; Used as a place holder for getSWFId
		* @return
		*/
		private static function empty():void
		{
		}
		
		public static function getFlashId():String
		{
			return flashId;
		}
		

		/**
		 * Method; instantiates the needed JavaScript and EventDispatcher in order to perform any
		 * dispatching and/or calls to other SWFs. This function must be called before any other actions
		 * within FlashInterface or an error will be thrown.
		 * @param root pointer to the Document root of the application. For AS 2.0 this is the <code>_root</code>. For AS 3.0, this is <code>root</code>.
		 * @param makePublic establishes whether the SWF can be accessed by other SWFs either directly or through a registration process. This property
		 * is in place for security. If you wish for other SWFs only to access your SWF through events, then set <code>makePublic</code> to <code>false</code>. If you
		 * wish to expose your public API to other SWFs, set <code>makePublic</code> to <code>true</code>.
		 * @return	Nothing.
		 */
		public static function publish(root:DisplayObject, makePublic:Boolean=false):void
		{
			var flashId:String = getId(root);
			setup(flashId);
			if(makePublic)
				flashIdList[flashId].swf = root;
			else
				delete flashIdList[flashId].swf;
		}	
		
		/**
		 * Method; embeds JavaScript into current HTML.
		 */
		private static function setupEIFunctions():void
		{
			// blocking checking for repeated invoking of this function
			//if(!staticInit)
			{
				staticInit = true;
				
				if(ExternalInterface.call("eval", "$avms"))
					return;
									
				var str:String = "";			
				
				str += "function getSWF(swfId)";
				str += "{";
				str += "	if (navigator.appName.indexOf('Microsoft') != -1)";
				str += "		return window[swfId];";
				str += "	return document[swfId];";
				str += "};";	
				
				str += "function getSWFIds()";
				str += "{";
				str += "	var swfobjects = document.getElementsByTagName('embed');";
				str += "	if(!swfobjects.length) swfobjects = document.getElementsByTagName('object');";
				str += "	var list = new Array();";
				str += "	for(var i=0; i<swfobjects.length; i++)";
				str += "		list.push(swfobjects[i].name ? swfobjects[i].name : swfobjects[i].id);";
				str += "	return list;";
				str += "};";
					
				str += "var $avms = new Object();";
				str += "function addAVMListener(swfId, flashId, event)";
				str += "{";
				str += "	if($avms[event] == null)";
				str += "		$avms[event] = new Object();";
				str += "	if($avms[event][swfId] == null)";
				str += "		$avms[event][swfId] = new Object();";
				str += "	$avms[event][swfId][flashId] = event;";
				//str += "	alert(swfId + ' ; ' +  flashId + ' ; ' +  event);";
				str += "};";
	
				str += "function removeAVMListener(swfId, flashId, event)";
				str += "{";
				str += "	delete $avms[event][swfId][flashId];";
				str += "};";
	
				str += "function dispatchAVMEvent(evt)";
				str += "{";
				str += "	var type = evt.type;";
				str += "	var swfList = $avms[type];";
				str += "	for(var e in swfList)";
				str += "	{";
				str += "		var flashList = swfList[e];";
				str += "		for(var f in flashList)";
				str += "		{";
				//str += "			alert(e + ' : ' + 'dispatchFlash_' + f);";
				str += "			getSWF(e)['dispatchFlash_' + f](f, evt);";
				str += " 		}";
				str += "	}";
				str += "};";		
				
				ExternalInterface.call("eval", str);		
			}		
		}

		/**
		* Method; sets up the ExternalInterface callback handlers for communicating with the SWF.
		* @param	id:String the flash id associated to this SWF.
		* @return 	Boolean indicates if it was successful or not.
		*/
		private static function setup(id:String):Boolean
		{				
			if(id == null)
				return false;

			setupEIFunctions();
						
			if(flashIdList[id] == null)
			{
				flashIdList[id] = new Object();
				flashIdList[id].dispatchHandler = dispatchHandler;
				flashIdList[id].callHandler = callHandler;
				flashIdList[id].registry = new Registry();
					
				ExternalInterface.addCallback("dispatchFlash_" + id, flashIdList[id].dispatchHandler);
				ExternalInterface.addCallback("callFlash_" + id, flashIdList[id].callHandler);		
			}
			
			return true;		
		}	
	}
}