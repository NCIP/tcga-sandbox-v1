Ext.namespace('tcga.dash.navigator');

tcga.dash.navigator.start = function(activeNode){
	var drawNavNode = function(config) {
		var defaultConfig = {
			text: ';-)',
			active: false,
			fontColor: null,
			fill: null,
			width: null,
			height: null
		};
		var localConfig = Ext.applyIf(config, defaultConfig);
		if (localConfig.active) {
			localConfig.fontColor = 'black';
			localConfig.fill = '#99ff99';
			localConfig.width = 51;
			localConfig.height = 17;
		}
		else {
			localConfig.fontColor = 'white';
			localConfig.fill = '#9999ff';
			localConfig.width = 45;
			localConfig.height = 15;
		}
		
		var e = r.ellipse(localConfig.x, localConfig.y, localConfig.width, localConfig.height).attr({
	   	'fill': localConfig.fill
	   });
		var t = r.text(localConfig.x, localConfig.y, localConfig.text).attr({
	   	'fill': localConfig.fontColor, 'font-weight': 'bold'
		});
		
		if (localConfig.dest) {
			e.attr({cursor: 'pointer'});
			e.click(function() {
				location.href = localConfig.dest;
			});
			t.attr({cursor: 'pointer'});
			t.click(function() {
				location.href = localConfig.dest;
			});
		}
	};
	
	var r = Raphael('dashNavigator', 345, 100);
	
	var x = 175;
	var y = 25;
	var yInt = 50;
	var xInt = 110;
	
	r.path('M' + x + ' ' + y + 'L' + (x - xInt) + ' ' + (y + yInt));
	r.path('M' + x + ' ' + y + 'L' + x + ' ' + (y + yInt));
	r.path('M' + x + ' ' + y + 'L' + (x + xInt) + ' ' + (y + yInt));

	drawNavNode({
		text: 'OverDash',
		active: (activeNode == 'OverDash'?true:false),
		x: x,
		y: y,
		dest: 'overDash.htm'
	});

	drawNavNode({
		text: 'Tissue\nAccrual',
		active: (activeNode == 'Tissue'?true:false),
		x: (x - xInt),
		y: (y + yInt),
		dest: 'overDashTissueAccrual.htm'
	});

	drawNavNode({
		text: 'Disease\nAnalysis WG',
		active: (activeNode == 'DAWG'?true:false),
		x: x,
		y: (y + yInt),
		dest: 'overDashDawg.htm'
	});

	drawNavNode({
		text: 'Center\nSpecific',
		active: (activeNode == 'Center'?true:false),
		x: (x + xInt),
		y: (y + yInt),
		dest: 'overDashCenter.htm'
	});
}
