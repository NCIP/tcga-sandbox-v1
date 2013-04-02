Ext.namespace('tcga.dataPortal');

tcga.dataPortal.createChart = function() {
	// Data
	var labels = ['Total', 'Copy Number', 'Methylation', 'Gene Expression', 'miRNA Expression', 'Sequence'];
	var n = 3;
	var m = 6;
	var data = [
		[441, 370, 10],
		[438, 377, 0],
		[429, 0, 0],
		[406, 0, 10],
		[354, 0, 10],
		[340, 283, 0]
	];

	/* Sizing and scales. */
	var w = 400;
	var h = 300;
	var x = pv.Scale.ordinal(pv.range(m)).splitBanded(0, w, n/(n+1));
	var y = pv.Scale.linear(0,500).range(0, h);

	/* The root panel. */
	var vis = new pv.Panel()
		 .canvas('cancerDetailsChart')
	    .width(w)
	    .height(h)
	    .bottom(20)
	    .left(20)
	    .right(10)
	    .top(5);

	/* The bars. */
	var bar = vis.add(pv.Panel)
	    .data(data)
	    .left(function() x(this.index))
	    .width(x.range().band)
	  .add(pv.Bar)
	    .data(function(d) d)
	    .left(function() this.index * x.range().band/(n + 1))
	    .height(function(d) y(d))
	    .bottom(0)
	    .width(x.range().band/(n + 1))
	    .fillStyle(pv.Colors.category20().by(pv.index));
	
	/* The value label. */
/*	bar.anchor("top").add(pv.Label)
	    .textStyle("white")
		 .textAngle(-Math.PI / 2)
	    .text(function(d) d.toFixed(1));*/
	
	/* The variable label. */
	bar.parent.anchor("bottom").add(pv.Label)
	   .textAlign("center")
	   .textMargin(5)
	   .text(function() labels[this.parent.index]);
	
	/* Y-axis ticks. */
	vis.add(pv.Rule)
	    .data(y.ticks(5))
	    .top(y)
	    .strokeStyle(function(d) d ? "rgba(255,255,255,.3)" : "#000")
	  .add(pv.Rule)
	    .bottom(0)
	    .width(5)
	    .strokeStyle("#000")
	    .anchor("left").add(pv.Label)
	    .text(y.tickFormat);
	
	vis.render();
}

Ext.onReady(tcga.dataPortal.createChart, this);

