function generateChartXML(sQunatity, sDiscount, sStock, sDetails, sManhattan, viewAng) {
    var datasets = "", manhattan= "", saleQty, details, discount, stock, x, y;

    stock = {
        "seriesname":"Stock Valuation",
        "renderas":"Line",
        "data":[{
            "value":"18000"
        },
        {
            "value":"15000"
        },
        {
            "value":"36000"
        },
        {
            "value":"38000"
        },
        {
            "value":"45000"
        },
        {
            "value":"98000"
        }
        ]
    };

    saleQty = {
        "seriesname":"Cumulative Sales",
        "data":[{
            "value":"71398.428735733"
        },
        {
            "value":"90117.710647583"
        },
        {
            "value":"103519.687332153"
        },
        {
            "value":"104854.154975891"
        },
        {
            "value":"129247.252159119"
        },
        {
            "value":"12885.0604515076"
        }
        ]
    };

    discount = {
        "seriesname":"Total Discount",
        "renderas":"area",
        "data":[{
            "value":"6077.83155584335"
        },
        {
            "value":"6632.60958886147"
        },
        {
            "value":"5146.66249036789"
        },
        {
            "value":"4971.29504346848"
        },
        {
            "value":"11286.9175815582"
        },
        {
            "value":"1109.98953613639"
        }
        ]
    };

    details = [{
        "seriesname":"Beverages",
        "data":[{
            "value":"10876.6499958038"
        },
        {
            "value":"27245.3999938965"
        },
        {
            "value":"34599.1500015259"
        },
        {
            "value":"27761.5749893188"
        },
        {
            "value":"22852.049987793"
        },
        {
            "value":"3566.69996452332"
        }
        ]
    },
    {
        "seriesname":"Condiments",
        "data":[{
            "value":"5824.19997406006"
        },
        {
            "value":"4737.89751815796"
        },
        {
            "value":"6293.96746826172"
        },
        {
            "value":"10773.2700042725"
        },
        {
            "value":"10087.0749511719"
        },
        {
            "value":"885.899993896484"
        }
        ]
    },
    {
        "seriesname":"Confections",
        "data":[{
            "value":"8778.14900970459"
        },
        {
            "value":"10834.5314922333"
        },
        {
            "value":"10074.0849685669"
        },
        {
            "value":"22877.1800079346"
        },
        {
            "value":"10012.9550094604"
        },
        {
            "value":"1215.17301559448"
        }
        ]
    },
    {
        "seriesname":"Dairy Products",
        "data":[{
            "value":"9844.85003662109"
        },
        {
            "value":"17691.3500366211"
        },
        {
            "value":"11454.5"
        },
        {
            "value":"13685.324962616"
        },
        {
            "value":"34679.9000701904"
        },
        {
            "value":"628.119995117188"
        }
        ]
    },
    {
        "seriesname":"Grains/Cereals",
        "data":[{
            "value":"5242.35000610352"
        },
        {
            "value":"12022.825012207"
        },
        {
            "value":"4060.01247406006"
        },
        {
            "value":"3325.40000915527"
        },
        {
            "value":"5537.60000610352"
        },
        {
            "value":"4419.00499725342"
        }
        ]
    },
    {
        "seriesname":"Meat/Poultry",
        "data":[{
            "value":"12767.1621875763"
        },
        {
            "value":"3511.47158813477"
        },
        {
            "value":"23334.0499343872"
        },
        {
            "value":"4083.66005706787"
        },
        {
            "value":"22256.4096755981"
        },
        {
            "value":"48"
        }
        ]
    },
    {
        "seriesname":"Produce",
        "data":[{
            "value":"12157.9000244141"
        },
        {
            "value":"1526"
        },
        {
            "value":"1172.80000305176"
        },
        {
            "value":"13031.1999359131"
        },
        {
            "value":"14290.6499023438"
        },
        {
            "value":"1137.38248634338"
        }
        ]
    },
    {
        "seriesname":"Seafood",
        "data":[{
            "value":"5907.16750144959"
        },
        {
            "value":"12548.2350063324"
        },
        {
            "value":"12531.1224822998"
        },
        {
            "value":"9316.54500961304"
        },
        {
            "value":"9530.61255645752"
        },
        {
            "value":"984.779998779297"
        }
        ]
    }];

    if (sQunatity == 1) {
        if (sDetails == 1) {
            datasets += details;
        }else {
            datasets += saleQty;
        }
    }
    if (sDiscount == 1) {
        datasets += discount;
    }
    if (sStock == 1) {
        datasets += stock;
    }
    if (sManhattan == 1) {
        manhattan = "0";
    }else {
        manhattan = "1";
    }
    if ( viewAng ) {
        x = viewAng.x
        y = viewAng.y
    } else {
        x = 30;
        y = -45;
    }



    var dataJSON = "";
    
    dataJSON = {
        "chart":{
            "palette":"3",
            "caption":"Company Report",
            "startangx":x,
            "endangx":x,
            "startangy": y,
            "endangy": y,
            "xaxisname":"Month",
            "yaxisname":"Amount",
            "numberprefix":" $",
            "decimals":"0",
            "showborder":"0",
            "showplotborder":"0",
            "borderalpha":"0",
            "borderthickness":"0",
            "chartontop":"0",
            "legendbgalpha":"60",
            "legendshadow":"0",
            "divlineeffect":"emboss",
            "divlinecolor":"999999",
            "chartorder":"line,area,column",
            "clustered":manhattan
        },
        "categories":[{
            "category":[{
                "label":"Jan"
            },
            {
                "label":"Feb"
            },
            {
                "label":"Mar"
            },
            {
                "label":"Apr"
            },
            {
                "label":"May"
            },
            {
                "label":"Jun"
            }
            ]
        }
        ],
        "dataset":[
            datasets
    ]
    };

console.log (datasets);

    console.log (dataJSON);
    return dataJSON;
}