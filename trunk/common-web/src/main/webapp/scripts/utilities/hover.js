Ext.namespace('tcga.util');

/*
 * Hovering text facilities
 * 
 * appearNear - a DOM object to hover near, if this is null, then use the coordinates as absolutes,
 * 	otherwise use them as offsets from the top/left of the hover near object
 * 	- Also of note - if appearNear is not set, the assumption is that showing and hiding of the
 * 		window will happen externally.
 * top - the top coordinate of the hover window
 * left - the left coordinate of the hover window
 * minWinWidth - the minimum width of the window, this is used when we're running up against the
 * 	right side of the screen
 * hoverClass - a class to apply to the hoverWin, this will replace the default hoverWin spec
 * styles - styles to be applied to the text shown in the hoverWin
 * 
 * Useful functions inherited from being an Ext.Element
 * show - show the window
 * hide - hide the window
 * update - update the text in the window
 */
	
tcga.util.hoverWin = Ext.extend(Ext.Element, {
	constructor: function(config) {
		var defaultConfig = {
			appearNear: null,
			top: 5,
			left: 5,
			minWinWidth: 100,
			styles: {'font-size': '12px'},
			hoverClass: 'hoverWin'
		};
		var localConfig = Ext.applyIf(config, defaultConfig);
	
		if (localConfig.id == null) {
			localConfig.id = Ext.id();
		}
		
		this.dom = document.createElement('div');
		this.dom.id = localConfig.id;
		document.body.appendChild(this.dom);

		tcga.util.hoverWin.superclass.constructor.call();

		this.addClass(localConfig.hoverClass);
		this.setStyle(localConfig.styles);
		this.position('absolute');
		this.hide();
		this.update(localConfig.text);

		if (localConfig.appearNear != null) {
			// Check to see if we're dealing with one target or several, if one, convert
			//		it to a single element array so we can use one routine for the assignment
			if (typeof localConfig.appearNear == 'string' || localConfig.appearNear.length == undefined) {
				var appearNear = [localConfig.appearNear];
			}
			else {
				var appearNear = localConfig.appearNear;
			}
			
			for (var ndx = 0;ndx < appearNear.length;ndx++) {
				this.setHover(appearNear[ndx], localConfig);
			}
		}
	},

	setHover: function(targetObj, localConfig) {
		var targetCmp = Ext.get(targetObj);
		
		// Set up the hover events to pay attention to
		targetCmp.addListener('mouseenter', function() {
			// Do the location check here in case the appearNear object has moved since creation
			//		of the hovering window.
			
			// If there is a appearNear object, then hover near that object and use the top, left
			//		offsets as offsets from that object
			var winLoc = tcga.util.getAdjustedLeftTop(localConfig);
			if (this.setLeftTop) {
				this.setLeftTop(winLoc.left, winLoc.top);
			}
			else if (this.win.setLeftTop) {
			this.win.setLeftTop(winLoc.left, winLoc.top);
			}

			this.show();
		}, this);
	
		targetCmp.addListener('mouseleave', function() {
			this.hide();
		}, this);
	}
})
