Ext.namespace('tcga.riphraph');

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

	tcga.graphwidgets.imageNode(paper, 'images/tss.gif', 'TSS', currLoc, vertLoc, squareSize);

	currLoc += squareSize;

	var tssBcrPathGoal = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2) + ' h 90');
	var tssBcrPathGoalLoc = currLoc;
	tssBcrPathGoal.attr('stroke', '#ccc');
	tssBcrPathGoal.attr('stroke-width', pathHeight * ((6886 - 2391)/6886));
	tssBcrPathGoal.node.onmouseover = function() {
		var aa = new Ext.Panel({
			id: 'aa',
			floating: true,
			height: 50,
			width: 200,
			html: 'Samples Contracted: 2211',
			listeners: {
				show: function(popup) {
					popup.setPosition(paperLeft + tssBcrPathGoalLoc + 25, paperTop + vertLoc + pathHeight + 20);
				}
			}
		});
		aa.render(Ext.getBody());
		aa.show();
	};
	tssBcrPathGoal.node.onmouseout = function() {
		var aa = Ext.getCmp('aa');
		aa.destroy();
	};
	tcga.graphwidgets.arrow({
		paper: paper,
		startX: currLoc + 90,
		startY: (vertLoc + squareSize/2),
		width: pathHeight * ((6886 - 2391)/6886),
		direction: 'E',
		color: '#ccc'
	});

	var tssBcrPathReceived = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 - pathHeight/2) + ' h 90');
	var tssBcrPathReceivedLoc = currLoc;
	tssBcrPathReceived.attr('stroke', pathColor);
	tssBcrPathReceived.attr('stroke-width', pathHeight * (2391/6886));
	tssBcrPathReceived.node.onmouseover = function() {
		var aa = new Ext.Panel({
			id: 'aa',
			floating: true,
			autoHeight: true,
			width: 175,
			html: '<b>Samples Received: 2391</b><br/>\
					<table style="border: solid 1px grey">\
						<tr><td><b>Cancer</b></td><td><b>Samples</b></td></tr>\
						<tr><td>AML</td><td>197</td></tr>\
						<tr><td>Colon</td><td>432</td></tr>\
						<tr><td>Kidney Clear Cell</td><td>242</td></tr>\
						<tr><td>Lung Ad</td><td>140</td></tr>\
						<tr><td>Lung Sq</td><td>364</td></tr>\
						<tr><td>Rectum</td><td>204</td></tr>\
					</table>',
			listeners: {
				show: function(popup) {
					popup.setPosition(paperLeft + tssBcrPathReceivedLoc + 25, paperTop + vertLoc + pathHeight);
				}
			}
		});
		aa.render(Ext.getBody());
		aa.show();
	};
	tssBcrPathReceived.node.onmouseout = function() {
		var aa = Ext.getCmp('aa');
		aa.destroy();
	};
	tcga.graphwidgets.arrow({
		paper: paper,
		startX: currLoc + 90,
		startY: (vertLoc + squareSize/2 - pathHeight/2),
		width: pathHeight * (2391/6886),
		direction: 'E',
		color: pathColor
	});

	currLoc += 100;

	var bcr = tcga.graphwidgets.imageNode(paper, 'images/bcr.gif', 'BCR', currLoc, vertLoc, squareSize);

	currLoc += squareSize;

	var bcrDccPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2) + ' H ' + (currLoc + 200 + 10));
	bcrDccPath.attr('stroke', pathColor)
	bcrDccPath.attr('stroke-width', pathHeight/3);
	
	bcr.toFront();

	var bcrCgccPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 - pathHeight/3) + ' h 50 l 100,-100 h 40');
	bcrCgccPath.attr('stroke', pathColor)
	bcrCgccPath.attr('stroke-width', pathHeight/3);
	var bcrCgccArrowPath = paper.path('M ' + (currLoc + 190) + ' ' + (vertLoc + squareSize/2 - 100 - pathHeight/3 - pathHeight/3/2) + ' l 10,5 -10,5 z');
	bcrCgccArrowPath.attr('fill', pathColor)
	bcrCgccArrowPath.attr('stroke-width', 0);
	
	var bcrGscPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 + pathHeight/3) + ' h 50 l 100,100 h 40');
	bcrGscPath.attr('stroke', pathColor)
	bcrGscPath.attr('stroke-width', pathHeight/3);
	var bcrGscArrowPath = paper.path('M ' + (currLoc + 190) + ' ' + (vertLoc + squareSize/2 + 100 + pathHeight/3 - pathHeight/3/2) + ' l 10,5 -10,5 z');
	bcrGscArrowPath.attr('fill', pathColor)
	bcrGscArrowPath.attr('stroke-width', 0);
	
	currLoc += 200;

	tcga.graphwidgets.imageNode(paper, 'images/dcc.gif', 'DCC', currLoc, vertLoc, squareSize);

	tcga.graphwidgets.imageNode(paper, 'images/cgcc.gif', 'CGCC', currLoc, vertLoc - 110, squareSize);

	tcga.graphwidgets.imageNode(paper, 'images/gsc.gif', 'GSC', currLoc, vertLoc + 110, squareSize);

	currLoc += squareSize;

	var cgccDccPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 - 110) + ' l ' + squareSize + ',0 0,' + (squareSize + (pathHeight/3) * 2) + ' -' + (squareSize - 10) + ',0');
	cgccDccPath.attr('stroke', pathColor)
	cgccDccPath.attr('stroke-width', pathHeight/3);
	var gscDccArrowPath = paper.path('M ' + (currLoc + 10) + ' ' + (vertLoc + squareSize/2 - pathHeight/3/2) + ' l -10,-5 10,-5 z');
	gscDccArrowPath.attr('fill', pathColor)
	gscDccArrowPath.attr('stroke-width', 0);
	
	var gscDccPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 + 110) + ' l ' + squareSize + ',0 0,-' + (squareSize + (pathHeight/3) * 2) + ' -' + (squareSize - 10) + ',0');
	gscDccPath.attr('stroke', pathColor)
	gscDccPath.attr('stroke-width', pathHeight/3);
	var gscDccArrowPath = paper.path('M ' + (currLoc + 10) + ' ' + (vertLoc + squareSize/2 + pathHeight/3 - pathHeight/3/2) + ' l -10,5 10,5 z');
	gscDccArrowPath.attr('fill', pathColor)
	gscDccArrowPath.attr('stroke-width', 0);

	var dccHttpPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2) + ' H ' + (currLoc + 200 - 10));
	dccHttpPath.attr('stroke', pathColor)
	dccHttpPath.attr('stroke-width', pathHeight/3);
	tcga.graphwidgets.arrow({
		paper: paper,
		startX: currLoc + 200 - 10,
		startY: vertLoc + squareSize/2,
		width: pathHeight/3,
		direction: 'E',
		color: pathColor
	});

	currLoc += 200;
	tcga.graphwidgets.imageNode(paper, 'images/ftpdist.gif', 'HTTP Distribution', currLoc, vertLoc + squareSize/4, squareSize, squareSize/2);
}

Ext.onReady(tcga.riphraph.start, this);
