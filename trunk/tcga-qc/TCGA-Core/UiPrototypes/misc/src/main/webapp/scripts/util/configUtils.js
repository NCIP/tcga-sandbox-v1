Ext.namespace("marcs.util.config");

marcs.util.config.apply = function(config, defaults) {
	if (config == undefined) {
		config = {};
	}
	Ext.applyIf(config, defaults);
	
	return config;
}

marcs.util.config.applyRacSectionDefaults = function(config) {
	marcs.util.config.apply(config, {
		showTitle: true,
		hideEdit: false
	});
	
	return config;
}
