Ext.namespace("marcs.util.display");

marcs.IMAGE_ROOT = 'images/';

marcs.util.display.displayLine = function(orientation, color, length) {
	var side;
	if (orientation) {
		if (orientation == 'vertical') {
			dimension = 'height';
			side = 'left'; 
		}
		else if (orientation == 'horizontal') {
			dimension = 'width';
			side = 'top'; 
		}
	}
	else {
		dimension = 'width';
		side = 'top'; 
	}
	if (!color) {
		color = '#000000';
	}
	if (!length) {
		length = '100%'
	}

	return '<div style="border-' + side + ': 1px solid ' + color +'; ' + dimension + ':' + length + '"></div>'
}

/*
 * Browser size reader that adjusts for the size of our header and footer unless passed the
 * optional give me the real size parameter.
 * Params: 
 * 		- realSize - boolean - indicates that the real size of the browser, not subtracting
 * 			the size of the header and footer, should be returned.  Defaults to false.
*/

marcs.util.display.minHeight = 600;
marcs.util.display.maxHeight = 1024;
marcs.util.display.minWidth = 800;
marcs.util.display.maxWidth = 1280;

marcs.util.display.getBrowserSize = function(realSize) {
	if (!realSize) {
		realSize = false;
	}

	var height = document.documentElement.clientHeight;
	if (height > marcs.util.display.maxHeight) {
		height = marcs.util.display.maxHeight;
	}
	else if (height < marcs.util.display.minHeight) {
		height = marcs.util.display.minHeight;
	}

	var width = document.documentElement.clientWidth;
	if (width > marcs.util.display.maxWidth) {
		width = marcs.util.display.maxWidth;
	}
	else if (width < marcs.util.display.minWidth) {
		width = marcs.util.display.minWidth;
	}
		
	return ({
		// 300 Accounts for the size of the recall audit check header.  Hmm, should put that number in a config js file.
		// 13 Accounts for a little space for borders.  Same comment on the config js file.
		height: (realSize?height:height - 300),
		width: (realSize?width:width - 13)
	});
}

marcs.util.display.displayImage = function(config) {
	var imgHtml = '<img src="' + marcs.IMAGE_ROOT + config.imagePath + '"';

	if (config.alt) {
		imgHtml += ' alt="' + config.alt + '"';
	}

	if (config.qtip) {
		imgHtml += ' ext:qtip="' + config.qtip + '"';
	}

	if (config.className) {
		imgHtml += ' class="' + config.className + 
			(config.action?' stdOnClickImage':'') +
			'"';
	}

	if (config.style) {
		imgHtml += ' style="' + config.style + '"';
	}

	if (config.action) {
		imgHtml += ' onclick="' + config.action + '"';
	}

	if (config.extraParams) {
		imgHtml += ' ' + config.extraParams;
	}

	imgHtml += '>';

	return imgHtml;
}

marcs.util.display.displayLinkWithAction = function(config) {
	var linkHtml = '<a';
	
	if (config.id) {
		linkHtml += ' id="' + config.id + '"';
	}

	if (config.qtip) {
		linkHtml += ' ext:qtip="' + config.qtip + '"';
	}

	linkHtml += ' class="';
	if (config.className) {
		linkHtml += config.className;
	}
	else {
		// Default class, with no decoration on the link
		linkHtml += 'stdOnClickLink';
	}
	linkHtml += '"';

	if (config.action) {
		linkHtml += ' onclick="' + config.action + '"';
	}

	if (config.extraParams) {
		linkHtml += ' ' + config.extraParams;
	}

	linkHtml += '>' + config.text + '</a>';

	return linkHtml;
}

marcs.util.display.getAppropriateWindowSize = function(suggestedSize) {
	var workingSize = marcs.util.display.getBrowserSize(true);
	
	// Note, the 20 fudge factor is thrown in as a buffer of space.
	if (suggestedSize < workingSize.height - 20) {
		return suggestedSize;
	}
	else {
		return workingSize.height - 20;
	}
}

// The modifier is used in case there are other elements on the screen that need to
//		be accounted for.  If modifier is null, then a standard modifier of 155 is used
//		to account for the app header, footer, trail and portlet elements.
marcs.util.display.getAppropriateTableSize = function(modifier) {
	var workingSize = marcs.util.display.getBrowserSize(true);
	if (!modifier) {
		modifier = 161;
	}
	
	// Note, the modifier is thrown in as a buffer of space.
	if (marcs.util.display.portletSizeStandards.tableMinHeight > workingSize.height - modifier) {
		return marcs.util.display.portletSizeStandards.tableMinHeight;
	}
	else {
		return workingSize.height - modifier;
	}
}
