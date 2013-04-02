var selectedCellInCols = new Array();
var selectedCellInRows = new Array();
var selectedSamples = new Array();
var selectedColHeaderChildren = new Array();

//flips state between Selected and not. Handled in JS only (no server call)
function toggleCell(cell) {
    var totalSelectableCol;
    var totalSelectableRow;
    var selectedCellsID = cell.id;
    var selectedSamplePosition = selectedCellsID.split("_cellID_");
    var selectedCellID = selectedSamplePosition[1];
    var selectedSampleID = selectedSamplePosition[0].split("column")[0];
    var columnNum = selectedSamplePosition[0].split("column")[1];
    levelHeaders[columnNum].length > 0 ? totalSelectableCol = levelHeaders[columnNum].length : totalSelectableCol = cellHeaders[columnNum].split(",").length - 1;
    totalSelectableRow = cellHeaders[selectedSampleID].split(",").length;

    if (selectedCells[selectedCellID] == null) {
        selectCell(cell, "cell", selectedCellID, selectedSampleID, columnNum, totalSelectableCol, totalSelectableRow);
    } else {
        unSelectCell(cell, "cell", selectedCellID, selectedSampleID, columnNum, totalSelectableCol, totalSelectableRow);
    }
}

//selection of cells selects cells and updates parent headers
function selectCell(cell, source, selectedCellID, selectedSampleID, columnNum, totalSelectableCol, totalSelectableRow) {
    selectedCells[selectedCellID] = selectedCellID;
    cell.className = 'cellselected';
    if (selectedCellInCols[columnNum] == undefined || selectedCellInCols[columnNum] == "null") {
        selectedCellInCols[columnNum] = 1;
    } else {
        selectedCellInCols[columnNum]++;
    }
    if (selectedCellInCols[columnNum] == totalSelectableCol && source != "headerCol") {
        markSelectedLevelHeader(levelNamesArray[columnNum]);
    }
    if (selectedCellInRows[selectedSampleID] == undefined) {
        selectedCellInRows[selectedSampleID] = 1;
    } else {
        selectedCellInRows[selectedSampleID]++;
    }
    if (selectedCellInRows[selectedSampleID] == totalSelectableRow) {
        markSelectedSampleHeader(selectedSampleID);
    }
}

//unselection of cells unselects cells and updates parent headers
function unSelectCell(cell, source, selectedCellID, selectedSampleID, columnNum, totalSelectableCol, totalSelectableRow) {
    selectedCells[selectedCellID] = null;
    cell.className = 'cell selectable';

    if (selectedCellInCols[columnNum] == totalSelectableCol & source != "headerCol") {
        markUnSelectedLevelHeader(levelNamesArray[columnNum]);
    }
    selectedCellInCols[columnNum]--;
    if (selectedCellInRows[selectedSampleID] == totalSelectableRow) {
        markUnSelectedSampleHeader(selectedSampleID);
    }
    selectedCellInRows[selectedSampleID]--;

}
//gathers all selected cells into a comma-separated list to send as POST parameter
function concatCellIds() {
    var cellIds = "";
    var key;
    var first = true;
    for (key in selectedCells) {
        if (selectedCells[key] != null && Ext.isPrimitive(selectedCells[key])) {
            if (!first) cellIds += ",";
            first = false;
            cellIds += key;
        }
    }
    var selectedCellsInput = document.getElementById("selectedCells");
    selectedCellsInput.value = cellIds;
    return cellIds;
}

// pass this the action string, and the hiddenform will be submitted
function submitForm(actionStr) {
    var formObj = document.hiddenform;
    formObj.action = actionStr;
    formObj.outerScroll.value = YAHOO.util.Dom.getDocumentScrollTop();
    if (document.getElementById('divContScroll')) {
        formObj.innerScroll.value = document.getElementById('divContScroll').scrollTop;
    }

    if (document.getElementById('scrollSizer')) {
        formObj.scrollSize.value = document.getElementById('scrollSizer').options[document.getElementById('scrollSizer').selectedIndex].value;
    }

    formObj.showMatrix.value = 'true';
    formObj.filterDisplayStyle.value = 'none';
    document.hiddenform.submit();
}

//flips header state between Selected or not. Involves a call to the server
function toggleHeader(event, header, columnStart) {
    if (columnStart == null) {
        //all headers except Center and platform headers
        if (tcga.util.hasClass(header, "selected")) {
            unSelectHeader(header.id);
        } else {
            selectHeader(header.id);
        }
    } else {
        //Center and platform Column headers
        if (tcga.util.hasClass(header, "selected")) {
            unSelectColumnHeader(header.id, columnStart);
        } else {
            selectColumnHeader(header.id, columnStart);
        }
    }
}

//selecting batch and level headers selects cells
function selectHeader(headerid) {
    var cellDomLocations = new Array();
    var headerIndex = headerid;

    if (headerid.indexOf("column") > -1) {
        //mark selected header and update header relationships
        markSelectedLevelHeader(headerid);

        headerIndex = parseInt(headerid.split("column")[1].split("_")[0]);

        if (cellHeaders[headerIndex + ""] != null) {
            cellDomLocations = cellHeaders[headerIndex].replace("undefined", "").split(",");
            for (var l = 0; l < cellDomLocations.length - 1; l++) {
                levelHeaders[headerIndex].push(document.getElementById(cellDomLocations[l]));
            }
            cellHeaders[headerIndex + ""] = null;
        }
        if (levelHeaders[headerIndex] != undefined) {
            for (var k = 0; k < levelHeaders[headerIndex].length; k++) {
                cell = levelHeaders[headerIndex][k];
                cellid = cell.id;
                cellIdParts = cellid.split("column");
                columnNum = cellIdParts[1].split("_")[0];
                selectCellNum = cellid.split("_cellID_")[1];
                selectedSampleID = cellIdParts[0];
                levelHeaders[columnNum].length > 0 ? totalSelectableCol = levelHeaders[columnNum].length : totalSelectableCol = cellHeaders[columnNum].split(",").length - 1;
                if (selectedCells[selectCellNum] == null) {
                    selectCell(cell, "headerCol", selectCellNum, selectedSampleID, columnNum, totalSelectableCol, cellHeaders[selectedSampleID].split(",").length);
                }
            }
        }

    } else if (cellHeaders[headerIndex] != undefined) {
        //the string is likely to lead with undefined if first batch had no 'availables'
        var idString = cellHeaders[headerIndex].replace("undefined,", "");

        if (idString.length > 0) {
            var idArray = idString.split(",");

            if (headerIndex.indexOf("_sample") == -1 && headerIndex.indexOf("column") == -1) {
                //if its batch header clicked, recursively apply to each sample id
                for (var j = 0; j < idArray.length; j++) {
                    selectHeader(idArray[j]);
                }

            } else {
                for (var i = 0; i < idArray.length; i++) {
                    cellid = idArray[i];
                    cell = document.getElementById(cellid);
                    cellIdParts = cellid.split("column");
                    columnNum = cellIdParts[1].split("_")[0];
                    selectCellNum = cellid.split("_cellID_")[1];
                    selectedSampleID = cellIdParts[0];
                    levelHeaders[columnNum].length > 0 ? totalSelectableCol = levelHeaders[columnNum].length : totalSelectableCol = cellHeaders[columnNum].split(",").length - 1;

                    if (selectedCells[selectCellNum] == null) {
                        selectCell(cell, "header", selectCellNum, selectedSampleID, columnNum, totalSelectableCol, cellHeaders[selectedSampleID].split(",").length);
                    }
                }
            }
        } else if (!tcga.util.hasClass(document.getElementById(headerIndex), "selected")) {
            markSelectedSampleHeader(headerIndex);
        }
    }
}

//Clicking on batch and level headers selects header then auto clicks child sample headers
function unSelectHeader(headerid) {
    var cellDomLocations = new Array();
    var headerIndex = headerid;

    if (headerid.indexOf("column") > -1) {
        //mark unselected header and update header relationships
        markUnSelectedLevelHeader(headerid);

        headerIndex = parseInt(headerid.split("column")[1].split("_")[0]);

        if (cellHeaders[headerIndex + ""] != null) {
            cellDomLocations = cellHeaders[headerIndex].replace("undefined", "").split(",");
            for (var l = 0; l < cellDomLocations.length - 1; l++) {
                levelHeaders[headerIndex].push(document.getElementById(cellDomLocations[l]));
            }
            cellHeaders[headerIndex + ""] = null;
        }
        if (levelHeaders[headerIndex] != undefined) {
            for (var k = 0; k < levelHeaders[headerIndex].length; k++) {
                cell = levelHeaders[headerIndex][k];
                cellid = cell.id;
                cellIdParts = cellid.split("column");
                columnNum = cellIdParts[1].split("_")[0];
                unSelectCellNum = cellid.split("_cellID_")[1];
                selectedSampleID = cellIdParts[0];
                levelHeaders[columnNum].length > 0 ? totalSelectableCol = levelHeaders[columnNum].length : totalSelectableCol = cellHeaders[columnNum].split(",").length - 1;
                if (selectedCells[unSelectCellNum] != null) {
                    unSelectCell(cell, "headerCol", unSelectCellNum, selectedSampleID, columnNum, totalSelectableCol, cellHeaders[selectedSampleID].split(",").length);
                }
            }
        }

    } else if (cellHeaders[headerIndex] != undefined) {
        //the string is likely to lead with undefined if first batch had no 'availables'
        var idString = cellHeaders[headerIndex].replace("undefined,", "");

        if (idString.length > 0) {
            var idArray = idString.split(",");

            if (headerIndex.indexOf("_sample") == -1 && headerIndex.indexOf("column") == -1) {
                //if its batch header clicked, recursively apply to each sample id
                for (var j = 0; j < idArray.length; j++) {
                    unSelectHeader(idArray[j]);
                }
            } else {
                for (var i = 0; i < idArray.length; i++) {
                    cellid = idArray[i];
                    cell = document.getElementById(cellid);
                    cellIdParts = cellid.split("column");
                    columnNum = cellIdParts[1].split("_")[0];
                    unSelectCellNum = cellid.split("_cellID_")[1];
                    selectedSampleID = cellIdParts[0];
                    levelHeaders[columnNum].length > 0 ? totalSelectableCol = levelHeaders[columnNum].length : totalSelectableCol = cellHeaders[columnNum].split(",").length - 1;

                    if (selectedCells[unSelectCellNum] != null) {
                        unSelectCell(cell, "headerRow", unSelectCellNum, selectedSampleID, columnNum, totalSelectableCol, cellHeaders[selectedSampleID].split(",").length);
                    }
                }
            }
        } else {
            markUnSelectedSampleHeader(headerIndex);
        }
    }
}

//select sample header... and update header parents
function markSelectedSampleHeader(selectedSampleID) {
    tcga.util.addClass(document.getElementById(selectedSampleID), "selected");
    selectedBatchID = selectedSampleID.split("sample")[0];
    batchNum = selectedBatchID.split("batch")[1].split("_")[0];
    if (selectedSamples[selectedBatchID] == undefined) {
        selectedSamples[selectedBatchID] = 1;
    } else {
        selectedSamples[selectedBatchID]++;
    }
    if (selectedSamples[selectedBatchID] == batchArray[batchNum][1]) {
        tcga.util.addClass(document.getElementById(selectedBatchID), "selected");
    }
}

//unselect sample header... and update header parents
function markUnSelectedSampleHeader(selectedSampleID) {
    tcga.util.removeClass(document.getElementById(selectedSampleID), "selected");
    selectedBatchID = selectedSampleID.split("sample")[0];
    batchNum = selectedBatchID.split("batch")[1].split("_")[0];
    if (selectedSamples[selectedBatchID] == batchArray[batchNum][1]) {
        tcga.util.removeClass(document.getElementById(selectedBatchID), "selected");
    }
    selectedSamples[selectedBatchID]--;
}

//select level header... and update header parents
function markSelectedLevelHeader(headerid) {
    if (!tcga.util.hasClass(document.getElementById(headerid), "selected")) {
        tcga.util.addClass(document.getElementById(headerid), "selected");
        centerParent = headerid.split("_l")[0] + "_";
        platformParent = headerid.split("_c")[0] + "_";

        if (selectedColHeaderChildren[centerParent] == undefined) {
            selectedColHeaderChildren[centerParent] = 1;
        } else {
            selectedColHeaderChildren[centerParent]++;
        }
        if (selectedColHeaderChildren[centerParent] == document.getElementById(centerParent).colSpan) {
            tcga.util.addClass(document.getElementById(centerParent), "selected");
        }

        if (selectedColHeaderChildren[platformParent] == undefined) {
            selectedColHeaderChildren[platformParent] = 1;
        } else {
            selectedColHeaderChildren[platformParent]++;
        }

        if (selectedColHeaderChildren[platformParent] == document.getElementById(platformParent).colSpan) {
            tcga.util.addClass(document.getElementById(platformParent), "selected");
        }
    }
}

//unselect level header... and update header parents
function markUnSelectedLevelHeader(headerid) {
    if (tcga.util.hasClass(document.getElementById(headerid), "selected")) {
        tcga.util.removeClass(document.getElementById(headerid), "selected");
        centerParent = headerid.split("_l")[0] + "_";
        platformParent = headerid.split("_c")[0] + "_";

        if (selectedColHeaderChildren[centerParent] == document.getElementById(centerParent).colSpan) {
            tcga.util.removeClass(document.getElementById(centerParent), "selected");
        }
        if (selectedColHeaderChildren[platformParent] == document.getElementById(platformParent).colSpan) {
            tcga.util.removeClass(document.getElementById(platformParent), "selected");
        }

        selectedColHeaderChildren[centerParent]--;
        selectedColHeaderChildren[platformParent]--;
    }
}

//Selecting Center and Platform headers recursively figures out 
//which level headers to select
function selectColumnHeader(headerid, j) {
    var i = 0;
    var k = 0;
    var currentCenterId;
    var currentLevelId;

    if (headerid.indexOf('c') > -1) {

        currentLevelId = headerid + 'l' + i + '_column' + j + '_';
        while (document.getElementById(currentLevelId) != null) {
            while (document.getElementById(currentLevelId) != null) {
                selectHeader(currentLevelId);
                currentLevelId = headerid + 'l' + ++i + '_column' + ++j + '_';
            }
            i = 0;
            currentLevelId = headerid + 'l' + i + '_column' + j + '_';
        }
    } else if (headerid.indexOf('p') > - 1) {
        currentCenterId = headerid + 'c' + k + '_';
        while (document.getElementById(currentCenterId) != null) {
            j = selectColumnHeader(currentCenterId, j);
            currentCenterId = headerid + 'c' + ++k + '_';
        }
    }
    //returns the next column if you need it.
    return j;
}


//Unselecting Center and Platform headers recursively figures out 
//which level headers to unselect
function unSelectColumnHeader(headerid, j) {
    var i = 0;
    var k = 0;
    var currentCenterId;
    var currentLevelId;

    if (headerid.indexOf('c') > -1) {

        currentLevelId = headerid + 'l' + i + '_column' + j + '_';
        while (document.getElementById(currentLevelId) != null) {
            while (document.getElementById(currentLevelId) != null) {
                unSelectHeader(currentLevelId);
                currentLevelId = headerid + 'l' + ++i + '_column' + ++j + '_';
            }
            i = 0;
            currentLevelId = headerid + 'l' + i + '_column' + j + '_';
        }
    } else if (headerid.indexOf('p') > - 1) {
        currentCenterId = headerid + 'c' + k + '_';
        while (document.getElementById(currentCenterId) != null) {
            j = unSelectColumnHeader(currentCenterId, j);
            currentCenterId = headerid + 'c' + ++k + '_';
        }
    }
    //returns the next column if you need it.
    return j;
}

//returns currently selected headers
function activeHeaders() {
    var activeHeadersArray = [];
    var activeCells = document.getElementById("scrollingTable").getElementsByTagName("td");
    var columnHeaderCells = document.getElementById("matrixHeader").getElementsByTagName("td");
    for (var i = 0; i < activeCells.length; i++) {
        if (activeCells[i].id.indexOf("header_") > -1 && tcga.util.hasClass(activeCells[i], "selected")) {
            activeHeadersArray.push(activeCells[i].id);
        }
    }
    for (var j = 0; j < columnHeaderCells.length; j++) {
        if (columnHeaderCells[j].id.indexOf("header_") > -1 && tcga.util.hasClass(columnHeaderCells[j], "selected")) {
            activeHeadersArray.push(columnHeaderCells[j].id);
        }
    }
    return activeHeadersArray;
}


function getWebServiceUrl() {
    var errors = [];
    var numErrors = 0;

    combineSampleFilterPieces();
    var filterForm = document.getElementById('hiddenform');
    var url = 'damws/jobprocess/xml?';
    url += makeUrlForParameter('disease', filterForm.diseaseType.value);
    url += makeUrlForMultiSelectParameter(filterForm.batch);

    url += makeUrlForMultiSelectParameter(filterForm.platformType);
    if (getNumSelected(filterForm.platformType) > 1) {
        errors[numErrors++] = 'Only one data type can be selected for web service queries.';
    }
    var clinicalSelected = false;
    for (var i = 0; i < filterForm.platformType.options.length; i++) {
        if (filterForm.platformType.options[i].selected) {
            if (filterForm.platformType.options[i].value == '-999') {
                clinicalSelected = true;
            }
        }
    }

    var centerPlatform = filterForm.center;
    var hasCenterPlatform = false;
    var centerString = '';
    var platformString = '';
    for (var i = 0; i < centerPlatform.options.length; i++) {
        if (centerPlatform.options[i].selected && centerPlatform.options[i].value != '') {
            var value = centerPlatform.options[i].value;
            var indexOfDot = value.indexOf('.');
            var centerId = value.substring(0, indexOfDot);
            var platform = value.substring(indexOfDot + 1);
            centerString += centerId + ",";
            platformString += platform + ",";
            hasCenterPlatform = true;
        }
    }
    url += buildUrlForMultipleParams("center", centerString);
    url += buildUrlForMultipleParams("platform", platformString);

    if (! hasCenterPlatform && !clinicalSelected) {  // no center/platform needed for clinical
        errors[numErrors++] = 'A center/platform must be selected';
    }
    var levelUrl = makeUrlForCheckboxParameter(filterForm.level);
    if (levelUrl != '') {
        url += levelUrl;
    } else if (!clinicalSelected) {
        errors[numErrors++] = 'A data level must be selected';
    }
    url += makeUrlForCheckboxParameter(filterForm.protectedStatus);
    url += makeUrlForCheckboxParameter(filterForm.tumorNormal);
    url += makeUrlForParameter('startDate', filterForm.startDate.value);
    url += makeUrlForParameter('endDate', filterForm.endDate.value);
    var sampleList = filterForm.sampleList.value;
    if (filterForm.samplesFromDropdowns && filterForm.samplesFromDropdowns.value != null) {
        if (sampleList != '') {
            sampleList += ',';
        }
        sampleList += filterForm.samplesFromDropdowns.value;
    }
    if (sampleList != '') {
        url += '&sampleList=' + escape(sampleList);
    }

    var serverUrl = document.location.href;
    var tcgaStart = serverUrl.indexOf("/tcga/");
    serverUrl = serverUrl.substring(0, tcgaStart + 6);
    var displayDiv = document.getElementById("webServiceUrl");
    displayDiv.style.display = 'block';

    var valueForTextArea;
    if (numErrors == 0) {
        valueForTextArea = serverUrl + url;
    } else {
        valueForTextArea = "This filter cannot be used with the web service. Errors:\n";
        for (var i = 0; i < numErrors; i++) {
            valueForTextArea += ' - ' + errors[i] + '\n';
        }
    }
    document.getElementById("webServiceUrlTextArea").value = valueForTextArea;

}

function makeUrlForParameter(name, value) {
    if (value != null && value != '' && value != 'mm/dd/yyyy') {
        return '&' + name + '=' + escape(value);
    } else {
        return '';
    }
}

function getNumSelected(multiselect) {
    var num = 0;
    for (var i = 0; i < multiselect.options.length; i++) {
        if (multiselect.options[i].selected) {
            num++;
        }
    }
    return num;
}

function makeUrlForMultiSelectParameter(select) {
    var url = '';
    var value = '';
    for (var i = 0; i < select.options.length; i++) {
        if (select.options[i].selected) {
            value += select.options[i].value + ',';
        }
    }
    if (value != '') {
        // remove last comma
        value = value.substring(0, value.length - 1);
    }
    url += makeUrlForParameter(select.name, value);
    return url;
}

function buildUrlForMultipleParams(param, value) {
    var url = '';
    if (value != '') {
        // remove last comma
        value = value.substring(0, value.length - 1);
    }
    url += makeUrlForParameter(param, value);
    return url;
}

function makeUrlForCheckboxParameter(checkBoxes) {
    var url = '';
    var value = '';
    for (var i = 0; i < checkBoxes.length; i++) {
        if (checkBoxes[i].checked) {
            value += checkBoxes[i].value + ',';
        }
    }
    if (value != '') {
        // remove last comma
        value = value.substring(0, value.length - 1);
    }
    url += makeUrlForParameter(checkBoxes[0].name, value);
    return url;
}

function checkFilterDates() {
    //make sure dates are not future
    var now = new Date().getTime();
    var startdate = document.getElementById("startDate").value;
    var enddate = document.getElementById("endDate").value;
    var startdateMillis = 0;
    var enddateMillis = 0;
    if (startdate != null && startdate.length > 0) {
        startdateMillis = Date.parse(startdate);
    }
    if (enddate != null && enddate.length > 0) {
        enddateMillis = Date.parse(enddate);
    }
    if (!isNaN(startdateMillis) && startdateMillis > now) {
        alert(startDateGreaterThanToday);
        return false;
    }
    if (!isNaN(enddateMillis) && enddateMillis > now) {
        alert(badEndDate);
        return false;
    }
    if (!isNaN(startdateMillis) && !isNaN(enddateMillis) && startdateMillis > enddateMillis) {
        alert(startDateGreaterThanEndDate);
        return false;
    }
    return true;
}

//combine sample Id parts into a single token for each sample and load into the arg3 hidden input
function combineSampleFilterPieces() {
    var part2Selects = YAHOO.util.Dom.getElementsByClassName('samplePart2', 'select', 'sampleWidgets');//document.getElementsByName("samplePart2");
    var part3Texts = YAHOO.util.Dom.getElementsByClassName('samplePart3', 'input', 'sampleWidgets');//document.getElementsByName("samplePart3");
    var part4Selects = YAHOO.util.Dom.getElementsByClassName('samplePart4', 'select', 'sampleWidgets');//document.getElementsByName("samplePart4");
    var sampleList = "";
    for (var i = 0; i < part2Selects.length; i++) {
        var s = "";
        if (part2Selects[i].selectedIndex >= 1) {
            s += part2Selects[i].options[part2Selects[i].selectedIndex].value;
        } else {
            s += "*";
        }
        s += "-";
        if (part3Texts[i].value != null && part3Texts[i].value != "" && part3Texts[i].value != "null") {
            s += part3Texts[i].value;
        } else {
            s += "*";
        }
        s += "-";
        if (part4Selects[i].selectedIndex >= 1) {
            s += part4Selects[i].options[part4Selects[i].selectedIndex].value;
        } else {
            s += "*";
        }
        if (s != "*-*-*") {
            sampleList += ("TCGA-" + s + ",");
        }
    }
    if (sampleList != "") {
        sampleList = sampleList.substring(0, sampleList.length - 1);
        var arg3 = document.getElementById("arg3");
        arg3.name = "samplesFromDropdowns";
        arg3.value = sampleList;
    }
}

// show or hide the filter panel/dialog thingie
function toggleFilter() {

    if (document.getElementById('filter').style.display == 'none') {
        // show filter
        blurred(document.getElementById('endDate'), 'mm/dd/yyyy');
        blurred(document.getElementById('startDate'), 'mm/dd/yyyy');
        document.getElementById('filter').style.display = 'block';
        document.getElementById('matrixContainer').style.display = 'none';
        document.getElementById('menuContainer').style.display = 'none';

        var defaultWidth = 978;
        var containerWidth = document.getElementById('container').style.width;
        if (containerWidth != defaultWidth) {
            document.getElementById('mainnav').style.width = (defaultWidth) + 'px';
            document.getElementById('container').style.width = (defaultWidth) + 'px';
            document.getElementById('footer').style.width = (defaultWidth) + 'px';
            document.getElementById('nci-banner').style.width = '102%';
        }
    } else {
        if (adjustedWidth > 950) {
            document.getElementById('nci-banner').style.width = 90 + adjustedWidth + "px";
            document.getElementById('mainnav').style.width = 60 + adjustedWidth + "px";
            document.getElementById('container').style.width = 60 + adjustedWidth + "px";
            document.getElementById('footer').style.width = 60 + adjustedWidth + "px";
        }
        // hide filter
        document.getElementById('filter').style.display = 'none';
        document.getElementById('matrixContainer').style.display = 'block';
        document.getElementById('menuContainer').style.display = 'block';
        adjustMatrixWidth();
    }
}

function removeFilter() {
    var url = contextPath + "/dataAccessMatrix.htm?mode=ApplyFilter&showMatrix=true&clearMatrix=true&diseaseType=" + diseaseType + "&colorSchemeName=" + document.hiddenform.colorSchemeName.value;
    window.location = url;
}

function clearFilterForm() {
    for (var i = 0; i < document.hiddenform.elements.length; i++) {
        var elem = document.hiddenform.elements[i];
        if (elem.type && (elem.type.toLowerCase() == 'text' ||
            elem.type.toLowerCase() == 'textarea' ||
            elem.type.toLowerCase() == 'file')) {
            elem.value = "";
        } else if (elem.type && elem.type.toLowerCase() == 'checkbox') {
            elem.checked = false;
        } else if (elem.type && elem.type.toLowerCase() == 'select-multiple') {
            elem.selectedIndex = -1;
        } else if (elem.type && elem.type.toLowerCase() == 'select-one') {
            elem.selectedIndex = 0;
        }
    }
}

var headersFrozen = false;
function windowScrolled() {
    if (headersFrozen) {
        checkPositions();
    }
}

function windowResized() {
    if (headersFrozen) {
        checkPositions();
    }
}

function setScrollRegion(size) {
    if (size == 'null' || size == '') {
        return;
    }
    document.hiddenform.scrollSize.value = size;
    document.getElementById('divContScroll').style.height = size.replace("100", "100%");
    tcga.util.setCookie("tcgaportal_currentscrollsize", size, 365);
}

function setScrollAmount() {
    if (document.hiddenform.outerScroll != 'null') {
        window.scrollTo(0, document.hiddenform.outerScroll.value);
    }
    if (document.hiddenform.innerScroll != 'null' && document.getElementById('divContScroll').style.height != '100') {
        document.getElementById('divContScroll').scrollTop = document.hiddenform.innerScroll.value;
    }
}

function makeOption(name, value, isSelected) {
    var option = document.createElement("option");
    option.appendChild(document.createTextNode(name));
    option.setAttribute("value", value);
    //option.innerText = name;
    if (isSelected) {
        option.setAttribute("selected", "selected");
    }
    return option;
}

function updateCalendar(cal, date) {
    if (date && date != "") {
        var a = /(\d+)\/(\d+)\/(\d+)/.exec(date);
        if (a) {
            cal.select(date);
            cal.cfg.setProperty("pagedate", a[1] + "/" + a[3]);
            cal.render();
        }
    }
}

function focused(input, hint) {
    if (input) {
        if (input.value == hint) {
            input.value = "";
        }
        input.style.color = "#000000";
    }
}

function blurred(input, hint) {
    if (input) {
        if (input.value == hint || input.value.length == 0) {
            input.style.color = "#D3D3D3";
            input.value = hint;
        }
    }
}

function toggleDiseaseSwitcher() {
    if (document.getElementById("diseaseSwitcher").style.visibility == 'hidden') {
        document.getElementById("diseaseSwitcher").style.left = YAHOO.util.Dom.getX(document.getElementById('pageTitle')) + "px";
        document.getElementById("diseaseSwitcher").style.top = (YAHOO.util.Dom.getY(document.getElementById('pageTitle')) + 20) + "px";
        document.getElementById("diseaseSwitcher").style.visibility = 'visible';
    } else {
        document.getElementById("diseaseSwitcher").style.visibility = 'hidden';
    }
}

function adjustMatrixWidth() {
    var matrixWidth = document.getElementById('matrixContainer').style.width;
    matrixWidth = new Number(matrixWidth.slice(0, matrixWidth.length - 2));
    if (matrixWidth > 948) {
        document.getElementById('mainnav').style.width = (matrixWidth + 30) + 'px';
        document.getElementById('container').style.width = (matrixWidth + 30) + 'px';
        document.getElementById('footer').style.width = (matrixWidth + 30) + 'px';
    }
}

//simply clears what is currently selected by the user
//the code simply unSelects all batches
function clearFilter() {
    //clear ui column header selections displays
    var firstPlatformHeaderId = "header_p0_";
    var j = 0;
    var k = 0;
    while (document.getElementById(firstPlatformHeaderId) != null) {
        k = unSelectColumnHeader(firstPlatformHeaderId, k);
        firstPlatformHeaderId = "header_p" + ++j + "_";
    }
}

function setColorScheme(scheme) {

    //show loding gif in case the next page is taking a while to load
    document.getElementById('mainnav').style.display = 'none';
    document.getElementById('container').style.display = 'none';
    document.getElementById('footer').style.display = 'none';
    document.getElementById('waitingDiv').style.display = 'block';

    //set to new scheme and save in cookie for a year
    document.hiddenform.colorSchemeName.value = scheme;
    tcga.util.setCookie("tcgaportal_colorschemename", scheme, 365);

    //use the same url we are on... no need for new url since we are simply reloading page and only change is the new scheme
    var url = "";

    //gather selected headers before submitting form
    document.hiddenform.selectedHeaders.value = activeHeaders();
    document.hiddenform.selectedCellInCols.value = selectedCellInCols;

    concatCellIds();
    submitForm(url);
}

function submitFilter() {
    var url = contextPath + "/dataAccessMatrix.htm?mode=ApplyFilter";
    url += ("&diseaseType=" + diseaseType);
    if (!checkFilterDates()) {
        return;
    }
    document.getElementById("arg3").value = "";
    combineSampleFilterPieces();
    submitForm(url);
}

function changeToNewDisease( diseaseSwitch ) {
	if (diseaseSwitch != null) {
		document.hiddenform.diseaseType.value = diseaseSwitch;
	}
    document.hiddenform.platformType.value="";
    document.hiddenform.batch.value="";
    document.hiddenform.center.value="";
    document.hiddenform.showMatrix.name = "showMatrixNotActive";

    var url = contextPath + "/dataAccessMatrix.htm?mode=ApplyFilter";
    document.getElementById("arg3").value = "";
    submitForm(url);
}

