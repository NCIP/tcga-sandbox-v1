/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

Ext.onReady(function () {
    Ext.QuickTips.init();
    Ext.apply(Ext.QuickTips.getQuickTip(), {
        showDelay: 50,
        maxWidth: 150,
        trackMouse: true
    });

    var formFilterValues;
    var serverUrl = Ext.get('serverUrl').getAttribute('value');
    var filterMap = {}; //order of member is the same as the form layout
    filterMap.bcr = Ext.get('bcr').getAttribute('value');
    filterMap.center = Ext.get('center').getAttribute('value');
    filterMap.batch = Ext.get('batch').getAttribute('value');
    filterMap.plateId = Ext.get('plateId').getAttribute('value');

    var bcrStore = new Ext.data.JsonStore({
        url: "pendingUUIDFilterData.json?filter=bcr",
        root: 'bcrData',
        autoLoad: true,
        listeners: {load: function () {
            Ext.getCmp('bcrCombo').setValue(filterMap.bcr)
        }},
        fields: ['id', 'text']
    });

    var centerStore = new Ext.data.JsonStore({
        url: "pendingUUIDFilterData.json?filter=center",
        root: 'centerData',
        autoLoad: true,
        listeners: {load: function () {
            Ext.getCmp('centerCombo').setValue(filterMap.center)
        }},
        fields: ['id', 'text']
    });

    //Create the data store
    var pendingUUIDStore = new Ext.data.JsonStore({
        url: "pendingUUIDReport.json",
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 50, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'pendingUUIDData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'bcr'},
            {name: 'center'},
            {name: 'shippedDate'},
            {name: 'plateId'},
            {name: 'batchNumber'},
            {name: 'plateCoordinate'},
            {name: 'dccReceivedDate'},
            {name: 'uuid'},
            {name: 'bcrAliquotBarcode'},
            {name: 'sampleType'},
            {name: 'analyteType'},
            {name: 'portionNumber'},
            {name: 'vialNumber'},
            {name: 'itemType'}
        ]
    });

    //Create comboBox for page selection
    var combo = new Ext.form.ComboBox({
        name: 'perpage',
        width: 50,
        store: new Ext.data.ArrayStore({
            fields: ['id'],
            data: [
                ['50'],
                ['100'],
                ['200']
            ]}),
        mode: 'local',
        value: '50',
        listWidth: 40,
        triggerAction: 'all',
        displayField: 'id',
        valueField: 'id',
        editable: false,
        forceSelection: true
    });

    //Create paging Toolbar
    var paging = new Ext.PagingToolbar({
        pageSize: 50,
        store: pendingUUIDStore,
        displayInfo: true,
        displayMsg: 'Displaying data {0} - {1} of {2}',
        emptyMsg: "No data to display",
        items: ['-', 'Per Page ', combo]
    });

    combo.on('select', function (combo, record) {
        paging.pageSize = parseInt(record.get('id'), 10);
        paging.doLoad(paging.cursor);
    }, this);

    //Create exports Toolbar
    var exports = new tcga.dataReports.toolbar({
        exportUrlxl: function () {
            exportUrl("xl");
        },
        exportUrlcsv: function () {
            exportUrl("csv");
        },
        exportUrltab: function () {
            exportUrl("tab");
        },
        filterFunction: function () {
            showFilterUrl('pendingUUIDReportDiv', serverUrl +
                '/datareports/shipped-items-pending-bcr-data-submission.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

    //Create the Grid
    var pendingUUIDGrid = new Ext.grid.GridPanel({
        store: pendingUUIDStore,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: true},
            columns: [
                {header: 'BCR', dataIndex: 'bcr', width: 40, tooltip: 'BCR'},
                {header: 'Center', dataIndex: 'center', width: 90, tooltip: 'Center'},
                {header: 'UUID', dataIndex: 'uuid', width: 110, id: 'uuid', renderer: makeSeleniumId,
                    tooltip: 'UUID'},
                {header: 'Barcode', dataIndex: 'bcrAliquotBarcode', width: 110, tooltip: 'Barcode'},
                {header: 'Batch', dataIndex: 'batchNumber', width: 40, tooltip: 'Batch'},
                {header: 'Plate Id', dataIndex: 'plateId', width: 50, tooltip: 'Plate Id'},
                {header: 'Plate Coordinate', dataIndex: 'plateCoordinate', width: 40, tooltip: 'Plate Coordinate'},
                {header: 'Date Shipped', dataIndex: 'shippedDate', width: 70, renderer: makeDate,
                    tooltip: 'Date Shipped'},
                {header: 'Sample Type', dataIndex: 'sampleType', width: 60, tooltip: 'Sample Type'},
                {header: 'Analyte Type', dataIndex: 'analyteType', width: 60, tooltip: 'Analyte Type'},
                {header: 'Portion', dataIndex: 'portionNumber', width: 50, tooltip: 'Portion'},
                {header: 'Vial', dataIndex: 'vialNumber', width: 35, tooltip: 'Vial'},
                {header: 'Item type', dataIndex: 'itemType', width: 65, tooltip: 'Item type'}
            ]}),
        stripeRows: true,
        forceFit: true,
        autoExpandColumn: 'uuid',
        height: 715,
        width: 955,
        frame: true,
        loadMask: true,
        tbar: exports,
        bbar: paging
    });

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = pendingUUIDGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = pendingUUIDGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'pendingUUIDExport.htm',
            children: [
                {tag: 'input', type: 'hidden', name: 'exportType', value: type},
                {tag: 'input', type: 'hidden', name: 'dir', value: dir},
                {tag: 'input', type: 'hidden', name: 'sort', value: sort},
                {tag: 'input', type: 'hidden', name: 'cols', value: "" + cols},
                {tag: 'input', type: 'hidden', name: 'filterReq', value: Ext.util.Format.htmlEncode(Ext.util.JSON.encode(filterMap))},
                {tag: 'input', type: 'hidden', name: 'formFilter', value: Ext.util.Format.htmlEncode(Ext.util.JSON.encode(formFilterValues))}
            ]
        });
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
    }

    //selenium Id renderer
    function makeSeleniumId(val, mD, rec, row) {
        return '<span id="seleniumId' + row + '">' + val + '</span>';
    }

    //date renderer
    function makeDate(value) {
        var dt = new Date();
        dt = Date.parseDate(value, "Y-m-d H:i:s.u");
        if (dt) {
            return dt.format("m/d/Y");
        } else {
            return "";
        }
    }

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 715
    });

    //Create comboBox for bcr
    var bcrCombo = new Ext.ux.form.LovCombo({
        id: 'bcrCombo',
        fieldLabel: 'BCR',
        emptyText: 'Select bcr...',
        name: 'bcr',
        store: bcrStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "bcrComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'bcr',
        editable: false,
        forceSelection: true
    });

    //Create comboBox for center
    var centerCombo = new Ext.ux.form.LovCombo({
        id: 'centerCombo',
        fieldLabel: 'Center',
        emptyText: 'Select centers...',
        name: 'center',
        store: centerStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "centerComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'center',
        editable: false,
        forceSelection: true
    });

    var filterBox = new Ext.form.FormPanel({
        frame: true,
        id: 'filterBox',
        labelWidth: 65,
        margins: '3 3 0 0',
        layout: 'column',
        items: [
            {width: 350, layout: 'form', items: [bcrCombo,
                {id: 'batchTextField', fieldLabel: 'Batch', name: 'batch', xtype: 'textfield',
                    emptyText: "Type a Batch...", value: filterMap.batch}]},
            {width: 300, layout: 'form', items: [centerCombo,
                {id: 'plateIdTextField', fieldLabel: 'Plate Id', name: 'plateId', xtype: 'textfield',
                    emptyText: "Type a Plate Id...", value: filterMap.plateId}]}
        ],
        buttons: [
            {text: 'Filter Now', id: 'filterNowbtn', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                pendingUUIDStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {text: 'Clear Filters', id: 'clearFiltersbtn', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                pendingUUIDStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    var panel = new Ext.Panel({
        title: 'Shipped Items Pending BCR Data Submission Report',
        renderTo: 'pendingUUIDReportDiv',
        width: 955,
        height: 715,
        iconCls: 'icon-report',
        layout: 'border',
        bodyStyle: 'z-index: 0',
        frame: true,
        plugins: [resizePanel],
        items: [
            {
                title: 'Filters Extended',
                region: 'north',
                height: 120,
                minSize: 120,
                maxSize: 120,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                listeners: {collapse: toggleOff, expand: toggleOn},
                floatable: false,
                cmargins: '0 0 2 0',
                id: 'nord',
                layout: 'fit',
                split: true,
                items: [filterBox]
            },
            {region: 'center', layout: 'fit', id: 'centre', items: [pendingUUIDGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    //Create a show/hide menu button on the toolbar
    var view = pendingUUIDGrid.getView();
    pendingUUIDGrid.getTopToolbar().add('-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('shipped-items-pending-bcr-data-submission.htm');
        }}
        , '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('pendingUUIDReportDiv', 'pendingUUIDReport');
        }
        }
    );
    pendingUUIDGrid.getTopToolbar().doLayout();

    pendingUUIDStore.on('beforeload', function () {
        pendingUUIDStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

});//End of Ext.onReady
