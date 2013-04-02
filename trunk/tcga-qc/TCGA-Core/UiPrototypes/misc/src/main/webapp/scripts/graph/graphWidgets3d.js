Ext.namespace('tcga.graph.widgets');

/*
 * Left in for compatibility with older versions of the demo.  No longer used by the current demo.
 */
/*
 * Draw just the node, which does not include the outputs.  Note that this returns the image.
 * There should probably be an option to draw a node without the image, and then return the rect
 * instead of the image, but this has not yet been explored.
 */
tcga.graph.widgets.imageOrTextNode = function(config) {
	if (!config.sizeV) {
		config.sizeV = config.sizeH;
	}
	var imgBg = config.paper.rect(config.horizLoc, config.vertLoc - config.sizeV, config.sizeH, config.sizeV + 20, 5)
	imgBg.attr('fill', '60-#ccc-#eee');
	if (config.image) {
		var node = config.paper.image(config.image, config.horizLoc + 5, config.vertLoc - config.sizeV + 5, config.sizeH - 10, config.sizeV - 10);
	}
	else {
		node = imgBg;
	}
	
	var nodeLabel = config.paper.text(config.horizLoc + config.sizeH/2, config.vertLoc + 7, config.label);
	nodeLabel.attr('font-size', '16px');
	
	return node;
}

/*
 * Draw a straight path 
 * 
 * Configuration:
 * 	paper - required
 * 	startX - required, the coordinates of the start location to put into an M SVG command
 * 	startY - required, the coordinates of the start location to put into an M SVG command
 * 	width - the width of the path
 * 	type - h, v, l - the type of line, use direction with H and V lines, use path for L lines
 * 	length - for use with H and V lines, gives the length of the path
 * 	path - the path to use for line
 * 	direction - N, S, E, W - the direction the path is pointing
 * 	arrow - boolean, draw an arrow at the end of the line, defaults to true
 * 	color - the color of the arrow
 */
tcga.graph.widgets.path = function(config) {
	var defaultConfig = {
		type: 'h',
		direction: 'E',
		color: 'green',
		arrow: true
	};
	var localConfig = Ext.applyIf(config, defaultConfig);

	var arrowDepth = (localConfig.arrow?10:0);

	var path = localConfig.paper.path('M ' + (localConfig.startX + 1) + ' ' + localConfig.startY + ' ' + localConfig.type + (localConfig.length - 1 - arrowDepth));
	path.attr('stroke', localConfig.color)
	path.attr('stroke-width', localConfig.width);

	if (localConfig.arrow) {
		var arrowConfig = {};
		Ext.apply(arrowConfig, localConfig);
   	arrowConfig.startX = localConfig.startX + localConfig.length - arrowDepth;
   	tcga.graph.widgets.arrow(arrowConfig);
   }
	
	var label = '';
	if (localConfig.label) {
		label += localConfig.label + ':\n';
	}
	label += localConfig.count + ' ';
	if (localConfig.numericLabel) {
		label += localConfig.numericLabel;
	}
	
	var pathLabel = localConfig.paper.text(localConfig.startX + (localConfig.length/2), localConfig.startY, label);
	pathLabel.attr('font-size', '12px');

	if (localConfig.listeners) {
		tcga.graph.listeners.create(path, localConfig.listeners);
		
		if (pathLabel != null) {
			tcga.graph.listeners.create(pathLabel, localConfig.listeners);
		}
	}

	return path;
}

tcga.graph.widgets.dPath = function(config) {
	var defaultConfig = {
		type: 'h',
		direction: 'E',
		color: 'green',
		arrow: true
	};
	var localConfig = Ext.applyIf(config, defaultConfig);
	
	// Backward compatibility
	if (localConfig.color == '#0f0') {
		localConfig.color = 'green';
	}
	
	localConfig.startY += localConfig.width/2;

	var path = localConfig.paper.depth.dLine(localConfig);
/*
	if (localConfig.arrow) {
		var arrowConfig = {};
		Ext.apply(arrowConfig, localConfig);
   	arrowConfig.startX = localConfig.startX + localConfig.length - arrowDepth;
   	tcga.graph.widgets.arrow(arrowConfig);
   }
*/
	
	var label = '';
	if (localConfig.label) {
		label += localConfig.label + ':\n';
	}
	label += localConfig.count + ' ';
	if (localConfig.numericLabel) {
		label += localConfig.numericLabel;
	}
	
	var pathLabel = localConfig.paper.text(localConfig.startX + (localConfig.length/2), localConfig.startY + (localConfig.width/2), label);
	pathLabel.attr('font-size', '12px');

	if (localConfig.listeners) {
		tcga.graph.listeners.create(path, localConfig.listeners);
		
		if (pathLabel != null) {
			tcga.graph.listeners.create(pathLabel, localConfig.listeners);
		}
	}

	return path;
}

/*
 * Draw a path with an elbow in it.  The elbow can bend up or down.
 * 
 * Configuration:
 * elbowtypes - 'hv', 'vh' - that is go horizontal then vertical, or vice versa
 * direction - the direction in which the end of the path is pointing
 * lineJoin - the type of join for the bend in the line [miter, round, bevel] - see
 * 	http://www.w3.org/TR/SVG/painting.html#StrokeLinejoinProperty
 * arrow - boolean indicating whether or not to include an arrow at the end of the path
 */
tcga.graph.widgets.elbowPath = function(config) {
	var defaultConfig = {
		elbowType: 'hv',
		direction: 'E',
		lineJoin: 'round',
		arrow: true
	};
	var localConfig = Ext.applyIf(config, defaultConfig);

	var path = '';
	var labelOffsetV = 30 + localConfig.labelOffsetV;
	if (localConfig.elbowType == 'hv') {
		path += ' h ' + (localConfig.hLength) + ' v ' + localConfig.vLength;
		if (localConfig.vLength > 0) {
			localConfig.direction = 'S';
		}
		else {
			localConfig.direction = 'N';
			labelOffsetV = labelOffsetV * -1;
		}
	}
	else {
		path += ' v ' + localConfig.vLength + ' h ' + (localConfig.hLength);
		if (localConfig.hLength > 0) {
			localConfig.direction = 'E';
		}
		else {
			localConfig.direction = 'W';
		}
	}
	var path = localConfig.paper.path('M ' + (localConfig.startX) + ' ' + localConfig.startY + path);
	path.attr('stroke', localConfig.color);
	path.attr('stroke-width', localConfig.width);
	path.attr('stroke-linejoin', localConfig.lineJoin);

	if (localConfig.arrow) {
		var arrowConfig = {};
		Ext.apply(arrowConfig, localConfig);
		arrowConfig.startX = localConfig.startX + localConfig.hLength;
		arrowConfig.startY = localConfig.startY + localConfig.vLength;
		tcga.graph.widgets.arrow(arrowConfig);
	}
	
	var label = '';
	if (localConfig.label) {
		label += localConfig.label + ':\n';
	}
	label += localConfig.count + ' ';
	if (localConfig.numericLabel) {
		label += localConfig.numericLabel;
	}

	var pathLabel = localConfig.paper.text(localConfig.startX + localConfig.hLength + localConfig.labelOffsetH, localConfig.startY + localConfig.vLength + labelOffsetV, label);
	pathLabel.attr('font-size', '12px');
	
	if (localConfig.listeners) {
		tcga.graph.listeners.create(path, localConfig.listeners);
		
		if (pathLabel) {
			tcga.graph.listeners.create(pathLabel, localConfig.listeners);
		}
	}

	return path;
}

tcga.graph.widgets.dElbowPath = function(config) {
	var defaultConfig = {
		elbowType: 'hv',
		direction: 'E',
		lineJoin: 'round',
		arrow: true
	};
	var localConfig = Ext.applyIf(config, defaultConfig);

	localConfig.startY += localConfig.width/2;
	
	if (!localConfig.color) {
		localConfig.color = drawingUtils.getNextColor();
	}

	var path = '';
	var labelOffsetV = 30 + localConfig.labelOffsetV;
	if (localConfig.elbowType == 'hv') {
		path += ' h ' + (localConfig.hLength) + ' v ' + localConfig.vLength;
		if (localConfig.vLength > 0) {
			localConfig.direction = 'S';
		}
		else {
			localConfig.direction = 'N';
			labelOffsetV = labelOffsetV * -1;
		}
	}
	else {
		path += ' v ' + localConfig.vLength + ' h ' + (localConfig.hLength);
		if (localConfig.hLength > 0) {
			localConfig.direction = 'E';
		}
		else {
			localConfig.direction = 'W';
		}
	}

	if (localConfig.direction == 'N') {
		var path = localConfig.paper.depth.dElbow(localConfig);
	}

	if (localConfig.arrow) {
		var arrowConfig = {};
		Ext.apply(arrowConfig, localConfig);
		arrowConfig.startX = localConfig.startX + localConfig.hLength + localConfig.width/2;
		arrowConfig.startY = localConfig.startY + localConfig.vLength;
		
		localConfig.paper.depth.dArrow(arrowConfig);
	}

	if (localConfig.direction == 'S') {
		var path = localConfig.paper.depth.dElbow(localConfig);
	}
	
	var label = '';
	if (localConfig.label) {
		label += localConfig.label + ':\n';
	}
	label += localConfig.count + ' ';
	if (localConfig.numericLabel) {
		label += localConfig.numericLabel;
	}

	var pathLabel = localConfig.paper.text(localConfig.startX + localConfig.hLength + localConfig.labelOffsetH, localConfig.startY + localConfig.vLength + labelOffsetV, label);
	pathLabel.attr('font-size', '12px');
	
	if (localConfig.listeners) {
		tcga.graph.listeners.create(path, localConfig.listeners);
		
		if (pathLabel) {
			tcga.graph.listeners.create(pathLabel, localConfig.listeners);
		}
	}

	return path;
}

/*
 * Draw an arrow at the end of an SVG line
 * Configuration:
 * 	paper - required
 * 	startX - required, the coordinates of the start location to put into an M SVG command, the bottom coordinate
 * 	startY - required, the coordinates of the start location to put into an M SVG command, the bottom coordinate
 * 	width - the width of the path requiring the arrow
 * 	direction - N, S, E, W - the direction the arrow is pointing
 * 	color - the color of the arrow
 * 	depth - defaults to 10px
 */
tcga.graph.widgets.arrow = function(config) {
	var localConfig = {
		depth: 10
	};
	Ext.apply(localConfig, config);
	
	localConfig.width = (localConfig.width/2 >= 10?localConfig.width/2:10);

	var arrowPath = '';
	if (localConfig.direction == 'E') {
		arrowPath = localConfig.depth + ',' + localConfig.width + ' -' + localConfig.depth + ',' + localConfig.width;
	}
	else if (localConfig.direction == 'W') {
		arrowPath = '-' + localConfig.depth + ',' + localConfig.width + ' ' + localConfig.depth + ',' + localConfig.width;
	}
	else if (localConfig.direction == 'N') {
		arrowPath = localConfig.width + ',-' + localConfig.depth + ' ' + localConfig.width + ',' + localConfig.depth;
	}
	else if (localConfig.direction == 'S') {
		arrowPath = localConfig.width + ',' + localConfig.depth + ' ' + localConfig.width + ',-' + localConfig.depth;
	}
	
	var startX = localConfig.startX;
	var startY = localConfig.startY;
	if (localConfig.direction == 'E' || localConfig.direction == 'W') {
		startY -= localConfig.width;
	}
	else {
		startX -= localConfig.width;
	}
	var arrow = localConfig.paper.path('M ' + startX + ',' + startY + ' l ' + arrowPath + ' z');
	if (localConfig.color) {
		arrow.attr('fill', localConfig.color)
	}
	arrow.attr('stroke', localConfig.color)
	arrow.attr('stroke-width', 0);
}

/*
 * A function to make it simpler to create a popup when mousing over an element in the
 * graph.  This should be updated once we decide whether or not we're going to be using
 * popups.  I'll document it once updated since I don't know how much the update, if it
 * happens, will change the function.
 */
tcga.graph.widgets.createMouseOverPopup = function(config) {
	var localConfig = {
		id: Ext.id(),
		floating: true,
		height: null,
		autoHeight: false,
		width: 100,
		mouseout: true
	};
	Ext.apply(localConfig, config);

	localConfig.target.node.onmouseover = function(e) {
		if (Ext.getCmp(localConfig.id) != null) {
			return;
		}
		
		e = window.event || e;
	
		var popupPanel = new Ext.Panel(localConfig);
		
		popupPanel.addListener('show', function(popup) {
			var winHeight = document.documentElement.clientHeight;
			var winWidth = document.documentElement.clientWidth;
			
			var popupXPos = e.clientX + 10;
			var popupYPos = e.clientY + 5;
			if ((popupXPos + popup.getWidth()) > winWidth) {
				popupXPos -= (popupXPos + popup.getWidth()) - winWidth;
			}
			if ((popupYPos + popup.getHeight()) > winHeight) {
				popupYPos -= (popupYPos + popup.getHeight()) - winHeight;
			}
			popup.setPosition(popupXPos, popupYPos);
		});
		popupPanel.render(Ext.getBody());
		popupPanel.show();
	};
	
	if (localConfig.mouseout) {
		localConfig.target.node.onmouseout = function() {
			var popupPanel = Ext.getCmp(localConfig.id);
			if (popupPanel != null) {
				popupPanel.destroy();
			}
		};
	}
}

/*
 * Walk through a set of outputs and return the index of the path that has connectToNextNode set.
 */
tcga.graph.widgets.getConnectingPathNdx = function(outputs) {
	for (var ndx = 0; ndx < outputs.length; ndx++) {
		if (outputs[ndx].connectToNextNode) {
			return ndx;
		}
   }

	// There is no connecting path	
	return -1;
}

/*
 * Walk through a vector of objects and sum the count fields, or alternately, sum a field
 * name that is passed in.  Return the sum.
 */
tcga.graph.widgets.totalCount = function(countSet, field) {
	if (!field) {
		field = 'count';
	}

	var total = 0;
	for (var ndx=0;ndx < countSet.length;ndx++) {
		total += countSet[ndx][field];
	}
	return total;
}

/*
 * Draw a node in the graph.
 * 
 * This function draws the box around the node, places the image in the node and adds the label
 * below the image.
 * 
 * Then the function cycles through the outputs from the node and calls the appropriate path
 * function to draw each one.
 */
tcga.graph.widgets.drawNode = function(config) {
	// Add the image to the node
	var node = tcga.graph.widgets.imageOrTextNode(config);
	
	/*
	 * If there are listeners on the node itself, pass those to the listener.create function.
	 * 
	 * Note that this is only adding the listeners to the image.  This is an area that should
	 * be improved to cover the rest of the node box.
	 */ 
	if (config.listeners) {
		tcga.graph.listeners.create(node, config.listeners);
	}
	
	// If there are no outputs configured, we're done!
	if (!config.outputs) {
		// There is no connecting path, so we return null, since there will be no following
		//		node to use a new y value.
		return null;
	}
	
	/*
	 * Now draw the outputs.  First, collect some information.
	 * 
	 * totalCount - the sum of the counts in the outputs
	 * connectingPathNdx - the index of the output that connects to the next node
	 * upCount - the number of outputs that point up
	 * downCount - the number of outputs that point down
	 */
	var totalCount = tcga.graph.widgets.totalCount(config.outputs);
	var connectingPathNdx = tcga.graph.widgets.getConnectingPathNdx(config.outputs);
	var upCount = connectingPathNdx;
	var downCount = config.outputs.length - connectingPathNdx;
	var upTotal = tcga.graph.widgets.totalCount(config.outputs.slice(0,connectingPathNdx - 1));
	
	/*
	 * Calculate the amount of vertical space to use for drawing the outputs.
	 * 
	 * vOffset - the number of pixels to offset from the bottom of the node, this is an
	 * aesthetic adjustment only.
	 */
	var vOffset = 8;
	if (config.scale != null){
		var totalPathHeight = config.pathHeight * (totalCount/(config.scale != null?config.scale:totalCount));
		vOffset += (config.sizeV - totalPathHeight)/2;
	}
	
	/*
	 * Set up a bunch of starting parameters about the location to start drawing from.
	 * 
	 * pathHeight - stores the current output height.
	 * pathDir - the direction of the current output, 'up' or 'down', not used for the output
	 * 	that connects to the next node.
	 * vertLoc - the current Y location at which to start drawing the output.  Note that this is the
	 * 	middle of the path and the width of the path goes out evenly on either side of the vertLoc.
	 * outputLoc - the current X location at which to start drawing the output.
	 * lengthDivider - used to figure out how far along to bend those elbows so that they are more
	 * 	or less evenly spaced.
	 * nextNodeCenter - the y coordinate of the center of the connecting path, this is returned and
	 * 	and used by the next node for positioning if the "center" option is true
	 */
	var pathHeight = 0;
	var pathDir = 'down';
	var vertLoc = config.vertLoc - config.sizeV + vOffset + config.pathHeight;
	var outputLoc = config.horizLoc + config.sizeH;
	var lengthDivider = config.outputs.length - downCount - 1;
	var nextNodeCenter = config.pathHeight;
	var verticalOffset = 15;
	var horizontalOffset = 40;
	var initialVerticalLength = 80;
	var length = config.pathLength;
	if (connectingPathNdx > 1) {
		var upLength = (upCount * horizontalOffset) + config.pathHeight * (upTotal/totalCount) - 
			(config.pathHeight * (config.outputs[0].count/totalCount))/2;
	}
	else if (connectingPathNdx == 1) {
		var upLength = config.pathLength/2;
	}
	var downLength = length/downCount;
	var upNdx = upCount;
	var downNdx = 0;
	for (var ndx = config.outputs.length - 1;ndx >= 0;ndx--) {
		var pathConfig = config.outputs[ndx];
		
		pathHeight = config.pathHeight * (pathConfig.count/totalCount);
		vertLoc -= pathHeight/2;
		
		// Set up the config for the path using our current location calculations.
		var localConfig = {};
		Ext.apply(localConfig, pathConfig);
		Ext.apply(localConfig, {
			paper: config.paper,
			startX: outputLoc,
			startY: vertLoc,
			width: pathHeight,
			numericLabel: config.numericLabel
		});
		
		// Draw a path that connects to the next node, which is always a straight path.
		if (pathConfig.connectToNextNode) {
			if (config.threeD) {
				tcga.graph.widgets.dPath(Ext.applyIf(localConfig, {
						length: length
					})
				);
			}
			else {
				tcga.graph.widgets.path(Ext.applyIf(localConfig, {
						length: length
					})
				);
			}
			
			pathDir = 'up';
			lengthDivider = upCount + 1;
			
			nextNodeCenter = vertLoc;
		}
		// Draw an elbow path pointing up
		else if (pathDir == 'up') {
			lengthDivider--;
			if (config.threeD) {
				localConfig.startY -= pathHeight;
				tcga.graph.widgets.dElbowPath(Ext.applyIf(localConfig, {
						elbowType: 'hv',
						vLength: -initialVerticalLength + (verticalOffset * (upNdx - 1)),
						hLength: upLength - localConfig.width/2,
						labelOffsetH: horizontalOffset * upNdx,
						labelOffsetV: verticalOffset * upNdx,
						curve: (localConfig.width <= 20?(localConfig.width < 5?5:localConfig.width):20)
					})
				);
			}
			else {
				tcga.graph.widgets.elbowPath(Ext.applyIf(localConfig, {
						elbowType: 'hv',
						vLength: -initialVerticalLength - (verticalOffset * upNdx),
						hLength: length * (lengthDivider/(upCount + 1)),
						labelOffsetH: horizontalOffset * (lengthDivider),
						labelOffsetV: verticalOffset * (upCount - lengthDivider)
					})
				);
			}
			
			upLength -= localConfig.width + horizontalOffset;
			upNdx--;
		}
		// Draw an elbow path pointing down (the only other choice)
		else {
			if (config.threeD) {
				tcga.graph.widgets.dElbowPath(Ext.applyIf(localConfig, {
						elbowType: 'hv',
						vLength: initialVerticalLength - (verticalOffset * downNdx),
						hLength: downLength - localConfig.width/2,
						labelOffsetH: horizontalOffset * downNdx,
						labelOffsetV: verticalOffset * downNdx,
						curve: (localConfig.width <= 20?(localConfig.width < 5?5:localConfig.width):20)
					})
				);
			}
			else {
				tcga.graph.widgets.elbowPath(Ext.applyIf(localConfig, {
						elbowType: 'hv',
						vLength: initialVerticalLength + (verticalOffset * (downCount - lengthDivider)),
						hLength: length * (lengthDivider/(downCount + 1)),
						labelOffsetH: horizontalOffset * (lengthDivider),
						labelOffsetV: verticalOffset * (downCount - lengthDivider)
					})
				);
			}
			
			downLength += localConfig.width + horizontalOffset;
			downNdx++;
		}

		vertLoc -= pathHeight/2;
	}
	
	return nextNodeCenter;
}

