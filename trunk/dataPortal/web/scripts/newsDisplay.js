/*******************************************************************************
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 ******************************************************************************/

/*
 * @class tcga.news.display
 * @extends Ext.Panel
 * <p>A quick panel extension to display n or more news articles that come from a json source.</p>
 * @param (Object} config The config object
 * @cfg {String} url
 * <p>The url for the news store.</p>
 * @cfg {String} numToDisplay
 * <p>The number of news articles to display.  The default is to display all articles from the source.</p>
 * @cfg {String} template
 * <p>A set of strings to feed to an Ext.XTemplate in case an alternative is desired.</p>
 * @cfg {String} language - not yet implemented
 * <p>To be used later to get articles in a particular language displayed.</p>
 * @constructor
 * @xtype newsdisplay
 */

Ext.namespace('tcga.news');

tcga.news.display = Ext.extend(Ext.Panel, {
    numToDisplay: 'xcount',
    
    initComponent: function() {
        var newsStore = new Ext.data.JsonStore({
            url: this.url,
            root: 'dccPortalNewsList',
            autoLoad: true,
            fields: [
                'date',
                'title',
                'article'
            ],
            listeners: {
                    'load': {
                    scope: this,
                    fn: function(store, recs) {
                    if (this.numToDisplay == undefined || this.numToDisplay == null) {
                        // No need to cut off the entries
                        return;
                    }
                    var total = store.getCount();
                    for (var ndx = this.numToDisplay; ndx < total; ndx++) {
                        store.removeAt(ndx);    
                    }
                }
                }
            }
        });

        var newsTemplate = new Ext.XTemplate(
                '<tpl for=".">',
                    '<tpl if="xindex <= ' + this.numToDisplay + '">',
                        '<div id="newsArticle{[xindex]}" class="newsDataTpl">',
                            '<b>{date} - {title}</b><br/>',
                            '{article}',
                            '<tpl if="xindex != ' + this.numToDisplay + '">',
                                '<hr size=1 noshade>',
                            '</tpl>',
                        '</div>',
                    '</tpl>',
                '</tpl>'
                );

        this.items =
            new Ext.DataView({
                store: newsStore,
                autoHeight: true,
                itemSelector: 'div.newsDataTpl',
                numToDisplay: 2,
                tpl: newsTemplate
            })
        ;

        tcga.news.display.superclass.initComponent.call(this);
    }
});

Ext.reg('newsdisplay', tcga.news.display);
