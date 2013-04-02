Ext.namespace("marcs.util.misc");

marcs.util.misc.redirectTo = function(location, newWindow) {
	if (newWindow != undefined && newWindow != false) {
		if (newWindow == true) {
			newWindow = '_blank';
		}
		window.open(location, newWindow);
	}
	else {
		window.location.replace(location);
	}
}
