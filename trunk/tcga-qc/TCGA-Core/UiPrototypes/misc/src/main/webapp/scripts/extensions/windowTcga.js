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
 * @class tcga.extensions.clickablePanel
 * @extends Ext.Panel
 * <p>Clickable Panel is a panel that adds the click event.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype clickpanel
 */
tcga.extensions.window = Ext.extend(Ext.Window, {
	frame: false,
	headerCfg: {
		tag: 'div',
		cls: 'wintop'
	},
	bodyCfg: {
		tag: 'div',
		cls: 'winbody'
	},
	footerCfg: {
		tag: 'div',
		cls: 'winfoot'
	}
});

Ext.reg('windowtcga', tcga.extensions.window);
