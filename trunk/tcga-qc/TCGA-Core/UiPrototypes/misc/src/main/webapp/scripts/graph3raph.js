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

tcga.riphraph.drawNodes = function(paper, currLoc, vertLoc, squareSize) {
	tcga.graphwidgets.imageNode(paper, 'images/tss.gif', 'TSS', currLoc, vertLoc, squareSize);

	currLoc += squareSize + 150;

	var bcr = tcga.graphwidgets.imageNode(paper, 'images/bcr.gif', 'BCR', currLoc, vertLoc, squareSize);

	currLoc += squareSize + 75;

	tcga.graphwidgets.imageNode(paper, 'images/cgcc.gif', 'CGCC', currLoc, vertLoc - 110, squareSize);

	tcga.graphwidgets.imageNode(paper, 'images/gsc.gif', 'GSC', currLoc, vertLoc + 110, squareSize);

	currLoc += 140;

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

	currLoc += squareSize + 150;

	tcga.graphwidgets.imageNode(paper, 'images/ftpdist.gif', 'HTTP Dist', currLoc, vertLoc + squareSize/4, squareSize, squareSize/2);
}

tcga.riphraph.drawPaths = function(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor, dummymode) {
	currLoc += squareSize;

	if (!dummymode) {
		var tssBcrPathGoal = tcga.graphwidgets.path({
			paper: paper,
			startX: currLoc,
			startY: (vertLoc + squareSize/2),
			width: pathHeight * ((6886 - 2391)/6886),
			type: 'h',
			length: 150,
			direction: 'E',
			color: '#ccc'
		});
		tcga.graphwidgets.createMouseOverPopup({
			target: tssBcrPathGoal,
			height: 50,
			width: 200,
			html: 'Samples still to be delivered: ' + (6886 - 2391)
		});
	}

	var tssBcrPathReceived = tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: (vertLoc + squareSize/2 - (dummymode?0:pathHeight/2)),
		width: pathHeight * (2391/6886),
		type: 'h',
		length: 150,
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
	currLoc += squareSize + 150;

	var bcrDccPath = tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: (vertLoc + squareSize/2),
		width: pathHeight/3,
		type: 'h',
		length: 215,
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

	var bcrCgccPath = tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc - squareSize/2,
		startY: vertLoc,
		elbowType: 'vh',
		vLength: -110 + squareSize/2,
		hLength: 105,
		color: pathColor,
		width: pathHeight/3
	});
	tcga.graphwidgets.createMouseOverPopup({
		id: 'bcrCgccPopup',
		target: bcrCgccPath,
		height: 270,
		width: 380,
		mouseout: false,
		shadow: false,
		html: '<b>Detailed Delivery List</b><a style="float: right;margin-right: 3px;cursor: pointer;" onclick="Ext.getCmp(\'bcrCgccPopup\').destroy();">X</a><div id="awesome-graph" class="graph" style="margin-top: 20px;width: 370px; height: 200px;"></div>',
		listeners: {
			show: tcga.graphpopups.tuftePopup
		}
	});
	tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc - squareSize/2,
		startY: vertLoc + squareSize + 20,
		elbowType: 'vh',
		vLength: 110 - 20 - squareSize/2,
		hLength: 105,
		color: pathColor,
		width: pathHeight/3
	});

	currLoc += squareSize + 75;

	tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc + squareSize/2 - 110,
		elbowType: 'hv',
		vLength: squareSize/2 + 20,
		hLength: 60 + squareSize/2,
		color: pathColor,
		width: pathHeight/3,
		direction: 'S'
	});
	tcga.graphwidgets.elbowPath({
		paper: paper,
		startX: currLoc,
		startY: vertLoc + squareSize/2 + 110,
		elbowType: 'hv',
		vLength: -(squareSize/2),
		hLength: 60 + squareSize/2,
		color: pathColor,
		width: pathHeight/3,
		direction: 'N'
	});

	currLoc += 140;

	tcga.graphwidgets.path({
		paper: paper,
		startX: currLoc,
		startY: vertLoc + squareSize/2,
		width: pathHeight/3,
		type: 'h',
		length: 150,
		direction: 'E',
		color: pathColor
	});
}

tcga.riphraph.start = function() {
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

	tcga.riphraph.createFilter(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
	
	tcga.riphraph.drawNodes(paper, currLoc, vertLoc, squareSize);

	tcga.riphraph.drawPaths(paper, currLoc, vertLoc, squareSize, pathHeight, pathColor);
}

Ext.onReady(tcga.riphraph.start, this);
