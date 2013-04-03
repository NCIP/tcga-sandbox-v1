var MLLinkedChartDataXML = '<?xml version="1.0" encoding="utf-8"?>\n\
<chart caption="Annual Sales Summary" subcaption="For the period of 2004 to 2007"\n\
    xAxisName="Year" yAxisName="Sales" numberPrefix="$" useRoundEdges="1">\n\
    <set label="2004" value="11610" link="newchart-xml-2004Quarters" tooltext="2004, $11.61K{br}click to see details" />\n\
    <set label="2005" value="10430" link="newchart-xml-2005Quarters" tooltext="2005, $10.43K{br}click to see details" />\n\
    <set label="2006" value="10170" link="newchart-xml-2006Quarters" tooltext="2006, $10.17K{br}click to see details" />\n\
    <set label="2007" value="11560" link="newchart-xml-2007Quarters" tooltext="2007, $11.56K{br}click to see details" />\n\
\n\
    <linkeddata id="2004Quarters">\n\
        <chart caption="Quarterly Sales Summary" subcaption="For the year 2004" \n\
            xAxisName="Quarter" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
            <set label="Q1" value="2740" link="newchart-xml-2004Q1"/>\n\
            <set label="Q2" value="2700" link="newchart-xml-2004Q2"/>\n\
            <set label="Q3" value="3180" link="newchart-xml-2004Q3"/>\n\
            <set label="Q4" value="2990" link="newchart-xml-2004Q4"/>\n\
       \n\
	\n\
            <linkeddata id="2004Q1">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the first quarter of year 2004"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" 	>\n\
                    <set label="January" value="1100" link="newchart-xml-2004Jan" />\n\
                    <set label="February" value="760" link="newchart-xml-2004Feb" />\n\
                    <set label="March" value="880" link="newchart-xml-2004Mar" />\n\
\n\
                    <linkeddata id="2004Jan">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For January 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="170" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="320" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Feb">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For February 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="370" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Mar">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For March 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="270" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2004Q2">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the second quarter of year 2004"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="April" value="1160" link="newchart-xml-2004Apr" />\n\
                    <set label="May" value="920" link="newchart-xml-2004May" />\n\
                    <set label="June" value="620" link="newchart-xml-2004Jun" />\n\
\n\
                    <linkeddata id="2004Apr">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For April 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="350" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="320" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004May">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For May 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="80" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="400" />\n\
                            <set label="Week 4" value="150" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Jun">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For June 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="210" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="120" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2004Q3">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the third quarter of year 2004"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="July" value="1140" link="newchart-xml-2004Jul" />\n\
                    <set label="August" value="860" link="newchart-xml-2004Aug" />\n\
                    <set label="September" value="1180" link="newchart-xml-2004Sep" />\n\
\n\
                    <linkeddata id="2004Jul">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For July 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="400" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="150" />\n\
                            <set label="Week 4" value="300" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Aug">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For August 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="310" />\n\
                            <set label="Week 2" value="250" />\n\
                            <set label="Week 3" value="100" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Sep">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For September 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2" \n\
						numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="370" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2004Q4">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the fourth quarter of year 2004"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="October" value="920" link="newchart-xml-2004Oct" />\n\
                    <set label="November" value="1020" link="newchart-xml-2004Nov" />\n\
                    <set label="December" value="1050" link="newchart-xml-2004Dec" />\n\
\n\
                    <linkeddata id="2004Oct">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For October 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="250" />\n\
                            <set label="Week 2" value="200" />\n\
                            <set label="Week 3" value="70" />\n\
                            <set label="Week 4" value="400" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Nov">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For November 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                           numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="400" />\n\
                            <set label="Week 2" value="100" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2004Dec">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For December 2004" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="400" />\n\
                            <set label="Week 2" value="150" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
			 \n\
			 </chart>\n\
    </linkeddata>\n\
\n\
    <linkeddata id="2005Quarters">\n\
        <chart caption="Quarterly Sales Summary" subcaption="For the year 2005"\n\
            xAxisName="Quarter" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
            <set label="Q1" value="3060" link="newchart-xml-2005Q1"/>\n\
            <set label="Q2" value="2030" link="newchart-xml-2005Q2"/>\n\
            <set label="Q3" value="2700" link="newchart-xml-2005Q3"/>\n\
            <set label="Q4" value="2640" link="newchart-xml-2005Q4"/>\n\
        \n\
	\n\
            <linkeddata id="2005Q1">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the first quarter of year 2005"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="January" value="3700" link="newchart-xml-2004Jan" />\n\
                    <set label="February" value="2900" link="newchart-xml-2004Feb" />\n\
                    <set label="March" value="3200" link="newchart-xml-2004Mar" />\n\
\n\
                    <linkeddata id="2005Jan">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For January 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="450" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="380" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Feb">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For February 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="200" />\n\
                            <set label="Week 3" value="100" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Mar">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For March 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="120" />\n\
                            <set label="Week 2" value="280" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2005Q2">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the second quarter of year 2005"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="April" value="3700" link="newchart-xml-2005Apr" />\n\
                    <set label="May" value="2900" link="newchart-xml-2005May" />\n\
                    <set label="June" value="3200" link="newchart-xml-2005Jun" />\n\
\n\
                    <linkeddata id="2005Apr">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For April 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="170" />\n\
                            <set label="Week 2" value="200" />\n\
                            <set label="Week 3" value="80" />\n\
                            <set label="Week 4" value="300" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005May">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For May 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="200" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="80" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Jun">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For June 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="150" />\n\
                            <set label="Week 2" value="50" />\n\
                            <set label="Week 3" value="100" />\n\
                            <set label="Week 4" value="300" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2005Q3">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the third quarter of year 2005"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="July" value="3700" link="newchart-xml-2005Jul" />\n\
                    <set label="August" value="2900" link="newchart-xml-2005Aug" />\n\
                    <set label="September" value="3200" link="newchart-xml-2005Sep" />\n\
\n\
                    <linkeddata id="2005Jul">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For July 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="230" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="120" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Aug">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For August 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="300" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="220" />\n\
                            <set label="Week 4" value="100" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Sep">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For September 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2" \n\
							numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="300" />\n\
                            <set label="Week 2" value="190" />\n\
                            <set label="Week 3" value="350" />\n\
                            <set label="Week 4" value="400" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2005Q4">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the fourth quarter of year 2005"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="October" value="3700" link="newchart-xml-2005Oct" />\n\
                    <set label="November" value="2900" link="newchart-xml-2005Nov" />\n\
                    <set label="December" value="3200" link="newchart-xml-2005Dec" />\n\
\n\
                    <linkeddata id="2005Oct">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For October 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="110" />\n\
                            <set label="Week 2" value="190" />\n\
                            <set label="Week 3" value="400" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Nov">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For November 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="200" />\n\
                            <set label="Week 2" value="260" />\n\
                            <set label="Week 3" value="120" />\n\
                            <set label="Week 4" value="250" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2005Dec">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For December 2005" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="300" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="120" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
			\n\
			</chart>\n\
    </linkeddata>\n\
\n\
    <linkeddata id="2006Quarters">\n\
        <chart caption="Quarterly Sales Summary" subcaption="For the year 2006"\n\
            xAxisName="Quarter" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
            <set label="Q1" value="2410" link="newchart-xml-2006Q1"/>\n\
            <set label="Q2" value="2800" link="newchart-xml-2006Q2"/>\n\
            <set label="Q3" value="2550" link="newchart-xml-2006Q3"/>\n\
            <set label="Q4" value="2410" link="newchart-xml-2006Q4"/>\n\
       \n\
	\n\
            <linkeddata id="2006Q1">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the first quarter of year 2006"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="January" value="870" link="newchart-xml-2006Jan" />\n\
                    <set label="February" value="890" link="newchart-xml-2006Feb" />\n\
                    <set label="March" value="650" link="newchart-xml-2006Mar" />\n\
\n\
                    <linkeddata id="2006Jan">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For January 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                             numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="370" />\n\
                            <set label="Week 2" value="200" />\n\
                            <set label="Week 3" value="100" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Feb">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For February 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="390" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Mar">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For March 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="80" />\n\
                            <set label="Week 2" value="250" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="120" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2006Q2">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the second quarter of year 2006"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="April" value="1300" link="newchart-xml-2006Apr" />\n\
                    <set label="May" value="440" link="newchart-xml-2006May" />\n\
                    <set label="June" value="1060" link="newchart-xml-2006Jun" />\n\
\n\
                    <linkeddata id="2006Apr">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For April 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="370" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="320" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006May">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For May 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                           numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="90" />\n\
                            <set label="Week 2" value="100" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="50" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Jun">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For June 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="370" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="100" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2006Q3">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the third quarter of year 2006"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="July" value="850" link="newchart-xml-2006Jul" />\n\
                    <set label="August" value="1030" link="newchart-xml-2006Aug" />\n\
                    <set label="September" value="670" link="newchart-xml-2006Sep" />\n\
\n\
                    <linkeddata id="2006Jul">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For July 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="200" />\n\
                            <set label="Week 2" value="100" />\n\
                            <set label="Week 3" value="250" />\n\
                            <set label="Week 4" value="300" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Aug">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For August 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="370" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="400" />\n\
                            <set label="Week 4" value="170" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Sep">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For September 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2" \n\
						numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="80" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2006Q4">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the fourth quarter of year 2006"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="October" value="590" link="newchart-xml-2006Oct" />\n\
                    <set label="November" value="690" link="newchart-xml-2006Nov" />\n\
                    <set label="December" value="1130" link="newchart-xml-2006Dec" />\n\
\n\
                    <linkeddata id="2006Oct">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For October 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                           numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="120" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="70" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Nov">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For November 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="200" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="100" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2006Dec">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For December 2006" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="320" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="320" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
			\n\
			 </chart>\n\
    </linkeddata>\n\
\n\
    <linkeddata id="2007Quarters">\n\
        <chart caption="Quarterly Sales Summary" subcaption="For the year 2007" \n\
            xAxisName="Quarter" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
            <set label="Q1" value="2690" link="newchart-xml-2007Q1"/>\n\
            <set label="Q2" value="2700" link="newchart-xml-2007Q2"/>\n\
            <set label="Q3" value="3180" link="newchart-xml-2007Q3"/>\n\
            <set label="Q4" value="2990" link="newchart-xml-2007Q4"/>\n\
       \n\
	\n\
            <linkeddata id="2007Q1">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the first quarter of year 2007"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="January" value="1050" link="newchart-xml-2007Jan" />\n\
                    <set label="February" value="760" link="newchart-xml-2007Feb" />\n\
                    <set label="March" value="880" link="newchart-xml-2007Mar" />\n\
\n\
                    <linkeddata id="2007Jan">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For January 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                           numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="120" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="320" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Feb">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For February 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="100" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="370" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Mar">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For March 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="270" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2007Q2">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the second quarter of year 2007"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="April" value="1160" link="newchart-xml-2007Apr" />\n\
                    <set label="May" value="920" link="newchart-xml-2007May" />\n\
                    <set label="June" value="620" link="newchart-xml-2007Jun" />\n\
\n\
                    <linkeddata id="2007Apr">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For April 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="350" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="320" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007May">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For May 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="80" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="400" />\n\
                            <set label="Week 4" value="150" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Jun">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For June 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="210" />\n\
                            <set label="Week 2" value="90" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="120" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2007Q3">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the third quarter of year 2007"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="July" value="1140" link="newchart-xml-2007Jul" />\n\
                    <set label="August" value="860" link="newchart-xml-2007Aug" />\n\
                    <set label="September" value="1180" link="newchart-xml-2007Sep" />\n\
\n\
                    <linkeddata id="2007Jul">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For July 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="400" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="150" />\n\
                            <set label="Week 4" value="300" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Aug">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For August 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="310" />\n\
                            <set label="Week 2" value="250" />\n\
                            <set label="Week 3" value="100" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Sep">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For September 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2" \n\
							numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="370" />\n\
                            <set label="Week 2" value="290" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
\n\
            <linkeddata id="2007Q4">\n\
                <chart caption="Monthly Sales Summary" subcaption="For the fourth quarter of year 2007"\n\
                    xAxisName="Month" yAxisName="Sales" numberPrefix="$" useRoundEdges="1" >\n\
                    <set label="October" value="920" link="newchart-xml-2007Oct" />\n\
                    <set label="November" value="1020" link="newchart-xml-2007Nov" />\n\
                    <set label="December" value="1050" link="newchart-xml-2007Dec" />\n\
\n\
                    <linkeddata id="2007Oct">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For October 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="250" />\n\
                            <set label="Week 2" value="200" />\n\
                            <set label="Week 3" value="70" />\n\
                            <set label="Week 4" value="400" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Nov">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For November 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
                            numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="400" />\n\
                            <set label="Week 2" value="100" />\n\
                            <set label="Week 3" value="200" />\n\
                            <set label="Week 4" value="320" />\n\
                        </chart>\n\
                    </linkeddata>\n\
\n\
                    <linkeddata id="2007Dec">\n\
                        <chart caption="Weekly Sales Summary" subcaption="For December 2007" showLabels="0" showLegend="1" pieSliceDepth="10" enableSmartLabels="0" labelDistance="2"\n\
							numberPrefix="$" bgColor="CBCBCB,E9E9E9" bgAlpha="50,50" bgRatio="0,100" bgAngle="270" showBorder="1" borderColor="767575" borderAlpha="50">\n\
                            <set label="Week 1" value="400" />\n\
                            <set label="Week 2" value="150" />\n\
                            <set label="Week 3" value="300" />\n\
                            <set label="Week 4" value="200" />\n\
                        </chart>\n\
                    </linkeddata>\n\
                    \n\
                </chart>\n\
            </linkeddata>\n\
			 \n\
			 </chart>\n\
    </linkeddata>\n\
\n\
</chart>';