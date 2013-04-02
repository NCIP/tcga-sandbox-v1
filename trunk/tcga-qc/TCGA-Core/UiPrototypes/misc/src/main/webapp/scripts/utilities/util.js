Ext.namespace('tcga.util');

/* From http://www.netlobo.com/url_query_string_javascript.html */
tcga.util.getUrlParam = function(name) {
	name = name.replace(/[\[]/,'\\\[').replace(/[\]]/,'\\\]');
	var regexS = '[\\?&]'+name+'=([^&#]*)';
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );
	
	if( results == null ) {
		return '';
	}
	else {
		results[1] = results[1].replace(/%20/g,' ')
		return results[1];
	}
}

/* 
 * A utility to be used for ensuring that a new DOM object will appear within the frame of
 * the window.
 * 
 * Returns: an object with the top and left coordinates
 * 
 * Config
 * top - a proposed top coordinate for the window, this is relative to the appearNear DOM
 * 	object if one is set
 * left - same as top but for the left coordinate
 * appearNear - a DOM object to appear near
 * minWidth - the minimum desired width that the new object
 */
tcga.util.getAdjustedLeftTop = function(config) {
	var defaultConfig = {
		appearNear: null,
		top: 5,
		left: 5,
		minWidth: 100
	};
	var localConfig = Ext.applyIf(config, defaultConfig);
	
	var top = localConfig.top;
	var left = localConfig.left;
	if (localConfig.appearNear != null) {
		var appearNear = Ext.get(localConfig.appearNear);

		if (appearNear != null) {
			top += appearNear.getTop();
			left += appearNear.getLeft();
		}
	}

	// Check on the 
	// Check to make sure we're not going off the page, if we are, adjust back a bit
	if (left + localConfig.minWidth > window.innerWidth) {
		left = window.innerWidth - localConfig.minWidth;

		// If this is FireFox or IE (now that's something usually not mentioned in the same clause),
		//		we also need to subtract off the scrollbar width
		if (Ext.isGecko || Ext.isIE) {
			left -= 40;
		}
	}
	
	return {left: left, top: top};
}

