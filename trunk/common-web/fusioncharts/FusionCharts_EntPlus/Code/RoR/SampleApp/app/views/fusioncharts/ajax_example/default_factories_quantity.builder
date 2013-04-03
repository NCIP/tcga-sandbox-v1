#Creates xml with values for Factory Output
#along with their names and a link to detailed action for each factory.
#It uses the factories parameter from locals hash.
#This data is used for building xml for chart with factory name and total output.
#Constructs the data url for the chart which will come up on clicking on the pie slice.
xml = Builder::XmlMarkup.new
xml.chart(:caption=>'Factory Output report', :subCaption=>'By Quantity', :pieSliceDepth=>'30', :showBorder=>'1', :formatNumberScale=>'0', :numberSuffix=>' Units' ) do
	factories.each do |factory|
		str_data_url = "javascript:updateChart(\""+factory.id.to_s+"\")"
		xml.set(:label=>factory.name,:value=>factory.total_quantity,:link=>str_data_url)
	end
end
