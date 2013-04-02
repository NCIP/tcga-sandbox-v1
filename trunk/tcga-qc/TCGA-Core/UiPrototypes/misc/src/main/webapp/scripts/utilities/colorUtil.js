Ext.namespace('tcga.colorUtil');

tcga.colorUtil.colorCycler = function(config) {
	var defaultConfig = {
		gradient: false,
		colors: null
	};
	this.localConfig = Ext.applyIf(config, defaultConfig);

	if (this.localConfig.colors && this.localConfig.colors.length != 0) {
		this.colors = this.localConfig.colors;
	}
	else {
		if (this.localConfig.gradient && this.localConfig.gradient == true) {
			this.localConfig.colors = [
				// Blue
				[
					['#94b7d2', '#e3f2fd'], 
					['#728da2', '#d3eafb'],
					['#3e4d57', '#83a2ba'],
					'#728da2'
				],

				// Green
				[
					['#769e00', '#c8db90'], 
					['#5c7a02', '#bfd973'], 
					['#39490d', '#68860d'],
					'#5c7a02'
				],
				
				//Red
				[
					['#b53b3b', '#f1bebe'], 
					['#8b2d2d', '#e89999'], 
					['#4b1a1a', '#a03434'],
					'#8b2d2d'
				],
				
				// Orange
				[
					['#d8783b', '#ffd8be'], 
					['#a65d2f', '#fec098'], 
					['#7e4a28', '#b96936'],
					'#a65d2f'
				],
				
				// Yellow
				[
					['#dbae27', '#f8d979'], 
					['#a58111', '#f5d675'], 
					['#604b09', '#b38d17'],
					'#a58111'
				],
				
				// Brown
				[
					['#cb9e17', '#e8c969'], 
					['#957101', '#e5c665'], 
					['#503b09', '#a37d07'],
					'#957101'
				]
			];
		}
		else {
			this.localConfig.colors = [
				'blue',
				'green',
				'red',
				'darkorange',
				'brown',
				'darkgoldenrod'
			];
		}
	}
	this.currColor = 0;

	this.reset = function() {
		this.currColor = 0;
	}

	this.getNextColor = function() {
		var color = this.localConfig.colors[this.currColor];
		this.currColor++;
		if (this.currColor == this.localConfig.colors.length) {
			this.currColor = 0;
		}
		
		return color;
	}
}
