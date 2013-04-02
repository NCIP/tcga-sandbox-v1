Ext.namespace('tcga.util');

/*
 * Hovering text over SVG
 * 
 * This is just a convenience function over tcga.util.hoverWin to provide the proper offsets
 * so that the hovering window is over the target on the SVG canvas.
 */

tcga.util.svgHoverWin = Ext.extend(tcga.util.hoverWin, {
	constructor: function(config) {
		var defaultConfig = {
			paperDiv: config.paper.canvas.parentNode
		};
		this.localConfig = Ext.applyIf(config, defaultConfig);
		
		tcga.util.svgHoverWin.superclass.constructor.call(this, this.localConfig);

		// Offset the coordinates passed in by the position of the graph
		//		Extra 3 offset on the top to drop the box in below the hover triggering area
		this.localConfig.left += this.localConfig.paperDiv.offsetLeft;
		this.localConfig.top += this.localConfig.paperDiv.offsetTop + 3;
		var aa = 1;
	},
	
	setHover: function(targetObj) {
		targetObj.hover(
			function() {
				this.setLeftTop(this.localConfig.left, this.localConfig.top);

				this.show();
			},
			function() {
				this.hide();
			},
			this, this
		);
	}
});
