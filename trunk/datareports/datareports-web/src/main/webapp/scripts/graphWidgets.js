/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

Ext.namespace('tcga.graph.widgets');

tcga.graph.widgets.outputOpacity = 0.6;

/*
 * Draw just the node, which does not include the outputs.  Note that this returns either the
 * image, or, if there is no image, the background box.
 */
tcga.graph.widgets.imageOrTextNode = function (config) {
    if (!config.sizeV) {
        config.sizeV = config.sizeH;
    }
    var imgBg = config.paper.rect(config.horizLoc, config.vertLoc - config.sizeV, config.sizeH, config.sizeV + 20, 5)
    imgBg.attr('fill', config.squareColor);
    var x = config.horizLoc + config.sizeH / 2;
    // Check if there is an image, and then check to see if an image would fit.
    if (config.image && config.image != '' && config.sizeH > 10 && config.sizeV > 11) {
        var node = config.paper.image(config.image, config.horizLoc + 5, config.vertLoc - config.sizeV + 5, config.sizeH - 10, config.sizeV - 10);
        var y = config.vertLoc + 7;
    }
    else {
        node = imgBg;
        // Find the middle of the box, then subtract half of the height
        // -10 is one half the offset that was added for the image - keeping to keep the
        //		same size ration on the boxes
        var y = config.vertLoc - 10;
    }

    if (config.label) {
        var nodeLabel = tcga.graph.widgets.text.displayText(config.label, {
            paper:config.paper,
            x:x,
            y:y,
            imgNode:(config.image && config.image != '' ? true : false),
            sizeV:config.sizeV,
            // Allow some space on either side
            maxWidth:config.sizeH - 12,
            maxHeight:config.sizeV - 12,
            valign:(config.image ? 'bottom' : 'center'),
            singleLine:(config.image ? true : false),
            hoverLocX:x,
            hoverLocY:y + 10
        }, {'font-size':'12px', 'font-weight':'bold'})

        // There's a nodeLabel, so we need to add hover to the node elements
        if (nodeLabel) {
            new tcga.util.svgHoverWin({
                paper:config.paper,
                appearNear:imgBg,
                text:nodeLabel,
                left:x,
                top:y + 10,
                ie7:{
                    left:0,
                    top:97
                }
            });

            if (config.image && config.image != '') {
                new tcga.util.svgHoverWin({
                    paper:config.paper,
                    appearNear:node,
                    text:nodeLabel,
                    left:x,
                    top:y + 10,
                    ie7:{
                        left:0,
                        top:97
                    }
                });
            }
        }
    }

    return node;
}

/*
 * Draw a straight path
 *
 * Configuration:
 * 	paper - required
 * 	startX - required, the coordinates of the start location to put into an M SVG command
 * 	startY - required, the coordinates of the start location to put into an M SVG command
 * 	width - the width of the path
 * 	type - h, v, l - the type of line, use direction with H and V lines, use path for L lines
 * 	length - for use with H and V lines, gives the length of the path
 * 	path - the path to use for line
 * 	direction - N, S, E, W - the direction the path is pointing
 * 	arrow - boolean, draw an arrow at the end of the line, defaults to true
 * 	color - the color of the arrow
 */
tcga.graph.widgets.path = function (config) {
    var defaultConfig = {
        type:'h',
        direction:'E',
        color:'green',
        arrow:true,
        arrowMinWidth:4
    };
    var localConfig = Ext.applyIf(config, defaultConfig);

    var arrowDepth = (localConfig.arrow ? defaultConfig.arrowMinWidth : 0);

    var path = localConfig.paper.path('M ' + (localConfig.startX + 1) + ' ' + localConfig.startY + ' ' + localConfig.type + (localConfig.length - 1 - arrowDepth));
    path.attr('stroke', localConfig.color);
    path.attr('stroke-width', localConfig.width);
    path.attr('stroke-opacity', tcga.graph.widgets.outputOpacity);

    if (localConfig.arrow) {
        var arrowConfig = {};
        Ext.apply(arrowConfig, localConfig);
        arrowConfig.startX = localConfig.startX + localConfig.length - arrowDepth;
        var arrow = tcga.graph.widgets.arrow(arrowConfig);
    }

    var label = '';
    if (localConfig.label) {
        label += localConfig.label + ': ';
    }
    label += localConfig.count + ' ';
    if (localConfig.numericLabel) {
        label += localConfig.numericLabel;
    }

    var pathLabel = tcga.graph.widgets.text.displayText(label, {
        paper:config.paper,
        x:localConfig.startX + (localConfig.length / 2),
        y:localConfig.startY,
        // Allow some space on either side
        maxWidth:localConfig.length,
        maxHeight:localConfig.width,
        hoverLocX:localConfig.startX + (localConfig.length / 2),
        hoverLocY:localConfig.startY + (localConfig.width / 2) - 5
    }, {'font-size':'12px'})

    if (localConfig.listeners) {
        tcga.graph.listeners.create(path, localConfig.listeners);

        if (pathLabel != null) {
            tcga.graph.listeners.create(pathLabel, localConfig.listeners);
        }
    }

    if (pathLabel) {
        // There's a pathLabel, so we need to add hover to the node elements
        new tcga.util.svgHoverWin({
            paper:config.paper,
            appearNear:path,
            text:pathLabel,
            left:localConfig.startX + (localConfig.length / 2),
            top:localConfig.startY + (localConfig.width / 2) - 5,
            ie7:{
                left:25,
                top:97
            }
        });

        if (localConfig.arrow) {
            new tcga.util.svgHoverWin({
                paper:config.paper,
                appearNear:arrow,
                text:pathLabel,
                left:localConfig.startX + (localConfig.length / 2),
                top:localConfig.startY + (localConfig.width / 2) - 5,
                ie7:{
                    left:25,
                    top:97
                }
            });
        }
    }

    return path;
}

/*
 * Draw a path with an elbow in it.  The elbow can bend up or down.
 *
 * Configuration:
 * elbowtypes - 'hv', 'vh' - that is go horizontal then vertical, or vice versa
 * direction - the direction in which the end of the path is pointing
 * lineJoin - the type of join for the bend in the line [miter, round, bevel] - see
 * 	http://www.w3.org/TR/SVG/painting.html#StrokeLinejoinProperty
 * arrow - boolean indicating whether or not to include an arrow at the end of the path
 */
tcga.graph.widgets.elbowPath = function (config) {
    var defaultConfig = {
        elbowType:'hv',
        direction:'E',
        lineJoin:'round',
        arrow:true
    };
    var localConfig = Ext.applyIf(config, defaultConfig);

    // Validate sizing of vLength vs the line width
    if (Math.abs(localConfig.vLength) < localConfig.width / 2) {
        if (localConfig.vLength > 0) {
            localConfig.vLength = localConfig.width / 2 + 10;
        }
        else {
            localConfig.vLength = -localConfig.width / 2 - 10;
        }
    }
    // Validate sizing of hLength vs the line width
    if (localConfig.hLength < localConfig.width) {
        localConfig.hLength += (localConfig.width / 2 - localConfig.hLength) + 10;
    }

    var path = '';
    var labelOffsetV = localConfig.labelOffsetV;
    if (localConfig.elbowType == 'hv') {
        path += ' h ' + (localConfig.hLength) + ' v ' + localConfig.vLength;
        if (localConfig.vLength > 0) {
            localConfig.direction = 'S';
        }
        else {
            localConfig.direction = 'N';
            labelOffsetV = labelOffsetV * -1;
        }
    }
    else {
        path += ' v ' + localConfig.vLength + ' h ' + (localConfig.hLength);
        if (localConfig.hLength > 0) {
            localConfig.direction = 'E';
        }
        else {
            localConfig.direction = 'W';
        }
    }
    var path = localConfig.paper.path('M ' + (localConfig.startX) + ' ' + localConfig.startY + path);
    path.attr('stroke', localConfig.color);
    path.attr('stroke-width', localConfig.width);
    path.attr('stroke-linejoin', localConfig.lineJoin);
    path.attr('stroke-opacity', tcga.graph.widgets.outputOpacity);

    if (localConfig.arrow) {
        var arrowConfig = {};
        Ext.apply(arrowConfig, localConfig);
        arrowConfig.startX = localConfig.startX + localConfig.hLength;
        arrowConfig.startY = localConfig.startY + localConfig.vLength;
        tcga.graph.widgets.arrow(arrowConfig);
    }

    var label = '';
    if (localConfig.label) {
        label += localConfig.label + ':\n';
    }
    label += localConfig.count + ' ';
    if (localConfig.numericLabel) {
        label += localConfig.numericLabel;
    }

    var pathLabel = localConfig.paper.text(localConfig.startX + localConfig.hLength + localConfig.labelOffsetH, localConfig.startY + localConfig.vLength + labelOffsetV, label);
    pathLabel.attr('font-size', '12px');

    if (localConfig.listeners) {
        tcga.graph.listeners.create(path, localConfig.listeners);

        if (pathLabel) {
            tcga.graph.listeners.create(pathLabel, localConfig.listeners);
        }
    }

    return path;
}

/*
 * Draw an arrow at the end of an SVG line
 * Configuration:
 * 	paper - required
 * 	startX - required, the coordinates of the start location to put into an M SVG command, the bottom coordinate
 * 	startY - required, the coordinates of the start location to put into an M SVG command, the bottom coordinate
 * 	width - the width of the path requiring the arrow
 * 	direction - N, S, E, W - the direction the arrow is pointing
 * 	color - the color of the arrow
 * 	depth - defaults to 10px
 */
tcga.graph.widgets.arrow = function (config) {
    var localConfig = {
        depth:10
    };
    Ext.apply(localConfig, config);

    localConfig.width = (localConfig.width / 2 >= 10 ? localConfig.width / 2 : 10);

    var arrowPath = '';
    if (localConfig.direction == 'E') {
        arrowPath = localConfig.depth + ',' + localConfig.width + ' -' + localConfig.depth + ',' + localConfig.width;
    }
    else if (localConfig.direction == 'W') {
        arrowPath = '-' + localConfig.depth + ',' + localConfig.width + ' ' + localConfig.depth + ',' + localConfig.width;
    }
    else if (localConfig.direction == 'N') {
        arrowPath = localConfig.width + ',-' + localConfig.depth + ' ' + localConfig.width + ',' + localConfig.depth;
    }
    else if (localConfig.direction == 'S') {
        arrowPath = localConfig.width + ',' + localConfig.depth + ' ' + localConfig.width + ',-' + localConfig.depth;
    }

    var startX = localConfig.startX;
    var startY = localConfig.startY;
    if (localConfig.direction == 'E' || localConfig.direction == 'W') {
        startY -= localConfig.width;
    }
    else {
        startX -= localConfig.width;
    }
    var arrow = localConfig.paper.path('M ' + startX + ',' + startY + ' l ' + arrowPath + ' z');
    if (localConfig.color) {
        arrow.attr('fill', localConfig.color)
        arrow.attr('fill-opacity', tcga.graph.widgets.outputOpacity);
    }
    arrow.attr('stroke', localConfig.color)
    arrow.attr('stroke-width', 0);
    arrow.attr('stroke-opacity', tcga.graph.widgets.outputOpacity);

    return arrow;
}

/*
 * A function to make it simpler to create a popup when mousing over an element in the
 * graph.  This should be updated once we decide whether or not we're going to be using
 * popups.  I'll document it once updated since I don't know how much the update, if it
 * happens, will change the function.
 */
tcga.graph.widgets.createMouseOverPopup = function (config) {
    var localConfig = {
        id:Ext.id(),
        floating:true,
        height:null,
        autoHeight:false,
        width:100,
        mouseout:true
    };
    Ext.apply(localConfig, config);

    localConfig.target.node.onmouseover = function (e) {
        if (Ext.getCmp(localConfig.id) != null) {
            return;
        }

        e = window.event || e;

        var popupPanel = new Ext.Panel(localConfig);

        popupPanel.addListener('show', function (popup) {
            var winHeight = document.documentElement.clientHeight;
            var winWidth = document.documentElement.clientWidth;

            var popupXPos = e.clientX + 10;
            var popupYPos = e.clientY + 5;
            if ((popupXPos + popup.getWidth()) > winWidth) {
                popupXPos -= (popupXPos + popup.getWidth()) - winWidth;
            }
            if ((popupYPos + popup.getHeight()) > winHeight) {
                popupYPos -= (popupYPos + popup.getHeight()) - winHeight;
            }
            popup.setPosition(popupXPos, popupYPos);
        });
        popupPanel.render(Ext.getBody());
        popupPanel.show();
    };

    if (localConfig.mouseout) {
        localConfig.target.node.onmouseout = function () {
            var popupPanel = Ext.getCmp(localConfig.id);
            if (popupPanel != null) {
                popupPanel.destroy();
            }
        };
    }
}

/*
 * Walk through a set of outputs and return the index of the path that has connectToNextNode set.
 */
tcga.graph.widgets.getConnectingPathNdx = function (outputs) {
    for (var ndx = 0; ndx < outputs.length; ndx++) {
        if (outputs[ndx].connectToNextNode) {
            return ndx;
        }
    }

    // There is no connecting path
    return 0;
}

/*
 * Walk through a vector of objects and sum the count fields, or alternately, sum a field
 * name that is passed in.  Return the sum.
 */
tcga.graph.widgets.totalCount = function (countSet, field) {
    if (!field) {
        field = 'count';
    }

    var total = 0;
    for (var ndx = 0; ndx < countSet.length; ndx++) {
        total += countSet[ndx][field];
    }
    return total;
}

/*
 * Various text tweaking facilities
 */
tcga.graph.widgets.text = {
    tc:null,

    initiateTextContext:function (styles) {
        if (!tcga.graph.widgets.text.tc) {
            tcga.graph.widgets.text.tc = new Ext.Element(document.createElement('div'));
            document.body.appendChild(tcga.graph.widgets.text.tc.dom);
            tcga.graph.widgets.text.tc.position('absolute');
            tcga.graph.widgets.text.tc.setLeftTop(-1000, -1000);
            tcga.graph.widgets.text.tc.hide();
        }

        tcga.graph.widgets.text.tc.setStyle(styles);
    },

    getSize:function (text) {
        tcga.graph.widgets.text.tc.update(text);
        var s = tcga.graph.widgets.text.tc.getSize();
        tcga.graph.widgets.text.tc.update('');
        return s;
    },

    /*
     * Get text height - if a width is not set in the styles, then the height will be for one line
     */
    getHeight:function (text, maxWidth, styles) {
        tcga.graph.widgets.text.initiateTextContext(styles);
        tcga.graph.widgets.text.tc.dom.style.width = (maxWidth ? maxWidth + 'px' : 'auto');

        return tcga.graph.widgets.text.getSize(text).height;
    },

    /*
     * Get text width as if it were all on one line
     */
    getWidth:function (text, styles) {
        tcga.graph.widgets.text.initiateTextContext(styles);
        tcga.graph.widgets.text.tc.dom.style.width = 'auto';

        if (text.indexOf('\n') != -1) {
            var lines = text.split('\n');
            var width = 0;
            for (var ndx = 0; ndx < lines.length; ndx++) {
                var currWidth = tcga.graph.widgets.text.getSize(lines[ndx]).width;
                width = (currWidth > width ? currWidth : width);
            }
        }
        else {
            width = tcga.graph.widgets.text.getSize(text).width;
        }

        return width;
    },

    /*
     * Break up text with \n based on a target width and a pixel height
     */
    splitText:function (text, maxWidth, styles) {
        var newStr = '';

        var currWidth = 0;
        var textList = text.split(' ');
        var spaceWidth = tcga.graph.widgets.text.getWidth('&nbsp;', styles);
        for (var ndx = 0; ndx < textList.length; ndx++) {
            var width = tcga.graph.widgets.text.getWidth(textList[ndx], styles);

            // Don't put a \n or space at the end of the string unnecessarily
            if (currWidth + width > maxWidth) {
                if (currWidth != 0) {
                    newStr += '\n' + textList[ndx] + ' ';
                    currWidth = (width + spaceWidth);
                }
                else if (currWidth == 0) {
                    newStr += textList[ndx] + '\n';
                }
            }
            else if (ndx + 1 != textList.length) {
                newStr += (textList[ndx] + ' ');
                currWidth += (width + spaceWidth);
            }
            else {
                newStr += textList[ndx];
                currWidth += width;
            }
        }
        return newStr;
    },

    maxDisplayStr:function (str, maxWidth, styles) {
        var ellipsisLength = tcga.graph.widgets.text.getWidth('...', styles);
        var displayStr = '';

        for (var ndx = 0; ndx < str.length; ndx++) {
            displayStr = str.substr(0, ndx);
            if (tcga.graph.widgets.text.getWidth(displayStr, styles) + ellipsisLength > maxWidth) {
                displayStr = str.substr(0, ndx - 1) + '...';
                break;
            }
        }

        return displayStr;
    },

    /*
     * Create a text node with the appropriate ellipses and hovering text.
     */
    displayText:function (text, config, styles) {
        // First, do a space check.  If the maxWidth or maxHeight are negative, then just return
        // 	the text to be displayed  by hovering.
        if (config.maxWidth <= 0 || (config.maxHeight <= 0 && !config.singleLine)) {
            return text;
        }

        var displayStr = '';
        var hoverText = null;
        var ellipsisLength = tcga.graph.widgets.text.getWidth('...', styles);
        var ellipsisHeight = tcga.graph.widgets.text.getHeight('...', config.maxWidth, styles);
        var textHeight = tcga.graph.widgets.text.getHeight(text, config.maxWidth, styles);

        if (config.singleLine) {
            var textWidth = tcga.graph.widgets.text.getWidth(text, styles);
            if (config.maxWidth && textWidth > config.maxWidth) {
                displayStr = tcga.graph.widgets.text.maxDisplayStr(text, config.maxWidth, styles);

                hoverText = text;
            }
            else {
                displayStr = text;
            }
        }
        else {
            if (config.maxWidth || config.maxHeight) {
                var multilineText = tcga.graph.widgets.text.splitText(text, config.maxWidth, styles);
                var width = tcga.graph.widgets.text.getWidth(multilineText, styles);
                var height = tcga.graph.widgets.text.getHeight(multilineText, config.maxWidth, styles);

                if (config.maxWidth && config.maxHeight) {
                    if ((height > config.maxHeight && width > config.maxWidth) || width > config.maxWidth) {
                        displayStr = tcga.graph.widgets.text.maxDisplayStr(text, config.maxWidth, styles);
                        hoverText = multilineText;
                    }
                    else if (height > config.maxHeight) {
                        var lines = multilineText.split('\n');
                        var potentialDisplayStr = '';
                        for (var ndx = 0; ndx < lines.length; ndx++) {
                            potentialDisplayStr += lines[ndx];
                            var pHeight = tcga.graph.widgets.text.getHeight(potentialDisplayStr, config.maxWidth, styles);
                            if (pHeight > config.maxHeight) {
                                if (ellipsisHeight > config.maxHeight) {
                                    displayStr = ' ';
                                }
                                else {
                                    displayStr = tcga.graph.widgets.text.maxDisplayStr(text, config.maxWidth, styles);
                                }
                                hoverText = multilineText;
                            }
                            else {
                                displayStr = potentialDisplayStr;
                            }
                        }
                    }
                }
                else if (config.maxWidth) {
                    if (width > config.maxWidth) {
                        displayStr = tcga.graph.widgets.text.maxDisplayStr(text, config.maxWidth, styles);
                        hoverText = multilineText;
                    }
                }
                else if (config.maxHeight) {
                    if (height > config.maxHeight) {
                        if (ellipsisHeight > config.maxHeight) {
                            displayStr = ' ';
                        }
                        else {
                            displayStr = tcga.graph.widgets.text.maxDisplayStr(text, config.maxWidth, styles);
                        }
                        hoverText = multilineText;
                    }
                }

                if (displayStr == '') {
                    displayStr = multilineText;
                }
            }
            else {
                displayStr = text;
            }
        }

        var y = config.y;
        if (config.valign == 'center') {
            var displayStrHeight = tcga.graph.widgets.text.getHeight(displayStr, config.maxWidth, styles);
            y = config.y - config.maxHeight / 2 + displayStrHeight / 2;
        }

        var textDisplay = config.paper.text(config.x, y, displayStr);
        var attrs = new Ext.util.MixedCollection();
        attrs.addAll(styles);
        attrs.eachKey(function (key, value) {
            textDisplay.attr(key, value);
        });
        if (hoverText) {
            new tcga.util.svgHoverWin({
                paper:config.paper,
                appearNear:textDisplay,
                text:hoverText,
                left:config.hoverLocX,
                top:config.hoverLocY,
                ie7:{
                    left:25,
                    top:97
                }
            });
        }

        if (hoverText) {
            return hoverText;
        }
        else {
            return null;
        }
    }
}


/*
 * Draw a node in the graph.
 *
 * This function draws the box around the node, places the image in the node and adds the label
 * below the image.
 *
 * Then the function cycles through the outputs from the node and calls the appropriate path
 * function to draw each one.
 */
tcga.graph.widgets.drawNode = function (config) {
    // Add the image to the node
    var node = tcga.graph.widgets.imageOrTextNode(config);

    /*
     * If there are listeners on the node itself, pass those to the listener.create function.
     *
     * Note that this is only adding the listeners to the image.  This is an area that should
     * be improved to cover the rest of the node box.
     */
    if (config.listeners) {
        tcga.graph.listeners.create(node, config.listeners);
    }

    // If there are no outputs configured, we're done!
    if (!config.outputs) {
        // There is no connecting path, so we return null, since there will be no following
        //		node to use a new y value.
        return null;
    }

    /*
     * Now draw the outputs.  First, collect some information.
     *
     * totalCount - the sum of the counts in the outputs
     * connectingPathNdx - the index of the output that connects to the next node
     * upCount - the number of outputs that point up
     * downCount - the number of outputs that point down
     */
    var totalCount = tcga.graph.widgets.totalCount(config.outputs);
    var connectingPathNdx = tcga.graph.widgets.getConnectingPathNdx(config.outputs);
    var upCount = connectingPathNdx;
    var downCount = config.outputs.length - connectingPathNdx - 1;

    /*
     * Calculate the amount of vertical space to use for drawing the outputs.
     *
     * vOffset - the number of pixels to offset from the bottom of the node, this is an
     * aesthetic adjustment only.
     */
    var vNodeOffset = 8;
    if (config.scale != null) {
        vNodeOffset += (config.sizeV - config.pathHeight) / 2;
    }

    /*
     * Set up a bunch of starting parameters about the location to start drawing from.
     *
     * pathHeight - stores the current output height.
     * pathDir - the direction of the current output, 'up' or 'down', not used for the output
     * 	that connects to the next node.
     * vertLoc - the current Y location at which to start drawing the output.  Note that this is the
     * 	middle of the path and the width of the path goes out evenly on either side of the vertLoc.
     * outputLoc - the current X location at which to start drawing the output.
     * lengthDivider - used to figure out how far along to bend those elbows so that they are more
     * 	or less evenly spaced.
     * nextNodeCenter - the y coordinate of the center of the connecting path, this is returned and
     * 	and used by the next node for positioning if the "center" option is true
     */
    var pathHeight = 0;
    var vOffset = 40;
    var hOffset = 20;
    var vertLoc = config.vertLoc - config.sizeV + vNodeOffset;
    var outputLoc = config.horizLoc + config.sizeH;
    var lengthDivider = 0;
    var nextNodeCenter = 0;
    var upNdx = 0;
    var downNdx = downCount;
    var labelOffset = 30;
    for (var ndx = 0; ndx < config.outputs.length; ndx++) {
        var pathConfig = config.outputs[ndx];
        var pathDir = config.outputs[ndx].pathDir;
        pathHeight = config.pathHeight * (pathConfig.count / totalCount);
        if (pathConfig.minPathWidth && pathConfig.minPathWidth > 0) {
            pathHeight = (pathHeight > pathConfig.minPathWidth ? pathHeight : pathConfig.minPathWidth);
        }
        vertLoc += pathHeight / 2;

        // Set up the config for the path using our current location calculations.
        var localConfig = {};
        Ext.apply(localConfig, pathConfig);
        Ext.apply(localConfig, {
            paper:config.paper,
            startX:outputLoc,
            startY:vertLoc,
            width:pathHeight,
            numericLabel:config.numericLabel
        });

        // Draw a path that connects to the next node, which is always a straight path.
        var length = (pathConfig.length ? pathConfig.length : config.pathLength);
        if (pathConfig.connectToNextNode) {
            tcga.graph.widgets.path(Ext.applyIf(localConfig, {
                length:length
            })
            );

            pathDir = 'down';
            lengthDivider = downCount + 1;

            nextNodeCenter = vertLoc;
        }
        // Draw an elbow path pointing up
        else if (pathDir == 'up') {
            // Figure out the vLength.  Poke up above the node so as to try to get less conflict
            //		between the label and the surrounding nodes
            var vLength = -(config.sizeV + vertLoc - config.vertLoc) - (vOffset * (upCount - upNdx - 1));

            tcga.graph.widgets.elbowPath(Ext.applyIf(localConfig, {
                elbowType:'hv',
                vLength:vLength,
                hLength:length * ((upNdx + 1) / (upCount + 1)),
                labelOffsetH:hOffset,
                labelOffsetV:labelOffset
            })
            );

            upNdx++;
        }
        // Draw an elbow path pointing down (the only other choice)
        else {
            lengthDivider--;
            var vLength = (config.vertLoc - vertLoc) + (vOffset * (downCount - downNdx + 1));

            tcga.graph.widgets.elbowPath(Ext.applyIf(localConfig, {
                elbowType:'hv',
                vLength:vLength,
                hLength:length * (downNdx / (downCount + 1)),
                labelOffsetH:hOffset,
                labelOffsetV:labelOffset
            })
            );

            downNdx--;
        }

        vertLoc += pathHeight / 2;
    }

    return nextNodeCenter;
}

