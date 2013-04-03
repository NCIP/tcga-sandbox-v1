/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

/*
 * Various drawing utils
 */
var drawingUtils = {
	origin: function(x, y) {
		return 'M' + x + ' ' + y;
	},
	
	line: function(x, y, x1, y1, newLine) {
		var command = (newLine?'L':' ');
		return command + x1 + ' ' + y1;
	},
	
	parGramAngle: function(x, y, x1, y1, w) {
		return Raphael.deg(Raphael.angle(x1, y1, x + w, y));
	}
}
