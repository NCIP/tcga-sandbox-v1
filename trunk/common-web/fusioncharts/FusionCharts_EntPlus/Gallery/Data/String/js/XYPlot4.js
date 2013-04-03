var dataString ='<chart caption="Investment Overview" yAxisName="Returns till date" xAxisName="Age (years)" showLegend="1" showLabels="1" xAxisMaxValue="10" xAxisMinValue="0" drawQuadrant="1" quadrantLabelTL="Best Performers" quadrantLabelTR="Stable Performers" quadrantLabelBR="Laggards" quadrantLabelBL="Under Performers" quadrantLineThickness="1" quadrantLineColor="000080" canvasBgAlpha="0"  bgColor="ffffff">\n\
\n\
\n\
<categories verticalLineThickness="1">\n\
	<category label="1" x="1" showVerticalLine="1"/>\n\
	<category label="2" x="2" showVerticalLine="1"/>\n\
	<category label="3" x="3" showVerticalLine="1"/>\n\
	<category label="4" x="4" showVerticalLine="1"/>\n\
	<category label="5" x="5" showVerticalLine="1"/>\n\
	<category label="6" x="6" showVerticalLine="1"/>\n\
	<category label="7" x="7" showVerticalLine="1"/>\n\
	<category label="8" x="8" showVerticalLine="1"/>\n\
	<category label="9" x="9" showVerticalLine="1"/>\n\
	<category label="10" x="10" showVerticalLine="0"/>\n\
</categories>\n\
\n\
<dataSet seriesName="Equities" color="000000" plotBorderThickness="0" showPlotBorder="1" anchorSides="3">\n\
	<set id="INVEQ324_2" x="2.8" y="33.6" tooltext="Script C2: 2.8 yrs., 33.6%"/>\n\
	<set id="INVEQ324_3" x="6.2" y="24.8" tooltext="Script D3: 6.2 yrs., 24.8%"/>\n\
	<set id="INVEQ324_4" x="1" y="80" tooltext="Script E4: 1 yrs., 14%"/>\n\
	<set id="INVEQ324_5" x="1.2" y="160.4" tooltext="Script F5: 1.2 yrs., 26.4%"/>\n\
	<set id="INVEQ324_6" x="7.4" y="114.4" tooltext="Script G6: 4.4 yrs., 114.4%"/>\n\
	<set id="INVEQ324_7" x="5.5" y="220" tooltext="Script H7: 8.5 yrs., 323%"/>\n\
	<set id="INVEQ324_8" x="6.9" y="289.8" tooltext="Script I8: 6.9 yrs., 289.8%"/>\n\
	<set id="INVEQ324_9" x="9.9" y="287.1" tooltext="Script J9: 9.9 yrs., 287.1%"/>\n\
	<set id="INVEQ324_9" x="3" y="280.1" tooltext="Script J9: 9.9 yrs., 287.1%"/>\n\
	<set id="INVEQ324_9" x="2" y="240.1" tooltext="Script J9: 9.9 yrs., 287.1%"/>\n\
	\n\
</dataSet>\n\
\n\
<dataSet id="DS2" seriesName="Mutual Funds" color="ff0000" showPlotBorder="1" anchorRadius="4">\n\
	<set id="INVMF324_3" x="4.7" y="230.3" tooltext="Fund C: 4.7 yrs., 230.3%"/>\n\
	<set id="INVMF324_5" x="3" y="100" tooltext="Fund E: 3 yrs., 24%"/>\n\
	<set id="INVMF324_6" x="2" y="94" tooltext="Fund F: 2 yrs., 94%"/>\n\
	<set id="INVMF324_8" x="7.9" y="242.8" tooltext="Fund H: 6.9 yrs., 289.8%"/>\n\
	<set id="INVMF324_10" x="7.1" y="333.7" tooltext="Fund J: 7.1 yrs., 333.7%"/>\n\
	<set id="INVMF324_8" x="8.5" y="70" tooltext="Fund H: 6.9 yrs., 289.8%"/>\n\
	<set id="INVMF324_9" x="1.3" y="310" tooltext="Fund I: 1.3 yrs., 15.6%"/>\n\
	\n\
</dataSet>\n\
\n\
<trendLines>\n\
	<line startValue="0"  endValue="200" color="ff0000" isTrendZone="1"/>\n\
	<line startValue="200"  endValue="400" color="cccccc"  isTrendZone="1"/>\n\
</trendLines>\n\
\n\
  <styles>\n\
	<definition>\n\
		<style name="myCaptionFont" type="font" font="Arial" size="14" bold="1" underline="1" /> \n\
		<style name="myCaptionFont1" type="font"  font="Arial" size="12" bold="1" underline="0" /> \n\
		<style name="myCaptionFont2" type="font" color="00ff00" /> \n\
	</definition>\n\
	<application>\n\
		<apply toObject="Caption" styles="myCaptionFont" /> \n\
		<apply toObject="QUADRANTLABELS" styles="myCaptionFont1" /> \n\
		<apply toObject="TRENDVALUES" styles="myCaptionFont2" /> \n\
	</application>\n\
</styles>\n\
\n\
</chart>';
