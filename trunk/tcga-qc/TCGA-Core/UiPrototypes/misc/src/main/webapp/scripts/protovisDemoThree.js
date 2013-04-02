Ext.namespace('tcga.protovis');

tcga.protovis.createDemoThree = function() {
	/*
	 * Get around the need for globals by creating the data with an appropriately namespaced
	 * variable and then assigning it to a local that won't mess up the protovis parsing.
	 */
	var data = tcga.protovis.data2;
	var dataLabels = tcga.protovis.data2labels;
	
	/* Sizing and scales. */
	var w = 400;
	var h = 400;
	var r = w/2;
	var a = pv.Scale.linear(0, pv.sum(data)).range(0, 2*Math.PI);
	var labelNdx = pv.Scale.linear(0, dataLabels.length);

	/* The root panel. */
	var vis = new pv.Panel()
		 .canvas('protovis_three')
	    .width(w)
	    .height(h);

	/* Y-axis and ticks. */
	vis.add(pv.Wedge)
		.data(data)
		.bottom(h/2)
		.left(w/2)
		.outerRadius(r - 10)
		.innerRadius(0)
		.angle(a)
	.anchor("center").add(pv.Label)
		.data(dataLabels)
		.textAngle(0)
		.text(function(d) d);
	
	vis.render();
	
	return vis;
}
