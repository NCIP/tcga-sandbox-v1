/*
 * Add some useful shapes to Raphael
 */
Raphael.fn.shapes = {
	/*
	 * x, y - top left corner
	 * x1, y1 - bottom left corner
	 * w - width
	 */
	hParGram: function(x, y, x1, y1, w) {
		// Start
		var p = drawingUtils.origin(x, y);
		// Left side
		p += drawingUtils.line(x, y, x1, y1, true);
		// Bottom
		p += drawingUtils.line(x1, y1, x1 + w, y1);
		// Right side
		p += drawingUtils.line(x1 + w, y1, x + w, y);
		// Close
		p += 'z';
		
		return this.path(p);
	},
	
	/*
	 * x, y - top left corner
	 * x1, y1 - bottom left corner
	 * h - height
	 */
	vParGram: function(x, y, x1, y1, h) {
		// Start
		var p = drawingUtils.origin(x, y);
		// Top
		p += drawingUtils.line(x, y, x1, y1, true);
		// Left side
		p += drawingUtils.line(x1, y1, x1, y1 + h);
		// Bottom
		p += drawingUtils.line(x, y, x, y + h);
		// Close
		p += 'z';
		
		return this.path(p);
	}
	
	
}
