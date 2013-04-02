Ext.namespace('tcga.protovis');

tcga.protovis.createDemoTwo = function() {
	/*
	 * Get around the need for globals by creating the data with an appropriately namespaced
	 * variable and then assigning it to a local that won't mess up the protovis parsing.
	 */
	var data = tcga.protovis.data;
	
	/* Sizing and scales. */
	var w = 400,
	    h = 400,
	    x = pv.Scale.linear(data, function(d) d.x).range(0, w),
	    y = pv.Scale.linear(0, 4).range(0, h);
	
	/* The root panel. */
	var vis = new pv.Panel()
		 .canvas('protovis_two')
	    .width(w)
	    .height(h)
	    .bottom(20)
	    .left(20)
	    .right(10)
	    .top(5);

	/* Y-axis and ticks. */
	vis.add(pv.Rule)
	    .data(y.ticks(5))
	    .bottom(y)
	    .strokeStyle(function(d) d ? "#eee" : "#000")
	  .anchor("left").add(pv.Label)
	    .text(y.tickFormat);
	
	/* X-axis and ticks. */
	vis.add(pv.Rule)
	    .data(x.ticks())
	    .visible(function(d) d)
	    .left(x)
	    .bottom(-5)
	    .height(5)
	  .anchor("bottom").add(pv.Label)
	    .text(x.tickFormat);
	
	/* The area with top line. */
	vis.add(pv.Area)
	    .data(data)
	    .bottom(1)
	    .left(function(d) x(d.x))
	    .height(function(d) y(d.y))
	    .fillStyle("rgb(121,173,210)")
	  .anchor("top").add(pv.Line)
	    .lineWidth(3);
	
	vis.render();
	
	return vis;
}
