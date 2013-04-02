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
		return Raphael.deg(Raphael.angle(x1 + w, y1, x, y));
	},
	
	getNextColor: function() {
		var color = colorGradientOrder[currColorGradient];
		currColorGradient++;
		if (currColorGradient == colorGradientOrder.length) {
			currColorGradient = 0;
		}
		
		return color;
	}
};

var colorGradientTriples = {
	blue: [
		['#94b7d2', '#e3f2fd'], 
		['#728da2', '#d3eafb'],
		['#3e4d57', '#83a2ba']
	],
	
	green: [
		['#769e00', '#c8db90'], 
		['#5c7a02', '#bfd973'], 
		['#39490d', '#68860d']
	],
	
	red: [
		['#b53b3b', '#f1bebe'], 
		['#8b2d2d', '#e89999'], 
		['#4b1a1a', '#a03434']
	],
	
	orange: [
		['#d8783b', '#ffd8be'], 
		['#a65d2f', '#fec098'], 
		['#7e4a28', '#b96936']
	],
	
	yellow: [
		['#dbae27', '#f8d979'], 
		['#a58111', '#f5d675'], 
		['#604b09', '#b38d17']
	],
	
	brown: [
		['#cb9e17', '#e8c969'], 
		['#957101', '#e5c665'], 
		['#503b09', '#a37d07']
	]
};

var currColorGradient = 0;

var colorGradientOrder = [
	'blue',
	'green',
	'red',
	'orange',
	'yellow'
];
