Ext.namespace('tcga.riphraphdcc');

tcga.riphraphdcc.createFilter = function(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor) {
	var diseaseStore = new Ext.data.JsonStore({
		url:'json/diseases.sjson',
		storeId:'diseases',
		root:'diseases',
		idProperty:'tumorId',
		fields: [
			'tumorId',
			'tumorName',
			'tumorDescription'
		],
		autoLoad: true,
		listeners: {
			load: function(store) {
				var diseaseRec = Ext.data.Record.create([
					'tumorId',
					'tumorName',
					'tumorDescription'
				]);
			   var allDiseases = new diseaseRec({
					'tumorId': -1,
					'tumorName': 'All',
					'tumorDescription': 'Show all diseases'
			   });
				store.insert(0, [allDiseases]);
			}
		}
	});

	new Ext.Panel({
		renderTo: 'filterPanel',
		border: false,
		layout: 'column',
		items: [{
			id: 'fieldDisease',
			xtype: 'combo',
			store: diseaseStore,
			triggerAction: 'all',
			displayField:'tumorDescription',
			valueField : 'tumorId',
			emptyText:'Select a disease',
			border: false,
			autoHeight: true,
			width: 270,
			listeners: {
				select: function(combo, rec, ndx) {
					paper.clear();
					if (rec.get('tumorId') == -1) {
						tcga.riphraphdcc.drawNodes(paper, currLoc, vertLoc, squareSize);
						tcga.riphraphdcc.drawPaths(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
					}
					else {
						tcga.riphraphdcc.drawNodes(paper, currLoc, vertLoc, squareSize);
						tcga.riphraphdcc.drawPaths(paper, currLoc, vertLoc, squareSize, 15, '#00f', true);
					}
				}
			}
		}, {
			xtype: 'button',
			text: '<span style="font-family: tahoma;font-weight: bold;">Clear</span>',
			width: 100,
			style: 'padding-left: 10px;',
			handler: function() {
				var diseaseCombo = Ext.getCmp('fieldDisease');
				diseaseCombo.setValue('-1');
				diseaseCombo.fireEvent('select', diseaseCombo, diseaseCombo.getStore().getAt(0), 0);
			}
		}]
	});
}

tcga.riphraphdcc.drawNodes = function(paper, currLoc, vertLoc, squareSize) {
	currLoc += 60;

	tcga.graphwidgets.imageNode(paper, 'images/gear-bevel.jpg', 'Deposit', currLoc, vertLoc - squareSize/2, squareSize);

	currLoc += squareSize + 60;

	tcga.graphwidgets.imageNode(paper, 'images/gear-bevel.jpg', 'Validation', currLoc, vertLoc - squareSize/2, squareSize);

	currLoc += squareSize + 60;

	tcga.graphwidgets.imageNode(paper, 'images/gear-bevel.jpg', 'Storage', currLoc, vertLoc - squareSize/2, squareSize);

	currLoc += squareSize + 60;

	tcga.graphwidgets.imageNode(paper, 'images/ftpdist.gif', 'Distribution', currLoc, vertLoc - squareSize/4, squareSize, squareSize/2);

	currLoc += squareSize + 75;

	tcga.graphwidgets.imageNode(paper, 'images/gear-bevel.jpg', 'GDAC', currLoc, vertLoc - 130, squareSize);

	tcga.graphwidgets.imageNode(paper, 'images/gear-bevel.jpg', 'caArray', currLoc, vertLoc + 90, squareSize);
}

tcga.riphraphdcc.drawPaths = function(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor, dummymode) {
	tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: 60,
		direction: 'E',
		color: pathColor
	});

	currLoc += squareSize + 60;

	tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: 60,
		direction: 'E',
		color: pathColor
	});
	
	currLoc += squareSize + 60;

	tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: 60,
		direction: 'E',
		color: pathColor
	});
	
	currLoc += squareSize + 60;

	tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: 60,
		direction: 'E',
		color: pathColor
	});
	
	currLoc += squareSize + 60;

	tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc - squareSize/2,
		startY: vertLoc - squareSize/4,
		elbowType: 'vh',
		vLength: -110 + squareSize/2,
		hLength: 105,
		color: pathColor,
		width: pathHeight/3
	});
	tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc - squareSize/2,
		startY: vertLoc + squareSize/4 + 20,
		elbowType: 'vh',
		vLength: 110 - 20,
		hLength: 105,
		color: pathColor,
		width: pathHeight/3
	});
}

tcga.riphraphdcc.start = function() {
	var horizOrigin = 1;
	var vertLoc = 150;

	var squareSize = 80;
	var squareCorners = 5;
	var labelOffset = squareSize + 5;
	var squareColor = '#ddd';
	var labelColor = '';

	var pathHeight = 30;
	var pathColor = '#0f0';
	
	var paper = Raphael('raphgraph', 900, 400);
	var raphGraph = Ext.get('raphgraph');
	var paperTop = raphGraph.getTop();
	var paperLeft = raphGraph.getLeft();
	
	var currLoc = horizOrigin;

	tcga.riphraphdcc.createFilter(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
	
	tcga.riphraphdcc.drawNodes(paper, currLoc, vertLoc, squareSize);

	tcga.riphraphdcc.drawPaths(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
}

Ext.onReady(tcga.riphraphdcc.start, this);
