/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function() {
	var linkConfigFeedUrl = '/web/news/linkConfig.json';
	if(tcgaHost.indexOf("localhost") > -1){
	  	linkConfigFeedUrl = 'json/linkConfig.sjson';
	}

	var newsStore = new Ext.data.JsonStore({
       url: linkConfigFeedUrl,
	   root: 'linkConfig',
	   autoLoad: true,
	   fields: [
	       'linkId',
	       {name: 'active', type: 'boolean'}
	   ],
	   listeners: {
			'load': {
				scope: this,
				fn: function(store, recs) {
                    var linkRef = {
                        'annotations': '<a href="' + tcgaSecureHost + '/annotations/">Annotations Manager</a>',
                        'uuid': '<a href="' + tcgaSecureHost + '/uuid/">UUID Manager</a>'
                    };

					for (var ndx0 = 0;ndx0 < store.getCount();ndx0++) {
						var replacementList = null;
						var active = recs[ndx0].get('active');
						if (active) {
							var linkId = recs[ndx0].get('linkId');
							var replacementList = Ext.query('*.' + linkId + 'Link')
	
							for (var ndx1 = 0;ndx1 < replacementList.length;ndx1++) {
								replacementList[ndx1].innerHTML = linkRef[linkId];
							}
						}
					}
				}
			}
	   }
	});
});
