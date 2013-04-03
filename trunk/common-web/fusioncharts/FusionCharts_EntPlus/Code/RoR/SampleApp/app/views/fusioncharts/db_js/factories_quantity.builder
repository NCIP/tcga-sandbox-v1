#Creates xml with values for factory output along with their names. It also creates
#a link to javascript function updateChart
#It uses the factories parameter from locals hash.
#This data is used for building xml for chart with factory name and total output.
xml = Builder::XmlMarkup.new
index_count = -1
xml.chart(:caption=>'Factory Output report', :subCaption=>'By Quantity', :pieSliceDepth=>'30', :showBorder=>'1', :formatNumberScale=>'0', :numberSuffix=>'Units') do
	factories.each do |factory|
   # Note that the same index_count must be used in the javsascript array
    index_count = index_count + 1 
		xml.set(:label=>factory.name,:value=>factory.total_quantity,:link=>'javaScript:updateChart('+index_count.to_s+ ',"'+factory.name+'");' )
	end
end