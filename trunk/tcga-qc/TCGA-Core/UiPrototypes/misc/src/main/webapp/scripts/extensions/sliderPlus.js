/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.extensions');

/**
 * @class tcga.extensions.sliderPlus
 * @extends Ext.Panel
 * <p>Highlighted slider adds a highlighted section between the thumbs on a multislider.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype highlightedslider
 */
tcga.extensions.sliderPlus = Ext.extend(Ext.slider.MultiSlider, {
	highlighter: null,
	// highlight may be set to 'left', 'right' or 'full' - full means that the highlighting is
	// 	between two sliders, left means one slider and to the left end, right, one slider and
	// 	to the right end
	highlight: 'full',
	
	adjustHighlighter: function() {
		if (this.highlighter) {
			// There is a 2 pixel offset between the left end of the slider and the leftmost
			//		position of the thumb.
			if (this.highlight == 'full' && this.thumbs[1]) {
				// There may only be one thumb
				this.highlighter.setWidth(this.thumbs[1].el.getLeft() - this.thumbs[0].el.getLeft());
				this.highlighter.setLeft(this.thumbs[0].el.getLeft() - this.el.getLeft() - 2);
			}
			else {
				// Need to get the parent container to get the x offset
				var parent = this.el.findParent('div', 1, true);
				if (this.highlight == 'left') {
					this.highlighter.setWidth(this.thumbs[0].el.getLeft() - this.el.getLeft() - 2);
					this.highlighter.setLeft(this.el.getLeft() - parent.getLeft());
				}
				else if (this.highlight == 'right') {
					this.highlighter.setWidth(this.el.getRight() - this.thumbs[0].el.getRight() + 2);
					this.highlighter.setRight(this.el.getRight() - parent.getRight());
				}
			}
		}
	},

	onShow: function() {
		this.syncThumb();
	},

	addHighlighter: function() {
		this.highlighter = this.innerEl.createChild({
			cls: 'x-slider-selected'
		});
		
		this.adjustHighlighter();
	},

	setValue: function(index, v, animate, changeComplete) {
		tcga.extensions.sliderPlus.superclass.setValue.call(this, index, v, false, changeComplete);

		this.adjustHighlighter();
	},

	syncThumb: function(index, v, animate, changeComplete) {
		tcga.extensions.sliderPlus.superclass.syncThumb.call(this);

		this.adjustHighlighter();
	},

	initComponent: function() {
		tcga.extensions.sliderPlus.superclass.initComponent.call(this);
		
		this.addListener('afterrender', this.addHighlighter);
		this.addListener('show', this.onShow);
	}
});

Ext.reg('sliderplus', tcga.extensions.sliderPlus);
