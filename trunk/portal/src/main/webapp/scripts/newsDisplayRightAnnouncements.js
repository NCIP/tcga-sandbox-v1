/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function() {
    var newsfeedUrl = '/web/news/newsFeed.json';
    if(tcgaHost.indexOf("localhost") > -1){
    	newsfeedUrl = '/tcga/json/newsFeed.json';
    }
    var newsDisplay = new tcga.news.display({
        renderTo: 'homeNewsArticles',
        border: false,
        url: newsfeedUrl,
        numToDisplay: 2
    });

    newsDisplay.render();
});
