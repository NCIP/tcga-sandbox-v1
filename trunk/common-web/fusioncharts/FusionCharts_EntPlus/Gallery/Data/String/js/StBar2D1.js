var dataString ='<chart bgColor="E9E9E9" outCnvBaseFontColor="666666" caption="Monthly Sales Summary Comparison"  xAxisName="Month" yAxisName="Sales" numberPrefix="$" showValues="0" \n\
numVDivLines="10" showAlternateVGridColor="1" AlternateVGridColor="e1f5ff" divLineColor="e1f5ff" vdivLineColor="e1f5ff"  baseFontColor="666666"\n\
toolTipBgColor="F3F3F3" toolTipBorderColor="666666" canvasBorderColor="666666" canvasBorderThickness="1" showPlotBorder="1" plotFillAlpha="80">\n\
<categories>\n\
	<category label="Jan" />\n\
	<category label="Feb" />\n\
	<category label="Mar" />\n\
	<category label="Apr" />\n\
	<category label="May" />\n\
	<category label="Jun" />\n\
	<category label="Jul" />\n\
	<category label="Aug" />\n\
	<category label="Sep" />\n\
	<category label="Oct" />\n\
	<category label="Nov" />\n\
	<category label="Dec" />\n\
</categories>\n\
<dataset seriesName="2004" color="B1D1DC" plotBorderColor="B1D1DC">\n\
	<set value="27400" />\n\
	<set value="29800"/>\n\
	<set value="25800" />\n\
	<set value="26800" />\n\
	<set value="29600" />\n\
	<set value="32600" />\n\
	<set value="31800" />\n\
	<set value="36700" />\n\
	<set value="29700" />\n\
	<set value="31900" />\n\
	<set value="32900" />\n\
	<set value="34800" />\n\
</dataset>\n\
<dataset seriesName="2003" color="C8A1D1" plotBorderColor="C8A1D1">\n\
	<set  />\n\
	<set />\n\
	<set value="4500"/>\n\
	<set value="6500"/>\n\
	<set value="7600" />\n\
	<set value="6800" />\n\
	<set value="11800" />\n\
	<set value="19700" />\n\
	<set value="21700" />\n\
	<set value="21900" />\n\
	<set value="22900" />\n\
	<set value="29800" />\n\
</dataset>\n\
<trendlines>\n\
	<line startValue="22000" endValue="58000" color="999999" displayValue="Target" dashed="1" thickness="2" dashGap="6" alpha="100" showOnTop="1"/>\n\
</trendlines>\n\
<styles>\n\
	<definition>\n\
		<style type="animation" name="TrendAnim" param="_alpha" duration="1" start="0" />\n\
	</definition>\n\
	<application>\n\
		<apply toObject="TRENDLINES" styles="TrendAnim" />\n\
	</application>\n\
</styles>\n\
</chart>'; 