Ext.namespace('tcga.uuid');

tcga.uuid.navigator = function(){
	var paper = null;

	var draw = function(activeNode){
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
			
			var e = paper.ellipse(localConfig.x, localConfig.y, localConfig.width, localConfig.height).attr({
		   	'fill': localConfig.fill
		   });
			var t = paper.text(localConfig.x, localConfig.y, localConfig.text).attr({
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
		
		if (paper == null) {
			paper = Raphael('uuidLocatorMap', 234, 250);
		}
		
		var x = 117;
		var y = 25;
		var yInt = 50;
		var xInt = 55;
		
		drawNavNode({
			text: 'Participant',
			active: (activeNode == 'Participant'?true:false),
			x: x,
			y: y
		});
		paper.path('M' + x + ' ' + y + 'L' + x + ' ' + (y + yInt)).toBack();
		
		y += yInt;

		drawNavNode({
			text: 'Sample',
			active: (activeNode == 'Sample'?true:false),
			x: x,
			y: y
		});
		paper.path('M' + x + ' ' + y + 'L' + x + ' ' + (y + yInt)).toBack();

		y += yInt;

		drawNavNode({
			text: 'Portion',
			active: (activeNode == 'Portion'?true:false),
			x: x,
			y: y
		});
		paper.path('M' + x + ' ' + y + 'L' + (x - xInt) + ' ' + (y + yInt)).toBack();
		paper.path('M' + x + ' ' + y + 'L' + (x + xInt) + ' ' + (y + yInt)).toBack();

		y += yInt;

		drawNavNode({
			text: 'Analyte',
			active: (activeNode == 'Analyte'?true:false),
			x: x - xInt,
			y: y
		});
		paper.path('M' + (x - xInt) + ' ' + y + 'L' + (x - xInt) + ' ' + (y + yInt)).toBack();

		drawNavNode({
			text: 'Slide',
			active: (activeNode == 'Slide'?true:false),
			x: x + xInt,
			y: y
		});

		y += yInt;

		drawNavNode({
			text: 'Aliquot',
			active: (activeNode == 'Aliquot'?true:false),
			x: x - xInt,
			y: y
		});
	};

	var navigatorPanel = new Ext.Panel({
		border: false,
		hideBorders: true,
		items: [{
			cls: 'stdLabel',
			html: 'UUID Location Map'
		}, {
			html: '<div id="uuidLocatorMap" class="boxcomplete" style="width: 234px;"></div>'
		}]
	});
	
	return {
		render: function() {
			navigatorPanel.render('uuidNavigator');
			draw(null);
		},
		
		setActiveNode: function(activeNode) {
			paper.clear();
			draw(activeNode);
		}
	}
}();
