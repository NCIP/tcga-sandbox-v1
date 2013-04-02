	<script type="text/javascript" src="scripts/utilities/util.js"></script> 
	<script type="text/javascript" src="scripts/utilities/textDisplay.js"></script> 
	<script type="text/javascript" src="scripts/utilities/hover.js"></script> 
	<script type="text/javascript" src="scripts/utilities/hoverSVG.js"></script> 
	<script type="text/javascript" src="scripts/utilities/colorUtil.js"></script> 
	<script type="text/javascript" src="scripts/linkConfig.js"></script> 
	<script type="text/javascript" src="scripts/thirdParty/jquery/jquery-1.4.2.js"></script> 
	<script type="text/javascript" src="scripts/thirdParty/jquery/jquery.hoverIntent.js"></script> 
	
	<script type="text/javascript">
		var checkForIe6 = function() {
		    var ua = navigator.userAgent;
		    var re  = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
		    if (re.exec(ua) != null) {
		        var rv = parseFloat( RegExp.$1 );
		        if ( rv < 7) {
		            document.getElementById('browserWarning').style.display = 'block';
		        }
		    }
		}
		 
		var checkForIe7 = function() {
		    var ua = navigator.userAgent;
		    var re  = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
		    if (re.exec(ua) != null) {
		        var rv = parseFloat( RegExp.$1 );
		        if ( rv == 7) {
		            return true;
		        }
		    }
			 
			 return false;
		}
		 
		$(document).ready(function() {
		 	checkForIe6();
		 
		   function show() {
		     var menu = $(this);
			  if (checkForIe7()) {
			  	 var parentCoords = menu.offset();
				 menu.children(".subNavMenu").css("top", parentCoords.top + 25);
				 menu.children(".subNavMenu").css("left", parentCoords.left + 10);
			  }
		     
		     menu.children(".subNavMenu").slideDown();
		   }
		  
		   function hide() { 
		     var menu = $(this);
		     menu.children(".subNavMenu").slideUp();
		   }
		 
		   $(".hoverMenu").hoverIntent({
		     sensitivity: 1, // number = sensitivity threshold (must be 1 or higher)
		     interval: 50,   // number = milliseconds for onMouseOver polling interval
		     over: show,     // function = onMouseOver callback (required)
		     timeout: 300,   // number = milliseconds delay before onMouseOut
		     out: hide       // function = onMouseOut callback (required)
		   });
		});
	</script>

