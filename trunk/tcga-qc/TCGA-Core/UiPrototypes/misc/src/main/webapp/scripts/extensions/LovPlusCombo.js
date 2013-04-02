// create namespace
Ext.ns('Ext.ux.form');

/**
 *
 * @class Ext.ux.form.LovPlusCombo
 * @extends Ext.ux.form.LovCombo
 */
Ext.ux.form.LovPlusCombo = Ext.extend(Ext.ux.form.LovCombo, {
    initComponent:function() {

        // template with checkbox
        if(!this.tpl) {
            this.tpl =
                 '<tpl for=".">'
                +'<div class="x-combo-list-item">'
                +'<img src="' + Ext.BLANK_IMAGE_URL + '" '
                +'class="ux-lovcombo-icon ux-lovcombo-icon-'
                +'{[values.' + this.checkField + '?"checked":"unchecked"' + ']}">'
                +'<div id="' + this.id + '{[xindex]}" class="ux-lovcombo-item-text">' + (this.displayTpl?this.displayTpl:('{' + (this.displayField || 'text') + '}')) + '</div>'
                +'</div>'
                +'</tpl>'
            ;
        }

        // call parent
        Ext.ux.form.LovPlusCombo.superclass.initComponent.apply(this, arguments);

        // install internal event handlers
        this.on({
             scope:this
            ,beforequery:this.onBeforeQuery
            //,blur:this.onRealBlur
        });

        // remove selection from input field
        this.onLoad = this.onLoad.createSequence(function() {
            if(this.el) {
                var v = this.el.dom.value;
                this.el.dom.value = '';
                this.el.dom.value = v;
            }
        });

    } // e/o function initComponent

}); // eo extend

// register xtype
Ext.reg('lovpluscombo', Ext.ux.form.LovPlusCombo);

// eof
