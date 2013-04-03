/*******************************************************************************
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.namespace('tcga.extensions');

/**
 * @class tcga.extensions.conflooktabpanel
 * @extends Ext.TabPanel
 * <p>Changes the look of the tab panel and allows the look to be configured.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype conflooktabpanel
 */
tcga.extensions.TabPanel = function(config) {
	var template = new Ext.Template(
		'<li class="{cls}" id="{id}">',
			'<a href="#" onclick="return false;">',
				'<span class="x-tab-strip-text {iconCls}">{text}</span>',
			'</a>',
		'</li>');

	// Setup our default configs
	var localConfig = {
		noClear: true,
		plain: true,
		tabCls: 'tcga-ext-tab',
		tabTmpl: template,
		tabOrientation: 'horizontal'
	};

	// Apply the overrides
	Ext.apply(localConfig, config);

	tcga.extensions.TabPanel.superclass.constructor.call(this, localConfig);
};

Ext.extend(tcga.extensions.TabPanel, Ext.TabPanel, {
	// Override Ext.TabPanel's onRender
	onRender: function(ct, position) {
		// SUPER SUPER
		Ext.TabPanel.superclass.onRender.call(this, ct, position);

		if(this.plain) {
			var pos = this.tabPosition == 'top' ? 'header' : 'footer';
			this[pos].addClass('x-tab-panel-' + pos + '-plain');
		}

		var st = this[this.stripTarget];
		
		if (this.hideTabs) {
			st.setDisplayed('none');
		}

		var stripWrapCls = this.tabCls ?
			'x-tab-strip-' + this.tabPosition + ' ' + this.tabCls :
			'x-tab-strip x-tab-strip-' + this.tabPosition;

		this.stripWrap = st.createChild({
			cls: 'x-tab-strip-wrap',
			cn: {
				tag: 'ul',
				cls: stripWrapCls
			}
		});

		this.strip = new Ext.Element(this.stripWrap.dom.firstChild);

		this.edge = this.strip.createChild({
			tag: 'li',
			cls: 'x-tab-edge'
		});
		  
		if( ! this.noClear) {
			this.strip.createChild({
				cls: 'x-clear'
			});
		}

		this.body.addClass('x-tab-panel-body-' + this.tabPosition);

		if( ! this.itemTpl) {
			var tt = this.tabTmpl ?
				this.tabTmpl :
				new Ext.Template(
					'<li class="{cls}" id="{id}">',
					'<a class="x-tab-strip-close" onclick="return false;"></a>',
					'<a class="x-tab-right" href="#" onclick="return false;">',
						'<em class="x-tab-left">',
							'<span class="x-tab-strip-inner">',
								'<span class="x-tab-strip-text {iconCls}">',
									'{text}',
								'</span>',
							'</span>',
						'</em>',
					'</a>',
					'</li>');

			tt.disableFormats = true;
			tt.compile();
			tcga.extensions.TabPanel.prototype.itemTpl = tt;
		}

		this.items.each(this.initTab, this);

		if(this.headerItems) {
			for (var ndx = 0; ndx < this.headerItems.length; ndx++) {
				this.stripWrap.createChild(this.headerItems[ndx]);
			}
		}
	}
});

Ext.reg('conflooktabpanel', tcga.extensions.TabPanel);
