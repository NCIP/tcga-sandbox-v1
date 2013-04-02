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
	
	var paper = Raphael('raphgraph', 800, 400);
	var raphGraph = Ext.get('raphgraph');
	var paperTop = raphGraph.getTop();
	var paperLeft = raphGraph.getLeft();
	
	var currLoc = horizOrigin;

	tcga.graphwidgets.imageNode(paper, 'images/tss.gif', 'TSS', currLoc, vertLoc, squareSize);

	currLoc += squareSize;

	var tssBcrPathGoal = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2) + ' H ' + (currLoc + 100));
	var tssBcrPathGoalLoc = currLoc;
	tssBcrPathGoal.attr('stroke', '#eee');
	tssBcrPathGoal.attr('stroke-width', pathHeight * (2211/3000));
	tssBcrPathGoal.node.onmouseover = function() {
		var aa = new Ext.Panel({
			id: 'aa',
			floating: true,
			height: 50,
			width: 200,
			html: 'To be delivered: 2211',
			listeners: {
				show: function(popup) {
					popup.setPosition(paperLeft + tssBcrPathGoalLoc + 25, paperTop + vertLoc + pathHeight);
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

	var tssBcrPathReceived = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 - pathHeight/2) + ' H ' + (currLoc + 100));
	var tssBcrPathReceivedLoc = currLoc;
	tssBcrPathReceived.attr('stroke', pathColor);
	tssBcrPathReceived.attr('stroke-width', pathHeight * (789/3000));
	tssBcrPathReceived.node.onmouseover = function() {
		var aa = new Ext.Panel({
			id: 'aa',
			floating: true,
			height: 50,
			width: 100,
			html: 'Received: 789',
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

	currLoc += 100;

	var bcr = tcga.graphwidgets.imageNode(paper, 'images/bcr.gif', 'BCR', currLoc, vertLoc, squareSize);

	currLoc += squareSize;

	var bcrDccPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2) + ' H ' + (currLoc + 200 + 10));
	bcrDccPath.attr('stroke', pathColor)
	bcrDccPath.attr('stroke-width', pathHeight/3);
	
	bcr.toFront();

	var bcrCgccPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 - pathHeight/3) + ' q 100,0 90,-50 t 100, -50');
	bcrCgccPath.attr('stroke', pathColor)
	bcrCgccPath.attr('stroke-width', pathHeight/3);
	var bcrCgccArrowPath = paper.path('M ' + (currLoc + 190) + ' ' + (vertLoc + squareSize/2 - 100 - pathHeight/3 - pathHeight/3/2) + ' l 10,5 -10,5 z');
	bcrCgccArrowPath.attr('fill', pathColor)
	bcrCgccArrowPath.attr('stroke-width', 0);
	
	var bcrGscPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2 + pathHeight/3) + ' q 100,0 90,50 t 100, 50');
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

	var cgccDccPath = paper.path('M ' + (currLoc) + ' ' + (vertLoc + squareSize/2 - 110) + ' a ' + (squareSize + pathHeight/3) + ',' + (squareSize/2) + ' 0 0,1 ' + pathHeight/3 + ',' + (squareSize + pathHeight/3 + pathHeight/3));
	cgccDccPath.attr('stroke', pathColor)
	cgccDccPath.attr('stroke-width', pathHeight/3);
	var gscDccArrowPath = paper.path('M ' + (currLoc + 10) + ' ' + (vertLoc + squareSize/2 - pathHeight/3/2) + ' l -10,-5 10,-5 z');
	gscDccArrowPath.attr('fill', pathColor)
	gscDccArrowPath.attr('stroke-width', 0);
	
	var gscDccPath = paper.path('M ' + (currLoc + 10) + ' ' + (vertLoc + squareSize/2 + pathHeight/3) + ' a 100,50 0 0,1 0,110');
	gscDccPath.attr('stroke', pathColor)
	gscDccPath.attr('stroke-width', pathHeight/3);
	var gscDccArrowPath = paper.path('M ' + (currLoc + 10) + ' ' + (vertLoc + squareSize/2 + pathHeight/3 - pathHeight/3/2) + ' l -10,5 10,5 z');
	gscDccArrowPath.attr('fill', pathColor)
	gscDccArrowPath.attr('stroke-width', 0);

	var dccFtpPath = paper.path('M ' + currLoc + ' ' + (vertLoc + squareSize/2) + ' H ' + (currLoc + 200 - 10));
	dccFtpPath.attr('stroke', pathColor)
	dccFtpPath.attr('stroke-width', pathHeight/3);
	var dccFtpArrowPath = paper.path('M ' + (currLoc + 200 - 10) + ' ' + (vertLoc + squareSize/2 + pathHeight/3 - pathHeight/3/2) + ' l -10,5 10,5 z');
	dccFtpArrowPath.attr('fill', pathColor)
	dccFtpArrowPath.attr('stroke-width', 0);

	currLoc += 200;
	tcga.graphwidgets.imageNode(paper, 'images/ftpdist.gif', 'FTP Dist', currLoc, vertLoc, squareSize);
}

Ext.onReady(tcga.riphraph.start, this);
