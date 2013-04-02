Ext.namespace('tcga.uuid');

tcga.uuid.historyManager = function(token) {
	if (token == null) {
		tcga.uuid.browser.hide();
		tcga.uuid.search.results.hide();
	}
	else if (token == 'Browse') {
		tcga.uuid.browser.show();
		tcga.uuid.search.results.hide();
	}
	else if (token == 'Search') {
		tcga.uuid.search.results.show();
		tcga.uuid.browser.hide();
	}
}
