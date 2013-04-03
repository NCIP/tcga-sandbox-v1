d=document;w=window;m=Math;
l={};l.gt=function(id){return d.getElementById(id);};l.op=function(ur,nm,pr){w.open(ur,nm,pr||'menubar=0,statusbar=0,width=640,height=480,scrollbars=yes'); return false;};
g={};g.cn=function(ob,cn){l.gt(ob).className=cn;};g.sh=function(obs,obh){g.cn(obs,'visible');if(obh) g.cn(obh,'hidden');};




var GALLERY_RENDERER = "flash";
var tmpChart = new FusionCharts("Column2D.swf", "tmpChartId", "560", "400", "0", "0");
var NO_FLASH = tmpChart.options.renderer=="javascript";
tmpFlash = null;
if(NO_FLASH || /GALLERY_RENDERER=javascript/i.test(document.cookie) )
{
	GALLERY_RENDERER = 'javascript';
	
}


if (typeof jQuery != 'undefined') { 
		
	$(document).ready(function()
	{
		$("div.qua-button-holder").html
		(
			'<a id="toggleView" class="qua qua-button view-chart-data jschart" href="javascript:void(0)" style="width:155px;'+(GALLERY_RENDERER.toLowerCase()=='javascript'?'display:none;':'')+'"><span>View JavaScript Chart</span></a>\n\
			<a id="toggleView" class="qua qua-button view-chart-data flashchart" href="javascript:void(0)" style="width:155px;'+(GALLERY_RENDERER.toLowerCase()=='flash'?'display:none;':'')+'"><span>View Flash Chart</span></a>\n\
			<a class="qua qua-button view-chart-data view-xml" href="javascript:void(0)"><span>View XML</span></a>\n\
			<a class="qua qua-button view-chart-data view-json" href="javascript:void(0)"><span>View JSON</span></a>'
		);
		
		$("div.show-code-block").html('<div class="show-code-close-btn"><a class="qua qua-button" href="javascript:void(0)"><span>Close</span></a></div>\n<pre class="prettyprint"></pre>');


		$("a.view-xml").click( function() {
			var chartDATA = '';
			chartDATA = chart.getChartData('xml').replace(/\</gi, "&lt;").replace(/\>/gi, "&gt;");
			showChartData(chartDATA);	
		});

		$("a.view-json").click( function() {
										 
			var chartDATA = '';
			chartDATA = JSON.stringify( chart.getChartData('json') ,null, 2);
			showChartData(chartDATA);	
		});
		
		
		$("a.jschart").click ( function()
			{
				$("a.jschart,a.flashchart").toggle();
				swapRenderer("javascript");
			}
		);
			
		$("a.flashchart").click ( function()
			{
				$("a.jschart,a.flashchart").toggle();
				swapRenderer("flash");
			}
		);

		$('.show-code-close-btn a').click(function() {
			$('.show-code-block').hide();
		});
	

		if(NO_FLASH)
		{
			$("#toggleView").remove();
			$(".qua-button-holder").css({"margin-left":"127px", "width":"250px"});
		}
		
	});

}

$(document).ready(function(){
	if(NO_FLASH) $("#toggleView").hide();
});

function swapRenderer (name) {
	
	if (!(chart instanceof FusionCharts)) 
	{
		return;
	}
	var newChart = chart.clone({ renderer: name , id:"ChartId_"+ name });
	chart.dispose();
	
	document.cookie = "GALLERY_RENDERER="+name.toLowerCase();
	
	newChart.render();
	chart = newChart;

}; 


function showChartData(data)
{
	$('pre.prettyprint').html( data );
	$('.show-code-block').css('height', ($(document).height() - 56) ).show();
	prettyPrint();
	
	
}
