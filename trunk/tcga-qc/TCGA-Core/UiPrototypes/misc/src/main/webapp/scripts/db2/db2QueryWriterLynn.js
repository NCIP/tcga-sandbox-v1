Ext.namespace('tcga.db2.queryWriter');

tcga.db2.queryWriter.templateFunctions = 	{
	isNotNull: function(valName, vals) {
		return (vals[valName] != null && vals[valName] != ''?true:false);
	},
	isNull: function(valName, vals) {
		return (vals[valName] != null && vals[valName] != ''?false:true);
	}
};

tcga.db2.queryWriter.reset = function() {
	Ext.get('queryList').update('Please select a way to start your analysis.');
}

tcga.db2.queryWriter.toggleQuery = function(queryNum) {
	var query = tcga.db2.query.storage.getQuery(queryNum);

	query.update('closed', (query.closed?false:true));
	tcga.db2.queryWriter.start();
}

tcga.db2.queryWriter.writeQueryClass = function(title, queryClass) {
	var htmlFrags = [];
	
	var titleWritten = false;
	for (var name in queryClass.values) {
		var order = queryClass.values[name].order;
		htmlFrags[order] = '';
		if (!titleWritten) {
			htmlFrags[order] += '<div class="queryLabel">' + title + ':</div>';
			if (queryClass.name) {
				htmlFrags[order] += '<div class="queryClassName">' + queryClass.name + '</div>';
			}
			
			titleWritten = true;
		}
		
		var valueHtml = '';
		if (queryClass.values[name].type == 'checkbox') {
			for (var ndx = 0;ndx < queryClass.values[name].value.length;ndx++) {
				valueHtml += (ndx != 0?', ':'') + queryClass.values[name].value[ndx];
			}
		}
		else if (queryClass.values[name].type == 'combo') {
				valueHtml = queryClass.values[name].desc;
		}
		else if (queryClass.values[name].type == 'slider') {
			valueHtml = queryClass.values[name].value.start + ' - ' + queryClass.values[name].value.end;
		}
		else if (queryClass.values[name].type == 'lossGainSlider') {
			if (queryClass.values[name].value.lossStart) {
				valueHtml += '<b>Loss:</b> ' + queryClass.values[name].value.lossStart + ' fold to ' + queryClass.values[name].value.lossEnd + ' or less';
				if (queryClass.values[name].value.percent) {
					valueHtml += ' in ' + queryClass.values[name].value.percent + '% of patients';
				}
			}
			if (queryClass.values[name].value.gainStart) {
				valueHtml += (valueHtml.length > 0?'<br/>':'');
				valueHtml += '<b>Gain:</b> ' + queryClass.values[name].value.gainStart + ' fold to ' + queryClass.values[name].value.gainEnd + ' or more';
				if (queryClass.values[name].value.percent) {
					valueHtml += ' in ' + queryClass.values[name].value.percent + '% of patients';
				}
			}
			
			valueHtml = '<br/>' + valueHtml;
		}
		else {
			value = queryClass.values[name].name;
		}
		
		htmlFrags[order] += '<div class="queryValue"><span class="queryValueLabel">' + name + ':</span> ' + valueHtml + '</div>';
	}

	var html = '';
	for (var ndx = 0;ndx < htmlFrags.length;ndx++) {
		html += (htmlFrags[ndx] != undefined?htmlFrags[ndx]:'');
	}

	return html;
}


tcga.db2.queryWriter.writeQuery = function(query, queryNum, count) {
	var html = '<div class="queryDisplay">';
	var currQueryNum = tcga.db2.query.storage.getCurrQueryNum();
	
	if (count > 1) {
		var checked = '';
		if (currQueryNum == -1 && queryNum == count - 1) {
			checked = 'checked';
		}
		else if (currQueryNum == queryNum) {
			checked = 'checked';
		}
		
		html += '<input name="querySelector" class="queryCheckbox"' + checked + ' type="radio" onclick="tcga.db2.selectors.loadQuery(' + queryNum + ')">';
	}

/*	
	html += '<div class="queryControl">' + 
				'<img onclick="tcga.db2.query.storage.saveQueryToLibrary(' + queryNum + ');tcga.db2.queryWriter.start();" ext:qtip="Save" class="queryControlButtons save hand" src="images/icons/foldersave4.png">' +
				'<img onclick="tcga.db2.query.storage.deleteQuery(' + queryNum + ');tcga.db2.queryWriter.start();" ext:qtip="Delete" class="queryControlButtons delete hand" src="images/icons/application_delete.png">' +
				'<img onclick="tcga.db2.history.addToken(\'results\')" ext:qtip="Go" class="queryControlButtons hand" src="images/icons/application_go.png">' +
				'<div onclick="tcga.db2.queryWriter.toggleQuery(' + queryNum + ')" class="queryControlButtons x-tool toggle' + (query.closed?' closed':'') + ' inline">&nbsp;</div></div>';
*/

/*
	if (query.shared) {
		html += '<div class="queryShared"><img ext:qtip="Shared" class="querySharedImage" src="images/icons/share-icon-12x12.png"></div>';
	}
	else {
		html += '<div class="queryShared"></div>';
	}
*/

	html += '<div class="queryName" style="height: ' + (query.name?'auto':'1px') + ';">' + (query.name?query.name:'&nbsp;') + '</div>';

	if (!query.closed) {
		html += '<div class="queryLabel">Disease:</div>';
		html += '<div class="queryValue">' + (query.disease?query.disease.description + ' (' + query.disease.name + ')':'None selected') + '</div>'

		html += tcga.db2.queryWriter.writeQueryClass('Patient Class', query.patientClass);
		html += tcga.db2.queryWriter.writeQueryClass('Molecular Set', query.molecularSet);
	}

	html += '</div>';
	
	html += '<div class="button blueButtonFill select" style="margin-left: 30px;margin-bottom: 10px;width: 72px;"  onclick="tcga.db2.history.addToken(\'results\')">Run Query</div>'

	return html;
}

tcga.db2.queryWriter.geneTemplate = new Ext.XTemplate(
 	'<tpl for=".">',
		'<div class="queryLabel">Selected Gene: (<a href="tcgaDb2.htm">edit</a>)</div>',
		'<tpl if="this.isNull(\'gene\', values)">',
			'None selected',
		'</tpl>',
		'<tpl if="this.isNotNull(\'gene\', values)">',
			'{gene}',
		'</tpl>',
		'<div class="queryLabel">Selected Pathway: (<a href="tcgaDb2.htm">edit</a>)</div>',
		'<tpl if="this.isNull(\'pathway\', values)">',
			'None selected',
		'</tpl>',
		'<tpl if="this.isNotNull(\'pathway\', values)">',
			'{pathway}',
		'</tpl>',
	'</tpl>',
	tcga.db2.queryWriter.templateFunctions
);

tcga.db2.queryWriter.mutationsTemplate = new Ext.XTemplate(
 	'<tpl for=".">',
		'<div class="queryLabel">Selected Mutation: (<a href="tcgaDb2.htm">edit</a>)</div>',
		'<tpl if="this.isNull(\'mutation\', values)">',
			'None selected',
		'</tpl>',
		'<tpl if="this.isNotNull(\'mutation\', values)">',
			'{gene}',
		'</tpl>',
	'</tpl>',
	tcga.db2.queryWriter.templateFunctions
);

tcga.db2.queryWriter.start = function() {
	if (tcga.db2.query.storage.getQueryCount() > 0) {
		var newHtml = '';
		
		// Need to amend this later when we have complementary queries
		var query = [tcga.db2.query.storage.getCurrQuery()];
		if (tcga.db2.selectors.queryType.find('patient') || tcga.db2.selectors.queryType.find('molecular')) {
			for (var ndx = 0;ndx < query.length;ndx++) {
				newHtml += tcga.db2.queryWriter.writeQuery(query[ndx], ndx, query.length);
			}
		}
		else if (tcga.db2.selectors.queryType.find('gene')) {
			newHtml = tcga.db2.queryWriter.geneTemplate.apply({gene: ''});
		}

		Ext.get('queryList').update(newHtml);
	}
	else {
		Ext.get('queryList').update('No current queries');
	}
}
