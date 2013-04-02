Ext.namespace('tcga.graph.listeners');

/*
 * Convenience functions for creating listeners.  This could use some enhancements, but does
 * the job for now.  Ideally, this would exploit all of the possibilities for interaction
 * that are provided in SVG and the Raphael library while blending them smoothly with some
 * common convenience functions.  Meanwhile, here's what we have:
 * 
 * hover - not used right now.  This would be the basis for creating popups when mousing over
 * 	elements of the graph.  Will need some enhancements.
 * link - when clicking on an element of the graph, redirect to another url.  Link elements
 * 	are given a pointer cursor type.
 * load - when clicking on an element of the graph, load a new data file, specified in the URL,
 * 	into the current page.  Load elements are given a pointer cursor type.
 * 
 * For a reference on the full set of interactions that might be handy to add a capability for
 * later, go here:
 * 	http://raphaeljs.com/reference.html#events
 * 
 */
tcga.graph.listeners.create = function(target, config) {
	for (var key in config) {
		if (config[key].type == 'hover') {
			tcga.graph.widgets.createMouseOverPopup(Ext.applyIf(config[key], {target: target}));
		}
		else if (config[key].type == 'link') {
			target.node.onclick = function() {
				location.href = config[key].url;
			};
			target.attr({cursor: 'pointer'});
		}
		else if (config[key].type == 'load') {
			target.node.onclick = function() {
				tcga.graph.draw.store.loadUrl(config[key].url);
			};
			target.attr({cursor: 'pointer'});
		}
	}
}
