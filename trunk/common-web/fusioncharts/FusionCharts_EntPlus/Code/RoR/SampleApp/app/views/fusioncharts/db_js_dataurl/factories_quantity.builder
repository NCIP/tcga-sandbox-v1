#Builds xml with values for factory output along with their names. It also creates
#a link to javascript function updateChart which will be called when the pie slice is clicked.
#The values required for building the xml is obtained from the factory data
#passed as locals parameter (factories).
xml = Builder::XmlMarkup.new
xml.chart(:caption=>'Factory Output report', :subCaption=>'By Quantity', :pieSliceDepth=>'30', :showBorder=>'1', :formatNumberScale=>'0', :numberSuffix=>' Units', :animation=>@animate_chart) do
	factories.each do|factory|
		xml.set(:label=>factory.name,:value=>factory.total_quantity,:link=>'javaScript:updateChart('+factory.id.to_s + ')')
	end
end
