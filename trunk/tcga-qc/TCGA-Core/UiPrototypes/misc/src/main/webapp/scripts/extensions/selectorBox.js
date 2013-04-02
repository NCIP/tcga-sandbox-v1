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
 * @class tcga.extensions.selectorBox
 * @extends Ext.Panel
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * @xtype selectorbox
 */
tcga.extensions.selectorBox = Ext.extend(Ext.Panel, {
	border: false,
	cls: 'selectorBox',
	defaults: {
		border: false
	},

	initComponent : function(){
		tcga.extensions.selectorBox.superclass.initComponent.call(this);
	}
});

Ext.reg('selectorbox', tcga.extensions.selectorBox);
