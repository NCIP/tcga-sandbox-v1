/*
 * Add some useful shapes to Raphael
 */
Raphael.fn.shapes = {
	/*
	 * x, y - bottom left corner
	 * x1, y1 - top left corner
	 * w - width
	 */
	hParGram: function(x, y, x1, y1, w) {
		// Start
		var p = drawingUtils.origin(x1, y1);
		// Left side
		p += drawingUtils.line(x1, y1, x, y, true);
		// Bottom
		p += drawingUtils.line(x, y, x + w, y);
		// Right side
		p += drawingUtils.line(x + w, y, x1 + w, y1);
		// Close
		p += 'Z';
		
		return this.path(p);
	},
	
	/*
	 * x, y - bottom left corner
	 * x1, y1 - top left corner
	 * h - height
	 */
	vParGram: function(x, y, x1, y1, h) {
		// Start
		var p = drawingUtils.origin(x1, y1);
		// Top
		p += drawingUtils.line(x1, y1, x, y, true);
		// Left side
		p += drawingUtils.line(x, y, x, y + h);
		// Bottom
		p += drawingUtils.line(x1, y1, x1, y1 + h);
		// Close
		p += 'Z';
		
		return this.path(p);
	}
}
