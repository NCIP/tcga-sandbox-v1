Ext.namespace('tcga.util');

/*
 * Text Fitting facilities - used to determine whether a piece of text is too large for the space
 * it is destined to occupy.  If the text is too large, then the text will be returned in a
 * shortened version of itself that will fit in the space and has an ellipsis on the end.
 * 
 * styles - styles to be applied to the text being fitted
 */
	
tcga.util.textFit = function(config) {
	var defaultConfig = {
		styles: {'font-size': '12px'}
	};
	if (!config) {
		this.localConfig = defaultConfig;
	}
	else {
		this.localConfig = Ext.applyIf(config, defaultConfig);
	}

	// Create the text context that will be used to check the size of the text
	this.tc = null;
	
	if (!this.tc) {
		this.tc = new Ext.Element(document.createElement('div'));
		document.body.appendChild(this.tc.dom);
		this.tc.position('absolute');
		this.tc.setLeftTop(-1000, -1000);
		this.tc.hide();
	}

	if (this.localConfig.styles) {
		this.tc.setStyle(this.localConfig.styles);
	}
	
	/*
	 * Get text size - get the size of a piece of text in pixels
	 * 
	 * text - the text to measure
	 * 
	 * Returns: the {height: h, width: w} in pixels
	 */
	this.getSize = function(text){
		this.tc.update(text);
		var s = this.tc.getSize();
		this.tc.update('');
		return s;
	};
	
	/*
	 * Get text height - if a width is not set in the styles, then the height will be for one line
	 * 
	 * text - the text to display
	 * maxWidth - the maximum width of the space to display the text in
	 * styles - the styles for the display area
	 * 
	 * Returns: the height in pixels
	 */
	this.getHeight = function(text, maxWidth, styles) {
		this.tc.setStyle(styles);
		this.tc.dom.style.width = (maxWidth?maxWidth + 'px':'auto');

      return this.getSize(text).height;
	};

	/*
	 * Get text width as if it were all on one line
	 * 
	 * text - the text to display
	 * styles - the styles for the display area
	 * 
	 * Returns: the width in pixels
	 */
	this.getWidth = function(text, styles) {
		this.tc.setStyle(styles);
		this.tc.dom.style.width = 'auto';

		if (text.indexOf('\n') != -1) {
			var lines = text.split('\n');
			var width = 0;
			for (var ndx = 0;ndx < lines.length;ndx++) {
				var currWidth = this.getSize(lines[ndx]).width;				
				width = (currWidth > width?currWidth:width); 
			}
		}
		else {
			width = this.getSize(text).width;
		}

		return width;
	};

	/*
	 * Break up text with \n based on a target width and a pixel height
	 * 
	 * text - the text to display
	 * maxWidth - the maximum width of the space to display the text in
	 * styles - the styles for the display area
	 * 
	 * Returns: the split string
	 */
	this.splitText = function(text, maxWidth, styles) {
		var newStr = '';

		var currWidth = 0;
		var textList = text.split(' ');
		var spaceWidth = this.getWidth('&nbsp;', styles);
		for (var ndx=0;ndx < textList.length;ndx++) {
			var width = this.getWidth(textList[ndx], styles);

			// Don't put a \n or space at the end of the string unnecessarily
			if (currWidth + width + spaceWidth > maxWidth) {
				if (currWidth != 0) {
					newStr += '\n' + textList[ndx] + ' ';
					currWidth = (width + spaceWidth);
				}
				else if (currWidth == 0) {
					newStr += textList[ndx] + '\n';
				}
			}
			else if (ndx + 1 != textList.length) {
				newStr += (textList[ndx] + ' ');
				currWidth += (width + spaceWidth);
			}
			else {
				newStr += textList[ndx];
				currWidth += width;
			}
		}
		
		return newStr;
	};
	
	/*
	 * Trim the text and add an ellipsys if it is too long
	 * 
	 * text - the text to display
	 * maxWidth - the maximum width of the space to display the text in
	 * styles - the styles for the display area
	 * 
	 * Returns: the trimmed string
	 */
	this.maxDisplayStr = function(str, maxWidth, styles) {
		var ellipsisLength = this.getWidth('...', styles);
		var displayStr = '';

		for (var ndx = 0;ndx < str.length;ndx++) {
			displayStr = str.substr(0,ndx);
			if (this.getWidth(displayStr, styles) + ellipsisLength > maxWidth) {
				displayStr = str.substr(0,ndx - 1) + '...';
				break;
			}
		}
		
		return displayStr;
	};

	/*
	 * Create a text display with the appropriate ellipses and hovering text
	 * 	Works for SVG text nodes, Ext.Elements, and DOM objects
	 * 
	 * config - the configuration for the text to display
	 * 	text - the text to display
	 * 	styles - the styles to apply to the displayed text
	 * 	maxWidth - the width within which to display the text
	 * 	maxHeight - the height within which to display the text
	 * 	singleLine - (boolean) true to display the text on a single line
	 * 	valign - set to 'center' to vertically align the text in the center of display space
	 * 	hoverObject - the object that is the target of the hovering text which is created is the
	 * 		text string is too long to be displayed in the space given.  The hoverObject can be an
	 * 		SVG object or an Ext.Element or a DOM node.  If an Ext.Element or DOM node, the text
	 * 		will be updated with the shortened text.
	 * 		NOTE: In the case of SVG, the text object is created, this is not true for DOM nodes
	 * 	svgContext - if the hoverObject is an SVG object, then we need the SVG context to draw it on
	 * 	positioning - 'relative' or 'absolute', 'absolute' is the default
	 * 	top - the top coordinate of the displayed text
	 * 	left - the left coordinate of the displayed text
	 */
	this.display = function(config) {
		var defaultConfig = {
			styles: null,
			top: 5,
			left: 5,
			minWinWidth: 100,
			styles: {'font-size': '12px'},
			hoverClass: 'hoverWin'
		};
		var localConfig = Ext.applyIf(config, defaultConfig);
		
		var displayStr = '';
		var hoverText = null;
		var ellipsisLength = this.getWidth('...', localConfig.styles);
		var ellipsisHeight = this.getHeight('...', localConfig.maxWidth, localConfig.styles);
		var textHeight = this.getHeight(localConfig.text, localConfig.maxWidth, localConfig.styles);
		
		if (localConfig.singleLine) {
			var textWidth = this.getWidth(localConfig.text, styles);
			if (config.maxWidth && textWidth > config.maxWidth) {
				displayStr = this.maxDisplayStr(localConfig.text, localConfig.maxWidth, localConfig.styles);
				
				hoverText = localConfig.text;
			}
			else {
				displayStr = localConfig.text;
			}
		}
		else {
			if (localConfig.maxWidth || localConfig.maxHeight) {
				var multilineText = this.splitText(localConfig.text, localConfig.maxWidth, localConfig.styles);
				var width = this.getWidth(multilineText, localConfig.styles);
				var height = this.getHeight(multilineText, localConfig.maxWidth, localConfig.styles);

				if (localConfig.maxWidth && localConfig.maxHeight) {
					if ((height > localConfig.maxHeight && width > localConfig.maxWidth) || width > localConfig.maxWidth) {
						displayStr = this.maxDisplayStr(localConfig.text, localConfig.maxWidth, localConfig.styles);
						hoverText = multilineText;
					}
					else if (height > localConfig.maxHeight) {
						var lines = multilineText.split('\n');
						var potentialDisplayStr = '';
						for (var ndx = 0;ndx < lines.length;ndx++) {
							potentialDisplayStr += lines[ndx];
							var pHeight = this.getHeight(potentialDisplayStr, localConfig.maxWidth, localConfig.styles);
							if (pHeight > localConfig.maxHeight) {
								if (ellipsisHeight > localConfig.maxHeight) {
									displayStr = ' ';
								}
								else {
									displayStr = this.maxDisplayStr(localConfig.text, localConfig.maxWidth, localConfig.styles);
								}
								hoverText = multilineText;
							}
							else {
								displayStr = potentialDisplayStr;
							}
						}
					}
				}
				else if (localConfig.maxWidth) {
					if (width > localConfig.maxWidth) {
						displayStr = this.maxDisplayStr(localConfig.text, localConfig.maxWidth, localConfig.styles);
						hoverText = multilineText;
					}
				}
				else if (localConfig.maxHeight) {
					if (height > localConfig.maxHeight) {
						if (ellipsisHeight > localConfig.maxHeight) {
							displayStr = ' ';
						}
						else {
						displayStr = this.maxDisplayStr(localConfig.text, localConfig.maxWidth, localConfig.styles);
						}
						hoverText = multilineText;
					}
				}
				
				if (displayStr == '') {
					displayStr = multilineText;
				}
			}
			else {
				displayStr = localConfig.text;
			}
		}

		if (localConfig.valign == 'center') {
			var displayStrHeight = this.getHeight(displayStr, localConfig.maxWidth, localConfig.styles);
			localConfig.top = localConfig.top - localConfig.maxHeight/2 + displayStrHeight/2;
		}

		// Create the svg object
		if (localConfig.svgContext) {
			var textDisplay = localConfig.svgContext.text(localConfig.left, localConfig.top, displayStr);
			var attrs = new Ext.util.MixedCollection();
			attrs.addAll(localConfig.styles);
			attrs.eachKey(function(key, value) {
				textDisplay.attr(key, value);
			});
		}
		else if (Ext.get(hoverObject)) {
			Ext.get(hoverObject).update(displayStr);
		}

		if (hoverText) {
				if (localConfig.svgContext) {
				var hoverObject = textDisplay;
				if (localConfig.hoverObject) {
					hoverObject = localConfig.hoverObject;
				}
	
				new tcga.util.svgHoverWin({
					paper: localConfig.svgContext,
					appearNear: hoverObject,
					text: hoverText,
					left: localConfig.left,
					top: localConfig.top
				});
			}
			else {
				new tcga.util.hoverWin({
					appearNear: hoverObject,
					text: hoverText
				});
			}
		}
		
		return displayStr;
	};
}
