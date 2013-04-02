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
    var formFilterValues;
    var formSubmitted = false;
    var serverUrl = Ext.get('serverUrl').getAttribute('value');
    var filterMap = {}; //order of member is the same as the form layout
    filterMap.disease = Ext.get('disease').getAttribute('value');
    filterMap.aliquotId = Ext.get('aliquotId').getAttribute('value');
    filterMap.aliquotUUID = Ext.get('aliquotUUID').getAttribute('value');
    filterMap.molecule = Ext.get('molecule').getAttribute('value');
    filterMap.center = Ext.get('center').getAttribute('value');
    filterMap.dateFrom = Ext.get('dateFrom').getAttribute('value');
    filterMap.dataType = Ext.get('dataType').getAttribute('value');
    filterMap.dateTo = Ext.get('dateTo').getAttribute('value');


//Create the data store
    var bamTelemetryStore = new Ext.data.JsonStore({
        url: "bamTelemetryReport.json",
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 50, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'bamTelemetryData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'disease'},
            {name: 'center'},
            {name: 'dateReceived'},
            {name: 'bamFile'},
            {name: 'aliquotUUID'},
            {name: 'aliquotId'},
            {name: 'participantId'},
            {name: 'sampleId'},
            {name: 'molecule'},
            {name: 'dataType'},
            {name: 'fileSize'}
        ]
    });

//Create All Disease,Center,Platform Json store.
    var diseaseStore = new Ext.data.JsonStore({
        url: "bamTelemetryFilterData.json?filter=disease",
        root: 'diseaseData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('diseaseCombo').setValue(filterMap.disease);
        }},
        fields: ['id', 'text']
    });
    var centerStore = new Ext.data.JsonStore({
        url: "bamTelemetryFilterData.json?filter=center",
        root: 'centerData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('centerCombo').setValue(filterMap.center);
        }},
        fields: ['id', 'text']
    });
    var dataTypeStore = new Ext.data.JsonStore({
        url: "bamTelemetryFilterData.json?filter=dataType",
        root: 'dataTypeData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('dataTypeCombo').setValue(filterMap.dataType);
        }},
        fields: ['id', 'text']
    });
    var moleculeStore = new Ext.data.JsonStore({
        url: "bamTelemetryFilterData.json?filter=molecule",
        root: 'moleculeData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('moleculeCombo').setValue(filterMap.molecule);
        }},
        fields: ['id', 'text']
    });

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
        store: bamTelemetryStore,
        displayInfo: true,
        displayMsg: 'Displaying BAM File {0} - {1} of {2}',
        emptyMsg: "No BAM File to display",
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
            showFilterUrl('bamTelemetryReportDiv', serverUrl +
                '/datareports/bamTelemetryReport.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

//Create resize Panel
    var resizeGrid = new Ext.ux.PanelResizer({
        minHeight: 500
    });

//Create the Grid
    var bamTelemetryGrid = new Ext.grid.GridPanel({
        store: bamTelemetryStore,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: true},
            columns: [
                {header: 'Disease', width: 40, dataIndex: 'disease', tooltip: 'Disease / Study name'},
                {header: 'Center', width: 90, dataIndex: 'center', tooltip: 'Receiving Center'},
                {header: 'Date', width: 60, dataIndex: 'dateReceived', tooltip: 'Date Received', renderer: makeDate},
                {header: 'BAM File', width: 200, dataIndex: 'bamFile', tooltip: 'BAM File'},
                {header: 'UUID', width: 170, dataIndex: 'aliquotUUID', tooltip: 'Aliquot UUID'},
                {header: 'Aliquot ID', width: 170, dataIndex: 'aliquotId', tooltip: 'Aliquot Id'},
                {header: 'Participant ID', width: 80, dataIndex: 'participantId', tooltip: 'Participant ID'},
                {header: 'Sample ID', width: 105, dataIndex: 'sampleId', tooltip: 'Sample ID'},
                {header: 'Molecule', width: 47, dataIndex: 'molecule', tooltip: 'Molecule Type'},
                {header: 'Data Type', width: 55, dataIndex: 'dataType', tooltip: 'Data Type'},
                {header: 'File Size', width: 50, dataIndex: 'fileSize', tooltip: 'File Size', renderer: makeFileSize}
            ]}),
        stripeRows: true,
        forceFit: true,
        height: 500,
        width: 945,
        frame: true,
        loadMask: true,
        tbar: exports,
        bbar: paging
    });

//date renderer
    function makeDate(value) {
        var dt = new Date();
        dt = Date.parseDate(value, "Y-m-d");
        if (dt) {
            return dt.format("m/d/Y");
        } else {
            return "";
        }
    }

//file size renderer
    function makeFileSize(value) {
        var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        if (value == 0) return 'n/a';
        var i = parseInt(Math.floor(Math.log(value) / Math.log(1024)));
        return Math.round(value / Math.pow(1024, i), 2) + ' ' + sizes[i];
    }

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = bamTelemetryGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = bamTelemetryGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'bamTelemetryExport.htm',
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

    //Create comboBox for dataType
    var dataTypeCombo = new Ext.ux.form.LovCombo({
        id: 'dataTypeCombo',
        fieldLabel: 'Data Type',
        emptyText: 'Select data types...',
        name: 'dataType',
        store: dataTypeStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "dataTypeComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'dataType',
        editable: false,
        forceSelection: true
    });

    //Create comboBox for molecule
    var moleculeCombo = new Ext.ux.form.LovCombo({
        id: 'moleculeCombo',
        fieldLabel: 'Molecule',
        emptyText: 'Select molecules...',
        name: 'molecule',
        store: moleculeStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "moleculeComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'molecule',
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
            {width: 300, layout: 'form', items: [diseaseCombo,
                {id: 'aliquotIdTextField', fieldLabel: 'Aliquot Id', name: 'aliquotId', xtype: 'textfield', width: 200,
                    emptyText: "Search Aliquot Id...", value: filterMap.aliquotId},
                {id: 'aliquotUUID', fieldLabel: 'UUID', name: 'aliquotUUID', xtype: 'textfield', width: 200,
                    emptyText: "Enter an Aliquot UUID...", value: filterMap.aliquotUUID}]},
            {width: 300, layout: 'form', items: [centerCombo,
                {width: 250, layout: 'form', labelWidth: 65, items: [
                    {id: 'dateFrom', fieldLabel: 'Date From', name: 'dateFrom',
                        xtype: 'datefield', emptyText: "Choose a Date...", value: filterMap.dateFrom, width: 150}
                ]},
                moleculeCombo
            ]},
            {width: 300, layout: 'form', items: [dataTypeCombo,
                {width: 250, layout: 'form', labelWidth: 65, items: [
                    {id: 'dateTo', fieldLabel: 'Date To', name: 'dateTo',
                        xtype: 'datefield', emptyText: "Choose a Date...", value: filterMap.dateTo, width: 150}
                ]}
            ]}
        ],
        buttons: [
            {xtype: 'buttonplus', id: 'filterNow', text: 'Filter Now', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                bamTelemetryStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {xtype: 'buttonplus', id: 'clearFilters', text: 'Clear Filters', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                bamTelemetryStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 550
    });

    var panel = new Ext.Panel({
        title: 'BAM Telemetry Report',
        renderTo: 'bamTelemetryReportDiv',
        width: 945,
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
            {region: 'center', layout: 'fit', id: 'centre', items: [bamTelemetryGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    //Create a show/hide menu button on the toolbar
    var view = bamTelemetryGrid.getView();
    bamTelemetryGrid.getTopToolbar().add('-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('bamTelemetryReport.htm');
        }}
        , '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('bamTelemetryReportDiv', 'bam_telemetry_report');
        }
        }
    );
    bamTelemetryGrid.getTopToolbar().doLayout();

    bamTelemetryStore.on('beforeload', function () {
        bamTelemetryStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

}); //End of Ext.onReady