d=document;w=window;m=Math;
l={};l.gt=function(id){return d.getElementById(id);};l.op=function(ur,nm,pr){w.open(ur,nm,pr||'menubar=0,statusbar=0,width=640,height=480,scrollbars=yes'); return false;};
g={};g.cn=function(ob,cn){l.gt(ob).className=cn;};g.sh=function(obs,obh){g.cn(obs,'visible');if(obh) g.cn(obh,'hidden');};

if (typeof jQuery != 'undefined') { 
		
	$(document).ready(function()
	{
		
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
		function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&laquo; ")).next(".msg_body_open").slideDown(600); },
			 function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&raquo; ")).next(".msg_body_open").slideUp(600); } 
		);
	
		$(".msg_handler_open_slider").toggle( 
			 function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&raquo; ")).next(".msg_body_open").slideUp(600); } ,
		function() { $(this).html($(this).html().replace(/\u00ab\s|\u00bb[\s]?/i, "&laquo; ")).next(".msg_body_open").slideDown(600); }
		);
		$(".msg_handler_open").toggle( 
				function() { $(this).find(".hide").hide() ; $(this).find(".show").show() ; $(this).next(".msg_body_open").hide(); } ,
				function() { $(this).find(".hide").show() ; $(this).find(".show").hide() ; $(this).next(".msg_body_open").show(); }
		);
		
		
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

