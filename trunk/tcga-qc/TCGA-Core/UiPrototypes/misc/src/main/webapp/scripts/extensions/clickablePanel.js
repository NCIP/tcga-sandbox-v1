Ext.namespace('tcga.extensions');

/**
 * @class tcga.extensions.clickablePanel
 * @extends Ext.Panel
 * <p>Clickable Panel is a panel that adds the click event.</p>
 * @constructor
 * @param {Object} config The config object
 * @xtype clickpanel
 */
tcga.extensions.ClickablePanel = Ext.extend(Ext.Panel, {

    initComponent : function(){
        tcga.extensions.ClickablePanel.superclass.initComponent.call(this);

        this.addEvents(
            /**
             * @event click
             * Fires after the Panel has been clicked.
             * @param {Ext.Panel} p the Panel which has been resized.
             */
            'click'
		);
	},
	
    // private
    onRender : function(ct, position){
        tcga.extensions.ClickablePanel.superclass.onRender.call(this, ct, position);

        this.mon(this.el, 'click', this.onClick, this);
		
		if (this.handler) {
			if (!this.scope) {
				this.scope = this;
			}
	        this.mon(this.el, 'click', this.handler, this.scope);
		}
	},
	
	onClick: function() {
        this.fireEvent('click', this);
	}
});

Ext.reg('clickpanel', tcga.extensions.ClickablePanel);
