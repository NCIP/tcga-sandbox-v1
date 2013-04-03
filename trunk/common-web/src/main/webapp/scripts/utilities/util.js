/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.util');

/* From http://www.netlobo.com/url_query_string_javascript.html */
tcga.util.getUrlParam = function(name) {
	name = name.replace(/[\[]/,'\\\[').replace(/[\]]/,'\\\]');
	var regexS = '[\\?&]'+name+'=([^&#]*)';
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );
	
	if( results == null ) {
		return '';
	}
	else {
		results[1] = results[1].replace(/%20/g,' ')
		return results[1];
	}
}

/* 
 * A utility to be used for ensuring that a new DOM object will appear within the frame of
 * the window.
 * 
 * Returns: an object with the top and left coordinates
 * 
 * Config
 * top - a proposed top coordinate for the window, this is relative to the appearNear DOM
 * 	object if one is set
 * left - same as top but for the left coordinate
 * appearNear - a DOM object to appear near
 * minWidth - the minimum desired width that the new object
 */
tcga.util.getAdjustedLeftTop = function(config) {
	var defaultConfig = {
		appearNear: null,
		top: 5,
		left: 5,
		minWidth: 100
	};
	var localConfig = Ext.applyIf(config, defaultConfig);
	
	var top = localConfig.top;
	var left = localConfig.left;
	if (localConfig.appearNear != null) {
		var appearNear = Ext.get(localConfig.appearNear);

		if (appearNear != null) {
			top += appearNear.getTop();
			left += appearNear.getLeft();
		}
	}

	// Check on the 
	// Check to make sure we're not going off the page, if we are, adjust back a bit
	if (left + localConfig.minWidth > window.innerWidth) {
		left = window.innerWidth - localConfig.minWidth;

		// If this is FireFox or IE (now that's something usually not mentioned in the same clause),
		//		we also need to subtract off the scrollbar width
		if (Ext.isGecko || Ext.isIE) {
			left -= 40;
		}
	}
	
	return {left: left, top: top};
}

tcga.util.adjustNarrowMenus = function() {
    var rightColumn = Ext.DomQuery.selectNode('div#sidebar');
    var boxmenus;
    var boxbodies;
    var ndx;
    if (rightColumn && rightColumn.className.indexOf('rollup') > -1) {
        boxmenus = Ext.query('div.boxMenu', rightColumn);
        boxbodies = Ext.query('ul.boxbody', rightColumn);
        for (ndx = 0;ndx < boxmenus.length;ndx++) {
            boxmenus[ndx].className += ' hoverMenu';
            boxbodies[ndx].className += ' subMenuRightGutter';
        }
    }

}

tcga.util.loadNavMenus = function() {

	function show() {
		 var menu = $(this);
		 var menuArrow = document.getElementById('menuarrow');
		 if (checkForIe7()) {
			var parentCoords = menu.offset();
			menu.children(".subNavMenu").css("top", parentCoords.top + 25);
             menu.children(".subNavMenu").css("left", parentCoords.left + 10);
		 }

         if (menu.hasClass('boxMenu')) {
             menu.children('.boxbody').slideDown();
         } else {
             menu.children(".subNavMenu").slideDown();
         }
         if (menuArrow)
         	menuArrow.className="open";
	}

	function hide() {
		 var menu = $(this);
		 var menuArrow = document.getElementById('menuarrow');
         if (menu.hasClass('boxMenu')) {
             menu.children('.boxbody').slideUp();
         } else {
		     menu.children(".subNavMenu").slideUp();
         }
         if (menuArrow)
         	menuArrow.className="";
	}

    tcga.util.adjustNarrowMenus();

	$(".hoverMenu").hoverIntent({
		 sensitivity: 1, // number = sensitivity threshold (must be 1 or higher)
		 interval: 50,   // number = milliseconds for onMouseOver polling interval
		 over: show,     // function = onMouseOver callback (required)
		 timeout: 300,   // number = milliseconds delay before onMouseOut
		 out: hide       // function = onMouseOut callback (required)
	});
}

tcga.util.hasClass = function(el, name) {
   return new RegExp('(\\s|^)'+name+'(\\s|$)').test(el.className);
}

tcga.util.addClass = function(el, name)
{
   if (!tcga.util.hasClass(el, name)) { el.className += (el.className ? ' ' : '') +name; }
}

tcga.util.removeClass = function(el, name)
{
   if (tcga.util.hasClass(el, name)) {
      el.className=el.className.replace(new RegExp('(\\s|^)'+name+'(\\s|$)'),' ').replace(/^\s+|\s+$/g, '');
   }
}

tcga.util.setCookie = function(c_name,value,exdays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
	document.cookie=c_name + "=" + c_value;
}

tcga.util.getCookie = function(c_name)
{
	var i,x,y,ARRcookies=document.cookie.split(";");
	for (i=0;i<ARRcookies.length;i++)
	{
	  x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
	  y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
	  x=x.replace(/^\s+|\s+$/g,"");
	  if (x==c_name){
	    return unescape(y);
	  }
	}
}

tcga.util.validateFindArchivesDates = function (form,start,end) {

	 if(tcga.util.validateDate(start)==false || tcga.util.validateDate(end)==false){
	 	return false;
	 }else{
	 	form.action="findArchives.htm";
	 	form.submit();
	 }
}

tcga.util.validateDate = function (field){
   var checkstr = "0123456789";
   var alertMsg = "Please use date format MM/DD/YYYY.";
   var DateField = field;
   var Datevalue = "";
   var DateTemp = "";
   var seperator = "/";
   var day;
   var month;
   var year;
   var leap = 0;
   var err = 0;
   var i;
   err = 0;
   DateValue = DateField.value;
   
   /*ensuring 2 digit date and 2 digit month */
   var splitBySlash = DateValue.split("/");
   var splitByDot = DateValue.split(".");
   if(splitBySlash.length == 3){
   		if(splitBySlash[0].length<2){
   			splitBySlash[0] = "0" + splitBySlash[0];
   		}else if(splitBySlash[0].length<2){
   			splitBySlash[0] = "xx";
   		}
   		if(splitBySlash[1].length<2){
   			splitBySlash[1] = "0" + splitBySlash[1];
   		}else if(splitBySlash[1].length>2){
   			splitBySlash[1] = "xx";
   		}
   		DateValue = splitBySlash.toString();
   }else if(splitBySlash.length > 3 || splitBySlash.length == 2){
        DateValue = "";
   }
   if(splitByDot.length == 3){
   		if(splitByDot[0].length<2){
   			splitByDot[0] = "0" + splitByDot[0];
   		} else if(splitByDot[0].length>2){
   			splitByDot[0] = "xx";
   		}
   		if(splitByDot[1].length<2){
   			splitByDot[1] = "0" + splitByDot[1];
   		}else if(splitByDot[1].length>2){
   			splitByDot[1] = "xx";
   		}
   		DateValue = splitByDot.toString();
   }else if(splitByDot.length > 3 || splitByDot.length == 2){
        DateValue = "";
   }
   
   /* Delete all chars except 0..9 */
   for (i = 0; i < DateValue.length; i++) {
	  if (checkstr.indexOf(DateValue.substr(i,1)) >= 0) {
	     DateTemp = DateTemp + DateValue.substr(i,1);
	  }
   }
   DateValue = DateTemp;
   
   /*catch empty strings*/
   if(DateValue.replace(/^\s*/, "").replace(/\s*$/, "").length==0){
      DateField.select();
	  DateField.focus();
	  alert(alertMsg);
	  return false;
   }
   
   /* Always change date to 8 digits - string*/
   /* if year is entered as 2-digit / always assume 20xx */
   if (DateValue.length == 6) {
      DateValue = DateValue.substr(0,4) + '20' + DateValue.substr(4,2); }
   if (DateValue.length != 8) {
      err = 19;}
   /* year is wrong if year = 0000 */
   year = DateValue.substr(4,4);
   if (year == 0) {
      err = 20;
   }
   /* Validation of month*/
   month = DateValue.substr(0,2);
   if ((month < 1) || (month > 12)) {
      err = 21;
   }
   /* Validation of day*/
   day = DateValue.substr(2,2);
   if (day < 1) {
     err = 22;
   }
   /* Validation leap-year / february / day */
   if ((year % 4 == 0) || (year % 100 == 0) || (year % 400 == 0)) {
      leap = 1;
   }
   if ((month == 2) && (leap == 1) && (day > 29)) {
      err = 23;
   }
   if ((month == 2) && (leap != 1) && (day > 28)) {
      err = 24;
   }
   /* Validation of other months */
   if ((day > 31) && ((month == "01") || (month == "03") || (month == "05") || (month == "07") || (month == "08") || (month == "10") || (month == "12"))) {
      err = 25;
   }
   if ((day > 30) && ((month == "04") || (month == "06") || (month == "09") || (month == "11"))) {
      err = 26;
   }
   /* if 00 ist entered, no error, deleting the entry */
   if ((day == 0) && (month == 0) && (year == 00)) {
      err = 0; day = ""; month = ""; year = ""; seperator = "";
   }
   /* if no error, write the completed date to Input-Field (e.g. 12/13/2001) */
   if (err == 0) {
      DateField.value = month + seperator + day + seperator + year;
   }
   /* Error-message if err != 0 */
   else {
      alert(alertMsg);
      DateField.select();
	  DateField.focus();
	  return false;
   }
}