function isLocalStorageAvailable()
{
	try
	{
		return ('localStorage' in window==true && window['localStorage'] !== null && typeof localStorage.getItem=='function');
	}
	catch(e)
	{
		return false;
	}
}

var isLocalStore = isLocalStorageAvailable();

	


d=document;w=window;m=Math;
l={};l.gt=function(id){return d.getElementById(id);};l.op=function(ur,nm,pr){w.open(ur,nm,pr||'menubar=0,statusbar=0,width=640,height=480,scrollbars=yes'); return false;};
g={};g.cn=function(ob,cn){l.gt(ob).className=cn;};g.sh=function(obs,obh){g.cn(obs,'visible');if(obh) g.cn(obh,'hidden');};


if (typeof jQuery != 'undefined') { 

	$(document).ready( function()
	{
		// automatic orphan location redirection
		if(navigator.userAgent.search(/MSIE/)>=0)
		{
			if(window.__orphanarticle!==false && top === self)	
			{
			   window.__orphanarticle = location.href.replace(/contents\//i, 'Contents/index.html?');
			}
		}
		else 
		{
			if (window.__orphanarticle!==false && window.parent && (window.parent === window || window.parent.location.domain !== window.location.domain)) 
			{
				window.__orphanarticle = location.href.replace(/contents\//i, 'Contents/index.html?');
			}
		}
			
		

		if (window.__orphanarticle) {
			
			$('body').prepend('<p class="highlightBlock"><strong><a title="Show Navigation Panels" href="'+ 
				window.__orphanarticle +'">You are viewing the standalone version of this article. To view this article along with the Table of Contents click here.</a></strong></p>');
		}
		
		
		//hide the all of the element with class msg_body
		$(".msg_body").hide();
		//toggle the componenet with class msg_body
		$(".msg_handler").toggle( 
				function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&laquo; ")).next(".msg_body").show(); },
				function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&raquo; ")).next(".msg_body").hide(); } 
		);
		
	 // slide in out		
		$(".msg_handler_slider").toggle( 
  		function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&laquo; ")).next(".msg_body").slideDown(600); },
			 function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&raquo; ")).next(".msg_body").slideUp(600); } 
		);
		
		$(".msg_handler_open_slider").toggle( 
			 function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&raquo; ")).next(".msg_body_open").slideUp(600); } ,
	  		 function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&laquo; ")).next(".msg_body_open").slideDown(600); }
		);

		$(".msg_handler_open").toggle( 
			function() { $(this).find(".hide").hide() ; $(this).find(".show").show() ; $(this).next(".msg_body_open").hide(); }, 
			function() { $(this).find(".hide").show() ; $(this).find(".show").hide() ; $(this).next(".msg_body_open").show(); }
		);
		
		
		
		// code for tabs
	   //Default Action
	   $(".tab_content").hide(); //Hide all content
				
				$("ul.tabs").each ( function(){ $(this).children("li:first").addClass("active").show(); } ) ;
				
				$(".tab_container").each ( function(){ $(this).children(".tab_content:first").show(); } ) ;
				
	   //On Click Event
	   $("ul.tabs li").click(function() {
																																			
							$(this).siblings(".active").removeClass("active"); //Remove any "active" class
		   $(this).addClass("active"); //Add "active" class to selected tab
								$(this).parent().siblings(".tab_container").children(".tab_content").hide(); //Hide all tab content
		   var activeTab = $(this).find("a").attr("href"); //Find the rel attribute value to identify the active tab + content
		   $(activeTab).fadeIn(); //Fade in the active content
		   return false;
	   });
		
		

		

		
		
		
	});
	

}

function addFCBreadcrumb(crumbArray)
{
	var html ="";
	html += "<p class=\"breadcrumb\">";
	for(var crumb=0; crumb<crumbArray.length; crumb++ )
	{
		var entryHTML = "";
		splitCrumb = crumbArray[crumb].split("|");
		if(splitCrumb[1]) entryHTML += "<a href=\""+splitCrumb[1]+"\">";
		if(splitCrumb[0]) entryHTML += splitCrumb[0] ;
		entryHTML += (splitCrumb[1] ? "</a> &gt; ": " &gt; ");
		html += entryHTML;
	}

	html = html.replace(/\s&gt\;\s*$/i, "");
	
	html += "</p>";
	return html;
	
}

function addFCFooter(prevLink, nextLink)
{
	var html =[];
	var prevLinkHTML,nextLinkHTML;
	html.push("<table class=\"fcfooter\" width=\"99%\" ><tr>");
	if(prevLink)
	{
		prevLinkDetails = prevLink.split("|");
		prevLinkHTML = "<a href=\"" + prevLinkDetails[1] + "\"> &laquo; " + prevLinkDetails[0] + "</a>";
	}
	if(nextLink)
	{
		nextLinkDetails = nextLink.split("|");
		nextLinkHTML = "<a href=\"" + nextLinkDetails[1] + "\">" + nextLinkDetails[0] + " &raquo; </a>";
	}

	html.push("<td align='left' width='50%'>"+(prevLinkHTML?prevLinkHTML:"")+"</td>");
	html.push("<td align='right' width='50%'>"+(nextLinkHTML?nextLinkHTML:"")+"</td>");

	html.push("</tr></table>");
	
	return html.join("");
}

function showMessage(msgText)
{
	
	if($('#messageBox #content').length>0)
	{
		$('#messageBox #content').append('<span>'+msgText+'</span>');
	}else
	{
		$('#messageBox').html( '<div id="close">&nbsp;</div><div id="content"><span>' + msgText + '</span></div><div class="clear"></div>' );
		$('#messageBox').fadeIn('slow');
		$('#messageBox #close').click( function() { $('#messageBox').remove(); }  );
	}
	
		
}

function showConditionalMessage(msgText, condition)
{
	if(condition) showMessage(msgText);
}
function addConditionalMessage(msgText, condition, isPrepend)
{
	if(condition) addMessage(msgText, isPrepend);
}


function addMessage(msgText, isPrepend)
{
	if($('#messageBox').is(':hidden'))
	{
		showMessage(msgText);
		return;
	}

	if(isPrepend) $('#messageBox #content').prepend('<span>'+msgText+'</span>');
	else  $('#messageBox #content').append('<span>'+msgText+'</span>');
}


function isLocal()
{
	return (location.protocol.search(/https?/)<0 ? "You seem to be running the sample from local file system. " : "");	
}

function isJSRenderer(chartObj)
{
	return chartObj.options.renderer=='javascript' ;
}




// documentation TOC 
if(!window.__doNotPutInCookie)
{
	var contentPagePath = unescape(location.toString()).replace(/^.+?contents\//i,"").replace(/^\s+?|\s+?$|[?].+|\#.+$/g,"");
	document.cookie = "menu="+ contentPagePath;
}

if(!window.__doNotPutInCookie && isLocalStorageAvailable() )
{
	localStorage.removeItem("fc__menu");
	localStorage.setItem("fc__menu", contentPagePath);
}	

