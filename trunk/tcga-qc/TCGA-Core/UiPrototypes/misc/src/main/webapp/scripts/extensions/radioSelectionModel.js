Ext.namespace('marcs.extensions');

/**
 * @class marcs.extensions.RadioSelectionModel
 * @extends Ext.grid.RowSelectionModel
 * A custom selection model that renders a column of checkboxes that can be toggled to select or deselect rows.
 * @constructor
 * @param {Object} config The configuration options
 */
marcs.extensions.RadioSelectionModel = Ext.extend(Ext.grid.RowSelectionModel, {

    /**
     * @cfg {Number} width The default width in pixels of the checkbox column (defaults to <tt>20</tt>).
     */
    width : 20,
    /**
     * @cfg {Boolean} sortable <tt>true</tt> if the checkbox column is sortable (defaults to
     * <tt>false</tt>).
     */
    sortable : false,

    // private
    menuDisabled : true,
    fixed : true,
    dataIndex : '',
    id : 'radioselector',
	singleSelect: true,

    constructor : function(){
        marcs.extensions.RadioSelectionModel.superclass.constructor.apply(this, arguments);

        if(this.checkOnly){
            this.handleMouseDown = Ext.emptyFn;
        }
    },

    // private
    initEvents : function(){
        marcs.extensions.RadioSelectionModel.superclass.initEvents.call(this);
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);

        }, this);
    },

    // private
    onMouseDown : function(e, t){
        if(e.button === 0 && t.className == 'x-grid3-row-radioselector'){ // Only fire if left-click
            e.stopEvent();
            var row = e.getTarget('.x-grid3-row');
            if(row){
                var index = row.rowIndex;
                if(this.isSelected(index)){
                    this.deselectRow(index);
                }else{
                    this.selectRow(index, true);
                }
            }
        }
    },

    // private
    renderer : function(v, p, record){
        return '<div class="x-grid3-row-radioselector">&#160;</div>';
    }
});