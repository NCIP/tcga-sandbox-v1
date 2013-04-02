Ext.namespace('tcga.graph.popups');

/*
 * Some popup examples.  If we decide to use popups on the graphs, this file will need to be
 * updated.  I think there are some generic features that could be added to make popups
 * consistent and easy to create.  No further documentation until we decide to make use of popups.
 */
tcga.graph.popups.raphPopup = function(popup) {
	var pathMolPathpaper = Raphael('bcrDccPathdiv', 350, 140);

	var currLoc = 30;
	var vertLoc = 120;
	var squareSize = 100;
	var pathLength = 100;
	var pathColor = '#0f0';
	
	var lph = squareSize;
	var totalSamples = 100;

	var cph = lph * (45/totalSamples);
	
	tcga.graph.widgets.drawNode({
		paper: pathMolPathpaper,
		label: 'Pathology QC',
		image: 'images/breast_cancer_slide_narrow.jpeg',
		horizLoc: currLoc,
		vertLoc: vertLoc,
		sizeH: squareSize,
		sizeV: squareSize,
		pathLength: pathLength,
		pathHeight: squareSize,
		numericLabel: 'samples',
		outputs: [{
			label: 'Pathology Failures',
			count: 33,
			connectToNextNode: true,
			color: 'red',
			arrow: false
		}, {
			label: 'Pathology Pass',
			count: 45,
			connectToNextNode: true,
			color: pathColor,
			arrow: false
		}, {
			label: 'Pathology Pending',
			count: 27,
			connectToNextNode: true,
			color: 'yellow',
			arrow: false
		}]
	});
	
	currLoc += squareSize + pathLength;

	tcga.graph.widgets.drawNode({
		paper: pathMolPathpaper,
		label: 'Molecular QC',
		image: 'images/DNA_Science.jpeg',
		horizLoc: currLoc,
		vertLoc: vertLoc,
		sizeH: squareSize,
		sizeV: squareSize,
		numericLabel: 'samples'
	});
}

tcga.graph.popups.tuftePopup = function() {
	jQuery('#awesome-graph').tufteBar({
		data: [
			[197, {label: 'AML'}],
			[432, {label: 'Colon'}],
			[242, {label: 'Kidney'}],
			[140, {label: 'Lung Ad'}],
			[364, {label: 'Lung Sq'}],
			[204, {label: 'Rectum'}]
		],
		barWidth: 0.8,
		barLabel:  function(index) { return this[0] },
		axisLabel: function(index) { return this[1].label },
		color:     function(index) { return ['#E57536', '#82293B'][index % 2] }
	});
/*	
	jQuery('#stacked-graph').tufteBar({
		data: [
			[[1.5, 1.0, 0.51], {label: '2005'}],
			[[2.0, 1.03, 0.6], {label: '2006'}],
			[[2.4, 0.9,  2.0], {label: '2007'}],
			[[1.5, 2.6, 0.45], {label: '2008'}]
		],
		barLabel:  function(index) {
		amount = ($(this[0]).sum() * 10000).toFixed(0);
			return '$' + $.tufteBar.formatNumber(amount);
		},
		axisLabel: function(index) { return this[1].label },
		legend: {
			data: ["North", "East", "West"]
		}
	});
	*/
}
