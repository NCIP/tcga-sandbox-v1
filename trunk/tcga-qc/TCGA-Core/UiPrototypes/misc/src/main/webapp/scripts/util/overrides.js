Ext.override(Ext.Panel, {
	addBodyClass: function(cls) {
        if(this.body) {
            this.body.addClass(cls);
        }
        else {
            this.cls = this.cls ? this.cls + ' ' + cls : cls;
        }
        return this;
	},

	removeBodyClass: function(cls) {
        if(this.body){
            this.body.removeClass(cls);
        }
        else if (this.cls) {
            this.cls = this.cls.split(' ').remove(cls).join(' ');
        }
        return this;
	},

	addBodyOverClass: function(overCls) {
        if(this.body) {
			this.el.addListener('mouseover', 
								function(){
									this.addBodyClass(overCls);
								},
								this);
			this.el.addListener('mouseout', 
								function(){
									this.removeBodyClass(overCls);
								},
								this);
			this.body.addClassOnOver(overCls);
			
        }
        return this;
	}
});

Ext.override(Ext.data.JsonStore, {
	getProxy: function() {
		return this.proxy;
	}
});

Ext.override(Ext.layout.ColumnLayout, {
    onLayout : function(ct, target){
        var cs = ct.items.items, len = cs.length, c, i;

        this.renderAll(ct, target);

        var size = this.getLayoutTargetSize();

        if(size.width < 1 && size.height < 1){ // display none?
            return;
        }

        var w = size.width - this.scrollOffset,
            h = size.height,
            pw = w;

        this.innerCt.setWidth(w);

        // some columns can be percentages while others are fixed
        // so we need to make 2 passes

        for(i = 0; i < len; i++){
            c = cs[i];
            if(!c.columnWidth){
                pw -= (c.getWidth() + c.getPositionEl().getMargins('lr'));
            }
        }

        pw = pw < 0 ? 0 : pw;

        for(i = 0; i < len; i++){
            c = cs[i];
            if (c.columnWidth == 1) {
				c.setSize(w);
			}
			else if(c.columnWidth){
                c.setSize(Math.floor(c.columnWidth * pw) - c.getPositionEl().getMargins('lr'));
            }
        }

        // Browsers differ as to when they account for scrollbars.  We need to re-measure to see if the scrollbar
        // spaces were accounted for properly.  If not, re-layout.
        if (Ext.isIE) {
            if (i = target.getStyle('overflow') && i != 'hidden' && !this.adjustmentPass) {
                var ts = this.getLayoutTargetSize();
                if (ts.width != size.width){
                    this.adjustmentPass = true;
                    this.onLayout(ct, target);
                }
            }
        }
        delete this.adjustmentPass;
    }
});
