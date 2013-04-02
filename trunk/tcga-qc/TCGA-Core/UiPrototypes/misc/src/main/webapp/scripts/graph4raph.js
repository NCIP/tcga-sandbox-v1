Ext.namespace('tcga.riphraph');

tcga.riphraph.createFilter = function(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor) {
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
						tcga.riphraph.drawNodes(paper, currLoc, vertLoc, squareSize);
						tcga.riphraph.drawPaths(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
					}
					else {
						tcga.riphraph.drawNodes(paper, currLoc, vertLoc, squareSize);
						tcga.riphraph.drawPaths(paper, currLoc, vertLoc, squareSize, 15, '#00f', true);
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

tcga.riphraph.drawNodes = function(paper, currLoc, vertLoc, baseSquareSize) {
	var pathLength = 100;
	
	// Normalize square sizes based on the amount of data.  Total data using Greg's numbers is: 3511
	squareSize = baseSquareSize * (3511/3511);
	tcga.graphwidgets.imageNode(paper, 'images/tss.gif', 'TSS', currLoc, vertLoc, squareSize);

	currLoc += squareSize + pathLength;

	// Into the second node we have 3404 samples
	squareSize = baseSquareSize * (3404/3511);

	var bcr = tcga.graphwidgets.imageNode(paper, 'images/bcr.gif', 'BCR', currLoc, vertLoc, squareSize);

	currLoc += squareSize + pathLength;

	// Into the second node we have 2419 samples
	squareSize = baseSquareSize * (2419/3511);

	var dccNode = tcga.graphwidgets.imageNode(paper, 'images/dcc.gif', 'DCC', currLoc, vertLoc, squareSize);
	tcga.graphwidgets.createMouseOverPopup({
	target: dccNode,
	autoHeight: true,
	width: 173,
	html: '<b>DCC Samples Received: 2391</b><br/>\
			<table style="border: solid 1px grey">\
				<tr><td><b>Cancer</b></td><td><b>Samples</b></td></tr>\
				<tr><td>AML</td><td>197</td></tr>\
				<tr><td>Colon</td><td>432</td></tr>\
				<tr><td>Kidney Clear Cell</td><td>242</td></tr>\
				<tr><td>Lung Ad</td><td>140</td></tr>\
				<tr><td>Lung Sq</td><td>364</td></tr>\
				<tr><td>Rectum</td><td>204</td></tr>\
			</table>'
	});

	dccNode.node.onclick = function() {
		location.href = 'graph3dcc.jsp';
	};
}

tcga.riphraph.drawPaths = function(paper, currLoc, baseVertLoc, baseSquareSize, basePathHeight, pathColor, dummymode) {
	var pathHeight = 0;
	var vertLoc = 0;
	var squareSize = 0;
	var pathLength = 0;

	// Into the second path we have 3404 samples
	pathHeight = basePathHeight * (107/3511);
	squareSize = baseSquareSize * (3511/3511);
	vertLoc = baseVertLoc - squareSize + 8 + pathHeight/2;
	currLoc += squareSize;
	pathLength = 100;

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: -50,
		hLength: pathLength/2,
		color: 'brown',
		width: pathHeight,
		direction: 'N'
	});

	// Into the second path we have 3404 samples
	// First, bump the vertLoc down by the width of the previous path
	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (3404/3511);
	vertLoc += pathHeight/2;
	squareSize = baseSquareSize * (3404/3511);

	var tssBcrPathReceived = tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: pathLength,
		direction: 'E',
		color: pathColor
	});
	tcga.graphwidgets.createMouseOverPopup({
		target: tssBcrPathReceived,
		autoHeight: true,
		width: 173,
		html: '<b>Samples Received: 2391</b><br/>\
				<table style="border: solid 1px grey">\
					<tr><td><b>Cancer</b></td><td><b>Samples</b></td></tr>\
					<tr><td>AML</td><td>197</td></tr>\
					<tr><td>Colon</td><td>432</td></tr>\
					<tr><td>Kidney Clear Cell</td><td>242</td></tr>\
					<tr><td>Lung Ad</td><td>140</td></tr>\
					<tr><td>Lung Sq</td><td>364</td></tr>\
					<tr><td>Rectum</td><td>204</td></tr>\
				</table>'
	});

	currLoc += squareSize + pathLength;

	vertLoc += pathHeight;
	pathHeight = basePathHeight * (951/3511);
	vertLoc = baseVertLoc - squareSize + 5 + pathHeight/2;
	squareSize = baseSquareSize * (3404/3511);

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: -80,
		hLength: pathLength/2,
		color: 'red',
		width: pathHeight,
		direction: 'N'
	});

	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (2419/3511);
	vertLoc += pathHeight/2;

	var bcrDccPath = tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: pathLength,
		direction: 'E',
		color: pathColor
	});
	

	tcga.graphwidgets.createMouseOverPopup({
		id: 'bcrDccPopup',
		target: bcrDccPath,
		height: 150,
		width: 350,
		mouseout: false,
		shadow: false,
		html: '<b>Detailed Delivery List</b><a style="float: right;margin-right: 3px;cursor: pointer;" onclick="Ext.getCmp(\'bcrDccPopup\').destroy();">X</a><div id="bcrDccPathdiv"></div>',
		listeners: {
			show: tcga.graphpopups.raphPopup
		}
	});

	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (34/3511);
	vertLoc += pathHeight/2;

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: 80,
		hLength: pathLength/2,
		color: 'yellow',
		width: pathHeight,
		direction: 'S'
	});

	vertLoc += pathHeight;
	pathHeight = basePathHeight * (597/3511);
	squareSize = baseSquareSize * (2419/3511);
	vertLoc = baseVertLoc - squareSize + 5 + pathHeight/2;

	currLoc += squareSize + pathLength;

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: -80,
		hLength: pathLength/3,
		color: 'red',
		width: pathHeight,
		direction: 'N'
	});

	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (74/3511);
	vertLoc += pathHeight/2;

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: -80,
		hLength: pathLength/3 * 2,
		color: 'orange',
		width: pathHeight,
		direction: 'N'
	});

	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (1367/3511);
	vertLoc += pathHeight/2;

	tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		width: pathHeight,
		type: 'h',
		length: pathLength/3,
		direction: 'E',
		color: pathColor
	});

	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (165/3511);
	vertLoc += pathHeight/2;

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: 80,
		hLength: pathLength/3 * 2,
		color: 'purple',
		width: pathHeight,
		direction: 'S'
	});

	vertLoc += pathHeight/2;
	pathHeight = basePathHeight * (208/3511);
	vertLoc += pathHeight/2;

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc,
		elbowType: 'hv',
		vLength: 80,
		hLength: pathLength/3,
		color: 'yellow',
		width: pathHeight,
		direction: 'S'
	});
}

tcga.riphraph.start = function() {
	var horizOrigin = 1;
	var vertLoc = 300;

	var squareSize = 200;
	var squareCorners = 5;
	var labelOffset = squareSize + 5;
	var squareColor = '#ddd';
	var labelColor = '';

	var pathHeight = 200;
	var pathColor = '#0f0';
	
	var paper = Raphael('raphgraph', 900, 400);
	var raphGraph = Ext.get('raphgraph');
	var paperTop = raphGraph.getTop();
	var paperLeft = raphGraph.getLeft();
	
	var currLoc = horizOrigin;

	tcga.riphraph.createFilter(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
	
	tcga.riphraph.drawNodes(paper, currLoc, vertLoc, squareSize);

	tcga.riphraph.drawPaths(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
}

Ext.onReady(tcga.riphraph.start, this);
