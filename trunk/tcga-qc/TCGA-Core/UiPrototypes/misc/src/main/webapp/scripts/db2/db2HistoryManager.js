Ext.namespace('tcga.db2');

tcga.db2.history = function() {
	var firstToken = 'start';
	var currToken = null;

	var pageInfo = {
		start: {
			div: 'startQuery',
			title: 'Start Query'
		},
		build: {
			div: 'buildQuery',
			title: 'Build Query'
		},
		results: {
			div: 'queryResults',
			title: 'Query Results'
		},
		library: {
			div: 'queryLibrary',
			title: 'Query Library'
		},
		preferences: {
			div: 'queryPreferences',
			title: 'Query Preferences'
		}
	}
	
	var trailRoot = '<a href="tcgaHome2.jsp">Home</a>';
	
	var trailStore = [];

	var pageEls = {};
	
	var addingToken = false;

	var updateTrail = function(token) {
		// History handling for the trail
		for (var ndx = trailStore.length - 1;ndx >= 0;ndx--) {
/*			if (trailStore[ndx] == token) {
				trailStore = (ndx > 0?trailStore.slice(0,ndx + 1):[]);
				break;
			}*/
			if (trailStore[ndx] != token) {
				trailStore.pop();
			}
			else {
				break;
			}
		}
	}

	return {
		setTitle: function(token) {
			Ext.get('watsonPageTitle').update(pageInfo[token].title);
		},
		
		setTrail: function(token) {
			// Add the root - which is just the top page
			var trailHtml = trailRoot;
			
			// Add each of the intermediate trail elements
			for (var ndx = 0;ndx < trailStore.length - 1;ndx++) {
				var token = trailStore[ndx];
				trailHtml += ' > <a href="#' + token + '">' + pageInfo[token].title + '</a>';
			}
			
			// Add the last trail element
			var token = trailStore[trailStore.length - 1];
			trailHtml += ' > <span class="trailDest">' + pageInfo[token].title + '</span>';
			
			Ext.get('trail').update(trailHtml);
		},
		
		addToken: function(token) {
			addingToken = true;
			Ext.History.add(token);
		},
		
		showPageDiv: function(token) {
			for (var pageEl in pageEls) {
				(pageEl == token?pageEls[pageEl].show():pageEls[pageEl].hide());
			}
		},
		
		manageQueryPageChange: function(newToken) {
			this.setTitle(newToken);
			
			if (!addingToken) {
				updateTrail(newToken);
			}
			else {
				trailStore.push(newToken);
				addingToken = false;
			}

			this.setTrail(newToken);
			
			this.showPageDiv(newToken);
			
			if (currToken) {
				tcga.db2[currToken].reset();
			}

			currToken = newToken;
			tcga.db2[newToken].init();
		},
		
		init: function() {
//			var token = firstToken;
//			this.addToken(token);

			// Set the visibility mode for all of the page divs
			for (var page in pageInfo) {
				var pageEl = Ext.get(pageInfo[page].div);
				pageEl.setVisibilityMode(Ext.Element.DISPLAY);
				pageEls[page] = pageEl;
			}
			
			// Make sure the correct pages are showing
//			this.showPageDiv(token);
			this.showPageDiv();
		}
	}
}();
