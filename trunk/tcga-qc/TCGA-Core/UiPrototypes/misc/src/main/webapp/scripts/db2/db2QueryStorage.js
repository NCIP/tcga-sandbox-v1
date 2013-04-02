Ext.namespace('tcga.db2.query');

tcga.db2.query.storage = function(){
	return {
		currQuery: 0,
		
		queryList: [],
		
		reset: function() {
			this.queryList = [];
			this.currQuery = 0;
		},
		
		getQueryCount: function() {
			return this.queryList.length;
		},
		
		setCurrQuery: function(queryNum) {
 			return this.currQuery = queryNum;
		},
		
		getCurrQueryNum: function() {
			return this.currQuery;
		},
		
		getCurrQuery: function() {
			return this.queryList[this.currQuery];
		},
		
		getQuery: function(queryNum) {
			return this.queryList[queryNum];
		},
		
		addQuery: function(query) {
			this.queryList.push(query);
			
			// Return the number of this query
			return this.queryList.length - 1;
		},
		
		// Deletes the query from query storage.
		deleteQuery: function(queryNum) {
			if (this.queryList != []) {
				var currQueryNum = this.getCurrQueryNum();
				var currQueryCount = this.getQueryCount();
				var loadQuery = false;
				if (currQueryNum == queryNum) {
					loadQuery = true;
				}
				if (currQueryNum == currQueryCount - 1) {
					this.setCurrQuery(currQueryNum - 1);
				}
		
				for (var ndx = queryNum;ndx < this.queryList.length - 1;ndx++) {
					this.queryList[ndx] = this.queryList[ndx + 1]
				}
				// Dump the last element now
				queryList.pop();
		
				if (loadQuery) {
					tcga.db2.selectors.loadQuery(tcga.db2.query.storage.getCurrQueryNum());
				}

				// Return true to indicate that we actually deleted something
				return true;
			}
			
			// Return false to indicate that there was nothing to delete
			return false;
		},
		
		// Save the query to the back end
		saveQueryToLibrary: function(queryNum) {
			var query = this.getQuery(queryNum);
			
			new tcga.extensions.window({
		      id: 'saveQueryWindow',
		      modal: true,
				title: 'Save Query to Library',
		      width: 350,
		      items: [{
					border: false,
					width: 340,
			      layout:'column',
					items: [{
						border: false,
						style: 'margin: 5px 0 5px 10px;font-weight: bold;',
						width: 350,
						html: '<span style="bold">Query Name:</span>'
					}, {
						id: 'queryName',
						xtype: 'textfield',
						hideLabel: true,
						value: (query && query.get('name')?query.get('name'):null),
						emptyText: 'Please enter a name for the query',
						style: 'margin: 0 90px 0 10px;',
						width: 260
					}, {
						id: 'errorField',
						xtype: 'label',
						style: 'margin-left:15px;color:red;',
						width: 340,
						text: 'Please enter a name for the query.',
						hidden:true
					}, {
						id: 'separateComps',
						xtype: 'checkbox',
						hideLabel: true,
						checked: (query && query.get('separateComps')?query.get('separateComps'):false),
						style: 'margin: 15px 0 0 10px;'
					}, {
						border: false,
						width: 280,
						style: 'margin-top: 15px;',
						html: 'Save patient classes and molecular data set as separate components'
					}, {
						id: 'share',
						xtype: 'checkbox',
						hideLabel: true,
						checked: (query && query.get('shared')?query.get('shared'):false),
						style: 'margin: 15px 0 15px 10px;'
					}, {
						border: false,
						width: 280,
						style: 'margin: 15px 0 15px 0;',
						html: 'Allow others to use this query'
					}]
				}],
				buttons: [{
					text: 'Save to Library',
					minWidth: 100,
					style: 'margin-left: 115px;margin-bottom: 5px;',
					handler: function() {
						var name = Ext.getCmp('queryName').getValue();
						if (!name) {
							var errorField = Ext.getCmp('errorField');
							errorField.show();
							return;
						}
		
						var shared = Ext.getCmp('share').getValue();
						Ext.getCmp('saveQueryWindow').close();

						query.update('name', name);
						query.update('shared', shared);

						/*
						 * Save to the back end here
						 */

						// Rerun the query writer to we can update the name
						tcga.db2.queryWriter.start();
					}
				}, {
					text: 'Cancel',
					minWidth: 100,
					style: 'margin-left: 10px;margin-bottom: 5px;',
					handler: function() {
						Ext.getCmp('saveQueryWindow').close();
					}
				}]
		  }).show();
		},
		
		// Deletes the query from query storage and the query library on the back end
		deleteQueryFromLibrary: function(queryNum) {
			this.deleteQuery(queryNum);

			/*
			 * Delete to the back end here
			 */
			
			return true;
		}
	}
}();

tcga.db2.query.queryClass = function() {
	return {
		name: '',
		values: null,
		
		get: function(key) {
			return this[key];
		},
		
		update: function(key, value) {
			this[key] = value;
		},
		
		getValue: function(key) {
			return (this.values?this.values[key]:null);
		},
		
		updateValue: function(key, value) {
			if (!this.values) {
				this.values = {};
			}
			this.values[key] = value;
		}		
	}
}

tcga.db2.query.query = function() {
	return {
		name: '',
		disease: null,
		baseQuery: null,
		patientClass: new tcga.db2.query.queryClass(),
		molecularSet: new tcga.db2.query.queryClass(),
		
		get: function(key) {
			return this[key];
		},
		
		update: function(key, value) {
			this[key] = value;
		},
		
		updateQueryClass: function(type, key, value) {
			(key == 'name'?this[type].update(key, value):this[type].updateValue(key, value));
		}
	}
}
