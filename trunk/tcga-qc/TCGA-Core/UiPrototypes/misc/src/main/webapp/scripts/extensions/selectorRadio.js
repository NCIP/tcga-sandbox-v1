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
 * @class tcga.extensions.selectorRadio
 * @extends tcga.extensions.selectorCheck
 * <p></p>
 * @constructor
 * @param {Object} config The config object
 * 	closed
 * 	queryClass, closed, order, parameterDesc, interactive, store
 * @xtype selectorRadio
 */
tcga.extensions.selectorRadio = Ext.extend(tcga.extensions.selectorCheck, {
	selectorType: 'radio',

	initComponent : function(){
		tcga.extensions.selectorRadio.superclass.initComponent.call(this);
	}
});

Ext.reg('selectorradio', tcga.extensions.selectorRadio);
