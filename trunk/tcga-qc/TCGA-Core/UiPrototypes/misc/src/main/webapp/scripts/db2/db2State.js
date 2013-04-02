Ext.namespace('tcga.db2.state');

tcga.db2.state.store = new tcga.db2.state.storage();

tcga.db2.state.storage = function(config) {
	return {
		cookieProvider: new Ext.state.CookieProvider(config),
		
		store: {},
		
		getCookie: function(name) {
			if (!this.store[name]) {
				var value = null;
				var cookie = this.cookieProvider.get(config.name);
				if (cookie) {
					value = this.cookieProvider.decodeValue(cookie);
				}

				this.store[name] = new tcga.db2.state.cookie({
					name: name,
					cookie: cookie,
					value: value
				});
			}
			
			return this.store[name];
		},
		
		setCookie: function(cookie) {
			this.cookieProvider.set(cookie.name, this.cookieProvider.encodeValue(cookie.value));

			return this.store[name] = cookie;
		}
	}
}

tcga.db2.state.cookie = function(config) {
	// A name for the cookie to store the state in MUST be provided!
	if (!config || !config.name) {
		return null;
	}

	return {
		name: config.name,
		
		cookie: config.cookie,
		
		value: config.value,
		
		getValue: function(name) {
			return (this.isNotNull(name)?this.value[name]:null);
		},
		
		getValueLength: function(name) {
			return (this.isNotNull(name)?this.value[name].length:0);
		},
		
		isNotNull: function(name) {
			if (!this.value) {
				return false;
			}
			var value = this.value[name];
			return ((value && value != '' && value != undefined)?true:false);
		},
		
		get: function(){
			return this.value;
		},
		
		set: function(value){
			this.value = value;
			tcga.db2.state.cookieProvider.set(this.name, tcga.db2.state.cookieProvider.encodeValue(this.value));
		
			return this.value;
		},
		
		update: function(name, value, order){
			if (!this.value) {
				this.value = {};
			}
			
			if (typeof(order) == 'number') {
				if (!this.value[name]) {
					this.value[name] = [];
				}
				this.value[name][order] = value;
			}
			else {
				this.value[name] = value;
			}
			tcga.db2.state.store.setCookie(this);
		
			return this.value;
		},
		
		reset: function(key){
			if (this.value && key) {
				if (typeof(this.value[key]) == 'string') {
					this.value[key] = '';
				}
				else {
					this.value[key] = null;
				}
			}
			else {
				this.value = null;
			}
			tcga.db2.state.store.setCookie(this);
		
			return this.value;
		}
	}
}
