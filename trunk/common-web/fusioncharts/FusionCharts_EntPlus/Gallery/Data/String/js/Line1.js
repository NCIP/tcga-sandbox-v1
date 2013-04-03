var dataString ='<chart caption="Temperature Monitoring (in degree C)" subCaption="(on 7/9/2006)" xAxisName="Time" yAxisMaxValue="100" bgColor="91AF46,FFFFFF"  divLineColor="91AF46" divLineAlpha="30" alternateHGridAlpha="5" canvasBorderColor="666666" baseFontColor="666666" lineColor="91AF46" numVDivlines="7" showAlternateVGridColor="1" anchorSides="3" anchorRadius="5" showValues="0">\n\
	<set label="0" value="34" />\n\
	<set label="3" value="27" />\n\
	<set label="6" value="42" />\n\
	<set label="9" value="50" />\n\
	<set label="12" value="68" />\n\
	<set label="15" value="56" />\n\
	<set label="18" value="48" />\n\
	<set label="21" value="34" />\n\
	<set label="24" value="30" />\n\
\n\
	<trendlines>\n\
		<line startvalue="50"  endValue="80" displayValue="Warning" color="BC9F3F" isTrendZone="1" showOnTop="0" alpha="25" valueOnRight="1"/> \n\
		<line startvalue="80"  endValue="100" displayValue="Critical" color="894D1B" isTrendZone="1" showOnTop="0" alpha="25" valueOnRight="1"/>   \n\
	</trendlines>\n\
<styles>\n\
        <definition>\n\
                    <style name="Anim1" type="animation" param="_alpha" start="0" duration="1" />\n\
        </definition>\n\
        <application>\n\
            <apply toObject="TRENDLINES" styles="Anim1" />\n\
        </application>\n\
    </styles>\n\
</chart>';
