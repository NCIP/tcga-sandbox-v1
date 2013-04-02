/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function () {
    Ext.QuickTips.init();
    var formFilterValues;
    var formSubmitted = false;
    var serverUrl = Ext.get('serverUrl').getAttribute('value');
    var filterMap = {}; //order of member is the same as the form layout
    filterMap.disease = Ext.get('disease').getAttribute('value');
    filterMap.levelOne = Ext.get('levelOne').getAttribute('value');
    filterMap.aliquotId = Ext.get('aliquotId').getAttribute('value');
    filterMap.center = Ext.get('center').getAttribute('value');
    filterMap.levelTwo = Ext.get('levelTwo').getAttribute('value');
    filterMap.bcrBatch = Ext.get('bcrBatch').getAttribute('value');
    filterMap.platform = Ext.get('platform').getAttribute('value');
    filterMap.levelThree = Ext.get('levelThree').getAttribute('value');


    //Create the data store
    var aliquotStore = new Ext.data.JsonStore({
        url: "aliquotReport.json",
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 50, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'aliquotData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'disease'},
            {name: 'aliquotId'},
            {name: 'bcrBatch'},
            {name: 'center'},
            {name: 'platform'},
            {name: 'levelOne'},
            {name: 'levelTwo'},
            {name: 'levelThree'}
        ]
    });

    //Create All Disease,Center,Platform Json store.
    var diseaseStore = new Ext.data.JsonStore({
        url: "aliquotFilterData.json?filter=disease",
        root: 'diseaseData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('diseaseCombo').setValue(filterMap.disease);
        }},
        fields: ['id', 'text']
    });
    var centerStore = new Ext.data.JsonStore({
        url: "aliquotFilterData.json?filter=center",
        root: 'centerData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('centerCombo').setValue(filterMap.center);
        }},
        fields: ['id', 'text']
    });
    var platformStore = new Ext.data.JsonStore({
        url: "aliquotFilterData.json?filter=platform",
        root: 'platformData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('platformCombo').setValue(filterMap.platform);
        }},
        fields: ['id', 'text']
    });

    function levelStore(level, val) {
        return new Ext.data.JsonStore({
            url: "aliquotFilterData.json?filter=level",
            root: 'levelData',
            autoLoad: 'true',
            listeners: {load: function () {
                Ext.getCmp("level" + level + "Combo").setValue(val);
            }},
            fields: ['id', 'text']
        });
    }

    //Create comboBox for page selection
    var combo = new Ext.form.ComboBox({
        name: 'perpage',
        width: 50,
        store: new Ext.data.ArrayStore({
            fields: ['id'],
            data: [
                ['50'],
                ['500'],
                ['1000']
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
        store: aliquotStore,
        displayInfo: true,
        displayMsg: 'Displaying Aliquot ID {0} - {1} of {2}',
        emptyMsg: "No Aliquot ID to display",
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
            showFilterUrl('aliquotReportDiv', serverUrl +
                '/datareports/aliquotReport.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

    //Create resize Panel
    var resizeGrid = new Ext.ux.PanelResizer({
        minHeight: 500
    });

    //Create the Grid
    var aliquotGrid = new Ext.grid.GridPanel({
        store: aliquotStore,
        columns: [
            {header: 'Aliquot ID', width: 165, sortable: true, dataIndex: 'aliquotId', id: 'aliquotId',
                hideable: false, tooltip: 'Aliquot Id', renderer: function (val, mD, rec, row) {
                return '<span id="aliquotId' + row + '">' + val + '</span>';
            }},
            {header: 'Disease', width: 65, sortable: true, dataIndex: 'disease', tooltip: 'Disease / Study name'},
            {header: 'BCR Batch', width: 70, sortable: true, dataIndex: 'bcrBatch', tooltip: 'BCR Batch'},
            {header: 'Receiving Center', width: 150, sortable: true, dataIndex: 'center',
                tooltip: 'Receiving Center'},
            {header: 'Platform', width: 145, sortable: true, dataIndex: 'platform', tooltip: 'Platform'},
            {header: 'Level 1 Data', width: 85, sortable: true, renderer: level1Data, dataIndex: 'levelOne',
                tooltip: 'Level 1 Data'},
            {header: 'Level 2 Data', width: 85, sortable: true, renderer: level2Data, dataIndex: 'levelTwo',
                tooltip: 'Level 2 Data'},
            {header: 'Level 3 Data', width: 85, sortable: true, renderer: level3Data, dataIndex: 'levelThree',
                tooltip: 'Level 3 Data'}
        ],
        stripeRows: true,
        forceFit: true,
        autoExpandColumn: 'aliquotId',
        height: 500,
        width: 925,
        frame: true,
        loadMask: true,
        tbar: exports,
        bbar: paging
    });

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = aliquotGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = aliquotGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'aliquotExport.htm',
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

    //levelData renderer: display Missing in red
    function levelData(value, aliquotId, level) {
        if (value == 'Not Submitted') {
            return value;
        } else return "<a style='cursor:pointer;' " +
            "onClick=\"showAliquotArchive('" + aliquotId + "'," + level + ")\">" + value + "</a>";
    }

    //level1 renderer
    function level1Data(value, metaData, record, rowIndex, colIndex) {
        return levelData(value, record.get('aliquotId'), 1);
    }

    //level2 renderer
    function level2Data(value, metaData, record, rowIndex, colIndex) {
        return levelData(value, record.get('aliquotId'), 2);
    }

    //level3 renderer
    function level3Data(value, metaData, record, rowIndex, colIndex) {
        return levelData(value, record.get('aliquotId'), 3);
    }

    //Create comboBox for disease
    var diseaseCombo = new Ext.ux.form.LovCombo({
        id: 'diseaseCombo',
        fieldLabel: 'Disease',
        emptyText: 'Select diseases...',
        name: 'disease',
        store: diseaseStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "diseaseComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'disease',
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

    //Create comboBox for platform
    var platformCombo = new Ext.ux.form.LovCombo({
        id: 'platformCombo',
        fieldLabel: 'Platform',
        emptyText: 'Select platforms...',
        name: 'platform',
        store: platformStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "platformComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'platform',
        editable: false,
        forceSelection: true
    });

    //Create comboBox for levels
    function lCombo(level, levelName, val) {
        return new Ext.ux.form.LovCombo({
            id: "level" + level + "Combo",
            fieldLabel: "Level " + level,
            emptyText: "Select level " + level + "...",
            name: "level" + levelName,
            store: levelStore(level, val),
            mode: 'local',
            width: 200,
            triggerAction: 'all',
            triggerConfig: {tag: "img", id: "level" + level + "Trigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
            displayField: 'text',
            valueField: 'id',
            hiddenName: "level" + levelName,
            editable: false,
            forceSelection: true
        });
    }

    var filterBox = new Ext.form.FormPanel({
        frame: true,
        id: 'filterBox',
        labelWidth: 65,
        margins: '3 3 0 0',
        layout: 'column',
        items: [
            {width: 300, layout: 'form', items: [diseaseCombo, lCombo('1', 'One', filterMap.levelOne),
                {id: 'aliquotIdTextField', fieldLabel: 'Aliquot Id', name: 'aliquotId', xtype: 'textfield', width: 200,
                    emptyText: "Search Aliquot Id...", value: filterMap.aliquotId}]},
            {width: 300, layout: 'form', items: [centerCombo, lCombo('2', 'Two', filterMap.levelTwo),
                {id: 'bcrBatchTextField', fieldLabel: 'BCR Batch', name: 'bcrBatch', xtype: 'textfield',
                    emptyText: "Type a BCR Batch...", value: filterMap.bcrBatch}]},
            {width: 300, layout: 'form', items: [platformCombo, lCombo('3', 'Three', filterMap.levelThree)]}
        ],
        buttons: [
            {xtype: 'buttonplus', id: 'filterNow', text: 'Filter Now', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                aliquotStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {xtype: 'buttonplus', id: 'clearFilters', text: 'Clear Filters', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                aliquotStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 550
    });

    var panel = new Ext.Panel({
        title: 'Aliquot Report',
        renderTo: 'aliquotReportDiv',
        width: 935,
        height: 550,
        iconCls: 'icon-report',
        layout: 'border',
        bodyStyle: 'z-index: 0;',
        frame: true,
        plugins: [resizePanel],
        items: [
            {
                title: 'Filters Extended',
                region: 'north',
                height: 150,
                minSize: 150,
                maxSize: 150,
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
            {region: 'center', layout: 'fit', id: 'centre', items: [aliquotGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    //Create a show/hide menu button on the toolbar
    var view = aliquotGrid.getView();
    aliquotGrid.getTopToolbar().add('-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('aliquotReport.htm');
        }}, '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('aliquotReportDiv', 'exp_aliquot_report');
        }});
    aliquotGrid.getTopToolbar().doLayout();

    aliquotStore.on('beforeload', function () {
        aliquotStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

}); //End of Ext.onReady

function showAliquotArchive(param1, param2) {

    //Create the json data reader
    var archiveReader = new Ext.data.JsonReader({
        root: 'aliquotArchiveData',
        fields: [
            {name: 'archiveName'},
            {name: 'fileName'},
            {name: 'fileUrl'}
        ]
    });

    //Create the data grouping store
    var archiveStore = new Ext.data.GroupingStore({
        reader: archiveReader,
        url: "aliquotArchive.json?aliquotId=" + param1 + "&level=" + param2,
        autoLoad: 'true',
        sortInfo: {field: 'archiveName', direction: "ASC"},
        groupField: 'archiveName'
    });

    //Create the Archive Grid
    var archiveGrid = new Ext.grid.GridPanel({
        store: archiveStore,
        renderTo: 'aliquotReportDiv',
        columns: [
            {header: 'Archive Name', sortable: true, dataIndex: 'archiveName', id: 'archive'},
            {header: 'File Name', width: 600, sortable: true, dataIndex: 'fileName', renderer: linkRender,
                id: 'fileName'}
        ],
        view: new Ext.grid.GroupingView({
            headersDisabled: true,
            forceFit: true,
            groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Files" : "File"]})'
        }),
        stripeRows: true,
        autoExpandColumn: 'fileName',
        height: 350,
        width: 600
    });

    //Create the window
    var win = new Ext.Window({
        title: "Aliquot Archive for Aliquot " + param1 + " and Level " + param2 + " Data",
        closable: true,
        width: 600,
        height: 350,
        modal: true,
        layout: 'fit',
        buttons: [
            {text: 'Close', handler: function () {
                win.hide();
            }}
        ]
    });

    function linkRender(value, metaData, record) {
        return "<a style='text-decoration:none;' " +
            "href='" + record.get('fileUrl') + "' target='_blank'>" + value + "</a>";
    }

    archiveGrid.getColumnModel().setHidden(0, true);
    win.add(archiveGrid);
    win.show();
}




