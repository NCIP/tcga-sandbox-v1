/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

//override ext basic form to add a clear form method.
//override ext basic form getFieldValues to add proper
// handling of date value with their format
Ext.override(Ext.form.BasicForm,{
    clear : function() {
        this.items.each(function(f) {
            f.originalValue = undefined;
            f.value = undefined;
            f.reset();
        });
        return this;
    },
    getFieldValues : function(dirtyOnly) {
        var o = {},n,key,val;
        this.items.each(function(f) {
            if (dirtyOnly !== true || f.isDirty()) {
                n = f.getName();
                key = o[n];
                if (Ext.isDate(f.getValue())){  //My date handling
                    val = f.getValue().format("m/d/Y");
                } else {
                    val = f.getValue();
                }
                if (Ext.isDefined(key)) {
                    if (Ext.isArray(key)) {
                        o[n].push(val);
                    } else {
                        o[n] = [key, val];
                    }
                } else {
                    o[n] = val;
                }
            }
        });
        return o;
    }
});

Ext.override(Ext.grid.ColumnModel, {
    /**
     * Returns an array of column config objects for the visibility given.
     * @param {Boolean} hidden True returns visible columns; False returns hidden columns (defaults to True).
     * @param {String} cfg Specify the config property to return (defaults to the config).
     * @return {Array} result
     */
    getColumnsVisible : function(visibility, cfg) {
        var visible = (visibility === false) ? false : true;
        var r = [];
        for (var i = 0, len = this.config.length; i < len; i++) {
            var c = this.config[i];
            var hidden = c.hidden ? true : false;
            if (hidden !== visible) {
                r[r.length] = c[cfg] || c;
            }
        }
        return r;
    }
});

//Override to remove the default sorting asc on name of any property grids
Ext.override(Ext.grid.PropertyGrid,{
    initComponent : function(){
        this.customRenderers = this.customRenderers || {};
        this.customEditors = this.customEditors || {};
        this.lastEditRow = null;
        var store = new Ext.grid.PropertyStore(this);
        this.propStore = store;
        var cm = new Ext.grid.PropertyColumnModel(this, store);
        this.addEvents('beforepropertychange','propertychange');
        this.cm = cm;
        this.ds = store.store;
        Ext.grid.PropertyGrid.superclass.initComponent.call(this);
        this.mon(this.selModel, 'beforecellselect', function(sm, rowIndex, colIndex){
            if(colIndex === 0){
                this.startEditing.defer(200, this, [rowIndex, 1]);
                return false;
            }
        }, this);
    }  
});

//Override of the renderUI function of Ext.grid.GridView to not render the show/hide columns
// menus at every column header.
// Note: the method is huge in the extJs sources. To be able to
//tweak only a few feature I still have to override the huge function to keep full functionality
//of the grid
Ext.override(Ext.grid.GridView, {
    renderUI : function() {
        var header = this.renderHeaders();
        var body = this.templates.body.apply({rows:'&#160;'});
        var html = this.templates.master.apply({
            body: body,
            header: header,
            ostyle: 'width:' + this.getOffsetWidth() + ';',
            bstyle: 'width:' + this.getTotalWidth() + ';'
        });
        var g = this.grid;
        g.getGridEl().dom.innerHTML = html;
        this.initElements();
        // get mousedowns early
        Ext.fly(this.innerHd).on('click', this.handleHdDown, this);
        this.mainHd.on({
            scope: this,
            mouseover: this.handleHdOver,
            mouseout: this.handleHdOut,
            mousemove: this.handleHdMove
        });
        this.scroller.on('scroll', this.syncScroll, this);
        if (g.enableColumnResize !== false) {
            this.splitZone = new Ext.grid.GridView.SplitDragZone(g, this.mainHd.dom);
        }
        if (g.enableColumnMove) {
            this.columnDrag = new Ext.grid.GridView.ColumnDragZone(g, this.innerHd);
            this.columnDrop = new Ext.grid.HeaderDropZone(g, this.mainHd.dom);
        }
        if (g.enableHdMenu !== false) {
            this.hmenu = new Ext.menu.Menu({id: g.id + '-hctx'});
            this.hmenu.add(
            {itemId:'asc', text: this.sortAscText, cls: 'xg-hmenu-sort-asc'},
            {itemId:'desc', text: this.sortDescText, cls: 'xg-hmenu-sort-desc'}
                    );
            if (g.enableColumnHide !== false) {
                this.colMenu = new Ext.menu.Menu({id:g.id + '-hcols-menu'});
                this.colMenu.on({
                    scope: this,
                    beforeshow: this.beforeColMenuShow,
                    itemclick: this.handleHdMenuClick
                });
            }
            this.hmenu.on('itemclick', this.handleHdMenuClick, this);
        }
        if (g.trackMouseOver) {
            this.mainBody.on({
                scope: this,
                mouseover: this.onRowOver,
                mouseout: this.onRowOut
            });
        }
        if (g.enableDragDrop || g.enableDrag) {
            this.dragZone = new Ext.grid.GridDragZone(g, {
                ddGroup : g.ddGroup || 'GridDD'
            });
        }
        this.updateHeaderSortState();
    }
});