function generateChartXML(sQunatity, sDiscount, sStock, sDetails, sManhattan, viewAng) {
    var datasets = "", manhattan= "", saleQty, details, discount, stock, x, y;
    
    saleQty = "<dataset seriesName='Cumulative Sales'>\n\
                    <set value='71398.428735733'/>\n\
                    <set value='90117.710647583'/>\n\
                    <set value='103519.687332153'/>\n\
                    <set value='104854.154975891'/>\n\
                    <set value='129247.252159119'/>\n\
                    <set value='12885.0604515076'/>\n\
                </dataset>";

    details =     "<dataset  seriesName='Beverages'>\n\
                        <set  value='10876.6499958038' />\n\
                        <set  value='27245.3999938965' />\n\
                        <set  value='34599.1500015259' />\n\
                        <set  value='27761.5749893188' />\n\
                        <set  value='22852.049987793' />\n\
                        <set  value='3566.69996452332' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Condiments' >\n\
                        <set  value='5824.19997406006' />\n\
                        <set  value='4737.89751815796' />\n\
                        <set  value='6293.96746826172' />\n\
                        <set  value='10773.2700042725' />\n\
                        <set  value='10087.0749511719' />\n\
                        <set  value='885.899993896484' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Confections' >\n\
                        <set  value='8778.14900970459' />\n\
                        <set  value='10834.5314922333' />\n\
                        <set  value='10074.0849685669' />\n\
                        <set  value='22877.1800079346' />\n\
                        <set  value='10012.9550094604' />\n\
                        <set  value='1215.17301559448' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Dairy Products' >\n\
                        <set  value='9844.85003662109' />\n\
                        <set  value='17691.3500366211' />\n\
                        <set  value='11454.5' />\n\
                        <set  value='13685.324962616' />\n\
                        <set  value='34679.9000701904' />\n\
                        <set  value='628.119995117188' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Grains/Cereals' >\n\
                        <set  value='5242.35000610352' />\n\
                        <set  value='12022.825012207' />\n\
                        <set  value='4060.01247406006' />\n\
                        <set  value='3325.40000915527' />\n\
                        <set  value='5537.60000610352' />\n\
                        <set  value='4419.00499725342' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Meat/Poultry' >\n\
                        <set  value='12767.1621875763' />\n\
                        <set  value='3511.47158813477' />\n\
                        <set  value='23334.0499343872' />\n\
                        <set  value='4083.66005706787' />\n\
                        <set  value='22256.4096755981' />\n\
                        <set  value='48' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Produce' >\n\
                        <set  value='12157.9000244141' />\n\
                        <set  value='1526' />\n\
                        <set  value='1172.80000305176' />\n\
                        <set  value='13031.1999359131' />\n\
                        <set  value='14290.6499023438' />\n\
                        <set  value='1137.38248634338' />\n\
                    </dataset>\n\
                    <dataset  seriesName='Seafood' >\n\
                        <set  value='5907.16750144959' />\n\
                        <set  value='12548.2350063324' />\n\
                        <set  value='12531.1224822998' />\n\
                        <set  value='9316.54500961304' />\n\
                        <set  value='9530.61255645752' />\n\
                        <set  value='984.779998779297' />\n\
                    </dataset>";

    discount = "<dataset  seriesName='Total Discount' renderAs='area' >\n\
                    <set  value='6077.83155584335' />\n\
                    <set  value='6632.60958886147' />\n\
                    <set  value='5146.66249036789' />\n\
                    <set  value='4971.29504346848' />\n\
                    <set  value='11286.9175815582' />\n\
                    <set  value='1109.98953613639' />\n\
                </dataset>";

    stock =     "<dataset  seriesName='Stock Valuation' renderAs='Line' >\n\
                    <set  value='18000' />\n\
                    <set  value='15000' />\n\
                    <set  value='36000' />\n\
                    <set  value='38000' />\n\
                    <set  value='45000' />\n\
                    <set  value='98000' />\n\
                </dataset>";


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
        manhattan += "clustered = '0'";
    }else {
        manhattan += "clustered = '1'";
    }
    if ( viewAng ) {
        x = viewAng.x
        y = viewAng.y
    } else {
        x = 30;
        y = -45;
    }

    var dataXML = "";
    //startAngX='30' endAngX='30' startAngY='-45' endAngY='-45'

    dataXML = "<chart palette='3' caption='Company Report' startAngX='" + x + "' endAngX='" + x + "' startAngY='" + y + "' endAngY='" + y + "' xAxisName='Month' yAxisName='Amount' numberPrefix=' $' decimals='0' showBorder='0' showPlotBorder='0' borderAlpha='0' borderThickness='0' chartOnTop='0' legendBgAlpha='60' legendShadow='0' divLineEffect='emboss' divlineColor='999999' chartOrder='line,area,column' " + manhattan + " >";

    dataXML += "<categories >\n\
                    <category label='Jan' />\n\
                    <category label='Feb' />\n\
                    <category label='Mar' />\n\
                    <category label='Apr' />\n\
                    <category label='May' />\n\
                    <category label='Jun' />\n\
                </categories>";

    dataXML += datasets;
    dataXML += "</chart>";
    
    return dataXML;
}