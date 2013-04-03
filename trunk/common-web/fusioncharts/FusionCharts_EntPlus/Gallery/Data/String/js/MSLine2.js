var dataString ='<chart palette="2" caption="Sales Comparison" showValues="0" numVDivLines="10" drawAnchors="0" numberPrefix="$" divLineAlpha="30" alternateHGridAlpha="20"  setAdaptiveYMin="1" >\n\
	<categories>\n\
		<category label="Jan" /> \n\
		<category label="Feb" /> \n\
		<category label="Mar" /> \n\
		<category label="Apr" /> \n\
		<category label="May" /> \n\
		<category label="Jun" /> \n\
		<category label="Jul" /> \n\
		<category label="Aug" /> \n\
		<category label="Sep" /> \n\
		<category label="Oct" /> \n\
		<category label="Nov" /> \n\
		<category label="Dec" /> \n\
	</categories>\n\
	<dataset seriesName="Current Year" color="A66EDD">\n\
		<set value="1127654" /> \n\
		<set value="1226234" /> \n\
		<set value="1299456" /> \n\
		<set value="1311565" /> \n\
		<set value="1324454" /> \n\
		<set value="1357654" /> \n\
		<set value="1296234" /> \n\
		<set value="1359456" /> \n\
		<set value="1391565" /> \n\
		<set value="1414454" /> \n\
		<set value="1671565" /> \n\
		<set value="1134454" /> \n\
	</dataset>\n\
	<dataset seriesName="Previous Year" color="F6BD0F">\n\
		<set value="927654" /> \n\
		<set value="1126234" /> \n\
		<set value="999456" /> \n\
		<set value="1111565" /> \n\
		<set value="1124454" /> \n\
		<set value="1257654" /> \n\
		<set value="1196234" /> \n\
		<set value="1259456" /> \n\
		<set value="1191565" /> \n\
		<set value="1214454" /> \n\
		<set value="1371565" /> \n\
		<set value="1434454" /> \n\
	</dataset>\n\
\n\
\n\
	<styles>\n\
		<definition>\n\
			<style name="XScaleAnim" type="ANIMATION" duration="0.5" start="0" param="_xScale" />\n\
			<style name="YScaleAnim" type="ANIMATION" duration="0.5" start="0" param="_yscale" />\n\
			<style name="XAnim" type="ANIMATION" duration="0.5" start="0" param="_yscale" />\n\
			<style name="AlphaAnim" type="ANIMATION" duration="0.5" start="0" param="_alpha" />\n\
		</definition>\n\
\n\
		<application>\n\
        		<apply toObject="CANVAS" styles="XScaleAnim, YScaleAnim,AlphaAnim" />\n\
	        	<apply toObject="DIVLINES" styles="XScaleAnim,AlphaAnim" />\n\
	        	<apply toObject="VDIVLINES" styles="YScaleAnim,AlphaAnim" />\n\
		        <apply toObject="HGRID" styles="YScaleAnim,AlphaAnim" />\n\
		</application>\n\
\n\
	</styles>\n\
</chart>';