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
    /**
     * clears value - same as lovCombo, but with the clearFilter commented out since
     * it messes up the filtering that's required for the receiving center combo
     */
    ,clearValue:function() {
        this.value = '';
        this.setRawValue(this.value);
        this.store.each(function(r) {
            r.set(this.checkField, false);
        }, this);
        if(this.hiddenField) {
            this.hiddenField.value = '';
        }
        this.applyEmptyText();
    } // eo function clearValue
    /**
     * Sets the value of the LovCombo - same as lovCombo, but with the clearFilter commented out since
     * it messes up the filtering that's required for the receiving center combo
     * @param {Mixed} v value
     */
    ,setValue:function(v) {
        if(v) {
            v = '' + v;
            if(this.valueField) {
                this.store.each(function(r) {
                    if (r.data[this.valueField] === this.selectAllOn && this.selectAllCheck())
                        r.set(this.checkField, true);
                    else if (r.data[this.valueField] === this.selectAllOn)
                        r.set(this.checkField, null);
                    else {
                        var checked = !(!v.match(
                             '(^|' + RegExp.escape(this.separator) + ')' + RegExp.escape(r.get(this.valueField))
                            +'(' + RegExp.escape(this.separator) + '|$)'))
                        ;
                        r.set(this.checkField, checked);
                    }
                }, this);

                this.value = this.getCheckedValue();
                this.setRawValue(this.getCheckedDisplay());
                if(this.hiddenField) {
                    this.hiddenField.value = this.value;
                }
            }
            else {
                this.value = v;
                this.setRawValue(v);
                if(this.hiddenField) {
                    this.hiddenField.value = v;
                }
            }
            if(this.el) {
                this.el.removeClass(this.emptyClass);
            }
        }
        else {
            this.clearValue();
        }
    } // eo function setValue
}); // eo extend

// register xtype
Ext.reg('lovpluscombo', Ext.ux.form.LovPlusCombo);

// eof
