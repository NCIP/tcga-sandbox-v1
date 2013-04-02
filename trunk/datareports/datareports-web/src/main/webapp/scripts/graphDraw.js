/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
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


Ext.namespace('tcga.graph.draw');

tcga.graph.draw.paper = {};

/*
 * The default graph configuration.  Any of these values may be overridden in the graph data file.
 *
 *		Parameters:
 * 		renderTo - the id of the DOM element, typically a DIV, in which to render the graph.
 *			paperSize - the size of the picture area to set up, it has a width and a height with
 *				the numbers being specified in pixels.
 *			center - centers each node on the path connecting path to it, true by default
 *			scale - used to specify whether a scale is being provided for the outputs in the graph
 *				or not.  Null indicates that the outputs in the graph will be scaled relative to each
 *				other.  That is, if you have 3 outputs with counts of 30, 40 and 50, and scale is null,
 *				the first output will have a size of 30/(30+40+50) percent of the available space for
 *				the outputs.  If a number is provided for the scale, then the first output will have
 *				a size of 30/scale percent of the available space.  The available space is defined by
 *				the pathHeight parameter. (See pRepDataSubsetScaled.sjson for a scaling example.)
 *			squareCorners - the rounding of the corners of the box around the node.  0 is square corners,
 * 			and any other number will round progressively more as it increases.
 *			squareSize - the width and height of the first node, others will be scaled relative to the
 * 			path coming into them.
 *			squareColor - the color of the square in the background of a node.
 *			pathHeight - the height of the space provided for the outputs.
 *			pathLength - the length of the space provided for the outputs.
 *			pathColors - the colors to be used, in order, for the outputs.  The colors may be specified
 *				as either the names of web colors (http://www.tayloredmktg.com/rgb/ - if you use a name,
 *				like "Indian Red", with a space in it, specify it as "indianred"), or in the usual hex
 *				#AABBCC format.

 */
tcga.graph.draw.defaultGraphConfig = {
    renderTo:'raphgraph',
    paperSize:{width:900, height:600},
    center:'true',
    scale:null,
    startPos:{x:1, y:450},
    squareSize:{width:150, height:200},
    squareCorners:5,
    squareColor:'60-#ccc-#eee',
    pathHeight:200,
    pathLength:100,
    minPathWidth:3,
    pathColors:[
        'blue',
        'red',
        'darkorange',
        'brown',
        'darkgoldenrod'
    ]
};

/*
 * The default configuration for drawing the totals box.
 *
 * Any or all of these parameters may be overridden in the graph data file.
 *
 *		Parameters:
 *			textSize - the pixel size of the text in the totals box.  Just a number, the specification is
 * 			for pixels.
 *			width - the width of the totals box in pixels.
 *			pos - an object for the x,y position of the box.  This is x and y from the upper left corner
 * 			of the box.  Hmm, perhaps this should be top,left rather than x,y.
 *			fill - the fill color or a gradient for the background of the totals box.
 *			counts - a list of objects defining the numbers to be shown in the totals box.
 *
 *			Counts Parameters:
 *			count - the number to be displayed to the right of the box.  The counts will be totaled
 * 			and displayed at the bottom of the totals box with the label "Total".
 *			label - the label to be displayed to the left of the box.
 *
 */
tcga.graph.draw.defaultTotals = {
    textSize:14,
    width:200,
    pos:{x:700, y:1},
    fill:'#d8eff6',
    title:'Cases Shipped/Pending'
};

/*
 * The default configuration for drawing the tumor types box.
 *
 * Any or all of these parameters may be overridden in the graph data file.
 *
 *		Parameters:
 *			textSize - the pixel size of the text in the totals box.  Just a number, the specification is
 * 			for pixels.
 *			width - the width of the totals box in pixels.
 *			pos - an object for the x,y position of the box.  This is x and y from the upper left corner
 * 			of the box.  Hmm, perhaps this should be top,left rather than x,y.
 *			fill - the fill color or a gradient for the background of the totals box.
 *			title - the title to appear in the box
 *
 */
tcga.graph.draw.defaultTumorTypes = {
    textSize:14,
    width:210,
    pos:{x:460, y:1},
    fill:'#d8eff6',
    title:'Tumors Represented'
};
/*
 * The function that creates the store driving the graph drawing.
 * Further comments inline.
 */
tcga.graph.draw.createStore = function (config) {
    /*
     * The default configuration for the store.  These parameters may be overridden
     * when calling this function by adding them as parameters to the config object
     * being passed in to the function.  The parameters are the usual parameters for
     * an Ext store.  The only thing added to the typical JsonStore used here are some
     * functions and listeners.
     *
     * Functions:
     * 	reset - reloads the original data file loaded for the store.  Just a renamed
     * 		wrapper on reload.
     * 	loadUrl - loads a new url, as specified in the function parameter, to the graph.
     *
     * Listeners:
     * 	load - once the store loads the data, then figure out if there is already a context
     * 		to draw on (the paper).  If there is already some paper, then clear the paper.
     * 		If there is no paper, apply any new configurations to the default and create the paper.
     * 		Then, draw the graph.
     *
     * 		Note that the paper object set is parameterized to store the paper with the name
     * 		of the DOM element containing the paper.
     *
     */
    var storeDefaultConfig = {
        storeId:'nodeDataStore',
        url:'json/fred.sjson',
        root:'nodeData',
        idProperty:'name',
        autoLoad:true,
        fields:[
            'name',
            'type',
            'label',
            'image',
            'numericLabel',
            'pathLength',
            'listeners',
            'vertLoc',
            'minPathWidth',
            'outputs'
        ],
        // Additional functions to add to the store
        reset:function () {
            this.reload();
        },
        loadUrl:function (url) {
            this.proxy.setUrl(url);
            this.reload();
        },
        listeners:{
            load:function (store) {
                var graphConfig = {};
                Ext.apply(graphConfig, (store.reader.jsonData.graphConfig ? store.reader.jsonData.graphConfig : {}),
                    tcga.graph.draw.defaultGraphConfig);

                if (store.getCount() > 0 && store.getAt(0).get('name') == 'FailErrorFail') {
                    Ext.get(graphConfig.renderTo).hide();
                    Ext.get('errorDiv').show();
                } else {
                    Ext.get(graphConfig.renderTo).show();
                    Ext.get('errorDiv').hide();
                    if (!tcga.graph.draw.paper[graphConfig.renderTo]) {
                        tcga.graph.draw.paper[graphConfig.renderTo] = Raphael(graphConfig.renderTo,
                            graphConfig.paperSize.width, graphConfig.paperSize.height);
                    } else {
                        tcga.graph.draw.paper[graphConfig.renderTo].clear();
                    }
                    tcga.graph.draw.createGraph(tcga.graph.draw.paper[graphConfig.renderTo], store);
                }
            }
        }
    };

    var storeConfig = {};
    Ext.apply(storeConfig, config, storeDefaultConfig);
    tcga.graph.draw.store = new Ext.data.JsonStore(storeConfig);

    return tcga.graph.draw.store;
}

/*
 * The function that actually interprets the graph data file and calls all of the graph
 * widgets and associated functions to draw the graph on the page.  A quick narrative is
 * inserted in comments in the file.
 */
tcga.graph.draw.createGraph = function (paper, store) {
    // Apply any configurations passed in from the config stream to the default config
    var graphConfig = {};
    Ext.apply(graphConfig, (store.reader.jsonData.graphConfig ? store.reader.jsonData.graphConfig : {}), tcga.graph.draw.defaultGraphConfig);

    // Set up the horizontal starting location
    var currLoc = graphConfig.startPos.x;

    /*
     * Cycle through the nodes, drawing them merrily as we go.
     *
     * 	prevTotalCount - used to keep track of the current total of the counts
     * 		for the previous node.  Helps to scale the current node in relation to
     * 		the previous one.
     * 	prevConnectionPathSize - the size of the path that connected to the current
     * 		node.  Helps with scaling again.
     * 	nextNodeCenter - the y coordinate of the center of the connecting path, this is returned and
     * 		and used by the next node for positioning if the "center" option is true
     */
    var nodeCount = store.getCount();
    var prevTotalCount = (graphConfig.scale != null ? graphConfig.scale : 1);
    var prevConnectingPathSize = (graphConfig.scale != null ? graphConfig.scale : 1);
    var prevConnectingPathHeight = graphConfig.pathHeight;
    var nextNodeCenter = graphConfig.startPos.y;
    //var sizeH = graphConfig.squareSize.width;
    var sizeV = graphConfig.squareSize.height;
    var sizeH = 120;

    for (var ndxNodes = 0; ndxNodes < nodeCount; ndxNodes++) {
        var node = store.getAt(ndxNodes);
        var outputs = node.get('outputs');

        // Get the total of the counts for all outputs from this node
        var nodeTotalCount = tcga.graph.widgets.totalCount(outputs);
        /*
         * Find the index of the output that connects to the next node.  All outputs before
         * that index will point up, all outputs after that index will point down.
         */
        var connectingPathNdx = tcga.graph.widgets.getConnectingPathNdx(outputs);
        var pathLength = (node.get('pathLength') ? node.get('pathLength') : graphConfig.pathLength);

        /*
         * Piece together the configuration for the node from the graph config, the node config, and
         * some calculations based on where we are in drawing the graph.  Hmm, this seems overly
         * complex.  And it really should copy untouched nodeConfigs in by default.
         */
        sizeV = sizeV * prevConnectingPathSize / prevTotalCount;
        //adjusting width ONLY for readability
        //sizeH * prevConnectingPathSize/prevTotalCount;
        if (ndxNodes == 3) {
            sizeH = 145;
        } else if (ndxNodes == 4) {
            sizeH = 65;
        }
        var nodeConfig = {
            paper:paper,
            label:node.get('label'),
            image:node.get('image'),
            center:graphConfig.center,
            horizLoc:currLoc,
            // The -10 is one half of the offset used to place the image in the box for the node
            vertLoc:((graphConfig.center == 'true' && ndxNodes != 0) ? nextNodeCenter + sizeV / 2 - 10 : graphConfig.startPos.y) + (node.get('vertLoc') ? node.get('vertLoc') : 0),
            sizeH:sizeH,
            sizeV:sizeV,
            pathLength:pathLength,
            pathHeight:(prevConnectingPathHeight ? prevConnectingPathHeight : graphConfig.pathHeight) * ((ndxNodes == 0 && graphConfig.scale != null) ? nodeTotalCount : prevConnectingPathSize) / (graphConfig.scale != null ? graphConfig.scale : prevTotalCount),
            numericLabel:node.get('numericLabel'),
            scale:graphConfig.scale,
            minPathWidth:graphConfig.minPathWidth,
            squareColor:graphConfig.squareColor,
            listeners:node.get('listeners')
        };

        /*
         * Now assemble the configuration for each of the outputs from the node.  Use the output
         * configs, the node config, and the graph config.  Note that the colors cycle through, so
         * if you get to the end of the color list they go back to the beginning.  colorNdx and
         * colorCount are used to track this cycle through the colors separately from the cycle
         * through the outputs.
         */
        nodeConfig.outputs = [];
        var colorCount = graphConfig.pathColors.length;
        var colorNdx = 0;
        for (var ndxOutputs = 0; ndxOutputs < outputs.length; ndxOutputs++) {
            nodeConfig.outputs[ndxOutputs] = {
                count:outputs[ndxOutputs].count,
                label:outputs[ndxOutputs].label,
                connectToNextNode:(outputs[ndxOutputs].connectToNextNode == 'true' ? true : false),
                color:(outputs[ndxOutputs].color ? outputs[ndxOutputs].color : graphConfig.pathColors[(colorNdx)]),
                arrow:(outputs[ndxOutputs].arrow == 'false' ? false : true),
                minPathWidth:graphConfig.minPathWidth,
                pathDir:outputs[ndxOutputs].pathDir
            };

            colorNdx = (colorNdx + 1 < graphConfig.pathColors.length ? colorNdx + 1 : 0);
        }

        /*
         * Now, draw the node, which also includes the outputs from the node.
         * nextNodeCenter - the y coordinate of the center of the connecting path, this is returned and
         * 	and used by the next node for positioning if the "center" option is true
         */
        nextNodeCenter = tcga.graph.widgets.drawNode(nodeConfig);

        /*
         * Update the current horizontal location based on the width of the node + outputs that
         * we just drew.
         */
        currLoc += sizeH + pathLength;
        // If there was a previous connecting path it will influence the size of the next node,
        // 	so capture it.  Otherwise, set these back to 1.
        if (outputs[connectingPathNdx]) {
            prevConnectingPathSize = outputs[connectingPathNdx].count;
            prevConnectingPathHeight = nodeConfig.pathHeight;
            prevTotalCount = nodeTotalCount;
        }
        else {
            prevConnectingPathSize = 1;
            prevConnectingPathHeight = graphConfig.pathHeight;
            prevTotal = 1;
        }
    }

    /*
     * Now, if there's a tumorTypes section in the graph data file, draw the tumorTypes block.
     */
    if (store.reader.jsonData.tumorTypes) {
        // Apply any configurations passed in from the config stream to the default config
        var tumorTypesConfig = {};
        Ext.apply(tumorTypesConfig, (store.reader.jsonData.tumorTypes ? store.reader.jsonData.tumorTypes : {}), tcga.graph.draw.defaultTumorTypes);

        var formatTumorText = function (text, align) {
            if (!align) {
                var align = 'start';
            }
            text.attr('text-anchor', align);
            text.attr('font-size', tumorTypesConfig.textSize + 'px');
        };

        /*
         * Set up some parameters for positioning the text correctly.
         * 	textBase - the starting vertical position within the box for the text
         */
        var textBase = tumorTypesConfig.pos.y + 12;

        /*
         * The locations of the labels and the counts within the text box.  The labels
         * are positioned relative to the left side of the box.  The counts are positioned
         * relative to the right side of the box.
         */
        var margins = 6;
        var labelX = tumorTypesConfig.pos.x + margins;
        var tumorsFormatted = tcga.graph.widgets.text.splitText(
            tumorTypesConfig.types,
            tumorTypesConfig.width - margins * 2,
            {'font-size':tumorTypesConfig.textSize + 'px'});

        // Draw the tumorTypes box
        var results = paper.rect(tumorTypesConfig.pos.x, 1, tumorTypesConfig.width,
            20 + tumorsFormatted.split("\n").length * (tumorTypesConfig.textSize + 6), 5);
        results.attr('fill', tumorTypesConfig.fill);

        // Draw the title
        var title = paper.text(labelX, textBase, tumorTypesConfig.title);
        formatTumorText(title);
        title.attr('font-weight', 'bold');
        textBase += 13;

        // The dividing line under the title
        paper.path('M' + tumorTypesConfig.pos.x + ' ' + textBase + 'L' + (tumorTypesConfig.pos.x + tumorTypesConfig.width) + ' ' + textBase);
        textBase += 9 * tumorsFormatted.split("\n").length;

        var tumorList = paper.text(labelX, textBase, tumorsFormatted);
        formatTumorText(tumorList);
    }

    /*
     * Now, if there's a totals section in the graph data file, draw the totals block.
     */
    if (store.reader.jsonData.totals) {
        // Apply any configurations passed in from the config stream to the default config
        var totalsConfig = {};
        Ext.apply(totalsConfig, (store.reader.jsonData.totals ? store.reader.jsonData.totals : {}), tcga.graph.draw.defaultTotals);

        // Get the total of the counts in the totals box - exclude anything with "received" or "held" in it
        var totalCount = 0;
        for (var ndx = 0; ndx < totalsConfig.counts.length; ndx++) {
            if (totalsConfig.counts[ndx].label.toLowerCase().indexOf('received') == -1 &&
                totalsConfig.counts[ndx].label.toLowerCase().indexOf('held') == -1) {
                totalCount += totalsConfig.counts[ndx].count;
            }
        }

        var formatTotalsText = function (text, align) {
            if (!align) {
                var align = 'start';
            }
            text.attr('text-anchor', align);
            text.attr('font-size', totalsConfig.textSize + 'px');
        };

        /*
         * Set up some parameters for positioning the text correctly.
         * 	textBase - the starting vertical position within the box for the text
         * 	textInterval - the space to jump down before printing the next bit of text, based
         * 		on the fontsize
         */
        var textBase = totalsConfig.pos.y + 12;
        var textInterval = totalsConfig.textSize + 6;

        /*
         * The locations of the labels and the counts within the text box.  The labels
         * are positioned relative to the left side of the box.  The counts are positioned
         * relative to the right side of the box.
         */
        var labelX = totalsConfig.pos.x + 6;
        var countX = totalsConfig.pos.x + totalsConfig.width - 4;

        // Draw the results box
        var results = paper.rect(totalsConfig.pos.x, 1, totalsConfig.width, (totalsConfig.counts.length * textInterval) + 60, 5);
        results.attr('fill', totalsConfig.fill);

        // Draw the title
        var title = paper.text(labelX, textBase, totalsConfig.title);
        title.attr('font-weight', 'bold');
        title.attr('text-anchor', 'start');
        title.attr('font-size', totalsConfig.textSize + 'px');
        textBase += 13;

        // The dividing line under the title
        paper.path('M' + totalsConfig.pos.x + ' ' + textBase + 'L' + (totalsConfig.pos.x + totalsConfig.width) + ' ' + textBase);
        textBase += 12;

        // Figure out where the totals dividing line goes - this is between the data and the total
        var dividerLoc = (totalsConfig.counts.length * textInterval) + 34;
        paper.path('M' + totalsConfig.pos.x + ' ' + dividerLoc + 'L' + (totalsConfig.pos.x + totalsConfig.width) + ' ' + dividerLoc);

        // Cycle through the labels and counts printing as we go.
        for (var ndx = 0; ndx < totalsConfig.counts.length; ndx++) {
            var label = paper.text(labelX, textBase + (textInterval * ndx), totalsConfig.counts[ndx].label + ':');
            formatTotalsText(label);
            var count = paper.text(countX, textBase + (textInterval * ndx), totalsConfig.counts[ndx].count);
            formatTotalsText(count, 'end');
        }

        // Add the 7 to the vertical position to account for the dividing line
        var totalsLabel = paper.text(labelX, textBase + (textInterval * ndx) + 7, (totalsConfig.label ? totalsConfig.label : 'Total') + ':');
        formatTotalsText(totalsLabel);
        var totalCount = paper.text(countX, textBase + (textInterval * ndx) + 7, totalCount);
        formatTotalsText(totalCount, 'end');
    }
}
