/*
 * Add some depth to Raphael
 */
Raphael.fn.depth = {
	dLine: function(config) {
		var defaultConfig = {
			xOff: 10,
			yOff: 20,
			refPoint: 'bottom',
			color: 'blue',
			gradient: true
		};
		var localConfig = Ext.applyIf(config, defaultConfig);

		localConfig.x1 = localConfig.startX + localConfig.xOff;
		localConfig.y1 = localConfig.startY - localConfig.yOff;
		
		if (localConfig.refPoint && localConfig.refPoint == 'bottom') {
			// Translate the reference points to the top
			localConfig.startY -= localConfig.width;
			localConfig.y1 -= localConfig.width;
		}
		
		var topFill = null;
		var leftFill = null;
		var faceFill = null;
		if (localConfig.color && !localConfig.gradient) {
			topFill = localConfig.color;
			faceFill = localConfig.color;
			leftFill = localConfig.color;
		}
		else if (localConfig.gradient == true) {
			var fill = null;
			if (colorGradientTriples[localConfig.color]) {
				fill = colorGradientTriples[localConfig.color]
			}
			else {
				// Future option, create the gradient
			}

			var fillAng = drawingUtils.parGramAngle(localConfig.startX, localConfig.startY, localConfig.x1, localConfig.y1, localConfig.length);
			topFill = fillAng + '-' + fill[0][0] + '-' + fill[0][1];
			var fillAng = drawingUtils.parGramAngle(localConfig.startX + localConfig.length, localConfig.startY, localConfig.x1 + localConfig.length, localConfig.y1, localConfig.width);
			faceFill = fillAng + '-' + fill[1][0] + '-' + fill[1][1];
			leftFill = '90-' + fill[2][0] + '-' + fill[2][1];
		}

		var top = this.shapes.hParGram(localConfig.startX, localConfig.startY, localConfig.x1, localConfig.y1, localConfig.length).attr({fill: topFill});
		var face = this.rect(localConfig.startX, localConfig.startY, localConfig.length, localConfig.width).attr({fill: faceFill});
		var left = this.shapes.vParGram(localConfig.startX + localConfig.length, localConfig.startY, localConfig.x1 + localConfig.length, localConfig.y1, localConfig.width).attr({fill: leftFill});
		
		return face;
	},
	
	dElbow: function(config) {
		var defaultConfig = {
			xOff: 10,
			yOff: 20,
			color: 'blue',
			gradient: true
		};
		var localConfig = Ext.applyIf(config, defaultConfig);
		
		if (!localConfig.curve) {
			localConfig.curve = localConfig.width;
		}
		localConfig.x1 = localConfig.startX + localConfig.xOff;
		localConfig.y1 = localConfig.startY - localConfig.yOff;
		
		var fill1 = null;
		var fill2 = null;
		var fill3 = null;
		if (localConfig.color && !localConfig.gradient) {
			fill1 = localConfig.color;
			fill2 = localConfig.color;
			fill3 = localConfig.color;
		}
		else if (localConfig.gradient == true) {
			var fill = null;
			if (colorGradientTriples[localConfig.color]) {
				fill = colorGradientTriples[localConfig.color]
			}
			else {
				// Future option, create the gradient
			}

			var fillAng = drawingUtils.parGramAngle(localConfig.startX + localConfig.hLength, localConfig.startY - localConfig.vLength, localConfig.x1 + localConfig.hLength, localConfig.y1 - localConfig.vLength, localConfig.width);
			fill1 = fillAng + '-' + fill[0][0] + '-' + fill[0][1];
			var fillAng = drawingUtils.parGramAngle(localConfig.startX, localConfig.startY, localConfig.startX + localConfig.hLength, localConfig.startY + localConfig.vLength, localConfig.vLength);
			fill2 = fillAng + '-' + fill[1][0] + '-' + fill[1][1];
			var fill3 = '90-' + fill[2][0] + '-' + fill[2][1];
		}

		// The elbow points up
		if (localConfig.vLength < 0) {
			this.shapes.hParGram(localConfig.x1, localConfig.y1, localConfig.startX, localConfig.startY, localConfig.hLength).attr({fill: fill2});

			this.path('M' + (localConfig.x1 + localConfig.hLength + localConfig.width) + ' ' + (localConfig.y1 + localConfig.vLength) + 
				'L' + (localConfig.x1 + localConfig.hLength + localConfig.width) + ' ' + localConfig.y1 + 
				'Q' + (localConfig.x1 + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY + localConfig.width) + ' ' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY + localConfig.width) +
				'Q' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + localConfig.startY + ' ' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + localConfig.startY +
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY + localConfig.vLength) + 
				'Z').attr({fill: fill3});
	
			this.path('M' + localConfig.startX + ' ' + localConfig.startY + 
				'L' + (localConfig.startX + localConfig.hLength - localConfig.curve) + ' ' + localConfig.startY + 
				'Q' + (localConfig.startX + localConfig.hLength) + ' ' + localConfig.startY + ' ' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY - localConfig.curve) + 
				'L' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY + localConfig.vLength) + 
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY + localConfig.vLength) + 
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + localConfig.startY + 
				'Q' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY + localConfig.width) + ' ' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY + localConfig.width) + 
				'L' + localConfig.startX + ' ' + (localConfig.startY + localConfig.width) +
				'Z').attr({fill: fill2});

			this.shapes.hParGram(localConfig.startX + localConfig.hLength, localConfig.startY + localConfig.vLength, localConfig.x1 + localConfig.hLength, localConfig.y1 + localConfig.vLength, localConfig.width).attr({fill: fill1});
		}
		// The elbow points down
		else {
			this.path('M' + localConfig.startX + ' ' + (localConfig.startY - localConfig.width) + 
				'L' + localConfig.x1 + ' ' + (localConfig.y1 - localConfig.width) + 
				'L' + (localConfig.x1 + localConfig.hLength) + ' ' + (localConfig.y1 - localConfig.width) + 
				'Q' + (localConfig.x1 + localConfig.hLength + localConfig.width) + ' ' + (localConfig.y1 - localConfig.width) + ' ' + (localConfig.x1 + localConfig.hLength + localConfig.width) + ' ' + (localConfig.y1) +
				'L' + (localConfig.x1 + localConfig.hLength + localConfig.width) + ' ' + (localConfig.y1 + localConfig.vLength) + 
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY + localConfig.vLength) + 
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + localConfig.startY + 
				'Z').attr({fill: fill3});
	
			this.path('M' + localConfig.startX + ' ' + localConfig.startY + 
				'L' + (localConfig.startX + localConfig.hLength - localConfig.curve) + ' ' + localConfig.startY + 
				'Q' + (localConfig.startX + localConfig.hLength) + ' ' + localConfig.startY + ' ' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY + localConfig.curve) + 
				'L' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY + localConfig.vLength) + 
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY + localConfig.vLength) + 
				'L' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + localConfig.startY + 
				'Q' + (localConfig.startX + localConfig.hLength + localConfig.width) + ' ' + (localConfig.startY - localConfig.width) + ' ' + (localConfig.startX + localConfig.hLength) + ' ' + (localConfig.startY - localConfig.width) + 
				'L' + localConfig.startX + ' ' + (localConfig.startY - localConfig.width) +
				'Z').attr({fill: fill2});
		}
	},
	
	dArrow: function(config) {
		var localConfig = {
			depth: 10,
			xOff: 10,
			yOff: 20,
			color: 'blue',
			gradient: true
		};
		Ext.apply(localConfig, config);
		
		localConfig.width = (localConfig.width >= 20?localConfig.width:20);
		var startX = localConfig.startX;
		var startY = localConfig.startY;
		startX -= localConfig.width/2;
		localConfig.x1 = startX + localConfig.xOff;
		localConfig.y1 = startY - localConfig.yOff;

		var topFill = null;
		var faceFill = null;
		var leftFill = null;
		if (localConfig.color && !localConfig.gradient) {
			topFill = localConfig.color;
			faceFill = localConfig.color;
			leftFill = localConfig.color;
		}
		else if (localConfig.gradient == true) {
			var fill = null;
			if (colorGradientTriples[localConfig.color]) {
				fill = colorGradientTriples[localConfig.color]
			}
			else {
				// Future option, create the gradient
			}

			var fillAng = drawingUtils.parGramAngle(startX, startY, localConfig.x1, localConfig.y1, localConfig.width);
			topFill = fillAng + '-' + fill[0][0] + '-' + fill[0][1];
			faceFill = '90-' + fill[1][0] + '-' + fill[1][1];
			var fillAng = drawingUtils.parGramAngle(startX + localConfig.width, startY, localConfig.x1 + localConfig.width, localConfig.y1, localConfig.width);
			leftFill = fillAng + '-' + fill[2][0] + '-' + fill[2][1];
		}

		var arrowPath = '';
		if (localConfig.direction == 'N') {
			arrowPath = localConfig.width/2 + ' -' + localConfig.depth + ' ' + localConfig.width/2 + ' ' + localConfig.depth;
		}
		else if (localConfig.direction == 'S') {
			arrowPath = localConfig.width/2 + ' ' + localConfig.depth + ' ' + localConfig.width/2 + ' -' + localConfig.depth;
		}
		
		var arrow = this.path('M ' + startX + ',' + startY + ' l ' + arrowPath + ' Z').attr({fill: faceFill});
		
		if (localConfig.direction == 'N') {
			this.path('M ' + startX + ' ' + startY +
				'L' + localConfig.x1 + ' ' + localConfig.y1 +
				'L' + (localConfig.x1 + localConfig.width/2) + ' ' + (localConfig.y1 - localConfig.depth) +
				'L' + (startX + localConfig.width/2) + ' ' + (startY - localConfig.depth) +
				'Z').attr({fill: topFill});

			this.path('M ' + (startX + localConfig.width/2) + ' ' + (startY - localConfig.depth) +
				'L' + (localConfig.x1 + localConfig.width/2) + ' ' + (localConfig.y1 - localConfig.depth) +
				'L' + (localConfig.x1 + localConfig.width) + ' ' + (localConfig.y1) +
				'L' + (startX + localConfig.width) + ' ' + (startY) +
				'Z').attr({fill: topFill});
		}
		else if (localConfig.direction == 'S') {
			this.shapes.hParGram(startX, startY, localConfig.x1, localConfig.y1, localConfig.width).attr({fill: topFill});
		}
	},

	dRectangle: function(config) {
		var defaultConfig = {
			xOff: 10,
			yOff: 20,
			color: 'blue',
			corners: 5,
			gradient: true
		};
		var localConfig = Ext.applyIf(config, defaultConfig);
		
		var depthFill = null;
		var rectFill = null;
		if (localConfig.color && !localConfig.gradient) {
			depthFill = localConfig.color;
			rectFill = localConfig.color;
		}
		else if (localConfig.gradient == true) {
			var fill = null;
			if (colorGradientTriples[localConfig.color]) {
				fill = colorGradientTriples[localConfig.color]
			}
			else {
				// Future option, create the gradient
			}

			depthFill = '45-' + fill[0][0] + '-' + fill[0][1];
			rectFill = '45-' + fill[1][0] + '-' + fill[1][1];
		}

		this.path('M' + (localConfig.horizLoc) + ' ' + (localConfig.vertLoc + Math.ceil(localConfig.corners/2)) +
			'L' + (localConfig.horizLoc + localConfig.xOff) + ' ' + (localConfig.vertLoc - localConfig.yOff + Math.floor(localConfig.corners/2)) +
			'Q' + (localConfig.horizLoc + localConfig.xOff + localConfig.corners) + ' ' + (localConfig.vertLoc - localConfig.yOff - localConfig.corners) + ' ' + (localConfig.horizLoc + (localConfig.xOff * 2) + Math.ceil(localConfig.corners)) + ' ' + (localConfig.vertLoc - localConfig.yOff - localConfig.corners) +
			'L' + (localConfig.horizLoc + localConfig.xOff/2 + localConfig.sizeH - localConfig.corners) + ' ' + (localConfig.vertLoc - localConfig.yOff - localConfig.corners) +
			'Q' + (localConfig.horizLoc + localConfig.xOff/2 + localConfig.sizeH + localConfig.corners) + ' ' + (localConfig.vertLoc - localConfig.yOff - localConfig.corners) + ' ' + (localConfig.horizLoc + localConfig.xOff/2 + localConfig.sizeH + localConfig.corners) + ' ' + (localConfig.vertLoc - localConfig.yOff + localConfig.corners) +
			'L' + (localConfig.horizLoc + localConfig.xOff/2 + localConfig.sizeH + localConfig.corners) + ' ' + (localConfig.vertLoc - localConfig.yOff + localConfig.sizeV - localConfig.corners) +
			'Q' + (localConfig.horizLoc + localConfig.xOff + localConfig.sizeH - localConfig.corners) + ' ' + (localConfig.vertLoc - localConfig.yOff + localConfig.sizeV + localConfig.corners) + ' ' + (localConfig.horizLoc + localConfig.sizeH) + ' ' + (localConfig.vertLoc + localConfig.sizeV - Math.ceil(localConfig.corners/2)) +
			'Z').attr({fill: depthFill});
		this.rect(localConfig.horizLoc, localConfig.vertLoc, localConfig.sizeH, localConfig.sizeV, localConfig.corners).attr({fill: rectFill});
	}
}
