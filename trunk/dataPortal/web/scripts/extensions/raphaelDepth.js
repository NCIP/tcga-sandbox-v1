/*
 * Add some depth to Raphael
 */
Raphael.fn.depth = {
	dLine: function(x, y, x1, y1, w, h, fill, refPoint) {
		if (refPoint && refPoint == 'bottom') {
			// Translate the reference points to the top
			y -= h;
			y1 -= h;
		}
		
		var top = this.shapes.hParGram(x, y, x1, y1, w);
		var left = this.shapes.vParGram(x + w, y, x1 + w, y1, h);
		var face = this.rect(x1, y1, w, h);
		
		if (fill) {
			var topFill = fill;
			var leftFill = fill;
			var faceFill = fill;

			if (typeof fill == 'object') {
				if (typeof fill[0] == 'string') {
					topFill = fill[0];
				}
				else {
					var fillAng = drawingUtils.parGramAngle(x, y, x1, y1, w);
					topFill = fillAng + '-' + fill[0][0] + '-' + fill[0][1];
				}
				if (typeof fill[1] == 'string') {
					faceFill = fill[1];
				}
				else {
					var fillAng = drawingUtils.parGramAngle(x + w, y, x1 + w, y1, h);
					faceFill = '90-' + fill[1][0] + '-' + fill[1][1];
				}
				if (typeof fill[2] == 'string') {
					leftFill = fill[2];
				}
				else {
					leftFill = fillAng + '-' + fill[2][0] + '-' + fill[2][1];
				}
			}

			top.attr({fill: topFill});
			left.attr({fill: leftFill});
			face.attr({fill: faceFill});
		}
		
		return [top, left, face];
	},
	
	// 10, 360, 40, 60, 60, 10
	// x, y, length, width, height, curve
	dElbow: function(x, y, l, w, h, c, fill) {
		var fill1 = drawingUtils.parGramAngle(50, 10, 10, 50, 100) + '-' + '#94b7d2' + '-' + '#e3f2fd'; 
		var fill2 = drawingUtils.parGramAngle(150, 10, 110, 50, 200) + '-' + '#3e4d57' + '-' + '#83a2ba'; 
		var fill3 = '90-#728da2-#d3eafb';
		
		this.path('M70 300L90 280L90 340Q90 350 80 360L65 375Z').attr({fill: fill2});
		this.shapes.hParGram(30, 340, 10, 360, 35).attr({fill: fill2});
		this.path('M10 360L40 360Q50 360 50 350L50 300L70 300L70 360Q70 380 50 380L10 380Z').attr({fill: fill3});
/*		paper.path('M' + x + ' ' + y + 
						'L' + (x + l - c) + ' ' + y + 
						'Q' + (x + l) + ' ' + y + ' ' + (x + l) + ' ' + (y - c) + 
						'L' + (x + l) + ' ' + (y - h) + 
						'L' + (x + h) + ' ' + (y - h) + 
						'L' + (x + h) + ' ' + y + 
						'Q' + (x + h) + ' ' + (y + curve * 2) + ' ' + (x + l) + ' ' + (y + curve * 2) + 
						'L' + x + ' ' + (y + curve * 2) + 'Z').attr({fill: fill3});*/
		this.shapes.hParGram(70, 280, 50, 300, 20).attr({fill: fill1});
	},
	
	dArrow: function() {
		
	}
}
