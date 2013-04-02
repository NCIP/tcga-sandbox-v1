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

Ext.namespace('tcga.datareports.codetables');

//Create export url
tcga.datareports.codetables.exportUrl = function (grid, code, type) {
    var dir, sort;
    var sortState = grid.store.getSortState();
    if (sortState) {
        dir = sortState.direction;
        sort = sortState.field;
    }
    var form = Ext.DomHelper.append(document.body, {
        tag: 'form',
        method: 'POST',
        action: 'codeTablesExport.htm',
        children: [
            {tag: 'input', type: 'hidden', name: 'exportType', value: type},
            {tag: 'input', type: 'hidden', name: 'dir', value: dir},
            {tag: 'input', type: 'hidden', name: 'sort', value: sort},
            {tag: 'input', type: 'hidden', name: 'codeTablesReport', value: "" + code}
        ]
    });
    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form);
};


//Create export url
function exportUrl(type) {
    var dir, sort, cols;
    var sortState = pendingUUIDGrid.store.getSortState();
    if (sortState) {
        dir = sortState.direction;
        sort = sortState.field;
    }
    cols = pendingUUIDGrid.getColumnModel().getColumnsVisible(true, 'dataIndex');

}

//Create exports Toolbar
tcga.datareports.codetables.exports = function (grid, code) {
    return {
        menu: [
            {
                text: 'Excel',
                iconCls: 'icon-xl',
                handler: function () {
                    tcga.datareports.codetables.exportUrl(grid, code, "xl");
                }
            },
            {
                text: 'CSV',
                iconCls: 'icon-txt',
                handler: function () {
                    tcga.datareports.codetables.exportUrl(grid, code, "csv");
                }
            },
            {
                text: 'Tab-delimited',
                iconCls: 'icon-txt',
                handler: function () {
                    tcga.datareports.codetables.exportUrl(grid, code, "tab");
                }
            }
        ],
        text: 'Export Data',
        iconCls: 'icon-grid'
    };
};

tcga.datareports.codetables.pagingToolbar = function (config) {
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
            ]
        }),
        mode: 'local',
        value: '25',
        listWidth: 40,
        triggerAction: 'all',
        displayField: 'id',
        valueField: 'id',
        editable: false,
        forceSelection: true
    });

    var paging = new Ext.PagingToolbar({
        pageSize: 25,
        store: config.store,
        displayInfo: true,
        displayMsg: config.displayMsg,
        emptyMsg: config.emptyMsg,
        items: ['-', 'Per Page ', combo]
    });

    combo.on('select', function (combo, record) {
        paging.pageSize = parseInt(record.get('id'), 10);
        paging.doLoad(paging.cursor);
    }, this);

    return paging;
};

tcga.datareports.codetables.codeReportPanelBorder = true;
tcga.datareports.codetables.codeReportPanelWidth = 955;

tcga.datareports.codetables.codeReportPanel = function (config) {
    var codeReportStore = new Ext.data.JsonStore({
        url: "codeTablesReport.json?codeTablesReport=" + config.code,
        //		url: 'json/' + config.code + '.sjson',
        remoteSort: (config.pagingToolbar ? true : false),
        autoLoad: (config.pagingToolbar ? {params: {start: 0, limit: 25}} : true),
        root: config.storeRoot,
        totalProperty: 'totalCount',
        fields: config.storeFields
    });

    if (config.pagingToolbar != false) {
        var paging = tcga.datareports.codetables.pagingToolbar({
            store: codeReportStore,
            displayMsg: config.displayMsg + ' {0} - {1} of {2}',
            emptyMsg: config.emptyMsg
        });
    }

    //Create the Grid
    var codeReportGrid = new Ext.grid.GridPanel({
        title: config.title,
        style: 'margin: auto;padding: 10px 0 10px 0;',
        store: codeReportStore,
        enableColumnHide: false,
        enableColumnMove: false,
        columns: config.columns,
        stripeRows: true,
        forceFit: true,
        height: 480,
        width: 730,
        frame: true,
        loadMask: true,
        bbar: (config.pagingToolbar != false) ? paging : null,
        tbar: new Ext.Toolbar()
    });

    var topToolbar = codeReportGrid.getTopToolbar();
    topToolbar.add({xtype: 'tbtext', text: 'Select a Code Table: '}, tcga.datareports.codetables.tableSelection(), '-',
        tcga.datareports.codetables.exports(codeReportGrid, config.code), '->',
        {text: 'Help', iconCls: 'icon-help', handler: function () {
            showHelp('codeTablesDiv', 'code_tables_report');
        }});
    topToolbar.doLayout();

    return codeReportGrid;
};

tcga.datareports.codetables.bcrBatchCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'BCR Batch',
        code: 'bcrBatchCode',
        pagingToolbar: true,
        storeRoot: 'bcrBatchCodeData',
        storeFields: [
            'bcrBatch',
            'studyCode',
            'studyName',
            'bcr'
        ],
        displayMsg: 'Displaying BCR Batch',
        emptyMsg: 'No BCR Batch to display',
        columns: [
            {
                header: 'BCR Batch',
                width: 100,
                sortable: true,
                dataIndex: 'bcrBatch',
                tooltip: 'BCR Batch', renderer: function (val, mD, rec, row) {
                return '<span id="bcrBatch' + row + '">' + val + '</span>';
            }
            },
            {
                header: 'Study Abbr.',
                width: 100,
                sortable: true,
                dataIndex: 'studyCode',
                tooltip: 'Study Abbreviation'
            },
            {
                header: 'Study Name',
                width: 300,
                sortable: true,
                dataIndex: 'studyName',
                tooltip: 'Study Name'
            },
            {
                header: 'BCR',
                width: 100,
                sortable: true,
                dataIndex: 'bcr',
                tooltip: 'BCR Short Name'
            }
        ]
    });
};

tcga.datareports.codetables.centerCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Center',
        code: 'centerCode',
        pagingToolbar: true,
        storeRoot: 'centerCodeData',
        storeFields: [
            'code',
            'centerName',
            'centerType',
            'centerDisplayName',
            'shortName'
        ],
        displayMsg: 'Displaying centers',
        emptyMsg: 'No centers to display',
        columns: [
            {header: 'Code', width: 50, sortable: true, dataIndex: 'code', tooltip: 'Code',
                renderer: function (val, mD, rec, row) {
                    return '<span id="centerCode' + row + '">' + val + '</span>';
                }},
            {header: 'Center Name', width: 150, sortable: true, dataIndex: 'centerName', tooltip: 'Center Name'},
            {header: 'Center Type', width: 100, sortable: true, dataIndex: 'centerType', tooltip: 'Center Type'},
            {header: 'Display Name', width: 250, sortable: true, dataIndex: 'centerDisplayName',
                tooltip: 'Center Display Name'},
            {header: 'Short Name', width: 100, sortable: true, dataIndex: 'shortName', tooltip: 'Center Short Name'}
        ]
    });
};

tcga.datareports.codetables.dataLevelCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Data Level',
        code: 'dataLevel',
        pagingToolbar: false,
        storeRoot: 'dataLevelData',
        storeFields: [
            'code',
            'definition'
        ],
        columns: [
            {header: 'Level Number', width: 100, sortable: true, dataIndex: 'code', tooltip: 'Code',
                renderer: function (val, mD, rec, row) {
                    return '<span id="levelNumberCode' + row + '">' + val + '</span>';
                }},
            {header: 'Definition', width: 300, sortable: true, dataIndex: 'definition', tooltip: 'Definition'}
        ]
    });
};

tcga.datareports.codetables.dataTypeCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Data Type',
        code: 'dataType',
        pagingToolbar: true,
        storeRoot: 'dataTypeData',
        storeFields: [
            'centerType',
            'displayName',
            'ftpDisplay',
            'available'
        ],
        displayMsg: 'Displaying data types',
        emptyMsg: 'No data types to display',
        columns: [
            {header: 'Center Type', width: 100, sortable: true, dataIndex: 'centerType', tooltip: 'Center Type',
                renderer: function (val, mD, rec, row) {
                    return '<span id="cnterTypeCode' + row + '">' + val + '</span>';
                }},
            {header: 'Display Name', width: 250, sortable: true, dataIndex: 'displayName', tooltip: 'Display Name'},
            {header: 'FTP Display', width: 100, sortable: true, dataIndex: 'ftpDisplay', tooltip: 'FTP Display'},
            {header: 'Available', width: 100, sortable: true, dataIndex: 'available', tooltip: 'Available'}
        ]
    });
};

tcga.datareports.codetables.diseaseStudyCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Disease Study',
        code: 'diseaseStudy',
        pagingToolbar: true,
        storeRoot: 'diseaseStudyData',
        storeFields: [
            'tumorName',
            'tumorDescription'
        ],
        displayMsg: 'Displaying studies',
        emptyMsg: 'No studies to display',
        columns: [
            {header: 'Study Abbreviation', width: 200, sortable: true, dataIndex: 'tumorName',
                tooltip: 'Study Abbreviation',
                renderer: function (val, mD, rec, row) {
                    return '<span id="tumorName' + row + '">' + val + '</span>';
                }},
            {header: 'Study Name', width: 400, sortable: true, dataIndex: 'tumorDescription',
                tooltip: 'Study Name'}
        ]
    });
};

tcga.datareports.codetables.platformCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Platform',
        code: 'platformCode',
        pagingToolbar: true,
        storeRoot: 'platformCodeData',
        storeFields: [
            'platformName',
            'platformAlias',
            'platformDisplayName',
            'available'
        ],
        displayMsg: 'Displaying platforms',
        emptyMsg: 'No platforms to display',
        columns: [
            {header: 'Platform Code', width: 200, sortable: true, dataIndex: 'platformName',
                tooltip: 'Platform Code',
                renderer: function (val, mD, rec, row) {
                    return '<span id="platformCode' + row + '">' + val + '</span>';
                }},
            {header: 'Platform Alias', width: 130, sortable: true, dataIndex: 'platformAlias',
                tooltip: 'Platform Alias'},
            {header: 'Platform Name', width: 300, sortable: true, dataIndex: 'platformDisplayName',
                tooltip: 'Platform Name'},
            {header: 'Available', width: 60, sortable: true, dataIndex: 'available', tooltip: 'Available'}
        ]
    });
};

tcga.datareports.codetables.portionAnalyteCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Portion Analyte',
        code: 'portionAnalyte',
        pagingToolbar: false,
        storeRoot: 'portionAnalyteData',
        storeFields: [
            'code',
            {name: 'definition', sortType: Ext.data.SortTypes.asUCText}
        ],
        columns: [
            {header: 'Code', width: 100, sortable: true, dataIndex: 'code', tooltip: 'Code',
                renderer: function (val, mD, rec, row) {
                    return '<span id="portionAnalyteCode' + row + '">' + val + '</span>';
                }},
            {header: 'Definition', width: 350, sortable: true, dataIndex: 'definition', tooltip: 'Definition'}
        ]
    });
};

tcga.datareports.codetables.sampleTypeCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Sample Type',
        code: 'sampleType',
        pagingToolbar: false,
        storeRoot: 'sampleTypeData',
        storeFields: [
            'sampleTypeCode',
            'definition',
            'shortLetterCode'
        ],
        columns: [
            {header: 'Code', width: 100, sortable: true, dataIndex: 'sampleTypeCode', tooltip: 'Code',
                renderer: function (val, mD, rec, row) {
                    return '<span id="sampleTypeCode' + row + '">' + val + '</span>';
                }},
            {header: 'Definition', width: 350, sortable: true, dataIndex: 'definition', tooltip: 'Definition'},
            {header: 'Short Letter Code', width: 150, sortable: true, dataIndex: 'shortLetterCode', tooltip: 'Short Letter Code'}
        ]
    });
};

tcga.datareports.codetables.tissueCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Tissue',
        code: 'tissue',
        pagingToolbar: false,
        storeRoot: 'tissueData',
        storeFields: [
            'tissue'
        ],
        columns: [
            {header: 'Tissue', width: 300, sortable: true, dataIndex: 'tissue', tooltip: 'Tissue',
                renderer: function (val, mD, rec, row) {
                    return '<span id="tissue' + row + '">' + val + '</span>';
                }}
        ]
    });
};

tcga.datareports.codetables.tissueSourceSiteCodePanel = function () {
    return tcga.datareports.codetables.codeReportPanel({
        title: 'Tissue Source Site',
        code: 'tissueSourceSite',
        pagingToolbar: true,
        storeRoot: 'tissueSourceSiteData',
        storeFields: [
            'code',
            'definition',
            'studyName',
            'bcr'
        ],
        displayMsg: 'Displaying TSS',
        emptyMsg: 'No TSS to display',
        columns: [
            {header: 'TSS Code', width: 60, sortable: true, dataIndex: 'code', tooltip: 'Tissue Source Site Code',
                renderer: function (val, mD, rec, row) {
                    return '<span id="tissueSourceSiteCode' + row + '">' + val + '</span>';
                }},
            {header: 'Source Site', width: 280, sortable: true, dataIndex: 'definition', tooltip: 'Source Site'},
            {header: 'Study Name', width: 200, sortable: true, dataIndex: 'studyName', tooltip: 'Study Name'},
            {header: 'BCR', width: 65, sortable: true, dataIndex: 'bcr', tooltip: 'BCR'}
        ]
    });
};

// The store of code tables
tcga.datareports.codetables.listStore = new Ext.data.ArrayStore({
    fields: ['id', 'fcn'],
    data: [
        ['BCR Batch', tcga.datareports.codetables.bcrBatchCodePanel],
        ['Center', tcga.datareports.codetables.centerCodePanel],
        ['Data Level', tcga.datareports.codetables.dataLevelCodePanel],
        ['Data Type', tcga.datareports.codetables.dataTypeCodePanel],
        ['Disease Study', tcga.datareports.codetables.diseaseStudyCodePanel],
        ['Platform', tcga.datareports.codetables.platformCodePanel],
        ['Portion Analyte', tcga.datareports.codetables.portionAnalyteCodePanel],
        ['Sample Type', tcga.datareports.codetables.sampleTypeCodePanel],
        ['Tissue', tcga.datareports.codetables.tissueCodePanel],
        ['Tissue Source Site', tcga.datareports.codetables.tissueSourceSiteCodePanel]
    ]
});

//Create table selection Toolbar
tcga.datareports.codetables.tableSelection = function () {
    return {
        xtype: 'combo',
        id: 'selectReport',
        width: 150,
        store: tcga.datareports.codetables.listStore,
        mode: 'local',
        listWidth: 150,
        triggerAction: 'all',
        triggerConfig: {tag: "img", id: "selectReportComboTrigger", src: Ext.BLANK_IMAGE_URL, cls: "x-form-trigger " + this.triggerClass},
        tpl: '<tpl for="."><div id="selectReport{[xindex]}" class="x-combo-list-item">{id}</div></tpl>',
        displayField: 'id',
        valueField: 'id',
        editable: false,
        forceSelection: true,
        listeners: {
            select: function (combo, rec) {
                var reportContainer = Ext.getCmp('codeReportTableContainer');
                reportContainer.removeAll();
                reportContainer.add(rec.get('fcn')());
                reportContainer.doLayout();
                Ext.getCmp('selectReport').setValue(rec.get('id'));
            }
        }
    };
};

tcga.datareports.codetables.init = function () {
    Ext.QuickTips.init();

    // Check for the codeTable param to figure out whether there has been a request to start with a particular table
    var codeTableSelection = tcga.util.getUrlParam('codeTable');
    var initialTable = tcga.datareports.codetables.bcrBatchCodePanel;
    if (codeTableSelection == '') {
        codeTableSelection = 'BCR Batch';
    }
    var codeTableSelected = tcga.datareports.codetables.listStore.find('id', codeTableSelection);
    if (codeTableSelected != -1) {
        initialTable = tcga.datareports.codetables.listStore.getAt(codeTableSelected).get('fcn');
        new Ext.Panel({
            id: 'codeReportTableContainer',
            renderTo: 'codeTablesDiv',
            border: false,
            width: 900,
            autoHeight: true,
            items: [initialTable()]
        });
        Ext.getCmp('selectReport').setValue(codeTableSelection);
    } else {
        var goodParams = '';
        initialTable = undefined;
        tcga.datareports.codetables.listStore.each(function (model) {
            var codeTable = model.get('id');
            goodParams += '<li><a href="codeTablesReport.htm?codeTable=' + codeTable + '">'
                + codeTable + '</a></li>';
        });
        Ext.get('codeTablesDiv').update('<p>The parameter: <b>' + codeTableSelection
            + '</b> is not a valid code table.</p><p>Below is a list of valid code table parameters:<br/><ul>'
            + goodParams
            + '</ul></p>');
    }
};

Ext.onReady(tcga.datareports.codetables.init);
