Ext.namespace('tcga.riphraph');

tcga.riphraph.createFilter = function(paper) {
	// Use local data for now so that the same file can be used for local and app server versions
	var diseaseData = {"diseases" : [ 
			{ "tumorDescription" : "Acute Myeloid Leukemia",
	        "tumorId" : 13,
	        "tumorName" : "LAML"
	      },
	      { "tumorDescription" : "Breast invasive carcinoma",
	        "tumorId" : 5,
	        "tumorName" : "BRCA"
	      },
	      { "tumorDescription" : "Colon adenocarcinoma",
	        "tumorId" : 6,
	        "tumorName" : "COAD"
	      },
	      { "tumorDescription" : "Glioblastoma multiforme",
	        "tumorId" : 1,
	        "tumorName" : "GBM"
	      },
	      { "tumorDescription" : "Kidney renal papillary cell carcinoma",
	        "tumorId" : 8,
	        "tumorName" : "KIRP"
	      },
	      { "tumorDescription" : "Lung adenocarcinoma",
	        "tumorId" : 4,
	        "tumorName" : "LUAD"
	      },
	      { "tumorDescription" : "Lung squamous cell carcinoma",
	        "tumorId" : 2,
	        "tumorName" : "LUSC"
	      },
	      { "tumorDescription" : "Ovarian serous cystadenocarcinoma",
	        "tumorId" : 3,
	        "tumorName" : "OV"
	      },
	      { "tumorDescription" : "Rectum adenocarcinoma",
	        "tumorId" : 24,
	        "tumorName" : "READ"
	      },
	      { "tumorDescription" : "Uterine Corpus Endometrioid Carcinoma",
	        "tumorId" : 23,
	        "tumorName" : "UCEC"
	      }
	    ]
	 };
	
	var diseaseStore = new Ext.data.JsonStore({
//		url:'json/diseases.sjson',
		data: diseaseData,
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
			mode: 'local',
			triggerAction: 'all',
			displayField:'tumorDescription',
			valueField : 'tumorId',
			emptyText:'Select a disease',
			border: false,
			autoHeight: true,
			style: 'margin-bottom: 10px;',
			width: 270,
			listeners: {
				select: function(combo, rec, ndx) {
					paper.clear();
					if (rec.get('tumorId') == -1) {
						var counts = {
							scale: null,
							igc: [107, 3404],
							pathology: [951, 2419, 34],
							molecular: [597, 74, 1367, 165, 208],
							totals: [1367, 407]
						};
						
						tcga.riphraph.draw(paper, counts);
					}
					else if (rec.get('tumorId') == 13) {
						var counts = {
							scale: 3511,
							igc: [18, 685],
							pathology: [201, 481, 3],
							molecular: [117, 17, 312, 22, 13],
							totals: [312, 46]
						};
						
						tcga.riphraph.draw(paper, counts);
					}
					else {
						var counts = {
							scale: null,
							igc: [18, 685],
							pathology: [201, 481, 3],
							molecular: [117, 17, 312, 22, 13],
							totals: [312, 46]
						};
						
						tcga.riphraph.draw(paper, counts);
					}
				}
			}
		}, {
			xtype: 'button',
			text: '<span style="font-family: tahoma;font-weight: bold;">Clear</span>',
			width: 100,
			style: 'margin-left: 10px;',
			handler: function() {
				var diseaseCombo = Ext.getCmp('fieldDisease');
				diseaseCombo.setValue('-1');
				diseaseCombo.fireEvent('select', diseaseCombo, diseaseCombo.getStore().getAt(0), 0);
			}
		}]
	});
}

tcga.riphraph.draw = function(paper, counts) {
	var horizOrigin = 1;
	var vertLoc = 300;

	var squareSize = 200;
	var squareCorners = 5;
	var labelOffset = squareSize + 5;
	var squareColor = '#ddd';
	var labelColor = '';

	var pathHeight = 200;
	var pathLength = 100;
	var pathColor = '#0f0';
	
	var raphGraph = Ext.get('raphgraph');
	var paperTop = raphGraph.getTop();
	var paperLeft = raphGraph.getLeft();
	
	var currLoc = horizOrigin;
	
	var totalCounts = function(countSet) {
		var total = 0;
		for (var ndx=0;ndx < countSet.length;ndx++) {
			total += countSet[ndx];
		}
		return total;
	}

	var scale = counts.scale;	
	var igcTotal = totalCounts(counts.igc);

	tcga.graphwidgets.drawNode({
		paper: paper,
		label: 'Received at IGC',
		image: 'images/igc.jpg',
		horizLoc: currLoc,
		vertLoc: vertLoc,
		sizeH: squareSize * 0.75,
		sizeV: squareSize,
		pathLength: pathLength,
		pathHeight: pathHeight * igcTotal/(scale != null?scale:igcTotal),
		numericLabel: 'samples',
		scale: scale,
		listeners: {
			mouseover: {
				type: 'hover',
				autoHeight: true,
				width: 173,
				html: '<b>IGC Samples Received: 2391</b><br/>\
						<table style="border: solid 1px grey">\
							<tr><td><b>Cancer</b></td><td><b>Samples</b></td></tr>\
							<tr><td>AML</td><td>197</td></tr>\
							<tr><td>Colon</td><td>432</td></tr>\
							<tr><td>Kidney Clear Cell</td><td>242</td></tr>\
							<tr><td>Lung Ad</td><td>140</td></tr>\
							<tr><td>Lung Sq</td><td>364</td></tr>\
							<tr><td>Rectum</td><td>204</td></tr>\
						</table>'
			},
			click: {
				type: 'link',
				href: 'graph3dcc.jsp'
			}
		},
		outputs: [{
			label: 'Initial Screen\nFailures',
			count: counts.igc[0],
			color: 'red'
		}, {
			count: counts.igc[1],
			connectToNextNode: true,
			color: pathColor,
			arrow: false
		}]
	});

	currLoc += (squareSize * 0.75) + pathLength;

	tcga.graphwidgets.drawNode({
		paper: paper,
		label: 'Pathology QC',
		image: 'images/breast_cancer_slide_narrow.jpeg',
		horizLoc: currLoc,
		vertLoc: vertLoc,
		sizeH: (squareSize * counts.igc[1]/igcTotal * 0.75),
		sizeV: (squareSize * counts.igc[1]/igcTotal),
		pathLength: pathLength,
		pathHeight: (pathHeight * counts.igc[1]/(scale != null?scale:igcTotal)),
		numericLabel: 'samples',
		scale: scale,
		listeners: {
			mouseover: {
				type: 'hover',
				id: 'tuftePopup',
				height: 270,
				width: 380,
				mouseout: false,
				shadow: false,
				html: '<b>Detailed Delivery List</b><a style="float: right;margin-right: 3px;cursor: pointer;" onclick="Ext.getCmp(\'tuftePopup\').destroy();">X</a><div id="awesome-graph" class="graph" style="margin-top: 20px;width: 370px; height: 200px;"></div>',
				listeners: {
					show: tcga.graphpopups.tuftePopup
				}
			}
		},
		outputs: [{
			label: 'Pathology Failures',
			count: counts.pathology[0],
			color: 'red'
		}, {
			label: 'Pathology Pass',
			count: counts.pathology[1],
			connectToNextNode: true,
			color: pathColor,
			arrow: false,
			listeners: {
				mouseover: {
					type: 'hover',
					id: 'pathMolPopup',
					height: 180,
					width: 350,
					mouseout: false,
					shadow: false,
					html: '<b>Detailed Delivery List</b><a style="float: right;margin-right: 3px;cursor: pointer;" onclick="Ext.getCmp(\'pathMolPopup\').destroy();">X</a><div id="bcrDccPathdiv"></div>',
					listeners: {
						show: tcga.graphpopups.raphPopup
					}
				}
			}
		}, {
			label: 'Pathology Pending',
			count: counts.pathology[2],
			color: 'yellow'
		}]
	});

	var pathologyTotal = totalCounts(counts.pathology);

	currLoc += (squareSize * counts.igc[1]/igcTotal * 0.75) + pathLength;

	tcga.graphwidgets.drawNode({
		paper: paper,
		label: 'Molecular QC',
		image: 'images/DNA_Science.jpeg',
		horizLoc: currLoc,
		vertLoc: vertLoc,
		sizeH: (squareSize * counts.pathology[1]/igcTotal * 0.75),
		sizeV: (squareSize * counts.pathology[1]/igcTotal),
		pathLength: pathLength * 1.5,
		pathHeight: (pathHeight * counts.pathology[1]/(scale != null?scale:igcTotal)),
		numericLabel: 'samples',
		scale: scale,
		outputs: [{
			label: 'Molecular DQ',
			count: counts.molecular[0],
			color: 'red'
		}, {
			label: 'Genotype DQ',
			count: counts.molecular[1],
			color: 'orange'
		}, {
			label: 'Molecular Pass',
			count: counts.molecular[2],
			connectToNextNode: true,
			color: pathColor,
			arrow: false,
			listeners: {
				mouseover: {
					type: 'hover',
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
				}
			}
		}, {
			label: 'Awaiting Shipment',
			count: counts.molecular[3],
			color: 'blue'
		}, {
			label: 'Molecular Pending',
			count: counts.molecular[4],
			color: 'yellow'
		}]
	});

	var molecularTotal = totalCounts(counts.molecular);

	currLoc += (squareSize * counts.pathology[1]/igcTotal * 0.75) + (pathLength * 1.5);

	tcga.graphwidgets.drawNode({
		paper: paper,
		label: 'Shipped',
		image: 'images/box.jpg',
		horizLoc: currLoc,
		vertLoc: vertLoc - 20,
		sizeH: (squareSize * counts.molecular[2]/igcTotal),
		sizeV: (squareSize * counts.molecular[2]/igcTotal)
	});
	
	var formatText = function(text, align) {
		if (!align) {
			var align = 'start';
		}
		text.attr('text-anchor', align);
		text.attr('font-size', '14px');
	};
	
	// The results box
	var results = paper.rect(719, 1, 180, 100)
	results.attr('fill', '0-#ffff00:50-#ffd700');
	paper.path('M719 51L900 51');
	var totalShipped = paper.text(725, 18, 'Total Shipped:');
	formatText(totalShipped);
	var shippedCount = paper.text(895, 18, counts.totals[0]);
	formatText(shippedCount, 'end');
	var pendingShipment = paper.text(725, 38, 'Pending Shipment:');
	formatText(pendingShipment);
	var pendingCount = paper.text(895, 38, counts.totals[1]);
	formatText(pendingCount, 'end');
	var shippedOr = paper.text(725, 65, 'Shipped or');
	formatText(shippedOr);
	var pendingShipment2 = paper.text(735, 85, 'Pending Shipment:');
	formatText(pendingShipment2);
	var pendingCount2 = paper.text(895, 85, counts.totals[0] + counts.totals[1]);
	formatText(pendingCount2, 'end');
}

tcga.riphraph.start = function(counts){
	var counts = {
		scale: null,
		igc: [107, 3404],
		pathology: [951, 2419, 34],
		molecular: [597, 74, 1367, 165, 208],
		totals: [1367, 407]
	};
	
	var paper = Raphael('raphgraph', 900, 450);

	tcga.riphraph.createFilter(paper);
	
	tcga.riphraph.draw(paper, counts);
}

Ext.onReady(tcga.riphraph.start, this);
