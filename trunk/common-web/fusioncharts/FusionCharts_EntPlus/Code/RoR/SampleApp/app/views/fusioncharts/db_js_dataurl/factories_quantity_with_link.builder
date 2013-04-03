#Creates xml with values for Factory Output
#along with their names.
#It uses the factories parameter from locals hash.
#This data is used for building xml for chart with factory name and total output.
xml = Builder::XmlMarkup.new
xml.chart(:caption=>'Factory Output report', :subCaption=>'By Quantity', :pieSliceDepth=>'30', :showBorder=>'1', :formatNumberScale=>'0', :numberSuffix=>'Units') do
	factories.each do |factory|
    str_link = 'newchart-xmlurl-factory_details?id='+factory.id.to_s
		xml.set(:label=>factory.name,:value=>factory.total_quantity,:link=>str_link)
	end
end
