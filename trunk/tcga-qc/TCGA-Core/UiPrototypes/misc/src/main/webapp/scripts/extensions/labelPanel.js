Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.LabelPanel
 * @extends Ext.Panel
 * <p>A panel for displaying labels.  These should be used consistently in a form so that the required and non-required fields all line up.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype valuefield
 */
marcs.extensions.LabelPanel = Ext.extend(Ext.Panel, {
	layout: 'column',
	labelWidth: 200,
	labelCls: 'stdLabel right',
	required: false,

    initComponent : function(){
	 	this.id = Ext.id();
		
		this.width = this.labelWidth;
		
		this.items = [{
			xtype: 'panel',
			border: false,
			cls: this.labelCls,
			width: this.labelWidth - 10,
			padding: '3px 0px 0px 0px',
			html: '<label>' + this.labelText + '</label>'
		}];
		
		if (this.required) {
			this.items.push({
				xtype: 'panel',
				border: false,
				width: 10,
				padding: '3px 0px 0px 0px',
				html: marcs.util.display.displayImage({
					imagePath: 'icons/required.gif',
					alt: 'Required field',
					style: 'vertical-align: top'
				})
			});
		}
		else {
			this.items.push({
				xtype: 'panel',
				border: false,
				width: 10,
				html: '&nbsp;'
			});
		}

        marcs.extensions.LabelPanel.superclass.initComponent.call(this);
	},
	
	getId: function() {
		return this.id;
	}	
});

Ext.reg('labelpanel', marcs.extensions.LabelPanel);
