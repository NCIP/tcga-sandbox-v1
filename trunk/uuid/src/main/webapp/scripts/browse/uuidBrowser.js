Ext.namespace('tcga.uuid');

tcga.uuid.browser = function () {
    var currentHighlight = [];
    var uuidSelected;
    var browseIds = {
        Participant: {
            title: 'participantTitle',
            grid: 'participantGrid',
            next: ['Sample']
        },
        Sample: {
            title: 'sampleTitle',
            grid: 'sampleGrid',
            next: ['Portion']
        },
        Portion: {
            title: 'portionTitle',
            grid: 'portionGrid',
            next: ['Analyte', 'Slide']
        },
        Analyte: {
            title: 'analyteTitle',
            grid: 'analyteGrid',
            next: ['Aliquot']
        },
        Aliquot: {
            title: 'aliquotTitle',
            grid: 'aliquotGrid',
            next: null
        },
        Slide: {
            title: 'slideTitle',
            grid: 'slideGrid',
            next: null
        }
    };

    var highlightGrid = function (titleId, gridBodyId) {
        if (currentHighlight.length != 0) {
            Ext.getCmp(currentHighlight[0]).body.removeClass('highlightGridTitleBackground');
            Ext.getCmp(currentHighlight[0]).body.addClass('lowlightGridTitleBackground');
        }

        currentHighlight = [titleId, gridBodyId];
        Ext.getCmp(titleId).body.removeClass('lowlightGridTitleBackground');
        Ext.getCmp(titleId).body.addClass('highlightGridTitleBackground');
    }

    var storeFields = [
        'uuid', 'parentUUID', 'barcode', 'uuidType', 'disease', 'receivingCenter', 'participantId',
        'sampleType', 'vialId', 'portionId', 'analyteType', 'tissueSourceSite', 'slide', 'platform',
        'plateId', 'shipped', 'shippedDate'
    ];

    var aliquotStore = new Ext.data.JsonStore({
        storeId: 'aliquotUuidResultsStore',
        url: 'aliquotUuid.json',
        root: 'aliquotUuidData',
        totalProperty: 'totalCount',
        baseParams: {start: 0, limit: 15},
        fields: storeFields
    });

    aliquotStore.on('beforeload', function () {
        aliquotStore.baseParams = {"parentUUID": uuidSelected};
    });

    var aliquotStorePaging = new Ext.PagingToolbar({
        id: 'aliquotStorePagingTb',
        pageSize: 15,
        store: aliquotStore,
        displayInfo: true,
        displayMsg: 'Displaying aliquot {0} - {1} of {2}',
        emptyMsg: "No aliquots to display"
    });

    var uuidBrowseResultsStore = new Ext.data.JsonStore({
        storeId: 'uuidBrowseResultsStore',
        url: 'uuidParent.json',
        root: 'uuidParentData',
        fields: storeFields,
        listeners: {
            load: function (store, recs, opts) {
                participantStore.removeAll();
                sampleStore.removeAll();
                portionStore.removeAll();
                analyteStore.removeAll();
                slideStore.removeAll();
                aliquotStore.load({params: {start: 0, limit: 15, uuid: opts.params.uuid}});
                tcga.uuid.browser.showWin();

                store.each(function (rec) {
                    switch (rec.get('uuidType')) {
                        case 'Participant':
                            participantStore.add(rec);
                            break;
                        case 'Sample':
                            sampleStore.add(rec);
                            break;
                        case 'Portion':
                            portionStore.add(rec);
                            break;
                        case 'Analyte':
                            analyteStore.add(rec);
                            break;
                        case 'Slide':
                            slideStore.add(rec);
                            break;
                    }
                });

                switch (opts.params.uuidType) {
                    case 'Participant':
                        tcga.uuid.navigator.setActiveNode('Participant');
                        highlightGrid(browseIds.Participant.title, browseIds.Participant.grid);
                        Ext.get(browseIds.Portion.title).hide();
                        uuidPortionGrid.hide();
                        Ext.get(browseIds.Analyte.title).hide();
                        uuidAnalyteGrid.hide();
                        Ext.get(browseIds.Aliquot.title).hide();
                        uuidAliquotGrid.hide();
                        Ext.get(browseIds.Slide.title).hide();
                        uuidSlideGrid.hide();
                        break;
                    case 'Sample':
                        tcga.uuid.navigator.setActiveNode('Sample');
                        Ext.get(browseIds.Portion.title).show();
                        uuidSampleGrid.getStore().filter('uuid', opts.params.uuid);
                        uuidPortionGrid.getStore().filter('parentUUID', opts.params.uuid);
                        uuidPortionGrid.show();
                        highlightGrid(browseIds.Sample.title, browseIds.Sample.grid);
                        Ext.get(browseIds.Analyte.title).hide();
                        uuidAnalyteGrid.hide();
                        Ext.get(browseIds.Aliquot.title).hide();
                        uuidAliquotGrid.hide();
                        Ext.get(browseIds.Slide.title).hide();
                        uuidSlideGrid.hide();
                        break;
                    case 'Portion':
                        tcga.uuid.navigator.setActiveNode('Portion');
                        uuidSampleGrid.getStore().filter('uuid', opts.params.parentUUID);
                        uuidPortionGrid.getStore().filter('uuid', opts.params.uuid);
                        Ext.get(browseIds.Portion.title).show();
                        uuidPortionGrid.show();
                        Ext.get(browseIds.Analyte.title).show();
                        uuidAnalyteGrid.getStore().filter('parentUUID', opts.params.uuid);
                        uuidAnalyteGrid.show();
                        Ext.get(browseIds.Slide.title).show();
                        uuidSlideGrid.getStore().filter('parentUUID', opts.params.uuid);
                        uuidSlideGrid.show();
                        highlightGrid(browseIds.Portion.title, browseIds.Portion.grid);
                        Ext.get(browseIds.Aliquot.title).hide();
                        uuidAliquotGrid.hide();
                        break;
                    case 'Analyte':
                        tcga.uuid.navigator.setActiveNode('Analyte');
                        uuidPortionGrid.getStore().filter('uuid', opts.params.parentUUID);
                        uuidSampleGrid.getStore().filter('uuid', uuidPortionGrid.getStore().getAt(0).get('parentUUID'));
                        Ext.get(browseIds.Portion.title).show();
                        uuidPortionGrid.show();
                        uuidAnalyteGrid.getStore().filter('uuid', opts.params.uuid);
                        Ext.get(browseIds.Analyte.title).show();
                        uuidAnalyteGrid.show();
                        uuidSlideGrid.getStore().filter('uuid', opts.params.uuid);
                        Ext.get(browseIds.Slide.title).show();
                        uuidSlideGrid.show();
                        Ext.get(browseIds.Aliquot.title).show();
                        uuidAliquotGrid.getStore().filter('parentUUID', opts.params.uuid);
                        uuidAliquotGrid.show();
                        highlightGrid(browseIds.Analyte.title, browseIds.Analyte.grid);
                        break;
                    case 'Slide':
                        tcga.uuid.navigator.setActiveNode('Slide');
                        uuidPortionGrid.getStore().filter('uuid', opts.params.parentUUID);
                        uuidSampleGrid.getStore().filter('uuid', uuidPortionGrid.getStore().getAt(0).get('parentUUID'));
                        Ext.get(browseIds.Portion.title).show();
                        uuidPortionGrid.show();
                        uuidAnalyteGrid.getStore().filter('uuid', opts.params.uuid);
                        Ext.get(browseIds.Analyte.title).show();
                        uuidAnalyteGrid.show();
                        uuidSlideGrid.getStore().filter('uuid', opts.params.uuid);
                        Ext.get(browseIds.Slide.title).show();
                        uuidSlideGrid.show();
                        highlightGrid(browseIds.Slide.title, browseIds.Slide.grid);
                        Ext.get(browseIds.Aliquot.title).hide();
                        uuidAliquotGrid.hide();
                        break;
                    case 'Aliquot':
                        tcga.uuid.navigator.setActiveNode('Aliquot');
                        uuidAliquotGrid.getStore().filter('uuid', opts.params.uuid);
                        Ext.get(browseIds.Aliquot.title).show();
                        uuidAliquotGrid.show();
                        uuidAnalyteGrid.getStore().filter('uuid', opts.params.parentUUID);
                        Ext.get(browseIds.Analyte.title).show();
                        uuidAnalyteGrid.show();
                        uuidSlideGrid.getStore().filter('uuid', opts.params.parentUUID);
                        Ext.get(browseIds.Slide.title).show();
                        uuidSlideGrid.show();
                        uuidPortionGrid.getStore().filter('uuid', uuidAnalyteGrid.getStore().getAt(0).get('parentUUID'));
                        Ext.get(browseIds.Portion.title).show();
                        uuidPortionGrid.show();
                        uuidSampleGrid.getStore().filter('uuid', uuidPortionGrid.getStore().getAt(0).get('parentUUID'));
                        highlightGrid(browseIds.Aliquot.title, browseIds.Aliquot.grid);
                }
            }
        }
    });

    var storeConfigs = {
        root: 'uuidParentData',
        fields: storeFields
    };

    var participantStore = new Ext.data.JsonStore(storeConfigs);
    var sampleStore = new Ext.data.JsonStore(storeConfigs);
    var portionStore = new Ext.data.JsonStore(storeConfigs);
    var analyteStore = new Ext.data.JsonStore(storeConfigs);
    var slideStore = new Ext.data.JsonStore(storeConfigs);

    var uuidParticipantGrid = new Ext.grid.GridPanel({
        id: browseIds.Participant.grid,
        viewConfig: { forceFit: true },
        loadMask: true,
        stripeRows: true,
        autoHeight: true,
        width: 676,
        frame: false,
        border: true,
        store: participantStore,
        enableColumnHide: false,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: false},
            columns: [
                {
                    header: 'UUID',
                    width: 215,
                    dataIndex: 'uuid',
                    renderer: function (val) {
                        return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Participant\', \'' + val + '\')">' + val + '</a>';
                    }
                },
                {
                    header: 'Barcode',
                    width: 160,
                    dataIndex: 'barcode'
                },
                {
                    id: 'participantNumber',
                    header: 'Participant Number',
                    width: 115,
                    dataIndex: 'participantId'
                },
                {
                    header: 'Disease',
                    width: 50,
                    dataIndex: 'disease'
                },
                {
                    id: 'tissueSourceSite',
                    header: 'Tissue Source Site',
                    width: 105,
                    dataIndex: 'tissueSourceSite'
                }
            ]})
    });


    var uuidSampleGrid = new Ext.grid.GridPanel({
        id: browseIds.Sample.grid,
        viewConfig: { forceFit: true },
        loadMask: true,
        stripeRows: true,
        autoHeight: true,
        width: 676,
        border: true,
        store: sampleStore,
        enableColumnHide: false,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: false},
            columns: [
                {
                    header: 'UUID',
                    width: 235,
                    dataIndex: 'uuid',
                    renderer: function (val) {
                        return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Sample\', \'' + val + '\')">' + val + '</a>';
                    }
                },
                {
                    header: 'Barcode',
                    width: 170,
                    dataIndex: 'barcode'
                },
                {
                    header: 'Sample Type',
                    width: 120,
                    dataIndex: 'sampleType'
                },
                {
                    header: 'Vial',
                    width: 40,
                    dataIndex: 'vialId'
                }
            ]})
    });

    var uuidPortionGrid = new Ext.grid.GridPanel({
        id: browseIds.Portion.grid,
        viewConfig: { forceFit: true },
        loadMask: true,
        stripeRows: true,
        autoHeight: true,
        width: 676,
        border: true,
        store: portionStore,
        enableColumnHide: false,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: false},
            columns: [
                {
                    header: 'UUID',
                    width: 235,
                    dataIndex: 'uuid',
                    renderer: function (val) {
                        return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Portion\', \'' + val + '\')">' + val + '</a>';
                    }
                },
                {
                    header: 'Barcode',
                    width: 170,
                    dataIndex: 'barcode'
                },
                {
                    header: 'Portion Code',
                    width: 160,
                    dataIndex: 'portionId'
                }
            ]})
    });

    var uuidAnalyteGrid = new Ext.grid.GridPanel({
        id: browseIds.Analyte.grid,
        viewConfig: { forceFit: true },
        loadMask: true,
        stripeRows: true,
        autoHeight: true,
        width: 528,
        style: 'margin-right: 3px;',
        border: true,
        store: analyteStore,
        enableColumnHide: false,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: false},
            columns: [
                {
                    id: 'uuid',
                    header: 'UUID',
                    width: 230,
                    dataIndex: 'uuid',
                    renderer: function (val) {
                        return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Analyte\', \'' + val + '\')">' + val + '</a>';
                    }
                },
                {
                    id: 'barcode',
                    header: 'Barcode',
                    width: 150,
                    dataIndex: 'barcode'
                },
                {
                    header: 'Analyte',
                    width: 120,
                    dataIndex: 'analyteType'
                }
            ]})
    });

    var uuidSlideGrid = new Ext.grid.GridPanel({
        id: browseIds.Slide.grid,
        viewConfig: { forceFit: true },
        loadMask: true,
        stripeRows: true,
        autoHeight: true,
        width: 408,
        border: true,
        store: slideStore,
        enableColumnHide: false,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: false},
            columns: [
                {
                    id: 'uuid',
                    header: 'UUID',
                    width: 205,
                    dataIndex: 'uuid',
                    renderer: function (val) {
                        return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Slide\', \'' + val + '\')">' + val + '</a>';
                    }
                },
                {
                    id: 'barcode',
                    header: 'Barcode',
                    width: 150,
                    dataIndex: 'barcode'
                }
            ]})
    });

    var uuidAliquotGrid = new Ext.grid.GridPanel({
        id: browseIds.Aliquot.grid,
        viewConfig: { forceFit: true },
        loadMask: true,
        stripeRows: true,
        width: 945,
        autoHeight: true,
        border: true,
        store: aliquotStore,
        enableColumnHide: false,
        bbar: aliquotStorePaging,
        colModel: new Ext.grid.ColumnModel({
            defaults: {sortable: false},
            columns: [
                {
                    id: 'uuid',
                    header: 'UUID',
                    width: 240,
                    dataIndex: 'uuid',
                    renderer: function (val) {
                        return '<a class="hand" onclick="tcga.uuid.browser.activateBrowseNode(\'Aliquot\', \'' + val + '\')">' + val + '</a>';
                    }
                },
                {
                    id: 'barcode',
                    header: 'Barcode',
                    width: 200,
                    dataIndex: 'barcode'
                },
                {
                    header: 'Plate ID',
                    width: 50,
                    dataIndex: 'plateId'
                },
                {
                    id: 'recCenter',
                    header: 'Receiving Center',
                    width: 160,
                    dataIndex: 'receivingCenter'
                },
                {
                    id: 'platform',
                    header: 'Platform',
                    width: 160,
                    dataIndex: 'platform'
                },
                {
                    id: 'shipped',
                    header: 'Shipped',
                    width: 50,
                    renderer: renderBoolean,
                    dataIndex: 'shipped'
                },
                {
                    id: 'shippedDate',
                    header: 'Shipped Date',
                    width: 85,
                    renderer: renderDate,
                    dataIndex: 'shippedDate'
                }
            ]})
    });

    function renderDate(value) {
        var dt = new Date();
        dt = Date.parseDate(value, "Y-m-d");
        if (dt) {
            return dt.format("m/d/Y");
        } else {
            return "";
        }
    }

    function renderBoolean(value) {
        return String.format("{0}", (value == 1) ? 'Yes' : 'No');
    }

    var navigatorPanel = tcga.uuid.navigator.getPanel();

    var browserWindow = new Ext.Window({
        id: 'uuidBrowserWindow',
        title: 'Biospecimen Metadata Browser Details',
        resizable: false,
        closable: true,
        autoscroll: true,
        closeAction: 'hide',
        width: 980,
        autoHeight: true,
        y: 122,
        bodyStyle: 'background-color: #ffffff;padding: 5px;',
        collapsible: false,
        modal: true,
        layout: 'column',
        buttons: [
            {text: 'Close', id: 'closeButton', handler: function () {
                Ext.getCmp('uuidBrowserWindow').hide();
            }}
        ],
        items: [
            {
                id: 'browsePanel',
                width: 960,
                style: 'padding: 5px;',
                border: false,
                hideBorders: true,
                items: [
                    {
                        border: false,
                        style: 'float:left;',
                        hideBorders: true,
                        items: [
                            { style: 'clear: both;width: 0;height: 10px;' },
                            {
                                id: browseIds.Participant.title,
                                cls: 'stdLabel',
                                html: 'Participant',
                                width: 676
                            },
                            uuidParticipantGrid,
                            { style: 'clear: both;width: 0;height: 10px;' },
                            {
                                id: browseIds.Sample.title,
                                style: 'margin-top: 10px;',
                                cls: 'stdLabel',
                                html: 'Sample',
                                width: 676
                            },
                            uuidSampleGrid,
                            { style: 'clear: both;width: 0;height: 10px;' },
                            {
                                id: browseIds.Portion.title,
                                style: 'margin-top: 10px;',
                                cls: 'stdLabel',
                                html: 'Portion',
                                width: 676
                            },
                            uuidPortionGrid
                        ]
                    },
                    {
                        border: false,
                        style: 'float:left;',
                        width: 240,
                        hideBorders: true,
                        items: navigatorPanel
                    },
                    {
                        style: 'clear: both;width: 0;height: 0;'
                    },
                    {
                        border: false,
                        layout: 'column',
                        hideBorders: true,
                        items: [
                            {
                                hideBorders: true,
                                items: [
                                    {
                                        id: browseIds.Analyte.title,
                                        style: 'margin: 10px 3px 0 0;width: 528px;',
                                        cls: 'stdLabel',
                                        html: 'Analyte'
                                    },
                                    uuidAnalyteGrid
                                ]
                            },
                            {
                                hideBorders: true,
                                style: 'margin-left: 5px;',
                                items: [
                                    {
                                        id: browseIds.Slide.title,
                                        style: 'margin-top: 10px;width: 408px;',
                                        cls: 'stdLabel',
                                        html: 'Slide'
                                    },
                                    uuidSlideGrid
                                ]
                            }
                        ]
                    },
                    { style: 'clear: both;width: 0;height: 10px;' },
                    {
                        id: browseIds.Aliquot.title,
                        style: 'margin-top: 10px;width: 945px;',
                        cls: 'stdLabel',
                        html: 'Aliquot'
                    },
                    uuidAliquotGrid
                ]
            }
        ]
    });

    return {
        showWin: function () {
            browserWindow.show();
            tcga.uuid.navigator.init();
            for (var browseId in browseIds) {
                Ext.getCmp(browseIds[browseId].title).body.addClass('lowlightGridTitleBackground');
            }
        },

        goTo: function (rec) {
            if (rec.get('uuid') != null) {
                var myMask = new Ext.LoadMask(Ext.getBody(),
                    {msg: "Please wait...", store: uuidBrowseResultsStore}
                );
                myMask.show();
                uuidBrowseResultsStore.load({params: {uuid: rec.get('uuid'), parentUUID: rec.get('parentUUID'),
                    uuidType: rec.get('uuidType')}});
            }
        },

        activateBrowseNode: function (node, val) {
            tcga.uuid.navigator.setActiveNode(node);

            if (node == 'Participant') {
                uuidSampleGrid.getStore().clearFilter();
                Ext.get(browseIds.Portion.title).hide();
                uuidPortionGrid.getSelectionModel().clearSelections();
                uuidPortionGrid.hide();
                Ext.get(browseIds.Analyte.title).hide();
                uuidAnalyteGrid.getSelectionModel().clearSelections();
                uuidAnalyteGrid.hide();
                Ext.get(browseIds.Aliquot.title).hide();
                uuidAliquotGrid.getSelectionModel().clearSelections();
                uuidAliquotGrid.hide();
                Ext.get(browseIds.Slide.title).hide();
                uuidSlideGrid.getSelectionModel().clearSelections();
                uuidSlideGrid.hide();
            }
            else if (node == 'Sample') {
                uuidPortionGrid.getSelectionModel().clearSelections();
                uuidPortionGrid.getStore().filter('parentUUID', val);
                Ext.get(browseIds.Analyte.title).hide();
                uuidAnalyteGrid.getSelectionModel().clearSelections();
                uuidAnalyteGrid.hide();
                Ext.get(browseIds.Aliquot.title).hide();
                uuidAliquotGrid.getSelectionModel().clearSelections();
                uuidAliquotGrid.hide();
                Ext.get(browseIds.Slide.title).hide();
                uuidSlideGrid.getSelectionModel().clearSelections();
                uuidSlideGrid.hide();
            }
            else if (node == 'Portion') {
                Ext.get(browseIds.Aliquot.title).hide();
                uuidAliquotGrid.getSelectionModel().clearSelections();
                uuidAliquotGrid.hide();
                uuidAnalyteGrid.getSelectionModel().clearSelections();
                uuidAnalyteGrid.getStore().filter('parentUUID', val);
                uuidSlideGrid.getSelectionModel().clearSelections();
                uuidSlideGrid.getStore().filter('parentUUID', val);
            }
            else if (node == 'Analyte') {
                uuidSelected = val;
                uuidAliquotGrid.getSelectionModel().clearSelections();
                uuidAliquotGrid.getStore().load({params: {start: 0, limit: 15}});
            }
            else if (node == 'Slide') {
                Ext.get(browseIds.Aliquot.title).hide();
                uuidAliquotGrid.getSelectionModel().clearSelections();
                uuidAliquotGrid.hide();
            }

            if (browseIds[node].next) {
                for (var ndx = 0; ndx < browseIds[node].next.length; ndx++) {
                    Ext.get(browseIds[browseIds[node].next[ndx]].title).show();
                    Ext.getCmp(browseIds[browseIds[node].next[ndx]].grid).show();
                }
            }
            highlightGrid(browseIds[node].title, browseIds[node].grid);
        }
    }

}();
