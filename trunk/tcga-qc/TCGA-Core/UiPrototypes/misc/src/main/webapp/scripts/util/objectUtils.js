Ext.namespace("marcs.util.object");

marcs.util.object.concat = function(/* Indeterminate number of arguments */) {
	var newObject = {};
	var arguments = marcs.util.object.concat.arguments;
	
	for (var ndx = 0;ndx < arguments.length;ndx++) {
		for (var argument in arguments[ndx]) {
			// Would be nice to be able to use this with functions.  Hmm, override param perhaps?
			if (typeof argument != 'function') {
				newObject[argument] = arguments[ndx][argument];
			}
		}
	}
	
	return newObject;
}
