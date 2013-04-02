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
