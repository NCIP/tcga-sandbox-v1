/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function() {
    var anchorRenderer = function(label, url) {
        if (url != null) {
            return '<a href="' + url + '" target="_blank">' + label + '</a>';
        }
        else {
            return label;
        }
    }
    
	var platformFeedUrl = '/web/news/platforms.json';
	if(tcgaHost.indexOf("localhost") > -1){
	  	platformFeedUrl = 'json/platforms.json';
	}

    var platformStore = new Ext.data.JsonStore({
        url: platformFeedUrl,
        root: 'platformsList',
        autoLoad: true,
        sortInfo: {
            field: 'code',
            direction: 'ASC'
        },
        fields: [
            'center',
            'code',
            'name',
            'nameLink',
            'sequenceDownload',
            'sequenceDownloadLink',
            'adfDownload',
            'adfDownloadLink'
        ]
    });

    new Ext.grid.GridPanel({
        renderTo: 'platforms',
        store: platformStore,
        border: true,
        columns: [{
            id: 'center',
            header: '<span class="platformTitles">Center</span>',
            width: 140,
        	sortable  : true,
            dataIndex: 'center',
            tooltip: 'The domain of the submitting center for this platform.',
				renderer: function(val, mD, rec, row) {
					return '<span id="platform' + row + '">' + val + '</span>';
				}
        }, {
            id: 'code',
        	sortable  : true,
            header: '<span class="platformTitles">TCGA Platform Code</span>',
            width: 240,
            dataIndex: 'code',
            tooltip: 'The code used in TCGA to represent the platform.'
        }, {
            id: 'name',
        	sortable  : true,
            header: '<span class="platformTitles">Platform Name</span>',
            width: 315,
            dataIndex: 'name',
            tooltip: 'The vendor\'s name of the platform.',
            renderer: function(val, mD, rec) {
                return anchorRenderer(val, rec.get('nameLink'));
            }
        }, {
            id: 'sequenceDownload',
        	sortable  : true,
            header: '<span class="platformTitles">Sequence Download</span>',
            width: 100,
            dataIndex: 'sequenceDownload',
            tooltip: 'FASTA files used to the create the TCGA ADF file and, for sequence based data, a link to the NCBI trace archives..',
            renderer: function(val, mD, rec) {
                return anchorRenderer(val, rec.get('sequenceDownloadLink'));
            }
        }, {
            id: 'adfDownload',
        	sortable  : true,
            header: '<span class="platformTitles">TCGA ADF Download</span>',
            width: 140,
            dataIndex: 'adfDownload',
            tooltip: 'The TCGA modified format of the standard MAGE ADF file.',
            renderer: function(val, mD, rec) {
                return anchorRenderer(val, rec.get('adfDownloadLink'));
            }
        }],
        stripeRows: true,
        forceFit: true,
        autoHeight: true,
        width: 935,
        loadMask: true
    });
});
