Ext.namespace("marcs.util.tabIndex");

// Pretend that this is private
marcs.util.tabIndex.value = 1;

marcs.util.tabIndex.getCurrentTabIndex = function() {
	return marcs.util.tabIndex.value;
}

marcs.util.tabIndex.getNextTabIndex = function() {
	return marcs.util.tabIndex.value++;
}

marcs.util.tabIndex.resetTabIndex = function() {
	marcs.util.tabIndex.value = 0;
}

