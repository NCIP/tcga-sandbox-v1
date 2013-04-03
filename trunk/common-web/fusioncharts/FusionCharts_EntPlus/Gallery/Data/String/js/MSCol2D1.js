var dataString ='<chart palette="2" rotateNames="0" animation="1" \n\
numdivlines="4" caption="Global Export Index : 2004" baseFont="Arial" baseFontSize="12" useRoundEdges="1" legendBorderAlpha="0">\n\
	<categories font="Arial" fontSize="12">\n\
		<category label="N. America" toolText="North America"/>\n\
		<category label="Asia" />\n\
		<category label="Europe"/>\n\
		<category label="Australia" />\n\
		<category label="Africa" />\n\
\n\
	</categories>\n\
	<dataset seriesname="Consumer Goods" color="9ACCF6" alpha="90" showValues="0" dashed="1">\n\
		<set value="30" />\n\
		<set value="26" />\n\
		<set value="29" />\n\
		<set value="31" />\n\
		<set value="34" />\n\
	</dataset>\n\
	\n\
	<dataset seriesname="Capital Goods" color="82CF27"  showValues="0" alpha="90">\n\
		<set value="27" />\n\
		<set value="25" />\n\
		<set value="28" />\n\
		<set value="26" />\n\
		<set value="10" />\n\
	</dataset>\n\
\n\
\n\
	<trendLines>\n\
		<line startValue="20" endValue="35" color="8BBA00" thickness="1" alpha="20" showOnTop="0" displayValue="Trend 1" isTrendZone="1"/>		\n\
	</trendLines>\n\
<styles>\n\
        <definition>\n\
            <style name="Anim1" type="animation" param="_xScale" start="0" duration="1" />\n\
            <style name="Anim2" type="animation" param="_alpha" start="0" duration="1" />\n\
			<style name="myCaptionFont" type="font" align="left"/>\n\
        </definition>\n\
        <application>\n\
            <apply toObject="TRENDLINES" styles="Anim1, Anim2" />\n\
			<apply toObject="Caption" styles="myCaptionFont" />\n\
        </application>\n\
    </styles>\n\
\n\
</chart>';