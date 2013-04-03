Ext.namespace('tcga.uuid.search');

var searchValues;

tcga.uuid.search.form = function(browseStore) {

    tcga.uuid.search.setRadioGroupForSearch = function(radio, checked) {
        if (radio == 'uuidSearch' && checked) {
            Ext.getCmp('uuidSearchRadio').setValue(true);
            Ext.getCmp('barcodeSearchRadio').setValue(false);
            Ext.getCmp('fileSearchRadio').setValue(false);
        }
        else if (radio == 'barcodeSearch' && checked) {
            Ext.getCmp('uuidSearchRadio').setValue(false);
            Ext.getCmp('barcodeSearchRadio').setValue(true);
            Ext.getCmp('fileSearchRadio').setValue(false);
        }
        else if (radio == 'fileSearch' && checked) {
            Ext.getCmp('uuidSearchRadio').setValue(false);
            Ext.getCmp('barcodeSearchRadio').setValue(false);
            Ext.getCmp('fileSearchRadio').setValue(true);
        }
    };

    tcga.uuid.search.uuidSearchForm = new Ext.form.FormPanel({
        id: 'uuidSearchForm',
        border: false,
        layout: 'column',
        fileUpload: true,
        style: 'margin: 10px 0 0 180px;',
        width: 400,
        items: [
            {
                ctCls: 'stdLabel',
                border: false,
                width: 100,
                xtype: 'radio',
                id: 'uuidSearchRadio',
                name: 'uuidSearchRadio',
                boxLabel: 'UUID:',
                inputValue: 'uuidSearch',
                checked: true,
                listeners: {check: function(radio, checked) {
                    tcga.uuid.search.setRadioGroupForSearch('uuidSearch', checked);
                }}
            },
            {
                id: 'uuidField',
                xtype: 'textfield',
                width: 300,
                style: '*height:23px;padding-left:2px;border-width:1px;',
                emptyText: 'Enter a UUID',
                stripCharsRe: /(^\s+|\s+$)/g,
                listeners: {focus: function() {
                    tcga.uuid.search.setRadioGroupForSearch('uuidSearch', true);
                }}
            },
            {
                border: false,
                height: 5,
                width: 400,
                html: '&nbsp;'
            },
            {
                ctCls: 'stdLabel',
                border: false,
                width: 100,
                xtype: 'radio',
                id: 'barcodeSearchRadio',
                name: 'barcodeSearchRadio',
                boxLabel: 'Barcode:',
                inputValue: 'barcodeSearch',
                listeners: {check: function(radio, checked) {
                    tcga.uuid.search.setRadioGroupForSearch('barcodeSearch', checked);
                }}
            },
            {
                id: 'barcodeField',
                xtype: 'textfield',
                width: 300,
                style: '*height:23px;padding-left:2px;border-width:1px;',
                emptyText: 'Enter a barcode',
                stripCharsRe: /(^\s+|\s+$)/g,
                listeners: {focus: function() {
                    tcga.uuid.search.setRadioGroupForSearch('barcodeSearch', true);
                }}
            },
            {
                border: false,
                height: 5,
                width: 400,
                html: '&nbsp;'
            },
            {
                ctCls: 'stdLabel',
                border: false,
                width: 100,
                xtype: 'radio',
                id: 'fileSearchRadio',
                name: 'fileSearchRadio',
                boxLabel: 'File:',
                inputValue: 'fileSearch',
                listeners: {check: function(radio, checked) {
                    tcga.uuid.search.setRadioGroupForSearch('fileSearch', checked);
                }}
            },
            {
                xtype: 'fileuploadfield',
                layout: 'form',
                emptyText: 'Select a File to import',
                id: 'form-file',
                name:'file',
                width: 296,
                allowBlank : false,
                blankText : 'This field is required.',
                validateOnBlur: false,
                listeners: {
                    focus: function() {
                        tcga.uuid.search.setRadioGroupForSearch('fileSearch', true);
                    },
                    render: function() {
                        Ext.get('form-file-file').on('click', function() {
                            tcga.uuid.search.setRadioGroupForSearch('fileSearch', true);
                        });
                    }
                }
            }
        ]
    });

    tcga.uuid.search.searchPanelButtons = new Ext.Panel({
        id: 'searchPanelButtons',
        bodyStyle: 'width: 120px;',
        border: false,
        layout: 'column',
        height: 105,
        items: [
            {
                xtype: 'buttonplus',
                id: 'searchButton',
                width: 76,
                height: 49,
                style: 'margin: 9px 0 0 10px;',
                text: '<span class="stdLabel">Search<span>',
                handler: function() {
                    Ext.getCmp('form-file').setValue(' ');
                    var paging = Ext.getCmp('resultsPagingTb');
                    var dataHandler = tcga.uuid.search.uuidSearchForm.getForm().getFieldValues();
                    searchValues = Ext.util.JSON.encode(dataHandler);
                    if (Ext.getCmp('fileSearchRadio').getValue() == true) {
                        tcga.uuid.search.uuidSearchForm.getForm().submit({
                            waitMsg: 'Uploading file...',
                            url: 'uuidUploadStatus.htm',
                            method: 'POST',
                            success : function(form, action) {
                                var res = Ext.util.JSON.decode(action.response.responseText);
                                dataHandler.uploadProcess = res.message;
                                searchValues = Ext.util.JSON.encode(dataHandler);
                                browseStore.proxy.setUrl('uuidBrowser.json');
                                browseStore.load({params:{start:0,limit:paging.pageSize, searchParams:searchValues}});
                            },
                            failure : function(form, action) {
                                if (action.response == undefined) {
                                    alert("Please upload a valid file.");
                                    return;
                                } else {
                                    var res = Ext.util.JSON.decode(action.response.responseText);
                                    Ext.Msg.show({
                                        title: 'Error',
                                        msg: res.message,
                                        minWidth: 200,
                                        modal: true,
                                        buttons: Ext.Msg.OK,
                                        icon: Ext.Msg.ERROR
                                    });
                                    return;
                                }
                            }
                        });

                    } else {
                        browseStore.load({params:{start:0,limit:paging.pageSize,searchParams:searchValues}});
                    }
                }
            },
            {
                xtype: 'buttonplus',
                id: 'resetButton',
                width: 76,
                height: 25,
                style: 'margin: 3px 0 0 10px;',
                text: '<span class="stdLabel">Reset<span>',
                handler: function() {
                    tcga.uuid.search.uuidSearchForm.getForm().reset();
                }
            }
        ]
    });

    var searchPanel = new Ext.Panel({
        id: 'uuidSearchPanel',
        title: 'UUID/Barcode',
        style: 'margin-top: 5px;',
        bodyStyle: 'width: 848px;',
        border: true,
        layout: 'column',
        height: 105,
        items: [
            tcga.uuid.search.uuidSearchForm,
            tcga.uuid.search.searchPanelButtons
        ]
    });

    new tcga.extensions.TabPanel({
        id: 'uuidBrowserTabPanel',
        renderTo: 'uuidSearch',
        border: false,
        autoHeight: true,
        hideBorders: true,
        activeTab: 0,
        items: [
            searchPanel,
            tcga.uuid.search.metadata(browseStore, (Ext.getCmp('resultsPagingTb') == undefined ? 50 :
                Ext.getCmp('resultsPagingTb').pageSize))
        ],
        listeners: {
            tabchange: function(tabPanel, newActiveTab) {
                if (newActiveTab != null) {
                    if (newActiveTab.title == 'Metadata') {
                        newActiveTab.setHeight(165);
                        tabPanel.setHeight(210);
                        Ext.get('uuidSearch').setHeight(210);
                    }
                    else {
                        newActiveTab.setHeight(105);
                        tabPanel.setHeight(150);
                        Ext.get('uuidSearch').setHeight(150);
                    }
                }
            }
        }
    });
}
