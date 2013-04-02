/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.dataPortal');

tcga.dataPortal.createChart = function(data) {
	var labels = [
		'Total',
		'Copy\nNumber',
		'Methylation',
		'Gene\nExpression',
		'miRNA\nExpression'/*,
		'Sequence'*/
	];

	var dataIndices = [
		'total',
		'copyNumber',
		'methylation',
		'geneExpression',
		'miRnaExpression'/*,
		'sequence'*/
	];

	// Data - transform for use in the chart
	var dataT = [];
	var dataMax = 0;
	for (var ndx0 = 0;ndx0 < dataIndices.length;ndx0++) {
		dataT[ndx0] = [];
		var dataTotal = 0;
		for (var ndx1 = 0;ndx1 < data.length;ndx1++) {
			dataT[ndx0][ndx1] = data[ndx1].get(dataIndices[ndx0]);
			dataTotal += dataT[ndx0][ndx1];
		}
		if (dataTotal > dataMax) {
			dataMax = dataTotal;
		}
	}

	/* Sizing and scales. */
	var w = 640;
	var h = 310;
	var maxHeightIndex = (Math.ceil(dataMax/100) + 1) * 100;
	var vScale = 240/maxHeightIndex;
	var hScale = 500;

	/* The root panel. */
	var graphPaper = Raphael('cancerDetailsChart', w, h);

	var y = 240;
	/*
	for (var ndx = 0;ndx < maxHeightIndex;ndx += 100) {
		graphPaper.text(80, y, ndx * vScale).attr({
			'text-anchor': 'end',
			'font-size': '12px',
			'font-family': 'Lucida Grande'
		});
		y -= 26;
	}
	*/
	var x = 135;
	for (var ndx = 0;ndx < labels.length;ndx++) {
		graphPaper.text(x, 275, labels[ndx]).attr({
			'font-size': '12px',
			'font-family': 'Lucida Grande'
		});
		x += 81;
	}

	// Create the axes
	//	graphPaper.path('M48 25L48 255L540 255'); - flat axes
	var p = Raphael.fn.shapes.vParGram.createDelegate(graphPaper, [88, 35, 103, 20, 220]);
	p().attr({
		'stroke': 'black',
		'fill': '45-#aaaaaa-#eeeeee'
	});
	var p = Raphael.fn.shapes.hParGram.createDelegate(graphPaper, [88, 255, 103, 240, 412]);
	p().attr({
		'stroke': 'black',
		'fill': '45-#aaaaaa-#eeeeee'
	});

	var x = 105;
	var y = 240;
	var w = 70;
	
	var colors = new tcga.colorUtil.colorCycler({gradient: true});
	var currColor = null;
	for (var ndx = 0; ndx < labels.length; ndx++) {
		var baseline = 0;
		currColor = colors.getNextColor();
		if (dataT[ndx][0] != 0) {
			var dLine = graphPaper.depth.dLine(x, y, x - 10, y + 10, w, dataT[ndx][0] * vScale, currColor, 'bottom');
			baseline = dataT[ndx][0] * vScale;
			new tcga.util.svgHoverWin({
				paper: graphPaper,
				appearNear: dLine,
				text: dataT[ndx][0],
				left: x + 60,
				top: y - dataT[ndx][0]/2 * vScale,
                ie7: {
                    left: 209,
                    top: 110
                }
			});
		}
		currColor = colors.getNextColor();
		if (dataT[ndx][1] != 0) {
			var dLine = graphPaper.depth.dLine(x, y - baseline, x - 10, y + 10 - baseline, w, dataT[ndx][1] * vScale, currColor, 'bottom');
			baseline += dataT[ndx][1] * vScale;
			new tcga.util.svgHoverWin({
				paper: graphPaper,
				appearNear: dLine,
				text: dataT[ndx][1],
				left: x + 60,
				top: y - (dataT[ndx][0] + dataT[ndx][1]/2) * vScale,
                ie7: {
                    left: 209,
                    top: 110
                }
			});
		}
		currColor = colors.getNextColor();
		if (dataT[ndx][2] != 0) {
			var dLine = graphPaper.depth.dLine(x, y - baseline, x - 10, y + 10 - baseline, w, dataT[ndx][2] * vScale, currColor, 'bottom');
			new tcga.util.svgHoverWin({
				paper: graphPaper,
				appearNear: dLine,
				text: dataT[ndx][2],
				left: x + 60,
				top: y - (dataT[ndx][0] + dataT[ndx][1] + dataT[ndx][2]/2) * vScale,
                ie7: {
                    left: 209,
                    top: 110
                }
			});
		}
		x += 81;
		colors.reset();
   }
	
	var left = 60;
	var top = 0;
	var textAttributes = {
		'font-size': '14px',
		'font-family': 'Lucida Grande',
		'text-anchor': 'start',
		'padding-bottom': '25px'
	};

    left -= 15;
	graphPaper.rect(left, top, 10, 10).attr({fill: colors.getNextColor()[3], stroke: 'none'});
	graphPaper.text(left + 15, top + 8, 'Tumor').attr(textAttributes);
	left += 165;
	graphPaper.rect(left, top, 10, 10).attr({fill: colors.getNextColor()[3], stroke: 'none'});
	graphPaper.text(left + 15, top + 8, 'Matched Normal').attr(textAttributes);
	left += 165;
	graphPaper.rect(left, top, 10, 10).attr({fill: colors.getNextColor()[3], stroke: 'none'});
	graphPaper.text(left + 15, top + 8, 'Unmatched Normal').attr(textAttributes);
}
