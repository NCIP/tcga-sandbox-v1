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
 * @class tcga.extensions.clickablePanel
 * @extends Ext.Panel
 * <p>Clickable Panel is a panel that adds the click event.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype clickpanel
 */
tcga.extensions.buttonPlus = Ext.extend(Ext.Button, {
	template: new Ext.Template(
			'<table id="{4}" cellspacing="0" class="x-btn {3}"><tbody class="{1}">',
			'<tr><td class="x-btn-tl"><i>&#160;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&#160;</i></td></tr>',
			'<tr><td class="x-btn-ml"><i>&#160;</i></td><td class="x-btn-mc"><em class="{2}" unselectable="on"><button id="{4}btn" type="{0}"></button></em></td><td class="x-btn-mr"><i>&#160;</i></td></tr>',
			'<tr><td class="x-btn-bl"><i>&#160;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&#160;</i></td></tr>',
			'</tbody></table>'),

	initComponent : function(){
		this.template.compile();

		tcga.extensions.buttonPlus.superclass.initComponent.call(this);
	}
});

Ext.reg('buttonplus', tcga.extensions.buttonPlus);
