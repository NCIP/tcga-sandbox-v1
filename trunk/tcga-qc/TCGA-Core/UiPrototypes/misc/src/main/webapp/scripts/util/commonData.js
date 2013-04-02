Ext.namespace("marcs.util.data");

marcs.util.data.states = [
	['Alabama', 'AL'],
	['Alaska', 'AK'],
	['American Samoa', 'AS'],
	['Arizona', 'AZ'],
	['Arkansas', 'AR'],
	['California', 'CA'],
	['Colorado', 'CO'],
	['Connecticut', 'CT'],
	['Delaware', 'DE'],
	['District of Columbia', 'DC'],
	['Federated States of Micronesia', 'FM'],
	['Florida', 'FL'],
	['Georgia', 'GA'],
	['Guam', 'GU'],
	['Hawaii', 'HI'],
	['Idaho', 'ID'],
	['Illinois', 'IL'],
	['Indiana', 'IN'],
	['Iowa', 'IA'],
	['Kansas', 'KS'],
	['Kentucky', 'KY'],
	['Louisiana', 'LA'],
	['Maine', 'ME'],
	['Marshall Islands', 'MH'],
	['Maryland', 'MD'],
	['Massachusetts', 'MA'],
	['Michigan', 'MI'],
	['Minnesota', 'MN'],
	['Mississippi', 'MS'],
	['Missouri', 'MO'],
	['Montana', 'MT'],
	['Nebraska', 'NE'],
	['Nevada', 'NV'],
	['New Hampshire', 'NH'],
	['New Jersey', 'NJ'],
	['New Mexico', 'NM'],
	['New York', 'NY'],
	['North Carolina', 'NC'],
	['North Dakota', 'ND'],
	['Northern Mariana Islands', 'MP'],
	['Ohio', 'OH'],
	['Oklahoma', 'OK'],
	['Oregon', 'OR'],
	['Palau', 'PW'],
	['Pennsylvania', 'PA'],
	['Puerto Rico', 'PR'],
	['Rhode Island', 'RI'],
	['South Carolina', 'SC'],
	['South Dakota', 'SD'],
	['Tennessee', 'TN'],
	['Texas', 'TX'],
	['Utah', 'UT'],
	['Vermont', 'VT'],
	['Virgin Islands', 'VI'],
	['Virginia', 'VA'],
	['Washington', 'WA'],
	['West Virginia', 'WV'],
	['Wisconsin', 'WI'],
	['Wyoming', 'WY']
];

marcs.util.data.typeOfNotice = [
	['Letter'],
	['Phone'],
	['Fax'],
	['Email'],
	['In Person']
];

marcs.util.data.noticeSource = [
	['Federal Agency'],
	['State Agency'],
	['Local Agency'],
	['Media'],
	['Trade Association']
];

marcs.util.data.destructionMethods = [
	['Dynamite'],
	['Hydrogen bomb'],
	['Illudium Q-38 Space Modulator'],
	['Missile Mike'],
	['Phaser']
];

marcs.util.data.dispositionMethods = [
	['Thrown out'],
	['Returned to manufacturer'],
	['Gave it to Cousin Vinny'],
	['Hole in ground'],
	['Phaser']
];

marcs.util.data.stateStore = Ext.extend(Ext.data.ArrayStore, {
    constructor: function(config){
        marcs.util.data.stateStore.superclass.constructor.call(this, Ext.apply(config, {
			fields: ['state', 'abbrev'],
			autoLoad: true,
			data: marcs.util.data.states
        }));
    }
});

marcs.util.data.dayStore = new Ext.data.ArrayStore({
	fields: ['day'],
	autoLoad: true,
	data: [
		['1'], ['2'], ['3'], ['4'], ['5'], ['6'], ['7'], ['8'], ['9'], ['10'],
		['11'], ['12'], ['13'], ['14'], ['15'], ['16'], ['17'], ['18'], ['19'], ['20'],
		['21'], ['22'], ['23'], ['24'], ['25'], ['26'], ['27'], ['28'], ['29'], ['30'],
		['31']
	]
});

marcs.util.data.monthStore = new Ext.data.SimpleStore({
	fields: ['month'],
	autoLoad: true,
	data: [
		['1'], ['2'], ['3'], ['4'], ['5'], ['6'], ['7'], ['8'], ['9'], ['10'],
		['11'], ['12']
	]
});
