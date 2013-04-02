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
    var formFilterValues = null;
    var serverUrl = Ext.get('serverUrl').getAttribute('value');
    var filterCenterEmail = Ext.get('centerEmail').getAttribute('value');
    var filterMap = {}; //order of member is the same as the form layout
    filterMap.disease = Ext.get('disease').getAttribute('value');
    filterMap.center = Ext.get('center').getAttribute('value');
    filterMap.portionAnalyte = Ext.get('portionAnalyte').getAttribute('value');
    filterMap.platform = Ext.get('platform').getAttribute('value');
    filterMap.levelFourSubmitted = Ext.get('levelFourSubmitted').getAttribute('value');

    //Create the data store
    var sampleSummaryStore = new Ext.data.JsonStore({
        url: "sampleSummaryReport.json?centerEmail=" + filterCenterEmail,
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 25, "filterReq": Ext.util.JSON.encode(filterMap)}},
        root: 'sampleSummaryData',
        totalProperty: 'totalCount',
        fields: [
            {name: 'disease'},
            {name: 'center'},
            {name: 'portionAnalyte'},
            {name: 'platform'},
            {name: 'totalBCRSent'},
            {name: 'totalCenterSent'},
            {name: 'totalBCRUnaccountedFor'},
            {name: 'totalCenterUnaccountedFor'},
            {name: 'totalLevelOne'},
            {name: 'totalLevelTwo'},
            {name: 'totalLevelThree'},
            {name: 'levelFourSubmitted'}
        ]
    });

    //Create All Disease,Center,Platform Json store.
    var diseaseStore = new Ext.data.JsonStore({
        url: "sampleSummaryFilterData.json?filter=disease",
        root: 'diseaseData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('diseaseCombo').setValue(filterMap.disease)
        }},
        fields: ['id', 'text']
    });
    var centerStore = new Ext.data.JsonStore({
        url: "sampleSummaryFilterData.json?filter=center",
        root: 'centerData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('centerCombo').setValue(filterMap.center)
        }},
        fields: ['id', 'text']
    });
    var portionAnalyteStore = new Ext.data.JsonStore({
        url: "sampleSummaryFilterData.json?filter=portionAnalyte",
        root: 'portionAnalyteData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('portionAnalyteCombo').setValue(filterMap.portionAnalyte)
        }},
        fields: ['id', 'text']
    });
    var platformStore = new Ext.data.JsonStore({
        url: "sampleSummaryFilterData.json?filter=platform",
        root: 'platformData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('platformCombo').setValue(filterMap.platform)
        }},
        fields: ['id', 'text']
    });
    var level4Store = new Ext.data.JsonStore({
        url: "sampleSummaryFilterData.json?filter=level",
        root: 'levelData',
        autoLoad: 'true',
        listeners: {load: function () {
            Ext.getCmp('level4Combo').setValue(filterMap.levelFourSubmitted)
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
                ['25'],
                ['50'],
                ['100']
            ]}),
        mode: 'local',
        value: '25',
        listWidth: 40,
        triggerAction: 'all',
        displayField: 'id',
        valueField: 'id',
        editable: false,
        forceSelection: true
    });

    //Create paging Toolbar
    var paging = new Ext.PagingToolbar({
        pageSize: 25,
        store: sampleSummaryStore,
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
            showFilterUrl('sampleSummaryReportDiv', serverUrl +
                '/datareports/sampleSummaryReport.htm?' + Ext.urlEncode(formFilterValues));
        }
    });

    //Create the Grid
    var sampleSummaryGrid = new Ext.grid.GridPanel({
        store: sampleSummaryStore,
        columns: [
            {header: 'Disease', width: 55, sortable: true, dataIndex: 'disease', id: 'disease',
                tooltip: 'Cancer Type', renderer: function (val, mD, rec, row) {
                return '<span id="disease' + row + '">' + val + '</span>';
            }},
            {header: 'Center', width: 130, sortable: true, dataIndex: 'center', id: 'center',
                tooltip: 'Receiving Center'},
            {header: 'Portion Analyte', width: 50, sortable: true, dataIndex: 'portionAnalyte',
                id: 'portionAnalyte', tooltip: 'Portion Analyte'},
            {header: 'Platform', width: 130, sortable: true, dataIndex: 'platform', id: 'platform',
                tooltip: 'Platform'},
            {header: 'BCR Reported', width: 85, sortable: true,
                renderer: totalBCRData, dataIndex: 'totalBCRSent', tooltip: 'Sample IDs BCR Reported Sending to Center'},
            {header: 'DCC Received', width: 85, sortable: true, renderer: totalCenterData,
                dataIndex: 'totalCenterSent', tooltip: 'Sample IDs DCC Received from Center'},
            {header: 'BCR Missing', width: 75, sortable: true,
                renderer: totalCenterData, dataIndex: 'totalBCRUnaccountedFor',
                tooltip: 'Unaccounted for BCR Sample IDs that Center Reported'},
            {header: 'Center Missing', width: 85, sortable: true,
                renderer: totalBCRData, dataIndex: 'totalCenterUnaccountedFor',
                tooltip: 'Unaccounted for Center Sample IDs that BCR Reported'},
            {header: 'Level 1', width: 50, sortable: true, renderer: totalCenterData,
                dataIndex: 'totalLevelOne', tooltip: 'Sample IDs with Level 1 Data'},
            {header: 'Level 2', width: 50, sortable: true, renderer: totalCenterData,
                dataIndex: 'totalLevelTwo', tooltip: 'Sample IDs with Level 2 Data'},
            {header: 'Level 3', width: 50, sortable: true, renderer: totalCenterData,
                dataIndex: 'totalLevelThree', tooltip: 'Sample IDs with Level 3 Data'},
            {header: 'Level 4', width: 50, sortable: true, renderer: level4Data,
                dataIndex: 'levelFourSubmitted', tooltip: 'Level 4 Submitted (Y/N)'}
        ],
        stripeRows: true,
        forceFit: true,
        autoExpandColumn: 'disease',
        height: 500,
        width: 945,
        frame: true,
        loadMask: true,
        tbar: exports,
        bbar: paging
    });

    //Create export url
    function exportUrl(type) {
        var dir, sort, cols;
        var sortState = sampleSummaryGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        cols = sampleSummaryGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'sampleSummaryExport.htm',
            children: [
                {tag: 'input', type: 'hidden', name: 'exportType', value: type},
                {tag: 'input', type: 'hidden', name: 'dir', value: dir},
                {tag: 'input', type: 'hidden', name: 'sort', value: sort},
                {tag: 'input', type: 'hidden', name: 'cols', value: "" + cols},
                {tag: 'input', type: 'hidden', name: 'filterCenterEmail', value: "" + filterCenterEmail},
                {tag: 'input', type: 'hidden', name: 'filterReq', value: Ext.util.Format.htmlEncode(Ext.util.JSON.encode(filterMap))},
                {tag: 'input', type: 'hidden', name: 'formFilter', value: Ext.util.Format.htmlEncode(Ext.util.JSON.encode(formFilterValues))}
            ]
        });
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
    }

    //totalBCRData renderer: display total as links
    function totalBCRData(value, metaData, record, rowIndex, colIndex) {
        if (value == 0) {
            return value;
        } else {
            var colId = sampleSummaryGrid.getColumnModel().getDataIndex(colIndex);
            var colName = sampleSummaryGrid.getColumnModel().getColumnTooltip(colIndex);
            var disease = record.get('disease');
            var center = record.get('center');
            var portionAnalyte = record.get('portionAnalyte');
            var platform = record.get('platform');
            var bcr = true;
            return "<a style='cursor:pointer;' " +
                "onClick=\"showSampleDetailed('" + colId + "','" + colName + "','" + disease + "','" + center + "','" +
                portionAnalyte + "','" + platform + "'," + bcr + ")\">" + value + "</a>";
        }
    }

    //totalCenterData renderer: display total as links
    function totalCenterData(value, metaData, record, rowIndex, colIndex) {
        if (value == 0) {
            return value;
        } else {
            var colId = sampleSummaryGrid.getColumnModel().getDataIndex(colIndex);
            var colName = sampleSummaryGrid.getColumnModel().getColumnTooltip(colIndex);
            var disease = record.get('disease');
            var center = record.get('center');
            var portionAnalyte = record.get('portionAnalyte');
            var platform = record.get('platform');
            var bcr = false;
            return "<a style='cursor:pointer;' " +
                "onClick=\"showSampleDetailed('" + colId + "','" + colName + "','" + disease + "','" +
                center + "','" + portionAnalyte + "','" + platform + "'," + bcr + ")\">" + value + "</a>";
        }
    }

    //level4 renderer: display Y* in red
    function level4Data(value) {
        if (value != "Y*") {
            return value;
        } else {
            return "<span style='color:red'>" + value + "</span>";
        }
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
        lazyRender: true,
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

    //Create comboBox for portionAnalyte
    var portionAnalyteCombo = new Ext.ux.form.LovCombo({
        id: 'portionAnalyteCombo',
        fieldLabel: 'Portion Analyte',
        emptyText: 'Select portion analytes...',
        name: 'portionAnalyte',
        store: portionAnalyteStore,
        mode: 'local',
        width: 200,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "portionAnalyteComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'portionAnalyte',
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

    //Create comboBox for level4
    var level4Combo = new Ext.ux.form.LovCombo({
        id: 'level4Combo',
        fieldLabel: 'Level 4 Submitted',
        emptyText: 'Select level 4...',
        name: 'level4',
        store: level4Store,
        mode: 'local',
        width: 150,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "level4ComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        displayField: 'text',
        valueField: 'id',
        hiddenName: 'levelFourSubmitted',
        editable: false,
        forceSelection: true
    });

    var filterBox = new Ext.form.FormPanel({
        frame: true,
        margins: '3 3 0 0',
        layout: 'column',
        items: [
            {width: 312, layout: 'form', labelWidth: 67, items: [diseaseCombo, centerCombo]},
            {width: 332, layout: 'form', labelWidth: 97, items: [portionAnalyteCombo, platformCombo]},
            {width: 272, layout: 'form', labelWidth: 107, items: [level4Combo]}
        ],
        buttons: [
            {xtype: 'buttonplus', id: 'filterNow', text: 'Filter Now', handler: function () {
                formFilterValues = filterBox.getForm().getFieldValues();
                sampleSummaryStore.load({params: {start: 0, limit: paging.pageSize}});
            }},
            {xtype: 'buttonplus', id: 'clearFilters', text: 'Clear Filters', handler: function () {
                filterBox.getForm().clear();
                formFilterValues = filterBox.getForm().getFieldValues();
                sampleSummaryStore.load({params: {start: 0, limit: paging.pageSize}});
            }}
        ]
    });

    //Create resize Panel
    var resizePanel = new Ext.ux.PanelResizer({
        minHeight: 550
    });

    var panel = new Ext.Panel({
        title: 'Sample-Counts Summary Report',
        renderTo: 'sampleSummaryReportDiv',
        width: 945,
        height: 550,
        iconCls: 'icon-report',
        layout: 'border',
        bodyStyle: 'z-index: 0',
        frame: true,
        plugins: [resizePanel],
        items: [
            {
                title: 'Filters Extended',
                region: 'north',
                height: 125,
                minSize: 125,
                maxSize: 125,
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
            {region: 'center', layout: 'fit', id: 'centre', items: [sampleSummaryGrid]}
        ]
    });

    function toggleOff() {
        Ext.getCmp('fToggle').toggle(false);
    }

    function toggleOn() {
        Ext.getCmp('fToggle').toggle(true);
    }

    //Create a show/hide menu button on the toolbar
    var view = sampleSummaryGrid.getView();
    sampleSummaryGrid.getTopToolbar().add('-',
        {iconCls: 'x-cols-icon', text: 'Show/Hide Columns', menu: view.colMenu}, '-',
        {text: 'Reset Table', iconCls: 'icon-reset', handler: function () {
            ahref('sampleSummaryReport.htm');
        }}, '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('sampleSummaryReportDiv', 'count_summary_report');
        }});
    sampleSummaryGrid.getTopToolbar().doLayout();

    sampleSummaryStore.on('beforeload', function () {
        sampleSummaryStore.baseParams = {
            "formFilter": Ext.util.JSON.encode(formFilterValues)
        };
    });

});//End of Ext.onReady

function showSampleDetailed(colId, colName, disease, center, portionAnalyte, platform, bcr) {

    //Create the json data store
    var sampleDetailedStore = new Ext.data.JsonStore({
        url: "sampleDetailedReport.json?cols=" + colId + "&disease=" + disease + "&center=" + center +
            "&portionAnalyte=" + portionAnalyte + "&platform=" + platform + "&bcr=" + bcr,
        remoteSort: true,
        autoLoad: {params: {start: 0, limit: 50}},
        root: 'sampleData',
        totalProperty: 'sampleTotal',
        fields: [
            {name: 'name'},
            {name: 'sampleDate'}
        ]
    });

    //Create comboBox for page selection
    var combo2 = new Ext.form.ComboBox({
        name: 'perpage2',
        width: 50,
        store: new Ext.data.ArrayStore({
            fields: ['id'],
            data: [
                ['50'],
                ['100'],
                ['500']
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
    var paging2 = new Ext.PagingToolbar({
        pageSize: 50,
        store: sampleDetailedStore,
        displayInfo: true,
        displayMsg: 'Displaying sample {0} - {1} of {2}',
        emptyMsg: "No sample to display",
        items: ['-', 'Per Page ', combo2]
    });

    combo2.on('select', function (combo, record) {
        paging2.pageSize = parseInt(record.get('id'), 10);
        paging2.doLoad(paging2.cursor);
    }, this);

    //Create exports Toolbar
    var exports2 = new Ext.Toolbar({
        items: [
            {
                menu: [
                    {text: 'Excel', iconCls: 'icon-xl', handler: function () {
                        exportUrl2("xl");
                    }},
                    {text: 'CSV', iconCls: 'icon-txt', handler: function () {
                        exportUrl2("csv");
                    }},
                    {text: 'Tab-delimited', iconCls: 'icon-txt', handler: function () {
                        exportUrl2("tab");
                    }}
                ],
                text: 'Export Data',
                iconCls: 'icon-grid'
            }
        ]
    });


    var dateHeader;
    if (bcr == true) {
        dateHeader = "Ship Date";
    } else {
        dateHeader = "Date Received";
    }

    //Create the sampleDetailed Grid
    var sampledDetailedGrid = new Ext.grid.GridPanel({
        store: sampleDetailedStore,
        renderTo: 'sampleSummaryReportDiv',
        columns: [
            {header: 'Samples', width: 250, sortable: true, dataIndex: 'name'},
            {header: dateHeader, width: 350, sortable: true, dataIndex: 'sampleDate',
                renderer: function (val) {
                    return '<span ext:qtip="' + val + '">' + val + '</span>';
                }
            }
        ],
        stripeRows: true,
        title: "Disease: " + disease + ", Center: " + center +
            ", Portion Analyte: " + portionAnalyte + ", Platform: " + platform,
        height: 450,
        width: 650,
        forceFit: true,
        loadMask: true,
        tbar: exports2,
        bbar: paging2
    });

    //date renderer
    function makeDate(value) {
        var dt = new Date();
        dt = Date.parseDate(value, "Y-m-d H:i:s.u");
        if (dt) {
            return dt.format("m/d/Y H:i");
        } else {
            return "";
        }
    }

    //Create export url
    function exportUrl2(type) {
        var dir, sort;
        var sortState = sampledDetailedGrid.store.getSortState();
        if (sortState) {
            dir = sortState.direction;
            sort = sortState.field;
        }
        var form = Ext.DomHelper.append(document.body, {
            tag: 'form',
            method: 'POST',
            action: 'sampleDetailedExport.htm',
            children: [
                {tag: 'input', type: 'hidden', name: 'exportType', value: type},
                {tag: 'input', type: 'hidden', name: 'dir', value: dir},
                {tag: 'input', type: 'hidden', name: 'sort', value: sort},
                {tag: 'input', type: 'hidden', name: 'cols', value: "" + colId},
                {tag: 'input', type: 'hidden', name: 'disease', value: "" + disease},
                {tag: 'input', type: 'hidden', name: 'center', value: "" + center},
                {tag: 'input', type: 'hidden', name: 'portionAnalyte', value: "" + portionAnalyte},
                {tag: 'input', type: 'hidden', name: 'platform', value: "" + platform},
                {tag: 'input', type: 'hidden', name: 'bcr', value: "" + bcr}
            ]
        });
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);
    }

    //Create the window
    var win = new Ext.Window({
        title: colName,
        closable: true,
        width: 650,
        height: 450,
        modal: true,
        layout: 'fit',
        buttons: [
            {text: 'Close', handler: function () {
                win.hide();
            }}
        ]
    });
    win.add(sampledDetailedGrid);
    win.show();
}




