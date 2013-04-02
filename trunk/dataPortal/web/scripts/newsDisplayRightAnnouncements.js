/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

Ext.onReady(function() {
    var newsDisplay = new tcga.news.display({
        renderTo: 'homeNewsArticles',
        border: false,
//      Test version
//        url: 'json/newsFeed.sjson',
//      Production version
        url: '/web/news/newsFeed.json',
        numToDisplay: 2
    });

    newsDisplay.render();
});
